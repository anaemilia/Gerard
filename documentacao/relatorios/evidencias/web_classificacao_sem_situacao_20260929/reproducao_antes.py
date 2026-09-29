from playwright.sync_api import sync_playwright
NOMES={"COMPOSICAO_MEDIDAS":"Composição de medidas","TRANSFORMACAO_MEDIDAS":"Transformação de medidas","COMPARACAO_MEDIDAS":"Comparação de medidas","COMPOSICAO_TRANSFORMACOES":"Composição de transformações","TRANSFORMACAO_RELACAO":"Transformação de relação","COMPOSICAO_RELACOES":"Composição de relações"}
with sync_playwright() as p:
    b=p.chromium.launch(); pg=b.new_page(viewport={"width":1500,"height":950})
    reqs=[]
    pg.on("response", lambda r: reqs.append((r.request.method, r.url.split("8080")[1], r.status)) if "/api/" in r.url else None)
    pg.goto("http://localhost:8080/"); pg.wait_for_timeout(1500)
    st=pg.evaluate("fetch('/api/situacao').then(r=>r.json())")
    oculta=st["situacao_id"]; print("modo",st["modo"],"situacao oculta:",oculta)
    cat_oculta=[k for k in NOMES if k in oculta.replace("TRANSFORMACAO_COMPOSTA_DOIS_PASSOS","COMPOSICAO_TRANSFORMACOES")]
    errada=[k for k in NOMES if k not in cat_oculta][0]
    print("tela vazia? activity-area:", pg.locator(".activity-area").count())
    print("clicando categoria (errada p/ situacao oculta):", NOMES[errada])
    pg.click(f"button[aria-label='{NOMES[errada]}']"); pg.wait_for_timeout(1200)
    st2=pg.evaluate("fetch('/api/situacao').then(r=>r.json())")
    print("modo apos clique:", st2["modo"], "| acoes:", [a["id"] for a in st2["acoes_disponiveis"]])
    print("Sortear Medidas desabilitado:", pg.is_disabled("button[aria-label='Sortear Medidas']"),
          "| Sortear Relações desabilitado:", pg.is_disabled("button[aria-label='Sortear Relações']"))
    print("activity-area visivel:", pg.locator(".activity-area").count())
    pg.screenshot(path="f2.png")
    print("REQS", reqs)
    b.close()
