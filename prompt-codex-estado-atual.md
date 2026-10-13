# Contexto e pendências do Gérard — retomada (2026-10-01)

Você vai continuar o trabalho no repositório do Gérard (Java Swing desktop + backend Java + `web-poc` em React/TSX).
Leia primeiro `CLAUDE.md` e `AGENTS.md` do repositório. Este prompt informa o estado atual; ele **não autoriza** por si só nenhuma alteração arquitetural, decisão pedagógica, exclusão ou commit.

## Regras que não podem ser quebradas (CLAUDE.md)
1. **Nunca inventar conteúdo de interface.** Todo texto, mensagem, tip ou tela vem de `src/gerard/i18n/mensagens_pt.properties`, do código desktop existente ou do log curado. Sem fonte real: diga "não encontrei fonte real para esta mensagem/tela/mecanismo" e pare.
2. **Correção = ação concluída por protocolo de mouse real**, não inspeção de código. Antes de declarar algo "corrigido", produza evidência: captura de tela, gravação ou log da sequência real de chamadas HTTP disparada pela interação. Se não conseguir, diga isso.
3. **Situação sem cobertura:** siga a ordem da Regra 3 antes de concluir "falta curadoria" (classes de domínio → desacoplamento → enriquecer e desativar o caminho antigo → só então a rota → só então "falta curadoria").
4. `Main.java` deve continuar **compositor/roteador fino**. Não coloque matemática, semântica, apresentação ou teste dentro dele.
5. **Não use `git add -A`.** A árvore tem muito material de pesquisa misturado. Faça stage por arquivo e só commite/publique se for pedido.
6. Não rode em segundo plano trabalho sensível em `Main.java`; a usuária quer acompanhar.
7. Ao rodar o app desktop, **confirme que a janela em primeiro plano é a do Gérard antes de qualquer clique simulado** (já houve cliques indo para outro aplicativo). Prefira Chrome headless via CDP para o web.

## Estado atual (tudo abaixo está commitado e publicado)
Branch: `web/deploy-render` (remoto `origin`, repositório `anaemilia/Gerard`).

Commits desta rodada, em ordem:
- `465c5ed` — Design System no `web-poc`: tokens únicos em `:root` do `web-poc/styles.css`, IBM Plex (Sans/Mono) em `web-poc/public/fonts`, botões primário/secundário, foco de 3px, traços do diagrama em INK, sucesso/erro com ícone além da cor, selo de conclusão ✓ + "Modelagem concluída" (some após 4 s por CSS).
- `fbafe90` — Design System no desktop: `GerardTema.instalar()` é o ponto único do tema Swing (chamado em `Main.aplicarTemaSwingPadrao`); `UITemaGerard` (neutros frios, azul de ação, sucesso/erro/alerta), `FeedbackMode` (Opção A: sucesso azul; Opção B: verde, troca em um lugar), `GerardButton`, `GerardButtonUI`, `GerardTabbedPaneUI`, `GerardBordaArredondada`, `GerardFontes`, `CoresRepresentacaoGerard`; selo de conclusão com o texto `ui.completion.completed`; ícones ✓/✕ por bloco na tela Construir; correção do `\n\n` literal em `pesq.summary.text` (pt/en/es/fr).
- `238303a` — Inversão de dependência das portas web: `PortaAjudaContextual` (valor neutro `NENHUMA`) e `PortaQuestionamentoPosicionamento` (valor neutro `NAO_APLICAVEL`) recebidas por construtor em `ServicoSorteioAtividadeWeb` e nos 6 `ServicoAtividadeWeb*`; `ServidorPrototipoWeb` (raiz de composição) entrega os adaptadores de `gerard.infraestrutura.web.scaffolding`. `verificar_localidade_arquitetural.py` rejeita `gerard.aplicacao.* -> gerard.infraestrutura.*`; baseline zerada. Registro em `RELATORIO_INVERSAO_PORTAS_WEB_2026-09-30.md`.
- `e3b07dd` — web: `<b>…</b>` do aviso de soltura incompatível vira `<strong>` (`web-poc/src/textoComNegrito.tsx`, sem `innerHTML`).

