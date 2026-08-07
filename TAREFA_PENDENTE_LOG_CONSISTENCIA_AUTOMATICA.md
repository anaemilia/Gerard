# Lacuna registrada — manutenção automática de consistência não gera log em produção

Status: **resolvida (2026-08-07).** Ver "Implementação" ao final —
`RELATORIO_LOG_CONSISTENCIA_AUTOMATICA_IMPLEMENTACAO_2026-08-07.md`.

---

## O achado

A recomputação automática de consistência entre representações — disparada
quando o diagrama atinge consistência ("azul") e o sistema ajusta
posições/representações para mantê-las sincronizadas — **não produz
nenhuma linha no log real de produção**, em nenhuma das três categorias de
Medidas (Composição, Transformação, Comparação).

## Evidência

Caminho rastreado em `Main.java`:
- `sincronizarTodasAsRepresentacoesAPartirDoVergnaud(elemento, origem)`
  (`Main.java:7299-7306`) resolve o valor ausente via
  `EstadoSemanticoCompartilhado.resolverRelacaoAditiva()`
  (`EstadoSemanticoCompartilhado.java:149-194`) e aplica o resultado com
  `aplicarEstadoCompartilhadoEmTodasAsRepresentacoes(snapshot, true)`.
- `aplicarEstadoCompartilhadoEmTodasAsRepresentacoes` (`Main.java:7202-7239`,
  corpo lido por completo): só manipula widgets de interface
  (`definirValorNoElementoNumeroRelativo`, `definirValorNoElementoMedida`,
  `sincronizarElementosSemanticosDoTexto`, `sincronizarDiagramaVennComRepresentacoes`,
  `sincronizarEixosComEstadoCompartilhado`) — nenhuma chamada a
  `loggerInteracaoGerard`, `registrarLogUsuario`, `registrarLogComputador`
  ou `registrarAcaoGranular`.
- Esse caminho é único e genérico, usado por 17+ pontos de chamada em
  `Main.java` (7304, 7312, e via esses dois métodos: 9696, 9824, 10689,
  10773, 10919, 11308, 11412, 11427, 11890, 11958, 12058, 12164, 12216,
  12242, 13130) — sem bifurcação por categoria nesse trecho.

Investigação completa: `RELATORIO_LOG_CONSISTENCIA_AUTOMATICA_2026-08-05.md`.

## Consequência prática

A distinção `ORIGEM_USUARIO`/`ORIGEM_SISTEMA`, normatizada em
`REFERENCE.md §4.8` e ilustrada com este mesmo caso concreto, hoje não tem
onde ser aplicada na prática para a recomputação automática de
consistência — porque essa recomputação não gera nenhuma linha de log em
produção, nem com origem `SISTEMA` nem com nenhuma outra.

## Autorização

Esta lacuna **não está autorizada a ser corrigida agora**. Existe só como
registro. Nenhuma decisão foi tomada sobre se deve gerar log, com que
granularidade, ou por qual mecanismo.

## Implementação (2026-08-07)

Pedida explicitamente. `EstadoSemanticoCompartilhado.Snapshot` passou a
expor `getIndiceResolvidoAutomaticamente()` — o domínio expõe o fato de
que resolveu algo; `Main.java` só lê esse fato e chama
`registrarLogComputador` (origem `ORIGEM_SISTEMA`, evento
`CONSISTENCIA_AUTOMATICA`). Cobre os dois casos descritos no achado
original: primeiro preenchimento e recálculo de consistência ("azul").
Detalhes completos, incluindo uma correção de rota (primeira tentativa
colocava a detecção em `Main.java` por diff — rejeitada por violar
localidade relacional, corrigida para expor o fato no domínio) e a
verificação (compilação completa, suíte comparativa de 40 cenários, 7
harnesses do piloto, suíte temporária dedicada de 10 checagens):
`RELATORIO_LOG_CONSISTENCIA_AUTOMATICA_IMPLEMENTACAO_2026-08-07.md`.

Caveat conhecido, não resolvido aqui: um ponto de chamada específico
(controle de barras da Comparação, arraste contínuo) pode gerar uma linha
de log por passo do arrasto quando o valor dependente muda a cada passo —
throttling de log durante gesto contínuo é uma decisão de política
separada, não tomada nesta tarefa.
