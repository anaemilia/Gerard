# Pendências 1, 4 e 10 — decisões aplicadas pelas regras já registradas (2026-09-29)

A pesquisadora autorizou ("faça") resolver as três pendências. Cada decisão
abaixo foi tomada aplicando uma regra já registrada, não uma preferência nova.

## 10 — incógnita preservada durante o material concreto (web)

Regra aplicada: preservação da incógnita "?" e `termo_desconhecido` como
fonte única (decisões consolidadas do Gérard).

Antes, na escalada de Composição de medidas, o servidor projetava a
incógnita como *conhecida* com a contagem-rascunho dos quadradinhos: o
enunciado e a caixa Todo passavam a mostrar "0", "1", "2"... sem nenhuma
confirmação.

Agora `ServicoAtividadeWebComposicao.projetarPapelParaEstado` mantém a
incógnita desconhecida/engatada e publica a contagem em
`rascunho_material_concreto`; só a cena do material concreto
(`comRascunhoMaterialConcreto`) e o pré-preenchimento do editor a usam.
Revalidado com Playwright (arrastes e cliques reais): após a escalada e três
cliques em "+", o enunciado e a caixa continuam com "?", o material mostra 3
e o editor abre pré-preenchido com 3.

## 4 — eixo dos inteiros: uma única fonte da regra matemática

Regra aplicada: Regra 3 do `CLAUDE.md` (nunca dois caminhos ativos) e
localidade relacional (a relação estrutural calcula as consequências).

Antes, o React calculava a soma/diferença (`estadoSemanticoExploratorio.ts`).
Agora a semântica exploratória é mantida (nada é gravado na tentativa), mas o
cálculo é do domínio: rota `/api/acoes/projetar-eixo` →
`ServicoSorteioAtividadeWeb.projetarAlteracaoEixo` →
`CatalogoRelacoesEstruturaisAditivas` + `recalcularParaConsistencia`. O
arquivo do cliente saiu de `web-poc/src` e foi guardado em
`tmp/descartado_2026-09-29/`.

Achado corrigido junto: a projeção antiga deixava uma medida ficar negativa
(José com −1 bola, diagrama ainda azul). O domínio do papel dependente
(`PapelQuantitativo.aceita`) agora recusa a posição; o cliente volta à
última projeção aceita. Revalidado: arrastar o valor relativo para a
esquerda para no último valor válido (−5 → referendo 1), sem medida negativa.

Decisão da pesquisadora (2026-09-29): *"mexer no eixo é mecanismo
exploratório, não precisa persistir"*. Removidos `/api/acoes/ajustar-valor-eixo`,
`ajustarValorPeloEixo` (interface, Comparação, Transformação, Sorteio) e a
chamada no `api.ts`. `TesteAjusteEixoComparacaoWeb` e
`TesteRevelarOcultarEixoWeb` agora verificam a projeção pelo domínio, que
nada é gravado e que a projeção com medida negativa é recusada.

## 1 — controles +/−: ramo "bloqueado pela modelagem" mantido

Regra aplicada: não remover sem prova (precedente da Fase 7.6, removida só
após dois runs Robot com zero ocorrências). A liberação depende de
`diagramaVergnaudPossuiConteudoSemantico()`, que é dinâmico; o material
concreto depende do bloqueio da incógnita. A análise de código não prova que
os dois nunca coexistam (ex.: peças retiradas do diagrama após a escalada).
O ramo fica como guarda; a pendência 7 (levar seu log ao registro factual)
continua válida.

## Verificações

- 606 fontes; 145/145 testes Java; `tsc --noEmit` aprovado;
- `verificar_regressao_gerard.py`: 280 OK, só a falha ambiental do Ant;
- checker de localidade aprovado;
- evidências: `documentacao/relatorios/evidencias/decisoes_eixo_incognita_20260929/`.

## Novo achado (não alterado)

`GET /api/situacao` **sorteia uma nova situação a cada chamada**
(`estadoInicial()`). Recarregar a página no meio da atividade — ou duas abas
— troca a situação e perde o progresso. Um GET não deveria mudar estado.
