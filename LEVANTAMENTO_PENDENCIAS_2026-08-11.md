# Levantamento de pendências — 2026-08-11

Auditoria da versão mais recente do repositório (`C:\Users\cecomp\Documents\aemq\git\Gerard`,
branch `codex/fase-7-3-handler-elemento-texto`), cruzando os cinco arquivos
`TAREFA_PENDENTE_*.md`, o roteiro de `gerard-handlers-de-interacao/SKILL.md`
(atualizado hoje) e o estado real do código (`Main.java`) contra o que cada
documento afirma. Ordenado da mais simples para a mais complexa
(esforço/risco estimado, não urgência). Nenhum item abaixo está autorizado a
começar — levantamento apenas, mesma convenção do levantamento anterior
(`LEVANTAMENTO_PENDENCIAS_2026-08-07.md`).

Os cinco `TAREFA_PENDENTE_*.md` de 07/08 estão, em sua maioria, encerrados —
confirmado por leitura completa dos cinco nesta auditoria. Restam quatro
lacunas reais, três delas registradas dentro desses próprios arquivos como
"não resolvido aqui", mais um item novo (fora desses cinco) sobre o roteiro
de extração de handlers.

## 1. `AG_EME` — mensagem explicativa ainda mínima — IMPLEMENTADO (2026-08-16)

