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

## Corte: magnitude das transformações complementares e limpeza morta (2026-09-01)

- A conversão de valor assinado em quantidade de unidades saiu da `Main` e
  passou para `PoliticaSinalTransformacaoComplementar.magnitudeParaUnidades`.
  Captura, relayout e detecção de presença usam agora a mesma política que já
  preservava o sinal do agrupamento.
- O texto assinado desenhado no cartão complementar deixou de concatenar
  `"+" + valor` e usa `ServicoQuantidadeContextual`, preservando grandeza e
  idioma.
- `converterValorRelativoCurado` e seu normalizador auxiliar foram removidos
  de `Main`: não possuíam consumidores e duplicavam a aplicação de sinal já
  realizada por `SemanticaCuradaSituacao`.
- `Main.java` compilou; passaram
  `TesteCapturadorValoresRepresentacaoComplementar`, ampliado com a magnitude,
  os pilotos de Transformação de Medidas e Composição de Transformações, e
  `TesteFormatacaoValoresVergnaud`.

## Corte: contrato canônico do catálogo de papéis (2026-09-01)

- `quantidadePassosTransformacaoComposta = 1` e todas as combinações
  constantes `false, 1` foram removidas de `Main`.
- `CatalogoPapeisSemanticosAditivos` oferece agora os contratos canônicos
  `tipo + papel -> índice` e `tipo + índice -> papel`. Os contratos encadeados
  antigos permanecem somente para consumidores legados fora do fluxo atual.
- Consultas de conclusão, Venn, limites e interação deixaram de atravessar
  `ScaffoldingQuestionamento`: o mapeamento pertence diretamente ao catálogo.
- `MapeadorPapelSemanticoTextoPadrao` também deixou de depender de
  scaffolding e de flags da cena removida; recebe apenas categoria e o
  mapeamento atual do estado compartilhado.
- `Main.java` compilou; passaram `TestePoliticaValoresAditivos`, ampliado com
  os contratos canônicos e o mapeador textual, `TesteP4_1FluxoTextoIncognita`
  e o piloto de Composição de Transformações.

## Corte: zonas de interação derivadas da cena portátil (2026-09-01)

- Foram removidos de `Main` os dois `switch` por categoria que definiam zonas
  de arraste de figuras e conectores com dezenas de coordenadas fixas.
- `GeradorCenaDiagramaAditivo` agora projeta a zona de cada figura pelas
  bissetrizes entre os centros reais das figuras da cena; a zona sempre inclui
  a própria figura e é recortada pela `AreaDiagrama`.
- A zona do conector é derivada de seus extremos/alvo e de margens
  proporcionais à área. Nenhum dos dois cálculos conhece Swing ou categoria.
- `Main` apenas converte `AreaDiagrama` para `Rectangle` no adaptador e aplica
  a margem mecânica do objeto arrastável.
- Também saíram dois helpers posicionais mortos de leitura numérica, sem
  consumidores desde a adoção das identidades semânticas.
- `TesteAreaDiagramaPortatil` agora valida, nas seis categorias, que toda zona
  permanece dentro da área e contém sua figura/conector. `Main.java` compilou;
  passaram o teste portátil, os seis pilotos estruturais e
  `TesteFeedbackMultissensorialPosicionamento`.

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
  `REVELAR_EIXO`/`OCULTAR_EIXO`. A pergunta de design que ficou em aberto
  (unificar com o mecanismo antigo do eixo de inteiros, ou manter os dois
  protocolos) foi respondida em 2026-09-01 — ver "Corte: mecanismo antigo do
  eixo de inteiros removido por inteiro" ao final deste documento. A resposta
  não foi unificação: o mecanismo antigo era código morto, então não havia o
  que unificar.
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

## Corte: mecanismo antigo do eixo de inteiros removido por inteiro (2026-09-01)

Resolve a pergunta de design deixada em aberto na fronteira acima (unificar
os dois mecanismos de eixo, ou mantê-los separados). A resposta não foi
unificação — foi remoção. Autorizado explicitamente pela usuária depois de
apresentado o escopo real (não são só os dois métodos-gatilho: é toda a
Fase 7.6, incluindo `mousePressed`/`mouseDragged`/`mouseReleased`).

