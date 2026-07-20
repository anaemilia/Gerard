# C158 — Ciclo generalizado da incógnita

## Regra consolidada

A modelagem só é concluída quando o item originalmente desconhecido percorre o ciclo completo:

1. a interrogação `?` é posicionada no papel semântico correto;
2. os demais papéis da categoria estão semanticamente corretos;
3. o usuário substitui o próprio item `?` por um número usando o protocolo de mouse/texto;
4. somente então a sincronização automática pode propagar esse valor às demais representações e a conclusão progressiva é ativada.

## Implementação

- `FaseConclusaoModelagem`: `INCOMPLETA`, `AGUARDANDO_PREENCHIMENTO_INCOGNITA` e `CONCLUIDA`.
- `PoliticaPreenchimentoIncognita`: centraliza a proteção do papel desconhecido.
- `EstadoPosicionamentoModelagem`: registra se o item era a incógnita original e se foi preenchido pelo protocolo mouse/texto.
- `ItemTextoArrastavel`: registra explicitamente a conclusão do protocolo.
- `EstadoSemanticoCompartilhado`: recebe um índice protegido e não resolve automaticamente a incógnita antes da liberação.
- `AvaliadorConclusaoModelagem`: exige exatamente uma incógnita original preenchida pelo protocolo.

## Abrangência

A regra usa papéis semânticos, não categorias fixas. Portanto, vale para composição, transformação, comparação, transformações compostas e relações, independentemente de a incógnita ocupar estado inicial, transformação, estado final, parte, todo, referido, referendo, valor relativo, relação ou estado intermediário.

## Preservações

- regra do primeiro posicionamento;
- bloqueio das representações complementares antes do Vergnaud;
- sincronização entre texto, Vergnaud, eixo, barras, coleções e processo concreto;
- números naturais e inteiros conforme o papel;
- grandezas de contagem e monetária;
- feedback de erro e permanência do item incorreto;
- conclusão progressiva com selo e tip;
- fluxo Sim/Não e Sortear na mesma categoria;
- dados curados inalterados.
