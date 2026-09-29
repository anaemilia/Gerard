# Estado verificado da consistência entre representações

Leia esta referência ao auditar a implementação existente, localizar a lógica
em produção ou avaliar a cobertura de regressão. Ela registra evidências
datadas; as regras normativas permanecem no arquivo principal.

## Verificação de 2026-07-20

As regras 1, 3 e 4 foram conferidas linha a linha contra o código e se
confirmaram. A regra 2 confirmou-se com escopo mais estreito do que um
“bloqueio geral”. A regra 5 foi confirmada em 2026-08-07. As afirmações
anteriores de que `termo_desconhecido` seria fonte única e de que `Main.java`
estaria livre de lógica semântica eram falsas; não use versões antigas dessas
afirmações.

## Localização da extração já realizada

### Gate de confirmação da incógnita

`ScaffoldingGraficoInteiros.identificarNaturezaInteracao` distingue interação
com componente de tela de interação com valor semântico. O gate fica no ponto
de chamada, não em `ScaffoldingGraficoInteiros`, `LayoutPainelEixoInteiros` ou
`PoliticaPreenchimentoIncognita`.
`sincronizarNumeroRelativoComGraficoSeNecessario` (`Main.java:11612-11674`)
só chama `sincronizarTodasAsRepresentacoesAPartirDoVergnaud` com
`Origem.EIXO_X` quando `liberadoParaPropagar` é verdadeiro, usando
`incognitaAguardandoConfirmacaoDeValor` durante o arrasto e
`confirmarValorIncognitaAceito` ao soltar.

O bloqueio vale somente quando o item movido é a própria incógnita pendente.
`incognitaAguardandoConfirmacaoDeValor` (`Main.java:5811-5818`) retorna falso
quando o item não é a incógnita original ou ainda não foi preenchido pelo
protocolo mouse/texto. O mesmo padrão protege `Origem.EIXO_X`,
`Origem.EIXO_VERTICAL` (`Main.java:10000`, `10998`) e `Origem.ARRASTE`
(`Main.java:11764`); não é bloqueio geral dos eixos.

### Simulação complementar do Venn

`simularEstadoCompartilhadoAposAlteracaoQuantidade` e
`estadoSimuladoRespeitaLimitesDasQuantidades` foram extraídos para
`gerard.campoaditivo.sincronizacao.SimuladorEstadoComplementarVenn`, com
wrappers homônimos preservados em `Main.java`/`TelaGerard` para manter os
tokens verificados por `scripts/verificar_regressao_gerard.py`.

A extração foi validada por compilação completa com `javac` e checagens ad hoc
de inferência aditiva, limite curado e valor com sinal. Não foi possível rodar
`ant clean jar`, `scripts/testar_*.sh` nem a regressão completa porque `ant`
não estava instalado naquele ambiente. Execute a bateria integral antes de
considerar a migração definitivamente encerrada.

Isso não foi uma auditoria completa: somente os dois métodos citados foram
extraídos. Outras checagens de sinal ou limite podem continuar em
`TelaGerard`. Qualquer nova extração é mudança arquitetural explícita e requer
autorização própria.

## Eixo dos inteiros é exploratório (decisão da usuária, 2026-09-29)

"Mexer no eixo é mecanismo exploratório, não precisa persistir." A reta não
grava na tentativa. A web projeta o dependente pela relação estrutural do
domínio (`/api/acoes/projetar-eixo` → `CatalogoRelacoesEstruturaisAditivas`),
sem cálculo no cliente; o domínio do papel dependente recusa projeções
inválidas (ex.: medida negativa). A antiga rota de persistência
(`ajustar-valor-eixo`) foi removida.

## Após a conclusão, toda modificação é exploratória (decisão, 2026-09-29)

"Depois do diagrama ficar azulzinho, toda modificação é exploratória." O modo
exploratório só termina quando **uma nova situação é sorteada**; restaurar não
o encerra. "Não lógica fora do proprietário": o dono é
`TentativaModelagemAditiva` (uma instância por situação sorteada, criada em
`finalizarCarregamentoSituacao`). Na primeira conclusão, `encerrarPorConclusao`
encerra a tentativa e os `PapelQuantitativo` participantes (papéis criados
depois herdam via `incorporar`). A partir daí: `constituir(registro)` não
constitui nenhuma ação (log e Modelador recebem apenas o que a tentativa
constitui, por `persistirAcaoDaTentativa`); `PapelQuantitativo` ignora
submissões e respostas Sim/Não; `IncognitaQuantitativa` não avalia
correspondência (regra `regra.incognita.exploracaoAposConclusao`); a
restauração não gera registro nem desfaz o encerramento. A Main só materializa
o visual: arrastar tira o azul e voltar à configuração correta devolve o azul
em silêncio (sem tip nem sequenciador). Não há trava em controlador
(`foiAtingida`/`RETOMADA_EXPLORACAO` foram revertidos). Na web, após a
conclusão só restam sorteios, reiniciar e o eixo (projeção exploratória).
Desde a persistência web (mesmo dia), a web usa o mesmo dono: cada sorteio
cria uma `TentativaModelagemAditiva` (`EscopoTentativaWeb`), os papéis dos
serviços web são incorporados a ela e `ServicoSorteioAtividadeWeb` a encerra
quando a atividade da categoria fica concluída; após isso nada é gravado e a
web não pergunta Sim/Não (o cliente só pergunta quando o servidor devolve a
pergunta).
Evidência: `tests/java/TesteConclusaoAtingidaExploratoria` (domínio) e
`tests/graphical/TesteRobotExploracaoAposConclusao` (protocolo real: sem
pergunta, sem registro de incógnita, restauração após o azul 0→0 registros
contra 0→1 antes do azul).

## Restauração web antes da recriação dos papéis (2026-09-29)

`EscopoTentativaWeb` conhece por `incorporar` os papéis da atividade corrente
e entrega essa fotografia a `TentativaModelagemAditiva.restaurar`, que captura
e encerra as sequências abertas antes de `ServicoAtividadeWeb.reiniciar`
recriar os objetos. A mudança não altera
valores reconciliados, fórmulas ou o modo exploratório após a conclusão.

Evidência: `TesteRestauracaoWebEncerraSequencia` aprovado e validação
Playwright `e2e_persistencia_modelagem_web.mjs` aprovada, incluindo o fluxo
de incógnita inteira de Transformação de medidas até o estado concluído.
