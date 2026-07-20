# Relatório de alteração - estado semântico compartilhado entre representações

**Data:** 2026-07-15  
**Ciclo:** C67  
**Tipo de intervenção:** generalização arquitetural e consistência entre representações.

## Regra consolidada
Cada categoria possui representações diagramáticas manipuláveis que expressam o mesmo estado matemático. Portanto, nenhuma representação deve funcionar como fonte isolada de verdade. Toda ação do usuário atualiza um estado semântico compartilhado e todas as representações são reconstruídas a partir desse estado.

## Implementação
Foi criado o módulo:

`gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado`

O módulo mantém:
- os três papéis principais da categoria;
- indicação de quais valores já foram modelados;
- papel alterado na ação corrente;
- origem da alteração;
- versão do estado;
- resolução da relação aditiva canônica `A + B = C`.

## Origens de alteração tratadas
- diagrama de Vergnaud;
- diagrama complementar/Venn;
- eixo x dos inteiros;
- eixo vertical da comparação;
- edição por duplo clique;
- arraste;
- exclusão;
- protocolos de interação/scaffolding.

## Fluxo arquitetural
1. uma ação altera uma representação;
2. os valores observáveis são enviados ao estado compartilhado;
3. a relação matemática da categoria é resolvida;
4. Vergnaud, diagrama complementar e eixos recebem o mesmo snapshot;
5. uma trava de sincronização evita ciclos de atualização recursiva.

## Relações preservadas
- composição: `Parte 1 + Parte 2 = Todo`;
- transformação: `Estado inicial + Transformação = Estado final`;
- comparação: `Referido + Valor relativo = Referendo`;
- composições e transformações de relações: estrutura geral `A + B = C` para os três papéis principais.

## Verificações
- compilação Ant/NetBeans: **aprovada**;
- regressão estrutural, i18n, vínculos, curadoria e logs: **aprovada**;
- teste isolado do estado compartilhado: **aprovado**;
- casos verificados no teste: composição `8 + 6 = 14`, edição do Todo, comparação `6 + 8 = 14` e transformação negativa `10 + (-3) = 7`.

## Limite da verificação
A compilação, a regressão automatizada e o teste do modelo foram executados. Não foi realizada inspeção visual manual exaustiva de todas as combinações de arraste em todas as categorias.
