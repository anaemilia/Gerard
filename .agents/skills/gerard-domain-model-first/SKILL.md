---
name: GERARD Domain Model First
description: Define o modelo de domínio semanticamente significativo como fonte única da verdade, sem confundir domínio, representação e interação.
---

# Modelo de Domínio como Fonte Única da Verdade

## Dependência normativa

Antes de aplicar esta skill, leia `gerard-semantic-model/REFERENCE.md`.

As definições de conceito, esquema, invariante operatório, teorema-em-ação, conceito-em-ação, representação, papel semântico e relação estrutural não podem ser redefinidas por esta skill.

## Objetivo

Todo elemento **semanticamente significativo** apresentado no GERARD deve corresponder a uma entidade, papel, valor, relação, situação, tentativa ou propriedade do modelo de domínio.

O diagrama, a GTN e outras formas visuais, textuais, numéricas ou linguísticas são representações coordenadas desse modelo. Elementos puramente visuais ou interativos permanecem nas camadas de representação e interação.

## Fonte única da verdade

O modelo de domínio deve concentrar:

- identidade e estado semântico;
- papéis nas representações de situações;
- valores e domínios numéricos;
- relações estruturais formais;
- restrições e invariantes computacionais;
- tentativas e estados relevantes da atividade;
- dados necessários para projeções e serialização.

Nenhuma regra semântica pode existir apenas na interface, na GTN, em um agente ou em um persistidor.

## Separação de níveis

### Domínio

Define o significado e o estado semanticamente relevante.

Pode conhecer:

- posição semântica em um esquema;
- papel e relações permitidas;
- domínio numérico;
- descritores abstratos de representação;
- regras locais e relações estruturais;
- estado anterior e posterior de operações do domínio.

Não pode conhecer:

- coordenadas em pixels;
- tamanho de componentes de tela;
- `Graphics2D`, Swing, JavaFX ou tecnologia equivalente;
- sombras, animações e efeitos visuais;
- alças e gestos específicos de mouse.

### Representação

Projeta o domínio em formas gráficas, textuais, numéricas, simbólicas ou linguísticas.

É responsável por:

- renderização;
- layout e coordenadas;
- aparência concreta;
- tradução de descritores abstratos em componentes visuais;
- sincronização com o estado do domínio.

### Interação

Define como usuário, sistema, agente ou pesquisador produz solicitações de mudança no domínio.

É responsável por:

- mouse, teclado, toque, GTN ou comandos de IA;
- conversão de gestos em comandos semanticamente identificados;
- distinção da origem da ação;
- apresentação de feedbacks e apoios.

Para um padrão concreto (ainda não implementado) de como estruturar essa camada em código, ver `gerard-handlers-de-interacao`.

## Representação dinâmica

A representação não deve ser tratada somente como uma forma estática. Manipulações, transformações e passagens entre formas gráficas, simbólicas e linguísticas podem integrar o processo representacional.

O modelo preserva a identidade e o significado dos elementos; skills e camadas de interação podem variar os apoios e as formas concretas apresentadas.

## Responsabilidades dos objetos

Um objeto de domínio deve responder apenas às perguntas coerentes com sua responsabilidade, por exemplo:

- Qual é minha identidade semântica?
- Qual papel exerço nesta representação?
- Qual estado possuo?
- Quais valores aceito?
- Com quais relações estruturais posso participar?
- Qual descritor abstrato de representação ofereço?
- Como sou serializado semanticamente?
- Minha condição local é consistente?

Perguntas sobre desenho concreto, posição de tela ou gesto de mouse pertencem a outras camadas.

## Relações que envolvem vários objetos

Uma regra que coordena papéis irmãos deve ser modelada por um objeto de escopo fechado, como:

- `RelacaoEstruturalComposicao`;
- `RelacaoEstruturalTransformacao`;
- `RelacaoEstruturalComparacao`.

Não coloque uma relação de vários papéis dentro de um único papel apenas para evitar serviços.

## Esquemas e conhecimento-em-ação

O sistema pode registrar ações e formular hipóteses analíticas sobre esquemas, teoremas-em-ação e conceitos-em-ação. Essas hipóteses não são estados factuais do diagrama e não devem ser confundidas com relações formais do domínio.

## Decisões obrigatórias

- O modelo de domínio é a única fonte de verdade semântica.
- O diagrama e a GTN são projeções do modelo.
- Todas as projeções usam as mesmas identidades semânticas.
- Nenhuma regra semântica fica na UI.
- O domínio não depende da tecnologia de interface.
- Relações formais recebem nomes de relações estruturais, nunca de invariantes operatórios.
- Eventos factuais e hipóteses analíticas permanecem separados.

## Anti-padrões

- Tratar cada componente visual como objeto de domínio.
- Chamar `Parte`, `Todo` ou `Transformação` de conceito completo.
- Armazenar estado somente em nós gráficos.
- Duplicar regras entre GTN e interface.
- Criar `InvarianteOperatorio` para verificar apenas uma equação formal.
- Registrar um cálculo automático como ação do usuário.
- Acoplar o domínio a Swing, JavaFX, AWT, pixels ou controles de mouse.

## Checklist

- O elemento é semanticamente significativo ou apenas visual?
- O estado está no domínio ou somente na interface?
- A regra é local, relacional ou uma política global?
- A relação foi nomeada como relação estrutural?
- GTN e diagrama continuam sincronizados pelo mesmo modelo?
- A representação concreta está separada do descritor abstrato?
- Eventos e hipóteses analíticas estão separados?
