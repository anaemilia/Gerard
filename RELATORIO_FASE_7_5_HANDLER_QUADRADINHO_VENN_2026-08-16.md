# Fase 7.5 — handler do quadradinho do diagrama de Venn

Data: 2026-08-16

## Objetivo

Continuar o roteiro incremental de `gerard-handlers-de-interacao` depois da Fase 7.4
(`HandlerInteracaoElementosDiagramaVergnaud`). Escolha da usuária, 2026-08-16: "ordene pelo mais
fácil e pode começar. O item 4 deixe pendente." — avaliada a dificuldade relativa dos dois
protocolos restantes (quadradinhos do diagrama Venn e eixo de inteiros) por contagem de
referências e complexidade dos pontos de chamada em `Main.java`; quadradinhos do Venn era o mais
simples dos dois, então começado primeiro. Item 4 (representação complementar de Relações,
projeto de valores negativos) permanece explicitamente pendente, não tocado.

Extrai de `Main.TelaGerard` o estado mecânico do gesto de reposicionar um `QuadradinhoVenn` já
colocado no diagrama complementar — pickup, deslocamento livre e detecção de qual `CirculoVenn`
continha o quadradinho no início e no fim do gesto.

## Fronteira de conhecimento adotada

`HandlerInteracaoQuadradinhoVenn` conhece somente:

- o quadradinho ativo do gesto;
- o deslocamento entre o ponteiro e o canto do quadradinho no pickup;
- movimento livre até a posição do ponteiro (sem limiar de arraste estrutural nem clamp por
  zona — mesmo comportamento de antes da extração; o quadradinho fica dentro do painel
  complementar por convenção de uso da interface, não por uma restrição geométrica aplicada
  aqui);
- qual círculo continha o quadradinho no início do gesto (guardado como fallback) e qual o
  contém ao final (`ResultadoSoltura`, mesmo padrão de "fatos do gesto, não decisão" já usado em
  `HandlerInteracaoItemTextoArrastavel.ResultadoSoltura`).

Deliberadamente fora do escopo, permanecem em `Main.TelaGerard`:

- hit-testing do quadradinho (localizar qual está sob o ponteiro);
- hover/foco fora de um arraste (`quadradinhoVennFocado` — usado por duplo-clique e tecla
  Delete, não é gesto de arraste; confirmado por grep completo de todos os usos que este campo é
  conceito separado do pickup e continua na tela sem alteração);
- os controles de clique de adicionar/remover quadradinho (affordance compartilhada com outras
  representações complementares, não exclusiva deste protocolo);
- duplo-clique para editar texto;
- a sincronização semântica em si
  (`sincronizarTodasAsRepresentacoesAPartirDoDiagramaComplementar`) — o handler só devolve os
  índices de origem/destino, quem chama decide o que fazer.

Mesma divisão de responsabilidade das Fases 7.2/7.3/7.4: o handler não decide semântica, só
mecânica do gesto.

## Alterações

- criado `src/gerard/interacao/arraste/HandlerInteracaoQuadradinhoVenn.java`;
- `Main.TelaGerard` passou a delegar pickup, movimento, conclusão (com resultado próprio) e
  cancelamento ao handler, em vez de manter os campos soltos `quadradinhoVennSelecionado`,
  `deslocamentoVennX`, `deslocamentoVennY`, `indiceCirculoVennOrigemArraste` (removidos);
  `quadradinhoVennFocado` foi mantido na tela, por ser conceito de hover/foco separado do
  pickup;
- os pontos que resetavam `quadradinhoVennSelecionado` para `null` (troca de situação, tecla
  Delete, reinício, etc.) passaram a chamar `handlerQuadradinhoVenn.cancelar()`;
- a renderização (pickup em primeiro plano, verificação de "não desenhar duas vezes" no fluxo
  normal, estado visual "arrastada") passou a ler `handlerQuadradinhoVenn.obterQuadradinhoAtivo()`
  em vez do campo removido;
