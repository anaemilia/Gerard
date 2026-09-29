# Spec — Localidade do conhecimento sem deslocamento de concentração

**Status:** normativa, promovida às regras de entrada dos agentes em 2026-09-28  
**Escopo:** código, skills e verificadores arquiteturais do Gérard  
**Motivação observada:** uma extração pode reduzir `Main` e ainda recriar a
mesma concentração em uma classe com outro nome.

## 1. Problema

Componentizar não é deslocar um bloco de código para outra classe. Uma
extração é inválida quando o novo elemento passa a interpretar conhecimentos
de localidades distintas — por exemplo, avaliação semântica, escolha
pedagógica, materialização visual e persistência — sem possuir autoridade
sobre eles. Esse erro preserva o acoplamento original e apenas muda seu nome.

O problema não é o uso das palavras `Controller`, `Service`, `Coordinator` ou
`Manager`. Nomes não concedem nem retiram autoridade. A unidade de análise é o
conhecimento que a classe interpreta, as decisões que toma e as dependências
concretas que reúne.

## 2. Objetivos

1. Impedir que extrações apenas transfiram concentração de responsabilidades.
2. Tornar explícito quem possui cada decisão antes da implementação.
3. Preservar a fonte única da verdade semântica e a propriedade factual dos
   registros.
4. Permitir coordenação legítima de escopo fechado sem criar autoridades
   centrais artificiais.
5. Converter as fronteiras arquiteturais em critérios verificáveis por testes
   e análise estática.

## 3. Não objetivos

- Proibir classes que coordenam operações. Relações estruturais e casos de uso
  podem coordenar dentro da autoridade que possuem.
- Definir uma taxonomia nova de domínio, scaffolding ou análise cognitiva.
- Prescrever um framework, padrão de injeção de dependência ou tecnologia de
  interface.
- Declarar que uma classe pequena está correta ou que uma classe grande está
  errada apenas por sua contagem de linhas.
- Autorizar automaticamente novas refatorações. Cada recorte continua sujeito
  à autorização explícita e à validação incremental.

## 4. Fontes normativas

Esta spec aplica, sem redefinir:

- `gerard-autorizacao-sem-invencao`;
- `gerard-domain-model-first`;
- `gerard-knowledge-locality-principle`;
- `gerard-knowledge-oriented-domain-objects`;
- `gerard-handlers-de-interacao`;
- `gerard-scaffolding-interacao`;
- `gerard-log-gestos-interacao`;
- `gerard-log-acao-instrumental`;
- `gerard-semantic-event-logging`;
- `gerard-consistencia-estado`, quando houver mais de uma representação.

Em caso de divergência, prevalecem o modelo semântico, a decisão explícita
mais recente da pesquisadora e a skill proprietária do conhecimento.

## 5. Invariante central

> Nenhum elemento arquitetural pode interpretar conhecimento cuja autoridade
> pertença a outra localidade. Transportar, ordenar chamadas ou materializar
> uma decisão não transfere a propriedade dessa decisão.

Consequências:

- redução de linhas não prova componentização;
- uma fachada não pode reinterpretar o que encaminha;
- um serviço de aplicação não pode calcular C/E, diagnóstico ou regra de
  ajuda;
- um logger não pode constituir a ação que persiste;
- uma interface não pode inferir significado pela geometria;
- um handler não pode converter gesto sem comando em ação instrumental;
- um objeto individual não pode possuir uma relação multiobjeto.

## 6. Localidades e autoridades

| Localidade | Pode decidir | Não pode decidir |
|---|---|---|
| Objeto semântico | Estado, restrição e fatos locais que possui | Relações entre irmãos, pixels, persistência |
| Relação/agregado de escopo fechado | Compatibilidade e consequências entre seus participantes | Política global, aparência, hipótese do pesquisador |
| Interação/handler | Sequência física e constituição do comando identificável | Regra matemática, C/E, política pedagógica |
| Caso de uso da aplicação | Ordem de invocação de portas e transporte de resultados prontos | Recalcular resultado, inventar registro ou escolher apresentação concreta |
| Scaffolding/proprietário pedagógico | Apoio permitido e decisão pedagógica no repertório autorizado | Estado matemático, persistência técnica |
| Representação/adaptador | Geometria, hit-test e materialização na plataforma | Significado semântico ou critério de correção |
| Infraestrutura | Persistir, transportar, indexar e exportar fatos prontos | Produzir fatos, C/E, diagnóstico ou hipótese |
| Análise do pesquisador | Hipóteses analíticas explicitamente atribuídas ao pesquisador | Alterar retroativamente o fato observado |

