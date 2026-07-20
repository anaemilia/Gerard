# Relatório de alteração — ajuda contextual “E agora?”

**Data:** 2026-07-16  
**Ciclo:** C91  
**Tipo de intervenção:** scaffolding contextual e orientação do próximo passo.

## Síntese
Foram acrescentados três botões discretos com interrogação, posicionados nas áreas do texto, do diagrama de Vergnaud e da representação complementar. Os botões ficam no cabeçalho visual dos cartões, fora das formas constitutivas dos diagramas formais.

## Interação
O mouseover ou o clique sobre a interrogação abre um painel contextual com três intenções:

1. Tenho uma dúvida;
2. Quero continuar;
3. Qual é o próximo passo?

Ao selecionar uma intenção, o próprio painel apresenta uma orientação específica para a área escolhida. O painel permanece aberto para permitir leitura e nova escolha, sendo fechado ao clicar fora dele.

## Abrangência
- área do enunciado textual;
- diagrama de Vergnaud;
- diagrama complementar atualmente exibido: Venn, composição de coleções ou gráfico de comparação;
- idiomas português, inglês e francês;
- todas as categorias aditivas, respeitando a existência ou não de representação complementar em cada categoria.

## Preservação do modelo formal
As interrogações foram inseridas apenas nos cabeçalhos dos cartões da interface. Nenhum símbolo foi acrescentado às formas, conectores ou papéis semânticos dos diagramas documentados.

## Verificações
- compilação Ant/NetBeans;
- teste das 27 combinações entre 3 áreas, 3 intenções e 3 idiomas;
- verificação estrutural da existência dos três botões e da abertura por mouseover/clique;
- regressão geral do projeto;
- inspeção visual em ambiente gráfico virtual das três interrogações, da abertura do painel por mouseover e da atualização da orientação após a escolha de uma opção.
