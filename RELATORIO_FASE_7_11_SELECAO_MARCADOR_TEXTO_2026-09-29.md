# Relatório — Fase 7.11: seleção do marcador do enunciado

**Data:** 2026-09-29  
**Escopo autorizado:** primeiro dos três recortes pendentes de
`Main.TelaGerard.mousePressed`.

## Localidade registrada antes da edição

| Aspecto | Decisão aplicada |
|---|---|
| Conhecimento/efeito | Sequência do pressionamento sobre um marcador matemático do enunciado |
| Proprietário | `HandlerInteracaoSelecaoMarcadorTexto`, de escopo fechado |
| Entrada | Marcador encontrado pela geometria real e fotografia dos itens arrastáveis |
| Saída | Resultado tipado: já posicionado, sem proxy ou seleção iniciada |
| Portas/colaboradores | `PoliticaUnicidadeElementoMatematicoTexto` e `SessaoArrasteTextoParaDiagrama` |
| Adaptador tecnológico | `TelaGerard`: hit-test, tooltip, logs, Modelador, foco, cursor, fantasma e repaint |

O handler não interpreta C/E, não conhece Swing/AWT, logger, Modelador,
scaffolding nem cursor. A seleção continua neutra porque ainda não existe
papel-alvo. A origem textual não é removida: a sessão cria a mesma cópia
representacional já usada antes da extração.

## Alteração

- Criados `HandlerInteracaoSelecaoMarcadorTexto` e
  `ResultadoSelecaoMarcadorTexto`.
- `mousePressed` passou a rotear o recorte por
  `processarSelecaoMarcadorTexto` e pelo handler.
- A mecânica portátil preserva a ordem: ausência → unicidade → abertura da
  sessão por cópia.
- O ratchet de `mousePressed` foi reduzido de 245 para 197 linhas.
- Criado `TesteHandlerInteracaoSelecaoMarcadorTexto` e acrescentadas quatro
  verificações estruturais ao verificador geral.

## Evidências

- Grafo de 22 skills e 90 relações: válido antes da alteração.
- `ant clean jar`: aprovado, 615 fontes compiladas.
- Teste dirigido: aprovado para clique ausente, ocorrência já posicionada e
  cópia não destrutiva.
- Robot dirigido no Windows: aprovado com pressionamento real, origem textual
  semanticamente preservada e segunda ocorrência bloqueada com o aviso já
  existente.
- Verificador de localidade: aprovado, sem nova dívida.
- Verificador geral: as verificações da Fase 7.11 foram aprovadas e permaneceram
  somente as duas falhas preexistentes da frente de conclusão em `Main`.
  Durante a implementação, o único desvio novo encontrado foi o limite
  intermediário do ratchet (198 contra 197 linhas), corrigido para o valor
  real.

## Incidentes descartados como evidência

As duas primeiras tentativas do novo Robot falharam no próprio harness: uma
sem Weka no classpath e outra por comparar identidade Java de um marcador que
é regenerado durante o repaint. Depois de corrigido para comparar a identidade
semântica observável, a execução válida terminou com código zero. Nenhuma das
duas tentativas inválidas foi apresentada como evidência do produto.
