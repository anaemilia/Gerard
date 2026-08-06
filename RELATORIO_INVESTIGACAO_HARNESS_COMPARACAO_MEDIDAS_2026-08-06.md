# Investigação — viabilidade de um harness de piloto para Comparação de Medidas

Data: 2026-08-06. Somente leitura — nenhum código, nenhuma edição de documentação, nenhum commit. Rigor epistêmico: [FATO NORMATIVO], [EVIDÊNCIA NO CÓDIGO], [INCONSISTÊNCIA], [INFERÊNCIA], [INCONCLUSIVO].

## 1. Leitura de `TAREFA_PENDENTE_COMPARACAO_MEDIDAS.md`

**[FATO NORMATIVO]** Lido por completo. Confirma a descrição do prompt: a lacuna não é "não existe nada para Comparação" — é "existe resolução em `EstadoSemanticoCompartilhado` e testes que verificam um valor calculado real (`TesteMapeamentoComparacaoComplementar`, `TesteSincronizacaoControlesPorCategoria`), mas com um contrato de domínio mais pobre que o do piloto": sem `EstadoConsistencia` tipado, sem `EventoDominio` publicado, rejeição silenciosa via `catch (IllegalArgumentException)`. O arquivo também registra, numa seção adicionada em 2026-08-05, a investigação da duplicação de nome `PapelQuantitativo` (duas classes, estruturalmente diferentes, zero consumidores em comum) — relevante para a pergunta 3 abaixo.

## 2. A descrição ainda bate com o código atual?

**[EVIDÊNCIA NO CÓDIGO] Sim, sem alteração.** Verificado por dois caminhos:

- Releitura de `EstadoSemanticoCompartilhado.java`: `resolverRelacaoAditiva()` continua em `149-194`; o `catch (IllegalArgumentException)` silencioso continua em `219-222`, com o mesmo comentário ("A relação matemática não pode converter uma medida em negativo. O valor anterior/ausente é preservado sem publicar estado inválido."). Nenhuma linha mudou.
- `git log --oneline -- <arquivo>` para os três arquivos citados (`EstadoSemanticoCompartilhado.java`, `TesteMapeamentoComparacaoComplementar.java`, `TesteSincronizacaoControlesPorCategoria.java`): **todos os três têm um único commit no histórico — o commit inicial (`05556ee`, baseline de produção)**. Nenhum dos três foi tocado por nenhum dos commits desta sessão (incluindo a reorganização recente de 5 commits, que só tocou `REFERENCE.md`, `TAREFA_PENDENTE_FLUXO_TENTATIVAS_E_SCAFFOLDING.md`, `Main.java`, `AgenteModelador.java`, `EventoLogGerard.java`, `LoggerInteracaoGerard.java`, `TelaArtefatoExplicativo.java` e os dois arquivos novos de mineração).

A descrição continua precisa, com confiança alta.

## 3. Dá para escrever `TestePilotoComparacaoMedidas`?

**[INFERÊNCIA] Sim, tecnicamente viável, seguindo exatamente o padrão já usado duas vezes — sem obstáculo arquitetural encontrado.**

Inspecionei o pacote `gerard.dominio.campoaditivo` (piloto) por completo. O que existe hoje:

| Classe | Papel no padrão |
|---|---|
| `PapelQuantitativo` | Objeto de domínio genérico e reaproveitável — já serve Composição e Transformação sem alteração. |
| `FabricaPapeisTransformacaoMedidas` | Fábrica de 3 papéis com domínio numérico e descritor de representação fixados por construção (`FabricaPapeisTransformacaoMedidas.java:19-35`). |
| `RelacaoEstruturalComposicao` | Só `verificarConsistencia(...)` — **não tem** `calcularValorAusente`/`aplicar` (`RelacaoEstruturalComposicao.java:48-60`). |
| `RelacaoEstruturalTransformacao` | Tem `verificarConsistencia` **e** `calcularValorAusente`/`aplicar`, com `ResultadoCalculo`/`EstadoConsistencia`/`OrigemAcao.ORIGEM_SISTEMA` — o contrato mais rico, já usado pelo harness de Transformação para testar resolução de incógnita. |

Para um `TestePilotoComparacaoMedidas` equivalente aos outros dois, faltaria criar:

