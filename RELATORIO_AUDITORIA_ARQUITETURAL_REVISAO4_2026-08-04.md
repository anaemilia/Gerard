# GERARD — AUDITORIA ARQUITETURAL · REVISÃO 4
## Alinhamento completo ao Modelo Semântico de Referência

Modo: somente leitura. Nenhum arquivo de código, teste, configuração, dado, documento normativo ou artigo foi alterado na produção deste relatório. Esta execução substitui e completa a Revisão 3.

Legenda epistêmica usada em todo o documento: **[FATO DOCUMENTAL]** · **[EVIDÊNCIA NO CÓDIGO]** · **[EVIDÊNCIA DE EXECUÇÃO]** · **[INFERÊNCIA]** · **[PROPOSTA]** · **[DECISÃO HUMANA PENDENTE]** · **[INCONCLUSIVO]**

---

## 1. Identificação da fonte canônica

### 1.1 Aplicação da hierarquia de prioridade exigida

**Prioridade 1 — arquivo fornecido explicitamente para esta tarefa.** Não recebi nenhum caminho de arquivo nesta mensagem. O texto da QP foi fornecido verbatim pelo usuário na Seção 22 da instrução — esse trecho específico é tratado como dado diretamente pelo usuário (usado na Seção 22 deste relatório), mas isso não constitui "o artigo" como um todo. **[FATO DOCUMENTAL — sobre o que foi recebido nesta mensagem]**

**Prioridade 2 — PDF interno de um ZIP explicitamente designado como versão oficial.** Busquei, nos quatro pacotes de reprodutibilidade do artigo presentes em `Downloads` (v3.12 ×2 cópias idênticas por nome, v3.13, v3.14), qualquer frase que designasse um deles como "oficial" ou "canônico". Não encontrei essa frase em nenhum `LEIA-ME.md` ou `MANIFESTO.txt`. Encontrei uma única frase de designação indireta: o `REGISTRO_DA_CORRECAO.md` do pacote v3.13 declara — **[FATO DOCUMENTAL, `REGISTRO_DA_CORRECAO.md` do ZIP `Objetos_de_Dominio_Semanticamente_Ricos_Gerard_v3_13_consistencia.zip`, linha 3]**:

> "Esta versão foi produzida a partir do ZIP v3.12 indicado pela autora como fonte normativa."

Essa frase designa v3.12 — não v3.13 — como fonte normativa, e o faz em passado, no contexto de descrever a origem de uma correção. Não há declaração equivalente, em nenhum pacote, dizendo que v3.13 ou v3.14 é agora a fonte normativa corrente.

**Prioridade 3 — arquivo indicado pelo manifesto e pelas somas SHA-256 do pacote oficial.** Encontrei **quatro** pacotes distintos, cada um internamente autoconsistente (contém `MANIFESTO.txt` + `SOMAS_SHA256.txt` + `REGISTRO_DA_CORRECAO.md` + `RELATORIO_COMPILACAO.txt` próprios): v3.12 (2 cópias de mesmo conteúdo por nome de arquivo, não verificadas byte a byte nesta revisão), v3.13, v3.14. Nenhum desses pacotes se autodeclara "o pacote oficial" em oposição aos demais. Portanto a Prioridade 3 não resolve para um único candidato — resolve para **quatro candidatos não desempatados**.

Dados registrados sobre os quatro candidatos:

| Pacote | Arquivo interno | Páginas (RELATORIO_COMPILACAO) | QP única declarada | SHA-256 do PDF interno |
|---|---|---|---|---|
| v3.12 (`Objetos_..._Gerard.zip`) | `Gerard_Artigo_Portugues_v3_12.pdf` | 27 | Não declarado explicitamente no RELATORIO_COMPILACAO (não menciona QP) | não recomputado nesta revisão |
| v3.13 (`..._v3_13_consistencia.zip`) | `Gerard_Artigo_Portugues_v3_13.pdf` | 23 | Sim — "Questões de pesquisa: 1, identificada como QP" | `4847d7cf4c4b51c96e6c153e1f92e2545aba626b172fd1f666165c3531bce39c` |
| v3.14 (`..._v3_14.zip`) | `Gerard_Artigo_Portugues_v3_14.pdf` | 28 | Sim, segundo `REGISTRO_DA_CORRECAO.md`: "substituição das três questões anteriores pela única questão identificada como QP" | `7e33a048a1d2683ce31ab6bf85f1084822f925d5185796af6cf9f0d55e957911` |

Adicionalmente, dois arquivos PDF avulsos existem em `Downloads` sem pacote/manifesto próprio (`Objetos_..._Gerard__1_.pdf`, 03/08 22:54, e `Objetos_..._Gerard__1_ (1).pdf`, 03/08 22:58) — este último foi o arquivo lido na Revisão 3 e tratado ali como canônico por ser o mais recente por data de modificação. Essa Revisão 4 **não repete esse critério**, porque a instrução desta execução proíbe explicitamente escolher por data de modificação.

**[INFERÊNCIA, não usada para decidir]**: os números de versão (v3.12 → v3.13 → v3.14) sugerem uma linha evolutiva, e o conteúdo de `REGISTRO_DA_CORRECAO.md` de v3.14 descreve uma reestruturação (nova introdução, remoção de menções ao Claude de uma subseção, substituição de "três questões" por uma QP única) que soa posterior e mais abrangente que a de v3.13 (que já tinha QP única, mas 23 páginas, 5 a menos que v3.14). Isso é uma inferência de plausibilidade sobre ordem cronológica, não uma designação de "oficial" — e a instrução desta auditoria proíbe decidir por essa via.

### 1.2 Critério de parada aplicado

Como nenhuma das prioridades 1–3 resolve para um único arquivo inequívoco, aplico exatamente o critério de parada especificado:

> **"Não foi possível estabelecer qual arquivo representa a versão canônica do artigo. A auditoria não prosseguirá com comparações documentais até decisão explícita da autora."**

**Escopo desta parada**: refere-se especificamente a afirmações que dependam do texto exato de uma versão específica do artigo (contagem de páginas definitiva, citações literais de seção/página, lista definitiva das cinco contribuições tal como redigida em uma versão específica). **Não** interrompe as demais 47 seções desta auditoria, que dependem de evidência de código, git e execução — essas prosseguem normalmente, como a própria instrução prevê ao listar 31 seções de escopo majoritariamente independentes do texto do artigo.

Onde uma afirmação anteriormente atribuída "ao artigo" (nas Revisões 2 e 3) for necessária para contextualizar um achado desta revisão, ela é citada como **[INFERÊNCIA]**, rotulada explicitamente como proveniente de uma leitura anterior de um arquivo cuja canonicidade não está mais estabelecida nesta execução — nunca como **[FATO DOCUMENTAL]**.

### 1.3 O que pode ser dito com segurança, independente da versão

Todas as quatro versões examinadas (v3.12, v3.13, v3.14, e a lida na Revisão 3) compartilham: o mesmo título ("Objetos de Domínio Semanticamente Ricos para Estruturas Aditivas: A Arquitetura do Gerard"), a mesma autora (Ana Emilia de Melo Queiroz, UNIVASF) e — pelas evidências dos `RELATORIO_COMPILACAO.txt` — a presença de uma questão de pesquisa (QP), embora v3.12 não a declare explicitamente no relatório de compilação examinado. Nenhuma versão é o artigo de codesign humano-IA (`ARTIGO_CODESIGN_HUMANO_IA_GERARD.pdf`), que trata de 113 ciclos de decisão C01–C147 e não desta arquitetura — esse artigo permanece excluído por instrução explícita da usuária.

---

## 2. Estado do repositório

**[EVIDÊNCIA DE EXECUÇÃO]** — comandos executados em `C:\Users\cecomp\Documents\aemq\Gerard`, 2026-08-04:

| Campo | Valor |
|---|---|
| Branch atual | `projeto-piloto-nova-arquitetura` |
| HEAD | `b43c61adcae85e8c1c8267b9be425344ac5aa550` — "docs(skills): atualiza skills para v2 e renomeia GERARD_SemanticModel" — 2026-08-04 14:11:19 -0300 |
| Tags | `baseline-piloto-papel-quantitativo` (aponta para `7f6ad5c`); `baseline-piloto-papel-quantitativo-v2` (aponta para `bc0e569`); `instalador-windows-v1.0.0` (não relacionada ao piloto) |
| Remoto | `origin` → `https://github.com/anaemilia/Gerard.git` (fetch e push) — nenhum comando de rede foi executado nesta revisão |
| Branches locais | `ajuda-adaptativa`, `feedback-visual-agentes`, `historico-2011`, `main`, `painel-atalho-categoria`, `projeto-piloto-nova-arquitetura` (atual) |
| `git status --short` | vazio (0 arquivos) — verificado antes e depois de toda operação de leitura/compilação/execução desta revisão |
| Arquivos modificados/staged/não rastreados | nenhum |
| Data do último commit | 2026-08-04 14:11:19 -0300 (`b43c61a`) |
| `bc0e569` é ancestral de `b43c61a`? | Sim — `git merge-base --is-ancestor bc0e569 b43c61a` retornou verdadeiro; histórico linear, sem divergência |

**Commits relacionados ao piloto** (`git log --oneline --all -- src/gerard/dominio/campoaditivo`):

| Commit | Data | Mensagem |
|---|---|---|
| `7f6ad5c` | 2026-08-01 20:21:12 -0300 | feat(domain): add isolated PapelQuantitativo architecture pilot |
| `48c917d` | 2026-08-01 20:29:02 -0300 | feat(domain): add isolated TransformacaoMedidas architecture pilot |
| `79476b2` | 2026-08-01 21:48:39 -0300 | fix(domain): distinguish structural relations from operative invariants |
| `bc0e569` | 2026-08-01 22:17:31 -0300 | refactor(domain): decouple semantic representation from diagram rendering |
| `b43c61a` | 2026-08-04 14:11:19 -0300 | docs(skills): atualiza skills para v2 e renomeia GERARD_SemanticModel |

Hashes dos arquivos do pacote piloto que poderiam ser alterados em uma futura implementação (SHA-256, estado do working tree nesta revisão, calculados apenas para registro — nenhum arquivo foi alterado):

| Arquivo | Situação |
|---|---|
| `src/gerard/dominio/campoaditivo/OrigemAcao.java` | lido integralmente nesta revisão, 18 linhas, inalterado |
| `src/gerard/dominio/campoaditivo/PapelQuantitativo.java` | lido integralmente, 194 linhas, inalterado |
| `src/gerard/dominio/campoaditivo/evento/EventoPapelQuantitativo.java` | lido integralmente, 96 linhas, inalterado |
| `src/gerard/dominio/campoaditivo/RelacaoEstruturalTransformacao.java` | lido integralmente, 113 linhas, inalterado |
| `src/gerard/dominio/campoaditivo/RelacaoEstruturalComposicao.java` | lido integralmente, 67 linhas, inalterado |

Não executei `git fetch`, `git pull` nem qualquer comando de rede nesta revisão.

---

## 3. Documentos examinados

| # | Documento | Caminho | Examinado nesta revisão |
|---|---|---|---|
| 1 | Artigo canônico | — | **Não determinado — ver Seção 1.2** |
| 2 | REFERENCE.md | `.claude/skills/gerard-semantic-model/REFERENCE.md` | Sim, integralmente (217 linhas) |
| 3 | MIGRATION_GUIDE.md | `.claude/skills/gerard-semantic-model/MIGRATION_GUIDE.md` | Sim, integralmente (46 linhas) |
| 4 | SKILL.md — Knowledge-Oriented Domain Objects | `.claude/skills/gerard-knowledge-oriented-domain-objects/` | Sim, integralmente (conteúdo já em contexto desta sessão) |
| 5 | SKILL.md — Knowledge Locality Principle | `.claude/skills/gerard-knowledge-locality-principle/` | Sim, integralmente |
| 6 | SKILL.md — Semantic Event Logging | `.claude/skills/gerard-semantic-event-logging/` | Sim, integralmente (141 linhas) |
| 7 | SKILL.md — Domain Model First | `.claude/skills/gerard-domain-model-first/` | Sim, integralmente |
| 8 | Matriz Suplementar S1 | — | Não encontrada — ver Seção 4 |
| 9 | Arquivo Suplementar S2 | — | Não encontrado — ver Seção 4 |
| 10 | Relatório de implementação em duas fases | — | Não encontrado como arquivo rastreado — ver Seção 4 |
| 11 | Manifesto (do código) | — | Não encontrado — ver Seção 4 |
| 12 | Somas SHA-256 (do código) | — | Não encontrado — ver Seção 4 |
| 13 | Registro de correções (do código) | — | Não encontrado — ver Seção 4 |
| 14 | Relatório de compilação (do código) | — | Não encontrado como arquivo — recriado nesta revisão como evidência de execução, Seção 5 |
| 15 | Arquivos de migração | `.claude/skills/gerard-semantic-model/MIGRATION_GUIDE.md` | Sim (é o próprio item 3) |
| 16 | Harnesses | `src/TestePilotoPapelQuantitativo.java`, `src/TestePilotoTransformacaoMedidas.java` | Sim, integralmente, e executados — Seção 5 |
| 17 | Código relevante do pacote piloto | `src/gerard/dominio/campoaditivo/**` (17 arquivos) | Lidos integralmente: `OrigemAcao`, `ContextoAcao`, `PapelQuantitativo`, `RelacaoEstruturalComposicao`, `RelacaoEstruturalTransformacao`, `EventoPapelQuantitativo`, `PublicadorEventoDominio`; demais 10 arquivos não lidos integralmente nesta revisão (listados na Seção 4) |
| 18 | Esquemas JSON/TSV/JSONL de registro | `dados/schema_agentes_execucao_gerard.json` e outros — ver Seção 7 (regressão de vocabulário) | Trechos relevantes examinados via agente de pesquisa |

---

## 4. Artefatos ausentes ou ambíguos

**[EVIDÊNCIA DE EXECUÇÃO]** — `git ls-files | grep -iE "MANIFESTO|SOMAS_SHA256|matriz.*S1|suplement.*S2|RELATORIO_COMPILACAO|REGISTRO_DA_CORRECAO"` não retornou nenhuma linha.

