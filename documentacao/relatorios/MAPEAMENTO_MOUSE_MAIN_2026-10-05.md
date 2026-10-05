# Mapeamento dos métodos de mouse da Main (bloco E) — somente leitura

Data: 2026-10-05. Estado: `src/Main.java` com 12.761 linhas. Nenhum código foi alterado por este levantamento.

## 1. Tamanho

| Método | Linhas | Papel |
|---|---:|---|
| `mousePressed` | 174 | cadeia de prioridade de quem recebe o pressionamento |
| `mouseDragged` + `processarMovimentoArraste` | 28 + 60 | despacho do movimento ao handler ativo |
| `mouseReleased` | 108 | soltura: coordena posicionamento, log, sinal, sincronização e conclusão |
| `mouseClicked` | 38 | duplo clique (edição de texto/valor) |
| `mouseMoved` | 205 | foco, dica (tooltip) e cursor por alvo |
| `mouseExited` | 9 | limpeza |
| **Total** | **~620** | |

Já existem 10 handlers extraídos (`HandlerInteracaoItemTextoArrastavel`, `...QuadradinhoVenn`,
`...PaineisEixosRelacoes`, `...ControleComparacao`, `...SeletoresOperacaoRelacoes`, `...SelecaoMarcadorTexto`,
`...ElementoTextoMovel`, `...ControlesUnidades`, `...PecaPalavraRascunho`, `...ArrasteIncremental`). Os métodos de mouse
da Main **já são, em grande parte, um despachante** sobre eles; o que sobra na Main é (a) a ORDEM de prioridade,
(b) o que acontece DEPOIS de cada handler responder e (c) o estado de foco/dica/cursor.

## 2. `mousePressed` (174 linhas)

Estrutura: cadeia de responsabilidade com 11 elos, cada um com `return` ao ser atendido:

1. editor de narrativa (modo à parte) → 2. limpeza de efeitos do arraste anterior (5 cancelamentos) →
3. controles de unidades → 4. controle da comparação → 5. lupa do painel de eixo → 6. seletores de operação →
7. painéis de eixo das Relações (com bloqueio por `interacaoRepresentacoesLiberadaPelaModelagem()`) →
8. marcador de texto → 9. elemento de texto móvel → 10. quadradinho de Venn (idem bloqueio) →
11. item arrastável → 12. conector (ignora duplo clique).

| Decisão inline | Onde | Dono natural |
|---|---|---|
| **Ordem de prioridade** dos 12 elos | corpo inteiro | um objeto `CadeiaPressionamento` (a prioridade é conhecimento único) |
| Gate "representações liberadas pela modelagem" (2 elos) | linhas do painel de eixo e do quadradinho | já vem de `interacaoRepresentacoesLiberadaPelaModelagem()`; falta só o elo consultar a mesma porta |
| Ignorar pressionamento de duplo clique no conector | elo 12 | `politicaGestoEstrutural` (já existe) |
| Efeito pós-clique repetido **5 vezes**: fantasma + arraste elástico + cursor de mão fechada + rastreio granular + `registrarAcaoGranular` | elos 7, 9, 10, 11, 12 | uma rotina `iniciarManipulacao(tipo, alvo)` única |

Avaliação: **bom candidato**, risco médio. A cadeia pode virar uma lista ordenada de elos `Pressionamento`
(interface com `tentar(x, y, evento) -> boolean`); a Main só materializa.

## 3. `mouseDragged` / `processarMovimentoArraste` (88 linhas)

Despacho por "quem está ativo": painéis de eixo → controle da comparação → quadradinho → elemento de texto
(com **decisão**: converter o elemento de texto em item do diagrama quando sai da área do texto; limites livres para
número/`?`, dentro dos limites para texto) → item arrastável (+ realce de proximidade e questionamento persistente) → conector.

| Decisão inline | Dono natural |
|---|---|
| Sair da área do texto converte o elemento em item (`podeEnviarParaDiagrama` + `estaNaAreaDoTexto`) | `HandlerInteracaoElementoTextoMovel` / política do enunciado |
| Número e `?` movem livres; outros ficam dentro dos limites | idem (política de limites do enunciado) |
| Suspender o destaque de conclusão durante a manipulação | `DecisaoFeedbackConclusao` / controlador de conclusão |

Avaliação: **candidato fácil** (a ordem é a mesma do pressionamento: uma única lista de "elos ativos").

## 4. `mouseReleased` (108 linhas) — o mais denso

Sequência fixa após soltar: encerra rastreio → se painel de eixo ativo: sincroniza e sai → se controle da comparação
ativo: confirma o valor da incógnita (via `FluxoConfirmacaoValorIncognita`) e sincroniza → item solto: registra o gesto,
centraliza se perto do alvo (`scaffoldingProximidade`), **avalia o posicionamento** (soltura x reavaliação),
questionamento persistente / limpeza, log da soltura, e, se correto: habilitação de sincronização do estado final,
**sinal do número relativo**, representações reativas; sincroniza o quadradinho; verifica a conclusão; repinta.

