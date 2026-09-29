---
name: gerard-knowledge-oriented-domain-objects
description: Orienta a criação de objetos semanticamente ricos sem tratar papéis ou elementos do diagrama como conceitos completos.
---

# Objetos de Domínio Orientados ao Conhecimento

## Dependência normativa

Leia `gerard-semantic-model/REFERENCE.md` antes de aplicar esta skill.

## Objetivo

Cada objeto de domínio representa um elemento semanticamente definido, como entidade, papel, valor, relação estrutural, situação, tentativa ou evidência contextualizada.

Objetos não representam automaticamente conceitos completos no sentido da Teoria dos Campos Conceituais.

## Princípios

- Objetos representam elementos semanticamente definidos, não simples componentes gráficos.
- Papéis como `Parte`, `Todo` e `Transformação` integram representações de situações.
- O conhecimento específico permanece próximo do objeto responsável por ele.
- Relações entre vários objetos ficam em coordenadores de escopo fechado.
- Aprendizado e políticas transversais permanecem no Modelador ou em serviços
  especializados; a escolha dentro de um repertório local pertence ao objeto
  semanticamente responsável.
- Objetos devem ser ricos, mas não devem tornar-se “god objects”.

## Conhecimento que um objeto pode encapsular

Conforme sua responsabilidade:

- identidade e significado semântico;
- domínio matemático local;
- estado e comportamento próprios;
- restrições locais;
- relações permitidas;
- descritor abstrato de representação;
- categorias de erro específicas do seu estado;
- chaves de mensagens específicas;
- dados serializáveis do domínio;
- produção de fatos necessários para eventos semânticos.
- propriedade e produção do registro factual das ações instrumentais que o
  objeto ou relação possui conhecimento para constituir e avaliar;
- repertório local de ajudas semanticamente aplicáveis;
- seleção de uma ajuda desse repertório a partir de diagnóstico factual e de
  uma projeção imutável do Modelo do Usuário.
- interpretação de uma projeção factual tipada do Modelo da Situação/Solução
  corrente, quando esse conhecimento pertence ao seu escopo.

## O que não pertence automaticamente ao objeto

- layout, pixel, cor concreta e componente de interface;
- estratégia pedagógica global ou que ultrapasse o escopo do objeto;
- aprendizagem de regras, execução de J48/PART ou Apriori;
- Modelo do Usuário mutável ou completo;
- análise de sequências que envolvem várias tentativas;
- afirmações sobre esquemas ou invariantes operatórios do usuário;
- persistência concreta e transporte de eventos.

Quando a tarefa alterar produção de registros, fronteira entre gesto e ação,
identidade de ação ou persistência factual, consulte
`gerard-log-gestos-interacao`, `gerard-log-acao-instrumental` e
`gerard-semantic-event-logging`. Esta skill conserva somente o princípio de
localidade: o proprietário do conhecimento produz o fato; infraestrutura não
adquire sua autoridade por persistir ou transportar o registro.

## Responsabilidades típicas

O objeto pode responder:

- Quem sou no modelo?
- Qual papel semântico exerço?
- Que valores aceito?
- Que restrições locais possuo?
- De que relações estruturais posso participar?
- Qual é meu estado atual?
- Que diagnóstico factual decorre de uma tentativa de alteração?
- Que registro factual devo produzir para a ação instrumental que constituo ou
  avalio?
- Que descritor abstrato forneço às representações?
- Que dados do domínio podem ser serializados?

## Situação-problema como agregado rico

Quando a tarefa envolver a composição entre `EstruturaAditiva`,
`NarrativaCurada`, correspondências papel--fato, pontes das seis categorias,
sidecar editorial, traduções ou promoção humana, aplique
`gerard-situacao-problema-agregado`.

Esta skill preserva somente o princípio geral: um agregado pode possuir o
conhecimento que cruza vários objetos quando ele for o menor escopo que conhece
todos os fatos necessários. A especificação interna de `SituacaoProblema` não
pertence à regra geral sobre objetos de domínio ricos.

## Relações estruturais

Regras como `Todo = Parte1 + Parte2` ou `EstadoFinal = EstadoInicial + Transformacao` pertencem a objetos relacionais, e não a um papel isolado.

Use nomes como:

- `RelacaoEstruturalComposicao`;
- `RelacaoEstruturalTransformacao`;
- `RelacaoEstruturalComparacao`.

Não use `InvarianteOperatorio` para essas classes.

## Diagnóstico e ajuda local

Um proprietário semântico pode produzir diagnóstico factual e selecionar uma
ajuda do próprio repertório sem receber componentes visuais ou o modelo mutável
completo. O protocolo de decisão pertence a `gerard-ajuda-adaptativa`; o
esquema e as projeções pertencem a `gerard-modelo-usuario`; o repertório e sua
materialização pertencem a `gerard-scaffolding-interacao`. Consulte essas
skills quando a tarefa alcançar qualquer desses conhecimentos. Esta skill não
duplica seus campos, algoritmos, códigos ou estados de implementação.

## Número inteiro e sinal representado

A semântica e o protocolo da seleção de sinal foram localizados em
`gerard-sinal-valor-inteiro`. Esta skill continua afirmando apenas a regra
geral: o domínio numérico local pertence ao objeto semanticamente responsável.
Consulte a skill especializada antes de alterar correspondência `+`/`-`, ordem
da interação ou capacidade de sinal publicada para as representações.

## Representação

O objeto pode fornecer um descritor abstrato com forma conceitual, símbolo e chave de rótulo. Ele não deve desenhar a si próprio nem conhecer tecnologia de renderização.

## Conhecimento-em-ação do usuário

Esquemas, teoremas-em-ação e conceitos-em-ação não são propriedades internas de `Parte`, `Todo`, `Transformação` ou outro papel.

O sistema pode manter hipóteses analíticas separadas, tipadas e revisáveis, sustentadas por registros explícitos. A falta de evidência pode resultar em nenhuma hipótese.

## Regra de revisão

Antes de criar ou enriquecer um objeto, aplique
[`references/roteiro-revisao.md`](references/roteiro-revisao.md). O roteiro
operacional não substitui as responsabilidades e exclusões deste núcleo.
