---
name: Semantic Event Logging for GERARD
description: Registra fatos semanticamente relevantes da atividade, preservando contexto, origem e separação entre evento factual e hipótese analítica.
---

# Registro de Eventos Semânticos

## Dependência normativa

Leia `gerard-semantic-model/REFERENCE.md` antes de aplicar esta skill.

## Objetivo

Registrar mudanças, tentativas, validações e ocorrências semanticamente relevantes do domínio sem reduzir a atividade a cliques, coordenadas ou mensagens de log técnico.

O domínio produz ou descreve o fato semântico. A infraestrutura persiste, indexa, consulta e exporta.

## Princípios

- O domínio não grava logs diretamente.
- Eventos representam fatos contextualizados, não interpretações automáticas sobre o usuário.
- Eventos são independentes da tecnologia de interface.
- A origem da ação é obrigatória.
- Valores calculados pelo sistema nunca são registrados como ações do usuário.
- Eventos e hipóteses analíticas são estruturas separadas.

## Contexto mínimo do evento

Sempre que aplicável, registrar:

- `event_id`;
- `session_id`;
- `local_user_id`;
- `attempt_id`;
- `problem_situation_id`;
- `representation_id`;
- `action_id` ou correlação com ação anterior;
- tipo semântico do evento;
- origem da ação;
- objeto ou papel semântico envolvido;
- modalidade de interação;
- estado anterior;
- estado posterior;
- valor proposto ou calculado;
- resultado da validação;
- diagnóstico factual;
- feedback apresentado, quando houver;
- data e hora;
- versão do modelo ou esquema de evento.

## Origem da ação

Usar enumeração explícita, no mínimo:

- `USUARIO`;
- `SISTEMA`;
- `INFERENCIA_COMPUTACIONAL`;
- `PESQUISADOR`.

Se uma relação estrutural calcula um valor, o evento deve registrar
`SISTEMA`, nunca `USUARIO` nem `INFERENCIA_COMPUTACIONAL` — o cálculo
aplica uma regra explícita e fixa, não deriva um juízo a partir de
evidências (critério completo em `REFERENCE.md §4.8`).

A sugestão de um código de invariante operatório pelo sistema
(`SugestorInvarianteOperatorio.sugerirCodigo`) não é, por si só, um evento com
origem definida — é um auxílio opcional. O evento que registra a mobilização
de um invariante operatório é sempre o do pesquisador selecionando ou criando
o invariante e relacionando-o à ação; sua origem é sempre `PESQUISADOR`.

## Tipos de eventos

Exemplos:

- papel selecionado;
- valor proposto;
- valor aceito;
- valor rejeitado;
- papel posicionado;
- representação alterada;
- relação estrutural verificada;
- cálculo sugerido pelo sistema;
- feedback apresentado;
- explicação solicitada;
- verbalização registrada;
- tentativa iniciada, concluída ou abandonada.

Os nomes devem expressar o significado da ação, não o gesto físico isolado.

## Eventos e verbalizações

Perguntas, respostas e explicações devem poder ser vinculadas:

- à tentativa;
- ao evento ou intervalo de eventos relevante;
- à representação e ao papel envolvidos;
- ao autor da verbalização;
- ao momento da atividade.

## Eventos não são invariantes operatórios

Uma ação, sequência de eventos ou verbalização não constitui automaticamente um esquema, teorema-em-ação ou conceito-em-ação.

A análise pode formular uma hipótese separada e revisável, com:

- tipo da hipótese;
- classe de situações;
- evidências citadas por identificador;
- critérios analíticos;
- nível de sustentação;
- interpretações alternativas;
- estado da hipótese.

Se os registros forem insuficientes, não formule hipótese.

## Publicação e infraestrutura

O publicador deve ser injetável por interface. Para funcionamento sem infraestrutura, prefira Null Object, como `PublicadorEventoDominio.NENHUM`, em vez de dependência `null`.

A infraestrutura é responsável por:

- persistir;
- indexar;
- consultar;
- exportar;
- aplicar retenção e anonimização;
- validar versão do esquema de evento.

## Regra de produção

Sempre que uma ação modificar, consultar, validar ou interpretar um estado semanticamente relevante, avalie:

1. ocorreu um fato que precisa ser reconstruído posteriormente?
2. qual é sua origem?
3. qual tentativa e situação fornecem o contexto?
4. qual estado mudou?
5. trata-se de evento factual ou hipótese analítica?

Produza o evento somente com o significado factual conhecido naquele momento.

## Consolidação durante um gesto contínuo

Atualizar uma representação a cada amostra do ponteiro não exige emitir um
novo evento semântico por pixel. No controle de barras de
`COMPARACAO_MEDIDAS`, o estado e as representações são propagados a cada
movimento, mas o evento `CONSISTENCIA_AUTOMATICA` retém somente o último
`Snapshot` e é gravado no término do gesto. Um novo pressionamento executa
um descarregamento defensivo caso o término anterior tenha sido interrompido.

Essa é uma política de granularidade do registro, não uma regra matemática
nem um bloqueio da sincronização visual. A identificação do papel recalculado
permanece no estado semântico/relação proprietária; a apresentação somente
retém o fato já produzido. As amostras intermediárias podem integrar o
registro factual do gesto segundo `gerard-log-gestos-interacao`, mas não se
transformam automaticamente em ações instrumentais.

Status verificado em 2026-08-30: os métodos
`registrarLogConsistenciaAutomaticaSeHouve` e
`flushLogConsistenciaAutomaticaPendenteDoArrasteComparacao` implementam a
retenção, e o verificador determinístico protege os términos normal e
defensivo.

## Anti-padrões

- Logar apenas `mouseClicked(x,y)`.
- Omitir a origem da ação.
- Registrar cálculo automático como ação do estudante.
- Vincular explicação apenas à situação, ignorando a tentativa.
- Inserir inferência cognitiva dentro do evento factual.
- Declarar invariante operatório a partir de um evento isolado.
- Fazer o domínio escrever diretamente em arquivo ou banco.
