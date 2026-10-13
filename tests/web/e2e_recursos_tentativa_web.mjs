// Evidência de navegador (Regra 2): a tentativa como recurso. Com mouse real, o idioma da situação e a ajuda
// contextual chegam aos seus recursos em /api/v1/tentativas/{id}/..., e nenhuma chamada usa as rotas antigas.
import fs from 'fs';
import path from 'path';
import { createRequire } from 'module';

const require = createRequire(import.meta.url);
const { chromium } = require(process.env.PLAYWRIGHT_MODULE ?? 'playwright');
const BASE_URL = process.env.GERARD_WEB_URL ?? 'http://localhost:8080/';
const EVIDENCIAS = path.resolve('documentacao', 'relatorios', 'evidencias', 'web_recursos_tentativa_20261013');
fs.mkdirSync(EVIDENCIAS, { recursive: true });
const resultados = [];
function verificar(nome, ok) { resultados.push([nome, ok]); console.log(`${ok ? '[OK]' : '[FALHA]'} ${nome}`); }

const ROTULOS = {
  COMPOSICAO_MEDIDAS: 'Composição de medidas', TRANSFORMACAO_MEDIDAS: 'Transformação de medidas',
  COMPARACAO_MEDIDAS: 'Comparação de medidas'
};
const browser = await chromium.launch(process.env.PLAYWRIGHT_BROWSER_PATH
  ? { executablePath: process.env.PLAYWRIGHT_BROWSER_PATH } : undefined);
const page = await browser.newPage({ viewport: { width: 1500, height: 1000 } });
let estado = null;
let ultimoEtag = null;
const chamadas = [];
page.on('response', async (r) => {
  if (!r.url().includes('/api/')) return;
  chamadas.push({ metodo: r.request().method(), status: r.status(), caminho: new URL(r.url()).pathname });
  if (r.url().includes('/api/v1/tentativas') && r.headers()['etag']) ultimoEtag = r.headers()['etag'];
  try {
    const j = await r.json();
    const e = j && j.estado ? j.estado : (j && j.schema === 'gerard.atividade-web.estado.v1' ? j : null);
    if (e && e.situacao_id) estado = e;
  } catch { /* não-JSON */ }
});
await page.goto(BASE_URL);
await page.waitForSelector('button[aria-label="Sortear Medidas"]');
await page.waitForTimeout(800);
async function clicar(sel) {
  const b = await page.locator(sel).first().boundingBox();
  await page.mouse.click(b.x + b.width / 2, b.y + b.height / 2);
  await page.waitForTimeout(500);
}

// Sorteia até a situação ter mais de um idioma, classifica certo pelo botão da categoria.
let achou = false;
for (let i = 0; i < 20 && !achou; i++) {
  await clicar('button[aria-label="Sortear Medidas"]');
  const categoria = /^PO_([A-Z_]+?)_[a-z]/.exec(estado.situacao_id)[1];
  if (!ROTULOS[categoria] || estado.idiomas_situacao.length < 2) continue;
  await clicar(`button[aria-label="${ROTULOS[categoria]}"]`);
  achou = estado.modo === 'CATEGORIA_CLASSIFICADA';
}
verificar('situação com mais de um idioma classificada pelo clique real', achou);
const idTentativa = estado.tentativa_id;
const recurso = (sufixo) => `/api/v1/tentativas/${idTentativa}${sufixo}`;
verificar('a classificação foi ao recurso da tentativa',
  chamadas.some(c => c.metodo === 'POST' && c.caminho === recurso('/classificacao') && c.status === 200));

const marco = chamadas.length;
// Idioma da situação: botão do globo, depois a outra língua.
await clicar('.language-switch-wrap button.help-mark');
await page.screenshot({ path: path.join(EVIDENCIAS, 'menu_idioma.png') });
await clicar('.language-switch-menu .help-menu-opcoes button:not([disabled])');
await page.waitForTimeout(500);
verificar('trocar o idioma chega a PUT /tentativas/{id}/idioma (gravar um estado é PUT)',
  chamadas.some(c => c.metodo === 'PUT' && c.caminho === recurso('/idioma') && c.status === 200));
await page.screenshot({ path: path.join(EVIDENCIAS, 'idioma_trocado.png') });

// Ajuda contextual: o "?" ao lado do enunciado abre ao passar o mouse; a primeira opção pede a mensagem.
const ajuda = page.locator('.help-mark-wrap:not(.language-switch-wrap) > button.help-mark').first();
const caixa = await ajuda.boundingBox();
await page.mouse.move(caixa.x + caixa.width / 2, caixa.y + caixa.height / 2);
await page.waitForTimeout(400);
await clicar('.help-menu-opcoes button');
await page.waitForTimeout(500);
verificar('pedir ajuda chega a /tentativas/{id}/ajudas',
  chamadas.some(c => c.metodo === 'POST' && c.caminho === recurso('/ajudas') && c.status === 200));
verificar('a mensagem da ajuda aparece', await page.locator('.help-tip').count() > 0);
await page.screenshot({ path: path.join(EVIDENCIAS, 'ajuda_contextual.png') });

// Outra aba (ou chamada) altera a tentativa por baixo: a próxima alteração da tela usa um ETag velho.
const origem = new URL(BASE_URL).origin;
const concorrente = await page.context().request.put(origem + recurso('/idioma'), {
  data: { codigo: estado.idiomas_situacao.find(i => i.atual).codigo }, headers: { 'If-Match': ultimoEtag } });
verificar('a alteração concorrente (outra aba) é aceita com o ETag certo', concorrente.status() === 200);
const antes = chamadas.length;
await page.mouse.move(5, 600); // fecha o menu de ajuda, que cobre o botão do idioma
await page.waitForTimeout(400);
await clicar('.language-switch-wrap button.help-mark');
await clicar('.language-switch-menu .help-menu-opcoes button:not([disabled])');
await page.waitForTimeout(800);
const depois = chamadas.slice(antes);
verificar('a tela, com o ETag velho, recebe 412 versao-desatualizada',
  depois.some(c => c.metodo === 'PUT' && c.caminho === recurso('/idioma') && c.status === 412));
verificar('a tela lê a tentativa de novo (GET) para saber o que é verdade',
  depois.some(c => c.metodo === 'GET' && c.caminho === recurso('') && c.status === 200));
await clicar('.language-switch-wrap button.help-mark');
await clicar('.language-switch-menu .help-menu-opcoes button:not([disabled])');
await page.waitForTimeout(800);
verificar('depois de ressincronizar, a mesma ação da tela passa (200)',
  chamadas.slice(antes + depois.length).some(c => c.metodo === 'PUT' && c.caminho === recurso('/idioma') && c.status === 200));
await page.screenshot({ path: path.join(EVIDENCIAS, 'depois_da_ressincronizacao.png') });

const antigas = chamadas.filter(c => /^\/api\/(acoes|sorteios|classificacao|reiniciar|situacao|gestos)/.test(c.caminho));
verificar('nenhuma chamada usou as rotas antigas com verbo na URI', antigas.length === 0);
verificar('as chamadas da tentativa usam a mesma identidade (id da tentativa em curso)',
  chamadas.slice(marco).filter(c => c.caminho.startsWith('/api/v1/tentativas/') && !c.caminho.includes(idTentativa)).length === 0);

await browser.close();
const falhas = resultados.filter(([, ok]) => !ok).length;
console.log(`RESUMO: ${resultados.length} verificações, ${falhas} falhas`);
process.exit(falhas === 0 ? 0 : 1);
