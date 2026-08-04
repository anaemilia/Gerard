# PROMPT PARA O CLAUDE — CORRIGIR A AUDITORIA MULTIAGENTE DO GÉRARD

Atue como engenheiro de software responsável pela correção da auditoria dos agentes MONITOR, ZDP e MODELADOR do projeto Gérard.

Estou fornecendo:

1. o projeto Gérard atualizado;
2. `RELATORIO_AUDITORIA_MULTIAGENTE_2026-07-31.md`;
3. os logs `agentes_execucao_*.jsonl` e `agentes_execucao_legivel_*.log`;
4. o arquivo `modelo_log_multiagente_canonico.json`;
5. o arquivo `schema_log_multiagente_canonico.json`.

## PROBLEMA CONFIRMADO

O sistema registrou 227 eventos para 45 ações catalogadas porque `avaliarQuestionamentoPosicionamento` é chamado por vários pontos internos, inclusive sincronizações e reavaliações de consistência.

Essas chamadas são reais, mas não podem ser tratadas como novas ações do usuário.

Atualmente, um único gesto pode:

- aumentar várias vezes o contador de erros;
- elevar artificialmente o nível de ajuda do ZDP;
- inserir vários casos no MODELADOR;
- aparentar uma sequência de erros inexistente;
- contaminar futuras regras J48, PART e Apriori.

A correção deve separar formalmente:

1. gesto físico do usuário;
2. ação semântica canônica;
3. avaliação do MONITOR;
4. reavaliação interna de consistência.

## OBJETIVO

Manter todas as avaliações internas para fins de auditoria técnica, mas garantir que somente a avaliação canônica de cada gesto altere:

- perfil pedagógico;
- contadores de erros e acertos;
- sequência de erros;
- nível de ajuda do ZDP;
- base de casos;
- dados usados para aprendizagem e mineração.

## REGRA CENTRAL

Cada gesto do usuário deve receber um `gesture_id` e um `action_id` estáveis.

Todas as reavaliações internas originadas pelo mesmo gesto devem reutilizar o mesmo `gesture_id` e `action_id`, mas possuir `evaluation_id` próprio.

Exemplo:

```text
gesture_id=GESTO-00017
action_id=ACAO-00017

evaluation_id=AVAL-00017-001  canonical=true
evaluation_id=AVAL-00017-002  canonical=false
evaluation_id=AVAL-00017-003  canonical=false
```

## CLASSIFICAÇÃO DOS EVENTOS

Todo evento deve registrar:

```json
{
  "origin": "soltura_usuario",
  "canonical": true,
  "reactive_evaluation": false,
  "counts_for_user_profile": true,
  "counts_for_error_sequence": true,
  "counts_for_case_base": true,
  "counts_for_rule_learning": true
}
```

Reavaliações internas devem registrar:

```json
{
  "origin": "sincronizacao_representacoes",
  "canonical": false,
  "reactive_evaluation": true,
  "counts_for_user_profile": false,
  "counts_for_error_sequence": false,
  "counts_for_case_base": false,
  "counts_for_rule_learning": false
}
```

## ORIGENS CONTROLADAS

Use, no mínimo:

- `soltura_usuario`;
- `selecao_categoria`;
- `selecao_sinal`;
- `quantificacao`;
- `solicitacao_ajuda`;
- `sincronizacao_representacoes`;
- `reavaliacao_consistencia`;
- `outro`.

## ZDP

O ZDP pode receber todas as avaliações para auditoria, mas só deve alterar o estado pedagógico quando:

```text
canonical=true
AND counts_for_error_sequence=true
```

A ajuda deve evoluir entre gestos reais:

```text
gesto 1 incorreto → nível 1
gesto 2 incorreto → nível 2
gesto 3 incorreto → nível 3
```

Nunca dentro do mesmo gesto por callbacks repetidos.

## MODELADOR

O MODELADOR deve inserir no máximo um caso por ação canônica.

Use chave de idempotência:

```text
session_id + episode_id + gesture_id
```

Antes de inserir, verificar se essa chave já foi processada.

Reavaliações internas podem ser registradas no log técnico, mas não podem:

- criar novo caso;
- atualizar perfil;
- aumentar contadores;
- alimentar mineração;
- incorporar padrão.

O MODELADOR deve receber explicitamente:

- `evaluation`;
- `error_type`;
- `expected_role`;
- `received_role`;
- `gesture_id`;
- `action_id`;
- `canonical`;
- `idempotency_key`.

Esses campos não podem chegar como `null` quando existirem no MONITOR.

## MONITOR

O MONITOR pode continuar stateless.

Toda avaliação deve registrar:

- origem;
- se é canônica;
- papel esperado;
- papel recebido;
- avaliação C/E;
- tipo de erro;
- justificativa;
- regras ativadas;
- `gesture_id`;
- `action_id`;
- `evaluation_id`.

