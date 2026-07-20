# Relatório de alteração — refinamento do afastamento entre controles e contagens no diagrama complementar

**Data:** 2026-07-15  
**Ciclo:** C104  
**Tipo de intervenção:** refinamento visual e operacional.

## Síntese
Mesmo após o reposicionamento anterior, os controles `+` e `−` ainda ficaram visualmente muito próximos dos números de contagem. Nesta correção, os controles foram reposicionados para ficarem mais próximos da borda superior lateral da representação e mais afastados da faixa onde o numeral é exibido.

## Alterações implementadas
- redução do afastamento horizontal em relação à borda da representação, trazendo os controles para mais perto do cartão;
- aumento do afastamento vertical superior, elevando ligeiramente a coluna de controles;
- preservação do empilhamento `+` acima de `−`;
- manutenção da lógica de fallback para o lado esquerdo quando não houver espaço à direita.

## Resultado esperado
Os ícones `+` e `−` permanecem próximos da representação correspondente, porém com separação visual mais confortável em relação aos números de quantidade.

## Verificação realizada
- compilação Ant/NetBeans: **aprovada**.
