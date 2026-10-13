// Evidência de navegador (Regra 2): as rotas que só tinham teste de serviço agora são exercitadas por mouse real, uma a
// uma, a partir da tela: material concreto (quadradinhos), escolha de sinal, eixo (visibilidade e projeção) e a pessoa
// (cadastrar, entrar, editar). Cada verificação olha a chamada HTTP que a ação da tela realmente disparou.
import fs from 'fs';
import path from 'path';
import { createRequire } from 'module';

const require = createRequire(import.meta.url);
const { chromium } = require(process.env.PLAYWRIGHT_MODULE ?? 'playwright');
const BASE_URL = process.env.GERARD_WEB_URL ?? 'http://localhost:8080/';
const EVIDENCIAS = path.resolve('documentacao', 'relatorios', 'evidencias', 'web_rotas_restantes_20261013');
fs.mkdirSync(EVIDENCIAS, { recursive: true });
const resultados = [];
function verificar(nome, ok) { resultados.push([nome, ok]); console.log(`${ok ? '[OK]' : '[FALHA]'} ${nome}`); }

const browser = await chromium.launch(process.env.PLAYWRIGHT_BROWSER_PATH
  ? { executablePath: process.env.PLAYWRIGHT_BROWSER_PATH } : undefined);
const page = await browser.newPage({ viewport: { width: 1500, height: 1000 } });
let estado = null;
const chamadas = [];
page.on('response', async (r) => {
  if (!r.url().includes('/api/')) return;
  const req = r.request();
  chamadas.push({ metodo: req.method(), status: r.status(), caminho: new URL(r.url()).pathname, corpo: req.postData() });
  try {
    const j = await r.json();
    const e = j && j.estado ? j.estado : (j && j.schema === 'gerard.atividade-web.estado.v1' ? j : null);
    if (e && e.situacao_id) estado = e;
  } catch { /* não-JSON */ }
});
await page.goto(BASE_URL);
await page.waitForSelector('button[aria-label="Sortear Medidas"]');
await page.waitForTimeout(800);

async function centro(loc) { const b = await loc.boundingBox(); return { x: b.x + b.width / 2, y: b.y + b.height / 2 }; }
async function clicar(sel) {
  const c = await centro(page.locator(sel).first());
  await page.mouse.click(c.x, c.y); await page.waitForTimeout(500);
}
async function arrastar(de, para) {
  await page.mouse.move(de.x, de.y); await page.mouse.down();
  for (let i = 1; i <= 15; i++) await page.mouse.move(de.x + (para.x - de.x) * i / 15, de.y + (para.y - de.y) * i / 15);
  await page.mouse.up(); await page.waitForTimeout(500);
}
const desde = (marco, metodo, sufixo) => chamadas.slice(marco)
  .filter(c => c.metodo === metodo && c.caminho.endsWith(sufixo));
function figuraDe(papel) { return estado.cena.figuras.find(f => f.chave_papel_semantico === papel); }

async function classificar(rotulo, aceita) {
  for (let i = 0; i < 150; i++) {
    await clicar('button[aria-label="Sortear Medidas"]');
    await page.waitForTimeout(300);
    const resp = page.waitForResponse(r => r.request().method() === 'POST' && r.url().endsWith('/classificacao'));
    await clicar(`button[aria-label="${rotulo}"]`);
    const j = await (await resp).json();
    await page.waitForTimeout(400);
    if (j.correta && (!aceita || aceita())) return true;
    for (let k = 0; k < 3; k++) {
      const dlg = page.locator('.confirmation-dialog');
      if (await dlg.count() === 0) break;
      await dlg.locator('button').last().click(); await page.waitForTimeout(400);
    }
  }
  return false;
}
async function posicionar(papel) {
  const elementos = (estado.cena?.elementos_texto ?? estado.elementos_texto).filter(e => e.papel_id);
  const i = elementos.findIndex(e => e.papel_id === papel);
  const spans = page.locator('.enunciado-elemento-semantico');
  await arrastar(await centro(spans.nth(i)), await centro(page.locator(`[data-figura-id="${figuraDe(papel).id}"]`)));
}
async function digitar(alvo, valor) {
  const f = figuraDe(alvo);
  const c = await centro(page.locator(`[data-figura-id="${f.id}"][data-editavel]`));
  await page.mouse.dblclick(c.x, c.y); await page.waitForTimeout(300);
  const input = page.locator(`[id="valor-${f.id}"]`);
  await input.fill(String(valor)); await input.press('Enter'); await page.waitForTimeout(700);
}

