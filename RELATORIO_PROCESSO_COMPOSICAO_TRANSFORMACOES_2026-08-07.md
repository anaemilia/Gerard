# Relatório — Processo de Transformação para Composição de Transformações (2026-08-07)

Pedido da usuária: substituir o diagrama de Venn (círculos + duas setas
convergindo) do painel complementar de **Composição de Transformações**
(`TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES`) pelo mesmo mecanismo
visual e interativo já usado em **Transformação de Medidas** — funil com
quadradinhos arrastáveis + controle de sinal (+/-) — mantendo a arquitetura
já estabelecida nas skills do projeto.

## Diagnóstico antes de implementar

O painel complementar já tem o gancho certo para isso:
`SeletorRepresentacaoComplementar.selecionar(tipo, cenaComposta)` decide a
representação por categoria (`TipoRepresentacaoComplementar`). Antes desta
mudança, `COMPOSICAO_TRANSFORMACOES` caía no caso `GENERICA` (círculos).

O widget de funil da Transformação de Medidas, porém, não é uma forma
decorativa: é uma pilha de 12 classes (`gerard.campoaditivo.transformacao.processo`)
com geometria (`GeometriaProcessoTransformacao`), estado
(`EstadoProcessoTransformacao`), distribuição de quadradinhos dentro do
funil trapezoidal (`LayoutUnidadesProcessoTransformacao`), controle de
sinal (`ControleSinalProcessoTransformacao`) e sincronização
(`SincronizadorUnidadesProcessoTransformacao`) — tudo hoje fixo em
exatamente 3 zonas, onde só a do meio é funil (as outras duas são caixas
de "estado", sem sinal). Composição de Transformações não tem "estado"
nenhum — são 3 transformações (números relativos, `TransformacaoFinal =
Transformacao1 + Transformacao2`, ver `RelacaoEstruturalComposicaoDeTransformacoes`),
então as 3 zonas precisam ser funil, não 2 caixas + 1 funil. Reaproveitar
o widget exigia um widget novo, análogo, não um ajuste pequeno — a usuária
confirmou esse escopo (paridade completa) antes de começar.

## O que foi implementado

Pacote novo `gerard.campoaditivo.transformacao.composicao`, isolado do
widget original (zero risco de regressão em Transformação de Medidas, que
continua em produção real, inalterada):

- `EstadoComposicaoTransformacoes` — análogo a `EstadoProcessoTransformacao`,
  mas os 3 índices usam `DominioNumerico.INTEIROS` (todos assinados,
  refletindo `RelacaoEstruturalComposicaoDeTransformacoes`), não a mistura
  NATURAIS/INTEIROS do processo simples.
- `GeometriaComposicaoTransformacoes` — canal único + 3 funis independentes
  (cada um pode ser inserção ou retirada de forma independente — cada
  papel tem seu próprio sinal, diferente do "tipo de processo" único da
  Transformação de Medidas).
- `LayoutComposicaoTransformacoes` — posiciona as 3 zonas (Transformação
  1, Transformação 2, Transformação Final) ao longo do canal.
- `RenderizadorComposicaoTransformacoesProcesso` — desenha cabeçalho, as 3
  zonas (todas tratadas como zona "processo") e o canal.
- `ControleSinalComposicaoTransformacoes` — controle de sinal +/-,
  parametrizado pelo índice do funil (0/1/2), diferente do original que
  assume um único funil implícito.
- `LayoutUnidadesComposicaoTransformacoes` — distribuição trapezoidal de
  quadradinhos dentro de cada funil, sem o ramo de "caixa de estado" (não
  existe aqui).
- `SincronizadorUnidadesComposicaoTransformacoes` — monta o
  `PlanoUnidadesProcessoTransformacao` (classe já genérica, reaproveitada
  sem cópia — só o cálculo que a alimenta é novo).
- `TipoRepresentacaoComplementar.PROCESSO_COMPOSICAO_TRANSFORMACOES` (novo
  valor) e `SeletorRepresentacaoComplementar` ligado para
  `COMPOSICAO_TRANSFORMACOES`.
- `GeradorCenaDiagramaVenn`: o case `COMPOSICAO_TRANSFORMACOES` (antes:
  círculos + 2 setas) agora delega para `LayoutComposicaoTransformacoes`.
