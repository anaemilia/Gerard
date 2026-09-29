// Regra 1 + Regra 2: a explicação da operação errada só vem do servidor
// (texto curado); sem ela nada é exibido. Mouse real nas três categorias
// com seletor de operação.
// Requer Playwright (npm i -g playwright) e o ServidorPrototipoWeb em :8080.
import { chromium } from 'playwright';

const browser = await chromium.launch();
const page = await browser.newPage({ viewport: { width: 1500, height: 1000 } });
let estado = null;
page.on('response', async (r) => {
  if (!r.url().includes('/api/')) return;
  try { const j = await r.json(); const e = j?.estado ?? (j?.modo ? j : null); if (e?.modo) estado = e; } catch {}
});
await page.goto('http://localhost:8080/');
await page.waitForSelector('button[aria-label="Sortear Relações"]');
await page.waitForTimeout(800);
const ROTULO = { COMPOSICAO_TRANSFORMACOES: 'Composição de transformações', TRANSFORMACAO_RELACAO: 'Transformação de relação', COMPOSICAO_RELACOES: 'Composição de relações' };
async function centro(loc) { const b = await loc.boundingBox(); return b && { x: b.x + b.width / 2, y: b.y + b.height / 2 }; }
async function arrastar(de, para) {
  await page.mouse.move(de.x, de.y); await page.mouse.down();
  for (let i = 1; i <= 15; i++) await page.mouse.move(de.x + (para.x - de.x) * i / 15, de.y + (para.y - de.y) * i / 15);
  await page.mouse.up(); await page.waitForTimeout(500);
}
let casos = 0, falhas = 0;
for (let rodada = 0; rodada < 40 && casos < 4; rodada++) {
  await page.locator('button[aria-label="Sortear Relações"]').click(); await page.waitForTimeout(500);
  let categoria = null;
  for (const [cat, rot] of Object.entries(ROTULO)) {
    const b = page.locator(`button[aria-label="${rot}"]`);
    if (await b.isDisabled()) continue;
    await b.click(); await page.waitForTimeout(600);
    if (estado.modo === 'CATEGORIA_CLASSIFICADA') { categoria = cat; break; }
    for (let k = 0; k < 3 && await page.locator('.confirmation-dialog').count(); k++) {
      await page.locator('.confirmation-dialog button').last().click(); await page.waitForTimeout(400);
      if (estado.modo === 'CATEGORIA_CLASSIFICADA') break;
    }
    if (estado.modo === 'CATEGORIA_CLASSIFICADA') { categoria = estado.categoria_selecionada; break; }
  }
  if (!categoria) continue;
  const elementos = (estado.cena?.elementos_texto ?? estado.elementos_texto ?? []).filter(e => e.papel_id);
  const spans = page.locator('.enunciado-elemento-semantico');
  for (let i = 0; i < elementos.length; i++) {
    const f = estado.cena?.figuras.find(x => x.chave_papel_semantico === elementos[i].papel_id);
    if (!f || elementos[i].incognita) continue;
    const alvo = await centro(page.locator(`[data-figura-id="${f.id}"]`));
    const origem = await centro(spans.nth(i));
    if (!alvo || !origem) continue;
    await arrastar(origem, alvo);
    const sinal = page.locator('.valor-figura-sinal-tip label').first();
    if (await sinal.count()) { await sinal.click(); await page.waitForTimeout(500); }
  }
  const botoes = page.locator('.op-seletor [role="button"]');
  if (await botoes.count() === 0) continue;
  for (const nome of ['Soma', 'Subtração', 'SOMA', 'SUBTRACAO']) {
    const b = page.locator(`.op-seletor [role="button"][aria-label="${nome}"]`).first();
    if (!(await b.count())) continue;
    const resp = page.waitForResponse(r => r.url().includes('escolher-operacao'), { timeout: 5000 }).catch(() => null);
    const c = await centro(b.locator('circle.op-anel'));
    await page.mouse.click(c.x, c.y);
    const r = await resp;
    if (!r) { console.log('  sem requisição ao clicar', nome); continue; }
    const j = await r.json();
    await page.waitForTimeout(400);
    if (j.aceita) continue;
    const textos = await page.locator('.op-explicacao').evaluateAll(els => els.map(e => e.textContent || ''));
    const ok = !textos.some(t => t.includes('Operação incorreta'))
      && (j.chave_mensagem ? textos.some(t => t.trim() === j.chave_mensagem.trim()) : textos.length === 0);
    casos++; if (!ok) falhas++;
    console.log(`${ok ? '[OK]' : '[FALHA]'} ${categoria}: rejeitada; servidor=${j.chave_mensagem ? 'explicação curada' : 'sem explicação'}; tela=${JSON.stringify(textos).slice(0, 120)}`);
    break;
  }
}
console.log(`RESUMO casos=${casos} falhas=${falhas}`);
await browser.close();
