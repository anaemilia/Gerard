#!/usr/bin/env python3
"""Verificação determinística do princípio da localidade do conhecimento (decisões em um só lugar).

Cada regra aponta o dono legítimo de um conhecimento e falha se uma cópia dessa decisão aparecer em outro
lugar (Main, clientes Swing/React, serviços web). Complementa os testes de comportamento por regra; os
limites marcados como dívida só podem diminuir (ratchet).

Uso: python scripts/verificar_localidade_conhecimento.py
"""
import re
import sys
from pathlib import Path

RAIZ = Path(__file__).resolve().parents[1]
SRC = RAIZ / "src"
violacoes = []


def codigo(caminho: Path) -> str:
    """Texto sem comentários e sem literais de texto, para não confundir documentação com decisão."""
    t = caminho.read_text(encoding="utf-8", errors="replace")
    t = re.sub(r"/\*.*?\*/", "", t, flags=re.S)
    t = re.sub(r"//[^\n]*", "", t)
    return t


def arquivos(padrao: str, base: Path = SRC):
    return sorted(base.rglob(padrao))


def contar(texto: str, expressao: str) -> int:
    return len(re.findall(expressao, texto))


def exigir(ok: bool, mensagem: str):
    print(("[OK]    " if ok else "[FALHA] ") + mensagem)
    if not ok:
        violacoes.append(mensagem)


# R1 — a fase da atividade (modelagem/exploratória) é conhecimento da tentativa.
DONOS_DA_FASE = {"TentativaModelagemAditiva.java", "PapelQuantitativo.java", "TentativaCorrente.java",
                 "FaseDaTentativa.java"}
fora = []
for f in arquivos("*.java"):
    if f.name in DONOS_DA_FASE:
        continue
    c = codigo(f)
    if re.search(r"\bestaEncerradaPorConclusao\s*\(|\bencerrarPorConclusao\s*\(", c):
        fora.append(f.relative_to(RAIZ).as_posix())
exigir(not fora, "R1: só a tentativa e os papéis lêem/alteram o encerramento por conclusão (fora: %s)" % fora)

# R2 — a Main liga referências de método à tentativa; não decide fase com lambda/classe anônima.
main = codigo(SRC / "Main.java")
exigir(contar(main, r"new\s+java\.util\.function\.BooleanSupplier\s*\(") == 0,
       "R2: a Main não cria BooleanSupplier anônimo (decisões de fase ficam na tentativa)")
exigir(contar(main, r"tentativaModelagemAtual\s*\.\s*admite") == 0,
       "R2: a Main não consulta admite*() direto na tentativa; usa tentativaCorrente")

# R3 — a decisão do apoio visual é do domínio/aplicação; nenhum cliente decide.
exigir(contar(main, r"\bPoliticaApoioVisual\b") == 0, "R3: a Main não usa PoliticaApoioVisual (usa DecisorAjudaVisual)")
DIVIDA_REPERTORIO_NA_MAIN = 0   # a explicação da categoria passou a ter dono (ExplicacaoNarrativaDaCategoria)
n = contar(main, r"\.selecionarRepertorioAjudaVisual\s*\(")
exigir(n <= DIVIDA_REPERTORIO_NA_MAIN, "R3: seleção de repertório na Main <= %d (hoje %d)" % (DIVIDA_REPERTORIO_NA_MAIN, n))

# R4 — o idioma não decide: gatilho, apoio e ilustração não conhecem idioma.
for nome in ("PoliticaApoioVisual.java", "DecisorAjudaVisual.java"):
    f = next(iter(arquivos(nome)))
    c = codigo(f)
    exigir(not re.search(r"Idioma|getCodigoIdioma|\"pt-BR\"|\"en\"|\"fr\"", c),
           "R4: %s não conhece idioma" % nome)

# R5 — clientes só renderizam a projeção, por tipo; nada de categoria/id de situação.
proibidos = r"COMPOSICAO_|TRANSFORMACAO_|COMPARACAO_|\"PO_|\"IN_|\"FR_|TipoSituacaoAditiva"
clientes = [RAIZ / "web-poc/src/HistorinhaPassiva.tsx"] + arquivos("Renderizador*Swing.java", SRC / "gerard/ui/ajuda") \
    + arquivos("RotuloAnimacaoComLegendas.java", SRC / "gerard/ui/ajuda") \
    + arquivos("PainelHistorinhaTextual.java", SRC / "gerard/ui/ajuda")
for f in clientes:
    exigir(not re.search(proibidos, codigo(f)), "R5: %s não decide por categoria/id" % f.name)

# R6 — o texto traduzido é argumento da cena, escolhido pelo renderizador da categoria (não pelos clientes).
exigir("argumentosHistorinha" in codigo(SRC / "gerard/campoaditivo/diagrama/servico/RenderizadorDiagramaAditivo.java"),
       "R6: o método polimórfico argumentosHistorinha(situacao, idioma) existe no renderizador da categoria")

# R7 — o vermelho de erro tem um só dono por plataforma (UITemaGerard no Swing) e o web espelha o mesmo valor.
soltos = []
for f in arquivos("*.java"):
    if f.name in ("UITemaGerard.java", "CoresRepresentacaoGerard.java", "TelaCuradoriaSituacoes.java"):
        continue
    if re.search(r"VERMELHO(_ESCURO)?\s*=\s*new\s+Color", codigo(f)):
        soltos.append(f.relative_to(RAIZ).as_posix())
exigir(not soltos, "R7: nenhum vermelho de erro/sinal definido fora do UITemaGerard (soltos: %s)" % soltos)
tema = (SRC / "gerard/ui/UITemaGerard.java").read_text(encoding="utf-8")
fonte = (SRC / "gerard/recursos/tema/feedback.properties").read_text(encoding="utf-8")
exigir(not re.search(r"COR_ERRO\w*\s*=\s*new\s+Color", tema),
       "R7: UITemaGerard não tem hex de erro próprio (lê feedback.properties)")
css_gerado = (RAIZ / "web-poc/tokens-feedback.css").read_text(encoding="utf-8")
valores = dict(l.split("=", 1) for l in fonte.splitlines() if l.strip() and not l.startswith("#"))
esperado = (":root { --erro:#%s; --erro-fundo:#%s; --erro-texto:#%s; }"
            % (valores["erro"].strip(), valores["erro_fundo"].strip(), valores["erro_texto"].strip()))
exigir(esperado in css_gerado, "R7: web-poc/tokens-feedback.css gerado a partir da mesma fonte (rode npm run tokens)")
exigir("--erro:" not in (RAIZ / "web-poc/styles.css").read_text(encoding="utf-8"),
       "R7: styles.css não define --erro (vem do CSS gerado)")

print()
if violacoes:
    print("%d violação(ões) da localidade do conhecimento." % len(violacoes))
    sys.exit(1)
print("Localidade do conhecimento verificada.")
