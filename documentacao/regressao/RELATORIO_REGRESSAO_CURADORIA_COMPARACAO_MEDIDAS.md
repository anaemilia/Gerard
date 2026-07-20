# Relatório de regressão - curadoria de comparação de medidas

## Alteração realizada

A curadoria humana foi ampliada para eliminar ambiguidade nos problemas de comparação de medidas. Foram adicionados os campos `referido`, `referendo` e `valor_relativo` à tela específica de curadoria, ao modelo de dados e ao arquivo TSV de metadados curados.

O campo `termo_desconhecido` deixou de ser editado como texto livre na tela específica e passou a ser um combo box com os papéis possíveis na legenda de Vergnaud: `transformação`, `estado_inicial`, `estado_final`, `referido`, `referendo`, `valor_relativo`, `parte_1`, `parte_2` e `todo`.

## Arquivos modificados

- `src/gerard/campoaditivo/modelo/SituacaoProblemaAditiva.java`
- `src/gerard/campoaditivo/servico/RepositorioSituacoesAditivas.java`
- `src/gerard/campoaditivo/curadoria/TelaCuradoriaSituacoes.java`
- `src/gerard/campoaditivo/curadoria/ConstrutorResultadoCurado.java`
- `scripts/verificar_regressao_gerard.py`
- `CHECKLIST_REGRESSAO_GERARD.md`

## Verificações executadas

Foi executado o procedimento de regressão:

```bash
bash scripts/verificar_regressao_gerard.sh
```

Resultado: `BUILD SUCCESSFUL`, com 0 erros críticos.

## Observação

Permanece apenas o aviso já conhecido sobre diferença na quantidade de situações-problema por idioma no TSV empacotado. Esse aviso não bloqueia a compilação nem a execução.
