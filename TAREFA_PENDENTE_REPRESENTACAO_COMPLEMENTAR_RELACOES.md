# Tarefa pendente — representação complementar própria para as categorias de Relações

Status: **não iniciada**. Registrada a pedido da usuária (2026-08-08), que
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

## O que foi feito (2026-08-08)

`SeletorRepresentacaoComplementar.deveExibir(...)` passou a checar também
`possuiRepresentacaoDefinida(tipo)` — verdadeiro só quando `selecionar(tipo,
false)` não é `GENERICA`. Como isso reaproveita o mesmo mecanismo já
existente de "esconder o diagrama complementar e dar a largura toda ao
diagrama de Vergnaud" (usado sempre que `deveExibirDiagramaComplementar()`
é falso, por qualquer motivo — ver `Main.obterAreasDiagramasProporcionais()`),
nenhuma mudança de layout foi necessária: o diagrama de Vergnaud já
assume a largura toda automaticamente quando não há diagrama complementar
a mostrar.

Escopo da mudança: só a decisão de exibir ou não. Nenhuma representação
nova foi desenhada — nem foi pedido. `TipoRepresentacaoComplementar.GENERICA`
e o código de desenho do diagrama genérico continuam existindo (podem
voltar a ser usados por outra categoria no futuro, ou substituídos por
uma representação própria para Relações).

Verificado sob Xvfb: `deveExibirDiagramaComplementar()` retorna `false`
para `TRANSFORMACAO_RELACAO`/`COMPOSICAO_RELACOES` (diagrama de Vergnaud
em largura total, nada ao lado) e continua `true` para as 4 categorias com
representação própria (`TRANSFORMACAO_MEDIDAS`, `COMPOSICAO_MEDIDAS`,
`COMPARACAO_MEDIDAS`, `COMPOSICAO_TRANSFORMACOES`) — sem regressão nos
widgets já existentes (processo de transformação, coleções, barras, funil
de composição de transformações).

## O que falta (não decidido, não autorizado)

Se e quando fizer sentido pedagogicamente, desenhar uma representação
complementar própria para `TRANSFORMACAO_RELACAO` e `COMPOSICAO_RELACOES`
— números relativos (podem ser negativos), diferente das medidas
absolutas que os widgets atuais (quadradinhos, barras, funil) já
representam. Não é uma decisão técnica isolada: exige entender o que
"material concreto" significa para uma relação/número relativo antes de
qualquer código, seguindo o mesmo processo já usado para outras decisões
de scaffolding neste projeto (perguntas de confirmação com a usuária
antes de codificar — ver `RELATORIO_AG_AE_DICA_POSICIONAMENTO_2026-08-08.md`
como exemplo do processo).

## Arquivo alterado

- `src/gerard/campoaditivo/representacao/SeletorRepresentacaoComplementar.java`
