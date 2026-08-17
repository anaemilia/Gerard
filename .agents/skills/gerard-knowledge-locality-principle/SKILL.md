---
name: Knowledge Locality Principle for GERARD
description: Localiza cada tipo de conhecimento no elemento arquitetural responsável, distinguindo conhecimento local, relacional, pedagógico e analítico.
---

# Princípio da Localidade do Conhecimento

## Dependência normativa

Leia `gerard-semantic-model/REFERENCE.md` antes de aplicar esta skill.

## Objetivo

Todo conhecimento deve permanecer próximo do elemento arquitetural que possui autoridade legítima sobre ele.

A localidade não significa colocar toda regra em um objeto individual. A localização correta depende da natureza do conhecimento.

## Tipos de localidade

### 1. Localidade no objeto

Use quando a regra depende apenas da identidade, do estado ou das restrições locais de uma entidade ou papel.

Exemplos:

- domínio numérico aceito por um papel;
- presença de valor;
- identidade semântica;
- descritor abstrato de representação.

### 2. Localidade relacional

Use um coordenador de escopo fechado quando a regra envolve vários objetos irmãos de uma mesma representação.

Exemplos:

- `RelacaoEstruturalComposicao`;
- `RelacaoEstruturalTransformacao`;
- compatibilidade entre papéis e unidades.

Esse coordenador não é uma política global e não deve ser chamado de invariante operatório.

### 3. Localidade pedagógica

Use skills ou serviços especializados quando a decisão envolve política de ensino, adaptação ou mediação.

Exemplos:

- escolher quando fornecer feedback;
- decidir nível de scaffolding;
- aplicar fading;
- selecionar uma intervenção pedagógica.

### 4. Localidade de infraestrutura

Use infraestrutura para persistir, indexar, transportar, renderizar ou exportar.

O domínio pode produzir dados e eventos, mas não grava arquivos, bancos ou logs diretamente.

### 5. Localidade epistemológica

Ações e verbalizações pertencem aos registros da atividade. Hipóteses sobre esquemas, teoremas-em-ação e conceitos-em-ação pertencem a um modelo analítico separado.

Nenhum objeto do diagrama é autoridade sobre o conhecimento-em-ação do usuário.

## Regras

- Evite conhecimento duplicado.
- Evite regras específicas em controllers e utilitários genéricos.
- Não coloque relações multiobjeto em um único papel.
- Não coloque política pedagógica global dentro do objeto de domínio.
- Não trate evento factual como hipótese cognitiva.
- Não trate hipótese analítica como verdade definitiva.
- Preserve a possibilidade de análise inconclusiva.

## Perguntas obrigatórias

Antes de adicionar uma regra, responda:

1. A regra depende de um único objeto?
2. Coordena vários objetos de escopo fechado?
3. É uma política pedagógica reutilizável?
4. É responsabilidade de infraestrutura?
5. É uma interpretação sobre a atividade do usuário?
6. Existe uma fonte normativa que sustenta o nome e o significado usados?

## Exemplo

Para `Todo = Parte1 + Parte2`:

- os domínios numéricos pertencem aos papéis;
- a equação pertence à `RelacaoEstruturalComposicao`;
- a decisão de dar uma pista pertence a uma skill pedagógica;
- o registro da ação pertence a um evento semântico;
- uma hipótese de teorema-em-ação pertence ao modelo analítico e exige evidências.
