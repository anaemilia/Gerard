# Fonte curada e compatibilidade da construção textual

Leia esta referência ao alterar campos de fragmentos, persistência tabular,
aliases históricos ou fallback de linhas antigas. As regras linguísticas e de
segurança permanecem no `SKILL.md` proprietário.

## Estado verificado em 2026-07-20

Uma das quatro afirmações centrais do rascunho original estava errada por
inverter Referido e Referendo. As demais possuíam ressalvas incorporadas ao
arquivo principal. Versões anteriores não são fonte válida.

## Fragmentos curados

`TelaCuradoriaSituacoes` possui os campos livres `trecho_texto_1` a
`trecho_texto_6` para trechos correspondentes aos papéis semânticos. A
correspondência é lida dos campos semânticos da situação, nunca inferida apenas
do nome da categoria.

Na persistência, `fragmento_texto_1` a `fragmento_texto_6` são colunas finais
opcionais de `situacoes_vergnaud.tsv` e propriedades de
`SituacaoProblemaAditiva`. Linhas antigas sem essas colunas continuam sendo
lidas com fragmentos vazios; a compatibilidade foi validada contra as 210
situações reais então existentes. Naquela verificação, nenhuma situação
curada tinha esses campos preenchidos: tratava-se de infraestrutura nova, não
de dado retroativo.

Quando preenchidos, os fragmentos curados são a fonte preferencial por papel.
Quando vazios, o fallback permitido é a extração a partir do enunciado já
validado.

## Registro tabular e categorias

O arquivo curado efetivamente carregado é `situacoes_vergnaud.tsv`. Seu
cabeçalho verificado contém:

`id, situacao_grupo_id, tipo_versao, versao_origem_id, validada, idioma, tipo, contexto, enunciado, fonte, subtipo, estado_inicial, transformacao, sinal_transformacao, estado_final, quantidade_1, quantidade_2, resultado, referido, referendo, valor_relativo, sinal_valor_relativo, termo_desconhecido, representacao_visual, observacoes, personagem_1, personagem_2, personagem_3`.

O campo `tipo` reconhece seis categorias canônicas:

- `COMPOSICAO_MEDIDAS`;
- `TRANSFORMACAO_MEDIDAS`;
- `COMPARACAO_MEDIDAS`;
- `COMPOSICAO_TRANSFORMACOES`;
- `TRANSFORMACAO_RELACAO`;
- `COMPOSICAO_RELACOES`.

`COMPOSICAO_TRANSFORMACAO_MEDIDAS` e
`TRANSFORMACAO_COMPOSTA_DOIS_PASSOS` são identificadores históricos aceitos
somente para compatibilidade de leitura e convergem para
`COMPOSICAO_TRANSFORMACOES`. Essa compatibilidade não cria categorias nem
autoriza renomear as demais.
