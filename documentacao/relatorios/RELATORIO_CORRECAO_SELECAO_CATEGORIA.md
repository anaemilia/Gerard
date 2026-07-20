# Correção do fluxo de seleção de categoria

A seleção realizada no menu **Tipo** representa a escolha da categoria de situações-problema que será treinada. Ela não é apenas uma troca visual de representação.

Ao selecionar uma categoria, o Gérard agora:

1. define a categoria escolhida;
2. busca uma situação validada na curadoria para essa categoria e para o idioma atual;
3. substitui o enunciado e os metadados da situação anterior;
4. carrega a definição de representação correspondente;
5. limpa integralmente a modelagem da atividade anterior;
6. inicializa os diagramas vazios, sem inserir números, sinais ou interrogação automaticamente.

Continuam preservando a modelagem: troca de idioma, troca de estilo de interação, abertura/fechamento da curadoria e atualização da camada textual.

Continuam restaurando a modelagem: seleção de categoria, botão Sortear e restauração explícita da atividade.
