---
name: gerard-modelo-usuario
description: Esquema, versionamento e projeções de leitura do Modelo do Usuário do Gérard — dimensões armazenadas, regras explicáveis publicadas pelo Agente Modelador e fotografia carregada no login. Use ao criar, revisar ou estender perfil, diagnóstico, regra adaptativa ou contexto de usuário. Esta skill possui o esquema; o Modelador possui a aprendizagem e os proprietários semânticos possuem a seleção dentro de seus repertórios locais.
---

# Modelo do Usuário — Gérard

## Status

As cinco dimensões nasceram do Quadro 5.60. A afirmação histórica de que não
havia implementação deixou de ser válida: hoje existem `ModeloUsuario`,
`RepositorioModeloUsuario`, `DiagnosticoTarefa` e inferência parcial. Antes de
estender o esquema, confrontar esta fonte com o código real.

## Regra de leitura do Quadro 5.60

Decisão explícita da usuária em 2026-08-12: somente as colunas 0
(`Dimensões`) e 2 (`Utilização no modelo`) contêm conhecimento que integra a
especificação do Modelo do Usuário. A coluna 1 (`Fonte`) é exclusivamente
referência/proveniência acadêmica.

Consequentemente, nomes como `Ecolab`, `Agente Diagnóstico` e `Cenários
AnimalWatch`, quando aparecem na coluna 1, não são dimensões, atributos,
componentes, agentes, condições de regra ou entradas da decisão adaptativa do
Gérard. Podem ser preservados apenas como citação ou metadado de proveniência,
separados do conhecimento executável. A fonte nunca altera a seleção de ajuda
nem o conteúdo de uma fotografia do Modelo do Usuário.

## Perfil não é o Modelo do Usuário inteiro

Decisão explícita da usuária em 2026-08-12: os perfis integram o Modelo do
Usuário, mas não são, sozinhos, o elemento de tomada de decisão adaptativa. O
modelo possui outras dimensões — nível/complexidade das tarefas, partes do
conhecimento e fases e diagnóstico da tarefa — que não podem ser substituídas
por preferências cadastrais ou de apresentação.

Preferências do perfil podem orientar como uma decisão já fundamentada será
materializada, por exemplo modalidade de mídia, forma da mensagem ou estilo de
interação. Elas não determinam isoladamente se haverá ajuda, quando ela será
oferecida nem qual função pedagógica será escolhida. A tomada de decisão deve
consultar a projeção multidimensional pertinente do Modelo do Usuário, o
diagnóstico factual corrente e as regras publicadas aplicáveis. Só depois dessa
decisão a mídia preferida escolhe a forma concreta de apresentação entre as
formas disponíveis para o repertório do proprietário semântico.

Um teste que varia somente preferências é válido como teste separado de
personalização/materialização da interface. Ele não deve ser apresentado como
teste da tomada de decisão adaptativa do Modelo do Usuário completo.

## Dimensões do Modelo do Usuário

### 1. Nível de tarefas (complexidade)

Tarefas variam de mais fáceis a mais complexas, dentro das categorias de estruturas aditivas usadas no Gérard (composição, transformação, comparação). Referência: organização de tarefas por grau de complexidade (Magina et al., 2000).

### 2. Partes do conhecimento e fases

Em qual categoria de estruturas aditivas o usuário tem maior domínio, e qual ele domina menos.

### 3. Perfil do aluno

- Nome, id, idade, sexo.
- Identificam unicamente o usuário; podem ser usados para mensagens privativas (ex.: usando o nome na mensagem).

### 4. Perfil da aprendizagem

- **Mídia preferida**: som, gráfico, linguagem natural, vídeo ou história em
  quadrinhos. É um único valor mutuamente exclusivo; a interface deve
  representá-lo como escolha única, e não como várias preferências booleanas.
  Vídeo e história em quadrinhos podem materializar a mesma narrativa curada
  em sintaxes visuais diferentes. Essa preferência não dispara ajuda e não
  escolhe sua função pedagógica.
