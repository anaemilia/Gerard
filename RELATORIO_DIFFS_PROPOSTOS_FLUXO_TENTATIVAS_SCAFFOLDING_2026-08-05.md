# Diffs propostos — cardinalidade ação:evento (itens 3/4 da Revisão 5) e repertório local de Scaffolding

Data: 2026-08-05. **Aplicados os dois** — Diff 1 em `REFERENCE.md:216-236`, Diff 2 criando `TAREFA_PENDENTE_FLUXO_TENTATIVAS_E_SCAFFOLDING.md`, ambos conferidos linha a linha contra o texto abaixo, sem divergência. Nenhuma recompilação nem harness necessários (só documentação). Nenhum commit, nenhum push.

## Contexto da decisão

Cardinalidade ação:evento adota a Alternativa B (1:N): uma ação começa na primeira tentativa de posicionamento de um item; se aceita, termina ali (um evento só); se rejeitada, cada nova tentativa do mesmo item é um evento correlacionado à mesma ação, até um limite de N=3 tentativas rejeitadas (fixo). Na terceira rejeição, o sistema exibe uma tela de ajuda; essa exibição fecha a ação. Acionar "restaurar" depois disso inicia uma ação nova e separada.

Princípio arquitetural adicional: cada objeto semântico deve carregar seu próprio repertório de Scaffolding (estilos de interação, mensagens, tipos de ajuda concreta) como conhecimento local — princípio da localidade do conhecimento. Seleção de qual elemento do repertório usar é decisão separada, ainda em aberto.

Nada disto está implementado. Não autorizado a começar implementação — só registro normativo e tarefa pendente.

---

## Diff 1 — `REFERENCE.md` §4.8

Inserção após a linha 214 ("...ver registro separado sobre a lacuna de log abaixo."), antes de "### 4.8.1":

```diff
 Nota de implementação (`OrigemAcao`, pacote piloto): esse vocabulário hoje
 existe só em `gerard.dominio.campoaditivo`, sem conexão com o log de ações
 de produção (`LoggerInteracaoGerard`/`EventoLogGerard`) — ver registro
 separado sobre a lacuna de log abaixo.
 
+Cardinalidade ação:evento: adotada a Alternativa B (1:N). Uma ação começa
+na primeira tentativa de posicionamento de um item; se aceita, a ação
+termina ali, um único evento. Se rejeitada, cada nova tentativa do mesmo
+item é um evento correlacionado à mesma ação (mesmo `action_id`), até um
+limite de `N=3` tentativas rejeitadas -- fixo, para não repetir a mesma
+mensagem de ajuda mais de três vezes ao participante. Na terceira
+rejeição, o sistema exibe uma tela de ajuda; essa exibição fecha a ação.
+Acionar o botão "restaurar" depois disso inicia uma ação nova, separada.
+
+Isso introduz um novo campo, `action_id`, que correlaciona os eventos de
+uma mesma ação -- distinto de `event_id` (o identificador de cada evento
+individual; hoje é `id_acao`, cuja renomeação para `event_id` é
+recomendada mas ainda não aplicada nesta decisão).
+
+Cada objeto semântico deve carregar seu próprio repertório de Scaffolding
+(estilos de interação possíveis -- manipulação, som, vibração, atração
+magnética, etc. -- mensagens e tipos de ajuda concreta) como conhecimento
+local, consistente com o princípio da localidade do conhecimento: manter
+esse repertório espalhado pela interface causaria inconsistência. A
+seleção de qual elemento do repertório usar em cada situação é uma decisão
+separada e ainda em aberto -- não resolvida por este registro.
+
 ### 4.8.1 Mobilização do invariante operatório: sugestão do sistema e atribuição do pesquisador
```

## Diff 2 — arquivo novo `TAREFA_PENDENTE_FLUXO_TENTATIVAS_E_SCAFFOLDING.md`

```diff
--- /dev/null
+++ TAREFA_PENDENTE_FLUXO_TENTATIVAS_E_SCAFFOLDING.md
@@
+# Tarefa pendente — fluxo de N=3 tentativas rejeitadas + repertório local de Scaffolding
+
+Status: **registrada como alvo de implementação futura. Não autorizada a
+começar.** Momento de implementação ainda não decidido.
+
+---
+
+## O fluxo-alvo
+
+N=3 tentativas rejeitadas do mesmo item → tela de ajuda → acionar
+"restaurar" inicia uma ação nova e separada. Decisão normativa completa em
+`REFERENCE.md §4.8` (cardinalidade ação:evento, Alternativa B, `action_id`).
+
+## Onde deve morar cada responsabilidade, quando implementado
+
+- **Contagem de tentativas e `action_id`**: conhecimento do próprio objeto
+  semântico (papel) -- consistente com o princípio da localidade do
+  conhecimento já adotado no projeto.
+- **Vibração/som**: execução mecânica da interface, reagindo a um sinal do
+  domínio -- mesmo padrão já usado por `chaveFeedbackPedagogico` em
+  `DiagnosticoErroPapel`.
+- **Escolha de qual ajuda concreta mostrar**: política pedagógica, hoje
+  associada a `gerard.Scaffolding.*` (produção) -- mas a lógica de seleção
+  em si ainda não está decidida (ver pendência nova abaixo).
+
+## Pendência nova, separada desta
+
+A lógica de seleção do estilo/ajuda apropriado dentro do repertório de
+Scaffolding de cada objeto -- ainda não decidida, não é resolvida por este
+registro.
+
+## Autorização
+
+Nenhum código, teste ou comportamento muda por este registro. Não
+autorizada a começar a implementação -- só o registro normativo
+(`REFERENCE.md §4.8`) e esta tarefa pendente existem até aqui.
```

---

## Status

Aplicados os dois em 2026-08-05. Nenhum commit, nenhum push sem pedido explícito.
