# Relatório: Relações reincluída no sorteio e nos ícones de adivinhação

Data: 2026-08-07

## Contexto

Em 2026-07-28 a usuária restringiu `CATEGORIAS_SORTEIO_LIVRE` (o pool de
"Nova situação-problema" e do quiz de adivinhação de categoria) só ao
grupo "Medidas", e por consequência os 3 ícones de atalho do grupo
"Relações" (Composição de Transformações, Transformação de Relação,
Composição de Relações) ficaram sempre desabilitados no quiz — nunca
seriam a resposta certa, então habilitá-los seria enganoso.

A usuária perguntou se já existe código funcional o suficiente para
reabilitá-los. Confirmado: sim — as 3 categorias já tinham dados curados
e renderizador próprio antes desta sessão, e a resolução numérica (a
parte que faltava para paridade real com Medidas) foi totalmente
delegada à arquitetura rica hoje (Fase B1 estendida em 2026-08-06, Fase
B2 completa em 2026-08-07). A usuária pediu para reincluir.

## O que mudou

Só `Main.java`, dois pontos:

1. `CATEGORIAS_SORTEIO_LIVRE`: voltou a incluir
   `COMPOSICAO_TRANSFORMACOES`, `TRANSFORMACAO_RELACAO`,
   `COMPOSICAO_RELACOES`, junto das 3 de Medidas (6 no total).
2. `atualizarHabilitacaoIconesAtalhoCategoria`: os 3 ícones de atalho de
   Relações passam a usar a mesma condição de habilitação das 3 de
   Medidas (`situacaoProblemaAtual != null && aguardandoAdivinhacaoCategoria`),
   no lugar do `false` fixo.

Os 2 tipos "Em construção" (`COMPOSICAO_TRANSFORMACAO_MEDIDAS`,
`TRANSFORMACAO_COMPOSTA_DOIS_PASSOS`) continuam de fora — nenhuma
mudança aqui os afeta, permanecem sem cobertura de piloto e sem caminho
de UI.

## Por que não precisou de nenhuma outra mudança

`clicarAtalhoCategoria` (o handler único dos 6 ícones) já era genérico —
despacha por `tipo` e compara contra `categoriaSorteioOculta` sem
nenhuma exclusão especial para Relações. `catalogoDefinicoesAditivas.obter(tipo)`
e a montagem da interpretação curada já eram exercitados sempre que a
categoria era selecionada pelo menu "Categoria" (que nunca ficou
restrito, só o sorteio/quiz) — clicar o ícone durante o quiz agora
alcança o mesmo caminho já testado, só que com `categoriaSorteioOculta`
podendo ser um dos 3 tipos de Relações.

## Verificação

- Projeto completo compilado (439 arquivos): **0 erros**.
- Revisão de código confirma que nenhum outro ponto desabilita esses 3
  botões (só o local alterado) e que o fluxo de validação da adivinhação
  não tem lógica especial por categoria.
- **Não verificado**: comportamento real da interface (sorteio de fato
  incluindo Relações, ícones clicáveis, quiz validando corretamente) —
  sem harness de GUI neste ambiente, mesmo padrão de risco já registrado
  para mudanças de UI nesta sessão. Recomendado testar manualmente:
  clicar "Nova situação-problema" algumas vezes até sortear uma categoria
  de Relações, e conferir que o ícone certo acerta o quiz.

## Escopo

Só `Main.java`. Nenhuma mudança em dados curados, renderizadores ou na
arquitetura rica — já estavam prontos antes desta etapa.