- `mousePressed`: pickup do quadradinho reescrito para chamar `handlerQuadradinhoVenn.iniciar(...)`,
  preservando o cancelamento incondicional do gesto anterior no início do hit-testing (mesmo
  comportamento de antes da extração, independentemente de o clique resultar em novo pickup ou
  não);
- `mouseDragged`: movimento delegado a `handlerQuadradinhoVenn.mover(x, y)`;
- `mouseReleased`: a computação de índice de origem/destino que antes era inline passou a vir de
  `handlerQuadradinhoVenn.concluir(circulosVenn)` (`ResultadoSoltura`), chamada no mesmo ponto do
  processamento em que a versão anterior calculava esses índices; a sincronização semântica
  passou a usar `resultadoSolturaQuadradinho.obterIndiceParaSincronizacao()` (destino se
  encontrado, senão origem — mesma regra de fallback de antes);
- método morto `sincronizarVergnaudAPartirDosQuadradinhosVenn()` (confirmado via grep: não é
  chamado em lugar nenhum) reescrito para ler `handlerQuadradinhoVenn.obterQuadradinhoAtivo()`;
  como consequência, seu fallback de índice de origem, hoje inalcançável, foi simplificado — sem
  efeito em tempo de execução, por ser código morto;
- criado teste unitário do contrato do handler
  (`tests/java/TesteHandlerInteracaoQuadradinhoVenn.java`), cobrindo: no-op seguro sem gesto
  ativo, `iniciar(null, ...)`, deslocamento e movimento livre sem limiar/clamp, soltura fora de
  qualquer círculo (fallback para a origem), soltura dentro de outro círculo (destino tem
  prioridade sobre a origem), `cancelar()` no meio do gesto, e lista de círculos nula (sem
  exceção);
- acrescentada a seção "Fase 7.5" ao verificador de regressão
  (`scripts/verificar_regressao_gerard.py`): existência da classe e do fio de chamadas
  (`iniciar`/`mover`/`concluir`/`cancelar`/`estaAtivo`) em `Main.java`, ausência dos quatro campos
  antigos, independência do handler em relação à tela e ao hit-testing, permanência de
  `quadradinhoVennFocado` na tela, e que a tela continua decidindo o hit-testing do pickup;
  também corrigida uma checagem pré-existente (não escrita nesta sessão, parte da proteção mais
  antiga de "elemento segurado é redesenhado em primeiro plano") que ainda citava o campo antigo
  `quadradinhoVennSelecionado` — atualizada para `handlerQuadradinhoVenn.obterQuadradinhoAtivo()`.

## Autoarmadilhas encontradas e corrigidas nesta própria sessão (antes de pedir validação real)

1. O Javadoc inicial da classe nova mencionava literalmente `Main.TelaGerard` e
   `encontrarQuadradinhoVenn` para explicar o que fica fora do escopo — texto correto, mas que
   faria a própria checagem nova de independência (`'Main' not in handler_quadradinho and ...
   'encontrarQuadradinhoVenn' not in handler_quadradinho`) falhar por menção em prosa, mesmo
   padrão de armadilha já visto na Fase 7.4. Encontrado por grep proativo e corrigido antes de
   rodar qualquer simulação.
2. A primeira simulação completa (cópia-sombra, `ant` stubado) reprovou com 1 falha: a checagem
   pré-existente descrita acima, que ninguém desta sessão havia tocado, mas que ficou
   desatualizada pela remoção do campo `quadradinhoVennSelecionado`. Corrigida, e então feita uma
   varredura por grep de todo o arquivo do verificador pelos quatro nomes de campo removidos para
   confirmar que nenhuma outra checagem pré-existente dependia deles — só restaram as referências
   da própria seção nova da Fase 7.5, que corretamente afirmam a ausência desses nomes.

## Verificação feita nesta sessão (sem JDK neste ambiente)

- Balanceamento de chaves/parênteses/colchetes verificado no arquivo novo e em `Main.java` nos
  trechos alterados — sem desbalanceamento.
- Revisão manual, linha a linha, do handler novo e do teste novo — não pude compilar/rodar
  `javac`/`java` neste ambiente (sem JDK, sem acesso de rede para instalar um).