- **Nível de escolaridade**: 1º grau, 2º grau, graduação, pós-graduação.
- Uso: orienta a construção de mensagens e o estilo de interação — usuários com maior grau de instrução podem receber mensagens mais abstratas; usuários com menor grau de instrução podem receber mensagens mais contextualizadas com o problema em resolução.

### 5. Diagnóstico da tarefa

- **Tarefa** (Ação Instrumental) — ver `gerard-log-acao-instrumental` para o esquema completo dessa referência; não duplicar aqui.
- **Suporte**: nenhum | parcial | total — o suporte recebido pelo usuário anteriormente ao tentar realizar a mesma tarefa. Objetivo: diminuir essa ajuda ao longo do tempo.
- **Internalização**: 0 ou 1 — quando o valor é 1, a ajuda para aquele conteúdo é removida totalmente.
- **Probabilidade de saber o conteúdo** — obtida via teorema de Bayes. Dependendo dessa probabilidade, o atributo Internalização vai para 1 ou 0.
- **Dificuldade autorrelatada, explicação do elemento e explicação geral** (extensão fora do Quadro 5.60, decidida com o usuário em 2026-07-23): campos escritos pelo próprio usuário no Artefato Explicativo (`TelaArtefatoExplicativo`) — dificuldade (fácil/intermediária/difícil) e justificativa por elemento, mais uma explicação geral da estratégia da tentativa. É o análogo escrito da entrevista pós-tarefa usada na tese de Queiroz (2012), que motivou a extensão. `AgenteModelador.registrarExplicacaoNoUltimoDiagnostico` complementa, com esses campos, o caso mais recente já armazenado para a mesma tarefa — não cria um caso novo. Opcional (o artefato é preenchido depois da ação, nem toda ação tem autorrelato) e a explicação geral se repete em todos os casos da mesma tentativa (replicação aceita por ora, não normalizada).
- **Nível conceitual estimado e curado** (extensão decidida com o usuário em 2026-07-23, a partir de Vergnaud 1998 — "The role of language and symbols in representation", seção 3): `nivelConceitualEstimado` é um palpite automático (`AnalisadorNivelConceitual`, casamento de padrão sobre `explicacaoElemento`) — sinal fraco, nunca usar direto em inferência de regras. `nivelConceitualCurado` é nulo até um pesquisador confirmar/corrigir na aba "Curadoria - Nível Conceitual" da Visão de Pesquisador; só esse campo deve alimentar `InferenciaRegrasModelador`.
- **Invariante operatório** (`invarianteOrigem`/`invarianteCodigo`/`invarianteSimbolico`/`invarianteObservacao`, extensão decidida com o usuário em 2026-07-23): ao contrário do nível conceitual, **não** é palpite automático — é atribuído diretamente pelo pesquisador no Artefato Explicativo, escolhendo de um catálogo fechado (ex.: "Todo = Parte₁ ∪ Parte₂") ou registrando uma forma simbólica nova. Por ser decisão humana direta, entra sem estágio de curadoria como insumo de `InferenciaRegrasModelador` (atributo `invarianteCodigo`, junto de tarefa/regraDeAcao/suporte) — não confundir com o campo teórico "invariante" que a tese original também menciona: nenhum agente calcula automaticamente qual invariante uma ação mobiliza, isso continua de fora de propósito.

## Relação com os agentes

- O **Agente Modelador** normaliza casos, executa J48/PART + Apriori e publica
  uma nova versão explicável deste modelo.
- No login, o sistema cria uma fotografia de sessão imutável da versão
  publicada mais recente para o usuário.
- Cada proprietário semântico consulta apenas uma projeção de leitura dessa
  fotografia e escolhe uma ajuda do próprio repertório.
- A seleção da ajuda pertence ao proprietário semântico, dentro de seu
  repertório local.

## Regras adaptativas publicadas

Cada regra disponibilizada para aplicação deve possuir, no mínimo:

