# Modelo Semântico de Referência do GERARD

**Versão:** 3.0
**Data:** 2026-08-15
**Status:** documento normativo; não é uma skill operacional.

## 1. Finalidade

Este documento define a natureza dos elementos do GERARD e o vocabulário que deve orientar o modelo de domínio, as skills, os agentes, os eventos, a persistência, as representações e a análise da atividade.

O modelo semântico responde:

> O que cada elemento do sistema é, que significado possui e quais relações pode manter?

As skills respondem:

> Que capacidades reutilizáveis podem operar sobre esses elementos?

As skills não podem redefinir os conceitos teóricos deste documento. Em caso de conflito, este modelo semântico prevalece.

## 2. Fundamento na Teoria dos Campos Conceituais

A arquitetura deve respeitar as definições apresentadas por Vergnaud (1998):

- **Esquema:** organização invariante da conduta para uma determinada classe de situações.
- **Teorema-em-ação:** proposição que o sujeito considera verdadeira.
- **Conceito-em-ação:** objeto, predicado ou categoria que o sujeito considera pertinente.
- **Invariantes operatórios:** teoremas-em-ação e conceitos-em-ação que constituem componentes essenciais dos esquemas.
- **Representação:** processo dinâmico relacionado à organização da ação e da linguagem, e não apenas uma forma gráfica estática.

Referência normativa:

> VERGNAUD, Gérard. A Comprehensive Theory of Representation for Mathematics Education. *The Journal of Mathematical Behavior*, v. 17, n. 2, p. 167–181, 1998. DOI: 10.1016/S0364-0213(99)80057-3.

## 3. Triplo dinâmico do conceito

Um conceito é considerado segundo o triplo:

`C = (S, I, R)`

em que:

- `S` representa classes de situações que atribuem sentido ao conceito;
- `I` representa os invariantes operatórios mobilizados nos esquemas;
- `R` representa sistemas e processos de representação.

Esse triplo não deve ser tratado como uma decomposição triangular estática. Situações, invariantes operatórios e representações participam de uma organização dinâmica e relacional mediada por esquemas, ação e linguagem.

Nenhum elemento isolado do diagrama é, por si só, um conceito completo no sentido da Teoria dos Campos Conceituais.

## 4. Categorias do modelo semântico

### 4.1 Situação e classe de situações

Uma situação-problema concreta é uma instância de uma classe de situações. A classe de situações é relevante para a análise dos esquemas mobilizados pelo sujeito.

### 4.2 Papéis semânticos

`Parte`, `Todo`, `Transformação`, `Estado Inicial`, `Estado Final`, `Referido`, `Referendo` e `Valor Relativo` são papéis semânticos presentes em representações de situações.

Eles podem ser implementados como objetos de domínio semanticamente ricos, mas não devem ser denominados conceitos completos.

### 4.2.1 Condições do valor de um papel

O valor de um papel semântico pode estar em uma ou mais das seguintes condições,
que descrevem disponibilidade, validação, origem operacional e histórico — não
uma sequência única de estados mutuamente exclusivos:

- **dado**: o valor foi fornecido como parte da situação-problema.
- **desconhecido**: o valor ainda não foi determinado.
- **proposto**: um valor foi submetido para o papel e aguarda validação.
- **aceito**: um valor proposto foi validado e passou a integrar o estado do papel.
- **rejeitado**: um valor proposto foi invalidado por uma restrição local ou por
  uma relação estrutural.
- **calculado**: um valor foi produzido por uma relação estrutural a partir de
  outros papéis conhecidos, antes de ser aplicado ao papel.
- **revisado**: um valor previamente aceito foi substituído por uma nova
  proposta. Condição reservada; nenhuma implementação atual a produz.

Essas condições não substituem a distinção binária entre valor conhecido e
desconhecido; elas qualificam como e por que um valor chegou a um desses dois
estados.

### 4.2.2 Designação da incógnita original

A incógnita original é o papel quantitativo designado pela situação-problema
para ser determinado pelo participante. Essa identidade contextual permanece
estável depois que um valor é proposto ou aceito.

Na implementação, a designação não pode ser recuperada apenas perguntando se o
valor atual está ausente: durante a montagem, um papel dado ainda não
preenchido também pode estar momentaneamente sem valor. A ausência descreve o
estado atual; a designação de incógnita descreve o papel na situação.

> **Status de implementação (P2.2B, 2026-08-13):**
> `IncognitaQuantitativa` registra explicitamente essa designação no pacote
> piloto. O objeto referencia o `PapelQuantitativo` e a categoria da situação,
> sem duplicar o valor nem inferir novamente a incógnita pela ausência atual.

### 4.3 Relações estruturais formais

Expressões como:

- `Todo = Parte1 + Parte2`;
- `EstadoFinal = EstadoInicial + Transformacao`;

são relações estruturais formais utilizadas pelo sistema para descrever, calcular ou verificar a consistência de uma representação.

Elas não são, por si mesmas, invariantes operatórios. Uma relação formal só pode aparecer em uma hipótese de teorema-em-ação quando existirem registros suficientes para sustentar que o sujeito a considera verdadeira e a mobiliza em sua atividade.

Nomes recomendados para objetos computacionais:

- `RelacaoEstruturalComposicao`;
- `RelacaoEstruturalTransformacao`;
- `RelacaoEstruturalComparacao`.

> **Status de implementação (2026-08-29)**: as três relações estão
> implementadas como objetos de domínio e possuem harnesses executáveis.
> `RelacaoEstruturalComparacao` preserva
> `Referendo = Referido + ValorRelativo`; a sincronização de produção delega
> às relações estruturais, conforme o encerramento registrado em
> `TAREFA_PENDENTE_COMPARACAO_MEDIDAS.md`.

