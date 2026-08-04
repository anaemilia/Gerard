# PROMPT PARA O CLAUDE — RODADA 4 DO GÉRARD

Atue como engenheiro de software responsável pela quarta rodada de correções e validação do projeto Gérard.

Analise integralmente o projeto e os artefatos da rodada 3, especialmente:

- `RELATORIO_AUDITORIA_MULTIAGENTE_2026-07-31(2).md`;
- `agentes_execucao_20260731_171956.jsonl`;
- `agentes_execucao_legivel_20260731_171956.log`;
- `cardinalidade_episodios_20260731_171956.tsv`;
- `robot_gestos_20260731_171956.log`;
- `schema_agentes_execucao_gerard.json`;
- `TesteCardinalidadeAuditoria.java`;
- `modelo_diagnostico_despacho_evento_v4.json`.

Preserve integralmente as correções anteriores: distinção canônico/reativo, reavaliações sem mutação, idempotência do ZDP e MODELADOR, localização semântica, recálculo de coordenadas, captura do segundo erro de Jamile S9, agregação da quantificação e cardinalidade por `gesture_id`.

## OBJETIVOS

1. Identificar a causa raiz do disparo triplo da interrogação.
2. Manter debounce e idempotência como defesa, mesmo após corrigir a causa.
3. Fazer validação completa e reexecutável do JSON Schema 4.0.0.
4. Executar ou deixar reexecutável a regressão completa.
5. Produzir uma matriz real de integração das regras por agente e tipo de ação.

## DISPARO TRIPLO

Uma única soltura física da interrogação produziu três eventos canônicos. Instrumente toda a cadeia e registre:

- `physical_event_id`;
- identidade do objeto `MouseEvent`;
- `AWTEvent.getID()`;
- `when`;
- botão e quantidade de cliques;
- coordenadas;
- componente fonte;
- thread;
- identidade e classe do listener;
- quantidade de listeners registrados;
- ponto do código onde cada listener foi registrado;
- contador de registro;
- stack trace ou fingerprint;
- `gesture_id`, `action_id` e `evaluation_id`;
- origem da avaliação;
- se abriu ação canônica;
- se chamou MONITOR, ZDP e MODELADOR;
- se houve mutação de estado;
- tempo desde o primeiro release correlacionado.

Use `modelo_diagnostico_despacho_evento_v4.json` como referência estrutural. Não copie os dados fictícios.

## HIPÓTESES A TESTAR

Verifique explicitamente:

1. listener registrado mais de uma vez;
2. mais de um componente recebendo o mesmo release;
3. redispatch manual;
4. chamada indireta de `mouseReleased`;
5. criação de novo `MouseEvent` durante sincronização;
6. listeners diferentes executando a mesma lógica;
7. reentrada causada por atualização da interface;
8. chamadas repetidas de `AgentAuditService.iniciarAcao`;
9. repetição do Robot;
10. correlação incorreta do debounce.

Para cada hipótese, registre método, evidência, resultado e conclusão.

## CORREÇÃO

Depois de confirmar a causa:

- corrija no ponto arquitetural correto;
- não apenas aumente a janela de debounce;
- não descarte eventos silenciosamente;
- não remova avaliações reativas legítimas;
- preserve semântica, internacionalização e sincronização;
- não coloque código solto na `Main`.

Uma soltura física deve produzir somente uma ação canônica.

## TESTES AUTOMATIZADOS

Acrescente testes permanentes para:

1. um release físico da interrogação gerar uma única ação canônica;
2. listeners não serem registrados em duplicidade;
3. cada componente possuir a quantidade esperada de listeners;
4. o mesmo `physical_event_id` não criar mais de uma ação canônica;
5. avaliações técnicas correlacionadas gerarem no máximo uma mutação real;
6. ZDP manter idempotência;
7. MODELADOR manter idempotência;
8. debounce não combinar gestos físicos diferentes;
9. dois gestos próximos receberem IDs distintos;
10. quantificação continuar agregada;
11. Jamile S9 continuar com seis ações pedagógicas;
12. 14/14 cardinalidades permanecerem consistentes;
13. schema completo aceitar log válido;
14. schema completo rejeitar log inválido;
15. JSONL e log legível permanecerem sincronizados;
16. falha deliberada do logger ser registrada;
17. falha da auditoria não interromper a interação.

## JSON SCHEMA 4.0.0

Inclua:

- `physical_event_id`;
- `event_object_identity`;
- `listener_identity`;
- `listener_class`;
- `listener_registration_count`;
- `stack_fingerprint`;
- `dispatch_index`;
- `dispatches_for_same_physical_release`;
- `root_cause`;
- `root_cause_evidence`;
- `schema_validation`;
- `regression_results`;
- `rule_engine_trace`.

Use `additionalProperties=false` nos blocos estáveis e crie validação automatizada real de `$ref`, `oneOf`, `required`, tipos, enums, formatos e propriedades extras.

## BASE DE REGRAS

Não conecte indiscriminadamente as 432 regras. Gere primeiro uma matriz:

| Agente | Tipo de ação | Base consultada | Regras disponíveis | Regras ativadas | Regras usadas | Lacuna |

Registre no log se a base foi consultada, quais regras foram avaliadas, ativadas, usadas ou descartadas e por quê.

Somente conecte uma regra nova quando houver contexto semântico suficiente, prioridade definida, teste automatizado e preservação do comportamento anterior.

## REGRESSÃO

Execute, quando disponível:

1. compilação completa;
2. `TesteCardinalidadeAuditoria`;
3. novos testes da rodada 4;
4. `TesteMonkeyGuiadoPorCasosReais`;
5. `scripts/verificar_regressao_gerard.py`;
6. todos os `scripts/testar_*.sh`;
7. validação completa do schema;
8. comparação JSONL versus log legível;
9. teste deliberado de falha do logger;
10. restauração dos TSV.

Quando uma ferramenta não estiver disponível, não invente execução. Registre comando, motivo, risco residual e forneça script reexecutável.

## CRITÉRIOS DE ACEITAÇÃO

A rodada só estará concluída quando:

- a causa do disparo triplo estiver confirmada ou tecnicamente delimitada por evidência;
- uma soltura física produzir uma ação canônica;
- debounce e idempotência permanecerem;
- Jamile S9 continuar correto;
- cardinalidade permanecer consistente;
- schema 4.0.0 tiver validação completa;
- falha do logger tiver teste real;
- regressão estiver executada ou reexecutável;
- nenhuma funcionalidade anterior regredir.

## ENTREGÁVEIS

Devolver:

1. projeto atualizado em ZIP;
2. `agentes_execucao.jsonl`;
3. `agentes_execucao_legivel.log`;
4. `robot_gestos.log`;
5. `despacho_mouse_released.log`;
6. `schema_agentes_execucao_gerard.json`;
7. `cardinalidade_episodios.tsv`;
8. `matriz_integracao_regras.tsv`;
9. relatório técnico;
10. causa raiz e evidências;
11. arquivos criados e modificados;
12. resultados de compilação e testes;
13. resultados da regressão;
14. validação completa do schema;
15. estudo atualizado de Jamile S9;
16. teste de falha do logger;
17. limitações restantes.

Não invente causas, testes, regras ativadas ou resultados.