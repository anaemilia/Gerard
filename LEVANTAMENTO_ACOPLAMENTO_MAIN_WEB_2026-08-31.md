# Levantamento inicial de acoplamento em `Main.java`

Data: 2026-08-31. Este documento é uma fila de extração, não uma declaração de
que toda ocorrência listada está errada. Cada item deve ser confrontado com o
proprietário semântico ou representacional antes de ser movido.

## P0 — conhecimento semântico ainda decidido na tela

- ~~`5121–5250`: quantidade de passos, estados intermediários e sinais de
  transformação composta, inclusive inferência por fragmentos do enunciado~~
  — removida em 2026-09-01. As categorias legadas que acionariam esse código
  já não pertencem ao modelo canônico e seus seletores retornam `false`; a
  tela ainda analisava verbos do enunciado em quatro recarregamentos, mas o
  resultado era sempre neutro. Nenhuma heurística foi movida para cliente ou
  serviço: a lógica morta foi eliminada.
- `5670–6664`: descoberta do papel incógnito, obtenção do valor curado,
  confirmação e sequência de tentativas. Parte já delega a objetos de domínio,
  mas `Main` ainda monta e seleciona conhecimento. Destino: serviço portátil da
  tentativa, consumido por Swing e API.
  - Extração em 2026-09-01: a comparação factual entre valor atual e valor
    curado passou para `SemanticaCuradaSituacao.PapelCurado.estadoModificadoPor`;
    `Main` apenas localiza o papel e consome o resultado triestado.
  - Extração em 2026-09-01: a precedência “estado vivo recalculado, senão
    curadoria” passou para `ResolvedorValorEsperadoIncognita`, serviço de
    aplicação sem Swing. `Main` não lê nem converte mais o valor curado para
    decidir o alvo da avaliação.
  - Extração em 2026-09-01: conversão do texto proposto, resolução do esperado
    e preparação das ações `TEXTO`/`QUANTIFICAR` passaram para
    `ServicoAvaliacaoAcaoIncognita`, que delega a avaliação factual à
    `IncognitaQuantitativa`. A tela entrega fatos observados e recebe o
    resultado/registro já constituído; log, Modelador e ajuda permanecem como
    efeitos posteriores, fora do proprietário semântico.
- `8267–8706`: valores e sincronização da Comparação de Medidas, com fallbacks
  entre curadoria, elementos visuais e índices. Destino: estado semântico
  compartilhado + relação estrutural de comparação.
  - Limpeza em 2026-09-01: a obtenção de valor curado deixou de manter uma
    segunda tabela manual de campos (`quantidade1`, `estadoFinal`, `referido`
    etc.). A consulta agora termina no `PapelCurado`; ausência de curadoria
    permanece ausência, sem fallback inventado pela tela.
  - Extração em 2026-09-01: o fallback `referendo - referido` para obter o
    valor relativo deixou de ser executado em `Main`. A tela fornece os dois
    fatos ao `ResolvedorRelacoesEstruturaisAditivas`, que resolve o papel
    ausente pela `RelacaoEstruturalComparacao`. Piloto de comparação, barras e
    proteção da incógnita foram aprovados em compilação isolada do diretório
    `build` compartilhado.
  - Extração em 2026-09-01: o controle vertical das barras deixou de repetir
    `referendo - valorRelativo` / `referido + valorRelativo`. Tanto as barras
    quanto a edição formal consomem `RecalculoComparacaoMedidas`, e a escolha
    do papel alvo usa `obterPapelIncognitaAtual()` em vez do texto bruto
    `termoDesconhecido`. Testes de barras, relação de comparação e proteção da
    incógnita foram aprovados.
  - Extração em 2026-09-01: dentro deste intervalo,
    `aplicarEdicaoValorRelativoComparacao` decidia por aritmética embutida
    (`referendo - relativo` / `referido + relativo`) qual papel recalcular ao
    editar o Valor Relativo direto no controle do gráfico de barras — uma
    decisão que respeita qual papel é a incógnita curricular da situação
    (`termoDesconhecido`), por isso distinta da prioridade fixa do
    resolvedor genérico (`RelacaoEstruturalComparacao.recalcularParaConsistencia`,
    que sempre prefere Referendo). Essa decisão saiu para
    `gerard.dominio.campoaditivo.RecalculoComparacaoMedidas.decidir`, método
    estático e puro (sem Swing); `Main` só aplica o resultado com
    `definirValorNoElementoMedida`, na mesma ordem de prioridade que já
    existia. O restante do intervalo (fallbacks entre curadoria, elementos
    visuais e índices para leitura/exibição) permanece em `Main`.
