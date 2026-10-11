// Validação por interação real de mouse (CLAUDE.md, Regra 2, e skill gerard-paridade-web-desktop): fluxo de decisão
// depois do azul na web (selo, tip Sim/Não, "Próxima tarefa"), paridade com FluxoFeedbackConclusaoModelagem do desktop.
// Não chama a API diretamente: só interage pelo mouse e lê as respostas que a SPA recebe.
// Requer Playwright (npm i -g playwright) e o ServidorPrototipoWeb em :8080.
import fs from 'fs';
import os from 'os';
import path from 'path';
import { createRequire } from 'module';

const require = createRequire(import.meta.url);
const { chromium } = require(process.env.PLAYWRIGHT_MODULE ?? 'playwright');

const LOGS = path.join(os.homedir(), 'Gerard', 'logs');
const BASE_URL = process.env.GERARD_WEB_URL ?? 'http://localhost:8080/';
const EVIDENCIAS = path.resolve('documentacao', 'relatorios', 'evidencias',
  'web_conclusao_20261010');
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
  const fig = page.locator(`[data-figura-id="${f.id}"]`);
  const c = await centro(fig);
  await page.mouse.dblclick(c.x, c.y); await page.waitForTimeout(300);
  const input = page.locator(`[id="valor-${f.id}"]`);
  await input.fill(String(valor)); await input.press('Enter'); await page.waitForTimeout(700);
}


// ---- Fluxo de conclusão (paridade com FluxoFeedbackConclusaoModelagem do desktop) ----
function propriedade(chave) {
  const f = path.resolve('mensagens_pt.properties');
  for (const l of fs.readFileSync(f, 'utf8').split(/\r?\n/)) if (l.startsWith(chave + '=')) return l.slice(chave.length + 1);
  throw new Error('chave ausente em mensagens_pt.properties: ' + chave);
}
const TXT = {
  selo: propriedade('ui.completion.completed'), pergunta: propriedade('ui.completion.congratulations'),
  sim: propriedade('ui.completion.yes'), nao: propriedade('ui.completion.no'), proxima: propriedade('ui.completion.next')
};
const chamadasConclusao = [];
page.on('request', (r) => { if (r.url().includes('/api/acoes/conclusao')) chamadasConclusao.push(r.postData()); });

function valorCorreto() {
  const v = {};
  for (const p of papeis()) if (p && p.id !== estado.modelagem.papel_desconhecido_original) v[p.id] = p.valor;
  const alvo = estado.modelagem.papel_desconhecido_original;
  return alvo.includes('todo') ? Object.values(v).reduce((a, b) => a + b, 0)
    : v['papel.todo'] - Object.entries(v).find(([k]) => k !== 'papel.todo')[1];
}
async function concluirPelaIncognita() {
  const alvo = estado.modelagem.papel_desconhecido_original;
  await posicionarTudo(alvo);
  await digitar(alvo, valorCorreto());
  return estado.modelagem?.concluida === true;
}
const botaoProxima = () => page.locator('.selo-conclusao-proxima');
const tip = () => page.locator('.tip-conclusao');
async function passarMouseNoLink() {
  const c = await centro(botaoProxima());
  await page.mouse.move(c.x + 2, c.y + 1); await page.mouse.move(c.x, c.y);
  await page.waitForTimeout(250);
}
async function tirarMouse() { await page.mouse.move(20, 20); await page.waitForTimeout(900); }

// Rodada 0: azul -> pausa -> link; hover abre o tip; sair fecha; "Não" permanece e esconde selo e tip.
verificar('composição de medidas classificada pelo clique real', await classificarComposicao());
const situacao0 = estado.situacao_id;
const antesAzul = linhasLog('WEB_');
verificar('modelagem concluída pela incógnita correta', await concluirPelaIncognita());
verificar('o estado publica o fluxo de conclusão (textos, pausa, 3 ações)',
  !!estado.conclusao && estado.conclusao.acoes.length === 3 && estado.conclusao.atraso_decisao_ms === 1150);
verificar('textos publicados = mensagens_pt.properties (ui.completion.*)',
  estado.conclusao.textos.selo === TXT.selo && estado.conclusao.textos.pergunta === TXT.pergunta
  && estado.conclusao.textos.sim === TXT.sim && estado.conclusao.textos.nao === TXT.nao
  && estado.conclusao.textos.proxima_tarefa === TXT.proxima);
await page.waitForTimeout(2200);
verificar('depois da pausa aparece o link "Próxima tarefa"', await botaoProxima().count() === 1
  && (await botaoProxima().textContent()).trim() === TXT.proxima);
