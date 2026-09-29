---
name: gerard-knowledge-locality-principle
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

Separe aprendizagem transversal de aplicação local. A política pertence ao
menor escopo que possua os fatos necessários; o proprietário semântico escolhe
em seu repertório, e a interface apenas apresenta a decisão. Para Modelador,
Modelo do Usuário e contextos histórico/corrente, leia
[`references/localidade-pedagogica.md`](references/localidade-pedagogica.md).

### 4. Localidade de infraestrutura

Use infraestrutura para persistir, indexar, transportar, renderizar ou exportar.

Ela não adquire autoridade sobre regras, decisões ou fatos por transportá-los.
Ao tratar API, propriedade de logs, I/O ou ações com vários participantes,
leia
[`references/localidade-infraestrutura.md`](references/localidade-infraestrutura.md).

### 5. Localidade epistemológica

Ações e verbalizações pertencem aos registros da atividade. Hipóteses sobre esquemas, teoremas-em-ação e conceitos-em-ação pertencem a um modelo analítico separado.

Nenhum objeto do diagrama é autoridade sobre o conhecimento-em-ação do usuário.

## Regras

- Evite conhecimento duplicado.
- Evite regras específicas em controllers e utilitários genéricos.
- Não coloque relações multiobjeto em um único papel.
- Não coloque política pedagógica transversal dentro de um papel isolado.
- Não concentre num agente central a escolha que pertence ao repertório de um
  proprietário semântico.
- Não transfira para um logger, serviço ou agente a propriedade do registro
  que pertence ao objeto rico; separe produção factual de persistência técnica.
- Não crie uma ação por objeto quando um único ato envolver vários objetos;
  use um proprietário relacional e referências aos participantes.
- Não entregue o Modelo do Usuário mutável completo aos objetos.
- Não trate evento factual como hipótese cognitiva.
- Não trate hipótese analítica como verdade definitiva.
- Preserve a possibilidade de análise inconclusiva.

## Aplicação em revisão

Ao localizar uma regra nova ou auditar uma distribuição existente, leia
[`references/roteiro-revisao.md`](references/roteiro-revisao.md). O roteiro e
o exemplo não acrescentam tipos de localidade aos cinco definidos acima.
