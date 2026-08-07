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

## Repertório concreto de Scaffolding — registrado em 2026-08-07

A usuária forneceu o repertório concreto (6 itens, prefixo `AG_`) que
preenche a lacuna deixada em aberto por `REFERENCE.md §4.8` ("A seleção de
qual elemento do repertório usar em cada situação é uma decisão separada e
ainda em aberto"). Classificação nos dois eixos já normatizados
(`REFERENCE.md §4.8`: tipo funcional × modalidade de entrega):

| Código | Ação | Tipo funcional | Modalidade de entrega | Status |
|---|---|---|---|---|
| `AG_EMS` | Exibir mensagem de sucesso | — (ausência de erro, não é reparo) | Visual | **Já implementado** — `SequenciadorFeedbackConclusao` |
| `AG_EMLQ` | Exibir mensagem de questionamento | Metacognitivo | Visual | **Já implementado** — `ScaffoldingQuestionamento.criarPerguntaConfirmacao`, disparado a cada rejeição (`ui.question.valueMismatch`) |
| `AG_EME` | Exibir mensagem explicativa | Conceitual | Visual | **Parcialmente implementado** — `mostrarDicaOperacaoIncognita`/`ui.hint.chooseOperation`, hoje só uma frase mínima ("escolha soma ou subtração") |
| `AG_EMCME` | Exibir material concreto + mensagem explicativa sobre o uso do material | Procedimental | Manipulativa + Visual | **Parcial** — a parte de mensagem existe (`mostrarAvisoLimiteTentativasAtingido`/`ui.notice.attemptLimitReached`, estendida em 2026-08-07 com uma dica); **a parte de destacar/mostrar material concreto não existe** |
| `AG_AC` | Automatizar a contagem | Procedimental | Guiada por movimento / manipulativa | **Não implementado — só descrito**, por decisão explícita da usuária ("deixe apenas a descrição, depois pensamos sobre como operacionalizar") |
| `AG_AE` | Automatizar passos da modelagem do problema | Estratégico | Guiada por movimento | **Não implementado — só descrito**, mesma decisão acima |

Ordem confirmada pela usuária (2026-08-07): `AG_EMS` não é um degrau da
escalada de erro — é o estado padrão/ausência de erro, já coberto pelo
mecanismo de sucesso existente. A escalada de erro dentro do fluxo de N=3
tentativas é `AG_EMLQ` → `AG_EME` → `AG_EMCME` → (`AG_AC`/`AG_AE`, fora do
fluxo de N=3, sem gatilho definido ainda).

**`AG_AC`/`AG_AE` — regra explícita**: qualquer operacionalização futura
desses dois itens tem que seguir o padrão arquitetural já estabelecido
nas skills do projeto (`gerard-domain-model-first`,
`gerard-knowledge-locality-principle`, `gerard-handlers-de-interacao` se
envolver reestruturar despacho de mouse, `gerard-semantic-event-logging`
para o evento correspondente) — não uma automação ad hoc dentro de
`Main.java`. Isso, somado à regra de segurança de
`gerard-scaffolding-interacao` ("em hipótese alguma a interface pode
automatizar passos cuja ordem não tenha vindo de autorização explícita do
pesquisador"), significa que a implementação de `AG_AC`/`AG_AE` exige uma
decisão de modelagem própria, apresentada e aprovada antes de qualquer
diff — não decidida aqui.

**`AG_EMCME` — material concreto implementado (2026-08-07).** A usuária
esclareceu que "material concreto" é o diagrama complementar que já existe
ao lado do diagrama de Vergnaud (quadradinhos/Venn, barras da Comparação,
processo da Transformação) — hoje sempre visível, deve passar a aparecer
só na última opção da escalada (3ª rejeição). Implementado:
`SeletorRepresentacaoComplementar.deveExibir` ganhou um parâmetro
`escaladaDeAjudaNoLimite`, computado em `Main.deveExibirDiagramaComplementar()`
a partir de `tentativasIncognitaAtual.estaBloqueadoPorLimiteTentativas()`
— sem estado novo, some de novo automaticamente ao "Restaurar". Ver
`RELATORIO_VISIBILIDADE_DIAGRAMA_COMPLEMENTAR_2026-08-07.md`, que também
documenta um bug de staleness encontrado e corrigido na chave de cache de
`garantirTentativasIncognitaAtual` (não tinha o id da situação-problema,
só o nome do papel). `AG_EMCME` está, com isso, implementado por
completo (mensagem + material concreto).

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