Deploy (já feito e confirmado no dashboard do Render):
- `gerard-web-deploy.onrender.com` constrói o branch `web/deploy-render` pelo `Dockerfile` (Java 11 com `-source 8 -target 8`; **todo o código precisa compilar em nível de linguagem Java 8**).
- `gerard-web-deploy-1.onrender.com` roda do repositório separado `C:\gd` (`anaemilia/gerard-web-deploy`, branch `main`), que contém só artefatos compilados. Procedimento de sincronização: `git archive HEAD` → compilar com `javac --release 17` (o runtime é `eclipse-temurin:17-jre-jammy`) → conferir bytecode 61 com `od -An -tu2 -j6 -N2 --endian=big X.class` → copiar `build/classes` e `web-poc/dist` para `C:\gd\app\` → commit com prefixo `deploy:` → push. O `lib/` não é versionado; use os jars de `lib/` do repositório. Já houve push que caiu no meio: confira sempre `git fetch` e compare local/remoto.

Verificação já feita: testes web 19/19; `tests/java` sem Robot 151/151 (dois de Swing precisam de display e dão `HeadlessException` em headless); verificador de localidade aprovado; Java 8 compila; no navegador (Chrome headless + CDP, eventos reais) o soltar incompatível é rejeitado e o menu "E agora? — Texto" responde com `ui.help.text.*`.

## Sem commit (não incluir em commits desta frente)
Relatórios soltos (`RELATORIO_FASE_7_12…`, `RELATORIO_FASE_7_13…`, `LEVANTAMENTO_PENDENCIAS_*`), `documentacao/relatorios/evidencias/web_incognita_sinal_20260929/*.png`, `scripts/verificar_regressao_gerard.py` (modificado, conteúdo não revisado), `.agents/skills/gerard-identidade-visual/*` (documentam a paleta nova e precisam de revisão), GIFs/storyboards `0*_historinha*`, PDFs, TSV/JSONL de mineração e demais artefatos de pesquisa. Origem/licença/destino ainda não inventariados.

## Pendências — analisar nesta ordem
**A. Podem ser analisadas sem decisão da pesquisadora**
1. **Verificadores de conclusão desatualizados** (`scripts/verificar_regressao_gerard.py`): duas expectativas exigem que `suspenderConclusaoDuranteManipulacao` reinicie o controlador e que `reiniciarConclusaoModelagem` não cuide do estado visual exploratório. Isso conflita com a decisão de 2026-09-29: depois do azul, manipulações são exploratórias e não desfazem a conclusão. Atualize a *expectativa* do verificador protegendo: (a) conclusão sem reinício durante manipulação exploratória; (b) retirada temporária do destaque azul; (c) reinício efetivo ao sortear outra situação. **Não altere `Main` para satisfazer o texto antigo do script.**
2. **Linha de base Windows sem resumo final** (`verificar_linha_base_windows.py`): a última execução compilou 622 fontes e 151 testes mas não produziu o agregado depois dos testes gráficos. Rode os testes gráficos **estritamente em série** e registre processos/timestamps. Não conte a tentativa anterior como 151/151.
3. **`bolas_509261012` em Composição de Transformações:** três valores errados sem diálogo ou escalada. Primeiro determine se o fluxo está corretamente aguardando operação/sinal; não presuma defeito.
4. **Layout 1600×1000 de Composição de Transformações:** seletor invadindo o enunciado e estado inicial cortado. Reproduza antes; se confirmado, corrija com geometria relativa (skill `gerard-posicionamento-relativo`), sem pixels coincidentes.
5. **Log granular legado** "Tentar adicionar unidade antes da modelagem": migrar a produção factual para o proprietário semanticamente autorizado, mantendo a persistência como infraestrutura. O log granular é **decentralizado de propósito**: não consolide os pontos de `registrarLogUsuario` e nunca misture com o log de gestos.
6. **Revalidar o web depois do novo estilo e das portas:** menu "E agora?" da área Vergnaud (só vi o rótulo), diálogos de usuário e chat, eixo numérico e o feedback de erro (tremor + tip) no navegador real. Use Playwright ou CDP com cliques/arrastos reais; evite gerar eventos de teste nos logs de produção (use servidor local com `-Duser.home` isolado).
7. **Desktop, ver ao vivo o arraste com os novos realces** (`EstiloRealceAlvo`: arrastar sobre, sugestão e encaixe ficaram em cinza/INK e mais parecidos; só a espessura os distingue). Avalie se a distinção pedagógica foi preservada.
8. **Combos e spinners do desktop** ainda no estilo padrão do Swing (a `GerardTema` não os cobre por UI própria).
9. **Revisar e commitar `.agents/skills/gerard-identidade-visual`** para registrar a paleta e o `GerardTema` como estado verificado.

**B. Dependem da pesquisadora (não decidir por ela)**
- Onde abre a **Análise da modelagem** do desktop (não foi localizada em menu nem ícones).
- Frase real por bloco da tela Construir para `accessibleName` (hoje só há ícone ✓/✕ além da cor; não existe texto por bloco em `mensagens_pt.properties`).
- Fonte e redação dos **sete textos sem fonte** de `BarraCategorias.tsx` (`RELATORIO_AUDITORIA_REGRA_1_BARRA_CATEGORIAS_2026-09-29.md`).
- Quando o seletor de operação das Relações deve aparecer em relação à confirmação da categoria.
- Se o diagnóstico "operação invertida" deve ficar em `IncognitaQuantitativa` nas duas plataformas ou continuar `VALOR_INCORRETO`.
- Cobertura curada das operações (1/4 Transformação de Relação, 1/2 Composição de Relações, 1/5 Composição de Transformações): depende de curadoria, não de inferência do código.
- Inventário e destino dos artefatos não rastreados; autorização para commit/push/exclusão em massa.
- Contexto após F5 (`POST /api/v1/tentativas` ainda sorteia nova situação): baixa prioridade por decisão dela.
- Artigo `documentacao/artigo_codesign/main.tex` linha 498 (afirma que a Fase 7.8 não recebeu validação Robot): não alterar sem autorização.

## Antes de entregar qualquer tarefa
- Compile o projeto inteiro em nível de linguagem Java 8 e rode `python scripts/verificar_localidade_arquitetural.py`.
- Rode os testes afetados em `tests/java` (classes com `main`; vários têm `package`, use o nome completo).
- Para qualquer mudança de comportamento, entregue a evidência exigida pela Regra 2 e diga explicitamente o que **não** foi validado.
- Não publique (push/deploy) sem pedido explícito. Informe objetivamente o que mudou, o que foi verificado e os riscos.
