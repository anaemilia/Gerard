# Relatório de correção — quebra de linha na descrição do gráfico de comparação

**Data:** 2026-07-15  
**Ciclo:** C40  
**Tipo de alteração:** refinamento visual e de legibilidade.

## Problema observado
A descrição exibida abaixo do título **Gráfico de comparação de medidas** era desenhada em uma única linha. Em larguras menores, o texto ultrapassava o limite direito do cartão e ficava cortado.

## Correção realizada
- inclusão de quebra de linha automática conforme a largura disponível no cartão;
- limitação da descrição a duas linhas, com reticências apenas quando o espaço ainda for insuficiente;
- reformulação sucinta da descrição em português, inglês e francês;
- preservação do título, do gráfico, das barras manipuláveis e das demais representações.

## Verificações
- compilação Ant/NetBeans aprovada;
- regressão geral aprovada;
- vínculos de traduções aprovados;
- identificadores técnicos continuam ocultos na curadoria;
- teste da comparação de medidas com `referido = 6`, `valor_relativo = +8` e `referendo = ?` aprovado.
