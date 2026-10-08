# Passo 1 do desenho — cabeçalho, texto do problema e painéis de eixo: preparar x desenhar (2026-10-08)

- `desenharCabecalho` -> `prepararCabecalho()` (posiciona painel de ações e botão de chat; decide status e x) +
  `desenharCabecalho(g2, cabecalho)` só pinta.
- `desenharTextoProblema` -> `prepararTextoProblema()` (zera marcadores, reposiciona botões, esconde controles sem categoria,
  dimensiona/posiciona/marca os elementos de texto; devolve ADIVINHACAO, SEM_CATEGORIA ou COMPLETO) +
  `desenharTextoProblema(g2, fase)` só pinta. Mudança de ordem: antes cada palavra era dimensionada, desenhada e marcada em
  sequência; agora todas são dimensionadas e marcadas e depois desenhadas.
- `desenharPaineisEixoRelacoes` -> `prepararPaineisEixoRelacoes()` (visibilidade) + desenho sem efeito.
- As medidas de fonte da preparação vêm do componente (`getFontMetrics`), não do Graphics2D.

Prova: `TesteMacacoAnotacaoMouseOver` (mouse real), 5 situações + tela inicial, em 3 partes (A flores; B bonecas e
Composição de medidas; C Comparação e Transformação de medidas), antigo (commit 695912d) e novo: 371 capturas por versão.

1. Fora da faixa do texto da situação-problema (y 205–245): 371 de 371 idênticas pixel a pixel.
2. Dentro da faixa (que é onde esta mudança atua), as capturas nunca são idênticas — nem do código antigo contra ele mesmo.
   Pixels diferentes na faixa, 77 capturas da situação de Comparação, 4 gravações (2 extras só do código antigo):
   antigo x antigo 362 (mediana), antigo(grav. 1) x antigo 366–386, NOVO x antigo 345–349, NOVO x antigo(grav. 1) 346.
   O novo fica dentro da dispersão que o código antigo já tem consigo mesmo. Conclusão limitada: não há evidência de
   mudança no texto, mas a faixa não foi provada idêntica pixel a pixel (a origem da variação não foi investigada).

Limites: a faixa do texto só foi comparada estatisticamente; PNGs não versionados.
