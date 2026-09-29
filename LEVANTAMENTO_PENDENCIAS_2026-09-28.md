# Levantamento de pendências — 2026-09-28

Levantado a partir do estado real do repositório (árvore de trabalho,
verificador, testes executados nesta sessão) e não de relatórios anteriores.
Cada item indica a evidência. Itens marcados **[decisão]** dependem da
pesquisadora; os demais podem ser executados após autorização.

## Encerradas nesta sessão

- Validação Robot dirigida da Fase 7.8 — 36/36
  (`RELATORIO_VALIDACAO_ROBOT_FASE_7_8_SELETORES_2026-09-28.md`).
- Próxima extração de `Main`: Fase 7.10, controles +/− do material concreto,
  equivalência Robot A/B em quatro categorias; `mousePressed` 350 → 245
  (`RELATORIO_FASE_7_10_CONTROLES_UNIDADES_2026-09-28.md`).
- Compilação LaTeX do artigo: não era só ambiente. Havia dois erros no
  fonte: `\\` faltando no cabeçalho da tabela `tab:antecedentes-skills`
  (`Misplaced \noalign`) e `T1` + `microtype` sem fonte escalável
  (`auto expansion is only possible with scalable fonts`). Corrigidos em
  `main.tex` e `overleaf/main.tex` (`\usepackage{lmodern}` e `\\`); o PDF
  compila com pdflatex e lualatex, 71 páginas, zero referências indefinidas.
  Requisito ambiental que permanece: classe `elsarticle` e `babel-portuges`
  instaladas no TeX local (MiKTeX instala sob demanda; Overleaf já tem).
- Política de rejeições: decisão registrada em
  `.agents/skills/gerard-log-acao-instrumental/references/estado-migracao.md`.

## Atualização (continuação, 2026-09-28)

- Item 1 decidido (a resposta conta para as 3) e item 7 implementado nas
  duas plataformas; item 5 resolvido; falha web real encontrada e corrigida
  (classificação de situação invisível travando a tela) e textos inventados
  do diálogo de classificação substituídos pelas chaves do desktop. Ver
  `RELATORIO_RESPOSTA_CONFIRMACAO_E_CLASSIFICACAO_WEB_2026-09-28.md`.
- `TesteRevelarOcultarEixoWeb` intermitente: **resolvido** — o repositório de situações
  aceita fonte de sorteio injetável (construtor padrão inalterado) e o teste usa
  semente fixa; 10/10 execuções aprovadas.
- Novo: na escalada web de Composição de medidas o "?" vira "0" no
  enunciado e a Parte 2 parece cortada.

## Atualização 2026-09-29

- Pendências 1, 4 e 10 resolvidas pelas regras registradas: ver
  `RELATORIO_DECISOES_EIXO_INCOGNITA_CONTROLES_2026-09-29.md` (1 = manter o
  ramo como guarda; 4 = projeção do eixo pelo domínio, sem medida negativa;
  10 = "?" preservado durante o material concreto).
- Novo: `GET /api/situacao` sorteia nova situação a cada chamada (recarregar a
  página perde a atividade).
- Decidido (2026-09-29): mexer no eixo é mecanismo exploratório e não
  persiste. A rota `ajustar-valor-eixo` e os métodos `ajustarValorPeloEixo`
  foram removidos; os testes passaram a verificar a projeção pelo domínio e
  que nada é gravado.

## Atualização 2026-09-29 (tarde)

- Decisão: após o diagrama ficar azul, toda modificação é exploratória, até
  uma nova situação ser sorteada (restaurar não encerra). Dono:
  `TentativaModelagemAditiva` + `PapelQuantitativo` (sem trava em
  controlador). Desktop implementado; web já vigente; Robot A/B confirma.
