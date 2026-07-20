# Relatório de alteração — representações no relato de bug

**Data:** 2026-07-15  
**Ciclo:** C75  
**Tipo de intervenção:** refinamento operacional e de observabilidade.

## Alteração
O relato de bug passa a registrar automaticamente as representações exibidas no momento em que o usuário abre o formulário.

## Representações reconhecidas
- Diagrama de Vergnaud;
- eixo dos números inteiros, quando estiver visível;
- diagrama de composição de coleções;
- gráfico de comparação de medidas;
- diagrama de Venn;
- representação adicional declarada no campo `representacao_visual` da curadoria.

## Persistência e e-mail
A lista é incluída:
- no corpo do e-mail preparado para `ana.queiroz@univasf.edu.br`;
- na cópia local `relatos_bug.tsv`.

O arquivo local das versões anteriores é migrado automaticamente para a nova coluna `representacoes`, sem apagar relatos existentes.