- identificador e versão;
- algoritmo de origem (`PART`, `J48` ou `APRIORI`);
- data de publicação e proveniência dos casos;
- condições de aplicação expressas em atributos do modelo e fatos permitidos;
- proprietário semântico e escopo (por exemplo, `PAPEL`, `RELACAO`,
  `TENTATIVA` ou `SITUACAO`), em vocabulário aberto a outros objetos
  semanticamente definidos;
- apoio recomendado dentre o repertório do proprietário;
- suporte, confiança e lift quando o algoritmo os fornecer;
- estado de publicação, permitindo rejeição ou retirada de uma regra.

Regra minerada não é invariante operatório nem conclusão sobre
conceito-em-ação. Atribuições analíticas continuam sendo do pesquisador.

## Fotografia da sessão e projeções

A fotografia carregada no login permanece estável até o logout. Atualizações
publicadas pelo Modelador passam a valer somente numa sessão posterior.

Essa fotografia representa o nível histórico/intersessões da adaptação. Ela
não contém nem substitui o Modelo da Situação/Solução corrente. Durante a
sessão, o proprietário semântico combina a projeção histórica com fatos
tipados e mínimos produzidos pela tentativa, pela situação ou pela relação
estrutural responsável. Esses fatos podem mudar a cada ação sem mutar a
fotografia carregada.

Não entregar `ModeloUsuario` mutável inteiro aos objetos. Produzir
`ContextoAdaptativoUsuario` imutável, reduzido ao proprietário e à decisão
corrente. A decisão deve registrar a versão da fotografia e a regra usada,
inclusive quando nenhuma regra aplicável for encontrada.

Este arquivo não decide como J48/PART e Apriori inferem regras nem define o
conteúdo dos repertórios; documenta o que é armazenado e exposto para leitura.

### Estado de implementação — P2.2A, 2026-08-12

`gerard.adaptacao.modelousuario.FotografiaModeloUsuario` implementa a cópia
multidimensional, tipada e estável de uma versão do `ModeloUsuario`. Ela não é
ligada ainda ao login nem à interface. As projeções distinguem três estados:

- `PRESENTE`: o valor foi efetivamente registrado;
- `AUSENTE_NO_MODELO`: a dimensão foi solicitada, mas não existe dado com
  proveniência operacional suficiente;
- `NAO_SOLICITADO`: a dimensão foi omitida por localidade do conhecimento.

Não transformar `AUSENTE_NO_MODELO` em zero, `false`, categoria, mídia ou nível
padrão. Em especial, os primitivos legados `internalizado=false` e
`probabilidadeSaberConteudo=0.0` não entram como valores conhecidos: como o
código atual não registra a proveniência de cálculo Bayesiano/publicação pelo
Modelador, a fotografia os expõe como ausentes.

`ContextoAdaptativoUsuario` não aceita mais um mapa genérico de atributos. Ele
exige uma `ProjecaoModeloUsuario` que solicite ao menos uma dimensão além de
perfil. Ensaios que tratem somente de mídia, escolaridade ou identificação usam
`ProjecaoPreferenciasUsuario`, separada e incapaz de se tornar, sozinha, contexto
de decisão adaptativa.

A fotografia recebe regras candidatas, copia somente as que possuem estado
`PUBLICADA` e ignora explicitamente `EXPERIMENTAL`, `RETIRADA` e `REJEITADA`.
Depois, o contexto local filtra as publicadas pelo proprietário semântico e por
seu escopo. Isso não publica automaticamente as regras históricas reais e não
liga nenhuma delas ao comportamento da aplicação.

### Primeiro consumidor da projeção — P2.2B, 2026-08-13

`IncognitaQuantitativa` é o primeiro consumidor operacional do contexto
tipado. Seu recorte pertinente solicita conjuntamente `NIVEL_TAREFAS` e
`DIAGNOSTICO_TAREFA`; o nível é lido para a categoria da situação e o suporte
anterior é lido do diagnóstico mais recente da mesma tarefa e do mesmo papel.
Ausência de uma dessas informações permanece ausência e não recebe valor
padrão.

