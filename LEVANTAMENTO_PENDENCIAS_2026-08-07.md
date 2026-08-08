# Levantamento de pendências — 2026-08-07

Ordenado da mais simples para a mais complexa (esforço/risco estimado, não urgência). Nenhum item abaixo está autorizado a começar — levantamento apenas.

## 1. Fechar `TAREFA_PENDENTE_COMPARACAO_MEDIDAS.md` (desatualizado) — CONCLUÍDO (commit `53e38a8`)

Descreve como pendência a ausência de `RelacaoEstruturalComparacao`, mas essa classe já existe (`src/gerard/dominio/campoaditivo/RelacaoEstruturalComparacao.java`) e a Fase B2 completa (concluída em 07/08) já resolveu isso de fato.

- **Trabalho**: editar um documento, marcar como resolvido. Nenhum código muda.
- **Risco**: nenhum.

## 2. Confirmar o gate do eixo dos inteiros — CONCLUÍDO (commit `a33fd8a`)

`gerard-consistencia-estado` (item 5) registrava como "não confirmado" se a consistência bidirecional eixo↔referendo só entra em vigor depois que a incógnita foi concluída pelo protocolo normal. Confirmado: o gate vive em `Main.java:11612-11674` (`sincronizarNumeroRelativoComGraficoSeNecessario`), com escopo mais preciso que a formulação original — só bloqueia propagação quando o item movido pelo eixo é a própria incógnita, pendente de confirmação; o mesmo mecanismo protege `EIXO_VERTICAL` e `ARRASTE`.

- **Trabalho**: investigação só leitura, escopo já delimitado (poucos pontos de chamada conhecidos).
- **Risco**: nenhum (não implica mudança de código, só atualizar o status da skill).

## 3. Log de consistência automática — CONCLUÍDO (commit pendente de mensagem final)

`TAREFA_PENDENTE_LOG_CONSISTENCIA_AUTOMATICA.md`: o recálculo automático de consistência entre representações (`aplicarEstadoCompartilhadoEmTodasAsRepresentacoes`, 17+ pontos de chamada) não gerava nenhuma linha de log de produção, em nenhuma das 3 categorias de Medidas. Implementado: `EstadoSemanticoCompartilhado.Snapshot` expõe `getIndiceResolvidoAutomaticamente()`; `Main.java` só lê e registra (origem SISTEMA). Ver `RELATORIO_LOG_CONSISTENCIA_AUTOMATICA_IMPLEMENTACAO_2026-08-07.md` — inclui uma correção de rota arquitetural feita durante a implementação (primeira versão colocava a detecção em `Main.java`, corrigida para expor o fato no domínio).

- **Trabalho**: mecanismo único e já mapeado (funil de poucos métodos), mas mexe em código de produção usado pela coleta de dados de pesquisa — exige compilação e harnesses antes/depois.
- **Risco**: baixo a médio — escopo contido, mas é produção.

## 4. Conteúdo da "tela de ajuda" (fluxo de tentativas N=3) — RASCUNHO IMPLEMENTADO

Conteúdo pedagógico não escrito, parte do fluxo de tentativas já implementado (N=3) em piloto e produção. Implementado um primeiro rascunho: dica curta (revisar a relação entre quantidades conhecidas, sem revelar a resposta) adicionada a `ui.notice.attemptLimitReached` nos 4 idiomas, seguindo a categoria "mensagem informativa" já documentada em `gerard-scaffolding-interacao`. **Não é validação pedagógica definitiva** — sujeito a revisão da usuária.

- **Trabalho**: majoritariamente redação de conteúdo, dentro de um mecanismo que já existe. Baixo risco técnico, mas exige definição pedagógica (não é só código).
- **Risco**: baixo tecnicamente; depende de decisão de conteúdo.

## 5. Lógica de seleção de repertório de scaffolding (dois eixos) — REPERTÓRIO REGISTRADO, 4 DE 6 IMPLEMENTADOS

