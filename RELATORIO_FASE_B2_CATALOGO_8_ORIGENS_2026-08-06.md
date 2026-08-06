# Fase B2, passo 2 — catálogo das 8 origens em Main.java

Data: 2026-08-06. Somente leitura — nenhum código tocado, nenhum commit. Escopo: passo 2 da Fase B já registrado em `TAREFA_PENDENTE_LOCALIDADE_CONHECIMENTO_ESTADO_COMPARTILHADO.md` — para cada uma das 8 origens (`Origem.VERGNAUD`, `DIAGRAMA_COMPLEMENTAR`, `EIXO_X`, `EIXO_VERTICAL`, `EDICAO_TEXTO`, `ARRASTE`, `EXCLUSAO`, `PROTOCOLO`), documentar o que cada uma faz e depende de `EstadoSemanticoCompartilhado` para. Pré-requisito para decidir a migração (B2) — nenhuma decisão tomada aqui.

---

## 1. Todas as origens (exceto duas exceções da seção 3) passam pelo mesmo funil de dois métodos

`sincronizarTodasAsRepresentacoesAPartirDoVergnaud(elemento, origem)` (linha 7299) e `sincronizarTodasAsRepresentacoesAPartirDoDiagramaComplementar(indiceAlterado, origem)` (linha 7308) são a porta de entrada única: cada uma captura o estado atual (`capturarEstadoCompartilhadoDoVergnaud`/`capturarEstadoCompartilhadoDoDiagramaComplementar`), chama `estadoSemanticoCompartilhado.atualizar(...)`, e aplica o resultado de volta em todas as representações via `aplicarEstadoCompartilhadoEmTodasAsRepresentacoes(snapshot, true)`. As 8 origens são só o rótulo que cada chamador passa para esse funil — confirma o achado já registrado na investigação anterior (o funil é estrutural, não são 8 caminhos paralelos).

## 2. Catálogo das 8 origens

| Origem | Onde/quando dispara | O que lê antes de sincronizar | Depende de `EstadoSemanticoCompartilhado` para |
|---|---|---|---|
| `VERGNAUD` | `sincronizarDiagramaVennComRepresentacoes` (L6933) — resync interno sempre que o diagrama Venn precisa refletir o Vergnaud, `indice=-1` (nenhum elemento específico) | Valores atuais dos elementos do Vergnaud | Resolver a relação aditiva sem um índice-alvo específico (cai no ramo "achar a única incógnita") e servir de fonte para os quadradinhos iniciais do Venn |
| `DIAGRAMA_COMPLEMENTAR` | Interação direta no diagrama Venn: ajuste de valor assinado por +/- (L8919), soltura de item (várias) | Contagem de quadradinhos por agrupamento, texto editável do nó | Mapear índice visual→semântico e resolver o papel dependente |
| `EIXO_VERTICAL` | Arraste do controle da barra de comparação, a cada movimento do mouse (`atualizarBarrasComparacaoAPartirDoControle`, L9672) | Valor corrente do controle da barra | Propagar o valor arrastado para o elemento "diferença" do Vergnaud e sincronizar |
| `EIXO_X` | Navegação no eixo x dos inteiros (L11292) | Valor do ponto no eixo | Propagar para o elemento Vergnaud correspondente, só depois de confirmação (`liberadoParaPropagar`) |
| `EDICAO_TEXTO` | Edição direta de texto em um elemento (3 pontos: L11428 genérico; L12166 diálogo de valor; L12243 valor relativo do gráfico de comparação) | Texto recém-digitado, convertido para número | Recalcular o(s) papel(is) dependente(s) a partir do texto editado |
| `ARRASTE` | Soltar um item arrastado — quadradinho do Venn (L10775), item de texto sobre um papel do Vergnaud (L11413), ou escolha de sinal via menu (L12060, tratado como conclusão de um posicionamento) | Posição final do item solto, ou sinal escolhido | Resolver o papel dependente após o posicionamento concluído |
| `EXCLUSAO` | Delete/Backspace com um quadradinho do Venn focado (L13114-13132) | Qual quadradinho foi removido, de qual agrupamento | Recalcular a partir da remoção |
| `PROTOCOLO` | Ver seção 3 — **não é o que o nome sugere** | — | — |

## 3. Achado principal: `PROTOCOLO` não significa "replay de protocolo de pesquisa" na maior parte dos usos — é o rótulo padrão de fallback

