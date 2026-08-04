# Aplicação de J48, PART e Apriori aos protocolos reais

## Preparação

- Arquivo analisado: `protocolos_reais_replay_completo.tsv`.
- Registros totais: 2781.
- Ações do sujeito com avaliação C/E: 743 (517 corretas e 226 erradas).
- Variável-alvo para J48/PART: `avaliacao_ce`.
- Validação: 5 partições estratificadas por protocolo, evitando que ações do mesmo protocolo apareçam simultaneamente em treino e teste.
- Variáveis textuais que revelavam diretamente o erro foram reduzidas a famílias neutras de ação e artefato.

> Observação: a execução local usa uma árvore por entropia equivalente à lógica geral do J48/C4.5 e uma implementação PART-like de cobertura sequencial. Os arquivos ARFF foram gerados para execução exata no WEKA 3.8.7.

## J48 equivalente

- Parâmetros escolhidos: `{"max_depth": 6, "min_samples_leaf": 15, "class_weight": "balanced"}`.
- Acurácia: 0.575.
- Acurácia balanceada: 0.597.
- F1 macro: 0.561.
- F1 da classe erro (E): 0.484.
- Matriz de confusão `[C,E]`: `[[279, 238], [78, 148]]`.

A árvore completa está em `j48_equivalente_arvore.txt`.

## PART equivalente

- Acurácia: 0.661.
- Acurácia balanceada: 0.567.
- F1 macro: 0.569.
- F1 da classe erro (E): 0.370.
- Matriz de confusão `[C,E]`: `[[417, 100], [152, 74]]`.
- Número de regras no modelo completo: 10; classe padrão: C.

As regras completas estão em `part_equivalente_regras.tsv` e `.txt`.

## Apriori

Foram produzidas duas análises:

1. regras por ação, com suporte mínimo de 5%, confiança mínima de 70% e lift mínimo de 1,05;
2. regras por protocolo, com suporte mínimo de 8%, confiança mínima de 70% e lift mínimo de 1,05.

- Regras por ação encontradas: 194.
- Regras por protocolo exibidas após filtro de consequentes analiticamente relevantes: 150.

## Cuidados de interpretação

- As classes são desbalanceadas: há mais acertos do que erros.
- As avaliações C/E foram produzidas na codificação dos quadros; os modelos aprendem padrões dessa codificação.
- Regras de associação indicam coocorrência, não causalidade.
- Ações de um mesmo participante e problema são temporalmente dependentes; por isso a validação foi agrupada por protocolo.
