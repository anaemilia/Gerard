# Item 4 — material concreto próprio de Relações: painéis de eixo por papel

Data: 2026-08-17

## Objetivo

Fechar o item 4 do levantamento de pendências
(`LEVANTAMENTO_PENDENCIAS_2026-08-11.md` /
`TAREFA_PENDENTE_REPRESENTACAO_COMPLEMENTAR_RELACOES.md`): `TRANSFORMACAO_RELACAO`
e `COMPOSICAO_RELACOES` mostravam o painel complementar em branco desde
2026-08-08 — sem nenhum material concreto próprio, diferente das outras
quatro categorias (quadradinhos, barras, processo/funil).

## Decisões da usuária (2026-08-16/17, em rodadas sucessivas de confirmação)

1. Um painel de eixo dos inteiros por papel da categoria — a mesma
   quantidade de painéis quanto for o número de relações (3 em
   `TRANSFORMACAO_RELACAO`: RelaçãoInicial/Transformação/RelaçãoFinal; 3 em
   `COMPOSICAO_RELACOES`: Relação1/Relação2/RelaçãoFinal) — não um painel
   único compartilhado. Posicionados "discretamente, manipulável, em cima e
   embaixo das relações".
2. Todos os papéis visíveis ao mesmo tempo (não um de cada vez, como o
   menu de escolha de sinal sob demanda já fazia).
3. Manipulável a qualquer momento, "mantendo a consistência entre
   representações".
4. Reaproveitar a classe já existente `ScaffoldingGraficoInteiros`,
   criando instâncias novas, em vez de um widget novo: "os dados e
   comportamento são iguais (mostram números inteiros, positivos e
   negativos, com um ponto de controle manipulável e consistência entre
   representações) — isso deve ser a base da tomada de decisão sobre
   reaproveitar ou não, sempre usando o princípio da localidade do
   conhecimento."
5. ~~Mesmo gatilho de visibilidade de todo outro material concreto do app —
   só depois da 3ª tentativa rejeitada consecutiva (`deveExibirDiagramaComplementar()`),
   nunca durante a modelagem normal: "mesma regra".~~ **Revisto no mesmo
   dia — ver "Revisão da regra de visibilidade (2026-08-17)" ao final.**

## Achados de arquitetura, feitos durante a investigação

- `ScaffoldingGraficoInteiros` já existe e já suporta exatamente o que a
  usuária descreveu (eixo com lado negativo/positivo, ponto de controle
  arrastável, painel flutuante com botão de esconder) — mas é usada hoje
  como **uma única instância compartilhada**, mostrada sob demanda quando o
  usuário escolhe o sinal de um número relativo digitado/solto (menu de
  radio buttons, `ScaffoldingNumeroRelativo.mostrarMenuEscolhaSinal`). Esse
  fluxo é usado por várias categorias (confirmado: também por Comparação
  de Medidas, `sincronizarValorRelativoComparacaoEmTodasAsDirecoes`, não
  só pelas categorias de Relações) e está amarrado a auditoria de pesquisa
  (`agentAuditService`), bloqueio de quantidade negativa e log granular —
  cerca de 25 pontos de integração em `Main.java`.
- Regeneralizar esse mecanismo único para várias instâncias simultâneas
  arriscaria regressão nesse fluxo já validado, sem forma de compilar/testar
  neste ambiente para confirmar que nada quebrou. Decisão acordada com a
  usuária: manter o mecanismo de instância única **intocado**, e criar um
  coordenador novo e paralelo (`PaineisEixosRelacoes`) exclusivo para o
  caso de vários papéis sempre visíveis — reaproveitando a mesma classe
  `ScaffoldingGraficoInteiros` (satisfaz "localidade do conhecimento": a
  lógica de eixo/sinal/ponto de controle/consistência não é duplicada, só
  instanciada mais de uma vez), sem tocar no fluxo existente.
