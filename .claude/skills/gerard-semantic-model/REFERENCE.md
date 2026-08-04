# Modelo Semântico de Referência do GERARD

**Versão:** 2.0  
**Data:** 2026-08-01  
**Status:** documento normativo; não é uma skill operacional.

## 1. Finalidade

Este documento define a natureza dos elementos do GERARD e o vocabulário que deve orientar o modelo de domínio, as skills, os agentes, os eventos, a persistência, as representações e a análise da atividade.

O modelo semântico responde:

> O que cada elemento do sistema é, que significado possui e quais relações pode manter?

As skills respondem:

> Que capacidades reutilizáveis podem operar sobre esses elementos?

As skills não podem redefinir os conceitos teóricos deste documento. Em caso de conflito, este modelo semântico prevalece.

## 2. Fundamento na Teoria dos Campos Conceituais

A arquitetura deve respeitar as definições apresentadas por Vergnaud (1998):

- **Esquema:** organização invariante da conduta para uma determinada classe de situações.
- **Teorema-em-ação:** proposição que o sujeito considera verdadeira.
- **Conceito-em-ação:** objeto, predicado ou categoria que o sujeito considera pertinente.
- **Invariantes operatórios:** teoremas-em-ação e conceitos-em-ação que constituem componentes essenciais dos esquemas.
- **Representação:** processo dinâmico relacionado à organização da ação e da linguagem, e não apenas uma forma gráfica estática.

Referência normativa:

> VERGNAUD, Gérard. A Comprehensive Theory of Representation for Mathematics Education. *The Journal of Mathematical Behavior*, v. 17, n. 2, p. 167–181, 1998. DOI: 10.1016/S0364-0213(99)80057-3.

## 3. Triplo dinâmico do conceito

Um conceito é considerado segundo o triplo:

`C = (S, I, R)`

em que:

- `S` representa classes de situações que atribuem sentido ao conceito;
- `I` representa os invariantes operatórios mobilizados nos esquemas;
- `R` representa sistemas e processos de representação.

Esse triplo não deve ser tratado como uma decomposição triangular estática. Situações, invariantes operatórios e representações participam de uma organização dinâmica e relacional mediada por esquemas, ação e linguagem.

Nenhum elemento isolado do diagrama é, por si só, um conceito completo no sentido da Teoria dos Campos Conceituais.

## 4. Categorias do modelo semântico

### 4.1 Situação e classe de situações

Uma situação-problema concreta é uma instância de uma classe de situações. A classe de situações é relevante para a análise dos esquemas mobilizados pelo sujeito.

### 4.2 Papéis semânticos

`Parte`, `Todo`, `Transformação`, `Estado Inicial`, `Estado Final`, `Referido`, `Referendo` e `Valor Relativo` são papéis semânticos presentes em representações de situações.

Eles podem ser implementados como objetos de domínio semanticamente ricos, mas não devem ser denominados conceitos completos.

### 4.3 Relações estruturais formais

Expressões como:

- `Todo = Parte1 + Parte2`;
- `EstadoFinal = EstadoInicial + Transformacao`;

são relações estruturais formais utilizadas pelo sistema para descrever, calcular ou verificar a consistência de uma representação.

Elas não são, por si mesmas, invariantes operatórios. Uma relação formal só pode aparecer em uma hipótese de teorema-em-ação quando existirem registros suficientes para sustentar que o sujeito a considera verdadeira e a mobiliza em sua atividade.

Nomes recomendados para objetos computacionais:

- `RelacaoEstruturalComposicao`;
- `RelacaoEstruturalTransformacao`;
- `RelacaoEstruturalComparacao`.

Evitar nomes como `InvarianteOperatorio` para classes que apenas verificam regras formais do sistema.

### 4.4 Objetos semânticos

Um objeto semântico representa uma entidade, um papel, um valor, uma relação estrutural, uma situação, uma tentativa ou outro elemento semanticamente definido do domínio.

Ele pode encapsular, conforme sua responsabilidade:

- identidade semântica;
- significado no domínio;
- restrições locais;
- relações permitidas;
- estado e comportamento próprios;
- descritores abstratos de representação;
- categorias de diagnóstico relacionadas ao seu estado;
- chaves de mensagens, quando forem específicas do elemento;
- dados serializáveis do domínio.

O objeto semântico não deve conhecer detalhes de renderização, como pixels, coordenadas de tela, `Graphics2D`, componentes Swing, efeitos visuais ou alças de mouse.

### 4.5 Representações

As representações incluem formas gráficas, textuais, numéricas, linguísticas e simbólicas, além das transformações e manipulações que participam do processo representacional.

