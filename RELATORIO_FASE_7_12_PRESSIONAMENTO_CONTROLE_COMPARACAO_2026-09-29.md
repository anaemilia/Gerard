# Fase 7.12 — Pressionamento da barra de Comparação (2026-09-29)

## Autorização

Recorte proposto com diff prévio e autorizado explicitamente pela usuária em
2026-09-29 ("sim"). Somente o protocolo de início/bloqueio da barra de
Comparação de Medidas em `Main.mousePressed` foi alterado.

## Trecho extraído

Os dois `if` consecutivos de `mousePressed` (após os controles de unidades):
bloqueio quando o clique cai no ponto de controle ou na escala e a interação
com as representações ainda não foi liberada; início do gesto quando cai no
mesmo alvo e está liberada.

## Matriz de localidade

| Conhecimento ou efeito | Proprietário | Entrada → saída | Porta | Adaptador |
|---|---|---|---|---|
| Sequência do pressionamento (alvo → liberação → início) | `HandlerInteracaoControleComparacao.pressionar` | x, y, liberado → `ResultadoPressionamentoControleComparacao` (`NAO_CONSUMIDO`, `BLOQUEADO`, `INICIADO`) | — | — |
| Estado "gesto em curso" | `HandlerInteracaoControleComparacao` (inalterado) | — | — | — |
| Hit-test do ponto de controle e da escala | tela (geometria concreta) | x, y → boolean | `AlvoControleComparacao` | classe anônima `adaptadorControleComparacao` em `TelaGerard` |
| Liberação da manipulação | `PoliticaInteracaoRepresentacoes` (inalterada) | → boolean | booleano passado pela tela (mesmo padrão de `HandlerInteracaoPaineisEixosRelacoes`) | — |
| Aviso de bloqueio e registro granular; fantasma, cursor, valor, arraste elástico, repaint | tela (inalterados) | desfecho → efeitos Swing | — | `apresentarPressionamentoControleComparacao` |

Nenhum controlador, serviço ou coordenador foi criado.

## Arquivos

- Novos: `src/gerard/interacao/arraste/AlvoControleComparacao.java`,
  `src/gerard/interacao/arraste/ResultadoPressionamentoControleComparacao.java`,
  `tests/java/TesteHandlerInteracaoPressionamentoControleComparacao.java`,
  `tests/graphical/TesteRobotControleComparacao.java`.
- Alterados: `HandlerInteracaoControleComparacao.java` (método `pressionar` e
  Javadoc), `Main.java` (porta anônima, apresentador, roteamento em
  `mousePressed`), `scripts/verificar_regressao_gerard.py` (ratchet e bloco
  "Fase 7.12").

## Comportamento preservado

Textos, chave do aviso, artefato do registro granular ("Controle do gráfico
de barras"), posição do trecho em `mousePressed`, geometria, efeitos visuais
e sincronização. Diferenças de execução verificadas como neutras: o hit-test
é avaliado uma vez (antes, duas quando liberado; o único efeito é a
inicialização idempotente da proporção); `iniciar()` ocorre antes do
fantasma (que não lê o estado do handler); a liberação é consultada mesmo
fora do alvo (leitura pura).

## Validação

- Build: 617 fontes, sem erros (javac, VM Linux).
- Teste dirigido `TesteHandlerInteracaoPressionamentoControleComparacao`:
  três desfechos, ordem alvo→liberação, hit-test único e estado só no início.
  `TesteHandlerInteracaoControleComparacao` (Fase 7.8) continua aprovado.
- Suíte Java: 151/151 aprovados.
- `verificar_regressao_gerard.py`: bloco "Fase 7.12" 6/6; ratchet de
  `mousePressed` 197 → 188. Única falha: Ant ausente na VM (ambiental). As
  duas falhas de campo-espelho da frente de conclusão, registradas no
  Windows, não apareceram nesta execução na VM e não pertencem a este
  recorte.
- `verificar_localidade_arquitetural.py`: aprovado (três dívidas web de
  linha de base inalteradas). Grafo de skills válido (22 nós, 90 relações).
- Robot real, serial, Linux/Xvfb 1600×1000, A/B (build anterior × Fase
  7.12) nas mesmas situações, traços idênticos:
  - `PO_COMPARACAO_MEDIDAS_bolas_487868670`: A 0 falhas, B 0 falhas;
  - `PO_COMPARACAO_MEDIDAS_dinheiro_1527166754`: A 0 falhas, B 0 falhas.
  Verifica: bloqueio grava exatamente um registro granular, mostra o aviso,
  não inicia o gesto nem altera a proporção; após o primeiro posicionamento
  por arraste real, pressionar inicia o gesto, arrastar muda a proporção,
  soltar encerra e não há novo registro de bloqueio.
  Evidências: `documentacao/relatorios/evidencias/robot_controle_comparacao_20260929/`.

### Tentativas descartadas (não são evidência)

Durante a construção do harness houve execuções inválidas, todas por defeito
do próprio Robot e reproduzidas também no build anterior: arraste em sentido
fixo que prendia a proporção no limite (0,0 ou 1,0); checagem do estado 150 ms
após o clique, antes de o EDT processá-lo (corrigida com `waitForIdle`);
situações diferentes entre A e B (corrigido com situação exigida por
argumento); e uma execução A cortada por `timeout`
(`INVALIDO_timeout_run03_…`, mantida e marcada como inválida).

## Pendente

- Repetir o Robot no Windows (validação feita em Linux/Xvfb).
- Próximo recorte, com autorização própria: lupa dos painéis de Relações.
