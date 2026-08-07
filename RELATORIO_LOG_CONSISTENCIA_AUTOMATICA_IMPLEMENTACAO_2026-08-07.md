# Implementação — log de produção para a recomputação automática de consistência

Data: 2026-08-07. Pedido explicitamente pela usuária (item 3 do
levantamento de pendências), com correção de rota arquitetural no meio da
implementação.

## O que faltava

`TAREFA_PENDENTE_LOG_CONSISTENCIA_AUTOMATICA.md` / `RELATORIO_LOG_CONSISTENCIA_AUTOMATICA_2026-08-05.md`:
a recomputação automática de consistência entre representações
(`aplicarEstadoCompartilhadoEmTodasAsRepresentacoes`, alimentada pelos dois
funis de escrita `capturarEstadoCompartilhadoDoVergnaud`/
`capturarEstadoCompartilhadoDoDiagramaComplementar`) não produzia nenhuma
linha de log de produção, em nenhuma das três categorias de Medidas.

## Primeira tentativa — rejeitada pela usuária

A primeira implementação comparou, dentro de `Main.java`, o array
`conhecidos` lido da UI antes de chamar `estadoSemanticoCompartilhado.atualizar(...)`
contra o snapshot devolvido depois, para inferir se algo tinha sido
resolvido automaticamente. Questionada ("por que na main? tem que seguir a
arquitetura definida nas skills") e revertida antes de compilar/commitar.

**Por que estava errada**: `EstadoSemanticoCompartilhado.resolverRelacaoAditiva()`
já sabe, no momento exato em que resolve um valor, que aquele valor foi
calculado — não observado. Reconstruir esse fato em `Main.java` por diff de
estado antes/depois duplicava conhecimento que já existe no lugar certo,
violando localidade relacional (`gerard-knowledge-locality-principle`): o
objeto que resolve a relação deve ser a fonte do fato, não o chamador.

## Implementação final

**`EstadoSemanticoCompartilhado.java`** (domínio/sincronização):
- Novo campo `indiceResolvidoAutomaticamente` (índice do papel que a
  relação estrutural resolveu ou recalculou automaticamente na última
  chamada de `atualizar()`; `-1` se nenhum).
- Exposto em `Snapshot.getIndiceResolvidoAutomaticamente()`.
- Resetado para `-1` no início de cada `atualizar()`, antes de
  `resolverRelacaoAditiva`.
- Detectado dentro de `definirSePermitido` — único ponto de escrita usado
  pelas duas rotas de resolução automática (preenchimento do papel ausente
  e recálculo de consistência), nunca chamado para o valor que o próprio
  usuário forneceu. Comparação valor-antes/valor-depois no próprio ponto de
  escrita cobre os dois casos que interessavam à tarefa original — "azul,
  primeiro preenchimento" (`null`→valor) e "azul, recálculo de
  consistência" (valor→outro valor) — sem precisar saber qual das duas
  rotas (`resolverViaRelacaoEstruturalRica`/`resolverConsistenciaViaRelacaoEstruturalRica`/
  algoritmo genérico) chamou.
- Nenhuma chamada a `LoggerInteracaoGerard` dentro do domínio — o princípio
  "o domínio não grava logs diretamente" (`gerard-semantic-event-logging`)
  continua respeitado; o domínio só expõe o fato.

**`Main.java`** (interação):
- `capturarEstadoCompartilhadoDoVergnaud`/`capturarEstadoCompartilhadoDoDiagramaComplementar`
  passam a chamar `registrarLogConsistenciaAutomaticaSeHouve(snapshot, origem)`
  logo após `estadoSemanticoCompartilhado.atualizar(...)`.
- O novo método só lê `snapshot.getIndiceResolvidoAutomaticamente()` — sem
  laço, sem comparação, sem decisão — e, se `>= 0`, chama
  `registrarLogComputador(...)` (origem `ORIGEM_SISTEMA`, mesmo padrão já
  usado por `registrarTentativaIncognita`), com evento
  `"CONSISTENCIA_AUTOMATICA"` e detalhe `papelResolvido=...; valor=...`.

## Verificação

- Compilação completa: 436 arquivos, 0 erros.
- `TesteComparativoEstadoSemanticoCompartilhado`: 40 cenários, 0
  divergências — os valores calculados continuam idênticos a antes da
  mudança (o novo campo é só metadado, não influencia nenhum cálculo).
- 7 harnesses do piloto (`TestePilotoComposicaoMedidas`,
  `TransformacaoMedidas`, `ComparacaoMedidas`, `ComposicaoDeTransformacoes`,
  `TransformacaoDeRelacao`, `ComposicaoDeRelacoes`, `TentativasRejeitadas`):
  todos passando.
- Suíte temporária dedicada (`TesteTemporarioIndiceResolvidoAutomaticamente.java`,
  criada, rodada e **deletada** antes deste commit, nunca commitada — 10
  checagens diretas sobre `getIndiceResolvidoAutomaticamente()`):
  primeiro preenchimento marca o índice certo; o índice que o próprio
  usuário alterou nunca é marcado; nada a resolver não marca; recálculo de
  consistência marca mesmo quando o papel já estava conhecido; uma chamada
  repetida com o mesmo valor (simulando um tick de arraste depois que o
  valor já foi escrito de volta no widget) não marca de novo; incógnita
  protegida não resolve nem marca. 10/10 OK.

## Característica conhecida, não decidida aqui: volume durante arraste contínuo

Um ponto de chamada (`atualizarBarrasComparacaoAPartirDoControle`,
`Main.java:~10000`, controle de barras da Comparação) já é comentado no
código como "chamado a cada movimento do mouse" e não é bloqueado pela
mesma guarda que protege a incógnita quando o item arrastado **não** é a
incógnita (ver regra 5 de `gerard-consistencia-estado`, confirmada em
2026-08-07). Se o valor dependente recalculado mudar a cada pixel de um
arrasto contínuo desse tipo, o novo log gera uma linha por mudança de
valor — não uma por movimento de mouse sem mudança (a checagem
"valorDepois != valorAntes" já filtra ticks repetidos com o mesmo valor),
mas ainda assim pode ser uma linha por passo do arrasto quando o valor
muda a cada passo. Não decidi throttling algum aqui — isso seria uma
decisão de política de log (quando registrar durante um gesto contínuo,
não um fato de domínio), fora do escopo desta tarefa. Sinalizando para
decisão futura, se o volume em uso real se mostrar um problema.

## Escopo

Só os dois arquivos citados. Nenhuma mudança em qualquer outro caminho de
produção. `TAREFA_PENDENTE_LOG_CONSISTENCIA_AUTOMATICA.md` marcada como
resolvida.