- Todo material concreto do app (quadradinhos, barras, processo/funil) só
  aparece depois da 3ª tentativa rejeitada consecutiva
  (`SeletorRepresentacaoComplementar`, decisão documentada de 2026-08-07,
  "não durante a modelagem normal") — os painéis novos seguem essa mesma
  regra, confirmado explicitamente pela usuária.

## Alterações

- `src/gerard/Scaffolding/grafico/ScaffoldingGraficoInteiros.java`: novo
  método público `definirPosicaoInicial(int x, int y)` — sobrescreve a
  posição padrão (centralizada, encostada no topo do diagrama, pensada
  para uma única instância) logo após `mostrar`/`registrarEscolha` criarem
  o painel, para que quem gerencia várias instâncias possa ancorar cada
  uma perto de um alvo específico. Não altera nenhum comportamento
  existente (só adiciona um setter opcional).
- Criado `src/gerard/ui/vergnaud/PaineisEixosRelacoes.java`: coordenador
  que gere N pares (`ScaffoldingGraficoInteiros` + `ApresentadorGraficoInteiros`
  já existente, reaproveitado), um por `ElementoVergnaud`. Expõe ciclo de
  vida (`ativar`/`desativar`/`estaAtivo`/`obterPaineis`) e agregação de
  interação (pressionamento, arraste, natureza da interação, ponto de
  controle, botão de esconder, desenho) — cada método aggregate itera os
  painéis e delega para a instância certa, sem duplicar a lógica interna
  da classe reaproveitada.
- `src/Main.java`:
  - novo campo `paineisEixosRelacoes` (`PaineisEixosRelacoes`), paralelo ao
    `scaffoldingGraficoInteiros` já existente, sem alterá-lo;
  - `desenharGraficoInteiros()`: acrescentado `atualizarPaineisEixosRelacoesConformeVisibilidade()`
    (ativa/desativa a cada repaint, espelhando `deveExibirDiagramaComplementar()`
    + categoria) e o desenho dos painéis ativos;
  - `ativarPaineisEixosRelacoes()` (novo): cria um painel por elemento de
    `elementosVergnaud`, com o valor atual (mesmos helpers já usados pelo
    mecanismo existente — `obterValorNumericoDoElemento`,
    `scaffoldingReacaoRepresentacoes`) e posição inicial alternando
    acima/abaixo por índice;
  - `paineisEixosRelacoes.desativar()` acrescentado nos 3 pontos que já
    limpavam `elementosVergnaud`/o mecanismo de instância única
    (`inicializarTelaSemCategoria`, `restaurarModelagemDiagrama`,
    `inicializarDiagramaVergnaud`) — evita painéis apontando para
    `ElementoVergnaud` descartados ao trocar de situação (inclusive
    sorteando de novo dentro da MESMA categoria de Relações, caso em que a
    autocorreção por repaint sozinha não bastaria);
  - `mousePressed`/`processarMovimentoArraste`/`mouseReleased`/`mouseMoved`:
    blocos novos e paralelos aos já existentes para `scaffoldingGraficoInteiros`,
    roteando pickup/arraste/soltura/hover para `paineisEixosRelacoes`, sem
    alterar os blocos já existentes;
  - `existePickupAtivo()`/`pontoSobreElementoArrastavel()`: passaram a
    considerar também `paineisEixosRelacoes`;
  - nova `sincronizarPainelEixoRelacaoSeNecessario(boolean)`: mesma lógica
    de `sincronizarNumeroRelativoComGraficoSeNecessario` (bloqueio de
    quantidade negativa, log, reação de consistência, sincronização entre
    representações, confirmação de valor da incógnita), generalizada para
    o papel/painel que teve alteração pendente — sem tocar no método
    original;
  - `informarBloqueioQuantidadeNegativa()` ganhou um overload com âncora
    explícita (`ItemTextoArrastavel`, `ElementoVergnaud`) — o método sem
    parâmetros passou a delegar para o novo, preservando exatamente o
    comportamento anterior para os 5 chamadores já existentes;
  - `obterRepresentacoesAtuaisParaRelatoBug()`: passou a listar a
    representação de eixo dos inteiros também quando os painéis de
    Relações estão ativos (mesma chave i18n já usada pelo mecanismo
    existente).
