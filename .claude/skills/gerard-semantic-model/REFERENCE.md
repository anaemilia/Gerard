# Modelo Semântico de Referência do GERARD

**Versão:** 2.0  
**Data:** 2026-08-01  
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

> **Status de implementação (2026-08-04)**: `RelacaoEstruturalComposicao`
> e `RelacaoEstruturalTransformacao` estão implementadas no pacote
> piloto, com harness próprio (`TestePilotoPapelQuantitativo` e
> `TestePilotoTransformacaoMedidas`, respectivamente).
> `RelacaoEstruturalComparacao`, seguindo esse mesmo padrão isolado por
> categoria, ainda não foi implementada no pacote piloto. Isso não
> significa ausência de lógica: a responsabilidade de resolver a
> terceira quantidade a partir de duas conhecidas, para as três
> categorias (incluindo Comparação), já é coberta hoje por
> `gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado`, numa
> arquitetura mais antiga e genérica, fora do pacote piloto e fora do
> modelo Domain Model First. Migrar essa responsabilidade para uma
> `RelacaoEstruturalComparacao` isolada — e decidir a relação entre as
> duas implementações — é o escopo da tarefa registrada em
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

Cardinalidade ação:evento: adotada a Alternativa B (1:N). Uma ação começa
na primeira tentativa de posicionamento de um item; se aceita, a ação
termina ali, um único evento. Se rejeitada, cada nova tentativa do mesmo
item é um evento correlacionado à mesma ação (mesmo `action_id`), até um
limite de `N=3` tentativas rejeitadas — fixo, para não repetir a mesma
mensagem de ajuda mais de três vezes ao participante. Na terceira
rejeição, o sistema exibe uma tela de ajuda; essa exibição fecha a ação.
Acionar o botão "restaurar" depois disso inicia uma ação nova, separada.

Isso introduz um novo campo, `action_id`, que correlaciona os eventos de
uma mesma ação — distinto de `event_id` (o identificador de cada evento
individual; hoje é `id_acao`, cuja renomeação para `event_id` é
recomendada mas ainda não aplicada nesta decisão).

Cada objeto semântico deve carregar seu próprio repertório de Scaffolding
(estilos de interação possíveis — manipulação, som, vibração, atração
magnética, etc. — mensagens e tipos de ajuda concreta) como conhecimento
local, consistente com o princípio da localidade do conhecimento: manter
esse repertório espalhado pela interface causaria inconsistência. A
seleção de qual elemento do repertório usar em cada situação é uma decisão
separada e ainda em aberto — não resolvida por este registro.

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

Esta é uma decisão de arquitetura-alvo, não uma implementação —
`EventoPapelQuantitativo` continua como está até uma decisão explícita de
implementar esta reestruturação.

Versionamento de esquema: versão embutida no campo "tipo" do envelope
(sufixo `.vN`, ex.: `papel_quantitativo.posicionado.v1`), seguindo a
convenção "type-based versioning" recomendada pela especificação
CloudEvents já referenciada na decisão de arquitetura de evento —
consumidores podem rotear versões diferentes do mesmo tipo de evento para
tratamentos diferentes. Não há campo de versão separado.

Esta é uma decisão de formato-alvo, não uma implementação —
`EventoPapelQuantitativo` continua como está até uma decisão explícita de
implementar a reestruturação envelope + payload (decisão anterior).

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

Esta é uma decisão de especificação-alvo, não uma implementação — nenhum
evento `FEEDBACK_EXIBIDO` existe ainda no código.

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

## 5. Princípios arquiteturais obrigatórios

1. O modelo de domínio é a fonte única da verdade semântica do sistema.
2. Representações gráficas e textuais são projeções coordenadas do mesmo estado semântico.
3. Elementos puramente visuais ou interativos não pertencem ao domínio.
4. Regras locais pertencem aos objetos responsáveis por elas.
5. Relações que envolvem vários objetos pertencem a coordenadores de escopo fechado.
6. Políticas pedagógicas gerais pertencem a skills ou serviços especializados.
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