- ~~`9378`, `10914–11184`: cálculos e equações de transformação e comparação
  ainda próximos do desenho~~ — verificado em 2026-09-01: já não há
  aritmética própria nesta área. `obterValorAtualControleComparacao`,
  `obterValorMaximoEscalaComparacao`, `obterValorRelativoAssinadoComparacao`
  e `inicializarProporcaoControleComparacaoSeNecessario` já delegam módulo,
  sinal e diferença a `RelacaoEstruturalComparacao`; a composição/transformação
  em processo já usa `EstadoComposicaoTransformacoes` e os renderizadores do
  pacote `gerard.campoaditivo.transformacao.composicao`. O que resta em
  `Main` (posições, fontes, cores dos rótulos da equação exibida) é
  apresentação genuína. Verificação por inspeção, sem compilação neste
  ambiente — os cortes citados já foram confirmados por `Main.java`
  compilando e pelos pilotos/testes listados nas seções acima.
- ~~`11234`: soma do Todo da composição dentro de `Main`~~ — extraída em
  2026-09-01 para `RelacaoEstruturalComposicao.calcularTodo`; o adaptador
  Swing conserva apenas a contagem das unidades concretas e o desenho.
- ~~`10835` e `11184`: formatadores sem uso de valor relativo/equação de
  comparação dentro de `Main`~~ — removidos em 2026-09-01; lógica morta não
  foi promovida artificialmente a serviço.
- ~~`8696–8710` e `10835`: descoberta da incógnita da comparação por texto e
  consulta do valor por posição em `elementosVergnaud`~~ — substituída em
  2026-09-01 pela projeção do `EstadoSemanticoCompartilhado`, usando o
  `MapeamentoPapeisRepresentacaoComplementar`. O projetor portátil não conhece
  Swing, SVG, rótulos nem posições visuais.
- `13156–13242`: reação e propagação das relações a partir de índices visuais.
  Destino: coordenadores relacionais; a tela deve apenas encaminhar comandos.
  - Preparação em 2026-09-01: `FiguraDiagrama` passou a transportar
    `chavePapelSemantico`; os seis renderizadores canônicos declaram a
    identidade de cada figura. Swing recebe a mesma chave em
    `ElementoVergnaud` e a API publica `chave_papel_semantico` no JSON. Isso
    permite substituir vizinhança/índice por identidade nos próximos cortes.
  - Primeiro uso em 2026-09-01: identificação do número relativo, do Estado
    Final, da incógnita protegida, do destino de uma soltura e do papel
    registrado no log passou a ler a chave da própria figura. A presença da
    lupa substituiu a inferência por `ELIPSE`. A propagação entre três papéis
    ainda usa vizinhança e permanece como o próximo corte relacional.
  - Extração em 2026-09-01: a segunda implementação de recálculo em `Main`
    (`reagirConsistencia*`, baseada nos vizinhos anterior/próximo) foi
    removida. Toda alteração segue diretamente para
    `EstadoSemanticoCompartilhado`, que resolve pelo
    `CatalogoRelacoesEstruturaisAditivas` e pelos objetos
    `RelacaoEstrutural*`. Os testes de proteção da incógnita, fluxo textual e
    três famílias estruturais foram aprovados. Não resta chamada a
    `elementosVergnaud.indexOf` em `Main`; a ponte posicional interna ainda
    necessária ao snapshot é obtida pelo catálogo a partir da chave do papel.
  - Extração em 2026-09-01: o bloqueio preventivo de resultado negativo
    deixou de procurar quantidades vizinhas e de inverter a operação em
    `Main`/scaffolding. `ResolvedorRelacoesEstruturaisAditivas` simula a
    tentativa com os mesmos objetos relacionais do estado compartilhado e
    valida o domínio do papel recalculado. Testes cobrem rejeição de
    `3 + (-10)` e aceitação de `3 + (-2)`.
