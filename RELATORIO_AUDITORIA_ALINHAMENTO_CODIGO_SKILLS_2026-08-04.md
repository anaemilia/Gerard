# Auditoria de Alinhamento — Código × Pacote Normativo das Skills

Modo: somente leitura. Nenhum arquivo `.java` foi alterado, criado ou apagado nesta auditoria.

Branch: `migracao-nomenclatura-relacao-estrutural`. `git status --short` no início desta auditoria mostrava apenas os quatro arquivos `.md` da correção normativa 2.1 (aplicados nesta mesma sessão, a pedido explícito da autora, ainda não commitados) e o relatório da Revisão 4 (não rastreado, de uma interação anterior) — nenhuma mudança de terceiros na árvore de trabalho.

As cinco skills (`gerard-semantic-model/REFERENCE.md`, `gerard-semantic-model/MIGRATION_GUIDE.md`, `gerard-domain-model-first/SKILL.md`, `gerard-knowledge-oriented-domain-objects/SKILL.md`, `gerard-knowledge-locality-principle/SKILL.md`, `gerard-semantic-event-logging/SKILL.md`) foram lidas por completo antes desta auditoria, com `REFERENCE.md` relido na versão já corrigida (correção 2.1: seção 4.2.1 e o critério `SISTEMA`/`INFERENCIA_COMPUTACIONAL` em 4.8).

Legenda: **[ALINHADO]** · **[DESALINHADO]** · **[AMBÍGUO]**

---

## a. Origem de eventos gerados por relações estruturais — deve ser sempre SISTEMA

**[ALINHADO]**

`RelacaoEstruturalTransformacao.java:70,77,83,88` — todo cálculo/aplicação de valor ausente produz `OrigemAcao.ORIGEM_SISTEMA`, sem exceção nos quatro pontos de uso. Testado explicitamente em `TestePilotoTransformacaoMedidas.java:149-150` ("evento da rejeição tem origem_da_acao = ORIGEM_SISTEMA"). Corresponde exatamente ao critério agora explícito em `REFERENCE.md §4.8`: "Uma relação estrutural que calcula ou aplica um valor ausente produz origem `SISTEMA`."

---

## b. `SugestorInvarianteOperatorio.sugerirCodigo` — sugestão sem origem até adoção

**[ALINHADO]**

`SugestorInvarianteOperatorio.java:31-64` é uma função pura: recebe categoria + chave de papel incógnito, retorna `String` (código de catálogo) ou `null`. Sem efeito colateral, sem campo de origem, sem produzir evento. É consumida em `TelaArtefatoExplicativo.java:527` (`preSelecionarInvarianteSugerido`, linhas 516-538) apenas para pré-selecionar um item de combo — nunca salva nada sozinha, nunca registra nenhuma origem. A sugestão, de fato, não tem origem registrada em nenhum momento antes de qualquer ação humana subsequente.

---

## c. Tela do pesquisador — seleção/criação de invariante e preservação da origem PESQUISADOR

**[AMBÍGUO]**

Quatro observações concretas, nenhuma delas resolvida nesta auditoria:

1. **`TelaArtefatoExplicativo.java:393`**: `String origemInvariante = inserirNovaForma.isSelected() ? "PESQUISADOR" : "CATALOGO";` — este campo **não é do tipo `OrigemAcao`**. É uma string livre de dois valores que responde a uma pergunta diferente ("de onde veio o valor: catálogo fechado ou forma nova digitada"), não "quem originou a ação". Já registrado como dimensão separada e não mapeável 1:1 a `OrigemAcao` na Revisão 5 (Seção 13).

2. **Nenhuma distinção entre "pesquisador adotou a sugestão do sistema" e "pesquisador escolheu livremente do catálogo, sem qualquer pré-seleção"** — ambos os casos produzem `"CATALOGO"` de forma idêntica em `salvarNoLog` (`TelaArtefatoExplicativo.java:389-418`). Depois de salvo, não há como reconstruir se a sugestão de `SugestorInvarianteOperatorio` influenciou a escolha do pesquisador.

