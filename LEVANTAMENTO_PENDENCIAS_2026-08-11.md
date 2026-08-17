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

## 1. `AG_EME` — mensagem explicativa ainda mínima

`TAREFA_PENDENTE_FLUXO_TENTATIVAS_E_SCAFFOLDING.md` classifica `AG_EME`
("exibir mensagem explicativa") como **parcialmente implementado**:
`Main.mostrarDicaOperacaoIncognita()` (`Main.java:6411-6416`) mostra só uma
frase fixa (`ui.hint.chooseOperation` → "escolha soma ou subtração e tente
de novo"), sem explicação conceitual do porquê. Confirmado hoje: o método
continua exatamente assim, sem mudança desde 07/08.

- **Trabalho**: redação de conteúdo pedagógico dentro de um mecanismo que já
  existe (mesmo padrão do rascunho de `ui.notice.attemptLimitReached`
  já aceito). Baixo risco técnico.
- **Risco**: baixo tecnicamente; depende de decisão de conteúdo pedagógico,
  não decidida ainda.

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

## 4. Representação complementar própria para as categorias de Relações — não iniciada

`TAREFA_PENDENTE_REPRESENTACAO_COMPLEMENTAR_RELACOES.md`: status explícito
"não iniciada". `TRANSFORMACAO_RELACAO` e `COMPOSICAO_RELACOES` mostram o
painel complementar em branco desde 08/08 (a pedido da usuária, que removeu
o diagrama genérico sem função mas não pediu o desenho da representação
definitiva). Nenhuma decisão pedagógica foi tomada sobre o que "material
concreto" significa para uma relação/número relativo (podem ser negativos),
diferente das medidas absolutas já representadas nas outras categorias.

- **Trabalho**: exige decisão pedagógica nova antes de qualquer código —
  mesmo processo de perguntas de confirmação já usado para `AG_AE`.
- **Risco**: médio — decisão de design ainda não iniciada, escopo (o que
  desenhar) não está definido.

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
