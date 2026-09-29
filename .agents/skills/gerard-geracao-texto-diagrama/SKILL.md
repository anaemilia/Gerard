---
name: gerard-geracao-texto-diagrama
description: Gera candidatas de enunciados para uso docente a partir de uma relação numérica ou de um estado semântico, sempre rastreadas a situações curadas do Gérard. Use quando um professor pedir novas situações, variações ou exercícios equivalentes. Não valida a candidata nem a grava diretamente no catálogo curado; a promoção para situação validada pertence ao pesquisador humano.
---

# Geração de candidatas de texto a partir do diagrama

## Escopo próprio

Esta skill organiza o caso de uso docente: partir de uma relação numérica ou
de um estado semântico e produzir uma ou mais **candidatas** de
situação-problema. A sintaxe e a construção do enunciado continuam pertencendo
a `gerard-construcao-texto`; esta skill não duplica suas regras.

Antes de gerar, leia integralmente:

- `gerard-construcao-texto`;
- `gerard-semantic-model/REFERENCE.md`;
- `gerard-domain-model-first`;
- `gerard-knowledge-locality-principle`.

## Fronteira de autoridade

Uma relação como `5 + 8 = 13` não determina sozinha uma situação, uma
categoria de Vergnaud, o papel desconhecido nem os personagens. Esses
elementos devem vir do pedido do professor ou de um padrão encontrado nas
situações curadas.

O texto produzido é `CANDIDATA_NAO_CURADA`. Somente o pesquisador humano pode:

- confirmar a categoria e os papéis semânticos;
- revisar personagens, contexto e formulação linguística;
- declarar a situação validada;
- inseri-la no catálogo curado utilizado pelo Gérard.

Nunca apresentar conteúdo gerado como situação já validada nem orientar sua
colagem direta no arquivo canônico.

## Fonte obrigatória

Localize o arquivo curado efetivamente carregado pela versão do Gérard em uso;
não escolha uma cópia apenas pelo nome. Consulte somente linhas validadas e
registre quais `id` ou `situacao_grupo_id` sustentaram cada candidata.

O campo `tipo` curado reconhece exatamente seis categorias canônicas:

**Medidas**

- `COMPOSICAO_MEDIDAS`;
- `TRANSFORMACAO_MEDIDAS`;
- `COMPARACAO_MEDIDAS`.

**Relações**

- `COMPOSICAO_TRANSFORMACOES`;
- `TRANSFORMACAO_RELACAO`;
- `COMPOSICAO_RELACOES`.

Essas são seis categorias, não seis variações obrigatórias de toda expressão
numérica. Incógnitas e subtipos variam dentro das categorias. Identificadores
históricos aceitos para leitura não criam categorias adicionais.

## Procedimento

Depois de identificar a fonte e os limites de autoridade, leia
[`references/procedimento-candidatas.md`](references/procedimento-candidatas.md)
para produzir candidatas e formatar a ficha entregue à revisão humana. O
procedimento não promove conteúdo ao catálogo nem redefine as seis categorias.
