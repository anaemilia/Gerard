# Verificação — achado 2 (`sugestaoAdotada`) e achado 3 (nota de documentação)

Data: 2026-08-05. Sem commit, sem push.

## 1. Diff aplicado — os 4 arquivos

### `REFERENCE.md` (achado 3, nota de documentação)
Aplicada exatamente como aprovada, inserida após o último parágrafo de §4.8.1, antes de "### 4.9":

> O registro de log distingue dois campos que respondem perguntas diferentes e não devem ser lidos como contraditórios entre si: o agente da linha (`"S"`/`"C"`, em `LoggerInteracaoGerard`) identifica quem realizou a ação de explicar/preencher a tela — sempre o participante (`"S"`), mesmo quando a mesma linha carrega a atribuição de um invariante; o campo de origem do invariante (`"PESQUISADOR"`/`"CATALOGO"`) identifica de onde veio o valor do código escolhido — não quem o escolheu, que é sempre o pesquisador. Nenhum dos dois campos corresponde a `OrigemAcao` (seção 4.8).

### `EventoLogGerard.java` (achado 2)
- `CABECALHO`: campo `"invariante_sugestao_adotada"` acrescentado ao final.
- Campo `private String invarianteSugestaoAdotada = "";`.
- `toTsv()`: campo acrescentado ao array `campos`.
- `deTsv(...)`: `evento.invarianteSugestaoAdotada = campo(campos, 30);` (índice fixo, mesmo padrão dos outros 6 campos finais).
- Getter/setter acrescentados.
- **Não faz parte do achado 2** (já estava no arquivo desta sessão, anterior à rodada atual): `normalizarAgente` ganhou um branch para `"P"` — ver seção 4 abaixo.

### `LoggerInteracaoGerard.java` (achado 2)
- Campo `invarianteSugestaoAdotadaAtual`.
- `associarInvarianteATentativaAtual` ganhou o 5º parâmetro `sugestaoAdotada`.
- **Extensão em relação à proposta original, não prevista quando o diff foi aprovado**: o método `associarInvarianteATentativaAtual` grava o invariante em **dois** lugares, não um — eu só tinha identificado um ao propor o diff. Os dois foram atualizados, para manter a mesma consistência que já existia nos outros 4 campos de invariante:
  1. Dentro do próprio `registrar(...)` (método central de gravação, ~linha 617): aplica os valores "Atual" a cada evento novo.
  2. Dentro do laço de reescrita de `associarInvarianteATentativaAtual` (~linha 721): reaplica retroativamente aos eventos já gravados da tentativa atual.
- Também identifiquei e atualizei um terceiro ponto, o reset de `iniciarTentativa(...)` (~linha 466), que zera os 4 campos de invariante ao começar uma tentativa nova — sem isso, `invarianteSugestaoAdotadaAtual` vazaria o valor da tentativa anterior para os primeiros eventos da tentativa seguinte, antes do pesquisador salvar de novo.
- `ContextoInteracao` (classe interna de snapshot/restauração): campo, construtor e `restaurarContexto` atualizados para espelhar o novo campo, junto com os outros 4.

### `TelaArtefatoExplicativo.java` (achado 2)
- Novo método privado `codigoSugeridoPeloSistema(String categoria)`, que duplica deliberadamente (não reaproveita) a lógica de busca do papel-incógnita já usada por `preSelecionarInvarianteSugerido`, e chama `sugestorInvarianteOperatorio.sugerirCodigo(...)`.
- Em `salvarNoLog(...)`: calcula `sugestaoAdotada` com os 3 valores acordados —`""` (sem sugestão para comparar), `"true"` (adotada), `"false"` (havia sugestão, pesquisador escolheu outra) — e passa como 5º argumento na chamada a `associarInvarianteATentativaAtual`.

Diff completo (`git diff`) disponível via terminal se quiser o patch bruto; os trechos acima cobrem 100% das mudanças de código.

## 2. Compilação

Árvore completa (421 arquivos `.java`), antes e depois do diff:

| | Antes | Depois |
|---|---|---|
| Código de saída | `0` | `0` |
| Avisos | 4 (pré-existentes: bootstrap classpath Java 8 + unchecked em `TesteUnidadeAnaliseABCD.java`) | os mesmos 4 |

## 3. Os quatro testes, antes e depois — resultado idêntico

