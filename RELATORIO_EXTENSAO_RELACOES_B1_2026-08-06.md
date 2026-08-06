# Extensão da delegação B1 aos 3 tipos "Relações"

Data: 2026-08-06. Continuação de `RELATORIO_INVESTIGACAO_5_TIPOS_NAO_COBERTOS_2026-08-06.md`. Nenhum commit feito ainda até esta edição.

## O que foi feito

Três classes ricas novas em `src/gerard/dominio/campoaditivo/`, mesmo padrão exato das três já existentes (Composição/Transformação/Comparação de Medidas): `verificarConsistencia`, `calcularValorAusente` (nunca modifica o papel, devolve `ResultadoCalculo`), `aplicar` (passo explícito separado, `IllegalStateException` se não houver valor calculável).

- `RelacaoEstruturalComposicaoDeTransformacoes`: TransformacaoFinal = Transformacao1 + Transformacao2.
- `RelacaoEstruturalTransformacaoDeRelacao`: RelacaoFinal = RelacaoInicial + Transformacao.
- `RelacaoEstruturalComposicaoDeRelacoes`: RelacaoFinal = Relacao1 + Relacao2.

Cada uma com sua fábrica de papéis (`FabricaPapeisComposicaoDeTransformacoes`, `FabricaPapeisTransformacaoDeRelacao`, `FabricaPapeisComposicaoDeRelacoes`) e harness (`TestePilotoComposicaoDeTransformacoes`, `TestePilotoTransformacaoDeRelacao`, `TestePilotoComposicaoDeRelacoes`).

Diferença em relação às 3 originais: os três papéis, nas três classes novas, são `INTEIROS` — não há papel `NATURAIS` em nenhuma delas. Isso porque estas são a categoria "Relações" de Vergnaud (números relativos), não "Medidas" (grandezas não-negativas) — ver a investigação anterior. Consequência prática: nenhum dos harnesses tem cenário de rejeição de valor por domínio, porque `INTEIROS.aceita(...)` sempre devolve `true`; os testes marcam isso explicitamente em vez de omitir.

**Forma de representação (`TipoRepresentacaoAbstrata`)**: usei `FIGURA_ELIPTICA` nos três papéis de cada classe, por consistência com a convenção já usada para papéis de transformação/valor relativo nas fábricas existentes. Diferente da decisão de forma em `FabricaPapeisComparacaoMedidas` (Fase A), que foi confirmada contra uma captura de tela da tela real de produção, esta é uma inferência — essas categorias nunca usaram o pacote piloto antes, não há "forma real" para confirmar contra. Se você souber de uma decisão de design diferente para essas categorias, é só falar.

## Extensão em `EstadoSemanticoCompartilhado`

`resolverViaRelacaoEstruturalRica` e `calcularComRelacaoRica` passaram a cobrir também `COMPOSICAO_TRANSFORMACOES`, `TRANSFORMACAO_RELACAO` e `COMPOSICAO_RELACOES`, com exatamente o mesmo critério de "primeiro preenchimento" já validado para os 3 tipos de Medidas (exatamente 1 papel incógnito, e essa incógnita não é a posição recém-tocada). Os 2 tipos "Em construção" (`COMPOSICAO_TRANSFORMACAO_MEDIDAS`, `TRANSFORMACAO_COMPOSTA_DOIS_PASSOS`) continuam fora, como já registrado na investigação — sem urgência, sem usuário alcançando-os pela UI hoje.

## Verificação

- Compilação completa: 435 arquivos, exit `0`.
- Os 6 harnesses do piloto (3 de Medidas + 3 de Relações novos): todos passando.
- Suíte comparativa (`TesteComparativoEstadoSemanticoCompartilhado`) estendida de 28 para **40 cenários** — adicionados os 3 tipos novos (preenchimento a partir de cada posição, fase 2/consistência com sobrescrita, incógnita na própria posição editada, 2 incógnitas, e um cenário específico confirmando que valor negativo nunca é rejeitado nos três papéis INTEIROS). `COMPOSICAO_TRANSFORMACAO_MEDIDAS` continua como controle dos tipos ainda não cobertos. **0 divergências** entre a versão antiga (algoritmo genérico puro) e a nova.

## O que não mudou

- Os 2 tipos "Em construção" continuam 100% no algoritmo genérico.
- `Main.java` e os 7 arquivos satélites: zero alteração.

## Status

Pendente de commit — seguindo para o passo seguinte (Fase B2, catalogar as 8 origens).
