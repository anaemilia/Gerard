# "Estado modificado" — registro de auditoria visível para papéis-dado divergentes do curado

Data: 2026-08-06. Extensão do que foi feito em `RELATORIO_ESTADO_MODIFICADO_ALVO_RECALCULADO_INCOGNITA_2026-08-06.md` (commit `d70a5c4`): aquela mudança só recalculava o *alvo* da incógnita a partir dos papéis-dado atuais. Faltava tornar visível — para pesquisa — que um papel-dado (não a incógnita) divergiu do curado, independente de a incógnita já ter sido respondida. Pedido explícito do usuário.

## Desenho

- `EstadoPosicionamentoModelagem` (`gerard.campoaditivo.conclusao`): novo campo `Boolean estadoModificado`, com getter `getEstadoModificado()`. Diferente de `valorCorrespondeAoCurado` (só relevante para a incógnita), este se aplica a **qualquer** papel. `true` quando o valor atual diverge do curado; `null` quando não há curado disponível para conferir (mesmo critério já usado em toda a família de comparações curado/atual). Construtor de 7 argumentos preservado (delega para o novo de 8 com `estadoModificado = null`) — nenhum chamador existente foi quebrado.
- `Main.java`, `capturarPosicionamentosConclusao()`: passou a computar `estadoModificado` para **todo** papel capturado (antes só computava `valorCorrespondeAoCurado`, e só para a incógnita), via novo método `calcularEstadoModificado(papel, valorAtual)` — mesma lógica de `valorDigitadoCorrespondeAoCurado`, mas comparando contra `obterValorCuradoParaPapel` diretamente (o curado real, não o alvo recalculado — aqui o objetivo é detectar a divergência em si, não julgar a incógnita).
- `Main.java`, novo método `registrarPapeisDadoModificadosSeHouver()`: no momento em que o estudante submete um valor para a incógnita (dentro do bloco `correto != null` de `confirmarValorIncognitaAceito` — mesma condição que já garante curado disponível e submissão real em andamento), percorre `capturarPosicionamentosConclusao()` e, se algum papel-dado (não a incógnita) estiver com `estadoModificado == true`, registra **um** log de sistema (`registrarLogComputador`, tipo `ESTADO_MODIFICADO`) listando papéis e valores divergentes. Não bloqueia nem altera o fluxo de confirmação existente — só acrescenta o registro.

## Por que esse ponto de disparo

Evitar duas armadilhas: (a) logar a cada `repaint()`/movimento de mouse (spam, sem valor) — por isso não foi colocado em `verificarConclusaoModelagem` (chamado dezenas de vezes por interação); (b) precisar instrumentar as 8 origens de mudança de estado individualmente (`EIXO_X`, `ARRASTE` etc. — escopo bem maior, tocaria pontos que "são da main" por design). O ponto escolhido — submissão de incógnita — já é o único lugar hoje instrumentado com granularidade de pesquisa para esse tipo de decisão (`registrarLogUsuario`, `agenteMonitor`, `agenteZDP`, `conectorVereditoModelador`), e é exatamente o momento em que a divergência passa a importar (a incógnita está sendo avaliada contra um contexto que mudou).

## Verificação

- Compilação completa: 435 arquivos, 0 erros.
- Rastreamento manual: no cenário relatado hoje (Transformação 5→2 via eixo x, estudante submete 18 para Estado final), `calcularEstadoModificado("papel.transformacao", "2")` retorna `true` (curado=5≠2); o log é emitido com `papeis=Transformação Reais=2` (rótulo localizado). No cenário sem desvio, todos os papéis-dado retornam `false` — nenhum log emitido, sem mudança de comportamento visível.
- Sem harness automatizado (mesma limitação de todo este trecho — depende de `ItemTextoArrastavel`/Swing/diálogo).

## O que não foi feito

Não foi criado um enum/tipo dedicado para "estado modificado" (ex.: ao lado de `EstadoConsistencia`/`OrigemAcao` no pacote piloto `gerard.dominio.campoaditivo`) — o campo ficou como `Boolean` em `EstadoPosicionamentoModelagem`, seguindo o padrão já existente (`valorCorrespondeAoCurado`) em vez de introduzir um tipo novo. Se for necessário formalizar mais (ex.: para os agentes pedagógicos reagirem a isso, não só para o log), fica para quando for pedido.
