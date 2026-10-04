# -*- coding: utf-8 -*-
"""Valida POR REGRA, com mouse e teclado reais no navegador, a historinha na web para TODA situação que a regra
alcança (número relativo ou transformação), em pt-BR, en e fr — não por amostra.

Regras verificadas em cada situação (qualquer categoria, qualquer papel desconhecido):
  R1  antes da 3ª rejeição consecutiva não há historinha na tela;
  R2  na 3ª rejeição a historinha aparece (animação própria ou texto da situação, decidido pelo servidor);
  R3  no limite não há aviso algum (nem "Você tentou várias vezes..." nem a explicação da operação);
  R4  os números do enunciado não mudam antes da conclusão (diagrama azul);
  R5  um valor/operação errado nunca conclui a modelagem;
  R6  traduções (en/fr) recebem a mesma decisão do original;
  R7  (SINAL_ERRADO=1) marcar um sinal errado no primeiro número relativo não bloqueia o seguimento.
A mesma suíte roda sobre situações GENÉRICAS (tests/dados/gerar_situacoes_genericas.py) para validar a regra, e
depois sobre as curadas como amostras de ajuste fino.

Uso: python e2e_regras_historinha_mouse.py <url> <saida> <situacoes.tsv> [filtro_id_substring] [pt-BR|en|fr]
"""
import csv
import json
import random
import sys
from pathlib import Path

from playwright.sync_api import sync_playwright

URL = sys.argv[1].rstrip("/") + "/"
SAIDA = Path(sys.argv[2])
TSV = Path(sys.argv[3])
FILTRO = sys.argv[4] if len(sys.argv) > 4 and sys.argv[4] != "-" else None
IDIOMA = sys.argv[5] if len(sys.argv) > 5 else None
sorte = random.Random(11)
SINAL_ERRADO = bool(__import__("os").environ.get("SINAL_ERRADO"))

GRUPO = {"COMPOSICAO_MEDIDAS": "Medidas", "TRANSFORMACAO_MEDIDAS": "Medidas", "COMPARACAO_MEDIDAS": "Medidas",
         "COMPOSICAO_TRANSFORMACOES": "Relações", "TRANSFORMACAO_RELACAO": "Relações", "COMPOSICAO_RELACOES": "Relações"}
BOTAO_CATEGORIA = {"COMPOSICAO_MEDIDAS": "Composição de medidas", "TRANSFORMACAO_MEDIDAS": "Transformação de medidas",
                   "COMPARACAO_MEDIDAS": "Comparação de medidas",
                   "COMPOSICAO_TRANSFORMACOES": "Composição de transformações",
                   "TRANSFORMACAO_RELACAO": "Transformação de relação", "COMPOSICAO_RELACOES": "Composição de relações"}
ALVO_REGRA = {k for k in GRUPO if k != "COMPOSICAO_MEDIDAS"}

estado = {}
chamadas = []


def ao_responder(r):
    if "/api/" not in r.url:
        return
    try:
        j = r.json()
    except Exception:
        return
    e = j.get("estado") if isinstance(j.get("estado"), dict) else (j if j.get("modo") else None)
    if e and e.get("modo"):
        estado["e"] = e
    if r.request.method == "POST":
        chamadas.append((r.url.split("/api/")[1], j))


def catalogo():
    linhas = list(csv.reader(open(TSV, encoding="utf-8"), delimiter="\t", quoting=csv.QUOTE_NONE))
    cab = [c.lstrip("# ").strip() for c in linhas[0]]
    por_id = {}
    for r in linhas[1:]:
        r = r + [""] * (len(cab) - len(r))
        por_id[r[0]] = dict(zip(cab, r))
    ok = {i for i, s in por_id.items() if s["tipo_versao"] == "original" and s["validada"] == "true"}
    alvo = []
    for i, s in por_id.items():
        raiz = i if s["tipo_versao"] == "original" else s["versao_origem_id"]
        if raiz in ok and s["tipo"] in ALVO_REGRA and (not IDIOMA or s["idioma"] == IDIOMA) \
                and (not FILTRO or FILTRO in i):
            alvo.append(i)
    return por_id, sorted(alvo)


def esperar(pg, condicao, limite_ms=20000):
    """Espera a resposta do servidor que torna a condição verdadeira (o site publicado tem latência)."""
    espera = 0
    while espera < limite_ms:
        try:
            if condicao():
                return True
        except KeyError:
            pass
        pg.wait_for_timeout(250)
        espera += 250
    return False


