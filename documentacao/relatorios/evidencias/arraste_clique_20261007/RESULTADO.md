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

## Fatia 3c — mouseReleased em cadeia de soltura (103 -> 12 linhas)

Mesma gravação (732 operações) com o código novo (`soltura.tsv.gz`) contra `depois` e `depois2` (código anterior):
79 e 83 chaves diferentes de 713, contra 80 entre as duas gravações do código anterior. Por campo, só aparecem as
mesmas duas classes de ruído (hash durante o arraste elástico; cursor mão/padrão). Nenhuma diferença em handlers
ativos, foco, dica, diálogo/menu nem no hash do estado das peças depois de soltar.
Limite: o macaco não cobre a soltura dos painéis de eixo das Relações (elo "painéis de eixo" sem prova por mouse real).
