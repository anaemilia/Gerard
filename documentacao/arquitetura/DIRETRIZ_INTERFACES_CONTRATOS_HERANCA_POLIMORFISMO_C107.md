# Diretriz arquitetural C107 - interfaces como contratos, herança e polimorfismo

## Decisão permanente

A partir do ciclo C107, novas funcionalidades e refatorações do Gérard devem priorizar:

1. **interfaces como contratos**, para declarar capacidades sem acoplar a interface gráfica a classes concretas;
2. **herança**, quando houver estado ou comportamento comum estável que possa ser centralizado em uma classe-base;
3. **polimorfismo**, para que controles, serviços e telas operem pelos contratos e aceitem implementações distintas sem condicionais por classe ou categoria.

A herança não deve ser usada apenas para reutilização acidental. Deve existir uma relação conceitual clara entre a abstração-base e suas especializações. Quando não houver essa relação, deve-se preferir composição.

## Aplicação inicial: unidades do diagrama complementar

Foram introduzidos os seguintes contratos:

- `RepresentacaoComUnidades` - contrato-base de leitura;
- `RepresentacaoComUnidadesAdicionaveis` - capacidade de acrescentar unidades;
- `RepresentacaoComUnidadesRemoviveis` - capacidade de retirar unidades;
- `OperacoesUnidadesVenn` - porta entre a abstração e a infraestrutura da tela.

A classe abstrata `RepresentacaoComUnidadesAbstrata` centraliza o agrupamento, o papel semântico e a contagem. A implementação `RepresentacaoVennEditavel` herda dessa base e implementa os contratos de adição e remoção.

Os controles `+` e `-` e a tela principal passaram a depender das interfaces, não da implementação concreta. Assim, uma representação futura poderá ser apenas adicionável, apenas removível ou possuir ambas as capacidades.

## Regra de regressão

A regressão geral deve verificar:

- existência dos contratos;
- herança dos contratos especializados a partir do contrato-base;
- uso da classe abstrata para estado comum;
- implementação polimórfica na tela;
- preservação da sincronização semântica, dos limites curados e dos logs.