- Criado `tests/java/TestePaineisEixosRelacoes.java`: harness executável
  (mesmo padrão sem JUnit) cobrindo ciclo de vida (ativar/desativar,
  idempotência de `ativar()` enquanto já ativo, `ativar(null)` seguro),
  agregados com lista vazia (nenhuma exceção, valores neutros), um arraste
  completo dirigido por coordenadas reais obtidas da própria instância
  (não adivinhadas), e isolamento entre dois painéis simultâneos
  (arrastar um não afeta o outro).
- `scripts/verificar_regressao_gerard.py`: nova seção "Item 4" — existência
  da classe e sua API, reaproveitamento real de `ScaffoldingGraficoInteiros`
  (`new ScaffoldingGraficoInteiros()` dentro do painel), fio de chamadas em
  `Main.java` (ativar/desativar ≥3 vezes/desenhar/processarPressionamento/
  arrastarPara/finalizarArraste/estaArrastando), gatilho de visibilidade
  ligado a `deveExibirDiagramaComplementar()` e às duas categorias,
  permanência intocada do mecanismo de instância única (`scaffoldingGraficoInteiros`/
  `itemGraficoInteiros`/`numeroRelativoGraficoInteiros`/`apresentadorGraficoInteiros`),
  existência do overload de `informarBloqueioQuantidadeNegativa` com
  contagem de chamadores, e existência dos testes do coordenador.
- `TAREFA_PENDENTE_REPRESENTACAO_COMPLEMENTAR_RELACOES.md` e
  `LEVANTAMENTO_PENDENCIAS_2026-08-11.md`: status atualizado para
  implementado, com o resumo das decisões e link para este relatório.

## Verificação feita nesta sessão (sem JDK neste ambiente)

- Balanceamento de chaves/parênteses/colchetes verificado (script próprio,
  ignora comentários e literais de string/char) em todos os arquivos Java
  tocados ou criados — sem desbalanceamento.
- Revisão manual de cada chamada nova contra a assinatura real do método
  chamado (grep dirigido a cada helper reaproveitado —
  `aplicarValorRelativoNoDiagrama`, `valorRelativoPreservaQuantidadesNaoNegativas`,
  `registrarLogUsuario`, `confirmarValorIncognitaAceito`,
  `incognitaAguardandoConfirmacaoDeValor`, `encontrarItemSobreElemento`,
  `reagirConsistenciaAPartirDoElemento`,
  `sincronizarTodasAsRepresentacoesAPartirDoVergnaud`, construtor de
  `ElementoVergnaud`) — não pude compilar/rodar `javac`/`java` neste
  ambiente (sem JDK, sem acesso de rede para instalar um).
- Simulação completa do verificador de regressão (cópia-sombra do
  repositório, com o único passo que exige JDK — `ant clean jar` —
  substituído por um stub que marca sucesso): **todas as checagens `[OK]`,
  0 `[ERRO]`, `APROVADO: verificador de regressão completo, nenhuma falha
  registrada.`** — inclui as 8 checagens novas do Item 4 e todas as
  checagens das fases e itens anteriores (nada foi quebrado por esta
  mudança, incluindo a Fase 7.5, já validada por você antes desta
  conversa).

## O que falta (precisa rodar no seu ambiente Windows real, na pasta `C:\Users\cecomp\Documents\aemq\git\Gerard`)

1. `ant clean jar` (via `python scripts\verificar_regressao_gerard.py`, que
   já roda o `ant clean jar` real internamente) — sucesso esperado:
   `APROVADO: verificador de regressão completo, nenhuma falha registrada.`
