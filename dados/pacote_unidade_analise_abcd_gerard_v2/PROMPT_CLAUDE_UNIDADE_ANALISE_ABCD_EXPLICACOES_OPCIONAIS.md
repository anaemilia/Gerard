# PROMPT PARA O CLAUDE — UNIDADE DE ANÁLISE A-B-C-D COM EXPLICAÇÕES OPCIONAIS

Atue como engenheiro de software responsável por ajustar a modelagem, os logs, a auditoria, a interface de explicações e os testes do projeto Gérard.

Use como referência:

- o projeto Gérard atualizado;
- os relatórios de auditoria anteriores;
- `modelo_unidade_analise_abcd_gerard_v2.json`.

## DEFINIÇÃO METODOLÓGICA OBRIGATÓRIA

A unidade de análise do Gérard é composta por quatro partes:

- **A — ação do computador**;
- **B — ação do usuário**;
- **C — perguntas explicativas disponibilizadas pelo sistema**;
- **D — respostas ou explicações fornecidas pelo usuário**.

As partes **A e B são obrigatórias**.

As partes **C e D são opcionais**.

C e D não dependem da presença física do pesquisador. As perguntas foram previamente elaboradas e incorporadas ao Gérard em uma tela que pode ser aberta pelo próprio usuário por meio de um botão localizado ao lado do texto da situação-problema.

O usuário pode:

1. não abrir a tela;
2. abrir e não responder;
3. responder parcialmente;
4. responder completamente.

Portanto, campos vazios não podem ser interpretados automaticamente como falta de compreensão, recusa, incapacidade ou erro.

## FORMAS VÁLIDAS DA UNIDADE

### Unidade básica

```text
A + B
```

Usada quando o usuário não fornece explicações.

### Unidade ampliada

```text
A + B + C + D
```

Usada quando o usuário abre a tela e fornece explicações.

A unidade continua válida para análise comportamental quando contém apenas A e B.

A análise explicativa só deve utilizar unidades em que C e D estejam disponíveis.

## COMPONENTE A — AÇÃO DO COMPUTADOR

Registrar:

- `action_id`;
- tipo da ação;
- texto ou mensagem apresentada;
- situação-problema;
- instrução;
- feedback;
- template utilizado;
- timestamps;
- eventos técnicos associados.

A pode incluir:

- texto da situação-problema;
- instruções;
- mensagens pré-formatadas;
- feedback;
- solicitação de ação;
- abertura da tela de explicações.

## COMPONENTE B — AÇÃO DO USUÁRIO

Cada ação do usuário deve ser classificada em exatamente um dos seis tipos de protocolo:

```text
SELECIONAR
POSICIONAR
ORIENTAR
QUANTIFICAR
CAMINHO
TEXTO
```

Definições:

- **Selecionar:** um item é escolhido a partir de um conjunto de itens.
- **Posicionar:** o mouse é posicionado em um ponto em um espaço de uma ou mais dimensões.
- **Orientar:** um ponto é escolhido em um espaço de duas ou mais dimensões.
- **Quantificar:** um valor numérico é especificado.
- **Caminho:** tarefas de posicionar e orientar são realizadas sequencialmente.
- **Texto:** textos em um espaço de duas dimensões são modificados, movidos ou editados.

Cada instância de B deve:

1. possuir `protocol_instance_id`;
2. possuir um `protocol_type`;
3. receber exatamente uma avaliação final:
   - `C` = correta;
   - `E` = errada;
4. produzir no máximo uma decisão efetiva do ZDP;
5. produzir no máximo uma atualização efetiva do Modelador;
6. inserir no máximo um caso;
7. manter eventos de mouse e callbacks apenas como eventos técnicos.

Não force seis ações por episódio. Os seis nomes são tipos de protocolo, não uma sequência fixa.

## COMPONENTE C — PERGUNTAS EXPLICATIVAS

As perguntas são previamente cadastradas pelo pesquisador, mas apresentadas pelo sistema.

Registrar:

- `button_available`;
- `button_activated`;
- `screen_opened`;
- `status`;
- lista de perguntas;
- `question_id`;
- tipo da pergunta;
- texto;
- se foi apresentada ao usuário;
- timestamps de abertura e fechamento.