Evitar nomes como `InvarianteOperatorio` para classes que apenas verificam regras formais do sistema.

### 4.4 Objetos semânticos

Um objeto semântico representa uma entidade, um papel, um valor, uma relação estrutural, uma situação, uma tentativa ou outro elemento semanticamente definido do domínio.

Ele pode encapsular, conforme sua responsabilidade:

- identidade semântica;
- significado no domínio;
- restrições locais;
- relações permitidas;
- estado e comportamento próprios;
- descritores abstratos de representação;
- categorias de diagnóstico relacionadas ao seu estado;
- chaves de mensagens, quando forem específicas do elemento;
- dados serializáveis do domínio.

O objeto semântico não deve conhecer detalhes de renderização, como pixels, coordenadas de tela, `Graphics2D`, componentes Swing, efeitos visuais ou alças de mouse.

### 4.5 Representações

As representações incluem formas gráficas, textuais, numéricas, linguísticas e simbólicas, além das transformações e manipulações que participam do processo representacional.

O domínio pode fornecer descritores abstratos, como papel, símbolo, forma conceitual e chave de rótulo. A camada de representação converte esses descritores em componentes concretos de interface.

### 4.6 Skills

Skills são capacidades reutilizáveis que operam sobre o modelo semântico, por exemplo:

- validação;
- diagnóstico;
- classificação;
- geração de feedback;
- scaffolding e fading;
- análise da interação;
- serialização e exportação;
- formulação de hipóteses analíticas.

Skills não constituem o conhecimento estrutural do domínio e não devem redefinir o significado dos objetos.

### 4.7 Tentativa de resolução

Uma tentativa organiza a atividade de um usuário em uma situação-problema específica. Deve permitir vincular:

- ações;
- estados sucessivos das representações;
- verbalizações e explicações;
- feedbacks apresentados;
- eventos do sistema;
- hipóteses analíticas e suas evidências.

A mesma situação-problema pode gerar várias tentativas.

### 4.8 Ação e evento semântico

Uma ação é uma ocorrência significativa na atividade. Um evento semântico é seu registro factual e contextualizado.

O evento deve distinguir a origem:

- `USUARIO`;
- `SISTEMA`;
- `INFERENCIA_COMPUTACIONAL`;
- `PESQUISADOR`.

O critério que distingue `SISTEMA` de `INFERENCIA_COMPUTACIONAL` é o tipo de
operação, não o determinismo do algoritmo — mesmo que a operação seja
complexa:

- `SISTEMA`: a operação aplica uma regra explícita e fixa (cálculo, validação,
  conversão, aplicação de um resultado já calculado) sem derivar um juízo a
  partir de evidências. Uma relação estrutural que calcula ou aplica um valor
  ausente produz origem `SISTEMA`.
- `INFERENCIA_COMPUTACIONAL`: a operação deriva uma classificação, diagnóstico,
  estimativa, hipótese ou sugestão interpretativa a partir de evidências, mesmo
  que o mecanismo usado para derivá-la seja determinístico.

Um evento não é uma interpretação automática sobre o conhecimento do sujeito.

Exemplo concreto do domínio: o preenchimento de um papel quantitativo
incógnito, da primeira vez, é sempre `USUARIO` — só o participante pode
preenchê-lo (`PapelQuantitativo.posicionar`, origem `ORIGEM_USUARIO` por
padrão). Depois que o diagrama atinge consistência, qualquer recálculo
automático que o sistema faça para manter posições e representações
sincronizadas (equação fixa `EstadoFinal = EstadoInicial + Transformação`
ou equivalente) é `SISTEMA` — nunca `INFERENCIA_COMPUTACIONAL`, porque não
deriva julgamento de evidências.

Nota de implementação (`OrigemAcao`, pacote piloto): esse vocabulário hoje
existe só em `gerard.dominio.campoaditivo`, sem conexão com o log de ações
de produção (`LoggerInteracaoGerard`/`EventoLogGerard`) — ver registro
separado sobre a lacuna de log em `TAREFA_PENDENTE_LOG_CONSISTENCIA_AUTOMATICA.md`.

Cardinalidade ação:evento: adotada a relação 1:N. Uma ação semanticamente
constituída pode produzir vários eventos factuais — comando, recálculos do
sistema, resultado da validação e apoios apresentados — correlacionados pelo
mesmo `action_id`.

Decisão corrigida pela usuária em 2026-08-11: **`ARRASTAR → POSICIONAR` é a
fronteira do gesto, não a definição suficiente de uma ação instrumental**.
`POSICIONAR` estabelece a posição final do gesto em qualquer ponto. Se o
ponto estiver fora de qualquer elemento do diagrama, o registro termina como
gesto com destino geométrico ausente; não há comando semântico, `action_id`,
avaliação C/E nem rejeição pedagógica. Se houver um elemento de destino, a
camada de interação pode produzir uma ação instrumental semanticamente
identificada, que então recebe `action_id` e pode ser avaliada.

Pressionamento, movimento e soltura são fatos técnicos correlacionados por
`gesture_id` em log próprio. Recálculos automáticos intermediários pertencem
à ação somente depois que ela existe e têm origem `SISTEMA`. Um novo gesto
não pode fabricar ação ausente nem reutilizar o `action_id` de uma ação
anterior.

O limite pedagógico de `N=3` continua sendo uma sequência de três ações
instrumentais rejeitadas do mesmo item. As ações possuem três `action_id`
distintos e são correlacionadas por `rejection_sequence_id`. Gestos sem ação
não entram na sequência. Acionar o botão "restaurar" também constitui outra
ação separada.

