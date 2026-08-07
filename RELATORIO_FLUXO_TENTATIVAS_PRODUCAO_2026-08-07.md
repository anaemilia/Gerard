# Relatório: fluxo de N=3 tentativas rejeitadas ligado em produção

Data: 2026-08-07

## Contexto

Continuação direta de `RELATORIO_...` desta mesma data (piloto: N=3
tentativas + action_id em `PapelQuantitativo`, commit `032e12e`). A
usuária pediu explicitamente para também ligar o mecanismo em produção,
mesmo com a pendência (registrada em
`TAREFA_PENDENTE_FLUXO_TENTATIVAS_E_SCAFFOLDING.md`) de qual ajuda
concreta mostrar na 3ª rejeição ainda não decidida — instrução literal:
"implementar mesmo sem padrão e anotar que o padrão é uma decisão
futura".

## Onde o mecanismo entra em produção

Investigação encontrou o ponto exato: `confirmarValorIncognitaAceito`
(Main.java) é onde cada submissão de valor para a incógnita atual é
avaliada como certa/errada (`valorDigitadoCorrespondeAoCurado`). Cada
vez que `correto == false` ali é, na prática, uma tentativa rejeitada no
sentido da REFERENCE.md §4.8.

## A ponte Main↔piloto, decidida com a usuária antes de implementar

Main.java não guardava nenhum `PapelQuantitativo` (isolado por design,
ver Fase B2 completa do mesmo dia). Para contar tentativas ali sem
duplicar o local documentado como correto ("conhecimento do próprio
objeto semântico"), Main passou a manter **uma instância só para
contagem** (`tentativasIncognitaAtual`), recriada sempre que o papel da
incógnita atual muda (`garantirTentativasIncognitaAtual`) — nunca usada
para `posicionar(...)` um valor real, isso continua exclusivo de
`estadoSemanticoCompartilhado`.

## O que mudou

### `PapelQuantitativo.java` / `TipoErroPapel.java` / `EventoPapelQuantitativo.java`
Já commitados na etapa anterior (piloto). Sem mudança nesta etapa.

### `Main.java`

- Novo campo `tentativasIncognitaAtual` (+ `papelDaTentativaAtual` para
  saber quando recriar).
- `garantirTentativasIncognitaAtual(papel)`: recria a instância quando o
  papel da incógnita atual mudou.
- `registrarTentativaIncognita(papelAlvo, correto, item)`: monta o
  `DiagnosticoErroPapel` (`VALOR_INCORRETO` quando errado,
  `Optional.empty()` quando certo — mesma convenção de
  `diagnosticarValorProposto`) e delega a
  `PapelQuantitativo.registrarTentativa`. Loga no
  `LoggerInteracaoGerard` de produção (`registrarLogComputador`) quando
  o limite é atingido — deliberadamente separado do publicador de
  eventos do piloto, que continua descartando
  (`PublicadorEventoDominio.NENHUM`).
- `mostrarAvisoLimiteTentativasAtingido()`: aviso mínimo, sem conteúdo
  pedagógico novo — só informa que passou do limite e para usar
  "Restaurar". Chave nova de i18n, `ui.notice.attemptLimitReached`, nos
  4 idiomas (PT/EN/ES/FR).
- `confirmarValorIncognitaAceito`: chama
  `registrarTentativaIncognita` logo após calcular `correto`; quando o
  limite é atingido nesta chamada, mostra o aviso mínimo em vez do
  diálogo normal de "tem certeza?" + dica (já que novas tentativas ficam
  bloqueadas de qualquer forma).
- `restaurarTentativasIncognitaAtual()`: chamado pelos dois botões
  "Restaurar" já existentes (`restaurarElementosForaDoDiagrama`,
  `restaurarModelagemDiagrama`) — nenhum foi desenhado especificamente
  para este fluxo, mas ambos já reiniciam a interação com o item; decisão
  de reaproveitar os dois em vez de criar um terceiro botão, confirmada
  com a usuária.
- Javadoc de `registrarLogPorOrigem` e de `PapelQuantitativo` corrigidos
  para não afirmarem mais que `OrigemAcao` é a única peça do piloto
  referenciada por Main.java.

### `mensagens_{pt,en,es,fr}.properties`
Nova chave `ui.notice.attemptLimitReached`, texto neutro/operacional
("você tentou várias vezes... use Restaurar"), sem revelar a resposta
nem introduzir orientação pedagógica nova.

## O que continua em aberto (por decisão explícita, não por lacuna técnica)

Qual ajuda concreta mostrar na 3ª rejeição continua **não decidida** —
`mostrarAvisoLimiteTentativasAtingido` é um placeholder mínimo, não a
"tela de ajuda" que `TAREFA_PENDENTE_FLUXO_TENTATIVAS_E_SCAFFOLDING.md`
descreve. Também não implementado nesta etapa: o repertório de
Scaffolding por objeto semântico (2 eixos, tipo funcional × modalidade de
entrega) e a lógica de seleção dentro dele — ambos continuam pendências
separadas, registradas mas não resolvidas pela REFERENCE.md §4.8.

## Verificação

- Projeto completo compilado (439 arquivos): **0 erros**.
- `TestePilotoTentativasRejeitadas` (piloto, etapa anterior): todos os
  casos passam, sem mudança nesta etapa.
- `TesteComparativoEstadoSemanticoCompartilhado` (40 cenários): 0
  divergências — confirma que a nova ponte não interfere em
  `estadoSemanticoCompartilhado`.
- **Não verificado**: comportamento real da interface Swing (diálogos,
  bloqueio efetivo após a 3ª rejeição, "Restaurar" reabrindo o item) —
  não há harness de GUI neste ambiente, mesmo padrão de risco já
  documentado em outras mudanças de UI desta sessão. A lógica foi
  revisada linha a linha (incluindo um bug de posicionamento de javadoc
  encontrado e corrigido durante a própria revisão, antes deste
  relatório) mas não executada interativamente.

## Escopo

`Main.java` (só os pontos listados acima — nenhuma outra função tocada),
`PapelQuantitativo.java` (javadoc de fronteira, sem mudança de
comportamento), 4 arquivos de mensagens. Nenhuma mudança em
`EstadoSemanticoCompartilhado`, `ScaffoldingLimiteQuantidadeVenn`, ou
qualquer outro caminho de sincronização já existente.
