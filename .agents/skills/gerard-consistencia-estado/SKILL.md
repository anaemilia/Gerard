---
name: gerard-consistencia-estado
description: Regras sobre a consistência de estado entre as representações do Gérard (texto, diagrama de Vergnaud, material concreto). Use sempre que for tocar em código relacionado a sincronização de valores semânticos, propagação de estado entre representações, ou ao comportamento "após o primeiro posicionamento" no Gérard. A maior parte deste comportamento já está implementada e verificada em produção (ver seção "Status de verificação"); esta skill existe para PROTEGER a lógica atual, não para reimplementá-la — mas dois pontos abaixo foram corrigidos porque a implementação real diverge do que se assumia.
---

# Consistência de estado entre representações — Gérard

## Status de verificação (2026-07-20; regra 5 confirmada em 2026-08-07)

As regras 1, 3, 4 e 5 abaixo foram conferidas linha a linha contra o código atual e se confirmam. A regra 2 se confirma, mas com escopo mais estreito do que "bloqueio geral". As duas afirmações originais sobre `termo_desconhecido` como fonte única e sobre `Main.java` estar livre de lógica semântica eram **falsas** e foram reescritas nas seções correspondentes — não trate versões anteriores deste texto como válidas.

Isto não é uma especificação para implementar do zero. É a documentação de um invariante existente, para que qualquer trabalho futuro (refatoração, nova feature, nova plataforma) não quebre esse comportamento sem querer.

## A regra

1. **Estado compartilhado.** O estado semântico é compartilhado entre as representações (texto, diagrama de Vergnaud, material concreto/quadradinhos) através de `gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado`, que mantém os valores centralizados e é acionado a partir de `Main.java` (`aplicarEstadoCompartilhadoEmTodasAsRepresentacoes` e as funções `sincronizarElementosSemanticosDoTexto` / `sincronizarDiagramaVennComRepresentacoes` / `sincronizarEixosComEstadoCompartilhado`).

2. **Bloqueio antes do primeiro posicionamento válido — escopo real.** Antes de haver conteúdo semântico no diagrama de Vergnaud, `gerard.Scaffolding.venn.CondicaoDiagramaVergnaudNaoVazio` desabilita os controles de adicionar/remover quadradinhos no diagrama Venn (`estadoModelagem.possuiConteudoSemantico()`). **Isto não é um freeze geral de todas as representações complementares** — é especificamente o bloqueio dos controles de unidade do Venn. Não presuma bloqueio de edição de texto ou de outros controles com base nesta regra sem checar o ponto específico.

3. **Propagação depois do primeiro posicionamento.** Mudanças em qualquer valor semântico (`quantidade_1`, `quantidade_2`, `resultado`, `referido`, `referendo`, `valor_relativo`, `termo_desconhecido`) recalculam os slots dependentes via `EstadoSemanticoCompartilhado.resolverRelacaoAditiva` e se refletem nas demais representações. A aritmética pura vive em `ScaffoldingReacaoRepresentacoes` (`calcularEstadoFinal`, `calcularQuantidadeDependente`); a fiação origem→propagação está em `Main.java`, marcada por `Origem` (ARRASTE, EIXO_X, EDICAO_TEXTO, DIAGRAMA_COMPLEMENTAR etc.).

4. **Sem auto-correção.** Peças já posicionadas corretamente continuam arrastáveis/reposicionáveis — não ficam travadas após o acerto (não existe lock desse tipo no código). Se uma peça movida gerar inconsistência, o sistema **não corrige nem desfaz automaticamente** o estado (não há lógica de undo/auto-correção). O usuário recebe o feedback padrão de erro — tremor (`ScaffoldingFeedbackMultissensorialErro`) + som (`Toolkit.beep()`) + pergunta de confirmação (`ScaffoldingQuestionamento.criarPerguntaConfirmacao`) — e precisa ele mesmo usar um protocolo de mouse para voltar a um estado consistente. Princípio geral: a interface só automatiza passos mediante autorização explícita do pesquisador — nunca por conta própria.