// ---- 1. Material concreto: quadradinhos ----------------------------------------------------------------------------
verificar('Composição de medidas classificada pelo clique real', await classificar('Composição de medidas'));
const alvoComposicao = estado.modelagem.papel_desconhecido_original;
for (const e of (estado.cena.elementos_texto ?? []).filter(e => e.papel_id && e.papel_id !== alvoComposicao)) await posicionar(e.papel_id);
await posicionar(alvoComposicao);
const valores = Object.fromEntries(['parte1', 'parte2', 'todo'].map(k => estado.modelagem[k]).filter(p => p.id !== alvoComposicao).map(p => [p.id, p.valor]));
const correto = alvoComposicao.includes('todo') ? Object.values(valores).reduce((a, b) => a + b, 0)
  : valores['papel.todo'] - Object.entries(valores).find(([k]) => k !== 'papel.todo')[1];
for (let i = 0; i < 6 && !estado.modelagem.material_concreto_disponivel; i++) {
  await digitar(alvoComposicao, correto + 1 + i);
  const opcao = page.locator('.valor-figura-confirmacao label').first();
  if (await opcao.count()) { await opcao.click(); await page.waitForTimeout(600); }
  const f = figuraDe(alvoComposicao);
  if (!(await page.locator(`[data-figura-id="${f.id}"][data-editavel]`).count())) await posicionar(alvoComposicao);
}
verificar('depois de erros seguidos, o material concreto fica disponível', estado.modelagem.material_concreto_disponivel === true
  && await page.locator('.material-concreto').count() === 1);
let marco = chamadas.length;
await clicar('.material-concreto .quadradinhos-controles button:last-of-type');
const mais = desde(marco, 'POST', '/quadradinhos');
verificar('o "+" do material concreto chega a .../papeis/{papel}/quadradinhos (200, delta +1)',
  mais.length === 1 && mais[0].status === 200 && /"delta":\s*1/.test(mais[0].corpo ?? '')
  && /\/papeis\/[^/]+\/quadradinhos$/.test(mais[0].caminho));
await page.screenshot({ path: path.join(EVIDENCIAS, 'quadradinhos_mais.png') });
marco = chamadas.length;
await clicar('.material-concreto .quadradinhos-controles button:first-of-type');
const menos = desde(marco, 'POST', '/quadradinhos');
verificar('o "−" chega ao mesmo recurso (200, delta −1)',
  menos.length === 1 && menos[0].status === 200 && /"delta":\s*-1/.test(menos[0].corpo ?? ''));

// ---- 2. Sinal e eixo --------------------------------------------------------------------------------------------
verificar('Transformação de medidas com número relativo conhecido classificada pelo clique real',
  await classificar('Transformação de medidas', () => estado.cena.figuras.some(
    f => f.requer_representacao_de_sinal && f.chave_papel_semantico !== estado.modelagem.papel_desconhecido_original)));
const alvoT = estado.modelagem.papel_desconhecido_original;
const relativo = estado.cena.figuras.find(f => f.requer_representacao_de_sinal && f.chave_papel_semantico !== alvoT);
marco = chamadas.length;
await posicionar(relativo.chave_papel_semantico);
verificar('o número relativo posicionado pede o sinal na tela', await page.locator('.valor-figura-sinal-tip').count() === 1);
await clicar('.valor-figura-sinal-tip label');
const sinais = desde(marco, 'POST', '/sinal');
verificar('escolher o sinal chega a .../papeis/{papel}/sinal (200)',
  sinais.length === 1 && sinais[0].status === 200 && /\/papeis\/[^/]+\/sinal$/.test(sinais[0].caminho));