> **Status de implementação (P3.2, 2026-08-24):** os dois comandos Restaurar
> da interface são tipos distintos de ação da tentativa/modelagem e recebem
> novo `action_id`. A ação de restauração encerra sequências anteriores, mas
> não recebe o `rejection_sequence_id` encerrado como se fosse uma quarta
> rejeição; essas identidades permanecem somente no contexto factual do
> registro. `TentativaModelagemAditiva` é o proprietário semântico da ação, e
> os papéis envolvidos aplicam apenas sua mudança local.

Isso introduz um novo campo, `action_id`, que correlaciona os eventos de
uma mesma ação — distinto de `event_id` (o identificador de cada evento
individual; hoje é `id_acao`, cuja renomeação para `event_id` é
recomendada mas ainda não aplicada nesta decisão).

Cada objeto semântico deve carregar seu próprio repertório de Scaffolding
(estilos de interação possíveis — manipulação, som, vibração, atração
magnética, etc. — mensagens e tipos de ajuda concreta) como conhecimento
local, consistente com o princípio da localidade do conhecimento: manter
esse repertório espalhado pela interface causaria inconsistência. Decisão da
usuária em 2026-08-11: o proprietário semântico também seleciona, entre os
itens do seu repertório, a ajuda aplicável ao diagnóstico factual corrente,
consultando somente uma projeção imutável e relevante do Modelo do Usuário.
Essa seleção devolve um descritor semântico de ajuda; a interface apenas o
materializa e registra o que foi efetivamente apresentado.

"Proprietário semântico" não significa necessariamente um papel isolado. A
localidade acompanha o escopo do conhecimento: uma restrição de um papel
pertence ao papel; uma relação entre papéis pertence à relação estrutural;
uma regra sobre a tentativa inteira pertence à tentativa; uma regra sobre a
situação pertence à situação-problema. Nenhum desses objetos conhece Swing,
geometria, persistência, Weka, Apriori ou o modelo mutável completo.

Vocabulário de modalidade de interação (já citado em
`gerard-semantic-event-logging/SKILL.md:41` como "modalidade de
interação", nunca definido até aqui — aberto, extensível, terminologia
alinhada à taxonomia de modalidades de IHC): `MOUSE`, `TOQUE` (dedo, ex.
dispositivos móveis), `TECLADO`, chamada programática, `NAO_INFORMADO`.
Novas modalidades podem ser adicionadas conforme novos dispositivos forem
suportados, sem quebrar as já existentes.

O repertório de Scaffolding de cada objeto semântico (parágrafo sobre o
repertório de Scaffolding, acima) é organizado em dois eixos independentes,
alinhados à literatura de Scaffolding pedagógico:

- **Tipo funcional** (Hannafin, Land & Oliver, 1999): conceitual (ajuda a
  entender um conceito), metacognitivo (ajuda a refletir sobre o próprio
  raciocínio), procedimental (ajuda a saber usar a interface/recursos),
  estratégico (sugere uma abordagem para resolver a tarefa).
- **Modalidade de entrega**: visual (mensagem/tela de ajuda), sonora
  (som), háptica (vibração), manipulativa (material concreto — diagramas
  com quadradinhos manipuláveis, alinhado ao conceito de manipulativos
  virtuais da educação matemática), guiada por movimento (atração
  magnética — altera a dinâmica do posicionamento para orientar o
  participante).

Ambos os eixos são abertos e extensíveis — não são enums fechados.
Scaffolding, nesse sentido, não se limita a uma resposta reativa a erro —
é suporte pedagógico ajustável (pode ser proativo, reativo, ou diminuir
conforme o participante ganha autonomia), consistente com a literatura de
zona de desenvolvimento proximal.

Arquitetura de evento: adotada a estrutura envelope + payload (Seção 19.1
da Revisão 5), com referência a três padrões existentes — xAPI/Experience
API e Caliper Analytics (1EdTech/IMS Global, específicos de eventos de
aprendizagem: estrutura Ator-Verbo-Objeto/Ação com contexto e resultado
opcionais) e CloudEvents (CNCF, especificação geral de arquitetura
orientada a eventos: envelope fixo com `data` variável). Nenhuma estrutura
nova foi inventada para esta decisão.

Envelope (núcleo fixo, presente em todo evento): `event_id`, `action_id`
(decisão de cardinalidade ação:evento, acima), tipo semântico do evento,
origem da ação (`OrigemAcao`), timestamp.

Payload (variável, específico por tipo de evento): estado anterior/
posterior, valor proposto ou calculado, resultado da validação,
diagnóstico factual (quando houver rejeição), modalidade de interação
(parágrafo sobre vocabulário de modalidade de interação, acima), estilo de
Scaffolding utilizado, quando houver (parágrafo sobre os dois eixos de
Scaffolding, acima).

Implementado em 2026-08-07: `EventoEnvelope` (núcleo fixo) e
`EventoPapelQuantitativo` (composição do envelope + payload) — ver
`TAREFA_PENDENTE_FLUXO_TENTATIVAS_E_SCAFFOLDING.md` e
`RELATORIO_FEEDBACK_EXIBIDO_2026-08-07.md`. A reestruturação preservou
todos os getters públicos originais e o formato de `paraMapa()` (só
ganhou chaves novas, nenhuma foi removida ou renomeada) — nenhum
consumidor existente do mapa quebrou.

Versionamento de esquema: versão embutida no campo "tipo" do envelope
(sufixo `.vN`, ex.: `papel_quantitativo.posicionado.v1`), seguindo a
convenção "type-based versioning" recomendada pela especificação
CloudEvents já referenciada na decisão de arquitetura de evento —
consumidores podem rotear versões diferentes do mesmo tipo de evento para
tratamentos diferentes. Não há campo de versão separado.

