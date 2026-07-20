# Relatório de atualização documental - C95

**Data:** 2026-07-16  
**Escopo do corpus:** até a versão C94.

## Atualização realizada
Os PDFs do artigo de co-design e da análise de complexidade foram atualizados para incorporar o episódio C93--C94. O texto registra que a primeira implementação da inicialização sem categoria ocultou indevidamente os painéis, porque a condição foi aplicada à estrutura da interface em vez de somente aos conteúdos educativos.

A decisão consolidada em C94 estabelece o invariante de **estrutura visual estável**: o painel do enunciado, o painel de Vergnaud e o painel complementar permanecem visíveis e vazios; apenas texto, diagramas, títulos e controles educativos são carregados após a escolha da categoria.

## Tratamento metodológico
- C92 permanece identificado como atualização documental, sem novo ciclo de decisão.
- C93 é registrado como implementação intermediária rejeitada, sem ingresso no conjunto de versões consistentes.
- C94 é incorporado como o 92º ciclo de decisão consistente.
- O corpus passa a ter 31 ciclos estruturais, 23 de consistência teórica/metodológica e 38 de refinamento visual/operacional.
- A regressão visual separada da verificação funcional passa a ser requisito explícito.

## Arquivos atualizados
- `ARTIGO_CODESIGN_HUMANO_IA_GERARD.pdf`;
- `ANALISE_COMPLEXIDADE_GERARD.pdf`;
- fontes LaTeX e arquivos CSV correspondentes;
- cópias versionadas C94.
