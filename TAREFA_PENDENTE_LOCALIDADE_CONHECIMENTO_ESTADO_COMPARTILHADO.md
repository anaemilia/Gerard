# Tarefa pendente — arquitetura rica integral + princípio da localidade do conhecimento em EstadoSemanticoCompartilhado

Status: **Fase A concluída (commit `c69fde4`, 2026-08-06). Fase B: investigação concluída, opção B1 implementada e depois estendida aos 3 tipos "Relações" alcançáveis pela UI (2026-08-06) — ver seção "Autorização". Fase B2 iniciando (levantamento, passo 2) — ver seção "Autorização".**

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

- **Fase A: concluída.** Composição ganhou o contrato rico, Comparação foi criada, os três harnesses (`TestePilotoComposicaoMedidas`, `TestePilotoTransformacaoMedidas`, `TestePilotoComparacaoMedidas`) passam, compilação limpa (424 arquivos). Commit `c69fde4`. Zero mudança em `Main.java` ou caminho de produção, como planejado.
- **Fase B, passo 1 (investigação): concluída**, sem código tocado — ver `RELATORIO_INVESTIGACAO_FASE_B_LOCALIDADE_CONHECIMENTO_2026-08-06.md`. Achado principal: só `Main.java` guarda estado mutável; os 7 arquivos satélites só leem `Snapshot`. `EstadoSemanticoCompartilhado` já atende 8 tipos de `TipoSituacaoAditiva` com uma fórmula genérica só de índice, não 3 — o pacote piloto cobre só 3 desses 8.
- **Fase B, opção B1: implementada e verificada (2026-08-06)**, sem commit ainda até esta edição. `resolverRelacaoAditiva()` em `EstadoSemanticoCompartilhado` agora delega para `RelacaoEstruturalComposicao/Transformacao/Comparacao.calcularValorAusente()` nos 3 tipos cobertos, mas só no caso de "primeiro preenchimento" (exatamente 1 papel incógnito entre os três, e essa incógnita não é a posição que acabou de ser tocada) — ver `RELATORIO_DELEGACAO_ESTADO_SEMANTICO_COMPARTILHADO_2026-08-06.md` para o porquê dessa condição (o algoritmo original mistura "preencher o que falta" com "sobrescrever para manter consistência depois que o diagrama já está completo" — só o primeiro bate com o contrato de `calcularValorAusente`). O segundo comportamento, e os outros 5 tipos, continuam 100% no algoritmo genérico original, inalterados. Verificado por compilação completa (426 arquivos), os três harnesses do piloto, e uma suíte comparativa nova de 28 cenários (`src/TesteComparativoEstadoSemanticoCompartilhado.java` + `src/EstadoSemanticoCompartilhadoOriginal.java`, cópia da versão anterior usada só para comparação) rodando a versão antiga e a nova lado a lado — 0 divergências. Zero mudança em `Main.java` ou nos 7 arquivos satélites.
- **Investigação dos 5 tipos não cobertos: concluída (2026-08-06)**, sem código tocado — ver `RELATORIO_INVESTIGACAO_5_TIPOS_NAO_COBERTOS_2026-08-06.md`. Achado: 2 dos 5 (`COMPOSICAO_TRANSFORMACAO_MEDIDAS`, `TRANSFORMACAO_COMPOSTA_DOIS_PASSOS`) são "Em construção" — nenhum caminho de UI os alcança hoje. Os outros 3 (`COMPOSICAO_TRANSFORMACOES`, `TRANSFORMACAO_RELACAO`, `COMPOSICAO_RELACOES`) são a categoria "Relações" de Vergnaud, alcançáveis pela UI, numericamente iguais às 3 relações já existentes mas conceitualmente distintos (números relativos, não medidas) — nenhuma classe existente os representa por nome.
- **Extensão da opção B1 aos 3 tipos "Relações": implementada e verificada (2026-08-06).** Três classes ricas novas no pacote piloto (`RelacaoEstruturalComposicaoDeTransformacoes`, `RelacaoEstruturalTransformacaoDeRelacao`, `RelacaoEstruturalComposicaoDeRelacoes`, cada uma com sua fábrica de papéis e harness `TestePiloto*`, mesmo padrão das 3 originais). `resolverViaRelacaoEstruturalRica` em `EstadoSemanticoCompartilhado` passou a cobrir também esses 3 tipos, mesmo critério de "primeiro preenchimento" já validado. Os 2 tipos "Em construção" continuam fora — sem urgência, sem usuário alcançando-os hoje. Suíte comparativa estendida de 28 para 40 cenários, 0 divergências. Compilação completa (435 arquivos) e todos os harnesses (6 pilotos + comparativo) passando.
- **Fase B2, passo 2 (catalogar as 8 origens): concluída (2026-08-06)**, sem código tocado — ver `RELATORIO_FASE_B2_CATALOGO_8_ORIGENS_2026-08-06.md`. Achado principal: as 8 origens não são 8 ações de UI paralelas — todas (exceto 2 usos de instância descartável para simulação) passam por um funil de 2 métodos (`sincronizarTodasAsRepresentacoesAPartirDoVergnaud`/`...DoDiagramaComplementar`). Achado inesperado: `Origem.PROTOCOLO` não significa "replay de protocolo de pesquisa" — é o rótulo padrão de fallback (`novaOrigem == null`); os 3 usos reais em `Main.java` são todos correções/simulações do sistema, não replay. **Confirmado**: existem 3 mecanismos de replay no repositório (`TesteReplayProtocolosReais` — pula a tela, chama os agentes direto; `TesteMonkeySemiGuiado` — Robot com ações aleatórias; `TesteMonkeyGuiadoPorCasosReais` — Robot seguindo o roteiro exato de um episódio real); os dois últimos tocam `EstadoSemanticoCompartilhado`, mas pelas origens genuínas de UI (`ARRASTE`, `EDICAO_TEXTO` etc.), nunca por `Origem.PROTOCOLO`. `PROTOCOLO` é seguro de tratar, numa eventual B2, como sinônimo de `OrigemAcao.ORIGEM_SISTEMA`.
- **Fase B2, migração de fato (`Main.java` manipulando os objetos do piloto diretamente), e Fase C: não autorizadas a começar.** Ficam registradas aqui para não se perderem só em conversa — retomar quando forem pedidas explicitamente.
