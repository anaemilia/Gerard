# Relatório da Fase 7.8 — seletores de operação das Relações

## Escopo autorizado

Foi extraído somente o protocolo de clique dos dois seletores de operação que
estava inline em `Main.TelaGerard.mousePressed`. Nenhum outro protocolo de
interação foi alterado.

## Distribuição das responsabilidades

- `ResultadoEscolhaOperacaoModelagem`: fato tipado produzido pelo critério de domínio;
- `AlvoSeletoresOperacaoRelacoes`: porta neutra da representação;
- `HandlerInteracaoSeletoresOperacaoRelacoes`: prioridade do primeiro seletor
  e liberação ordenada do segundo;
- `AdaptadorSeletoresOperacaoRelacoes`: tradução dos widgets desktop;
- `CasoDeUsoSelecaoOperacoesRelacoes`: caso de uso portátil que sequencia portas
  estreitas, sem conhecer Swing, som ou logger;
- `ApresentadorFeedbackEscolhaOperacaoSwing`: materialização exclusiva do som;
- `PersistidorAcaoInstrumentalLogGerard`: encaminhamento exclusivo do registro
  factual já constituído;
- `Main.TelaGerard`: composição dos colaboradores e uma chamada de roteamento.

## Matriz de localidade

| Conhecimento ou efeito | Proprietário autorizado | Entrada | Saída | Consumidor | Tecnologia permitida |
|---|---|---|---|---|---|
| Critério soma/subtração | proprietário semântico da escolha | categoria, tipo, escolha e esperado | resultado factual | caso de uso | sem UI |
| Ordem dos dois seletores | handler do protocolo | coordenadas neutras e porta dos seletores | escolha constituída ou ausência | caso de uso | sem Swing/AWT |
| Geometria dos seletores | representação desktop | coordenadas e widgets | resultado do hit-test | handler pela porta | AWT somente no adaptador/widget |
| Decisão de feedback | proprietário do resultado | correção já avaliada | `NENHUM` ou `SOM_ERRO` | materializador | sem Swing |
| Som | adaptador Swing | decisão abstrata | efeito sonoro | usuário | Swing/AWT permitido |
| Registro instrumental | proprietário semântico | ação constituída | registro factual completo | persistidor | sem I/O |
| Persistência | infraestrutura de log | registro pronto | confirmação técnica | logger | I/O permitido |
| Limpeza de foco | porta específica da tela | escolha concluída | foco transitório limpo | tela | plataforma na implementação |
| Reavaliação da conclusão | porta específica da tela | escolha concluída | conclusão reavaliada | modelagem | sem recalcular C/E da escolha |

### Localidade corrigida incrementalmente

`CriterioOperacaoModelagem`, no domínio, passou a avaliar a tentativa em
`OperacaoAditiva` e a produzir `ResultadoEscolhaOperacaoModelagem`, inclusive
C/E, diagnóstico, `action_id` e feedback abstrato. O resultado não depende de
`OpcaoOperacaoCuradoria`. `AvaliacaoEscolhaOperacaoRelacao` permanece somente
como ponte compatível: lê os campos do registro curado legado, converte as
duas operações e fornece ao critério o contexto factual já observado.

A realização localizada foi retirada da avaliação e passou a
`campoaditivo.representacao.texto.RealizadorTextoExplicacaoOperacaoRelacao`,
compartilhado pelas duas plataformas.

O handler e o serviço de aplicação não importam AWT, Swing, logger,
scaffolding nem classes visuais. O critério de domínio produz o registro
factual completo; a ponte de curadoria não calcula C/E e os adaptadores não
recalculam seus campos. Exemplos teóricos não foram convertidos em
comportamento novo.

## Comportamento preservado

- o primeiro seletor tem prioridade sobre o segundo;
- o segundo só recebe cliques depois de a primeira resposta estar correta;
- erro continua produzindo apenas som e a explicação local do widget;
- cada escolha consumida continua produzindo um único registro C/E;
- foco transitório é limpo e a conclusão é reavaliada após a escolha;
- os textos e marcadores dos registros foram preservados.

## Verificações executadas

- grafo de skills: válido, 22 nós e 90 relações;
- build Ant: 598 fontes compiladas e JAR gerado;
- `TesteHandlerInteracaoSeletoresOperacaoRelacoes`: aprovado;
- ratchet de `mousePressed`: reduzido de 406 para 350 linhas;
- verificações estruturais da Fase 7.8: aprovadas.
- checker genérico de localidade: autoteste aprovado e nenhuma violação nova;
  depois da extração da persistência concreta do serviço de sorteio web, a
  linha de base caiu de quatro para três ocorrências. As três ligações web com
  scaffolding concreto permanecem registradas como dívida, sem serem
  declaradas arquitetura correta.

## Continuação: registro da atividade web

`ServicoSorteioAtividadeWeb` deixou de conhecer `LoggerInteracaoGerard` e
`ControladorContextoSituacao`. O caso de uso agora depende de
`PortaRegistroAtividadeWeb`; `RegistradorAtividadeWebLogGerard`, no pacote do
pesquisador, adapta essa porta à infraestrutura existente, e
`ServidorPrototipoWeb` realiza a composição. A porta nula mantém os testes e
usos isolados sem efeito de persistência. A política de ajuda, a seleção
aleatória e o conteúdo das respostas não foram alterados.

O verificador completo ainda reporta cinco falhas normativas preexistentes em
skills de narrativa rica, fora do escopo desta fase. Não foi executado um
harness Robot específico; por isso, a fase está verificada isoladamente, não
declarada como validação comportamental integral.
