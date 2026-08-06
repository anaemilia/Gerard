# Relatório: enriquecimento de TipoErroPapel/DiagnosticoErroPapel (piloto)

Data: 2026-08-06

## Contexto

Primeiro dos três passos combinados para a Fase B2, na ordem definida pelo
usuário: "Enriquecer o piloto primeiro — dar mais valores reais a
`TipoErroPapel`/`DiagnosticoErroPapel` (zero risco de produção, mesmo
espírito da Fase A) antes de cogitar ligar isso à Main."

Antes desta mudança, `TipoErroPapel` tinha exatamente um valor
(`VALOR_FORA_DO_DOMINIO`), e nenhuma das 6 classes ricas de relação
estrutural (`RelacaoEstrutural*`) sabia avaliar um valor **proposto** —
só sabiam calcular o valor que falta (`calcularValorAusente`). Não havia
como o piloto distinguir "o estudante errou porque inverteu a operação"
de "o estudante errou por outro motivo".

## O que mudou

### `TipoErroPapel` — de 1 para 3 valores

```java
public enum TipoErroPapel {
    VALOR_FORA_DO_DOMINIO,
    OPERACAO_INVERTIDA,
    VALOR_INCORRETO
}
```

`OPERACAO_INVERTIDA` é fundamentado na literatura de Vergnaud sobre erros
característicos em problemas aditivos: o estudante aplica a operação
inversa da correta (soma quando devia subtrair, ou vice-versa) — um
padrão de erro qualitativamente diferente de um erro aritmético genérico.

### Novo método em todas as 6 classes `RelacaoEstrutural*`

```java
public Optional<DiagnosticoErroPapel> diagnosticarValorProposto(
        PapelQuantitativo papelA, PapelQuantitativo papelB, PapelQuantitativo papelC,
        PapelQuantitativo papelAlvo, ValorNumerico valorProposto)
```

Diferente de `calcularValorAusente` (que só preenche o que falta, nunca
avalia um valor já digitado), este método recebe um valor **proposto**
para o papel-alvo e devolve:

- `Optional.empty()` — proposta correta;
- `DiagnosticoErroPapel(VALOR_FORA_DO_DOMINIO, ...)` — proposta fora do
  domínio aceito pelo papel (ex.: negativo numa Medida);
- `DiagnosticoErroPapel(OPERACAO_INVERTIDA, ...)` — proposta corresponde
  exatamente ao resultado da operação inversa;
- `DiagnosticoErroPapel(VALOR_INCORRETO, ...)` — proposta errada sem
  padrão reconhecido.

Pré-condição idêntica à de `calcularValorAusente`: `papelAlvo` precisa
ser exatamente o único papel incógnito entre os três, senão lança
`IllegalStateException` (contrato do chamador, mesmo estilo de
`aplicar(...)`).

Aplicado nas 6 classes: `RelacaoEstruturalTransformacao`,
`RelacaoEstruturalComposicao`, `RelacaoEstruturalComparacao`,
`RelacaoEstruturalComposicaoDeTransformacoes`,
`RelacaoEstruturalTransformacaoDeRelacao`,
`RelacaoEstruturalComposicaoDeRelacoes`.

## Escopo — só o piloto, zero Main.java

Nenhuma linha de `Main.java` foi tocada. `PapelQuantitativo` (piloto)
segue exatamente como estava, "isolado por design" — nada mudou nessa
fronteira. Esta etapa só adiciona capacidade nova dentro do pacote já
isolado `gerard.dominio.campoaditivo`.

## Verificação

Cobertura de teste adicionada às 6 harnesses (`TestePiloto*.java`),
seção `diagnosticarValorProposto (2026-08-06)`:

- **Medidas** (Transformação, Composição, Comparação — domínio com
  restrição): correta / operação invertida / genérica errada / fora do
  domínio. `TestePilotoTransformacaoMedidas` ainda cobre o caso de
  pré-condição violada (duas incógnitas) com um helper
  `verificaLancaIllegalState`.
- **Relações** (Composição de Transformações, Transformação de Relação,
  Composição de Relações — domínio INTEIROS irrestrito, sem cenário de
  rejeição por domínio, conforme o próprio javadoc dessas classes):
  correta / operação invertida / genérica errada.

Compilação completa do projeto (435 arquivos) após todas as 6 edições
de produção + 6 edições de harness: **0 erros**.

Execução dos 6 harnesses via `java -cp ...`: todos os 6 imprimiram
"TODOS OS TESTES ... PASSARAM." incluindo a nova seção, sem nenhuma
falha (`checar` não gerou nenhum "FALHOU").

## O que NÃO foi verificado / não se aplica

- Nenhuma integração com Main.java ou com a UI — não existe, por
  design, nesta etapa.
- Nenhuma chave de i18n foi adicionada em `mensagens_pt.properties` —
  as chaves `erro.papel.*`/`feedback.papel.*`/`correcao.papel.*` em
  `DiagnosticoErroPapel` permanecem placeholders deliberadamente não
  ligadas ao catálogo de mensagens, conforme o próprio javadoc da
  classe já documentava antes desta mudança.

## Próximos passos (ordem já combinada com o usuário)

1. ~~Enriquecer o piloto~~ — concluído nesta etapa.
2. Adotar `OrigemAcao` na Main.java, tipando a origem das ações no lugar
   dos flags ad-hoc.
3. Fase B2 completa — Main manipula `PapelQuantitativo` diretamente.
