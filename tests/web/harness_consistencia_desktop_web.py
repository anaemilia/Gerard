# -*- coding: utf-8 -*-
"""Harness de consistência entre as versões desktop e web do Gérard (todas as historinhas) e verificação
das rotas da API.

Parte 1 (web): para CADA situação que a regra alcança (número relativo ou transformação), em pt-BR, en e fr,
conduz a API pelas mesmas rotas hipermídia que o React usa (`acoes_disponiveis`) até 3 rejeições
consecutivas com valores/operações errados, e registra: contagem de rejeições, `chave_mensagem` a cada
passo (deve ser nula no limite), `ajuda_visual_acionada` e a `ajuda_visual` entregue (tipo, referência,
idioma, trechos). Antes do limite não pode haver apoio.

Parte 2 (consistência): compara, situação a situação, com a tabela do teste do macaco do desktop
(tests/graphical/TesteMacacoHistorinhaNumeroRelativo -> resultado_macaco.tsv, colunas de apoio): mesmo
tipo de apoio, mesma ilustração, mesmos trechos, limite em 3 rejeições nas duas versões.

Parte 3 (rotas): toda rota registrada em ServidorPrototipoWeb responde a GET/POST/PUT/DELETE sem 5xx e com
JSON; método não suportado dá 405 (nunca 500); rota /api desconhecida dá 404; todo `href` entregue em
`acoes_disponiveis` e todo caminho usado por web-poc/src/api.ts está registrado.

Uso: python harness_consistencia_desktop_web.py <url_base> <pasta_saida> [resultado_macaco.tsv ...]
"""
import csv
import json
import os
import random
import re
import sys
import urllib.error
import urllib.request
from pathlib import Path

RAIZ = Path(__file__).resolve().parents[2]
BASE = (sys.argv[1] if len(sys.argv) > 1 else "http://localhost:8093").rstrip("/")
SAIDA = Path(sys.argv[2] if len(sys.argv) > 2 else ".")
TSV_MACACO = [Path(p) for p in sys.argv[3:]]
CURADORIA = Path(os.environ.get("GERARD_CURADORIA", str(Path.home() / "Gerard" / "curadoria" / "situacoes_vergnaud_curadas.tsv")))
sorte = random.Random(2026)
falhas = []
desabilitadas = set()
chamadas = []   # sequência real de chamadas HTTP (evidência)


def http(metodo, caminho, corpo=None, bruto=None, cabecalhos=None):
    dados = bruto if bruto is not None else (json.dumps(corpo).encode("utf-8") if corpo is not None else None)
    h = {"Content-Type": "application/json"} if dados is not None else {}
    h.update(cabecalhos or {})
    req = urllib.request.Request(BASE + caminho, data=dados, headers=h, method=metodo)
    try:
        with urllib.request.urlopen(req, timeout=90) as r:
            status, texto, tipo = r.status, r.read().decode("utf-8", "replace"), r.headers.get("Content-Type", "")
    except urllib.error.HTTPError as e:
        status, texto, tipo = e.code, e.read().decode("utf-8", "replace"), e.headers.get("Content-Type", "")
    try:
        j = json.loads(texto)
    except Exception:
        j = None
    chamadas.append((metodo, caminho, status))
    return status, j, tipo, texto


def falhar(msg):
    falhas.append(msg)
    print("[FALHA]", msg)


# ------------------------------------------------------------------ catálogo de situações (mesma fonte do servidor)
def carregar_catalogo():
    linhas = list(csv.reader(open(CURADORIA, encoding="utf-8"), delimiter="\t", quoting=csv.QUOTE_NONE))
    h = [c.lstrip("# ").strip() for c in linhas[0]]
    col = {c: i for i, c in enumerate(h)}
    por_id = {}
    for r in linhas[1:]:
        r = r + [""] * (len(h) - len(r))
        por_id[r[0]] = {c: r[i] for c, i in col.items()}
    originais_ok = {i for i, s in por_id.items() if s["tipo_versao"] == "original" and s["validada"] == "true"}
    alvo = []
    for i, s in por_id.items():
        raiz = i if s["tipo_versao"] == "original" else s["versao_origem_id"]
        if raiz in originais_ok and (s["tipo_versao"] == "original" or raiz in por_id):
            alvo.append(i)
    return por_id, alvo


