// Evidência de navegador (Regra 2/4): o registro factual da web PARA após 3 escolhas de operação erradas (composição de relações); a ação do limite é gravada; restaurar reabre.
import fs from 'fs';
import os from 'os';
import path from 'path';
import { createRequire } from 'module';

const require = createRequire(import.meta.url);
const { chromium } = require(process.env.PLAYWRIGHT_MODULE ?? 'playwright');

const LOGS = path.join(os.homedir(), 'Gerard', 'logs');
const BASE_URL = process.env.GERARD_WEB_URL ?? 'http://localhost:8080/';
const EVIDENCIAS = path.resolve('documentacao', 'relatorios', 'evidencias',
  'web_limite_20261011');
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

async function classificarTransformacaoComIncognitaAssinada() {
  for (let i = 0; i < 120; i++) {
    await clicar('button[aria-label="Sortear Medidas"]');
    await page.waitForTimeout(400);
    const resp = page.waitForResponse(r => r.request().method() === 'POST' && r.url().includes('categoria'));
    await clicar('button[aria-label="Transformação de medidas"]');
    const j = await (await resp).json();
    await page.waitForTimeout(400);
    if (j.correta) {
      const alvoAtual = estado?.modelagem?.papel_desconhecido_original;
      if (alvoAtual && figuraDe(alvoAtual)?.requer_representacao_de_sinal) return true;
    }
    for (let k = 0; k < 3; k++) {
      const dlg = page.locator('.confirmation-dialog');
      if (await dlg.count() === 0) break;
      const botao = dlg.locator('button').last();
      const ponto = await centro(botao);
      await page.mouse.click(ponto.x, ponto.y);
      await page.waitForTimeout(400);
    }
  }
  return false;
}

function papeis() { const m = estado.modelagem; return ['parte1', 'parte2', 'todo'].map(k => m[k]); }
function figuraDe(papel) { return estado.cena.figuras.find(f => f.chave_papel_semantico === papel); }
async function posicionarTudo(alvo) {
  const elementos = (estado.cena?.elementos_texto ?? estado.elementos_texto).filter(e => e.papel_id);
  const spans = page.locator('.enunciado-elemento-semantico');
  const conhecidos = elementos.map((e, i) => [e, i]).filter(([e]) => e.papel_id !== alvo);
  const incog = elementos.map((e, i) => [e, i]).filter(([e]) => e.papel_id === alvo);
  for (const [e, i] of [...conhecidos, ...incog]) {
    const f = figuraDe(e.papel_id); if (!f) continue;
    await arrastar(await centro(spans.nth(i)), await centro(page.locator(`[data-figura-id="${f.id}"]`)));
  }
}
async function digitar(alvo, valor) {
  const f = figuraDe(alvo);
  const fig = page.locator(`[data-figura-id="${f.id}"][data-editavel]`);
  const c = await centro(fig);
  await page.mouse.dblclick(c.x, c.y); await page.waitForTimeout(300);
  const input = page.locator(`[id="valor-${f.id}"]`);
  await input.fill(String(valor)); await input.press('Enter'); await page.waitForTimeout(700);
}

async function classificarRelacoes() {
  for (let i = 0; i < 80; i++) {
    await clicar('button[aria-label="Sortear Relações"]'); await page.waitForTimeout(500);
    const resp = page.waitForResponse(r => r.request().method() === 'POST' && r.url().includes('categoria'));
    await clicar('button[aria-label="Composição de relações"]');
    const j = await (await resp).json(); await page.waitForTimeout(500);
    if (j.correta) return true;
    for (let k = 0; k < 3; k++) {
      const d = page.locator('.confirmation-dialog'); if (await d.count() === 0) break;
      await d.locator('button').last().click(); await page.waitForTimeout(400);
    }
  }
  return false;
}
verificar('Composição de relações classificada pelo clique real', await classificarRelacoes());
const elementos = (estado.cena?.elementos_texto ?? estado.elementos_texto).filter(e => e.papel_id);
const spans = page.locator('.enunciado-elemento-semantico');
const alvo = elementos.find(e => e.incognita)?.papel_id;
verificar('incógnita identificada no enunciado', !!alvo);
const conhecidos = elementos.map((e, i) => [e, i]).filter(([e]) => e.papel_id !== alvo);
let soma = 0;
// Mesma situação curada de TesteWebRegistroParaNoLimite (idades: "mais velha", "mais velho"): sinais positivos; a relação final é a soma.
for (const [e, i] of conhecidos) {
  soma += Number(e.valor);
  await arrastar(await centro(spans.nth(i)), await centro(page.locator(`[data-figura-id="${figuraDe(e.papel_id).id}"]`)));
  await clicar('input[aria-label="positivo"]'); await page.waitForTimeout(600);
}
const iAlvo = elementos.map((e, i) => [e, i]).find(([e]) => e.papel_id === alvo)[1];
await arrastar(await centro(spans.nth(iAlvo)), await centro(page.locator(`[data-figura-id="${figuraDe(alvo).id}"]`)));
await digitar(alvo, soma);
await clicar('input[aria-label="positivo"]'); await page.waitForTimeout(800);
verificar('incógnita correta aceita (a escolha de operação é o passo seguinte)', estado.modelagem?.concluida !== true);

const chamadas = [];
page.on('request', (r) => { if (r.method() === 'POST' && r.url().includes('/api/acoes/')) chamadas.push(r.url().split('/api/acoes/')[1]); });
const serie = [];
for (let k = 0; k < 6; k++) {
  const antes = linhasLog('Escolher a operação');
  await clicar('g[aria-label="Subtração"]'); await page.waitForTimeout(700);
  serie.push(linhasLog('Escolher a operação') - antes);
}
console.log('linhas gravadas por escolha errada (mouse real):', JSON.stringify(serie));
console.log('chamadas HTTP:', JSON.stringify(chamadas));
verificar('as 3 primeiras escolhas erradas são gravadas (a 3ª é a do limite)', serie.slice(0, 3).every(n => n > 0));
verificar('nada é gravado depois do limite', serie.slice(3).every(n => n === 0));
verificar('as chamadas HTTP continuaram depois do limite', chamadas.filter(c => c.includes('escolher-operacao')).length === 6);
const antesSoma = linhasLog('Escolher a operação');
await clicar('g[aria-label="Soma"]'); await page.waitForTimeout(700);
verificar('nem a escolha correta é gravada durante a ajuda visual', linhasLog('Escolher a operação') === antesSoma);
await page.screenshot({ path: path.join(EVIDENCIAS, 'operacao_registro_parado_no_limite.png') });

const aposLimite = linhasLog('Restaurar a área do diagrama');
await clicar('button[aria-label="Limpar a área do diagrama e recomeçar a modelagem"]');
await page.waitForTimeout(600);
verificar('restaurar reabre o registro (a restauração é gravada)', linhasLog('Restaurar a área do diagrama') > aposLimite);
await browser.close();
const falhas = resultados.filter(([, ok]) => !ok).length;
console.log(`RESUMO: ${resultados.length} verificações, ${falhas} falhas`);
process.exit(falhas === 0 ? 0 : 1);
