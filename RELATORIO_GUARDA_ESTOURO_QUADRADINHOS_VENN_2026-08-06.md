# Relatório: guarda de estouro de int em ScaffoldingLimiteQuantidadeVenn (botões +/- dos quadradinhos)

Data: 2026-08-06

## Contexto

Continuação da série de guardas de estouro desta sessão (piloto:
`RELATORIO_GUARDA_ESTOURO_INTEIRO_2026-08-06.md`; produção:
`RELATORIO_GUARDA_ESTOURO_PRODUCAO_2026-08-06.md`; curadoria:
`RELATORIO_LIMITE_DIGITOS_CURADORIA_2026-08-06.md`). A usuária pediu:
"Manter consistência do +infinito e -infinito nos botões de + e - dos
quadradinhos."

## Investigação

Os botões "+"/"-" dos quadradinhos (unidades do diagrama complementar)
passam por dois caminhos em `Main.java`:

1. **Contagem de quadradinhos em si**
   (`adicionarQuadradinhoAoAgrupamentoInterno`/
   `removerQuadradinhoDoAgrupamentoInterno`, e a variante assinada
   `alterarValorAssinadoTransformacao`) — usa `ArrayList.size() + 1` ou
   `valorAnterior ± 1`. Incrementos de 1 por clique, na prática imune a
   estouro (precisaria de ~2 bilhões de cliques). Já delega a
   `EstadoSemanticoCompartilhado` (guardado desde o passo anterior) para
   sincronizar o estado semântico.

2. **Cálculo do limite curado que trava o botão**
   (`obterLimiteSemanticoCuradoDoAgrupamento` →
   `ScaffoldingLimiteQuantidadeVenn.resolverLimite`/`resolverSoma`) —
   este é o método que decide até quanto o "+" pode ir antes de travar
   (`podeAdicionarUnidade()` em `RepresentacaoVennEditavel`, que consulta
   `obterLimiteSemantico()`). `resolverSoma` fazia `a.intValue() +
   b.intValue()` e `c.intValue() - b.intValue()` sem proteção — mesma
   classe de bug já corrigida em outros lugares.

Os valores de entrada vêm dos campos curados da situação (via
`obterValorCuradoPorIndiceEChave`), hoje limitados a 2 dígitos (≤99)
pelo gate de curadoria do passo anterior — exceto o único registro
legado (SP_0008, 417, já `validada=false`). Ou seja, o estouro literal
de `int` não é alcançável com os dados atuais. Mesmo assim, corrigido
por consistência (mesmo raciocínio já usado para justificar a correção
em produção): um estouro aqui não travaria com erro — escreveria um
limite estourado (positivo virando negativo ou vice-versa), que se
comportaria como um "+infinito" espúrio (limite nunca atingido, "+"
libera adição ilimitada) ou "-infinito" espúrio (limite negativo,
`Math.max(0, limite)` trava tudo em zero, "+" nunca funciona) — o efeito
exato que a usuária descreveu.

`atingiuLimite(int, Integer)`, na mesma classe, tem uma chamada a
`Math.abs(limiteCurado.intValue())` que também estouraria silenciosamente
se `limiteCurado == Integer.MIN_VALUE` (`Math.abs` do mínimo permanece
negativo) — mas confirmado novamente como código morto (nenhum call site
em todo o `src`), não alterado.

## O que mudou

`ScaffoldingLimiteQuantidadeVenn.resolverSoma`: as 3 operações passaram a
usar `Math.addExact`/`Math.subtractExact` dentro de um try/catch
`ArithmeticException`. Em caso de estouro, a incógnita permanece
irresolvida (`null`) em vez de receber um valor estourado — mesmo
tratamento que o método já dava quando as entradas eram `null` (situação
não determinável, silenciosamente ignorada). Isso propaga para
`resolverLimite`, que devolve `null` (sem limite resolvido) em vez de um
limite estourado; o botão "+"/"-" trata `null` como "sem limite
semântico" (mesmo comportamento de hoje quando a curadoria não define um
dos três papéis).

## Verificação

- Projeto completo compilado (435 arquivos): **0 erros**.
- Verificação dirigida (script ad-hoc, fora do harness permanente):
  - Caso normal (3 + 4): resolve `7`, sem mudança de comportamento.
  - Soma estourando (2000000000 + 2000000000): resolve `null` em vez de
    `-294967296`.
  - Subtração estourando (`Integer.MIN_VALUE - 2000000000`): resolve
    `null` em vez de um valor positivo espúrio por wraparound.
  - Papel já conhecido (não precisa resolver): devolve o valor direto,
    sem ser afetado por estouro em outro papel do mesmo triplete.

## Escopo

Só `ScaffoldingLimiteQuantidadeVenn.java`. Nenhuma mudança em `Main.java`
nem em `RepresentacaoVennEditavel`/`RepresentacaoComUnidadesAdicionaveis`
— a mudança é interna ao cálculo do limite; o contrato (`Integer` ou
`null`) não mudou.
