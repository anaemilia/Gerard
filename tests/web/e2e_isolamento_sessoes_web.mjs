// Evidência de navegador (Regra 2): dois visitantes simultâneos no mesmo servidor não interferem um no outro.
// A e B são contextos de navegador distintos (cookies próprios), com mouse real. B sorteia outras situações enquanto A
// está com a dele aberta; o clique de A na categoria certa da situação de A tem que ser julgado correto.
import fs from 'fs';
import path from 'path';
import { createRequire } from 'module';

const require = createRequire(import.meta.url);
const { chromium } = require(process.env.PLAYWRIGHT_MODULE ?? 'playwright');
const BASE_URL = process.env.GERARD_WEB_URL ?? 'http://localhost:8080/';
const EVIDENCIAS = path.resolve('documentacao', 'relatorios', 'evidencias', 'web_isolamento_sessoes_20261013');
fs.mkdirSync(EVIDENCIAS, { recursive: true });
const resultados = [];
function verificar(nome, ok) { resultados.push([nome, ok]); console.log(`${ok ? '[OK]' : '[FALHA]'} ${nome}`); }

function propriedade(chave) {
  for (const bruta of fs.readFileSync(path.resolve('mensagens_pt.properties'), 'utf8').split('\n')) {
    const linha = bruta.replace(/\r$/, '');
    if (linha.startsWith(chave + '=')) return linha.slice(chave.length + 1);
  }
  throw new Error('chave ausente: ' + chave);
}
const categoriaDe = (situacaoId) => /^PO_([A-Z_]+?)_[a-z]/.exec(situacaoId)[1];
// Rótulos dos botões como a barra de categorias os apresenta (BarraCategorias.tsx).
const ROTULOS = {
  COMPOSICAO_MEDIDAS: 'Composição de medidas', TRANSFORMACAO_MEDIDAS: 'Transformação de medidas',
  COMPARACAO_MEDIDAS: 'Comparação de medidas', COMPOSICAO_TRANSFORMACOES: 'Composição de transformações',
  TRANSFORMACAO_RELACAO: 'Transformação de relação', COMPOSICAO_RELACOES: 'Composição de relações'
};
const rotuloDe = (categoria) => ROTULOS[categoria];

const browser = await chromium.launch(process.env.PLAYWRIGHT_BROWSER_PATH
  ? { executablePath: process.env.PLAYWRIGHT_BROWSER_PATH } : undefined);
async function visitante() {
  const contexto = await browser.newContext({ viewport: { width: 1500, height: 1000 } });
  const page = await contexto.newPage();
  const v = { contexto, page, estado: null, veredito: null };
  page.on('response', async (r) => {
    if (!r.url().includes('/api/')) return;
    try {
      const j = await r.json();
      if (r.url().includes('/classificacao')) v.veredito = j;
      const e = j && j.estado ? j.estado : (j && j.schema === 'gerard.atividade-web.estado.v1' ? j : null);
      if (e && e.situacao_id) v.estado = e;
    } catch { /* não-JSON */ }
  });
  await page.goto(BASE_URL);
  await page.waitForSelector('button[aria-label="Sortear Medidas"]');
  await page.waitForTimeout(800);
  return v;
}
async function clicar(page, sel) {
  const b = await page.locator(sel).first().boundingBox();
  await page.mouse.click(b.x + b.width / 2, b.y + b.height / 2);
}

const a = await visitante();
const b = await visitante();
for (const v of [a, b]) { await clicar(v.page, 'button[aria-label="Sortear Medidas"]'); await v.page.waitForTimeout(600); }
const cookies = async (v) => (await v.contexto.cookies()).filter(c => c.name === 'gerard_sessao').map(c => c.value);
const [ca] = await cookies(a);
const [cb] = await cookies(b);
verificar('cada visitante recebe a sua sessão (cookies diferentes)', !!ca && !!cb && ca !== cb);

// B sorteia várias vezes até ter uma situação de categoria diferente da de A.
const situacaoDeA = a.estado.situacao_id;
const categoriaDeA = categoriaDe(situacaoDeA);
let rodadas = 0;
while (rodadas < 30 && (categoriaDe(b.estado.situacao_id) === categoriaDeA || rodadas < 3)) {
  await clicar(b.page, 'button[aria-label="Sortear Medidas"]');
  await b.page.waitForTimeout(500);
  rodadas++;
}
verificar('B tem agora uma situação de categoria diferente da de A', categoriaDe(b.estado.situacao_id) !== categoriaDeA);
await b.page.screenshot({ path: path.join(EVIDENCIAS, 'visitante_b_sorteou_outra_situacao.png') });

// A clica na categoria certa da situação de A, depois de B ter mexido no servidor.
const enunciadoA = await a.page.locator('body').innerText();
verificar('a tela de A não mudou por causa de B', a.estado.situacao_id === situacaoDeA);
await clicar(a.page, `button[aria-label="${rotuloDe(categoriaDeA)}"]`);
await a.page.waitForTimeout(800);
verificar('o clique certo de A é julgado correto (não contra a situação de B)', a.veredito && a.veredito.correta === true);
await a.page.screenshot({ path: path.join(EVIDENCIAS, 'visitante_a_julgado_certo.png') });

// E o inverso: B clica na categoria que seria a certa para A (diferente da dela) e é julgado incorreto.
b.veredito = null;
await clicar(b.page, `button[aria-label="${rotuloDe(categoriaDeA)}"]`);
await b.page.waitForTimeout(800);
verificar('o mesmo clique feito por B, com outra situação, é julgado incorreto', b.veredito && b.veredito.correta === false);

await browser.close();
const falhas = resultados.filter(([, ok]) => !ok).length;
console.log(`RESUMO: ${resultados.length} verificações, ${falhas} falhas`);
process.exit(falhas === 0 ? 0 : 1);