| Decisão inline | Dono natural |
|---|---|
| "Clique parado = reavaliação de consistência, não nova ação" (`itemRealmenteMoveu` → origem da avaliação) | tentativa/proprietário do posicionamento (a regra já está documentada nas skills de log e consistência) |
| Centralizar ao soltar perto do alvo | `scaffoldingProximidade` (já decide; a Main só chama) |
| Posicionamento incorreto → tip + tremor; correto → limpa tip e propaga | **sequência** análoga às já extraídas (`FluxoEscolhaSinalNumeroRelativo`, `FluxoConfirmacaoValorIncognita`): um `FluxoSolturaItem` |
| O que só roda se o posicionamento estiver correto (3 passos) | idem |

Avaliação: **maior ganho de localidade**, risco **alto**: toca o log factual de gestos e ações (que tem pipeline
próprio e `action_id`). Recomendo só depois de um macaco que exercite soltura certa, errada, reavaliação e parada.

## 5. `mouseClicked` (38 linhas)

Duplo clique: edição de texto de elemento, valor numérico, `editarNumeroNatural` (fluxo da tentativa, já mapeado como
parte do bloco D e ainda na Main). Pequeno; entra junto com a cadeia.

## 6. `mouseMoved` (205 linhas) — dica, foco e cursor

Cadeia de 10 ramos, cada um repete o mesmo padrão: **zerar focos** (`elementoTextoFocado`, `itemFocado`,
`quadradinhoVennFocado`) → ligar dica (`mostrarAnotacaoMouseOver`, `textoAnotacaoMouseOver`) → escolher cursor
(mão/aberta/padrão) → `repaint` → `return`.

| Decisão inline | Dono natural |
|---|---|
| Dica por alvo (remover/adicionar quadradinho com 4 estados: bloqueado/liberado/limite/mínimo) | cada representação (`RepresentacaoComUnidades*`) já sabe `podeAdicionar/RemoverUnidade`; falta a mensagem vir de lá |
| Mensagem de bloqueio quando a modelagem ainda não liberou a interação | já centralizada em `obterMensagemBloqueio*` |
| Cursor por alvo | tabela alvo → cursor |
| Foco exclusivo (só um alvo focado) | objeto `FocoDeInteracao` (hoje 3 campos soltos na Main) |

Avaliação: **candidato muito bom e de baixo risco funcional** (só apresentação: dica e cursor). Cobertura por
mouse real é fácil: mover o mouse e conferir `textoAnotacaoMouseOver`/cursor por alvo.

## 7. Cobertura de teste por mouse real (Regra 2) hoje

Já existem: `TesteRobotControlesUnidades`, `TesteRobotControleComparacao`, `TesteRobotLupaEixoRelacao`,
`TesteRobotSelecaoMarcadorTexto`, `TesteRobotSeletoresOperacaoRelacoes`, `TesteRobotExploracaoAposConclusao`
(soltura/conclusão), `TesteMacacoHistorinhaNumeroRelativo` (arrasto e soltura em 66 situações),
`TesteMacacoSinalNumeroRelativo`. **Faltam**: um macaco de `mouseMoved` (dica/cursor por alvo) e um macaco de soltura
com os 4 desfechos do item (certa, errada, reavaliação parada, fora de qualquer elemento).

## 8. Ordem recomendada (do menor ao maior risco)

1. **Foco + dica + cursor** de `mouseMoved` → `FocoDeInteracao` e tabela de dicas/cursor (só apresentação).
   Escrever primeiro o macaco de dica/cursor, depois mover.
2. **Efeito pós-pressionamento repetido 5×** → uma rotina única (fantasma, arraste elástico, cursor, rastreio, ação granular).
3. **Cadeia de prioridade** de `mousePressed`/`mouseDragged`/`mouseClicked` → lista ordenada de elos (um objeto).
4. **`FluxoSolturaItem`** (soltura) → por último, com o macaco novo e o log factual verificado contra a linha de base.

## 9. Riscos já conhecidos

- O log de gestos é um pipeline separado do log de ações; não pode ser fundido (decisão da pesquisadora).
- A soltura reavalia o posicionamento como **reavaliação** quando não houve movimento: confundir isso cria uma
  segunda ação instrumental por clique parado.
- `mouseReleased` chama `verificarConclusaoModelagem()` ao fim; a ordem relativa à propagação importa (a conclusão
  lê o estado já sincronizado).
- Nenhum passo pode concluir a tarefa por conta própria: cada papel só se satisfaz pela interação humana própria.