1. **`FabricaPapeisComparacaoMedidas`** (nome análogo) — três papéis: Referido, Valor Relativo, Referendo. Domínios numéricos já estão definidos em produção (`CatalogoPapeisSemanticos.java:25-27,33-34`, achado da investigação anterior desta sessão): Referido/Referendo → `NATURAIS`; Valor Relativo → `INTEIROS`.
2. **`RelacaoEstruturalComparacao`** — precisaria seguir o padrão **rico** (Transformação), não o simples (Composição), porque o objetivo declarado do harness é testar resolução de incógnita (`calcularValorAusente`), não só verificação de consistência. A relação aditiva é a mesma forma (`Referendo = Referido + ValorRelativo`), então a implementação de `calcularValorAusente` seria estruturalmente análoga à de `RelacaoEstruturalTransformacao` (três ramos: incógnita em cada um dos três papéis).
3. **`src/TestePilotoComparacaoMedidas.java`** — mesmo estilo dos outros dois: `checar(...)` com `AssertionError`, publicador local (`List<EventoDominio>`), linha final `TODOS OS TESTES DO PILOTO DE COMPARAÇÃO DE MEDIDAS PASSARAM.`

Nenhuma peça de infraestrutura do pacote piloto (`EventoDominio`, `PublicadorEventoDominio`, `ContextoAcao`, `DiagnosticoErroPapel`, `EstadoConsistencia`, `ResultadoCalculo`, `OrigemAcao`) precisaria de alteração — todas já são genéricas o suficiente, confirmado pelo fato de já servirem dois esquemas diferentes sem mudança.

## 4. Avaliação sobre (a) reimplementar isolado, (b) delegar, ou (c) migrar/extrair

**Pedido explícito: isto é recomendação, não decisão — a decisão continua sua, a ser tomada quando (e se) esta tarefa for retomada.**

**[INFERÊNCIA] A opção (a) — reimplementar isoladamente — é a mais alinhada ao precedente já estabelecido pelos outros dois pilotos**, por evidência direta do próprio código e do histórico de commits:

- O commit de criação do piloto (`7f6ad5c`, "feat(domain): add isolated PapelQuantitativo architecture pilot") descreve explicitamente reaproveitamento **seletivo e deliberado** de só `gerard.semantica.numero.*` (`DominioNumerico`/`ValorNumerico`/`NumeroNatural`/`NumeroInteiro`/`ValorDesconhecido`) — primitivas numéricas puras, sem estado, sem efeito colateral. O commit não reaproveita, delega para, ou migra nenhuma classe de produção equivalente que já existisse (e existiam: `gerard.semantica.papel.PapelQuantitativo`, `CatalogoPapeisSemanticos`, ambos anteriores ao piloto e resolvendo um problema adjacente — achado já registrado na seção de duplicação de nome deste mesmo arquivo de tarefa).
- O javadoc de `PapelQuantitativo.java:37-38` declara a isolação como propriedade de design: "não é referenciado por `Main.java` nem por nenhum caminho de produção."
- `EstadoSemanticoCompartilhado` é descrita no próprio `REFERENCE.md §4.3` como "numa arquitetura mais antiga e genérica, fora do pacote piloto e fora do modelo Domain Model First" — delegar (b) importaria essa arquitetura antiga para dentro do piloto, na direção contrária ao que o piloto existe para demonstrar. Migrar/extrair (c) exigiria alterar `EstadoSemanticoCompartilhado`, usada em 17+ pontos de `Main.java` — uma mudança de superfície muito maior e mais arriscada do que qualquer coisa que os outros dois pilotos precisaram fazer (nenhum deles tocou um arquivo de produção).

Ou seja: o padrão observado duas vezes é "construir do zero, reaproveitando só primitivas de baixo nível sem estado" — não "delegar para" ou "migrar de" uma implementação de produção existente, mesmo quando uma já resolve um problema adjacente. A opção (a) é a que replica esse padrão; (b) e (c) rompem com ele, cada uma por um motivo diferente.

## O que não foi feito

Nenhuma classe criada, nenhum harness escrito, nenhum arquivo de código ou documentação editado, nenhum commit. Aprovação item por item, conforme o processo já registrado em `TAREFA_PENDENTE_COMPARACAO_MEDIDAS.md`, continua pendente antes de qualquer diff.
