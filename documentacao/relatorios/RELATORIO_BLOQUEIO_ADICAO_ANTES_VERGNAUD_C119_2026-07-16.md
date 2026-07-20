# C119 — Bloqueio da adição de unidades antes da modelagem em Vergnaud

## Regressão identificada

O controle `+` das representações complementares, especialmente no gráfico de barras da comparação de medidas, aceitava cliques antes de os elementos semânticos terem sido posicionados no diagrama de Vergnaud.

## Regra restaurada

A adição de quadradinhos somente é liberada depois que os três elementos semânticos ativos da modelagem estão explicitamente posicionados no diagrama de Vergnaud. A interrogação conta como elemento posicionado e permanece `?`.

São aceitas duas formas explícitas de preenchimento:

1. posicionamento de um item do enunciado sobre o elemento semântico correto;
2. edição direta do elemento por duplo clique.

Valores produzidos automaticamente pela sincronização entre representações não liberam o controle.

## Comportamento da interface

Antes da conclusão da modelagem:

- o `+` aparece em cinza;
- o cursor permanece padrão;
- o clique não cria unidades;
- o tooltip orienta: “Posicione primeiro os elementos semânticos no diagrama de Vergnaud.”

Depois da conclusão da modelagem:

- o `+` recupera a aparência azul e o cursor de mão;
- um clique acrescenta exatamente uma unidade;
- o limite semântico curado e o feedback de limite permanecem ativos.

## Arquitetura

A condição foi isolada por contratos e polimorfismo:

- `EstadoModelagemVergnaud`;
- `CondicaoHabilitacaoAdicaoUnidades`;
- `CondicaoHabilitacaoAdicaoUnidadesAbstrata`;
- `CondicaoAposPosicionamentoVergnaud`.

O estado de edição direta foi acrescentado a `ElementoVergnaud` sem alterar atualizações reativas ou a preservação da interrogação.

## Verificações

- compilação Ant/NetBeans;
- bloqueio real do clique antes da modelagem;
- liberação após os três elementos explicitamente preenchidos;
- incremento unitário após a liberação;
- aparência desabilitada e habilitada do controle;
- limite semântico;
- mapeamento visual–semântico da comparação;
- sincronização textual com preservação da interrogação;
- pickup, cursores e arraste físico;
- regressão estrutural geral.
