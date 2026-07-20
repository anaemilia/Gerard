# Relatório de alteração — alinhamento da opção “Na UFPE em 2009”

**Data:** 2026-07-15  
**Ciclo:** C84  
**Tipo de intervenção:** refinamento visual localizado.

## Problema observado
A seta indicadora do submenu aparecia imediatamente após o texto, deixando o item visualmente comprimido e desalinhado em relação à largura do painel.

## Alterações implementadas
- o item passou a ocupar toda a largura útil do menu;
- o texto foi alinhado à mesma margem esquerda dos demais elementos;
- a seta vetorial passou a ser desenhada na extremidade direita da linha;
- foi reservada margem interna para impedir sobreposição entre texto e indicador.

## Resultado esperado
A opção **Na UFPE em 2009** aparece como uma linha de menu completa, com o texto à esquerda e a seta discreta alinhada à direita.

## Verificação realizada
- compilação Ant/NetBeans: **aprovada**;
- regressão geral: **aprovada**.
