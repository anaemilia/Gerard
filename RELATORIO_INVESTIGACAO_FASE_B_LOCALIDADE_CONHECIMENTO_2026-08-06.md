# Investigação — Fase B: localidade do conhecimento em EstadoSemanticoCompartilhado

Data: 2026-08-06. Somente leitura — nenhum código, nenhuma edição de documentação normativa, nenhum commit. Rigor epistêmico: [FATO NORMATIVO], [EVIDÊNCIA NO CÓDIGO], [INCONSISTÊNCIA], [INFERÊNCIA], [INCONCLUSIVO].

Escopo: passo 1 (investigação) da Fase B registrada em `TAREFA_PENDENTE_LOCALIDADE_CONHECIMENTO_ESTADO_COMPARTILHADO.md`. Objetivo: entender os 70 usos de `EstadoSemanticoCompartilhado` em 8 arquivos antes de qualquer decisão de design.

---

## 1. Achado principal: a superfície de risco é bem menor do que "70 usos" sugeria

**[EVIDÊNCIA NO CÓDIGO]** Dos 8 arquivos, só `Main.java` guarda uma instância mutável de `EstadoSemanticoCompartilhado` (`estadoSemanticoCompartilhado`, linha 773). Os outros 7 arquivos (`SimuladorEstadoComplementarVenn`, `EstadoProcessoTransformacao`, `SincronizadorUnidadesProcessoTransformacao`, os dois sincronizadores de texto, `ConversorIndiceEstadoCompartilhado`) só recebem e leem o objeto imutável `Snapshot` — nenhum deles guarda estado, nenhum deles chama `atualizar(...)`. `SimuladorEstadoComplementarVenn` chega a instanciar seu próprio `EstadoSemanticoCompartilhado` local, descartável, só para simular um snapshot hipotético — não é o estado compartilhado real.

Ou seja: migrar ou enriquecer só precisa decidir o que acontece dentro de `Main.java` e dentro de `EstadoSemanticoCompartilhado` — os 7 arquivos satélites continuam funcionando sem mudança nenhuma, desde que o tipo `Snapshot` (ou algo com a mesma interface de leitura) continue existindo.

## 2. Os 53 usos em Main.java não são 53 lugares com lógica própria — são ~8 pontos de entrada indo para 3 métodos centrais

**[EVIDÊNCIA NO CÓDIGO]** Main.java concentra a lógica em três métodos:

- `capturarEstadoCompartilhadoDoVergnaud(indice, origem)` (linha 7081) e `capturarEstadoCompartilhadoDoDiagramaComplementar(indice, origem)` (linha 7133) — leem os componentes visuais (elementos do Vergnaud, círculos do Venn, texto editável), montam `Integer[]`/`boolean[]`, e chamam `estadoSemanticoCompartilhado.atualizar(...)`.
- `aplicarEstadoCompartilhadoEmTodasAsRepresentacoes(snapshot, ...)` (linha 7202) — pega o `Snapshot` resultante e escreve de volta nos elementos do Vergnaud, no texto (via `sincronizadorElementosSemanticosTexto`), no diagrama Venn e nos eixos.

As oito origens (`VERGNAUD`, `DIAGRAMA_COMPLEMENTAR`, `EIXO_X`, `EIXO_VERTICAL`, `EDICAO_TEXTO`, `ARRASTE`, `EXCLUSAO`, `PROTOCOLO`) são só a etiqueta que cada evento de UI (arraste do mouse, edição de texto, replay de protocolo etc.) passa para esses mesmos métodos — não são 8 implementações paralelas da regra aditiva. A regra em si mora só dentro de `EstadoSemanticoCompartilhado.resolverRelacaoAditiva()`, chamada de um único lugar (`atualizar(...)`).

**Implicação prática**: o conhecimento matemático que "localidade do conhecimento" pede para não ficar espalhado já não está espalhado por Main.java — está concentrado em um método privado de uma classe. O que está espalhado é a leitura/escrita dos componentes Swing (isso é legitimamente orquestração de UI, não conhecimento de domínio — não é o que o princípio pede para mover).

## 3. Achado inesperado: `EstadoSemanticoCompartilhado` já serve 8 categorias com uma fórmula genérica, não 3

**[EVIDÊNCIA NO CÓDIGO]** `TipoSituacaoAditiva` tem 8 valores, não 3: `COMPOSICAO_MEDIDAS`, `TRANSFORMACAO_MEDIDAS`, `COMPARACAO_MEDIDAS`, e mais cinco tipos compostos/relacionais (`COMPOSICAO_TRANSFORMACAO_MEDIDAS`, `COMPOSICAO_TRANSFORMACOES`, `TRANSFORMACAO_COMPOSTA_DOIS_PASSOS`, `TRANSFORMACAO_RELACAO`, `COMPOSICAO_RELACOES`).