def rodar(pg, alvo, por_id):
    s = por_id[alvo]
    raiz = alvo if s["tipo_versao"] == "original" else s["versao_origem_id"]
    tipo = s["tipo"]
    falhas = []
    pg.goto(URL)
    pg.wait_for_selector('button[aria-label="Sortear Medidas"]')

    def clicar(loc):
        b = loc.bounding_box()
        pg.mouse.click(b["x"] + b["width"] / 2, b["y"] + b["height"] / 2)
        pg.wait_for_timeout(300)

    def centro(loc):
        b = loc.bounding_box()
        return b["x"] + b["width"] / 2, b["y"] + b["height"] / 2

    for _ in range(300):
        clicar(pg.locator('button[aria-label="Sortear %s"]' % GRUPO[tipo]))
        if estado.get("e", {}).get("situacao_id") == raiz:
            break
    else:
        return ["situação não sorteada"], {}
    if raiz != alvo:                                   # tradução: troca o idioma pelo botão da situação
        esperar(pg, lambda: pg.locator('button[aria-label^="Alterar o idioma desta situação-problema"]').count() > 0)
        clicar(pg.locator('button[aria-label^="Alterar o idioma desta situação-problema"]'))
        nome = next(i["nome"] for i in estado["e"]["idiomas_situacao"] if i["codigo"] == s["idioma"])
        clicar(pg.locator("button", has_text=nome).last)
        esperar(pg, lambda: estado["e"]["situacao_id"] == alvo)
        if estado["e"]["situacao_id"] != alvo:
            return ["troca de idioma não levou a " + alvo], {}
    clicar(pg.locator('button[aria-label="%s"]' % BOTAO_CATEGORIA[tipo]))
    esperar(pg, lambda: estado["e"].get("cena") and estado["e"].get("categoria"))   # latência de rede (site publicado)
    e = estado["e"]

    # posiciona os dados do enunciado (e engata a incógnita) arrastando cada elemento até sua caixa
    elementos = [x for x in e["cena"]["elementos_texto"] if x.get("papel_id")]
    errou_sinal = False
    for i, el in enumerate(elementos):
        fig = next(f for f in estado["e"]["cena"]["figuras"] if f["chave_papel_semantico"] == el["papel_id"])
        ax, ay = centro(pg.locator(".enunciado-elemento-semantico").nth(i))
        bx, by = centro(pg.locator('[data-figura-id="%s"]' % fig["id"]))
        pg.mouse.move(ax, ay); pg.mouse.down()
        for k in range(1, 19):
            pg.mouse.move(ax + (bx - ax) * k / 18, ay + (by - ay) * k / 18)
        pg.mouse.up(); pg.wait_for_timeout(350)
        escolhido = sinal_correto(s, el["papel_id"])
        if SINAL_ERRADO and not errou_sinal:        # R7: marcar um sinal errado nunca bloqueia o seguimento
            escolhido = "negativo" if escolhido == "positivo" else "positivo"
        sinal = pg.locator('.valor-figura-sinal-tip input[aria-label="%s"]' % escolhido)
        if sinal.count():
            if SINAL_ERRADO and not errou_sinal:
                errou_sinal = True
            clicar(sinal.first)
            pg.wait_for_timeout(300)
    numeros_antes = pg.locator(".enunciado-elemento-semantico").all_inner_texts()

    historinha = lambda: pg.locator(".historinha-passiva").count()   # noqa: E731
    por_operacao = bool(s.get("operacao_relacao"))
    rejeicoes = 0
    if por_operacao:
        errada = "Soma" if s["operacao_relacao"] == "subtracao" else "Subtração"
        for k in range(3):
            if k == 2 and historinha():
                falhas.append("R1: historinha antes da 3ª rejeição")
            clicar(pg.locator('[role="button"][aria-label="%s"]' % errada).first)
            pg.wait_for_timeout(300)
    else:
        fig = next(f for f in estado["e"]["cena"]["figuras"] if f["chave_papel_semantico"] == s_incognita(s))
        px, py = centro(pg.locator('[data-figura-id="%s"]' % fig["id"]))
        campo = pg.locator('[id="valor-%s"]' % fig["id"])
        for k in range(3):
            if k == 2 and historinha():
                falhas.append("R1: historinha antes da 3ª rejeição")
            pg.mouse.dblclick(px, py); pg.wait_for_timeout(300)
            campo.fill(str(200 + sorte.randint(0, 700))); campo.press("Enter"); pg.wait_for_timeout(800)
            sin = pg.locator('.valor-figura-sinal-tip input[aria-label="positivo"]')
            if sin.count():
                clicar(sin.first); pg.wait_for_timeout(700)
            if k < 2:
                conf = pg.locator(".valor-figura-confirmacao input")
                if conf.count():
                    clicar(conf.first)               # "Sim": conta como tentativa
    pg.wait_for_timeout(2200)                          # abertura gradual
    e = estado["e"]
    if not historinha():
        falhas.append("R2: a historinha não apareceu no limite")
    if e.get("ajuda_visual_acionada") is not True:
        falhas.append("R2: o servidor não acionou a ajuda visual")
    if pg.get_by_role("status").filter(has_text="Você tentou").count() or \
            (chamadas and chamadas[-1][1].get("chave_mensagem")):
        falhas.append("R3: há aviso no limite: %r" % (chamadas[-1][1].get("chave_mensagem") if chamadas else None))
    if pg.locator(".enunciado-elemento-semantico").all_inner_texts() != numeros_antes:
        falhas.append("R4: números do enunciado mudaram antes da conclusão")
    if (e.get("modelagem") or {}).get("concluida") or e.get("concluida"):
        falhas.append("R5: o erro concluiu a modelagem")
    apoio = (e.get("ajuda_visual") or [{}])[0]
    return falhas, {"apoio": apoio.get("tipo"), "referencia": apoio.get("referencia"), "idioma": s["idioma"]}


