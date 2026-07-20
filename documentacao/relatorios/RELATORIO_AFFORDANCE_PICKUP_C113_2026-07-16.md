# Relatório de alteração — affordance de pickup

**Data:** 2026-07-16  
**Ciclo:** C113  
**Tipo de intervenção:** refinamento de interação e arquitetura.

## Objetivo
Reforçar visualmente o momento em que um elemento manipulável é pego pelo usuário, sem alterar posições semânticas, regras de sincronização, limites, logs ou demais comportamentos consolidados.

## Comportamentos implementados
- cursor de mão aberta ao passar sobre elementos arrastáveis;
- cursor de mão fechada durante o arraste;
- escala visual discreta de 106% no pickup;
- elevação de dois pixels e sombra curta;
- remoção temporária do elemento da passagem normal de pintura;
- redesenho do elemento segurado em uma passagem final, garantindo prioridade visual.

## Elementos abrangidos
- números e interrogação conduzidos do enunciado;
- palavras móveis do enunciado;
- itens semânticos posicionados no diagrama;
- quadradinhos das representações complementares;
- elementos e conectores manipuláveis do diagrama de Vergnaud;
- pontos de controle do eixo inteiro e da comparação.

## Arquitetura
O recurso segue a diretriz de interfaces como contratos, herança e polimorfismo:

- `DesenhavelPickup`: contrato mínimo dos elementos renderizáveis;
- `RenderizadorPickup`: contrato de apresentação;
- `RenderizadorPickupAbstrato`: template comum de escala, elevação e sombra;
- `RenderizadorPickupElevado`: implementação concreta do estilo discreto;
- `FornecedorCursoresPickup`: contrato dos cursores;
- `FornecedorCursoresPickupAbstrato`: cache e fallback compartilhados;
- `FornecedorCursoresPickupSwing`: implementação vetorial dos cursores.

## Preservação funcional
A transformação é exclusivamente visual. As coordenadas reais, áreas de colisão, valores semânticos e regras de soltura permanecem inalterados.

## Verificações
- compilação Ant/NetBeans: aprovada;
- regressão geral: aprovada;
- teste unitário da escala, sombra e fallback dos cursores: aprovado;
- teste Swing de pickup, movimento e soltura de item e quadradinho: aprovado;
- contratos de unidades, renderização 2,5D, limites semânticos, adição e remoção: aprovados;
- mapeamento visual–semântico da comparação: aprovado;
- feedback multissensorial, ajuda contextual e inicialização sem categoria: aprovados;
- inspeção visual em ambiente gráfico virtual: aprovada.
