# Relatório de alteração — gráfico de comparação com segmento graduado, ponto de controle e personagens

**Data:** 2026-07-15  
**Ciclo:** C41  
**Tipo de intervenção:** refinamento visual e teórico-metodológico com intervenção explícita do pesquisador.

## Síntese
A representação de comparação de medidas foi refinada para reduzir textos acessórios e concentrar a leitura na estrutura diagramática. O texto explicativo foi removido e o segmento do valor relativo passou a funcionar como eixo graduado de `0` até `n`, onde `n` é o valor relativo curado. Também foi incluído um ponto de controle visual, no mesmo estilo do eixo dos inteiros, sincronizado com a diferença observável entre as barrinhas do item desconhecido e do item de referência. Além disso, os nomes/objetos preenchidos nos campos de personagens passaram a ser mostrados abaixo de **Referido** e **Referendo** quando disponíveis.

## Alterações implementadas
- remoção do texto explicativo da área de comparação;
- manutenção apenas dos diagramas e de seus rótulos essenciais;
- substituição do cartão textual do valor relativo por um segmento graduado de `0` a `n`;
- inclusão de ponto de controle visual azul sincronizado com as barrinhas;
- exibição de `personagem_1` e `personagem_2` abaixo de **Referido** e **Referendo**;
- preservação da curadoria como fonte da verdade sem alterar os dados internos.

## Verificação realizada
- compilação do projeto em Ant/NetBeans: **aprovada**;
- gráfico de comparação com curadoria de comparação de medidas: **aprovado**;
- preservação dos campos de personagens na curadoria: **mantida**.