Esta tabela aplica as localidades existentes; não cria uma sexta localidade.

## 7. Coordenação legítima

Coordenação é legítima somente quando pelo menos uma destas condições ocorre:

1. o elemento possui o conhecimento relacional completo e coordena apenas os
   participantes de seu escopo fechado; ou
2. o elemento é um caso de uso que apenas sequencia portas e resultados já
   decididos, sem importar tecnologias concretas nem reinterpretar dados.

Um caso de uso pode ordenar `avaliar → persistir → apresentar`, mas:

- `avaliar` devolve decisão factual pronta;
- `persistir` recebe o mesmo fato sem recalcular;
- `apresentar` recebe a decisão sem escolher outra política;
- a ordem não concede ao caso de uso autoridade sobre as três decisões.

## 8. Sinais de deslocamento de concentração

Uma extração deve ser rejeitada quando qualquer condição abaixo ocorrer:

- a nova classe importa simultaneamente dependências concretas de UI,
  logging e scaffolding;
- a nova classe monta manualmente campos de um registro que deveriam ter sido
  produzidos pelo proprietário semântico;
- o mesmo método calcula correção, escolhe feedback e grava o resultado;
- callbacks genéricos escondem regras de domínio ou textos normativos;
- a classe consulta geometria para inferir papel, relação ou significado;
- o adaptador compara valor proposto e esperado;
- o persistidor cria `action_id`, C/E, diagnóstico ou participantes;
- a classe extraída reproduz condicionais anteriormente removidas de `Main`;
- o teste comprova apenas que o método original diminuiu;
- a justificativa arquitetural depende do nome atribuído à nova classe.

## 9. Artefato obrigatório antes da implementação

Toda alteração arquitetural deve registrar uma matriz como esta:

| Conhecimento/efeito | Proprietário autorizado | Entrada | Saída | Consumidor | Tecnologia permitida |
|---|---|---|---|---|---|
| Regra semântica | Objeto/relação proprietária | Comando factual | Resultado factual | Caso de uso | Nenhuma UI |
| Sequência do gesto | Handler do protocolo | Dados neutros | Comando/observações | Porta semântica | Sem Swing/AWT |
| Feedback | Proprietário pedagógico + apresentador | Decisão pronta | Materialização | Usuário | Plataforma no adaptador |
| Registro | Proprietário semântico | Ação constituída | Registro factual | Persistidor | Sem I/O no proprietário |
| Persistência | Infraestrutura | Registro pronto | Confirmação técnica | Repositório/logger | I/O permitido |

Cada linha deve apontar para evidência existente. Células desconhecidas não
podem ser preenchidas por suposição.

## 10. Requisitos P0

### P0.1 — Propriedade explícita

Cada decisão nova ou movida deve possuir exatamente um proprietário
arquitetural identificado.

**Aceitação:**

- o relatório da mudança contém a matriz de responsabilidades;
- nenhuma mesma regra aparece implementada em dois proprietários;
- consumidores recebem valores prontos ou consultam o proprietário.

### P0.2 — Registro factual na origem autorizada

C/E, diagnóstico, `action_id`, `rejection_sequence_id`, regra factual e
participantes são produzidos pelo menor objeto ou agregado com conhecimento
suficiente.

**Aceitação:**

- o registro implementa `RegistroFactualAcaoInstrumental` quando aplicável;
- logger e adaptadores não comparam valores para produzir C/E;
- reenviar o mesmo `action_id` não cria outra ação;
- um ato multiobjeto produz um registro, não um por participante.

### P0.3 — Portas estreitas

Casos de uso dependem de contratos definidos pelo efeito necessário, não de
classes concretas de interface, som, arquivo ou logger.

**Aceitação:**

- pacote de aplicação não importa Swing/AWT;
- serviço de aplicação não importa `LoggerInteracaoGerard` nem implementação
  concreta de scaffolding;
- cada adaptador concreto materializa uma única fronteira coerente;
- substituir um adaptador por um fake não exige carregar a interface gráfica.

### P0.4 — Main compositora e roteadora

`Main.TelaGerard` pode instanciar colaboradores e encaminhar eventos, mas não
conserva inline a mecânica particular, a avaliação semântica ou a montagem do
registro do protocolo extraído.

**Aceitação:**

- o método central contém somente obtenção do evento, chamada e decisão de
  fluxo como `return`;
