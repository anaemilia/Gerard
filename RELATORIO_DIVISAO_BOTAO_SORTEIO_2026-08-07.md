# Relatório: botão Sortear dividido em dois — Medidas e Relações

Data: 2026-08-07

## Contexto

Pedido direto da usuária, apontando o cabeçalho da aba Diagramar:
"divida em dois botões de sorteio: sorteio de medidas e sorteio de
relações". Antes, um único botão de dado (🎲) sorteava entre as 6
categorias de uma vez (`CATEGORIAS_SORTEIO_LIVRE`). Agora são dois
botões lado a lado, cada um restrito ao seu grupo.

## O que mudou

### `Main.java`

- `CATEGORIAS_SORTEIO_LIVRE` (6 tipos) foi dividida em
  `CATEGORIAS_SORTEIO_MEDIDAS` (3) e `CATEGORIAS_SORTEIO_RELACOES` (3).
  `CATEGORIAS_SORTEIO_LIVRE` continua existindo, como união das duas — só
  o item de menu Arquivo > Nova situação-problema ainda usa ela (sorteia
  entre as 6, comportamento inalterado).
- `sortearNovaSituacao()` (menu) virou uma casca fina sobre um método
  novo, `sortearDentroDoGrupo(grupo, descricaoElemento)`, que faz o
  log de interação + `iniciarQuizCategoria`. Dois métodos novos,
  `sortearSituacaoMedidas()` e `sortearSituacaoRelacoes()`, chamam o
  mesmo método comum com o grupo restrito — nenhuma duplicação de lógica.
- `botaoFerramentaSortear` (campo único) virou dois campos:
  `botaoFerramentaSortearMedidas` e `botaoFerramentaSortearRelacoes`,
  criados em `criarBotoesCabecalhoEmbutidos`, um do lado do outro
  (bounds 58,8 e 100,8), cada um ligado ao seu método de sorteio e com
  tooltip próprio.
- Ícone: `criarIconeFerramentaSortear()` passou a receber uma letra
  ('M' ou 'R') e desenha um pequeno distintivo circular no canto do dado
  com essa letra — mesmo traço fino neutro dos outros ícones, sem
  inventar um símbolo novo (o dado continua sendo "sortear"; a letra só
  diferencia qual grupo). Essa é uma decisão minha de design visual, não
  pedida explicitamente — fácil de trocar se a usuária preferir outra
  forma de diferenciar os dois botões.
- `caixaIndicadorAgenteMonitor` (LEDs dos 3 agentes) deslocada de x=100
  para x=142 para abrir espaço pro segundo botão.
- `atualizarEstadoItensMenuPorAba` agora habilita/desabilita os dois
  botões novos (mesma regra que o antigo botão único).

### i18n (`mensagens_{pt,en,es,fr}.properties`)

Duas chaves novas, `ui.tooltip.random.measures` e
`ui.tooltip.random.relations`, nos 4 idiomas. `ui.tooltip.random`
(usada só pelo item de menu) ficou inalterada.

## Verificação

- Compilação completa (438 arquivos): **0 erros**.
- Boot real sob Xvfb: aplicação sobe sem exceção, os dois botões
  aparecem lado a lado no cabeçalho com os distintivos M/R visíveis
  (screenshot conferido visualmente).
- Teste pontual (Robot, feito e apagado depois de usar — não faz parte
  do projeto): 5 cliques em cada botão, 10 no total, **0 exceções**.
  Confirmado que "Sortear Medidas" só sorteou entre
  COMPARACAO_MEDIDAS/TRANSFORMACAO_MEDIDAS/COMPOSICAO_MEDIDAS, e
  "Sortear Relações" só entre
  TRANSFORMACAO_RELACAO/COMPOSICAO_RELACOES/COMPOSICAO_TRANSFORMACOES —
  nunca um tipo do outro grupo.

## Escopo

`Main.java` (só os pontos listados) e os 4 arquivos de mensagens.
Nenhuma mudança em `EstadoSemanticoCompartilhado`, no piloto, ou em
qualquer lógica de resolução numérica — só a divisão do ponto de
entrada de sorteio.