3. **`LoggerInteracaoGerard.registrarExplicacaoMatematica`** (linhas 742-774) grava a linha inteira com `registrar("S", ...)` — código de agente `"S"` (sujeito/participante) **hardcoded na chamada**, independentemente do valor de `invarianteOrigem` recebido como parâmetro. Quando `invarianteOrigem == "PESQUISADOR"` (o pesquisador digitou uma forma simbólica nova), a mesma linha de log carrega uma contradição interna: o campo de agente do registro diz participante, o campo `invariante_origem` dentro dos `detalhes` da mesma linha diz pesquisador.

4. Não está estabelecido nesta auditoria se as linhas de `LoggerInteracaoGerard`/`EventoLogGerard` (mecanismo de log textual anterior ao pacote normativo v2.0, com cabeçalho de 30 colunas e código de agente de uma letra) são, de fato, os "eventos semânticos" que `REFERENCE.md §4.8` regula, ou um sistema de registro paralelo, pré-existente, ainda não avaliado formalmente contra esse modelo.

---

## d. Recorrência da confusão relação-estrutural / invariante-operatório

**[ALINHADO]**

Nenhuma recorrência nova localizada. `SugestorInvarianteOperatorio` tem o nome correto para o que faz — sugere um *código* de invariante teórico para o pesquisador considerar, nunca modela ou computa a relação formal (distinção mantida explicitamente no javadoc da própria classe, linhas 3-27). Os campos `invarianteCodigo`/`invarianteSimbolico`/`invarianteOrigem`/`invarianteObservacao` de `DiagnosticoTarefa.java:36-39` armazenam a atribuição teórica do pesquisador (comentário de classe, linhas 18-23: "nenhum agente calcula automaticamente qual invariante uma ação mobiliza... mas o pesquisador já atribui um... decisão humana direta"), corretamente separados das classes `RelacaoEstrutural*` do pacote piloto.

---

## e. Distribuição de responsabilidades sistema / pesquisador / usuário

| Ponto | Classificação | Evidência |
|---|---|---|
| `PapelQuantitativo.posicionar` — usuário fornece dado da situação-problema | **[ALINHADO]** | `OrigemAcao.ORIGEM_USUARIO` por padrão, `PapelQuantitativo.java:130` |
| `RelacaoEstruturalTransformacao` — sistema aplica regra explícita e fixa | **[ALINHADO]** | Ver item a |
| `AnalisadorNivelConceitual.classificar` → `DiagnosticoTarefa.nivelConceitualEstimado` | **[DESALINHADO]** | `AgenteModelador.registrarExplicacaoNoUltimoDiagnostico` (linhas 209-234) chama o classificador e grava o resultado direto no campo via `diagnostico.setNivelConceitualEstimado(...)` — **nenhum evento é produzido, nenhuma origem é registrada**. Pelo critério agora adotado em `REFERENCE.md §4.8`, esta operação deriva uma classificação a partir de evidência textual — seria `INFERENCIA_COMPUTACIONAL` — mas hoje não passa por nenhum mecanismo de evento/origem: é mutação direta de campo, sem observabilidade |
| `AgenteMonitor`/`AgenteZDP` → `OrigemAvaliacao` | **[AMBÍGUO]** | Usam um vocabulário de origem inteiramente diferente (`SOLTURA_USUARIO`/`SELECAO_CATEGORIA`/`REAVALIACAO_CONSISTENCIA`/etc., 10 valores, distinguindo canônico/reativo — não usuário/sistema/pesquisador/inferência). Não está estabelecido se esses fluxos de produção foram concebidos para se alinhar a `REFERENCE.md §4.8`, ou se são um sistema anterior e paralelo, fora do escopo do pacote normativo v2.0 |
| Fluxo de invariante da tela do pesquisador (`TelaArtefatoExplicativo`/`LoggerInteracaoGerard`) | **[DESALINHADO]** / **[AMBÍGUO]** | Ver item c, achados 1-4 |
| `InferenciaRegrasModelador.inferir` (PART/Apriori via Weka) | **[AMBÍGUO]** | Sem persistência localizada e sem chamador confirmado em produção (Revisão 4, Seção 10) — sem evidência de efeito observável hoje, não é possível avaliar alinhamento com segurança |

---

## Estado ao final desta auditoria