Implementado em 2026-08-07 junto com a reestruturação envelope +
payload (decisão anterior) — `TipoEventoPapel.chaveVersionada()` gera o
sufixo `.v1` a partir do próprio enum.

Evento `FEEDBACK_EXIBIDO`: especificado, com critério de confirmação que
depende da modalidade de entrega do Scaffolding (parágrafo sobre
repertório de Scaffolding, acima):

- **Modalidades passivas** (visual, sonora): critério "renderizado" —
  dispara quando o componente de interface foi de fato construído e
  exibido na tela, ou o som foi de fato reproduzido — não no momento em
  que o sistema apenas decide mostrar algo (`chaveFeedbackPedagogico`
  sendo produzida não basta). Alinhado à distinção entre impressão
  servida e impressão renderizada usada em métricas de publicidade
  digital (IAB/MRC).
- **Modalidades interativas** (háptica, guiada por movimento, manipulativa):
  critério "affordance ativada" — dispara quando o mecanismo de interação
  foi disponibilizado para o participante operar (ex.: a atração magnética
  foi habilitada durante o arraste; o material concreto ficou
  manipulável) — não quando o participante de fato opera sobre ele. A
  operação do participante é uma ação própria dele, registrada
  separadamente (vocabulário de modalidade de interação, acima), não
  parte deste evento.

Em nenhum dos dois casos o evento afirma que o participante percebeu,
entendeu ou prestou atenção ao feedback — isso continua não registrado,
consistente com a proibição já existente de declarar interpretações sobre
o participante a partir de um evento isolado (Seção 4.10).

Implementado em 2026-08-07: `TipoEventoPapel.FEEDBACK_EXIBIDO` e
`ModalidadeEntregaScaffolding` (piloto), publicado tanto no piloto
(`EventoPapelQuantitativo.feedbackExibido(...)`) quanto no log real de
produção — `Main.registrarFeedbackExibido(...)` traduz o mesmo
vocabulário (estiloScaffolding, modalidade, critério de confirmação)
para uma linha real no log, sem instanciar a classe do piloto em
`Main.java` (o domínio não grava logs diretamente — mesmo padrão de
todo o resto desta seção). Ligado a 5 pontos reais de disparo:
AG_EMLQ, AG_EME, AG_EMCME (mensagem e material concreto) e AG_EMS —
ver `TAREFA_PENDENTE_FLUXO_TENTATIVAS_E_SCAFFOLDING.md`.

### 4.8.1 Mobilização do invariante operatório: sugestão do sistema e atribuição do pesquisador

O invariante operatório mobilizado em uma ação não é calculado nem inferido
pelo sistema — é atribuído pelo pesquisador, como interpretação de evidências,
nunca como resultado de um algoritmo.

Dois mecanismos distintos participam desse processo:

- **Sugestão do sistema**: `SugestorInvarianteOperatorio.sugerirCodigo` propõe
  um código candidato para apoiar o pesquisador. É auxiliar e não-vinculante:
  enquanto não for adotada, essa sugestão não registra origem no evento
  correspondente — o campo `origem` fica vazio.
- **Atribuição do pesquisador**: na visão do pesquisador, um combobox permite
  selecionar um invariante operatório já existente ou criar um novo, e
  relacioná-lo à ação. Esse é o evento que registra que um invariante foi
  mobilizado; sua origem é sempre `PESQUISADOR`, adotando ou não uma sugestão
  do sistema.

O registro de log usa um terceiro código de agente, `"P"` (Pesquisador),
além de `"S"` (participante) e `"C"` (computador): a linha gravada por
`registrarExplicacaoMatematica` usa `"P"` porque carrega a atribuição de um
invariante — seção estruturalmente rotulada "PREENCHIMENTO DO PESQUISADOR"
na tela (`TelaArtefatoExplicativo.java:234-305`: combobox `invarianteCatalogo`
+ campo `observacaoInvariante`). Atribuir um invariante é sempre uma decisão
do pesquisador, nunca do participante — nunca `"S"` para essa linha.

Essa mesma linha também carrega o autorrelato do participante
(`explicacaoElemento`/`dificuldade`, seção "Tarefa matemática",
`TelaArtefatoExplicativo.java:150-205`) — conteúdo do participante, sem
rótulo de autoria do pesquisador no código ou na interface. Marcar a linha
inteira como `"P"` é uma simplificação conhecida: hoje não há um esquema
que distinga, dentro da mesma linha, a autoria de cada trecho
separadamente. Consequência prática: linhas de atribuição de invariante
não entram nas contagens e detecções de ação do participante que dependem
do agente `"S"` (ex.: sequências causais computador-participante em
`TelaVisaoPesquisador`).

A abertura de `TelaArtefatoExplicativo` exige senha do pesquisador
(`autenticarPesquisador()`, reaproveitando `SENHA_VISAO_PESQUISADOR`,
`Main.java:2154-2158`), sempre, sem exceção — a tela só é construída depois
da autenticação bem-sucedida. O motivo é a seção do pesquisador: o
invariante operatório é uma ação mental do participante que só o
pesquisador está em posição de validar e codificar — a senha garante que
o pesquisador esteja presente para fazer isso. Ela não implica que o
autorrelato também seja preenchido pelo pesquisador; a estrutura da tela
mostra o contrário (seção 1, sem rótulo de autoria do pesquisador). A
senha não resolve a simplificação do parágrafo anterior — só garante que a
parte que exige o pesquisador (a seção 4) tenha, de fato, o pesquisador
presente.

