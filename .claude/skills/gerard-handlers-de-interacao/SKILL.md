---
name: gerard-handlers-de-interacao
description: Direção arquitetural (ainda não implementada) para separar o código de manipulação de mouse/teclado (arrastar, soltar, hover, clique) dos elementos representacionais e de TelaGerard, hoje concentrado ali. Use ao planejar como extrair handlers de interação de Main.java, ao decidir onde colocar um novo protocolo de arraste/clique, ou quando a tela estiver difícil de navegar por causa de lógica de mouse. Registrada em 2026-08-07 a partir de uma discussão externa com a usuária — não confundir com gerard-scaffolding-interacao (que decide o que cada protocolo faz, não onde o código mora).
---

# Handlers de interação modulares — direção (não implementada)

## Dependência normativa

Leia `gerard-semantic-model/REFERENCE.md` e `gerard-domain-model-first`
antes de aplicar esta skill. Esta skill não redefine nada das duas —
ela propõe uma implementação concreta para a camada "Interação" que
`gerard-domain-model-first` já separa de Domínio e Representação, mas
sem prescrever como estruturar isso em código.

## Status: proposta registrada, não implementada

`TelaGerard` (`Main.java`) implementa `MouseListener`, `MouseMotionListener`
e `KeyListener` diretamente (`Main.java:466`). `mousePressed` sozinho tem
~366 linhas (`Main.java:10462`–`10828`), despachando por tipo de elemento
(`ItemTextoArrastavel`, `ElementoTextoMovel`, quadradinhos do diagrama
Venn, eixo de inteiros, elementos de Vergnaud...) dentro do mesmo método;
`mouseDragged` (`10828`), `mouseReleased` (`10950`) e `mouseMoved`
(`12843`) seguem o mesmo padrão. Nada disso muda por este registro — é
só o diagnóstico que motiva a proposta abaixo.

## Por que isso não contradiz a arquitetura já registrada

`gerard-domain-model-first` já define a Interação como camada distinta
de Domínio e Representação, responsável por "conversão de gestos em
comandos semanticamente identificados" e "distinção da origem da
ação" — mas nunca disse *onde* esse código deveria morar. Esta skill
preenche essa lacuna com um padrão concreto, sem alterar nenhuma regra
das outras skills: `PapelQuantitativo` e as classes `RelacaoEstrutural*`
continuam no Domínio, sem saber de mouse, pixel ou Swing — muda só onde
o despacho de eventos de mouse é organizado.

## O padrão proposto

1. **Elemento representacional** (`ItemTextoArrastavel`, `ElementoTextoMovel`,
   `ElementoVergnaud`) continua rico, mas sem código de mouse — só estado
   e como se desenha, exatamente como já é hoje.
2. **Um handler de interação por tipo de elemento** concentra a lógica
   hoje espalhada entre `mousePressed`/`mouseDragged`/`mouseReleased`/
   `mouseMoved` para aquele tipo específico — decide o que o gesto
   significa (iniciar arraste, soltar sobre um alvo, hover) e chama o
   objeto de domínio ou de sincronização (`EstadoSemanticoCompartilhado`,
   os `RelacaoEstrutural*`) correspondente.
3. **`TelaGerard` roteia**, não decide — recebe o evento bruto do Swing e
   repassa pro handler certo, em vez de conter a lógica de decisão
   inline.

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

`mousePressed` com 366 linhas concentra risco alto para uma mudança só;
extrações grandes de uma vez são exatamente o tipo de refatoração que
`gerard-consistencia-estado` pede pra não presumir como "melhoria" sem
confirmação.

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

## Autorização

Registro normativo apenas. Nenhum código muda em função deste documento.
A extração de `Main.java` é trabalho grande e arriscado, na mesma tela
que `gerard-consistencia-estado` documenta como tendo comportamento já
validado em produção — só deve começar com autorização explícita e um
plano de verificação, do mesmo jeito que as outras mudanças de
arquitetura desta sessão (Fase B1, Fase B2, N=3 tentativas) foram
confirmadas antes de implementar.
