# Diff final proposto — REFERENCE.md §4.8.1 (agente "P")

Data: 2026-08-05. **Aplicado** — aprovado e aplicado em `REFERENCE.md:213-249`, texto conferido linha a linha contra este diff, sem divergência. Nenhuma recompilação nem harness necessários (mudança só em documentação). Nenhum commit, nenhum push.

## Estado atual confirmado

`REFERENCE.md` §4.8.1 (`.claude/skills/gerard-semantic-model/REFERENCE.md:213-220`) continua na versão antiga — só `"S"`/`"C"`, descreve `"S"` como "sempre o participante" para a linha de `registrarExplicacaoMatematica`. Nada mudou desde `RELATORIO_SENHA_TELA_ARTEFATO_EXPLICATIVO_2026-08-05.md`.

Este texto incorpora as duas investigações anteriores desta sessão:
- `RELATORIO_ORIGEM_AGENTE_P_2026-08-05.md` — origem e mecânica do código `"P"`.
- `RELATORIO_SECOES_TELA_ARTEFATO_EXPLICATIVO_2026-08-05.md` — só a seção 4 da tela ("PREENCHIMENTO DO PESQUISADOR", `TelaArtefatoExplicativo.java:234-305`) é estruturalmente do pesquisador; a seção 1 (autorrelato, linhas 150-205) não é.

## Diff proposto (linhas 213-220 de REFERENCE.md)

```diff
-O registro de log distingue dois campos que respondem perguntas diferentes
-e não devem ser lidos como contraditórios entre si: o agente da linha
-(`"S"`/`"C"`, em `LoggerInteracaoGerard`) identifica quem realizou a ação de
-explicar/preencher a tela — sempre o participante (`"S"`), mesmo quando a
-mesma linha carrega a atribuição de um invariante; o campo de origem do
-invariante (`"PESQUISADOR"`/`"CATALOGO"`) identifica de onde veio o valor do
-código escolhido — não quem o escolheu, que é sempre o pesquisador. Nenhum
-dos dois campos corresponde a `OrigemAcao` (seção 4.8).
+O registro de log usa um terceiro código de agente, `"P"` (Pesquisador),
+além de `"S"` (participante) e `"C"` (computador): a linha gravada por
+`registrarExplicacaoMatematica` usa `"P"` porque carrega a atribuição de um
+invariante -- seção estruturalmente rotulada "PREENCHIMENTO DO PESQUISADOR"
+na tela (`TelaArtefatoExplicativo.java:234-305`: combobox `invarianteCatalogo`
++ campo `observacaoInvariante`). Atribuir um invariante é sempre uma decisão
+do pesquisador, nunca do participante -- nunca `"S"` para essa linha.
+
+Essa mesma linha também carrega o autorrelato do participante
+(`explicacaoElemento`/`dificuldade`, seção "Tarefa matemática",
+`TelaArtefatoExplicativo.java:150-205`) -- conteúdo do participante, sem
+rótulo de autoria do pesquisador no código ou na interface. Marcar a linha
+inteira como `"P"` é uma simplificação conhecida: hoje não há um esquema
+que distinga, dentro da mesma linha, a autoria de cada trecho
+separadamente. Consequência prática: linhas de atribuição de invariante
+não entram nas contagens e detecções de ação do participante que dependem
+do agente `"S"` (ex.: sequências causais computador-participante em
+`TelaVisaoPesquisador`).
+
+A abertura de `TelaArtefatoExplicativo` exige senha do pesquisador
+(`autenticarPesquisador()`, reaproveitando `SENHA_VISAO_PESQUISADOR`,
+`Main.java:2154-2158`), sempre, sem exceção -- a tela só é construída depois
+da autenticação bem-sucedida. O motivo é a seção do pesquisador: o
+invariante operatório é uma ação mental do participante que só o
+pesquisador está em posição de validar e codificar -- a senha garante que
+o pesquisador esteja presente para fazer isso. Ela não implica que o
+autorrelato também seja preenchido pelo pesquisador; a estrutura da tela
+mostra o contrário (seção 1, sem rótulo de autoria do pesquisador). A
+senha não resolve a simplificação do parágrafo anterior -- só garante que a
+parte que exige o pesquisador (a seção 4) tenha, de fato, o pesquisador
+presente.
+
+O campo de origem do invariante (`"PESQUISADOR"`/`"CATALOGO"`) responde uma
+pergunta diferente -- de onde veio o valor do código escolhido (criado pelo
+pesquisador ali mesmo, ou selecionado de um catálogo já existente), não
+quem registrou a linha. Nenhum dos campos de agente ou de origem do
+invariante corresponde a `OrigemAcao` (seção 4.8).
```

## Status

Aplicado em 2026-08-05. Nenhum commit, nenhum push sem pedido explícito.
