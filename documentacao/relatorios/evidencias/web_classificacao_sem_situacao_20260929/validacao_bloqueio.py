from playwright.sync_api import sync_playwright
NOMES=["Composição de medidas","Transformação de medidas","Comparação de medidas","Composição de transformações","Transformação de relação","Composição de relações"]
with sync_playwright() as p:
    b=p.chromium.launch(); pg=b.new_page(viewport={"width":1500,"height":950})
    posts=[]
    pg.on("request", lambda r: posts.append(r.url.split("8080")[1]) if r.method=="POST" else None)
    pg.goto("http://localhost:8080/"); pg.wait_for_timeout(1500)
    print("1) antes do sorteio: categorias desabilitadas:", [pg.is_disabled(f"button[aria-label='{n}']") for n in NOMES])
    pg.click(f"button[aria-label='{NOMES[0]}']", force=True); pg.wait_for_timeout(800)
    print("   POSTs apos clique forçado:", posts)
    print("   Sortear habilitado:", not pg.is_disabled("button[aria-label='Sortear Medidas']"))
    pg.click("button[aria-label='Sortear Medidas']"); pg.wait_for_timeout(1500)
    print("2) apos sorteio: activity-area:", pg.locator(".activity-area").count(),
          "| categorias Medidas habilitadas:", [not pg.is_disabled(f"button[aria-label='{n}']") for n in NOMES[:3]])
    enun=pg.locator(".statement-panel").inner_text()[:160].replace("\n"," ")
    print("   enunciado:", enun)
    pg.screenshot(path="f3_apos_sorteio.png")
    pg.click(f"button[aria-label='{NOMES[2]}']"); pg.wait_for_timeout(1200)
    print("3) classificacao apos ver o enunciado, POSTs:", posts)
    pg.screenshot(path="f3_apos_classificar.png")
    b.close()