5. **Caso especial do eixo dos inteiros — confirmado (2026-08-07), com escopo mais preciso do que a formulação original.** `ScaffoldingGraficoInteiros.identificarNaturezaInteracao` distingue interação de componente de tela (nunca é erro) de interação com valor semântico. O gate não vive em `ScaffoldingGraficoInteiros`/`LayoutPainelEixoInteiros` nem em `PoliticaPreenchimentoIncognita` — vive no ponto de chamada, em `Main.java`: `sincronizarNumeroRelativoComGraficoSeNecessario` (`Main.java:11612-11674`) só propaga para as demais representações (`sincronizarTodasAsRepresentacoesAPartirDoVergnaud(..., Origem.EIXO_X)`) quando `liberadoParaPropagar` é verdadeiro — `!incognitaAguardandoConfirmacaoDeValor(item)` durante o arrasto, `confirmarValorIncognitaAceito(item)` ao soltar. A formulação original ("só depois que a incógnita foi concluída") é verdadeira só quando o item movido pelo eixo **é** a própria incógnita: `incognitaAguardandoConfirmacaoDeValor` (`Main.java:5811-5818`) retorna `false` — ou seja, libera a propagação imediatamente, a cada passo do arrasto — sempre que o item não é a incógnita original ou ainda não foi preenchido pelo protocolo mouse/texto. Não é "eixo trava até incógnita terminar" em geral; é "o valor da incógnita não se propaga para as outras representações enquanto está pendente de confirmação", e só isso. O mesmo padrão (`incognitaAguardandoConfirmacaoDeValor`/`confirmarValorIncognitaAceito`) protege igualmente `Origem.EIXO_VERTICAL` (`Main.java:10000`, `10998`) e `Origem.ARRASTE` (`Main.java:11764`) — não é exclusivo do eixo dos inteiros, é o mecanismo geral que aplica `PoliticaPreenchimentoIncognita` a qualquer origem de escrita.

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

## Termo desconhecido / incógnita — corrigido

`termo_desconhecido` **não é** a fonte única de verdade para a incógnita. `gerard.campoaditivo.curadoria.ResolvedorIncognitaCurada` mostra que dois mecanismos coexistem por design: o símbolo "?" digitado diretamente no campo do papel semântico, e o campo `termo_desconhecido`. Quando eles divergem, a classe sinaliza um conflito curatorial (`mensagemInconsistencia`) em vez de escolher um como autoritativo. Ao mexer nessa área: não presuma que gravar em `termo_desconhecido` basta, e não crie uma regra permanente que "resolve" a divergência escolhendo um lado — trate como o conflito que `ResolvedorIncognitaCurada` já modela, e corrija a inconsistência na origem dos dados quando possível.

## Onde essa lógica vive hoje — corrigido (atualizado em 2026-07-20)

A recomendação de arquitetura (regras matemáticas/semânticas em modelos/serviços/políticas; comportamento de interface em controladores/componentes; `Main.java` limitada a inicialização/composição/coordenação) é a direção desejável, e parte dela foi aplicada de forma explícita: `simularEstadoCompartilhadoAposAlteracaoQuantidade` e `estadoSimuladoRespeitaLimitesDasQuantidades` foram extraídas para `gerard.campoaditivo.sincronizacao.SimuladorEstadoComplementarVenn`, com wrappers de mesmo nome mantidos em `Main.java`/`TelaGerard` (preservando os tokens checados por `scripts/verificar_regressao_gerard.py`, linhas ~488-490). Validado via compilação completa (`javac`, sem libs externas — `javac.classpath` vazio no projeto) e uma bateria de checagens comportamentais ad hoc (inferência aditiva, limite curado, valor com sinal) escrita para esta extração; **não** foi possível rodar `ant clean jar` nem os scripts `scripts/testar_*.sh`/`verificar_regressao_gerard.py` completos neste ambiente porque `ant` não está instalado — se `ant` estiver disponível em outra máquina, rode a bateria completa antes de considerar isso definitivamente fechado.

**Isto não foi uma auditoria completa.** Só os dois métodos citados como exemplo foram extraídos; não presuma que toda a lógica semântica saiu de `Main.java` — outras checagens de sinal/limite podem continuar inline em outros métodos da `TelaGerard`. Se for extrair mais alguma coisa, trate como mudança arquitetural explícita, combinada com o usuário antes — não como parte de uma tarefa não relacionada.

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
