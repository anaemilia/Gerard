# CHANGELOG C162 — distribuição trapezoidal no funil

## Correção
Os quadradinhos do processo de transformação eram distribuídos em uma área retangular interna. Como o funil possui laterais inclinadas, algumas unidades ficavam visualmente próximas ou sobre as bordas, sobretudo nas linhas inferiores.

## Implementação
O `LayoutUnidadesProcessoTransformacao` passou a:

- calcular a largura interna do polígono em cada linha;
- centralizar os quadradinhos dentro dessa largura;
- usar mais unidades nas linhas próximas à abertura e menos perto do gargalo;
- reduzir o tamanho somente quando a magnitude exigir;
- verificar que os quatro cantos de cada unidade permanecem no interior do funil.

Para uma transformação `+5`, a formação padrão é `2–2–1`.

## Preservado
- estados inicial e final;
- sinal da transformação;
- controles `+` e `−`;
- inserção e retirada;
- sincronização semântica;
- regra do primeiro posicionamento;
- grandezas de contagem e dinheiro;
- ciclo da incógnita;
- conclusão progressiva.