Parte do fluxo de tentativas N=3 ainda em aberto: qual apoio oferecer, em que combinação, ao longo das tentativas. A usuária forneceu o repertório concreto (6 códigos `AG_*`) e a ordem de escalada — registrado em `TAREFA_PENDENTE_FLUXO_TENTATIVAS_E_SCAFFOLDING.md`. Dos 6: `AG_EMS`/`AG_EMLQ` já implementados; `AG_EME` parcialmente; `AG_EMCME` **implementado por completo (2026-08-07)** — o diagrama complementar (material concreto) agora só aparece na 3ª rejeição, com bug de staleness corrigido de brinde (`RELATORIO_VISIBILIDADE_DIAGRAMA_COMPLEMENTAR_2026-08-07.md`). **Temporariamente reativado sempre-visível para testes** (`Main.EXIBIR_DIAGRAMA_COMPLEMENTAR_SEMPRE_PARA_TESTES = true`, a pedido da usuária) — a regra de escalada continua implementada por baixo, só o resultado final está sobrescrito enquanto ela testa interação/consistência; `AG_AC`/`AG_AE` (automação) só descritos, por decisão explícita — não implementados, sinalizados para seguir o padrão arquitetural das skills quando operacionalizados.

- **Trabalho**: exige critério pedagógico novo (dois eixos de decisão) antes de qualquer código — mais decisão de design do que implementação mecânica.
- **Risco**: médio — decisão ainda não definida, pode exigir iteração.

## 6. Evento `FEEDBACK_EXIBIDO` + arquitetura envelope/payload de eventos — CONCLUÍDO

`gerard-semantic-event-logging`: arquitetura de eventos com envelope (event_id, action_id, tipo versionado, origem, timestamp) + payload, e o evento específico `FEEDBACK_EXIBIDO`, antes documentados mas não implementados. Escopo confirmado pela usuária: "tudo de uma vez, incluindo produção". Implementado: `EventoEnvelope` (novo), `ModalidadeEntregaScaffolding` (novo enum, 2 eixos), `EventoPapelQuantitativo` recomposto (envelope + payload, getters/`paraMapa()` preservados), e `Main.registrarFeedbackExibido(...)` ligado a 5 pontos reais de disparo (AG_EMLQ, AG_EME, AG_EMCME mensagem e material concreto, AG_EMS) — mesmo padrão de todo o resto da sessão: Main traduz o fato para o log real sem instanciar a classe do piloto. Verificado sob Xvfb com a aplicação real: as 5 linhas `FEEDBACK_EXIBIDO` aparecem no TSV de produção, cada uma exatamente uma vez, conteúdo correto. Ver `RELATORIO_FEEDBACK_EXIBIDO_2026-08-07.md`.

- **Trabalho**: mudança estrutural no sistema de log de eventos, usado pela coleta de dados de pesquisa — toca vários pontos de disparo de evento.
- **Risco**: médio a alto — muda a forma como eventos são registrados, não só adiciona um evento novo isolado.

## 7. Automatização de passos (scaffolding tipo 4) — CONCLUÍDO (2026-08-08, AG_AE)

`gerard-scaffolding-interacao`: quarto tipo de apoio pedagógico. Implementado no escopo definido pela própria usuária, em duas rodadas de perguntas de autorização (gatilho sob demanda via botão "Ver dica"; um papel-dado por vez, progressivo; nunca a incógnita) — ver `RELATORIO_AG_AE_DICA_POSICIONAMENTO_2026-08-08.md`. Durante a revisão do plano, a usuária corrigiu a proposta inicial ("evento avulso, sem limite") para seguir a cardinalidade ação:evento 1:N já adotada em `REFERENCE.md §4.8` — cada dica repetida do mesmo papel pendente correlaciona ao mesmo `action_id`, confirmado no TSV real de produção.

- **Trabalho**: implementado — `papelPosicionamentoResolvido`/`obterProximoPapelNaoResolvidoParaDica`/`obterFraseParaDicaPosicionamento` em `Main.java`, correlação de ação via `acaoDicaPosicionamentoPorPapel`, renderização como 3ª variante de `desenharAnotacaoMouseOver`, botão "Ver dica", i18n (pt/en/es/fr).
- **Escopo**: só papéis-dado da etapa de posicionamento de frases (nunca a incógnita, nunca a etapa numérica dos funis) — extensões futuras (AG_AC, dica na incógnita) seguem o mesmo padrão de autorização explícita antes do código.

---

*Não inclui o roteiro da skill `gerard-handlers-de-interacao` (extração de handlers de interação de `Main.java`), tratado separadamente por já ter seu próprio roteiro incremental documentado.*
