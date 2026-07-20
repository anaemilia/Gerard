# Relatório de intervenção do pesquisador: distância semântica dos blocos

Data: 14 de julho de 2026  
Classificação: intervenção teórico-metodológica e pedagógica

## Situação inicial

A proposta inicial para a nova aba previa apresentar, junto aos trechos corretos, blocos com os mesmos valores numéricos pertencentes a outras categorias de Vergnaud. A intenção era impedir que a montagem fosse resolvida apenas pela correspondência dos números.

Essa formulação ainda deixava uma questão pedagógica sem controle: o grau de afastamento entre os blocos corretos e os não compatíveis.

## Intervenção explícita do pesquisador

O pesquisador estabeleceu que os blocos de outras categorias não poderiam ser muito diferentes dos corretos. Um afastamento excessivo transformaria a atividade em um “jogo dos sete erros”: o usuário eliminaria alternativas por diferenças superficiais, com pouca interpretação do diagrama, baixa exigência cognitiva e tendência ao tédio.

Foi então definido o princípio de **distância semântica controlada**.

Os blocos devem preservar, sempre que possível:

- os mesmos personagens;
- o mesmo objeto ou grandeza;
- o mesmo contexto narrativo;
- os mesmos valores numéricos;
- vocabulário próximo;
- extensão e estrutura sintática aproximadas.

A variação principal deve ocorrer em:

- relação entre as quantidades;
- papel semântico atribuído a cada valor;
- categoria aditiva de Vergnaud;
- sentido de ganho, perda, composição ou comparação.

O controle possui dois limites. Os blocos não podem ser distantes a ponto de permitir eliminação imediata, nem próximos a ponto de produzir duas soluções semanticamente válidas.

## Efeito sobre o requisito

Antes da intervenção, bastaria acrescentar frases pertencentes a outras categorias. Depois da intervenção, a atividade passou a exigir que essas frases fossem construídas sob restrições semânticas e linguísticas explícitas.

A tarefa deixou de ser tratada como ordenação textual e passou a avaliar simultaneamente:

1. leitura do diagrama;
2. identificação da categoria;
3. associação dos valores aos papéis semânticos;
4. seleção dos blocos compatíveis;
5. organização de um enunciado coerente.

## Efeito sobre a implementação

A intervenção foi materializada nas seguintes decisões:

- o gerador preserva personagens, contexto, objeto e valores;
- diferenças são concentradas nas relações semânticas;
- frases meramente contextuais são agrupadas a frases quantitativas, evitando uma pista formal evidente;
- o sinal de transformação ou comparação é preservado;
- são apresentados somente três ou quatro blocos não compatíveis;
- corretos e não compatíveis usam a mesma aparência;
- categoria, papel e condição de acerto permanecem metadados internos;
- a interface não usa rótulos como “distrator”, “correto” ou nomes de categorias;
- a validação exige a combinação correta, e não uma diferença gráfica;
- o log registra o critério `distancia_semantica_controlada` na tentativa.

## Justificativa da classificação

Este episódio não constitui apenas refinamento visual. A intervenção modificou o mecanismo cognitivo esperado na atividade e redefiniu os critérios de validade dos blocos. Por isso, o ciclo foi classificado como **consistência teórica e metodológica** no corpus de co-design humano-IA do Gérard.

A decisão demonstra autoridade epistemológica do pesquisador: a IA havia proposto um formato tecnicamente realizável, mas foi o pesquisador quem determinou o nível de proximidade necessário para que a atividade mobilizasse raciocínio semântico em vez de simples eliminação perceptiva.
