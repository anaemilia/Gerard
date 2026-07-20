# Relatório de regressão — curadoria com categorias coloridas

## Alteração solicitada

Na aba Curadoria, a coluna `categoria_vergnaud` foi movida para antes do `enunciado`, mantendo a tabela principal reduzida aos campos de controle.

## Alterações implementadas

- A ordem da tabela principal passou a ser: `categoria_vergnaud`, `enunciado`, `validada`, `idioma`, `contexto`.
- O clique no texto da situação-problema permanece abrindo a tela de curadoria específica.
- Cada categoria de Vergnaud recebeu cor própria no texto da coluna `categoria_vergnaud`, para reduzir ruído visual e facilitar a localização pelo curador.
- A tela específica de curadoria permanece sem redundância dos metadados na tabela principal.
- O script de regressão foi atualizado para verificar a nova ordem das colunas e a coloração das categorias.

## Procedimento de regressão executado

Comando executado:

```bash
bash scripts/verificar_regressao_gerard.sh
```

Resultado:

- Compilação: `BUILD SUCCESSFUL`.
- JAR gerado: `dist/GerardNetBeans_D3_Leitura_Redes_Transicoes.jar`.
- Erros críticos: 0.
- Avisos: 1, já conhecido, referente à diferença de quantidade de situações-problema entre os idiomas no TSV empacotado.

## Funcionalidades verificadas

- Curadoria humana como fonte da verdade.
- Exibição somente de situações curadas.
- Mensagens de sistema sem interpretação matemática.
- Clique no enunciado abrindo a curadoria específica.
- Metadados detalhados apenas na tela específica.
- Internacionalização preservada.
- Sincronização entre diagrama de Vergnaud, eixo e diagrama de composição de coleções.
- Logs e visualizações D3 preservados.