O campo de origem do invariante (`"PESQUISADOR"`/`"CATALOGO"`) responde uma
pergunta diferente — de onde veio o valor do código escolhido (criado pelo
pesquisador ali mesmo, ou selecionado de um catálogo já existente), não
quem registrou a linha. Nenhum dos campos de agente ou de origem do
invariante corresponde a `OrigemAcao` (seção 4.8).

### 4.8.2 Os cinco elementos "origem" fora de OrigemAcao

Os cinco elementos "origem" fora de `OrigemAcao` permanecem separados por
design, não por lacuna a resolver. Cada um vive em um Contexto Delimitado
(Bounded Context, Eric Evans, Domain-Driven Design) genuinamente
diferente: `OrigemAcao` no pacote piloto de domínio; `OrigemAvaliacao` na
avaliação de agentes pedagógicos; a lista de agentes de
`ontologia_gerard.json` no domínio de conhecimento de pesquisa;
`origemEvento` na proveniência de importação de dados históricos;
`invarianteOrigem` na autoria de anotação teórica (§4.8.1); os códigos
`"S"`/`"C"`/`"P"` no log de produção (§4.8.1).

Um Contexto Delimitado é, segundo Evans, uma fronteira onde se elimina
ambiguidade — termos, definições e regras se aplicam de forma consistente
dentro dele, mas não precisam (e não devem) ser forçados a um modelo único
fora dele. Nenhuma unificação é proposta — a coincidência parcial de
vocabulário entre alguns desses elementos (ex.: USUARIO/SISTEMA/
PESQUISADOR aparecendo em mais de um) é superficial, não estrutural.

### 4.9 Verbalização e explicação

Verbalizações e explicações são registros contextualizados da atividade. Podem ocorrer antes, durante ou depois de uma ação e devem ser vinculadas à tentativa e, quando aplicável, ao evento correspondente.

Pergunta e resposta são registros distintos, correlacionados, não
fundidos em um só — modelados como um par adjacente (Schegloff & Sacks,
1973, Análise da Conversação): dois registros produzidos por partes
diferentes (o sistema ou pesquisador que pergunta; o participante que
responde), ordenados (a pergunta é sempre a primeira parte, a resposta a
segunda) e tipados (a pergunta torna um tipo específico de resposta
esperado, não qualquer registro). A resposta sempre referencia a pergunta
que a originou — correlação, não identidade. Isso responde as perguntas
5-7 da Seção 17 da Revisão 5: aceitação/rejeição continuam resultado da
mesma ação (Seção 15); pergunta e resposta, quando existirem como
elementos distintos de verbalização, seguem este modelo de par adjacente.

Esta é uma decisão de modelagem-alvo, não uma implementação — nenhuma
classe de pergunta/resposta existe ainda no pacote piloto.

Elas não constituem automaticamente invariantes operatórios.

### 4.10 Evidência e hipótese analítica

Ações, sequências, estratégias, escolhas, erros, acertos e verbalizações podem fornecer evidências para hipóteses revisáveis sobre:

- esquemas;
- teoremas-em-ação;
- conceitos-em-ação.

Uma hipótese deve registrar:

- tipo: `TEOREMA_EM_ACAO`, `CONCEITO_EM_ACAO` ou relação entre ambos;
- classe de situações;
- tentativa;
- registros utilizados como evidência;
- critérios analíticos aplicados;
- nível de sustentação;
- interpretações alternativas;
- estado: candidata, sustentada, revisada, refutada ou inconclusiva;
- autor ou agente responsável pela interpretação.

A ausência de evidência suficiente deve resultar em **nenhuma hipótese**, e não em inferência forçada.

### 4.11 Modelo do Usuário e decisão adaptativa distribuída

Decisão arquitetural da usuária em 2026-08-11:

1. Os objetos ricos da representação possuem e produzem os logs factuais dos
   gestos que os envolvem. Os objetos semânticos ou relações estruturais
   possuem e produzem os logs das ações instrumentais e das ajudas que lhes
   pertencem. Cada ação possui um único `action_id`; quando envolve vários
   objetos, o menor proprietário relacional ou agregado registra a ação uma
   vez e referencia seus participantes. Esses registros fornecem casos para o
   Agente Modelador.
2. O Agente Modelador executa J48/PART e Apriori, mantém a proveniência das
   regras e publica uma nova versão explicável do Modelo do Usuário.
3. No login, o sistema carrega uma fotografia versionada do modelo. Essa
   fotografia permanece estável durante a sessão; uma versão publicada pelo
   Modelador só é usada em um login posterior.
4. Cada proprietário semântico recebe apenas um `ContextoAdaptativoUsuario`
   de leitura, projetado para o seu escopo. Ele combina esse contexto com o
   diagnóstico factual que possui e seleciona uma ajuda do próprio repertório.
5. A decisão resultante deve identificar a regra e a versão do modelo usadas.
   A camada de apresentação concretiza a modalidade escolhida; o objeto rico
   correspondente produz separadamente os registros da decisão e da exibição
   confirmada.

O aprendizado de padrões fica concentrado no Modelador; a aplicação das
regras fica distribuída nos objetos que possuem o conhecimento semântico e o
repertório correspondente. Como consequência, a arquitetura possui somente o
Agente Modelador, porque a ele pertencem J48/PART, Apriori e a publicação das
regras aprendidas. A Zona de Desenvolvimento Proximal continua sendo
fundamento pedagógico, sem se tornar um componente de software.

A autoridade sobre certo/errado e a propriedade do log da ação pertencem ao
objeto semântico ou à relação estrutural que valida a ação. Esses proprietários
produzem registros factuais tipados, incluindo C/E e contexto quando
aplicável. O registro do gesto pertence ao objeto rico da representação, sem
avaliação semântica. A infraestrutura apenas transporta, persiste e consulta
esses registros, preservando seus proprietários, e os disponibiliza ao
Modelador. A seleção da ajuda pertence ao proprietário semântico do repertório
correspondente. O Agente Modelador é o único agente da arquitetura vigente.

