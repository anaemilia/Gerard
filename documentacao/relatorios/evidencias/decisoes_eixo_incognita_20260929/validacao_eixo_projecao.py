from playwright.sync_api import sync_playwright
import re
def drag(pg,a,b,steps=15):
    pg.mouse.move(*a); pg.mouse.down()
    for i in range(1,steps+1): pg.mouse.move(a[0]+(b[0]-a[0])*i/steps, a[1]+(b[1]-a[1])*i/steps)
    pg.mouse.up(); pg.wait_for_timeout(900)
def centro(loc):
    r=loc.bounding_box(); return (r["x"]+r["width"]/2, r["y"]+r["height"]/2)
with sync_playwright() as p:
    b=p.chromium.launch(); pg=b.new_page(viewport={"width":1500,"height":950})
    reqs=[]
    pg.on("response", lambda r: reqs.append((r.url.split("8080")[1], r.status, (r.text()[:200] if ("projetar" in r.url or r.status>=400) else ""))) if "/api/acoes" in r.url else None)
    pg.goto("http://localhost:8080/"); pg.wait_for_timeout(1000)
    for i in range(200):
        pg.click("button[aria-label='Sortear Medidas']"); pg.wait_for_timeout(400)
        st=pg.evaluate("fetch('/api/situacao').then(r=>r.json())")
        if st["situacao_id"]=="PO_COMPARACAO_MEDIDAS_bolas_487868670": break
    print(st["situacao_id"])
    pg.click("button[aria-label='Comparação de medidas']"); pg.wait_for_timeout(1200)
    toks=pg.locator("h1 .enunciado-elemento-semantico")
    t=[toks.nth(i) for i in range(toks.count())]
    figs={pg.locator(".scene-figure").nth(i).text_content():pg.locator(".scene-figure").nth(i) for i in range(pg.locator(".scene-figure").count())}
    fig=lambda s:[v for k,v in figs.items() if s in k][0]
    drag(pg,centro(t[0]),centro(fig("Referido")))
    drag(pg,centro(t[1]),centro(fig("Valor relativo")))
    pg.locator("input[aria-label='positivo']").first.check(); pg.wait_for_timeout(900)
    lz=pg.locator(".scene-magnifier").first; pg.mouse.click(*centro(lz)); pg.wait_for_timeout(1200)
    print("REVELAR antes da incognita:", reqs[-1])
    pg.mouse.click(*centro(lz)); pg.wait_for_timeout(1000)
    drag(pg,centro(t[2]),centro(fig("Referendo")))
    inp=pg.locator(".valor-figura-tip input[type=number]"); inp.fill("14"); inp.press("Enter"); pg.wait_for_timeout(1500)
    pg.screenshot(path="/tmp/claude-0/x0.png")
    lupa=pg.locator(".scene-magnifier").first; pg.mouse.click(*centro(lupa)); pg.wait_for_timeout(1500); pg.screenshot(path="/tmp/claude-0/x05.png"); print("REQS apos lupa:", reqs[-2:])
    ponto=pg.locator(".eixo-ponto-controle").first
    print("eixo aberto:", ponto.count())
    a=centro(ponto); area=pg.locator(".eixo-numerico-svg").first.bounding_box()
    destino=(area["x"]+area["width"]*0.15, a[1])
    antes=fig("Referendo").text_content()
    drag(pg,a,destino,20); pg.wait_for_timeout(900)
    print("Referendo antes:", antes, "| depois:", fig("Referendo").text_content(), "| Valor relativo:", fig("Valor relativo").text_content())
    pg.screenshot(path="/tmp/claude-0/x1.png")
    proj=[r for r in reqs if "projetar" in r[0]]
    print("chamadas projetar-eixo:", len(proj)); print(proj[-1] if proj else None)
    b.close()