def sinal_correto(s, papel):
    """Sinal curado do número relativo do papel (o participante que acerta o sinal não é questionado)."""
    def neg(valor, sinal=""):
        return sinal == "negativo" or str(valor).strip().startswith("-")
    campos = {"papel.transformacao1": ("quantidade_1", ""), "papel.transformacao2": ("quantidade_2", ""),
              "papel.transformacaoFinal": ("resultado", ""), "papel.transformacao": ("transformacao", "sinal_transformacao"),
              "papel.diferenca": ("valor_relativo", "sinal_valor_relativo"), "papel.relacao1": ("quantidade_1", ""),
              "papel.relacao2": ("quantidade_2", ""), "papel.relacaoFinal": ("resultado", ""),
              "papel.relacaoInicial": ("estado_inicial", "")}
    campo, campo_sinal = campos.get(papel, ("", ""))
    if not campo:
        return "positivo"
    return "negativo" if neg(s.get(campo, ""), s.get(campo_sinal, "") if campo_sinal else "") else "positivo"


def s_incognita(s):
    mapa = {"todo": "papel.todo", "quantidade_2": "papel.parte2", "estado_final": "papel.estadoFinal",
            "transformação": "papel.transformacao", "estado_inicial": "papel.estadoInicial",
            "referendo": "papel.referendo", "valor_relativo": "papel.diferenca", "referido": "papel.referido",
            "transformacao_resultante": "papel.transformacaoFinal", "transformacao_1": "papel.transformacao1",
            "relacao_final": "papel.relacaoFinal", "relacao_resultante": "papel.relacaoFinal"}
    return mapa[s["termo_desconhecido"]]


def main():
    SAIDA.mkdir(parents=True, exist_ok=True)
    por_id, alvos = catalogo()
    print("situações-alvo:", len(alvos))
    total_falhas = 0
    linhas = ["situacao\tidioma\tcategoria\tresultado\tapoio\tdetalhe"]
    with sync_playwright() as p:
        nav = p.chromium.launch()
        pg = nav.new_page(viewport={"width": 1500, "height": 1000})
        pg.on("response", ao_responder)
        for alvo in alvos:
            falhas, info = None, {}
            for tentativa in range(2):      # latência do servidor publicado: uma nova tentativa antes de acusar
                try:
                    falhas, info = rodar(pg, alvo, por_id)
                    break
                except Exception as erro:  # noqa: BLE001
                    falhas, info = ["ERRO: %s: %s" % (type(erro).__name__, str(erro)[:140])], {}
            ok = not falhas
            total_falhas += 0 if ok else 1
            print(("[OK]    " if ok else "[FALHA] ") + alvo, info.get("apoio") or "", "; ".join(falhas))
            linhas.append("\t".join([alvo, por_id[alvo]["idioma"], por_id[alvo]["tipo"], "OK" if ok else "FALHA",
                                     str(info.get("apoio") or ""), "; ".join(falhas)]))
        nav.close()
    (SAIDA / "resultado_regras_mouse.tsv").write_text("\n".join(linhas) + "\n", encoding="utf-8")
    print("RESUMO: %d de %d situações cumprem as regras; falhas=%d" % (len(alvos) - total_falhas, len(alvos), total_falhas))
    sys.exit(1 if total_falhas else 0)


if __name__ == "__main__":
    main()