Quando a categoria estiver disponível como enum semântico, use-a para consultar as regras de domínio.

Não use apenas texto localizado para a lógica.

## PERFIL

Separar:

```json
{
  "model_profile_before": null,
  "audit_session_counters_before": {}
}
```

Não chamar agregados técnicos de “perfil do usuário” quando não forem estado persistido pelo MODELADOR.

Use nomes explícitos:

- `user_profile_before`;
- `user_profile_after`;
- `audit_session_counters_before`;
- `audit_session_counters_after`.

## DIVERGÊNCIA

Quando não houver resultado esperado:

```json
{
  "comparison_status": "not_evaluable",
  "divergence": null
}
```

Não usar `divergence=false` quando a comparação não puder ser realizada.

## LOGS

Gerar:

1. `agentes_execucao.jsonl`;
2. `agentes_execucao_legivel.log`;
3. `schema_agentes_execucao_gerard.json`.

Os dois logs devem ser produzidos a partir do mesmo objeto de auditoria.

## FORMATO OBRIGATÓRIO

Usar como referência estrutural:

- `modelo_log_multiagente_canonico.json`;
- `schema_log_multiagente_canonico.json`.

Não copiar os valores de exemplo como se fossem dados reais.

Campos indisponíveis devem ser `null`, acompanhados de motivo.

## REGRAS DE IMPLEMENTAÇÃO

- Respeitar a arquitetura atual.
- Não inserir lógica solta em `Main.java`.
- Não duplicar a lógica dos agentes dentro do logger.
- O logger não pode recalcular decisões.
- A instrumentação não pode alterar o comportamento pedagógico.
- Preservar internacionalização.
- Não remover chamadas internas de consistência.
- Apenas classificar chamadas como canônicas ou reativas.
- Não ocultar reavaliações internas: mantê-las no log técnico.
- Não permitir inserção duplicada de casos.
- Usar IDs semânticos e estáveis.
- Escrever arquivos em UTF-8.
- Em falha do logger, a atividade principal deve continuar.
- Registrar falhas de auditoria em arquivo separado.

## VALIDAÇÃO OBRIGATÓRIA

Após implementar, executar:

1. compilação completa;
2. todos os testes existentes;
3. `scripts/verificar_regressao_gerard.py`;
4. todos os `scripts/testar_*.sh`;
5. monkey guiado por casos reais;
6. validação JSON Schema;
7. comparação JSONL versus log legível;
8. restauração de `perfis_usuario.tsv`;
9. restauração de `diagnosticos_tarefa.tsv`.

## TESTE DE CARDINALIDADE

Para cada episódio, verificar:

```text
ações canônicas observadas
=
avaliações canônicas do MONITOR
=
decisões pedagógicas contabilizadas pelo ZDP
=
atualizações de perfil do MODELADOR
=
casos pedagógicos inseridos
```

As avaliações reativas podem ser mais numerosas, mas devem ter:

```text
canonical=false
counts_for_user_profile=false
counts_for_error_sequence=false
counts_for_case_base=false
counts_for_rule_learning=false
```

## CASO OBRIGATÓRIO: JAMILE S9

O episódio deve registrar exatamente estas ações canônicas:

1. `32 → transformação` — erro;
2. `32 → estado final` — erro;
3. `32 → estado inicial` — correto;
4. `22 → transformação` — correto;
5. `? → estado final` — correto;
6. `54 → estado final` — correto.

Reavaliações internas podem existir, mas devem compartilhar o mesmo `gesture_id` da ação que as originou e não alterar contadores.

O contador canônico deve evoluir:

```text
0 → 1 → 2 → 0
```

Não pode evoluir três vezes dentro do primeiro gesto.

## CRITÉRIOS DE ACEITAÇÃO

A correção só está concluída quando:

- um gesto possui um único `gesture_id`;
- uma ação canônica possui um único `action_id`;
- reavaliações compartilham o mesmo gesto;
- apenas eventos canônicos atualizam o perfil;
- apenas eventos canônicos alteram a ZDP;
- apenas eventos canônicos inserem caso;
- não há caso duplicado;
- o episódio Jamile S9 é reconstruído fielmente;
- os logs continuam contendo as avaliações reativas;
- o JSON Schema rejeita registros incompletos;
- não há regressão funcional.

## ENTREGÁVEIS

Devolver:

- projeto completo atualizado em ZIP;
- `agentes_execucao.jsonl`;
- `agentes_execucao_legivel.log`;
- JSON Schema atualizado;
- relatório técnico;
- lista de arquivos alterados;
- resultado da compilação;
- resultado dos testes;
- resultado da regressão;
- tabela de cardinalidade por episódio;
- análise específica do episódio Jamile S9;
- explicação de como a idempotência foi implementada;
- limitações restantes.

Não declarar testes executados quando não tiverem sido executados.
Não inventar diagnósticos, estratégias, regras, métricas ou estados.