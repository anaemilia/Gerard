# Acionamento independente do acervo

Decisão explícita da usuária em 2026-10-02: se há número relativo, há historinha após três rejeições. A disponibilidade de conteúdo não pode interferir na tomada de decisão; historinhas ausentes serão resolvidas depois.

O proprietário da política continua sendo o domínio. `TipoSituacaoAditiva.deveAcionarHistorinha` recebe ocorrência de número relativo e escalada no limite, devolvendo a decisão sem consultar o repertório. A projeção web publica `ajuda_visual_acionada` separadamente de `ajuda_visual`. Swing seleciona a representação a partir da ocorrência semântica de número relativo; os adaptadores apenas materializam conteúdo disponível da própria categoria.

Não foram criadas nem emprestadas historinhas. Ausência de conteúdo não revoga a decisão já tomada.

Verificações: compilação Java completa com source/target 8; build TypeScript/Vite; decisão e repertório nas 42 situações curadas; processo de transformação com 36 verificações; grafo de skills válido. Teste real por mouse/teclado em Lucas: valor errado 1, Sim, valor errado 2; terceira rejeição mantém `ajuda_visual_acionada=true` com acervo vazio; resposta correta 5 conclui. O teste prolongado de valores subsequentes apresentou instabilidade de sincronização do editor; a sequência focal da terceira rejeição até conclusão passou integralmente.
