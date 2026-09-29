# Aplicação da localidade de infraestrutura

Leia esta referência quando a tarefa envolver API, persistência, transporte,
renderização, exportação ou propriedade de registros.

A infraestrutura pode persistir, indexar, transportar, renderizar ou exportar,
mas não adquire autoridade sobre o conhecimento transportado. A API semântica
é infraestrutura enquanto HTTP/JSON, serialização e compatibilidade; publica
decisões dos proprietários sem possuir suas regras. Consulte
`gerard-api-semantica` para contratos e maturidade.

Objetos ricos produzem os registros factuais que pertencem ao próprio
conhecimento: o objeto representacional registra o gesto que o envolve; o
objeto semântico ou a relação registra a ação constituída e seu resultado. A
infraestrutura persiste, indexa e transporta sem interpretar. Objetos não
executam diretamente I/O em arquivo ou banco.

Uma ação com vários objetos continua sendo uma única ação. O registro pertence
ao menor proprietário relacional ou agregado que conhece o conjunto; os
objetos participantes aparecem por referência, sem duplicação.
