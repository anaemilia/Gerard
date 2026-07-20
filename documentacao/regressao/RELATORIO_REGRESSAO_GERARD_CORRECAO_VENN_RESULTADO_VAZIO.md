# Relatório de regressão — correção da contagem no resultado vazio do Venn

## Problema observado

No diagrama de composição de medidas, a coleção resultado estava vazia, mas o número exibido ao lado do círculo resultado aparecia como `10`. Isso gerava inconsistência visual, pois não havia quadradinhos dentro do círculo resultado.

## Correção realizada

A lógica foi separada em dois usos distintos:

1. O número exibido ao lado de cada círculo representa a quantidade real de quadradinhos atualmente dentro daquele círculo.
2. A expressão inferior continua apresentando a soma das duas parcelas, por exemplo: `7 + 3 = 10`.

Assim, quando o círculo resultado está vazio, ele mostra `0`, enquanto a expressão inferior mostra a soma calculada das parcelas.

## Arquivos alterados

- `src/Main.java`
- `scripts/verificar_regressao_gerard.py`

## Verificações executadas

Foi executado:

```bash
bash scripts/verificar_regressao_gerard.sh
```

Resultado:

- Compilação: `BUILD SUCCESSFUL`.
- Erros críticos: 0.
- Avisos: 1, referente à diferença já conhecida na quantidade de situações-problema entre os idiomas.
- Internacionalização: 547 chaves em português, inglês e francês.
- Foram verificados: eixo dos inteiros, botões de restauração, Venn, sincronização entre representações, extração contextual de numerais, classificação de situações críticas, logs e visualizações D3.

## Resultado esperado após a correção

Para uma composição `7 + 3`, com a coleção resultado ainda vazia:

- círculo da primeira parcela: `7`;
- círculo da segunda parcela: `3`;
- círculo resultado: `0`;
- expressão inferior: `7 + 3 = 10`.
