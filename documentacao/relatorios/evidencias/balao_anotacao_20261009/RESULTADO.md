# Passo 2 do desenho — balão da anotação extraído (2026-10-09)

`desenharBalaoAnotacao` (58 linhas) e seus 7 auxiliares de quebra/medida/pintura de texto formatado (`<b>`) saíram da
`Main.TelaGerard` para `gerard.campoaditivo.diagrama.apresentacao.RenderizadorBalaoAnotacao`. A classe só mede e pinta:
recebe mensagem, âncora, largura/altura do painel, "forçar abaixo" e as três cores; não lê nem altera estado da tela.
Na `Main` resta um delegador de 3 linhas. `FragmentoAnotacao` deixou de ser importado pela `Main`.

Prova: `TesteMacacoAnotacaoMouseOver` (mouse real), 3 situações + tela inicial, 213 capturas por versão; antigo = HEAD
(1d88600) compilado à parte. Pixel a pixel com a faixa y 205–245 mascarada (ruído de 1 px já registrado): 213 de 213 idênticas.

Limites: a faixa mascarada não é comparada (balão que caia nela não é verificado); PNGs não versionados.
Pendentes do passo 2: pickup (`desenharPickupEmPrimeiroPlano`), elementos do Venn, ícones.

## Pickup: decidir x desenhar (2026-10-09)

`desenharPickupEmPrimeiroPlano` (7 blocos que liam os handlers e desenhavam no mesmo passo) virou:
`obterDesenhaveisPickup()` (só lê o estado dos handlers e devolve, na ordem de desenho, a lista de `DesenhavelPickup`) +
`desenharPickupEmPrimeiroPlano(g2)` (só percorre a lista e chama `renderizadorPickup`). O renderizador de pickup
(`gerard.Scaffolding.pickup`) já existia e não mudou.

Prova: novo `tests/graphical/TesteMacacoPickupSegurando` (mouse real, captura SEGURANDO o botão, antes de soltar:
palavra do enunciado, peça de texto e quadradinho do Venn; 3 situações; 96 capturas por rodada). Com 250 ms entre passos
o teste era instável (antigo x antigo: 62 de 96 diferentes, balão de hover); com `-Despera=1500` antigo x antigo = 96/96
idênticas e antigo x novo = 96/96 idênticas (comparação da imagem inteira, sem máscara). A captura confere que o pickup
(peça com sombra) aparece na imagem. O teste de anotação seguiu 213/213 idêntico depois desta mudança.

Limites: não cobre conector em pickup, ponto de controle de eixo/comparação nem item de proxy em feedback.

## Venn: elementos desenhados por `RenderizadorElementosVenn` (2026-10-09)

`gerard.ui.venn.RenderizadorElementosVenn` (estático, só pinta): coleção da composição, barra da comparação (recebe o
valor já formatado), contagens + equação da composição (recebe as quantidades e o total já calculados) e seta. Na `Main`
ficam a escolha da representação (`desenharCirculoVenn`), o cálculo do valor/quantidades e delegadores.

Prova (mouse real, antigo = HEAD 1d88600 compilado à parte):
- comparação de medidas + composição de transformações: anotação 149/149 idênticas (faixa y 205–245 mascarada) e
  pickup segurando 72/72 idênticas (imagem inteira, `-Despera=1500`);
- composição de medidas (`PO_COMPOSICAO_MEDIDAS_bolas_1098018440`): pickup segurando 36/36; anotação 79/79 depois de
  descontar o fenômeno abaixo.
Fenômeno de teste, não de código: nas 16 primeiras capturas (cenário de composição) os ícones da barra superior
(globo, inseto, avatar, entrar) às vezes ainda não estão desenhados; isso ocorre em ordem aleatória, inclusive
antigo x antigo (c_a_antes x c_a_antes2: 16 diferentes). Pares com o mesmo estado da barra são idênticos.
Limite: seta e transformação de medidas não foram exercitadas (ids PO_TRANSFORMACAO_MEDIDAS_* não validados por
`listarValidadas`; ver `Ids.java`: `PO_TRANSFORMACAO_MEDIDAS_figurinhas_1595370160` é o id válido, ainda não rodado).

## Ícones: `gerard.ui.icones.IconesInterface` (2026-10-09)

Os 11 `criarIcone*` da `Main` (restaurar, editar, idioma da situação, chat, interrogação contextual, comunidade, site,
entrar, reportar bug, comparar categorias, ferramenta sortear), a silhueta de pessoa sem foto e o recorte circular saíram
para a classe estática; na `Main` ficaram delegadores de 3 linhas (mesmos nomes, chamadores intactos). Leitura de foto
do disco e do recurso do Gérard continuam na `Main` (não são desenho). Main: -547 linhas líquidas neste passo.

Prova: `TesteMacacoAnotacaoMouseOver`, composição e comparação de medidas, 155 capturas por versão. Com a barra superior
no mesmo estado nos dois lados, 79/79 (composição) e todas as da comparação idênticas pixel a pixel, ícones da barra
(comunidade, site, bug, avatar, entrar), "comparar categorias", sortear, restaurar, editar, chat e info incluídos.
Limite: a silhueta sem foto (usuário logado sem foto) e o ícone de erro de foto não aparecem nessas capturas.

## Fechamento: transformação de medidas (seta) (2026-10-09)

