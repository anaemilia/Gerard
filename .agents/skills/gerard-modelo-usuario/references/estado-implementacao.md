# Estado verificado da implementação do Modelo do Usuário

Leia esta referência ao alterar fotografia, ciclo de vida da sessão,
projeções, consumidores ou rastreabilidade das decisões adaptativas. O esquema,
as dimensões e as fronteiras de autoridade permanecem no `SKILL.md`.

## P2.2A — fotografia tipada (2026-08-12)

`gerard.adaptacao.modelousuario.FotografiaModeloUsuario` implementa a cópia
multidimensional, tipada e estável de uma versão do `ModeloUsuario`. As
projeções distinguem três estados:

- `PRESENTE`: o valor foi efetivamente registrado;
- `AUSENTE_NO_MODELO`: a dimensão foi solicitada, mas não existe dado com
  proveniência operacional suficiente;
- `NAO_SOLICITADO`: a dimensão foi omitida por localidade do conhecimento.

Não transformar `AUSENTE_NO_MODELO` em zero, `false`, categoria, mídia ou nível
padrão. Os primitivos legados `internalizado=false` e
`probabilidadeSaberConteudo=0.0` não entram como valores conhecidos enquanto
não houver proveniência de cálculo Bayesiano ou publicação pelo Modelador.

`ContextoAdaptativoUsuario` exige uma `ProjecaoModeloUsuario` que solicite ao
menos uma dimensão além de perfil. Ensaios somente de mídia, escolaridade ou
identificação usam `ProjecaoPreferenciasUsuario`, que não pode se tornar,
sozinha, contexto de decisão adaptativa.

A fotografia copia somente regras com estado `PUBLICADA` e ignora
`EXPERIMENTAL`, `RETIRADA` e `REJEITADA`. O contexto local filtra as regras
publicadas pelo proprietário semântico e por seu escopo. Isso não publica
automaticamente regras históricas nem as liga ao comportamento da aplicação.

## P2.2B — primeiro consumidor (2026-08-13)

`IncognitaQuantitativa` é o primeiro consumidor operacional do contexto
tipado. Solicita conjuntamente `NIVEL_TAREFAS` e `DIAGNOSTICO_TAREFA`; lê o
nível para a categoria da situação e o suporte anterior do diagnóstico mais
recente da mesma tarefa e do mesmo papel. Ausência permanece ausência.

O harness varia `PERFIL_APRENDIZAGEM` mantendo os demais fatos e confirma que
a seleção permanece igual. Isso não declara preferências inúteis; preserva que
perfil isolado não escolhe a função pedagógica do apoio.

## P2.3A — carregamento no login (2026-08-13)

`SessaoAdaptativaUsuario` lê o `ModeloUsuario` no
`RepositorioModeloUsuario` somente quando o diálogo confirma o login, cria
`FotografiaModeloUsuario.carregarNoLogin` e mantém a mesma instância até o
logout. Alterações posteriores no repositório ou na coleção de regras não
atravessam a fotografia.

A fotografia usa `conteudo-sha256:` seguido da impressão digital do conteúdo
congelado. A identificação é determinística e independente da ordem de
transporte das regras; não substitui identidade e versão editorial de cada
regra.

Desde P2.4A.1, a configuração real lê
`~/Gerard/analises/regras_adaptativas_publicadas.jsonl`, escrito pelo Modelador
e filtrado por usuário. `regras_inferidas.tsv` permanece experimental, e a
base JSON derivada dos protocolos humanos permanece em outro esquema. Nenhuma
é elevada a `PUBLICADA` por conveniência. Publicações posteriores ao login só
integram a fotografia da sessão seguinte.

## P2.3B — projeção da incógnita atual (2026-08-13)

O proprietário semântico declara as dimensões relevantes por
`dimensoesModeloUsuarioRelevantes()`. `SessaoAdaptativaUsuario` usa essa
declaração para produzir o contexto a partir da fotografia ativa, filtrando as
regras pelo proprietário e escopo. `Main.java` não escolhe essa lista.

Para `IncognitaQuantitativa`, a projeção contém exatamente
`NIVEL_TAREFAS` e `DIAGNOSTICO_TAREFA`. `PERFIL_ALUNO`,
`PERFIL_APRENDIZAGEM` e `PARTES_CONHECIMENTO_E_FASES` ficam
`NAO_SOLICITADO`. A versão continua sendo a fotografia criada no login mesmo
quando o repositório mutável é alterado durante a sessão.

## P2.3C — rastreabilidade da decisão (2026-08-13)

A decisão aplicada registra a versão da fotografia, o identificador e a versão
da regra publicada, o proprietário semântico, o diagnóstico factual e o apoio
escolhido. `SEM_REGRA_APLICAVEL` também é uma decisão registrável e não autoriza
fabricar regra a partir da base JSON histórica. A apresentação do apoio é outro
fato, registrado somente após confirmação da representação.

Uma decisão que aplica regra também transporta e registra o algoritmo de
origem e a proveniência dos casos congelados naquela regra. Isso mantém a
fundamentação reconstruível quando o catálogo for substituído, sem transformar
proveniência em dimensão do Modelo do Usuário ou em condição de seleção.
