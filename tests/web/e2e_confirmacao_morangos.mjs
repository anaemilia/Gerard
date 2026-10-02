import fs from 'node:fs';
import {createRequire} from 'node:module';
const require=createRequire(import.meta.url);
const {chromium}=require(process.env.PLAYWRIGHT_MODULE??'playwright');
const dir='tmp/confirmacao-morangos';fs.mkdirSync(dir,{recursive:true});
const browser=await chromium.launch();const page=await browser.newPage({viewport:{width:1500,height:1000}});
let estado;const chamadas=[];
page.on('response',async r=>{if(!r.url().includes('/api/'))return;try{const j=await r.json(),e=j.estado??(j.modo?j:null);if(e?.modo)estado=e;if(r.request().method()==='POST')chamadas.push({url:r.url(),pedido:r.request().postData(),resposta:j});}catch{}});
async function pausa(){await page.waitForTimeout(350);}
async function centro(l){const b=await l.boundingBox();return{x:b.x+b.width/2,y:b.y+b.height/2};}
async function clicar(l){const p=await centro(l);await page.mouse.click(p.x,p.y);await pausa();}
function exigir(ok,nome){console.log(`${ok?'[OK]':'[FALHA]'} ${nome}`);if(!ok)throw new Error(nome);}
try{
 await page.goto('http://localhost:8092');await page.waitForSelector('button[aria-label="Sortear Medidas"]');
 for(let i=0;i<160;i++){
  await clicar(page.locator('button[aria-label="Sortear Medidas"]'));
  if(estado?.situacao_id!=='PO_TRANSFORMACAO_MEDIDAS_frutas_93128185')continue;
  await clicar(page.locator('button[aria-label="Transformação de medidas"]'));
  if(estado?.situacao_id==='PO_TRANSFORMACAO_MEDIDAS_frutas_93128185'&&estado.modelagem)break;
 }
 if(estado?.situacao_id!=='PO_TRANSFORMACAO_MEDIDAS_frutas_93128185')throw new Error('exemplar não sorteado');
 for(const [e,i]of estado.cena.elementos_texto.filter(e=>e.papel_id).map((e,i)=>[e,i])){
  const f=estado.cena.figuras.find(f=>f.chave_papel_semantico===e.papel_id);
  const a=await centro(page.locator('.enunciado-elemento-semantico').nth(i)),b=await centro(page.locator(`[data-figura-id="${f.id}"]`));
  await page.mouse.move(a.x,a.y);await page.mouse.down();for(let k=1;k<=18;k++)await page.mouse.move(a.x+(b.x-a.x)*k/18,a.y+(b.y-a.y)*k/18);await page.mouse.up();await pausa();
  const sinal=page.locator('.valor-figura-sinal-tip input[aria-label="negativo"]');if(await sinal.count())await clicar(sinal);
 }
 const f=estado.cena.figuras.find(f=>f.chave_papel_semantico==='papel.estadoFinal');
 const p=await centro(page.locator(`[data-figura-id="${f.id}"]`));await page.mouse.dblclick(p.x,p.y);await pausa();
 const input=page.locator(`[id="valor-${f.id}"]`);await input.fill('1');
 await page.route('**/api/acoes/posicionar',async route=>{
  const response=await route.fetch();await page.waitForTimeout(600);await route.fulfill({response});
 });
 // Tecla mantida: eventos reais de repetição, sem uma nova intenção de envio.
 await input.focus();await page.keyboard.down('Enter');await page.keyboard.down('Enter');await page.keyboard.down('Enter');await page.keyboard.up('Enter');
 await page.waitForTimeout(6500);
 console.log('DIALOGO_APOS_6500MS='+await page.locator('.valor-figura-confirmacao').count());
 console.log('SUBMISSOES='+chamadas.filter(c=>c.url.endsWith('/api/acoes/posicionar')).length);
 console.log('CONFIRMACOES='+chamadas.filter(c=>c.url.endsWith('/api/acoes/responder-confirmacao-valor')).length);
 exigir(await page.locator('.valor-figura-confirmacao').count()===1,'pergunta permanece sem resposta');
 exigir(chamadas.filter(c=>c.url.endsWith('/api/acoes/posicionar')).length===1,'Enter repetido produz somente uma submissão');
 exigir(chamadas.filter(c=>c.url.endsWith('/api/acoes/responder-confirmacao-valor')).length===0,'não registra confirmação automática');
 await page.screenshot({path:dir+'/apos_enter.png'});
 await page.route('**/api/acoes/responder-confirmacao-valor',route=>route.abort());
 await clicar(page.locator('.valor-figura-confirmacao input').last());
 exigir(await page.locator('.valor-figura-confirmacao').count()===1,'falha de rede mantém pergunta');
 await page.unroute('**/api/acoes/responder-confirmacao-valor');
 // Resposta explícita, agora com a rede disponível.
 await clicar(page.locator('.valor-figura-confirmacao input').last());
 if(await input.count()){
  await input.fill('2');await input.press('Enter');await page.waitForTimeout(1100);
  console.log('DIALOGO_NO_LIMITE='+await page.locator('.valor-figura-confirmacao').count());
  console.log('CONFIRMACOES_NO_LIMITE='+chamadas.filter(c=>c.url.endsWith('/api/acoes/responder-confirmacao-valor')).length);
  await page.screenshot({path:dir+'/limite.png'});
  await page.mouse.dblclick(p.x,p.y);await pausa();
  const editor=page.locator(`[id="valor-${f.id}"]`);await editor.fill('7');await editor.press('Enter');await page.waitForTimeout(1100);
 }
 exigir(estado.modelagem.concluida===true,'conclusão real com valor correto');await page.screenshot({path:dir+'/concluida.png'});
}finally{fs.writeFileSync(dir+'/sequencia-http.json',JSON.stringify(chamadas,null,2));await browser.close();}
