// Evidência de navegador (Regra 2/4): selo de erro da modelagem (paridade com SeloErroModelagem do desktop): valor errado da incógnita acende selo e vermelho; correto, restauração e conclusão apagam.
import fs from 'fs';
import os from 'os';
import path from 'path';
import { createRequire } from 'module';

const require = createRequire(import.meta.url);
const { chromium } = require(process.env.PLAYWRIGHT_MODULE ?? 'playwright');

const LOGS = path.join(os.homedir(), 'Gerard', 'logs');
const BASE_URL = process.env.GERARD_WEB_URL ?? 'http://localhost:8080/';
const EVIDENCIAS = path.resolve('documentacao', 'relatorios', 'evidencias',
  'web_erro_20261011');
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
    const resp = page.waitForResponse(r => r.request().method() === 'POST' && r.url().endsWith('/classificacao'));
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
    const resp = page.waitForResponse(r => r.request().method() === 'POST' && r.url().endsWith('/classificacao'));
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

verificar('Composição de medidas classificada pelo clique real', await classificarComposicao());
const alvo = estado.modelagem.papel_desconhecido_original;
await posicionarTudo(alvo);
const v = {}; for (const p of papeis()) if (p && p.id !== alvo) v[p.id] = p.valor;
const correto = alvo.includes('todo') ? Object.values(v).reduce((a, b) => a + b, 0)
  : v['papel.todo'] - Object.entries(v).find(([k]) => k !== 'papel.todo')[1];
const selo = () => page.locator('.selo-erro');
const painelErro = () => page.locator('.diagram-panel-erro');
const corDoContorno = () => page.evaluate(() => {
  const r = document.querySelector('.diagram-panel .scene-figure rect, .diagram-panel .scene-figure ellipse');
  return getComputedStyle(r).stroke;
});
const corErro = await page.evaluate(() => { const e = document.createElement('i'); e.style.color = 'var(--erro)'; document.body.append(e); const c = getComputedStyle(e).color; e.remove(); return c; });
const rgb = (c) => c.replace(/\s/g, '');
verificar('antes de qualquer erro: sem selo e sem painel de erro', await selo().count() === 0 && await painelErro().count() === 0);
verificar('o estado publica NEUTRO', estado.cena.estado_feedback === 'NEUTRO');

await digitar(alvo, correto + 1);
verificar('valor errado: o estado publica ERRO (decidido pelo domínio)', estado.cena.estado_feedback === 'ERRO');
verificar('valor errado: o selo vermelho aparece', await selo().count() === 1);
verificar('valor errado: o painel fica em erro e o contorno da figura é a cor de erro',
  await painelErro().count() === 1 && rgb(await corDoContorno()) === rgb(corErro));
await page.screenshot({ path: path.join(EVIDENCIAS, 'selo_erro_apos_valor_errado.png') });

const opcao = page.locator('.valor-figura-confirmacao label').first();
if (await opcao.count()) { await opcao.click(); await page.waitForTimeout(600); }
verificar('o erro persiste enquanto não há valor correto nem restauração', await selo().count() === 1);

await clicar('button[aria-label="Limpar a área do diagrama e recomeçar a modelagem"]'); await page.waitForTimeout(700);
verificar('restaurar apaga o selo e o vermelho', await selo().count() === 0 && await painelErro().count() === 0);

await posicionarTudo(alvo);
await digitar(alvo, correto + 2);
verificar('novo erro acende de novo', await selo().count() === 1);
if (await opcao.count()) { await opcao.click(); await page.waitForTimeout(600); }
if (!figuraDe(alvo) || !(await page.locator(`[data-figura-id="${figuraDe(alvo).id}"][data-editavel]`).count())) await posicionarTudo(alvo);
await digitar(alvo, correto);
verificar('valor correto conclui a modelagem', estado.modelagem?.concluida === true);
verificar('a conclusão apaga o selo de erro e o painel de erro', await selo().count() === 0 && await painelErro().count() === 0);
await page.screenshot({ path: path.join(EVIDENCIAS, 'selo_erro_apagado_na_conclusao.png') });
await browser.close();
const falhas = resultados.filter(([, ok]) => !ok).length;
console.log(`RESUMO: ${resultados.length} verificações, ${falhas} falhas`);
process.exit(falhas === 0 ? 0 : 1);
