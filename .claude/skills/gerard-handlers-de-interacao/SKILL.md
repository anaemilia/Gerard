---
name: gerard-handlers-de-interacao
description: Direção arquitetural e estado da extração incremental do código de manipulação de mouse/teclado (arrastar, soltar, hover e clique) de Main.TelaGerard. Use ao extrair ou revisar um protocolo de interação, ao decidir onde colocar um adaptador Swing ou handler, ou quando a tela voltar a concentrar mecânica particular. Não confundir com gerard-scaffolding-interacao, que decide o que o protocolo faz, e não onde sua mecânica reside.
---

# Handlers de interação modulares

## Dependência normativa

Leia `gerard-semantic-model/REFERENCE.md` e `gerard-domain-model-first`
antes de aplicar esta skill. Esta skill não redefine nada das duas —
ela propõe uma implementação concreta para a camada "Interação" que
`gerard-domain-model-first` já separa de Domínio e Representação, mas
sem prescrever como estruturar isso em código.

## Status: extração incremental em curso

`TelaGerard` (`Main.java`) implementa `MouseListener`, `MouseMotionListener`
e `KeyListener` diretamente. Quatro famílias já possuem handlers locais,
mas `mousePressed`, `mouseReleased` e `mouseMoved` ainda contêm trechos
extensos de despacho e mecânica particular. A inclusão das três categorias
de relações acrescentou novas necessidades à interface; por isso, o total
de linhas de `Main.java` não é uma medida válida do progresso desta
extração. A medida relevante é se cada protocolo central deixa de possuir
a mecânica particular que já pode ser encaminhada a um handler.

## Por que isso não contradiz a arquitetura já registrada

`gerard-domain-model-first` já define a Interação como camada distinta
de Domínio e Representação, responsável por "conversão de gestos em
comandos semanticamente identificados" e "distinção da origem da
ação" — mas nunca disse *onde* esse código deveria morar. Esta skill
preenche essa lacuna com um padrão concreto, sem alterar nenhuma regra
das outras skills: `PapelQuantitativo` e as classes `RelacaoEstrutural*`
continuam no Domínio, sem saber de mouse, pixel ou Swing — muda só onde
o despacho de eventos de mouse é organizado.

## O padrão adotado

1. **Elemento representacional** (`ItemTextoArrastavel`, `ElementoTextoMovel`,
   `ElementoVergnaud`) continua rico, mas sem receber eventos de mouse. Ele
   mantém o estado e a realização visual e, quando possui esse conhecimento,
   produz o registro factual de sua própria participação no gesto.
2. **O adaptador da interface** conhece Swing, recebe `MouseEvent`, consulta
   a geometria e o hit-testing reais da árvore de componentes e traduz o
   evento bruto em dados explícitos para o protocolo.
3. **Um handler de interação por protocolo** mantém o estado e a sequência
   do gesto. Recebe coordenadas e tipos geométricos neutros; AWT, Swing e a
   classe visual concreta terminam no adaptador da plataforma.
4. **O proprietário semântico** avalia a ação constituída usando seu
   conhecimento local ou relacional, sem receber `MouseEvent`, componentes
   Swing ou coordenadas de tela.
5. **`TelaGerard` compõe e roteia** — recebe o evento bruto e o encaminha;
   não conserva inline a mecânica particular de cada protocolo.

Fluxo obrigatório:

`MouseEvent/Swing -> adaptador da plataforma -> handler portátil -> porta da representação -> proprietário semântico, quando houver ação constituída`

Nomes devem usar o vocabulário real do domínio Gérard (`ItemTextoArrastavel`,
`ElementoVergnaud`...), não termos genéricos como "SemanticElement" ou
"BoxElement".

## Roteiro incremental sugerido

Nenhuma etapa abaixo está autorizada a começar sem confirmação explícita
da usuária — mesma regra de segurança de `gerard-consistencia-estado`.

