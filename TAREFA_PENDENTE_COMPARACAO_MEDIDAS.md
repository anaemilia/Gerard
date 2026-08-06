# Tarefa pendente — Comparação de Medidas no pacote piloto

Status: **registrada, não autorizada a começar.** Retomar somente quando pedido explicitamente.

---

## O que falta

- Classe `RelacaoEstruturalComparacao`, paralela a `RelacaoEstruturalComposicao` e `RelacaoEstruturalTransformacao` (`src/gerard/dominio/campoaditivo/`), cobrindo os papéis semânticos de Comparação segundo Vergnaud: **Referido**, **Referendo**, **Valor Relativo**.
- Harness próprio, paralelo a `TestePilotoPapelQuantitativo` (Composição) e `TestePilotoTransformacaoMedidas` (Transformação) — nome ainda não decidido.
- Provavelmente uma fábrica de papéis análoga a `FabricaPapeisTransformacaoMedidas`, para os três papéis de Comparação.

`RelacaoEstruturalComparacao` já está prevista em `REFERENCE.md` (seção 4.3, lista de "nomes recomendados para objetos computacionais") — o nome e o lugar já estão normativamente definidos; falta a implementação.

## Contexto

Registrado em 2026-08-04, a partir de uma pergunta direta sobre por que toda verificação de antes/depois desta sessão cobria só dois harnesses (Composição, Transformação), quando Vergnaud descreve três categorias de estruturas aditivas. Investigação confirmou: nenhuma classe ou harness de Comparação existe em lugar nenhum do repositório; a lacuna nunca tinha sido documentada como decisão deliberada de escopo antes desta sessão (ver `REFERENCE.md §4.3` e `CHANGELOG.md` v2.3, ambos atualizados nesta mesma data).

## Contexto arquitetural (achado em investigação de acompanhamento, 2026-08-04)

**Esta tarefa não parte do zero.** Existe uma classe em produção,
`gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado`, que já
resolve a terceira quantidade a partir de duas conhecidas, genericamente,
para as três categorias de estrutura aditiva — incluindo Comparação
(`TipoSituacaoAditiva.COMPARACAO_MEDIDAS`). Segundo o próprio javadoc da
classe: "texto, Vergnaud, barras, eixo e tabuleiro não decidem localmente
se um valor aceita sinal" — ela é o modelo compartilhado que sincroniza as
representações de Comparação de Medidas já em funcionamento na tela
principal do Gerard (instanciada em `Main.java:773`).

Já existem testes cobrindo especificamente essa responsabilidade para
Comparação, fora da suíte dos harnesses do piloto:
`TesteMapeamentoComparacaoComplementar` e
`TesteSincronizacaoControlesPorCategoria` (ambos em `scripts/testes/`).

### Precisão sobre o que esse teste verifica (correção, 2026-08-05)

Uma resposta anterior, dada só em conversa (nunca registrada em arquivo),
afirmou que não existe em nenhuma camada do projeto uma resolução de
incógnita de Comparação com valor calculado e verificado. Isso estava
errado e contradizia o próprio parágrafo acima, já registrado nesta
sessão. Registro aqui a correção, para o arquivo não ficar ambíguo:

`TesteMapeamentoComparacaoComplementar` (linhas 21-41) exercita
`EstadoSemanticoCompartilhado.resolverRelacaoAditiva()`
(`EstadoSemanticoCompartilhado.java:149-194`) com um caso real: referido=6
e referendo alterado de 14 para 15 (ambos conhecidos) leva o valor
relativo a ser recalculado de 8 para **9** (`15-6`), e o teste verifica
esse valor calculado — não é só checagem de mapeamento de índice/geometria.

Ainda assim, o contrato dessa resolução é mais pobre que o das classes do
piloto (`RelacaoEstruturalComposicao`/`RelacaoEstruturalTransformacao`),
em pontos específicos:
- não expõe estados de consistência tipados (`EstadoConsistencia`:
  `CONSISTENTE`/`INCONSISTENTE`/`REPRESENTACAO_INCOMPLETA`) para o teste
  checar — a resolução acontece dentro de `atualizar(...)` sem essa saída;
- não publica `EventoDominio` nem passa por `PublicadorEventoDominio` —
  nenhum evento semântico é gerado ou verificado nesse cálculo;
- a rejeição de valor inválido é um `catch (IllegalArgumentException)`
  silencioso (`EstadoSemanticoCompartilhado.java:219-222`), sem
  `DiagnosticoErroPapel`, sem chave de mensagem/feedback/correção, e sem
  teste que exercite essa rejeição especificamente para Comparação;
- o teste parte de um estado com os três valores já conhecidos, não de
  uma representação vazia sendo preenchida incógnita por incógnita, como
  o piloto faz com `posicionar(...)`/`calcularValorAusente(...)`.

Ou seja: a lacuna real não é "não existe resolução nem teste para
Comparação" — é "existe resolução e um teste que verifica um valor
calculado real, mas com um contrato de domínio mais pobre que o do
piloto". Essa é a formulação a usar daqui para frente.

**Antes de escrever qualquer código para `RelacaoEstruturalComparacao`, é
preciso decidir a relação entre ela e `EstadoSemanticoCompartilhado`** —
por exemplo: a nova classe do piloto reimplementa a mesma lógica de forma
isolada (duplicando-a), delega para a classe existente, ou a tarefa é na
verdade migrar/extrair a lógica de `EstadoSemanticoCompartilhado` para o
pacote piloto? Essa decisão de modelagem faz parte do passo 2 do processo
abaixo, não pode ser presumida.

