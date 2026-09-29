# Checklist das fronteiras do domínio

Leia esta referência ao auditar uma alteração, classificar um elemento ou
revisar regressões entre domínio, representação e interação.

## Anti-padrões

- Tratar cada componente visual como objeto de domínio.
- Chamar `Parte`, `Todo` ou `Transformação` de conceito completo.
- Armazenar estado somente em nós gráficos.
- Duplicar regras entre GTN e interface.
- Criar `InvarianteOperatorio` para verificar apenas uma equação formal.
- Registrar cálculo automático como ação do usuário.
- Acoplar o domínio a Swing, JavaFX, AWT, pixels ou controles de mouse.

## Perguntas de revisão

- O elemento é semanticamente significativo ou apenas visual?
- O estado está no domínio ou somente na interface?
- A regra é local, relacional ou política global?
- A relação foi nomeada como relação estrutural?
- GTN e diagrama continuam sincronizados pelo mesmo modelo?
- A representação concreta está separada do descritor abstrato?
- Eventos e hipóteses analíticas permanecem separados?