| Artefato | Declarado por versões do artigo examinadas em revisões anteriores | Encontrado localmente | Caminho | Estado |
|---|---|---|---|---|
| Referência do Modelo Semântico | Sim | Sim | `.claude/skills/gerard-semantic-model/REFERENCE.md` | encontrado e examinado |
| Guia de migração | Sim | Sim | `.claude/skills/gerard-semantic-model/MIGRATION_GUIDE.md` | encontrado e examinado |
| 4 skills de desenvolvimento | Sim | Sim | `.claude/skills/gerard-*` | encontrado e examinado |
| Matriz Suplementar S1 | Sim (em versões examinadas anteriormente) | Não | — | não encontrado no repositório local examinado |
| Arquivo Suplementar S2 | Sim | Não | — | não encontrado no repositório local examinado |
| Relatório de implementação em duas fases (arquivo dedicado versionado) | Sim | Não como arquivo git-tracked | — | não encontrado nos arquivos rastreados da branch e do repositório local examinados; existe conteúdo equivalente apenas como Artifact publicado fora do git nesta mesma conversa (Revisões 1–3) |
| `MANIFESTO.txt` do código | Sim | Não | — | não encontrado no repositório local examinado |
| `SOMAS_SHA256.txt` do código | Sim | Não | — | não encontrado no repositório local examinado |
| Registro de correções do código | Sim | Não | — | não encontrado no repositório local examinado |
| Lista `@arquivo` histórica usada pelo `javac` | Não declarado explicitamente como artefato do pacote | Não | — | não encontrado — não há `.lst`, `filelist`, `sources.txt`, `build.log` rastreados (`git ls-files` filtrado, vazio) |

**Não afirmo que esses artefatos não existam globalmente** — podem estar em outro remoto, outra branch não fornecida, ou em algum dos pacotes `.zip` do artigo em `Downloads` (que contêm seus próprios `MANIFESTO.txt`/`SOMAS_SHA256.txt`, mas referentes ao texto do artigo, não ao código-fonte do piloto). O estado acima é estritamente sobre a branch e o repositório local examinados nesta revisão.

---

## 5. Evidências sobre 413 arquivos

**[EVIDÊNCIA DE EXECUÇÃO]** — busca por "413" em todo o conteúdo rastreado do repositório (`git grep -n "413" -- '*.md' '*.txt' '*.java' '*.json' '*.tsv' '*.properties'`): 10 ocorrências, **todas** substrings numéricas incidentais em dados de mineração/testes não relacionados (ex.: `RESULTADOS_TESTES_C165/resumo.tsv:44` — duração de teste "4131" ms; `dataset_modelagem_com_predicoes.tsv:371` — um ID de linha "1413"). Nenhuma ocorrência de "413 arquivos" ou equivalente em nenhum artefato rastreado pelo git.

**[EVIDÊNCIA DE EXECUÇÃO]** — não há lista `@arquivo`, log de compilação ou script de build rastreado que registre uma contagem de arquivos históricos (Seção 4).

**[INFERÊNCIA, rotulada como tal]** — versões do artigo lidas em revisões anteriores desta linha de auditoria (não recanonizadas nesta revisão — ver Seção 1.2) afirmavam repetidamente "413 arquivos Java" compilados durante a implementação de 01/08/2026. Não reafirmo esse número como **[FATO DOCUMENTAL]** nesta revisão porque a fonte que o continha não tem canonicidade estabelecida agora.

### 5.1 Recompilação real desta revisão

**[EVIDÊNCIA DE EXECUÇÃO]**:

- Diretório: `C:\Users\cecomp\Documents\aemq\Gerard`
- Comando: `javac -encoding UTF-8 -source 1.8 -target 1.8 -cp "lib/weka-stable-3.8.6.jar;lib/bounce-0.18.jar" -d C:\Users\cecomp\AppData\Local\Temp\gerard_audit_rev4_classes $(find src -name '*.java')`
- Data/hora: início 2026-08-04 16:19:34, fim 2026-08-04 16:19:46
- Código de saída: `0`
- Resumo da saída: 4 avisos, todos preexistentes (bootstrap classpath não configurado para `-source 8`; uma classe com operações não verificadas em `TesteUnidadeAnaliseABCD.java`) — nenhum erro.
- Diretório de saída fora do repositório: confirmado (`AppData\Local\Temp`, não dentro de `C:\Users\cecomp\Documents\aemq\Gerard`).
- `git status --short` antes: 0 arquivos. Depois: 0 arquivos.
- Quantidade de arquivos `.java` passados ao comando: **419** (`find src -name '*.java' | wc -l` executado imediatamente antes da chamada ao `javac`, mesmo valor usado no `$(...)`).

### 5.2 Matriz de categorias

| Categoria | Fonte / estado | Quantidade | Unidade contada | Verificação |
|---|---|---|---|---|
| A — arquivos rastreados no commit `7f6ad5c` (tag Fase 1) | `git ls-tree -r --name-only 7f6ad5c -- src \| grep -c '\.java$'` | 338 | arquivos `.java` rastreados | **[EVIDÊNCIA DE EXECUÇÃO]** |
| B — arquivos rastreados no commit `48c917d` (Fase 2) | idem | 341 | idem | **[EVIDÊNCIA DE EXECUÇÃO]** |
| C — arquivos rastreados no commit `79476b2` | idem | 346 | idem | **[EVIDÊNCIA DE EXECUÇÃO]** |
| D — arquivos rastreados no commit `bc0e569` (tag v2) | idem | 347 | idem | **[EVIDÊNCIA DE EXECUÇÃO]** |
| E — arquivos rastreados no commit `b43c61a` (HEAD) | idem | 419 | idem | **[EVIDÊNCIA DE EXECUÇÃO]** |
| F — arquivos no working tree atual | `find src -name "*.java" \| wc -l` | 419 | arquivos `.java` no disco | **[EVIDÊNCIA DE EXECUÇÃO]** |
| G — arquivos efetivamente passados ao `javac` nesta revisão | comando registrado em 5.1 | 419 | argumentos de arquivo `.java` | **[EVIDÊNCIA DE EXECUÇÃO]** |
| H — arquivos citados por versões do artigo lidas em revisões anteriores (não recanonizadas) | leitura anterior, fonte não recanonizada | "413" (não reconfirmado) | não especificado com precisão suficiente para reconstrução | **[INCONCLUSIVO]** |

### 5.3 Formulação mínima aceitável adotada nesta revisão

**[FATO DOCUMENTAL, com a ressalva de fonte não recanonizada]**: versões do artigo lidas anteriormente relatam que 413 arquivos Java foram compilados.

**[EVIDÊNCIA DE EXECUÇÃO]**: o HEAD atual (`b43c61a`) contém 419 arquivos Java, e uma recompilação real nesta revisão, com todos os 419 arquivos do working tree atual, teve código de saída 0.

**[INCONCLUSIVO]**: o conjunto exato dos 413 caminhos fornecidos à compilação histórica de 01/08/2026 não foi reconstruído com os artefatos disponíveis nesta revisão. Não apresento a hipótese `347 + 66 = 413` como fato — os 66 arquivos não foram identificados nominalmente, e não há evidência direta (lista, log) de que estivessem no working tree da execução histórica. Essa hipótese, se necessária, deve ser tratada como **[PROPOSTA]** de reconstrução, não como conclusão.

---

## 6. Estado atual de 419 arquivos

Já coberto nas linhas E, F e G da Seção 5.2 — **[EVIDÊNCIA DE EXECUÇÃO]** tripla e concordante: HEAD, working tree e argumento real ao `javac` nesta revisão convergem em 419.

---

## 7. Vocabulário das origens no artigo

Por força da Seção 1.2 (critério de parada), **não afirmo** nesta revisão nenhuma citação textual do artigo como **[FATO DOCUMENTAL]**. Registro como **[INFERÊNCIA]**, explicitamente não recanonizada: leituras anteriores (Revisão 3, arquivo `Objetos_..._Gerard__1_ (1).pdf`, canonicidade agora indeterminada) atribuíram a uma seção do artigo (numerada 3.7 naquela leitura) a distinção entre origem `sistema` (ação observável iniciada pela aplicação, ex.: apresentar feedback) e origem `processo computacional` (operação interna automática e determinística, ex.: cálculo por relação estrutural), além de `participante` e `pesquisador`. Essa distinção **não pôde ser reconfirmada nesta revisão** como pertencente a uma versão canônica específica, porque a Seção 1.2 impede comparação documental até decisão da autora sobre qual arquivo é a fonte.

Um dado indireto, porém **[FATO DOCUMENTAL]** sobre um artefato diferente (não o artigo, mas parte do seu pacote de reprodutibilidade): o arquivo `REGISTRO_DA_CORRECAO.md` da versão v3.13 (ver Seção 1.1) lista, entre as correções conceituais aplicadas àquela versão do texto, o item "distinção entre origem `sistema` e origem `processo computacional`" — confirmando que, em pelo menos uma revisão editorial documentada do artigo, essa distinção terminológica foi deliberadamente introduzida pela autora. Isso não substitui a leitura do texto final, mas é evidência documental direta (não inferência) de que a distinção existiu como decisão editorial registrada.

---

## 8. Vocabulário nos documentos normativos

**[EVIDÊNCIA NO CÓDIGO/DOCUMENTO]** — `REFERENCE.md:138-143`:

```
O evento deve distinguir a origem:
- `USUARIO`;
- `SISTEMA`;
- `INFERENCIA_COMPUTACIONAL`;
- `PESQUISADOR`.
```

Quatro categorias. `PROCESSO_COMPUTACIONAL` não aparece neste documento em nenhuma linha (confirmado por busca de agente — Seção 9). `REFERENCE.md:19` declara explicitamente: "As skills não podem redefinir os conceitos teóricos deste documento. Em caso de conflito, este modelo semântico prevalece." — isto é, o próprio REFERENCE.md se autodeclara superior às skills em caso de conflito, mas não faz nenhuma declaração sobre precedência em relação ao artigo.

**[EVIDÊNCIA NO CÓDIGO/DOCUMENTO]** — `MIGRATION_GUIDE.md:26`:

```
Se houver aplicação automática, o evento deve registrar origem `SISTEMA` ou `INFERENCIA_COMPUTACIONAL`.
```

Trata as duas categorias como alternativas para o mesmo caso ("aplicação automática"), sem definir um critério de desempate entre elas.

**[EVIDÊNCIA NO CÓDIGO/DOCUMENTO]** — `gerard-semantic-event-logging/SKILL.md:53-60`:

```
Usar enumeração explícita, no mínimo:
- `USUARIO`;
- `SISTEMA`;
- `INFERENCIA_COMPUTACIONAL`;
- `PESQUISADOR`.

Se uma relação estrutural calcula um valor, o evento deve registrar `SISTEMA` ou `INFERENCIA_COMPUTACIONAL`, nunca `USUARIO`.
```

Mesmo padrão do MIGRATION_GUIDE.md: trata o cálculo por relação estrutural como ambíguo entre `SISTEMA`/`INFERENCIA_COMPUTACIONAL`, sem critério de desempate.

**Conclusão factual desta seção**: os três documentos normativos internos ao repositório (`REFERENCE.md`, `MIGRATION_GUIDE.md`, `SKILL.md` de eventos) são internamente consistentes entre si (mesmas quatro categorias: `USUARIO/SISTEMA/INFERENCIA_COMPUTACIONAL/PESQUISADOR`), mas nenhum deles usa o termo `PROCESSO_COMPUTACIONAL`.

---

## 9. Vocabulário no código e nos dados

Levantamento bruto obtido por agente de pesquisa dedicado (busca `git grep` recursiva em todo o repositório, todos os tipos de arquivo relevantes), consolidado aqui.

### 9.1 Tipo `OrigemAcao` (pacote piloto) — confinamento total

**[EVIDÊNCIA NO CÓDIGO]** — `src/gerard/dominio/campoaditivo/OrigemAcao.java:9-17`: enum com `ORIGEM_USUARIO`, `ORIGEM_SISTEMA`, `ORIGEM_INFERENCIA`, `ORIGEM_PESQUISADOR`.

Consumidores confirmados, em **todo o repositório**: `PapelQuantitativo.java:130,136,141`, `RelacaoEstruturalTransformacao.java:15,70,77,83,88,95`, `ResultadoCalculo.java:17,26,30,45`, `EventoPapelQuantitativo.java:5,24,34,39,57,69`, e os dois harnesses `TestePilotoPapelQuantitativo.java:4,48` e `TestePilotoTransformacaoMedidas.java:5`. **Nenhuma outra classe em todo o repositório** importa ou referencia `gerard.dominio.campoaditivo.OrigemAcao`. `ORIGEM_INFERENCIA` e `ORIGEM_PESQUISADOR` só aparecem na própria declaração do enum — nenhum ponto de código os instancia.

**Nenhuma ocorrência** de `ORIGEM_USUARIO`/`ORIGEM_SISTEMA`/`ORIGEM_INFERENCIA`/`ORIGEM_PESQUISADOR` em arquivos `.json`, `.jsonl`, `.tsv` ou `.properties` de todo o repositório.

`PROCESSO_COMPUTACIONAL`: **nenhuma ocorrência em nenhum arquivo do repositório**, de nenhum tipo. `actionOrigin`: **nenhuma ocorrência**.

### 9.2 Três outros vocabulários de origem, paralelos e não conectados a `OrigemAcao`

Este é o achado central desta seção: **existem pelo menos quatro vocabulários de "origem de uma ação/evento" no repositório, todos mutuamente desconectados no código**:

**(a) `OrigemAcao` (pacote piloto)** — 4 valores, confinado a `gerard.dominio.campoaditivo` + 2 harnesses (Seção 9.1).

**(b) `OrigemAvaliacao` (produção, `gerard.pesquisador.auditoria`)** — **[EVIDÊNCIA NO CÓDIGO]** `OrigemAvaliacao.java:12-22`: `SOLTURA_USUARIO`, `SELECAO_CATEGORIA`, `SELECAO_SINAL`, `QUANTIFICACAO`, `SOLICITACAO_AJUDA`, `SINCRONIZACAO_REPRESENTACOES`, `REAVALIACAO_CONSISTENCIA`, `ATUALIZACAO_DIAGRAMA`, `ATUALIZACAO_REPRESENTACAO`, `OUTRO` (10 valores). Usado extensivamente em `Main.java` (linhas 10616-10618, 10965, e outras 10 ocorrências listadas pelo agente de pesquisa) e em todo `gerard.pesquisador.auditoria`/`gerard.pesquisador.analiseunidade`. Serializado como chave JSON `"origin"`, valores em `snake_case` minúsculo via `OrigemAvaliacao.paraTexto()` (ex.: `"soltura_usuario"`). Confirmado também nos schemas `dados/schema_agentes_execucao_gerard.json:90-98` e `dados/pacote_correcao_log_multiagente_gerard/schema_log_multiagente_canonico.json:136-144`, com a distinção documentada no próprio schema entre origens "canônicas" (gesto real do usuário: `soltura_usuario`, `selecao_categoria`, `selecao_sinal`, `quantificacao`, `solicitacao_ajuda`) e "reativas" (reavaliação interna disparada por uma canônica anterior, sem constituir novo gesto).

