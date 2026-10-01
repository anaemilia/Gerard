#!/usr/bin/env python3
"""Bloqueios textuais mínimos contra deslocamento de concentração.

Não prova correção arquitetural. Detecta dependências concretas incompatíveis
nas fronteiras definidas pela spec de localidade e conserva a revisão humana.
"""

from __future__ import annotations

import argparse
import json
import re
import sys
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
SRC = ROOT / "src"
BASELINE = (ROOT / "documentacao" / "specs"
            / "localidade-arquitetural-baseline.json")

IMPORT_RE = re.compile(r"^\s*import\s+([^;]+);", re.MULTILINE)
CLASS_RE = re.compile(
    r"\b(?:class|interface|enum)\s+"
    r"([A-Za-z0-9_]*(?:Controller|Controlador|Service|Servico|Coordinator|"
    r"Coordenador|Manager|Gerenciador|CasoDeUso)[A-Za-z0-9_]*)\b"
)

UI_CONCRETA = ("java.awt.", "javax.swing.", "gerard.ui.")
PERSISTENCIA_CONCRETA = (
    "gerard.pesquisador.log.LoggerInteracaoGerard",
    "java.io.File",
    "java.io.Writer",
    "java.nio.file.Files",
)
SCAFFOLDING_CONCRETO = ("gerard.Scaffolding.",)
# Direção de dependência: a aplicação conhece PORTAS; os adaptadores concretos
# (gerard.infraestrutura.*) são entregues pela raiz de composição.
INFRAESTRUTURA_CONCRETA = ("gerard.infraestrutura.",)


def imports(texto: str) -> tuple[str, ...]:
    return tuple(IMPORT_RE.findall(texto))


def possui_prefixo(valores: tuple[str, ...], prefixos: tuple[str, ...]) -> bool:
    return any(valor.startswith(prefixo) for valor in valores for prefixo in prefixos)


def decisoes_ce(texto: str) -> bool:
    padroes = (
        r"\b(?:esperad[oa]|corret[oa])\s*==",
        r"==\s*(?:esperad[oa]|corret[oa])\b",
        r"\bcorrespondeA\s*\(",
        r"ResultadoAvaliacaoAcaoInstrumental\.(?:CORRETA|ERRADA)",
    )
    return any(re.search(padrao, texto) for padrao in padroes)


def auditar_fonte(caminho: Path, texto: str) -> list[str]:
    falhas: list[str] = []
    deps = imports(texto)
    nome = caminho.name
    relativo = caminho.as_posix()

    if nome.startswith("Handler"):
        proibidas = UI_CONCRETA + PERSISTENCIA_CONCRETA + SCAFFOLDING_CONCRETO
        if possui_prefixo(deps, proibidas):
            falhas.append("handler importa UI, persistência ou scaffolding concreto")

    if "/gerard/aplicacao/" in relativo:
        proibidas = UI_CONCRETA + PERSISTENCIA_CONCRETA + SCAFFOLDING_CONCRETO
        if possui_prefixo(deps, proibidas):
            falhas.append("aplicação importa UI, persistência ou scaffolding concreto")
        if possui_prefixo(deps, INFRAESTRUTURA_CONCRETA):
            falhas.append(
                "aplicação importa infraestrutura concreta (inverter por porta)"
            )

    if re.search(r"(?:Persistidor|Repositorio|Repository)", nome):
        if decisoes_ce(texto):
            falhas.append("infraestrutura contém decisão C/E")

    if CLASS_RE.search(texto):
        localidades = sum((
            possui_prefixo(deps, UI_CONCRETA),
            possui_prefixo(deps, PERSISTENCIA_CONCRETA),
            possui_prefixo(deps, SCAFFOLDING_CONCRETO),
            decisoes_ce(texto),
        ))
        if localidades >= 2:
            falhas.append(
                "coordenação genérica reúne duas ou mais localidades concretas"
            )

    return falhas


def candidatos() -> list[Path]:
    return sorted(SRC.rglob("*.java"))


def carregar_baseline() -> set[str]:
    dados = json.loads(BASELINE.read_text(encoding="utf-8"))
    return {
        f"{item['arquivo']}: {item['regra']}"
        for item in dados.get("dividas_conhecidas", [])
    }


def autoteste() -> list[str]:
    falhas: list[str] = []
    mini_main = """
        package gerard.aplicacao;
        import javax.swing.JPanel;
        import gerard.pesquisador.log.LoggerInteracaoGerard;
        class ControladorDesktop {
            boolean avaliar(int esperado, int proposto) {
                return esperado == proposto;
            }
        }
    """
    caso_portas = """
        package gerard.aplicacao.interacao;
        import gerard.aplicacao.interacao.PortaPersistenciaAcaoInstrumental;
        import gerard.aplicacao.interacao.PortaFeedbackEscolhaOperacaoRelacao;
        class CasoDeUsoEscolha {
            void executar() { }
        }
    """
    if not auditar_fonte(Path("src/gerard/aplicacao/ControladorDesktop.java"), mini_main):
        falhas.append("autoteste não rejeitou mini-Main")
    app_importa_infra = """
        package gerard.aplicacao.portabilidade;
        import gerard.infraestrutura.web.scaffolding.AdaptadorAjudaContextualWeb;
        class ServicoQualquer {
            void executar() { }
        }
    """
    if not auditar_fonte(Path("src/gerard/aplicacao/portabilidade/ServicoQualquer.java"), app_importa_infra):
        falhas.append("autoteste não rejeitou aplicação que importa infraestrutura")
    if auditar_fonte(Path("src/gerard/aplicacao/interacao/CasoDeUsoEscolha.java"), caso_portas):
        falhas.append("autoteste rejeitou caso de uso composto somente por portas")
    return falhas


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--autoteste", action="store_true")
    args = parser.parse_args()

    problemas = autoteste() if args.autoteste else []
    if not args.autoteste:
        encontrados: set[str] = set()
        for caminho in candidatos():
            texto = caminho.read_text(encoding="utf-8", errors="replace")
            for falha in auditar_fonte(caminho, texto):
                relativo = caminho.relative_to(ROOT).as_posix()
                encontrados.add(f"{relativo}: {falha}")
        baseline = carregar_baseline()
        problemas.extend(sorted(encontrados - baseline))
        problemas.extend(
            f"baseline obsoleta, remover entrada: {item}"
            for item in sorted(baseline - encontrados)
        )
        for divida in sorted(encontrados & baseline):
            print(f"[PENDENTE] {divida}")

    if problemas:
        for problema in problemas:
            print(f"[ERRO] {problema}")
        return 1
    print("APROVADO: bloqueios mínimos de localidade arquitetural.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
