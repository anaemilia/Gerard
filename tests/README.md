# Suítes de testes Java

Esta é a estrutura de destino dos testes que antes estavam misturados ao
código de produção em `src`.

- `java/`: testes determinísticos executados automaticamente por
  `scripts/verificar_linha_base_windows.py`.
- `graphical/`: testes manuais que usam Swing e `java.awt.Robot`; exigem uma
  sessão gráfica e controle exclusivo do mouse e do teclado.
- `legacy/`: verificadores históricos mantidos para rastreabilidade, mas que
  não representam o contrato atual e não entram na regressão automática.

`TesteComparativoEstadoSemanticoCompartilhado` permanece em `legacy/` porque
compara o estado atual com uma implementação anterior e acusa três
divergências esperadas nos cenários em que a incógnita protegida não deve ser
preenchida automaticamente.

Os testes ainda existentes em `scripts/testes` continuam ativos durante a
migração gradual e serão consolidados numa etapa posterior.
