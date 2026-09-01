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

## Situação-problema como agregado rico

Quando o conhecimento cruza a estrutura aditiva e uma narrativa curada, o
menor proprietário legítimo é `SituacaoProblema`. Ela compõe, sem fundir:

- `EstruturaAditiva`: categoria, papéis, relações e incógnita original;
- `NarrativaCurada`: participantes, objetos contados, inventários, marcadores
  temporais e eventos quantitativos ordenados, quando a história possuir
  mudanças;
- correspondências explícitas, declaradas pela curadoria humana, entre cada
  papel e o fato narrativo que realiza seu valor.

Não associe participantes, objetos ou papéis pela posição em uma lista, campo,
figura ou tela. O agregado pode validar a compatibilidade entre os dois lados
porque possui o escopo completo; ele não deve corrigir silenciosamente uma
divergência humana.

A sequência produzida pelo agregado é semântica e independente de mídia.
Renderizadores de texto, quadrinhos, animação e vídeo materializam essa
sequência em suas sintaxes próprias, sem possuir o cálculo, os papéis ou a
curadoria. Conteúdo convertido ou gerado continua
`CANDIDATA_NAO_CURADA` até promoção explícita pelo pesquisador.

Um `EventoNarrativoCurado` não é uma ação instrumental do participante. O
primeiro pertence ao conteúdo da história; a segunda pertence à atividade do
usuário sobre a interface e segue o esquema factual de ações.

Na categoria canônica `COMPOSICAO_TRANSFORMACOES`, a estrutura rica possui
exatamente seis papéis: estado inicial, transformação 1, estado intermediário,
transformação 2, transformação resultante e estado final. O estado
intermediário deve apontar explicitamente para o estado produzido pelo evento
curado correspondente; não o associe por índice de personagem, posição de
campo ou ordem visual.

As transformações 1 e 2 representam eventos efetivos e pertencem aos inteiros
não nulos. A transformação resultante não representa um terceiro evento e
permanece no domínio dos inteiros: ela pode ser zero quando os efeitos se
anulam, como em `+2 + (-2) = 0`.

Na categoria canônica `COMPOSICAO_MEDIDAS`, a estrutura rica possui exatamente
três papéis — parte 1, parte 2 e todo — ligados pela relação pertencente a
`RelacaoEstruturalComposicao`. Uma narrativa de composição estática pode ter
zero eventos: nesse caso, o estado final declarado preserva o inventário
inicial e existe para validar a declaração humana, não para inventar uma
transformação temporal.

Quando a curadoria distinguir variantes contadas de uma mesma família, uma
parte pode referenciar explicitamente um `ObjetoContado`, enquanto o todo pode
referenciar o total da `FamiliaObjeto`. Essa é uma possibilidade de modelagem
declarada pelo humano, não uma regra universal da categoria nem uma associação
inferida do texto, da ordem ou da posição visual.

Na categoria canônica `COMPARACAO_MEDIDAS`, a estrutura rica possui Referido,
Valor Relativo e Referendo, ligados por `RelacaoEstruturalComparacao` segundo
`Referendo = Referido + ValorRelativo`. Referido e Referendo pertencem aos
naturais; Valor Relativo pertence aos inteiros e pode ser nulo. Na narrativa
estática, a referência do Valor Relativo recebe nominalmente o participante do
Referendo e o participante do Referido e calcula a diferença nessa ordem. Não
associe esses participantes pela posição dos campos `personagem_*`, pela ordem
textual ou pela posição visual. A chave viva é `papel.diferenca`; “Valor
Relativo” é o nome conceitual do papel.

Em `TRANSFORMACAO_MEDIDAS`, Estado Inicial e Estado Final são medidas naturais,
enquanto a Transformação representa uma mudança efetiva e pertence aos inteiros
não nulos. Somente nesse esquema a consequência pode ser expressa como: uma
transformação direta igual a zero produziria `EstadoFinal = EstadoInicial` e não
constitui uma situação de Transformação de Medidas.

