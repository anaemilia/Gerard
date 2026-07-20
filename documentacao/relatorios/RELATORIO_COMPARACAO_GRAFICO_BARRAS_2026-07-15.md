# Relatório de alteração — comparação de medidas em gráfico de barras manipulável

**Data:** 2026-07-15  
**Ciclo:** C36  
**Tipo de intervenção:** refinamento teórico-metodológico e visual com intervenção explícita do pesquisador.

## Síntese
A representação da categoria **comparação de medidas** na área antes ocupada pelo diagrama de Venn foi substituída por um **gráfico de barras manipulável**. A decisão foi motivada pela observação do pesquisador de que, para comparação de medidas, a leitura por barras torna mais evidente a relação entre **referido**, **referendo** e **valor relativo**, além de aproximar a interface de representações escolares usuais.

## Alterações implementadas
- substituição do layout da comparação de medidas por duas barras verticais manipuláveis;
- manutenção dos **quadradinhos arrastáveis**, agora organizados como barrinhas/segmentos dentro das barras;
- inclusão de um cartão lateral para o **valor relativo**;
- exibição de resumo visual da diferença entre as quantidades representadas;
- criação de título, descrição e tooltip específicos para o gráfico de comparação em português, inglês e francês;
- preservação dos diagramas anteriores para composição e transformação.

## Decisão de projeto
A alteração foi restrita à representação da categoria **comparação de medidas**. O módulo de composição de medidas continua com a representação de coleções, e as demais categorias mantêm a representação anterior.

## Consistência com a curadoria
A construção do gráfico utiliza os valores sincronizados com a situação-problema curada e com a modelagem do usuário. Assim, a representação gráfica não depende apenas da ordem textual dos números e permanece coerente com os papéis semânticos curados.

## Verificação realizada
- compilação do projeto em NetBeans/Ant: **aprovada**;
- preservação da aba Construir situação-problema: **mantida**;
- internacionalização das novas mensagens: **incluída**.
