---
name: gerard-log-gestos-interacao
description: Registra gestos físicos observáveis da interação no Gérard sem avaliação matemática, sem diagnóstico semântico e sem convertê-los automaticamente em ações instrumentais. Use ao criar, revisar ou alterar rastreamento de arrastar, soltar, clicar, orientar, caminho, coordenadas ou destino geométrico; especialmente quando uma interação pode terminar fora de qualquer elemento do diagrama.
---

# Log factual de gestos de interação

## Fronteira obrigatória

Um gesto é uma ocorrência física observável na interface. Uma ação
instrumental só existe quando a camada de interação consegue produzir um
comando com significado semântico.

- `ARRASTAR → POSICIONAR` sempre pode formar um gesto concluído.
- Soltar fora de qualquer elemento do diagrama produz destino geométrico
  `FORA_DE_ELEMENTO_DO_DIAGRAMA`, mas não produz ação instrumental.
- Soltar sobre um elemento pode constituir uma ação. O proprietário semântico
  produz sua validação e seu único registro instrumental. Nada disso pertence
  ao log de gestos.

Decisão da usuária em 2026-08-14: o registro do gesto pertence ao objeto rico
da representação participante. Esse objeto o produz a partir das observações
geométricas fornecidas pelo handler. Uma porta de infraestrutura pode
persisti-lo, mas não o avalia.

## Esquema mínimo

Registrar somente fatos observáveis:

- `gesture_id`;
- tipo do gesto;
- objeto e artefato manipulados como referências de interface;
- coordenadas inicial e final;
- resumo da trajetória: amostras, mudanças de orientação e distância;
- destino geométrico: sobre elemento, fora de elemento ou não classificado.

Não incluir `C/E`, diagnóstico, invariante inferido, papel de destino,
`action_id` ou `rejection_sequence_id`.

## Regras de implementação

1. Derivar destino e coordenadas da geometria real da representação; nunca
   usar pixels fixos nem inferir alvo sem hit-test.
2. Registrar o gesto independentemente de ele produzir ação.
3. Deixar o handler encerrar apenas o protocolo físico e entregar suas
   observações; ele não conhece o significado do destino.
4. Deixar a camada de interação reconhecer, pela geometria real, se existe
   um comando semântico e encaminhar somente comandos constituídos ao
   proprietário semântico.
5. Somente o proprietário semântico produz o registro instrumental e seu
   resultado factual.
6. Não usar ausência de ação como erro matemático ou rejeição pedagógica.

## Estado da versão correta

Na versão `git/Gerard`, a separação foi implementada incrementalmente para o
protocolo de `ItemTextoArrastavel`:

- `HandlerInteracaoItemTextoArrastavel` produz `ResumoGestoArraste` somente
  com observações físicas;
- `ItemTextoArrastavel`, como objeto rico da representação participante,
  produz `RegistroGestoInteracao`;
- `LoggerGestosInteracaoGerard` implementa uma porta de saída e grava
  `gerard_gestos_<sessão>.tsv`, sem `C/E`, diagnóstico ou identificador de
  ação;
- `TelaGerard` classifica o destino pela geometria real antes de qualquer
  centralização automática;
- soltar fora de elemento ou clicar sem deslocamento não chama
  `registrarLogSolturaItem`; uma soltura sobre elemento produz no máximo um
  registro instrumental, reutilizando a avaliação já calculada.

Os demais protocolos ainda usam o rastreador granular legado. Portanto, esta
é uma prova incremental verificada, não a conclusão da migração de todos os
gestos.

## Relações

- `gerard-handlers-de-interacao`: encerra o protocolo físico e entrega as
  observações à camada de interação, sem conhecer o destino semântico.
- `gerard-posicionamento-relativo`: fornece a geometria real do hit-test.
- `gerard-log-acao-instrumental`: recebe somente a ação constituída.
- `gerard-semantic-event-logging`: registra fatos semânticos posteriores, não
  eventos técnicos do mouse.
