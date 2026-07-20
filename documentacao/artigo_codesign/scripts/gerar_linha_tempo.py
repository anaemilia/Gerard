from __future__ import annotations
import csv
from collections import Counter
from pathlib import Path

root = Path(__file__).resolve().parents[1]
source = root / "dados" / "versoes_consistentes.csv"
out = root / "secoes" / "linha_tempo_gerada.tex"

LABELS = {
    "ESTRUTURAL_GENERALIZACAO": "Ciclos estruturais e de generalização",
    "CONSISTENCIA_TEORICO_METODOLOGICA": "Ciclos de consistência teórica e metodológica",
    "REFINAMENTO_VISUAL_OPERACIONAL": "Ciclos de refinamento visual e operacional",
}
ORDER = list(LABELS)

def esc(s: str) -> str:
    replacements = {
        "\\": r"\textbackslash{}", "&": r"\&", "%": r"\%", "$": r"\$",
        "#": r"\#", "_": r"\_", "{": r"\{", "}": r"\}",
        "~": r"\textasciitilde{}", "^": r"\textasciicircum{}",
    }
    for a, b in replacements.items():
        s = s.replace(a, b)
    return s

rows = list(csv.DictReader(source.open(encoding="utf-8")))
counts = Counter(r["classe"] for r in rows)
lines = [
    r"\begin{table}[ht]", r"\centering",
    r"\caption{Distribuição dos ciclos de decisão por classe analítica.}",
    r"\label{tab:classes-ciclos}",
    r"\begin{tabular}{lr}", r"\toprule",
    r"\textbf{Classe} & \textbf{Quantidade} \\", r"\midrule",
]
for key in ORDER:
    lines.append(f"{esc(LABELS[key])} & {counts[key]} \\\\")
lines += [r"\bottomrule", r"\end{tabular}", r"\end{table}"]

for key in ORDER:
    group = [r for r in rows if r["classe"] == key]
    lines += [
        f"\\subsubsection{{{esc(LABELS[key])}}}",
        r"\begin{landscape}", r"\begingroup", r"\setlength{\tabcolsep}{3pt}", r"\small",
        r"\begin{longtable}{p{1.0cm}p{1.9cm}p{4.0cm}p{5.0cm}p{6.2cm}p{5.7cm}}",
        f"\\caption{{{esc(LABELS[key])} incorporados em versões consistentes.}}\\\\",
        r"\toprule",
        r"\textbf{Ciclo} & \textbf{Data} & \textbf{Objetivo} & \textbf{Proposta inicial} & \textbf{Intervenção do pesquisador} & \textbf{Decisão consolidada} \\",
        r"\midrule", r"\endfirsthead", r"\toprule",
        r"\textbf{Ciclo} & \textbf{Data} & \textbf{Objetivo} & \textbf{Proposta inicial} & \textbf{Intervenção do pesquisador} & \textbf{Decisão consolidada} \\",
        r"\midrule", r"\endhead",
    ]
    for row in group:
        lines.append("{} & {} & {} & {} & {} & {} \\\\".format(
            esc(row["id"]), esc(row["data"]), esc(row["objetivo"]), esc(row["proposta_ia"]),
            esc(row["intervencao_pesquisador"]), esc(row["impacto"])))
        lines.append(r"\addlinespace[2pt]")
    lines += [r"\bottomrule", r"\end{longtable}", r"\endgroup", r"\end{landscape}"]

out.write_text("\n".join(lines), encoding="utf-8")
print(f"Gerado: {out} ({len(rows)} ciclos; {dict(counts)})")
