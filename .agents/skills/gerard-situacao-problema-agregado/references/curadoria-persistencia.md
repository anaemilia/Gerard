# Curadoria e persistência da situação-problema rica

Leia esta referência ao alterar a ponte tabular, o sidecar, o editor de
curadoria, o status editorial ou o compartilhamento da narrativa por traduções.

A ponte do registro tabular para o agregado rico é um adaptador de curadoria,
não uma responsabilidade de `SituacaoProblema`. Ela pode transportar valores,
incógnita e operações já declarados, mas deve exigir à parte a narrativa e as
correspondências nominais. Campo obrigatório ausente, operação não curada ou
conflito entre `termo_desconhecido` e `?` interrompe a construção com
diagnóstico. A marca `validada` do registro anterior não promove
automaticamente a nova representação rica.

A narrativa e as correspondências ricas devem ser persistidas em um sidecar
XML versionado próprio da curadoria. Esse registro pode referenciar a situação
tabular pelo identificador, mas não pode reconstruir participantes, objetos,
eventos ou orientações a partir de `personagem_*`, da ordem das colunas, do
enunciado ou da geometria. A serialização é responsabilidade de infraestrutura;
os objetos de domínio permanecem independentes de XML, JSON, arquivos e Swing.
Quando o registro rico estiver ausente, a conversão deve parar com diagnóstico
factual, sem recorrer a uma inferência de compatibilidade.

O editor Swing é somente um adaptador de entrada das declarações humanas. Ele
expõe campos nominais para participantes, famílias, objetos, inventários,
eventos, marcadores temporais e correspondências, mas delega a montagem a um
componente independente da interface. Linhas, colunas e posições do formulário
não possuem significado semântico; todos os vínculos usam identificadores
declarados. Uma declaração completa mas divergente pode ser preservada como
candidata, acompanhada dos diagnósticos e de confirmação explícita do
pesquisador, sem correção silenciosa.

A versão original é a proprietária do sidecar semântico. Traduções alteram a
realização textual e referenciam a narrativa rica da original por
`versao_origem_id`; elas não criam participantes, objetos, eventos ou
correspondências paralelos.

O status editorial pertence ao registro da representação rica e começa como
`CANDIDATA_NAO_CURADA`. A marca `validada` da tabela histórica não o promove.
Somente um ato explícito do pesquisador no editor pode registrar
`VALIDADA_PELO_PESQUISADOR`, e essa promoção é bloqueada enquanto a conversão
produzir qualquer diagnóstico. Sidecars anteriores, que não possuem o status,
são lidos conservadoramente como candidatos.
