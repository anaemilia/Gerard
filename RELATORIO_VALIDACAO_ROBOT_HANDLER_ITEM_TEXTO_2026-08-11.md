# Validação com harness Robot real — Fase 7.2 (HandlerInteracaoItemTextoArrastavel)

Data: 2026-08-11
Escopo: validar com o harness gráfico real (`TesteMonkeyGuiadoPorCasosReais`, controle de
mouse/teclado via `java.awt.Robot`) que a extração do handler de item textual arrastável
(Fase 7.2) não alterou o comportamento observável, já que esse teste não roda
automaticamente em CI/headless.

## Ambiente

- `ant` não estava no PATH nem em `ANT_HOME`. Localizado o Ant embarcado no plugin Gradle
  do IntelliJ IDEA 2026.2 (`.../plugins/gradle-plugin/lib/ant/{ant.jar,ant-launcher.jar}`),
  invocado via `java -cp ... org.apache.tools.ant.launch.Launcher`. Mesmo mecanismo de
  fallback já usado por `scripts/verificar_regressao_gerard.py`.
- A execução via Git Bash falhou ao montar o classpath de wildcard/`;` (conversão de path
  do MSYS mangla o argumento); refeito em PowerShell nativo, que funcionou sem ajuste.

## Comandos executados

```text
java -cp "<IDEA>/plugins/gradle-plugin/lib/ant/ant-launcher.jar;.../ant.jar" \
     org.apache.tools.ant.launch.Launcher -noinput clean jar
javac -encoding UTF-8 -cp build\classes -d tmp_teste tests\graphical\TesteMonkeyGuiadoPorCasosReais.java
java -cp "build\classes;tmp_teste;lib\weka-stable-3.8.6.jar;lib\bounce-0.18.jar" TesteMonkeyGuiadoPorCasosReais
```

`ant clean jar`: **BUILD SUCCESSFUL** (453 fontes, 4 avisos de `-source 8` obsoleto, sem
erro). `dist/GerardNetBeans_D3_Leitura_Redes_Transicoes.jar` gerado.

Nota: o comando de execução do roteiro original (`java -cp "build\classes;tmp_teste" ...`)
falhou com `NoClassDefFoundError: weka/core/Instance` — `AgenteModelador` depende de
`lib/weka-stable-3.8.6.jar`, que não estava no classpath informado. Foi necessário
acrescentar `lib\weka-stable-3.8.6.jar;lib\bounce-0.18.jar`, replicando o classpath do
alvo `run` de `build.xml`.

## Resultado do harness

Log: `%USERPROFILE%\Gerard\logs\monkey_casos_reais_20260811_133757.log`

```text
Fim: 14 episodio(s) reais rodados via Robot, 45 passo(s) sem divergencia, 0 divergencia(s).
Arquivos reais (perfis_usuario.tsv / diagnosticos_tarefa.tsv) restaurados ao estado anterior ao teste.
```

- **Sem exceção não tratada**: nem no console do processo, nem no log principal, nem no
  log de despacho de `mouseReleased` (`despacho_mouse_released_20260811_133810.log`, 0
  ocorrências de erro/exceção/falha em 145 linhas).
- **Listeners**: `MouseListener=1 MouseMotionListener=1` — confirma que a extração do
  handler não duplicou nem removeu o registro de listeners em `TelaGerard`.
- **14 episódios reais** (catálogo de casos de doutorado: Felipe Wanderley, Jamile,
  Jamilly), cobrindo situações de Transformação (Maria/figurinhas) e Composição
  (Lucas/figurinhas). Backup/restauração de `perfis_usuario.tsv` e
  `diagnosticos_tarefa.tsv` funcionou (linha final do log confirma restauração).

## Comportamento de arraste do item textual (foco da Fase 7.2)

Todos os 14 episódios exercitam o fluxo completo do item textual arrastável, e o
comportamento observado é consistente com o esperado:

1. **Pickup do texto do enunciado**: cada `[passo]` reporta
   `origem=texto_enunciado` — o item numérico é pego a partir do texto, não do diagrama,
   confirmando que o handler extraído ainda distingue a origem do arraste.
2. **Drag até o papel-alvo correto**: os pontos de destino batem com o papel semântico
   esperado por episódio (`papel.estadoInicial`, `papel.transformacao`,
   `papel.estadoFinal`, `papel.parte1`, `papel.parte2`, `papel.todo`), sem nenhum caso de
   soltura fora do alvo nos 45 passos.
3. **Reposicionamento de item já no diagrama**: no episódio "Jamile 09-06-10 (S9)" o
   monkey erra de propósito (arrasta 32 para o papel errado duas vezes) e depois corrige,
   com `origem=item_arrastavel_diagrama` nas tentativas subsequentes — confirma que um
   item textual já solto no diagrama é redetectado corretamente como item arrastável do
   diagrama (não mais como texto do enunciado) em arrastes posteriores.
4. **Arraste da interrogação e digitação**: em 13 dos 14 episódios a interrogação
   (`?`) é arrastada do enunciado até o papel-alvo antes do duplo-clique + digitação do
   valor calculado. No episódio "FelipeWanderley 19-07-10 - Questão 6" o log registra
   explicitamente `interrogacao ja no diagrama para papel.todo, pulando arrasto
   preparatorio` — o harness detecta estado já satisfeito e pula a etapa redundante sem
   erro, indicando que a lógica de idempotência do handler não regrediu.

## Conclusão

Passou sem exceção não tratada. O comportamento de arraste do item textual (pickup,
drag, soltura, incluindo o caso de correção/reposicionamento e o caso de interrogação já
presente) ficou consistente com os 14 episódios reais do catálogo — nenhuma divergência
detectada pelo harness Robot. A extração de `HandlerInteracaoElementoTextoMovel` (Fase
7.2/7.3 em andamento) não introduziu regressão observável neste harness gráfico real.
