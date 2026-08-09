#!/usr/bin/env python3
"""Compila o Gerard e executa os testes Java autocontidos no Windows.

Este verificador não substitui o build Ant. Ele separa falhas do código-fonte
de falhas de portabilidade da configuração NetBeans/Ant.
"""

from __future__ import annotations

import os
from pathlib import Path
import re
import shutil
import subprocess
import sys


ROOT = Path(__file__).resolve().parents[1]
WORK = ROOT / "tmp" / "linha-base-windows"
CLASSES = WORK / "classes"
TEST_CLASSES = WORK / "test-classes"
LIBS = sorted((ROOT / "lib").glob("*.jar"))
TESTES_COM_INTERFACE_GRAFICA = {
    "TesteAbaMontagem",
    "TesteBloqueioDinamicoIdiomaCuradoria",
    "TesteConclusaoAcionaNovaSituacao",
    "TesteInicializacaoSemCategoria",
}

if hasattr(sys.stdout, "reconfigure"):
    sys.stdout.reconfigure(encoding="utf-8", errors="replace")


def executar(comando: list[str], *, timeout: int = 120) -> subprocess.CompletedProcess[str]:
    return subprocess.run(
        comando,
        cwd=ROOT,
        text=True,
        encoding="utf-8",
        errors="replace",
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        timeout=timeout,
    )


def nome_classe_teste(arquivo: Path) -> str:
    texto = arquivo.read_text(encoding="utf-8", errors="replace")
    pacote = re.search(r"^\s*package\s+([\w.]+)\s*;", texto, re.MULTILINE)
    return f"{pacote.group(1)}.{arquivo.stem}" if pacote else arquivo.stem


def criar_argfile(nome: str, arquivos: list[Path]) -> Path:
    destino = WORK / nome
    destino.write_text(
        "\n".join(f'"{arquivo.as_posix()}"' for arquivo in arquivos),
        encoding="utf-8",
    )
    return destino


def main() -> int:
    if os.name != "nt":
        print("ERRO: este verificador é específico para Windows.")
        return 2

    javac = shutil.which("javac")
    java = shutil.which("java")
    if not javac or not java:
        print("ERRO: java e javac precisam estar disponíveis no PATH.")
        return 2

    if WORK.exists():
        shutil.rmtree(WORK)
    CLASSES.mkdir(parents=True)
    TEST_CLASSES.mkdir(parents=True)

    libs_trabalho = WORK / "lib"
    libs_trabalho.mkdir(parents=True)
    dependencias_trabalho: list[Path] = []
    for jar in LIBS:
        copia = libs_trabalho / jar.name
        shutil.copyfile(jar, copia)
        dependencias_trabalho.append(copia)

    fontes = sorted((ROOT / "src").rglob("*.java"))
    testes = sorted((ROOT / "tests" / "java").rglob("Teste*.java"))
    argfile_fontes = criar_argfile("fontes.txt", fontes)
    argfile_testes = criar_argfile("testes.txt", testes)
    classpath_dependencias = os.pathsep.join(str(jar) for jar in dependencias_trabalho)

    print(f"JAVA={java}")
    print(f"JAVAC={javac}")
    print(f"FONTES={len(fontes)}")
    print(f"TESTES_JAVA={len(testes)}")
    print(f"DEPENDENCIAS={len(LIBS)}")

    compilacao = executar([
        javac,
        "-encoding", "UTF-8",
        "-source", "8",
        "-target", "8",
        "-cp", classpath_dependencias,
        "-d", str(CLASSES),
        f"@{argfile_fontes}",
    ], timeout=300)
    print(compilacao.stdout)
    if compilacao.returncode != 0 or "An exception has occurred in the compiler" in compilacao.stdout:
        print("RESULTADO_COMPILACAO=FALHA")
        return 1
    print("RESULTADO_COMPILACAO=OK")

    for recurso in (ROOT / "src").rglob("*"):
        if recurso.is_file() and recurso.suffix != ".java":
            destino = CLASSES / recurso.relative_to(ROOT / "src")
            destino.parent.mkdir(parents=True, exist_ok=True)
            shutil.copy2(recurso, destino)

    classpath_testes = os.pathsep.join((str(CLASSES), classpath_dependencias))
    compilacao_testes = executar([
        javac,
        "-encoding", "UTF-8",
        "-source", "8",
        "-target", "8",
        "-cp", classpath_testes,
        "-d", str(TEST_CLASSES),
        f"@{argfile_testes}",
    ], timeout=300)
    print(compilacao_testes.stdout)
    if compilacao_testes.returncode != 0:
        print("RESULTADO_COMPILACAO_TESTES=FALHA")
        return 1
    print("RESULTADO_COMPILACAO_TESTES=OK")

    classpath_execucao = os.pathsep.join((str(CLASSES), str(TEST_CLASSES), classpath_dependencias))
    aprovados: list[str] = []
    graficos_nao_executados: list[str] = []
    obsoletos: list[tuple[str, str]] = []
    falhas: list[tuple[str, str]] = []
    for teste in testes:
        classe = nome_classe_teste(teste)
        if classe in TESTES_COM_INTERFACE_GRAFICA:
            graficos_nao_executados.append(classe)
            print(f"[GRÁFICO] {classe}: compilado; execução requer ambiente com display")
            continue
        try:
            resultado = executar([
                java,
                "-Djava.awt.headless=true",
                "-cp", classpath_execucao,
                classe,
            ], timeout=45)
        except subprocess.TimeoutExpired:
            falhas.append((classe, "TIMEOUT após 45 segundos"))
            print(f"[FALHA] {classe}: TIMEOUT")
            continue
        if resultado.returncode == 0:
            aprovados.append(classe)
            print(f"[OK] {classe}")
        else:
            resumo = " | ".join(resultado.stdout.strip().splitlines()[-4:])
            falhas.append((classe, resumo))
            print(f"[FALHA] {classe}: {resumo}")

    print(f"TESTES_APROVADOS={len(aprovados)}")
    print(f"TESTES_GRAFICOS_NAO_EXECUTADOS={len(graficos_nao_executados)}")
    print(f"TESTES_OBSOLETOS={len(obsoletos)}")
    print(f"TESTES_REPROVADOS={len(falhas)}")
    for classe, motivo in obsoletos:
        print(f"TESTE_OBSOLETO={classe} :: {motivo}")
    for classe in graficos_nao_executados:
        print(f"TESTE_GRAFICO={classe} :: compilado; requer ambiente com display")
    for classe, motivo in falhas:
        print(f"FALHA_TESTE={classe} :: {motivo}")
    return 1 if falhas or obsoletos else 0


if __name__ == "__main__":
    raise SystemExit(main())
