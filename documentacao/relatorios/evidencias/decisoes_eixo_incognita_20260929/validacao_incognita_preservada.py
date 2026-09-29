from playwright.sync_api import sync_playwright
def drag(pg,a,b):
    pg.mouse.move(*a); pg.mouse.down()
    for i in range(1,16): pg.mouse.move(a[0]+(b[0]-a[0])*i/15, a[1]+(b[1]-a[1])*i/15)
    pg.mouse.up(); pg.wait_for_timeout(900)
def centro(loc):
    r=loc.bounding_box(); return (r["x"]+r["width"]/2, r["y"]+r["height"]/2)
with sync_playwright() as p:
    b=p.chromium.launch(); pg=b.new_page(viewport={"width":1500,"height":950})
    pg.goto("http://localhost:8080/"); pg.wait_for_timeout(1000)
    for i in range(40):
        pg.click("button[aria-label='Sortear Medidas']"); pg.wait_for_timeout(500)
        st=pg.evaluate("fetch('/api/situacao').then(r=>r.json())")
        if "COMPOSICAO_MEDIDAS" in st["situacao_id"]: break
    pg.click("button[aria-label='Composição de medidas']"); pg.wait_for_timeout(1200)
    toks=pg.locator("h1 .enunciado-elemento-semantico")
    con=[i for i in range(toks.count()) if "conhecido" in (toks.nth(i).get_attribute("aria-label") or "")]
    inc=[i for i in range(toks.count()) if "Incógnita" in (toks.nth(i).get_attribute("aria-label") or "")][0]
    figs={pg.locator(".scene-figure").nth(i).text_content():pg.locator(".scene-figure").nth(i) for i in range(pg.locator(".scene-figure").count())}
    fig=lambda t:[v for k,v in figs.items() if t in k][0]
    drag(pg,centro(toks.nth(con[0])),centro(fig("Parte 1"))); drag(pg,centro(toks.nth(con[1])),centro(fig("Parte 2"))); drag(pg,centro(toks.nth(inc)),centro(fig("Todo")))
    for v in ("1","2"):
        inp=pg.locator(".valor-figura-tip input[type=number]"); inp.fill(v); inp.press("Enter"); pg.wait_for_timeout(1100)
        if v=="1":
            labels=pg.locator(".valor-figura-confirmacao label").all_inner_texts()
            pg.locator(".valor-figura-confirmacao input[type=radio]").nth([i for i,t in enumerate(labels) if "Não" in t][0]).check(); pg.wait_for_timeout(1100)
    print("enunciado apos escalada:", pg.locator("h1#enunciado").inner_text())
    print("caixa Todo:", fig("Todo").text_content())
    mc=pg.locator(".material-concreto button")
    plus=[i for i in range(mc.count()) if mc.nth(i).inner_text().strip()=="+"]
    for _ in range(3): mc.nth(plus[0]).click(); pg.wait_for_timeout(500)
    print("apos +3 no material: enunciado:", pg.locator("h1#enunciado").inner_text(), "| Todo:", fig("Todo").text_content())
    print("contagem material:", pg.locator(".material-concreto").inner_text().replace("\n"," | ")[-60:])
    pg.screenshot(path="/tmp/claude-0/e1.png")
    pg.mouse.dblclick(*centro(fig("Todo"))); pg.wait_for_timeout(800)
    inp=pg.locator(".valor-figura-tip input[type=number]")
    print("editor aberto:", inp.count(), "pre-preenchido:", inp.input_value() if inp.count() else None)
    b.close()
