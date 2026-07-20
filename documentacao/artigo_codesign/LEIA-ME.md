# Artigo sobre co-design humano-IA no Gérard

## Atualização incremental

1. Acrescente uma linha a `dados/versoes_consistentes.csv` somente quando houver uma versão consistente.
2. Execute `make`.
3. O script atualiza `secoes/linha_tempo_gerada.tex` e recompila `main.pdf`.

## Critério mínimo de versão consistente

- artefato gerado;
- código disponível;
- compilação registrada;
- inspeção funcional ou visual;
- ausência de regressão conhecida no momento da aceitação.

O texto analítico deve ser revisado quando os novos dados alterarem categorias, padrões ou conclusões.

## Regressão documental cruzada

O arquivo `dados/decisoes_metodologicas.csv` registra decisões metodológicas ou de tratamento de dados que devem aparecer no resumo, nos resultados e na discussão.

Antes da compilação, execute:

```bash
make validar-regressao
```

A tarefa `make` e o alvo Ant `versao-consistente` executam essa validação automaticamente. A compilação falha quando uma decisão ativa não está retroalimentada nas três regiões do artigo.

## Atualização C33

O corpus inclui a criação da aba de construção da situação-problema e a intervenção do pesquisador que estabeleceu distância semântica controlada entre os blocos corretos e os não compatíveis.