- `14435–14763`: resolução de papéis por posição, índice ou valor textual.
  Destino: identidades semânticas explícitas transportadas nos elementos.
  - Limpeza em 2026-09-01: a propagação textual entre passos da antiga
    transformação encadeada foi removida, pois seu próprio seletor declara a
    categoria fora do modelo canônico. Nenhuma lógica morta foi promovida a
    serviço.
  - Extração em 2026-09-01: as buscas por índice/valor textual
    (`obterChavePapelExataPorIndice`, `obterChavePapelExataPorValor`,
    `obterChavePapelCanonicoPorIndice`, `obterChavePapelCanonicoPorValor`,
    `aplicarFallbackCuradoItemDesconhecido`, `converterParaPapelCanonico`)
    saíram de `Main` para `gerard.interpretacao.modelo.ResolvedorPapelInterpretado`,
    um serviço sem Swing que opera somente sobre `ResultadoInterpretacao`.
    `Main` preserva wrappers de mesmo nome, sem alterar nenhum ponto de
    chamada existente. Isto move o código, não a estratégia: a resolução
    continua por índice/valor textual quando o elemento não carrega
    `chavePapelSemantico` — o destino completo (identidade explícita em
    todo elemento, eliminando a busca posicional) permanece em aberto.

## P1 — decisão de representação ainda na tela

- `7219–7315`: composição do desenho e rótulos de passos.
  - Extração em 2026-09-01: o subtítulo/participante de cada figura deixou de
    ser escolhido em `Main` por categoria, índice e fragmentos do rótulo. A
    projeção consulta o `PapelCurado` por `chavePapelSemantico`; a API publica
    `subtitulo` e React apenas o materializa. A posição inicial do painel de
    eixo também deixou de depender da paridade do índice e usa o espaço
    geométrico real disponível acima/abaixo.
  - Limpeza em 2026-09-01: as duas categorias compostas já removidas do
    modelo canônico deixaram de manter flags constantes, bifurcações de cena,
    rótulos de passos, zonas especiais e geradores compactos dentro de
    `Main`. Com eles saíram as fórmulas mortas `parte1 + parte2`,
    `totalInicial - transformação` e a reconstrução posicional de estados.
    Criação, atualização, centralização e interação usam somente a cena
    canônica de `GeradorCenaDiagramaAditivo`. Os seis pilotos estruturais,
    fluxo textual e proteção da incógnita foram aprovados após o corte.
- `8156–8305` e `8866–9630`: escolha de representação complementar, montagem,
  reposicionamento e cenas compostas.
- `9747–11238`: renderização e controles do diagrama complementar/Venn,
  incluindo barras de comparação e equações visuais.
- `10802–11184`: geometria, escala e controle visual específicos da comparação.
- `15123–15292`: segunda implementação de miniaturas das categorias dentro da
  própria tela.

Destino: descritores portáteis de cena e serviços de layout; Swing, web e
mobile apenas materializam esses descritores.

## Extração concluída neste ciclo

- `FiguraDiagrama` passou a publicar `PosicaoRotuloFigura` e `exibirLupa`.
- `RenderizadorComparacaoMedidas` decide rótulos acima/abaixo.
- renderizadores de números relativos/transformações publicam a lupa.
- `Main` copia essas decisões para o adaptador Swing; não classifica mais o
  rótulo de comparação por texto para decidir sua posição.
- `PaineisEixosRelacoes` cria painéis pela decisão `exibirLupa`, não por uma
  nova lista de categorias.
- a API JSON transporta posição de rótulo, lupa e viewport; o React os
  materializa em SVG.

## Investigação: remoção de `reagirConsistenciaDaTransformacao` (2026-09-01)

Durante a busca por lógica ainda acoplada, encontrei que uma edição anterior
(não documentada neste arquivo) já havia removido de `Main` os métodos
`reagirConsistenciaAPartirDoElemento`, `reagirConsistenciaDaTransformacao` e
`atualizarEstadoFinalAPartirDoNumeroRelativo` — o mecanismo antigo, específico
de Transformação de Medidas/Relação, que recalculava estado final ou relação a
partir da origem da alteração. Investiguei se isso deixou a categoria
`TRANSFORMACAO_RELACAO` sem sincronização reativa. Conclusão: não deixou.

- `CatalogoRelacoesEstruturaisAditivas.criar` já tem um `case
  TRANSFORMACAO_RELACAO` que monta os três papéis (`relacaoInicial`,
  `transformacao`, `relacaoFinal`) via `FabricaPapeisTransformacaoDeRelacao` e
  os associa a `RelacaoEstruturalTransformacaoDeRelacao.transformacaoDeRelacao()`
  — já testada e madura.
