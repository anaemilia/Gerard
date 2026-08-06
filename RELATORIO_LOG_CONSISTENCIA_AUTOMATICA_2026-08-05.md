# Investigação — o segundo estágio ("azul") deixa rastro no log de ações?

Data: 2026-08-05. Somente leitura — nenhum código foi alterado.

## Contexto

Comportamento pretendido descrito pela autora: "(1) da primeira vez apenas o participante pode preencher o item desconhecido (isso já está implementado); (2) quando o diagrama fica azul (denotando consistência atingida), fica aberto para o sistema efetuar ações para manter a consistência entre posições e entre representações." Investigação verifica se essa distinção de dois estágios aparece no log de ações real, ou só no comportamento do código sem gerar registro.

## 1. Primeiro preenchimento (participante) — evento de domínio existe, mas não chega ao log de ações

`PapelQuantitativo.posicionar(ValorNumerico)` (`src/gerard/dominio/campoaditivo/PapelQuantitativo.java:129-131`) delega para a sobrecarga com origem explícita:
```java
public Optional<DiagnosticoErroPapel> posicionar(ValorNumerico valorProposto) {
    return posicionar(valorProposto, OrigemAcao.ORIGEM_USUARIO, ContextoAcao.NAO_INFORMADO);
}
```
Confirma a Revisão 5: o evento é produzido com `ORIGEM_USUARIO` por padrão, publicado via `publicar(new EventoPapelQuantitativo(...))` (linhas 147/157).

**Esse evento não chega ao log de ações real.** Busca em toda a árvore `gerard.dominio.campoaditivo` por qualquer referência a `LoggerInteracaoGerard` — zero ocorrências. O evento só vai para o `PublicadorEventoDominio` injetado no construtor; nos harnesses, é uma `List<EventoDominio>` em memória local (`TestePilotoPapelQuantitativo.java:27-28`). Confirma isolamento do pacote piloto (`PapelQuantitativo.java:37-38`), não referenciado por `Main.java`. `OrigemAcao` tem zero ocorrências em `Main.java` — o log de ações de produção (`LoggerInteracaoGerard`/`EventoLogGerard`, TSV real) não usa esse vocabulário em nenhum ponto.

## 2/3/4. Manutenção de consistência após "azul" — silenciosa, sem evento de log, mesmo mecanismo nas três categorias

`EstadoSemanticoCompartilhado.resolverRelacaoAditiva()` (`EstadoSemanticoCompartilhado.java:149-194`) roda dentro de `atualizar(...)` e resolve o terceiro valor silenciosamente — sem publicar nada, sem `PublicadorEventoDominio`, sem conceito de evento.

Caminho rastreado até a interface, em `Main.java`:
- `sincronizarTodasAsRepresentacoesAPartirDoVergnaud(elemento, origem)` (`Main.java:7299-7306`) chama `capturarEstadoCompartilhadoDoVergnaud(...)` (invoca `estadoSemanticoCompartilhado.atualizar(...)`, resolvendo o valor ausente) e depois `aplicarEstadoCompartilhadoEmTodasAsRepresentacoes(snapshot, true)`.
- `aplicarEstadoCompartilhadoEmTodasAsRepresentacoes` (`Main.java:7202-7239`, corpo completo lido): só chama `definirValorNoElementoNumeroRelativo`, `definirValorNoElementoMedida`, `sincronizarElementosSemanticosDoTexto`, `sincronizarDiagramaVennComRepresentacoes`, `sincronizarEixosComEstadoCompartilhado` — nenhuma chamada a `loggerInteracaoGerard`, `registrarLogUsuario`, `registrarLogComputador` ou `registrarAcaoGranular`.
- `sincronizarDiagramaVennComRepresentacoes` (`Main.java:6933-7062+`, corpo lido): só manipula `circulosVenn`/`quadradinhosVenn`, zero chamada ao logger.

Esse caminho é único e genérico, não bifurca por categoria — usado por 17+ pontos de chamada em `Main.java` (7304, 7312, e via esses dois métodos: 9696, 9824, 10689, 10773, 10919, 11308, 11412, 11427, 11890, 11958, 12058, 12164, 12216, 12242, 13130), cobrindo Composição/Transformação/Comparação com o mesmo código — `TipoSituacaoAditiva` é só parâmetro repassado, não ponto de ramificação nesse trecho.

Em contraste, o log de ações real (`registrarAcaoGranular`, `Main.java:10820-10824`, ex. protocolo `"POSICIONAR"` em `finalizarRastreamentoGranular`, linha 10873) registra a ação do próprio usuário ao soltar um elemento — chamada separada e paralela, disparada pelo mesmo handler de mouse que também aciona a sincronização; sem relação de causa nem chamada de log dentro do caminho de resolução automática.

## Conclusão (pergunta 4)

A manutenção de consistência após "azul" hoje **não produz nenhum evento/linha de log**, em nenhuma das três categorias. A distinção `ORIGEM_USUARIO`/`ORIGEM_SISTEMA` do piloto (`OrigemAcao`) não tem, na prática, lugar correspondente no log de produção para essa segunda etapa — porque ela não gera nenhuma linha ali. A única distinção de origem que existe no log real é o código de agente `"S"`/`"C"`/`"P"` (`EventoLogGerard.normalizarAgente`), decidido por qual método é chamado (`registrarUsuario` vs. `registrarComputador`), não por um parâmetro de origem explícito — e nenhum dos dois é chamado para a recomputação automática de consistência.

Não decidi nem alterei nada.
