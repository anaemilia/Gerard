---
name: gerard-posicionamento-relativo
description: Regra de arquitetura para posicionamento de qualquer elemento na interface do Gérard (coordenadas, limites de arraste/clamp, offsets, áreas de clique ou de desenho) — deve ser derivado da geometria real do contêiner ou elemento relacionado, nunca um número de pixel fixo copiado ou adivinhado. Use sempre que for escrever, revisar ou corrigir código de posicionamento, limites de arraste, hit-test, ou layout em Main.java/TelaGerard — especialmente ao adicionar um novo painel, mudar o tamanho ou a posição de um existente, investigar um bug de sobreposição/elemento fora do lugar, ou quando encontrar um número de pixel "solto" (sem constante nomeada) numa conta de coordenada.
---

# Posicionamento relativo ao contêiner — Gérard

## A regra

Um elemento nunca deve ter sua posição, limite de arraste, ou área de interação definidos por um número de pixel fixo que *coincide* com a geometria de outro elemento — deve ser derivado *daquela* geometria, direto. Coincidência hoje é divergência amanhã: no dia em que o outro elemento mudar de tamanho ou posição, todo número fixo que só "parecia certo" para de bater, silenciosamente, sem erro de compilação e sem teste automatizado que pegue.

Isso vale para qualquer elemento — painel, card, diagrama, palavra arrastável, quadradinho, círculo. Não é uma regra só sobre o enunciado ou sobre texto.

## Caso concreto que motivou esta skill (2026-08-06)

Para o histórico, a correção aplicada e o procedimento de investigação, leia
[`references/caso-area-enunciado.md`](references/caso-area-enunciado.md).

## Perguntas obrigatórias antes de escrever um número de pixel

1. Este valor depende do tamanho ou posição de outro elemento (painel, barra, card, diagrama)? Se sim: existe uma constante nomeada ou um método que já expõe essa geometria? Use-o.
2. Se esse outro elemento mudar de tamanho no futuro, quantos lugares no código precisariam ser lembrados e atualizados manualmente? Se a resposta é "mais de um", é sinal de que a fonte deveria ser única — extraia uma constante ou método, não copie o número.
3. Este número "parece" bater com outro já existente no código (ex.: 58 perto de 55, 184 perto de 190)? Proximidade não é evidência de que a conta está certa — é sinal de que alguém já derivou esse valor antes e você está reconstruindo (pior: divergindo) em vez de reaproveitar.
4. Estou copiando um valor de outro trecho sem entender de onde ele vem? Ache a fonte real (o método que desenha/define aquela área) antes de reusar o número.
