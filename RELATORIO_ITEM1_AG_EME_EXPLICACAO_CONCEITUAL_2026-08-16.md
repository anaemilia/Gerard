# Item 1 (AG_EME) — explicação conceitual do papel manipulado, no objeto rico

Data: 2026-08-16

## Objetivo

Fechar o item 1 do levantamento de pendências de 2026-08-11
(`LEVANTAMENTO_PENDENCIAS_2026-08-11.md`): `AG_EME` ("exibir mensagem
explicativa") mostrava, para as seis categorias, sempre a mesma frase
operacional (`ui.hint.chooseOperation` → "Escolha soma ou subtração e
digite o valor do {0}."), sem explicar conceitualmente o que aquele papel
significa.

## Decisão da usuária (2026-08-16)

1. "Preserve a localidade do conhecimento, pois foi pensada para essa
   finalidade: encapsular conhecimento para agir como objeto rico." — a
   explicação deve morar no objeto rico (`PapelQuantitativo`/
   `DescritorRepresentacaoPapel`, skill `gerard-knowledge-oriented-domain-objects`),
   não em texto solto na UI de `Main.java`.
2. "Parte1 e Parte2 usam o mesmo texto 'Parte'? Sim. Conceitualmente são
   iguais." — papéis simétricos numa mesma relação estrutural (Parte1/
   Parte2, Transformação1/Transformação2, Relação1/Relação2) compartilham a
   mesma explicação conceitual; papéis com posição estrutural diferente
   (o resultado de uma composição, por exemplo Todo, TransformaçãoFinal,
   RelaçãoFinal) têm explicação própria.
3. "Faça tudo, depois reviso." — arquitetura e conteúdo pedagógico das 13
   explicações, nas 4 línguas da interface, implementados nesta sessão.

## O que já existia (achado durante a investigação)

`PapelQuantitativo` já era exatamente o "objeto rico" da skill
`gerard-knowledge-oriented-domain-objects` (`chave`, `nomeConceitual`,
`descritorRepresentacao`), e já existiam fábricas completas e testadas
(`tests/java/TestePiloto*.java`) para todos os papéis dos 6 esquemas
formais do campo aditivo: `PapelQuantitativo.parte1/parte2/todo`,
`FabricaPapeisTransformacaoMedidas`, `FabricaPapeisComparacaoMedidas`,
`FabricaPapeisComposicaoDeTransformacoes`, `FabricaPapeisTransformacaoDeRelacao`,
`FabricaPapeisComposicaoDeRelacoes`. Essas fábricas não tocam nenhum
caminho de produção — são usadas só pelos testes-piloto, exatamente como
documentado em `PapelQuantitativo` ("hipótese arquitetural ainda não
validada" para uso como estado vivo). Não havia necessidade de reabrir essa
hipótese: bastava usar essas fábricas como catálogo somente-leitura de
metadados (chave de explicação), sem rotear estado real de produção por
elas.

Achado relevante: a chave viva usada por `Main.java` para "Valor Relativo"
em Comparação de Medidas é `"papel.diferenca"`
(`Main.obterValorCuradoPorIndiceEChave`), enquanto a fábrica-piloto usa
`"papel.valorRelativo"` — mesmo conceito, chave diferente. Da mesma forma,
`"papel.referente"` é sinônimo histórico vivo de `"papel.referendo"`
(`CatalogoPapeisSemanticos.registrarNatural("papel.referente", "Referendo")`,
`ResolvedorIncognitaCurada.eh(t, "referendo", "referente")`). Nenhuma dessas
duas chaves tem fábrica própria — são tratadas explicitamente no catálogo
novo, apontando para a explicação do papel real correspondente.

## Alterações

- `DescritorRepresentacaoPapel`: novo campo `chaveExplicacaoConceitual`
  (chave de mensagem i18n, nunca texto final — mesma regra já aplicada a
  `chaveRotulo`), com construtor novo e os dois construtores antigos
  preservados por compatibilidade (delegam com explicação vazia).
- `PapelQuantitativo.parte1/parte2/todo` e as 5 `FabricaPapeis*` passaram a
  fornecer a chave de explicação de cada papel.
- Criado `gerard.dominio.campoaditivo.CatalogoExplicacoesConceituaisPapel`:
  coordenador de escopo fechado (skill `gerard-knowledge-oriented-domain-objects`,
  "Relações entre vários objetos ficam em coordenadores de escopo
  fechado") que resolve a chave viva de um papel (a mesma que
  `Main.obterPapelIncognitaAtual()` já usa) para a chave de explicação,
  consultando os objetos ricos já existentes — não inventa conhecimento
  novo, só liga o que já existia à UI. Trata os dois sinônimos vivos sem
  fábrica própria (`papel.diferenca`, `papel.referente`). Fallback
  genérico (`explicacao.papel.generica`) para qualquer chave desconhecida
  ou nula — nunca lança exceção.
- `Main.mostrarDicaOperacaoIncognita()`: passou a montar a mensagem como
  explicação conceitual + instrução operacional (a mesma frase de antes),
  em vez de só a instrução operacional.
- Conteúdo pedagógico escrito nas 4 línguas da interface (pt/en/es/fr),
  13 chaves novas (`explicacao.papel.*`) em cada arquivo de mensagens,
  fundamentado nas fórmulas formais já documentadas nas classes
  `RelacaoEstrutural*` do domínio (Composição de Medidas: Todo=Parte1+Parte2;
  Transformação de Medidas: EstadoFinal=EstadoInicial+Transformação;
  Comparação de Medidas: Referendo=Referido+ValorRelativo; Composição de
  Transformações: TransformaçãoFinal=Transformação1+Transformação2;
  Transformação de Relação: RelaçãoFinal=RelaçãoInicial+Transformação;
  Composição de Relações: RelaçãoFinal=Relação1+Relação2).
- Novo teste `tests/java/TesteCatalogoExplicacoesConceituaisPapel.java`
  (mesmo padrão executável dos `TestePiloto*.java`, sem JUnit): confirma
  que todo papel vivo resolve para explicação específica (não a genérica);
  Parte1==Parte2 e Relação1==Relação2 (mesmo texto); Todo, TransformaçãoFinal
  e RelaçãoFinal são conceitualmente diferentes dos seus insumos; os dois
  sinônimos (`referente`/`diferenca`) resolvem para o papel real
  correspondente; chave desconhecida/nula cai no fallback sem exceção; e
  toda chave de explicação devolvida resolve para texto real, não vazio e
  diferente da própria chave, nas 4 línguas.
- Proteção estrutural acrescentada a `scripts/verificar_regressao_gerard.py`
  (seção "Item 1 (AG_EME)"): existência do campo/getter novo, presença da
  chave de explicação em cada fábrica, existência do catálogo e do
  tratamento dos sinônimos, uso do catálogo em `mostrarDicaOperacaoIncognita`,
  e paridade das 13 chaves nas 4 línguas.

## Verificação feita nesta sessão (sem JDK neste ambiente)

- Balanceamento de chaves/parênteses/colchetes verificado por script
  próprio (ignora comentários e literais de string/char) em todos os
  arquivos Java tocados ou criados — sem desbalanceamento.
- `python3 -m py_compile scripts/verificar_regressao_gerard.py` — sintaxe
  Python válida.
- Simulação do verificador de regressão completo (cópia-sombra do
  repositório, com o único passo que exige JDK — `ant clean jar` —
  substituído por um stub que marca sucesso): **222 checagens `[OK]`, 0
  `[ERRO]`, `APROVADO`** — inclui as 6 checagens novas específicas deste
  item mais as 13 de paridade i18n, e todas as checagens das fases e
  decisões anteriores (nada foi quebrado por esta mudança).
- Uma autoarmadilha foi encontrada e corrigida durante essa simulação: a
  checagem "mostrarDicaOperacaoIncognita consulta o catálogo..." buscava
  a substring exata `CatalogoExplicacoesConceituaisPapel.obterChaveExplicacao`,
  mas o código quebra essa chamada em duas linhas (nome da classe totalmente
  qualificado numa linha, `.obterChaveExplicacao(...)` na linha seguinte,
  mesmo estilo já usado em outras chamadas de `Main.java`) — a checagem foi
  reescrita para procurar as duas partes separadamente.
- Revisão manual, linha a linha, da lógica de `CatalogoExplicacoesConceituaisPapel`
  contra as asserções do novo teste (não pude compilar/rodar `javac`/`java`
  neste ambiente — sem JDK, sem acesso de rede para instalar um).

## O que falta (precisa rodar no seu ambiente Windows real)

1. `ant clean jar` — build completo.
2. `python scripts\verificar_regressao_gerard.py` — desta vez rodando o
   `ant clean jar` de verdade (não o stub usado aqui), deve terminar em
   `APROVADO: verificador de regressão completo, nenhuma falha registrada.`
3. Compilar e rodar o teste novo (não faz parte de `ant`/do verificador de
   regressão — é um harness executável independente, mesmo padrão dos
   `TestePiloto*.java`):
   ```
   javac -cp build\classes -d build\classes tests\java\TesteCatalogoExplicacoesConceituaisPapel.java
   java -cp build\classes TesteCatalogoExplicacoesConceituaisPapel
   ```
   Sucesso esperado: uma linha `OK - ...` para cada verificação, terminando em
   `TODOS OS TESTES DE CatalogoExplicacoesConceituaisPapel PASSARAM.` — sem
   `AssertionError` nem exceção.
4. Verificação visual (opcional, mas recomendável antes de considerar
   fechado): rodar o Gerard (`java -jar dist\GerardNetBeans_D3_Leitura_Redes_Transicoes.jar`
   ou `ant run`), chegar a uma incógnita em qualquer categoria, clicar em
   "Ver dica" o suficiente para disparar `AG_EME` (ou o gatilho existente
   dessa affordance), e conferir que a caixa de diálogo agora mostra duas
   frases: a explicação conceitual do papel, depois a instrução de
   escolher soma ou subtração — nos quatro idiomas, se possível.

## Conclusão

Item 1 implementado e verificado estruturalmente (sem JDK neste ambiente).
Falta a validação real (build, verificador de regressão com `ant` de
verdade, teste novo, e checagem visual) no seu ambiente Windows — os
comandos acima replicam o mesmo processo já usado nas fases e decisões
anteriores desta sessão.
