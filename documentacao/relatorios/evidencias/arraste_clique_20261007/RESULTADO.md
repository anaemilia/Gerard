# Fatia 3b — movimento do arraste e duplo clique em cadeias ordenadas (2026-10-07)

Mouse real (Robot), `tests/graphical/TesteMacacoArrasteCliqueMouse.java`: para o centro de cada peça, arraste curto e
longo (assinatura durante e depois) e clique simples/duplo. 732 operações por execução (flores CT e bolas Comparação,
fases vazio/modelado/limite).

| comparação | chaves em comum | diferenças |
|---|---|---|
| antes x depois | 705 | 84 |
| depois x depois2 (MESMO código, duas execuções) | 713 | 80 |
| antes x depois2 | 705 | 91 |

Diferenças antes/depois que não aparecem também entre execuções do mesmo código: **0**.
As diferenças são ruído de temporização: 69 são só o hash do estado capturado durante a animação do arraste elástico;
o restante é cursor mão/padrão em pontos vazios e deslocamento de 1 px de layout entre execuções. Quem estava ativo
(handlers), diálogo/menu e estado final das peças coincidem.

Conclusão limitada: não houve regressão detectada nesta amostra (2 situações); ela não prova equivalência fora dela.
Tabelas completas: `antes.tsv.gz`, `depois.tsv.gz`, `depois2.tsv.gz`.