- o ratchet diminui quando linhas são removidas;
- nenhuma regra é movida para um método auxiliar privado de `Main` apenas para
  satisfazer a contagem.

### P0.5 — Preservação comportamental

A distribuição de responsabilidades não altera ordem, disponibilidade,
feedback ou consistência sem autorização específica.

**Aceitação:**

- teste dirigido cobre a sequência antes/depois;
- testes de domínio cobrem a decisão sem interface;
- testes de adaptador cobrem somente materialização;
- protocolo gráfico relevante recebe validação Robot antes de ser declarado
  integralmente validado.

## 11. Verificações automatizadas mínimas

O verificador arquitetural deve falhar quando:

1. um handler importar `java.awt`, `javax.swing`, logger ou scaffolding;
2. um serviço de aplicação importar UI concreta, logger concreto ou
   scaffolding concreto;
3. um persistidor contiver lógica de C/E, comparação entre esperado/proposto
   ou geração de identidade;
4. `Main` voltar a conter marcadores, textos e montagem do registro do
   protocolo extraído;
5. o proprietário não produzir uma implementação do contrato factual;
6. a redução de um método central não atualizar seu ratchet;
7. uma classe retirada reaparecer com as mesmas dependências sob outro nome.

As verificações textuais são um primeiro bloqueio, não prova suficiente.
Testes comportamentais e revisão da matriz continuam obrigatórios.

## 12. Cenários de aceitação

### Cenário A — extração válida

**Dado** um protocolo concentrado em `Main`,  
**quando** gesto, decisão, feedback e persistência são distribuídos aos seus
proprietários e conectados por portas,  
**então** `Main` apenas roteia, o handler permanece portátil e a infraestrutura
recebe um fato completo.

### Cenário B — mini-Main rejeitada

**Dado** que uma nova classe recebe o evento, calcula C/E, escolhe som, monta o
log e solicita repaint,  
**quando** a revisão arquitetural é executada,  
**então** a mudança falha mesmo que `Main` tenha diminuído.

### Cenário C — coordenação relacional legítima

**Dado** que uma relação estrutural possui o conhecimento sobre vários papéis,
  
**quando** ela calcula as consequências entre esses participantes,  
**então** a coordenação é aceita porque permanece em escopo fechado e não
incorpora UI, persistência ou hipótese analítica.

### Cenário D — serviço de aplicação legítimo

**Dado** um resultado factual já produzido,  
**quando** o serviço o encaminha por portas independentes de persistência e
apresentação,  
**então** a coordenação é aceita se o serviço não reinterpretar o resultado e
não conhecer os adaptadores concretos.

## 13. Métricas de sucesso

### Indicadores imediatos

- 100% das extrações arquiteturais com matriz de responsabilidade;
- zero importações proibidas detectadas nos handlers e serviços de aplicação;
- zero campos factuais construídos em UI, logger ou persistidor;
- ratchets atualizados em toda redução de método central.

### Indicadores de manutenção

- nenhuma reintrodução de regra já extraída em `Main`;
- alterações de UI não exigem modificar proprietários semânticos;
- alterações de persistência não exigem modificar avaliação ou scaffolding;
- divergências arquiteturais são detectadas antes da validação manual.

## 14. Fases de adoção

1. **Ratificar a spec:** concluído em 2026-09-28.
2. **Codificar bloqueios:** concluído em 2026-09-28 por
   `scripts/verificar_localidade_arquitetural.py`, integrado ao verificador de
   regressão. A dívida anterior fica enumerada nominalmente em
   `localidade-arquitetural-baseline.json`; novas ocorrências falham, e uma
   entrada resolvida também exige a remoção explícita da linha de base.
3. **Aplicar ao recorte atual:** auditar integralmente a Fase 7.8 pela matriz.
4. **Aplicar incrementalmente:** exigir a matriz em cada nova extração
   explicitamente autorizada.
5. **Promover a regra de entrada:** referência incluída no `AGENTS.md` em
   2026-09-28; as skills proprietárias continuam sendo atualizadas somente
   quando o conhecimento interno delas mudar.

## 15. Decisões deliberadamente não tomadas

- não se cria uma nova categoria de localidade;
- não se escolhe framework de arquitetura ou injeção;
- não se autoriza reescrever todos os protocolos existentes;
- não se afirma que todas as classes atuais já obedecem à spec;
- a promoção desta spec não autoriza refatorações fora de recortes
  explicitamente aprovados.
