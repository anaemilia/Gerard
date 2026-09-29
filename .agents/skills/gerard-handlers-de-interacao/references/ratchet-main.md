# Ratchet de Main compositora e roteadora

Leia esta referência ao alterar métodos de eventos de `Main`, limites de
tamanho ou verificações da presença e conexão dos handlers.

`scripts/verificar_regressao_gerard.py` protege a direção arquitetural:

- os tamanhos correntes de `mousePressed`, `mouseDragged`,
  `processarMovimentoArraste`, `mouseReleased`, `mouseClicked` e `mouseMoved`
  são limites máximos, não metas;
- crescimento desses métodos falha deterministicamente;
- quando uma extração reduz um método, o limite deve ser reduzido na mesma
  alteração;
- a presença e a conexão dos handlers extraídos continuam verificadas.

A contagem de linhas não demonstra localidade correta. Verifique também que
Swing ficou na fronteira, a mecânica no handler e a regra do domínio no
proprietário semântico. O tamanho total de `Main.java` não participa do
ratchet, pois novas funcionalidades representacionais podem aumentar a classe
sem justificar crescimento dos protocolos de interação.