Estados permitidos:

```text
not_opened
opened_not_answered
partially_answered
answered
```

Quando o usuário não abrir a tela:

```json
{
  "button_activated": false,
  "screen_opened": false,
  "status": "not_opened",
  "questions": [
    {
      "question_id": "Q-01",
      "presented_to_user": false
    }
  ]
}
```

Não apagar a existência da pergunta cadastrada. Apenas registrar que ela não foi apresentada.

## COMPONENTE D — RESPOSTAS DO USUÁRIO

Registrar:

- `question_id`;
- texto da resposta;
- status;
- timestamps;
- se a resposta foi parcial;
- se foi salva;
- se foi posteriormente editada;
- identificador da tentativa ou unidade de análise.

Estados permitidos:

```text
not_opened
opened_not_answered
partially_answered
answered
```

Quando a tela não for aberta:

```json
{
  "status": "not_opened",
  "responses": []
}
```

Quando a tela for aberta sem resposta:

```json
{
  "status": "opened_not_answered",
  "responses": []
}
```

Quando houver resposta parcial:

```json
{
  "status": "partially_answered",
  "responses": [
    {
      "question_id": "Q-01",
      "content": "Resposta parcial..."
    }
  ]
}
```

## CAMPO DE COMPLETUDE

Registrar:

```text
analysis_unit_completeness
```

Valores permitidos:

```text
A_B
A_B_C
A_B_C_D
```

Use:

- `A_B` quando a tela não foi aberta;
- `A_B_C` quando a tela foi aberta, as perguntas foram apresentadas, mas nenhuma resposta foi concluída;
- `A_B_C_D` quando houver ao menos uma resposta registrada.

Não use completude para avaliar qualidade cognitiva. Ela indica apenas disponibilidade de dados.

## INTERPRETAÇÃO DA AUSÊNCIA

Criar um campo:

```text
missing_explanation_reason
```

Valores sugeridos:

```text
user_did_not_open_explanation_screen
user_opened_but_did_not_answer
user_answered_partially
technical_failure
not_applicable
unknown
```

Não inferir automaticamente que ausência de resposta significa:

- falta de compreensão;
- recusa;
- desinteresse;
- erro;
- dificuldade conceitual.

Essas interpretações só podem ser feitas quando houver outros dados que as sustentem.

## ARQUIVOS DE SAÍDA

Gerar:

### `unidades_analise.jsonl`

Uma linha por unidade A-B-C-D.

Este é o arquivo principal da pesquisa.

### `acoes_usuario_protocolos.jsonl`

Uma linha por instância de protocolo B.

### `eventos_tecnicos.jsonl`

Uma linha por evento técnico.

### `explicacoes_usuario.jsonl`

Uma linha por interação com a tela de explicações ou por resposta registrada.

Todos os arquivos devem compartilhar:

- `analysis_unit_id`;
- `episode_id`;
- `session_id`;
- `user_id`;
- `protocol_instance_id`, quando aplicável.

## RELAÇÃO ENTRE OS ARQUIVOS

```text
analysis_unit_id
├── A_computer_action
├── B_user_action
│   └── protocol_instance_id
├── C_explanation_questions
└── D_user_explanations
```

Eventos técnicos devem ser vinculados à unidade e à instância de protocolo, mas não criam nova unidade de análise.

## IDEMPOTÊNCIA

Use:

```text
session_id + episode_id + analysis_unit_id + protocol_instance_id
```

Uma mesma instância não pode:

- gerar duas avaliações finais;
- aumentar duas vezes o erro no ZDP;
- atualizar duas vezes o Modelador;
- inserir dois casos.

Salvar ou editar uma explicação não deve reprocessar a ação B.

## INTERFACE

Verifique a tela existente na visão do pesquisador e o botão ao lado do texto.

A implementação deve registrar:

- botão exibido;
- botão acionado;
- tela aberta;
- tela fechada;
- campos apresentados;
- campos preenchidos;
- salvamento;
- cancelamento;
- edição posterior.

Não obrigar o usuário a preencher as explicações.

