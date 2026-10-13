// Evidência de navegador (Regra 2): na fase exploratória da Composição de transformações, mexer numa transformação pela reta
// do eixo (mouse real) faz o estado final e a transformação resultante acompanharem — e um valor que o domínio recusa
// (zero numa transformação) não é aplicado. A API é somente observada.
import fs from 'node:fs';
import path from 'node:path';
import { createRequire } from 'node:module';
const require = createRequire(import.meta.url);
const { chromium } = require(process.env.PLAYWRIGHT_MODULE ?? 'playwright');
const dir = path.resolve('documentacao/relatorios/evidencias/web_exploracao_composicao_transformacoes_20261013');
fs.mkdirSync(dir, { recursive: true });
const linhas = fs.readFileSync('src/gerard/campoaditivo/dados/situacoes_vergnaud.tsv','utf8').trim().split(/\r?\n/);
const cab = linhas.shift().replace(/^#\s*/, '').split('\t');
const curadas = Object.fromEntries(linhas.map(l => { const v = l.split('\t'); const o = Object.fromEntries(cab.map((k,i)=>[k,v[i]??''])); return [o.id,o]; }));
const browser = await chromium.launch(process.env.PLAYWRIGHT_BROWSER_PATH ? { executablePath: process.env.PLAYWRIGHT_BROWSER_PATH } : undefined);
const page = await browser.newPage({viewport:{width:1500,height:1000}});
let estado; const chamadas=[];
page.on('response', async r => {
  if (!r.url().includes('/api/')) return;
  try { const j=await r.json(); const e=j.estado??(j.modo?j:null); if(e?.modo) estado=e;
    if(r.request().method()==='POST') chamadas.push({url:r.url(),pedido:r.request().postData(),resposta:j});
  } catch {}
});
function exigir(ok,nome){console.log(`${ok?'[OK]':'[FALHA]'} ${nome}`); if(!ok)throw new Error(nome);}
async function pausa(){await page.waitForTimeout(450);}
async function centro(loc){const b=await loc.boundingBox(); if(!b)throw new Error('elemento não visível');return{x:b.x+b.width/2,y:b.y+b.height/2};}
async function clicar(loc){const c=await centro(loc);await page.mouse.click(c.x,c.y);await pausa();}
async function arrastar(de,para){await page.mouse.move(de.x,de.y);await page.mouse.down();for(let i=1;i<=18;i++)await page.mouse.move(de.x+(para.x-de.x)*i/18,de.y+(para.y-de.y)*i/18);await page.mouse.up();await pausa();}
function figura(papel){return estado.cena.figuras.find(f=>f.chave_papel_semantico===papel);}
function valorCurado(papel,c){const campos={ 'papel.relacaoInicial':'estado_inicial','papel.transformacao':'transformacao','papel.relacaoFinal':c.tipo==='TRANSFORMACAO_RELACAO'?'estado_final':'resultado','papel.relacao1':'quantidade_1','papel.relacao2':'quantidade_2','papel.transformacao1':'quantidade_1','papel.transformacao2':'quantidade_2','papel.transformacaoFinal':'resultado','papel.estadoInicial':'estado_inicial','papel.estadoIntermediario':'estado_intermediario','papel.estadoFinal':'estado_final','papel.estadoInicialParte1':'estado_inicial_parte1','papel.estadoInicialParte2':'estado_inicial_parte2'};let v=c[campos[papel]];if(papel==='papel.transformacao'&&c.sinal_transformacao==='negativo'&&!v.startsWith('-'))v='-'+v;return Number(v);}
function respostaDaRelacao(alvo,c){
  if(c.tipo==='COMPOSICAO_TRANSFORMACOES'){ // a categoria publica transformacao_1, transformacao_2 e transformacao_final (final = t1 + t2)
    const m=estado.modelagem;const w=Object.fromEntries([m.transformacao_1,m.transformacao_2,m.transformacao_final].filter(Boolean).map(p=>[p.id,p.valor]));
    return alvo==='papel.transformacaoFinal'?w['papel.transformacao1']+w['papel.transformacao2']
      :alvo==='papel.transformacao1'?w['papel.transformacaoFinal']-w['papel.transformacao2']:w['papel.transformacaoFinal']-w['papel.transformacao1'];
  }
  const papeis=estado.modelagem.papeis??['relacao_1','relacao_2','relacao_final'].map(k=>estado.modelagem[k]);
  const v=Object.fromEntries(papeis.filter(p=>p.id!==alvo).map(p=>[p.id,p.valor]));
  if(c.tipo==='TRANSFORMACAO_RELACAO')return alvo==='papel.relacaoFinal'?v['papel.relacaoInicial']+v['papel.transformacao']
    :alvo==='papel.relacaoInicial'?v['papel.relacaoFinal']-v['papel.transformacao']:v['papel.relacaoFinal']-v['papel.relacaoInicial'];
  return alvo==='papel.relacaoFinal'?v['papel.relacao1']+v['papel.relacao2']
    :alvo==='papel.relacao1'?v['papel.relacaoFinal']-v['papel.relacao2']:v['papel.relacaoFinal']-v['papel.relacao1'];
}
async function sinal(negativo){const s=page.locator(`.valor-figura-sinal-tip input[aria-label="${negativo?'negativo':'positivo'}"]`);if(await s.count())await clicar(s.first());}
const rotulos={TRANSFORMACAO_RELACAO:'Transformação de relação',COMPOSICAO_RELACOES:'Composição de relações',COMPOSICAO_TRANSFORMACOES:'Composição de transformações'};
async function sortear(tipo){for(let i=0;i<160;i++){
  await clicar(page.locator('button[aria-label="Sortear Relações"]'));
  await clicar(page.locator(`button[aria-label="${rotulos[tipo]}"]`));
  for(let k=0;k<3&&await page.locator('.confirmation-dialog:visible').count();k++){
    await page.waitForTimeout(250);const d=page.locator('.confirmation-dialog:visible button').last();if(await d.isVisible())await clicar(d);
  }
  if(estado?.categoria_selecionada===tipo&&estado.modelagem&&curadas[estado.situacao_id]?.operacao_relacao
      && (tipo !== 'COMPOSICAO_TRANSFORMACOES' || estado.situacao_id === 'PO_COMPOSICAO_TRANSFORMACOES_bolas_509261012'))return;
}throw new Error('não sorteou '+tipo);}
async function posicionar(c){const es=estado.cena.elementos_texto.filter(e=>e.papel_id);const spans=page.locator('.enunciado-elemento-semantico');
  for(const [e,i]of es.map((e,i)=>[e,i]).sort((a,b)=>Number(a[0].incognita)-Number(b[0].incognita))){const f=figura(e.papel_id);if(!f)continue;
    await arrastar(await centro(spans.nth(i)),await centro(page.locator(`[data-figura-id="${f.id}"]`)));await sinal(valorCurado(e.papel_id,c)<0);
  }
}
async function operacao(qual,op){const seletores=page.locator('.op-seletor');const s=seletores.nth(qual);
  const b=s.locator(`[role="button"][aria-label="${op==='soma'?'Soma':'Subtração'}"] circle.op-anel`);
  const resposta=page.waitForResponse(r=>r.url().endsWith('/operacoes')&&r.request().method()==='POST',{timeout:5000});
  await clicar(b); await resposta;
}

async function valoresVisuais() {
  const v = {};
  for (const f of estado.cena.figuras) {
    const alvo = page.locator(`[data-figura-id="${f.id}"] .scene-figure-valor`);
    if (await alvo.count()) v[f.chave_papel_semantico] = Number((await alvo.first().textContent()).replace('−', '-'));
  }
  return v;
}
const consistente = (v) => (v['papel.transformacaoFinal'] === v['papel.transformacao1'] + v['papel.transformacao2'])
  && (v['papel.estadoFinal'] === v['papel.estadoInicial'] + v['papel.transformacaoFinal']);
const FLORES = 'PO_COMPOSICAO_TRANSFORMACAO_MEDIDAS_flores_422431114'; // a situação do relato: 5 rosas, -1, -1, resultante -2, final 3
async function sortearFlores() {
  for (let i = 0; i < 400; i++) {
    await clicar(page.locator('button[aria-label="Sortear Relações"]'));
    if (estado.situacao_id !== FLORES) continue;
    await clicar(page.locator('button[aria-label="Composição de transformações"]'));
    for (let k = 0; k < 3 && await page.locator('.confirmation-dialog:visible').count(); k++) {
      await page.waitForTimeout(250);
      const d = page.locator('.confirmation-dialog:visible button').last();
      if (await d.isVisible()) await clicar(d);
    }
    if (estado.categoria_selecionada === 'COMPOSICAO_TRANSFORMACOES') return;
  }
  throw new Error('não sorteou a situação das rosas');
}
try {
  await page.goto(process.env.GERARD_WEB_URL ?? 'http://localhost:8080/');
  await page.waitForSelector('button[aria-label="Sortear Relações"]'); await pausa();
  await sortearFlores();
  console.log('SITUACAO=' + estado.situacao_id);
  // Posiciona os dados (as transformações são negativas: "deu 1 rosa") e digita o estado final.
  const elementos = estado.cena.elementos_texto.filter(e => e.papel_id);
  const spans = page.locator('.enunciado-elemento-semantico');
  for (const [e, i] of elementos.map((e, i) => [e, i]).sort((a, b) => Number(a[0].incognita) - Number(b[0].incognita))) {
    const f = figura(e.papel_id); if (!f) continue;
    await arrastar(await centro(spans.nth(i)), await centro(page.locator(`[data-figura-id="${f.id}"]`)));
    await sinal(/transformacao/.test(e.papel_id));
  }
  const incognita = estado.modelagem.acoes_disponiveis.find(a => a.id === 'PROPOR_VALOR_PAPEL')?.corpo?.papel_id;
  const fi = figura(incognita); const pi = await centro(page.locator(`[data-figura-id="${fi.id}"]`));
  await page.mouse.dblclick(pi.x, pi.y); await pausa();
  const campo = page.locator(`[id="valor-${fi.id}"]`); await campo.fill('3'); await pausa();
  const enviada = page.waitForResponse(r => /\/valor$/.test(r.url()) && r.request().method() === 'POST');
  await campo.press('Enter'); await pausa(); await (await enviada).finished(); await pausa();
  exigir(estado.modelagem.concluida === true, 'Composição de transformações (as rosas) concluída pelo clique real (azul)');
  exigir(Array.isArray(estado.cena.relacao_exploratoria?.papeis)
    && estado.cena.relacao_exploratoria.papeis.includes('papel.estadoFinal'),
    'a relação exploratória publica o estado final como papel que acompanha as transformações');

  const antes = await valoresVisuais();
  exigir(consistente(antes), 'no azul, o diagrama é consistente: resultante = T1 + T2 e estado final = inicial + resultante '
    + JSON.stringify(antes));
  const t1 = figura('papel.transformacao1');
  await clicar(page.locator(`[data-figura-id="${t1.id}"] [aria-label="Ver o eixo x deste número relativo"]`));
  await page.waitForSelector('.eixo-ponto-controle');
  const escala = figura('papel.transformacao1').eixo.escala;
  const passo = 232 / escala;
  const ponto = await centro(page.locator('.eixo-ponto-controle'));
  // Um passo para o lado em que o estado final continua natural: se T1 é negativa, sobe; senão desce, sem passar do limite.
  const direcao = antes['papel.estadoFinal'] + 1 >= 0 && antes['papel.transformacao1'] <= 0 ? 1 : -1;
  await arrastar(ponto, { x: ponto.x + direcao * passo, y: ponto.y });
  const depois = await valoresVisuais();
  exigir(depois['papel.transformacao1'] !== antes['papel.transformacao1'], 'a reta mudou a transformação 1 ' + JSON.stringify(depois));
  exigir(depois['papel.transformacaoFinal'] === depois['papel.transformacao1'] + depois['papel.transformacao2'],
    'a transformação resultante acompanhou (T1 + T2)');
  exigir(depois['papel.estadoFinal'] === depois['papel.estadoInicial'] + depois['papel.transformacaoFinal'],
    'o estado final acompanhou (inicial + resultante)');
  await page.screenshot({ path: path.join(dir, 'exploracao_estado_final_acompanha.png') });

  // Um valor que o domínio recusa — zero numa transformação — não é aplicado e nada fica inconsistente.
  const valorT1 = depois['papel.transformacao1'];
  const pontoAtual = await centro(page.locator('.eixo-ponto-controle'));
  await arrastar(pontoAtual, { x: pontoAtual.x - (valorT1 * passo), y: pontoAtual.y }); // de volta para o 0
  const noZero = await valoresVisuais();
  exigir(noZero['papel.transformacao1'] !== 0, 'zero numa transformação (inteiros não nulos) não é aplicado');
  exigir(consistente(noZero), 'o diagrama segue consistente depois da tentativa de zero ' + JSON.stringify(noZero));
  await page.screenshot({ path: path.join(dir, 'exploracao_zero_recusado.png') });
} finally { await browser.close(); }
