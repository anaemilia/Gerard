# Relatório de alteração — consistência semântica entre diagrama de Vergnaud e gráfico de barras na comparação

**Data:** 2026-07-15  
**Ciclo:** C51  
**Tipo de intervenção:** correção semântica e representacional com intervenção explícita do pesquisador.

## Síntese
Foi corrigida uma inconsistência semântica na categoria **comparação de medidas**. Alguns rótulos do diagrama de Vergnaud ainda utilizavam a nomenclatura antiga (**Referente** e **Relação**) e a interpretação dos índices internos do diagrama estava invertendo os papéis de **Referido** e **Referendo** na sincronização com o gráfico de barras.

## Alterações implementadas
- atualização dos rótulos do diagrama de Vergnaud para **Referido**, **Valor relativo** e **Referendo**;
- correção do mapeamento interno dos elementos do diagrama formal na ordem: `referido`, `valor relativo`, `referendo`;
- correção da verificação do termo desconhecido resolvido no diagrama para a mesma ordem;
- preservação dos rótulos já usados no gráfico de barras, agora consistentes com o diagrama de Vergnaud.

## Resultado esperado
Em uma comparação como `Paulo tem 6 bolas. José tem 8 bolas a mais que Paulo.`, o diagrama e o gráfico passam a concordar em que:
- **Referido** = Paulo = 6;
- **Valor relativo** = +8;
- **Referendo** = José = 14.

## Verificação realizada
- compilação em Ant/NetBeans: **aprovada**.
