# Tarefa pendente — arquitetura rica integral + princípio da localidade do conhecimento em EstadoSemanticoCompartilhado

Status: **registrada, não autorizada a começar (Fase B/C abaixo). Fase A autorizada, ver seção "Autorização".**

---

## Decisão registrada em 2026-08-06 (conversa, formalizada aqui)

Pedido: a arquitetura rica (contrato de `RelacaoEstruturalTransformacao` — `calcularValorAusente`/`aplicar`/`EstadoConsistencia`/`DiagnosticoErroPapel`) deve ser implementada **integralmente** para as três categorias aditivas (Composição, Transformação, Comparação), e `Main.java` deve ficar "só com o código necessário", seguindo fielmente o princípio da localidade do conhecimento já citado em `REFERENCE.md`. Isso implica migrar o uso de produção de `EstadoSemanticoCompartilhado` para os objetos de domínio do pacote piloto (`gerard.dominio.campoaditivo`), não só criar Comparação isolada.

## Escopo real, levantado em 2026-08-06 (investigação, só leitura)

`EstadoSemanticoCompartilhado` — não `Main.java` sozinho — é referenciada **70 vezes em 8 arquivos**:

| Arquivo | Ocorrências | Papel |
|---|---|---|
| `Main.java` | 53 | Motor de sincronização da tela principal: Vergnaud, diagrama complementar (Venn), eixo X, eixo vertical, edição de texto, arraste, exclusão, replay de protocolo (`Origem.*` — 8 origens distintas) |
| `gerard/campoaditivo/sincronizacao/SimuladorEstadoComplementarVenn.java` | 5 | Simulação do diagrama complementar |
| `gerard/campoaditivo/transformacao/processo/SincronizadorUnidadesProcessoTransformacao.java` | 3 | Sincronização do processo de Transformação |
| `gerard/campoaditivo/transformacao/processo/EstadoProcessoTransformacao.java` | 2 | Estado do processo de Transformação |
| `gerard/campoaditivo/sincronizacao/texto/SincronizadorElementosSemanticosTexto.java` | 2 | Sincronização de texto |
| `gerard/campoaditivo/sincronizacao/texto/SincronizadorElementosSemanticosTextoAbstrato.java` | 2 | Base abstrata dos sincronizadores de texto |
| `gerard/ui/enunciado/ConversorIndiceEstadoCompartilhado.java` | 1 | Conversão de índice para o enunciado |
| `EstadoSemanticoCompartilhado.java` (própria classe) | 2 | — |

**[EVIDÊNCIA NO CÓDIGO]** `EstadoSemanticoCompartilhado` já é genérica para as três categorias via `TipoSituacaoAditiva` + `CatalogoEsquemasCategoriasAditivas.obter(tipo).obterDominioCompartilhado(indice)` — não é uma classe por categoria como o pacote piloto. É um estado único, mutável, `synchronized`, versionado por `Snapshot`, que resolve a relação aditiva genericamente (soma/subtração entre os três papéis) para qualquer uma das três categorias, dependendo de qual dos três valores está "alterado" (`indiceAlterado`) — a mesma classe já atende Composição, Transformação e Comparação em produção hoje.

Isso é estruturalmente diferente do pacote piloto (uma classe `RelacaoEstruturalX` por categoria, com objetos `PapelQuantitativo` individuais, eventos publicados, `DiagnosticoErroPapel`). Migrar "fielmente ao princípio da localidade do conhecimento" significa decidir **como** essas duas arquiteturas convergem — não é uma troca mecânica de chamada.

## Por que isto não pode ser um diff único, sem compilação intermediária

- 70 pontos de uso em 8 arquivos, a maior parte em `Main.java` (13.000+ linhas), em código de produção que sincroniza a tela usada para coleta de dados de pesquisa real (protocolos reais de participantes, ver `protocolos_reais_replay.tsv`).
- Não há capacidade de compilar Java neste ambiente onde a investigação foi feita (sem JDK, sem acesso root para instalar) — qualquer diff gerado aqui não pode ser verificado antes de chegar até você.
- O próprio processo já em uso neste repositório (`TAREFA_PENDENTE_COMPARACAO_MEDIDAS.md`, seção "Processo a seguir") exige compilação e harnesses rodados **antes e depois de cada mudança** — uma mudança desse tamanho, feita de uma vez, tornaria impossível isolar o que quebrou, se algo quebrar.

## Plano faseado proposto

**Fase A — pacote piloto completo, zero risco de produção (pode começar imediatamente, já aprovada nesta conversa):**
- `RelacaoEstruturalComposicao` ganha contrato rico (`calcularValorAusente`/`aplicar`/`EstadoConsistencia`/`DiagnosticoErroPapel`), igual ao de `RelacaoEstruturalTransformacao`.
- `RelacaoEstruturalComparacao` (nova) com o mesmo contrato rico, `FabricaPapeisComparacaoMedidas`, papéis Referido/Referendo/Valor Relativo.
- `TestePilotoPapelQuantitativo` atualizado para exercitar `calcularValorAusente` de Composição; `TestePilotoComparacaoMedidas` novo.
- Nenhuma linha de `Main.java` ou de qualquer um dos 8 arquivos de produção acima é tocada nesta fase.

**Fase B — investigação dedicada da migração de produção (não começar sem isso):**
- Catalogar, um a um, o que cada uma das 8 origens (`Origem.VERGNAUD`, `DIAGRAMA_COMPLEMENTAR`, `EIXO_X`, `EIXO_VERTICAL`, `EDICAO_TEXTO`, `ARRASTE`, `EXCLUSAO`, `PROTOCOLO`) faz e depende de `EstadoSemanticoCompartilhado` para.
- Decidir explicitamente (apresentado item por item, como já é o padrão): `EstadoSemanticoCompartilhado` é substituída pelos objetos do piloto, ou ela própria é enriquecida no lugar (ganha `EstadoConsistencia`/eventos/`DiagnosticoErroPapel` sem trocar de classe)? As duas leituras satisfazem "localidade do conhecimento" de formas diferentes, com riscos e esforços muito diferentes.
- Mapear os outros 7 arquivos consumidores e o que cada um exigiria na transição.

**Fase C — migração incremental (só depois da Fase B decidida):**
- Uma origem/concern por vez, com compilação e os harnesses rodados antes e depois de cada etapa, no ambiente com JDK (IntelliJ) — não em lote.

## Autorização

- **Fase A: autorizada** (aprovada nesta conversa, decisões de nomes e contrato já confirmadas).
- **Fase B e C: não autorizadas a começar.** Ficam registradas aqui para não se perderem só em conversa — retomar quando a Fase B for pedida explicitamente.
