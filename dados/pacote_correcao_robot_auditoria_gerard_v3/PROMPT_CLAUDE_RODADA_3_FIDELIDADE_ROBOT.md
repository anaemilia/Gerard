ATUE COMO ENGENHEIRO DE SOFTWARE RESPONSÁVEL PELA TERCEIRA RODADA DE CORREÇÕES DO PROJETO GÉRARD.

Analise o projeto completo e estes arquivos:
- RELATORIO_AUDITORIA_MULTIAGENTE_2026-07-31(1).md;
- agentes_execucao_20260731_145939.jsonl;
- agentes_execucao_legivel_20260731_145939.log;
- cardinalidade_episodios_20260731_145939.tsv;
- monkey_casos_reais_20260731_145939.log;
- schema_agentes_execucao_gerard.json;
- modelo_log_robot_auditoria_multiagente_v3.json.

A rodada anterior corrigiu a separação entre ações canônicas e avaliações reativas. Preserve integralmente essa correção.

OBJETIVO

Resolver a perda do segundo gesto incorreto do episódio Jamile S9, instrumentar o replay por Robot e criar testes automatizados permanentes. O sistema deve distinguir:

1. gesto físico;
2. ação pedagógica canônica;
3. subevento técnico;
4. avaliação do MONITOR;
5. decisão do ZDP;
6. atualização do MODELADOR.

PROBLEMA

O protocolo de Jamile S9 prevê:

1. 32 → transformação — E;
2. 32 → estado final — E;
3. 32 → estado inicial — C;
4. 22 → transformação — C;
5. ? → estado final — C;
6. 54 → estado final — C.

O segundo erro foi executado pelo harness, mas não apareceu na auditoria. Não trate a animação como causa confirmada. Instrumente o fluxo para descobrir exatamente onde o gesto desaparece.

INSTRUMENTAÇÃO DO ROBOT

Para cada gesto, registre:

- gesture_id;
- action_id;
- evaluation_id;
- passo do protocolo;
- coordenadas atuais de origem e destino;
- timestamp de mousePressed;
- componente encontrado;
- item selecionado;
- papel e valor do item;
- quantidade de mouseDragged;
- último ponto do arraste;
- alvo detectado;
- timestamp de mouseReleased;
- item selecionado na soltura;
- destino detectado;
- avaliação disparada ou não;
- motivo quando não disparada;
- número de tentativas;
- se as coordenadas foram recalculadas antes do gesto.

Use estados controlados:

started, pickup_failed, dragging, drop_failed,
evaluation_not_dispatched, completed, cancelled, exception.

Nenhum gesto pode desaparecer silenciosamente. Mesmo um pickup fracassado deve gerar registro técnico.

RECOMPUTAÇÃO DAS COORDENADAS

Antes de cada gesto:

1. localizar novamente o componente por identificador semântico;
2. obter seus limites atuais;
3. recalcular o centro;
4. validar visibilidade;
5. validar que o ponto pertence ao componente;
6. só então executar mousePressed.

Não reutilize coordenadas obtidas antes de feedback, tremor ou animação.

Em falha:
- registrar;
- tentar novamente de forma limitada;
- não avançar como se o passo tivesse sido executado.

JAMILE S9

A correção só será aceita quando o segundo erro:

- possuir gesture_id próprio;
- possuir action_id próprio;
- chegar ao MONITOR;
- tiver target_role=estadoFinal;
- receber avaliação E;
- elevar erros consecutivos de 1 para 2 apenas uma vez;
- produzir uma única decisão real do ZDP;
- inserir um único caso no MODELADOR.

A progressão deve ser:

0 → 1 → 2 → 0 → 0 → 0 → 0.

QUANTIFICAÇÃO

A posição da interrogação e a validação do valor digitado devem permanecer auditáveis, mas formar uma única ação pedagógica de quantificação, com subeventos técnicos compartilhando o mesmo action_id.

Exemplo:

{
  "action_type": "quantificacao",
  "subevents": [
    {"type": "verificar_posicao_interrogacao", "counts_as_user_action": false},
    {"type": "validar_valor_digitado", "counts_as_user_action": true}
  ]
}

