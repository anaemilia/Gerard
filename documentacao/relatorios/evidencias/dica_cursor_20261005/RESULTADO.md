# Foco, dica e cursor do mouse — comparação antes/depois (2026-10-06)

Macaco `tests/graphical/TesteMacacoDicaCursorMouse.java`: move o mouse REAL por uma grade (passo 64) sobre toda a tela e
uma varredura fina (passo 10) sobre a área da representação complementar, em 4 situações
(Composição de medidas, Transformação de medidas, Comparação de medidas, Composição de transformações), nas fases
"vazio", "modelado" e "limite". Registra a cada ponto: dica (mostrar, texto), cursor e foco.

| Gravação | Pontos | sha256 (16 primeiros) |
|---|---:|---|
| código original (`antes.tsv`) | 23.868 | 6ed554c98a159041 |
| código novo (`depois.tsv`) | 23.868 | 6ed554c98a159041 |

Resultado: **0 diferenças** ponto a ponto. Reprodutibilidade do original (grade grossa, 4 situações): 5.400 pontos, 0 diferenças
entre duas rodadas.

Cobertura dos controles de quadradinho na varredura fina: estados "remover liberado" (mão, foco `[remover]`) e
"limite da quantidade curada" (adicionar bloqueado). Os 8 estados da regra pura estão cobertos por
`TesteDicaControleQuadradinho` (16 verificações); o caminho do clique no controle por `TesteRobotControlesUnidades`
(OK, capturas em `robot_controles_unidades/`). As tabelas TSV completas não foram versionadas (2,5 MB cada).
