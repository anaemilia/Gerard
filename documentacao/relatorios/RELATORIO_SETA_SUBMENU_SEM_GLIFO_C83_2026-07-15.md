# Relatório de alteração — seta de submenu sem dependência de glifo

**Data:** 2026-07-15  
**Ciclo:** C83  
**Tipo de intervenção:** correção visual e de compatibilidade.

## Problema observado
A seta colocada depois de **Na UFPE em 2009** aparecia como um pequeno quadrado em alguns ambientes. Isso ocorria porque o caractere Unicode utilizado não estava disponível na fonte carregada pela interface.

## Alteração implementada
- remoção do caractere Unicode usado como seta;
- criação de um ícone vetorial simples desenhado pelo próprio Swing;
- posicionamento do ícone depois do texto, preservando o padrão visual de submenu;
- manutenção do espaçamento discreto entre rótulo e indicador.

## Resultado esperado
A opção passa a aparecer como **Na UFPE em 2009** seguida de uma seta discreta para a direita, sem depender da fonte instalada e sem exibir quadrado substituto.

## Verificações realizadas
- compilação Ant/NetBeans: aprovada;
- regressão automatizada do projeto: aprovada.
