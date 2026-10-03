// Mouse e teclado reais; respostas da API são observadas, nunca simuladas.
import fs from 'node:fs';
import path from 'node:path';
import {createRequire} from 'node:module';
const require=createRequire(import.meta.url);
const {chromium}=require(process.env.PLAYWRIGHT_MODULE??'playwright');
const dir=path.resolve('documentacao/relatorios/evidencias/historinhas_folhas_20261002/web');
fs.mkdirSync(dir,{recursive:true});
const base='documentacao/producao_historinhas/especificacoes';
const specs=fs.readdirSync(base).filter(n=>/^\d\d_.*\.json$/.test(n)).map(n=>JSON.parse(fs.readFileSync(path.join(base,n),'utf8')));
const linhas=fs.readFileSync('src/gerard/campoaditivo/dados/situacoes_vergnaud.tsv','utf8').trim().split(/\r?\n/);
const cab=linhas.shift().replace(/^#\s*/,'').split('\t');
const curadas=Object.fromEntries(linhas.map(l=>{const v=l.split('\t');const o=Object.fromEntries(cab.map((k,i)=>[k,v[i]??'']));return[o.id,o];}));
const browser=await chromium.launch({executablePath:process.env.PLAYWRIGHT_EXECUTABLE});
const page=await browser.newPage({viewport:{width:1500,height:1000}});
page.on('pageerror',e=>console.log('ERRO PAGINA:',e.message));
let estado;const chamadas=[];
page.on('response',async r=>{if(!r.url().includes('/api/'))return;try{const j=await r.json(),e=j.estado??(j.modo?j:null);if(e?.modo)estado=e;if(r.request().method()==='POST')chamadas.push({url:r.url(),pedido:r.request().postData(),resposta:j});}catch{}});
const exigir=(ok,n)=>{console.log(`${ok?'[OK]':'[FALHA]'} ${n}`);if(!ok)throw new Error(n);};
const pausa=()=>page.waitForTimeout(650);
async function centro(l){const b=await l.boundingBox();if(!b)throw new Error('sem geometria');return{x:b.x+b.width/2,y:b.y+b.height/2};}
async function clicar(l){const p=await centro(l);await page.mouse.click(p.x,p.y);await pausa();}
async function arrastar(a,b){await page.mouse.move(a.x,a.y);await page.mouse.down();for(let k=1;k<=18;k++)await page.mouse.move(a.x+(b.x-a.x)*k/18,a.y+(b.y-a.y)*k/18);await page.mouse.up();await pausa();}
const rotulos={TRANSFORMACAO_MEDIDAS:'Transformação de medidas',COMPARACAO_MEDIDAS:'Comparação de medidas',COMPOSICAO_TRANSFORMACOES:'Composição de transformações'};
function negativo(p,c){const campos={'papel.transformacao1':'quantidade_1','papel.transformacao2':'quantidade_2','papel.transformacaoFinal':'resultado'};return(p==='papel.transformacao'&&c.sinal_transformacao==='negativo')||(p==='papel.diferenca'&&c.sinal_valor_relativo==='negativo')||String(c[campos[p]]??'').startsWith('-');}
async function posicionar(c){const elementos=estado.cena.elementos_texto.filter(e=>e.papel_id).map((e,i)=>[e,i]);
 for(const[e,i]of elementos.sort((a,b)=>Number(a[0].incognita)-Number(b[0].incognita))){
  const f=estado.cena.figuras.find(f=>f.chave_papel_semantico===e.papel_id);if(!f)continue;
  await arrastar(await centro(page.locator('.enunciado-elemento-semantico').nth(i)),await centro(page.locator(`[data-figura-id="${f.id}"]`)));
  const s=page.locator(`.valor-figura-sinal-tip input[aria-label="${negativo(e.papel_id,c)?'negativo':'positivo'}"]`);if(await s.count())await clicar(s.first());
  if(e.incognita && !estado.cena.figuras.find(x=>x.id===f.id)?.engatada){
   await page.screenshot({path:path.join(dir,'arraste_nao_engatado.png')});
   console.log('REPETINDO ARRASTE',e.papel_id,i);
   await pausa();
   await arrastar(await centro(page.locator('.enunciado-elemento-semantico').nth(i)),await centro(page.locator(`[data-figura-id="${f.id}"]`)));
  }
 }
}
async function operacao(indice,op){const l=page.locator('.op-seletor').nth(indice).locator(`[role="button"][aria-label="${op==='soma'?'Soma':'Subtração'}"] circle.op-anel`);if(await l.isVisible())await clicar(l);}
try{
 await page.goto(process.env.GERARD_WEB_URL??'http://localhost:8142/');await page.waitForSelector('button[aria-label="Sortear Medidas"]');await pausa();
 const alvos=[specs[0],specs[2],specs[6],specs[12]];
 for(const esp of alvos){
  const grupo=esp.categoria==='COMPOSICAO_TRANSFORMACOES'?'Relações':'Medidas';
  let encontrou=false;
  for(let i=0;i<160;i++){
   await clicar(page.locator(`button[aria-label="Sortear ${grupo}"]`));
   if(estado?.situacao_id!==esp.id_situacao)continue;
   await clicar(page.locator(`button[aria-label="${rotulos[esp.categoria]}"]`));
   if(estado?.modelagem){encontrou=true;break;}
  }
  exigir(encontrou,'sorteou '+esp.id_situacao);
  exigir(await page.locator('.historinha-passiva').count()===0,'sem historinha antes do limite');
  const c=curadas[esp.id_situacao];await posicionar(c);
  if(c.operacao_relacao)await operacao(0,c.operacao_relacao.toLowerCase());
  if(c.operacao_estado_transformacao)await operacao(1,c.operacao_estado_transformacao.toLowerCase());
  const alvo=estado.modelagem.papel_desconhecido_original??estado.modelagem.acoes_disponiveis.find(a=>a.id==='PROPOR_VALOR_PAPEL')?.corpo?.papel_id;
  const f=estado.cena.figuras.find(f=>f.chave_papel_semantico===alvo);
  const slug=path.basename(esp.saida).replace(/_historinha$/,'');
  if(!f){
   exigir(esp.id_situacao==='PO_TRANSFORMACAO_COMPOSTA_DOIS_PASSOS_chocolates_1269597383', 'limitação apenas na curadoria conhecida da Geisa');
   exigir(estado.modelagem.estado_final?.conhecido===true && estado.modelagem.estado_final.valor===16,
    'Geisa: API publica estado final conhecido 16, sem incógnita editável');
   await page.screenshot({path:path.join(dir,`${slug}_limitacao_curadoria.png`)});
   console.log('PENDÊNCIA GEISA: a curadoria/API impede propor a incógnita nesta situação; dados preservados.');
   continue;
  }
  for(let n=1;n<=3;n++){
   const p=await centro(page.locator(`[data-figura-id="${f.id}"]`));await page.mouse.dblclick(p.x,p.y);await pausa();
   const input=page.locator(`[id="valor-${f.id}"]`);await input.fill(String(n));await pausa();
   const enviada=page.waitForResponse(r=>r.url().endsWith('/api/acoes/posicionar'),{timeout:10000});
   await input.press('Enter');await pausa();
   const sinal=page.locator('.valor-figura-sinal-tip input[aria-label="positivo"]');if(await sinal.count())await clicar(sinal.first());
   await (await enviada).finished();await pausa();
   const confirmacao=page.locator('.valor-figura-confirmacao input').first();if(await confirmacao.count()){
    const concluida=page.waitForResponse(r=>r.url().endsWith('/api/acoes/responder-confirmacao-valor'));
    await clicar(confirmacao);await (await concluida).finished();
    await page.locator('.valor-figura-confirmacao').waitFor({state:'hidden'});await pausa();
   }
   const resposta=chamadas.filter(c=>c.url.endsWith('/api/acoes/posicionar')).at(-1)?.resposta;
   exigir(resposta?.rejeicoes_consecutivas===n,'contagem '+n+' '+esp.titulo);
   exigir(await page.locator('.historinha-passiva').count()===(n===3?1:0),'animação somente na terceira rejeição '+esp.titulo+' '+n);
   await page.screenshot({path:path.join(dir,`${slug}_rejeicao_${n}.png`)});
  }
  const apoio=estado.ajuda_visual[0];const ref=esp.saida.split('/ajuda/')[1];
  exigir(apoio.tipo==='HISTORINHA_ILUSTRADA'&&apoio.referencia===ref,'backend selecionou animação própria '+esp.titulo);
  const img=page.locator('.historinha-passiva img');await img.waitFor();
  await page.waitForFunction(()=>{const i=document.querySelector('.historinha-passiva img');return i?.complete&&i.naturalWidth>0;});
  exigir((await img.getAttribute('src')).endsWith(ref+'.gif'),'GIF correto carregado '+esp.titulo);
  await page.waitForTimeout(2000);await page.screenshot({path:path.join(dir,`${slug}_animacao.png`)});
 }
 console.log('APROVADO: três situações novas com três rejeições reais e GIF próprio carregado; limitação da Geisa registrada sem alterar curadoria.');
}finally{fs.writeFileSync(path.join(dir,'sequencia-http.json'),JSON.stringify(chamadas,null,2));await browser.close();}