Não transfira essa justificativa para as categorias de números relativos. Em
`TRANSFORMACAO_RELACAO`, Relação Inicial e Relação Final pertencem aos inteiros e
podem ser zero; a transformação operante continua sendo um evento efetivo e não
nulo. Assim, `RelaçãoInicial = +2` e `Transformação = -2` produzem legitimamente
`RelaçãoFinal = 0`. Em `COMPOSICAO_TRANSFORMACOES`, as duas transformações
componentes são eventos não nulos, mas a Transformação Resultante pode ser zero
quando seus efeitos se anulam. Em `COMPOSICAO_RELACOES`, todos os papéis são
relações inteiras e podem ser zero. Valide o domínio de cada papel; nunca rejeite
zero genericamente por pertencer a uma categoria de Relações.

Na ponte rica de `COMPOSICAO_RELACOES`, cada um dos três papéis referencia uma
diferença orientada entre participantes nominalmente declarados. A operação
curada pertence à relação estrutural do agregado: soma pode realizar uma
configuração encadeada e subtração pode realizar uma configuração com
referência comum. Não determine essa operação pelos sinais, pelo enunciado,
pelos campos `personagem_*` ou pela posição visual. O pesquisador declara tanto
a operação quanto as três correspondências, e uma divergência permanece como
candidata diagnosticada. Relações opostas podem totalizar zero sem violar o
domínio de nenhum papel.

Em `TRANSFORMACAO_RELACAO`, não confunda a relação estrutural com a operação
pedida ao participante. A transformação descreve uma mudança; soma ou subtração
selecionada na interface é um procedimento de resolução curado. Preserve esse
segundo conhecimento em um critério próprio, como `CriterioOperacaoModelagem`,
sem usá-lo para recalcular a relação final declarada pelo pesquisador. O objeto
relacional considera os participantes nominalmente associados a cada relação e
ao evento, inclusive quando a relação final inverte a orientação da inicial.
Esta decisão é específica da ponte dessa categoria e não redefine as operações
já curadas das outras categorias.

A ponte do registro tabular para o agregado rico é um adaptador de curadoria,
não uma responsabilidade de `SituacaoProblema`. Ela pode transportar valores,
incógnita e operações já declarados, mas deve exigir à parte a narrativa e as
correspondências nominais. Campo obrigatório ausente, operação não curada ou
conflito entre `termo_desconhecido` e `?` interrompe a construção com
diagnóstico. A marca `validada` do registro anterior não promove
automaticamente a nova representação rica.

A narrativa e as correspondências ricas devem ser persistidas em um sidecar
XML versionado próprio da curadoria. Esse registro pode referenciar a situação
tabular pelo identificador, mas não pode reconstruir participantes, objetos,
eventos ou orientações a partir de `personagem_*`, da ordem das colunas, do
enunciado ou da geometria. A serialização é responsabilidade de infraestrutura;
os objetos de domínio permanecem independentes de XML, JSON, arquivos e Swing.
Quando o registro rico estiver ausente, a conversão deve parar com diagnóstico
factual, sem recorrer a uma inferência de compatibilidade.

O editor Swing é somente um adaptador de entrada das declarações humanas. Ele
expõe campos nominais para participantes, famílias, objetos, inventários,
eventos, marcadores temporais e correspondências, mas delega a montagem a um
componente independente da interface. Linhas, colunas e posições do formulário
não possuem significado semântico; todos os vínculos usam identificadores
declarados. Uma declaração completa mas divergente pode ser preservada como
candidata, acompanhada dos diagnósticos e de confirmação explícita do
pesquisador, sem correção silenciosa.

A versão original é a proprietária do sidecar semântico. Traduções alteram a
realização textual e referenciam a narrativa rica da original por
`versao_origem_id`; elas não criam participantes, objetos, eventos ou
correspondências paralelos.

O status editorial pertence ao registro da representação rica e começa como
`CANDIDATA_NAO_CURADA`. A marca `validada` da tabela histórica não o promove.
Somente um ato explícito do pesquisador no editor pode registrar
`VALIDADA_PELO_PESQUISADOR`, e essa promoção é bloqueada enquanto a conversão
produzir qualquer diagnóstico. Sidecars anteriores, que não possuem o status,
são lidos conservadoramente como candidatos.

## Relações estruturais

Regras como `Todo = Parte1 + Parte2` ou `EstadoFinal = EstadoInicial + Transformacao` pertencem a objetos relacionais, e não a um papel isolado.

Use nomes como:

- `RelacaoEstruturalComposicao`;
- `RelacaoEstruturalTransformacao`;
- `RelacaoEstruturalComparacao`.

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
