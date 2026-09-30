// Validação por interação real de mouse (CLAUDE.md, Regra 2): a modelagem
// web persiste cada ação pela tentativa; depois do azul nada mais é gravado.
// Não chama a API diretamente: só lê as respostas que a SPA recebe.
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
  'web_incognita_sinal_20260929');
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

verificar('Composição de medidas classificada pelo clique real', await classificarComposicao());
const alvo = estado.modelagem.papel_desconhecido_original;
const L0 = linhasLog('WEB_');
const restAntes = linhasLog('tipo_restauracao=');
await clicar('button[aria-label="Limpar a área do diagrama e recomeçar a modelagem"]');
await page.waitForTimeout(600);
verificar(`restauração antes do azul é gravada (${restAntes}->${linhasLog('tipo_restauracao=')})`, linhasLog('tipo_restauracao=') === restAntes + 1);

await posicionarTudo(alvo);
const pos = linhasLog('WEB_POSICIONAR_PAPEL');
verificar(`arrastes gravados como posicionamento (${pos})`, pos >= 3);
const v = {}; for (const p of papeis()) if (p && p.id !== alvo) v[p.id] = p.valor;
const correto = alvo.includes('todo') ? Object.values(v).reduce((a, b) => a + b, 0)
  : v['papel.todo'] - Object.entries(v).find(([k]) => k !== 'papel.todo')[1];
const pv0 = linhasLog('WEB_PROPOR_VALOR_PAPEL'); const cv0 = linhasLog('WEB_CONFIRMACAO_VALOR_INCOGNITA');
await digitar(alvo, correto + 1);
const errada = linhasLog('WEB_PROPOR_VALOR_PAPEL') - pv0;
verificar(`valor errado gravado pela incógnita (${errada})`, errada === 1);
const opcao = page.locator('.valor-figura-confirmacao label').first();
verificar('pergunta de confirmação apareceu', await opcao.count() === 1);
if (await opcao.count()) { await opcao.click(); await page.waitForTimeout(600); }
verificar(`resposta Sim gravada (+${linhasLog('WEB_CONFIRMACAO_VALOR_INCOGNITA') - cv0})`, linhasLog('WEB_CONFIRMACAO_VALOR_INCOGNITA') - cv0 === 1);
if (!figuraDe(alvo) || !(await page.locator(`[data-figura-id="${figuraDe(alvo).id}"][data-editavel]`).count())) await posicionarTudo(alvo);
await digitar(alvo, correto);
verificar(`valor correto gravado (+${linhasLog('WEB_PROPOR_VALOR_PAPEL') - pv0})`, linhasLog('WEB_PROPOR_VALOR_PAPEL') - pv0 === 2);
verificar('diagrama azul (concluída)', estado.modelagem?.concluida === true
  && await page.locator('.diagram-panel-concluido').count() === 1);
await page.screenshot({ path: path.join(EVIDENCIAS, 'composicao_medidas_azul.png') });

const noAzul = linhasLog('WEB_'); const restAzul = linhasLog('tipo_restauracao=');
await clicar('button[aria-label="Limpar a área do diagrama e recomeçar a modelagem"]');
await page.waitForTimeout(600);
await posicionarTudo(alvo);
await digitar(alvo, correto + 3);
const perguntaExploratoria = await page.locator('.valor-figura-confirmacao').count();
verificar(`após o azul: restauração não gravada (${restAzul}->${linhasLog('tipo_restauracao=')})`, linhasLog('tipo_restauracao=') === restAzul);
verificar('após o azul: sem pergunta de confirmação', perguntaExploratoria === 0);
verificar(`após o azul: nenhuma ação gravada (${noAzul}->${linhasLog('WEB_')})`, linhasLog('WEB_') === noAzul);

verificar('nova situação sorteada', await classificarComposicao());
await posicionarTudo(estado.modelagem.papel_desconhecido_original);
verificar(`nova situação volta a gravar (${noAzul}->${linhasLog('WEB_')})`, linhasLog('WEB_') > noAzul);

verificar('Transformação de medidas com incógnita assinada classificada pelo clique real',
  await classificarTransformacaoComIncognitaAssinada());
const alvoAssinado = estado.modelagem.papel_desconhecido_original;
await posicionarTudo(alvoAssinado);
const valoresAssinados = Object.fromEntries(estado.modelagem.papeis
  .filter(p => p.id !== alvoAssinado).map(p => [p.id, p.valor]));
const corretoAssinado = valoresAssinados['papel.estadoFinal'] - valoresAssinados['papel.estadoInicial'];
verificar('incógnita assinada encontrada', alvoAssinado === 'papel.transformacao'
  && Number.isInteger(corretoAssinado) && corretoAssinado !== 0);

