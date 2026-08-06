# Investigação — quais campos de `TelaArtefatoExplicativo` são "do pesquisador"

Data: 2026-08-05. Somente leitura — nenhum código foi alterado. Investigação disparada por uma correção sua: "mediado não é digitado. O pesquisador está ao lado do participante, orientando-o. Apenas os invariantes operatórios são, de fato, preenchidos pelo pesquisador."

## Achado

A tela tem 4 seções (`src/gerard/pesquisador/tentativa/TelaArtefatoExplicativo.java`), e só uma delas é estrutural e rotuladamente do pesquisador:

1. **`matematica`** (linhas 150-205) — moldura com título `analise.mathTask` ("Tarefa matemática"). Contém, por elemento, `LinhaResposta` com o campo combinado "Dificuldade e explicação" (`analise.difficultyAndReason`, linha 163) — é aqui que ficam `explicacaoElemento`/`dificuldade`, o autorrelato do participante. **Sem rótulo de pesquisador.**
2. **`interacao`** (linhas 208-216) — resumo somente leitura da interação, título `analise.interactionTask`.
3. **fotografia** (linhas 219-232) — imagem estática, somente leitura.
4. **`pesquisador`** (linhas 234-305) — moldura com título `analise.researcher`, que em `mensagens_pt.properties:148` é literalmente **"PREENCHIMENTO DO PESQUISADOR"**. O comentário da própria linha 234 confirma: `// Preenchimento do pesquisador: catálogo simbólico e construtor controlado.` Contém: o combobox `invarianteCatalogo` (linha 249), o checkbox "inserir nova forma simbólica" + construtor de símbolos (condicional), o campo "Forma construída" (linha 284: `formaConstruida.setEditable(false)` — só pré-visualização, não digitável diretamente) e a área de texto "Observação do pesquisador" (`observacaoInvariante`, linhas 291-299).

## Conclusão

Só a seção 4 é estrutural e rotuladamente "do pesquisador" — na prática, um combobox (invariante) + um campo de texto livre (observação), exatamente os dois campos apontados na captura de tela desta conversa. A seção 1 (autorrelato: `explicacaoElemento`/`dificuldade`/`explicacaoGeral`) fica sob o título "Tarefa matemática", sem nenhum rótulo de autoria do pesquisador no código ou na interface.

Isso invalida a formulação usada num rascunho anterior da nota de `REFERENCE.md` §4.8.1 ("a inserção de TODOS os campos da tela é mediada pelo pesquisador — inclusive o autorrelato"): não tem sustentação na estrutura real da tela — o próprio código separa as duas coisas em seções distintas, uma rotulada como do pesquisador, outra não.

## Pendência

A nota final de `REFERENCE.md` §4.8.1 ainda não foi reescrita nem aplicada — a versão atual do arquivo continua a antiga (só `"S"`/`"C"`, ver `RELATORIO_SENHA_TELA_ARTEFATO_EXPLICATIVO_2026-08-05.md`). Falta decidir o texto final incorporando esta distinção (só invariante = seção do pesquisador; autorrelato = seção sem rótulo de autoria, mediado/orientado mas não atribuído ao pesquisador na tela).
