# Extração do desenho de pista visual para EstrategiaEstiloInteracao

Data: 2026-08-06. Motivado pela discussão "Gerard é essencialmente interação, protocolos de mouse e estilos de interação — talvez isso esteja deixando a Main grande". Investigação (só leitura) mostrou que a hipótese era parcialmente certa, mas mais estreita do que parecia — ver seção 1. A extração feita aqui (única aprovada) é o achado concreto dessa investigação.

## 1. O que a investigação encontrou

`EstiloInteracao` (`gerard.estilointeracao`) já existe como abstração de primeira classe — enum de 5 estilos + Strategy (`EstrategiaEstiloInteracao`/`EstrategiasEstiloInteracao`) + registro. Já está em uso real de produção, não é código morto: `ScaffoldingProximidade` delega a ela para decidir o estado de realce (`EstadoRealceAlvo`) e se aplica atração magnética, chamada a partir de `atualizarRealceAlvoProximidade` em `Main.java`, no arraste de um marcador de texto do enunciado até um elemento de Vergnaud — a única interação do sistema que passa por esse mecanismo.

O restante dos handlers de mouse (`mousePressed`, `mouseDragged`, `mouseReleased`, `mouseMoved`) **não** é "estilo de interação" nesse vocabulário — é despacho de hit-test entre ~9 tipos de alvo, bookkeeping de seleção, plumbing de cursor/fantasma de arraste e log granular de pesquisa. Isso é orquestração legítima da `Main`, fora do escopo desta mudança.

**Achado concreto**: `desenharPistaVisualDoModo` (`Main.java`, então linhas 6520-6569) duplicava o despacho por `EstiloInteracao` — só que para decidir *como desenhar* a pista de cada estilo, não o estado. A abstração já existente (`EstrategiaEstiloInteracao.calcularEstado`) cobria só a metade "decidir"; a metade "desenhar" continuava como um `if/else` solto em `Main.java`, ramificando de novo sobre o mesmo enum.

## 2. O que foi feito

- `EstrategiaEstiloInteracao` (interface): novo método `desenhar(Graphics2D g2, int itemX, itemY, itemLargura, itemAltura, alvoX, alvoY, alvoLargura, alvoAltura, boolean proximo, dentro)`. Assinatura em primitivos — a estratégia não passou a depender de `ItemTextoArrastavel`/`ElementoVergnaud` (classes de `Main.java`), preservando o isolamento do pacote `gerard.estilointeracao`.
- `EstrategiasEstiloInteracao`: cada uma das 5 estratégias ganhou sua implementação de `desenhar`, com o código movido **verbatim** (mesma aritmética, mesmas constantes) das 5 ramificações que estavam em `desenharPistaVisualDoModo`. Os dois helpers de desenho (`desenharCantosDeEnquadramento`, `desenharSetaCurta`) também migraram para cá como métodos `private static`, usados só pelas estratégias AFFORDANCE e SNAP_TO_TARGET respectivamente — eram usados só ali em `Main.java` também (confirmado por busca antes de mover).
- `ScaffoldingProximidade`: novo método `desenhar(...)`, passthrough consistente com os três já existentes (`calcularEstadoAlvo`, `deveAplicarAtracaoMagnetica`, `deveCentralizarAoSoltar`).
- `Main.java`: `desenharPistaVisualDoModo` passou a só extrair a geometria de `item`/`alvo` e delegar a `scaffoldingProximidade.desenhar(modoFeedbackTeste, ...)`, mantendo o mesmo save/restore de `Stroke`/`Color` ao redor da chamada. `desenharCantosDeEnquadramento` e `desenharSetaCurta` foram removidos (migraram, não foram duplicados).

## 3. Verificação

- Compilação completa: 435 arquivos, 0 erros (`javac` com `weka-stable-3.8.6.jar`/`bounce-0.18.jar` no classpath).
- Revisão linha a linha: cada um dos 5 blocos de desenho foi comparado contra o original antes do commit — mesmas fórmulas, mesmas constantes de cor/traço, mesma condição de guarda (`proximo`/`dentro`) por estilo.
- Busca por referências soltas após a remoção: nenhuma ocorrência restante de `desenharCantosDeEnquadramento`/`desenharSetaCurta` em `Main.java`; único uso de `EstiloInteracao.*` remanescente é a declaração do campo `modoFeedbackTeste` (inalterada).
- Não há harness automatizado para o desenho em si (é saída visual, `Graphics2D`) — a verificação foi por equivalência estrutural do código movido, não por execução. Mudança limitada a uma função de renderização sem estado próprio (não haviam efeitos colaterais na lógica de dados/pesquisa).

## 4. Resultado

`Main.java`: 13.662 → 13.593 linhas (-69, líquido). Pequeno em relação ao tamanho do arquivo, mas remove a única duplicação de despacho por `EstiloInteracao` que existia — não é uma nova extração inventada, é completar um padrão (Strategy) que já estava em produção pela metade.
