# P3.1 — identidade da ação e da sequência de rejeições

Data: 2026-08-24

Branch: `codex/auditoria-arquitetural-pos-505553d`

Linha de base anterior: `f0ca4a8`

## Escopo realizado

A primeira migração foi aplicada ao protocolo `TEXTO` usado para preencher a
incógnita. A alteração não muda a geometria, a interface, o conteúdo dos
feedbacks nem o instante em que o apoio da terceira rejeição é apresentado.

O fluxo agora distingue:

- `gesture_id`: identidade do gesto físico, mantida exclusivamente no fluxo de
  gestos;
- `action_id`: identidade nova para cada submissão semanticamente constituída;
- `rejection_sequence_id`: correlação compartilhada somente pelas rejeições
  consecutivas da mesma sequência.

Três rejeições consecutivas produzem três `action_id` diferentes e um único
`rejection_sequence_id`. Os eventos já existentes de limite, disponibilização
do material concreto e mensagem da terceira rejeição conservam a identidade da
terceira ação e a mesma sequência; eles não são recontados como ações do
participante.

## Localidade das responsabilidades

- `PapelQuantitativo` emite a identidade da ação e mantém o estado da sequência
  de rejeições.
- `IdentidadeAcaoInstrumentalPapel` e
  `ResultadoRegistroTentativaPapel` transportam fatos tipados, sem Swing/AWT.
- `LoggerInteracaoGerard` apenas grava as identidades recebidas.
- `EventoLogGerard` acrescenta os dois campos ao final do TSV e continua lendo
  linhas antigas, nas quais esses campos ficam vazios.
- `IdentificacaoEvento` deixou de definir gesto e ação como a mesma identidade;
  a auditoria aceita o `action_id` emitido pelo proprietário semântico.
- `RegistroGestoInteracao` e seu TSV permaneceram inalterados e sem C/E,
  diagnóstico, `action_id` ou `rejection_sequence_id`.

No protocolo migrado, os três registros anteriores da mesma entrada
(`Substituir`, `TEXTO` e `QUANTIFICAR`) foram consolidados em uma única ação
avaliada. Os demais protocolos mantêm o comportamento anterior e não devem ser
descritos como migrados por esta fase.

## Compatibilidade

- A assinatura anterior de `registrarTentativaIncognita(String, boolean,
  ItemTextoArrastavel)` foi preservada como adaptador para os harnesses e
  protocolos existentes.
- A API booleana anterior de `PapelQuantitativo.registrarTentativa` foi mantida
  e delega à nova API tipada.
- `action_id` e `rejection_sequence_id` são as duas últimas colunas do TSV.
- Uma tentativa realizada enquanto o papel está bloqueado continua sendo um
  fato com `action_id` próprio, mas não amplia a sequência de três rejeições
  avaliadas.
- Um acerto encerra a sequência corrente; restaurar zera o estado da sequência.

## Verificação

Linha de base antes da alteração:

- 472 fontes Java;
- 85 testes Java compilados;
- 81 testes executados e aprovados;
- 4 testes gráficos compilados e não executados;
- 0 falhas.

Resultado depois da alteração:

- 474 fontes Java;
- 86 testes Java compilados;
- 82 testes executados e aprovados;
- 4 testes gráficos compilados e não executados;
- 0 falhas.

Testes focados:

- `TestePilotoTentativasRejeitadas`: três ações distintas, uma sequência,
  bloqueio, restauração, nova sequência e acerto;
- `TesteIdentidadeLogAcaoInstrumental`: round-trip dos novos campos e leitura
  compatível de linha antiga;
- `TesteRegistroGestoInteracao`: ausência das identidades de ação no log de
  gestos.

O verificador determinístico `scripts/verificar_regressao_gerard.py` foi
ampliado com as invariantes da P3.1 e terminou com:

`APROVADO: verificador de regressão completo, nenhuma falha registrada.`

## Limite deliberado

Esta fase corrige identidade e persistência do primeiro protocolo. Ela não
integra ainda `IncognitaQuantitativa`, `RegistroAcaoInstrumental`, fotografia do
Modelo do Usuário ou decisão adaptativa publicada pelo Modelador. Essa
integração continua pertencendo à P4 e não foi simulada por conveniência.
