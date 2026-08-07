# Relatório: botões de sorteio flanqueando o separador (simetria)

Data: 2026-08-07

## Contexto

Continuação direta de `RELATORIO_BOTOES_SORTEIO_JUNTO_GRUPOS_2026-08-07.md`
(commit `1ca18fd`). A usuária notou, olhando o resultado, que o layout
ficou visualmente estranho: "Sortear Medidas" ficava entre o grupo
Medidas e o separador central (parecendo uma peça de transição), enquanto
"Sortear Relações" ficava sozinho na ponta direita do painel, depois do
último ícone, com um vão grande até o botão "próximo passo" (que só
aparece depois que uma categoria é confirmada) — os dois pareciam
desalinhados um em relação ao outro.

## O que mudou

Só `reposicionarPainelAtalhoCategoria` (`Main.java`): `botaoFerramenta
SortearRelacoes` deixou de ficar depois do 3º ícone de Relações e passou
a ficar logo **depois do separador central**, espelhando `botaoFerramenta
SortearMedidas`, que já ficava logo **antes** dele. Os dois agora
flanqueiam o separador de forma simétrica, e nenhum fica pendurado
sozinho numa borda do painel. `larguraTotal` (usada para centralizar o
conjunto) foi recalculada para a nova disposição.

## Verificação

- Compilação completa: 0 erros.
- Boot real sob Xvfb + screenshot: os dois dados agora ficam visualmente
  espelhados dos dois lados do separador.
- Teste pontual via Robot (apagado depois de usar): confirmou por
  coordenada — `sortearMedidas` termina 16px antes do separador,
  `sortearRelacoes` começa 16px depois dele, mesmo espaçamento dos dois
  lados. 8 cliques (4 por botão): 0 exceções.

## Escopo

Só `Main.java`, só o método de posicionamento.
