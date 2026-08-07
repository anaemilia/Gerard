# Conteúdo pedagógico da tela de ajuda — primeiro rascunho (item 4 do levantamento)

Data: 2026-08-07. Pedido explicitamente ("implemente a lista de pendências... seguindo as skills").

## O que faltava

O mecanismo de N=3 tentativas rejeitadas (implementado em produção e piloto
mais cedo neste dia) mostrava, ao atingir o limite, só um aviso
operacional puro: "você tentou várias vezes, use o botão Restaurar" — sem
nenhuma orientação pedagógica, por decisão explícita de shipar o mecanismo
antes do conteúdo (`TAREFA_PENDENTE_FLUXO_TENTATIVAS_E_SCAFFOLDING.md`).

## O que foi feito

`ui.notice.attemptLimitReached` (`mensagens_{pt,en,es,fr}.properties`)
ganhou uma frase curta entre o aviso original e a instrução de "Restaurar":
revisar a relação entre as quantidades já conhecidas, sem em nenhum
momento revelar o valor da incógnita. Nenhuma classe nova, nenhum
mecanismo novo — é uma string localizada avulsa, exatamente como
`gerard-scaffolding-interacao` já documenta para a categoria "mensagem
informativa" (sem estrutura própria; verificar caso a caso).

Texto em português (referência; ver os outros 3 idiomas nos arquivos):

> Você tentou várias vezes o valor do {0}. Revise a relação entre as
> quantidades que você já conhece antes de tentar de novo. Use o botão
> Restaurar para recomeçar este item.

## Por que este texto e não outro

- Curto (2 frases), consistente com o princípio da skill de evitar
  linguagem natural extensa como mecanismo primário de feedback.
- Não revela a resposta nem a operação específica — só direciona a
  atenção para a relação entre as quantidades, coerente com a ênfase de
  Vergnaud em relações entre grandezas, não em "adivinhar o número".
- Reaproveita o padrão já existente (`ui.hint.chooseOperation`, mostrado
  em outro ponto do fluxo) de dar uma dica de direção sem fazer a tarefa
  pelo estudante.

**Isto não é uma validação pedagógica definitiva.** É um rascunho razoável
para não deixar o mecanismo (já funcionando) sem nenhum conteúdo — a
usuária pode querer um texto diferente, mais específico por categoria, ou
integrado ao sistema de "ajuda contextual" já existente (botões "E agora?"
por área — Texto/Vergnaud/Complementar, `ScaffoldingAjudaContextual`), que
é um mecanismo separado, não tocado aqui.

## Verificação

- Compilação completa: 436 arquivos, 0 erros.
- Suíte temporária dedicada (criada, rodada, deletada, nunca commitada):
  carrega os 4 arquivos `.properties` e formata a mensagem via
  `MessageFormat` com um valor de teste — confirma que o `{0}` substitui
  corretamente e que o francês (único com convenção de escape `''` para
  apóstrofo) não quebrou, já que o texto novo em francês não usa
  apóstrofo.

## Escopo

Só os 4 arquivos de mensagens + um comentário Javadoc em `Main.java`
(nenhuma lógica mudou, só a string carregada). `TAREFA_PENDENTE_FLUXO_TENTATIVAS_E_SCAFFOLDING.md`
atualizada para refletir o rascunho — o repertório de Scaffolding em dois
eixos (item 5 do levantamento) continua não decidido, não tocado aqui.
