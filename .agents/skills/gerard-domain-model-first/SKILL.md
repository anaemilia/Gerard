---
name: gerard-domain-model-first
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

A **API semântica do Gérard** é uma fronteira de publicação desse modelo para
adaptadores web e mobile. O nome qualifica o conteúdo transportado, não torna
HTTP ou JSON proprietários do significado. Consulte `gerard-api-semantica`
para contratos, versionamento e maturidade dessa fronteira.

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
- repertórios de ajuda específicos dos proprietários semânticos;
- seleção semântica dentro desses repertórios a partir de diagnóstico factual
  e contexto adaptativo imutável.

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

Produção factual permanece com o proprietário do conhecimento e persistência
permanece na infraestrutura. Ao alterar a fronteira entre gesto, comando,
ação instrumental e evento persistido, consulte `gerard-log-gestos-interacao`,
`gerard-log-acao-instrumental` e `gerard-semantic-event-logging`; esta skill
não possui seus esquemas nem suas identidades de correlação.

Para um padrão concreto (ainda não implementado) de como estruturar essa camada em código, ver `gerard-handlers-de-interacao`.

## Representação dinâmica

A representação não deve ser tratada somente como uma forma estática. Manipulações, transformações e passagens entre formas gráficas, simbólicas e linguísticas podem integrar o processo representacional.

O modelo preserva a identidade e o significado dos elementos; skills e camadas de interação podem variar os apoios e as formas concretas apresentadas.

## Interior dos objetos de domínio

Esta skill define em qual camada o conhecimento reside, mas não repete como
dividi-lo entre objeto local, relação estrutural, agregado e hipótese
analítica. Ao criar ou enriquecer esses elementos, aplique
`gerard-knowledge-oriented-domain-objects`.

Permanece obrigatória a fronteira: desenho concreto, posição e gesto ficam
fora do domínio; relações multiobjeto não são empurradas para um papel
isolado; aprendizagem e persistência do Modelo do Usuário não pertencem ao
objeto; hipóteses sobre conhecimento-em-ação não se tornam estado factual.

## Decisões obrigatórias

- O modelo de domínio é a única fonte de verdade semântica.
- O diagrama e a GTN são projeções do modelo.
- Todas as projeções usam as mesmas identidades semânticas.
- Nenhuma regra semântica fica na UI.
- O domínio não depende da tecnologia de interface.
- Relações formais recebem nomes de relações estruturais, nunca de invariantes operatórios.
- Eventos factuais e hipóteses analíticas permanecem separados.

## Revisão arquitetural

Ao auditar uma alteração, classificar um elemento ou revisar regressões de
fronteira, leia
[`references/checklist-fronteiras.md`](references/checklist-fronteiras.md).
O checklist operacional não substitui as decisões obrigatórias desta skill.
