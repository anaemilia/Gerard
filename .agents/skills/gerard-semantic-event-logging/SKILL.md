---
name: gerard-semantic-event-logging
description: Registra fatos semanticamente relevantes da atividade, preservando contexto, origem e separação entre evento factual e hipótese analítica.
---

# Registro de Eventos Semânticos

## Dependência normativa

Leia `gerard-semantic-model/REFERENCE.md` antes de aplicar esta skill.

## Objetivo

Registrar mudanças, tentativas, validações e ocorrências semanticamente relevantes do domínio sem reduzir a atividade a cliques, coordenadas ou mensagens de log técnico.

O Objeto Semanticamente Rico ou a relação estrutural que possui o conhecimento
produz e possui o registro factual. A infraestrutura persiste, indexa,
consulta e exporta sem assumir sua propriedade semântica.

## Princípios

- O objeto rico produz o registro como valor factual tipado, sem executar I/O
  direto em arquivo ou banco.
- Eventos representam fatos contextualizados, não interpretações automáticas sobre o usuário.
- Eventos são independentes da tecnologia de interface.
- A origem da ação é obrigatória.
- Valores calculados pelo sistema nunca são registrados como ações do usuário.
- Eventos e hipóteses analíticas são estruturas separadas.
- Uma ação instrumental conserva um único `action_id`, mesmo quando referencia
  vários objetos semânticos; eventos derivados correlacionam-se com essa ação
  sem recriá-la.

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
- versão do Modelo do Usuário e regra adaptativa aplicada, quando houver
  decisão de ajuda;
- algoritmo de origem e proveniência dos casos da regra publicada, quando uma
  regra tiver sido aplicada;
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
- ajuda adaptativa decidida;
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

O publicador recebe um registro já produzido pelo objeto proprietário. Ele não
constitui a ação, não avalia C/E, não reinterpreta o gesto e não passa a ser o
dono do log por gravá-lo.

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

Para ajuda adaptativa, registrar dois fatos distintos:

- **decisão de ajuda**: proprietário semântico, diagnóstico, versão do modelo,
  regra aplicada e apoio escolhido;
- **feedback exibido**: confirmação de que a representação materializou o
  apoio, segundo o critério de sua modalidade.

O evento de decisão deve permitir reconstruir os dois níveis temporais usados:
a versão da fotografia histórica e a projeção factual corrente do Modelo da
Situação/Solução. Quando houver regra aplicada, registrar também seu algoritmo
e sua proveniência de casos; não depender de o catálogo futuro ainda conter a
mesma versão.

Uma decisão não prova exibição; uma exibição não prova compreensão.

## Anti-padrões

- Logar apenas `mouseClicked(x,y)`.
- Omitir a origem da ação.
- Registrar cálculo automático como ação do estudante.
- Vincular explicação apenas à situação, ignorando a tentativa.
- Inserir inferência cognitiva dentro do evento factual.
- Declarar invariante operatório a partir de um evento isolado.
- Fazer o objeto executar I/O diretamente em arquivo ou banco, ou atribuir ao
  persistidor a propriedade semântica do registro.
- Emitir uma ação duplicada para cada objeto participante de uma mesma ação.
