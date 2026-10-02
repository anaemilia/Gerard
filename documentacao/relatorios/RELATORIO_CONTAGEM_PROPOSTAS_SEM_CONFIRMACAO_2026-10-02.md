# Três propostas erradas, sem contar Sim/Não

Decisão explícita da usuária em 2026-10-02: o limite deve aparecer na terceira proposta de valor errada, sem consumir tentativa pela confirmação da mesma resposta.

`PapelQuantitativo.registrarRespostaConfirmacaoValorRejeitado` preserva o contador e o bloqueio existentes. Continua produzindo evento e identidade próprios para confirmação/retirada, correlacionados à sequência. `IncognitaQuantitativa` conserva o registro factual e seus diagnósticos; a mudança não envolve confirmação de categoria nem contagem por gestos.

Verificado: compilação Java completa; teste de confirmação e retirada sem incremento; teste de tentativas, identidade, bloqueio e restauração; fluxo P4.1 com 12 verificações; grafo de skills. Navegador por mouse/teclado: 1 errado → Sim mantém 1 → 2 errado sem limite → Sim mantém 2 → 3 errado com limite e feedback persistente → 5 correto conclui. Fluxo Não, Enter repetido e recuperação de falha HTTP também passou.

Vídeo fornecido pela usuária, `2026-10-02 15-11-14.mp4`: a comparação Claudenice/Nádia mostra 20 errado e depois 21 com material liberado; após restaurar, aparecem 2 e depois 12 com material liberado. Quadros extraídos em `tmp/video-desktop-20261002`. A sessão desktop observada executa `out/production/Gerard` pelo IntelliJ; precisa recompilar e reiniciar para carregar código alterado. A gravação é evidência do comportamento anterior, não validação gráfica da correção no desktop.
