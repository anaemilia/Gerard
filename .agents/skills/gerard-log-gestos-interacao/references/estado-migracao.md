# Estado da migração do log de gestos

Leia esta referência ao continuar ou auditar a separação entre gesto físico e
ação instrumental. Ela descreve a prova incremental existente, não amplia o
esquema factual da skill.

Na versão `git/Gerard`, a separação foi implementada para o protocolo de
`ItemTextoArrastavel`:

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

Os demais protocolos ainda usam o rastreador granular legado. Esta é uma
prova incremental verificada, não a conclusão da migração de todos os gestos.
