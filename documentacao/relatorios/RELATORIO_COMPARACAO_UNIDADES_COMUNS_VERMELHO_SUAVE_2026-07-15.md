# Relatório de alteração - correspondência cromática das unidades comuns

**Data:** 2026-07-15  
**Ciclo:** C47  
**Tipo de intervenção:** consistência teórico-metodológica e representacional, com intervenção explícita do pesquisador.

## Decisão
Na representação de comparação de medidas, os quadradinhos das barras **Referido** e **Referendo** passam a ser pareados de baixo para cima. A mesma quantidade presente nas duas barras recebe a cor vermelho suave. As unidades excedentes da barra maior permanecem na cor neutra.

Formalmente, se `r` é a quantidade do referido e `e` é a quantidade do referendo, são destacadas as primeiras `min(r,e)` unidades de cada barra, ordenadas de baixo para cima.

## Justificativa
A cor passa a codificar a parte comum entre as duas medidas. Assim, o usuário pode distinguir visualmente:

- a quantidade compartilhada pelas duas barras;
- as unidades excedentes que constituem a diferença ou valor relativo;
- a alteração da correspondência quando o ponto de controle modifica a barra desconhecida.

A escolha de vermelho suave evita contraste agressivo e preserva a legibilidade dos contornos.

## Implementação
- as unidades de cada barra são identificadas por pertencimento geométrico;
- cada conjunto é ordenado de baixo para cima;
- a quantidade comum é calculada por `min(r,e)`;
- os pares correspondentes recebem preenchimento vermelho suave e contorno vermelho mais escuro;
- as unidades excedentes preservam o preenchimento cinza-azulado anterior;
- a correspondência é recalculada a cada renderização, acompanhando a manipulação do ponto azul.

## Verificação
- compilação Ant/NetBeans: **aprovada**;
- compatibilidade com Java 8: **preservada**;
- sincronização dinâmica com as quantidades atuais das barras: **preservada**.