1. Escolher **um** protocolo de mouse como prova de conceito (ex.:
   arraste de `ItemTextoArrastavel`) e extrair só esse handler, mantendo
   os demais como estão.
2. Comparar comportamento antes/depois com um harness de regressão real
   (mesmo padrão desta sessão — `TesteMonkeySemiGuiado` ou equivalente
   dirigido por `Robot`, não só leitura de código).
3. Só depois de validado, repetir para os demais protocolos, um de cada
   vez — nunca extrair todos de uma vez.
4. Tratar cada extração como mudança arquitetural explícita, com relatório
   próprio, seguindo a convenção já estabelecida no projeto
   (`RELATORIO_*.md`).

### Status do roteiro

- **Fase 7.2 — validada.** Prova de conceito do passo 1: extração de
  `HandlerInteracaoItemTextoArrastavel` de `Main.TelaGerard`
  (`RELATORIO_FASE_7_2_HANDLER_ITEM_TEXTO_2026-08-09.md`). Passo 2
  cumprido em 2026-08-11 com `TesteMonkeyGuiadoPorCasosReais` (Robot
  real, 14 episódios reais do catálogo de doutorado, 45 passos, 0
  divergências, sem exceção não tratada) — ver
  `RELATORIO_VALIDACAO_ROBOT_HANDLER_ITEM_TEXTO_2026-08-11.md`. Pickup,
  drag, soltura, reposicionamento e o caso de item já presente no
  diagrama ficaram consistentes com o comportamento anterior à extração.
- **P1.1 — gesto separado da ação no item textual.** O mesmo handler agora
  encerra a trajetória física em `ResumoGestoArraste`, sem conhecer alvo
  semântico. `ItemTextoArrastavel` produz o registro factual, a tela fornece
  somente a classificação derivada da geometria real e uma porta separada o
  persiste. O rastreador granular legado deixou de receber esse protocolo.
- **Fase 7.3 — validada.** Segundo protocolo (`ElementoTextoMovel`, ver
  `HandlerInteracaoElementoTextoMovel`) extraído seguindo o passo 3 (um de
  cada vez). Validado pelo mesmo harness Robot real da Fase 7.2 (os dois
  handlers convivem no mesmo build e são exercitados pelos mesmos 14
  episódios — ver `RELATORIO_FASE_7_3_HANDLER_ELEMENTO_TEXTO_MOVEL_2026-08-11.md`).
- **Fase 7.4 — validada.** Terceiro protocolo: elementos e conectores do
  diagrama de Vergnaud (`ElementoVergnaud`/`ConectorVergnaud`), extraídos
  para `HandlerInteracaoElementosDiagramaVergnaud` — decisão explícita da
  usuária, 2026-08-16, sobre qual dos três protocolos restantes (quadradinhos
  do Venn, eixo de inteiros, elementos de Vergnaud) extrair a seguir. Ver
  `RELATORIO_FASE_7_4_HANDLER_ELEMENTOS_DIAGRAMA_VERGNAUD_2026-08-16.md`
  para a validação (build, verificador de regressão e harness Robot real).
  Na decisão posterior da usuária, o reposicionamento livre de
  `ElementoVergnaud` deixou de constituir ação permitida.
- **P4.2 — protocolo do conector tornado portátil.** O protocolo ainda
  válido dos conectores usa `HandlerInteracaoArrasteIncremental`, que depende
  somente de `AlvoMovelIncremental` e `LimitesMovimento`. A classe
  `AdaptadorMovimentoConectorVergnaud` possui a tradução desktop entre
  `Rectangle` e o contrato neutro. O antigo handler misto, que ainda continha
  o protocolo inativo de `ElementoVergnaud`, foi removido.
- **Fase 7.5 — implementada e verificada isoladamente.** O protocolo dos quadradinhos do diagrama de
  Venn foi transferido para `HandlerInteracaoQuadradinhoVenn`. A tela faz o
  hit-testing e encaminha início, movimento, conclusão e cancelamento. O
  teste `TesteAffordancePickupUI` usa o acesso encapsulado
  `obterQuadradinhoAtivo()`.