Assim, Jamile S9 deve possuir seis ações pedagógicas canônicas, não sete.

CARDINALIDADE

Atualize cardinalidade_episodios.tsv com:

- gestos físicos;
- ações pedagógicas canônicas;
- subeventos técnicos;
- avaliações canônicas;
- avaliações reativas;
- falhas de pickup;
- falhas de drop;
- avaliações não disparadas;
- decisões reais do ZDP;
- atualizações reais do MODELADOR;
- casos inseridos;
- duplicados bloqueados;
- divergências.

Deve valer:

ações pedagógicas canônicas
=
decisões reais do ZDP
=
atualizações reais do MODELADOR
=
casos inseridos.

TESTES PERMANENTES

Implemente testes reexecutáveis para:

1. pickup válido gera evento;
2. pickup inválido gera falha auditável;
3. coordenadas são recalculadas antes de cada gesto;
4. feedback visual não invalida o gesto seguinte;
5. o segundo erro de Jamile S9 chega ao MONITOR;
6. esse erro altera a ZDP uma única vez;
7. esse erro insere um único caso;
8. Jamile S9 gera seis ações pedagógicas;
9. subeventos da quantificação compartilham action_id;
10. subeventos técnicos não contam como nova ação;
11. avaliações reativas não alteram estado;
12. idempotência bloqueia repetição;
13. gesture_id, action_id e evaluation_id são únicos;
14. divergence=null quando não há expectativa;
15. JSONL e log legível possuem os mesmos event_id;
16. schema rejeita evento incompleto;
17. falhas do Robot nunca são silenciosas.

JSON SCHEMA 3.0.0

Inclua no schema:

- robot_trace;
- gesture_status;
- failure_reason;
- protocol_step_executed;
- gesture_reached_audit;
- gesture_reached_monitor;
- pickup_retry_count;
- coordinates_recomputed_before_gesture;
- subevents;
- counts_as_user_action;
- diagnostico_fidelidade_robot.

Use additionalProperties=false nos blocos estáveis e torne obrigatórios os campos críticos.

ARQUITETURA

Crie ou ajuste componentes equivalentes a:

- RobotGestureTrace;
- RobotGestureAttempt;
- RobotGestureStatus;
- ProtocolStepExecutionResult;
- SemanticComponentLocator;
- GestureCoordinateResolver;
- CanonicalActionAggregator;
- ActionSubevent;
- EpisodeCardinalityReport.

Não coloque a solução na Main. Não use coordenadas fixas como identidade principal. Preserve papéis semânticos, internacionalização, sincronização entre representações e regras já consolidadas.

VALIDAÇÃO

Execute, quando disponíveis:

- compilação completa;
- testes novos;
- testes existentes;
- scripts/verificar_regressao_gerard.py;
- todos os scripts/testar_*.sh;
- TesteMonkeyGuiadoPorCasosReais;
- validação completa do JSONL contra o schema;
- comparação JSONL versus log legível;
- restauração dos TSV reais;
- inspeção detalhada de Jamile S9;
- teste deliberado de falha de pickup;
- teste deliberado de falha do logger.

Não declare como executado aquilo que não foi executado.

CRITÉRIOS DE ACEITAÇÃO

- segundo erro de Jamile S9 capturado;
- seis ações pedagógicas canônicas;
- quantificação agregada em uma ação;
- nenhuma falha do Robot silenciosa;
- coordenadas recalculadas;
- reavaliações continuam sem alterar estado;
- testes permanentes criados;
- schema 3.0.0 válido;
- nenhuma regressão funcional.

ENTREGÁVEIS

Devolver:

1. projeto atualizado em ZIP;
2. agentes_execucao.jsonl;
3. agentes_execucao_legivel.log;
4. schema_agentes_execucao_gerard.json;
5. cardinalidade_episodios.tsv;
6. falhas_auditoria.log;
7. robot_gestos.log;
8. relatório técnico;
9. arquivos criados e modificados;
10. resultado da compilação e testes;
11. análise detalhada de Jamile S9;
12. evidência da captura do segundo erro;
13. evidência da recomputação de coordenadas;
14. evidência da agregação da quantificação;
15. limitações restantes.

Não invente eventos, testes ou resultados.