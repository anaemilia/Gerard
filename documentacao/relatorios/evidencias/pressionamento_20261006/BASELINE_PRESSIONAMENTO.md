# Baseline do pressionamento do mouse (golden master) — 2026-10-06

Macaco `tests/graphical/TesteMacacoPressionamentoMouse.java`: pressiona e solta o mouse REAL em pontos da área de
trabalho (abaixo da faixa de ferramentas/categorias, para não sortear nada) e registra, em cada ponto, depois de
PRESSIONAR e depois de SOLTAR: quem ficou ativo (item, texto, quadradinho, conector, painel de eixo, controle da
comparação, arraste elástico), cursor, foco, dica, diálogo/menu aberto e uma assinatura do estado das peças.

Varreduras por situação e fase: grade grossa (passo 120), centros de todas as peças ("alvos"), varredura fina do
diagrama (passo 36) e da representação complementar (passo 16, só na fase "limite").
Situações: Composição de transformações (rosas), Comparação de medidas (bolas), Transformação de relação (bonecas).

| Arquivo | Pontos | O que cobre |
|---|---:|---|
| `antes.tsv.gz` | 7.322 | fases modelado e limite (≈ 48 min de mouse real) |
| `antes_vazio.tsv.gz` | 88 | fase vazio: elementos de texto do enunciado |

Quem recebeu o pressionamento (após pressionar): conector 190, item 51, painel de eixo 82, controle da comparação 22,
quadradinho 5, texto 41, resto sem alvo. O piloto (1 situação, 748 pontos) deu 0 diferenças entre duas rodadas.

Gerado com o código de `11a0dcc` (antes da refatoração da cadeia de prioridade de `mousePressed`). Depois de
refatorar, regravar com o mesmo comando e comparar ponto a ponto; qualquer diferença é regressão.

    java TesteMacacoPressionamentoMouse <saida.tsv> 120 16 \
      PO_COMPOSICAO_TRANSFORMACAO_MEDIDAS_flores_422431114,PO_COMPARACAO_MEDIDAS_bolas_487868670,PO_TRANSFORMACAO_RELACAO_bonecas_620955739
    java -Dsovazio=1 TesteMacacoPressionamentoMouse <saida_vazio.tsv> 120 16 <os mesmos ids>

## Resultado da refatoração da cadeia de `mousePressed` (2026-10-06)

Código novo: `CadeiaPressionamento` com 10 elos nomeados (`mousePressed` de 168 para 27 linhas). Comparação ponto a ponto
com o mouse real, por chave (situação, fase, x, y):

| Conjunto | Pontos | Diferenças |
|---|---:|---:|
| fase vazio (texto do enunciado) | 88 | 0 |
| grade, diagrama e área complementar (modelado + limite) | 7.171 em comum | 0 |
| alvos (centro de cada peça, modelado + limite; regravados nos dois códigos com a coordenada do texto corrigida) | 274 | 0 |

Ressalva: o baseline principal foi gravado antes de o macaco ganhar a fase "vazio" e a correção da coordenada do texto;
por isso os "alvos" foram regravados à parte nos dois códigos (antigo e novo) e os 88 pontos da fase vazio vêm de outra
gravação. As tabelas completas do "depois" não foram versionadas (1,1 MB).