- Simulação completa do verificador de regressão (cópia-sombra do repositório, com o único passo
  que exige JDK — `ant clean jar` — substituído por um stub que marca sucesso), rodada duas
  vezes: a primeira reprovou pela autoarmadilha pré-existente descrita acima (1 `[ERRO]`); depois
  da correção, cópia-sombra nova a partir do zero: **todas as checagens `[OK]`, 0 `[ERRO]`,
  `APROVADO: verificador de regressão completo, nenhuma falha registrada.`** — inclui as 6
  checagens novas desta fase e todas as checagens das fases e itens anteriores (nada foi quebrado
  por esta mudança).

## Nuance de cobertura do harness Robot

O catálogo de episódios reais usado por `TesteMonkeyGuiadoPorCasosReais`
(`RepositorioProtocolosReaisReplay`) não contém nenhuma referência a "quadradinho" — os 14
episódios gravados (Felipe Wanderley, Jamile, Jamilly — Transformação e Composição de medidas)
não exercitam arraste de `QuadradinhoVenn` no diagrama complementar, mesma limitação de cobertura
já registrada no relatório da Fase 7.4 para o próprio gesto dela. A mecânica específica deste
protocolo (pickup, deslocamento livre, detecção de círculo de origem/destino, fallback de
sincronização) fica coberta pelo teste unitário novo, não pelo harness Robot. Ainda assim,
recomendo rodar o harness Robot real depois do build, como checagem indireta de não-regressão
geral (garante que o hit-testing, `existePickupAtivo()` e a renderização — que passam a consultar
o handler novo em todo evento de mouse do diagrama — continuam funcionando nos 14 episódios sem
exceção nem divergência), mesmo sem cobertura direta deste gesto específico.

## O que falta (precisa rodar no seu ambiente Windows real, na pasta `C:\Users\cecomp\Documents\aemq\git\Gerard`)

1. `ant clean jar` — build completo. Sucesso esperado: `BUILD SUCCESSFUL`.
2. `python scripts\verificar_regressao_gerard.py` — desta vez com o `ant clean jar` de verdade
   (não o stub usado aqui). Sucesso esperado:
   `APROVADO: verificador de regressão completo, nenhuma falha registrada.`
3. Compilar e rodar o teste novo:
   ```
   javac -cp build\classes -d build\classes tests\java\TesteHandlerInteracaoQuadradinhoVenn.java
   java -cp build\classes TesteHandlerInteracaoQuadradinhoVenn
   ```
   Sucesso esperado: termina em uma única linha,
   `Teste aprovado: handler preserva pickup, movimento livre sem limiar/clamp, detecção de
   círculo de origem/destino e fallback de sincronização para o quadradinho do diagrama de
   Venn.` — sem `AssertionError` nem exceção.
4. Harness Robot real (`TesteMonkeyGuiadoPorCasosReais`, mesmo comando já usado nas Fases
   7.2/7.3/7.4), recomendado como checagem de não-regressão geral apesar da nuance de cobertura
   acima. Sucesso esperado: `14 episodio(s) reais rodados via Robot, 45 passo(s) sem divergencia,
   0 divergencia(s)`, arquivos reais restaurados, sem erro/exceção nos logs.
5. Verificação visual (opcional, mas recomendável): rodar o Gerard, chegar a uma
   situação-problema com diagrama complementar (Venn) visível, arrastar um quadradinho de um
   círculo para outro e de volta para o mesmo círculo, e conferir que o movimento continua livre
   e a sincronização das outras representações acontece normalmente ao soltar.

## Conclusão

A extração de `HandlerInteracaoQuadradinhoVenn` está implementada e verificada estruturalmente
neste ambiente (sem JDK) — falta a validação real acima no seu ambiente Windows. Depois dela,
resta do roteiro (`gerard-handlers-de-interacao`) apenas o eixo de inteiros, o mais complexo dos
protocolos restantes, a começar só depois da validação desta fase. Item 4 (representação
complementar de Relações) continua explicitamente pendente, não tocado.