- `ResolvedorRelacoesEstruturaisAditivas.resolver` é genérico: preenche o papel
  ausente (`calcularValorAusente`) quando há uma incógnita, ou recalcula por
  consistência (`recalcularParaConsistencia`) quando o índice alterado é
  conhecido — para qualquer `TipoSituacaoAditiva`, TRANSFORMACAO_RELACAO
  incluída.
- Comparando o `diff` contra o HEAD (`2355ba4`): em todo ponto de chamada
  removido, a chamada nova (`sincronizarTodasAsRepresentacoesAPartirDoVergnaud`,
  que aciona esse resolvedor genérico via `EstadoSemanticoCompartilhado`) já
  existia e já rodava lado a lado com o mecanismo antigo. Ou seja, os dois
  caminhos já coexistiam antes desta remoção; o antigo havia se tornado
  redundante, e a remoção elimina duplicação, não cobertura.
- `SeletorIndicesEstadoCompartilhado.selecionar` cai no caso padrão `{0, 1, 2}`
  para um diagrama de três elementos simples, que é a ordem usada pela fábrica
  de papéis desta categoria.

Ressalva: não há `javac`/`ant`/rede neste ambiente, então esta conclusão é por
inspeção estrutural do código, não por compilação nem pela bateria de
regressão do projeto. Nenhuma restauração foi aplicada — o código permanece
como a edição anterior o deixou.

## Próxima fronteira recomendada

Extrair primeiro o protocolo completo da lupa/eixo para uma ação portátil:
estado fechado, ação `REVELAR_EIXO`, estado revelado e ação `OCULTAR_EIXO`.
Depois disso, Swing e web podem executar o mesmo protocolo sem duplicar a
decisão de disponibilidade.

### Levantamento concreto para esta fronteira (2026-09-01)

Busquei por `Main` para localizar exatamente o que precisaria virar
protocolo portátil, sem tentar a extração — o alcance é maior que os cortes
anteriores (mexe em vários handlers de mouse simultaneamente) e eu não
consigo compilar nem rodar a bateria de regressão neste ambiente.

- Hoje existem DOIS mecanismos paralelos e não unificados, e o próprio
  código já documenta essa duplicação como deliberada e temporária: o
  Javadoc de `sincronizarPainelEixoRelacaoSeNecessario` diz explicitamente
  "mecanismo novo e paralelo... não substitui nem compartilha estado com o
  mecanismo já existente acima" (o mecanismo antigo é
  `sincronizarNumeroRelativoComGraficoSeNecessario`).
  - Mecanismo antigo (categorias fora de Relações): campos
    `itemGraficoInteiros`/`numeroRelativoGraficoInteiros`, apresentado por
    `apresentadorGraficoInteiros` (mostrar/registrarEscolha/atualizarGeometria),
    acionado por `mostrarGraficoInteirosNumeroRelativo` e
    `registrarEscolhaGraficoInteiros`.
  - Mecanismo novo (categorias de Relações): `paineisEixosRelacoes`, um
    painel por papel, ativado/desativado por `ativarPaineisEixosRelacoes` /
    `desativarPaineisEixosRelacoes`, com a lupa desenhada e clicada por
    `paineisEixosRelacoes.desenharLupas`/`processarPressionamentoLupa`.
  - A escolha de qual dos dois mecanismos vale para a categoria atual já é
    uma única decisão estrutural pura, `devemExibirPaineisEixosRelacoes()`
    (existe algum elemento com `exibirLupa`) — não duplicada, mas ainda só
    em `Main`.
- `exibirLupa` já nasce portátil: é campo de `FiguraDiagrama`
  (`isExibirLupa()`), publicado no JSON da API como `exibir_lupa`
  (`ServicoSorteioAtividadeWeb`) e só copiado para `ElementoVergnaud` na
  montagem da cena Swing. A API, porém, não publica a decisão agregada
  ("existe algum papel com lupa nesta cena?") — cada consumidor (Swing hoje,
  React no futuro) precisaria recalculá-la varrendo a lista, exatamente a
  duplicação que este item quer evitar.