**Achado que motivou a remoção.** Rastreando todos os pontos onde uma figura
de "número relativo" é construída — os seis renderizadores canônicos
(`RenderizadorComparacaoMedidas`, `RenderizadorTransformacaoMedidas`,
`RenderizadorComposicaoTransformacoes`, `RenderizadorTransformacaoRelacao`,
`RenderizadorComposicaoRelacoes`, e a variante grande) — todos passam
exclusivamente por `RenderizadorDiagramaAditivoBase.relacao()`,
`.transformacao()` ou `.relacaoGrande()`, e essas três chamam `new
FiguraDiagrama(..., exibirLupa=true, ...)` sem nenhuma exceção; `medida()`
(o papel de medida/quadrado) sempre passa `exibirLupa=false`. Não existe
nenhum caminho de código que construa uma figura de número relativo sem a
lupa. Como `devemExibirPaineisEixosRelacoes()` é verdadeiro sempre que existe
pelo menos um `exibirLupa=true` no diagrama, e os dois pontos de entrada do
mecanismo antigo (`mostrarGraficoInteirosNumeroRelativo`,
`registrarEscolhaGraficoInteiros`) já continham `if
(devemExibirPaineisEixosRelacoes()) return;` desde a Fase 7.7, o mecanismo
antigo era inalcançável em qualquer uma das seis categorias — não só nas de
Relações, como o comentário original do guard sugeria ("nas demais
categorias... nada muda"). O próprio comentário do guard já continha a pista:
"só o eixo antigo é que fica de fora aqui" — escrito para valer em toda
categoria, não só Relações.

**Verificação antes de agir.** Confirmado por dois caminhos independentes
nesta sessão, na máquina Windows com JDK/Ant reais (a primeira vez que este
código roda de fato, não só compila, desde as extrações de 2026-09-01):

- Leitura estática de todos os seis renderizadores (acima).
- Dois runs do harness Robot (`TesteMonkeySemiGuiado`, seeds `20260901` e
  `777888`, ~200 iterações combinadas) cobrindo 5 das 6 categorias
  (Composição de Medidas, Transformação de Medidas, Comparação de Medidas,
  Transformação de uma Relação, Composição de Transformações), incluindo
  arrastes diretos em figuras de número relativo (`papel.diferenca`,
  `papel.transformacao2`, `papel.transformacaoFinal`) — zero ocorrências do
  mecanismo antigo em instrumentação temporária (adicionada só numa cópia
  não rastreada de `Main.java` fora do repositório, nunca no arquivo real,
  e descartada depois). Composição de Relações (6ª categoria) não foi
  alcançada pelo sorteio aleatório do harness nas duas rodadas, mas usa a
  mesma `relacaoGrande()` já exercitada nas outras categorias de Relações —
  mesma garantia estática.
- Durante essa investigação, dois JVMs Robot ficaram vivos além do esperado
  (a classe de teste nunca chama `System.exit()`, e o Swing/AWT mantém a
  JVM viva mesmo com o loop de iterações já terminado) — encerrados
  manualmente depois de confirmado, sem impacto no achado.

**O que foi removido.**

- `Main.java`: campos `itemGraficoInteiros`, `numeroRelativoGraficoInteiros`,
  a instância única `scaffoldingGraficoInteiros`, `apresentadorGraficoInteiros`,
  `adaptadorInteracaoEixoInteiros`, `handlerEixoInteiros`; os métodos
  `mostrarGraficoInteirosNumeroRelativo`, `registrarEscolhaGraficoInteiros`,
  `sincronizarNumeroRelativoComGraficoSeNecessario`,
  `atualizarGraficoInteirosDuranteMovimento`,
  `limparGraficoInteirosSeForItemAtivo`, `limparGraficoInteiros`; os blocos
  correspondentes em `mousePressed`, `processarMovimentoArraste` (chamado por
  `mouseDragged`), `mouseReleased`, `mouseMoved`/`mouseExited` (foco e dica
  do botão de esconder e do ponto de controle do eixo único) e no handler de
  tecla Delete. O parâmetro `atualizarGrafico`, que não fazia mais nada,
  saiu de `aplicarValorRelativoNoDiagrama`/`definirValorNoElementoNumeroRelativo`
  (9 chamadores ajustados). `desenharGraficoInteiros` virou
  `desenharPaineisEixoRelacoes` (só desenha o que ainda existe). O overload
  sem parâmetros de `informarBloqueioQuantidadeNegativa` passou a ancorar em
  `(null, null)` diretamente — comportamento idêntico, já que os campos que
  alimentava eram sempre nulos.
- Arquivos apagados: `src/gerard/interacao/arraste/AlvoInteracaoEixoInteiros.java`,
  `src/gerard/interacao/arraste/HandlerInteracaoEixoInteiros.java`,
  `src/gerard/ui/vergnaud/AdaptadorInteracaoEixoInteiros.java`,
  `src/gerard/ui/vergnaud/FonteGeometriaInteracaoEixoInteiros.java`,
  `tests/java/TesteHandlerInteracaoEixoInteiros.java`.
- `tests/java/TesteTemporarioItem4Relacoes.java`: removido o trecho que
  invocava `mostrarGraficoInteirosNumeroRelativo` por reflexão e checava
  `scaffoldingGraficoInteiros.isVisivel()` — não havia mais o que verificar
  (o resto do teste, sobre os painéis novos, continua intacto e passando).
- `scripts/verificar_regressao_gerard.py`: a seção "Fase 7.6" virou uma
  verificação de que o mecanismo antigo foi removido por inteiro (arquivos
  ausentes, nenhum símbolo residual em `Main`), em vez de verificar sua
  presença. Duas checagens que dependiam de contagens/padrões literais do
  mecanismo antigo foram reescritas. O ratchet de `LIMITES_PROTOCOLOS_MAIN`
  foi atualizado para os tamanhos reais depois do corte: `mousePressed`
  442→401, `processarMovimentoArraste` 67→60, `mouseReleased` 111→103,
  `mouseMoved` 229→205 (`mouseDragged` e `mouseClicked` não mudaram).

**Não removido, propositalmente.** As classes concretas `ApresentadorGraficoInteiros`
e `ScaffoldingGraficoInteiros` continuam existindo — cada `PaineisEixosRelacoes.Painel`
instancia a sua própria `ScaffoldingGraficoInteiros`, e `PaineisEixosRelacoes`
usa `ApresentadorGraficoInteiros`. Só a instância única de `Main` e o
protocolo dedicado a ela (Fase 7.6) foram removidos — não as classes que o
mecanismo novo (Fase 7.7) continua reaproveitando.

**Verificação depois de agir.** `Main.java` compilou isoladamente sem erros;
`verificar_linha_base_windows.py` aprovou 104/104 testes executáveis (105→104,
a diferença é `TesteHandlerInteracaoEixoInteiros`, apagado); o verificador
estrutural completo terminou `APROVADO`, nenhuma falha.

## Corte: proteção de valores curados visíveis fora da tela (2026-09-01)

A auditoria encontrou a `Main` repetindo, em caminhos de comparação e de
representação complementar, a regra `papel != null && !papel.isDesconhecido()`
antes de converter um valor curado. Essa não é uma decisão gráfica: é a
proteção semântica que impede a resposta curada de vazar para uma
representação antes de o aluno resolver a incógnita.

`SemanticaCuradaSituacao.PapelCurado` passou a fornecer
`getValorInteiroVisivel()`, que devolve `null` para a incógnita e o inteiro
materializado para um papel conhecido. A fonte semântica também passou a
oferecer as consultas portáteis `buscarValorInteiroVisivel(...)` e
`buscarParticipante(...)`. A `Main` usa essas consultas prontas e não
inspeciona mais `isDesconhecido()` nem converte texto curado nos caminhos de
renderização. O mesmo contrato pode ser consumido pela API sem duplicar a
regra no React ou no futuro cliente móvel.

Não houve mudança funcional: valores conhecidos continuam disponíveis,
incógnitas continuam ocultas, e participantes continuam vindo da curadoria.
Verificação: compilação isolada de `Main.java`,
`TesteComparacaoBarrasCuradoria` aprovado e
`TesteProtecaoIncognitaEstadoCompartilhado` aprovado com 11 verificações.

## Corte: natureza da representação complementar declarada pela cena (2026-09-01)

A `Main` inferia diretamente pela categoria se a representação complementar
era uma coleção, um gráfico de barras de comparação ou o Venn usado nas
demais categorias. Essa classificação aparecia em relato de erro, ajuda,
desenho, interação, escala e tooltips, embora seja uma propriedade portátil
da cena e não do Swing.

`CenaDiagramaVenn` passou a declarar `Natureza` com os valores `COLECOES`,
`BARRAS_COMPARACAO` e `VENN`, além de carregar a natureza materializada em
cada instância. `GeradorCenaDiagramaVenn` atribui essa informação ao criar a
cena. Os predicados transitórios da `Main`, usados pelos adaptadores Swing,
consultam agora a classificação pertencente à cena em vez de codificar as
categorias localmente.

Esse metadado pode integrar a projeção JSON da API: o React ou um cliente
móvel escolhe o componente gráfico solicitado, mas não decide qual
representação corresponde à situação. Não houve mudança visual ou
interativa. Verificação: compilação isolada de `Main.java`, construção das
seis categorias em `TesteAreaDiagramaPortatil`, materialização da natureza
nas seis cenas em `TesteComparacaoBarrasCuradoria`, dois pilotos estruturais
e servidor web respondendo HTTP 200.

## Corte: forma dos nós declarada pela cena complementar (2026-09-01)

Mesmo após a extração da natureza, a `Main` ainda deduzia em dois pontos se
cada zona complementar deveria ser retangular ou elíptica, comparando a
categoria da situação. `NoDiagramaVenn` passou a declarar `Forma.ELIPSE` ou
`Forma.RETANGULO`; geradores e layouts materializam a forma juntamente com
posição, tamanho, rótulo e valor. A adaptação Swing apenas copia
`no.getForma()` para o componente legado.

Não houve mudança visual: Composição de Medidas, Transformação de Medidas,
Comparação de Medidas e Composição de Transformações preservam zonas
retangulares; Transformação de Relação e Composição de Relações preservam
elipses. A forma passa a estar disponível para serialização pela API sem o
React inferir categorias. Verificação: `Main.java` compilou, testes de cena
portátil e barras passaram para as seis categorias, e o servidor permaneceu
respondendo HTTP 200.

## Direção acordada: rascunho de consistência visual no cliente

Estados intermediários de arraste/edição serão efêmeros e mantidos somente
em memória no cliente. Não serão persistidos em arquivo, banco, curadoria ou
`localStorage`. O React pode propagar imediatamente o mesmo rascunho entre
suas representações sem requisições por movimento; somente uma ação
semanticamente concluída (`drop`, confirmação de valor ou mudança de sinal)
é enviada ao servidor. O servidor continua proprietário da validação
matemática, recálculo, proteção da incógnita e scaffolding, devolvendo então
um novo snapshot autoritativo. A implementação desse fluxo depende de diff
prévio e aprovação explícita, conforme a proteção registrada na skill de
consistência de estado.

### Implementação inicial do rascunho efêmero

`web-poc/src/estadoRepresentacoes.ts` introduz um reducer exclusivamente em
memória, separado da API. Ele mantém o último snapshot autoritativo recebido,
posições visuais provisórias por identidade de figura e o identificador de
uma ação pendente. Receber qualquer novo snapshot do servidor descarta todo
o rascunho. O reducer não importa `api.ts`, não conhece `fetch`, relações
matemáticas, curadoria ou persistência do navegador.

`App.tsx` passou a receber snapshots por esse reducer, e
`GeradorCenaGerard.tsx` projeta posições efêmeras quando existirem, usando a
geometria da API nos demais casos. Nenhum gesto foi habilitado ainda: a API
precisa publicar previamente as capacidades permitidas por figura, para que
o React não as infira pela categoria.

Verificação atual:

- teste isolado do reducer confirmou retenção do rascunho entre snapshots e
  descarte ao chegar um novo snapshot;
- busca estática confirmou que `fetch` continua restrito a `api.ts` e não há
  `localStorage`/`sessionStorage`;
- TypeScript e Vite compilaram 199 módulos;
- linha de base antes e depois: 104 testes Java aprovados, 5 gráficos
  compilados/não executados por exigirem display e zero reprovações;
- servidor respondeu HTTP 200 com o novo bundle.

Durante a criação da linha de base foi corrigido um vestígio independente:
`SemanticComponentLocator` ainda referenciava o campo removido
`quantidadePassosTransformacaoComposta`. Ele agora usa diretamente o overload
canônico de `CatalogoPapeisSemanticosAditivos`, permitindo novamente a
compilação completa das 548 fontes.

## Corte: capacidades de interação publicadas pela API (2026-09-02)

A cena portátil não autorizava interações por figura. Habilitar o primeiro
editor diretamente no React pela categoria, forma ou posição recriaria
conhecimento de aplicação no cliente. `ServicoSorteioAtividadeWeb` passou a
projetar `interacoes_permitidas` em cada figura a partir das ações que o
servidor já anunciou no mesmo snapshot.

Para a modelagem web hoje existente, somente a figura cujo
`chave_papel_semantico` coincide com o `papel_id` de
`PROPOR_VALOR_PAPEL` recebe:

```json
{
  "tipo": "EDITAR_VALOR",
  "acao_id": "PROPOR_VALOR_PAPEL",
  "fase_envio": "CONFIRMACAO",
  "papel_id": "papel.todo"
}
```

Todas as demais figuras recebem uma lista vazia. Assim, as cinco categorias
sem serviço de modelagem web permanecem visíveis, mas não são tornadas
interativas por suposição do cliente.

O React consome essa capacidade para permitir clique ou teclado somente na
figura autorizada. Digitação fica em `EstadoRepresentacoes` na memória e não
faz HTTP. `Confirmar` localiza a ação anunciada pelo servidor e chama uma
única vez `api.posicionar`; `Cancelar` apenas descarta o rascunho. O servidor
continua validando a proposta e devolvendo o snapshot autoritativo.

Verificação:

- resposta HTTP real de Composição de Medidas: Parte 1 e Parte 2 sem
  capacidades; Todo com `EDITAR_VALOR`;
- resposta HTTP real de Comparação de Medidas: três listas vazias;
- `TesteServicoSorteioAtividadeWeb` verifica identidade semântica e
  autorização por papel;
- teste isolado do reducer confirma digitação efêmera e descarte no snapshot;
- TypeScript/Vite: 199 módulos compilados;
- regressão Java pós-contrato: 104 aprovados, 5 gráficos compilados/não
  executados, zero reprovações.

## Corte: modelagem web de Transformação de Medidas (2026-09-02)

O ciclo funcional foi ampliado sem ensinar semântica ao React.
`ServicoAtividadeWebTransformacaoMedidas` constrói os papéis pela
`FabricaPapeisTransformacaoMedidas`, identifica a incógnita curada e delega
a validação da proposta à `RelacaoEstruturalTransformacao`. O serviço comum
`ServicoAtividadeWeb` permite que o sorteio coordene Composição e
Transformação de Medidas sem depender de uma implementação específica.

Depois do acerto da categoria, a API publica `EDITAR_VALOR` somente na figura
correspondente à incógnita. A digitação continua efêmera no cliente e apenas
a confirmação produz uma requisição; propostas incorretas não alteram o
estado semântico do servidor. As outras quatro categorias permanecem sem
modelagem interativa, em vez de receberem comportamento inferido no cliente.

Verificação:

- `TesteServicoAtividadeWebTransformacaoMedidas`: rejeição sem mutação e
  aceitação/conclusão pelo valor curado;
- `TestePilotoTransformacaoMedidas` e `TesteServicoSorteioAtividadeWeb`
  aprovados;
- API HTTP real: três figuras e uma única capacidade, no papel curado
  `papel.estadoFinal` da situação sorteada;
- TypeScript/Vite: 199 módulos compilados;
- regressão integral: 105 testes aprovados, 5 gráficos compilados/não
  executados e zero reprovações.

## Corte: modelagem web de Comparação de Medidas (2026-09-02)

`ServicoAtividadeWebComparacaoMedidas` coordena os três papéis produzidos por
`FabricaPapeisComparacaoMedidas` e delega o diagnóstico matemático a
`RelacaoEstruturalComparacao`. A equação, os domínios (medidas naturais e
valor relativo inteiro), a orientação do valor relativo e o reconhecimento
de operação invertida permanecem no domínio; o cliente recebe somente o
snapshot e a capacidade associada à incógnita curada.

Propostas rejeitadas não preenchem o papel. Uma proposta aceita é aplicada
ao proprietário semântico e a conclusão depende da consistência da relação
`Referendo = Referido + ValorRelativo`. Não foi acrescentada consulta durante
a digitação: o envio continua ocorrendo apenas em `CONFIRMACAO`.

Verificação:

- teste próprio do serviço cobre anúncio da incógnita, rejeição sem mutação e
  conclusão pelo valor curado;
- o piloto de Comparação preservou diferenças positivas, negativas e nulas,
  os domínios dos papéis, diagnóstico e proteção contra overflow;
- API HTTP real: três figuras e uma única capacidade `EDITAR_VALOR`, no papel
  curado `papel.referido` da situação sorteada;
- regressão integral repetida: 106 testes aprovados, 5 gráficos
  compilados/não executados e zero reprovações. Uma execução anterior teve
  oscilação temporal isolada em `TesteSequenciadorFeedbackConclusao`; o mesmo
  binário passou isoladamente e na repetição integral, sem alteração nesse
  componente.

## Auditoria das categorias de Relações (2026-09-02)

A tentativa de ampliar o mesmo serviço ternário para Transformação de
Relação foi interrompida antes de ser publicada porque um teste com a primeira
situação validada revelou que a soma simples não preserva a semântica curada.
Na situação das bonecas, a relação inicial `+3`, a transformação `+5` e a
relação final `+2` dependem de quem sofreu o evento e da inversão dos
participantes entre as relações inicial e final. Tratar o resultado como
`+3 + +5` produziria `+8`, matematicamente errado para a narrativa.

O domínio já contém `RelacaoEstruturalTransformacaoDeRelacaoOrientada`, que
possui esse conhecimento a partir de referências narrativas explícitas, e
`ConversorSituacaoProblemaRica` sabe construí-la. Contudo, o
`ContextoCarregamentoAtividade` consumido pela API ainda transporta apenas o
registro tabular, a definição e a interpretação textual; não transporta a
`EstruturaAditiva` rica. O runtime de consistência do desktop também chega à
relação pelo `CatalogoRelacoesEstruturaisAditivas`, que usa a versão ternária
simples. A ponte rica permanece concentrada no fluxo de curadoria.

Não há sidecars de narrativas ricas no diretório padrão desta instalação.
Assim, inventar orientação a partir de personagens posicionais ou apenas de
`operacao_relacao` violaria a localidade do conhecimento. O próximo corte
necessário é integrar ao carregamento um resultado rico explicitamente
curado (ou uma indisponibilidade diagnosticada), para que desktop e API
consumam a mesma relação estrutural. A tentativa simplificada e seu teste
foram removidos; Transformação de Relação continua visível, mas sem capacidade
interativa na API.

### Ponte rica entregue à aplicação

`FachadaCarregamentoAtividade` passou a consultar
`ServicoSituacaoProblemaRicaCurada` e incluir seu resultado em
`ContextoCarregamentoAtividade`. A compatibilidade é conservadora: ausência
ou falha de leitura do sidecar não impede a exibição tabular e produz um
diagnóstico explícito; não há inferência por texto, posição ou personagem.

`RelacaoEstruturalTransformacaoDeRelacaoOrientada` agora implementa o mesmo
contrato de diagnóstico de propostas das demais relações. O serviço
`ServicoAtividadeWebTransformacaoRelacaoRica` cria a tentativa a partir do
agregado validado e deixa vazia somente a incógnita original. O sorteio o
ativa exclusivamente quando o contexto informa uma situação rica válida.
Logo, a instalação atual, sem sidecars ricos, continua sem oferecer uma
capacidade enganosa; quando a curadoria existir, API e cliente não precisarão
reinterpretar sua orientação.

O teste rico das bonecas confirma a diferença essencial: proposta `+8`
(soma superficial) é rejeitada sem concluir, enquanto `+2` é aceita e conclui
a relação orientada. A regressão integral compilou 552 fontes e 112 testes:
107 executáveis aprovados, 5 gráficos compilados/não executados e nenhuma
reprovação. TypeScript/Vite compilou 199 módulos.
### Corte incremental: seleção do plano de unidades de transformação

- A `Main.TelaGerard` não instancia mais os dois sincronizadores semânticos
  nem repete o despacho entre processo simples e composição de
  transformações.
- `SelecionadorPlanoUnidadesTransformacao`, independente de Swing, recebe o
  tipo de representação já decidido por `SeletorRepresentacaoComplementar` e
  delega ao sincronizador proprietário. As outras representações retornam
  `null`, preservando o comportamento anterior.
- A categoria não é reinferida pelo estado nem pelo cliente: a decisão
  existente continua centralizada no seletor de representação.
- Verificação posterior: 553 fontes compiladas; 108 testes executáveis
  aprovados; 5 testes gráficos compilados e não executados por exigirem
  display; zero reprovações.

### Corte incremental: conclusão integral fora da Main

- `ControladorConclusaoModelagem` passou a receber também o fato de que os
  requisitos adicionais da atividade foram satisfeitos. A sobrecarga anterior
  permanece compatível e assume que não existem requisitos adicionais.
- A `Main` ainda adapta o estado dos seletores Swing, mas não combina mais
  esse resultado com a conclusão dos papéis nem mantém uma segunda memória de
  transição. `CONCLUIDA_AGORA` é produzido pelo controlador portátil e pode
  ser consumido futuramente por API, desktop ou mobile.
- O protocolo visual de feedback permaneceu intacto: destaque, atraso, selo,
  tip, cancelamento e mensagens continuam sendo materializados pelo Swing.
- `TesteConclusaoModelagem` cobre agora papéis completos com operação
  pendente, conclusão ao responder a operação e retorno ao estado incompleto.
- Verificação posterior: 553 fontes compiladas; 108 testes executáveis
  aprovados; 5 testes gráficos compilados e não executados por exigirem
  display; zero reprovações.

### Corte incremental: identidade específica de papel fora da Main

- A enumeração de chaves de papéis consideradas específicas deixou
  `Main.obterChavePapelExataDoItem` e passou para
  `CatalogoPapeisSemanticosAditivos.chavePapelEspecifica`.
- O conjunto anterior foi preservado exatamente, incluindo o sinônimo
  histórico `papel.referente` e o prefixo legado `papel.transformacao*`;
  `papel.valor`, `null` e chaves desconhecidas continuam rejeitados.
- A tela conserva somente a adaptação de `ItemTextoArrastavel`; o critério
  de identidade pode agora ser consumido pelos adaptadores desktop, web e
  mobile sem dependência de Swing.
- Verificação posterior: 553 fontes compiladas; 109 testes executáveis
  aprovados; 5 testes gráficos compilados e não executados por exigirem
  display; zero reprovações.

### Corte incremental: projeção dos valores da comparação fora da Main

- `ProjetorValoresComparacaoComplementar`, independente de Swing e de
  geometria, passou a possuir a precedência entre valores modelados,
  valor relativo curado visível e resolução pela relação estrutural.
- A regra de não antecipar a curadoria antes de qualquer modelagem foi
  preservada; Referido e Referendo continuam vindo apenas da modelagem, e a
  incógnita continua protegida por `buscarValorInteiroVisivel`.
- A `Main` agora apenas localiza os três elementos Swing, extrai seus valores
  observados e solicita a projeção. As leituras curadas de Referido e
  Referendo, que eram calculadas mas nunca usadas, foram removidas.
- O teste dedicado cobre cena vazia, precedência do valor modelado, fallback
  curado, resolução estrutural, domínio natural das medidas e preservação do
  sinal relativo.
- Verificação posterior: 554 fontes compiladas; 110 testes executáveis
  aprovados; 5 testes gráficos compilados e não executados por exigirem
  display; zero reprovações.

### Corte incremental: objeto factual do log por identidade semântica

- `Main.obterObjetoParaLog` deixou de inferir `OBJ1…OBJ8` por palavras do
  rótulo localizado (`estado inicial`, `referente`, `parte` etc.) e pela
  propriedade visual `exibirLupa`.
- `CatalogoObjetosLogAcaoInstrumental`, independente de interface e idioma,
  associa a chave semântica ao vocabulário factual legado. Papéis de
  transformação/relação usam sua natureza semântica; Referendo e o alias
  histórico Referente convergem em `OBJ3`.
- A mudança corrige a divergência em que `papel.referendo` podia cair em
  `OBJ8` porque a tradução exibida não continha a palavra portuguesa
  `referente`.
- Limite deste corte: a soltura `POSICIONAR` ainda grava pelo caminho legado
  `registrarLogUsuario`. A migração para um único
  `RegistroFactualAcaoInstrumental` produzido pelo proprietário semântico
  permanece pendente; o catálogo não deve ser interpretado como conclusão
  dessa migração.
- Verificação posterior: 555 fontes compiladas; 111 testes executáveis
  aprovados; 5 testes gráficos compilados e não executados por exigirem
  display; zero reprovações.
