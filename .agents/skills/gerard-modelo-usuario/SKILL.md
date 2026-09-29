---
name: gerard-modelo-usuario
description: Esquema, versionamento e projeções de leitura do Modelo do Usuário do Gérard — dimensões armazenadas, regras explicáveis publicadas pelo Agente Modelador e fotografia carregada no login. Use ao criar, revisar ou estender perfil, diagnóstico, regra adaptativa ou contexto de usuário. Esta skill possui o esquema; o Modelador possui a aprendizagem e os proprietários semânticos possuem a seleção dentro de seus repertórios locais.
---

# Modelo do Usuário — Gérard

## Status

As cinco dimensões nasceram do Quadro 5.60. Ao verificar classes, consumidores
e fases implementadas, consulte
[`references/estado-implementacao.md`](references/estado-implementacao.md).
O estado do código não redefine o esquema conceitual.

## Regra de leitura do Quadro 5.60

Decisão explícita da usuária em 2026-08-12: somente as colunas 0
(`Dimensões`) e 2 (`Utilização no modelo`) contêm conhecimento que integra a
especificação do Modelo do Usuário. A coluna 1 (`Fonte`) é exclusivamente
referência/proveniência acadêmica.

Consequentemente, nomes como `Ecolab`, `Agente Diagnóstico` e `Cenários
AnimalWatch`, quando aparecem na coluna 1, não são dimensões, atributos,
componentes, agentes, condições de regra ou entradas da decisão adaptativa do
Gérard. Podem ser preservados apenas como citação ou metadado de proveniência,
separados do conhecimento executável. A fonte nunca altera a seleção de ajuda
nem o conteúdo de uma fotografia do Modelo do Usuário.

## Perfil não é o Modelo do Usuário inteiro

Decisão explícita da usuária em 2026-08-12: os perfis integram o Modelo do
Usuário, mas não são, sozinhos, o elemento de tomada de decisão adaptativa. O
modelo possui outras dimensões — nível/complexidade das tarefas, partes do
conhecimento e fases e diagnóstico da tarefa — que não podem ser substituídas
por preferências cadastrais ou de apresentação.

Preferências do perfil podem orientar como uma decisão já fundamentada será
materializada, por exemplo modalidade de mídia, forma da mensagem ou estilo de
interação. Elas não determinam isoladamente se haverá ajuda, quando ela será
oferecida nem qual função pedagógica será escolhida. A tomada de decisão deve
consultar a projeção multidimensional pertinente do Modelo do Usuário, o
diagnóstico factual corrente e as regras publicadas aplicáveis. Só depois dessa
decisão a mídia preferida escolhe a forma concreta de apresentação entre as
formas disponíveis para o repertório do proprietário semântico.

Um teste que varia somente preferências é válido como teste separado de
personalização/materialização da interface. Ele não deve ser apresentado como
teste da tomada de decisão adaptativa do Modelo do Usuário completo.

## Dimensões do Modelo do Usuário

### 1. Nível de tarefas (complexidade)

Tarefas variam de mais fáceis a mais complexas, dentro das categorias de estruturas aditivas usadas no Gérard (composição, transformação, comparação). Referência: organização de tarefas por grau de complexidade (Magina et al., 2000).

### 2. Partes do conhecimento e fases

Em qual categoria de estruturas aditivas o usuário tem maior domínio, e qual ele domina menos.

### 3. Perfil do aluno

Identificação cadastral do usuário. Para campos e uso permitido, leia
[`references/dimensoes-perfil.md`](references/dimensoes-perfil.md).

### 4. Perfil da aprendizagem

Preferências de materialização e escolaridade não decidem isoladamente se
haverá ajuda nem sua função pedagógica. Para campos, cardinalidade e uso,
leia [`references/dimensoes-perfil.md`](references/dimensoes-perfil.md).

### 5. Diagnóstico da tarefa

O diagnóstico articula tarefa, suporte, internalização, probabilidade, autorrelato
e campos analíticos curados. Ao alterar qualquer desses campos, sua origem ou
seu uso na inferência, leia
[`references/dimensao-diagnostico-tarefa.md`](references/dimensao-diagnostico-tarefa.md).
O esquema da própria ação instrumental permanece em
`gerard-log-acao-instrumental`.

## Relação com os agentes

- O **Agente Modelador** normaliza casos, executa J48/PART + Apriori e publica
  uma nova versão explicável deste modelo.
- No login, o sistema cria uma fotografia de sessão imutável da versão
  publicada mais recente para o usuário.
- Cada proprietário semântico consulta apenas uma projeção de leitura dessa
  fotografia e escolhe uma ajuda do próprio repertório.
- A seleção da ajuda pertence ao proprietário semântico, dentro de seu
  repertório local.

## Regras adaptativas publicadas

Ao criar, revisar ou alterar o ciclo de vida de regras publicadas, leia
[`references/regras-publicadas.md`](references/regras-publicadas.md). O
formato da regra não redefine as dimensões do modelo nem autoriza atribuições
analíticas automáticas.

## Fotografia da sessão e projeções

Ao alterar estabilidade da sessão, projeções ou contexto adaptativo, leia
[`references/fotografia-e-projecoes.md`](references/fotografia-e-projecoes.md).
O estado concreto da implementação permanece na referência seguinte.

## Estado da implementação

Ao alterar fotografia, carregamento no login, projeções, primeiro consumidor
ou rastreabilidade das decisões, leia
[`references/estado-implementacao.md`](references/estado-implementacao.md).
Esse histórico não é necessário para consultar o esquema conceitual do Modelo
do Usuário nem para distinguir perfil, diagnóstico e regras publicadas.
