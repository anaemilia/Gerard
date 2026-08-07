# Material concreto (diagrama complementar) só na última opção da escalada — item 5

Data: 2026-08-07. Pedido explicitamente: "os diagramas que já aparecem ao
lado do diagrama de Vergnaud [...] hoje eles sempre estão presentes, com a
escalada do erro, eles aparecerão quando for a última opção."

## O que mudou

Antes: `SeletorRepresentacaoComplementar.deveExibir(categoriaSelecionada, tipo)`
mostrava o diagrama complementar (quadradinhos do Venn/Composição, barras
da Comparação, painel de processo da Transformação, e a variante genérica
das 3 categorias de Relações) sempre que uma categoria estava selecionada
e havia um tipo de situação — ou seja, sempre, durante toda a modelagem.

Depois: `deveExibir` ganhou um terceiro parâmetro,
`escaladaDeAjudaNoLimite` — o material concreto só aparece quando a
escalada de Scaffolding da incógnita atual chegou à última opção (3ª
tentativa rejeitada consecutiva, o mesmo estado que já disparava a tela de
ajuda do item 4 — AG_EMCME no repertório do item 5). `Main.deveExibirDiagramaComplementar()`
passa a computar isso a partir do estado que já existia
(`tentativasIncognitaAtual.estaBloqueadoPorLimiteTentativas()`), sem novo
campo de estado sticky: some de novo automaticamente ao clicar
"Restaurar" (que já zera esse bloqueio), como pedido.

Único ponto de chamada (`Main.deveExibirDiagramaComplementar()`) — os ~10
lugares que dependiam dele (renderização, hit-test de adicionar/remover
quadradinho, botão de ajuda contextual, layout do divisor entre
diagramas) não precisaram de nenhuma mudança própria.

## Bug encontrado e corrigido durante a implementação

`garantirTentativasIncognitaAtual` cacheava a instância de contagem de
tentativas só pelo nome do papel (`"papel.todo"`, `"papel.referido"`
etc.), sem o id da situação-problema. Duas situações diferentes podem ter
incógnitas com o mesmo nome de papel — nesse caso, uma situação nova
herdava o bloqueio de tentativas da situação anterior: a primeira
tentativa real do participante na situação nova já nasceria bloqueada,
sem nunca ter sido avaliada. Esse bug já existia antes desta mudança (é
do mecanismo de N=3 tentativas em si), só ficou com efeito visível agora
que a visibilidade do diagrama complementar depende do mesmo estado.
Corrigido: a chave agora é `papel + "@" + id_da_situacao`.

## Verificação

- Compilação completa: 436 arquivos, 0 erros.
- `TesteComparativoEstadoSemanticoCompartilhado`: 40 cenários, 0
  divergências (não afetado por esta mudança, rodado por precaução).
- Suíte temporária dedicada, sob Xvfb, aplicação real, sorteando uma
  situação real da curadoria (criada, rodada, deletada, nunca commitada):
  usa reflection só para invocar métodos privados de `TelaGerard`
  (`sortearSituacaoMedidas`, `confirmarCategoriaAdivinhada`,
  `registrarTentativaIncognita`, `restaurarTentativasIncognitaAtual`,
  `deveExibirDiagramaComplementar`) — nenhuma lógica reimplementada, só
  invocação do código de produção real. 8 checagens, todas OK na segunda
  rodada:
  - diagrama complementar oculto antes de qualquer tentativa;
  - `PapelQuantitativo` bloqueado após 3 rejeições;
  - diagrama complementar visível após 3 rejeições;
  - diagrama complementar oculto de novo após "Restaurar";
  - bug de staleness demonstrado (bloqueado antes da correção) e corrigido
    (instância nova, não bloqueada, após trocar a situação-problema com o
    mesmo nome de papel).
  - Na primeira rodada do teste, uma falha real apareceu (diagrama não
    ficava visível) — rastreada até uma lacuna do próprio roteiro do
    teste (não completava o passo de "adivinhar a categoria" antes de
    testar), não um bug no código de produção; corrigida no teste, não no
    código.

## Escopo

`SeletorRepresentacaoComplementar.java` (novo parâmetro) e `Main.java`
(`deveExibirDiagramaComplementar` computa o novo parâmetro;
`garantirTentativasIncognitaAtual` ganha o id da situação na chave).
Nenhum dos ~10 call sites que dependem de `deveExibirDiagramaComplementar()`
precisou de mudança própria.

## Atualização — ativação temporária só para testes (mesmo dia)

A pedido da usuária, logo após esta implementação: manter o diagrama
complementar sempre visível por enquanto, para ela testar a interação com
ele e a manutenção de consistência entre representações sem precisar
errar 3 vezes a cada verificação. `Main.EXIBIR_DIAGRAMA_COMPLEMENTAR_SEMPRE_PARA_TESTES = true`
sobrescreve só o resultado final de `deveExibirDiagramaComplementar()` —
a regra de escalada acima continua implementada e calculada normalmente
por baixo. Apagar essa constante (e o `||` que a usa) restaura o
comportamento definitivo sem mais nenhuma mudança de código.