**(c) Vocabulário textual livre em `EventoLogGerard`/`LoggerInteracaoGerard`** — **[EVIDÊNCIA NO CÓDIGO]** campos `origemEvento`/`origem_evento` (string livre, ex. `"QUADROS_FIEIS_AGENTES_CS_PDF"`) e `invarianteOrigem`/`invariante_origem` (valores como `"CATALOGO"` ou `"PESQUISADOR"`, ver `TelaArtefatoExplicativo.java:393`), mais um código de agente separado de um caractere, `"C"`/`"S"`, normalizado por `EventoLogGerard.normalizarAgente(String)` (linhas 228-241) a partir de tokens livres como `"COMPUTADOR"`/`"SUJEITO"`/`"USUARIO"`.

**(d) Lista `"agentes"` em `ontologia_gerard.json:93-98`** — **[EVIDÊNCIA NO CÓDIGO/DADO]**: `["USUARIO", "COMPUTADOR", "PESQUISADOR", "SISTEMA"]` — quatro valores, nomes parcialmente coincidentes com `OrigemAcao` (`USUARIO`, `SISTEMA`, `PESQUISADOR`) mas com `COMPUTADOR` no lugar de `ORIGEM_INFERENCIA`/`PROCESSO_COMPUTACIONAL`, e sem prefixo `ORIGEM_`.

### 9.3 Regressão documental cruzada consolidada

| Artefato | Vocabulário atual | Significado atribuído | Conflito |
|---|---|---|---|
| `REFERENCE.md` §4.8 | `USUARIO/SISTEMA/INFERENCIA_COMPUTACIONAL/PESQUISADOR` | Não define diferença entre `SISTEMA` e `INFERENCIA_COMPUTACIONAL` | Sim — quarto valor não corresponde a `PROCESSO_COMPUTACIONAL` de nenhuma leitura anterior do artigo |
| `MIGRATION_GUIDE.md`/skill de eventos | idem | "cálculo automático" pode ser `SISTEMA` OU `INFERENCIA_COMPUTACIONAL`, sem critério | Sim — ambiguidade interna, não resolvida pelos próprios documentos normativos |
| `OrigemAcao.java` (código piloto) | `ORIGEM_USUARIO/SISTEMA/INFERENCIA/PESQUISADOR` | `ORIGEM_SISTEMA` javadoc: "calculado/aplicado automaticamente... a partir de uma relação estrutural"; `ORIGEM_INFERENCIA` javadoc: "proposto por um mecanismo de inferência (ex.: agente pedagógico), não uma regra determinística" | Sim — `ORIGEM_SISTEMA` no código cobre exatamente o caso de cálculo determinístico, e `ORIGEM_INFERENCIA` é reservado a um caso (inferência não determinística) sem nenhum consumidor real no código (Seção 10) |
| `RelacaoEstruturalTransformacao.java:70,77,83,88` (uso real) | `OrigemAcao.ORIGEM_SISTEMA` em todo cálculo | Todo cálculo de valor ausente por relação estrutural é `ORIGEM_SISTEMA` | Consistente internamente com o javadoc do próprio enum; conflito potencial só existe se comparado a uma versão do artigo que separe `sistema` de `processo computacional` — comparação suspensa por Seção 1.2 |
| `OrigemAvaliacao.java` (produção) | 10 valores, `snake_case`, serializado como `"origin"` | Distingue canônico/reativo, não usuário/sistema/pesquisador/inferência | Vocabulário estruturalmente diferente, não mapeável 1:1 para `OrigemAcao` |
| `ontologia_gerard.json` "agentes" | `USUARIO/COMPUTADOR/PESQUISADOR/SISTEMA` | Lista fechada, sem definição individual no próprio arquivo (não lida a definição de cada termo nesta revisão) | 4 valores, mas `COMPUTADOR` no lugar do 4º valor de `OrigemAcao` |
| `EventoLogGerard`/`LoggerInteracaoGerard` | string livre `origemEvento` + código `"C"`/`"S"` | Sem enumeração fechada — texto histórico proveniente de importação de dados de pesquisa antigos | Não comparável a enum; é o vocabulário mais antigo e menos estruturado dos quatro |

**Decisão sobre este conflito**: **[DECISÃO HUMANA PENDENTE]**. Não escolho silenciosamente qual dos quatro vocabulários deve prevalecer, nem proponho unificá-los nesta revisão — isso seria início de P1, fora do escopo de inspeção.

---

## 10. Mecanismos de inferência existentes

Levantamento por agente de pesquisa dedicado, verificado quanto à precisão de citação.

### 10.1 Classes examinadas e classificação

| Classe / método | Entrada | Saída | Determinístico? | Validação humana antes de uso | Persistido? |
|---|---|---|---|---|---|
| `AgenteModelador.armazenarCaso` (linhas 79-109) | `DiagnosticoTarefa`, `idempotencyKey` | Arquivamento; sem comparação de similaridade (comentário explícito do próprio código, linhas 157-158) | Sim | N/A (não produz sugestão) | Sim, `diagnosticos_tarefa.tsv` |
| `AgenteModelador.registrarExplicacaoNoUltimoDiagnostico` (209-234) | Texto livre de explicação | Chama `AnalisadorNivelConceitual.classificar` | Ver linha abaixo | Ver linha abaixo | Sim |
| `AgenteModelador.inferirRegras` (243-248) | Diagnósticos, limiares mínimos | Delega a `InferenciaRegrasModelador.inferir` | Ver linha abaixo | — | — |
| **`InferenciaRegrasModelador.inferir`** (99-141) — **único mecanismo estatístico/não determinístico localizado** | `List<DiagnosticoTarefa>` | `PART.toString()` + `Apriori.toString()` (Weka) | **Não** — classificador de regras (árvores C4.5 parciais) e associação estatística | **Nenhuma localizada** — saída nunca é lida por outro componente do pipeline | **Nenhuma persistência localizada** — objeto `Resultado` fica em memória |
| `AnalisadorNivelConceitual.classificar` (70-94) | Texto livre | Um de 5 níveis (`NivelConceitualExplicacao`) | Sim — casamento de substring contra listas fechadas de vocabulário, normalização de acentos | **Sim, explícita** — `TelaVisaoPesquisador.java:787-804`, combo de curadoria humana grava `nivelConceitualCurado` | Sim, `diagnosticos_tarefa.tsv`, colunas de estimado e curado |
| `SugestorInvarianteOperatorio.sugerirCodigo` (31-64) | categoria, chave de papel incógnito | Código de catálogo fechado ou `null` | Sim — tabela `switch`/`if` fixa | Sim, por design (comentário explícito: "nunca salva sozinho") — ponto de consumo em tela não lido nesta revisão | Indiretamente, se o pesquisador confirmar |
| `ConectorVereditoModelador.mapearSuporte` (79-85) | `CamadaEstrategiaZDP` | `NivelSuporte` | Sim — tabela de conversão fixa | N/A | — |
| `AgenteMonitor.avaliarPosicionamento`/`avaliarCategoria`/etc. (67-262) | Estado do papel/diagrama | Veredito de correção | Sim — comparações booleanas diretas; comentário do próprio código: "usedInFinalDecision=false" para regras de domínio anexadas apenas como contexto | Não — veredito usado imediatamente para feedback | Via `AgentAuditService`/`JsonlAgentAuditWriter` |
| `AgenteZDP.decidirEstrategia`/`calcularCamada` (99-232) | Contagem de erros consecutivos por (usuário, tarefa) | Camada N0-N2 | Sim — if/else sobre inteiros | Não | Via `AgentAuditService` |
| `MotorRegrasConhecimento.regrasQueBatem`/`bate` (34-141) | Fatos + regras carregadas | Regras que batem, por prioridade | Sim — operadores fixos (`=`,`!=`,`>`,`entre` etc.), sem peso/probabilidade | N/A | Via consumidores |

### 10.2 Regras J48/PART/Apriori pré-mineradas (dados estáticos)

**[EVIDÊNCIA NO CÓDIGO]** `regras_j48_part.jsonl`/`regras_apriori.jsonl` contêm regras já mineradas fora do app, com `"status":"experimental"`. `LeitorBaseConhecimentoGerard.lerJ48Part()`/`lerApriori()` (linhas 41-47) existem, mas **nenhum chamador foi localizado** em `AgenteMonitor`, `AgenteZDP` ou qualquer outro ponto de produção — ambos usam apenas `lerDominio()`/`lerPedagogicas()`. `MotorRegrasConhecimento` só considera regras com `status="ativa"` (Seção 10.1), e essas regras pré-minerabas têm `status="experimental"` — mesmo que fossem carregadas, ficariam inertes.

### 10.3 Respostas às perguntas obrigatórias

- **Existe mecanismo real de inferência no código?** Sim — `InferenciaRegrasModelador` (Weka PART + Apriori), único caso estatístico/não determinístico identificado nesta revisão.
- **Quais classes o executam?** `InferenciaRegrasModelador.inferir`, chamado por `AgenteModelador.inferirRegras`.
- **Qual entrada utiliza?** Lista de `DiagnosticoTarefa` já persistidos, mais dois limiares mínimos de instâncias.
- **Qual saída produz?** Texto (`String`) de regras PART e de associação Apriori — não uma decisão estruturada, não um score.
- **O resultado é determinístico?** Não (algoritmo estatístico/indutivo).
- **O resultado é uma hipótese?** Funcionalmente sim — texto de regras candidatas — mas não é modelado como `HipóteseAnalítica` no sentido de `REFERENCE.md §4.10`; é uma `String` solta em um objeto `Resultado`.
- **Quem valida ou revisa?** **[INCONCLUSIVO]** — nenhum ponto de validação humana localizado para esta saída específica nesta revisão (diferente de `AnalisadorNivelConceitual`, que tem curadoria confirmada).
- **O resultado é persistido?** **[EVIDÊNCIA NO CÓDIGO, negativa]** — nenhuma persistência localizada.
- **Há dados existentes com essa origem?** **[INCONCLUSIVO]** — não localizado nesta revisão; consistente com a ausência de persistência e de chamadores em produção (`AgenteModelador.inferirRegras` não tem nenhum ponto de chamada localizado em `Main.java` ou telas).

### 10.4 Relevância direta para a Seção 9 (vocabulário de origem)

Nenhum dos mecanismos de inferência auditados aqui usa ou produz um valor `OrigemAcao.ORIGEM_INFERENCIA`. `ORIGEM_INFERENCIA` permanece, nesta revisão, um valor de enum sem nenhum produtor real no código — nem determinístico nem estatístico.

---

## 11. Conflitos documentais

Consolidação dos conflitos já detalhados nas Seções 7-9:

1. **`INFERENCIA_COMPUTACIONAL` (docs normativos) vs. `PROCESSO_COMPUTACIONAL` (leitura anterior, não recanonizada, do artigo)** — nomes diferentes para um conceito potencialmente relacionado (operação automática do sistema); não resolvido nesta revisão por força da Seção 1.2.
2. **`OrigemAcao` (piloto, 4 valores) vs. `OrigemAvaliacao` (produção, 10 valores) vs. lista `"agentes"` de `ontologia_gerard.json` (4 valores, com `COMPUTADOR`) vs. vocabulário textual livre de `EventoLogGerard`** — quatro esquemas de origem paralelos, sem nenhuma ponte de código entre eles (Seção 9.2).
3. **`REFERENCE.md`/`MIGRATION_GUIDE.md`/skill tratam `SISTEMA` e `INFERENCIA_COMPUTACIONAL` como alternativas intercambiáveis** para o mesmo caso ("aplicação automática"), sem critério de desempate — ambiguidade interna aos próprios documentos normativos, independente de qualquer comparação com o artigo.
4. **Duas famílias de identificador de ação, ambas chamadas "action id" em português ou inglês, sem nenhuma ligação de código** — ver Seção 17 (Auditoria de `id_acao`).

Todos os quatro permanecem **[DECISÃO HUMANA PENDENTE]** nesta revisão.

---

## 12. Auditoria de P1

P1 trata da origem das ocorrências e de sua codificação — mas a Seção 9 revelou que o problema é mais amplo do que a comparação `OrigemAcao` vs. artigo tratada nas Revisões 2-3: há quatro vocabulários de origem coexistindo no repositório, não dois.

### 12.1 Consumidores de `OrigemAcao` — busca completa

**[EVIDÊNCIA DE EXECUÇÃO/CÓDIGO]** confirmada por agente de pesquisa com busca `git grep` recursiva em todo o repositório (não apenas o pacote piloto): os únicos consumidores de `gerard.dominio.campoaditivo.OrigemAcao` em todo o repositório são as próprias classes do pacote piloto (`PapelQuantitativo`, `RelacaoEstruturalTransformacao`, `ResultadoCalculo`, `EventoPapelQuantitativo`) e os dois harnesses `TestePilotoPapelQuantitativo.java`/`TestePilotoTransformacaoMedidas.java`. **Não uso a expressão "nenhum consumidor externo" sem essa busca completa — ela foi feita, e o resultado é: zero consumidores fora do pacote piloto e dos dois harnesses.**

### 12.2 Sete alternativas avaliadas

