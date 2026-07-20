# Relatório de atualização — Curadoria reorganizada

## Alteração solicitada
A aba Curadoria foi simplificada para não exibir os metadados detalhados como colunas da tabela principal. O texto da situação-problema permanece visível na tabela e, ao receber clique do mouse, abre a tela de curadoria específica da situação.

## Arquivos alterados
- `src/gerard/campoaditivo/curadoria/TelaCuradoriaSituacoes.java`
- `scripts/verificar_regressao_gerard.py`

## Ajustes implementados
- A tabela principal da aba Curadoria agora exibe apenas: `enunciado`, `validada`, `idioma`, `categoria_vergnaud` e `contexto`.
- Foram retiradas da tabela principal as colunas: `id`, `fonte`, `subtipo`, `estado_inicial`, `transformacao`, `estado_final`, `quantidade_1`, `quantidade_2`, `resultado`, `termo_desconhecido`, `representacao_visual` e `observacoes`.
- O clique no texto/enunciado abre a janela modal de curadoria específica.
- A janela de curadoria específica mantém o enunciado no topo e organiza verticalmente os metadados removidos da tabela principal.
- A janela específica mantém barra de rolagem vertical quando necessário e não usa barra horizontal.
- Os metadados continuam sendo preservados no salvamento da curadoria.

## Regressão executada
Comando executado:

```bash
bash scripts/verificar_regressao_gerard.sh
```

Resultado:
- `BUILD SUCCESSFUL`
- 0 erros críticos
- 1 aviso conhecido: diferença na quantidade de situações-problema por idioma no TSV empacotado.

## Funcionalidades verificadas
- Compilação Ant e geração do JAR.
- Internacionalização.
- Curadoria humana como fonte da verdade.
- Exibição apenas de situações curadas.
- Mensagens de sistema sem interpretação matemática.
- Sincronização entre representações.
- Diagrama de composição de coleções.
- Logs e visualizações D3.
- Nova estrutura da aba Curadoria e tela específica por situação.
