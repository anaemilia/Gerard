# Relatório da Fase 7.10 — controles +/− do material concreto

Autorização: escolha explícita da usuária em 2026-09-28 ("Controles +/−").
Numeração: "Fase 7.9" já nomeia no verificador a porta de registro da
atividade web; por isso esta fase é a 7.10.

## Escopo

Somente o protocolo de clique nos controles de adicionar/remover unidades,
que estava inline em `Main.TelaGerard.mousePressed` (111 linhas). Hover
(`mouseMoved`), desenho dos controles e demais protocolos não foram tocados.

## Matriz de localidade

| Conhecimento ou efeito | Proprietário autorizado | Entrada | Saída | Consumidor | Tecnologia permitida |
|---|---|---|---|---|---|
| Prioridade remover > adicionar; ordem liberação → limite → aplicação | `HandlerInteracaoControlesUnidades` | coordenadas neutras + porta | `ResultadoControleUnidades` | apresentação da tela | sem Swing/AWT |
| Hit-test dos controles | adaptador desktop (hit-test já existente da tela) | coordenadas | agrupamento opaco | handler pela porta | AWT só no adaptador |
| Liberação pela modelagem | política existente (`condicaoEdicaoAposInicioVergnaud` + `politicaInteracaoRepresentacoes`) | — | booleano | handler pela porta | sem UI |
| Limite de quantidade | `RepresentacaoComUnidades*`; valor assinado: simulador do estado semântico | agrupamento | booleano | handler pela porta | sem UI |
| Aplicação | representação editável / `alterarValorAssinadoTransformacao` | agrupamento | `ResultadoAplicacaoControleUnidade` (fato) | handler (repassa sem recalcular) | — |
| Anotação, cursor, foco, feedback de limite, log granular legado, reavaliação da conclusão | `apresentarResultadoControleUnidades` (Swing) | desfecho já decidido | efeitos visuais/log | usuário | Swing permitido |

O handler e a porta não importam AWT, Swing, `CirculoVenn`, localização,
logger nem scaffolding (verificado pelo script). A apresentação não decide
liberação, limite ou aplicação: só traduz o desfecho, preservando as
assimetrias existentes (bloqueio de *adicionar* grava log granular, o de
*remover* não; limite de *remover* usa anotação, o de *adicionar* usa
`registrarLimiteQuantidadeAtingido`).

## Arquivos

- novos: `src/gerard/interacao/unidades/{OperacaoControleUnidade,
  AlvoControlesUnidades, ResultadoAplicacaoControleUnidade,
  ResultadoControleUnidades, HandlerInteracaoControlesUnidades}.java`;
  `tests/java/TesteHandlerInteracaoControlesUnidades.java`;
  `tests/graphical/TesteRobotControlesUnidades.java`;
- alterados: `src/Main.java` (composição do handler + adaptador desktop,
  método de apresentação, roteamento de 6 linhas em `mousePressed`);
  `scripts/verificar_regressao_gerard.py` (ratchet e bloco "Fase 7.10").

## Verificações executadas (VM Linux local, OpenJDK 11, Xvfb)

- compilação: 606 fontes, sem erro;
- testes Java automáticos: **144/144** aprovados (inclui o novo teste do
  handler: prioridade, bloqueio antes do limite, limite impede aplicação,
  repasse do fato, erro sem fato);
- `verificar_regressao_gerard.py`: 276 OK; única falha é ambiental
  ("Ant não encontrado"); ratchet de `mousePressed` **350 → 245** linhas;
- `testar_adicao_quadradinhos_venn.sh` e `testar_remocao_quadradinhos_venn.sh`: OK;
- grafo de skills válido (22 nós, 90 relações); checker de localidade:
  aprovado, sem dívida nova (continuam as 3 ligações web já registradas);
- **Robot A/B de equivalência**: o mesmo harness, com as mesmas situações
  curadas, rodado sobre o build anterior (A) e o posterior (B). Em cada
  categoria: modelagem real por arraste, três valores errados na incógnita
  até a escalada que revela o material concreto, depois +2/−3 e + até o
  limite em cada agrupamento; após cada clique registra unidades por
  agrupamento, valor de referência, anotação, limite questionado, foco,
  cursor e contagem de logs. **Traços idênticos** em Composição de medidas
  (29 linhas), Transformação de medidas (30), Composição de transformações
  (32) e Comparação de medidas (22); zero exceções; `MouseListener` único.
- regressão cruzada: o Robot da Fase 7.8 rodado no build novo: 36/36.

Evidências: `documentacao/relatorios/evidencias/robot_fase_7_10_controles_unidades_20260929/`
(`comparacao_AB.tsv`, traços, logs, capturas, `SHA256SUMS.txt`).

## Achados (não alterados)

1. **Ramo "bloqueado pela modelagem" inalcançável pela interface atual.** O
   material concreto só é exibido depois da escalada da ajuda (limite de
   tentativas da incógnita, `SeletorRepresentacaoComplementar.deveExibir`),
   que exige Vergnaud já iniciado; por isso o hit-test nunca encontra
   controle antes disso. O ramo foi preservado e está coberto pelo teste
   unitário, mas pode ser código morto, como ocorreu na Fase 7.6. Decisão
   da usuária antes de remover.
2. O log granular do bloqueio de adição continua no rastreador legado, não
   no registro factual de ação instrumental — dívida registrada, fora do
   escopo.
3. Em Composição de transformações com operações curadas (bolas_509261012),
   três valores errados na incógnita não levaram à escalada (sem diálogo);
   provavelmente a edição depende da escolha de operação/sinal antes. Não
   investigado.

## Conclusão

`mousePressed` passa a só compor e rotear também este protocolo. A
equivalência comportamental foi demonstrada por interação Robot real nas
quatro categorias com material concreto manipulável. Validação feita em
Linux/Xvfb; repetição no Windows recomendada para a parte visual.