- Antes de extrair uma ação portátil `REVELAR_EIXO`/`OCULTAR_EIXO`, a
  pergunta de design em aberto é se os dois mecanismos acima devem virar um
  só (um único estado fechado/revelado por papel, com o mecanismo antigo
  tratado como caso particular de um painel) ou se permanecem dois
  protocolos distintos que só compartilham a decisão de disponibilidade.
  Essa decisão muda o formato do estado/ação portátil e por isso não deveria
  ser tomada sem compilar e rodar a bateria de regressão — recomendo que
  fique para quem tiver esse ambiente disponível.

### Corte aplicado sem compilação: decisão de disponibilidade dos painéis de eixo (2026-09-01)

Com autorização explícita da usuária para tentar mesmo sem poder compilar
neste ambiente, apliquei o corte de menor risco entre os três candidatos
(este, novas fases de `mousePressed`/`mouseDragged`, ou descritores
portáteis de cena) — os outros dois seguem não tentados, pelos motivos já
registrados acima.

- Nova classe `gerard.campoaditivo.diagrama.modelo.DecisaoExibicaoPaineisEixo`:
  "existe pelo menos uma figura com lupa" — a mesma redução que já existia
  em `Main.devemExibirPaineisEixosRelacoes`, agora em um lugar só, com uma
  sobrecarga para `List<FiguraDiagrama>` (API web) e outra para `boolean[]`
  (adaptador Swing, que ainda consulta `ElementoVergnaud`).
- `Main.devemExibirPaineisEixosRelacoes` passou a montar o mesmo array de
  booleanos que já montava implicitamente no laço anterior e delegar a
  redução à classe nova — mesma guarda (`categoriaSelecionadaParaAtividade`,
  `elementosVergnaud != null`), mesma fonte de dados
  (`elemento.exibirLupa`), mesma ordem de leitura. Não mudei a fonte de
  dados para `cenaDiagramaAtual.getFiguras()` porque não pude confirmar,
  sem compilar, que os dois sempre têm o mesmo tamanho/conteúdo em todo
  ponto onde este método é chamado — preservar `elementosVergnaud` mantém o
  comportamento idêntico ao de antes do corte.
- `ServicoSorteioAtividadeWeb.projetarCena` passou a publicar
  `paineis_eixo_disponiveis` (booleano, no nível da cena) usando a mesma
  classe — a API web agora expõe a decisão agregada, em vez de obrigar um
  futuro consumidor (React, por exemplo) a recalculá-la varrendo `figuras`.
- Encontrei, mas **não toquei**, um campo já existente e nunca aceso:
  `item.put("lupa_habilitada", Boolean.FALSE)`, por figura, sempre
  hard-coded como falso e sem nenhum consumidor no repositório. Ele parece
  ser exatamente o placeholder desta fronteira (provavelmente o estado
  revelado/fechado por papel, não a disponibilidade agregada), mas como não
  há nenhum consumidor para confirmar a semântica pretendida, decidir o
  valor certo aqui seria adivinhar — fica registrado para quem for desenhar
  o protocolo `REVELAR_EIXO`/`OCULTAR_EIXO`.
  - Atualização em 2026-09-01: o campo ganhou consumidor —
    `web-poc/src/cena-gerard/FiguraCenaGerard.tsx` usa `figura.lupa_habilitada`
    para acrescentar a classe CSS `scene-magnifier-enabled` ao ícone da lupa
    (`aria-label="Eixo numérico em desenvolvimento"`). Isso não decide a
    semântica pendente — o consumidor é só estilo visual do ícone, a
    interação de revelar/ocultar ainda não existe no cliente web — mas
    confirma que manter `Boolean.FALSE` hoje está correto: o protocolo
    `REVELAR_EIXO`/`OCULTAR_EIXO` genuinamente não está disponível no
    cliente web ainda, então "desabilitado" é o valor real, não um
    placeholder esquecido. Ligar este campo a `ControleVisibilidadeEixoPapel`
    (ver "Corte: estado revelado/fechado do eixo extraído para fora de Swing",
    ao final deste documento) sem primeiro construir a interação
    correspondente no cliente web produziria um valor sempre falso de
    qualquer forma, já que toda projeção de cena ocorre antes de qualquer
    revelação — não há atalho aqui.
