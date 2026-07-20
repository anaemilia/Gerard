# Agente Modelador

⚠️ Proposta teórica — ver aviso de status em `../SKILL.md`.

## Papel

Mantém o modelo do usuário atualizado.

## Arquitetura: Agente Reativo Simples

Não precisa tratar a mesma imprevisibilidade do Agente ZDP — só recebe, armazena e periodicamente modifica o modelo do usuário.

## Percepções

- **Estratégia Pedagógica** (recebida do Agente ZDP), derivada de percepção + modelo do usuário + conteúdo pedagógico disponível.
- **Identificação do usuário** — armazenada junto com o novo caso.

## Ações

1. Armazena o novo caso recebido na base de dados "Modelo do Usuário" — ver `gerard-modelo-usuario` para o esquema completo das 5 dimensões armazenadas (nível de tarefas, partes do conhecimento e fases, perfil do aluno, perfil da aprendizagem, diagnóstico da tarefa). Este arquivo não repete esse esquema — foca no que o Modelador *faz* com os dados, não em como eles são estruturados.
2. Periodicamente, roda os algoritmos de aprendizagem de máquina para inferir regras a partir dos novos casos:
   - **J48.PART** — indução de regras baseada em árvore de decisão.
   - **APRIORI** — mineração de regras de associação.
   - Combinados via AND.
   - Entrada principal para essas regras: a dimensão "Diagnóstico da tarefa" (Tarefa, Suporte, Internalização, Probabilidade de saber o conteúdo) — é dela que vêm as ações fundamentais geradas por cada usuário, base do aprendizado.
3. Gera o "Modelo do Usuário modificado".

Objetivo: manter o modelo do usuário atualizado.

## Por que J48.PART + APRIORI, e não algoritmos mais recentes

J48 (C4.5), PART e Apriori são de fins dos anos 90/1994 — mais antigos que alternativas como Random Forest, Gradient Boosting ou FP-Growth. **Não trocar por essas alternativas sem confirmação explícita do usuário.** O motivo não é desempenho bruto, é **explicabilidade**:

- J48/PART geram regras "se-então" legíveis e rastreáveis até os casos originais — cada regra carrega sua própria justificativa.
- Apriori entrega métricas explícitas por regra (suporte, confiança, lift), mostrando exatamente a força da relação entre elementos.
- Modelos como Random Forest/Gradient Boosting são caixas-pretas: qualquer explicação (ex. via SHAP) é uma reconstrução aproximada do que o modelo fez, não a lógica real de decisão.

Para o pesquisador, o que importa não é só a acurácia estatística das regras, mas a **significância pedagógica** de cada uma — entender por que ela foi gerada e sua relação com outros elementos associados. Regra explícita > reconstrução aproximada, mesmo que estatisticamente "melhor".
