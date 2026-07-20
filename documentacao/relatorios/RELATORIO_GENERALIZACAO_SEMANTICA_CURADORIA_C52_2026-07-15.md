# Relatório de alteração — generalização da semântica proveniente da curadoria

**Data:** 2026-07-15  
**Ciclo:** C52  
**Tipo de intervenção:** refatoração semântica e arquitetural com intervenção explícita do pesquisador.

## Síntese
As associações entre papéis semânticos, valores, sinais, termo desconhecido e participantes/objetos deixaram de ser tratadas por regras específicas espalhadas pela interface. Foi criada uma fonte semântica central, alimentada pelos campos da curadoria, para todas as categorias aditivas suportadas.

## Fonte da verdade
A nova classe `SemanticaCuradaSituacao` passa a fornecer:

- os papéis correspondentes à categoria curada;
- os valores registrados em cada papel;
- os sinais de transformação e de valor relativo;
- o papel ocupado pelo item desconhecido;
- os participantes, grupos, animais ou objetos registrados em `personagem_1`, `personagem_2` e `personagem_3`.

Os nomes formais dos papéis continuam localizados em português, inglês e francês, mas a associação concreta entre papel, valor e participante vem da situação selecionada na curadoria.

## Categorias abrangidas
A generalização foi aplicada a:

- composição de medidas;
- transformação de medidas;
- composição seguida de transformação;
- comparação de medidas;
- composição de transformações;
- transformação composta em dois passos;
- transformação de uma relação;
- composição de relações.

## Comparação de medidas
Na comparação, a ordem semântica centralizada é:

1. `referido`;
2. `valor relativo`;
3. `referendo`.

Os valores e participantes exibidos no diagrama de Vergnaud e no gráfico de barras passam a ser recuperados pelos respectivos papéis curados, sem associação específica a nomes ou números de um enunciado particular.

## Compatibilidade
A chave legada `papel.referente` continua aceita como alias interno de `papel.referendo` em pontos de compatibilidade, mas novas estruturas curadas usam `papel.referendo`.

## Verificação realizada
- compilação em Ant/NetBeans: **aprovada**;
- integração da fonte semântica central com a interpretação curada: **aprovada por compilação**;
- integração com rótulos, participantes e valores da comparação: **aprovada por compilação**.
