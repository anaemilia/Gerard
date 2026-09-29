# Provas de portabilidade dos handlers

Leia esta referência ao portar protocolos ou revisar adaptadores concretos de
desktop, web ou mobile.

`LimitesMovimento` é uma prova da fronteira: a geometria real do enunciado
continua sendo calculada pelo objeto de representação, enquanto
`HandlerInteracaoElementoTextoMovel` recebe intervalo neutro e não importa
`java.awt.Rectangle` nem Swing. Componentes visuais Swing não são artefatos
reutilizáveis entre plataformas.

O protocolo dos conectores fornece outra prova. O handler portátil conhece
somente `AlvoMovelIncremental`, deltas e `LimitesMovimento`. A representação
desktop é alcançada por `AdaptadorMovimentoConectorVergnaud`; web ou mobile
implementam outra porta sem herdar `ConectorVergnaud` ou `Rectangle`.

Essas classes demonstram o contrato atual, mas não constituem uma lista
fechada de adaptadores ou mecanismos de interação.