2. Compilar e rodar o teste novo:
   ```
   javac -cp build\classes -d build\classes tests\java\TestePaineisEixosRelacoes.java
   java -cp build\classes TestePaineisEixosRelacoes
   ```
   Sucesso esperado: termina em uma única linha,
   `Teste aprovado: PaineisEixosRelacoes gere várias instâncias de
   ScaffoldingGraficoInteiros com ciclo de vida, agregação de estado e
   isolamento corretos entre painéis.` — sem `AssertionError` nem exceção.
3. Verificação visual (recomendada antes de considerar fechado, já que
   esta é a primeira vez que este material concreto aparece de verdade):
   rodar o Gerard, sortear uma situação de Transformação de Relação ou
   Composição de Relações e conferir que os 3 painéis de eixo — um por
   papel — já aparecem imediatamente (não é mais preciso errar 3 vezes;
   ver "Revisão da regra de visibilidade" abaixo), manipuláveis,
   e que arrastar o ponto de controle de qualquer um atualiza o círculo
   correspondente no diagrama de Vergnaud e as demais representações
   (mesma consistência já validada no mecanismo de instância única).
   Também vale conferir que Comparação de Medidas (que usa o mecanismo
   antigo) continua funcionando exatamente como antes — checagem de não
   regressão do fluxo que ficou intocado.
4. Harness Robot real (`TesteMonkeyGuiadoPorCasosReais`), como checagem
   indireta de não-regressão geral — mesma ressalva de cobertura já
   registrada nas Fases 7.4/7.5: o catálogo de episódios reais não inclui
   nenhum passo de Relações com este material concreto especificamente
   (são todos de Medidas), então não cobre o gesto novo em si, só confirma
   que o resto do fluxo (hit-testing, renderização, despacho de mouse)
   continua sem exceção nos 14 episódios/45 passos de sempre.

## Revisão da regra de visibilidade (2026-08-17, mesmo dia)

Depois da validação real dos itens acima (build, teste de unidade do
coordenador e teste de integração dirigindo `Main.TelaGerard` de verdade —
todos aprovados pela usuária), ela viu o comportamento real do app
(captura de tela: só o widget antigo de instância única aparecendo, os 3
painéis novos ainda escondidos porque a 3ª tentativa rejeitada ainda não
tinha ocorrido) e esperava ver os 3 eixos imediatamente: "Nesse caso
existem três número relativos, deveria existir três eixos" — seguido de
"tire essa regra: Para ver os 3 eixos: erre a Relação final 3 vezes
seguidas, sem acertar entre as tentativas."

Isso reverte a decisão 5 original ("mesma regra"). Mudança aplicada:

- `Main.java`, `devemExibirPaineisEixosRelacoes()`: deixou de depender de
  `deveExibirDiagramaComplementar()` (que só fica `true` depois da 3ª
  tentativa rejeitada). Passou a depender só de
  `categoriaSelecionadaParaAtividade` + a categoria ser uma das duas de
  Relações — mesma condição usada por quase toda a tela para saber se uma
  categoria está ativa, sem nenhuma dependência do histórico de tentativas.
  O restante do fio (`atualizarPaineisEixosRelacoesConformeVisibilidade()`,
  ativar/desativar a cada repaint) não mudou.
- `scripts/verificar_regressao_gerard.py`: a checagem do gatilho de
  visibilidade do Item 4 foi reescrita para exigir o novo corpo do método
  (`return categoriaSelecionadaParaAtividade && (...)`) em vez de checar a
  presença de `deveExibirDiagramaComplementar()` (checagem antiga, fraca,
  porque esse texto aparece em outros lugares de `Main.java` de qualquer
  jeito).
- Quadradinhos, barras e processo/funil **não mudaram** — continuam
  esperando a 3ª tentativa rejeitada, como sempre. A mudança é exclusiva
  dos painéis novos de Relações.
- `TAREFA_PENDENTE_REPRESENTACAO_COMPLEMENTAR_RELACOES.md` e
  `LEVANTAMENTO_PENDENCIAS_2026-08-11.md` atualizados para refletir a regra
  nova.

