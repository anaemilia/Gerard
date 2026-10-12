// Evidência de navegador (Regra 2/4): número solto na caixa errada fica na caixa, com a pergunta aberta, até ser corrigido (paridade com itemQuestionadoPersistente da Main).
import fs from 'fs';
import os from 'os';
import path from 'path';
import { createRequire } from 'module';

const require = createRequire(import.meta.url);
const { chromium } = require(process.env.PLAYWRIGHT_MODULE ?? 'playwright');

const LOGS = path.join(os.homedir(), 'Gerard', 'logs');
const BASE_URL = process.env.GERARD_WEB_URL ?? 'http://localhost:8080/';
const EVIDENCIAS = path.resolve('documentacao', 'relatorios', 'evidencias',
  'web_numero_errado_20261011');
fs.mkdirSync(EVIDENCIAS, { recursive: true });
function linhasLog(trecho) {
  if (!fs.existsSync(LOGS)) return 0;
  let n = 0;
  for (const f of fs.readdirSync(LOGS)) {
    if (!f.startsWith('gerard_interacao_')) continue;
    for (const l of fs.readFileSync(path.join(LOGS, f), 'utf8').split('\n')) if (l.includes(trecho)) n++;
  }
  return n;
}
const resultados = [];
function verificar(nome, ok) { resultados.push([nome, ok]); console.log(`${ok ? '[OK]' : '[FALHA]'} ${nome}`); }

const browser = await chromium.launch(process.env.PLAYWRIGHT_BROWSER_PATH
  ? { executablePath: process.env.PLAYWRIGHT_BROWSER_PATH } : undefined);
const page = await browser.newPage({ viewport: { width: 1500, height: 1000 } });
let estado = null;
page.on('response', async (r) => {
  if (!r.url().includes('/api/') || r.request().method() === 'GET' && !r.url().endsWith('/api/situacao')) return;
  try {
    const j = await r.json();
    const e = j && j.estado ? j.estado : (j && j.schema === 'gerard.atividade-web.estado.v1' ? j : null);
    if (e && e.modo) estado = e;
  } catch { /* não-JSON */ }
});
await page.goto(BASE_URL);
await page.waitForSelector('button[aria-label="Sortear Medidas"]');
await page.waitForTimeout(800);

async function clicar(sel) { const b = await page.locator(sel).first().boundingBox(); await page.mouse.click(b.x + b.width / 2, b.y + b.height / 2); }
async function arrastar(de, para) {
  await page.mouse.move(de.x, de.y); await page.mouse.down();
  for (let i = 1; i <= 15; i++) await page.mouse.move(de.x + (para.x - de.x) * i / 15, de.y + (para.y - de.y) * i / 15);
  await page.mouse.up(); await page.waitForTimeout(500);
}
async function centro(loc) { const b = await loc.boundingBox(); return { x: b.x + b.width / 2, y: b.y + b.height / 2 }; }

async function classificarComposicao() {
  for (let i = 0; i < 60; i++) {
    await clicar('button[aria-label="Sortear Medidas"]');
    await page.waitForTimeout(500);
    const resp = page.waitForResponse(r => r.request().method() === 'POST' && r.url().includes('categoria'));
    await clicar('button[aria-label="Composição de medidas"]');
    const j = await (await resp).json();
    await page.waitForTimeout(400);
    if (j.correta) return true;
    for (let k = 0; k < 3; k++) {
      const dlg = page.locator('.confirmation-dialog');
      if (await dlg.count() === 0) break;
      await dlg.locator('button').last().click(); await page.waitForTimeout(400);
    }
  }
  return false;
}

function figuraDe(papel) { return estado.cena.figuras.find(f => f.chave_papel_semantico === papel); }
verificar('Composição de medidas classificada pelo clique real', await classificarComposicao());
const alvo = estado.modelagem.papel_desconhecido_original;
const dosElementos = () => (estado.cena?.elementos_texto ?? estado.elementos_texto).filter(e => e.papel_id && e.papel_id !== alvo);
const spans = page.locator('.enunciado-elemento-semantico');
const todos = (estado.cena?.elementos_texto ?? estado.elementos_texto).filter(e => e.papel_id);
const idx = (papel) => todos.findIndex(e => e.papel_id === papel);
const [b, a] = dosElementos().map(e => e.papel_id); // o número de a (Parte 2) vai à caixa de b (Parte 1): a trajetória não cruza a caixa certa
const figura = (papel) => page.locator(`[data-figura-id="${figuraDe(papel).id}"]`);
const textoNumero = (papel) => todos[idx(papel)].valor;
const tip = page.locator('.valor-figura-tip');

// 1) soltar o número de "a" na caixa de "b" (errada)
await arrastar(await centro(spans.nth(idx(a))), await centro(figura(b)));
const q = estado.cena.posicionamento_questionado;
verificar('o servidor publica o posicionamento questionado na caixa errada', !!q && q.figura_chave_papel === b);
verificar('a caixa errada mostra o número solto', String(figuraDe(b).valor) === String(textoNumero(a)));
verificar('a pergunta aparece', (await tip.count()) === 1 && (await tip.textContent()).includes('Tem certeza'));
const pergunta = (await tip.textContent()).trim();
const caixa = await figura(b).textContent();
verificar('o número está desenhado na caixa errada', caixa.includes(String(textoNumero(a))));
await page.screenshot({ path: path.join(EVIDENCIAS, 'numero_na_caixa_errada.png') });

// 2) o erro continua visível até ser corrigido (bem além dos 4 s do aviso antigo)
await page.waitForTimeout(6000);
verificar('depois de 6 s a pergunta continua aberta', (await tip.count()) === 1 && (await tip.textContent()).trim() === pergunta);
verificar('depois de 6 s o número continua na caixa errada', (await figura(b).textContent()).includes(String(textoNumero(a))));
await page.screenshot({ path: path.join(EVIDENCIAS, 'numero_na_caixa_errada_apos_6s.png') });

// 3) restaurar limpa o erro
await clicar('button[aria-label="Limpar a área do diagrama e recomeçar a modelagem"]');
await page.waitForTimeout(700);
verificar('restaurar apaga o erro', !estado.cena.posicionamento_questionado && (await tip.count()) === 0);

// 4) errar de novo e corrigir: soltar o mesmo número na caixa certa
await arrastar(await centro(spans.nth(idx(a))), await centro(figura(b)));
verificar('novo erro publicado', !!estado.cena.posicionamento_questionado);
await arrastar(await centro(spans.nth(idx(a))), await centro(figura(a)));
verificar('corrigido: o estado não publica mais a pergunta', !estado.cena.posicionamento_questionado);
verificar('corrigido: a pergunta some da tela', (await tip.count()) === 0);
verificar('corrigido: o número está na caixa certa', String(figuraDe(a).valor) === String(textoNumero(a)));
verificar('corrigido: a caixa errada ficou vazia', figuraDe(b).valor === null);
await page.screenshot({ path: path.join(EVIDENCIAS, 'numero_corrigido.png') });

await browser.close();
const falhas = resultados.filter(([, ok]) => !ok).length;
console.log(`RESUMO: ${resultados.length} verificações, ${falhas} falhas`);
process.exit(falhas === 0 ? 0 : 1);