`PO_TRANSFORMACAO_MEDIDAS_figurinhas_1595370160`, versão cumulativa (balão + pickup + Venn + ícones) contra HEAD:
anotação 99/100 idênticas (a diferente é a 1ª captura da execução, tela inicial, com a janela ainda ajustando o
tamanho: a imagem inteira desloca; não é desenho) e pickup segurando 36/36 idênticas. Fecha o limite da seta/transformação
de medidas anotado acima.

## Fluxo da conclusão da modelagem: `FluxoFeedbackConclusaoModelagem` (2026-10-10)

`configurarFeedbackConclusaoModelagem` perdeu os ouvintes do tip (Sim/Não), do selo (clique, hover) e do sequenciador
(decidir/cancelar) e os textos do log `CONCLUSAO_MODELAGEM`: foram para `gerard.ui.conclusao.FluxoFeedbackConclusaoModelagem`.
A `Main` só realiza as ações pela `Porta` (destaque, selo, tip, repintura, nova situação, log) e mantém a ordem original
das operações. `inicializarDiagramaVergnaud` não foi tocado.

Prova: `tests/graphical/TesteMacacoConclusaoFluxo` (mouse real, `-Dpausa=1200`), composição de medidas
`PO_COMPOSICAO_MEDIDAS_bolas_1098018440`, três rodadas: (0) conclui, passa o mouse no círculo do selo (tip), tira, volta,
escolhe "Não"; (1) conclui, passa o mouse e escolhe "Sim" (nova situação); (2) conclui e clica no selo (próxima tarefa).
Três execuções (antigo = HEAD 1d88600, novo = 4b41bb6, antigo de novo): as 8 capturas (selo, tip sobre o selo, tip some
com o mouse fora, tip de novo, escolheu Não, Não com mouse fora, selo das rodadas 1 e 2) são idênticas pixel a pixel
nos três pares (faixa y 205–245 mascarada). Linhas de resultado idênticas entre as três: após "Não" permanece
(`tipo=COMPOSICAO_MEDIDAS`), após "Sim" e após o clique a situação muda (id diferente da fixa).

Limites:
- as escolhas Sim/Não/Próxima tarefa NÃO geraram linha `CONCLUSAO_MODELAGEM; escolha=...` no log nem na versão antiga
  (3 linhas "Sinalizar conclusão", 0 de escolha, nos três arquivos de sessão): a igualdade vale, mas o teste não prova o
  conteúdo dessas linhas; falta saber por que o log não as grava nesse caminho (possível filtro do modo exploratório
  depois da conclusão; não investigado);
- `concluida=true` depois de "Sim"/clique é igual nas três execuções (leitura do campo `tentativaModelagemAtual`); não
  interpretado aqui;
- só composição de medidas.

## Montagem dos elementos do diagrama de Vergnaud

`inicializarDiagramaVergnaud` passou a delegar a realização da cena (deslocamento de centralização, zona permitida de
cada figura e conector, marca da incógnita) para `gerard.campoaditivo.diagrama.apresentacao.MontadorElementosVergnaud`.
`criarZonaSemanticaElemento`, `criarZonaSemanticaConector` e `deslocarZonaSemantica` saíram da `Main` (só eram usados
ali). A `Main` mantém os efeitos de tela e a ativação dos seletores.

Prova: `TesteMacacoAnotacaoMouseOver` e `TesteMacacoPickupSegurando` (mouse real, `-Despera=1500`), versão anterior x
nova, comparação pixel a pixel:
- `PO_TRANSFORMACAO_MEDIDAS_figurinhas_1595370160`: 100/100 e 36/36 idênticas;
- `PO_COMPOSICAO_MEDIDAS_bolas_1098018440`: 79/79 e 36/36 idênticas.

Limites: só essas duas categorias; relações, composição de transformações e Venn não foram capturadas; a base de
comparação é a versão anterior ao fluxo da conclusão (já verificado separadamente).

### Complemento: outras categorias, encaixe e redimensionamento (MontadorElementosVergnaud)

Mesma comparação (versão anterior x nova, pixel a pixel, faixa y 205–245 mascarada), hover da anotação e arrastar segurando:
- `PO_TRANSFORMACAO_RELACAO_bonecas_620955739`: 65/65 e 24/24; `PO_COMPOSICAO_RELACOES_dinheiro_161113042`: 65/65 e 24/24;
- `PO_COMPOSICAO_TRANSFORMACAO_MEDIDAS_flores_422431114`: 73/73 e 36/36; `PO_COMPARACAO_MEDIDAS_bolas_487868670`: 77/77 e 36/36.

Encaixe e redimensionamento: `tests/graphical/TesteMacacoEncaixeRedimensiona` (mouse real). Arrasta cada palavra até a figura
correspondente (captura a cada soltura), solta uma palavra fora de qualquer figura, tira a janela do estado maximizado
(preparo programático) e arrasta o canto inferior direito: 1484x732 -> 1264x572 -> 1484x732 -> 1644x832. 9 capturas por cenário:
- composição de medidas: 9/9 idênticas;
- transformação de relação e composição de transformações: 8/9 cada; a diferença é só em `e3_janela_menor`, 12x16 px no
  quadradinho do "?" do enunciado (segunda linha), e a versão anterior contra ela mesma deu a mesma diferença no mesmo lugar
  (97 e 100 px): ruído conhecido do texto, fora da faixa mascarada por causa da quebra de linha com a janela menor.

Limites: o Venn (outro gerador de cena) não foi capturado; a saída do estado maximizado é preparo programático, o
redimensionamento em si é por arrasto real do canto.
