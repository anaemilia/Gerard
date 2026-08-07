# Relatório — Evento `FEEDBACK_EXIBIDO` + arquitetura envelope/payload (2026-08-07)

Item 6 de `LEVANTAMENTO_PENDENCIAS_2026-08-07.md`. Escopo confirmado pela
usuária: "tudo de uma vez, incluindo produção" — tanto a reestruturação do
piloto (`REFERENCE.md §4.8`) quanto a ligação ao log real de produção.

## O que existia antes

`REFERENCE.md §4.8` já especificava, em três decisões distintas, sempre
com a ressalva explícita "não uma implementação": a arquitetura de evento
envelope + payload (Seção 19.1 da Revisão 5, referenciando xAPI, Caliper
Analytics e CloudEvents), o versionamento de esquema (sufixo `.vN`
embutido no tipo), e o evento `FEEDBACK_EXIBIDO` em si (com o critério de
confirmação dependente da modalidade de entrega). Nenhuma das três tinha
código correspondente.

## Fase A — `EventoEnvelope` + versionamento

Novo arquivo `src/gerard/dominio/campoaditivo/evento/EventoEnvelope.java`
— o núcleo fixo de todo evento: `eventId` (UUID), `actionId`,
`tipoVersionado` (String), `origemAcao` (`OrigemAcao`),
`timestampEpocaMillis`.

`TipoEventoPapel` ganhou o valor `FEEDBACK_EXIBIDO` e o método
`chaveVersionada()`, que gera `"papel_quantitativo." +
name().toLowerCase() + ".v1"` — a convenção "type-based versioning" do
CloudEvents já referenciada em `REFERENCE.md §4.8`.

## Fase B — `ModalidadeEntregaScaffolding` + evento FEEDBACK_EXIBIDO

Novo enum `src/gerard/dominio/campoaditivo/ModalidadeEntregaScaffolding.java`
— VISUAL, SONORA, HAPTICA, MANIPULATIVA, GUIADA_POR_MOVIMENTO — com
`ehPassiva()`/`ehInterativa()` determinando o critério de confirmação do
evento ("renderizado" para passivas, "affordance ativada" para
interativas), exatamente como especificado em `REFERENCE.md §4.8`.

`EventoPapelQuantitativo` foi recomposto para conter um `EventoEnvelope`
internamente (em vez de campos soltos de id/action/origem/timestamp) mais
os campos de payload, incluindo dois novos: `estiloScaffolding` e
`modalidadeEntrega`. Novo factory estático `feedbackExibido(...)`. Todos
os getters públicos originais foram preservados (compatibilidade externa
mantida) e `paraMapa()` só ganhou chaves novas (`tipo_versionado`,
`estilo_scaffolding`, `modalidade_entrega`) — nenhuma chave existente foi
removida ou renomeada, para não quebrar nenhum consumidor do mapa.

Uma primeira tentativa de implementação fazia `getTipo()` reconstruir o
enum por parsing reverso da string versionada guardada no envelope, para
"evitar duplicar" o campo tipo. Reconhecida como frágil antes de compilar
— revertida para simplesmente manter um campo `TipoEventoPapel tipo`
direto na classe, ao lado do envelope (que só guarda a string
pré-computada). Mais simples e mais robusto; nenhum ciclo de verificação
foi desperdiçado com a versão frágil.

## Fase C — ligação ao log real de produção

Novo método privado em `Main.java`,
`registrarFeedbackExibido(String estiloScaffolding,
ModalidadeEntregaScaffolding modalidade, String detalhesExtra)` — segue o
mesmo padrão já usado nesta sessão inteira para consistência automática e
tentativas: Main lê/traduz o fato para uma linha real de log
(`registrarLogComputador`), **sem instanciar
`EventoPapelQuantitativo`** — essa classe continua isolada no pacote
piloto por design (ver seu próprio javadoc). O vocabulário
(`estiloScaffolding`, `modalidade`, critério de confirmação) é o mesmo dos
dois lados; só o mecanismo de persistência é diferente — "o domínio não
grava logs diretamente" (`gerard-semantic-event-logging`).

Ligado aos 5 pontos reais de disparo já existentes no fluxo de tentativas
(`TAREFA_PENDENTE_FLUXO_TENTATIVAS_E_SCAFFOLDING.md`):