Reverificado nesta sessão, depois da mudança: balanceamento de
chaves/parênteses (script próprio) sem desbalanceamento em `Main.java` e
`scripts/verificar_regressao_gerard.py`; nova simulação completa do
verificador de regressão (cópia-sombra, `ant clean jar` stubado) —
novamente todas as checagens `[OK]`, 0 `[ERRO]`,
`APROVADO: verificador de regressão completo, nenhuma falha registrada.`,
incluindo a checagem do Item 4 já ajustada para a nova regra. Ainda falta
validação real desta mudança específica no seu ambiente (regressão real +
conferência visual sem precisar errar 3 vezes).

## Supressão do eixo único antigo nas categorias de Relações (2026-08-17, mesmo dia)

Depois da validação real da regra de visibilidade acima, a usuária testou
de verdade e viu que, ao escolher o sinal de um número relativo (menu de
radio buttons positivo/negativo, mecanismo já existente e intocado), o
eixo único antigo também aparecia — sobrepondo o enunciado no topo da tela
— **junto** com os 3 painéis novos já visíveis. Redundante: o papel em
questão já tinha seu próprio painel entre os 3 novos. Ela pediu: "Tres
relações apenas tres eixos" e confirmou que a supressão deve valer "apenas
nas categorias de relações" — nas demais (Comparação de Medidas etc.), o
eixo único antigo continua aparecendo exatamente como sempre.

Mudança aplicada, escopo mínimo (só os dois pontos de entrada do eixo
antigo, sem tocar no menu de escolha de sinal em si nem em nenhuma outra
categoria):

- `Main.java`, `mostrarGraficoInteirosNumeroRelativo(...)` e
  `registrarEscolhaGraficoInteiros(...)`: cada uma ganhou um guard logo no
  início — `if (devemExibirPaineisEixosRelacoes()) { return; }` — que
  impede o eixo único antigo (`apresentadorGraficoInteiros`/
  `scaffoldingGraficoInteiros`) de ser mostrado ou atualizado quando a
  categoria ativa é uma das duas de Relações. O menu de escolha de sinal
  (`scaffoldingNumeroRelativo.mostrarMenuEscolhaSinal`, tratado
  separadamente) não foi tocado — continua aparecendo normalmente, é só o
  eixo/painel flutuante que fica de fora.
- `scripts/verificar_regressao_gerard.py`: duas checagens novas no Item 4 —
  confirma que a assinatura original dos dois métodos foi preservada (guard
  é aditivo) e que o guard `devemExibirPaineisEixosRelacoes()` aparece
  exatamente 2 vezes (uma por método).
- `tests/java/TesteTemporarioItem4Relacoes.java`: novo passo que chama
  `mostrarGraficoInteirosNumeroRelativo` diretamente (reflection, mesmo
  padrão já usado no arquivo) numa categoria de Relações e confirma que
  `scaffoldingGraficoInteiros.isVisivel()` continua `false` depois — prova
  automatizada da supressão.

Reverificado nesta sessão: balanceamento de chaves/parênteses sem
desbalanceamento em `Main.java`, `scripts/verificar_regressao_gerard.py` e
`tests/java/TesteTemporarioItem4Relacoes.java`; nova simulação completa do
verificador de regressão (cópia-sombra, `ant clean jar` stubado) —
novamente todas as checagens `[OK]`, 0 `[ERRO]`,
`APROVADO: verificador de regressão completo, nenhuma falha registrada.`
Falta validação real desta mudança específica no ambiente Windows da
usuária.

## Correção: painéis não atualizavam quando o valor mudava por fora do próprio arraste (2026-08-17, mesmo dia)

Depois da supressão do eixo antigo acima, a usuária testou de verdade e
viu os 3 painéis novos com valores presos ao que tinham na criação (ex.:
"+4"/"+2" já digitados nos papéis, mas os eixos mostrando vazio) — "eixos
não mudam com a mudança dos elementos no diagrama". Causa raiz: o único
ponto que atualizava o eixo quando o valor mudava por outro caminho (menu
de escolha de sinal, protocolo da incógnita) era
`sincronizarEixosComEstadoCompartilhado()`, que chama
`registrarEscolhaGraficoInteiros(...)` — exatamente o método que acabou de
ser suprimido nas categorias de Relações. Sem substituto, os painéis novos
ficaram órfãos de atualização, só recebendo valor uma vez, na criação.