# ------------------------------------------------------------------ condução por situação
RELATIVAS = ("COMPARACAO_MEDIDAS", "TRANSFORMACAO_MEDIDAS", "COMPOSICAO_TRANSFORMACOES",
             "TRANSFORMACAO_RELACAO", "COMPOSICAO_RELACOES")


def acao(estado, id_, filtro=None):
    for a in estado.get("acoes_disponiveis", []):
        if a.get("id") == id_ and (filtro is None or filtro(a)):
            return a
    return None


def executar(a, extra=None):
    trilha.append(a.get("id"))
    corpo = dict(a.get("corpo") or {})
    corpo.update(extra or {})
    return http(a["metodo"], a["href"], corpo if (a.get("corpo") or extra) else None)


def estado_de(j):
    if j is None:
        return None
    return j.get("estado") if isinstance(j.get("estado"), dict) else (j if j.get("modo") else None)


def sortear_situacao(alvo_id, por_id, grupo):
    """Sorteia até cair na situação-alvo (original) e depois troca para o idioma pedido."""
    s = por_id[alvo_id]
    raiz = alvo_id if s["tipo_versao"] == "original" else s["versao_origem_id"]
    for _ in range(400):
        st, j, _, _ = http("POST", "/api/sorteios/" + grupo, {})
        if j and j.get("situacao_id") == raiz:
            break
    else:
        return None
    est = j
    if raiz != alvo_id:
        codigo = por_id[alvo_id]["idioma"]
        st, j, _, _ = http("POST", "/api/situacao/idioma", {"codigo": codigo})
        est = estado_de(j) or j
        if not est or est.get("situacao_id") != alvo_id:
            return None
    return est


def conduzir_ate_o_limite(alvo_id, por_id):
    del trilha[:]
    s = por_id[alvo_id]
    tipo = s["tipo"]
    grupo = "medidas" if tipo.endswith("_MEDIDAS") or tipo == "COMPOSICAO_TRANSFORMACAO_MEDIDAS" else "relacoes"
    est = sortear_situacao(alvo_id, por_id, "medidas" if tipo in ("COMPOSICAO_MEDIDAS", "TRANSFORMACAO_MEDIDAS", "COMPARACAO_MEDIDAS") else "relacoes")
    if est is None:
        return {"status": "NAO_SORTEADA"}
    st, j, _, _ = executar(acao(est, "ESCOLHER_CATEGORIA", lambda a: a["corpo"]["categoria"] == tipo))
    est = estado_de(j) or est
    passos = []
    antes_do_limite_com_apoio = False
    sinal_ja = set()
    engatados = set()
    op_errada = "SUBTRACAO"
    for _ in range(80):
        if (est.get("modelagem") or {}).get("concluida"):
            # Concluída sem nenhuma tentativa do participante: a web não oferece caminho de erro (rota de
            # proposta de valor ou escolha de operação) para esta situação, ao contrário do desktop.
            return {"status": "CONCLUIU_POR_ACIDENTE" if passos else "WEB_SEM_CAMINHO_DE_ERRO",
                    "trilha": list(trilha[-12:]), "passos": passos}
        # Operação (Soma/Subtração): só cliques errados.
        a = acao(est, "ESCOLHER_OPERACAO_RELACAO")
        if a is not None:
            st, j, _, _ = executar(a, {"operacao": op_errada})
            r = j or {}
            if r.get("aceita"):                      # acertou sem querer: reinicia e usa a outra opção
                op_errada = "SOMA" if op_errada == "SUBTRACAO" else "SUBTRACAO"
                est = estado_de(http("POST", "/api/reiniciar", {})[1]) or est
                continue
            est = r.get("estado") or est
            passos.append(("operacao", op_errada, r.get("chave_mensagem"), est.get("ajuda_visual_acionada")))
            if est.get("ajuda_visual_acionada"):
                break
            if est.get("ajuda_visual"):
                antes_do_limite_com_apoio = True
            continue
        # Dados conhecidos e incógnita
        a = acao(est, "POSICIONAR_CONHECIDO")
        if a is not None:
            st, j, _, _ = executar(a, {"origem_papel_id": a["corpo"]["papel_id"]})
            est = estado_de(j) or est
            continue
        a = acao(est, "ESCOLHER_SINAL_NUMERO_RELATIVO", lambda x: json.dumps(x.get("corpo")) not in sinal_ja)
        if a is not None:
            sinal_ja.add(json.dumps(a.get("corpo")))
            st, j, _, _ = executar(a)
            est = estado_de(j) or est
            continue
        a = acao(est, "ENGATAR_INCOGNITA", lambda x: ("engatar", x["corpo"]["papel_id"]) not in engatados)
        if a is not None:
            engatados.add(("engatar", a["corpo"]["papel_id"]))
            st, j, _, _ = executar(a, {"origem_papel_id": a["corpo"]["papel_id"]})
            est = estado_de(j) or est
            continue
        a = acao(est, "PROPOR_VALOR_PAPEL")
        if a is None:
            return {"status": "SEM_ACAO", "trilha": list(trilha[-12:]), "passos": passos, "acoes": [x.get("id") for x in est.get("acoes_disponiveis", [])]}
        ultimo_valor[0] = 200 + sorte.randint(0, 798)
        st, j, _, _ = executar(a, {"valor": ultimo_valor[0]})
        r = j or {}
        est = r.get("estado") or est
        passos.append(("valor", ultimo_valor[0], r.get("chave_mensagem"), r.get("limite_atingido")))
        if r.get("aceita"):
            return {"status": "CONCLUIU_POR_ACIDENTE", "passos": passos}
        if r.get("limite_atingido") or est.get("ajuda_visual_acionada"):
            break
        if est.get("ajuda_visual"):
            antes_do_limite_com_apoio = True
        if r.get("chave_mensagem"):                  # pergunta de confirmação: responde "Sim" (conta como tentativa)
            st, j, _, _ = http("POST", "/api/acoes/responder-confirmacao-valor",
                               {"papel_id": a["corpo"]["papel_id"], "confirmou": True, "valor": ultimo_valor[0]})
            r2 = j or {}
            est = r2.get("estado") or est
            passos.append(("sim", None, r2.get("chave_mensagem"), r2.get("limite_atingido")))
            if r2.get("limite_atingido") or est.get("ajuda_visual_acionada"):
                break
    apoio = (est.get("ajuda_visual") or [None])[0]
    return {"status": "OK", "trilha": list(trilha[-12:]), "passos": passos, "acionada": bool(est.get("ajuda_visual_acionada")), "apoio": apoio,
            "antes_com_apoio": antes_do_limite_com_apoio,
            "aviso_no_ultimo_passo": passos[-1][2] if passos else None}


