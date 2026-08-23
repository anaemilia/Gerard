#!/usr/bin/env python3
"""Valida e percorre o grafo de consulta das skills do Gérard."""

from __future__ import annotations

import argparse
import json
import sys
from collections import defaultdict, deque
from pathlib import Path


RAIZ = Path(__file__).resolve().parents[1]
REGISTRO_PADRAO = RAIZ / ".agents" / "skills" / "dependencies.json"
RELACOES_ORDENADORAS = frozenset({"requires", "constrains"})


def carregar(caminho: Path) -> dict:
    with caminho.open("r", encoding="utf-8") as arquivo:
        registro = json.load(arquivo)
    if not isinstance(registro, dict):
        raise ValueError("a raiz do registro deve ser um objeto JSON")
    return registro


def caminho_seguro(caminho: str) -> Path | None:
    candidato = (RAIZ / caminho).resolve()
    try:
        candidato.relative_to(RAIZ.resolve())
    except ValueError:
        return None
    return candidato


def detectar_ciclo(ids: set[str], arestas: list[dict]) -> list[str] | None:
    adjacencias: dict[str, list[str]] = defaultdict(list)
    for aresta in arestas:
        if aresta.get("relation") in RELACOES_ORDENADORAS:
            adjacencias[str(aresta.get("from"))].append(str(aresta.get("to")))
    visitando: set[str] = set()
    visitados: set[str] = set()
    pilha: list[str] = []

    def visitar(no: str) -> list[str] | None:
        if no in visitando:
            return pilha[pilha.index(no):] + [no]
        if no in visitados:
            return None
        visitando.add(no)
        pilha.append(no)
        for destino in sorted(adjacencias.get(no, [])):
            ciclo = visitar(destino)
            if ciclo:
                return ciclo
        pilha.pop()
        visitando.remove(no)
        visitados.add(no)
        return None

    for no in sorted(ids):
        ciclo = visitar(no)
        if ciclo:
            return ciclo
    return None


