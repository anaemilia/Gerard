# Relatório de alteração — correção da coleção Todo na composição de medidas

**Data:** 2026-07-15  
**Ciclo:** C66  
**Tipo de intervenção:** correção de consistência entre o diagrama de Vergnaud e o diagrama de composição de coleções.

## Problema observado
Na composição de medidas, os valores de **Parte 1** e **Parte 2** geravam quadradinhos nas regiões correspondentes do diagrama de Venn. Entretanto, mesmo quando o usuário posicionava/preenchia o valor de **Todo** no diagrama de Vergnaud, a coleção total permanecia vazia e exibia contagem zero.

## Causa
A região **Todo** estava configurada apenas como área visual, sem habilitação para receber e renderizar quadradinhos.

## Alteração implementada
- habilitação da região **Todo** como coleção manipulável;
- quando o usuário posiciona/preenche o valor de **Todo** no diagrama de Vergnaud, a mesma quantidade de quadradinhos passa a ser criada na coleção total;
- a sincronização inversa permanece: mover ou remover quadradinhos da coleção total atualiza o valor de **Todo** no diagrama de Vergnaud;
- o Venn continua iniciando vazio e só recebe quadradinhos após ação do usuário.

## Resultado esperado
Para **Parte 1 = 8**, **Parte 2 = 6** e **Todo = 14**, o diagrama de composição passa a mostrar:
- 8 quadradinhos em Parte 1;
- 6 quadradinhos em Parte 2;
- 14 quadradinhos em Todo;
- expressão 8 + 6 = 14.

## Verificação realizada
- compilação em Ant/NetBeans: **aprovada**.