- **Fase 7.6 — retirada em 2026-09-01, não mantida.** O protocolo do eixo
  flutuante único (`HandlerInteracaoEixoInteiros`, `AlvoInteracaoEixoInteiros`,
  `AdaptadorInteracaoEixoInteiros`, `FonteGeometriaInteracaoEixoInteiros`) foi
  validado e aprovado em 2026-08-28 (ver histórico abaixo), mas o mecanismo
  desktop que ele envolvia (`itemGraficoInteiros`/`numeroRelativoGraficoInteiros`/
  `scaffoldingGraficoInteiros` em `Main`) acabou se revelando código morto:
  a regra generalizada 2026-08-18 ("todo número relativo ou transformação
  carrega uma lupa") é aplicada sem exceção pelos seis renderizadores
  canônicos, então `devemExibirPaineisEixosRelacoes()` é sempre verdadeiro em
  qualquer diagrama com número relativo — o guard que os dois gatilhos deste
  mecanismo já tinham desde a Fase 7.7 os tornava inalcançáveis nas seis
  categorias, não só nas de Relações. Confirmado por leitura estática de
  todos os renderizadores e por dois runs do harness Robot cobrindo 5 das 6
  categorias com arrastes diretos em número relativo, zero ocorrências.
  Removido por inteiro (protocolo, classes concretas de instância única e
  teste) em vez de unificado com a Fase 7.7 — não havia o que unificar, um
  dos dois lados já não existia na prática. `Main.java`, o verificador
  completo e a bateria de testes foram reaprovados após a remoção
  (`LEVANTAMENTO_ACOPLAMENTO_MAIN_WEB_2026-08-31.md` tem o registro
  completo). Os painéis individuais das categorias de Relações (Fase 7.7,
  abaixo) não foram afetados e continuam sendo o único mecanismo de eixo.
- **Fase 7.7 — validada.** Após autorização explícita da usuária, o protocolo
  dos painéis individuais de eixo das categorias de Relações foi transferido
  para `HandlerInteracaoPaineisEixosRelacoes`, que depende somente da porta
  `AlvoInteracaoPaineisEixosRelacoes`. A tradução de `Rectangle`, dimensões
  reais, hit-testing e estado visual pertence a
  `AdaptadorInteracaoPaineisEixosRelacoes` e
  `FonteGeometriaInteracaoPaineisEixosRelacoes`, na fronteira desktop. A
  `Main` preserva a autorização contextual, solicita a sincronização antes da
  conclusão visual e roteia o repaint; o handler não conhece Swing/AWT,
  semântica, logs, scaffolding nem Modelo do Usuário. Em 2026-08-30 passaram o
  build de 533 fontes, o verificador completo, 96 testes executáveis e o Robot
  de 60 segundos (58 iterações, zero erros).

- **Fase 7.8 — validada.** Candidato original (controles de clique de
  adicionar/remover unidade do Venn) reavaliado antes de qualquer extração:
  a decisão de permissão já estava inteiramente delegada aos proprietários
  semânticos, sem gesto com estado a extrair — criar um handler ali seria
  indireção sem ganho. Trocado, com autorização explícita, pelo arraste do
  ponto de controle da barra de Comparação de Medidas
  (`arrastandoControleComparacao`), que tem a mesma forma dos protocolos já
  extraídos. `HandlerInteracaoControleComparacao` concentra o único fato
  mecânico do gesto — estar ou não em curso — já que a conversão de posição
  em valor já delegava a `RecalculoComparacaoMedidas`/
  `RelacaoEstruturalComparacao` antes desta extração. O harness Robot
  (`TesteMonkeySemiGuiado`) não dirige este controle especificamente (só
  item textual, digitação de incógnita, troca de categoria e nova
  situação); a validação automatizada real desta fase é
  `TesteHandlerInteracaoControleComparacao`, unitário. Ver
  `RELATORIO_FASE_7_8_HANDLER_CONTROLE_COMPARACAO_2026-09-03.md`.

`mousePressed` com 401 linhas ainda concentra risco alto para uma mudança só;
extrações grandes de uma vez são exatamente o tipo de refatoração que
`gerard-consistencia-estado` pede pra não presumir como "melhoria" sem
confirmação.

## Regra determinística: Main compositora e roteadora

> A Main deve progressivamente se tornar uma compositora e roteadora, sem
> concentrar a mecânica particular de cada protocolo.

Essa direção possui um *ratchet* no verificador frequente
`scripts/verificar_regressao_gerard.py`:

- os tamanhos correntes de `mousePressed`, `mouseDragged`,
  `processarMovimentoArraste`, `mouseReleased`, `mouseClicked` e
  `mouseMoved` são limites máximos, e não metas permanentes;
- qualquer crescimento desses métodos falha deterministicamente;
- quando uma extração reduzir um método, o limite registrado deve ser
  reduzido na mesma alteração, impedindo a reintrodução posterior;
- a presença e a conexão dos handlers já extraídos continuam verificadas
  pelo mesmo script.

A contagem de linhas é apenas uma trava de regressão. Ela não demonstra
localidade correta por si só: toda alteração deve também verificar se Swing
ficou na fronteira de interação, se a mecânica ficou no handler e se a regra
do domínio permaneceu no proprietário semântico legítimo. O tamanho total de
`Main.java` não participa do ratchet, pois funcionalidades representacionais
novas podem aumentar a classe sem justificar o crescimento dos protocolos.

## Consequência para web e mobile

A extração também deve criar uma fronteira de portabilidade. Eventos e tipos
da plataforma terminam no adaptador: `MouseEvent` no desktop, `PointerEvent`
na web e gestos de toque no mobile. O protocolo recebe dados de interação
equivalentes e não componentes da plataforma.

A primeira prova desta fronteira na versão corrente é `LimitesMovimento`: a
geometria real do enunciado continua sendo calculada pelo objeto de
representação, mas `HandlerInteracaoElementoTextoMovel` recebe um intervalo
neutro, sem importar `java.awt.Rectangle` ou Swing. O padrão deve ser aplicado
incrementalmente; os componentes visuais Swing não são considerados
reutilizáveis.

O protocolo de conectores fornece a segunda prova: o handler portátil conhece
somente `AlvoMovelIncremental`, deltas e `LimitesMovimento`; a representação
Swing é alcançada pelo `AdaptadorMovimentoConectorVergnaud`. Uma versão web ou
mobile implementará outra porta sem herdar `ConectorVergnaud` nem `Rectangle`.

JSON ou XML podem futuramente serializar comandos e estados quando existir
uma fronteira externa real. O formato de transporte pertence à infraestrutura
e não substitui os objetos semanticamente ricos nem a sintaxe própria de cada
sistema de representação.

## Relação com outras skills

- `gerard-scaffolding-interacao` decide **o quê** cada protocolo de mouse
  deve fazer (cor, tremor, som, quando disparar erro); esta skill decide
  **onde** esse código mora. As duas convivem — um handler extraído por
  esta skill ainda segue as regras de comportamento da outra.
- `gerard-posicionamento-relativo` continua valendo dentro de qualquer
  handler novo — nenhum handler deve introduzir número de pixel solto.
- `gerard-knowledge-locality-principle` lista 5 tipos de localidade
  (objeto, relacional, pedagógica, infraestrutura, epistemológica);
  interação de mouse/teclado não tem categoria própria ali hoje. Esta
  skill não altera essa lista — é uma oportunidade de revisão futura,
  não decidida aqui.

## Autorização e segurança

Cada nova extração continua exigindo autorização explícita e validação do
protocolo afetado. O ratchet autoriza somente a verificação frequente; ele
não autoriza refatorações automáticas nem substitui os testes funcionais e
Robot exigidos por `gerard-consistencia-estado`.
