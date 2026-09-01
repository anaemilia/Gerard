# Fase 7.6 — protocolo do eixo flutuante de inteiros

Data: 2026-08-28

## Escopo confirmado

A alteração abrange somente o eixo flutuante único usado como apoio à
representação de números inteiros. Os painéis individuais associados aos
papéis das categorias de Relações permaneceram inalterados e constituem um
protocolo distinto.

## Distribuição de responsabilidades

- `HandlerInteracaoEixoInteiros` mantém o ciclo portátil de pressionamento,
  movimento, conclusão e cancelamento.
- `AlvoInteracaoEixoInteiros` é a porta neutra entre o protocolo e uma
  representação concreta.
- `AdaptadorInteracaoEixoInteiros` traduz o protocolo para
  `ScaffoldingGraficoInteiros`.
- `FonteGeometriaInteracaoEixoInteiros` fornece ao adaptador as dimensões e a
  área reais vigentes no momento de cada evento. `Rectangle`, layout e
  hit-testing permanecem no lado desktop.
- `Main.TelaGerard` compõe os participantes e roteia os resultados. Nela
  permanecem a autorização contextual da interação, a materialização visual,
  o registro factual e a solicitação de sincronização.

O handler não possui conhecimento matemático, não decide o valor correto e
não interpreta o Modelo do Usuário. A autoridade sobre o valor e suas regras
permanece nos objetos semânticos. O estado compartilhado oferece a fotografia
comum; o coordenador de sincronização somente ordena o transporte dessa
fotografia para as representações, sem se tornar proprietário do conhecimento.

### Achado arquitetural fora do escopo desta fase

`CoordenadorSincronizacaoRepresentacoes` respeita a fronteira acima: ele apenas
ordena a aplicação de um snapshot e evita propagação recursiva. Entretanto,
`EstadoSemanticoCompartilhado` ainda instancia
`ConversorValoresEstadoAditivo` e `ResolvedorRelacoesEstruturaisAditivas` e
executa a resolução automática. Portanto, a implementação atual ainda não
atinge integralmente a arquitetura-alvo na qual cada objeto semanticamente
rico possui a interpretação e as consequências de sua própria mudança. Essa
questão deve ser tratada como pendência separada, mediante rastreamento dos
proprietários semânticos envolvidos; não foi misturada à extração do protocolo
de mouse desta fase.

## Proteções determinísticas

O verificador passou a exigir a existência da porta, do handler, do adaptador,
da fonte de geometria e do teste específico. Também impede que o protocolo
portátil passe a depender de Swing, AWT, `Main`, `Rectangle`, dimensões de tela
ou `ScaffoldingGraficoInteiros`.

Os limites dos métodos centrais foram reduzidos para a fotografia atual:

- `mousePressed`: 448 linhas (antes, 453);
- `processarMovimentoArraste`: 68 linhas (antes, 69);
- `mouseReleased`: 110 linhas (antes, 111).

## Verificação

- build Ant: 500 fontes compiladas e JAR gerado;
- `TesteHandlerInteracaoEixoInteiros`: aprovado;
- verificador estrutural completo: aprovado sem falhas;
- linha de base Windows: 86 testes executados, 86 aprovados, quatro testes
  exclusivamente gráficos compilados e classificados como dependentes de
  display;
- fonte curada: 210 situações, 37 colunas e exatamente seis categorias;
- Robot `TesteMonkeySemiGuiado`: 60 segundos, seed `20260828`, 56 iterações e
  zero erros. O processo foi encerrado depois de o próprio harness imprimir a
  conclusão, pois uma thread AWT permaneceu viva.

Durante a linha de base foi corrigido um teste antigo que ainda chamava o
método removido `Main.registrarTentativaIncognita`. O preparo do teste agora
registra as três rejeições diretamente em `PapelQuantitativo`, proprietário
atual desse estado. Nenhum comportamento da aplicação foi alterado por essa
correção.
