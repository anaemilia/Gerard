# Levantamento de pendências — 2026-09-15

Este levantamento substitui integralmente dois relatórios produzidos pelo
Codex em 15/09/2026 ("Pendências vigentes — 15/09/2026" e sua revisão
posterior), que descreviam consolidação de árvore, homologação do
repertório pedagógico P3 e autorização de P6 como concluídas. Nenhum desses
três itens tinha correspondência real no repositório no momento em que
foram verificados.

Verificação feita: `git status`, `git log`/`reflog`, `git stash list`,
`git worktree list`, `git branch -a`, `git ls-remote origin` e
`git fsck --unreachable` — nenhum branch local, branch remoto, stash ou
objeto órfão contém os documentos `PROPOSTA_REPERTORIO_PEDAGOGICO_P3_
2026-09-15.md` ou qualquer arquivo equivalente, nem commits além dos
listados no item 1. A pesquisadora confirmou que a sessão do Codex parou
antes de salvar qualquer coisa. Os dois relatórios do Codex devem ser
tratados como não confiáveis e não são fonte primária para nenhuma decisão
futura (ver `.agents/skills/gerard-autorizacao-sem-invencao/SKILL.md`).

Build e testes não foram executados nesta sessão — nenhuma alegação de
"aprovado"/"homologado" feita pelos relatórios do Codex (build Ant, 625
fontes, 102 testes, cinco testes gráficos no display real) foi verificada
aqui e nenhuma delas deve ser tratada como fato até execução real.

## 1. Encerrada — consolidar e versionar as alterações reais pendentes

No início da sessão, `git status` mostrava `src/Main.java` e
`src/gerard/ui/usuario/DialogoUsuario.java` modificados, mais o arquivo
`documentacao/arquitetura/LEVANTAMENTO_MODULOS_FUNCIONAIS_GERARD_2026-09-
10.md` não rastreado. Cada diff foi revisado e commitado nominalmente por
frente:

- `77a4e67` — fix: padronizar estilo do botão primário no diálogo de
  usuário (superfície suave + hover, alinhado à identidade visual);
- `6ce0916` — feat: reorganizar acessos do cabeçalho (Comunidade, Site,
  Reportar bug, Gérard-Vergnaud, usuário) num painel à esquerda e adicionar
  botão de chat e ícone de "Entrar";
- `75a809f` — docs: registrar o levantamento dos módulos funcionais do
  Gérard.

`_descartado_editor_narrativa_canvas/` permanece deliberadamente fora do
commit — ver item 4.

## 2. Sem evidência — repertório pedagógico P3

`PROPOSTA_REPERTORIO_PEDAGOGICO_P3_2026-09-15.md` não existe em nenhuma
fonte real (arquivo em disco, commit, branch ou stash). Não há texto-base
em português nem critérios de homologação para confirmar. Este item não
pode ser reaberto como "aguardando decisão da pesquisadora sobre um texto
já pronto" — o texto nunca existiu. Para existir, precisa ser escrito do
zero a partir de fonte primária real (decisão explícita da pesquisadora,
situação curada ou modelo de domínio já implementado); o relatório do
Codex não conta como fonte primária.

## 3. Bloqueada — "mostrar modelo completo" (P6)

Depende do item 2. Sem repertório P3 real e homologado, não há conteúdo
pedagógico aprovado para popular essa apresentação. Nenhuma linha de
código deve ser escrita para P6 antes disso.

## 4. Aberta — decisão sobre `_descartado_editor_narrativa_canvas/`

Diretório não rastreado, fora de `src/`, contendo `ChipPalavraRascunho.
java`, `PainelEditorNarrativa.java`, `PainelSacoPalavras.java`,
`TransferHandlerPecaPalavra.java` e `WrapLayout.java`. O nome indica
descarte deliberado de um protótipo (editor de narrativa em canvas); não
há convenção prévia de `.gitignore` para esse padrão nem histórico de commit
dele. Decisão (apagar ou manter fora do versionamento) não tomada nesta
sessão — cabe à pesquisadora.

## 5. Esclarecida — versão-alvo do build de deploy

Não é uma inconsistência: existem dois pipelines de deploy distintos,
verificados diretamente nesta sessão.

- Este repositório (`Dockerfile`/`render.yaml`, serviço Render
  `gerard-web-poc`) compila a partir do código-fonte dentro da imagem
  Docker, com `eclipse-temurin:11-jdk` e `-source 8 -target 8` — build
  autocontido, criado em 31/08 e último ajuste em 04/09/2026.
- `C:\gd` (repositório separado `gerard-web-deploy`, README confirma "não é
  o repositório de código-fonte") copia artefatos já compilados
  (`app/build`, `app/lib`, `app/web-poc`) para uma imagem
  `eclipse-temurin:17-jre-jammy`; é este que roda o serviço
  `gerard-web-deploy-1`. Já contém o commit `212fd89` ("recompilar com
  --release 17") que corrigiu o `UnsupportedClassVersionError` registrado
  na memória de projeto — a correção já está em produção desde 11/09/2026.

Pendência real aqui: `C:\gd` está 4 commits atrás deste repositório (parado
em `19d6ce7`, o mesmo commit que antecede os quatro commits desta sessão —
`77a4e67`, `6ce0916`, `75a809f`, `ff65101`). Se as mudanças de hoje devem
ir ao ar, falta recompilar com `--release 17`, copiar os artefatos para
`C:\gd` e dar push — passo manual, não deve ser feito sem autorização
explícita da pesquisadora (é ação visível externamente/deploy).

**Atualização 16/09/2026 — executado com autorização da pesquisadora:**
build limpo via `git archive HEAD` + `javac --release 17` (classpath dos
jars locais, já que `lib/*.jar` está no `.gitignore` e não entra no
archive); class file version verificada em `61` (Java 17, bate com o
runtime `eclipse-temurin:17-jre-jammy`). `web-poc/dist` recompilado
(`npm run build`) mas ficou byte-idêntico ao anterior — só `src/Main.java`
e `src/gerard/ui/usuario/DialogoUsuario.java` mudaram desde o último sync,
nenhum arquivo de `web-poc/src`. Testado localmente (servidor subiu na
porta 8091, respondeu HTTP 200) antes de sincronizar. Commit `6f550d3` em
`C:\gd`, push feito (`954ee68..6f550d3`). **Não verificado**: se o deploy
no Render (`gerard-web-deploy-1`) terminou com sucesso — isso só se
confirma no dashboard do Render (aba Events), não por este ambiente.