ultimo_valor = [0]
trilha = []


# ------------------------------------------------------------------ partes do harness
def parte_web():
    por_id, alvo = carregar_catalogo()
    resultado = {}
    for i in sorted(alvo):
        s = por_id[i]
        if s["tipo"] not in RELATIVAS:
            continue
        http("POST", "/api/reiniciar", {})
        try:
            r = conduzir_ate_o_limite(i, por_id)
        except Exception as e:  # noqa: BLE001
            r = {"status": "ERRO:" + type(e).__name__ + ":" + str(e)[:80]}
        resultado[i] = r
        ok = r.get("status") == "OK" and r.get("acionada") and r.get("apoio") and not r.get("antes_com_apoio") \
            and r.get("aviso_no_ultimo_passo") is None
        print(("[OK]   " if ok else "[FALHA] ") + i, r.get("status"), (r.get("apoio") or {}).get("tipo"))
        if not ok:
            falhar("web: " + i + " => " + json.dumps({k: v for k, v in r.items() if k != "passos"}, ensure_ascii=False)[:600])
    return resultado


def ler_macaco():
    tabela = {}
    for p in TSV_MACACO:
        for r in csv.DictReader(open(p, encoding="utf-8"), delimiter="\t"):
            tabela[r["situacao"]] = r
    return tabela


def parte_consistencia(web):
    macaco = ler_macaco()
    if not macaco:
        print("[AVISO] sem tabela do macaco do desktop: consistência desktop x web não avaliada")
        return
    for i, r in web.items():
        d = macaco.get(i)
        if d is None:
            falhar("consistência: sem registro do desktop para " + i)
            continue
        if d.get("status") != "APARECEU":
            falhar("consistência: desktop não mostrou a historinha em " + i + " (" + str(d.get("status")) + ")")
            continue
        apoio = r.get("apoio") or {}
        if d.get("apoio") and d["apoio"] != apoio.get("tipo"):
            falhar("consistência: tipo de apoio difere em %s (desktop=%s web=%s)" % (i, d["apoio"], apoio.get("tipo")))
        if d.get("referencia") not in (None, "") and d["referencia"] != (apoio.get("referencia") or ""):
            falhar("consistência: ilustração difere em %s (desktop=%s web=%s)" % (i, d["referencia"], apoio.get("referencia")))
        web_trechos = " | ".join(apoio.get("trechos") or [])
        if d.get("trechos") is not None and d["trechos"] != web_trechos:
            falhar("consistência: trechos diferem em %s (desktop=%r web=%r)" % (i, d["trechos"], web_trechos))


