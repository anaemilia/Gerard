# Relatório de alteração - remoção incremental de quadradinhos

**Data:** 2026-07-16  
**Ciclo:** C100

## Alteração
Foi acrescentado um controle vetorial de subtração ao lado de cada agrupamento que possui quadradinhos visíveis. O controle aparece somente quando há ao menos uma unidade que possa ser removida.

## Comportamento
- remove exatamente um quadradinho por acionamento;
- preserva os textos dos quadradinhos remanescentes;
- reorganiza a grade adaptativa;
- registra a ação no log;
- sincroniza a nova cardinalidade com o estado semântico compartilhado e com as demais representações;
- aplica-se a todas as categorias cujas representações complementares exibem quadradinhos.

## Verificação
- compilação Ant/NetBeans aprovada;
- internacionalização em português, inglês e francês;
- PDFs do artigo e da análise de complexidade atualizados.
