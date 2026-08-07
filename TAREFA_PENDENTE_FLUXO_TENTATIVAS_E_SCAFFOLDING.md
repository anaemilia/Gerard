# Tarefa pendente — fluxo de N=3 tentativas rejeitadas + repertório local de Scaffolding

Status: **mecanismo de tentativas implementado (2026-08-07)** — piloto
(`RELATORIO_...PapelQuantitativo...md`, commit `032e12e`) e produção
(`RELATORIO_FLUXO_TENTATIVAS_PRODUCAO_2026-08-07.md`). Conteúdo
pedagógico da tela de ajuda: **primeiro rascunho implementado
(2026-08-07)** — ver "Conteúdo da tela de ajuda" abaixo; não é validação
pedagógica definitiva. O repertório local de Scaffolding (2 eixos) e a
lógica de seleção dentro dele **continuam não implementados, não
decididos**.

---

## O que já foi implementado (2026-08-07)

- `PapelQuantitativo.registrarTentativa`/`restaurar`/
  `estaBloqueadoPorLimiteTentativas`: contagem de tentativas + action_id
  + bloqueio ao atingir N=3, exatamente como descrito abaixo. Estado mora
  no papel, como este documento e a REFERENCE.md §4.8 determinam.
- Ligado em produção: `confirmarValorIncognitaAceito` (Main.java) conta
  cada rejeição real da incógnita; ao atingir 3, mostra um aviso mínimo
  (`mostrarAvisoLimiteTentativasAtingido`) e bloqueia novas tentativas
  até um dos dois botões "Restaurar" existentes ser acionado.
- **Conteúdo da tela de ajuda — primeiro rascunho (2026-08-07)**: a
  mensagem (`ui.notice.attemptLimitReached`, `mensagens_{pt,en,es,fr}.properties`)
  ganhou uma dica curta — revisar a relação entre as quantidades já
  conhecidas, sem revelar o valor da incógnita — mantendo o aviso
  operacional ("use Restaurar") que já existia. Segue a categoria
  "mensagem informativa" de `gerard-scaffolding-interacao` (string
  localizada avulsa, sem mecanismo estrutural dedicado — não existe um
  padrão de classe pré-estabelecido para isso, como a skill já avisa).
  **Isto não é uma validação pedagógica definitiva** — é um rascunho
  razoável dentro do estilo já usado pelo projeto (curto, não revela a
  resposta), para não deixar o mecanismo (já implementado) sem nenhum
  conteúdo. Revisão/ajuste do texto é esperada.

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
