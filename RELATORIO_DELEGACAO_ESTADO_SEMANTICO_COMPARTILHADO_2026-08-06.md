# Delegação B1 — EstadoSemanticoCompartilhado passa a usar as classes ricas da Fase A

Data: 2026-08-06. Escopo: Fase B, opção B1, de `TAREFA_PENDENTE_LOCALIDADE_CONHECIMENTO_ESTADO_COMPARTILHADO.md` (ver também `RELATORIO_INVESTIGACAO_FASE_B_LOCALIDADE_CONHECIMENTO_2026-08-06.md`). Nenhum commit feito.

## O que foi pedido e o que mudou no meio do caminho

B1, como proposto na investigação, era descrito como troca mecânica de baixo risco: `resolverRelacaoAditiva()` passaria a delegar para `RelacaoEstruturalComposicao/Transformacao/Comparacao.calcularValorAusente()` nos 3 tipos cobertos pelo piloto, mantendo o algoritmo genérico como reserva para os outros 5. Ao ler `resolverRelacaoAditiva()` linha a linha antes de mexer, achei que essa premissa de "troca mecânica" não se sustentava: o método não implementa só "resolver a única incógnita" — ele mistura isso com um segundo comportamento, recalcular um papel **mesmo que já estivesse preenchido**, em resposta a qual posição (`indiceAlterado`) acabou de ser tocada. Esse segundo comportamento é o que mantém as representações sincronizadas durante arraste/edição depois que o diagrama já está completo — e não tem equivalente em `calcularValorAusente()`, que se recusa a calcular (`NAO_RESOLVIVEL_NESTE_ESTADO`) sempre que não há exatamente uma incógnita.

Levei essa divergência para você duas vezes (ver conversa) antes de escrever qualquer código. Sua resposta — preenchimento pelo usuário até o diagrama ficar completo ("azul"), preenchimento automático de consistência depois disso — confirmou que são dois problemas de domínio genuinamente distintos, não uma sobreposição acidental. A partir disso, cheguei ao critério exato que separa os dois: **existe exatamente uma incógnita entre os três papéis, e essa incógnita não é a posição que acabou de ser tocada (`indiceAlterado`)** — só nesse caso o resultado é idêntico ao que `calcularValorAusente()` calcularia, verificado algebricamente para as três categorias antes de implementar.

## O que foi implementado

Em `src/gerard/campoaditivo/sincronizacao/EstadoSemanticoCompartilhado.java`:

- `resolverRelacaoAditiva()` agora chama `resolverViaRelacaoEstruturalRica(...)` primeiro. Se esse caminho tratar o caso (retorna `true`), o algoritmo genérico nem roda.
- `resolverViaRelacaoEstruturalRica(...)`: só age para os 3 tipos cobertos (`COMPOSICAO_MEDIDAS`, `TRANSFORMACAO_MEDIDAS`, `COMPARACAO_MEDIDAS`). Conta incógnitas entre os três papéis; se não for exatamente 1, ou se a única incógnita for a própria posição editada, devolve `false` e o algoritmo genérico de sempre roda sem alteração nenhuma (cobre os outros 5 tipos, a fase de consistência pós-preenchimento, e o caso "usuário apagou o campo que está editando").
- `calcularComRelacaoRica()`: monta um trio descartável de `PapelQuantitativo` do pacote piloto (via `PapelQuantitativo.parte1/parte2/todo`, `FabricaPapeisTransformacaoMedidas`, `FabricaPapeisComparacaoMedidas`, todos com `PublicadorEventoDominio.NENHUM` — sem efeito colateral de evento), popula os dois papéis conhecidos com os valores atuais, e chama `RelacaoEstruturalComposicao/Transformacao/Comparacao.calcularValorAusente(...)`.
- O valor resultante é escrito de volta usando o `definirSePermitido(...)` **já existente** — não um caminho novo de escrita. Isso significa que a validação de domínio (`NATURAIS` rejeita negativo) e o respeito ao `indiceIncognitaProtegida`/`permitirPreenchimentoIncognita` continuam sendo feitos exatamente pelo código que já fazia isso antes; só a aritmética (qual fórmula usar) foi delegada.

