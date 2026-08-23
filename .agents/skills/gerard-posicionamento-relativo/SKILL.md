---
name: gerard-posicionamento-relativo
description: Regra de arquitetura para posicionamento de qualquer elemento na interface do Gérard (coordenadas, limites de arraste/clamp, offsets, áreas de clique ou de desenho) — deve ser derivado da geometria real do contêiner ou elemento relacionado, nunca um número de pixel fixo copiado ou adivinhado. Use sempre que for escrever, revisar ou corrigir código de posicionamento, limites de arraste, hit-test, ou layout em Main.java/TelaGerard — especialmente ao adicionar um novo painel, mudar o tamanho ou a posição de um existente, investigar um bug de sobreposição/elemento fora do lugar, ou quando encontrar um número de pixel "solto" (sem constante nomeada) numa conta de coordenada.
---

# Posicionamento relativo ao contêiner — Gérard

## A regra

Um elemento nunca deve ter sua posição, limite de arraste, ou área de interação definidos por um número de pixel fixo que *coincide* com a geometria de outro elemento — deve ser derivado *daquela* geometria, direto. Coincidência hoje é divergência amanhã: no dia em que o outro elemento mudar de tamanho ou posição, todo número fixo que só "parecia certo" para de bater, silenciosamente, sem erro de compilação e sem teste automatizado que pegue.

Isso vale para qualquer elemento — painel, card, diagrama, palavra arrastável, quadradinho, círculo. Não é uma regra só sobre o enunciado ou sobre texto.

## Caso concreto que motivou esta skill (2026-08-06)

`Main.java` tinha duas cópias independentes da altura vertical da área do enunciado:

- `estaNaAreaDoTexto(x, y)`: `y >= 55 + ALTURA_PAINEL_ATALHOS_CATEGORIA && y <= 190 + ALTURA_PAINEL_ATALHOS_CATEGORIA` — corretamente amarrada à constante.
- O clamp de arraste de palavra em `processarMovimentoArraste` (ramo `elementoTextoSelecionado`): `int limiteSuperior = 58 + altura; int limiteInferior = 184;` — **sem** a constante.

`ALTURA_PAINEL_ATALHOS_CATEGORIA` cresceu de 110 para 130 em 2026-07-28 (decisão da usuária, para abrir espaço aos rótulos "Medidas"/"Relações" acima dos ícones — comentário no próprio código, linha ~7420). `estaNaAreaDoTexto` acompanhou porque referenciava a constante. O clamp de arraste não acompanhou porque tinha 58/184 soltos — visualmente pareciam próximos de 55/190 (a diferença nem chamava atenção lendo o código), mas eram uma cópia independente, calculada uma vez e nunca mais revisitada.

Resultado, um mês depois: arrastar uma palavra do enunciado para cima deixava ela presa sobrepondo a barra de ícones — porque o limite parava ~130px abaixo de onde deveria. Ver `RELATORIO_BUG_LIMITE_SUPERIOR_ARRASTE_TEXTO_ENUNCIADO_2026-08-06.md` e os commits `e89e718`/`cfe582b`.

A correção final não foi só somar a constante que faltava (isso foi a primeira tentativa, `e89e718` — ainda preservava dois números "mágicos" por trás). Foi eliminar a segunda cópia: `TOPO_AREA_ENUNCIADO`/`BASE_AREA_ENUNCIADO`, duas constantes novas, viraram a única fonte, usadas tanto em `estaNaAreaDoTexto` quanto no clamp (`cfe582b`). Os mesmos números (55/190) que já definiam a área real desenhada por `desenharCard(g2, 15, 55 + ALTURA_PAINEL_ATALHOS_CATEGORIA, getWidth() - 30, 135, 18)`.

## Exemplos do padrão correto já no código

`ALTURA_PAINEL_ATALHOS_CATEGORIA` é o exemplo positivo — uma constante única referenciada em ~20 pontos (`Y_BASE_VERGNAUD = 215 + ALTURA_PAINEL_ATALHOS_CATEGORIA`, `Y_BASE_VENN`, posições de botões, áreas de card, `desenharFaixaAtalhoCategoria`). Quando ela muda, todo mundo que depende dela muda junto, automaticamente. É esse padrão que faltava replicar para a área do enunciado antes da correção de hoje.

## Perguntas obrigatórias antes de escrever um número de pixel

1. Este valor depende do tamanho ou posição de outro elemento (painel, barra, card, diagrama)? Se sim: existe uma constante nomeada ou um método que já expõe essa geometria? Use-o.
2. Se esse outro elemento mudar de tamanho no futuro, quantos lugares no código precisariam ser lembrados e atualizados manualmente? Se a resposta é "mais de um", é sinal de que a fonte deveria ser única — extraia uma constante ou método, não copie o número.
3. Este número "parece" bater com outro já existente no código (ex.: 58 perto de 55, 184 perto de 190)? Proximidade não é evidência de que a conta está certa — é sinal de que alguém já derivou esse valor antes e você está reconstruindo (pior: divergindo) em vez de reaproveitar.
4. Estou copiando um valor de outro trecho sem entender de onde ele vem? Ache a fonte real (o método que desenha/define aquela área) antes de reusar o número.

## Onde procurar ao investigar um bug de posição/sobreposição

Grep pelo nome da constante relevante (`ALTURA_PAINEL_ATALHOS_CATEGORIA` e afins) para ver todos os pontos que já dependem dela — se o código com bug NÃO aparece nessa lista mas deveria (porque está na mesma área visual), esse é frequentemente o próprio bug: uma cópia solta que ficou de fora quando a constante foi introduzida ou alterada.
