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

Objetos ricos da camada de representação possuem e produzem os registros dos
gestos físicos que os envolvem. Objetos ricos do domínio e relações
estruturais possuem e produzem os registros das ações instrumentais e dos
resultados factuais que pertencem às suas regras. Essa divisão preserva a
localidade: coordenadas e trajetórias não entram no domínio, e C/E não é
calculado pela representação. A infraestrutura apenas persiste ou transporta
os registros produzidos.

A multiplicidade de participantes não multiplica ações. Se uma única ação
envolve vários objetos semânticos, o menor objeto relacional ou agregado de
escopo fechado que compreende a ação produz um único registro com um único
`action_id`; os objetos participantes são apenas referenciados nesse registro.

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

## Relações estruturais

Regras como `Todo = Parte1 + Parte2` ou `EstadoFinal = EstadoInicial + Transformacao` pertencem a objetos relacionais, e não a um papel isolado.

Use nomes como:

- `RelacaoEstruturalComposicao`;
- `RelacaoEstruturalTransformacao`.

Não use `InvarianteOperatorio` para essas classes.

## Diagnóstico e ajuda local

O objeto pode produzir um diagnóstico factual, por exemplo:

- valor fora do domínio;
- papel incompatível com a relação;
- estado incompleto;
- representação estruturalmente inconsistente.

O objeto pode selecionar uma ajuda de seu repertório local usando esse
diagnóstico e um `ContextoAdaptativoUsuario` somente de leitura. O resultado
é uma decisão semântica — por exemplo, código do apoio, finalidade, modalidade
abstrata, regra aplicada e versão do modelo — e não um componente visual.

A representação decide como materializar a decisão em sua própria sintaxe. O
registro factual confirma separadamente o que foi efetivamente exibido.

O contexto da sessão não deve chegar como um mapa global. Objetos como
`FatosSelecaoAjudaIncognita` e `FatosSelecaoAjudaPosicionamento` exemplificam
projeções locais e tipadas do Modelo da Situação/Solução: elas mudam durante a
tentativa, enquanto a fotografia do Modelo do Usuário permanece estável.

Mensagens devem ser representadas por chaves de internacionalização, não por texto final embutido no domínio.

## Número inteiro e sinal representado — P5.2

`NumeroInteiro` possui a correspondência entre seu valor e a opção de sinal
da representação binária. O papel curado fornece o número normativo e mantém
a tentativa da ação; nem a tela nem o logger inspecionam o inteiro para
decidir C/E. Na sintaxe atual, que oferece somente `+` e `-`, zero corresponde
à opção MAIS. Isso é uma decisão de representação compatível com a interface
existente, não a afirmação conceitual de que zero é positivo.

Quando o valor curado está ausente, é `?` ou não é inteiro, não há critério de
avaliação de sinal. A ausência de critério não autoriza a interface a inferir
um valor esperado pelo texto, pela posição ou por outro elemento visual.

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
6. O contexto do usuário foi reduzido ao mínimo necessário e permanece
   imutável durante a sessão?
7. Estou preservando uma única ação e apenas referenciando seus vários objetos
   participantes, em vez de duplicar o registro?

Use as respostas para escolher entre objeto local, relação estrutural, skill, infraestrutura ou hipótese analítica.