def validar(registro: dict) -> list[str]:
    erros: list[str] = []
    if registro.get("schema_version") != 1:
        erros.append("schema_version deve ser 1")
    relacoes = registro.get("relations")
    relacoes_validas = set(relacoes) if isinstance(relacoes, dict) else set()
    if not relacoes_validas:
        erros.append("relations deve declarar relações")

    nos = registro.get("nodes")
    if not isinstance(nos, list):
        return erros + ["nodes deve ser uma lista"]
    ids: set[str] = set()
    caminhos: set[str] = set()
    for indice, no in enumerate(nos):
        if not isinstance(no, dict):
            erros.append(f"nodes[{indice}] deve ser objeto")
            continue
        identificador = no.get("id")
        tipo = no.get("kind")
        caminho = no.get("path")
        proprietario = no.get("knowledge_owner")
        if not isinstance(identificador, str) or not identificador:
            erros.append(f"nodes[{indice}].id inválido")
            continue
        if identificador in ids:
            erros.append(f"id duplicado: {identificador}")
        ids.add(identificador)
        if tipo not in {"skill", "reference", "normative-reference"}:
            erros.append(f"kind inválido em {identificador}: {tipo}")
        if not isinstance(proprietario, str) or not proprietario:
            erros.append(f"knowledge_owner ausente em {identificador}")
        if not isinstance(caminho, str) or not caminho:
            erros.append(f"path ausente em {identificador}")
            continue
        normalizado = Path(caminho).as_posix()
        if normalizado in caminhos:
            erros.append(f"path duplicado: {normalizado}")
        caminhos.add(normalizado)
        absoluto = caminho_seguro(normalizado)
        if absoluto is None:
            erros.append(f"path fora do projeto: {normalizado}")
        elif not absoluto.is_file():
            erros.append(f"arquivo inexistente: {normalizado}")
        if tipo == "skill":
            esperado = f".agents/skills/{identificador}/SKILL.md"
            if normalizado != esperado:
                erros.append(f"skill {identificador} deve apontar para {esperado}")

    encontradas = {
        caminho.relative_to(RAIZ).as_posix()
        for caminho in (RAIZ / ".agents" / "skills").glob("*/SKILL.md")
    }
    registradas = {
        Path(no["path"]).as_posix()
        for no in nos
        if isinstance(no, dict) and no.get("kind") == "skill" and no.get("path")
    }
    for caminho in sorted(encontradas - registradas):
        erros.append(f"skill sem nó: {caminho}")
    for caminho in sorted(registradas - encontradas):
        erros.append(f"nó sem skill: {caminho}")

    arestas = registro.get("edges")
    if not isinstance(arestas, list):
        return erros + ["edges deve ser uma lista"]
    assinaturas: set[tuple[str, str, str]] = set()
    for indice, aresta in enumerate(arestas):
        if not isinstance(aresta, dict):
            erros.append(f"edges[{indice}] deve ser objeto")
            continue
        origem = str(aresta.get("from"))
        destino = str(aresta.get("to"))
        relacao = str(aresta.get("relation"))
        assinatura = (origem, relacao, destino)
        if origem not in ids:
            erros.append(f"origem desconhecida em edges[{indice}]: {origem}")
        if destino not in ids:
            erros.append(f"destino desconhecido em edges[{indice}]: {destino}")
        if origem == destino:
            erros.append(f"auto-relação em edges[{indice}]")
        if relacao not in relacoes_validas:
            erros.append(f"relação desconhecida em edges[{indice}]: {relacao}")
        if not str(aresta.get("reason", "")).strip():
            erros.append(f"reason ausente em edges[{indice}]")
        if assinatura in assinaturas:
            erros.append(f"aresta duplicada: {assinatura}")
        assinaturas.add(assinatura)
    if not erros:
        ciclo = detectar_ciclo(ids, arestas)
        if ciclo:
            erros.append("ciclo em requires/constrains: " + " -> ".join(ciclo))
    return erros


def percorrer(registro: dict, sementes: list[str], incluir_relacionadas: bool):
    ids = {no["id"] for no in registro["nodes"]}
    desconhecidas = sorted(set(sementes) - ids)
    if desconhecidas:
        raise ValueError("sementes desconhecidas: " + ", ".join(desconhecidas))
    adjacencias: dict[str, list[dict]] = defaultdict(list)
    for aresta in registro["edges"]:
        adjacencias[aresta["from"]].append(aresta)
    pais: dict[str, tuple[str, str] | None] = {}
    fila: deque[tuple[str, int]] = deque()
    condicionais: list[tuple[str, str, str]] = []
    compartilhadas: list[tuple[str, str, str]] = []
    niveis: list[list[str]] = []
    for semente in sementes:
        if semente not in pais:
            pais[semente] = None
            fila.append((semente, 0))
    while fila:
        atual, nivel = fila.popleft()
        while len(niveis) <= nivel:
            niveis.append([])
        niveis[nivel].append(atual)
        for aresta in sorted(adjacencias.get(atual, []),
                             key=lambda item: (item["relation"], item["to"])):
            destino = aresta["to"]
            relacao = aresta["relation"]
            if relacao not in RELACOES_ORDENADORAS and not incluir_relacionadas:
                condicionais.append((atual, relacao, destino))
                continue
            if destino not in pais:
                pais[destino] = (atual, relacao)
                fila.append((destino, nivel + 1))
            elif pais[destino] != (atual, relacao):
                compartilhadas.append((atual, relacao, destino))
    return niveis, pais, compartilhadas, condicionais