`resolverRelacaoAditiva()` **não lê o campo `tipo` em nenhum momento** — a mesma álgebra genérica de índice (soma/subtração entre as três posições, dependendo de qual foi alterada) atende aos 8 tipos. `tipo` só é usado para decidir o **domínio numérico** de cada posição (`dominioDoIndice` → `CatalogoEsquemasCategoriasAditivas.obter(tipo).obterDominioCompartilhado(indice)`), não para escolher a fórmula.

Isso significa que os três `RelacaoEstruturalX` do pacote piloto (Composição, Transformação, Comparação — Fase A) cobrem só 3 dos 8 tipos que a produção já atende. Se a Fase B substituir `EstadoSemanticoCompartilhado` pelos objetos do piloto, os outros 5 tipos ficam sem cobertura, a menos que se decida (a) criar mais 5 classes de relação, ou (b) verificar se os 5 tipos compostos se reduzem a aplicações sequenciais das mesmas 3 relações (não verificado nesta investigação — é uma pergunta em aberto, não uma resposta).

## 4. Referência órfã encontrada: `PLANO_REFATORACAO_ARQUITETURA_GERARD.md`

**[INCONCLUSIVO]** Onze arquivos (`ConversorIndiceEstadoCompartilhado.java`, `MapeadorPapelSemanticoTextoPadrao.java`, `AjustadorDimensoesItensTexto.java`, `SeletorItensTexto.java`, `ConsultasDiagramaVenn.java`, `UtilitariosComparacaoBarras.java`, `AreaTituloCategoriaEnunciado.java`, `ServicoLocalizacao.java`, `ValidadorTraducaoCurada.java`, `SerializacaoD3.java`, `scripts/verificar_regressao_gerard.py`) citam `PLANO_REFATORACAO_ARQUITETURA_GERARD.md` como a fonte de um plano de refatoração em fases ("Fase 1 do plano de refatoração"). O arquivo não existe no repositório — mesma categoria de problema já registrada no relatório da Revisão 5 (documento normativo citado, nunca salvo).

Isso é uma boa notícia disfarçada: confirma que **já existe um histórico de extrair lógica pura de `Main.java` para classes dedicadas** (os próprios arquivos que citam o plano são exemplos disso — `ConversorIndiceEstadoCompartilhado`, extraído do método `converterIndiceRealParaPapel` de `Main.java`, é um dos 8 arquivos deste levantamento). Ou seja, o padrão que a Fase B propõe não é inédito — já foi feito antes, parcialmente, para pedaços menores. Só não sabemos, sem o documento, que critério guiou o que foi extraído e o que ficou.

## 5. Duas opções de design, com trade-offs concretos agora (não só em abstrato)

**Opção B1 — enriquecer `EstadoSemanticoCompartilhado` no lugar (recomendada para começar):**
`resolverRelacaoAditiva()` passa a instanciar os `PapelQuantitativo` do piloto e delegar para `RelacaoEstruturalComposicao/Transformacao/Comparacao.calcularValorAusente()` nos 3 tipos já cobertos pela Fase A, mantendo o algoritmo genérico atual como *fallback* para os outros 5 tipos. A API pública de `EstadoSemanticoCompartilhado` (`atualizar`, `snapshot`, `Snapshot`) não muda. **Zero mudança em `Main.java` e nos 7 arquivos satélites.** Risco: baixo — é uma troca de implementação interna, testável comparando o comportamento antes/depois com os testes que já existem (`TesteMapeamentoComparacaoComplementar`, `TesteSincronizacaoControlesPorCategoria`) mais os harnesses do piloto.

**Opção B2 — substituir `EstadoSemanticoCompartilhado` pelos objetos do piloto na tela:**
Main.java passaria a manipular `PapelQuantitativo`/`RelacaoEstruturalX` diretamente, ganhando `EstadoConsistencia` tipado, `DiagnosticoErroPapel` e eventos publicados nas 8 origens. Isso é o que de fato entregaria o contrato rico *em produção*, não só internamente — mas exige tocar nos ~8 pontos de entrada em Main.java, decidir o que fazer com os 5 tipos compostos não cobertos pela Fase A, e decidir como `Snapshot` (consumido pelos 7 arquivos satélites) é preservado ou substituído. Risco: alto, conforme já registrado.

Nenhuma das duas foi decidida aqui — isto é levantamento, não escolha.

## O que não foi feito

Nenhum código alterado, nenhum arquivo de produção tocado, nenhum commit. Decisão sobre B1 vs. B2 (ou uma combinação: B1 agora, B2 depois de decidido o que fazer com os 5 tipos compostos) continua sua.