Não bloquear a resolução da atividade se a tela não for aberta.

## TESTES AUTOMATIZADOS

Criar testes permanentes para:

1. unidade A-B ser válida sem C-D;
2. unidade A-B-C-D ser válida;
3. botão não acionado gerar `not_opened`;
4. tela aberta sem resposta gerar `opened_not_answered`;
5. resposta parcial gerar `partially_answered`;
6. resposta completa gerar `answered`;
7. ausência de resposta não ser convertida em erro;
8. uma ação B receber apenas uma avaliação C/E;
9. cada ação B ser classificada em um dos seis protocolos;
10. eventos técnicos não criarem nova unidade;
11. salvar explicação não reprocessar ZDP;
12. editar explicação não inserir novo caso;
13. `analysis_unit_id` correlacionar A, B, C e D;
14. análise comportamental aceitar A-B;
15. análise explicativa usar apenas unidades com respostas;
16. falha técnica na tela ser registrada como `technical_failure`;
17. as correções anteriores permanecerem válidas.

## CARDINALIDADE

Gerar `cardinalidade_unidades_analise.tsv` com:

- `episode_id`;
- `analysis_units_total`;
- `units_A_B`;
- `units_A_B_C`;
- `units_A_B_C_D`;
- `protocol_instances_total`;
- `correct_actions`;
- `error_actions`;
- `explanation_screens_opened`;
- `opened_not_answered`;
- `partially_answered`;
- `answered`;
- `technical_events`;
- `divergences`.

Deve valer:

```text
analysis_units_total
=
units_A_B + units_A_B_C + units_A_B_C_D
```

E:

```text
protocol_instances_total
=
correct_actions + error_actions
```

## JSON SCHEMA

Criar ou atualizar schemas para:

- unidade de análise;
- ação do computador;
- ação do usuário;
- perguntas explicativas;
- respostas;
- eventos técnicos.

Usar:

- `required`;
- `enum`;
- `oneOf`, quando necessário;
- `additionalProperties=false` nos blocos estáveis;
- validação de data e hora;
- validação da relação entre status e conteúdo.

Exemplo:

- `status=not_opened` exige `screen_opened=false`;
- `status=answered` exige pelo menos uma resposta;
- `analysis_unit_completeness=A_B_C_D` exige D com resposta.

## PRESERVAÇÃO

Não remover:

- distinção canônico/reativo;
- idempotência;
- logs técnicos;
- localização semântica;
- recálculo de coordenadas;
- rastreamento do Robot;
- auditoria dos agentes;
- validação de schema;
- testes das rodadas anteriores;
- correção do disparo triplo;
- taxonomia dos seis protocolos.

## CRITÉRIOS DE ACEITAÇÃO

A alteração estará concluída quando:

- A e B forem obrigatórios;
- C e D forem opcionais;
- ausência de C-D tiver status explícito;
- botão não acionado não for interpretado como erro;
- unidade A-B continuar válida;
- unidade A-B-C-D for corretamente correlacionada;
- cada ação B possuir um protocolo e uma avaliação C/E;
- salvar explicação não reprocessar os agentes;
- arquivos de análise e logs técnicos estiverem separados;
- testes permanentes cobrirem os quatro estados;
- não houver regressão funcional.

## ENTREGÁVEIS

Devolver:

1. projeto atualizado em ZIP;
2. `unidades_analise.jsonl`;
3. `acoes_usuario_protocolos.jsonl`;
4. `eventos_tecnicos.jsonl`;
5. `explicacoes_usuario.jsonl`;
6. `cardinalidade_unidades_analise.tsv`;
7. schemas atualizados;
8. relatório técnico;
9. matriz A-B-C-D;
10. matriz dos seis protocolos;
11. arquivos criados e modificados;
12. resultado da compilação;
13. resultado dos testes;
14. resultado da regressão;
15. exemplos reais de unidade A-B;
16. exemplos reais de unidade A-B-C-D;
17. limitações restantes.

Não invente dados, respostas, perguntas apresentadas, testes ou resultados.
Não trate ausência de explicação como erro.
Não obrigue o usuário a abrir a tela.