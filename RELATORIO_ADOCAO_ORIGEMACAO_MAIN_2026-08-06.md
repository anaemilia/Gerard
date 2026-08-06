# Relatório: adoção de OrigemAcao em Main.java

Data: 2026-08-06

## Contexto

Segundo dos três passos combinados para retomar a fronteira Main↔piloto,
na ordem definida pelo usuário: depois de enriquecer o piloto (passo 1,
commit `e28a490`), "adotar só `OrigemAcao` na Main, para tipar a origem
das ações no lugar dos flags ad-hoc — cruza a fronteira 'isolado por
design' de forma bem mais estreita que a B2 completa."

## O flag ad-hoc encontrado

Main.java já distinguia ações do usuário de ações do computador para
efeito de log de pesquisa (`LoggerInteracaoGerard`) — mas essa distinção
era feita puramente por **qual método o chamador escolhia invocar**:
`registrarLogUsuario(...)` ou `registrarLogComputador(...)`, cada um
chamando diretamente o método correspondente de `LoggerInteracaoGerard`
(`registrarUsuario`/`registrarComputador`). Não havia nenhum valor
tipado carregando essa decisão — era uma convenção de nome de método,
não um dado.

Essa é exatamente a categoria que `OrigemAcao`
(`gerard.dominio.campoaditivo`) já modela:
`ORIGEM_USUARIO`/`ORIGEM_SISTEMA`/`ORIGEM_INFERENCIA`/`ORIGEM_PESQUISADOR`.

## O que mudou

- Novo import em `Main.java`: `gerard.dominio.campoaditivo.OrigemAcao`.
- `registrarLogUsuario(...)` e `registrarLogComputador(...)` — as duas
  assinaturas públicas (dentro da classe) permanecem **idênticas**;
  nenhum dos ~17 pontos de chamada foi tocado.
- Ambas agora delegam para um novo método privado único,
  `registrarLogPorOrigem(OrigemAcao origem, ...)`, que decide qual
  método de `LoggerInteracaoGerard` chamar com base no valor tipado de
  `origem`, não mais por qual wrapper foi escolhido.
- `ce` e `objeto` (que só fazem sentido para ações do usuário —
  avaliação de acerto/erro e objeto apontado) são passados como `null`
  pelo wrapper de computador e descartados no ramo `ORIGEM_SISTEMA` —
  exatamente o que já acontecia antes (`LoggerInteracaoGerard.registrarComputador`
  nunca recebeu esses dois parâmetros).
- Comportamento **byte-a-byte equivalente** ao anterior: os mesmos
  parâmetros chegam aos mesmos métodos de `LoggerInteracaoGerard`, na
  mesma ordem. É um refatoramento puro de tipagem da decisão de
  despacho, não uma mudança de comportamento.

## Escopo — cruzamento estreito da fronteira "isolado por design"

`PapelQuantitativo.java` documentava "este piloto é isolado por design:
não é referenciado por Main.java nem por nenhum caminho de produção".
Essa afirmação deixou de ser 100% literal (o pacote agora tem uma peça
referenciada por Main), então o javadoc foi atualizado para precisar o
que continua isolado (`PapelQuantitativo`, `RelacaoEstrutural*`,
`ResultadoCalculo`, `DiagnosticoErroPapel` — nada disso é usado por
Main) e registrar a exceção pontual: só o enum `OrigemAcao`, sem nenhuma
lógica associada, passou a tipar uma decisão que já existia em Main.

Nada além de `OrigemAcao` foi tocado: nenhum `PapelQuantitativo`,
nenhuma classe `RelacaoEstrutural*`, nenhum `calcularValorAusente`/
`aplicar`/`diagnosticarValorProposto` é usado por Main.java. A hipótese
de reuso mais amplo da arquitetura rica continua não validada — este
passo não a valida nem a invalida, só usa o tipo de valor mais simples
do pacote para uma finalidade estreita e já existente em Main.

## Verificação

Compilação completa do projeto (435 arquivos): **0 erros**.

Verificação de equivalência comportamental feita por leitura/raciocínio
sobre o diff (não há harness de GUI/log neste ambiente): confirmado que
os dois wrappers produzem exatamente as mesmas chamadas a
`LoggerInteracaoGerard` que produziam antes da mudança, para os mesmos
parâmetros de entrada.

## O que NÃO foi verificado / fora de escopo

- Não há harness automatizado para `LoggerInteracaoGerard` — a
  equivalência comportamental foi verificada por leitura do código, não
  por execução comparativa (mesmo padrão de risco documentado nos
  relatórios anteriores desta sessão sobre mudanças em `Main.java`).
- `ORIGEM_INFERENCIA` e `ORIGEM_PESQUISADOR` não têm, hoje, nenhum ponto
  de chamada em `Main.java` — caem no mesmo ramo de `ORIGEM_SISTEMA` em
  `registrarLogPorOrigem` por não existir, ainda, um registro de log
  específico para essas origens. Não é uma lacuna introduzida por esta
  mudança: antes dela, essas duas origens simplesmente não existiam como
  conceito em Main.

## Próximos passos (ordem já combinada com o usuário)

1. ~~Enriquecer o piloto~~ — concluído (`e28a490`).
2. ~~Adotar `OrigemAcao` na Main~~ — concluído nesta etapa.
3. Fase B2 completa — Main manipula `PapelQuantitativo` diretamente,
   substitui `EstadoSemanticoCompartilhado`. Maior risco/escopo desta
   sequência; pendente, só a começar quando pedida explicitamente.