- **Não validado por compilação nem pela bateria de regressão** — apenas
  por inspeção (balanceamento de chaves/parênteses e ausência de referência
  pendente contra o HEAD). Ambos os métodos alterados são curtos e a lógica
  é a mesma redução booleana trivial de antes, mas isso não substitui
  `Main.java` compilando e os testes/Robot exigidos por
  `gerard-consistencia-estado`/`gerard-handlers-de-interacao`.


## Corte: estado numérico da comparação entre categorias (2026-09-01)

- A classe interna `ModeloNumericoComparacao` foi removida de `Main`.
- O estado e a relação `total = primeira parcela + segunda parcela` passaram
  para `gerard.aplicacao.EstadoNumericoComparacaoCategorias`, sem dependência
  de Swing, geometria ou índices visuais. O total usa a relação estrutural
  canônica de composição, inclusive sua proteção contra estouro inteiro.
- A atualização deixou de receber `TipoSituacaoAditiva + indice`. A camada
  visual traduz o alvo clicado para um papel explícito (`PRIMEIRA_PARCELA`,
  `SEGUNDA_PARCELA` ou `TOTAL`) e o estado portátil recebe somente esse papel.
- A auditoria revelou um defeito nessa fronteira: a miniatura de Comparação
  desenha os alvos na ordem `total, primeira parcela, segunda parcela`, mas a
  rotina antiga tratava qualquer categoria como `primeira parcela, segunda
  parcela, total`. Assim, editar o quadrado superior alterava a primeira
  parcela. O mapeamento explícito corrige essa inversão sem colocar a fórmula
  no adaptador Swing.
- Verificação: `Main.java` compilou isoladamente; passaram
  `TesteComparacaoBarrasCuradoria`, `TestePilotoComparacaoMedidas`,
  `TestePilotoComposicaoMedidas` e `TestePilotoTransformacaoMedidas`.

## Corte: projeção numérica do controle Venn de Comparação (2026-09-01)

- `Main` deixou de calcular `abs(referendo - referido)` e de reaplicar o
  sinal do Valor Relativo ao módulo escolhido no eixo.
- `RelacaoEstruturalComparacao` agora expõe o Valor Relativo assinado, seu
  módulo para projeções de escala e a preservação da orientação do valor
  atual. Os três métodos protegem também subtração/módulo não representável.
- A tela conserva somente decisões de apresentação: origem dos valores,
  proporção da escala, coordenadas e desenho do marcador.
- `TestePilotoComparacaoMedidas` passou a verificar diferença positiva e
  negativa, módulo e preservação do sinal. `Main.java`, o piloto e o teste
  de alinhamento das barras foram aprovados após o corte.

## Corte: remoção da ordem visual como identidade semântica (2026-09-01)

- Foi adicionada em `Main` uma única consulta transitória por
  `chavePapelSemantico`; ela localiza a figura sem supor posição na lista.
- Todos os usos restantes de `elementosVergnaud.get(0/1/2)` foram removidos.
  Isso inclui leitura da modelagem para o Venn, atualização do Valor Relativo,
  sincronização do controle de comparação, confirmação ao soltar e edição
  direta de Referido/Valor Relativo/Referendo.
- A ordem dos elementos continua sendo sintaxe da cena, mas não determina
  mais qual papel matemático será lido ou alterado pela `Main`.
- Verificação: não há ocorrência de `elementosVergnaud.get(0/1/2)` em
  `Main.java`; a classe compilou isoladamente; passaram os seis pilotos
  estruturais e `TesteProtecaoIncognitaEstadoCompartilhado`.

## Corte: política de restauração após edição rejeitada no eixo (2026-09-01)

- A escolha duplicada `valorAnterior != null ? valorAnterior : abs(candidato)`
  saiu dos dois protocolos de eixo da `Main` e passou para
  `gerard.interacao.eixo.PoliticaRestauracaoValorRelativo`.
- A política é portátil e não conhece Swing, categoria matemática ou
  scaffolding. A tela ainda decide quando rejeitar, informar e interromper a
  propagação; a política decide apenas qual valor local restaurar.
- O caso `Integer.MIN_VALUE`, cujo `Math.abs` continuava negativo, agora
  restaura zero quando não há valor anterior representável.
- O fixture de `TesteSinalRelativoSemQuantidadeNegativa` foi atualizado para
  construir figuras com `chavePapelSemantico` no campo próprio, em vez de
  usar a chave como rótulo visual pelo construtor legado. O teste agora também
  verifica o estado compartilhado e a resolução da identidade semântica.
