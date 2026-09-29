from playwright.sync_api import sync_playwright
import json
def drag(pg,a,b):
    pg.mouse.move(*a); pg.mouse.down()
    for i in range(1,16): pg.mouse.move(a[0]+(b[0]-a[0])*i/15, a[1]+(b[1]-a[1])*i/15)
    pg.mouse.up(); pg.wait_for_timeout(900)
def centro(loc):
    r=loc.bounding_box(); return (r["x"]+r["width"]/2, r["y"]+r["height"]/2)
with sync_playwright() as p:
    b=p.chromium.launch(); pg=b.new_page(viewport={"width":1500,"height":950})
    seq=[]
    def on_resp(r):
        if "/api/acoes" in r.url:
            try:
                j=r.json(); seq.append({k:j.get(k) for k in ("aceita","resposta","registrada","rejeicoes_consecutivas","limite_atingido","rejection_sequence_id","action_id")} | {"url":r.url.split("8080")[1]})
            except Exception: pass
    pg.on("response", on_resp)
    pg.goto("http://localhost:8080/"); pg.wait_for_timeout(1000)
    for i in range(40):
        pg.click("button[aria-label='Sortear Medidas']"); pg.wait_for_timeout(500)
        st=pg.evaluate("fetch('/api/situacao').then(r=>r.json())")
        if "COMPOSICAO_MEDIDAS" in st["situacao_id"]: break
    print(st["situacao_id"], st["enunciado"])
    pg.click("button[aria-label='Composição de medidas']"); pg.wait_for_timeout(1200)
    toks=pg.locator("h1 .enunciado-elemento-semantico")
    conhecidos=[i for i in range(toks.count()) if "conhecido" in (toks.nth(i).get_attribute("aria-label") or "")]
    inc=[i for i in range(toks.count()) if "Incógnita" in (toks.nth(i).get_attribute("aria-label") or "")][0]
    figs={pg.locator(".scene-figure").nth(i).text_content().replace("\n"," "):pg.locator(".scene-figure").nth(i) for i in range(pg.locator(".scene-figure").count())}
    print("figuras", list(figs))
    def fig(txt): return [v for k,v in figs.items() if txt in k][0]
    drag(pg, centro(toks.nth(conhecidos[0])), centro(fig("Parte 1")))
    drag(pg, centro(toks.nth(conhecidos[1])), centro(fig("Parte 2")))
    drag(pg, centro(toks.nth(inc)), centro(fig("Todo")))
    inp=pg.locator(".valor-figura-tip input[type=number]")
    print("edicao aberta:", inp.count())
    inp.fill("1"); inp.press("Enter"); pg.wait_for_timeout(1200)
    pg.screenshot(path="/tmp/claude-0/r1_pergunta.png")
    print("pergunta:", pg.locator(".valor-figura-confirmacao").inner_text().replace("\n"," | "))
    radios=pg.locator(".valor-figura-confirmacao input[type=radio]")
    labels=pg.locator(".valor-figura-confirmacao label").all_inner_texts()
    print("opcoes:", labels)
    idx_nao=[i for i,t in enumerate(labels) if "Não" in t][0]
    radios.nth(idx_nao).check(); pg.wait_for_timeout(1200)
    inp=pg.locator(".valor-figura-tip input[type=number]")
    print("edicao reaberta apos Nao:", inp.count())
    inp.fill("2"); inp.press("Enter"); pg.wait_for_timeout(1500)
    pg.screenshot(path="/tmp/claude-0/r2_limite.png")
    print("pergunta ainda visivel apos limite:", pg.locator(".valor-figura-confirmacao").count())
    print("material concreto:", pg.locator(".material-concreto-aviso").all_inner_texts())
    # --- cenario Sim ---
    seq.append({"url":"---- cenario Sim ----"})
    for i in range(40):
        pg.click("button[aria-label='Sortear Medidas']"); pg.wait_for_timeout(500)
        st=pg.evaluate("fetch('/api/situacao').then(r=>r.json())")
        if "COMPOSICAO_MEDIDAS" in st["situacao_id"]: break
    pg.click("button[aria-label='Composição de medidas']"); pg.wait_for_timeout(1200)
    toks=pg.locator("h1 .enunciado-elemento-semantico")
    conhecidos=[i for i in range(toks.count()) if "conhecido" in (toks.nth(i).get_attribute("aria-label") or "")]
    inc=[i for i in range(toks.count()) if "Incógnita" in (toks.nth(i).get_attribute("aria-label") or "")][0]
    figs={pg.locator(".scene-figure").nth(i).text_content():pg.locator(".scene-figure").nth(i) for i in range(pg.locator(".scene-figure").count())}
    drag(pg, centro(toks.nth(conhecidos[0])), centro(fig("Parte 1")))
    drag(pg, centro(toks.nth(conhecidos[1])), centro(fig("Parte 2")))
    drag(pg, centro(toks.nth(inc)), centro(fig("Todo")))
    inp=pg.locator(".valor-figura-tip input[type=number]"); inp.fill("1"); inp.press("Enter"); pg.wait_for_timeout(1200)
    labels=pg.locator(".valor-figura-confirmacao label").all_inner_texts()
    pg.locator(".valor-figura-confirmacao input[type=radio]").nth([i for i,t in enumerate(labels) if "Sim" in t][0]).check(); pg.wait_for_timeout(1200)
    pg.screenshot(path="/tmp/claude-0/r3_apos_sim.png")
    print("apos Sim: edicao aberta?", pg.locator(".valor-figura-tip input[type=number]").count())
    pg.mouse.dblclick(*centro(fig("Todo"))); pg.wait_for_timeout(900)
    if not pg.locator(".valor-figura-tip input[type=number]").count():
        pg.mouse.click(*centro(fig("Todo"))); pg.wait_for_timeout(900)
    inp=pg.locator(".valor-figura-tip input[type=number]")
    print("edicao reaberta pelo clique:", inp.count())
    if inp.count():
        inp.fill("3"); inp.press("Enter"); pg.wait_for_timeout(1500)
    pg.screenshot(path="/tmp/claude-0/r4_limite_sim.png")
    print("pergunta visivel:", pg.locator(".valor-figura-confirmacao").count(), "| material concreto:", pg.locator(".material-concreto-aviso").all_inner_texts())
    print("texto final:", pg.locator(".activity-area").inner_text()[:600].replace("\n"," | "))
    for s in seq: print(s)
    b.close()
