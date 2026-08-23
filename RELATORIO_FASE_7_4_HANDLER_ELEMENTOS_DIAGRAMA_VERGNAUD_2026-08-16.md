# Fase 7.4 — handler dos elementos e conectores do diagrama de Vergnaud

Data: 2026-08-16

> Nota: existe um arquivo `RELATORIO_FASE_7_4_HANDLER_ELEMENTOS_DIAGRAMA_VERGNAUD_2026-08-15.md`
> com a data errada no nome (erro de digitação do assistente durante a sessão — o trabalho todo
> foi feito em 2026-08-16, não 08-15). Este é o relatório correto e completo; o outro foi
> substituído por uma nota curta de correção apontando para este.

## Objetivo

Continuar o roteiro incremental de `gerard-handlers-de-interacao`: depois da Fase 7.2
(`HandlerInteracaoItemTextoArrastavel`) e da Fase 7.3 (`HandlerInteracaoElementoTextoMovel`),
extrair de `Main.TelaGerard` o estado mecânico do gesto de reposicionamento dos elementos e
conectores já colocados no diagrama de Vergnaud (`ElementoVergnaud`/`ConectorVergnaud`) —
terceiro dos três protocolos restantes listados em `LEVANTAMENTO_PENDENCIAS_2026-08-11.md`
(quadradinhos do Venn, eixo de inteiros, elementos de Vergnaud). Escolha explícita da usuária,
2026-08-16: elementos de Vergnaud, não os quadradinhos do diagrama Venn nem o eixo de inteiros.

## Fronteira de conhecimento adotada

`HandlerInteracaoElementosDiagramaVergnaud` conhece somente:

- o elemento ou o conector ativo (mutuamente exclusivos dentro de um mesmo gesto);
- o deslocamento entre o ponteiro e o elemento no pickup (elemento) ou o último ponto do
  ponteiro (conector, que se move por delta);
- o limiar de arraste estrutural (`ControladorLimiarArrasteEstrutural`, instanciado dentro do
  próprio handler) — pequenas oscilações do mouse não confundem clique/duplo-clique com arraste
  real, mesma regra de antes da extração;
- movimento dentro dos limites recebidos da tela (não calcula a geometria, só aplica);
- conclusão e cancelamento do gesto.

Continuam em `Main.TelaGerard`, junto dos subsistemas responsáveis:

- hit-testing (`encontrarElementoVergnaud`/`encontrarConectorVergnaud`, que percorrem as listas
  completas de elementos/conectores do diagrama);
- política de duplo-clique (`PoliticaGestoEstrutural`);
- fantasma/arraste elástico (`iniciarFantasmaElementoVergnaud`, `iniciarFantasmaConector`,
  `iniciarArrasteElastico`);
- foco de outros protocolos (item textual, quadradinho do Venn);
- log de ação instrumental (`registrarAcaoGranular`, rastreamento granular);
- renderização (desenho normal fora do pickup, renderização em primeiro plano durante o pickup)
  e despacho dos eventos Swing.

Mesma divisão de responsabilidade das Fases 7.2/7.3: o handler não decide semântica nem
pedagogia, só mecânica do gesto. Diferente das duas fases anteriores, este protocolo não tem
avaliação semântica na soltura (reposicionar um elemento já colocado no diagrama de Vergnaud é
puramente visual, dentro dos limites da zona permitida de cada elemento/conector) — por isso o
handler não precisa de um método `concluir()` com valor de retorno como
`HandlerInteracaoItemTextoArrastavel.ResultadoSoltura`; `cancelar()` basta para os dois pontos em
que o gesto termina (reset defensivo no início de `mousePressed` e ao final do processamento de
`mouseReleased`), exatamente como o código fazia antes da extração.

## Alterações

- criado `src/gerard/interacao/arraste/HandlerInteracaoElementosDiagramaVergnaud.java`;
- `Main.TelaGerard` passou a delegar pickup (elemento e conector), movimento (com limiar e
  clamp), finalização do limiar e cancelamento ao handler, em vez de manter esse estado como
  campos soltos (`elementoVergnaudSelecionado`, `conectorVergnaudSelecionado`, `deslocamentoX`,
  `deslocamentoY`, `mouseAnteriorX`, `mouseAnteriorY`) e um `ControladorLimiarArrasteEstrutural`
  compartilhado na classe da tela;
- os seis pontos que resetavam `elementoVergnaudSelecionado`/`conectorVergnaudSelecionado` para
  `null` em conjunto (troca de situação-problema, nova situação, reinício, etc.) passaram a
  chamar `handlerElementosDiagramaVergnaud.cancelar()`;
- a renderização (desenho normal e desenho em primeiro plano durante o pickup) passou a ler
  `obterElementoAtivo()`/`obterConectorAtivo()` em vez dos campos removidos;
- criado teste unitário do contrato do handler
  (`tests/java/TesteHandlerInteracaoElementosDiagramaVergnaud.java`), cobrindo pickup de
  elemento e de conector, limiar de arraste estrutural, clamp pela zona permitida, movimento do
  conector por delta e exclusividade mútua entre elemento e conector;
- acrescentada proteção estrutural ao verificador de regressão
  (`scripts/verificar_regressao_gerard.py`): confirma a existência da classe, do fio de chamadas
  (`iniciarElemento`/`iniciarConector`/`mover`/`finalizarLimiar`/`cancelar`) em `Main.java`, a
  ausência dos campos antigos e do controlador de limiar duplicado, a independência do handler
  em relação à tela e ao hit-testing, e que a tela continua responsável pelo hit-testing.

## Correções feitas durante a própria validação (2026-08-16)

