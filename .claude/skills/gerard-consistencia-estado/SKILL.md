---
name: gerard-consistencia-estado
description: Regras sobre a consistência de estado entre as representações do Gérard (texto, diagrama de Vergnaud, material concreto). Use sempre que for tocar em código relacionado a sincronização de valores semânticos, propagação de estado entre representações, ou ao comportamento "após o primeiro posicionamento" no Gérard. A maior parte deste comportamento já está implementada e verificada em produção (ver seção "Status de verificação"); esta skill existe para PROTEGER a lógica atual, não para reimplementá-la — mas dois pontos abaixo foram corrigidos porque a implementação real diverge do que se assumia.
---

# Consistência de estado entre representações — Gérard

## Status de verificação (2026-07-20; regra 5 confirmada em 2026-08-07; regras 3 e 5 atualizadas em 2026-09-03)

As regras 1, 3, 4 e 5 abaixo foram conferidas linha a linha contra o código atual e se confirmam. A regra 2 se confirma, mas com escopo mais estreito do que "bloqueio geral". As duas afirmações originais sobre `termo_desconhecido` como fonte única e sobre `Main.java` estar livre de lógica semântica eram **falsas** e foram reescritas nas seções correspondentes — não trate versões anteriores deste texto como válidas.

Em 2026-09-03, as regras 3 e 5 foram reconferidas e corrigidas: ambas citavam classes/métodos já removidos por refatorações legítimas registradas em `LEVANTAMENTO_ACOPLAMENTO_MAIN_WEB_2026-08-31.md` (`ScaffoldingReacaoRepresentacoes` apagada em 2026-09-01; o mecanismo único do eixo de inteiros removido por inteiro na mesma data). O **invariante comportamental** que cada regra protege continua verdadeiro — só a localização do código citado estava desatualizada. Números de linha específicos foram removidos onde citados (drift constante e silencioso); prefira grep por nome de método/classe.

Isto não é uma especificação para implementar do zero. É a documentação de um invariante existente, para que qualquer trabalho futuro (refatoração, nova feature, nova plataforma) não quebre esse comportamento sem querer.

## A regra

1. **Estado compartilhado.** O estado semântico é compartilhado entre as representações (texto, diagrama de Vergnaud, material concreto/quadradinhos) através de `gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado`, que mantém os valores centralizados e é acionado a partir de `Main.java` (`aplicarEstadoCompartilhadoEmTodasAsRepresentacoes` e as funções `sincronizarElementosSemanticosDoTexto` / `sincronizarDiagramaVennComRepresentacoes` / `sincronizarEixosComEstadoCompartilhado`).

2. **Bloqueio antes do primeiro posicionamento válido — escopo real.** Antes de haver conteúdo semântico no diagrama de Vergnaud, `gerard.Scaffolding.venn.CondicaoDiagramaVergnaudNaoVazio` desabilita os controles de adicionar/remover quadradinhos no diagrama Venn (`estadoModelagem.possuiConteudoSemantico()`). **Isto não é um freeze geral de todas as representações complementares** — é especificamente o bloqueio dos controles de unidade do Venn. Não presuma bloqueio de edição de texto ou de outros controles com base nesta regra sem checar o ponto específico.

3. **Propagação depois do primeiro posicionamento — atualizado em 2026-09-03.** Mudanças em qualquer valor semântico (`quantidade_1`, `quantidade_2`, `resultado`, `referido`, `referendo`, `valor_relativo`, `termo_desconhecido`) recalculam os slots dependentes via `EstadoSemanticoCompartilhado.resolverRelacaoAditiva`, que delega a `ResolvedorRelacoesEstruturaisAditivas.resolver` (preenche o papel ausente ou recalcula por consistência, para qualquer `TipoSituacaoAditiva`) e daí aos objetos `RelacaoEstrutural*` (`RelacaoEstruturalComposicao`, `RelacaoEstruturalComparacao`, `RelacaoEstruturalTransformacao`...) escolhidos por `CatalogoRelacoesEstruturaisAditivas`. A aritmética pura vive nesses objetos — **não** em `ScaffoldingReacaoRepresentacoes`, que foi removida em 2026-09-01 por estar órfã (`LEVANTAMENTO_ACOPLAMENTO_MAIN_WEB_2026-08-31.md`, "Corte: remoção do scaffolding reativo legado da Main"); sua única utilização produtiva remanescente antes da remoção já era outra (magnitude/sinal do número relativo, hoje em `ServicoQuantidadeContextual`), não `calcularEstadoFinal`/`calcularQuantidadeDependente`. A fiação origem→propagação está em `Main.java`, marcada por `Origem` (ARRASTE, EIXO_X, EIXO_VERTICAL, EDICAO_TEXTO, DIAGRAMA_COMPLEMENTAR etc.).

