# Tentativa por situação

A tela **Análise da modelagem** passou a exibir a ordem da tentativa para o mesmo usuário e a mesma situação-problema, em vez do identificador técnico completo.

## Regra

- chave principal: usuário + `situacao_grupo_id`;
- fallback: usuário + `situacao_versao_id`;
- último fallback: usuário + enunciado;
- a contagem considera tentativas registradas nos arquivos `gerard_interacao_*.tsv`;
- o identificador técnico continua preservado no log;
- a nova coluna `tentativa_numero_situacao` foi acrescentada ao final do log, preservando a ordem das colunas anteriores.

## Exibição

`Tentativa nesta situação: 1`, `2`, `3` etc.

## Verificação

- compilação Ant: `BUILD SUCCESSFUL`;
- compatibilidade de leitura com logs anteriores preservada;
- português, inglês, francês e espanhol atualizados.
