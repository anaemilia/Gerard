from __future__ import annotations
import csv
import re
import sys
import unicodedata
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
TEX = ROOT / "main.tex"
REGISTRY = ROOT / "dados" / "decisoes_metodologicas.csv"


def normalize(text: str) -> str:
    text = unicodedata.normalize("NFKD", text)
    text = "".join(ch for ch in text if not unicodedata.combining(ch))
    text = re.sub(r"\\[a-zA-Z@]+\*?(?:\[[^\]]*\])?", " ", text)
    text = text.replace("{", " ").replace("}", " ")
    text = re.sub(r"\s+", " ", text)
    return text.casefold().strip()


def section(tex: str, start_pattern: str, end_pattern: str | None) -> str:
    start = re.search(start_pattern, tex, flags=re.S)
    if not start:
        raise ValueError(f"Seção inicial não encontrada: {start_pattern}")
    begin = start.end()
    if end_pattern is None:
        return tex[begin:]
    end = re.search(end_pattern, tex[begin:], flags=re.S)
    return tex[begin: begin + end.start()] if end else tex[begin:]


def contains(haystack: str, needle: str) -> bool:
    return normalize(needle) in normalize(haystack)


def main() -> int:
    tex = TEX.read_text(encoding="utf-8")
    abstract = section(tex, r"\\begin\{abstract\}", r"\\end\{abstract\}")
    results = section(tex, r"\\section\{Resultados(?: preliminares)?\}", r"\\section\{Discussão\}")
    discussion = section(tex, r"\\section\{Discussão\}", r"\\section\{Ameaças à validade\}")

    errors: list[str] = []
    rows = list(csv.DictReader(REGISTRY.open(encoding="utf-8")))
    active = [r for r in rows if r.get("ativa", "").strip().upper() == "SIM"]
    for row in active:
        checks = (
            ("resumo", abstract, row["resumo_token"]),
            ("resultados", results, row["resultados_token"]),
            ("discussão", discussion, row["discussao_token"]),
        )
        for label, content, token in checks:
            if not token.strip():
                errors.append(f"{row['id']}: token vazio para {label}")
            elif not contains(content, token):
                errors.append(
                    f"{row['id']}: decisão metodológica não retroalimentada em {label}. "
                    f"Token esperado: {token!r}"
                )

    if errors:
        print("FALHA NA REGRESSÃO DOCUMENTAL DO ARTIGO", file=sys.stderr)
        for err in errors:
            print(f"- {err}", file=sys.stderr)
        print(
            "Atualize resumo, resultados e discussão, ou revise o registro em "
            "dados/decisoes_metodologicas.csv.", file=sys.stderr,
        )
        return 1

    print(
        f"Regressão documental aprovada: {len(active)} decisões metodológicas "
        "estão refletidas no resumo, nos resultados e na discussão."
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