def ordem_leitura(registro: dict, descobertos: set[str]) -> list[str]:
    adjacencias: dict[str, list[str]] = defaultdict(list)
    for aresta in registro["edges"]:
        if (aresta["relation"] in RELACOES_ORDENADORAS
                and aresta["from"] in descobertos and aresta["to"] in descobertos):
            adjacencias[aresta["from"]].append(aresta["to"])
    visitados: set[str] = set()
    ordem: list[str] = []

    def visitar(no: str) -> None:
        if no in visitados:
            return
        visitados.add(no)
        for dependencia in sorted(adjacencias.get(no, [])):
            visitar(dependencia)
        ordem.append(no)

    for no in sorted(descobertos):
        visitar(no)
    return ordem


def imprimir_percurso(registro: dict, sementes: list[str], incluir: bool) -> None:
    niveis, pais, compartilhadas, condicionais = percorrer(registro, sementes, incluir)
    print("\nBusca em largura:")
    for indice, nivel in enumerate(niveis):
        print(f"- nível {indice}: {', '.join(sorted(nivel))}")
    print("\nÁrvore de percurso da tarefa:")
    filhos: dict[str, list[tuple[str, str]]] = defaultdict(list)
    for no, pai in pais.items():
        if pai:
            filhos[pai[0]].append((no, pai[1]))

    def escrever(no: str, profundidade: int, relacao: str | None = None) -> None:
        rotulo = f"[{relacao}] " if relacao else ""
        print("  " * profundidade + f"- {rotulo}{no}")
        for filho, relacao_filho in sorted(filhos.get(no, [])):
            escrever(filho, profundidade + 1, relacao_filho)

    for raiz in sorted(no for no, pai in pais.items() if pai is None):
        escrever(raiz, 0)
    if compartilhadas:
        print("Referências compartilhadas:")
        for origem, relacao, destino in sorted(set(compartilhadas)):
            print(f"- {origem} -[{relacao}]-> {destino}")
    if condicionais:
        print("Relações condicionais conforme o escopo:")
        for origem, relacao, destino in sorted(set(condicionais)):
            print(f"- {origem} -[{relacao}]-> {destino}")
    print("\nOrdem de leitura:")
    for indice, no in enumerate(ordem_leitura(registro, set(pais)), 1):
        print(f"{indice}. {no}")


def imprimir_mermaid(registro: dict) -> None:
    print("flowchart TD")
    chaves = {}
    for indice, no in enumerate(registro["nodes"]):
        chave = f"N{indice}"
        chaves[no["id"]] = chave
        print(f'    {chave}["{no["id"]}"]')
    for aresta in registro["edges"]:
        print(f'    {chaves[aresta["from"]]} -->|"{aresta["relation"]}"| '
              f'{chaves[aresta["to"]]}')


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--registro", type=Path, default=REGISTRO_PADRAO)
    parser.add_argument("--validar", action="store_true")
    parser.add_argument("--percorrer", nargs="+", metavar="SKILL")
    parser.add_argument("--incluir-relacionadas", action="store_true")
    parser.add_argument("--mermaid", action="store_true")
    argumentos = parser.parse_args()
    try:
        registro = carregar(argumentos.registro)
    except (OSError, ValueError, json.JSONDecodeError) as erro:
        print(f"ERRO: não foi possível carregar o grafo: {erro}", file=sys.stderr)
        return 1
    erros = validar(registro)
    if erros:
        print("Grafo de skills inválido:", file=sys.stderr)
        for erro in erros:
            print(f"- {erro}", file=sys.stderr)
        return 1
    if argumentos.validar or (not argumentos.percorrer and not argumentos.mermaid):
        print(f"Grafo de skills válido: {len(registro['nodes'])} nós, "
              f"{len(registro['edges'])} relações.")
    if argumentos.percorrer:
        try:
            imprimir_percurso(registro, argumentos.percorrer,
                              argumentos.incluir_relacionadas)
        except ValueError as erro:
            print(f"ERRO: {erro}", file=sys.stderr)
            return 1
    if argumentos.mermaid:
        imprimir_mermaid(registro)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
