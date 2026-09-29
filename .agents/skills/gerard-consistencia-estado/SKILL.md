---
name: gerard-consistencia-estado
description: Regras sobre a consistência de estado entre as representações do Gérard (texto, diagrama de Vergnaud, material concreto). Use sempre que for tocar em código relacionado a sincronização de valores semânticos, propagação de estado entre representações, ou ao comportamento "após o primeiro posicionamento" no Gérard. A maior parte deste comportamento já está implementada e verificada em produção; esta skill existe para proteger a lógica atual, não para reimplementá-la.
---

# Consistência de estado entre representações — Gérard

## Fronteira com o protocolo localizado

Esta skill continua proprietária dos invariantes gerais de consistência e do
comportamento historicamente verificado. Para mudanças no mecanismo de
publicação, reconciliação e difusão de valores entre representações, consulte
também `gerard-propagacao-estado-semantico`, que possui esse protocolo
específico. Esta referência mantém compatibilidade durante a extração
incremental; não autoriza remover as proteções documentadas abaixo.

## Status de verificação (2026-07-20; regra 5 confirmada em 2026-08-07)

Ao auditar evidências, localizar a extração já realizada ou avaliar a cobertura
dos testes, leia
[`references/estado-implementacao.md`](references/estado-implementacao.md).
As regras abaixo documentam invariantes existentes, não uma especificação para
reimplementá-los do zero.

## A regra

1. **Estado compartilhado.** Texto, diagrama de Vergnaud, material concreto e
   demais projeções devem refletir um único estado semântico reconciliado. O
   mecanismo de publicação, ouvintes e snapshots pertence a
   `gerard-propagacao-estado-semantico`.

2. **Bloqueio antes do primeiro posicionamento válido — escopo real.** Antes de haver conteúdo semântico no diagrama de Vergnaud, `gerard.Scaffolding.venn.CondicaoDiagramaVergnaudNaoVazio` desabilita os controles de adicionar/remover quadradinhos no diagrama Venn (`estadoModelagem.possuiConteudoSemantico()`). **Isto não é um freeze geral de todas as representações complementares** — é especificamente o bloqueio dos controles de unidade do Venn. Não presuma bloqueio de edição de texto ou de outros controles com base nesta regra sem checar o ponto específico.

3. **Propagação depois do primeiro posicionamento.** Uma mudança semântica
   confirmada reconcilia os papéis dependentes pela relação estrutural e se
   reflete nas demais representações. Nenhuma representação mantém cálculo ou
   verdade paralela; consulte `gerard-propagacao-estado-semantico` para o
   protocolo.

4. **Sem auto-correção.** Peças já posicionadas corretamente continuam arrastáveis/reposicionáveis — não ficam travadas após o acerto (não existe lock desse tipo no código). Se uma peça movida gerar inconsistência, o sistema **não corrige nem desfaz automaticamente** o estado (não há lógica de undo/auto-correção). O usuário recebe o feedback padrão de erro — tremor (`ScaffoldingFeedbackMultissensorialErro`) + som (`Toolkit.beep()`) + pergunta de confirmação (`ScaffoldingQuestionamento.criarPerguntaConfirmacao`) — e precisa ele mesmo usar um protocolo de mouse para voltar a um estado consistente. Princípio geral: a interface só automatiza passos mediante autorização explícita do pesquisador — nunca por conta própria.

5. **Confirmação antes de propagar a incógnita.** O valor da incógnita não se
   propaga para outras representações enquanto estiver pendente de
   confirmação. Isso não constitui bloqueio geral do eixo ou de valores que
   não sejam a incógnita. Para a localização verificada do gate e seu escopo
   por origem, consulte
   [`references/estado-implementacao.md`](references/estado-implementacao.md).

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
   recalcular, desfazer ou antecipar a sincronização. A responsabilidade
   localizada dessa seleção pertence a `gerard-sinal-valor-inteiro`.

9. **Projeções remotas.** Web e mobile devem receber, pela API semântica do
   Gérard, projeções do mesmo estado e comandos avaliados pelos mesmos
   proprietários usados no desktop. O contrato JSON não cria uma segunda fonte
   de verdade e o cliente não recompõe relações ausentes. Consulte
   `gerard-api-semantica` para versionamento e maturidade do contrato.

## Termo desconhecido / incógnita — corrigido

`termo_desconhecido` **não é** a fonte única de verdade para a incógnita. `gerard.campoaditivo.curadoria.ResolvedorIncognitaCurada` mostra que dois mecanismos coexistem por design: o símbolo "?" digitado diretamente no campo do papel semântico, e o campo `termo_desconhecido`. Quando eles divergem, a classe sinaliza um conflito curatorial (`mensagemInconsistencia`) em vez de escolher um como autoritativo. Ao mexer nessa área: não presuma que gravar em `termo_desconhecido` basta, e não crie uma regra permanente que "resolve" a divergência escolhendo um lado — trate como o conflito que `ResolvedorIncognitaCurada` já modela, e corrija a inconsistência na origem dos dados quando possível.

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
