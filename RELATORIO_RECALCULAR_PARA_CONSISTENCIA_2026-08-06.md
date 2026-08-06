# Relatório: recalcularParaConsistencia nas 6 classes RelacaoEstrutural*

Data: 2026-08-06

## Contexto

Fecha a lacuna encontrada na investigação da Fase B2 completa
(`RELATORIO_INVESTIGACAO_FASE_B2_COMPLETA_2026-08-06.md`, Achado 2):
`calcularValorAusente` só resolve o caso "exatamente um papel incógnito"
(primeiro preenchimento). Não existia, no piloto, nenhuma capacidade
equivalente ao segundo algoritmo de
`EstadoSemanticoCompartilhado.resolverRelacaoAditiva` — "os três papéis
já estão preenchidos, um deles muda, recalcular um dos outros dois para
preservar a consistência" —, o caminho comum quando o sujeito arrasta um
valor já posicionado num diagrama já completo.

Zero risco de produção: mudança inteiramente dentro do pacote piloto
`gerard.dominio.campoaditivo`, nenhuma linha de `Main.java` tocada.

## O que foi implementado

Novo método `recalcularParaConsistencia(papelA, papelB, papelC,
papelAlterado)` em cada uma das 6 classes `RelacaoEstrutural*`
(Composição, Transformação e Comparação de Medidas; Composição de
Transformações, Transformação de Relação e Composição de Relações).

Réplica fiel, papel por papel, do algoritmo genérico que já existia em
`EstadoSemanticoCompartilhado.resolverRelacaoAditiva` (branches
`indiceAlterado == 0/1/2`): dado qual papel acabou de ser alterado,
**nunca recalcula o próprio papel alterado**; entre os outros dois,
prioriza recalcular o papel "C" (o resultado da soma, ex.: Todo,
EstadoFinal, Referendo, RelacaoFinal) quando o par necessário está
totalmente conhecido; só recalcula um dos dois papéis restantes quando
C não está disponível como alvo (porque C foi o papel alterado, ou
porque falta valor no par).

Mesmo contrato de `calcularValorAusente`: não modifica nenhum papel, só
devolve um `ResultadoCalculo` — aplicar o valor é decisão explícita de
outra camada, via `aplicar(...)`, já existente. Quando nenhum dos pares
necessários está totalmente conhecido, devolve
`EstadoConsistencia.NAO_RESOLVIVEL_NESTE_ESTADO` (situação legítima, não
uma exceção — mesmo padrão de `calcularValorAusente`).

`papelAlterado` precisa ser exatamente um dos três papéis da relação
(comparação por referência); caso contrário, lança
`IllegalArgumentException` — violação de contrato do chamador, mesmo
estilo de `exigirNaoNulo`.

## Verificação de fidelidade ao algoritmo original

A correspondência com o algoritmo de `EstadoSemanticoCompartilhado` foi
verificada branch a branch antes da implementação (não só testada depois):
para os três casos de papel alterado (A, B ou C), a ordem de prioridade
de recálculo e os pares de precondição usados em
`recalcularParaConsistencia` foram conferidos 1:1 contra
`indiceAlterado == 0/1/2` em `resolverRelacaoAditiva`.

## Verificação

Cobertura de teste adicionada às 6 harnesses (`TestePiloto*.java`),
seção `recalcularParaConsistencia (2026-08-06)`, com os mesmos valores
já usados nos testes de `diagnosticarValorProposto` (A=10, B=-3, C=7 ou
equivalente por esquema):

- Papel A alterado, A e B conhecidos → recalcula C.
- Papel C alterado, A e C conhecidos → recalcula B (Transformação de
  Medidas usa esse caso de forma diferente — ver nota abaixo).
- Papel B (ou o papel restante) alterado, só esse par conhecido →
  recalcula A (caminho de fallback).
- Só um papel conhecido → `NAO_RESOLVIVEL_NESTE_ESTADO`.
- `TestePilotoTransformacaoMedidas` também cobre
  `IllegalArgumentException` para `papelAlterado` que não pertence à
  relação (representativo dos 6, mesmo padrão da cobertura de
  precondição já existente nesse harness para `diagnosticarValorProposto`/
  `calcularValorAusente`).

Compilação completa do projeto (435 arquivos): **0 erros**.

Execução dos 6 harnesses via linha de comando: todos os 6 imprimiram
"TODOS OS TESTES ... PASSARAM." incluindo a nova seção, sem nenhuma
falha.

## O que NÃO foi verificado / fora de escopo

- Nenhuma integração com `Main.java` — este método não é chamado por
  nenhum caminho de produção; fecha a lacuna de capacidade encontrada na
  investigação, mas não decide, por si só, se/quando a Fase B2 completa
  deve prosseguir.
- Não substitui `EstadoSemanticoCompartilhado.resolverRelacaoAditiva` —
  o algoritmo genérico em produção continua exatamente como estava.

## Próximo passo

Com essa lacuna fechada, a Fase B2 completa deixa de depender de design
novo no piloto — o próximo passo, se e quando autorizado, é decidir como
`Main.java` adotaria `PapelQuantitativo` de fato (ver os demais achados
do relatório de investigação: os 2 funis de escrita concentrados, o uso
de `EstadoSemanticoCompartilhado` como calculadora descartável em
`SimuladorEstadoComplementarVenn`, e os 4 arquivos leitores de
`Snapshot` fora de `Main.java`). Não autorizado ainda.
