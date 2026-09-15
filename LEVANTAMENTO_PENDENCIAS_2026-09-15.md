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

## 5. A verificar — versão-alvo do build de deploy

`Dockerfile` compila com `eclipse-temurin:11-jdk`/`-source 8 -target 8`. Uma
memória de projeto anterior registra um incidente de incompatibilidade
envolvendo JDK 17 no runtime de deploy. Não investigado a fundo nesta
sessão (fora do escopo tratado); verificar qual é a versão-alvo correta
antes do próximo deploy em Render.
