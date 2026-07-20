# C150 — Funil estrutural e correção do questionamento semântico

- O funil superior passa a ser desenhado como estrutura neutra quando a transformação ainda é desconhecida ou vale zero.
- Os controles `+` e `−` da transformação foram reposicionados junto ao funil ativo, sem permanecerem soltos no topo do painel.
- Antes do primeiro posicionamento no diagrama de Vergnaud, os controles continuam visíveis, porém desabilitados, preservando a regra consolidada.
- O primeiro clique em `+` ou `−`, após a liberação da modelagem, define o sentido da transformação e materializa os quadradinhos no funil correspondente.
- O questionamento semântico persistente agora é removido assim que o item é reposicionado no papel correto.
- O valor do estado final é aceito no estado final sem manter o tip de erro de uma tentativa anterior.
- As demais regras de sincronização, não negatividade dos estados, valor inteiro da transformação, arraste, feedback, conclusão, curadoria e construção foram preservadas.