| Alternativa | Aderência ao artigo | Aderência ao modelo normativo | Aderência ao código atual | Arquivos afetados | Consumidores externos | Dados históricos afetados | Testes afetados | Reversibilidade | Risco |
|---|---|---|---|---|---|---|---|---|---|
| **A — renomear diretamente o enum** (`OrigemAcao` ganha `PROCESSO_COMPUTACIONAL`) | **[INCONCLUSIVO]** — depende de fonte não recanonizada | Exigiria alterar `REFERENCE.md`/`MIGRATION_GUIDE.md`/skill (que usam `INFERENCIA_COMPUTACIONAL`, não `PROCESSO_COMPUTACIONAL`) | Requer mudar 4 pontos de uso em `RelacaoEstruturalTransformacao` + javadoc | ~6 (enum, 4 usos, javadoc) | 0 (confirmado, Seção 12.1) | Nenhum dado persistido de produção usa `OrigemAcao` (Seção 9.1) — apenas harnesses em memória | 2 harnesses | Alta — rename simples | Baixo, dado zero consumidores externos |
| **B — preservar nomes internos + código de exportação** | **[INCONCLUSIVO]** | Não resolve a ambiguidade interna dos próprios documentos normativos (Seção 8) | Nenhuma mudança no enum em si | 1-2 (nova função de exportação) | 0 | Nenhum | 0 | Alta | Baixo, mas não resolve o conflito, só o esconde |
| **C — adaptador de serialização** | **[INCONCLUSIVO]** | Introduz conceito (`Adaptador`) não citado nas 4 skills examinadas | Nenhuma mudança no enum | 1-2 | 0 | Nenhum | 0 | Alta | Baixo, mesmo problema de B |
| **D — migração documental antes da migração do código** | Resolveria primeiro a ambiguidade interna de `REFERENCE.md`/`MIGRATION_GUIDE.md`/skill (Seção 8), independente do artigo | Corrige a ambiguidade `SISTEMA`/`INFERENCIA_COMPUTACIONAL` nos próprios documentos primeiro | Código muda só na 2ª etapa | 2-3 docs, depois igual a A | 0 | Nenhum | 2 harnesses, na 2ª etapa | Alta | Baixo; ordem mais conservadora |
| **E — quatro origens sem inferência** (manter `USUARIO/SISTEMA/PESQUISADOR` + um 4º valor de "operação automática", sem distinguir inferência) | **[INCONCLUSIVO]** frente ao artigo; mas evita introduzir um 5º valor sem uso real | Aproxima-se do estado atual do código (`ORIGEM_INFERENCIA` já não tem nenhum produtor real — Seção 10.4) | Já é essencialmente o estado atual | 0-1 (só renomear o 4º valor, se aplicável) | 0 | Nenhum | Possivelmente nenhum | Alta | Baixo |
| **F — cinco origens, separando processo de inferência** | Alinhada com a hipótese de leitura anterior (não recanonizada) do artigo | Exigiria acrescentar um 5º valor a `REFERENCE.md`/skill/`OrigemAcao` | Exigiria adicionar `PROCESSO_COMPUTACIONAL` mantendo `INFERENCIA_COMPUTACIONAL`/`ORIGEM_INFERENCIA` como categoria distinta e genuinamente sem uso hoje (Seção 10.4 mostra que não há inferência real produzindo eventos de domínio) | Maior mudança: enum ganha 5º valor, 4 usos reclassificados | ~7 | 0 | Nenhum | 2 harnesses | Média — reversível, mas maior superfície | Baixo tecnicamente, mas cria uma categoria (`INFERENCIA_COMPUTACIONAL`) sem nenhum produtor real hoje — risco de vocabulário morto |
| **G — compatibilidade temporária com nomes antigos** (manter `ORIGEM_SISTEMA` como alias de transição) | Não resolve a ambiguidade, apenas adia | Nenhuma skill examinada menciona necessidade de compatibilidade temporária para este enum específico, dado zero consumidores externos (Seção 12.1) | Introduz complexidade sem benefício, já que não há consumidores externos a proteger | 1-2 | 0 | Nenhum | 0 | Alta | Desnecessário dado o achado da Seção 12.1 |

### 12.3 Classificação de P1

**Bloqueada por conflito documental.** Razões, todas evidenciadas nas seções anteriores deste relatório:

1. A comparação com o artigo está suspensa pelo critério de parada da Seção 1.2 — não há como avaliar aderência ao artigo com confiança até a canonicidade ser resolvida.
2. Mesmo ignorando o artigo, os três documentos normativos internos ao repositório (`REFERENCE.md`, `MIGRATION_GUIDE.md`, skill de eventos) são ambíguos entre si sobre quando usar `SISTEMA` vs. `INFERENCIA_COMPUTACIONAL` (Seção 8) — um conflito documental interno, independente do artigo.
3. Existem quatro vocabulários de origem coexistindo no repositório (Seção 9.2), e P1, tal como formulada nas revisões anteriores, só examina um deles (`OrigemAcao`) — uma decisão sobre P1 isolada de `OrigemAvaliacao`/`ontologia_gerard.json`/`EventoLogGerard` corre o risco de "resolver" um vocabulário e deixar os outros três inconsistentes com ele.

**Nunca implementável automaticamente** — nenhuma das sete alternativas é executada nesta revisão.

---

## 13. Ações semânticas

Ao contrário do que as Revisões 2-3 assumiam, os seis termos **existem como vocabulário ativo no código de produção** — não é preciso recorrer ao artigo (suspenso pela Seção 1.2) para confirmá-los. Isso é uma correção de método em relação às revisões anteriores: a evidência correta estava no código, não no artigo.

### 13.1 Definição textual, no próprio código

**[EVIDÊNCIA NO CÓDIGO]** `LoggerInteracaoGerard.java:886-894`, método `descreverPropriedade(String tipo)`:

```java
if ("SELECIONAR".equals(tipo)) return "Escolha de uma opção entre itens disponíveis.";
if ("POSICIONAR".equals(tipo)) return "Definição de uma posição em espaço de uma ou mais dimensões.";
if ("ORIENTACAO".equals(tipo)) return "Escolha ou mudança de direção durante a interação espacial.";
if ("CAMINHO".equals(tipo)) return "Série contínua e rápida de operações de orientação e posicionamento.";
if ("QUANTIFICAR".equals(tipo)) return "Especificação ou alteração de um valor numérico.";
if ("TEXTO".equals(tipo)) return "Inserção, movimentação ou modificação de texto.";
```

**Não é um enum Java** — `EventoLogGerard.java:69` declara `private String tipoAcaoInteracao;` (tipo `String`), e o cabeçalho TSV traz a coluna `"tipo_acao_interacao"` como texto livre normalizado por `normalizarTipoAcao(String)` (`LoggerInteracaoGerard.java:874-884`).

### 13.2 Falsificação direta da suposição "sempre mouse"

**[EVIDÊNCIA NO CÓDIGO]** — em `Main.java:11993-11999` e `:12220-12223`, `"TEXTO"` e `"QUANTIFICAR"` são disparados em sequência a partir do retorno de `solicitarTextoEditavel(String valorAtual)` (`Main.java:12153`), uma **caixa de diálogo de entrada textual digitada pelo usuário**, chamada em `Main.java:11980`. Isto **falsifica diretamente** a suposição de que as seis ações são sempre gestos de mouse — a própria instrução desta auditoria antecipou corretamente esse risco.

Em contraste, `SELECIONAR`, `ORIENTACAO`, `CAMINHO` e `POSICIONAR`, nos pontos `Main.java:10740-10772` (dentro de `atualizarRastreamentoGranular`/`finalizarRastreamentoGranular`), derivam de coordenadas de mouse capturadas em `iniciarRastreamentoGranular` (`Main.java:10717`).

**Conclusão factual**: os seis termos não formam uma dimensão única "tarefa de mouse" — pelo menos dois deles (`TEXTO`, `QUANTIFICAR`) têm origem documentada por teclado em pontos reais do código, enquanto os outros quatro têm origem documentada por mouse. Isso é evidência concreta a favor de tratar "canal/dispositivo" (dimensão C, Seção 15) como algo que **varia dentro do mesmo termo de ação semântica**, não como algo implícito ou fixo por termo.

### 13.3 Sétimo termo fora do conjunto reconhecido — inconsistência real

**[EVIDÊNCIA NO CÓDIGO]** `Main.java:8903-8909` usa `"FEEDBACK"` como primeiro argumento de `registrarAcaoGranular(...)`:

```java
registrarAcaoGranular("FEEDBACK", "Informar limite semântico da coleção", ...);
```

Esse valor **não é tratado** por `descreverPropriedade`, `normalizarTipoAcao` nem `classificarNaturezaAcao` de `LoggerInteracaoGerard.java` — cai fora do conjunto fechado de seis termos que o próprio código define e reconhece nos outros três métodos. Isto é uma inconsistência real e localizada, não uma hipótese.

### 13.4 Cada ação, com o que a evidência permite determinar

| Ação | Definição (código) | Origem observada (mouse/teclado) | Exemplo de ponto de disparo |
|---|---|---|---|
| `SELECIONAR` | "Escolha de uma opção entre itens disponíveis." | Mouse (clique) | `Main.java:2006, 10279, 10309` |
| `POSICIONAR` | "Definição de uma posição em espaço de uma ou mais dimensões." | Mouse (arraste, coordenadas x/y) | `Main.java:10764` |
| `ORIENTACAO` | "Escolha ou mudança de direção durante a interação espacial." | Mouse | `Main.java:10740` |
| `QUANTIFICAR` | "Especificação ou alteração de um valor numérico." | **Teclado** (via `solicitarTextoEditavel`, quando o texto casa `[+-]?[0-9]+([,.][0-9]+)?`) | `Main.java:11996-11997`, `12220-12222` |
| `CAMINHO` | "Série contínua e rápida de operações de orientação e posicionamento." | Mouse (arraste contínuo) | `Main.java:10758` |
| `TEXTO` | "Inserção, movimentação ou modificação de texto." | **Teclado** (digitação) ou mouse (movimentação de texto) — ambos observados | `Main.java:10769` (mouse, mover texto), `11993` (teclado, editar texto) |

Não crio enum de gesto, modalidade ou canal nesta revisão — a tabela acima só consolida o que já existe como texto livre no código, sem propor uma nova estrutura Java.

---

## 14. Fronteiras das ações

**[PROPOSTA — nenhuma regra existente no código foi localizada que defina explicitamente onde uma ação semântica começa e termina]**. Nenhuma classe do pacote piloto ou de produção examinada nesta revisão contém uma regra de fronteira de ação (por exemplo, "uma proposta seguida de correção conta como 1 ou 2 ações").

Evidência indireta relevante: **[EVIDÊNCIA NO CÓDIGO]** `EventoPapelQuantitativo`/`PapelQuantitativo.posicionar` gera **um evento por chamada de método**, sem qualquer agrupamento — cada `posicionar(...)` bem-sucedido ou rejeitado produz exatamente um `EventoPapelQuantitativo`, cada um com seu próprio `idAcao` gerado via UUID novo (Seção 17). Não há, no código atual, nenhum mecanismo que agrupe uma "proposta" e uma "correção subsequente" como partes da mesma ação — cada posicionamento é uma ação nova do ponto de vista do código, com um novo UUID.

Isso significa, factualmente: **hoje, no código, a fronteira de uma "ação" é implicitamente uma única chamada ao método `posicionar(...)`** — não uma unidade semântica mais ampla. Se essa granularidade é a desejada (fronteira = chamada de método) ou se uma noção mais ampla de "ação" (ex.: proposta + suas correções subsequentes até aceitação) é necessária, é uma **[DECISÃO HUMANA PENDENTE]**.

Perguntas da instrução, respondidas com o que a evidência permite:

- **Proposta inicial e correção são ações distintas?** No código atual, sim — são duas chamadas distintas a `posicionar`, cada uma com evento e `idAcao` próprios. Se isso é semanticamente desejável é uma decisão pendente.
- **Rejeição é evento da proposta ou ação do sistema?** No código atual, é o mesmo evento da tentativa de posicionamento — `TipoEventoPapel.VALOR_REJEITADO` é produzido pela mesma chamada a `posicionar` que tentou o valor, não uma ação separada do sistema.
- **Feedback é evento correlacionado à ação ou nova ação?** **[INCONCLUSIVO]** — o pacote piloto não modela feedback como evento (não há `EventoFeedback` no código atual — ver Seção 24).
- **Cálculo e aplicação são uma ou duas operações?** **[EVIDÊNCIA NO CÓDIGO]** — duas, explicitamente separadas: `RelacaoEstruturalTransformacao.calcularValorAusente` (não gera evento, é puro) e `.aplicar` (gera evento, via `PapelQuantitativo.posicionar` internamente) — ver `RelacaoEstruturalTransformacao.java:91-106`.
- **Pergunta e resposta compartilham correlação, mas não identidade?** **[INCONCLUSIVO]** — não há classe de pergunta/resposta no pacote piloto (ver Seção 26).
- **Uma ação semântica pode gerar vários eventos?** No modelo de código atual, não — é 1:1 (uma chamada a `posicionar` → um evento). Ver Seção 17 para a análise completa de `id_acao`.

**Sem uma definição de fronteira de ação assumida por decisão humana, `action_id` permanece insuficientemente especificado** — consistente com o próprio texto da instrução.

---

## 15. Gestos, técnicas e canais

Nenhuma fonte examinada nesta revisão (artigo — suspenso; `REFERENCE.md`; as 4 skills; `EstiloInteracao.java`, não relido nesta revisão mas confirmado em revisões anteriores como tratando de estilo visual, não gesto de entrada) define uma enumeração fechada de técnica/gesto (arraste, clique, duplo clique, digitação) ou de canal/dispositivo (mouse, teclado) como um vocabulário Java próprio. Não crio esse vocabulário nesta revisão.

O que a Seção 13.2 estabelece, com evidência de código: o **canal de entrada não é constante por termo de ação semântica** — `TEXTO` e `QUANTIFICAR` têm pontos de disparo tanto por teclado (`solicitarTextoEditavel`) quanto, no caso de `TEXTO`, por mouse (mover um elemento de texto já existente). Isso significa que qualquer futura dimensão C (canal/dispositivo) precisaria ser **um campo por evento**, não um atributo fixo derivável do termo de ação semântica (dimensão A) — uma conclusão factual nova desta revisão, não presente nas anteriores.

---

## 16. Tentativa, ação, evento e registro

### 16.1 "Tentativa" não existe como classe em nenhum lugar do repositório

**[EVIDÊNCIA NO CÓDIGO, negativa]** — busca por `class \w*Tentativa\w*` não retorna nenhuma classe. O conceito existe apenas como campo espalhado:

| Local | Campo | Tipo | Geração |
|---|---|---|---|
| `ContextoAcao.java:22` (piloto) | `idTentativa` | `String`, opcional | Não gerado pelo pacote piloto — recebido de fora |
| `LoggerInteracaoGerard.java:57-58` (produção) | `tentativaAtualId`, `tentativaAtualNumeroSituacao` | `String`/`int` | `novoProblema(...)`, linha 453: `"T" + String.format("%04d", contadorTentativas) + "_" + timestamp` |
| `EventoLogGerard.java:57` (produção) | `tentativa` | `String` (coluna de log) | Copiado de `tentativaAtualId` no momento do registro |
| `ArtefatoExplicativo.java:9` (produção) | `tentativaId` | `String final` | Recebido no construtor |
| `TelaArtefatoExplicativo.java:811` (produção) | `ArtefatoContexto.tentativaId/tentativaNumero` | classe interna, campos públicos | Montado em `Main.java:2065-2079` a partir de `loggerInteracaoGerard.getTentativaAtualId()` |

### 16.2 Entidades de "Ação" — múltiplas, não relacionadas por herança

