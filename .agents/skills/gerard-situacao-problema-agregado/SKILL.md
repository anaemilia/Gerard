---
name: gerard-situacao-problema-agregado
description: Arquitetura e curadoria da SituaçãoProblema rica que articula EstruturaAditiva, NarrativaCurada e correspondências explícitas. Use ao alterar o agregado, as pontes das seis categorias, sidecar/status editorial ou traduções; não use para mera realização textual.
---

# Situação-problema como agregado rico

## Dependências normativas

Antes de aplicar esta skill, leia:

- `gerard-semantic-model/REFERENCE.md`;
- `gerard-domain-model-first/SKILL.md`;
- `gerard-knowledge-locality-principle/SKILL.md`;
- `gerard-knowledge-oriented-domain-objects/SKILL.md`.

## Escopo e autoridade

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

## Leituras condicionais

- Ao alterar papéis, domínios numéricos, relações ou correspondências de uma
  das seis categorias, leia
  [`references/regras-categorias.md`](references/regras-categorias.md).
- Ao alterar a ponte tabular, sidecar, editor, status editorial ou traduções,
  leia
  [`references/curadoria-persistencia.md`](references/curadoria-persistencia.md).

As referências continuam subordinadas ao contrato deste agregado. Elas
separam condições de consulta; não criam novos proprietários de conhecimento.

## Limites

- A sintaxe da realização textual pertence a `gerard-construcao-texto`.
- Geração de candidatas textuais pertence a `gerard-geracao-texto-diagrama`.
- Eventos narrativos curados não entram no log como ações do participante.
- XML, arquivos, Swing e renderização não entram nos objetos de domínio.
- Nenhum dado ausente pode ser reconstruído por posição, ordem ou texto livre.
