# Relatório: Fase B2 completa — Main.java passa a chamar objetos do piloto

Data: 2026-08-07

## Contexto

Retomada da Fase B2 completa (Main.java manipula os objetos do piloto),
registrada como pendente em
`TAREFA_PENDENTE_LOCALIDADE_CONHECIMENTO_ESTADO_COMPARTILHADO.md` desde
2026-08-06. A investigação anterior
(`RELATORIO_INVESTIGACAO_FASE_B2_COMPLETA_2026-08-06.md`) tinha
encontrado um bloqueio real (Achado 2: faltava, no piloto, a capacidade
de "recalcular um papel já conhecido" — só existia
`calcularValorAusente`, que exige exatamente 1 incógnito). Essa lacuna
foi fechada no mesmo dia (`recalcularParaConsistencia` nas 6 classes
`RelacaoEstrutural*`), mas a decisão de executar a migração em si ficou
pendente.

## Decisão de escopo, tomada com a usuária antes de implementar

Duas perguntas, porque a investigação (Achado 3) tinha encontrado uma
tensão de design real: `PapelQuantitativo` foi desenhado para objetos
com identidade persistente e eventos (`PublicadorEventoDominio`), mas já
existia um precedente de uso descartável (sem identidade, sem eventos)
dentro do próprio `EstadoSemanticoCompartilhado.calcularComRelacaoRica()`.

1. **Modelo de identidade**: a resposta foi "a prioridade é que a main
   apenas chame métodos de outros objetos que, preferencialmente, já
   funcionem bem" — confirma o padrão descartável (sem eventos), reusando
   o que já estava testado, em vez de introduzir identidade
   persistente/eventos novos em código de produção usado para coleta de
   dados reais.
2. **Simulador do diagrama Venn** (`SimuladorEstadoComplementarVenn`,
   calculadora "e se?" descartável, Achado 3): "sim, incluir".

## O que "migrar Main.java para os objetos do piloto" significa aqui

Main.java **não guarda nem manipula `PapelQuantitativo` diretamente** —
e não precisou mudar uma única linha. A razão: Main.java já não continha
lógica de domínio própria; os dois funis de escrita
(`capturarEstadoCompartilhadoDoVergnaud`/
`...DoDiagramaComplementar`, Achado 1) só chamam
`estadoSemanticoCompartilhado.atualizar(...)`. Isso já era o padrão "Main
só chama métodos de objetos que funcionam" — o objeto que Main chama é
quem precisava passar a delegar 100% ao piloto, não Main em si.

Antes desta mudança, `EstadoSemanticoCompartilhado.resolverRelacaoAditiva`
tinha dois algoritmos: "primeiro preenchimento" (já delegado ao piloto
desde a Fase B1, via `calcularValorAusente`) e "preenchimento automático
de consistência" (quando os três papéis já estão preenchidos e um muda —
o caminho mais comum durante interação real, ex.: arrastar um valor já
posicionado) — este último continuava sendo um algoritmo genérico próprio
de índices, só guardado contra estouro de int (passo anterior), nunca
delegado.

## O que mudou

Só `EstadoSemanticoCompartilhado.java`:

1. **`criarTrioDePapeis()`** — extraído da antiga `calcularComRelacaoRica()`:
   constrói os 3 `PapelQuantitativo` descartáveis (`PublicadorEventoDominio.NENHUM`)
   do tipo atual, posicionados com os valores já conhecidos. Único ponto
   de construção do trio, agora compartilhado entre os dois algoritmos.
2. **`calcularValorAusenteDoTipo`/`recalcularParaConsistenciaDoTipo`** —
   dispatch por `TipoSituacaoAditiva` para o método certo (`calcularValorAusente`
   ou `recalcularParaConsistencia`) de uma das 6 classes `RelacaoEstrutural*`.
3. **`resolverConsistenciaViaRelacaoEstruturalRica`** (novo, chamado logo
   depois de `resolverViaRelacaoEstruturalRica` em `resolverRelacaoAditiva`):
   quando os três papéis já estão conhecidos e `indiceAlterado` é 0, 1 ou
   2 (mesma condição que o algoritmo genérico exigia), delega a
   `recalcularParaConsistencia` do piloto e escreve o resultado no papel
   que ele decidiu recalcular (identificado por igualdade de referência
   contra o trio construído, já que os papéis não têm nome/índice
   embutido). O algoritmo genérico de índices que existia antes só
   permanece ativo, agora, para os 2 tipos "Em construção" que o piloto
   não cobre.
4. `SimuladorEstadoComplementarVenn.simular()` **não precisou de nenhuma
   mudança**: já criava um `EstadoSemanticoCompartilhado` descartável e
   chamava `atualizar(...)` (mesmo padrão "e se?" do Achado 3) — herdou a
   delegação completa automaticamente, incluindo estouro guardado.

## Verificação

- Projeto completo compilado (435 arquivos): **0 erros**.
- `TesteComparativoEstadoSemanticoCompartilhado` (40 cenários, compara
  bit a bit contra `EstadoSemanticoCompartilhadoOriginal`): **0
  divergências**, incluindo os 6 cenários "fase 2: consistência, todos
  preenchidos, edita X → sobrescreve Y" — exatamente o algoritmo que
  passou a ser delegado ao piloto nesta mudança.
- Verificação dirigida (script ad-hoc): cenário de consistência com
  estouro (soma recalculada de `indice2` estouraria `int`) — confirma que
  o valor anterior é preservado (`2000000006`), não escreve lixo por
  wraparound. Prova que a guarda de estouro de produção (passo anterior
  desta sessão) continua ativa através do novo caminho delegado.

## Escopo e o que não mudou

- **Zero linhas alteradas em `Main.java`** — nem nos dois funis de
  escrita, nem em nenhum dos 8 pontos que antes eram catalogados como
  "origens". A superfície de produção tocada é só
  `EstadoSemanticoCompartilhado.java`.
- `PapelQuantitativo`/`RelacaoEstrutural*`/`ResultadoCalculo` continuam
  isolados por design (só referenciados de dentro do próprio
  `EstadoSemanticoCompartilhado`, nunca de `Main.java` diretamente).
- Os 4 arquivos satélites que só leem `Snapshot` (Achado 4) não
  precisaram mudar — o formato de saída (`Snapshot`) é o mesmo de antes.
- Os 2 tipos "Em construção" (`COMPOSICAO_TRANSFORMACAO_MEDIDAS`,
  `TRANSFORMACAO_COMPOSTA_DOIS_PASSOS`) continuam fora da arquitetura
  rica — sem cobertura no piloto, sem caminho de UI que os alcance hoje,
  sem urgência (mesmo status desde a investigação de 2026-08-06).

## Conclusão

Com isso, `resolverRelacaoAditiva` delega **100% da lógica de relação
aditiva** ao piloto para os 6 tipos alcançáveis pela UI — os dois
algoritmos que antes dividiam a responsabilidade (um já delegado, um
genérico próprio) agora são ambos chamadas a `RelacaoEstrutural*`. A
Fase B2 completa está encerrada, no espírito acordado: Main.java continua
magro e sem lógica de domínio, e tudo que ele aciona através de
`EstadoSemanticoCompartilhado` agora é, de fato, o piloto por baixo.