Nota de terminologia da usuária: "Relações" no domínio do app tem três
categorias — Composição de Transformações, Transformação de Relação e
Composição de relações —, não duas. Os painéis novos deste item cobrem só
as duas últimas; Composição de Transformações já tem material concreto
próprio (funil, fora do escopo do Item 4) e não foi tocada aqui.

Correção aplicada:

- `Main.java`, novo método `atualizarValorPainelEixoRelacao(Painel)`:
  extrai a lógica de leitura do valor do elemento (mesma usada em
  `ativarPaineisEixosRelacoes()`, agora reaproveitada em vez de duplicada —
  localidade do conhecimento) sem mexer na posição do painel.
- Novo método `atualizarPaineisEixosRelacoesComValoresAtuais()`: percorre
  os painéis ativos e reaplica o valor atual do elemento correspondente,
  pulando qualquer painel que esteja sendo arrastado no momento (evita
  disputa com o gesto do usuário — o próprio arraste já escreve de volta no
  elemento em tempo real).
- Chamada a partir de `aplicarEstadoCompartilhadoEmTodasAsRepresentacoes(...)`
  — o mesmo ponto central por onde toda outra representação (Vergnaud,
  texto, diagrama complementar, eixo antigo) já se mantém sincronizada — em
  vez de espalhar gatilhos novos pelo código.
- `scripts/verificar_regressao_gerard.py`: checagem nova confirmando a
  existência dos dois métodos, o guard contra sobrescrever painel em
  arraste, e a chamada a partir do ponto central.
- `tests/java/TesteTemporarioItem4Relacoes.java`: novo passo que muda o
  valor do elemento Transformação diretamente (sem passar pelo arraste do
  próprio painel) e confirma que o painel correspondente reflete o novo
  valor depois de `sincronizarTodasAsRepresentacoesAPartirDoVergnaud`.

Reverificado nesta sessão: balanceamento de chaves/parênteses sem
desbalanceamento em `Main.java`, `scripts/verificar_regressao_gerard.py` e
`tests/java/TesteTemporarioItem4Relacoes.java`; nova simulação completa do
verificador de regressão (cópia-sombra, `ant clean jar` stubado) —
novamente todas as checagens `[OK]`, 0 `[ERRO]`,
`APROVADO: verificador de regressão completo, nenhuma falha registrada.`
Falta validação real desta correção específica no ambiente Windows da
usuária.

## Visibilidade individual por lupa (2026-08-17, mesmo dia, terceira revisão)