O harness varia `PERFIL_APRENDIZAGEM` mantendo os demais fatos e confirma que
a seleção permanece igual. Isso não declara que preferências sejam inúteis:
apenas preserva a decisão de que perfil isolado não é autoridade para escolher
a função pedagógica do apoio.

### Carregamento no login — P2.3A, 2026-08-13

`SessaoAdaptativaUsuario` é a fronteira de ciclo de vida da fotografia. Ela lê
o `ModeloUsuario` cadastrado no `RepositorioModeloUsuario` somente quando o
diálogo confirma o login, cria `FotografiaModeloUsuario.carregarNoLogin` e
mantém essa mesma instância até o logout. Alterações posteriores no repositório
do modelo ou na coleção fornecida de regras não atravessam a fotografia.

A fotografia de login usa `conteudo-sha256:` seguido da impressão digital do
conteúdo congelado. A identificação é determinística e independente da ordem de
transporte das regras. Ela identifica o recorte completo carregado e não
substitui a identidade e a versão editorial próprias de cada regra.

A fonte de regras é um contrato separado. Desde a P2.4A.1, a configuração real
lê `~/Gerard/analises/regras_adaptativas_publicadas.jsonl`, escrito
explicitamente pelo Modelador e filtrado por usuário. `regras_inferidas.tsv`
continua somente experimental, e a base JSON derivada dos protocolos humanos
continua em outro esquema, sem proprietário semântico e repertório local
compatíveis. Nenhuma delas é elevada a `PUBLICADA` por conveniência durante o
login. Uma publicação posterior ao login só integra a próxima fotografia.

### Projeção para a incógnita atual — P2.3B, 2026-08-13

O proprietário semântico declara suas dimensões relevantes por
`dimensoesModeloUsuarioRelevantes()`. `SessaoAdaptativaUsuario` usa essa
declaração para produzir o contexto a partir da fotografia ativa, filtrando as
regras pelo proprietário e escopo já declarados. A fronteira de sessão não
recebe uma lista de dimensões escolhida por `Main.java`.

Para `IncognitaQuantitativa`, a projeção contém exatamente
`NIVEL_TAREFAS` e `DIAGNOSTICO_TAREFA`. `PERFIL_ALUNO`,
`PERFIL_APRENDIZAGEM` e `PARTES_CONHECIMENTO_E_FASES` ficam
`NAO_SOLICITADO`; portanto, preferências não vazam por conveniência para esse
contexto. A versão continua sendo a da fotografia criada no login, inclusive
quando o repositório mutável é alterado durante a sessão.

### Rastreabilidade da decisão — P2.3C, 2026-08-13

A decisão aplicada registra a versão dessa mesma fotografia, o identificador e
a versão da regra publicada, o proprietário semântico, o diagnóstico factual e
o apoio escolhido. `SEM_REGRA_APLICAVEL` também é uma decisão registrável e não
autoriza fabricar regra a partir da base JSON histórica. A apresentação do
apoio é outro fato, registrado somente após confirmação da representação.

Para que a fundamentação permaneça reconstruível mesmo quando o catálogo for
substituído numa publicação futura, uma decisão que aplica regra também deve
transportar e registrar o algoritmo de origem e a proveniência dos casos
congelados naquela regra. Isso não transforma proveniência em dimensão do
Modelo do Usuário nem em condição de seleção; é metadado de rastreabilidade.

---

*Conteúdo reconciliado em 2026-09-03 a partir de `.agents/skills/gerard-modelo-usuario/SKILL.md`
(mais recente e verificado contra o código real — ver `.claude/skills/gerard-consistencia-estado`
para o histórico desta sessão de auditoria de skills). A versão anterior deste arquivo, de
2026-07-20, afirmava incorretamente que nenhuma das cinco dimensões estava implementada; as oito
classes citadas acima foram confirmadas presentes em `src/` antes desta substituição.*
