# Caso da área do enunciado

Leia esta referência ao investigar regressões de posição ou ao precisar da
evidência concreta que motivou a regra de geometria relativa.

Em 2026-08-06, `Main.java` continha duas cópias independentes da altura
vertical da área do enunciado:

- `estaNaAreaDoTexto(x, y)` usava `55/190 + ALTURA_PAINEL_ATALHOS_CATEGORIA`;
- o clamp de `processarMovimentoArraste`, no ramo
  `elementoTextoSelecionado`, usava `58 + altura` e `184` sem a constante.

Quando `ALTURA_PAINEL_ATALHOS_CATEGORIA` passou de 110 para 130, em
2026-07-28, somente o primeiro cálculo acompanhou a mudança. O resultado foi
uma palavra presa sobre a barra de ícones. O histórico está em
`RELATORIO_BUG_LIMITE_SUPERIOR_ARRASTE_TEXTO_ENUNCIADO_2026-08-06.md` e nos
commits `e89e718`/`cfe582b`.

A correção final eliminou a segunda cópia: `TOPO_AREA_ENUNCIADO` e
`BASE_AREA_ENUNCIADO` tornaram-se a fonte compartilhada do hit-test e do
clamp. `ALTURA_PAINEL_ATALHOS_CATEGORIA` é outro exemplo positivo, referenciado
pelas posições e áreas que dependem dela.

Ao investigar um bug semelhante, procure todas as referências à constante que
define a geometria real. Se o trecho com defeito não estiver entre elas, embora
dependa da mesma área visual, procure uma cópia numérica que ficou fora da
fonte única.
