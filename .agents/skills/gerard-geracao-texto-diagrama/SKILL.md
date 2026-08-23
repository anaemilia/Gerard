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

1. Identifique os valores e a relação numérica fornecidos sem lhes atribuir
   uma categoria por conta própria.
2. Selecione padrões curados compatíveis com a categoria, o subtipo, o papel
   desconhecido, o idioma e o contexto pedidos.
3. Reutilize os fragmentos curados quando existirem; caso contrário, siga o
   fallback permitido por `gerard-construcao-texto`.
4. Realize os mesmos papéis semânticos em sintaxe textual própria, preservando
   a relação numérica e a identidade de cada papel. Personagens são lidos dos
   campos nomeados; nunca são associados por posição no diagrama.
5. Verifique a candidata contra o padrão curado e marque qualquer ausência de
   lastro. Sem correspondência clara, informe a lacuna em vez de improvisar.

## Saída para revisão humana

Para cada candidata, apresente pelo menos:

```text
status: CANDIDATA_NAO_CURADA
categoria:
subtipo:
papel_desconhecido:
valores_por_papel:
personagens_por_campo:
idioma:
enunciado:
referencias_curadas:
observacoes_para_revisao:
```

O formato é uma ficha de revisão, não o esquema de transporte entre cliente e
servidor e não uma linha pronta do catálogo curado.
