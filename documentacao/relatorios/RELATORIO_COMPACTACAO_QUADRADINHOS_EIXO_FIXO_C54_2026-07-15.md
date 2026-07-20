# Relatório de alteração — compactação dos quadradinhos e fixação do eixo

**Data:** 2026-07-15  
**Ciclo:** C54  
**Tipo de intervenção:** correção visual e de estabilidade representacional.

## Síntese
Na comparação de medidas, poucos quadradinhos estavam sendo distribuídos por toda a altura da barra, gerando grandes espaços entre eles. Além disso, a posição vertical do eixo do valor relativo dependia da posição do quadradinho superior da barra de Referido, fazendo o eixo mudar quando a quantidade era alterada.

## Alterações implementadas
- adoção de espaçamento compacto padrão entre os quadradinhos;
- organização das unidades de baixo para cima;
- redução adaptativa do tamanho e do espaçamento somente quando a quantidade não cabe na altura da barra;
- fixação da base do eixo pela geometria das barras, sem depender da quantidade de quadradinhos;
- aplicação da mesma base e altura fixa ao desenho, área de interação e cálculo do arraste do ponto azul.

## Resultado esperado
- seis quadradinhos aparecem agrupados na parte inferior da barra, com distância regular;
- a inclusão ou remoção de quadradinhos não desloca verticalmente o eixo;
- o ponto azul continua operando na mesma escala e na mesma posição geométrica.

## Verificação realizada
- compilação em Ant/NetBeans: **aprovada**.
