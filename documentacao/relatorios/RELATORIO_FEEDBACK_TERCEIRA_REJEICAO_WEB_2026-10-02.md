# Feedback após confirmação da crença equivocada

Relato da usuária: valor errado, confirmação Sim, fechamento da pergunta e novas ações sem retorno perceptível do limite de três tentativas.

A sequência reproduzida registra uma rejeição para a proposta, a segunda para Sim e a terceira para a próxima proposta errada. O servidor devolve `limite_atingido=true` e a mensagem localizada `ui.notice.attemptLimitReached`. O cliente descartava essa mensagem nos dois caminhos que atingem o limite e encerrava a edição, deixando a ação sem encaminhamento visível quando não havia conteúdo de historinha.

Correção: materializar a mensagem existente do servidor, ancorada na figura, sem temporizador; limpar ao acertar, restaurar ou sortear. Proposta e confirmação recebem o mesmo tratamento. A avaliação, o contador e a decisão de historinha continuam no domínio; não foi criada política de contagem no cliente nem conteúdo narrativo.

Verificação: build TypeScript/Vite e grafo de skills aprovados. Mouse/teclado real em Lucas: proposta errada 1 → Sim (segunda rejeição) → proposta errada 2 (terceira); feedback visível e presente após 4,5 segundos; valor correto 5 conclui e limpa o aviso. Roteiro: `tests/web/e2e_confirmacao_morangos.mjs`, com `GERARD_TESTAR_SIM=1` e `GERARD_TESTAR_DECISAO=1`.