Os protocolos `TEXTO` e `QUANTIFICAR` da incógnita, a classificação de
categoria, a escolha de sinal e o posicionamento materializam essa fronteira:
o proprietário semântico produz um único registro factual e o Modelador o
recebe diretamente. A participação de vários objetos semânticos é representada
no mesmo registro, sem multiplicar a ação.

Regras mineradas são artefatos computacionais versionados para adaptação, não
invariantes operatórios nem hipóteses automáticas sobre conceitos-em-ação. Os
campos interpretativos continuam sendo preenchidos exclusivamente pelo
pesquisador humano. Um objeto semântico nunca conclui o que o participante
"sabe"; ele somente aplica uma regra publicada aos fatos e ao contexto
permitido.

#### 4.11.1 Dois níveis temporais de contexto

A arquitetura distingue, sem os transformar em dois decisores centrais:

- **Modelo do Usuário**: contexto histórico/intersessões, versionado e
  congelado no login;
- **Modelo da Situação/Solução**: estado contextual intrasseção da estrutura
  semântica construída pelas ações do participante sobre elementos da
  interface, incluindo os fatos da tentativa e do curso das situações
  interativas pertinentes à decisão corrente.

O Modelo da Situação/Solução não é um mapa global entregue a todos os objetos.
Cada proprietário recebe somente a projeção factual tipada que pertence à sua
decisão. Para a incógnita, essa projeção já aparece como
`FatosSelecaoAjudaIncognita` (verificado em `src/`, 2026-09-03). A referência
equivalente para o posicionamento, `FatosSelecaoAjudaPosicionamento`, é citada
na fonte de origem deste parágrafo mas **não foi encontrada em `src/`** nesta
verificação — tratar como projeto ainda não integrado, não como fato atual,
até confirmar.

O mesmo proprietário semântico combina seus conhecimentos, seu estado e suas
relações com esses fatos correntes e com a projeção histórica do Modelo do
Usuário. A decisão pode mudar durante a sessão porque os fatos correntes
mudaram, sem que a fotografia histórica seja atualizada. Progressão de
complexidade da situação e intensidade do scaffolding são decisões distintas
e devem permanecer em proprietários/repertórios compatíveis com seus escopos.

Fonte conceitual para preservar o curso e o contexto das situações
interativas: AKHRAS, F. N.; SELF, J. A. System Intelligence in Constructivist
Learning. *International Journal of Artificial Intelligence in Education*,
v. 11, n. 4, p. 344--376, 2000. A observação empírica da usuária, oriunda das
sessões de mestrado/doutorado, é que a ausência de progressão de dificuldade
podia produzir tédio. Esse registro fundamenta a investigação da progressão,
mas não autoriza o sistema a diagnosticar automaticamente tédio nem a inventar
limiares de progressão.

> **Status de implementação (P2.3C, 2026-08-13):** o login real cria a
> fotografia por `SessaoAdaptativaUsuario` e mantém a mesma instância até o
> logout. Na ausência de um repositório editorial de versões publicadas, o
> código identifica o conteúdo congelado por `conteudo-sha256:`. A base JSON
> histórica e as regras TSV experimentais não são promovidas por esse
> carregamento. A situação atual já é ligada à `IncognitaQuantitativa` por sua
> designação curada e recebe somente `NIVEL_TAREFAS` e
> `DIAGNOSTICO_TAREFA`. Conflitos de designação permanecem explícitos. Havendo
> regra publicada aplicável, a decisão local é materializada pela representação
> e decisão/apresentação são registradas separadamente. A produção ainda usa
> fonte vazia de regras publicadas; nesse caso, `SEM_REGRA_APLICAVEL` é
> registrado e o comportamento visual legado permanece como fallback explícito.

### 4.12 Situação-problema rica e narrativa independente de mídia

Decisão arquitetural da usuária em 2026-08-29: a futura geração de histórias,
quadrinhos, animações e vídeos deve partir de uma `SituacaoProblema` rica, e
não do texto final nem dos componentes da interface. O agregado coordena dois
modelos que preservam suas autoridades próprias:

- a `EstruturaAditiva`, formada pela categoria, pelos papéis quantitativos,
  pelas relações estruturais e pelo papel que era desconhecido no enunciado;
- a `NarrativaCurada`, formada pelos participantes nomeados pelo pesquisador,
  pelos objetos contados, inventários/estados, marcadores temporais e eventos
  quantitativos ordenados.

A correspondência entre um papel e um fato narrativo é declarada
explicitamente pela curadoria. Ela nunca pode ser inferida pela posição do
campo, do personagem, da figura ou do componente. A situação-problema, por
ser o menor agregado que conhece os dois lados, valida essas correspondências
sem transferir a regra matemática para a narrativa nem a sintaxe narrativa
para os papéis.

Um evento narrativo curado descreve uma mudança quantitativa da história; ele
não é uma ação instrumental do participante na interface. A sequência
narrativa resultante é um roteiro semântico independente de mídia. Adaptadores
de texto, quadrinhos, animação ou vídeo apenas realizam essa sequência em suas
próprias sintaxes e não recalculam valores, redefinem papéis ou corrigem a
curadoria.

Uma conversão ou geração automática permanece
`CANDIDATA_NAO_CURADA`. Somente o pesquisador humano pode promover a situação
para `VALIDADA_PELO_PESQUISADOR`. Uma divergência é diagnosticada e preservada;
o agregado não sobrescreve a declaração humana silenciosamente.

