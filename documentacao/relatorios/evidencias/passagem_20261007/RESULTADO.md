# Fatia 3d — mouseMoved em cadeia de passagem (167 -> 11 linhas), 2026-10-07

Macaco `TesteMacacoPressionamentoMouse` em modo `-Dsoalvos=1` (centro de cada peça, fases modelado e limite) nas 3
situações: flores (Composição de transformações), bolas (Comparação) e bonecas (Transformação de relação, com painéis
de eixo). Mouse real: o macaco move o mouse até o ponto (aciona mouseMoved: cursor, foco, dica), pressiona e solta.
Antigo = cópia (worktree) do commit cb70e62; novo = esta alteração. Comparação por chave (cenário, fase, x, y):

| pontos antes | pontos depois | em comum | diferenças |
|---:|---:|---:|---:|
| 274 | 274 | 274 | 0 |

Limite: cobre os centros das peças e dos controles, não uma varredura fina de toda a tela (a grade completa leva ~48 min);
trechos vazios entre peças não foram comparados nesta rodada. O "[FALHA] passo 9" no log vem do harness de modelagem
e aparece igual nas duas gravações.