### Duplicação de nome `PapelQuantitativo` — investigação concluída (2026-08-05)

Existem duas classes com esse nome. Não são o mesmo conceito com
nomenclatura duplicada (não é o padrão já visto de
`InvarianteOperatorio*`→`RelacaoEstrutural*`) — são estruturalmente
diferentes, sem consumidores em comum, e com responsabilidades distintas.

**`gerard.semantica.papel.PapelQuantitativo`**
(`src/gerard/semantica/papel/PapelQuantitativo.java:1-30`) — a mais antiga,
já no commit inicial do repositório (`05556ee`, 2026-07-20, baseline de
produção). Implementa `PapelSemantico` (`PapelSemantico.java:4`, só
`getChave()`/`getNomeConceitual()`). Três campos (`chave`,
`nomeConceitual`, `dominio`), sem estado mutável, sem `posicionar`, sem
evento. É um **descritor imutável de tipo de papel** — registra a que
domínio numérico (NATURAIS/INTEIROS) cada chave de papel pertence.
Instanciada por `CatalogoPapeisSemanticos.java:47,51,61,64,67,69`, que já
registra `papel.referido`/`papel.referendo`/`papel.valorRelativo`
(linhas 25-27, 33-34). Consumida em produção por
`CatalogoPerfisCategoriasAditivas.java:7`,
`FabricaInstanciaSemanticaAditiva.java:16`,
`ConversorTextoParaQuantidadeSemantica.java:3`, `ElementoNumerico.java:4`,
`QuantidadeSemantica.java:4`, `CategoriaSimples.java:3`,
`CatalogoEsquemasCategoriasAditivas.java:5`,
`EsquemaCategoriaAditiva.java:5`, `CategoriaComposta.java:3`,
`ComponenteCategoria.java:3`. Notavelmente, `CatalogoEsquemasCategoriasAditivas`
é a mesma classe que `EstadoSemanticoCompartilhado.dominioDoIndice(...)`
usa — ou seja, já está na espinha dorsal da resolução de Comparação de
Medidas em produção.

**`gerard.dominio.campoaditivo.PapelQuantitativo`**
(`src/gerard/dominio/campoaditivo/PapelQuantitativo.java:1-194`) — a mais
nova, criada em `7f6ad5c` (2026-08-01, "feat(domain): add isolated
PapelQuantitativo architecture pilot"), como implementação de referência
das skills DomainModelFirst/KnowledgeOrientedDomainObjects/
KnowledgeLocalityPrinciple/SemanticEventLogging. Não implementa
`PapelSemantico`. Tem estado mutável (`valorAtual`), método
`posicionar(ValorNumerico, OrigemAcao, ContextoAcao)`
(linhas 141-161) que valida, publica `EventoPapelQuantitativo` e devolve
`Optional<DiagnosticoErroPapel>` com chaves de mensagem/feedback/correção
pedagógica. É um **objeto de domínio com ciclo de vida** — papel como
instância viva de uma tentativa, não como categoria estática. Consumida
só por suas próprias fábricas (linhas 68-84),
`FabricaPapeisTransformacaoMedidas.java:20,26,32` e os dois harnesses do
piloto (`TestePilotoPapelQuantitativo.java:5`,
`TestePilotoTransformacaoMedidas.java:6`) — confirma o próprio javadoc da
classe (linhas 37-38): "não é referenciado por Main.java nem por nenhum
caminho de produção."

**Zero consumidores em comum.** A mensagem do commit `7f6ad5c` cita reaproveitamento
deliberado de `gerard.semantica.numero.*`, mas não menciona
`gerard.semantica.papel.PapelQuantitativo` nem `CatalogoPapeisSemanticos`
em nenhum momento, apesar de ambos já existirem havia dias e resolverem
um problema adjacente (a que domínio numérico um papel pertence). As
fábricas do piloto (`parte1`/`parte2`/`todo`,
`FabricaPapeisTransformacaoMedidas`) hardcodam
`DominioNumerico.NATURAIS`/`INTEIROS` em vez de consultar o catálogo já
existente.

**Decisão de modelagem em aberto, não presumida aqui**: se
`RelacaoEstruturalComparacao` for escrita reaproveitando
`gerard.dominio.campoaditivo.PapelQuantitativo`, caberia decidir, no passo
2 do processo abaixo, se as fábricas de papéis (incluindo as três novas de
Comparação — Referido/Referendo/Valor Relativo) devem continuar
hardcodando o domínio numérico ou passar a consultar
`CatalogoPapeisSemanticos`, que já registra exatamente esses três papéis.

## Processo a seguir quando esta tarefa for retomada

Mesmo processo já estabelecido nesta sessão para qualquer mudança de código:

1. Auditoria/investigação primeiro — somente leitura, antes de qualquer decisão de modelagem.
2. Decisões de modelagem (nomes, papéis, assinaturas, regras de validação) apresentadas para aprovação explícita, item por item, antes de qualquer diff.
3. Diff exato mostrado antes de aplicar.
4. Compilação completa e os harnesses existentes (`TestePilotoPapelQuantitativo`, `TestePilotoTransformacaoMedidas`) rodados antes e depois de cada mudança — mais o harness novo de Comparação, assim que existir.
5. Nenhum commit nem push sem pedido explícito.

## Autorização

Esta tarefa **não está autorizada a começar agora**. Existe só como registro, para ser retomada quando pedido explicitamente.