A primeira rodada do verificador de regressão, feita pela usuária, reprovou por duas falhas nas
checagens desta própria fase — nenhuma delas era um problema no código de produção, ambas eram
autoarmadilhas nas checagens novas:

1. `handler permanece independente da tela e do hit-testing dos elementos/conectores` falhava
   porque o comentário Javadoc do próprio handler citava os nomes literais
   `encontrarElementoVergnaud`/`encontrarConectorVergnaud` (para explicar que o hit-testing
   continua na tela) — a checagem por substring pegava essa menção em prosa. Reescrito o
   comentário sem os nomes literais dos métodos.
2. A checagem do item 2 (flag de teste removida, ver `RELATORIO_AJUSTES_LEVANTAMENTO_PENDENCIAS_2026-08-16.md`)
   tinha o mesmo problema: o comentário em `Main.java` que documenta a remoção da constante cita
   o nome dela por completo. Reescrita a checagem para procurar a declaração exata (`private
   static final boolean EXIBIR_DIAGRAMA_COMPLEMENTAR_SEMPRE_PARA_TESTES`) e o uso em `||`, não
   qualquer menção ao nome.
3. Bug real, independente do anterior: as checagens novas (itens 2, 3 e desta fase) tinham sido
   acrescentadas depois do último portão de reprovação do script (`if errors: ... sys.exit(1)`)
   — uma falha ali não derrubaria o código de saída nem imprimiria `REPROVADO` no final, só o
   `[ERRO]` individual passaria despercebido num script que "termina" sem checar o resultado
   agregado. Acrescentado um portão final (`if errors: REPROVADO; sys.exit(1)` / `APROVADO`) ao
   fim do arquivo.

Todas as correções foram simuladas neste ambiente (sem JDK disponível) rodando o corpo inteiro
do script contra os arquivos reais do repositório, com o único passo que exige JDK (`ant clean
jar`) substituído por um stub que apenas marca sucesso — as 191 checagens restantes, incluindo
todas as desta fase e das fases anteriores, passaram sem nenhuma falha antes de pedir a segunda
rodada real para a usuária.

## Verificação

- `ant clean jar` (rodado pela usuária, ambiente Windows real): **BUILD SUCCESSFUL**.
- verificador de regressão arquitetural (`scripts/verificar_regressao_gerard.py`, rodado pela
  usuária depois das correções acima): **APROVADO**, nenhuma falha.
- harness Robot real (`TesteMonkeyGuiadoPorCasosReais`, rodado pela usuária em 2026-08-16):
  uma primeira tentativa foi interrompida por queda de energia no meio do episódio 14/14 (sem
  relação com o código — confirmado que os arquivos reais `perfis_usuario.tsv` e
  `diagnosticos_tarefa.tsv` continuavam byte a byte idênticos aos backups feitos pelo harness
  antes de começar, comparação por SHA-256). Segunda rodada, completa:

  ```
  Fim: 14 episodio(s) reais rodados via Robot, 45 passo(s) sem divergencia, 0 divergencia(s).
  Arquivos reais (perfis_usuario.tsv / diagnosticos_tarefa.tsv) restaurados ao estado anterior ao teste.
  ```

  `Listeners registrados em TelaGerard: MouseListener=1 MouseMotionListener=1` — confirma que a
  extração do handler não duplicou nem removeu o registro de listeners. Zero ocorrências de
  erro/exceção/falha em `despacho_mouse_released_20260816_221235.log` e em
  `eventos_tecnicos_20260816_221235.jsonl`. Mesmos 14 episódios reais do catálogo de doutorado
  usados nas validações das Fases 7.2/7.3 (Felipe Wanderley, Jamile, Jamilly — Transformação e
  Composição de medidas).

  Nuance de cobertura, para ser precisa: os episódios do catálogo exercitam pickup e arraste de
  `ItemTextoArrastavel` a partir do enunciado (fluxo das Fases 7.2/7.3), não incluem um gesto
  script­ado de "arrastar um `ElementoVergnaud`/`ConectorVergnaud` já colocado no diagrama para
  outra posição" — esse reposicionamento é uma ação de organização visual livre, sem papel
  semântico definido em nenhum episódio gravado. Ainda assim, a extração desta fase é exercitada
  indiretamente em todo episódio: o hit-testing (`encontrarElementoVergnaud`/
  `encontrarConectorVergnaud`) roda a cada `mousePressed`, a checagem `existePickupAtivo()`/
  `handlerElementosDiagramaVergnaud.estaAtivo()` roda a cada evento de mouse, e a renderização
  (`obterElementoAtivo()`/`obterConectorAtivo()`) roda a cada repaint do diagrama de Vergnaud —
  sem nenhuma exceção ou divergência nos 45 passos. A mecânica específica do gesto (pickup,
  limiar de arraste, clamp pela zona permitida, movimento do conector por delta) é coberta pelo
  teste unitário `TesteHandlerInteracaoElementosDiagramaVergnaud`, não pelo harness Robot — mesma
  divisão de responsabilidade já usada nas fases anteriores entre teste unitário do contrato do
  handler e harness Robot para regressão do fluxo real mais amplo.

## Conclusão

A extração de `HandlerInteracaoElementosDiagramaVergnaud` está concluída e validada: build
limpo, verificador de regressão arquitetural aprovado, e harness Robot real sem divergência nem
exceção não tratada nos 14 episódios/45 passos do catálogo de doutorado. Restam do roteiro
(`gerard-handlers-de-interacao`, passo 3): quadradinhos do diagrama Venn e eixo de inteiros —
nenhum dos dois autorizado a começar sem nova escolha explícita da usuária.
