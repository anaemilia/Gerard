---
name: gerard-tela-via-gerador-de-cena
description: Regra de arquitetura — tudo o que aparece na tela do diagrama do Gérard passa pelo gerador de cena. Os objetos ricos de domínio decidem (conteúdo, presença, estado); o gerador de cena apenas projeta essas decisões numa cena; os adaptadores (Swing, React) realizam a cena. Use sempre que for adicionar, mudar ou corrigir qualquer coisa visível na tela do diagrama, ou ao ver Main.java/React atribuindo conteúdo, presença ou estado de um elemento por conta própria, ou o gerador decidindo regra.
---

# Tela passa pelo gerador de cena; quem decide é o objeto rico

## A regra

Três papéis distintos, nesta ordem:

1. **Objetos ricos de domínio decidem.** O proprietário semântico (papel,
   relação estrutural, tentativa, situação) decide *o que* deve aparecer:
   conteúdo, presença, estado, apoio pedagógico. Ver
   `gerard-knowledge-locality-principle` e `gerard-domain-model-first`.
2. **O gerador de cena só renderiza.** Recebe as decisões dos objetos ricos e as
   *projeta* numa cena (`CenaDiagramaAditivo`, `CenaDiagramaVenn`). Não calcula
   regra matemática, semântica nem pedagógica; não escolhe ajuda; não decide
   se algo aparece. Traduz decisões em descritores abstratos de apresentação.
3. **Os adaptadores realizam a cena.** Swing (desktop) e React (web) leem a cena
   e calculam a geometria concreta em pixels. Não decidem conteúdo, presença
   nem estado.

Se é tela, passa pelo gerador de cena. Se é regra, pertence a um objeto rico.

## Onde está

- Diagrama de Vergnaud: `GeradorCenaDiagramaAditivo`
  (`src/gerard/campoaditivo/diagrama/servico/`) → `CenaDiagramaAditivo`
  (`FiguraDiagrama`, `ConectorDiagrama`, narrativa, estado de feedback).
- Diagrama complementar (Venn): `GeradorCenaDiagramaVenn`
  (`src/gerard/campoaditivo/venn/servico/`) → `CenaDiagramaVenn`.
- Consumidores: `Main.java` (desktop) e `ServicoSorteioAtividadeWeb` /
  `ProjetorAjudaVisualWeb` (web) leem a mesma cena e as mesmas decisões.

## Como acrescentar um recurso visual

1. **Decisão no objeto rico proprietário.** Se o recurso depende de uma regra
   (limite de rejeições, papel relativo, conclusão), ela já está, ou deve ser
   criada, no objeto que possui esse conhecimento (ex.:
   `TentativaModelagemAditiva.estaNoLimiteAjudaVisual()`).
2. **Campo ou descritor abstrato na cena** para carregar o resultado (ex.:
   `FiguraDiagrama.getSubtitulo()`, `PosicaoRotuloFigura.ACIMA`).
3. **O gerador projeta**, por parâmetro, o que o objeto rico informou, num método
   que devolve nova cena imutável (padrão: `comFeedback`,
   `comElementosTextoNarrativa`). O gerador não consulta o domínio para decidir.
4. **O adaptador apenas lê a cena e desenha.** Geometria concreta em pixels fica
   nele, derivada da geometria real (ver `gerard-posicionamento-relativo`).

## O que não pode acontecer

- `Main.java` ou o React **decidirem** conteúdo, presença ou estado de um
  elemento (ex.: atribuir o subtítulo de uma figura buscando o personagem por
  conta própria; calcular localmente se um painel de ajuda aparece).
- O **gerador decidir regra** (matemática, semântica, pedagógica). Ele só recebe
  o resultado já decidido pelo objeto rico.
- Duas fontes para o mesmo elemento visual (uma na cena, outra no adaptador).
- Corrigir só no adaptador um defeito cuja causa é conteúdo, ordem ou presença:
  a correção está no objeto rico que decide ou na projeção da cena.

O que **pode** ficar no adaptador: pixels, fonte, espaçamento entre linhas
derivado das métricas, animação, cursor, foco, efeitos de interação — a
realização de algo que a cena já trouxe.

## Perguntas antes de mexer na tela

1. Estou mudando *quem decide* algo (regra) ou só como a cena *mostra* o que já
   foi decidido? A regra vai para o objeto rico; a projeção, para o gerador.
2. Desktop e web mostrariam o mesmo resultado? Se a regra existe só em um
   adaptador, ela está no lugar errado.
3. O gerador está calculando algo que um objeto rico deveria informar?
4. O adaptador continua desenhando só a partir da cena depois da mudança?

## Verificação

- Teste sem Swing e sem HTTP: o objeto rico decide; o gerador projeta; a cena
  traz o resultado (ex.: o subtítulo de cada figura).
- Robot (desktop) ou clique real (web) confirma que a cena foi realizada.
- Procurar atribuições de conteúdo fora da cena: campos de `ElementoVergnaud`
  (`rotulo`, `subtitulo`, `rotulosAcima`) atribuídos por `Main.java` devem vir de
  `FiguraDiagrama`.

## Dívida conhecida ao registrar esta skill (2026-10-02)

- `Main.aplicarSubtitulosPersonagensNoDiagramaVergnaud` busca o personagem em
  `SemanticaCuradaSituacao` e o atribui ao elemento sem passar pela cena.
- `Main.deveExibirDiagramaComplementar` combina o bloqueio local do papel com o
  seletor; a decisão deveria vir pronta do objeto rico (a tentativa) e a cena
  apenas projetá-la.

## Fontes relacionadas

- `gerard-domain-model-first` e `gerard-knowledge-locality-principle`: quem
  decide é o objeto rico proprietário do conhecimento.
- `gerard-api-semantica`: a cena e as decisões são publicadas para web e mobile;
  o cliente só materializa.
- `gerard-posicionamento-relativo`: a geometria concreta deriva da geometria real.
- `gerard-consistencia-estado` e `gerard-identidade-visual`.
