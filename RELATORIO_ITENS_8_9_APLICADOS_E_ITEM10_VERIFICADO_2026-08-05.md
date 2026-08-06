# Itens 8/9 aplicados; item 10 verificado contra o código e diff proposto

Data: 2026-08-05.

---

## Itens 8 e 9 — aplicados

- **Item 8** (evento `FEEDBACK_EXIBIDO`): aplicado em `REFERENCE.md` §4.8, logo após o trecho de versionamento de esquema, antes de "### 4.8.1". Conferido linha a linha, sem divergência.
- **Item 9** (pergunta/resposta como par adjacente): aplicado em `REFERENCE.md` §4.9, após o trecho sobre vinculação à tentativa/evento. Conferido linha a linha, sem divergência.

Nenhuma recompilação nem harness rodados (só documentação). Nenhum commit, nenhum push.

---

## Item 10 — verificação dos cinco elementos "origem" contra o código atual

Local definido: nova subseção `REFERENCE.md ### 4.8.2`, logo depois de 4.8.1.

Todos os cinco confirmados — nenhum ajuste necessário no texto proposto:

1. **`OrigemAvaliacao`** (`src/gerard/pesquisador/auditoria/OrigemAvaliacao.java:12-22`): ainda os mesmos 10 valores, distinção canônico/reativo intacta via `isCanonica()` (`SOLTURA_USUARIO`, `SELECAO_CATEGORIA`, `SELECAO_SINAL`, `QUANTIFICACAO`, `SOLICITACAO_AJUDA` = canônicos; `SINCRONIZACAO_REPRESENTACOES`, `REAVALIACAO_CONSISTENCIA`, `ATUALIZACAO_DIAGRAMA`, `ATUALIZACAO_REPRESENTACAO`, `OUTRO` = reativos).
2. **Lista `"agentes"` de `ontologia_gerard.json`** (linhas 93-98): `["USUARIO", "COMPUTADOR", "PESQUISADOR", "SISTEMA"]` — mesmos quatro valores.
3. **`origemEvento`** (`EventoLogGerard.java:68`): continua campo de texto livre, sem enum, não afetado por nenhuma correção desta sessão.
4. **`invarianteOrigem`** (`EventoLogGerard.java:74`, atribuído em `TelaArtefatoExplicativo.java:393`): `"CATALOGO"`/`"PESQUISADOR"`, consistente com §4.8.1.
5. **Códigos de agente** (`EventoLogGerard.normalizarAgente`, linhas 239-247): três ramos — `"C"` (239-241), `"P"` (242-244), `"S"` (245-247) — reflete a correção já aplicada nesta sessão.

## Diff proposto — `REFERENCE.md ### 4.8.2` (não aplicado)

Inserção após a linha 382 (fim de §4.8.1), antes de "### 4.9":

```diff
 O campo de origem do invariante (`"PESQUISADOR"`/`"CATALOGO"`) responde uma
 pergunta diferente — de onde veio o valor do código escolhido (criado pelo
 pesquisador ali mesmo, ou selecionado de um catálogo já existente), não
 quem registrou a linha. Nenhum dos campos de agente ou de origem do
 invariante corresponde a `OrigemAcao` (seção 4.8).
 
+### 4.8.2 Os cinco elementos "origem" fora de OrigemAcao
+
+Os cinco elementos "origem" fora de `OrigemAcao` permanecem separados por
+design, não por lacuna a resolver. Cada um vive em um Contexto Delimitado
+(Bounded Context, Eric Evans, Domain-Driven Design) genuinamente
+diferente: `OrigemAcao` no pacote piloto de domínio; `OrigemAvaliacao` na
+avaliação de agentes pedagógicos; a lista de agentes de
+`ontologia_gerard.json` no domínio de conhecimento de pesquisa;
+`origemEvento` na proveniência de importação de dados históricos;
+`invarianteOrigem` na autoria de anotação teórica (§4.8.1); os códigos
+`"S"`/`"C"`/`"P"` no log de produção (§4.8.1).
+
+Um Contexto Delimitado é, segundo Evans, uma fronteira onde se elimina
+ambiguidade -- termos, definições e regras se aplicam de forma consistente
+dentro dele, mas não precisam (e não devem) ser forçados a um modelo único
+fora dele. Nenhuma unificação é proposta -- a coincidência parcial de
+vocabulário entre alguns desses elementos (ex.: USUARIO/SISTEMA/
+PESQUISADOR aparecendo em mais de um) é superficial, não estrutural.
+
 ### 4.9 Verbalização e explicação
```

## Status

Itens 8/9: aplicados. Item 10: proposto, aguardando aprovação.
