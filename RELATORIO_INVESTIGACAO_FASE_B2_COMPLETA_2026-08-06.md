# Relatório: investigação (só leitura) da Fase B2 completa

Data: 2026-08-06

## Pergunta investigada

Antes de decidir se vale a pena fazer a Fase B2 completa (Main manipula
`PapelQuantitativo` diretamente, substitui `EstadoSemanticoCompartilhado`),
mapear com precisão o que cada ponto de sincronização realmente precisaria
para migrar — não uma estimativa de superfície, uma leitura do código.

## Achado 1 — a superfície real é bem menor do que "70 usos em 8 arquivos" sugeria

O catálogo anterior (tarefa #17, só leitura) contou 53 ocorrências em
`Main.java`, mas ler o código mostra que quase todas convergem para
**dois pontos de escrita únicos**:

- `capturarEstadoCompartilhadoDoVergnaud(indice, origem)` (linha ~7112) —
  lê os 3 elementos do diagrama de Vergnaud e chama
  `estadoSemanticoCompartilhado.atualizar(...)`.
- `capturarEstadoCompartilhadoDoDiagramaComplementar(indice, origem)`
  (linha ~7175) — lê os 3 nós do diagrama Venn complementar e chama o
  mesmo `atualizar(...)`.

Os "8 origens" (`VERGNAUD`, `DIAGRAMA_COMPLEMENTAR`, `EIXO_X`,
`EIXO_VERTICAL`, `EDICAO_TEXTO`, `ARRASTE`, `EXCLUSAO`, `PROTOCOLO`) não
são 8 caminhos de código independentes — são 8 *valores* que diferentes
chamadores passam para os mesmos dois funis, através de dois
wrappers finos (`sincronizarTodasAsRepresentacoesAPartirDoVergnaud` e
`sincronizarTodasAsRepresentacoesAPartirDoDiagramaComplementar`, linha
~7341). O resultado (`Snapshot`) sai dos dois funis para um único ponto
de distribuição, `aplicarEstadoCompartilhadoEmTodasAsRepresentacoes`
(linha ~7244), que empurra os valores para Vergnaud/Venn/eixos/texto.

**Isso é uma notícia boa para a viabilidade de uma migração de fato**: a
arquitetura de Main já está mais concentrada do que o catálogo de
superfície sugeria.

## Achado 2 — a lacuna real: "recalcular um papel já conhecido"

`EstadoSemanticoCompartilhado.resolverRelacaoAditiva()` (a função central
que decide o que fazer a cada atualização) tem **dois algoritmos
distintos**, não um só:

1. **"Primeiro preenchimento"** — exatamente um dos três papéis está
   incógnito → calcula esse único valor. **Já delegado às classes ricas
   do piloto desde a Fase B1** (`calcularValorAusente`, ver
   `resolverViaRelacaoEstruturalRica`, linha 242).
2. **"Preenchimento automático de consistência"** — os três já estão
   preenchidos, um deles muda (ex.: o sujeito arrasta um número já
   posicionado), e o sistema recalcula um dos *outros dois* para manter
   a soma consistente. Esse é o algoritmo genérico ainda não migrado
   (linhas 165–213 de `EstadoSemanticoCompartilhado`), e **não tem
   equivalente no contrato do piloto**: `calcularValorAusente` exige
   precondição de "exatamente um incógnito" — não sabe (nem tenta)
   recalcular um valor que já está preenchido.

Isso é usado o tempo todo durante interação real: o sujeito arrasta um
valor já posicionado no diagrama completo, e outro valor precisa se
ajustar para preservar a relação aditiva. Não é um caso de borda raro —
é o caminho comum de "editar depois de já ter preenchido tudo".

**Consequência**: migrar Main para `PapelQuantitativo` diretamente não é
"trocar o objeto que guarda os três valores" — primeiro seria preciso
**desenhar e implementar, no piloto, uma capacidade nova** (algo como
"recalcular um papel já conhecido a partir de uma mudança em outro",
distinto de `calcularValorAusente`), ou aceitar um sistema híbrido onde
parte da lógica continua fora do piloto. Isso é trabalho de design novo,
não redirecionamento mecânico de chamadas.

## Achado 3 — `PapelQuantitativo` tem identidade e eventos; `EstadoSemanticoCompartilhado` também é usado como calculadora descartável

`PapelQuantitativo` é desenhado para objetos com identidade persistente
que publicam eventos a cada `posicionar(...)` (via `PublicadorEventoDominio`).
Mas `EstadoSemanticoCompartilhado` hoje é usado de duas formas bem
diferentes:

- Como **estado real persistente** (o campo de instância em `Main.java`,
  mutado ao longo da interação) — esse uso mapearia razoavelmente bem
  para objetos `PapelQuantitativo` persistentes.
- Como **calculadora "e se?" descartável**: `SimuladorEstadoComplementarVenn.simular()`
  (linha 90) cria `new EstadoSemanticoCompartilhado()` do zero a cada
  chamada, só para rodar `atualizar(...)` como função pura — verificar
  se uma mudança proposta no diagrama Venn respeitaria os limites antes
  de ser aplicada de fato — e descarta o resultado. Já existe um
  precedente exatamente desse padrão dentro do próprio
  `calcularComRelacaoRica()` (linha 280): cria `PapelQuantitativo`
  descartáveis com `PublicadorEventoDominio.NENHUM` só para uma
  chamada. Migrar esse uso específico é viável — mas só cobre o
  sub-caso "primeiro preenchimento" (Achado 2); a simulação do Venn usa
  o algoritmo completo, incluindo o de consistência que ainda não existe
  no piloto.

## Achado 4 — 4 arquivos fora de Main são só leitores de `Snapshot`

`SincronizadorUnidadesProcessoTransformacao`, `EstadoProcessoTransformacao`,
`SincronizadorElementosSemanticosTexto`/`...Abstrato` e
`ConversorIndiceEstadoCompartilhado` só leem `Snapshot` (`getValor`,
`isConhecido`, `getDominio`, por índice 0–2, formato posicional/array).
Não é uma dificuldade conceitual — qualquer substituto precisaria
oferecer uma leitura equivalente por trio de papéis —, mas é superfície
adicional a tocar fora de `Main.java` caso o formato de leitura mude.

## Conclusão

A Fase B2 completa não é hoje uma "troca mecânica de objeto que guarda
três valores por três `PapelQuantitativo`". Ela depende de um trabalho
de design ainda não feito — dar ao piloto uma capacidade que ele não
tem (recalcular um papel já conhecido para preservar consistência,
Achado 2) — antes de qualquer redirecionamento de chamadas em
`Main.java`. Sem essa capacidade, o máximo que dá para migrar
mecanicamente hoje é o sub-caso "primeiro preenchimento", que **já foi
migrado** (Fase B1, isso já está feito).

A superfície de `Main.java` em si (Achado 1) é mais tratável do que o
catálogo original sugeria — só 2 funis de escrita, não dezenas de
caminhos espalhados —, então o risco de "espalhar a mudança por um
arquivo de 13 mil linhas" é menor do que se temia. O bloqueio real não é
tamanho de superfície, é a lacuna de capacidade no Achado 2.

## Caminhos possíveis a partir daqui

1. **Fechar a lacuna do Achado 2 primeiro, no piloto** (zero risco de
   produção, mesmo espírito das Fases A e do passo 1 já feito): desenhar
   e implementar a capacidade de "recalcular um papel já conhecido"
   nas 6 classes `RelacaoEstrutural*`, com harness de teste, antes de
   cogitar qualquer mudança em `Main.java`. Só depois disso a B2
   completa se torna uma migração mecânica de verdade.
2. **Não fazer B2 completa** — os dois funis de escrita + o fan-out já
   são uma arquitetura razoavelmente limpa; o valor arquitetural
   principal (lógica de domínio centralizada, decisão de origem tipada)
   já foi capturado nos passos 1 e 2. Full B2 passaria a ser
   justificado só por completude conceitual, não por um problema
   concreto que resolve.
3. **Migração parcial só do funil "primeiro preenchimento"**: os dois
   pontos onde `resolverViaRelacaoEstruturalRica` já delega ao piloto
   (dentro de `EstadoSemanticoCompartilhado`) poderiam, em tese, ser
   promovidos para usar `PapelQuantitativo` com identidade persistente
   em vez de descartável — mas isso muda o modelo de eventos sem
   resolver a lacuna do Achado 2, então o ganho é pequeno perto do
   risco de mexer em código de produção usado para coleta de dados.