const valorAntesSinal = linhasLog('WEB_PROPOR_VALOR_PAPEL');
const confirmacaoAntesSinal = linhasLog('WEB_CONFIRMACAO_VALOR_INCOGNITA');
await digitar(alvoAssinado, Math.abs(corretoAssinado));
verificar('seletor de sinal da incógnita apareceu após a magnitude',
  await page.locator('.valor-figura-sinal-tip').count() === 1);
await clicar(`input[aria-label="${corretoAssinado < 0 ? 'positivo' : 'negativo'}"]`);
await page.waitForTimeout(700);
verificar('sinal errado submeteu e registrou um valor incorreto',
  linhasLog('WEB_PROPOR_VALOR_PAPEL') === valorAntesSinal + 1);
const confirmacaoAssinada = page.locator('.valor-figura-confirmacao label').first();
verificar('valor assinado errado abriu pergunta Sim/Não', await confirmacaoAssinada.count() === 1);
if (await confirmacaoAssinada.count()) {
  const ponto = await centro(confirmacaoAssinada);
  await page.mouse.click(ponto.x, ponto.y);
  await page.waitForTimeout(600);
}
verificar('resposta à pergunta do valor assinado foi registrada',
  linhasLog('WEB_CONFIRMACAO_VALOR_INCOGNITA') === confirmacaoAntesSinal + 1);
if (!figuraDe(alvoAssinado)
    || !(await page.locator(`[data-figura-id="${figuraDe(alvoAssinado).id}"][data-editavel]`).count())) {
  await posicionarTudo(alvoAssinado);
}
await digitar(alvoAssinado, Math.abs(corretoAssinado));
await clicar(`input[aria-label="${corretoAssinado < 0 ? 'negativo' : 'positivo'}"]`);
await page.waitForTimeout(800);
verificar('valor assinado correto foi registrado após a escolha do sinal',
  linhasLog('WEB_PROPOR_VALOR_PAPEL') === valorAntesSinal + 2);
verificar('Transformação de medidas ficou azul com a incógnita assinada correta',
  estado.modelagem?.concluida === true
    && await page.locator('.diagram-panel-concluido').count() === 1);

// A reta altera o papel por mouseDragged. O papel transmite em broadcast;
// cada interessado recolhe o que reconhece. Depois do azul, as projeções já
// vieram calculadas pelo domínio no snapshot e o gesto não chama a API.
const papelEixo = estado.modelagem.papeis.find(p => p.id === 'papel.transformacao');
const figuraEixo = figuraDe(papelEixo.id);
await clicar(`[data-figura-id="${figuraEixo.id}"] [aria-label="Ver o eixo x deste número relativo"]`);
await page.waitForSelector('.eixo-ponto-controle');
let requisicoesProjecaoEixo = 0;
const contarProjecaoEixo = req => {
  if (req.url().includes('/api/acoes/projetar-eixo')) requisicoesProjecaoEixo++;
};
page.on('request', contarProjecaoEixo);
const idsPapeisEixo = estado.modelagem.papeis.map(p => p.id);
async function valoresVisuais(ids) {
  const valores = {};
  for (const id of ids) {
    const figura = figuraDe(id);
    valores[id] = Number(await page.locator(
      `[data-figura-id="${figura.id}"] .scene-figure-valor`).textContent());
  }
  return valores;
}
const antesEixo = await valoresVisuais(idsPapeisEixo);
const escalaEixo = figuraEixo.eixo.escala;
const pontoEixo = await centro(page.locator('.eixo-ponto-controle'));
const passoEixo = 232 / escalaEixo;
const direcaoEixo = antesEixo[papelEixo.id] > 0 ? -1 : 1;
await arrastar(pontoEixo, { x: pontoEixo.x + direcaoEixo * passoEixo, y: pontoEixo.y });
const depoisEixo = await valoresVisuais(idsPapeisEixo);
verificar('mouseDragged da reta muda o número relativo',
  depoisEixo[papelEixo.id] !== antesEixo[papelEixo.id]);
verificar('interessado no broadcast atualiza sua própria projeção',
  idsPapeisEixo.some(id => id !== papelEixo.id && depoisEixo[id] !== antesEixo[id]));
verificar('broadcast exploratório concluído não chama a API', requisicoesProjecaoEixo === 0);
page.off('request', contarProjecaoEixo);
await page.screenshot({ path: path.join(EVIDENCIAS, 'transformacao_medidas_incognita_assinada_azul.png') });
console.log(`L0=${L0} RESUMO falhas=${resultados.filter(r => !r[1]).length}`);
await browser.close();
if (resultados.some(r => !r[1])) process.exitCode = 1;