**[EVIDÊNCIA NO CÓDIGO]** Dentro do próprio `EstadoSemanticoCompartilhado.atualizar()`: `origem = novaOrigem == null ? Origem.PROTOCOLO : novaOrigem;` (linha 152, e igual na cópia `EstadoSemanticoCompartilhadoOriginal`). `PROTOCOLO` é literalmente o valor-padrão quando nenhuma origem mais específica é passada — não um rótulo reservado para replay de arquivo de protocolo de pesquisa.

Os 3 usos explícitos de `Origem.PROTOCOLO` em `Main.java` confirmam isso — nenhum deles é replay de protocolo:

- **L8811-8816** (`simularEstadoCompartilhadoAposAlteracaoValorAssinado`): cria uma instância **descartável** de `EstadoSemanticoCompartilhado` (`new EstadoSemanticoCompartilhado()`, não a instância real) só para simular "o que aconteceria se eu incrementasse/decrementasse este valor" — usado para decidir se os botões +/- devem ficar habilitados (`podeIncrementarValorAssinadoTransformacao`/`podeDecrementarValorAssinadoTransformacao`). Nunca toca o estado real.
- **L11886-11891** (`restaurarValorRelativoPositivoSeguro`): o sistema corrige um valor para um "seguro" positivo depois de um estado inválido — correção iniciada pelo sistema, não pelo usuário.
- **L12206-12217** (`aplicarEdicaoValorRelativoComparacao`, ramo de bloqueio): o sistema resincroniza depois de bloquear um valor negativo — outra correção interna.

`SimuladorEstadoComplementarVenn.java` (um dos 7 arquivos satélites, já catalogado como "só lê Snapshot" na investigação anterior) segue o mesmo padrão: usa uma instância descartável com `Origem.PROTOCOLO` para decidir, sem tocar o estado real, se um novo valor respeitaria os limites de não-negatividade — é a versão extraída dessa mesma lógica de simulação.

**Implicação para a Fase B2**: o modelo de "8 origens = 8 tipos de ação do usuário" que o plano original presumia não é exatamente isso. `PROTOCOLO` é, na prática, "correção/simulação do sistema, sem uma origem de UI mais específica" — três usos concretos, nenhum sendo replay de arquivo. Migrar para os objetos do piloto (que têm `OrigemAcao.ORIGEM_SISTEMA` como conceito already-existente e correto para isso) na verdade se encaixa bem aqui — `PROTOCOLO` mapearia naturalmente para `ORIGEM_SISTEMA`, não para uma nova categoria. Mas isso precisa ser decidido explicitamente, não presumido: será que o replay de protocolos reais (`protocolos_reais_replay.tsv`, citado nos relatórios anteriores) passa por algum outro caminho, fora de `Origem.PROTOCOLO`? Não confirmado nesta investigação — os arquivos de replay (`TesteReplayProtocolosReais.java`, `RepositorioProtocolosReaisReplay`) chamam diretamente os agentes (Monitor/ZDP/Modelador), não a cadeia de sincronização de `Main.java` — então "replay" e "sincronização de tela" parecem ser dois sistemas paralelos, não o mesmo caminho. Vale confirmar antes de decidir a B2.

## 4. O que isso muda para a decisão da Fase B2

Nenhuma decisão tomada aqui — só o levantamento pedido. Três pontos que a decisão da B2 precisaria enfrentar, agora com evidência concreta em vez de suposição:

- O funil de 2 métodos (`sincronizarTodasAsRepresentacoesAPartirDoVergnaud`/`...DoDiagramaComplementar`) é o ponto real de entrada — migrar significa reescrever esses 2 métodos e os ~18 pontos de chamada que passam por eles, não 8 pontos independentes.
- `PROTOCOLO` não é uma origem de UI como as outras 7 — é "correção interna do sistema". Um mapeamento direto para `OrigemAcao.ORIGEM_SISTEMA` do pacote piloto parece natural, mas depende de confirmar que não há um uso real de replay de protocolo passando por aqui que eu não tenha encontrado.
- Há pelo menos 2 usos de instância **descartável** de `EstadoSemanticoCompartilhado` (simulação de +/-, em `Main.java` e em `SimuladorEstadoComplementarVenn`) — migrar para os objetos do piloto precisaria preservar essa capacidade de "simular sem efeito colateral", que os objetos do piloto (mutáveis, mas sem essa noção de instância descartável dedicada) não têm de graça.

## O que não foi feito

Nenhum código alterado, nenhum arquivo de produção tocado, nenhum commit. Decisão sobre como (ou se) prosseguir com a Fase B2 continua seu.
