import fs from 'node:fs';
import path from 'node:path';
import {createRequire} from 'node:module';
const require=createRequire(import.meta.url);
const {chromium}=require(process.env.PLAYWRIGHT_MODULE??'playwright');
const dir=path.resolve('documentacao/relatorios/evidencias/composicao_sem_rejeicoes_20261002/web');fs.mkdirSync(dir,{recursive:true});
const browser=await chromium.launch();const page=await browser.newPage({viewport:{width:1500,height:1000}});
let estado;const chamadas=[];
page.on('response',async r=>{if(!r.url().includes('/api/'))return;try{const j=await r.json();const e=j.estado??(j.modo?j:null);if(e?.modo)estado=e;if(r.request().method()==='POST')chamadas.push({url:r.url(),pedido:r.request().postData(),resposta:j});}catch{}});
const pausa=()=>page.waitForTimeout(450);
async function centro(loc){const b=await loc.boundingBox();if(!b)throw new Error('elemento invisível');return{x:b.x+b.width/2,y:b.y+b.height/2};}
async function clicar(loc){const p=await centro(loc);await page.mouse.click(p.x,p.y);await pausa();}
function exigir(ok,nome){console.log(`${ok?'[OK]':'[FALHA]'} ${nome}`);if(!ok)throw new Error(nome);}
try{
 await page.goto('http://localhost:8092/');await page.waitForSelector('button[aria-label="Sortear Medidas"]');
 for(let i=0;i<100;i++){
  await clicar(page.locator('button[aria-label="Sortear Medidas"]'));
  await clicar(page.locator('button[aria-label="Composição de medidas"]'));
  for(let n=0;n<3&&await page.locator('.confirmation-dialog:visible').count();n++)await clicar(page.locator('.confirmation-dialog:visible button').last());
  if(estado?.categoria_selecionada==='COMPOSICAO_MEDIDAS'&&estado.modelagem)break;
 }
 exigir(estado?.categoria_selecionada==='COMPOSICAO_MEDIDAS','categoria composição');console.log('SITUACAO='+estado.situacao_id);
 const elementos=estado.cena.elementos_texto.filter(e=>e.papel_id);
 for(let i=0;i<elementos.length;i++){
  const e=elementos[i],f=estado.cena.figuras.find(f=>f.chave_papel_semantico===e.papel_id);
  const a=await centro(page.locator('.enunciado-elemento-semantico').nth(i)),b=await centro(page.locator(`[data-figura-id="${f.id}"]`));
  await page.mouse.move(a.x,a.y);await page.mouse.down();for(let k=1;k<=18;k++)await page.mouse.move(a.x+(b.x-a.x)*k/18,a.y+(b.y-a.y)*k/18);await page.mouse.up();await pausa();
 }
 const m=estado.modelagem;console.log('MODELAGEM='+JSON.stringify(m));
 const alvo=m.acoes_disponiveis.find(a=>a.id==='PROPOR_VALOR_PAPEL').corpo.papel_id;
 const papeis=[m.parte1,m.parte2,m.todo];
 const conhecidos=papeis.filter(p=>p.id!==alvo);
 const esperado=alvo==='papel.todo'?conhecidos[0].valor+conhecidos[1].valor:m.todo.valor-conhecidos.find(p=>p.id!=='papel.todo').valor;
 const f=estado.cena.figuras.find(f=>f.chave_papel_semantico===alvo),p=await centro(page.locator(`[data-figura-id="${f.id}"]`));
 await page.mouse.dblclick(p.x,p.y);await pausa();const input=page.locator(`[id="valor-${f.id}"]`);await input.fill(String(esperado));await input.press('Enter');await pausa();
 exigir(estado.modelagem.concluida===true,'conclusão real sem erros');
 exigir(estado.modelagem.material_concreto_disponivel===false,'material concreto não liberado');
 exigir(!estado.cena_material_concreto,'cena do material ausente');
 await page.screenshot({path:path.join(dir,'composicao_concluida_sem_rejeicoes.png')});
}finally{fs.writeFileSync(path.join(dir,'sequencia-http.json'),JSON.stringify(chamadas,null,2));await browser.close();}
