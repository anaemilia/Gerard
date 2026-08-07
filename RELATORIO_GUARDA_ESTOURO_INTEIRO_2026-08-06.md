# Relatório: guarda de estouro de inteiro nas 6 classes RelacaoEstrutural*

Data: 2026-08-06

## O bug encontrado

Levantado pela usuária ("ter cuidado com o +infinito e o -infinito"), e
confirmado empiricamente no piloto antes de qualquer mudança:

```
Composição de Relações: 2000000000 + 2000000000  →  -294967296, estado CONSISTENTE
Composição de Medidas:  2000000000 + 2000000000  →  -294967296, estado CONSISTENTE
```

Estouro silencioso de `int` (wraparound do Java), reportado como
`CONSISTENTE` — ou seja, o sistema devolvia um número **errado** e o
rotulava como correto.

Nas categorias de Medidas o efeito era pior de outra forma: `aplicar(...)`
depois rejeitava o negativo resultante no domínio NATURAIS, e o papel
ficava sem valor (`preenchido=false`) — o sujeito age e nada acontece,
sem nenhum feedback.

**Atingia as 6 categorias igualmente**, não só as de Relações.

## Onde os valores grandes podem entrar (investigação)

- **Eixo x navegável**: já limitado — `limitar(valor, -escala, escala)`,
  com `escala = Math.max(5, |valor inicial|)` em
  `ScaffoldingGraficoInteiros`. O arraste não produz valores grandes.
- **Digitação no texto**: `ServicoQuantidadeContextual.converterParaInteiroLegado`
  usa `intValueExact()` com `ArithmeticException` tratada — rejeita o que
  não cabe em `int`, mas aceita qualquer coisa que caiba (ex.: 2 bilhões).
  É o caminho aberto.
- **Dados curados**: os valores dos enunciados de
  `situacoes_vergnaud.tsv` vão de 0 a 80, com um único 417 — nenhuma
  situação real precisa de mais de 3 dígitos.

## O que mudou

Nas 6 classes `RelacaoEstrutural*` (Composição, Transformação e
Comparação de Medidas; Composição de Transformações, Transformação de
Relação e Composição de Relações):

- `calcularValorAusente` e `recalcularParaConsistencia` passam a usar
  `Math.addExact`/`Math.subtractExact` através de dois helpers privados
  (`resultadoDaSoma`/`resultadoDaSubtracao`). Quando a conta não é
  representável, devolvem `EstadoConsistencia.NAO_RESOLVIVEL_NESTE_ESTADO`
  sem valor calculado — o mesmo estado que `calcularValorAusente` já usava
  para "não há uma incógnita única a resolver". Um valor não representável
  não é erro do estudante nem violação de contrato do chamador: é um
  estado legítimo a comunicar.
- `verificarConsistencia` guarda a soma da mesma forma e devolve
  `NAO_RESOLVIVEL_NESTE_ESTADO` — não dá para afirmar consistência nem
  inconsistência comparando contra um número estourado.
- `diagnosticarValorProposto`:
  - o cálculo do padrão "operação invertida" foi extraído para
    `valorDaOperacaoInvertida(...)`, que devolve `null` quando estoura —
    nesse caso o padrão simplesmente não é reconhecido e o diagnóstico cai
    no genérico `VALOR_INCORRETO`, em vez de comparar contra lixo;
  - a pré-condição ("papelAlvo é a única incógnita") passou a ser
    verificada **diretamente** (via novo helper `contarIncognitas`), não
    mais inferida de `!esperado.temValorCalculavel()`. Isso era necessário
    porque, com a guarda, `calcularValorAusente` também devolve
    `NAO_RESOLVIVEL` por estouro — o que não é violação de contrato do
    chamador e não deve virar a mesma `IllegalStateException`. O caso de
    estouro agora lança `ArithmeticException`, com mensagem própria.

Efeito colateral positivo: como os retornos viraram chamadas a helpers,
as 6 classes ficaram mais curtas e a duplicação da contagem de incógnitas
saiu de `calcularValorAusente`.

## Escopo

Só o pacote piloto `gerard.dominio.campoaditivo`. Nenhuma linha de
`Main.java` tocada.

**Importante — o mesmo bug existe em produção e NÃO foi corrigido aqui:**
`EstadoSemanticoCompartilhado.resolverRelacaoAditiva` (o algoritmo
genérico usado hoje pela UI) continua fazendo `valor(0) + valor(1)` sem
guarda. Corrigir isso é mudança em caminho de produção e não estava no
escopo autorizado desta etapa. Fica registrado como pendência.

## Verificação

Cobertura de teste adicionada às 6 harnesses, seção
`guarda de estouro de int (2026-08-06)`:

- `calcularValorAusente` com 2e9 + 2e9 → `NAO_RESOLVIVEL_NESTE_ESTADO`,
  sem valor calculado (documenta explicitamente o comportamento anterior
  no rótulo do teste: "antes dava -294967296/CONSISTENTE").
- `recalcularParaConsistencia` idem.
- `verificarConsistencia` com soma não representável →
  `NAO_RESOLVIVEL_NESTE_ESTADO`.
- Em `TestePilotoTransformacaoMedidas` (representativo dos 6):
  `aplicar(...)` de um resultado sem valor calculável continua lançando
  `IllegalStateException`; `diagnosticarValorProposto` com valor correto
  não representável lança `ArithmeticException`.

Projeto completo compilado (435 arquivos): **0 erros**. Os 6 harnesses
executados: todos passaram, incluindo todas as seções anteriores
(`diagnosticarValorProposto`, `recalcularParaConsistencia`) — nenhuma
regressão.

## Próximo passo (passo 2, separado como combinado)

Limite de magnitude pedagógico — teto em `DominioNumerico` aplicado às 6
categorias. É decisão de pesquisa, não de programação: requer a definição
do teto. Os dados curados (0–80, outlier 417) são a evidência disponível.
