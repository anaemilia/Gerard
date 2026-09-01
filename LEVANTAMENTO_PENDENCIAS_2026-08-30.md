# Levantamento de pendências — 2026-08-30

Auditoria do repositório (`C:\Users\cecomp\Documents\aemq\git\Gerard`,
branch `codex/auditoria-arquitetural-pos-505553d`), cruzando o commit mais
recente (`101dbfc`, 25/08), os arquivos ainda não commitados no diretório
de trabalho, o roteiro de `gerard-handlers-de-interacao/SKILL.md` e o
levantamento anterior (`LEVANTAMENTO_PENDENCIAS_2026-08-11.md`). Ordenado
da mais simples para a mais complexa (esforço/risco estimado, não
urgência). Nenhum item abaixo está autorizado a começar — levantamento
apenas, mesma convenção dos levantamentos anteriores.

Do levantamento de 11/08, os cinco itens estão encerrados. O item 3
(throttling de log) foi implementado em 16/08; o item 5 foi desdobrado e
executado nas Fases 7.4, 7.5 e 7.6. Permanecem abaixo apenas decisões ou
frentes posteriores, já reconciliadas com as verificações atuais.

## 1. Commitar a Fase 7.6 (handler do eixo de inteiros)

Trabalho já implementado e verificado: `RELATORIO_FASE_7_6_HANDLER_EIXO_
INTEIROS_2026-08-28.md` registra build Ant (500 fontes), `TesteHandler
InteracaoEixoInteiros` aprovado, verificador estrutural sem falhas, linha
de base de 86 testes e harness Robot (`TesteMonkeySemiGuiado`, 60s, seed
`20260828`, 56 iterações, zero erros). Os arquivos novos seguem não
commitados: `HandlerInteracaoEixoInteiros`, `AlvoInteracaoEixoInteiros`,
`AdaptadorInteracaoEixoInteiros`, `FonteGeometriaInteracaoEixoInteiros` e
o teste correspondente, além da atualização do roteiro em
`gerard-handlers-de-interacao/SKILL.md`.

- **Trabalho**: revisar o conjunto de arquivos e commitar — sem escrever
  código novo.
- **Risco**: nenhum tecnicamente, já verificado. Cuidado operacional: o
  `git status` deste ambiente mostra ~1100 arquivos como modificados por
  diferença de fim de linha (CRLF/LF) entre este ambiente Linux e o
  checkout Windows — não são mudanças reais e não devem entrar no commit;
  a seleção de arquivos precisa ser explícita (`git add` nominal), nunca
  `git add -A`/`git add .`.

## 2. Encerrada — consolidação do log no arraste das barras de Comparação

A versão atual atualiza o estado e as representações a cada movimento, mas
retém somente o último `Snapshot` para o log
`CONSISTENCIA_AUTOMATICA`. O evento é descarregado no `mouseReleased`; um
novo `mousePressed` oferece término defensivo para um arraste interrompido.
O verificador determinístico protege os dois caminhos. Portanto, não existe
trabalho de implementação pendente neste item.

## 3. Encerrada — validação com JDK dos painéis por papel das Relações

A validação real já ocorreu. A linha de base atual compilou 529 fontes e
100 testes; 95 testes executáveis foram aprovados, cinco testes gráficos
foram compilados e excluídos da automação, e não houve falhas.
`TestePaineisEixosRelacoes` foi executado com êxito. Não existe alteração de
código pendente neste item.

## 4. Frente verificada — situação-problema rica e curadoria narrativa

A frente já possui os seis vínculos canônicos entre as categorias de
Vergnaud e as situações ricas, persistência complementar em XML, editor
dedicado, promoção ao catálogo reservada ao pesquisador humano e leitura
retrocompatível das situações curadas antigas. Os Itens 37 a 42 do
verificador determinístico protegem esses contratos. A linha de base atual
foi aprovada, e `TesteDialogoCuradoriaNarrativaRica` também foi executado
manualmente em ambiente gráfico com êxito.

Não há requisito funcional já decidido aguardando implementação nessa
frente. Uma nova evolução precisa partir de objetivo explícito. O risco
residual é apenas operacional: os arquivos continuam não commitados e não
devem ser misturados inadvertidamente com alterações alheias.

## 5. Escolher o próximo protocolo do roteiro de handlers (Fase 7.7)

Com a Fase 7.6 concluída, `gerard-handlers-de-interacao/SKILL.md` registra
explicitamente que os painéis individuais das categorias de Relações "não
pertenciam a este recorte e só podem ser revistos como outro protocolo,
com nova autorização explícita" — ainda não escolhido nem autorizado.

- **Trabalho**: maior escopo entre os itens deste levantamento — qualquer
  protocolo novo precisa de handler, testes, proteção estrutural no
  verificador de regressão e relatório próprio, mesmo padrão das seis
  fases já concluídas.
- **Risco**: médio — mesma cautela já registrada na skill: `mousePressed`
  ainda concentra 448 linhas de lógica de várias décadas de decisões de
  UI; nenhuma extração deve começar sem escolha explícita de qual
  protocolo vem primeiro.
