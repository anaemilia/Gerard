# Correção dos protocolos na tela de explicações

A tela de explicações agora agrupa os protocolos reais registrados no log pela chave lógica:

- `tentativa_id`
- `elemento_semantico`

Foi corrigida a incompatibilidade entre a coluna técnica `objeto=OBJ_INTERACAO` e o valor semântico preservado no campo `detalhes`.

A leitura passa a recuperar o elemento pelas chaves, nesta ordem:

1. `valor=...`
2. `objeto=...`
3. `texto=...`

Também foi incluído o evento granular `SELECIONAR` quando um marcador semântico do enunciado é convertido em item manipulável.

Os protocolos continuam vindo dos eventos reais:

- pressionar/selecionar o elemento: `SELECIONAR`
- mudanças de direção durante o arraste: `ORIENTACAO`
- trajetória registrada: `CAMINHO`
- soltura/posição final: `POSICIONAR`

A correção é retrocompatível com eventos já registrados na tentativa atual, porque a tela interpreta o valor semântico existente em `detalhes`, sem inferir pela posição final.

Compilação Ant: `BUILD SUCCESSFUL`.
