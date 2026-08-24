#!/usr/bin/env python3
"""Valida a fonte canônica das situações curadas sem reinterpretá-las."""

from __future__ import annotations

import argparse
import csv
import hashlib
import json
from collections import Counter
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
CANONICO = ROOT / "src/gerard/campoaditivo/dados/situacoes_vergnaud.tsv"
MANIFESTO = ROOT / "documentacao/curadoria/manifesto_curadoria_canonica.json"
DUPLICATA_LEGADA = ROOT / "dados/situacoes_vergnaud.tsv"

CABECALHO_ESPERADO = [
    "# id", "situacao_grupo_id", "tipo_versao", "versao_origem_id",
    "validada", "idioma", "tipo", "contexto", "enunciado", "fonte",
    "subtipo", "estado_inicial", "transformacao", "sinal_transformacao",
    "estado_final", "quantidade_1", "quantidade_2", "resultado", "referido",
    "referendo", "valor_relativo", "sinal_valor_relativo",
    "termo_desconhecido", "representacao_visual", "observacoes",
    "personagem_1", "personagem_2", "personagem_3", "fragmento_texto_1",
    "fragmento_texto_2", "fragmento_texto_3", "fragmento_texto_4",
    "fragmento_texto_5", "fragmento_texto_6", "operacao_relacao",
    "estado_intermediario", "operacao_estado_transformacao",
]

CATEGORIAS_CANONICAS = {
    "COMPOSICAO_MEDIDAS": 56,
    "TRANSFORMACAO_MEDIDAS": 86,
    "COMPARACAO_MEDIDAS": 20,
    "COMPOSICAO_TRANSFORMACOES": 32,
    "TRANSFORMACAO_RELACAO": 8,
    "COMPOSICAO_RELACOES": 8,
}


def sha256(caminho: Path) -> str:
    digest = hashlib.sha256()
    with caminho.open("rb") as arquivo:
        for bloco in iter(lambda: arquivo.read(1024 * 1024), b""):
            digest.update(bloco)
    return digest.hexdigest().upper()


def falhar(mensagem: str) -> None:
    raise SystemExit(f"FALHA: {mensagem}")


def ler_tsv(caminho: Path) -> list[list[str]]:
    try:
        with caminho.open("r", encoding="utf-8", newline="") as arquivo:
            return list(csv.reader(arquivo, delimiter="\t"))
    except UnicodeDecodeError as erro:
        falhar(f"{caminho} não está em UTF-8: {erro}")


def validar(comparar_ativo: bool) -> None:
    if not CANONICO.is_file():
        falhar(f"fonte canônica ausente: {CANONICO}")
    if not MANIFESTO.is_file():
        falhar(f"manifesto ausente: {MANIFESTO}")
    if DUPLICATA_LEGADA.exists():
        falhar(f"cópia versionada redundante voltou a existir: {DUPLICATA_LEGADA}")

    manifesto = json.loads(MANIFESTO.read_text(encoding="utf-8"))
    hash_atual = sha256(CANONICO)
    hash_esperado = str(manifesto["sha256"]).upper()
    if hash_atual != hash_esperado:
        falhar(f"hash do arquivo canônico mudou: {hash_atual} != {hash_esperado}")

    linhas = ler_tsv(CANONICO)
    if not linhas:
        falhar("arquivo canônico vazio")
    if linhas[0] != CABECALHO_ESPERADO:
        falhar("cabeçalho diferente do esquema curado de 37 colunas")

    dados = linhas[1:]
    if len(dados) != int(manifesto["linhas_de_dados"]):
        falhar(f"quantidade de linhas alterada: {len(dados)}")
    for numero_linha, campos in enumerate(dados, start=2):
        if len(campos) != len(CABECALHO_ESPERADO):
            falhar(f"linha {numero_linha} tem {len(campos)} colunas; esperado: 37")

    ids = [linha[0] for linha in dados]
    if any(not identificador for identificador in ids):
        falhar("há situação sem id")
    duplicados = sorted(identificador for identificador, total in Counter(ids).items() if total > 1)
    if duplicados:
        falhar(f"ids duplicados: {', '.join(duplicados)}")

    categorias = Counter(linha[6] for linha in dados)
    if dict(categorias) != CATEGORIAS_CANONICAS:
        falhar(f"categorias ou contagens divergentes: {dict(categorias)}")

    validadas = sum(1 for linha in dados if linha[4].lower() == "true")
    if validadas != int(manifesto["linhas_validadas"]):
        falhar(f"quantidade de situações validadas alterada: {validadas}")

    if comparar_ativo:
        ativo = Path(manifesto["fonte_humana_ativa"])
        if not ativo.is_file():
            falhar(f"fonte humana ativa ausente: {ativo}")
        if ativo.read_bytes() != CANONICO.read_bytes():
            falhar("fonte canônica não é cópia literal da fonte humana ativa")

    print("APROVADO: fonte canônica íntegra, 210 situações, 37 colunas e 6 categorias.")
    print(f"SHA256={hash_atual}")


def main() -> None:
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "--comparar-ativo",
        action="store_true",
        help="exige igualdade byte a byte com o arquivo humano ativo",
    )
    args = parser.parse_args()
    validar(args.comparar_ativo)


if __name__ == "__main__":
    main()