- `Main.java`: nova gate `ehComposicaoTransformacoesProcesso()` (espelha
  `ehProcessoTransformacaoMedidas()`), roteamento em `desenharDiagramaVenn`,
  e extensão da interação de mouse (sinal +/-, quadradinhos) para os 3
  funis via um conjunto de métodos de despacho novos
  (`obterAreaControleSinalAdicionar`, `controleSinalContemAdicionar/Remover`,
  `desenharControleSinalAdicionar/Remover`) que decidem, num único ponto,
  entre o widget de Transformação de Medidas (funil implícito) e o de
  Composição de Transformações (funil indexado) — evitando duplicar a
  ramificação em cada um dos 5 pontos de desenho/hit-test.
- i18n: `ui.transformationCompositionBoard.title`/`.description` nos 4
  idiomas (pt/en/es/fr).

## Bug real encontrado e corrigido durante a verificação

A lógica de "valor com sinal" (`ehAgrupamentoTransformacaoComSinal`,
`simularEstadoCompartilhadoAposAlteracaoValorAssinado`,
`SimuladorEstadoComplementarVenn.respeitaLimites`/`simular`) já era
genérica por índice — bastou trocar `ehProcessoTransformacaoMedidas()` por
`(ehProcessoTransformacaoMedidas() || ehComposicaoTransformacoesProcesso())`
em cada um desses pontos. Mas um quarto ponto, o que de fato **persiste**
o valor no estado semântico compartilhado após uma interação real
(`capturarEstadoCompartilhadoDoDiagramaComplementar`), tinha a mesma
checagem e foi esquecido na primeira passada. Sem essa correção, valores
negativos digitados/clicados em qualquer um dos 3 funis eram salvos como
se fossem positivos (o sinal se perdia na persistência, não só na
simulação) — descoberto pelo teste real sob Xvfb ao tentar decrementar
Transformação 1 de 5 até -4: o valor ficava preso oscilando perto de 0 em
vez de continuar caindo. Corrigido com a mesma extensão de gate.

## Verificação

1. Compilação completa (436+ arquivos, 0 erros).
2. Harnesses existentes (`TestePilotoTentativasRejeitadas`,
   `TesteComparativoEstadoSemanticoCompartilhado` 40/40,
   `TestePilotoComposicaoMedidas`) — sem regressão.
3. Teste real sob Xvfb (`TesteTemporarioComposicaoTransformacoesProcesso.java`,
   deletado após a verificação, nunca commitado), contra a aplicação real,
   via reflection nos mesmos métodos privados que uma interação real
   dispara (nunca reimplementando a lógica):
   - Sorteio real até cair em `COMPOSICAO_TRANSFORMACOES`, confirmação de
     categoria, reconstrução do diagrama complementar.
   - As 3 zonas são retangulares e mostram quadradinhos (funil, não
     círculo) — confirma o roteamento para o novo renderizador.
   - `desenharDiagramaVenn` real, com `Graphics2D` de verdade
     (`BufferedImage`), não lança exceção.
   - `alterarValorAssinadoTransformacao` (mesmo método que um clique real
     no controle de sinal dispara) usado para incrementar Transformação 1
     até 5 e Transformação 2 até 3 — `TransformacaoFinal` é recalculada
     automaticamente para 8, via o mesmo mecanismo de consistência já
     usado no resto do projeto (`resolverRelacaoAditiva`).
   - Decremento de Transformação 1 de 5 até -4 (9 decrementos) — valor
     atravessa zero corretamente, `TransformacaoFinal` recalculada para
     -1; `desenharDiagramaVenn` continua sem exceção com um funil em
     retirada.
   - Smoke test final: sorteio real até cair em `TRANSFORMACAO_MEDIDAS`
     confirma que o widget original continua com
     `ehProcessoTransformacaoMedidas() == true`,
     `ehComposicaoTransformacoesProcesso() == false`, e desenha sem
     exceção — nenhuma regressão no widget em produção real.

Todos os 18 checks passaram.

## Observação de escopo

Não alterado: `COMPOSICAO_TRANSFORMACAO_MEDIDAS` ("Composição seguida de
transformação", enum distinto de `COMPOSICAO_TRANSFORMACOES`) e as demais
categorias de "Relações" (`TRANSFORMACAO_RELACAO`, `COMPOSICAO_RELACOES`)
continuam com a representação `GENERICA` (círculos) — o pedido foi
especificamente para `COMPOSICAO_TRANSFORMACOES` ("Composição de
transformações").
