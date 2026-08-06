# Itens 8, 9 e 10 da fila da Revisão 5 — status

Data: 2026-08-05. Nenhum dos diffs aplicado ainda.

---

## Item 8 (Seção 21, Seção 31.8) — evento `FEEDBACK_EXIBIDO`

Diff **sem mudança** em relação ao já mostrado nesta sessão (ver `RELATORIO_DIFF_PROPOSTO_FEEDBACK_EXIBIDO...` se existir, ou a mensagem anterior desta conversa) — só a seção "Fontes" ganhou uma citação adicional: Don Norman (*The Design of Everyday Things*), com a correção do próprio Norman de "affordance" para "affordance percebida" (em telas, controla-se a affordance percebida, nunca a percepção real do usuário).

Diff proposto (inserção em `REFERENCE.md` §4.8, após o trecho de versionamento de esquema, antes de "### 4.8.1"):

```diff
 Esta é uma decisão de formato-alvo, não uma implementação —
 `EventoPapelQuantitativo` continua como está até uma decisão explícita de
 implementar a reestruturação envelope + payload (decisão anterior).
 
+Evento `FEEDBACK_EXIBIDO`: especificado, com critério de confirmação que
+depende da modalidade de entrega do Scaffolding (parágrafo sobre
+repertório de Scaffolding, acima):
+
+- **Modalidades passivas** (visual, sonora): critério "renderizado" --
+  dispara quando o componente de interface foi de fato construído e
+  exibido na tela, ou o som foi de fato reproduzido -- não no momento em
+  que o sistema apenas decide mostrar algo (`chaveFeedbackPedagogico`
+  sendo produzida não basta). Alinhado à distinção entre impressão
+  servida e impressão renderizada usada em métricas de publicidade
+  digital (IAB/MRC).
+- **Modalidades interativas** (háptica, guiada por movimento, manipulativa):
+  critério "affordance ativada" -- dispara quando o mecanismo de interação
+  foi disponibilizado para o participante operar (ex.: a atração magnética
+  foi habilitada durante o arraste; o material concreto ficou
+  manipulável) -- não quando o participante de fato opera sobre ele. A
+  operação do participante é uma ação própria dele, registrada
+  separadamente (vocabulário de modalidade de interação, parágrafo
+  anterior), não parte deste evento.
+
+Em nenhum dos dois casos o evento afirma que o participante percebeu,
+entendeu ou prestou atenção ao feedback -- isso continua não registrado,
+consistente com a proibição já existente de declarar interpretações sobre
+o participante a partir de um evento isolado (Seção 4.10).
+
+Esta é uma decisão de especificação-alvo, não uma implementação -- nenhum
+evento `FEEDBACK_EXIBIDO` existe ainda no código.
+
 ### 4.8.1 Mobilização do invariante operatório: sugestão do sistema e atribuição do pesquisador
```

Citação a "Seção 4.10" conferida — existe (`REFERENCE.md:362`, "Evidência e hipótese analítica").

**Status: proposto, aguardando aprovação.**

---

## Item 9 (Seção 17 perguntas 5-7, Seção 22, Seção 31.9) — pergunta e resposta como par adjacente

Ponto de inserção confirmado: `REFERENCE.md` §4.9, após a linha 358 ("...devem ser vinculadas à tentativa e, quando aplicável, ao evento correspondente."), antes de "Elas não constituem automaticamente invariantes operatórios."

```diff
 Verbalizações e explicações são registros contextualizados da atividade. Podem ocorrer antes, durante ou depois de uma ação e devem ser vinculadas à tentativa e, quando aplicável, ao evento correspondente.
 
+Pergunta e resposta são registros distintos, correlacionados, não
+fundidos em um só -- modelados como um par adjacente (Schegloff & Sacks,
+1973, Análise da Conversação): dois registros produzidos por partes
+diferentes (o sistema ou pesquisador que pergunta; o participante que
+responde), ordenados (a pergunta é sempre a primeira parte, a resposta a
+segunda) e tipados (a pergunta torna um tipo específico de resposta
+esperado, não qualquer registro). A resposta sempre referencia a pergunta
+que a originou -- correlação, não identidade. Isso responde as perguntas
+5-7 da Seção 17 da Revisão 5: aceitação/rejeição continuam resultado da
+mesma ação (Seção 15); pergunta e resposta, quando existirem como
+elementos distintos de verbalização, seguem este modelo de par adjacente.
+
+Esta é uma decisão de modelagem-alvo, não uma implementação -- nenhuma
+classe de pergunta/resposta existe ainda no pacote piloto.
+
 Elas não constituem automaticamente invariantes operatórios.
```

**Status: proposto, aguardando aprovação.**

---

## Item 10 (Seção 13, Seção 31.10) — cinco elementos "origem" como Contextos Delimitados

**Bloqueado — não posso propor o diff ainda.** A instrução pedia para eu mostrar onde essa comparação já existe em `REFERENCE.md` antes de propor a inserção exata. Investiguei e **ela não existe**: busquei todo o arquivo por `OrigemAvaliacao`, `ontologia_gerard.json`, `origemEvento`, `invarianteOrigem` — nenhuma ocorrência de comparação consolidada. As únicas menções a `OrigemAcao` isolada estão em três pontos (`REFERENCE.md:211, 277, 354`), nenhum deles uma tabela ou comparação dos cinco elementos.

Essa comparação existe só no relatório da Revisão 5 (nunca salvo no repositório, confirmado em investigação anterior desta sessão) e no scratchpad `revisao5_relatorio.md`.

**Preciso de decisão sobre onde inserir este parágrafo** antes de propor um diff — por exemplo: nova subseção dedicada (`### 4.8.2`?), nota solta em §4.8 perto das outras notas de `OrigemAcao`, ou outro lugar do arquivo. Não presumi.

---

## Resumo do que falta

- Aprovar item 8 (diff pronto, texto igual ao já mostrado antes).
- Aprovar item 9 (diff pronto, novo).
- Decidir onde vai o item 10 antes que eu possa propor o diff dele.
