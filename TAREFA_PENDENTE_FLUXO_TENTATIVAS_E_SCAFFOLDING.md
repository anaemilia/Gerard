# Tarefa pendente — fluxo de N=3 tentativas rejeitadas + repertório local de Scaffolding

Status: **registrada como alvo de implementação futura. Não autorizada a
começar.** Momento de implementação ainda não decidido.

---

## O fluxo-alvo

N=3 tentativas rejeitadas do mesmo item → tela de ajuda → acionar
"restaurar" inicia uma ação nova e separada. Decisão normativa completa em
`REFERENCE.md §4.8` (cardinalidade ação:evento, Alternativa B, `action_id`).

## Onde deve morar cada responsabilidade, quando implementado

- **Contagem de tentativas e `action_id`**: conhecimento do próprio objeto
  semântico (papel) — consistente com o princípio da localidade do
  conhecimento já adotado no projeto.
- **Vibração/som**: execução mecânica da interface, reagindo a um sinal do
  domínio — mesmo padrão já usado por `chaveFeedbackPedagogico` em
  `DiagnosticoErroPapel`.
- **Escolha de qual ajuda concreta mostrar**: política pedagógica, hoje
  associada a `gerard.Scaffolding.*` (produção) — mas a lógica de seleção
  em si ainda não está decidida (ver pendência nova abaixo).

## Pendência nova, separada desta

A lógica de seleção do estilo/ajuda apropriado dentro do repertório de
Scaffolding de cada objeto — ainda não decidida, não é resolvida por este
registro.

## Nota de atualização (2026-08-06) — vocabulário criado depois deste registro

Decisões posteriores a este registro, hoje em `REFERENCE.md §4.8`, dão
vocabulário mais preciso ao que este arquivo já descrevia. Não altera nem
contradiz nada acima — é só a ligação de vocabulário, para quem ler os
dois documentos em conjunto:

- A exibição da tela de ajuda na terceira tentativa rejeitada (fluxo
  descrito acima) corresponde ao evento `FEEDBACK_EXIBIDO`, especificado
  depois deste registro, cujo critério de confirmação depende da
  modalidade de entrega do Scaffolding ("renderizado" para
  visual/sonora; "affordance ativada" para háptica/guiada por
  movimento/manipulativa).
- "Vibração/som", citado acima como execução mecânica da interface,
  corresponde às modalidades de entrega "háptica" e "sonora" da
  taxonomia de dois eixos do repertório de Scaffolding (tipo funcional ×
  modalidade de entrega), definida depois deste registro.

## Autorização

Nenhum código, teste ou comportamento muda por este registro. Não
autorizada a começar a implementação — só o registro normativo
(`REFERENCE.md §4.8`) e esta tarefa pendente existem até aqui.
