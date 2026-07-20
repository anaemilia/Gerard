# Relatório de alteração — incremento unitário no referendo da comparação

**Data:** 2026-07-16  
**Ciclo:** C112  
**Base adotada:** C110, indicada pelo especialista como versão visual correta.  
**Tipo de intervenção:** correção semântica, arquitetural e de regressão.

## Problema observado
Na comparação de medidas, a ordem visual da representação complementar é `referido`, `referendo` e `valor relativo`. O estado semântico compartilhado, entretanto, utiliza `referido`, `valor relativo` e `referendo`. A implementação tratava o índice visual como se fosse o índice semântico. Assim, um clique no controle `+` da segunda barra podia ser interpretado como alteração do valor relativo e reconstruir a barra com várias unidades.

## Correção
Foi introduzido um contrato explícito de mapeamento entre ordem visual e ordem semântica. Para comparação de medidas, o mapeamento é:

- visual 0 (referido) -> semântico 0;
- visual 1 (referendo) -> semântico 2;
- visual 2 (valor relativo) -> semântico 1.

O mapeamento é aplicado na identificação do papel, na obtenção do limite curado e na captura do estado a partir do diagrama complementar.

## Diretriz arquitetural
A correção segue a diretriz do projeto:

- interface como contrato;
- classe abstrata para validação e comportamento comum;
- implementações concretas para identidade e comparação;
- fábrica e despacho polimórfico para selecionar o mapeamento por categoria.

## Resultado esperado
Ao clicar uma vez em `+` na barra do referendo, apenas uma unidade é acrescentada. O valor relativo é recalculado a partir das duas barras, e a representação não cria várias unidades de forma inesperada.
