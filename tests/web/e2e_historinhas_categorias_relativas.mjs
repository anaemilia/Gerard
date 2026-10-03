// Interação real por mouse/teclado. A API é somente observada, nunca chamada pelo teste.
import fs from 'node:fs';
import path from 'node:path';
import { createRequire } from 'node:module';
const require = createRequire(import.meta.url);
const { chromium } = require(process.env.PLAYWRIGHT_MODULE ?? 'playwright');
const dir = path.resolve('documentacao/relatorios/evidencias/historinhas_categorias_relativas_20261002/web');
fs.mkdirSync(dir, { recursive: true });
const linhas = fs.readFileSync('src/gerard/campoaditivo/dados/situacoes_vergnaud.tsv','utf8').trim().split(/\r?\n/);
const cab = linhas.shift().replace(/^#\s*/, '').split('\t');
const curadas = Object.fromEntries(linhas.map(l => { const v = l.split('\t'); const o = Object.fromEntries(cab.map((k,i)=>[k,v[i]??''])); return [o.id,o]; }));
const browser = await chromium.launch();
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
  const resposta=page.waitForResponse(r=>r.url().includes('/api/acoes/escolher-operacao')&&r.request().method()==='POST',{timeout:5000});
  await clicar(b); await resposta;
}
try{
  await page.goto(process.env.GERARD_WEB_URL??'http://localhost:8092/');
  await page.waitForSelector('button[aria-label="Sortear Relações"]');await pausa();
  for(const tipo of Object.keys(rotulos)){
    await sortear(tipo);const c=curadas[estado.situacao_id];console.log('SITUACAO='+c.id);
    exigir(await page.locator('.historinha-passiva').count()===0,tipo+' antes do limite vazio');
    await posicionar(c);
    const correta=c.operacao_relacao.toLowerCase();const errada=correta==='soma'?'subtracao':'soma';
    for(let n=1;n<=3;n++){await operacao(0,errada);
      exigir(await page.locator('.historinha-passiva').count()===(n===3?1:0),tipo+' historinha na rejeição '+n);
      await page.screenshot({path:path.join(dir,`${tipo}_rejeicao_${n}.png`)});
    }
    const src=await page.locator('.historinha-passiva img').getAttribute('src');
    exigir(src.includes(tipo.toLowerCase()+'/'),tipo+' referência da própria categoria');
    await operacao(0,correta);
    if(c.operacao_estado_transformacao)await operacao(1,c.operacao_estado_transformacao.toLowerCase());
    const alvo=estado.modelagem.papel_desconhecido_original
      ??estado.modelagem.acoes_disponiveis.find(a=>a.id==='PROPOR_VALOR_PAPEL')?.corpo?.papel_id;
    if(alvo){const f=figura(alvo);const p=await centro(page.locator(`[data-figura-id="${f.id}"]`));await page.mouse.dblclick(p.x,p.y);await pausa();
      const resposta=respostaDaRelacao(alvo,c);
      const input=page.locator(`[id="valor-${f.id}"]`);await input.fill(String(Math.abs(resposta)));await pausa();
      const enviada=page.waitForResponse(r=>r.url().endsWith('/api/acoes/posicionar')&&r.request().method()==='POST');
      await input.press('Enter');await pausa();await sinal(resposta<0);await (await enviada).finished();await pausa();
    }
    exigir(estado.modelagem.concluida===true,tipo+' conclusão real');
    exigir(await page.locator('.historinha-passiva').count()===1,tipo+' conserva historinha na conclusão');
    await page.screenshot({path:path.join(dir,`${tipo}_concluida.png`)});
    await clicar(page.locator('button[aria-label="Limpar a área do diagrama e recomeçar a modelagem"]'));
    exigir(await page.locator('.historinha-passiva').count()===0,tipo+' restauração oculta historinha');
  }
}finally{fs.writeFileSync(path.join(dir,'sequencia-http.json'),JSON.stringify(chamadas,null,2));await browser.close();}
