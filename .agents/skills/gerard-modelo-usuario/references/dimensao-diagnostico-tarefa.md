# Dimensão Diagnóstico da Tarefa

Leia esta referência ao alterar os campos de diagnóstico, sua origem ou seu
uso na inferência. O esquema geral do Modelo do Usuário permanece no
`SKILL.md`; o esquema da ação pertence a `gerard-log-acao-instrumental`.

- **Tarefa (Ação Instrumental):** referencia a ação registrada, sem duplicar
  aqui seu esquema.
- **Suporte:** `nenhum`, `parcial` ou `total`, registrando o apoio recebido
  anteriormente ao realizar a mesma tarefa. Seu objetivo registrado é permitir
  diminuir a ajuda ao longo do tempo.
- **Internalização:** `0` ou `1`. Quando vale `1`, a ajuda daquele conteúdo é
  retirada.
- **Probabilidade de saber o conteúdo:** obtida pelo teorema de Bayes e usada
  na atualização da internalização.

## Extensões decididas em 2026-07-23

**Dificuldade autorrelatada, explicação do elemento e explicação geral** são
preenchidas pelo usuário no Artefato Explicativo. A dificuldade admite fácil,
intermediária ou difícil; a justificativa pertence ao elemento e a explicação
geral descreve a estratégia da tentativa. Esses campos são o análogo escrito
da entrevista pós-tarefa usada na tese de Queiroz (2012).

`AgenteModelador.registrarExplicacaoNoUltimoDiagnostico` complementa o caso
mais recente da mesma tarefa; não cria outro caso. O preenchimento é opcional,
e a explicação geral permanece replicada nos casos da tentativa enquanto não
houver decisão de normalização.

**Nível conceitual estimado e curado:** `nivelConceitualEstimado` é um sinal
fraco produzido por casamento de padrão em `AnalisadorNivelConceitual` e nunca
alimenta diretamente a inferência. `nivelConceitualCurado` permanece nulo até
confirmação ou correção humana; somente o valor curado pode alimentar
`InferenciaRegrasModelador`.

**Invariante operatório:** `invarianteOrigem`, `invarianteCodigo`,
`invarianteSimbolico` e `invarianteObservacao` registram atribuição direta do
pesquisador no Artefato Explicativo. O pesquisador seleciona um item do
catálogo ou registra uma forma simbólica nova. Por ser atribuição humana, o
código pode ser insumo de `InferenciaRegrasModelador` sem estágio automático
de curadoria. Nenhum agente calcula automaticamente qual invariante uma ação
mobiliza.
