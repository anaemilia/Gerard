---
name: gerard-construcao-texto
description: Construção de texto (enunciado de situação-problema) a partir de um diagrama de Vergnaud preenchido, com baixa distância semântica entre os pedaços de texto sugeridos. Use sempre que for gerar, sugerir ou validar enunciados/textos a partir de valores semânticos preenchidos no diagrama do Gérard (composição, transformação ou comparação de medidas).
---

# Construção de texto a partir do diagrama — Gérard

## Fonte curada e compatibilidade

Ao alterar campos de fragmentos, persistência tabular, aliases históricos de
categorias ou o fallback para linhas antigas, leia
[`references/fonte-curada-compatibilidade.md`](references/fonte-curada-compatibilidade.md).
Essa fotografia datada não é necessária para discutir somente a realização
linguística de papéis já fornecidos.

## O que esta skill cobre

Direção inversa da interação normal: em vez de o usuário extrair valores do texto para o diagrama, esta skill trata da geração de texto a partir de um diagrama já preenchido (valores semânticos definidos).

## Regra central

O texto gerado deve ter baixa distância semântica entre os pedaços de texto sugeridos — ou seja, os fragmentos de texto propostos para cada valor semântico (`quantidade_1`, `quantidade_2`, `resultado`, `referido`, `referendo`, `valor_relativo`, `termo_desconhecido`) devem ser próximos, coerentes e naturais em relação ao papel que aquele valor exerce na situação-problema.

Quando fragmentos curados forem fornecidos, use-os por papel em vez de inferir
a correspondência pelo nome da categoria. Na ausência deles, somente o
fallback documentado na referência de compatibilidade pode ser usado.

### Autoridade humana sobre `relacao_final`

Na curadoria de `TRANSFORMACAO_RELACAO`, `relacao_final` — magnitude e sinal — permanece editável mesmo quando a operação (soma ou subtração) está selecionada. Ao salvar, o sistema preserva exatamente o valor informado pelo pesquisador; não o recalcula nem o corrige a partir de `relacao_inicial`, `transformacao` e `operacao`. A consistência dessa declaração curatorial é responsabilidade do pesquisador humano.

## Fonte de verdade obrigatória

Nunca improvise textos ou diagramas sem consultar a situação curada que lhes
dá lastro. Um texto sem referência curada não tem garantia de correção
semântica nem de validação humana. Categorias, papéis e correspondências ricas
seguem `gerard-situacao-problema-agregado`; a sintaxe textual permanece nesta
skill.

## Papéis semânticos por categoria (cuidado com inversão)

### Comparação de medidas — corrigido

**Não existe uma regra fixa de "referendo = incógnita, referido = valor conhecido".** O código (`ResolvedorIncognitaCurada.java:147-150,230-235`, `InferidorSubtipoVergnaud.java:76-79`) e as mensagens localizadas (`mensagens_pt.properties:368,370`) mostram que a comparação de medidas tem **três subtipos possíveis**, dependendo de qual papel é a incógnita naquela situação específica:

1. Referido como incógnita.
2. Diferença (valor relativo) como incógnita.
3. Referendo como incógnita.

Qual papel é a incógnita é definido pelo campo curado `termo_desconhecido` daquela situação específica, não por uma convenção fixa de nomenclatura. **Antes de gerar ou validar um texto de comparação, confira o `termo_desconhecido` da linha curada correspondente — não assuma pela posição/nome do papel.**

(Não foi possível confirmar por amostragem de dados reais: nas linhas de `COMPARACAO_MEDIDAS` do `situacoes_vergnaud.tsv` inspecionadas, as colunas `referido`/`referendo`/`termo_desconhecido` estavam em branco — a curadoria completa desses campos aparentemente ainda não foi feita para essas linhas específicas. Isso não muda a conclusão sobre o código, só significa que não há exemplo de dado real para ilustrar.)

### Transformação de medidas — ressalva

Convenção editorial: mesma pessoa/sujeito ao longo do tempo, entre estado inicial e estado final — não são duas pessoas diferentes. **Isso não é garantido pelo código**: `personagem_1` (ligado a `estado_inicial`) e `personagem_2` (ligado a `estado_final`) são dois campos de texto livre na curadoria (`SemanticaCuradaSituacao.java:63-66`), sem nenhuma validação que force o mesmo nome nos dois. É uma convenção de conteúdo a seguir ao escrever/curar situações, não um invariante que o sistema aplique — ao gerar texto novo, siga a convenção, mas não presuma que dados existentes necessariamente a obedecem sem checar.

## Regra de segurança

Ao gerar um novo texto a partir de um diagrama preenchido, verificar se a situação já existe no log curado (mesmo `situacao_grupo_id`) antes de criar uma variante nova. Se não houver correspondência clara, sinalizar isso ao usuário em vez de inventar um enunciado sem lastro nos dados curados.
