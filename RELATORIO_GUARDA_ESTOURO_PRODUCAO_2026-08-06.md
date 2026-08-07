# Relatório: guarda de estouro de int em produção (EstadoSemanticoCompartilhado)

Data: 2026-08-06

## Contexto

Continuação direta de `RELATORIO_GUARDA_ESTOURO_INTEIRO_2026-08-06.md`,
que corrigiu o mesmo bug só no piloto e registrou explicitamente que
`EstadoSemanticoCompartilhado.resolverRelacaoAditiva` — o algoritmo
genérico que a UI de fato usa hoje — continuava vulnerável. A usuária
pediu para corrigir também aqui.

## O que mudou

`resolverRelacaoAditiva`, no algoritmo genérico (o que roda quando os
três papéis já estão preenchidos e um muda — "fase 2, consistência" —
e também no fallback de "exatamente um faltante"), tinha 6 pontos que
somavam ou subtraíam `int` sem proteção: `valor(0) + valor(1)`,
`valor(2) - valor(0)`, etc.

Esses 6 pontos foram trocados por chamadas a dois novos métodos
privados, `definirSomaSePermitido`/`definirSubtracaoSePermitido`, que
envolvem `Math.addExact`/`Math.subtractExact` numa captura de
`ArithmeticException`: quando a conta não é representável como `int`,
**nada é escrito** — o valor anterior (ou a ausência de valor, no
primeiro preenchimento) é preservado, o mesmo tratamento que `definir(...)`
já dava para um valor inválido no domínio do papel (ex.: negativo em
NATURAIS).

O caminho da arquitetura rica (`resolverViaRelacaoEstruturalRica`, que
delega às 6 classes `RelacaoEstrutural*` desde a Fase B1) já estava
protegido pela guarda anterior — `calcularValorAusente` devolve
`NAO_RESOLVIVEL_NESTE_ESTADO` ao estourar, e `temValorCalculavel()`
falso já impede a escrita. Esta mudança cobre a outra metade do
algoritmo: o de preenchimento automático de consistência, que só existe
aqui (não tem equivalente no piloto — ver
`RELATORIO_INVESTIGACAO_FASE_B2_COMPLETA_2026-08-06.md`, Achado 2).

## Verificação

- `TesteComparativoEstadoSemanticoCompartilhado` (harness já existente,
  compara `EstadoSemanticoCompartilhado` contra
  `EstadoSemanticoCompartilhadoOriginal` campo a campo): 40 cenários,
  0 falhas — **nenhuma regressão** nos valores de magnitude normal. Este
  harness compara antes/depois e espera equivalência; não foi estendido
  com cenário de estouro porque seu propósito é justamente provar
  ausência de mudança de comportamento — um cenário de estouro
  divergiria dele por definição (esse é o ponto da correção).
- Verificação dirigida (script ad-hoc, fora do harness permanente) nos
  dois caminhos do algoritmo genérico:
  - Sobrescrita de consistência (três já preenchidos, um alterado, soma
    estoura): valor anterior do papel afetado é preservado — não grava
    `-294967296`.
  - Primeiro preenchimento (papel começa desconhecido, soma dos outros
    dois estoura): papel permanece desconhecido — não grava
    `-294967296`.
- Projeto completo compilado (435 arquivos): **0 erros**.

## Escopo

Só `EstadoSemanticoCompartilhado.java`. Nenhuma mudança em `Main.java`
nem em nenhum outro arquivo de produção.
