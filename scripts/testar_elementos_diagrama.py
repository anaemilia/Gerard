#!/usr/bin/env python3
"""Compila e executa um teste de fumaça headless das classes gráficas extraídas."""
from pathlib import Path
import shutil
import subprocess
import sys

ROOT = Path(__file__).resolve().parents[1]
BUILD = ROOT / "build" / "classes"
TEST_SRC = ROOT / "scripts" / "testes" / "TesteElementosDiagrama.java"
TEST_OUT = ROOT / "build" / "test-classes"


def executar(cmd):
    resultado = subprocess.run(cmd, cwd=ROOT, text=True, stdout=subprocess.PIPE, stderr=subprocess.STDOUT)
    print(resultado.stdout)
    if resultado.returncode != 0:
        raise SystemExit(resultado.returncode)


if shutil.which("ant") is None or shutil.which("javac") is None or shutil.which("java") is None:
    print("ERRO: ant, javac e java são necessários para esta regressão.")
    raise SystemExit(2)

executar(["ant", "clean", "jar"])
TEST_OUT.mkdir(parents=True, exist_ok=True)
executar(["javac", "-encoding", "UTF-8", "-cp", str(BUILD), "-d", str(TEST_OUT), str(TEST_SRC)])
executar(["java", "-Djava.awt.headless=true", "-cp", f"{BUILD}:{TEST_OUT}", "TesteElementosDiagrama"])
print("REGRESSÃO DOS ELEMENTOS GRÁFICOS: APROVADA")
