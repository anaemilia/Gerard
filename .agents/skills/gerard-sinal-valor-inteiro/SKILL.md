---
name: gerard-sinal-valor-inteiro
description: Semântica e protocolo da seleção de sinal para papéis cujo universo numérico é inteiro no Gérard. Use ao alterar aceitação de valores assinados, correspondência entre NumeroInteiro e +/-, capacidade publicada pela API ou ordem número-antes-do-sinal no desktop e na web.
---

# Sinal de valores inteiros

## Responsabilidade

Esta skill possui a correspondência entre o universo numérico declarado por um
papel, seu valor inteiro e a opção de sinal materializada pelas representações.
Ela não possui fórmulas entre papéis, propagação de estado, gestos, geometria,
aparência ou persistência.

Leia as fontes obrigatórias declaradas em `dependencies.json`. O modelo
semântico define os papéis; o domínio continua sendo a fonte do universo
numérico aceito por cada papel.

## Contrato

- A capacidade de receber sinal vem do papel e de seu valor numérico, nunca da
  categoria, do índice visual, do texto ou da posição na tela.
- Qualquer papel que o domínio declare no universo dos inteiros pode
  materializar seleção de sinal. Quantidades declaradas naturais não podem
  receber valor negativo nem exibir essa capacidade como se a possuíssem.
- Uma incógnita conserva o universo esperado mesmo antes de possuir valor.
- A API semântica publica essa capacidade ou o descritor equivalente; clientes
  web e mobile não a redescobrem por nomes de categoria ou de componente.

Não mantenha uma lista paralela de categorias assinadas na interface. Quando
novos papéis inteiros forem introduzidos, sua capacidade deve decorrer do
modelo publicado.

## Ordem da interação

O seletor de sinal integra o preenchimento do valor inteiro, mas não antecede o
número. A ordem preservada é:

1. posicionar ou inserir o número no papel;
2. quando o papel declarar universo inteiro, materializar a escolha de sinal;
3. avaliar a opção no proprietário semântico;
4. aplicar localmente o valor assinado;
5. entregar a mudança ao protocolo de propagação e confirmação já existente.

A interface pode apresentar questionamento após uma escolha incompatível, mas
não decide C/E e não corrige silenciosamente o valor. A migração do veredito
para o domínio não autoriza antecipar, desfazer ou recalcular a sincronização.

## Correspondência representacional

`NumeroInteiro` possui a correspondência entre seu valor e a opção da
representação binária. Na sintaxe atual, que oferece somente `+` e `-`, zero
corresponde à opção MAIS. Essa é uma decisão de representação compatível com a
interface existente; não afirma conceitualmente que zero seja positivo.

Quando o valor curado está ausente, é `?` ou não é inteiro, não existe critério
para avaliar o sinal. A ausência de critério não autoriza inferência pelo
enunciado, pela geometria ou por outro componente.

## Distribuição das responsabilidades

- O papel e `NumeroInteiro` conhecem universo, valor normativo e veredito.
- A relação estrutural conhece cálculos que envolvem vários papéis.
- A propagação difunde o novo estado depois da decisão local.
- O handler conhece a sequência física de interação.
- Swing, React e outras representações materializam o descritor recebido.
- A API transporta a capacidade sem se tornar sua proprietária.
- O logger registra o fato decidido; não reavalia o sinal.

## Verificação

Ao alterar esta responsabilidade:

- teste papéis inteiros e naturais sem selecionar casos por categoria na UI;
- confirme a ordem número, sinal e propagação;
- compare desktop e web usando o mesmo descritor semântico;
- verifique zero, positivo, negativo, incógnita e valor curado ausente;
- confirme que quantidades naturais não se tornam negativas em nenhuma
  representação;
- execute a regressão existente antes de considerar a mudança concluída.

