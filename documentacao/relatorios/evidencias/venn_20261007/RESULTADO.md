# Passo 1 do desenho — desenharDiagramaVenn: preparar x desenhar (2026-10-07)

`desenharDiagramaVenn` (119 linhas) mostrava/ocultava historinhas, posicionava e habilitava o botão de ajuda,
reconstruía a cena do Venn (`sincronizarDiagramaVennComRepresentacoes`) e atualizava o cache dos quadradinhos
correspondentes da comparação, tudo dentro do paint. Agora:
`prepararDiagramaComplementar()` -> `FaseDiagramaComplementar` (HISTORINHAS, OCULTO, GENERICO, COMPLETO) concentra os
efeitos; `desenharDiagramaVenn(g2, fase)` só lê e desenha. A preparação roda no mesmo ponto de antes do paintComponent
(logo antes do desenho), então as camadas anteriores veem o mesmo estado.

Prova: `TesteMacacoAnotacaoMouseOver` (mouse real) em 5 situações — Composição de transformações (flores), Comparação
(bolas), Transformação de relação (bonecas, representação genérica), Composição de medidas (bolas, Venn) e Transformação
de medidas (balas) — 370 capturas da janela por versão. Antigo = cópia do commit 3bd4305.
Capturas pixel a pixel com a faixa do texto da situação-problema mascarada (y 205–245, ruído de 1 px já registrado em
`anotacao_20261007`): 370 de 370 idênticas.

Limites: a sincronização continua dentro do ciclo de pintura (só separada do desenho); movê-la para os eventos de mudança
de estado/tamanho muda o momento em que o estado é recalculado e exige mapear todos os pontos de mudança. Faixa mascarada
não comparada; PNGs não versionados.
