from playwright.sync_api import sync_playwright
NOMES={"COMPOSICAO_MEDIDAS":"Composição de medidas","TRANSFORMACAO_MEDIDAS":"Transformação de medidas","COMPARACAO_MEDIDAS":"Comparação de medidas"}
with sync_playwright() as p:
    b=p.chromium.launch(); pg=b.new_page(viewport={"width":1500,"height":950})
    seq=[]
    pg.on("request", lambda r: seq.append(r.method+" "+r.url.split("8080")[1]) if "/api/" in r.url else None)
    pg.goto("http://localhost:8080/"); pg.wait_for_timeout(1200)
    pg.click("button[aria-label='Sortear Medidas']"); pg.wait_for_timeout(1500)
    st=pg.evaluate("fetch('/api/situacao').then(r=>r.json())")
    cat=st["situacao_id"].split("PO_")[1]
    certa=[k for k in NOMES if cat.startswith(k)][0]
    errada=[k for k in NOMES if k!=certa][0]
    print("situacao",st["situacao_id"],"clicando errada:",errada)
    pg.click(f"button[aria-label='{NOMES[errada]}']"); pg.wait_for_timeout(1200)
    d=pg.locator(".confirmation-dialog")
    print("DIALOGO:", d.inner_text().replace("\n"," | "))
    pg.screenshot(path="/tmp/claude-0/f4_dialogo.png")
    pg.locator(".confirmation-dialog button").nth(1).click(); pg.wait_for_timeout(1200)
    print("apos 'Não': dialogo presente?", pg.locator(".confirmation-dialog").count())
    print("SEQ", seq)
    b.close()