Nenhuma linha de `Main.java` ou dos outros 7 arquivos satélites foi tocada, como no plano original.

## Verificação

Não havia JDK neste ambiente (mesma limitação registrada na investigação da Fase B) — resolvido baixando o pacote `.deb` do OpenJDK 11 via `apt-get download` (sem root) e extraindo com `dpkg-deb -x`, sem instalar nada no sistema.

1. **Compilação completa do projeto** (424 arquivos, com `lib/weka-stable-3.8.6.jar` e `lib/bounce-0.18.jar` no classpath): exit `0`, sem erros novos (só o aviso pré-existente de `TesteUnidadeAnaliseABCD.java`, não relacionado a esta mudança).
2. **Os três harnesses do piloto** (`TestePilotoComposicaoMedidas`, `TestePilotoTransformacaoMedidas`, `TestePilotoComparacaoMedidas`) continuam passando — eles testam as classes ricas isoladamente, não `EstadoSemanticoCompartilhado`, então confirmam que o piloto em si não foi alterado.
3. **Verificação comparativa antes/depois** (não existia suíte automatizada para `EstadoSemanticoCompartilhado` no repositório — os nomes `TesteMapeamentoComparacaoComplementar`/`TesteSincronizacaoControlesPorCategoria` citados na investigação da Fase B não correspondem a arquivos existentes). Construí uma verificação dedicada: uma cópia exata do arquivo **antes** da mudança (`EstadoSemanticoCompartilhadoOriginal`, renomeada para não colidir, compilada junto com o restante do projeto real) rodando lado a lado com a classe **nova**, sobre 28 cenários idênticos — as mesmas chamadas `atualizar(...)`, comparando o `Snapshot` resultante campo a campo. Cobertura:
   - Os 3 tipos cobertos × preenchimento a partir de cada uma das 3 posições, incluindo o caminho de fallback (`indiceAlterado` fora de 0–2).
   - Fase 2 (consistência): todos os três papéis já preenchidos, edição de um deles sobrescrevendo outro — confirma que esse caminho continua 100% no algoritmo genérico, sem delegação.
   - Incógnita na própria posição editada (usuário apaga um campo) — confirma que não há auto-preenchimento indevido.
   - Rejeição de valor negativo em domínio `NATURAIS`, nas três categorias.
   - 2 e 3 incógnitas (não resolvível).
   - `indiceIncognitaProtegida`/`permitirPreenchimentoIncognita` (assinatura de 7 argumentos).
   - Um tipo **não** coberto pelo piloto (`COMPOSICAO_TRANSFORMACAO_MEDIDAS`) como controle — confirma que os 5 tipos restantes não são tocados por esta mudança.
   - Uma sequência de 4 passos simulando um arraste real (preencher parte1, preencher parte2 → sistema calcula todo, arrastar parte1 → sistema sobrescreve todo, apagar parte2 → sistema não reage).

   Resultado: **28 cenários, 0 falhas** — saída idêntica entre antes e depois em todos eles.

Nenhum dos artefatos de verificação (`EstadoSemanticoCompartilhadoOriginal.java`, `VerificacaoComparativaDelegacao.java`) foi salvo no repositório — viveram só no ambiente de compilação temporário, como ferramenta de verificação, não como código do projeto. Posso recriá-los e salvar em `src/` se você quiser manter essa suíte comparativa para regressão futura.

## O que não mudou

- `Main.java` e os 7 arquivos satélites: zero alteração, como já era o caso na Fase A.
- Os 5 tipos de `TipoSituacaoAditiva` fora do piloto: continuam 100% no algoritmo genérico original.
- O comportamento de "fase 2" (sincronização de consistência, podendo sobrescrever um valor já preenchido): continua no algoritmo genérico original — não é o problema que `calcularValorAusente()` resolve, e misturar os dois teria mudado comportamento em produção.

## Status

Alterado: `src/gerard/campoaditivo/sincronizacao/EstadoSemanticoCompartilhado.java` (115 linhas adicionadas, nenhuma removida). Nenhum commit feito. `git status --short` mostra só esse arquivo modificado (além deste relatório e do da investigação, ambos não rastreados).
