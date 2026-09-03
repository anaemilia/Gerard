# Fase 7.8 — estado mecânico do arraste do controle de Comparação

Data: 2026-09-03

## Objetivo

Extrair de `Main.TelaGerard` o único estado mecânico do gesto de arraste do
ponto de controle (ou clique inicial na escala) da barra de Comparação de
Medidas — o booleano solto `arrastandoControleComparacao`, lido e escrito em
seis pontos espalhados por `mousePressed`, `processarMovimentoArraste`,
`mouseReleased`, `paintComponent` (duas vezes) e `existePickupAtivo()`.

## Escopo autorizado e reavaliado nesta mesma sessão

O candidato original desta fase eram os controles de clique de
adicionar/remover unidade do diagrama complementar (Venn). A inspeção do
código antes de escrever qualquer linha mostrou que esses controles não têm
gesto com estado a extrair: a decisão de permissão já está inteiramente
delegada aos proprietários semânticos (`RepresentacaoComUnidadesAdicionaveis`/
`Removiveis`, `PoliticaSinalTransformacaoComplementar`), e a única duplicação
real é uma chamada repetida a `ehAgrupamentoTransformacaoComSinal(...)` — uma
limpeza local, não uma fronteira arquitetural. Criar uma classe ali seria
indireção sem ganho.

O arraste do controle de Comparação, ao contrário, tem a mesma forma dos
protocolos já extraídos (início/movimento/soltura com estado), então a
usuária autorizou trocar o alvo para ele, mantendo o mesmo nível de decisão
por etapa exigido por `gerard-handlers-de-interacao`.

## O que foi extraído — e o que deliberadamente não foi

Diferente de `HandlerInteracaoQuadradinhoVenn` (que rastreia deslocamento e
círculo de origem), este gesto não acumula nenhum estado geométrico: a cada
movimento, `aplicarControleComparacaoPeloMouse(y)` recalcula a proporção e o
valor diretamente da posição vertical do ponteiro e da geometria real do
eixo, e já delegava a conversão de proporção em valor a
`RecalculoComparacaoMedidas`/`RelacaoEstruturalComparacao` antes desta
extração. O único fato mecânico que restava era **estar ou não em curso**.

`HandlerInteracaoControleComparacao` (`gerard.interacao.arraste`) concentra
esse fato: `iniciar()`, `estaAtivo()`, `concluir()`. Nada mais.

Fora do escopo, permanecem em `Main`:

- hit-testing do controle e da escala (`Rectangle`, geometria da cena);
- a conversão de posição em valor (já delegada ao domínio, acima);
- a confirmação/sincronização semântica ao soltar o mouse, incluindo a
  consulta a `confirmarValorIncognitaAceito` sobre o papel da diferença — o
  handler não sabe que esse papel existe.

## Localidade das responsabilidades

- `Main` não guarda mais o booleano diretamente; consulta
  `handlerControleComparacao.estaAtivo()` nos seis pontos originais
  (`mousePressed`, `processarMovimentoArraste`, `mouseReleased`,
  `paintComponent` × 2, `existePickupAtivo()`), que já combinava os
  `estaAtivo()` de todos os outros handlers de interação — este era o único
  booleano solto na mesma lista.
- O throttling do log `CONSISTENCIA_AUTOMATICA` durante o arraste contínuo
  (`registrarLogConsistenciaAutomaticaSeHouve`/
  `flushLogConsistenciaAutomaticaPendenteDoArrasteComparacao`) não mudou de
  dono — continua em `Main`, só a condição de guarda passou a consultar o
  handler.

## Verificação

- compilação completa (`javac` direto, 556 fontes): sem erros;
- linha de base Windows: 113 testes executáveis aprovados (112 anteriores +
  `TesteHandlerInteracaoControleComparacao`, novo), 5 gráficos
  compilados/não executados por exigirem display, zero reprovações;
- verificador estrutural completo: `APROVADO`, nenhuma falha;
- ratchet dos protocolos: inalterado (`mousePressed` 401, `mouseMoved` 205,
  `processarMovimentoArraste` 60, `mouseReleased` 103 — a extração trocou
  leitura/escrita de campo por chamada de método, sem remover linhas).

### Sobre o harness Robot

`TesteMonkeySemiGuiado` (seed `20260903`, 90s) rodou com autorização
explícita da usuária e terminou com **85 iterações, 0 erros** — mas, lendo o
próprio harness, suas ações sorteadas são limitadas a arrastar itens
textuais, digitar valor numa incógnita, trocar categoria e pedir nova
situação-problema; ele **não** dirige o ponto de controle nem a escala da
barra de Comparação. O run de 0 erros é evidência de não-regressão geral,
não uma validação específica deste protocolo. Por isso este relatório
também depende de `TesteHandlerInteracaoControleComparacao`, que cobre as
transições de estado do handler isoladamente (inativo → ativo → concluído,
`concluir()` idempotente sem gesto ativo — o mesmo reset defensivo usado no
início de `mousePressed`). Estender o harness Robot para dirigir este
controle especificamente fica registrado como lacuna, não decidido aqui.

## Resultado

`Main` deixou de manter o booleano do gesto do controle de Comparação como
campo solto; a lista de `estaAtivo()` que já unificava os demais handlers
agora está completa. O ganho é mais de consistência arquitetural (mais um
protocolo saiu do compositor) do que de redução de linhas — este gesto já
era, na prática, majoritariamente delegado ao domínio antes desta extração.
