# Combobox de sinal para categorias de relações na curadoria — 2026-08-08

## Pedido

A partir de uma captura de tela da tela "Curadoria da situação-problema" (uma
situação `COMPOSICAO_RELACOES` com `relacao_1="+7"`, `relacao_2="-2"`,
`relacao_resultante="5"` digitados como texto livre, sinal embutido à mão):

> "quando for categorias de relações, coloque em cada campo, um combobox para
> escolha do sinal;"

## Diagnóstico

A curadoria já possuía, desde antes desta sessão, um mecanismo coeso e
reutilizável para exigir a escolha explícita do sinal em vez de deixá-lo
implícito no texto digitado: o pacote `gerard.campoaditivo.curadoria.sinal`
(`ControladorSinaisCuradoria`, `PainelValorComSinalCuradoria`,
`PapelSinalCuradoria`, `PoliticaSinalCuradoria`, `OpcaoSinalCuradoria`,
`ModoPersistenciaSinalCuradoria`). Esse mecanismo já cobria:

- `transformacao`/`sinalTransformacao` (Transformação de medidas, Transformação
  composta, **e também Transformação de uma relação**, que reaproveita os
  mesmos campos) — sinal como metadado separado, escolha obrigatória.
- `valorRelativo`/`sinalValorRelativo` (Comparação de medidas) — idem.
- `quantidade1`/`quantidade2`/`resultado` de **Composição de transformações**
  — sinal embutido no próprio valor (não há metadado próprio no modelo),
  seletor sempre visível mas opcional (comentário original do código: "A
  composição de transformações já possuía seletores de sinal no formato
  legado... preservados como opcionais").

Faltava exatamente o que a captura de tela mostra: os papéis de **Composição
de relações** (`relacao_1`, `relacao_2`, `relacao_resultante`, que reaproveitam
`quantidade1`/`quantidade2`/`resultado`) e os de **Transformação de uma
relação** que ainda não tinham seletor (`relacao_inicial`, `relacao_final`,
que reaproveitam `estadoInicial`/`estadoFinal` — o campo `transformacao`
dessa categoria já era coberto). `SituacaoProblemaAditiva` não tem
`sinalRelacao1`/`sinalRelacao2`/`sinalRelacaoInicial`/etc. — não há metadado
próprio para nenhum desses campos, exatamente a mesma situação de Composição
de transformações.

## Decisão de design

Estender o mecanismo já existente, replicando **exatamente** o tratamento já
dado a Composição de transformações (mesma classe, mesmo modo de persistência,
mesma política de opcionalidade), em vez de criar um mecanismo novo:

- Novos papéis em `PapelSinalCuradoria`: `RELACAO_INICIAL`, `RELACAO_FINAL`
  (Transformação de uma relação), `RELACAO_1`, `RELACAO_2`,
  `RELACAO_RESULTANTE` (Composição de relações).
- Registrados com `ModoPersistenciaSinalCuradoria.EMBUTIDO_NO_VALOR` (sinal
  dentro do valor, já que não há metadado próprio) — mesmo modo do padrão
  legado de Composição de transformações.
- Registrados via `registrar(...)` direto (não `registrarSeAplicavel`), e
  **sem** alterar `PoliticaSinalCuradoria.exigeEscolha` — ou seja, o seletor
  aparece sempre para essas categorias, mas a escolha não é bloqueante no
  salvamento, seguindo o precedente já documentado no próprio código-fonte
  para o caso estruturalmente idêntico (sinal embutido, sem metadado). Não
  havia necessidade de uma nova decisão pedagógica: é a mesma decisão já
  tomada e registrada para Composição de transformações.
- O campo `transformacao` de Transformação de uma relação **não foi tocado**
  — já era coberto (obrigatório, metadado separado) antes desta mudança.

Essa escolha respeita o princípio da localidade do conhecimento: a tela
apenas registra quais campos usam seletor para a categoria atual
(`controladorSinais.registrar(...)`), sem decidir por conta própria nenhuma
regra de obrigatoriedade — essa regra continua centralizada em
`PoliticaSinalCuradoria`, que não precisou mudar.

## Implementação

**`PapelSinalCuradoria.java`**: 5 novos valores de enum, cada um com sua
chave de i18n (`curadoria.sinal.papel.relacaoInicial`, `.relacaoFinal`,
`.relacao1`, `.relacao2`, `.relacaoResultante`), adicionadas nos 4 idiomas
(pt/en/es/fr).

**`TelaCuradoriaSituacoes.java`**:
- Novos booleanos `composicaoRelacoes` e `transformacaoRelacao` (paralelos ao
  já existente `composicaoTransformacoes`).
- 5 novos `PainelValorComSinalCuradoria` (`painelSinalRelacaoInicial`,
  `painelSinalRelacaoFinal`, `painelSinalRelacao1`, `painelSinalRelacao2`,
  `painelSinalRelacaoResultante`), cada um só instanciado quando o tipo da
  situação corresponde.
- Bloco de layout por categoria: `TRANSFORMACAO_RELACAO` e
  `COMPOSICAO_RELACOES` passam a exibir os novos painéis em vez dos
  `JTextField` crus.
- Bloco de "semântica herdada" (tradução somente-texto): guardas
  `if (painelSinalRelacaoX == null) configurarCampoHerdado(...)` adicionadas
  para `campoEstadoInicial`/`campoEstadoFinal`, e os guardas existentes de
  `campoQuantidade1`/`campoQuantidade2`/`campoResultado` passaram a checar
  também os novos painéis (`&& painelSinalRelacaoX == null`), evitando dupla
  configuração quando um painel os envolve.
- Bloco de persistência (`aplicarCamposDaCuradoriaDetalhada`): `estadoInicial`
  e `estadoFinal` passam a rotear por `controladorSinais.obterValorParaPersistencia`
  (antes era texto cru); `quantidade1`/`quantidade2`/`resultado` passam a
  encadear a consulta por `RELACAO_1`/`RELACAO_2`/`RELACAO_RESULTANTE` além
  da já existente `TRANSFORMACAO_1`/`TRANSFORMACAO_2`/`TRANSFORMACAO_RESULTANTE`
  — como os painéis são mutuamente exclusivos por tipo, o encadeamento é
  seguro (quando um painel não existe para o tipo atual, a consulta cai no
  valor bruto do campo, preservando o comportamento anterior).

Nenhuma lógica de `PoliticaSinalCuradoria`, `ControladorSinaisCuradoria` ou
`PainelValorComSinalCuradoria` foi alterada — só reuso.

## Verificação

Compilação completa do projeto (`javac`, todas as fontes) sem erros.

Teste temporário (`TesteTemporarioCuradoriaRelacoes.java`, criado, executado
sob Xvfb e removido antes do commit) abriu a tela real de curadoria
(`abrirTelaCuradoriaSituacao`, via reflection sobre membro privado, sem
reimplementar nenhuma lógica) para 4 linhas:

1. `COMPOSICAO_RELACOES` — confirmado: painéis presentes para `RELACAO_1`,
   `RELACAO_2`, `RELACAO_RESULTANTE`; sinal aplicado via combobox real
   (`+7`→`-7` negativo, `2`→`+2` positivo, `5`→`-5` negativo) refletido
   corretamente em `painel.obterValorParaPersistencia()` (o mesmo método que
   o salvamento chama).
2. `TRANSFORMACAO_RELACAO` — confirmado: painéis presentes para
   `RELACAO_INICIAL`, `TRANSFORMACAO` (já existente), `RELACAO_FINAL`; sinal
   aplicado corretamente (`-3`, `4` sem sinal embutido pois é metadado
   separado, `+7`).
3. **Regressão** `COMPOSICAO_TRANSFORMACOES` — confirmado que o comportamento
   legado (opcional, sinal embutido) continua idêntico após a mudança nos
   guardas de "semântica herdada".
4. **Regressão** `TRANSFORMACAO_MEDIDAS` — confirmado que `estado_inicial` e
   `estado_final` **não** ganharam seletor de sinal (continuam campo de texto
   simples, como deve ser para medidas absolutas) e que o seletor mandatório
   de `transformacao` permanece intacto.

Total: 22 verificações, todas aprovadas (`TODOS OS CHECKS PASSARAM`).

O teste não acionou o botão "Salvar e fechar" real (que grava em
`~/Gerard/curadoria/situacoes_vergnaud_curadas.tsv` e passa por validações
estruturais de vínculo entre versões, ortogonais a esta mudança) — em vez
disso, leu o valor diretamente da mesma API pública (`obterValorParaPersistencia()`)
que o código de salvamento invoca, o que verifica exatamente o trecho alterado
sem depender do restante da cadeia de gravação.

## Arquivos alterados

- `src/gerard/campoaditivo/curadoria/sinal/PapelSinalCuradoria.java`
- `src/gerard/campoaditivo/curadoria/TelaCuradoriaSituacoes.java`
- `src/gerard/i18n/mensagens_{pt,en,es,fr}.properties`