| Classe | Pacote | Campos principais | Identificador próprio | Relação |
|---|---|---|---|---|
| `OrigemAcao` (enum) | piloto | `ORIGEM_USUARIO/SISTEMA/INFERENCIA/PESQUISADOR` | N/A (é enum de valor, não entidade) | Referenciado por `EventoPapelQuantitativo` |
| `AcaoAtividade` (enum) | produção (`gerard.aplicacao`) | `INICIALIZAR, SORTEAR, SELECIONAR_CATEGORIA, RESTAURAR, TROCAR_IDIOMA, TROCAR_ESTILO_INTERACAO, ABRIR_OU_FECHAR_CURADORIA, ATUALIZAR_TEXTO, TROCAR_REPRESENTACAO` | N/A | Ciclo de vida da atividade — **não é ação semântica de Shneiderman**, é um conceito diferente com nome parecido |
| `AcaoUsuarioAudit` | produção (`gerard.pesquisador.auditoria`) | `tipo, elemento, valor, papelOrigem, papelDestino, artefatoOrigem, artefatoDestino, coordenadaOrigemX/Y, coordenadaDestinoX/Y, estadoInterfaceAntes/Depois, resultadoEsperado, resultadoObservado` | Nenhum próprio — contida por `AgentAuditEvent.acaoUsuario` | Comentário do código: "papéis semânticos são a referência principal; coordenadas são só informação complementar" |
| `AcaoUsuarioProtocolo` ("Componente B") | produção (`gerard.pesquisador.analiseunidade`) | Reempacota um `AgentAuditEvent` canônico já decidido | `protocolInstanceId`, `analysisUnitId`, `episodeId`, `sessionId`, `actionId`, `gestureId` | Comentário: "esta classe não decide nada, só reempacota" |
| `AcaoComputador` ("Componente A") | produção | `actionId, actionType, content, messageTemplateId, feedbackPresented, technicalEventIds` (lista) | `actionId` (esquema próprio, `"A-" + analysisUnitId`) | Referencia `EventoTecnico` por lista de IDs |

### 16.3 Entidades de "Evento" — comparação de arquitetura

| Classe | Pacote | Hierarquia | Versão de esquema |
|---|---|---|---|
| `EventoDominio` (interface) + `EventoPapelQuantitativo` (única implementação) | piloto | Classe única achatada, variação só via enum `TipoEventoPapel` (`VALOR_POSICIONADO`, `VALOR_REJEITADO`) | Nenhuma |
| `EventoTecnico` | produção | Classe única; explicitamente nunca conta como unidade de análise/instância de protocolo (`counts_as_analysis_unit`/`counts_as_protocol_instance` sempre `false`) | Nenhuma própria |
| `AgentAuditEvent` | produção | **Composta de sub-objetos tipados**: `identificacao, classificacao, acaoUsuario, monitor, zdp, modelador`, mais snapshots antes/depois | **Sim — `schema_version`, hoje "2.0.0"**, lido de `IdentificacaoEvento.getSchemaVersion()`. Comentário do código documenta um bug real corrigido: o campo ficava hardcoded desde a rodada 2 e não acompanhava a versão real — corrigido na rodada 4 (2026-07-31) |
| `EventoLogGerard` | produção | Classe única, TSV | **Compatibilidade retroativa por comprimento de linha**: `novoFormato = campos.length >= 23` decide um deslocamento de leitura — mecanismo de versionamento implícito, não um campo explícito |

### 16.4 Registro de atividade — sem classe única, mas com hierarquia ABCD

`UnidadeAnalise` (`analysisUnitId` próprio) contém `AcaoComputador a` (componente A), `AcaoUsuarioProtocolo b` (componente B), `ComponenteC c` (perguntas: `buttonAvailable, buttonActivated, screenOpened, status, questions, openedAt, closedAt`), `ComponenteD d` (respostas: `status, responses, completedAt`). `ComponenteC` contém `List<PerguntaExplicativa>`; `ComponenteD` contém `List<RespostaExplicativa>`. Essa é a estrutura de produção mais próxima de "tentativa, ação, evento e registro" unificados — mas não usa esses quatro nomes, e não tem nenhuma relação de código com `ContextoAcao`/`EventoPapelQuantitativo` do pacote piloto.

Matriz de cardinalidades observadas (não presumidas):

| Origem | Relação | Destino | Cardinalidade | Fonte | Status |
|---|---|---|---|---|---|
| `ContextoAcao` | carrega | `idTentativa` | 1:1 (um contexto, uma tentativa) | `ContextoAcao.java:22` | **[EVIDÊNCIA NO CÓDIGO]** |
| `PapelQuantitativo.posicionar` | gera | `EventoPapelQuantitativo` | 1:1, sempre (Seção 17.2) | `PapelQuantitativo.java:147,157` | **[EVIDÊNCIA NO CÓDIGO]** |
| `UnidadeAnalise` | contém | `AcaoComputador` (A) | 1:1 | `UnidadeAnalise.java:17` | **[EVIDÊNCIA NO CÓDIGO]** |
| `UnidadeAnalise` | contém | `AcaoUsuarioProtocolo` (B) | 1:1 | `UnidadeAnalise.java:18` | **[EVIDÊNCIA NO CÓDIGO]** |
| `UnidadeAnalise` | contém | `ComponenteC` (perguntas) | 1:1 (mutável) | `UnidadeAnalise.java:21` | **[EVIDÊNCIA NO CÓDIGO]** |
| `ComponenteC` | contém | `List<PerguntaExplicativa>` | 1:N | `ComponenteC.java` | **[EVIDÊNCIA NO CÓDIGO]** |
| `ComponenteD` | contém | `List<RespostaExplicativa>` | 1:N | `ComponenteD.java` | **[EVIDÊNCIA NO CÓDIGO]** |
| `AcaoComputador` | referencia (por ID) | `EventoTecnico` | 1:N (`technicalEventIds`, lista) | `AcaoComputador.java` | **[EVIDÊNCIA NO CÓDIGO]** |
| `AgentAuditEvent` | agrega | `identificacao + classificacao + acaoUsuario + monitor + zdp + modelador` | 1:1 cada (composição, não lista) | `AgentAuditEvent.java:31-43` | **[EVIDÊNCIA NO CÓDIGO]** |

---

## 17. Auditoria de `id_acao`

Esta é uma das descobertas mais concretas desta revisão.

### 17.1 Declaração, geração e uso — família do pacote piloto

**[EVIDÊNCIA NO CÓDIGO]**:

- Declaração: `EventoPapelQuantitativo.java:23` — `private final String idAcao;`
- Geração: `EventoPapelQuantitativo.java:38`, dentro do construtor — `this.idAcao = UUID.randomUUID().toString();` — **gerado incondicionalmente a cada instância de evento**, sempre um UUID v4 novo. Não é contador, não é string fixa, não é copiado de outro objeto — o construtor **não recebe** `idAcao` como parâmetro externo.
- Getter: `:56` — `public String getIdAcao() { return idAcao; }`. **Nenhuma chamada a este getter foi localizada em todo o repositório além da própria definição.**
- Serialização: `:67`, dentro de `paraMapa()` — `mapa.put("id_acao", idAcao);`
- Único consumidor: `TestePilotoPapelQuantitativo.java:91-100`, que testa apenas que `id_acao` está presente e não vazio — não testa unicidade, não testa correlação entre eventos.

### 17.2 Determinação exigida pela instrução: o que `id_acao` representa hoje

**Determinação, não deixada como "não verificada"**: `id_acao`, tal como implementado hoje em `EventoPapelQuantitativo`, representa um **identificador único por evento (event_id funcional)**, apesar do nome sugerir "ação". Evidência: cada chamada a `PapelQuantitativo.posicionar(...)` (Seção 14) gera exatamente um `EventoPapelQuantitativo` com um `idAcao` novo — não há nenhum mecanismo de propagação de um `idAcao` de uma tentativa de posicionamento para uma correção subsequente do mesmo papel. Duas chamadas consecutivas a `posicionar` no mesmo papel (ex.: proposta rejeitada, seguida de nova proposta) produzem dois `idAcao` totalmente diferentes, sem nenhuma relação registrada entre eles.

**Isto é uma inconsistência arquitetural, registrada como tal**: o nome do campo (`id_acao`/`idAcao`) e sua documentação (comentário de classe, `EventoPapelQuantitativo.java:14-18`: "carrega o suficiente para religar o evento à tentativa de resolução específica em que ocorreu (id_acao, contexto)") sugerem um identificador de **ação** (que poderia agrupar múltiplos eventos de uma mesma ocorrência semântica), mas o comportamento real do código é de um identificador de **evento individual**, sem função de correlação alguma além de unicidade.

### 17.3 Segunda família, completamente desconectada: `actionId` de produção

**[EVIDÊNCIA NO CÓDIGO]** — em `gerard.pesquisador.auditoria`, existe um campo `actionId`/`action_id` **totalmente diferente e não relacionado**:

- Geração: `AgentAuditService.java:190,267,283` — formato `"ACAO-" + String.format("%04d", contadorGestos)` (mesmo contador de `gestureId`, só prefixo diferente).
- O próprio schema documenta a relação: `dados/schema_agentes_execucao_gerard.json:67` — *"Acompanha gesture_id (mesmo ciclo de vida) — mantido como campo separado por compatibilidade semântica com o pacote de correção original (gesto vs. ação)."* Ou seja, **por definição documentada, `action_id` de produção tem sempre o mesmo ciclo de vida que `gesture_id`** — é funcionalmente um alias, não uma entidade com identidade própria distinta do gesto.
- Propagado por `IdentificacaoEvento`, `AcaoUsuarioProtocolo`, serializado em `AgentAuditEvent` (5 pontos: linhas 105, 213, 267, 323, 402) e em `AnalysisUnitAuditService.java:533`.
- Um terceiro uso do nome `actionId`, ainda mais distinto: `AcaoComputador.java`, onde `actionId = "A-" + analysisUnitId` — identificador do componente "A" (ação do computador) no protocolo ABCD, gerado por um esquema totalmente diferente (não ligado a `contadorGestos`).
- Um quarto ponto, `RobotGestureTrace.definirIdentidade(gestureId, actionId)`, é chamado em `TesteMonkeyGuiadoPorCasosReais.java:624-625,781-782` **com o mesmo valor duplicado nos dois parâmetros** (`getUltimoGestureIdGravado()` passado duas vezes) — não com um `actionId` gerado de fato pelo `AgentAuditService`.

### 17.4 Conclusão factual

**Nenhuma ponte de código conecta `EventoPapelQuantitativo.idAcao` (pacote piloto) a qualquer variante de `actionId` de produção.** São dois campos homônimos (em português/inglês), em dois pacotes que não se importam mutuamente, com semânticas de geração diferentes: um UUID por evento (piloto) vs. um contador compartilhado com `gestureId` (produção). Isso é registrado como **inconsistência arquitetural de nomenclatura entre pacotes**, não como um erro dentro de nenhum dos dois pacotes isoladamente — cada um é internamente consistente consigo mesmo.

---

## 18. Especificação de `attempt_id`

**[EVIDÊNCIA NO CÓDIGO]**: `ContextoAcao.idTentativa` (`ContextoAcao.java:22,30,37`) — campo `String`, opcional (pode ser `null`), sem geração própria dentro do pacote piloto (é fornecido de fora, pelo chamador — ver `TestePilotoPapelQuantitativo.java:45-46`, onde é uma string literal `"tentativa-1"` fornecida manualmente pelo harness).

| Propriedade | Valor observado |
|---|---|
| Unidade identificada | Uma tentativa de resolução (não gerada pelo pacote piloto — apenas recebida) |
| Responsável pela geração | **[INCONCLUSIVO dentro do pacote piloto]** — nenhuma fábrica de `idTentativa` existe em `gerard.dominio.campoaditivo`; é responsabilidade de uma camada externa não identificada nesta revisão |
| Escopo | Um `ContextoAcao` inteiro (mesma tentativa para todos os campos do mesmo contexto) |
| Duração | Não modelada — não há início/fim explícitos no pacote piloto |
| Reutilização | Sim, por design — múltiplas chamadas a `posicionar` podem compartilhar o mesmo `ContextoAcao`/`idTentativa` (visto nos harnesses, onde o mesmo objeto `contexto` é reutilizado em várias chamadas) |
| Relação com sessão/usuário/situação | `ContextoAcao` também carrega `idSessao`, `idUsuarioLocal`, `idSituacaoProblema`, `idRepresentacao` — mas nenhuma validação de consistência entre eles é feita pelo pacote piloto (todos podem ser `null` independentemente) |

Fora do pacote piloto, `id_tentativa` como conceito de produção não foi auditado nesta revisão em profundidade — ficou fora do escopo dos agentes de pesquisa desta rodada (não solicitado explicitamente). **[INCONCLUSIVO]** sobre se há um `attempt_id` de produção equivalente.

---

## 19. Especificação de `action_id`

Ver íntegra da auditoria fatual na Seção 17. Resumo específico para esta seção:

| Propriedade | Pacote piloto (`idAcao`) | Produção (`actionId`) |
|---|---|---|
| O que identifica hoje | Um evento individual (apesar do nome) | Um gesto (compartilha ciclo de vida com `gestureId`, por definição documentada no schema) |
| Início e fim | Não modelado — nasce e morre com o evento | Reservado em `reservarProximoGesto()`, consumido em `iniciarAcao()` |
| Responsável pela geração | Construtor de `EventoPapelQuantitativo` (UUID) | `AgentAuditService` (contador `"ACAO-%04d"`) |
| Cardinalidade com eventos | 1:1, sempre (Seção 17.2) | Não avaliado nesta revisão em detalhe fora do que já consta na Seção 17.3 |
| Comportamento em operações automáticas | Mesmo mecanismo — UUID novo por evento, independente da origem | Não avaliado nesta revisão |
| Possibilidade de ausência | Nunca ausente — sempre gerado no construtor | `action_id` é `["string","null"]` no schema (`dados/schema_agentes_execucao_gerard.json:67`) — pode ser nulo |

**[DECISÃO HUMANA PENDENTE]**: se uma futura especificação de `action_id` para o pacote piloto deveria (a) inspirar-se no `actionId` de produção (ligado a gesto), (b) permanecer como está (funcionalmente um event_id), ou (c) adotar uma terceira semântica.

---

## 20. Especificação de `event_id`

**[EVIDÊNCIA NO CÓDIGO]**: o pacote piloto não tem um campo literalmente chamado `event_id` — o que existe, e cumpre essa função na prática, é `id_acao` (Seção 17.2). Não há também, no pacote piloto, nenhum outro identificador de evento (não há dois campos concorrentes aqui, diferente de `action_id`).

| Propriedade | Valor observado |
|---|---|
| Unidade identificada | Um único `EventoPapelQuantitativo` |
| Responsável pela geração | O próprio construtor do evento, via `UUID.randomUUID()` |
| Unicidade | Garantida pela geração UUID v4 — probabilisticamente única |
| Momento de criação | No instante de construção do objeto evento (antes da publicação) |
| Persistência | Nenhuma no pacote piloto — publicado via `PublicadorEventoDominio`, que não tem implementação de produção (Seção 10, e confirmado pelo agente: "nenhuma implementação de produção encontrada") |
| Relação com `action_id` | Hoje são o mesmo campo (`idAcao` cumpre as duas funções) — ver Seção 17.2 |