- **Resolvido (2026-09-29): a modelagem web persiste.** Antes, só a nova
  situação e alguns registros granulares eram gravados, por isso uma
  modelagem não concluída sumia ao recarregar. Agora cada ação (classificação,
  posicionamento, engate do "?", valor da incógnita, Sim/Não, sinal,
  operação, restauração) é constituída pelo mesmo proprietário do desktop e
  segue pela `TentativaModelagemAditiva` da situação sorteada até o mesmo log
  (`EscopoTentativaWeb`, `PortaRegistroAtividadeWeb` estende
  `PortaPersistenciaAcaoInstrumental`). Após o azul nada é gravado, e a web
  deixa de perguntar Sim/Não. Evidência: `TesteModelagemWebPersisteNaTentativa`,
  `TesteSinalOperacaoWebPersistemNaTentativa` e
  `tests/web/e2e_persistencia_modelagem_web.mjs` (mouse real, 13/13).
  Pendente de baixa prioridade: manter o contexto após F5 (GET /api/situacao
  ainda sorteia uma situação nova, o que agora encerra a tentativa gravada).

- **Resolvido (2026-09-29): texto inventado no seletor de operação web.**
  `App.tsx` exibia "Operação incorreta." quando o servidor não mandava
  explicação (Regra 1). Agora só aparece a explicação curada do servidor; sem
  ela nada é exibido (o desktop só emite o som de erro). Evidência:
  `tests/web/e2e_operacao_sem_texto_inventado.mjs` (mouse real, 4/4) e
  checagem nova em `verificar_regressao_gerard.py`.
- A auditar (Regra 1): textos fixos em `BarraCategorias.tsx` (diálogos de
  relato de problema, usuário e curadoria) — conferir se vêm do desktop ou
  precisam ir para `mensagens_*.properties`.

## Lista revisada — 2026-09-29

Revisão item a item contra o código atual (verificador, testes e grep nesta
data). A numeração antiga (1–18) aparece entre parênteses para rastreio.

### Encerradas (verificadas)

- (1) Resposta Sim/Não conta para as 3 rejeições — decidido e implementado.
- (2) Ramo "bloqueado pela modelagem" dos controles +/− — decidido: manter
  como guarda.
- (4) Eixo com duas fontes de verdade — projeção pelo domínio, sem medida
  negativa, exploratória (não grava); rota antiga removida.
- (5, 7) Confirmação de valor rejeitado registrada nas duas plataformas.
- "?" virando "0" na escalada web — "?" preservado durante o material
  concreto.
- `TesteRevelarOcultarEixoWeb` intermitente — semente fixa.
- Modo exploratório após o azul (desktop e web), dono
  `TentativaModelagemAditiva`.
- Persistência da modelagem web pela tentativa (ver acima).
- Texto inventado "Operação incorreta." no seletor web.
- Restauração web captura nos papéis incorporados as sequências abertas antes
  de recriá-los. Evidência: `TesteRestauracaoWebEncerraSequencia`.
- Incógnita inteira validada por mouse real em Transformação de medidas:
  magnitude, sinal errado, pergunta Sim/Não, confirmação, sinal correto e
  diagrama azul, com origens `WEB_*` no TSV. Evidência:
  `tests/web/e2e_persistencia_modelagem_web.mjs` (20/20) e capturas em
  `documentacao/relatorios/evidencias/web_incognita_sinal_20260929/`.
- Auditoria Regra 1 de `BarraCategorias.tsx`: relato conforme; usuário
  conforme após duas substituições literais; sete textos do upload de
  curadoria listados para decisão em
  `RELATORIO_AUDITORIA_REGRA_1_BARRA_CATEGORIAS_2026-09-29.md`.
- Validação Robot repetida no Windows, em série: seletores de operação 36/36
  (`C:\Users\cecomp\Gerard\logs\robot_fase_7_8_20260929_151921`), controles
  de unidades aprovado (`robot_controles_unidades_run_20260929_152018`) e
  exploração após conclusão com zero falhas.

### Abertas — por prioridade sugerida

**A. Rápidas / sem decisão**

1. **Encerrado (2026-09-29):** restauração web inclui as sequências de
   rejeição dos participantes incorporados antes de recriar os papéis.
2. **Encerrado (2026-09-29):** incógnita com sinal exercitada por mouse real
   em Transformação de medidas, com persistência no log e conclusão azul.
3. **Auditoria encerrada (2026-09-29):** relato e usuário conformes; sete
   textos do upload de curadoria aguardam decisão explícita, listados no
   relatório da auditoria.
