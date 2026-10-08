# Passo 1 do desenho — desenharAreaDiagrama: preparar x desenhar (2026-10-08)

`desenharAreaDiagrama` (82 linhas) escondia controles sem categoria, reposicionava três botões, escondia o de ajuda
complementar e chamava `inicializarDiagramaVergnaud` dentro do paint. Agora `prepararAreaDiagrama()` concentra esses
efeitos e devolve `AreaDiagramaPreparada` (categoria escolhida?, x do divisor, limites dos painéis Vergnaud e complementar,
calculados ANTES da inicialização do diagrama, como sempre foram); `desenharAreaDiagrama(g2, area)` só lê e desenha.

Prova: `TesteMacacoAnotacaoMouseOver` (mouse real), tela inicial sem categoria + 3 situações (flores, bonecas/genérica,
Composição de medidas/Venn): 215 capturas por versão, antigo = cópia do commit e367691. Pixel a pixel com a faixa do texto
da situação-problema mascarada (y 205–245, ruído de 1 px já registrado): 215 de 215 idênticas.

Escopo reduzido de propósito (a execução anterior com 5 situações foi encerrada por pouca memória). Limites: a
inicialização do diagrama continua no ciclo de pintura (só separada do desenho); faixa mascarada não comparada; PNGs não
versionados.
