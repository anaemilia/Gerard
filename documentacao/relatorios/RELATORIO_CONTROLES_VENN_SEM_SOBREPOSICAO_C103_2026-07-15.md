# Relatório de alteração — reposicionamento dos controles de adição e remoção no diagrama complementar

**Data:** 2026-07-15  
**Ciclo:** C103  
**Tipo de intervenção:** refinamento visual e operacional.

## Síntese
Os controles `+` e `−` dos agrupamentos com quadradinhos estavam sendo desenhados sobre a mesma faixa visual usada pelos números de contagem. Nesta correção, os controles foram reposicionados para a região superior lateral de cada representação, evitando sobreposição com os numerais exibidos ao lado dos agrupamentos.

## Alterações implementadas
- reposicionamento do botão `+` para a parte superior lateral do agrupamento;
- reposicionamento do botão `−` logo abaixo do botão `+`, na mesma coluna visual;
- preservação do mecanismo de fallback para o lado esquerdo quando não há espaço à direita;
- manutenção dos comportamentos de adicionar/remover, logs e sincronização semântica.

## Resultado esperado
Os ícones `+` e `−` deixam de cobrir os números de quantidade, permanecendo visualmente próximos da representação correspondente.

## Verificação realizada
- compilação Ant/NetBeans: **aprovada**.