`TAREFA_PENDENTE_FLUXO_TENTATIVAS_E_SCAFFOLDING.md` classificava `AG_EME`
("exibir mensagem explicativa") como **parcialmente implementado**:
`Main.mostrarDicaOperacaoIncognita()` (`Main.java:6411-6416`) mostrava só uma
frase fixa (`ui.hint.chooseOperation` → "escolha soma ou subtração e tente
de novo"), sem explicação conceitual do porquê.

Decisão da usuária (2026-08-16): a explicação deve morar no objeto rico
("preserve a localidade do conhecimento, pois foi pensada para essa
finalidade: encapsular conhecimento para agir como objeto rico"), e deve
explicar conceitualmente o objeto rico manipulado via protocolo de mouse.
Parte1 e Parte2 compartilham o mesmo texto conceitual ("Parte") — são
conceitualmente iguais.

Implementado: `DescritorRepresentacaoPapel` ganhou `chaveExplicacaoConceitual`;
as fábricas já existentes de `PapelQuantitativo` (`PapelQuantitativo.parte1/
parte2/todo`, `FabricaPapeisTransformacaoMedidas`, `FabricaPapeisComparacaoMedidas`,
`FabricaPapeisComposicaoDeTransformacoes`, `FabricaPapeisTransformacaoDeRelacao`,
`FabricaPapeisComposicaoDeRelacoes` — já existiam como piloto testado, só não
estavam ligadas à UI para esta finalidade) passaram a fornecer essa chave;
o novo `CatalogoExplicacoesConceituaisPapel` resolve a chave viva do papel
(a mesma que `Main.obterPapelIncognitaAtual()` já usa) para a chave de
explicação, consultando os objetos ricos já existentes, sem duplicar
conhecimento. `mostrarDicaOperacaoIncognita()` passou a mostrar a
explicação conceitual antes da instrução operacional já existente. Ver
`RELATORIO_ITEM1_AG_EME_EXPLICACAO_CONCEITUAL_2026-08-16.md` para os
detalhes completos e a verificação.

## 2. Flag de teste `EXIBIR_DIAGRAMA_COMPLEMENTAR_SEMPRE_PARA_TESTES` ainda ativa

`TAREFA_PENDENTE_FLUXO_TENTATIVAS_E_SCAFFOLDING.md` registra que essa
constante (`Main.java:8283`) foi ativada **temporariamente**, a pedido da
usuária, em 07/08, para testar a interação com o diagrama complementar sem
precisar errar 3 vezes a cada verificação. Confirmado hoje por leitura direta
do código: `EXIBIR_DIAGRAMA_COMPLEMENTAR_SEMPRE_PARA_TESTES = true` continua
ativa (`Main.java:8283`, usada em `Main.java:8307`) — o diagrama complementar
segue aparecendo sempre, não só na 3ª rejeição, quatro dias depois do pedido
original.

- **Trabalho**: apagar a constante e o `||` que a usa em
  `deveExibirDiagramaComplementar()` restaura o comportamento definitivo,
  sem nenhuma outra mudança de código (já documentado assim na própria
  tarefa pendente).
- **Risco**: nenhum tecnicamente. Falta só confirmar com a usuária se a fase
  de teste dela já terminou.

## 3. Throttling de log não decidido — arraste contínuo das barras de Comparação

`TAREFA_PENDENTE_LOG_CONSISTENCIA_AUTOMATICA.md`, seção "Implementação",
registra um caveat explicitamente não resolvido: o controle de barras da
Comparação, durante arraste contínuo, pode gerar uma linha de log
`CONSISTENCIA_AUTOMATICA` por passo do gesto (o valor dependente muda a cada
pixel arrastado). Throttling de log durante gesto contínuo é chamado de
"decisão de política separada, não tomada nesta tarefa" — continua não
tomada.

- **Trabalho**: exige decidir uma política (ex.: só logar ao soltar o mouse,
  ou agregar por janela de tempo) antes de qualquer código — mais decisão de
  design do que implementação mecânica.
- **Risco**: baixo a médio — não quebra nada hoje, mas pode estar inflando o
  volume de log de pesquisa sem que ninguém tenha decidido que está ok.

## 4. Representação complementar própria para as categorias de Relações — IMPLEMENTADO (2026-08-17)

`TAREFA_PENDENTE_REPRESENTACAO_COMPLEMENTAR_RELACOES.md` estava "não
iniciada". Decisão pedagógica tomada pela usuária em 2026-08-16/17 (mesmo
processo de perguntas de confirmação usado para `AG_AE`): um painel de
eixo dos inteiros por papel (3 por categoria), todos visíveis ao mesmo
tempo, manipuláveis, posicionados acima/abaixo de cada papel do diagrama
de Vergnaud, reaproveitando a classe já existente
`ScaffoldingGraficoInteiros` (várias instâncias novas, não um widget novo).
Visibilidade: decisão revista pela usuária no mesmo dia, depois de ver o
comportamento real — em vez do mesmo gatilho de todo outro material
concreto do app (só depois da 3ª tentativa rejeitada), os painéis de
Relações ficam sempre visíveis quando a categoria ativa é uma das duas de
Relações, sem esperar nenhuma tentativa rejeitada. Também nesse dia: o
eixo único antigo (menu de escolha de sinal) deixou de aparecer nas
categorias de Relações, já que os painéis novos por papel cobrem o mesmo
lugar — nas demais categorias (Comparação de Medidas etc.) continua igual.
Ver `RELATORIO_ITEM4_PAINEIS_EIXOS_RELACOES_2026-08-17.md` para os
detalhes completos e a verificação (estrutural, sem JDK neste ambiente —
falta validação real).

## 5. Roteiro de extração de handlers de interação — próximo protocolo não escolhido

`gerard-handlers-de-interacao/SKILL.md` (atualizado hoje): Fase 7.2
(`HandlerInteracaoItemTextoArrastavel`) e Fase 7.3
(`HandlerInteracaoElementoTextoMovel`) estão extraídas e validadas pelo
harness Robot real de 11/08. O passo 3 do roteiro — repetir a extração, um
protocolo de cada vez — ainda não tem o próximo protocolo escolhido. Restam,
de `mousePressed`/`mouseDragged`/`mouseReleased`/`mouseMoved` em
`Main.TelaGerard`:

- quadradinhos do diagrama Venn;
- eixo de inteiros;
- elementos de Vergnaud.

- **Trabalho**: maior escopo entre os itens deste levantamento — cada
  protocolo extraído precisa de handler novo, testes, proteção estrutural no
  verificador de regressão e relatório próprio (mesmo padrão das duas fases
  já concluídas).
- **Risco**: médio — mesma cautela já registrada na skill: `mousePressed`
  concentra lógica de várias décadas de decisões de UI; nenhuma extração
  deve começar sem escolha explícita de qual protocolo vem primeiro.

---

*Não incluí como pendência os dois `ROTEIRO_TESTE_VISUAL_*.md` (conclusão
por categoria, tabuleiro de transformação) — são roteiros de verificação
manual reutilizáveis, sem um campo de status que indique se/quando foram
executados pela última vez. Vale confirmar com a usuária se ainda refletem o
comportamento atual antes de tratá-los como pendência real ou descartá-los
da lista.*
