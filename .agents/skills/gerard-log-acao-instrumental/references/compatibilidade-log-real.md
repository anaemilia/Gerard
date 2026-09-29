# Compatibilidade com o log real

Leia esta referência ao comparar o Quadro 4.55 com `EventoLogGerard`, alterar
o TSV ou preservar compatibilidade com arquivos anteriores. A comparação foi
verificada em 2026-07-20.

- A tarefa de interação já usa no código real os termos de Shneiderman:
  `SELECIONAR`, `ORIENTACAO`, `CAMINHO`, `POSICIONAR`, `TEXTO` e
  `QUANTIFICAR`.
- O log real contém os campos equivalentes aos do Quadro 4.55 e acrescenta
  contexto de sessão, situação, idioma, categoria, enunciado, origem,
  detalhes, propriedade, mudança observável, natureza e efeito da ação.
- O campo único de invariantes do quadro foi decomposto em
  `invariante_origem`, `invariante_codigo`, `invariante_simbolico` e
  `invariante_observacao`. Isso é uma granularidade maior, não uma lacuna.
- Não foi verificado se `usuario` usa em runtime um identificador numérico
  equivalente ao exemplo “04”; não presumir esse formato.

Ao estender o TSV, acrescentar campos ao final e preservar a leitura de logs
antigos, seguindo a convenção já usada em `EventoLogGerard.deTsv()`.
