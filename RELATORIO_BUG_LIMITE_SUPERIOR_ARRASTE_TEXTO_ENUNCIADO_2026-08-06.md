# Bug registrado — limite superior de arraste de palavra do enunciado sobrepõe a barra de ícones

Data: 2026-08-06. Registrado a partir de relato do usuário (print de tela) durante teste manual, não investigado a fundo nem corrigido — fica para retomada posterior, por pedido explícito ("deixe registrado para depois").

## Sintoma

Ao arrastar uma palavra comum do enunciado (ex.: "economizado", não um número nem "?") para cima, ela sobrepõe visualmente a barra de ícones de categoria (Medidas/Relações) acima do enunciado, em vez de parar abaixo dela. A palavra "solta" fica presa nessa posição alta, sobreposta à barra, com aparência de texto colado/fora de lugar.

## Causa provável (não confirmada por execução — só leitura de código)

`Main.java`, método `processarMovimentoArraste` (ramo `elementoTextoSelecionado`, dentro de `if (!ehNumeroOuInterrogacaoDoTexto(elementoTextoSelecionado))`):

```java
int limiteSuperior = 58 + elementoTextoSelecionado.altura;
```

`58` é uma constante fixa em pixels que não é derivada da altura real da barra de ícones de categoria. Se a barra ocupar mais que ~58px (o que o print sugere), a palavra pode ser arrastada para dentro da área da barra antes de ser barrada pelo clamp.

## Por que não foi corrigido agora

Encontrado durante teste manual do usuário logo após a extração de `RELATORIO_EXTRACAO_DESENHO_ESTILO_INTERACAO_2026-08-06.md` (Fase B2/estilo de interação). Confirmado que não tem relação com essa mudança — o clamp e toda a lógica de `elementoTextoSelecionado` em `processarMovimentoArraste` não foram tocados nela. Por pedido do usuário, fica registrado para uma sessão futura em vez de misturar a correção com o commit já verificado.

## Próximo passo, quando retomado

- Medir/obter a altura real da barra de ícones de categoria (Medidas/Relações) em vez do `58` fixo, ou usar uma constante nomeada que documente a origem do valor.
- Confirmar reprodução isolando o cenário (arrastar uma palavra comum para cima) antes de alterar.
- Verificar se `limiteEsquerdo`/`limiteDireito`/`limiteInferior` (mesma função) têm o mesmo tipo de problema (valores fixos não derivados da geometria real dos painéis vizinhos).