| Código | Ponto de chamada | Modalidade |
|---|---|---|
| `AG_EMLQ` | `confirmarValorIncognitaAceito` (pergunta de confirmação de valor divergente) | VISUAL |
| `AG_EME` | `mostrarDicaOperacaoIncognita` (dica de soma/subtração) | VISUAL |
| `AG_EMCME` (mensagem) | `mostrarAvisoLimiteTentativasAtingido` | VISUAL |
| `AG_EMCME` (material concreto) | `registrarTentativaIncognita`, no bloqueio por limite | MANIPULATIVA |
| `AG_EMS` | `configurarFeedbackConclusaoModelagem` → `aoSolicitarDecisao()` | VISUAL |

`AG_AC`/`AG_AE` não têm gatilho — continuam só descritos, nada a ligar.

## Verificação

1. **Compilação completa**: `javac` sobre os 436 arquivos de `src/`
   (classpath incluindo `lib/weka-stable-3.8.6.jar` e
   `lib/bounce-0.18.jar`) — 0 erros, só o aviso pré-existente de operação
   unchecked em um arquivo não relacionado.
2. **Harnesses**: `TestePilotoTentativasRejeitadas` (todos os checks
   passaram, confirmando que a recomposição de `EventoPapelQuantitativo`
   não quebrou nada), `TesteComparativoEstadoSemanticoCompartilhado` (40
   cenários, 0 falhas), `TestePilotoComposicaoMedidas` (todos os checks
   passaram) — nenhuma regressão.
3. **Execução real sob Xvfb**: um teste temporário
   (`TesteTemporarioFeedbackExibido.java`, deletado após a verificação,
   nunca commitado) lançou a aplicação real, sorteou uma situação de
   Composição de Medidas, e exercitou os 5 pontos de disparo — os 3
   atrás de `JOptionPane` modais (`AG_EME`, `AG_EMCME` mensagem) foram
   disparados via `invokeLater` e dispensados com um `Robot` (tecla
   ENTER), técnica adaptada de `varrerDialogosAbertos`/
   `existeDialogoVisivel` em `TesteMonkeySemiGuiado.java`; `AG_EMCME`
   material concreto foi exercitado diretamente (3 rejeições reais, sem
   diálogo); `AG_EMLQ` e `AG_EMS` foram verificados por chamada direta ao
   helper compartilhado `registrarFeedbackExibido` (mesmo caminho até o
   log dos outros 3) — seus call sites reais exigiriam, respectivamente,
   um valor divergente real digitado e uma modelagem completa até o selo
   de conclusão, roteiro bem mais longo do que o ganho de rigor
   justificava para esta verificação.

   O TSV real de produção (`$HOME/Gerard/logs/gerard_interacao_*.tsv`)
   recebeu exatamente 5 linhas `FEEDBACK_EXIBIDO`, uma por código, sem
   duplicação, cada uma com `estilo=`, `modalidade=` e `criterio=`
   corretos — por exemplo:

   ```
   ...FEEDBACK_EXIBIDO	estilo=AG_EMCME (material concreto); modalidade=MANIPULATIVA; criterio=affordance ativada (modalidade interativa); diagrama complementar passou a estar disponível...
   ...FEEDBACK_EXIBIDO	estilo=AG_EME; modalidade=VISUAL; criterio=renderizado (modalidade passiva); dica genérica de escolher soma ou subtração...
   ```

## Documentação atualizada

- `REFERENCE.md §4.8`: as três afirmações "decisão-alvo, não
  implementação" (arquitetura de evento, versionamento de esquema, evento
  `FEEDBACK_EXIBIDO`) substituídas por notas de implementação datadas de
  2026-08-07, cada uma referenciando este relatório.
- `gerard-semantic-model/CHANGELOG.md`: nova entrada 2.7.
- `TAREFA_PENDENTE_FLUXO_TENTATIVAS_E_SCAFFOLDING.md`: nova seção
  descrevendo a implementação; status no topo atualizado.
- `LEVANTAMENTO_PENDENCIAS_2026-08-07.md`: item 6 marcado concluído.

## Observação de escopo, não decidida aqui

`AG_AC`/`AG_AE` (automatização de contagem e de passos da modelagem)
continuam sem gatilho de disparo — nenhuma decisão de quando/como
publicar `FEEDBACK_EXIBIDO` para eles foi tomada, nem deveria ser, por
decisão explícita da usuária já registrada em
`TAREFA_PENDENTE_FLUXO_TENTATIVAS_E_SCAFFOLDING.md`. O item 7 do
levantamento (automatização de passos) trata disso separadamente.