verificar('o selo mostra o texto de ui.completion.completed',
  (await page.locator('.selo-conclusao-texto').first().textContent()).trim() === TXT.selo);
verificar('sem mouse sobre o link, o tip está fechado', await tip().count() === 0);
await passarMouseNoLink();
verificar('o mouse sobre o link abre o tip com a pergunta e Sim/Não', await tip().count() === 1
  && (await tip().locator('.tip-conclusao-pergunta').textContent()).trim() === TXT.pergunta
  && (await tip().locator('.tip-conclusao-opcao').allTextContents()).map(t => t.trim()).join('|') === `${TXT.sim}|${TXT.nao}`);
await page.screenshot({ path: path.join(EVIDENCIAS, 'conclusao_tip_aberto.png') });
await tirarMouse();
verificar('tirar o mouse fecha o tip e o link permanece', await tip().count() === 0 && await botaoProxima().count() === 1);
await passarMouseNoLink();
const opcaoNao = tip().locator('.tip-conclusao-opcao', { hasText: TXT.nao });
const linhasAntesNao = linhasLog('WEB_'); const escolhasAntesNao = linhasLog('escolha=');
const pNao = await centro(opcaoNao);
await page.mouse.move(pNao.x, pNao.y); await page.waitForTimeout(150);
await page.mouse.click(pNao.x, pNao.y); await page.waitForTimeout(900);
verificar('"Não" permanece na mesma situação, ainda concluída',
  estado.situacao_id === situacao0 && estado.modelagem?.concluida === true);
verificar('"Não" esconde o tip e o link', await tip().count() === 0 && await botaoProxima().count() === 0);
await page.screenshot({ path: path.join(EVIDENCIAS, 'conclusao_apos_nao.png') });
verificar(`a escolha é decisão de interface: nenhuma linha nova no log (${linhasAntesNao}->${linhasLog('WEB_')})`,
  linhasLog('WEB_') === linhasAntesNao && linhasLog('escolha=') === escolhasAntesNao);

// Rodada 1: "Sim" inicia nova situação.
verificar('nova situação (composição) classificada', await classificarComposicao());
verificar('rodada Sim: modelagem concluída', await concluirPelaIncognita());
await page.waitForTimeout(2200);
const situacao1 = estado.situacao_id;
await passarMouseNoLink();
const pSim = await centro(tip().locator('.tip-conclusao-opcao', { hasText: TXT.sim }));
await page.mouse.move(pSim.x, pSim.y); await page.waitForTimeout(150);
await page.mouse.click(pSim.x, pSim.y); await page.waitForTimeout(1200);
// O sorteio pode devolver a mesma situação; "nova situação" = volta à classificação da categoria, sem conclusão.
verificar('"Sim" inicia uma nova situação (volta à classificação da categoria)', estado.modo === 'CLASSIFICACAO_CATEGORIA');
verificar('a nova situação não nasce concluída nem com selo', estado.modelagem?.concluida !== true && await botaoProxima().count() === 0);

// Rodada 2: clique direto no link "Próxima tarefa".
verificar('nova situação (composição) classificada', await classificarComposicao());
verificar('rodada link: modelagem concluída', await concluirPelaIncognita());
await page.waitForTimeout(2200);
const situacao2 = estado.situacao_id;
const cLink = await centro(botaoProxima());
await page.mouse.move(cLink.x, cLink.y); await page.waitForTimeout(150);
await page.mouse.click(cLink.x, cLink.y); await page.waitForTimeout(1200);
console.log(`situação antes do clique: ${situacao2}; depois: ${estado.situacao_id}; modo=${estado.modo}; concluida=${estado.modelagem?.concluida}`);
verificar('o clique em "Próxima tarefa" inicia uma nova situação (volta à classificação da categoria)',
  estado.modo === 'CLASSIFICACAO_CATEGORIA' && estado.modelagem?.concluida !== true && await botaoProxima().count() === 0);

console.log('Chamadas HTTP de conclusão (corpo):', JSON.stringify(chamadasConclusao));
verificar('as escolhas chegaram à API: NAO, SIM e PROXIMA_TAREFA, nessa ordem',
  chamadasConclusao.map(c => JSON.parse(c).escolha).join(',') === 'NAO,SIM,PROXIMA_TAREFA');
await browser.close();
const falhas = resultados.filter(([, ok]) => !ok).length;
console.log(`RESUMO: ${resultados.length} verificações, ${falhas} falhas`);
process.exit(falhas === 0 ? 0 : 1);