Depois de validar a correção do bug de sincronização acima, a usuária achou
os 3 eixos aparecendo de uma vez, sempre visíveis, poluído demais: "Eu achei
que os eixo aparecendo logo no início deixou a tela muito poluída." Proposta
dela: um ícone de lupa perto de cada círculo/retângulo do papel — clicar
revela o eixo daquele papel ("uma amplificação da visão do que é um número
relativo e por que ele aparece com sinal"), um de cada vez, independente dos
outros. Fechar pelo botão "esconder" já existente do próprio painel faz a
lupa reaparecer.

Implementado dentro de `PaineisEixosRelacoes` (mesma classe que já desenha e
trata a interação do eixo — localidade do conhecimento):

- `Painel` ganhou um campo `revelado` (privado, começa falso). Todos os
  métodos de desenho/interação do coordenador (`desenhar`,
  `processarPressionamento`, `contemPontoControle`, `contemAlgumPainel`,
  `contemBotaoEsconder`, `identificarNaturezaInteracao`, `estaArrastando`,
  `encontrarArrastando`, `encontrarComAlteracaoPorInteracao`) passaram a
  ignorar papéis ainda não revelados — como se o eixo simplesmente não
  existisse até a lupa ser clicada.
- Novos métodos: `desenharLupas` (desenha a lupa de cada papel ainda não
  revelado), `processarPressionamentoLupa` (testa o clique contra cada lupa
  e revela o painel correspondente), `contemLupa` (hover/tooltip),
  `encontrarComOcultacaoPorInteracao`/`ocultarRevelacao` (detectam quando o
  próprio painel se escondeu, para a lupa voltar a aparecer).
- `atualizarValorPainelEixoRelacao`/`atualizarPaineisEixosRelacoesComValoresAtuais`
  (a correção de sincronização acima) continuam rodando por igual em papéis
  revelados ou não — o valor interno do eixo fica sempre em dia, só a
  exibição/interação é que depende da lupa. Isso garante que, ao clicar na
  lupa, o eixo já aparece com o valor certo, não zerado.
- `Main.java`: `desenharGraficoInteiros()` chama `paineisEixosRelacoes.desenharLupas(g2)`;
  novo método `prepararPainelEixoRelacao(painel, larguraTela, alturaTela)`
  (valor + posição) extraído de `ativarPaineisEixosRelacoes()` para ser
  reaproveitado também na revelação por lupa; `mousePressed` testa a lupa
  antes de qualquer outra interação da tela e, ao consumir, chama
  `prepararPainelEixoRelacao` no painel recém-revelado; o ramo que já lida
  com o retorno de `paineisEixosRelacoes.processarPressionamento(...)`
  ganhou a detecção de ocultação (`encontrarComOcultacaoPorInteracao`/
  `ocultarRevelacao`) para a lupa voltar; `mouseMoved` ganhou tooltip/cursor
  de mão ao passar sobre uma lupa.
- Novas chaves i18n (pt/en/es/fr): `ui.tooltip.integerAxis.reveal`.
- `tests/java/TestePaineisEixosRelacoes.java`: os dois testes que já
  interagiam com o eixo (`testarArrasteDeUmPainel`,
  `testarIsolamentoEntreDoisPaineis`) precisaram passar a revelar o painel
  primeiro (helper `revelarPainel`, que espelha a geometria privada da
  lupa) — sem isso, quebrariam, já que a interação de eixo agora exige
  revelação. Novo teste `testarVisibilidadePorLupa`: confirma que os
  papéis começam escondidos, que revelar um não afeta o outro (visibilidade
  independente), que a interação de eixo é bloqueada antes de revelar e
  passa a funcionar depois, e que a lupa de um papel já revelado para de
  responder a cliques.
- `tests/java/TesteTemporarioItem4Relacoes.java`: também precisou revelar
  `painelRelacaoInicial` pela lupa antes do teste de arraste que já existia;
  ganhou também a checagem de que revelar um papel não revela os outros.

Reverificado nesta sessão: balanceamento de chaves/parênteses sem
desbalanceamento em todos os arquivos tocados; nova simulação completa do
verificador de regressão (cópia-sombra, `ant clean jar` e a checagem de
existência do JAR também stubados desta vez, já que nenhum dos dois faz
sentido numa simulação puramente estrutural) — novamente todas as
checagens `[OK]`, 0 `[ERRO]`, `APROVADO: verificador de regressão
completo, nenhuma falha registrada.`, incluindo as novas checagens da lupa
e dos testes atualizados. Falta validação real desta funcionalidade no
ambiente Windows da usuária — build, os dois testes de unidade, e o check
visual (lupa aparece, revela o eixo com o valor certo, esconder faz a lupa
voltar).

## Conclusão

O material concreto de `TRANSFORMACAO_RELACAO`/`COMPOSICAO_RELACOES` está
implementado e verificado estruturalmente neste ambiente (sem JDK) — falta
a validação real acima no seu ambiente Windows. Item 4 do levantamento de
pendências fica assim fechado, restando apenas o roteiro de extração de
handlers (eixo de inteiros, o último protocolo, ainda não autorizado a
começar).
