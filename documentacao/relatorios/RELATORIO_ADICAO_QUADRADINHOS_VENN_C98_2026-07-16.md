# Relatório de alteração — adição de quadradinhos nos agrupamentos

**Data:** 2026-07-16  
**Ciclo:** C98

## Alteração
Foi acrescentado um controle circular com sinal de adição ao lado de cada agrupamento do diagrama complementar que representa unidades por quadradinhos.

## Comportamento
- o ícone é desenhado diretamente pelo Swing e não depende de caracteres da fonte;
- ao passar o mouse, aparece a orientação para adicionar uma unidade;
- ao clicar, um novo quadradinho é criado no agrupamento correspondente;
- os quadradinhos são redistribuídos na grade adaptativa sem apagar textos editados nos elementos existentes;
- a quantidade atualizada é propagada para o diagrama de Vergnaud e para o estado semântico compartilhado;
- a ação é registrada no log de interação.

## Escopo
O controle aparece somente em agrupamentos que admitem unidades visuais. Cartões de relação ou valor relativo, que não representam coleções de quadradinhos, permanecem sem o controle.