4. **Sem auto-correção.** Peças já posicionadas corretamente continuam arrastáveis/reposicionáveis — não ficam travadas após o acerto (não existe lock desse tipo no código). Se uma peça movida gerar inconsistência, o sistema **não corrige nem desfaz automaticamente** o estado (não há lógica de undo/auto-correção). O usuário recebe o feedback padrão de erro — tremor (`ScaffoldingFeedbackMultissensorialErro`) + som (`Toolkit.beep()`) + pergunta de confirmação (`ScaffoldingQuestionamento.criarPerguntaConfirmacao`) — e precisa ele mesmo usar um protocolo de mouse para voltar a um estado consistente. Princípio geral: a interface só automatiza passos mediante autorização explícita do pesquisador — nunca por conta própria.

5. **Caso do eixo x das categorias de Relações — confirmado em 2026-08-07, mecanismo substituído em 2026-09-01, invariante reverificado em 2026-09-03.** O mecanismo original desta regra (instância única `itemGraficoInteiros`/`scaffoldingGraficoInteiros`, acionada por `sincronizarNumeroRelativoComGraficoSeNecessario`) foi removido por inteiro em 2026-09-01: rastreamento estático de todos os renderizadores canônicos e dois runs do harness Robot confirmaram que já era código inalcançável em qualquer categoria — desde a Fase 7.7, `devemExibirPaineisEixosRelacoes()` é verdadeiro sempre que existe um número relativo, e os dois pontos de entrada do mecanismo antigo já continham um guard que devolvia antes de fazer qualquer coisa (`LEVANTAMENTO_ACOPLAMENTO_MAIN_WEB_2026-08-31.md`, "Corte: mecanismo antigo do eixo de inteiros removido por inteiro"). O mecanismo vivo é o painel individual por papel (`PaineisEixosRelacoes`, Fase 7.7 de `gerard-handlers-de-interacao`), e o **mesmo invariante continua verdadeiro nele**: `sincronizarPainelEixoRelacaoSeNecessario(boolean confirmarAoFinalizar)` só propaga para as demais representações (`sincronizarTodasAsRepresentacoesAPartirDoVergnaud(..., Origem.EIXO_X)`) quando `liberadoParaPropagar` é verdadeiro — `confirmarAoFinalizar ? confirmarValorIncognitaAceito(itemIncognita) : !incognitaAguardandoConfirmacaoDeValor(itemIncognita)`. `incognitaAguardandoConfirmacaoDeValor` devolve `false` (libera a propagação imediatamente, a cada passo do arrasto) sempre que o item não é a incógnita original ou ainda não foi preenchido pelo protocolo mouse/texto — não é "eixo trava até incógnita terminar" em geral; é "o valor da incógnita não se propaga para as outras representações enquanto está pendente de confirmação", e só isso. O mesmo padrão (`incognitaAguardandoConfirmacaoDeValor`/`confirmarValorIncognitaAceito`) protege igualmente outras origens de escrita marcadas `Origem.EIXO_VERTICAL` e `Origem.ARRASTE` em `Main.java` — não é exclusivo do eixo x das Relações, é o mecanismo geral que aplica `PoliticaPreenchimentoIncognita` a qualquer origem. (Números de linha deliberadamente omitidos aqui — já divergiram uma vez desde 2026-08-07 só por causa de edições não relacionadas; use os nomes de método para localizar o código atual.)

6. **Cópia representacional ao sair do texto — confirmado em 2026-08-23.**
   Arrastar um número ou a incógnita do enunciado cria um novo
   `ItemTextoArrastavel` em `converterElementoTextoEmItemDiagrama`; o
   `ElementoTextoMovel` de origem não é removido. O enunciado preserva sua
   informação enquanto a cópia participa da construção do diagrama. Não
   substituir esse protocolo por uma movimentação destrutiva do texto.

7. **Confirmação da incógnita — confirmado em 2026-08-23.** O valor digitado
   para a incógnita não deve ser propagado como resposta aceita apenas porque
   foi digitado. `incognitaAguardandoConfirmacaoDeValor` mantém a alteração
   local enquanto `confirmarValorIncognitaAceito` realiza a confirmação no
   fim do protocolo. Componentes que materializam esse fluxo podem variar por
   representação, mas devem preservar a separação entre digitar e confirmar.

8. **Seleção do sinal — migração P5.2 confirmada em 2026-08-25.** A avaliação
   C/E da opção `+`/`-` pertence agora ao papel e ao `NumeroInteiro`, mas essa
   mudança não altera a ordem do protocolo. A interface continua validando
   primeiro a posição, depois protege as quantidades contra resultados
   negativos, aplica o sinal localmente, materializa o questionamento quando
   necessário e só então executa as mesmas rotinas de propagação e confirmação
   já existentes. Não usar a migração do veredito como autorização para
   recalcular, desfazer ou antecipar a sincronização.

