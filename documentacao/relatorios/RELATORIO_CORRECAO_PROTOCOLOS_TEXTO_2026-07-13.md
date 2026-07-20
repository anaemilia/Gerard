# Correção dos protocolos por elemento semântico

Base: versão com fotografia recortada do estado atual da modelagem.

Alterações:

- elementos semânticos oriundos do enunciado passam a manter a classificação de texto durante toda a condução;
- o identificador real do elemento (`6`, `8`, `?`, etc.) é preservado no rastreamento;
- um deslocamento efetivo gera `CAMINHO` e `POSICIONAR` mesmo quando o sistema operacional entrega poucas amostras de `mouseDragged`;
- a movimentação de elemento semântico textual gera também `TEXTO`;
- `ORIENTACAO` continua sendo registrada somente quando há direção observável no percurso;
- a tela de explicações continua lendo os protocolos do arquivo único de interação e agrupando por `tentativa_id + elemento_semantico`.

Sequência esperada, conforme os eventos reais:

`SELECIONAR -> ORIENTACAO -> CAMINHO -> POSICIONAR -> TEXTO`

A ausência de mudança de direção pode omitir `ORIENTACAO`, mas não deve omitir `CAMINHO`, `POSICIONAR` e `TEXTO` quando houve deslocamento.

Verificação:

- compilação Ant: BUILD SUCCESSFUL;
- 96 arquivos Java compilados;
- nenhum preenchimento automático do diagrama foi introduzido;
- estado da modelagem, fotografia, curadoria e idiomas foram preservados.
