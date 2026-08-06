# Bug — limite superior de arraste de palavra do enunciado sobrepõe a barra de ícones

Data: 2026-08-06. Registrado a partir de relato do usuário (print de tela) durante teste manual; **corrigido** na retomada da mesma tarde, por pedido explícito do usuário.

## Sintoma

Ao arrastar uma palavra comum do enunciado (ex.: "economizado", não um número nem "?") para cima, ela sobrepõe visualmente a barra de ícones de categoria (Medidas/Relações) acima do enunciado, em vez de parar abaixo dela. A palavra "solta" fica presa nessa posição alta, sobreposta à barra, com aparência de texto colado/fora de lugar.

## Causa confirmada

`Main.java`, método `processarMovimentoArraste` (ramo `elementoTextoSelecionado`, dentro de `if (!ehNumeroOuInterrogacaoDoTexto(elementoTextoSelecionado))`):

```java
int limiteSuperior = 58 + elementoTextoSelecionado.altura;
int limiteInferior = 184;
```

`58` e `184` eram constantes fixas em pixels que não acompanhavam `ALTURA_PAINEL_ATALHOS_CATEGORIA` (L7425, `= 130`) — a altura real da barra de ícones de categoria (Medidas/Relações) acima do enunciado. `estaNaAreaDoTexto` (L9838-9840), a função irmã que decide se um ponto está dentro da área do enunciado, já soma essa constante (`55 + ALTURA_PAINEL_ATALHOS_CATEGORIA` / `190 + ALTURA_PAINEL_ATALHOS_CATEGORIA`); o clamp de arraste tinha ficado para trás — provavelmente escrito antes da barra crescer para a altura atual, e nunca atualizado junto.

## Correção aplicada (2026-08-06, tarde)

```java
int limiteSuperior = 58 + ALTURA_PAINEL_ATALHOS_CATEGORIA
        + elementoTextoSelecionado.altura;
int limiteInferior = 184 + ALTURA_PAINEL_ATALHOS_CATEGORIA;
```

Os offsets relativos originais (58, 184) foram preservados — só a constante que faltava foi somada, no mesmo padrão já usado por `estaNaAreaDoTexto`. `limiteEsquerdo`/`limiteDireito` (mesma função) não dependem da altura da barra de ícones (são margens horizontais) — conferido, sem o mesmo problema.

## Verificação

Compilação completa: 435 arquivos, 0 erros. Sem harness automatizado (mesma limitação de todo código de arraste em `Main.java`/Swing) — verificado por leitura comparada com `estaNaAreaDoTexto`, que usa a mesma constante para a mesma área.
