---
name: Knowledge-Oriented Domain Objects for GERARD
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
- Políticas pedagógicas gerais permanecem em skills ou serviços especializados.
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

## O que não pertence automaticamente ao objeto

- layout, pixel, cor concreta e componente de interface;
- estratégia pedagógica global;
- decisão sobre quando oferecer ajuda ou fading;
- análise de sequências que envolvem várias tentativas;
- afirmações sobre esquemas ou invariantes operatórios do usuário;
- persistência concreta e transporte de eventos.

## Responsabilidades típicas

O objeto pode responder:

- Quem sou no modelo?
- Qual papel semântico exerço?
- Que valores aceito?
- Que restrições locais possuo?
- De que relações estruturais posso participar?
- Qual é meu estado atual?
- Que diagnóstico factual decorre de uma tentativa de alteração?
- Que descritor abstrato forneço às representações?
- Que dados do domínio podem ser serializados?

## Relações estruturais

Regras como `Todo = Parte1 + Parte2` ou `EstadoFinal = EstadoInicial + Transformacao` pertencem a objetos relacionais, e não a um papel isolado.

Use nomes como:

- `RelacaoEstruturalComposicao`;
- `RelacaoEstruturalTransformacao`.

Não use `InvarianteOperatorio` para essas classes.

## Diagnóstico e feedback

O objeto pode produzir um diagnóstico factual, por exemplo:

- valor fora do domínio;
- papel incompatível com a relação;
- estado incompleto;
- representação estruturalmente inconsistente.

A skill de feedback decide como, quando e em que linguagem pedagógica apresentar esse diagnóstico.

Mensagens devem ser representadas por chaves de internacionalização, não por texto final embutido no domínio.

## Representação

O objeto pode fornecer um descritor abstrato com forma conceitual, símbolo e chave de rótulo. Ele não deve desenhar a si próprio nem conhecer tecnologia de renderização.

## Conhecimento-em-ação do usuário

Esquemas, teoremas-em-ação e conceitos-em-ação não são propriedades internas de `Parte`, `Todo`, `Transformação` ou outro papel.

O sistema pode manter hipóteses analíticas separadas, tipadas e revisáveis, sustentadas por registros explícitos. A falta de evidência pode resultar em nenhuma hipótese.

## Regra de revisão

Antes de criar ou enriquecer um objeto, pergunte:

1. Este conhecimento é específico deste elemento?
2. A regra envolve somente seu estado ou coordena vários objetos?
3. Trata-se de fato do domínio ou de política pedagógica?
4. Trata-se de observação factual ou de interpretação sobre o usuário?
5. O objeto conheceria tecnologia de interface se essa regra fosse inserida aqui?

Use as respostas para escolher entre objeto local, relação estrutural, skill, infraestrutura ou hipótese analítica.
