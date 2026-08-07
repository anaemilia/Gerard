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

## 4. Conteúdo da "tela de ajuda" (fluxo de tentativas N=3)

Conteúdo pedagógico ainda não escrito, parte do fluxo de tentativas já implementado (N=3) em piloto e produção.

- **Trabalho**: majoritariamente redação de conteúdo, dentro de um mecanismo que já existe. Baixo risco técnico, mas exige definição pedagógica (não é só código).
- **Risco**: baixo tecnicamente; depende de decisão de conteúdo.

## 5. Lógica de seleção de repertório de scaffolding (dois eixos)

Parte do fluxo de tentativas N=3 ainda em aberto: qual apoio (dos 3 tipos já implementados) oferecer, em que combinação, ao longo das tentativas.

- **Trabalho**: exige critério pedagógico novo (dois eixos de decisão) antes de qualquer código — mais decisão de design do que implementação mecânica.
- **Risco**: médio — decisão ainda não definida, pode exigir iteração.

## 6. Evento `FEEDBACK_EXIBIDO` + arquitetura envelope/payload de eventos

`gerard-semantic-event-logging`: arquitetura de eventos com envelope (contexto: sessão, tentativa, situação, representação) + payload, e o evento específico `FEEDBACK_EXIBIDO`, documentados mas não implementados.

- **Trabalho**: mudança estrutural no sistema de log de eventos, usado pela coleta de dados de pesquisa — toca vários pontos de disparo de evento.
- **Risco**: médio a alto — muda a forma como eventos são registrados, não só adiciona um evento novo isolado.

## 7. Automatização de passos (scaffolding tipo 4)

`gerard-scaffolding-interacao`: quarto tipo de apoio pedagógico, hoje só um rótulo de legenda não usado existe; nenhum código implementa. A própria skill exige que, "em hipótese alguma", a ordem de automação seja decidida sem autorização explícita do pesquisador — ou seja, o desenho pedagógico completo (o que automatizar, quando, sob que autorização) ainda precisa ser feito do zero, orientado diretamente pela usuária.

- **Trabalho**: maior item da lista — novo tipo de scaffolding, sem desenho pedagógico prévio, exige definição passo a passo com a usuária antes de qualquer linha de código.
- **Risco**: alto — nada está decidido ainda, nem o comportamento, só o rótulo.

---

*Não inclui o roteiro da skill `gerard-handlers-de-interacao` (extração de handlers de interação de `Main.java`), tratado separadamente por já ter seu próprio roteiro incremental documentado.*
