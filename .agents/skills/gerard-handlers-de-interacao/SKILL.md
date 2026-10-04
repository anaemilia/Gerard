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

Ao continuar ou auditar a extração, leia
[`references/status-extracao.md`](references/status-extracao.md). O progresso
é medido pela retirada de mecânica particular dos métodos centrais, não pelo
total bruto de linhas de `Main.java`.

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

## Gesto não é ação instrumental

Decisão da usuária em 2026-08-11: `ARRASTAR → POSICIONAR` delimita o
protocolo físico do gesto, mas não basta para constituir uma ação
instrumental. Se a soltura ocorrer fora de qualquer elemento do diagrama, o
gesto termina com destino geométrico `FORA_DE_ELEMENTO_DO_DIAGRAMA`; não há
comando semântico, avaliação C/E nem rejeição pedagógica. Somente um destino
semanticamente identificável permite à camada de interação produzir um
comando que o proprietário semântico poderá avaliar.

O schema físico pertence a `gerard-log-gestos-interacao`. O objeto rico da
representação participante produz o registro factual do gesto a partir das
observações fornecidas pelo handler; uma porta apenas o persiste. O handler
não se torna proprietário desse registro e não envia um gesto sem comando ao
log de ação instrumental.

## Roteiro incremental sugerido

O procedimento, as fases verificadas e os riscos ainda abertos estão em
[`references/status-extracao.md`](references/status-extracao.md). Cada nova
fase continua exigindo autorização explícita e validação por comportamento.

## Regra determinística: Main compositora e roteadora

> A Main deve progressivamente se tornar uma compositora e roteadora, sem
> concentrar a mecânica particular de cada protocolo.

Essa direção possui um ratchet determinístico. Ao alterar métodos de eventos
de `Main`, limites de tamanho ou verificações da conexão dos handlers, leia
[`references/ratchet-main.md`](references/ratchet-main.md). A contagem de
linhas é somente trava de regressão e nunca prova localidade correta.

## Consequência para web e mobile

A extração também deve criar uma fronteira de portabilidade. Eventos e tipos
da plataforma terminam no adaptador: `MouseEvent` no desktop, `PointerEvent`
na web e gestos de toque no mobile. O protocolo recebe dados de interação
equivalentes e não componentes da plataforma.

Ao portar ou revisar adaptadores concretos, leia as provas atuais em
[`references/portabilidade.md`](references/portabilidade.md). Elas demonstram
o contrato, mas não limitam futuras plataformas às classes já existentes.

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
- `gerard-log-gestos-interacao` possui o schema factual do gesto. O handler
  encerra o protocolo físico e fornece observações, enquanto o objeto rico da
  representação produz o registro e a infraestrutura somente o persiste.
- `gerard-log-acao-instrumental` recebe apenas comandos semanticamente
  constituídos. O proprietário semântico produz um único registro por ação,
  ainda que vários objetos participem dela.
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

## Decisão de fase não mora na Main (nem espalhada)

Saber em que fase está a atividade (primeira modelagem, conclusão, exploração) é conhecimento do proprietário semântico da tentativa (`TentativaModelagemAditiva`), publicado por uma referência estável (`TentativaCorrente`: `admiteRegistroFactual()`, `admiteExploracao()`). Logger, publicador de gestos, estado compartilhado e sincronizador do enunciado recebem **referências de método** dessa porta; a `Main` apenas as liga (`tentativaCorrente::admiteExploracao`) e informa a tentativa corrente ao carregar a situação. É proibido escrever lambdas ou classes anônimas com `tentativa… != null && …estaEncerradaPorConclusao()` na `Main` ou em qualquer cliente: cada cópia dessa decisão é uma decisão pulverizada. Há duas fases (`FaseDaTentativa`): **modelagem** (dados curados e papéis preenchidos não mudam; tudo é registrado) e **exploratória** (começa no diagrama azul; recalcula para manter as representações consistentes; nada é registrado). Reiniciar ou sortear começa uma nova modelagem. A conclusão observada por qualquer representação passa por um único ponto, `TentativaModelagemAditiva.encerrarSeConcluida(FonteDeConclusao)`; cada plataforma só implementa como observa o azul.
