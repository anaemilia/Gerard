# Relatório de regressão — Curadoria detalhada por situação-problema

## Alteração implementada

Foi preservada a aba de Curadoria já existente e acrescentada uma tela específica para curadoria de cada situação-problema. Ao clicar no texto/enunciado de uma linha da tabela de curadoria, abre-se uma janela em frente com o enunciado no topo e os metadados em organização vertical.

## Campos disponíveis na tela detalhada

- categoria_vergnaud
- subcategoria
- representacao_visual
- quantidade_1
- quantidade_2
- resultado
- termo_desconhecido
- estado_inicial
- transformacao
- estado_final
- validada
- idioma

## Decisões de interface

- O enunciado fica no topo com quebra de linha automática.
- A tela detalhada não usa barra de rolagem horizontal.
- Os campos de metadados ficam em uma única coluna vertical de leitura/edição.
- O curador pode aplicar a edição apenas na tabela ou aplicar e salvar a curadoria no arquivo de metadados curados.

## Procedimento de regressão executado

Comando:

```bash
bash scripts/verificar_regressao_gerard.sh
```

Resultado: `BUILD SUCCESSFUL`.

Erros críticos: 0.

Aviso remanescente: diferença já conhecida na quantidade de situações-problema por idioma no TSV empacotado.
