---
name: gerard-construcao-texto
description: Construção de texto (enunciado de situação-problema) a partir de um diagrama de Vergnaud preenchido, com baixa distância semântica entre os pedaços de texto sugeridos. Use sempre que for gerar, sugerir ou validar enunciados/textos a partir de valores semânticos preenchidos no diagrama do Gérard (composição, transformação ou comparação de medidas).
---

# Construção de texto a partir do diagrama — Gérard

## Status de verificação (2026-07-20)

1 das 4 afirmações centrais do rascunho original estava **errada** — e era exatamente o tipo de erro que o próprio documento avisava para tomar cuidado (inversão referido/referendo). As outras duas têm ressalvas importantes, corrigidas abaixo. Não trate versões anteriores deste texto como válidas.

## O que esta skill cobre

Direção inversa da interação normal: em vez de o usuário extrair valores do texto para o diagrama, esta skill trata da geração de texto a partir de um diagrama já preenchido (valores semânticos definidos).

## Regra central

O texto gerado deve ter baixa distância semântica entre os pedaços de texto sugeridos — ou seja, os fragmentos de texto propostos para cada valor semântico (`quantidade_1`, `quantidade_2`, `resultado`, `referido`, `referendo`, `valor_relativo`, `termo_desconhecido`) devem ser próximos, coerentes e naturais em relação ao papel que aquele valor exerce na situação-problema.

## Campos de trecho de texto na curadoria (implementado em 2026-07-20)

A `TelaCuradoriaSituacoes` agora tem 3 campos de texto livre — `trecho_texto_1`, `trecho_texto_2`, `trecho_texto_3` — onde o pesquisador pode escrever, para cada um dos três papéis semânticos daquela categoria (nesta ordem), o trecho de frase natural correspondente a ele. Toda categoria simples do domínio (`CategoriaSimples` em `CatalogoEsquemasCategoriasAditivas.java`) tem exatamente três papéis, então os três campos cobrem: `COMPOSICAO_MEDIDAS`, `TRANSFORMACAO_MEDIDAS`, `COMPARACAO_MEDIDAS`, `COMPOSICAO_TRANSFORMACOES`, `TRANSFORMACAO_RELACAO`, `COMPOSICAO_RELACOES`.

As duas categorias compostas (duas categorias simples encadeadas) têm mais papéis, então ganham mais 3 campos — `trecho_texto_4`, `trecho_texto_5`, `trecho_texto_6` — reaproveitando o mesmo padrão uma segunda vez:
- `COMPOSICAO_TRANSFORMACAO_MEDIDAS`: 1-3 cobrem parte_1/parte_2/todo, 4-6 cobrem estado_inicial/transformação/estado_final.
- `TRANSFORMACAO_COMPOSTA_DOIS_PASSOS`: os 6 campos existem no modelo, mas a tela hoje só mostra 4 papéis para essa categoria (estado_inicial, transformacao_1, transformacao_2, estado_final) — a correspondência exata entre trecho_texto_N e cada um desses 4 papéis não foi fixada na implementação inicial; confira `TelaCuradoriaSituacoes.java` (bloco `categoriaComposta`) antes de usar os fragmentos dessa categoria especificamente.

Persistência: os 6 campos (`fragmento_texto_1`..`fragmento_texto_6`) foram adicionados como colunas finais do `situacoes_vergnaud.tsv` e do modelo `SituacaoProblemaAditiva` (getters `getFragmentoTexto1()`..`getFragmentoTexto6()`). São opcionais e retrocompatíveis — linhas antigas do TSV sem essas colunas continuam carregando normalmente, com os fragmentos vazios (validado contra as 210 situações reais do arquivo curado). Nenhuma situação curada existente tem esses campos preenchidos ainda — é infraestrutura nova, não dado retroativo.

Use estes campos, quando preenchidos, como a fonte preferencial de fragmentos de texto por papel — são mais confiáveis do que inferir a partir do `enunciado` inteiro, porque foram escritos pelo pesquisador especificamente para esse propósito. Quando estiverem vazios (a maioria dos dados hoje), caia de volta para a extração a partir do enunciado validado, como já era feito antes.

## Fonte de verdade obrigatória

Para gerar corretamente situações-problema e diagramas por categoria, é obrigatório usar o log curado do Gérard — arquivo `situacoes_vergnaud.tsv`, com colunas `id, situacao_grupo_id, tipo_versao, versao_origem_id, validada, idioma, tipo, contexto, enunciado, fonte, subtipo, estado_inicial, transformacao, sinal_transformacao, estado_final, quantidade_1, quantidade_2, resultado, referido, referendo, valor_relativo, sinal_valor_relativo, termo_desconhecido, representacao_visual, observacoes, personagem_1, personagem_2, personagem_3` — confirmado contra o header real do arquivo. Ele contém itens validados em português, com traduções em inglês/francês.

Nunca improvisar textos ou diagramas sem consultar essa referência. Um texto gerado sem base no log curado não tem garantia de estar semanticamente correto nem de ter sido validado.

## Categorias suportadas — corrigido

- `COMPOSICAO_MEDIDAS`
- `TRANSFORMACAO_MEDIDAS`
- `COMPARACAO_MEDIDAS`
- `COMPOSICAO_TRANSFORMACAO_MEDIDAS` — **faltava na lista original.** É a composição de duas partes seguida de uma transformação (parte_1, parte_2, todo, estado_inicial, transformação, estado_final). Ver `TipoSituacaoAditiva.java:8`.
- `COMPOSICAO_TRANSFORMACOES`
- `TRANSFORMACAO_RELACAO`
- `COMPOSICAO_RELACOES`
- `TRANSFORMACAO_COMPOSTA_DOIS_PASSOS`

Confira `TipoSituacaoAditiva.java` antes de assumir que esta lista está completa — é o enum, não este texto, a fonte de verdade sobre quais categorias existem.

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