4. Remover `Claude outputs/sync_web.tgz` (65 MB, temporário da validação
   Playwright) — requer autorização de exclusão.

**B. Componentização (cada uma com autorização própria)**

5. (9) Extrações de `mousePressed`: Fase 7.11 (marcador do enunciado) e
   Fase 7.12 (pressionamento da barra de Comparação, 2026-09-29) concluídas;
   Fase 7.13 (lupa de Relações): a mecânica já estava localizada; só a
   apresentação saiu do corpo de `mousePressed` (ratchet 174). Sequência
   registrada concluída; repetir os Robots 7.12/7.13 no Windows.

**C. Achados de interface/dados a investigar**

8. (11) Composição de transformações `bolas_509261012`: três valores errados
   sem diálogo nem escalada no harness — confirmar se a edição depende da
   escolha de operação/sinal (esperado) ou se é falha.
9. (12) Layout de Composição de transformações (1600×1000): primeiro
   seletor de operação invade o enunciado; estado inicial cortado.
10. (13) Cobertura curada de operações: só 1/4 (TR), 1/2 (CR) e 1/5 (CT)
    têm `operacao_relacao`/`operacao_estado_transformacao`; nas demais o
    seletor não aparece (curadoria).

**D. Decisões [decisão]**

11. (3) O seletor de operação das Relações deve aparecer depois que a
    categoria adivinhada é confirmada? Não verificado.
12. Diagnóstico da incógnita na web: com a unificação, a web registra
    `VALOR_INCORRETO` como o desktop e deixou de reconhecer
    "operação invertida" (não usado pela interface). Manter assim ou levar
    esse diagnóstico para `IncognitaQuantitativa` nas duas plataformas?

**E. Documentação, ambiente e versionamento**

13. (14) `main.tex`, linha 498: parágrafo diz que a Fase 7.8 "ainda não
    recebeu validação própria por interação Robot" — já recebeu (36/36).
    Texto autoral; apontado precisamente e não alterado.
14. (15) **Concluído em 2026-09-29:** os três testes Robot foram repetidos
    no Windows, em série, sem falhas.
15. (16) **Desatualizado neste ambiente:** o Ant embarcado do IntelliJ foi
    encontrado e usado pelo verificador.
16. (17) **Não reproduzido no Windows:** as alegadas cinco falhas normativas
    em skills de narrativa rica não apareceram na validação atual.
17. (18) Árvore de trabalho com muitas frentes misturadas. Consolidar em
    commits por frente antes da próxima fase.

**Baixa prioridade (usuária, 2026-09-29):** manter o contexto após F5 /
recarregar (`GET /api/situacao` sorteia nova situação).

## Lista vigente após os itens A1–A3 — 2026-09-29

### Execução condicionada a autorização

1. Excluir `Claude outputs/sync_web.tgz` (arquivo temporário de 65 MB). A
   exclusão continua aguardando autorização explícita.
2. Extrair de `Main.mousePressed`, cada recorte com autorização própria. A
   seleção do marcador do enunciado foi validada na Fase 7.11, inclusive por
   Robot real no Windows. Restam: início/bloqueio da barra de comparação e
   lupa dos painéis de Relações.
3. Corrigir as três dívidas de localidade web já controladas pela linha de
   base: `AjudaContextualWeb`, `AvaliadorOrigemDestinoWeb` e
   `ServicoSorteioAtividadeWeb`.
4. Migrar o log granular legado “Tentar adicionar unidade antes da
   modelagem” para produção factual no proprietário.
5. Investigar os achados de Composição de transformações: ausência de diálogo
   em `bolas_509261012` e sobreposição/corte no layout 1600×1000.

### Decisões da pesquisadora

6. Definir a fonte dos sete textos do upload de curadoria listados em
   `RELATORIO_AUDITORIA_REGRA_1_BARRA_CATEGORIAS_2026-09-29.md`.
7. Decidir quando o seletor de operação das Relações deve aparecer após a
   confirmação da categoria.