def rotas_registradas():
    fonte = (RAIZ / "src/gerard/infraestrutura/web/ServidorPrototipoWeb.java").read_text(encoding="utf-8")
    return sorted(set(re.findall(r'createContext\("(/[^"]*)"', fonte)))


def parte_rotas(estados_vistos):
    rotas = [r for r in rotas_registradas() if r.startswith("/api/")]
    print("rotas /api registradas:", len(rotas))
    for rota in rotas:
        for metodo in ("GET", "POST", "PUT", "DELETE"):
            corpo = None if metodo in ("GET", "DELETE") else {}
            st, j, tipo, texto = http(metodo, rota, corpo)
            if st == 503 and j is not None and "erro" in j:
                desabilitadas.add(rota)          # recurso desligado neste servidor: resposta JSON explícita, não é defeito
            elif st >= 500:
                falhar("rota %s %s respondeu %d: %s" % (metodo, rota, st, texto[:120]))
            if st != 404 and j is None and "json" not in tipo and texto.strip():
                falhar("rota %s %s respondeu corpo não JSON (%d)" % (metodo, rota, st))
        st, j, _, _ = http("POST", rota, bruto=b"{isto nao e json")
        if st >= 500 and not (st == 503 and j is not None and "erro" in j):
            falhar("rota POST %s com JSON inválido respondeu %d" % (rota, st))
    st, j, _, _ = http("GET", "/api/rota-inexistente")
    if st not in (404,):
        falhar("rota /api desconhecida deveria dar 404, deu %d" % st)
    if desabilitadas:
        print("[INFO] rotas desligadas neste servidor (503 JSON explícito):", sorted(desabilitadas))
    api_ts = (RAIZ / "web-poc/src/api.ts").read_text(encoding="utf-8")
    usadas = set(re.findall(r'"(/api/[a-z0-9/\-]+)"', api_ts))
    for u in sorted(usadas):
        if not any(u == r or u.startswith(r.rstrip("/") + "/") for r in rotas):
            falhar("api.ts usa " + u + " sem rota registrada")
    for h in sorted(estados_vistos):
        if not any(h == r or h.startswith(r.rstrip("/") + "/") for r in rotas):
            falhar("href de acoes_disponiveis sem rota registrada: " + h)


def main():
    SAIDA.mkdir(parents=True, exist_ok=True)
    st, j, _, _ = http("GET", "/api/situacao")
    if st != 200:
        print("servidor indisponível:", st)
        sys.exit(2)
    web = parte_web()
    hrefs = set()
    for m, c, s in chamadas:
        hrefs.add(c.split("?")[0])
    parte_consistencia(web)
    parte_rotas({h for h in hrefs if h.startswith("/api/")})
    with open(SAIDA / "resultado_web.tsv", "w", encoding="utf-8") as f:
        f.write("situacao\tstatus\tapoio\treferencia\tidioma\ttrechos\tacionada\tpassos_sem_aviso_no_limite\n")
        for i, r in web.items():
            a = r.get("apoio") or {}
            f.write("\t".join([i, str(r.get("status")), str(a.get("tipo", "")), str(a.get("referencia", "")),
                               str(a.get("idioma", "")), " | ".join(a.get("trechos") or []),
                               str(r.get("acionada")), str(r.get("aviso_no_ultimo_passo") is None)]) + "\n")
    with open(SAIDA / "chamadas_http.log", "w", encoding="utf-8") as f:
        for m, c, s in chamadas:
            f.write("%s %s %d\n" % (m, c, s))
    total = len(web)
    ok = sum(1 for r in web.values() if r.get("status") == "OK" and r.get("acionada"))
    print("RESUMO: web com historinha no limite em %d de %d situações; falhas=%d" % (ok, total, len(falhas)))
    sys.exit(1 if falhas else 0)


if __name__ == "__main__":
    main()
