# Regras das seis categorias no agregado rico

Leia esta referência ao alterar papéis, domínios numéricos, relações ou
correspondências entre a estrutura aditiva e a narrativa curada.

## Composição de transformações

Na categoria canônica `COMPOSICAO_TRANSFORMACOES`, a estrutura rica possui
exatamente seis papéis: estado inicial, transformação 1, estado intermediário,
transformação 2, transformação resultante e estado final. O estado
intermediário deve apontar explicitamente para o estado produzido pelo evento
curado correspondente; não o associe por índice de personagem, posição de
campo ou ordem visual.

As transformações 1 e 2 representam eventos efetivos e pertencem aos inteiros
não nulos. A transformação resultante não representa um terceiro evento e
permanece no domínio dos inteiros: ela pode ser zero quando os efeitos se
anulam, como em `+2 + (-2) = 0`.

## Composição de medidas

Na categoria canônica `COMPOSICAO_MEDIDAS`, a estrutura rica possui exatamente
três papéis — parte 1, parte 2 e todo — ligados pela relação pertencente a
`RelacaoEstruturalComposicao`. Uma narrativa de composição estática pode ter
zero eventos: nesse caso, o estado final declarado preserva o inventário
inicial e existe para validar a declaração humana, não para inventar uma
transformação temporal.

Quando a curadoria distinguir variantes contadas de uma mesma família, uma
parte pode referenciar explicitamente um `ObjetoContado`, enquanto o todo pode
referenciar o total da `FamiliaObjeto`. Essa é uma possibilidade de modelagem
declarada pelo humano, não uma regra universal da categoria nem uma associação
inferida do texto, da ordem ou da posição visual.

## Comparação de medidas

Na categoria canônica `COMPARACAO_MEDIDAS`, a estrutura rica possui Referido,
Valor Relativo e Referendo, ligados por `RelacaoEstruturalComparacao` segundo
`Referendo = Referido + ValorRelativo`. Referido e Referendo pertencem aos
naturais; Valor Relativo pertence aos inteiros e pode ser nulo. Na narrativa
estática, a referência do Valor Relativo recebe nominalmente o participante do
Referendo e o participante do Referido e calcula a diferença nessa ordem. Não
associe esses participantes pela posição dos campos `personagem_*`, pela ordem
textual ou pela posição visual. A chave viva é `papel.diferenca`; “Valor
Relativo” é o nome conceitual do papel.

## Transformação de medidas

Em `TRANSFORMACAO_MEDIDAS`, Estado Inicial e Estado Final são medidas naturais,
enquanto a Transformação representa uma mudança efetiva e pertence aos inteiros
não nulos. Somente nesse esquema a consequência pode ser expressa como: uma
transformação direta igual a zero produziria `EstadoFinal = EstadoInicial` e não
constitui uma situação de Transformação de Medidas.

## Categorias de números relativos

Não transfira essa justificativa para as categorias de números relativos. Em
`TRANSFORMACAO_RELACAO`, Relação Inicial e Relação Final pertencem aos inteiros e
podem ser zero; a transformação operante continua sendo um evento efetivo e não
nulo. Assim, `RelaçãoInicial = +2` e `Transformação = -2` produzem legitimamente
`RelaçãoFinal = 0`. Em `COMPOSICAO_TRANSFORMACOES`, as duas transformações
componentes são eventos não nulos, mas a Transformação Resultante pode ser zero
quando seus efeitos se anulam. Em `COMPOSICAO_RELACOES`, todos os papéis são
relações inteiras e podem ser zero. Valide o domínio de cada papel; nunca rejeite
zero genericamente por pertencer a uma categoria de Relações.

Na ponte rica de `COMPOSICAO_RELACOES`, cada um dos três papéis referencia uma
diferença orientada entre participantes nominalmente declarados. A operação
curada pertence à relação estrutural do agregado: soma pode realizar uma
configuração encadeada e subtração pode realizar uma configuração com
referência comum. Não determine essa operação pelos sinais, pelo enunciado,
pelos campos `personagem_*` ou pela posição visual. O pesquisador declara tanto
a operação quanto as três correspondências, e uma divergência permanece como
candidata diagnosticada. Relações opostas podem totalizar zero sem violar o
domínio de nenhum papel.

Em `TRANSFORMACAO_RELACAO`, não confunda a relação estrutural com a operação
pedida ao participante. A transformação descreve uma mudança; soma ou subtração
selecionada na interface é um procedimento de resolução curado. Preserve esse
segundo conhecimento em um critério próprio, como `CriterioOperacaoModelagem`,
sem usá-lo para recalcular a relação final declarada pelo pesquisador. O objeto
relacional considera os participantes nominalmente associados a cada relação e
ao evento, inclusive quando a relação final inverte a orientação da inicial.
Esta decisão é específica da ponte dessa categoria e não redefine as operações
já curadas das outras categorias.
