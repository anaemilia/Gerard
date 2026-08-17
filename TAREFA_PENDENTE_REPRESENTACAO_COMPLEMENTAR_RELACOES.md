# Tarefa pendente — representação complementar própria para as categorias de Relações

Status: **parcialmente decidida (2026-08-16); desenho concreto ainda não
iniciado.** Registrada a pedido da usuária (2026-08-08), que
pediu para remover o diagrama complementar genérico dessas categorias
"por enquanto" — não pediu (nem autorizou) o desenho da representação
definitiva.

## Contexto

`TRANSFORMACAO_RELACAO` e `COMPOSICAO_RELACAO` — as duas categorias
"Relações" que não são `COMPOSICAO_TRANSFORMACOES` — nunca tiveram uma
representação complementar própria. `SeletorRepresentacaoComplementar.
selecionar(tipo, cenaComposta)` sempre as mapeou para o fallback
`TipoRepresentacaoComplementar.GENERICA`: círculos vazios ligados por
setas, sem quadradinhos, sem controles de adicionar/remover, sem nenhum
conteúdo manipulável — só uma decoração estática reaproveitando o
desenho genérico de círculos+setas do antigo diagrama de Venn.

Ao sortear uma situação de Composição de relações (captura de tela da
usuária, 2026-08-08), esse diagrama genérico aparecia ao lado do diagrama
de Vergnaud, ocupando metade da tela sem nenhuma função. A usuária pediu:
"tire esse diagrama do lado do diagrama de Vergnaud. Por enquanto deixe
sem nada e gere uma pendência na documentação."

## O que foi feito (2026-08-08, corrigido no mesmo dia)

Primeira tentativa (revertida): `SeletorRepresentacaoComplementar.deveExibir(...)`
passou a checar também `possuiRepresentacaoDefinida(tipo)`, o que fazia
`Main.deveExibirDiagramaComplementar()` retornar `false` para as categorias
de Relações e, por consequência, `Main.obterAreasDiagramasProporcionais()`
dava a largura toda ao diagrama de Vergnaud. A usuária corrigiu: **"o de
Vergnaud ocupa a largura toda. Não, deixe-o na mesma posição. Não mexa
no diagrama de Vergnaud."** — ela só queria o conteúdo do painel ao lado
removido, não o Vergnaud redimensionado.

Correção aplicada: revertido `SeletorRepresentacaoComplementar.deveExibir(...)`
para a forma original (sem checar `GENERICA`) — `deveExibirDiagramaComplementar()`
e a área do diagrama de Vergnaud voltam a ser calculados exatamente como
antes, para todas as categorias, sem exceção. A supressão do conteúdo do
fallback `GENERICA` foi movida para um ponto mais estreito, só de
desenho/interação, em `Main.java`:

- Novo método `ehRepresentacaoComplementarGenerica()` (mesmo padrão de
  `ehProcessoTransformacaoMedidas()`/`ehComposicaoTransformacoesProcesso()`).
- `desenharDiagramaVenn(Graphics2D)`: quando `ehRepresentacaoComplementarGenerica()`
  é verdadeiro, pula o card, o título, as setas, os círculos, os
  quadradinhos e os controles +/-, e esconde explicitamente
  `botaoAjudaComplementar` — mas SEM alterar o gate do topo
  (`deveExibirDiagramaComplementar()`), que continua controlando só se o
  método roda ou não. `sincronizarDiagramaVennComRepresentacoes(...)`
  continua rodando normalmente (mantém o estado sincronizado, só a pintura
  é suprimida).
- `encontrarRepresentacaoPeloControleAdicionarQuadradinho`/
  `encontrarRepresentacaoPeloControleRemoverQuadradinho`: também retornam
  `null` quando `ehRepresentacaoComplementarGenerica()`, evitando cliques
  em áreas de controle que não são mais desenhadas.
- `obterRepresentacoesAtuaisParaRelatoBug()`: não lista mais "Diagrama de
  Venn" como representação disponível para as categorias de Relações.

Escopo da mudança: só o que é desenhado/clicável no painel complementar.
Nenhuma representação nova foi desenhada — nem foi pedido.
`TipoRepresentacaoComplementar.GENERICA` e o código de desenho do diagrama
genérico continuam existindo (podem voltar a ser usados por outra
categoria no futuro, ou substituídos por uma representação própria para
Relações).

Verificado sob Xvfb + reflection: `obterAreaVisivelDiagramasVergnaud()`
devolve exatamente o mesmo `Rectangle` (`x=15,y=345,width=615,height=339`
na resolução testada) para `COMPOSICAO_RELACOES`, `TRANSFORMACAO_RELACAO`,
`TRANSFORMACAO_MEDIDAS`, `COMPOSICAO_MEDIDAS`, `COMPARACAO_MEDIDAS` e
`COMPOSICAO_TRANSFORMACOES` — o diagrama de Vergnaud não muda de posição
nem de tamanho em nenhum caso. `botaoAjudaComplementar` fica invisível só
para as duas categorias de Relações, visível nas outras 4. Capturas de
tela confirmam: painel direito em branco para Composição de relações,
widgets (barras de comparação, funil de composição de transformações etc.)
intactos nas demais categorias — sem regressão.

## Decisão parcial (2026-08-16)

A usuária decidiu uma das perguntas em aberto: a futura representação
complementar de Relações **deve suportar valores negativos** — ao
contrário das medidas absolutas (sempre positivas) já representadas pelos
outros widgets (quadradinhos, barras, funil), uma relação/número relativo
é um inteiro que pode ser positivo ou negativo, e qualquer material
concreto desenhado para essa categoria precisa expressar as duas
possibilidades, não só a magnitude.

Isso é só uma restrição de design, não uma especificação completa. Não
altera nenhum código: não há ainda decisão sobre a forma concreta do
material (o que exatamente é desenhado, como o sinal é indicado
visualmente, se reaproveita algum widget existente ou é um desenho novo).

## O que falta (não decidido, não autorizado)

Se e quando fizer sentido pedagogicamente, desenhar uma representação
complementar própria para `TRANSFORMACAO_RELACAO` e `COMPOSICAO_RELACOES`,
já sabendo que precisa suportar negativos (decisão acima). O restante do
desenho concreto continua exigindo decisão pedagógica antes de qualquer
código, seguindo o mesmo processo já usado para outras decisões de
scaffolding neste projeto (perguntas de confirmação com a usuária antes de
codificar — ver `RELATORIO_AG_AE_DICA_POSICIONAMENTO_2026-08-08.md` como
exemplo do processo).

## Arquivos alterados

- `src/gerard/campoaditivo/representacao/SeletorRepresentacaoComplementar.java`
  (revertido para a forma original)
- `src/Main.java` (`ehRepresentacaoComplementarGenerica()`,
  `desenharDiagramaVenn`, `encontrarRepresentacaoPeloControleAdicionarQuadradinho`,
  `encontrarRepresentacaoPeloControleRemoverQuadradinho`,
  `obterRepresentacoesAtuaisParaRelatoBug`)
