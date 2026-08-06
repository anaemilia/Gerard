# Diffs propostos — critério SISTEMA/INFERENCIA_COMPUTACIONAL e lacuna de log

Data: 2026-08-05. **Aplicados os dois** — Diff 1 em `REFERENCE.md:193-213`, Diff 2 criando `TAREFA_PENDENTE_LOG_CONSISTENCIA_AUTOMATICA.md`, ambos conferidos linha a linha contra o texto abaixo, sem divergência. Nenhuma recompilação nem harness necessários (só documentação). Nenhum commit, nenhum push.

Base: `RELATORIO_LOG_CONSISTENCIA_AUTOMATICA_2026-08-05.md`.

---

## Diff 1 — `REFERENCE.md` §4.8 (Decisão 1 da fila, Seção 31.1 da Revisão 5)

Inserção logo após a linha 193 ("Um evento não é uma interpretação automática sobre o conhecimento do sujeito."), antes de "### 4.8.1":

```diff
 Um evento não é uma interpretação automática sobre o conhecimento do sujeito.
 
+Critério de desempate entre `SISTEMA` e `INFERENCIA_COMPUTACIONAL` para
+operações automáticas: `SISTEMA` é a reaplicação de uma regra ou equação
+fixa, sem derivar classificação, diagnóstico ou hipótese a partir de
+evidências -- mesmo que a operação seja complexa. `INFERENCIA_COMPUTACIONAL`
+é quando o sistema produz uma classificação, diagnóstico, estimativa ou
+regra derivada de evidências, ainda que por um algoritmo determinístico.
+
+Exemplo concreto do domínio: o preenchimento de um papel quantitativo
+incógnito, da primeira vez, é sempre `USUARIO` -- só o participante pode
+preenchê-lo (`PapelQuantitativo.posicionar`, origem `ORIGEM_USUARIO` por
+padrão). Depois que o diagrama atinge consistência, qualquer recálculo
+automático que o sistema faça para manter posições e representações
+sincronizadas (equação fixa `EstadoFinal = EstadoInicial + Transformação`
+ou equivalente) é `SISTEMA` -- nunca `INFERENCIA_COMPUTACIONAL`, porque não
+deriva julgamento de evidências.
+
+Nota de implementação (`OrigemAcao`, pacote piloto): esse vocabulário hoje
+existe só em `gerard.dominio.campoaditivo`, sem conexão com o log de ações
+de produção (`LoggerInteracaoGerard`/`EventoLogGerard`) -- ver registro
+separado sobre a lacuna de log abaixo.
+
 ### 4.8.1 Mobilização do invariante operatório: sugestão do sistema e atribuição do pesquisador
```

## Diff 2 — arquivo novo `TAREFA_PENDENTE_LOG_CONSISTENCIA_AUTOMATICA.md`

Mesmo padrão estrutural de `TAREFA_PENDENTE_COMPARACAO_MEDIDAS.md`:

```diff
--- /dev/null
+++ TAREFA_PENDENTE_LOG_CONSISTENCIA_AUTOMATICA.md
@@
+# Lacuna registrada — manutenção automática de consistência não gera log em produção
+
+Status: **registrada como lacuna, não como bug.** Sem decisão sobre se deve
+ser corrigida, nem como. Não autorizada a começar.
+
+---
+
+## O achado
+
+A recomputação automática de consistência entre representações — disparada
+quando o diagrama atinge consistência ("azul") e o sistema ajusta
+posições/representações para mantê-las sincronizadas — **não produz
+nenhuma linha no log real de produção**, em nenhuma das três categorias de
+Medidas (Composição, Transformação, Comparação).
+
+## Evidência
+
+Caminho rastreado em `Main.java`:
+- `sincronizarTodasAsRepresentacoesAPartirDoVergnaud(elemento, origem)`
+  (`Main.java:7299-7306`) resolve o valor ausente via
+  `EstadoSemanticoCompartilhado.resolverRelacaoAditiva()`
+  (`EstadoSemanticoCompartilhado.java:149-194`) e aplica o resultado com
+  `aplicarEstadoCompartilhadoEmTodasAsRepresentacoes(snapshot, true)`.
+- `aplicarEstadoCompartilhadoEmTodasAsRepresentacoes` (`Main.java:7202-7239`,
+  corpo lido por completo): só manipula widgets de interface
+  (`definirValorNoElementoNumeroRelativo`, `definirValorNoElementoMedida`,
+  `sincronizarElementosSemanticosDoTexto`, `sincronizarDiagramaVennComRepresentacoes`,
+  `sincronizarEixosComEstadoCompartilhado`) — nenhuma chamada a
+  `loggerInteracaoGerard`, `registrarLogUsuario`, `registrarLogComputador`
+  ou `registrarAcaoGranular`.
+- Esse caminho é único e genérico, usado por 17+ pontos de chamada em
+  `Main.java` (7304, 7312, e via esses dois métodos: 9696, 9824, 10689,
+  10773, 10919, 11308, 11412, 11427, 11890, 11958, 12058, 12164, 12216,
+  12242, 13130) — sem bifurcação por categoria nesse trecho.
+
+Investigação completa: `RELATORIO_LOG_CONSISTENCIA_AUTOMATICA_2026-08-05.md`.
+
+## Consequência prática
+
+A distinção `ORIGEM_USUARIO`/`ORIGEM_SISTEMA`, normatizada em
+`REFERENCE.md §4.8` e ilustrada com este mesmo caso concreto, hoje não tem
+onde ser aplicada na prática para a recomputação automática de
+consistência — porque essa recomputação não gera nenhuma linha de log em
+produção, nem com origem `SISTEMA` nem com nenhuma outra.
+
+## Autorização
+
+Esta lacuna **não está autorizada a ser corrigida agora**. Existe só como
+registro. Nenhuma decisão foi tomada sobre se deve gerar log, com que
+granularidade, ou por qual mecanismo.
```

---

## Status

Aplicados os dois em 2026-08-05. Nenhum commit, nenhum push sem pedido explícito.
