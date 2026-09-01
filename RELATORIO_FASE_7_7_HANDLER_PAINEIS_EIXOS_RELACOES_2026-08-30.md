# Fase 7.7 — protocolo portátil dos painéis de eixo das Relações

Data: 2026-08-30

## Objetivo

Isolar do protocolo de interação o conhecimento específico de Swing/AWT dos
painéis individuais de eixo usados nas categorias de Relações. A alteração
preserva o comportamento corrente: a interface desktop captura o evento e
renderiza a representação; o handler conduz um ciclo portátil de
pressionamento, movimento, conclusão e cancelamento.

## Escopo autorizado

O recorte abrange somente a interação com painéis já revelados: manipulação do
valor, deslocamento do painel e acionamento do controle de ocultação. A lupa
que revela cada painel, os seletores de operação, a sincronização semântica, a
confirmação da incógnita, o registro factual e a escolha de scaffolding não
foram redefinidos.

## Localidade das responsabilidades

- `HandlerInteracaoPaineisEixosRelacoes` mantém o ciclo portátil e depende
  somente de `AlvoInteracaoPaineisEixosRelacoes`.
- `AlvoInteracaoPaineisEixosRelacoes` expressa natureza e modo da interação
  sem tipos de plataforma.
- `AdaptadorInteracaoPaineisEixosRelacoes` traduz o protocolo para a
  representação desktop e concentra `Rectangle`, hit-testing e estado visual.
- `FonteGeometriaInteracaoPaineisEixosRelacoes` entrega dimensões e área reais
  no instante de cada evento; nenhuma coordenada de layout foi copiada para o
  handler.
- `Main.TelaGerard` compõe e roteia os participantes, fornece a autorização
  contextual e solicita a sincronização. A sincronização final continua
  ocorrendo antes da conclusão visual, na mesma ordem da versão anterior.
- Os objetos e relações semânticos continuam proprietários da validade e das
  consequências matemáticas. O handler não conhece Swing/AWT, Modelo do
  Usuário, logs, scaffolding ou regras do domínio.

## Proteções determinísticas

O verificador passou a exigir a porta, o handler, a fonte de geometria, o
adaptador e o teste específico. Também rejeita dependências de Swing/AWT,
`MouseEvent`, `Rectangle`, `Graphics2D`, `Main` ou classes visuais concretas no
protocolo portátil e impede que a `Main` volte a chamar diretamente a mecânica
extraída.

O ratchet dos protocolos foi reduzido para a fotografia corrente:

- `mousePressed`: 442 linhas (antes, 448);
- `processarMovimentoArraste`: 67 linhas (antes, 68);
- os demais limites permaneceram inalterados.

## Verificação

- build Ant: 533 fontes compiladas e JAR gerado;
- verificador estrutural completo: aprovado, nenhuma falha;
- fonte curada: 210 situações, 37 colunas e exatamente seis categorias;
- linha de base Windows: 101 testes compilados, 96 testes executáveis
  aprovados, cinco testes exclusivamente gráficos compilados e classificados
  como dependentes de display, zero reprovação;
- `TesteHandlerInteracaoPaineisEixosRelacoes`: aprovado, cobrindo bloqueio
  contextual, ciclo portátil, ocultação e adaptação da geometria desktop;
- Robot `TesteMonkeySemiGuiado`: 60 segundos, seed `20260830`, 58 iterações e
  zero erros. O processo foi encerrado depois de o próprio harness imprimir a
  conclusão, pois a thread AWT permaneceu viva.

## Resultado

A `Main` deixou de possuir a classificação, o pressionamento, o arraste, a
conclusão e a ocultação particulares desses painéis. O comportamento semântico
e a ordem de sincronização foram preservados. Uma futura interface web ou
mobile poderá implementar outra adaptação da porta portátil sem reutilizar
`Rectangle`, `MouseEvent` ou componentes Swing.
