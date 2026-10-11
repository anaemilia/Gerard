# Matriz de paridade web ↔ desktop (modelagem)

Estado em 2026-10-10. "Verificado" = exercitado por interação real ou log real nos dois lados.
Atualize a cada questão resolvida; não marque "verificado" por inferência do código.

| Funcionalidade | Desktop | Web | Camada comum | Situação |
|---|---|---|---|---|
| Arrastar palavra do enunciado ao diagrama | Handler ElementoTextoMovel | EnunciadoInterativo / EditorNarrativa | domínio + cena | existe nos dois; paridade não reverificada nesta sessão |
| Valor da incógnita + confirmação Sim/Não | Main / FluxoConfirmacao | api.posicionar / responderConfirmacaoValor | PapelQuantitativo | existe nos dois |
| Sinal do número relativo | handlers de sinal | EscolhaSinalFigura | domínio | existe nos dois |
| Eixo de inteiros / lupa | PaineisEixosRelacoes | EixoNumericoFigura + rotas de eixo | domínio | existe nos dois |
| Seletores de operação | SeletorOperacaoRelacaoAluno | SeletorOperacaoDiagramaGerard | AvaliacaoEscolhaOperacaoRelacao | existe nos dois |
| Quadradinhos do Venn (+/−) | HandlerQuadradinhoVenn | api.ajustarQuadradinho | domínio + cena | existe nos dois |
| Historinhas (ajuda visual) | Main | HistorinhaPassiva | DecisorAjudaVisual | existe nos dois; excludente com o Venn |
| Restaurar | Main.registrarAcaoRestauracao | api.reiniciar | TentativaModelagemAditiva.restaurar | existe nos dois |
| Registro para no limite de 3 rejeições (incógnita e operação) | **verificado**: mouse real, log real | **verificado** (2026-10-11), incógnita: navegador com mouse real, linhas gravadas por tentativa errada `[2,2,1,0,0,0]` (a 3ª, do limite, é gravada; depois nada) e restaurar reabre (`tests/web/e2e_registro_para_no_limite_web.mjs`, 7/7); operação: só serviço (`TesteWebRegistroParaNoLimite`) | TentativaModelagemAditiva | paridade fechada para a incógnita; falta navegador para a escolha de operação errada 3 vezes
| Fluxo de conclusão (selo, tip Sim/Não, nova situação) | **verificado** (mouse real, 8 capturas idênticas depois de passar a usar `EscolhaConclusaoModelagem`) | **verificado** (2026-10-10): `SeloConclusao.tsx` + `ProjetorConclusaoWeb` + rota `/api/acoes/conclusao`; navegador com mouse real, 20 verificações, 2 execuções (`tests/web/e2e_conclusao_fluxo_web.mjs`); `TesteWebConclusaoFluxo` | EscolhaConclusaoModelagem (decisão e pausa de 1150 ms); textos de `ui.completion.*` | paridade fechada; o selo do desktop fica fixo e o da web segue o texto "Modelagem concluída" que some após 4 s (diferença de realização herdada) |
| Rótulo fora da caixa (com ou sem valor; ex.: "Todo" com `14`) | bloco de rótulo sempre fora da caixa (abaixo, salvo ACIMA) | **verificado** (2026-10-11): `coordenadaYDoRotulo` põe o rótulo abaixo sempre; o centro da caixa é só do valor/"?"; viewport reserva o espaço; capturas vazio/preenchido em composição e transformação em `web_conclusao_20261010/`; e2e de conclusão 20/20 | cena (`FiguraDiagrama.getLinhasRotulo`) | paridade fechada
| Anotação com balão ao passar o mouse | RenderizadorBalaoAnotacao | **sem equivalente encontrado** | a definir | lacuna: levantar a fonte real do texto antes de propor |
| Selo de erro (marca vermelha ao lado do diagrama) | SeloErroModelagem | a verificar (feedback pela cena) | cena/feedback | a verificar |
| Seleção de marcador do texto | HandlerSelecaoMarcadorTexto | a verificar | a verificar | a verificar |
| Pickup (item em primeiro plano ao segurar) | desenhado em Main | arraste HTML5 do navegador | — | diferença de realização; decidir se é paridade exigida |
| Barras da comparação | existe (sem situação que as acione) | — | — | uso futuro, não remover |