9. **Projeções remotas.** Web e mobile devem receber, pela API semântica do
   Gérard, projeções do mesmo estado e comandos avaliados pelos mesmos
   proprietários usados no desktop. O contrato JSON não cria uma segunda fonte
   de verdade e o cliente não recompõe relações ausentes. Consulte
   `gerard-api-semantica` para versionamento e maturidade do contrato.

## Termo desconhecido / incógnita — corrigido

`termo_desconhecido` **não é** a fonte única de verdade para a incógnita. `gerard.campoaditivo.curadoria.ResolvedorIncognitaCurada` mostra que dois mecanismos coexistem por design: o símbolo "?" digitado diretamente no campo do papel semântico, e o campo `termo_desconhecido`. Quando eles divergem, a classe sinaliza um conflito curatorial (`mensagemInconsistencia`) em vez de escolher um como autoritativo. Ao mexer nessa área: não presuma que gravar em `termo_desconhecido` basta, e não crie uma regra permanente que "resolve" a divergência escolhendo um lado — trate como o conflito que `ResolvedorIncognitaCurada` já modela, e corrija a inconsistência na origem dos dados quando possível.

## Onde essa lógica vive hoje — corrigido (atualizado em 2026-07-20)

A recomendação de arquitetura (regras matemáticas/semânticas em modelos/serviços/políticas; comportamento de interface em controladores/componentes; `Main.java` limitada a inicialização/composição/coordenação) é a direção desejável, e parte dela foi aplicada de forma explícita: `simularEstadoCompartilhadoAposAlteracaoQuantidade` e `estadoSimuladoRespeitaLimitesDasQuantidades` foram extraídas para `gerard.campoaditivo.sincronizacao.SimuladorEstadoComplementarVenn`, com wrappers de mesmo nome mantidos em `Main.java`/`TelaGerard` (preservando os tokens checados por `scripts/verificar_regressao_gerard.py` — a referência a linhas específicas foi removida desta skill em 2026-09-03 por já estar errada; o script cresceu e a checagem migrou de posição mais de uma vez desde que este parágrafo foi escrito. Grep pelo nome do token no script em vez de confiar num número de linha aqui). Validado, no momento da extração original, via compilação completa e uma bateria de checagens comportamentais ad hoc (inferência aditiva, limite curado, valor com sinal); desde então, sessões com `javac`/ambiente completo já rodaram a bateria de regressão real e o verificador estrutural completo várias vezes sobre este mesmo código (ver `LEVANTAMENTO_ACOPLAMENTO_MAIN_WEB_2026-08-31.md` para o histórico), então a ressalva original sobre `ant` não estar disponível não se aplica mais a toda sessão — confirme o ambiente atual antes de reciclar essa ressalva.

**Isto não foi uma auditoria completa quando escrito.** Só os dois métodos citados como exemplo foram extraídos por essa mudança específica; não presuma que toda a lógica semântica saiu de `Main.java` só por causa deste parágrafo — outras checagens de sinal/limite podem continuar inline em outros métodos da `TelaGerard`, e `LEVANTAMENTO_ACOPLAMENTO_MAIN_WEB_2026-08-31.md` é a fonte mais atual e abrangente sobre o que ainda está acoplado (ela documenta uma auditoria muito mais extensa, feita depois desta). Se for extrair mais alguma coisa, trate como mudança arquitetural explícita, combinada com o usuário antes — não como parte de uma tarefa não relacionada.

## Regra de segurança — NÃO PULAR

Antes de alterar qualquer código relacionado a esta lógica:

1. Pare e mostre o diff proposto ao usuário antes de aplicar.
2. Não presuma que uma refatoração é "melhoria" sem confirmação explícita. O comportamento atual é o correto, mesmo que o código pareça poder ser simplificado.
3. Rode a bateria de testes de regressão existente (scripts com logs individuais, resumo.tsv, códigos de saída, hashes SHA-256) e compare os resultados byte a byte contra a execução anterior.
4. Nunca apresente um resultado reaproveitado como se fosse uma nova execução. Se os timestamps/hashes não forem de uma execução real e atual, isso é inaceitável.
5. Se não for possível rodar os testes (ambiente sem acesso ao projeto completo), avise explicitamente que a mudança não foi validada contra a bateria de regressão, em vez de assumir que está tudo certo.

## Princípio geral do domínio — ver `gerard-scaffolding-interacao`

O princípio de não automatizar passos sem autorização do pesquisador e o
estado atual de `AG_AE` estão documentados na seção “Automatização de passos”
de `gerard-scaffolding-interacao`. Consulte essa fonte proprietária em vez de
duplicar aqui a definição ou o status de implementação.