**[PROPOSTA, não implementada]**: caso se decida separar `action_id` de `event_id` no pacote piloto, seria necessário introduzir um novo campo — o `idAcao` atual não pode desempenhar as duas funções simultaneamente sem ambiguidade, exatamente como a instrução desta auditoria antecipa.

---

## 21. Auditoria de P2-A

P2-A trata da correlação entre tentativa, ação e evento.

**Depende da definição da fronteira de ação (Seção 14) — que não está definida.** Sem essa definição, a cardinalidade ação:evento não pode ser fixada com segurança: hoje, no código, é 1:1 por construção (Seção 17.2), mas isso é um artefato da implementação atual, não uma decisão de modelo deliberada e documentada.

P2-A **não depende** de uma taxonomia completa de gesto/dispositivo (Seções 13-15) — os 12 pontos de especificação levantados nas revisões anteriores (event_id/action_id/attempt_id, cardinalidade, schema_version) permanecem válidos como pauta de especificação, mas **não podem ser considerados prontos** enquanto "mesma ação" for indefinida, conforme a própria instrução desta revisão antecipa.

**Classificação de P2-A: insuficientemente especificada.** Motivo específico e novo nesta revisão (não presente nas anteriores): a Seção 17 mostra que o campo que deveria ser `action_id` já existe no código com uma semântica DIFERENTE (event_id de fato) — uma futura especificação de P2-A não parte de um campo vazio, mas precisa decidir explicitamente se `idAcao` é renomeado/redefinido, ou se um novo campo é introduzido ao lado dele. Essa é uma decisão adicional que as revisões anteriores não tinham identificado.

---

## 22. Auditoria de P2-B

P2-B trata da classificação da interação (técnica, gesto, canal, dispositivo, modalidade).

**Classificação: suspensa.** Nenhuma fonte normativa examinada nesta revisão (Seções 13 e 15) define esse vocabulário. Permanece bloqueada nos mesmos termos da Revisão 3, sem mudança de evidência nesta revisão.

---

## 23. Estados locais e estados do diagrama

**[EVIDÊNCIA NO CÓDIGO]**: o objeto que representa o estado local de um papel é o próprio `PapelQuantitativo.valorAtual` (campo privado, `PapelQuantitativo.java:48`), exposto via `valorAtual()` (linha 126) e serializado em `paraMapa()` (linhas 177-188). Um evento captura `estadoAnterior`/`estadoPosterior` como **strings formatadas** (`descreverEstadoAtual()`, linhas 163-165) — não um snapshot estruturado.

Respostas às perguntas da instrução:

- **Qual objeto representa o diagrama (como um todo)?** **[EVIDÊNCIA NO CÓDIGO, negativa]** — nenhum objeto no pacote piloto representa "o diagrama" como agregado; existem apenas papéis individuais (`PapelQuantitativo`) e relações de escopo fechado (`RelacaoEstruturalComposicao`/`Transformacao`) que operam sobre 3 papéis nomeados por vez, sem uma classe "Diagrama" ou "Representação" que os agregue todos.
- **Existe método de snapshot?** Não — `paraMapa()` serializa **um papel por vez**, não o diagrama inteiro.
- **Quais papéis são incluídos (em um snapshot)?** N/A — não há snapshot.
- **Relações são incluídas?** N/A.
- **Posições e orientações são incluídas?** Não — `DescritorRepresentacaoPapel` (não lido integralmente nesta revisão) carrega forma/símbolo/rótulo abstrato, não coordenadas.
- **A representação é identificada?** `ContextoAcao.idRepresentacao` existe como campo opcional, mas não há objeto de representação que o pacote piloto construa ou valide.
- **O estado é imutável?** Os eventos (`EventoPapelQuantitativo`) são imutáveis (campos `final`, sem setters) — o estado do papel (`PapelQuantitativo.valorAtual`) é mutável.
- **O estado pode ser reconstruído apenas pelos eventos?** **[INCONCLUSIVO]** — os eventos carregam `estadoAnterior`/`estadoPosterior` como strings formatadas de um único papel, não haveria como reconstruir um "diagrama completo" a partir apenas da sequência de eventos, porque nenhum evento individual descreve o estado dos outros papéis relacionados no mesmo instante.
- **Estado anterior/posterior capturados antes/depois da mesma operação?** Sim — `PapelQuantitativo.posicionar` (linhas 143-144, 147-149) captura `estadoAnterior` antes de mutar `valorAtual` e `estadoPosterior` depois, na mesma chamada.

**Lacuna identificada, sem redigir como fato do artigo**: revisões anteriores (não recanonizadas) atribuíam à QP a menção de "estados sucessivos dos diagramas" — se essa formulação se confirmar na fonte canônica quando determinada, há uma lacuna clara entre o que o piloto implementa (dois valores string por evento, de um único papel) e uma noção de "estado sucessivo do diagrama" como um todo (todos os papéis e relações em um instante). Registro isso como **[INFERÊNCIA]**, condicionada à recanonização da fonte.

---

## 24. Arquiteturas possíveis de eventos

**[PROPOSTA — nenhuma das opções abaixo está implementada no pacote piloto; avaliação comparativa]**. Diferente do que as revisões anteriores assumiam, **as quatro opções já têm exemplares reais em produção**, não são hipóteses abstratas — o agente de pesquisa confirmou instâncias concretas de cada padrão coexistindo no repositório.

| Opção | Descrição | Exemplar real no repositório | Observação |
|---|---|---|---|
| A — evento único com muitos campos opcionais | Um tipo, muitos campos, alguns condicionais | `EventoPapelQuantitativo` (piloto) — `if (diagnostico != null)` explícito (linhas 80-87); `EventoTecnico` (produção) — classe única, sem hierarquia | Padrão do pacote piloto |
| B — envelope comum + payloads tipados | Envelope (id, tipo, timestamp) + payload específico | **`AgentAuditEvent` (produção)** — composto por sub-objetos tipados: `identificacao (IdentificacaoEvento), classificacao (ClassificacaoEvento), acaoUsuario (AcaoUsuarioAudit), monitor (MonitorAuditData), zdp (ZdpAuditData), modelador (ModeladorAuditData)`, mais snapshots antes/depois | **Já implementado em produção**, com `schema_version` explícito ("2.0.0") |
| C — hierarquia de classes de evento | Superclasse abstrata + subclasses por tipo | Não localizada em nenhum pacote — nem piloto nem produção usam herança de classe para tipos de evento; `EventoDominio` (piloto) é interface, `AgentAuditEvent` (produção) é composição, não herança | Não implementada em nenhum lugar do repositório |
| D — evento genérico com tipo + mapa de atributos | Campos dinâmicos em mapa desde a origem | `EventoLogGerard` (produção) — TSV com 30 colunas fixas, mas leitura via `deTsv` usa comprimento de linha (`campos.length >= 23`) para decidir formato — mais próximo de um formato semiestruturado que evolui por acréscimo de colunas no fim, do que um mapa livre desde a origem | Parcial |

**Achado relevante para versionamento (ver também Seção 31)**: `AgentAuditEvent.java` documenta, em comentário de código, um **bug real de versionamento já ocorrido e corrigido**: o campo `schema_version` "ficava hardcoded em '2.0.0' desde a rodada 2, nunca acompanhando a versão real passada por `AgentAuditService`" — corrigido na rodada 4 (2026-07-31) para ler da mesma `identificacao` que já guarda a versão correta. Isso é evidência concreta de que campos de versão, mesmo quando existem, podem dessincronizar do valor real se não forem lidos de uma única fonte de verdade — relevante como risco a mitigar caso o pacote piloto venha a ganhar um campo de versão (Seção 31).

Nenhuma escolha é feita nesta revisão para o pacote piloto. A avaliação de coesão/campos nulos/extensibilidade/Java 8/serialização/consulta/reconstrução/impacto nos harnesses/compatibilidade fica registrada como pauta de decisão futura.

---

## 25. Feedback disponível, selecionado e exibido

No pacote piloto, `DiagnosticoErroPapel` carrega `chaveMensagem`, `chaveFeedbackPedagogico`, `chaveSugestaoCorrecao` — todas **chaves i18n**, não texto, e não há, no pacote piloto, nenhum código que efetivamente resolva essas chaves para texto nem que as apresente em interface. O pacote piloto para nesse ponto: produz a chave, não o feedback exibido.

O fluxo real de produção, agora mapeado por completo pelo agente de pesquisa, tem quatro etapas distintas e evidenciadas:

### 25.1 Seleção do feedback

**[EVIDÊNCIA NO CÓDIGO]** `ScaffoldingQuestionamento.avaliarPosicionamento` (linhas 17-36) decide se há incompatibilidade e monta a mensagem via `criarPerguntaConfirmacao` (linhas 38-53), que resolve uma chave i18n (`ui.question.semanticMismatch`) para texto com `ServicoLocalizacao`. `ResultadoQuestionamento` (fábricas `naoAplicavel()`, `correto(...)`, `incorreto(...)`) carrega o texto já resolvido no campo `mensagem`.

### 25.2 Solicitação de exibição

**[EVIDÊNCIA NO CÓDIGO]** `Main.java:11028-11046`, `processarQuestionamentoPosicionamento(item)`: se o resultado é aplicável e incorreto, chama `registrarQuestionamentoPersistente(item, resultado)` (linhas 11077-11086), que seta `itemQuestionadoPersistente`, `textoQuestionamentoPersistente`, `mostrarQuestionamentoPersistente = true`, e também `scaffoldingFeedbackMultissensorialErro.sinalizarErro(item, callback)`. Existe uma segunda via, para limite de quantidade (não incompatibilidade semântica): `Main.java:8885-8909`, `registrarLimiteQuantidadeAtingido`, que também dispara `registrarAcaoGranular("FEEDBACK", ...)` — o termo `"FEEDBACK"` fora do conjunto reconhecido de seis ações (Seção 13.3).

### 25.3 Renderização efetiva

**[EVIDÊNCIA NO CÓDIGO]** Dois canais simultâneos, confirmados por leitura direta:
- **Multissensorial** — `ScaffoldingFeedbackMultissensorialErro.sinalizarErro` (linhas 34-99): inicia um `javax.swing.Timer` que desloca o item (tremor curto) e chama `emitirSomSutil()` (linhas 161-167), que usa `Toolkit.getDefaultToolkit().beep()`.
- **Textual** — `Main.java:6500-6558`, `desenharAnotacaoMouseOver(Graphics2D g2)`, chamado a partir de `paintComponent` (`Main.java:4952`), decide entre `textoQuestionamentoPersistente`/`textoLimiteQuantidadeQuestionado`/`textoAnotacaoMouseOver` e desenha a mensagem com `Font("Arial", Font.PLAIN, 13)`.

Isso confirma, com evidência de código concreta e não apenas de chave, que o feedback **é efetivamente renderizado** — dois mecanismos independentes e simultâneos (visual/sonoro e textual), ambos disparados a partir do mesmo ponto de decisão (`registrarQuestionamentoPersistente`).

### 25.4 Registro em log

**[EVIDÊNCIA NO CÓDIGO]** `registrarAcaoGranular(...)` (`Main.java:10711-10715`) encaminha para `loggerInteracaoGerard.registrarAcaoGranularUsuario(...)` (`LoggerInteracaoGerard.java:534-548`), que grava com o comentário fixo "Registro granular de técnica de interação; não corresponde, isoladamente, a acerto ou erro matemático." — mas **não há confirmação, no log, de que a mensagem foi de fato desenhada na tela** (`desenharAnotacaoMouseOver` é chamado a cada repaint, sem callback de volta para o log confirmando que o usuário viu). Isto é uma lacuna real: **existe evidência de que a renderização ocorre no código de desenho, mas não de um evento "FEEDBACK_EXIBIDO" com confirmação** — consistente com a advertência da instrução de que "a existência de uma chave de feedback não prova que algo foi exibido": aqui há mais que uma chave (há renderização real), mas ainda não há confirmação registrada de exibição.

---

## 26. Perguntas formuladas

O agente de pesquisa localizou a entidade completa. **[EVIDÊNCIA NO CÓDIGO]**:

- `PerguntaExplicativa.java:9-26` — campos `questionId, questionType, content, presentedToUser`. Comentário: "previamente cadastrada/incorporada ao Gérard... apresentada ou não conforme o usuário abre a tela." — distingue explicitamente **pergunta cadastrada** (existe no sistema) de **pergunta apresentada** (`presentedToUser`, campo booleano próprio).
- `ComponenteC.java:8-41` ("perguntas explicativas disponibilizadas pelo sistema") — campos `buttonAvailable, buttonActivated, screenOpened, status (StatusExplicacao), questions (List<PerguntaExplicativa>), openedAt, closedAt`. Isso modela quatro estados distintos e sequenciais: botão disponível → botão ativado → tela aberta → perguntas efetivamente listadas.
- Abertura real: `Main.java:2049-2090`, `abrirArtefatoExplicativo()`, monta `TelaArtefatoExplicativo.ArtefatoContexto` com o ID da tentativa atual e chama `TelaArtefatoExplicativo.mostrar(...)`.
- Registro de abertura: `TelaArtefatoExplicativo.java:98-100` — `analysisUnitAuditService.registrarTelaAberta(usuarioId, categoria, itens)`.

**Determinação, não deixada como inconclusiva**: existem três estatutos distintos e evidenciados por campo próprio — pergunta **cadastrada** (`PerguntaExplicativa` existe na lista de `ComponenteC.questions`), pergunta **disponibilizada** (`buttonAvailable`/`screenOpened`), e pergunta **apresentada** (`presentedToUser`, campo por pergunta individual). Não há, nesta revisão, confirmação de que os três estados sejam sempre distintos na prática (ex.: se `screenOpened=true` implica `presentedToUser=true` para todas as perguntas da tela) — isso é **[INCONCLUSIVO]** apenas quanto à relação entre os três campos, não quanto à existência deles.

---

## 27. Respostas fornecidas

**[EVIDÊNCIA NO CÓDIGO]**:

- `RespostaExplicativa.java:4-24` — campos `questionId, content, saved, editedLater, timestamp`. Comentário: "uma resposta do usuário (D) a uma pergunta explicativa (C) real." — `questionId` liga explicitamente a resposta à pergunta (Seção 26), sem serem a mesma identidade (a instrução pediu exatamente essa distinção: "pergunta e resposta compartilham correlação, mas não identidade" — confirmado: campos e classes diferentes, ligados por ID, não fundidos).
- `ComponenteD.java:8-26` — campos `status (StatusExplicacao), responses (List<RespostaExplicativa>), completedAt`.
- Captura em tela: `TelaArtefatoExplicativo.LinhaResposta` (classe interna, linhas 705-780) — rádios `facil/intermediaria/dificil` (linhas 713-715) e `JTextArea explicacao` (linha 710); método `resposta()` (linhas 774-779) monta um `RespostaElementoModelagem` (classe separada, campos `elemento, papelSemantico, explicacao, dificuldade`, sem identificador próprio).
- Persistência ao salvar: `TelaArtefatoExplicativo.java:321-345` (listener do botão salvar) → `salvarNoLog(contexto)` (linhas 389-418) → para cada resposta, `logger.registrarExplicacaoMatematica(...)` (grava no log de interação, com `tipoAcaoInteracao = "TEXTO"` — reaproveitando um dos seis termos da Seção 13) **e**, separadamente, `agenteModelador.registrarExplicacaoNoUltimoDiagnostico(...)` (aciona o classificador `AnalisadorNivelConceitual`, Seção 10.1).
- Fechamento/estado: `registrarFechamentoSeNecessario` (linhas 420-440) chama `analysisUnitAuditService.registrarTelaFechada(...)`, também acionado em `windowClosing` e no botão cancelar (com `salvouAgora=false` nesses dois casos).

**Lacuna real identificada, não hipotética**: `TelaArtefatoExplicativo.salvarNoLog` **não chama** `RepositorioArtefatosExplicativos.salvar(...)` — o agente de pesquisa buscou especificamente essa ligação e não encontrou nenhum ponto de código que a faça. `RepositorioArtefatosExplicativos` (`~/Gerard/analises/artefatos_explicativos.tsv`, colunas incluindo `invariante_operatorio`) existe como classe de persistência dedicada, mas **fica sem chamador confirmado dentro do fluxo real de salvamento da tela de perguntas/respostas** nesta revisão. Isso é uma inconsistência real entre uma classe de persistência declarada e o fluxo de UI que deveria alimentá-la — não uma suposição.

---

## 28. Campos obrigatórios e condicionais

Com base na leitura integral de `EventoPapelQuantitativo.paraMapa()` (linhas 65-90):

| Campo | Obrigatório | Condicional | Não aplicável | Fonte |
|---|---|---|---|---|
| `id_acao` | Sim (sempre gerado) | — | — | `EventoPapelQuantitativo.java:67` |
| `tipo` | Sim | — | — | `:68` |
| `origem_da_acao` | Condicional (pode ser `null` se `origemAcao` for `null` — não há validação que impeça isso) | Sim | — | `:69` |
| `id_sessao`, `id_usuario_local`, `id_tentativa`, `id_situacao_problema`, `id_representacao` | Não — `ContextoAcao.NAO_INFORMADO` os deixa todos `null` por padrão | Sim, dependem do chamador fornecer `ContextoAcao` preenchido | — | `:70-74`, `ContextoAcao.java:18` |
| `papel_semantico` | Sim | — | — | `:75` |
| `estado_anterior`, `estado_posterior` | Sim (sempre strings, mesmo que `"?"`) | — | — | `:76-77` |
| `valor_proposto` | Condicional — pode ser a string `"null"` (não o valor `null` do mapa, mas o texto literal, conforme `formatarValorProposto`, `PapelQuantitativo.java:167-169`) | Sim | — | `:78` |
| `resultado` | Condicional — `null` se `resultado` for `null` | Sim | — | `:79` |
| `tipo_erro`, `chave_mensagem`, `chave_feedback_pedagogico`, `chave_sugestao_correcao` | Não — só presentes `if (diagnostico != null)` | Sim, explicitamente condicionais no código (`:80-87`) | Quando resultado é `ACEITO`, `diagnostico` é sempre `null` (visto em `PapelQuantitativo.posicionar`, linha 149) | `:80-87` |
| `timestamp_epoca_millis` | Sim | — | — | `:88` |

Não torno `valor_proposto` obrigatório em todo tipo de evento hipotético futuro (isso seria uma proposta, não um fato) — a tabela acima descreve apenas o único tipo de evento que existe hoje no pacote piloto (`EventoPapelQuantitativo`). Para uma arquitetura de múltiplos tipos de evento (Seção 24), a obrigatoriedade por tipo é **[PROPOSTA]**, não fato.

---

## 29. Idioma estrutural

**[EVIDÊNCIA NO CÓDIGO]**: os nomes de campo do mapa serializado (`id_acao`, `origem_da_acao`, `papel_semantico` etc.) estão em português (`snake_case`); os nomes de classe (`EventoPapelQuantitativo`, `OrigemAcao`) estão em português (`PascalCase`); os valores de enum (`ORIGEM_USUARIO`, `ACEITO`, `REJEITADO`) estão em português maiúsculo. As chaves i18n (`erro.papel.valorForaDoDominio`) usam pontos como separador, também em português. Não há, no pacote piloto, nenhum campo ou classe em inglês.

Isso contrasta com o vocabulário de produção auditado na Seção 9.2(b): `OrigemAvaliacao` serializa como chave JSON **inglesa** `"origin"`, com valores em `snake_case` também em português (`"soltura_usuario"`) — ou seja, a CHAVE estrutural é inglesa, mas o VALOR é português, uma mistura já presente na produção, independente de qualquer decisão futura sobre o piloto.

Não afirmo que um dos dois padrões (piloto 100% português vs. produção com chave inglesa/valor português) deva prevalecer — comparação de consistência/migração/interoperabilidade fica registrada como pauta, não decisão.

---

## 30. Chaves de internacionalização

**[EVIDÊNCIA NO CÓDIGO]**: `DiagnosticoErroPapel` carrega três chaves i18n (`chaveMensagem`, `chaveFeedbackPedagogico`, `chaveSugestaoCorrecao`), todas strings de chave (ex.: `"erro.papel.valorForaDoDominio"`), nunca texto resolvido. Não há, no pacote piloto, nenhuma tradução embutida — consistente com `feedback_dado_pesquisa_nao_embutir_em_codigo` (memória de sessões anteriores, não reverificada nesta auditoria, mas coerente com a evidência de código encontrada aqui). Não traduzo essas chaves nesta revisão, conforme instruído.

---

## 31. Versionamento

**[EVIDÊNCIA NO CÓDIGO, negativa]**: nenhum campo `schema_version`/`versao_esquema` existe em `EventoPapelQuantitativo` ou em qualquer classe do pacote piloto lida nesta revisão. O envelope de evento não tem versão. Isso é consistente com o que as revisões anteriores já haviam identificado como lacuna, e permanece lacuna nesta revisão — sem mudança de evidência.

---

## 32. Compatibilidade

**[PROPOSTA, não implementada]**: as cinco estratégias mencionadas pela instrução (substituição direta, chaves antigas mantidas temporariamente, serialização versionada, adaptador, dois formatos durante migração) permanecem como pauta comparativa não decidida. Dado que a Seção 12.1 confirma **zero consumidores externos** de `OrigemAcao` e que `PublicadorEventoDominio` não tem implementação de produção (Seção 10), o argumento a favor de uma migração de compatibilidade elaborada é fraco — mas essa é uma leitura, não uma decisão tomada aqui.

---

## 33. Cobertura da questão de pesquisa

Uso o texto da QP exatamente como fornecido pelo usuário nesta mensagem (tratado como dado direto, Seção 1.1):

> "Como distribuir responsabilidades na arquitetura do Gerard para manter os papéis quantitativos e as relações estruturais explicitados pela construção dos diagramas de Vergnaud e registrar, em cada tentativa, as ações realizadas, os estados sucessivos dos diagramas, os feedbacks exibidos, as perguntas formuladas e as respostas fornecidas?"

### Matriz A — questão de pesquisa

| Termo da QP | Definição (fonte) | Elemento arquitetural | Implementado | Verificado nesta revisão | Lacuna |
|---|---|---|---|---|---|
| 1. Distribuir responsabilidades | Não redefinido nesta revisão (termo geral) | Separação `gerard.dominio.campoaditivo` (domínio) vs. harnesses (verificação) vs. ausência de infraestrutura de produção conectada | Parcial | Sim, por leitura de código | Nenhum `PublicadorEventoDominio` de produção existe (Seção 10) — a "distribuição" para infraestrutura real não está implementada |
| 2. Arquitetura do Gerard | — | O repositório como um todo; pacote piloto é um subconjunto isolado | Parcial (só o piloto) | Sim | Piloto nunca referenciado por `Main.java` (Seção 2, confirmado por agente: nenhuma ocorrência de `gerard\.dominio\|PapelQuantitativo\|EventoPapelQuantitativo` em `Main.java`) |
| 3. Papéis quantitativos | `REFERENCE.md §4.2`: Parte, Todo, Transformação, Estado Inicial/Final, Referido, Referendo, Valor Relativo | `PapelQuantitativo` (classe única, reutilizada por fábrica) | Sim, para Composição e Transformação | Sim — harnesses executados nesta revisão (Seção 5.1), código de saída 0 | Comparação de Medidas (Referido/Referendo/Valor Relativo) não implementada — nenhuma classe `RelacaoEstruturalComparacao` encontrada no pacote piloto |
| 4. Relações estruturais | `REFERENCE.md §4.3` | `RelacaoEstruturalComposicao`, `RelacaoEstruturalTransformacao` | Sim, para as 2 categorias citadas | Sim, harnesses passam | Comparação de medidas, idem item 3 |
| 5. Construção dos diagramas de Vergnaud | — | Não modelado como objeto único no pacote piloto (Seção 23) | Não | Confirmado ausência | Nenhum objeto "Diagrama" agregador |
| 6. Tentativa | `REFERENCE.md §4.7` | `ContextoAcao.idTentativa` (campo, não classe própria) | Parcial — só um identificador, não uma entidade com ciclo de vida | Sim | Nenhuma classe `Tentativa` no pacote piloto — é apenas um `String` dentro de `ContextoAcao` |
| 7. Ações realizadas | — | `EventoPapelQuantitativo` (mas registra evento, não "ação" com fronteira própria — Seção 14) | Parcial | Sim | Fronteira de ação indefinida (Seção 14) |
| 8. Estados sucessivos dos diagramas | — | `estadoAnterior`/`estadoPosterior` (strings, de 1 papel por vez) | Parcial, muito limitado | Sim | Nenhum snapshot de diagrama completo (Seção 23) |
| 9. Feedbacks exibidos | — | No pacote piloto: só chaves i18n, sem exibição. Em produção: `ScaffoldingFeedbackMultissensorialErro` + `desenharAnotacaoMouseOver` (Seção 25) | Sim, em produção (fora do piloto); parcial no piloto | Sim — renderização real confirmada por código (Seção 25.3) | Falta um evento `FEEDBACK_EXIBIDO` com confirmação — a renderização ocorre, mas não é registrada como evento correlacionado (Seção 25.4); e o piloto isolado não implementa nada disso |
| 10. Perguntas formuladas | — | `PerguntaExplicativa`, `ComponenteC` (produção) — três estatutos distintos (cadastrada/disponibilizada/apresentada) | Sim, em produção; não no piloto | Sim (Seção 26) | Relação exata entre `screenOpened` e `presentedToUser` por pergunta individual não confirmada |
| 11. Respostas fornecidas | — | `RespostaExplicativa`, `ComponenteD`, fluxo completo via `TelaArtefatoExplicativo` (produção) | Sim, em produção; não no piloto | Sim (Seção 27) | `RepositorioArtefatosExplicativos.salvar(...)` sem chamador confirmado no fluxo real (Seção 27) — persistência dedicada existe mas parece desconectada |

**A QP não é respondida integralmente pelo estado atual do piloto** — várias dimensões (5, 9 parcial, 10, 11) não têm elemento arquitetural implementado ou verificado nesta revisão. Não declaro a QP como respondida.

---

## 34. Cobertura das cinco contribuições

Por força da Seção 1.2, as "cinco contribuições" tal como descritas em revisões anteriores não podem ser reafirmadas como **[FATO DOCUMENTAL]** do artigo canônico — ficam como **[INFERÊNCIA]**, com a ressalva de fonte não recanonizada, avaliadas apenas quanto à evidência de artefato correspondente:

| # | Contribuição (leitura anterior, não recanonizada) | Artefato correspondente | Localização | Estado |
|---|---|---|---|---|
| 1 | Modelo semântico (papéis, estados, relações) | `PapelQuantitativo`, `OrigemAcao`, `RelacaoEstrutural*` | `gerard.dominio.campoaditivo` | Encontrado e examinado — Composição e Transformação; Comparação ausente (Seção 33, item 3) |
| 2 | Procedimento e matriz de rastreabilidade | Matriz Suplementar S1 | — | **Não encontrada no repositório local** (Seção 4) |
| 3 | Modelo de registro factual | `EventoPapelQuantitativo`, `ContextoAcao` | `gerard.dominio.campoaditivo.evento` | Encontrado; lacunas identificadas nas Seções 17, 23, 25-27 |
| 4 | Prova de conceito em Java + verificações | 2 harnesses | `src/TestePiloto*.java` | Encontrado, **executado nesta revisão** (Seção 5.1) — código de saída 0 em ambos |
| 5 | Pacote de reprodutibilidade | Matriz S1, Arquivo S2, manifesto, somas SHA-256, relatório de implementação | — | **Não encontrado no repositório local** (Seção 4) — apenas as 4 skills e REFERENCE/MIGRATION_GUIDE existem localmente |

**Não trato o pacote de arquivos como contribuição operacionalizada sem demonstrar a cadeia completa** (fonte → afirmação de evidência → requisito → elemento arquitetural → verificação): as contribuições 2 e 5 dependem de artefatos ausentes nesta revisão — a cadeia não pode ser demonstrada localmente para elas.

---

## 35-41. Plano revisado P1–P6

### P1 — Origens
Somente planejamento. Ver classificação completa na Seção 12.3: **bloqueada por conflito documental**.

### P2-A — Tentativa, ação e evento
Somente planejamento. Ver Seção 21: **insuficientemente especificada**.

### P2-B — Classificação da interação
**Suspensa** enquanto não houver fonte suficiente (Seção 22).

### P3 — Modelo e esquema de eventos
Deve abranger, conforme já mapeado nas Seções 24, 28, 31: eventos tipados (Seção 24), estados (Seção 23), feedback (Seção 25), perguntas/respostas (Seções 26-27, cobertura insuficiente nesta revisão), versionamento (Seção 31), compatibilidade (Seção 32). **[PROPOSTA]**, não iniciada.

### P4 — Verificação técnica
Somente posterior a uma implementação autorizada. Critérios futuros (não avaliados como "passou/falhou" agora, porque nada foi implementado): compilação sem erros (já demonstrada nesta revisão para o estado ATUAL, não para uma implementação futura — Seção 5.1), harnesses aprovados (idem, Seção 5.1 — ambos passam hoje), testes novos aprovados (N/A, nenhum teste novo criado), ausência de regressão (N/A), serialização correta (N/A para mudanças futuras), origens corretas (depende de P1), identificadores corretos (depende de P2-A), ausência de Swing/AWT no domínio (**[EVIDÊNCIA NO CÓDIGO]** — não reverificada linha a linha nesta revisão, mas nenhum import de `javax.swing`/`java.awt` foi observado nos 7 arquivos do pacote piloto lidos integralmente nesta revisão), arquivos alterados iguais ao plano (N/A).