#### 4.12.1 Ponte explícita a partir da curadoria tabular

O registro tabular existente e o agregado rico coexistem durante a migração.
`ConversorSituacaoProblemaRica` é um adaptador da camada de curadoria: o
domínio rico não depende de `SituacaoProblemaAditiva`, do TSV nem de Swing.
Nesta etapa, a conversão executável cobre as categorias canônicas
`COMPOSICAO_MEDIDAS`, `TRANSFORMACAO_MEDIDAS`, `COMPARACAO_MEDIDAS`,
`COMPOSICAO_TRANSFORMACOES`, `TRANSFORMACAO_RELACAO` e
`COMPOSICAO_RELACOES`; nomes históricos
presentes em identificadores de proveniência não constituem novas categorias.

##### Composição de transformações

Para essa categoria, a fonte precisa declarar os seis papéis — estado inicial,
transformação 1, estado intermediário, transformação 2, transformação
resultante e estado final — e as duas operações: entre as transformações e
entre o estado inicial e a transformação resultante. As transformações 1 e 2
são inteiros não nulos com sinal embutido; os estados são naturais. A
transformação resultante é inteira e pode ser zero quando as componentes se
anulam, como em `+2 + (-2) = 0`. As relações locais preservadas no agregado
são:

- transformação 1 [operação curada] transformação 2 = transformação resultante;
- estado inicial + transformação 1 = estado intermediário;
- estado intermediário + transformação 2 = estado final;
- estado inicial [operação curada] transformação resultante = estado final.

##### Transformação de medidas

Para `TRANSFORMACAO_MEDIDAS`, a fonte declara estado inicial e estado final
como números naturais e a transformação como inteiro não nulo, com sinal
curado explicitamente. A relação local é
`EstadoFinal = EstadoInicial + Transformacao`. Como o papel representa uma
mudança efetiva, uma transformação direta igual a zero — e, portanto,
`EstadoFinal = EstadoInicial` — não constitui situação dessa categoria.

##### Composição de medidas

Para `COMPOSICAO_MEDIDAS`, a fonte declara parte 1, parte 2 e todo como números
naturais e a relação local `Todo = Parte1 + Parte2`. Não há operação de
transformação a inventar. Uma narrativa estática pode conter zero eventos e
declarar como final o mesmo inventário inicial, desde que os marcadores
preservem a ordem e a declaração seja validada.

Se a situação curada distinguir variantes de uma mesma família, as partes
podem apontar nominalmente para objetos contados específicos e o todo pode
apontar para o total da família. Essa correspondência é apenas uma possibilidade
explícita de curadoria; não autoriza inferência pelo texto, pela ordem dos
campos ou pela posição visual.

##### Comparação de medidas

Para `COMPARACAO_MEDIDAS`, a fonte declara Referido e Referendo como números
naturais e Valor Relativo como inteiro com sinal curado explicitamente. A
relação local é `Referendo = Referido + ValorRelativo`; por isso, o valor
relativo pode ser positivo, negativo ou nulo.

Na narrativa estática, cada medida aponta nominalmente para o participante e a
família de objetos declarados pelo pesquisador. A correspondência do Valor
Relativo usa uma referência relacional explícita que calcula
`quantidade(participante do Referendo) - quantidade(participante do Referido)`.
A ordem dos personagens no registro, no texto, na tela ou em uma coleção nunca
define essa correspondência. A chave canônica viva do papel é
`papel.diferenca`; seu nome conceitual permanece **Valor Relativo**.

##### Composição de relações

Para `COMPOSICAO_RELACOES`, Relação 1, Relação 2 e Relação Final são números
relativos e podem ser positivos, negativos ou nulos. Cada papel aponta para
uma diferença orientada entre dois participantes nominalmente declarados pela
curadoria. Os campos `personagem_*`, a ordem do texto e a posição visual não
definem essas orientações.

`operacao_relacao` declara a operação estrutural curada entre as duas relações
e também fornece o critério normativo para a escolha de soma ou subtração pelo
participante. Na configuração encadeada, por exemplo, as relações de A com B e
de B com C podem ser somadas para obter a relação de A com C. Na configuração
com referência comum, as relações de A com C e de B com C podem ser subtraídas
para obter a relação de A com B. O conversor não escolhe a configuração pelos
sinais nem pelo enunciado: ele exige a operação e as três correspondências
orientadas declaradas pelo pesquisador.

Como todos os papéis são relativos, relações opostas podem compor uma Relação
Final igual a zero, como em `+4 + (-4) = 0`. Essa nulidade não representa uma
transformação sem efeito.

##### Transformação de relação

Para `TRANSFORMACAO_RELACAO`, Relação Inicial e Relação Final são números
relativos e podem ser zero. A Transformação representa o evento efetivo e é um
inteiro não nulo. Cada relação narrativa declara nominalmente e em ordem os
dois participantes comparados; a transformação declara o participante afetado,
a família de objetos e a chave do evento. Não derive nenhuma dessas identidades
dos campos `personagem_*`, do texto ou da posição visual.

A relação estrutural orientada adota a orientação da Relação Inicial. Se o
evento altera o primeiro participante, sua variação atua com o mesmo sinal; se
altera o segundo, atua com sinal inverso. A Relação Final pode manter ou inverter
a ordem dos dois participantes, e o objeto relacional converte sua orientação
antes de verificar a consistência. Por exemplo, uma relação inicial `+3` de
Julia em relação a Maria, seguida de um acréscimo `+5` para Maria, pode produzir
uma relação final `+2` de Maria em relação a Julia; na orientação inicial, isso
corresponde a `+3 + (-5) = -2`. Também é válido obter relação final `0`, como em
`+2 + (-2) = 0`.