- `Main.java` compilou; passaram `TesteSinalRelativoSemQuantidadeNegativa` e
  `TesteProtecaoIncognitaEstadoCompartilhado`, ampliado para Comparação de
  Medidas completa e incompleta.

## Corte: remoção do scaffolding reativo legado da Main (2026-09-01)

- `Main` deixou de instanciar `ScaffoldingReacaoRepresentacoes`. A única
  utilização produtiva restante dessa classe era sintaxe numérica para os
  eixos: magnitude, sinal e recomposição do número relativo.
- Essas operações passaram para `ServicoQuantidadeContextual`, ao lado da
  conversão e formatação já dependentes da grandeza e do idioma da situação.
  O serviço agora fornece magnitude localizada, sinal e a ponte legada
  `magnitude + sinal -> inteiro`.
- O fallback zero para texto inválido foi preservado. Magnitudes monetárias
  continuam respeitando o locale da situação.
- `Main.java` compilou; passaram `TesteFormatacaoValoresVergnaud`,
  `TesteFronteiraContextoQuantidade` e
  `TesteSinalRelativoSemQuantidadeNegativa`.

## Corte: módulo do Valor Relativo fora da Main (2026-09-01)

- As projeções de um Valor Relativo conhecido para módulo de escala deixaram
  de usar `Math.abs` diretamente em `Main`.
- `RelacaoEstruturalComparacao.calcularModuloDoValorRelativo` é agora o único
  proprietário dessa conversão no fluxo de Comparação, inclusive para
  sincronização bidirecional e cálculo do máximo do eixo.
- A tela conserva somente a proporção e a geometria do controle.
- `Main.java` compilou; passaram `TestePilotoComparacaoMedidas`, ampliado com
  a projeção de módulo, e `TesteComparacaoBarrasCuradoria`.

## Corte: estado revelado/fechado do eixo extraído para fora de Swing (2026-09-01)

Este corte não estava registrado neste levantamento — encontrado só ao investigar
por que o verificador estrutural (`scripts/verificar_regressao_gerard.py`) ainda
falhava depois de compilar e rodar a bateria completa pela primeira vez nesta
máquina (ver "Próxima fronteira recomendada" acima, que já havia mapeado este
exato ponto como o candidato de menor risco).

- O campo booleano `revelado`, antes solto dentro de `PaineisEixosRelacoes.Painel`,
  saiu para `gerard.interacao.eixo.ControleVisibilidadeEixoPapel`: um enum
  `FECHADO`/`REVELADO` com `podeRevelar()`/`revelar()`/`ocultar()`, sem Swing,
  AWT, geometria ou conhecimento de qual mecanismo de eixo (novo ou legado) o
  está usando.
- `Painel.estaRevelado()` passou a delegar a esse objeto; os oito pontos do
  coordenador que antes liam `painel.revelado` diretamente (desenho, hit-test,
  arraste, botão de esconder) passaram a chamar `painel.estaRevelado()`.
- Isso é exatamente o "estado fechado/revelado por papel" que a fronteira
  recomendada apontava como pré-requisito do protocolo portátil
  `REVELAR_EIXO`/`OCULTAR_EIXO` — a pergunta de design que ficou em aberto
  (unificar com o mecanismo antigo do eixo de inteiros, ou manter os dois
  protocolos) **continua em aberto**; este corte só move o estado do papel
  novo (Relações) para um objeto portátil, não decide a unificação.
- Verificação: `TesteControleVisibilidadeEixoPapel` (já existente, não
  documentado aqui) cobre transição de estado; a bateria completa (105/105
  testes) e o verificador estrutural (agora com todas as ~2000 checagens
  alcançadas, não só as anteriores a um `sys.exit` antecipado) passaram nesta
  sessão — a primeira verificação real deste corte desde que foi escrito.
- Também corrigido nesta sessão: o Javadoc de `PaineisEixosRelacoes.ativar`
  ainda descrevia o critério antigo (`elemento.tipo == TipoFiguraDiagrama.ELIPSE`,
  inferência geométrica) — desatualizado desde que o critério virou o
  descritor semântico `elemento.exibirLupa`. Comentário corrigido e import
  não utilizado de `TipoFiguraDiagrama` removido.