await page.screenshot({ path: path.join(EVIDENCIAS, 'sinal_escolhido.png') });

const lupa = `[data-figura-id="${figuraDe(relativo.chave_papel_semantico).id}"] [aria-label="Ver o eixo x deste número relativo"]`;
marco = chamadas.length;
await clicar(lupa);
await page.waitForSelector('.eixo-ponto-controle', { timeout: 5000 });
const reveladas = desde(marco, 'PUT', '/eixo/visibilidade');
verificar('a lupa chega a PUT .../eixo/visibilidade (200) e o eixo aparece',
  reveladas.length === 1 && reveladas[0].status === 200 && /"visivel":\s*true/.test(reveladas[0].corpo ?? ''));
await page.screenshot({ path: path.join(EVIDENCIAS, 'eixo_revelado.png') });

marco = chamadas.length;
const ponto = await centro(page.locator('.eixo-ponto-controle'));
const escala = figuraDe(relativo.chave_papel_semantico).eixo.escala;
await arrastar(ponto, { x: ponto.x + 232 / escala, y: ponto.y });
await page.waitForTimeout(600);
const projecoes = desde(marco, 'POST', '/eixo/projecao');
verificar('arrastar o ponto do eixo chega a .../eixo/projecao (200) enquanto a modelagem não concluiu',
  projecoes.length >= 1 && projecoes.every(c => c.status === 200));

marco = chamadas.length;
await clicar(`[data-figura-id="${figuraDe(relativo.chave_papel_semantico).id}"] [aria-label="Ocultar eixo X"]`);
const ocultadas = desde(marco, 'PUT', '/eixo/visibilidade');
verificar('a mesma lupa, agora "Ocultar eixo X", grava visivel=false (PUT 200) e o eixo some',
  ocultadas.length === 1 && ocultadas[0].status === 200 && /"visivel":\s*false/.test(ocultadas[0].corpo ?? '')
  && await page.locator('.eixo-ponto-controle').count() === 0);

// ---- 3. A pessoa ------------------------------------------------------------------------------------------------
const nomeInicial = `Teste Rotas ${Date.now() % 100000}`;
marco = chamadas.length;
await clicar('button[aria-label="Selecionar ou cadastrar usuário"]');
await page.locator('.usuario-cadastro input').first().fill(nomeInicial);
await page.waitForTimeout(200);
await clicar('button:has-text("Cadastrar e entrar")');
await page.waitForTimeout(800);
const criacao = desde(marco, 'POST', '/api/v1/usuarios');
const entrada = desde(marco, 'POST', '/api/v1/sessao/usuario');
verificar('cadastrar chega a POST /api/v1/usuarios (201) e entrar a POST /api/v1/sessao/usuario (200)',
  criacao.length === 1 && criacao[0].status === 201 && entrada.length === 1 && entrada[0].status === 200);
verificar('a barra passa a mostrar o nome de quem entrou', (await page.locator('.usuario-trigger-logado').textContent()).includes(nomeInicial));
marco = chamadas.length;
await clicar('button[aria-label="Selecionar ou cadastrar usuário"]');
const novoNome = nomeInicial + ' B';
await page.locator('.usuario-cadastro input').first().fill(novoNome);
await page.waitForTimeout(200);
await clicar('button:has-text("Salvar")');
await page.waitForTimeout(800);
const edicao = desde(marco, 'PUT', '');
verificar('salvar chega a PUT /api/v1/usuarios/{id} (200), pelo link "editar" da pessoa',
  edicao.length === 1 && edicao[0].status === 200 && /^\/api\/v1\/usuarios\/[^/]+$/.test(edicao[0].caminho));
verificar('a barra mostra o nome editado', (await page.locator('.usuario-trigger-logado').textContent()).includes(novoNome));
await page.screenshot({ path: path.join(EVIDENCIAS, 'pessoa_editada.png') });

await browser.close();
const falhas = resultados.filter(([, ok]) => !ok).length;
console.log(`RESUMO: ${resultados.length} verificações, ${falhas} falhas`);
process.exit(falhas === 0 ? 0 : 1);