Em `TRANSFORMACAO_RELACAO`, `operacao_relacao` é a resposta curada para o procedimento de soma ou subtração
pedido ao participante. Ela fica em `CriterioOperacaoModelagem` e não é usada
como operador da relação estrutural nem para recalcular `relacao_final`. A
declaração humana divergente é preservada como candidata acompanhada de
diagnóstico; não é corrigida silenciosamente.

O conversor não extrai participantes, objetos, eventos ou correspondências do
enunciado. Esses conhecimentos chegam como `NarrativaCurada` e vínculos
nominais explícitos. A referência do estado intermediário usa a chave do
evento que produziu esse estado; a posição na tela ou a posição de um
personagem nunca substitui essa chave.

##### Persistência da narrativa rica

O registro tabular histórico e a narrativa rica são artefatos complementares.
A tabela continua sendo a fonte dos valores formais, da categoria, das
operações e da incógnita já curados. Um sidecar XML versionado, identificado
pelo `id` da situação, preserva nominalmente participantes, famílias, objetos,
características, marcadores temporais, inventários, eventos e correspondências
entre papéis e fatos narrativos. O sidecar não redefine as seis categorias nem
constitui uma nova fonte teórica.

O adaptador de persistência reconstrói somente declarações explícitas do
pesquisador. Ele rejeita referências nominais inexistentes e formato estrutural
inválido, mas não corrige uma decisão semântica humana divergente. A ausência do
sidecar impede a criação da representação rica e produz diagnóstico factual;
os campos `personagem_*`, a ordem do texto e as posições visuais não são usados
como alternativa inferencial. XML e caminhos de arquivo permanecem fora dos
objetos de domínio.

Se faltar um campo obrigatório da respectiva categoria, narrativa ou
correspondência, ou se as duas declarações da incógnita divergirem, a conversão
para antes de criar o agregado e devolve diagnóstico factual. Se o agregado puder ser
construído mas suas relações ou correspondências forem inconsistentes, ele
permanece candidato acompanhado dos diagnósticos. Mesmo quando o registro
tabular está marcado como validado, a nova representação continua
`CANDIDATA_NAO_CURADA` até revisão humana específica.

##### Edição humana da narrativa rica

A tela de curadoria da versão original pode coletar essas declarações em um
formulário próprio. A identidade de cada participante, família, objeto, evento
e correspondência é nominal; a ordem das linhas, a coluna `personagem_*`, o
texto do enunciado e a geometria não substituem identificadores explícitos. A
tela materializa a entrada e apresenta diagnósticos, enquanto um montador sem
dependência de Swing constrói o agregado.

Se a declaração puder ser estruturada, mas divergir das relações formais, o
pesquisador pode preservá-la deliberadamente como candidata depois de ler os
diagnósticos. Não há correção automática. Versões traduzidas reutilizam a
narrativa semântica pertencente à original, identificada por
`versao_origem_id`, e variam somente a realização textual.

A promoção para `VALIDADA_PELO_PESQUISADOR` é outro ato humano explícito,
persistido no sidecar da versão original. Ela só é aceita quando a conversão
da estrutura e da narrativa não produz diagnósticos. A validação histórica do
registro tabular não substitui esse ato. Um sidecar de formato anterior sem
status editorial é interpretado conservadoramente como
`CANDIDATA_NAO_CURADA`.

## 5. Princípios arquiteturais obrigatórios

1. O modelo de domínio é a fonte única da verdade semântica do sistema.
2. Representações gráficas e textuais são projeções coordenadas do mesmo estado semântico.
3. Elementos puramente visuais ou interativos não pertencem ao domínio.
4. Regras locais pertencem aos objetos responsáveis por elas.
5. Relações que envolvem vários objetos pertencem a coordenadores de escopo fechado.
6. Aprendizado, publicação e política pedagógica transversal pertencem ao
   Modelador ou a serviços especializados; a seleção entre ajudas de um
   repertório local pertence ao proprietário semântico desse repertório.
7. Eventos registram fatos; hipóteses analíticas registram interpretações.
8. Valores calculados pelo sistema nunca devem ser registrados como ações do usuário.
9. A arquitetura deve preservar a possibilidade de resultado inconclusivo na análise do conhecimento-em-ação.
10. O sistema não deve reificar esquemas ou invariantes operatórios como propriedades fixas de elementos da interface.

## 6. Exemplo de aplicação

Em uma representação de Composição de Medidas:

- `Parte1`, `Parte2` e `Todo` são papéis semânticos;
- `Todo = Parte1 + Parte2` é uma relação estrutural formal;
- o arraste de um valor para `Todo` é uma ação do usuário;
- a validação do valor é uma capacidade do sistema;
- o evento registra o que ocorreu e em qual contexto;
- uma explicação do usuário é uma verbalização vinculada à tentativa;
- somente uma análise explícita de múltiplos registros pode sustentar uma hipótese de teorema-em-ação ou conceito-em-ação.

## 7. Vocabulário proibido ou condicionado

Evitar:

- “cada objeto representa um conceito”;
- “cada elemento do diagrama é um conceito”;
- “a relação estrutural é um invariante operatório”;
- “a ação revela diretamente um invariante”;
- “a manipulação produz necessariamente invariantes”;
- “o triângulo é a estrutura essencial do conceito”.

Preferir:

- “objeto semanticamente definido do domínio”;
- “papel semântico em uma representação de situação”;
- “relação estrutural formal”;
- “registro de atividade que pode sustentar uma hipótese”;
- “triplo dinâmico e relacional `C = (S, I, R)`”.
