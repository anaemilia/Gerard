# Relatório de regressão — Construir situação-problema e tooltip da categoria

Data: 14 de julho de 2026

## Escopo

Verificar a alteração de nomenclatura e a transferência da definição da categoria para o `onmouseover` do nome da categoria, sem regressão nas demais representações.

## Verificações específicas

- aba em português denominada **Construir situação-problema**;
- título interno denominado **Construa a situação-problema**;
- instrução orienta selecionar e organizar blocos para construir a situação;
- definição da categoria não é desenhada permanentemente na atividade;
- área interativa coincide com o nome renderizado da categoria;
- tooltip aparece sobre o nome e não aparece fora dele;
- tooltip utiliza a definição localizada da categoria;
- redimensionamento recalcula a área interativa;
- renderizador padrão continua exibindo a descrição nas telas que não solicitam sua ocultação;
- botão **Novo diagrama** continua alternando situação e categoria;
- português, inglês e francês permanecem disponíveis;
- registros da nova atividade usam a nomenclatura de construção.

## Testes executados

O script `scripts/testar_montagem_situacao.sh` foi ampliado com `TesteTooltipCategoriaConstrucao.java` e executa:

- compilação completa com Ant;
- 99 verificações do gerador de blocos e do catálogo;
- teste da nomenclatura e do tooltip em ambiente headless;
- teste da integração da aba e do botão **Novo diagrama** em ambiente gráfico virtual.

Resultado: aprovado.
