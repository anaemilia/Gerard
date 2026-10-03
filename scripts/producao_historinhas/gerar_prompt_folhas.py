#!/usr/bin/env python3
"""Gera prompt-codex-gerar-folhas-historinhas.md a partir do rascunho das 13 historias pendentes."""
import json
import re
import unicodedata
from pathlib import Path

RAIZ = Path(__file__).resolve().parents[2]
RASCUNHO = RAIZ / "documentacao/producao_historinhas/rascunhos/historias_pendentes_rascunho.json"
SAIDA = RAIZ / "prompt-codex-gerar-folhas-historinhas.md"
REFERENCIA = (r"C:\Users\cecomp\Documents\aemq\Gerard\videos\composicao_transformacoes_historias_2026-08-21"
              r"\storyboard_01_joao_bilas.png")
PASTA_SAIDA = (r"C:\Users\cecomp\Documents\aemq\git\Gerard\documentacao\producao_historinhas\folhas")


def slug(texto: str) -> str:
    texto = unicodedata.normalize("NFKD", texto).encode("ascii", "ignore").decode().lower()
    return re.sub(r"[^a-z0-9]+", "_", texto).strip("_")


def main() -> None:
    dados = json.loads(RASCUNHO.read_text(encoding="utf-8"))
    l = []
    l.append("# Prompt para o Codex — gerar as 13 folhas ilustradas das historinhas pendentes\n")
    l.append("Você tem a ferramenta de geração de imagem (`image_gen`, a mesma usada nas historinhas existentes "
             "do Gérard). A tarefa é **somente gerar as 13 folhas** abaixo e salvá-las numa pasta. Não altere "
             "código, não faça commit nem push, não mexa em `C:\\gd` nem no Render.\n")
    l.append("## Formato (igual às folhas existentes)\n")
    l.append("Cada folha é **uma imagem 16:9 com 4 quadros empilhados, um por linha**, cada quadro uma faixa "
             "larga e panorâmica (como `" + REFERENCIA + "`, 1672×941). **Abra essa folha como referência de "
             "estilo e de enquadramento antes de começar.**\n")
    l.append("Regras para todas as folhas:\n")
    for regra in (
        "Estilo: ilustração de livro infantil, pintada à mão, paleta quente e terrosa (ocre, terracota, verdes "
        "suaves), cenário cotidiano brasileiro — o mesmo das folhas existentes.",
        "**Os mesmos personagens nos 4 quadros**, com a mesma roupa e o mesmo rosto.",
        "**Nenhum texto, letra, número ou legenda em lugar nenhum da imagem** (as legendas e equações são "
        "acrescentadas depois por um script).",
        "Quadros separados por linhas brancas finas; ordem de leitura de cima para baixo (quadro 1 no topo).",
        "Mostre quantidades de forma natural (objetos contáveis), sem exatidão numérica obrigatória.",
        "Se a folha sair com mais ou menos de 4 quadros, quadros repetidos, texto escrito ou personagens "
        "inconsistentes, **gere de novo** até ficar certa.",
    ):
        l.append("- " + regra)
    l.append("")
    l.append("## Onde salvar\n")
    l.append("Pasta: `" + PASTA_SAIDA + "\\` — um PNG por história, com o nome indicado em cada item "
             "(`NN_slug_folha.png`).\n")
    l.append("## As 13 histórias\n")
    l.append("Para cada folha, use o estilo e as regras acima e a história abaixo (quadros 1 a 4, de cima para "
             "baixo):\n")
    for i, h in enumerate(dados["historias"], 1):
        l.append(f"### {i:02d} — {h['titulo']}")
        l.append(f"Arquivo: `{i:02d}_{slug(h['titulo'])}_folha.png`\n")
        l.append(h["prompt_historia"])
        l.append("")
    l.append("## Ao terminar\n")
    l.append("Liste os 13 arquivos gerados com o caminho completo e diga, para cada um, se tem exatamente 4 "
             "quadros e nenhum texto. Não gere nada além disso. Quem monta os GIFs e registra no Gérard é "
             "outra etapa.\n")
    SAIDA.write_text("\n".join(l), encoding="utf-8")
    print(len(dados["historias"]), "historias ->", SAIDA)


if __name__ == "__main__":
    main()