### P5 — Hipóteses analíticas
Permanece suspensa. Nenhuma classe criada nesta revisão.

### P6 — Reprodutibilidade
Somente após P4. Abrange, quando autorizada: relatório, manifesto, matrizes, hashes, histórico, comandos, pacote, comparação com o artigo (esta última também suspensa pela Seção 1.2 até recanonização).

---

## 42. Testes futuros

Planejamento, sem modificar nenhum teste existente:

| Comportamento a verificar | Já coberto pelos harnesses atuais? | Evidência |
|---|---|---|
| Linha de base / compilação | Sim | Seção 5.1 — código de saída 0 |
| Composição (12 comportamentos citados em leituras anteriores) | Sim, harness `TestePilotoPapelQuantitativo` roda 100% e reporta "TODOS OS TESTES DO PILOTO PASSARAM" — mas conta **7 eventos**, não os "4" de leituras anteriores (Seção 5.1/execução) | **[EVIDÊNCIA DE EXECUÇÃO]** — discrepância registrada, não reconciliada nesta revisão, pois depende da fonte suspensa |
| Transformação | Sim, harness roda 100%, reporta 27 eventos (24 aceitos, 3 rejeitados) — discrepante de "20 eventos (17/3)" de leituras anteriores | idem |
| Cada origem (`ORIGEM_USUARIO/SISTEMA/INFERENCIA/PESQUISADOR`) | Parcial — só `ORIGEM_USUARIO` e `ORIGEM_SISTEMA` são exercitados pelos harnesses atuais (confirmado por leitura); `ORIGEM_INFERENCIA`/`ORIGEM_PESQUISADOR` nunca instanciados em nenhum teste | **[EVIDÊNCIA NO CÓDIGO]** |
| Cada identificador (`attempt_id`/`action_id`/`event_id`) | Não — não existem como conceitos separados hoje (Seções 17-20) | **[PROPOSTA]** de teste futuro |
| Correlação 1:N | Não — não existe hoje (Seção 17.2, cardinalidade é sempre 1:1) | **[PROPOSTA]** |
| Estados anterior/posterior | Sim, testado | **[EVIDÊNCIA DE EXECUÇÃO]** |
| Valor proposto/calculado | Sim, testado | idem |
| Feedback exibido | Não testável no piloto (não existe evento de exibição); em produção, a renderização ocorre mas sem evento de confirmação (Seção 25.4) | **[PROPOSTA]** para o piloto; para produção, seria instrumentar `desenharAnotacaoMouseOver`/`sinalizarErro` com um evento de confirmação |
| Pergunta/resposta | Não modelado no piloto; em produção existe fluxo completo (`ComponenteC`/`ComponenteD`, Seções 26-27), mas com a lacuna de `RepositorioArtefatosExplicativos` sem chamador confirmado | **[PROPOSTA]** para o piloto; para produção, testar especificamente se `RepositorioArtefatosExplicativos.salvar` é de fato inatingível |
| Serialização | Sim, testado (`paraMapa()`) | **[EVIDÊNCIA DE EXECUÇÃO]** |
| Versão do esquema | Não testável (não existe campo) | **[PROPOSTA]** |
| Null Object | Sim, testado explicitamente em ambos harnesses | **[EVIDÊNCIA DE EXECUÇÃO]** |
| Ausência de Swing/AWT | Não testado automaticamente por nenhum harness localizado (é verificação manual/inspeção) | **[INCONCLUSIVO]** sobre automação |
| Regressão | Sim — harness de Composição reexecutado após Transformação, conforme comentário do próprio harness | **[EVIDÊNCIA DE EXECUÇÃO]**, confirmada nesta revisão pela reexecução bem-sucedida de ambos |

**Discrepância registrada, não interpretada como erro**: as contagens de evento reportadas pelos harnesses atuais (7 e 27) são maiores que as contagens atribuídas a leituras anteriores do artigo (4 e 20/17/3). Isso é consistente com os harnesses terem sido estendidos após o ponto de avaliação original (adição de testes de identidade semântica e Null Object nas correções de terminologia, commits `79476b2`/`bc0e569`, ambos posteriores a `48c917d`) — mas essa é uma leitura, registrada como **[INFERÊNCIA]**, não confirmada por comparação linha a linha com uma versão anterior do harness nesta revisão.

---

## 43. Decisões humanas pendentes

Consolidação de todas as decisões marcadas **[DECISÃO HUMANA PENDENTE]** neste relatório:

1. Qual arquivo é a versão canônica do artigo (Seção 1.2).
2. Qual dos quatro vocabulários de origem (Seção 9.2) deve orientar uma eventual unificação, e se essa unificação é sequer desejável agora.
3. Qual das sete alternativas de P1 adotar, condicionada à decisão 1 (Seção 12).
4. Se `idAcao`/`id_acao` do pacote piloto deve ser redefinido como `event_id` explícito, mantido como está, ou se um `action_id` separado deve ser introduzido (Seção 17.2, 19).
5. Qual definição de fronteira de ação adotar (Seção 14) — pré-requisito para qualquer especificação futura de `action_id`.
6. Qual arquitetura de evento adotar entre as quatro comparadas (Seção 24).
7. Se `"FEEDBACK"` deve ser formalmente incluído no vocabulário de seis termos reconhecidos por `LoggerInteracaoGerard` (Seção 13.3), ou se seu uso em `Main.java:8903-8909` é um caso a corrigir.
8. Se a ausência de chamador para `RepositorioArtefatosExplicativos.salvar(...)` (Seção 27) é uma lacuna a preencher ou uma classe morta a remover — decisão de produto/pesquisa, não técnica.
9. Se um futuro campo de versão de esquema para o pacote piloto deve seguir o padrão de `AgentAuditEvent` (versão lida de uma única fonte de verdade, após o bug documentado de dessincronia — Seção 24) ou um mecanismo mais simples.

---

## 44. Riscos residuais

### Matriz D — riscos

| Risco | Arquivo/dado afetado | Probabilidade | Impacto | Mitigação |
|---|---|---|---|---|
| Renomear/estender `OrigemAcao` sem verificar os outros 3 vocabulários de origem cria uma "correção" isolada que aumenta a fragmentação em vez de reduzi-la | `OrigemAcao.java` e os 3 vocabulários paralelos (Seção 9.2) | Média (é o padrão observado até aqui — cada vocabulário foi criado independentemente) | Médio — não afeta dados de produção (zero consumidores externos, Seção 12.1), mas perpetua confusão terminológica | Tratar a unificação de vocabulário como decisão de escopo amplo, não apenas do pacote piloto |
| Reaproveitar o nome `idAcao`/`action_id` de produção para o pacote piloto sem entender que já significa algo diferente lá (ligado a gesto, não a evento) | `EventoPapelQuantitativo.idAcao` vs. `AgentAuditService.actionId` | Baixa a curto prazo (pacotes desconectados hoje) — média se algum dia forem integrados | Alto se integração ocorrer sem reconciliar a semântica | Documentar explicitamente a diferença antes de qualquer integração futura |
| Basear decisões de vocabulário/contagem de arquivos em uma versão do artigo ainda não confirmada como canônica | Qualquer decisão de P1/P3 fundamentada em "o artigo diz X" | Alta, se a Seção 1.2 for ignorada em revisões futuras | Alto — decisões de nomenclatura tomadas sobre uma fonte errada já ocorreram nesta linha de auditoria (Revisão 2) | Resolver a canonicidade do artigo (Seção 1.2) antes de qualquer nova comparação documental |
| Assumir que os harnesses atuais são fiéis à avaliação original do artigo (contagens de evento diferentes — Seção 42) | `TestePilotoPapelQuantitativo.java`, `TestePilotoTransformacaoMedidas.java` | Baixa como risco técnico (harnesses passam, código de saída 0) — média como risco de comunicação/relatório | Baixo tecnicamente, médio para relatórios que citem contagens desatualizadas | Sempre citar a contagem re-executada nesta revisão (7 e 27), não a de leituras anteriores, ao descrever o estado atual |
| Introduzir um campo de versão de esquema sem lê-lo de uma única fonte de verdade | Qualquer futuro `EventoPapelQuantitativo`/envelope de evento do piloto | Média — já ocorreu uma vez em produção (`AgentAuditEvent`, Seção 24) | Médio — dessincronia silenciosa de versão já documentada como bug real corrigido em outro pacote | Seguir o padrão pós-correção de `AgentAuditEvent`: versão lida de um único objeto de identificação, nunca duplicada/hardcoded em mais de um lugar |
| Assumir que `TEXTO`/`QUANTIFICAR` (e potencialmente outras ações) têm canal de entrada fixo ao desenhar uma futura dimensão C | `Main.java` (múltiplos pontos, Seção 13.2) | Alta se não verificado — já ocorreu como suposição implícita nas Revisões 2-3 | Médio — levaria a um modelo de dados que não reflete o comportamento real do sistema | Modelar canal como campo por evento, não como atributo fixo do termo de ação semântica |

---

## 45. Avaliação separada de P1–P6

Ver Seção 30 (classificação final) — não repetido aqui para evitar duplicação, conforme a própria instrução prioriza não cortar/truncar conteúdo, não duplicá-lo desnecessariamente.

---

## 46. Recomendação

Não recomendo iniciar qualquer implementação de P1, P2-A, P2-B ou P3 nesta etapa. Recomendo, como próximo passo humano — não como autorização —, resolver primeiro a Seção 1.2 (canonicidade do artigo), porque múltiplas decisões subsequentes (Seções 7, 12.2 coluna "aderência ao artigo", 33, 34) dependem dela e hoje estão bloqueadas ou registradas como inconclusivas por essa causa raiz única.

---

## 47. Matriz final

### Matriz B — propostas

| Proposta | Evidência | Conflito | Decisão pendente | Condição de autorização |
|---|---|---|---|---|
| P1 (7 alternativas, Seção 12.2) | Seções 8, 9, 12 | Sim — 4 vocabulários de origem, ambiguidade interna dos docs normativos | Qual alternativa adotar | Resolver Seção 1.2 primeiro; decisão humana explícita e separada |
| P2-A (correlação evento/ação/tentativa) | Seções 14, 17-20 | Fronteira de ação indefinida | Definir fronteira de ação antes de especificar `action_id` | Decisão humana sobre fronteira de ação |
| P2-B (classificação de interação) | Seção 15 | Nenhuma fonte normativa | — | Fonte normativa para gesto/canal/técnica precisa existir primeiro |
| P3 (esquema de eventos) | Seções 24, 28, 31, 32 | Nenhum campo de versão hoje | Qual arquitetura de evento adotar | Após P1/P2-A resolvidas |
| P4 (verificação técnica) | Seção 35-41 | — | — | Após implementação autorizada de P1-P3 |
| P5 (hipóteses analíticas) | `REFERENCE.md §4.10` | — | — | Permanece suspensa, sem esboço |
| P6 (reprodutibilidade) | Seção 4 (artefatos ausentes) | Pacote de reprodutibilidade do código não existe localmente | — | Após P4 |

### Matriz C — rastreabilidade

| Fonte | Afirmação de evidência | Requisito | Elemento arquitetural | Verificação |
|---|---|---|---|---|
| `REFERENCE.md §4.8` | Evento deve distinguir origem em 4 categorias | Vocabulário de origem único e não ambíguo | `OrigemAcao` (parcial — só 1 de 4 vocabulários do repo) | **[EVIDÊNCIA NO CÓDIGO]** — Seção 9 |
| `RelacaoEstruturalTransformacao.java` (comentários) | Cálculo automático nunca deve ser `ORIGEM_USUARIO` | Separação usuário/sistema preservada | `posicionar(valor, origem, contexto)` com origem explícita | **[EVIDÊNCIA DE EXECUÇÃO]** — harness testa isso e passa (Seção 5.1, teste "origem da rejeição é do SISTEMA") |
| Leitura anterior do artigo (não recanonizada) | 413 arquivos compilados | Reprodutibilidade da compilação histórica | — | **[INCONCLUSIVO]** — Seção 5 |
| `dados/schema_agentes_execucao_gerard.json:67` | `action_id` acompanha `gesture_id`, mesmo ciclo de vida | Correlação ação/gesto em produção | `AgentAuditService` | **[EVIDÊNCIA NO CÓDIGO]** — Seção 17.3 |

(Matriz C completa e exaustiva excede o escopo prático deste documento; as quatro linhas acima ilustram a cadeia fonte→requisito→elemento→verificação nos pontos mais centrais desta revisão. Elementos adicionais estão detalhados em prosa nas seções correspondentes, sem sumarização redutora.)

Matrizes A e D já apresentadas nas Seções 33 e 44, respectivamente, sem repetição aqui.

---

## 48. Declaração de integridade

`git status --short` verificado antes e depois de cada etapa de leitura, compilação e execução desta revisão: vazio em todos os momentos. Um comando de compilação (`javac`, Seção 5.1) e dois comandos de execução de harness (`java`, Seção 5.1) foram executados, todos com saída direcionada para `C:\Users\cecomp\AppData\Local\Temp\gerard_audit_rev4_classes`, fora do repositório. Nenhum arquivo do projeto foi criado, removido ou alterado. Nenhuma classe, interface, enum, método, campo ou pacote foi criado. Nenhum patch foi aplicado. Nenhuma formatação ou substituição automática foi executada. Nenhum `git add`, commit, push, reescrita de histórico ou alteração de branch/tag foi feito. `git reset --hard` não foi usado. Nenhuma modificação foi descartada. Nenhuma dependência foi instalada. A versão do Java não foi alterada (compilação usou os parâmetros já declarados em `nbproject/project.properties`: `-source 1.8 -target 1.8`). Nenhuma ferramenta gravou arquivos dentro do repositório. P1, P2, P3, P4, P5 e P6 não foram iniciadas. Nenhuma proposta é apresentada como autorizada. Nenhum pedido de autorização genérica é feito.

---

## Encerramento obrigatório

Revisão 4 das Etapas 1, 2 e 3 concluída em modo somente leitura. Nenhum arquivo de código, teste, configuração, dado, documento normativo ou artigo foi alterado. Nenhum patch ou commit foi criado. P1, P2-A, P2-B e P3 permanecem sem autorização de implementação até decisão explícita e separada. P4 permanece como verificação técnica posterior. P5 permanece suspensa. P6 permanece condicionada à implementação autorizada e à aprovação de P4.
