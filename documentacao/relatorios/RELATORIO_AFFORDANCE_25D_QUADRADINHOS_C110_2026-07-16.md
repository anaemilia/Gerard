# Relatório de alteração — affordance 2,5D discreta dos quadradinhos

**Data:** 2026-07-16  
**Ciclo:** C110  
**Tipo de intervenção:** refinamento visual, interação e arquitetura.

## Síntese
Os quadradinhos permanecem essencialmente planos para favorecer a contagem, mas recebem um relevo discreto que reforça sua condição de unidades manipuláveis. O efeito torna-se mais evidente apenas no foco e durante o arraste.

## Alterações implementadas
- sombra curta e suave no estado normal;
- realce mínimo nas bordas superior e esquerda;
- escurecimento discreto nas bordas inferior e direita;
- intensificação moderada do relevo no `mouseover`;
- elevação visual e sombra um pouco mais pronunciada durante o arraste;
- preservação das paletas semânticas de composição, comparação e transformação;
- manutenção dos textos internos e da contagem visual;
- ausência de perspectiva, faces internas ou gradientes fortes.

## Arquitetura
A renderização foi organizada por contratos e polimorfismo:

- `RenderizadorUnidadeVenn`: contrato comum;
- `RenderizadorUnidadeVennAbstrato`: herança do comportamento compartilhado;
- implementações concretas para composição, comparação correspondente, comparação excedente e transformações positiva/negativa;
- `FabricaRenderizadoresUnidadeVenn`: seleção polimórfica da implementação adequada.

## Resultado esperado
A unidade continua simples e contável em repouso. Ao receber foco ou ser arrastada, passa a comunicar com maior clareza que pode ser manipulada, sem assumir aparência de bloco tridimensional físico.