8. Decidir se o diagnóstico “operação invertida” deve existir em
   `IncognitaQuantitativa` nas duas plataformas ou se permanece
   `VALOR_INCORRETO`.

### Validação e documentação

9. O verificador geral encontra duas falhas na frente de conclusão em
    `Main`: uso de campo-espelho em `suspenderConclusaoDuranteManipulacao` e
    em `reiniciarConclusaoModelagem`. Não foram corrigidas por estarem fora do
    grupo A.
10. O texto autoral desatualizado foi apontado precisamente em
    `documentacao/artigo_codesign/main.tex`, linha 498, sem alteração.
11. O agrupamento da árvore por frente foi proposto abaixo; os commits não
    foram criados.

### Dados e baixa prioridade

12. Cobertura curada das operações permanece parcial: 1/4 em Transformação
    de relação, 1/2 em Composição de relações e 1/5 em Composição de
    transformações.
13. Manter o contexto após F5 continua em baixa prioridade por decisão da
    pesquisadora.

No Windows atual, o verificador encontrou e usou o Ant embarcado do IntelliJ;
portanto, “Ant fora do PATH” não é uma falha deste ambiente.

### Proposta de agrupamento em commits — sem executar

Não usar `git add -A`: `Main.java`, `scripts/verificar_regressao_gerard.py`,
skills e relatórios cruzam mais de uma frente e precisam ser selecionados por
hunk depois de revisão. Ordem sugerida:

1. **Regras arquiteturais e grafo de skills** — `AGENTS.md`,
   `.agents/skills/dependencies.json`, skills novas/alteradas, referências e
   `documentacao/specs/`.
2. **Narrativa rica e curadoria** — agregado/representação textual, sidecars,
   telas/testes de curadoria, dados narrativos e documentação correspondente.
3. **Interação desktop Fases 7.8/7.10** — handlers, portas, adaptadores Swing,
   seletores, controles de unidades, testes Java/Robot e relatórios dessas
   fases; selecionar apenas os hunks correspondentes de `Main.java`.
4. **Tentativa, log e modo exploratório** — domínio da tentativa/incógnita,
   persistidores, Modelador, testes e evidências de rejeição/restauração.
5. **Portabilidade e persistência web** — serviços Java web, contrato HTTP,
   `web-poc`, testes Java/Playwright e auditoria da Regra 1.
6. **Artigo e documentação autoral** — `documentacao/artigo_codesign/`, cópia
   Overleaf e relatórios que não pertençam diretamente às frentes anteriores.
7. **Dados e artefatos de pesquisa** — TSV/JSON/ARFF/PDF/ZIP, imagens,
   storyboards e animações; revisar proveniência e necessidade antes de
   versionar arquivos grandes.

Diretórios gerados por testes devem ser classificados como evidência válida ou
temporário antes de qualquer commit. Neste estado,
`robot_controles_unidades_run_20260929_152018/` é a execução Windows válida;
`robot_controles_unidades_run_20260929_151812/` veio da tentativa concorrente
invalidada e aguarda autorização para exclusão.

## Histórico — lista original de 2026-09-28

### 1. Decisões pendentes

1. **[decisão]** A resposta Sim/Não a "Tem certeza que esse é o valor do
   X?" conta para o limite de três rejeições consecutivas (que bloqueia a
   incógnita e escala para o material concreto)? Sem essa resposta a
   implementação do item 4 altera ou não a contagem — não inferir.
2. **[decisão]** Ramo "bloqueado pela modelagem" dos controles +/−: pela
   interface atual é inalcançável (material concreto só aparece após a
   escalada). Remover como código morto (como na Fase 7.6) ou manter?
3. **[decisão]** "Nova situação-problema" sorteia entre todas as categorias
   (fluxo de adivinhação). O seletor de operação das Relações deve aparecer
   depois que a categoria adivinhada é confirmada? Não verificado.

### 2. Web — candidatas à "falha funcional a revalidar"

Nenhum registro no repositório identifica a falha. A análise da árvore não
commitada aponta estas candidatas; nenhuma foi revalidada por interação real
(Regra 2 do `CLAUDE.md`), porque isso exige servidor + frontend + navegador:

4. **Eixo dos inteiros com duas fontes de verdade.** `App.tsx` deixou de
   chamar `api.ajustarValorEixo` e passou a calcular no cliente
   (`estadoSemanticoExploratorio.ts`, soma/diferença por índice de papel).
   Ao mesmo tempo, o servidor ganhou `ajustarValorPeloEixo` no domínio
   (`relacao.recalcularParaConsistencia`) e a rota
   `/api/acoes/ajustar-valor-eixo` — hoje sem consumidor. Viola a Regra 3
   (caminho antigo e novo ativos em paralelo) e a localidade relacional
   (matemática da relação no React). Sintoma provável: o eixo mexe na tela,
   mas o servidor não registra nem reavalia a conclusão; o estado se perde ao
   trocar de tentativa.
5. **Confirmação de valor rejeitado.** `aoNegarValor` não envia nada ao
   servidor e `aoConfirmarValor` só adota o snapshot já rejeitado — contraria
   a decisão de registrar toda resposta (ver item 7).
6. Incógnita com sinal: a escolha de sinal foi movida do campo de edição
   para `EscolhaSinalFigura` após a digitação da magnitude
   (`sinalPendenteIncognita`). `tsc` passa; fluxo real não exercitado.

### 3. Implementação a autorizar

7. Implementar a decisão de rejeições nas duas plataformas: o proprietário
   da incógnita produz um registro por resposta (Sim e Não), correlacionado
   por `rejection_sequence_id`; o desktop deixa de ignorar `opcao`; o web
   envia a resposta ao servidor. Depende do item 1.
8. Três dívidas de localidade web ainda na linha de base:
   `AjudaContextualWeb`, `AvaliadorOrigemDestinoWeb`,
   `ServicoSorteioAtividadeWeb` (aplicação importando UI/persistência/
   scaffolding concretos).
9. Próximas extrações de `mousePressed` (245 linhas), cada uma com
   autorização própria: seleção do marcador do enunciado (~50 linhas);
   bloqueio/início do controle da barra de comparação; lupa dos painéis de
   Relações.
10. Log granular legado do bloqueio de adição de unidades → registro factual
    no proprietário (hoje em `registrarAcaoGranular`).

### 4. Achados de interface/dados

11. Composição de transformações com as duas operações curadas
    (`bolas_509261012`): três valores errados na incógnita não produziram
    diálogo nem escalada no harness. Verificar se a edição depende da escolha
    de operação/sinal antes (esperado) ou se é falha.
12. Layout de Composição de transformações (1600×1000): o primeiro seletor
    de operação invade a caixa do enunciado; o estado inicial fica cortado.
13. Cobertura curada: entre as situações validadas, só 1/4 (TR), 1/2 (CR) e
    1/5 (CT, duas operações) têm `operacao_relacao` /
    `operacao_estado_transformacao`; nas outras o seletor não aparece.

### 5. Documentação, ambiente e versionamento

14. Artigo (`main.tex`): o parágrafo sobre a Fase 7.8 diz que ela "ainda não
    recebeu validação própria por interação Robot" — agora recebeu. Texto
    autoral; não alterado. `artigo_skills_overleaf.zip` **atualizado** com as
    correções (compila com latexmk: 59 páginas, zero referências indefinidas).
15. Repetir no Windows os harness `TesteRobotSeletoresOperacaoRelacoes` e
    `TesteRobotControlesUnidades` (validação feita em Linux/Xvfb).
16. Ant fora do PATH na VM; o verificador marca isso como única falha. No
    Windows o fallback do IntelliJ continua disponível.
17. O relatório da Fase 7.8 citava "cinco falhas normativas preexistentes
    em skills de narrativa rica"; o verificador executado hoje não as
    reproduz (276 OK, só a falha do Ant). Tratar a afirmação como
    desatualizada até nova execução no Windows.
18. Árvore de trabalho com mais de 40 arquivos modificados e dezenas de não
    rastreados misturando frentes (web, skills, Fase 7.8, Fase 7.10,
    artigo). Consolidar em commits por frente antes da próxima fase.