| Teste | Antes | Depois |
|---|---|---|
| `TestePilotoPapelQuantitativo` | exit `0` · 7 eventos · TODOS PASSARAM | exit `0` · 7 eventos · TODOS PASSARAM |
| `TestePilotoTransformacaoMedidas` | exit `0` · 27 eventos (24 aceitos/3 rejeitados) · TODOS PASSARAM | exit `0` · 27 eventos (24/3) · TODOS PASSARAM |
| `TesteMapeamentoComparacaoComplementar` | exit `0` · "Teste aprovado" | exit `0` · "Teste aprovado" |
| `TesteSincronizacaoControlesPorCategoria` | exit `0` · "Teste aprovado" | exit `0` · "Teste aprovado" |

Nenhuma diferença em nenhum dos quatro.

Nota metodológica: para obter o "antes" desses dois testes de Comparação — que nunca tinham rodado nesta sessão — usei `git show HEAD:<arquivo>` para reconstituir os 3 arquivos de código no estado anterior ao diff, numa árvore de compilação separada, sem tocar a árvore de trabalho (a tentativa de `git stash` foi abortada pelo próprio Git por um conflito não relacionado em outro arquivo, `TAREFA_PENDENTE_COMPARACAO_MEDIDAS.md`, e nada foi alterado).

## 4. Alcançabilidade de `salvarNoLog` / `associarInvarianteATentativaAtual` pelos dois testes de Comparação

**Resposta explícita: nenhum dos dois testes exercita `salvarNoLog` ou `associarInvarianteATentativaAtual`, nem diretamente nem indiretamente.**

Verificado por dois caminhos:
- Busca textual direta nos dois arquivos de teste (`grep`) por `salvarNoLog`, `associarInvarianteATentativaAtual`, `TelaArtefatoExplicativo`, `LoggerInteracaoGerard` — nenhuma ocorrência.
- Busca dentro de `EstadoSemanticoCompartilhado.java` (a classe que os dois testes de fato exercitam) por qualquer referência a `LoggerInteracaoGerard`, `associarInvariante` ou `TelaArtefatoExplicativo` — nenhuma ocorrência.

Os dois testes cobrem a sincronização do estado semântico compartilhado (diagrama, texto, unidades) e o mapeamento de papéis complementares — não tocam a tela de explicitação nem o logger de interação. O diff de `sugestaoAdotada` é, portanto, seguro em relação a Comparação por não ter caminho de código que o alcance a partir desses dois testes — não por presunção, mas por verificação direta.

## 5. Pendência a decidir — inconsistência não resolvida entre a nota recém-aplicada e o código

A nota de documentação recém-aplicada (seção 1 acima) diz que o agente `"S"` em `registrarExplicacaoMatematica` é "sempre o participante". Mas essa linha exata do código, **desde uma rodada anterior desta mesma sessão**, está assim:

```java
registrar("P",
        "TAREFA_MATEMATICA_EXPLICACAO",
        ...
```

Ou seja: o código já não usa mais `"S"` nessa linha — foi trocado para `"P"` numa correção anterior (quando a interpretação de trabalho era outra), e `EventoLogGerard.normalizarAgente` ganhou um branch para `"P"` na mesma rodada. A nota que acabamos de aprovar e aplicar descreve o comportamento **antigo** (`"S"`), não o comportamento **atual** do código (`"P"`).

Isso não bloqueou a verificação pedida (compilação e os 4 testes passam de qualquer forma, porque nenhum deles lê o código de agente). Mas a documentação e o código estão descrevendo coisas diferentes agora, e isso não foi decidido explicitamente por você nesta sessão — só percebido agora, ao confirmar o diff.

Duas saídas possíveis, sem eu presumir qual:
1. Reverter `"P"` → `"S"` em `LoggerInteracaoGerard.registrarExplicacaoMatematica` e remover o branch `"P"` de `normalizarAgente`, alinhando o código à nota que acabamos de escrever.
2. Ajustar a nota para descrever o `"P"` como está hoje (mas isso muda o que você definiu como "o agente identifica sempre o participante", e você foi explícita: "Não altere `registrarExplicacaoMatematica` nem o código de agente 'S'").

Preciso da sua decisão antes de tocar nesse ponto — nenhuma das duas opções foi aplicada.
