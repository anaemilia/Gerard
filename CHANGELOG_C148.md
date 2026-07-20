# C148 — Processo de transformação com canal, funis e quadradinhos

A representação complementar da categoria **Transformação de medidas** deixou de usar três contêineres equivalentes e passou a apresentar um processo dinâmico:

- estado inicial em um contêiner de quadradinhos;
- canal horizontal de passagem;
- estado final em um contêiner de quadradinhos;
- funil superior para transformação positiva/inserção;
- funil inferior para transformação negativa/retirada;
- somente o canal quando a transformação é zero ou desconhecida.

A forma visual é derivada do `NumeroInteiro` associado à transformação. As quantidades dos estados permanecem naturais, e a magnitude da transformação é concretizada por `abs(valor)` quadradinhos, sem perder o sinal no estado semântico compartilhado.

Foram criados componentes próprios no pacote `gerard.campoaditivo.transformacao.processo`, mantendo a `Main.java` restrita à coordenação e delegação.
