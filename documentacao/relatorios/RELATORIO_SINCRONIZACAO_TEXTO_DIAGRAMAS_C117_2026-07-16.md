# C117 — Sincronização dos elementos semânticos do texto com os diagramas

## Problema observado

Após uma alteração no estado dos diagramas, os valores exibidos nos elementos semânticos do enunciado permaneciam com os dados anteriores. No caso de comparação de medidas, os diagramas podiam apresentar `Referido = 4`, `Valor relativo = 8` e `Referendo = 12`, enquanto o enunciado ainda mostrava `6`, `8` e `?`.

## Correção

A propagação do `EstadoSemanticoCompartilhado.Snapshot` passou a incluir os elementos visuais do enunciado. Sempre que Vergnaud, a representação complementar ou um eixo altera o estado, os elementos textuais vinculados aos mesmos papéis semânticos recebem os valores atuais.

A correção abrange:

- números ainda posicionados na frase;
- a interrogação quando o papel desconhecido já possui valor no estado compartilhado;
- marcadores que foram deslocados dentro da área do enunciado;
- preservação de prefixos, sufixos e pontuação do token textual;
- recalculo do fluxo da frase sem desfazer deslocamentos realizados pelo usuário.

Itens já posicionados nos diagramas não são sobrescritos pelo sincronizador textual, pois continuam sendo tratados pelas regras próprias das representações formais e dos números relativos.

## Arquitetura

Foram introduzidos os contratos:

- `ElementoSemanticoTexto`;
- `MapeadorPapelSemanticoTexto`;
- `SincronizadorElementosSemanticosTexto`.

O percurso comum de sincronização foi centralizado em `SincronizadorElementosSemanticosTextoAbstrato`, e a apresentação de valores aditivos foi implementada por `SincronizadorElementosSemanticosTextoAditivo`.

`ElementoTextoMovel` e `ItemTextoArrastavel` implementam o mesmo contrato. O valor semântico original é preservado, inclusive quando era `?`, para manter consistência de logs, validações e identificação da incógnita.

## Comportamento esperado

No exemplo de comparação:

- estado anterior: `6`, `8`, `?`;
- estado atualizado dos diagramas: `4`, `8`, `12`;
- estado atualizado do enunciado: `4`, `8`, `12`.

A frase curada armazenada não é modificada. A atualização ocorre somente na camada visual e manipulável da tentativa atual.

## Verificações

- compilação Ant/NetBeans aprovada;
- teste unitário do contrato de sincronização aprovado;
- teste integrado da `TelaGerard` aprovado;
- pontuação textual preservada durante troca de valor;
- marcador deslocado no enunciado atualizado sem ser recriado;
- incógnita original preservada para logs e validações;
- incremento unitário da comparação preservado;
- adição, remoção e limite semântico de quadradinhos preservados;
- affordance de pickup e arraste físico preservados;
- regressão estrutural geral aprovada;
- 210 versões e 72 grupos conceituais mantidos consistentes.
