# Relatório de correção — botão Novo diagrama e variedade mínima

Data: 14 de julho de 2026  
Vínculo metodológico: desdobramento operacional do ciclo C33

## Problema observado

Na aba **Construir situação-problema**, o botão **Novo diagrama** podia apresentar novamente a mesma situação-problema. Em instalações cuja curadoria possuía apenas uma situação completamente validada e preenchida, a repetição era permanente: o botão executava o sorteio, mas o conjunto de candidatas continha um único elemento.

A aparência resultante era a de um botão sem funcionamento, embora o evento estivesse conectado ao método de recarga.

## Causa

A primeira implementação usava exclusivamente situações que satisfaziam simultaneamente estes critérios:

- situação marcada como validada na curadoria;
- idioma selecionado;
- categoria inicialmente suportada pela montagem;
- três valores do diagrama completamente preenchidos.

Esse filtro é necessário para preservar a curadoria como fonte da verdade. Entretanto, durante a fase inicial do corpus, ele pode produzir apenas uma atividade disponível. Além disso, a seleção aleatória permitia repetição imediata quando havia mais de uma candidata.

## Correção implementada

A correção possui duas partes complementares:

1. **Seleção sem repetição imediata.** A aba registra o identificador e a categoria da última situação. Ao pressionar **Novo diagrama**, prioriza uma situação diferente e, quando houver opção, também uma categoria diferente.
2. **Catálogo mínimo de referência.** Foi criado um catálogo interno com três atividades explicitamente controladas em português, inglês e francês: composição, transformação e comparação de medidas. Todas usam os valores 4, 7 e 11 e preservam personagens, objeto e proximidade linguística, variando a relação semântica.

As situações validadas pelo pesquisador continuam tendo precedência e são combinadas ao catálogo sem duplicação. O catálogo não modifica o arquivo de curadoria nem transforma automaticamente situações não validadas em situações curadas.

## Relação com a intervenção do pesquisador

O catálogo não introduz um novo princípio teórico. Ele operacionaliza a intervenção já registrada no ciclo C33: a atividade precisa oferecer alternativas estruturalmente variadas, mas semanticamente próximas, para evitar resposta por eliminação superficial.

Os exemplos de referência foram mantidos deliberadamente próximos:

- mesmos personagens;
- mesmo objeto;
- mesmos valores;
- três relações distintas: composição, transformação e comparação.

Assim, a variedade do botão não converte a atividade em uma sucessão de contextos desconectados nem em um “jogo dos sete erros”.

## Arquivos alterados

- `src/gerard/campoaditivo/montagem/CatalogoAtividadesMontagemPadrao.java`
- `src/gerard/campoaditivo/montagem/TelaMontagemSituacao.java`
- `scripts/testes/TesteMontagemSituacao.java`
- `scripts/testes/TesteAbaMontagem.java`
- `scripts/verificar_regressao_gerard.py`

## Verificação

- compilação dos 113 arquivos Java aprovada;
- JAR reconstruído;
- teste da montagem aprovado com 99 verificações;
- teste de interface confirmou que **Novo diagrama** não repete imediatamente a situação e alterna a categoria quando possível;
- regressão estrutural geral aprovada;
- curadoria e idiomas preservados.

## Integridade da versão verificada

SHA-256 do JAR: `70AFD37FB09B10806314F2CBBE11F32DE84E04D25B8F14D683C6B2B6DECDCEFE`.