O domínio pode fornecer descritores abstratos, como papel, símbolo, forma conceitual e chave de rótulo. A camada de representação converte esses descritores em componentes concretos de interface.

### 4.6 Skills

Skills são capacidades reutilizáveis que operam sobre o modelo semântico, por exemplo:

- validação;
- diagnóstico;
- classificação;
- geração de feedback;
- scaffolding e fading;
- análise da interação;
- serialização e exportação;
- formulação de hipóteses analíticas.

Skills não constituem o conhecimento estrutural do domínio e não devem redefinir o significado dos objetos.

### 4.7 Tentativa de resolução

Uma tentativa organiza a atividade de um usuário em uma situação-problema específica. Deve permitir vincular:

- ações;
- estados sucessivos das representações;
- verbalizações e explicações;
- feedbacks apresentados;
- eventos do sistema;
- hipóteses analíticas e suas evidências.

A mesma situação-problema pode gerar várias tentativas.

### 4.8 Ação e evento semântico

Uma ação é uma ocorrência significativa na atividade. Um evento semântico é seu registro factual e contextualizado.

O evento deve distinguir a origem:

- `USUARIO`;
- `SISTEMA`;
- `INFERENCIA_COMPUTACIONAL`;
- `PESQUISADOR`.

Um evento não é uma interpretação automática sobre o conhecimento do sujeito.

### 4.9 Verbalização e explicação

Verbalizações e explicações são registros contextualizados da atividade. Podem ocorrer antes, durante ou depois de uma ação e devem ser vinculadas à tentativa e, quando aplicável, ao evento correspondente.

Elas não constituem automaticamente invariantes operatórios.

### 4.10 Evidência e hipótese analítica

Ações, sequências, estratégias, escolhas, erros, acertos e verbalizações podem fornecer evidências para hipóteses revisáveis sobre:

- esquemas;
- teoremas-em-ação;
- conceitos-em-ação.

Uma hipótese deve registrar:

- tipo: `TEOREMA_EM_ACAO`, `CONCEITO_EM_ACAO` ou relação entre ambos;
- classe de situações;
- tentativa;
- registros utilizados como evidência;
- critérios analíticos aplicados;
- nível de sustentação;
- interpretações alternativas;
- estado: candidata, sustentada, revisada, refutada ou inconclusiva;
- autor ou agente responsável pela interpretação.

A ausência de evidência suficiente deve resultar em **nenhuma hipótese**, e não em inferência forçada.

## 5. Princípios arquiteturais obrigatórios

1. O modelo de domínio é a fonte única da verdade semântica do sistema.
2. Representações gráficas e textuais são projeções coordenadas do mesmo estado semântico.
3. Elementos puramente visuais ou interativos não pertencem ao domínio.
4. Regras locais pertencem aos objetos responsáveis por elas.
5. Relações que envolvem vários objetos pertencem a coordenadores de escopo fechado.
6. Políticas pedagógicas gerais pertencem a skills ou serviços especializados.
7. Eventos registram fatos; hipóteses analíticas registram interpretações.
8. Valores calculados pelo sistema nunca devem ser registrados como ações do usuário.
9. A arquitetura deve preservar a possibilidade de resultado inconclusivo na análise do conhecimento-em-ação.
10. O sistema não deve reificar esquemas ou invariantes operatórios como propriedades fixas de elementos da interface.

## 6. Exemplo de aplicação

Em uma representação de Composição de Medidas:

- `Parte1`, `Parte2` e `Todo` são papéis semânticos;
- `Todo = Parte1 + Parte2` é uma relação estrutural formal;
- o arraste de um valor para `Todo` é uma ação do usuário;
- a validação do valor é uma capacidade do sistema;
- o evento registra o que ocorreu e em qual contexto;
- uma explicação do usuário é uma verbalização vinculada à tentativa;
- somente uma análise explícita de múltiplos registros pode sustentar uma hipótese de teorema-em-ação ou conceito-em-ação.

## 7. Vocabulário proibido ou condicionado

Evitar:

- “cada objeto representa um conceito”;
- “cada elemento do diagrama é um conceito”;
- “a relação estrutural é um invariante operatório”;
- “a ação revela diretamente um invariante”;
- “a manipulação produz necessariamente invariantes”;
- “o triângulo é a estrutura essencial do conceito”.

Preferir:

- “objeto semanticamente definido do domínio”;
- “papel semântico em uma representação de situação”;
- “relação estrutural formal”;
- “registro de atividade que pode sustentar uma hipótese”;
- “triplo dinâmico e relacional `C = (S, I, R)`”.