Nenhum arquivo `.java` foi alterado, criado, editado ou apagado. Nenhum commit foi feito. A auditoria está completa e apresentada para revisão — aguardando decisão sobre os itens `[AMBÍGUO]` (especialmente item c, achados 1-4) e aprovação item a item dos `[DESALINHADO]`:

1. Campo de agente hardcoded `"S"` em `LoggerInteracaoGerard.registrarExplicacaoMatematica`, que pode contradizer `invariante_origem == "PESQUISADOR"` na mesma linha.
2. Ausência de evento/origem para a operação de `AnalisadorNivelConceitual.classificar`.

Nenhuma implementação foi iniciada.

---

## Continuação — evidência completa dos achados 1 e 3 (item c), 2026-08-04

Retomando o achado 2 (rastrear se o pesquisador adotou a sugestão do sistema), pausado esperando achados 3 e 4. Achado 4 já estava concluído (`LoggerInteracaoGerard`/`EventoLogGerard` são independentes de `OrigemAcao` — Seção "Estado ao final desta auditoria" acima). Evidência completa de arquivo/linha para 1 e 3, antes de propor o diff de `sugestaoAdotada`:

### Achado 1 — `origemInvariante` nunca é lido como `OrigemAcao`

`git grep -n "origemInvariante" -- '*.java'` — 4 ocorrências, todas em `TelaArtefatoExplicativo.java`:

- **linha 393**: `String origemInvariante = inserirNovaForma.isSelected() ? "PESQUISADOR" : "CATALOGO";`
- **linha 400**: passado para `logger.associarInvarianteATentativaAtual(origemInvariante, ...)`.
- **linha 405**: passado para `logger.registrarExplicacaoMatematica(...)`.
- **linha 415**: passado para `agenteModelador.registrarExplicacaoNoUltimoDiagnostico(...)`.

Os três métodos receptores declaram o parâmetro como `String` (`origemInvariante`/`invarianteOrigem`) — nenhum declara `OrigemAcao`, nenhum converte, nenhum compara a `ORIGEM_PESQUISADOR`. Gravado só como texto: `LoggerInteracaoGerard.java:712` (`evento.setInvarianteOrigem(invarianteOrigemAtual)`) e `LoggerInteracaoGerard.java:756` (concatenado em `detalhes`).

**Confirmado: `origemInvariante` e `OrigemAcao` nunca se tocam em nenhum ponto do código.**

### Achado 3 — código de agente `"S"`/`"C"`: produtores e consumidores completos

**Produtores**, em `LoggerInteracaoGerard.java`:
- linha 531: `registrar("S", ...)`
- linha 544: `registrar("S", ...)`
- linha 557: `registrar("C", ...)`
- linha 761: `registrar("S", ...)` — dentro de `registrarExplicacaoMatematica`.

**Único chamador de `registrarExplicacaoMatematica`**: `TelaArtefatoExplicativo.java:404`.

**Consumidores de `getAgenteDaAcao()`**:
- `EstatisticasLogGerard.java:23` (`totalPorAgente`), `:63` (`contarPorAgente`).
- `TelaVisaoPesquisador.java` — 20+ pontos, os relevantes:
  - linha 1572: `"S".equals(e.getAgenteDaAcao()) && (t.contains("tipo de problema") || ...)`
  - linha 1575: `"C".equals(e.getAgenteDaAcao()) && (t.contains("feedback") || ...)`
  - linha 1660: `"C".equals(ant.getAgenteDaAcao()) && "S".equals(atual.getAgenteDaAcao()) && "C".equals(atual.getCe()) && mesmoContexto(ant, atual) && ...` — sequência causal computador→sujeito.
  - linhas 1704, 1734, 1745, 1755: mesmo padrão `"C"` seguido de `"S"`, variações (erro, sequência de três).
  - linha 941: exportação JSON, chave `"agente"`.
- `EventoLogGerard.java:137,199`: `evento.ce = "C".equals(evento.agenteDaAcao) ? "" : normalizarCe(...)`.
- `LoggerInteracaoGerard.java:313`: mesma lógica na reescrita do log.

**Conclusão**: `"S"` depende de significar especificamente "participante/sujeito" — as detecções causais (linhas 1660/1704/1734/1745/1755) só fazem sentido se `"S"` for consistentemente o participante. Pergunta diferente de "quem atribuiu o invariante" (sempre o pesquisador, já confirmado) — mas a linha de `registrarExplicacaoMatematica` entra nessas mesmas contagens/detecções sempre como `"S"`, independente de quem preencheu o invariante.

### Pendente

Com achados 1, 3 e 4 completos, falta decidir se achados 2 (`sugestaoAdotada`) e 3 (agente `"S"` incorreto) devem ser resolvidos na mesma linha de código ou separadamente, antes de propor o diff de `sugestaoAdotada`. Nenhuma proposta de diff foi feita ainda nesta continuação.

---

## Decisão sobre achados 1, 2 e 3 (2026-08-05)

**Achado 1**: fechado, sem ação — confirmado que `origemInvariante` e `OrigemAcao` nunca se tocam.

**Achado 3**: não é um bug. `"S"` responde a uma pergunta diferente de `invariante_origem` — quem realizou a ação de explicar/preencher (sempre o participante, agente `"S"`), não quem atribuiu o código do invariante (sempre o pesquisador, nunca o participante, já confirmado no achado d). As duas coisas coexistem sem contradição na mesma linha. Não alterar `registrarExplicacaoMatematica` nem o código de agente `"S"` — mudar isso quebraria as detecções causais (sequências `C`→`S`) que já dependem dessa consistência em `TelaVisaoPesquisador`. Única ação: documentação.

### Nota de documentação (achado 3) — proposta, não aplicada

Em `REFERENCE.md §4.8.1`, logo após o último parágrafo ("...adotando ou não uma sugestão do sistema."), antes de `### 4.9`:

```diff
   do sistema.
 
+O registro de log distingue dois campos que respondem perguntas diferentes
+e não devem ser lidos como contraditórios entre si: o agente da linha
+(`"S"`/`"C"`, em `LoggerInteracaoGerard`) identifica quem realizou a ação de
+explicar/preencher a tela — sempre o participante (`"S"`), mesmo quando a
+mesma linha carrega a atribuição de um invariante; o campo de origem do
+invariante (`"PESQUISADOR"`/`"CATALOGO"`) identifica de onde veio o valor do
+código escolhido — não quem o escolheu, que é sempre o pesquisador. Nenhum
+dos dois campos corresponde a `OrigemAcao` (seção 4.8).
+
 ### 4.9 Verbalização e explicação
```

### Diff proposto — achado 2 (`sugestaoAdotada`), não aplicado

`associarInvarianteATentativaAtual` tem um único chamador em todo o repositório (`TelaArtefatoExplicativo.java:400`) — mudar sua assinatura é seguro. Três arquivos:

**1. `EventoLogGerard.java`** — novo campo `invariante_sugestao_adotada`, mesmo padrão dos 4 campos de invariante já existentes (`CABECALHO`, campo privado, `toTsv()`, `deTsv()` no índice fixo 30, getter/setter).

**2. `LoggerInteracaoGerard.java`** — novo parâmetro `sugestaoAdotada` em `associarInvarianteATentativaAtual`; novo campo `invarianteSugestaoAdotadaAtual`. Também precisa tocar a classe interna `ContextoInteracao` (linhas 385-434), que espelha os 4 campos `*Atual` de invariante para `capturarContextoAtual()`/`restaurarContexto(...)` — sem isso, restaurar um contexto capturado perderia o valor silenciosamente.

**3. `TelaArtefatoExplicativo.java`** — novo método privado `codigoSugeridoPeloSistema(String categoria)` (duplica a busca de papel incógnito já feita em `preSelecionarInvarianteSugerido`, sem alterar aquele método); em `salvarNoLog`, calcula `sugestaoAdotada` comparando o código escolhido ao sugerido, passa como 5º argumento na chamada existente. Sem mudança de UI.

**Decisão de modelagem não presumida**: proposto `sugestaoAdotada` com três valores — `""` (sem sugestão disponível), `"true"` (adotada), `"false"` (havia sugestão, pesquisador escolheu outra coisa) — em vez de só dois. Aguardando confirmação ou preferência por dois valores.

Nada foi aplicado. Aguardando aprovação da nota e do diff.
