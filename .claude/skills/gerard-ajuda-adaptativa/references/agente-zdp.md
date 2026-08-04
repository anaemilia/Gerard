# Agente ZDP

⚠️ Proposta teórica — ver aviso de status em `../SKILL.md`.

**Fontes**: material original (RFAs e Figuras 5.77–5.85) + relatório de pesquisa
"Análise de situações interativas no Gerard: subsídios para o design de uma
interface inteligente em problemas de estruturas aditivas" (Queiroz, 2026,
Univasf — fornecido pelo usuário em 2026-07-22). O relatório de 2026 é
material empírico (protocolo em papel, 5 usuários, 556 ações) que preenche
boa parte da lacuna de operacionalização que o material original já
reconhecia como incompleta (ver "Nota de honestidade" abaixo) — mas continua
sendo proposta de design, não algo testado num sistema computacional real.

## Papel

Usa o conceito de Zona de Desenvolvimento Proximal (Vygotsky) para decidir a
estratégia pedagógica mais efetiva, visando transformar habilidades
potenciais em habilidades reais.

## Arquitetura: Agente Baseado em Modelo

O ambiente é dinâmico — muda enquanto o ZDP delibera — e estratégico — o
próprio usuário ou o ZDP podem alterá-lo. Por isso, diferente do Monitor, o
ZDP precisa manter **estado interno**: armazena a percepção recebida como
estado atual do mundo, guarda estados anteriores, e mantém informação sobre
"como o mundo evolui" e "o que minhas ações fazem", independente de suas
próprias ações.

Antes de oferecer ajuda, verifica se a configuração do ambiente ainda é a
mesma que tem armazenada.

## Percepções

- **Ação Instrumental Avaliada** (recebida do Agente Monitor).
- **Estado Atual** do ambiente.

## ⚠️ Divergência a reportar, não resolver silenciosamente

O relatório de 2026 (Tabela 44) decompõe a decisão de ajuda em **cinco**
componentes agentivos — agente de modelo do usuário, agente de análise de
padrões, agente pedagógico, agente de scaffolding e agente de antecipação da
ajuda — enquanto a arquitetura de três agentes desta skill trata "decidir a
estratégia pedagógica" como responsabilidade de um único Agente ZDP. Os dois
não são incompatíveis: os cinco componentes do relatório podem ser lidos como
**subcomponentes internos** do Agente ZDP (mantendo a sociedade de três
agentes no nível externo), mas essa leitura é uma interpretação minha, não
algo que o relatório declare explicitamente. Confirmar com o usuário antes de
tratar isso como decidido — por ora, as seções abaixo descrevem os cinco
papéis como funções que o Agente ZDP cumpre, não como agentes/threads
separados.

## Ações

1. Verifica se o estado atual bate com o estado armazenado (ambiente
   estratégico).
2. Consulta as regras presentes no **Modelo do Usuário**.
3. Consulta os **conteúdos pedagógicos disponíveis** — a folha de
   granularidade aqui é o tipo de scaffold a oferecer: Mensagens,
   Automatização de passos, Mostrar Modelo Completo, entre outros. Ver
   `gerard-scaffolding-interacao` para a taxonomia completa desses tipos —
   este arquivo não repete a lista, só decide *quando* e *qual* usar.
4. Constrói a Estratégia Pedagógica, em função de: tarefa em execução +
   informações inferidas do modelo do usuário + conteúdo pedagógico.
5. Disponibiliza a estratégia à interface, para o usuário e para o Agente
   Modelador.

Objetivo: conduzir o usuário à mobilização de proposições verdadeiras, tanto
sobre o uso da interface quanto sobre os conceitos veiculados nela.

## Camadas progressivas de ajuda (relatório 2026, Tabela 49)

O relatório propõe uma escada de oito níveis para a ação "constrói a
Estratégia Pedagógica" (passo 4 acima) — cada nível aciona um tipo diferente
de scaffold e é acionado por uma condição diferente na ação avaliada pelo
Monitor:

| Nível | Camada | Quando acionar | Ação esperada |
|---|---|---|---|
| N0 | Condução da tarefa | Início da resolução, passagem de etapa, orientar o fluxo básico | Apresenta a tarefa, solicita a categoria, orienta preenchimento, valida continuidade |
| N1 | Validação operacional | Erro local imediatamente verificável (escolha incompatível, campo inadequado, resposta antecipada, ausência de sinal) | Sinaliza o erro, solicita revisão, sem explicação conceitual extensa |
| N2 | Feedback específico | Erro que se repete ou sugere dificuldade na relação enunciado↔categoria↔diagrama | Explicita a relação envolvida, indica a função do elemento manipulado |
| N3 | Ajuda sobre o modelo | Usuário identifica os números no enunciado mas não a função dos espaços do diagrama | Nomeia os elementos: parte, todo, referente, referido, estado inicial, estado final, número relativo |
| N4 | Ajuda representacional | Erro recorrente ou dificuldade de visualizar a relação só pelo texto+diagrama formal | Oferece representação concreta (quadradinhos, conjuntos, fábrica) |
| N5 | Ponte entre representações | Usuário acerta na representação concreta mas erra ao voltar ao diagrama formal | Relaciona explicitamente enunciado verbal, representação concreta e modelo formal |
| N6 | Regulação da compreensão | Acerto sem justificativa, acerto após muitas tentativas, correção por eliminação, verbalização de dúvida, dependência de ajuda | Solicita explicação, interpreta a justificativa, decide se mantém/reduz/aprofunda |
| N7 | Antecipação da ajuda | Sinais iniciais de bloqueio semelhantes a dificuldades já observadas em problemas anteriores | Sugere intervenção preventiva antes que o erro se repita ou vire ciclo de tentativa e erro |

N0–N2 dependem só da ação atual (papel próximo do que hoje é o Agente
Monitor + scaffolding reativo já implementado em `ScaffoldingQuestionamento`
— ver `gerard-scaffolding-interacao`). **N3 em diante é onde a ZDP de fato
precisa de estado/histórico** (modelo do usuário, recorrência, padrão por
problema) — é a parte que ainda não existe em código nenhum.

## Regras preliminares de decisão (relatório 2026, Tabela 50)

Regras condição→interpretação→ação, derivadas empiricamente (5 usuários, 556
ações) — tratar como ponto de partida a validar, não como tabela de verdade
definitiva:

| Condição observada | Interpretação provável | Ação recomendada |
|---|---|---|
| Primeiro erro numa etapa | Engano pontual ou hipótese inicial inadequada | Questionamento leve (N1); permitir nova tentativa |
| Mesmo erro repetido após feedback | A mensagem anterior não reorganizou a ação | Substituir feedback genérico por ajuda específica sobre o elemento (N2/N3) |
| Erro de sinal com valor corretamente posicionado | Dificuldade na direção da transformação/comparação | Feedback sobre ganho/perda/acréscimo/decréscimo |
| Erro de sinal com valor em posição incorreta | O problema real é posicionamento, não sinal | Priorizar feedback de posição antes do de sinal — não tratar como erro de sinal |
| Pedido explícito de dica | Usuário reconhece dificuldade | Ajuda contextualizada (N3+); registrar dependência de suporte no modelo |
| Dúvida sobre procedimento da interface | Dificuldade operacional, não conceitual | Orientação de uso; **não** classificar como erro matemático |
| Leitura/releitura do enunciado | Organização metacognitiva da tarefa | Ação neutra; aguardar próxima ação avaliável, não intervir |
| Acerto após ajuda | Possível reorganização local | Reduzir gradualmente o apoio (retirada progressiva); observar se o acerto se mantém |
| Acertos sucessivos sem ajuda | Indício de autonomia | Retirar retornos visuais adicionais; manter só condução mínima (N0) |

A distinção "erro de sinal vs. erro de posição" é a mais citada no relatório
como fonte de ajuda mal focalizada no protótipo em papel: oferecer feedback
de sinal quando o problema real é posição faz o usuário "corrigir" o sinal e
persistir no erro (ver protocolo do usuário 01, Tabela 21/22 do relatório) —
por isso a regra acima prioriza checar posição antes de sinal.

## Catálogo de mensagens de ajuda por tipo de erro (fonte: TCC 2011, Ana Emília + Alisson)

**Fonte diferente das demais seções deste arquivo**: não vem do relatório
2026 nem do material original da tese — é uma tabela de um documento de
2011 (`ajudas-modificadoAnaEmilai.doc`, autor original "Alisson", última
edição por "Ana Emilia de Melo Queiroz" em 2011-07-12), do período de TCC,
15 anos anterior à arquitetura de três agentes documentada no resto desta
skill. Trazida para cá em 2026-07-26 a pedido do usuário. Tratar como
material histórico de referência para o catálogo de **Conteúdo Pedagógico**
(o repositório citado em `../SKILL.md` como consultado pelo Agente ZDP na
ação 3, "Consulta os conteúdos pedagógicos disponíveis") — não como
especificação já validada nem como texto pronto para produção (ver cautela
abaixo).

A tabela cruza tipo de erro/etapa × estratégia pedagógica × duas variantes de
mensagem — uma "padrão" (igual para todos) e uma "personalizada" que seguiria
a estratégia escolhida:

| Etapa da tarefa | Estratégia utilizada | Ajuda Padrão | Ajuda Personalizada [Segundo a estratégia] |
|---|---|---|---|
| Erro de sinal na transformação | Feedback instrucional | Tem certeza que o sinal está correto? | Tem certeza que o sinal está correto? O sinal não reflete a transformação entre a quantidade inicial e a quantidade final. |
| Erro de sinal na comparação | Feedback instrucional | Tem certeza que o sinal está correto? | Tem certeza que o sinal está correto? O sinal não reflete a relação entre o referido e o referente. |
| Erro de posicionamento | Automatização de passos com dica completa | Você tem certeza que o elemento foi posicionado corretamente? | Aqui tem um detalhe. Dê uma olhada na minha dissertação |
| Erros consecutivos de posicionamento | Automatização de passos com dica completa | Você tem certeza que o elemento foi posicionado corretamente? | Aqui tem um detalhe. Dê uma olhada na minha dissertação, na parte que fala sobre ajuda usando rótulos. |
| Erros de categorização | Feedback de questionamento | Na situação-problema existem partes que formam um todo? [Exemplo, composição] | Já estão segundo a estratégia. |
| Erro consecutivo de categorização | Feedback de questionamento | Na situação-problema existem partes que formam um todo? [Exemplo, composição] | Já estão segundo a estratégia. |
| Erro de cálculo numérico | Feedback de questionamento | Valor incorreto! | Você tem certeza que o valor está correto? |

**Como as 3 estratégias citadas se relacionam com o que já existe/está
documentado**:

- **"Feedback de questionamento"** — mesmo nome do tipo "Questionamento" já
  documentado em `gerard-scaffolding-interacao` (seção 2) e implementado em
  `ScaffoldingQuestionamento`/`AgenteMonitor`. As mensagens de exemplo aqui
  ("Na situação-problema existem partes que formam um todo?", "Valor
  incorreto!") são compatíveis com esse mecanismo já existente.
- **"Automatização de passos com dica completa"** — mesmo nome do tipo 4 de
  `gerard-scaffolding-interacao`, explicitamente marcado lá como **ainda não
  implementado**. A qualificação "com dica completa" sugere que pode haver
  variantes (ex.: dica parcial) não documentadas em lugar nenhum — não
  presumir que existam sem confirmar com o usuário.
- **"Feedback instrucional"** — não bate exatamente com nenhum dos 4 tipos
  de `gerard-scaffolding-interacao`; as mensagens de exemplo são perguntas
  ("Tem certeza que o sinal está correto?"), então na prática se parece mais
  com "Questionamento" do que com "Mensagem informativa" apesar do nome.
  Divergência a reportar ao usuário, não resolver silenciosamente.

**Cautela sobre o conteúdo em si**: as mensagens de "Ajuda Personalizada"
para erro de posicionamento ("Dê uma olhada na minha dissertação...") são
claramente texto de rascunho/placeholder em primeira pessoa referindo-se à
dissertação da própria autora do documento — não é cópia pronta para
produção. Nas linhas de categorização, "Ajuda Personalizada" repete a mesma
frase curta ("Já estão segundo a estratégia.") nas duas linhas, o que também
tem cheiro de placeholder ainda não desenvolvido, não de conteúdo final.

**Como isto se relaciona com a distinção erro-de-sinal-vs-erro-de-posição da
Tabela 50 (seção acima)**: esta tabela de 2011 já tratava sinal e
posicionamento como categorias de erro separadas, cada uma com sua própria
estratégia — o relatório 2026 (15 anos depois) chega à mesma distinção
independentemente e a eleva a regra explícita de priorização (checar posição
antes de sinal). Não é o mesmo material, mas os dois convergem no mesmo
diagnóstico.

### Complemento: mensagens de categorização por subtipo (fonte: imagem colada pela usuária, 2026-07-26; conteúdo confirmado pela usuária como sendo de 2011, mesmo período da tabela acima)

Tabela colada diretamente pela usuária (sem arquivo/metadados de origem no
momento do envio, mas confirmada pela usuária em 2026-07-26 como sendo do
mesmo período de 2011 da tabela anterior — não se sabe qual das duas é
cronologicamente mais recente entre si, só que ambas são da mesma época).
Formato mais simples — só "Tipo de erro" × "Mensagem exibida para o
usuário", sem as colunas de estratégia/padrão vs. personalizada da tabela
anterior:

| Tipo de erro | Subtipo | Mensagem exibida para o usuário |
|---|---|---|
| Erro categorização | Composição | Na situação-problema existem partes que formam um todo? |
| Erro categorização | Transformação | Na situação-problema existe um conjunto inicial que se transforma num conjunto final? |
| Erro categorização | Comparação | Na situação-problema existe um conjunto que está sendo comparado em relação a outro conjunto? |
| Erro categorização | Multiplicação | Na situação-problema o valor total é desconhecido? |
| Erro categorização | Divisão por partes | Na situação-problema o valor de cada parte é desconhecida? |
| Erro categorização | Divisão por cotas | Na situação-problema o número de partes é desconhecida? |
| Posicionamento | — | Você tem certeza que o elemento foi posicionado corretamente? Clique "Ok" para tentar novamente! |
| Sinal | — | Tem certeza que o sinal está correto? Clique "Ok" para tentar novamente! |
| calculo | — | Valor incorreto! Leia a questão novamente e... *(texto cortado na imagem enviada — não presumir o final)* |

**Esta tabela completa o placeholder da tabela anterior**: a linha "Erros de
categorização" da tabela de 2011 tinha só uma mensagem genérica com a nota
"[Exemplo, composição]" — um placeholder indicando que deveria haver uma
mensagem por categoria. Esta tabela nova é exatamente esse detalhamento: uma
mensagem de questionamento específica para cada subtipo de categorização,
com o mesmo padrão de pergunta ("Na situação-problema existe/existem...?").
As mensagens de Composição/Transformação/Comparação aqui substituem/
detalham diretamente a mensagem genérica da tabela anterior.

⚠️ **Divergência a reportar, não resolver silenciosamente**: as 3 últimas
subcategorias de "Erro categorização" — **Multiplicação, Divisão por
partes, Divisão por cotas** — são estruturas do **campo multiplicativo** de
Vergnaud, não do campo aditivo. O Gérard atual (`TipoSituacaoAditiva`, ver
`gerard-modelo-usuario`/código) só implementa categorias aditivas
(Composição/Transformação/Comparação de medidas e suas variantes
compostas) — não existe nenhuma categoria multiplicativa em código. Esta
tabela pode ser: (a) um objetivo de escopo mais amplo para o Gérard no
futuro (campo multiplicativo além do aditivo), (b) material de um contexto
diferente (outro sistema/protótipo) reaproveitado aqui por engano, ou (c)
uma ambição antiga não levada adiante. Não presumir nenhuma dessas
hipóteses — perguntar à usuária antes de tratar isso como escopo real do
Gérard.

As mensagens de "Posicionamento"/"Sinal" aqui são quase idênticas às
"Ajuda Padrão" da tabela anterior ("Você tem certeza que o elemento foi
posicionado corretamente?"/"Tem certeza que o sinal está correto?"), com um
acréscimo de UX ("Clique 'Ok' para tentar novamente!") que a outra tabela
não tinha — como as duas são do mesmo período (2011, confirmado pela
usuária), sem saber qual é cronologicamente mais recente entre si, não dá
para afirmar qual das duas versões da mensagem é a mais tardia/refinada.

## Diários de bordo do doutorado (D:\doutorado\Experimentos, 2010) — evidência de campo pré-arquitetura (2026-07-30)

⚠️ Fonte diferente de todas as anteriores: não é relatório de pesquisa nem
tabela — são os **diários de bordo da própria usuária** (`diárioBordoAnaEmilia*.doc`),
escritos em campo durante a coleta de dados do doutorado (junho–julho de
2010), 5 anos antes da tese e 16 anos antes do relatório de 2026. 12 diários
únicos identificados (18 arquivos no total, 6 são cópias byte-a-byte em
`transcrições/`, confirmadas por hash MD5) — extraídos com `antiword` e lidos
na íntegra. Um deles (`sem entrevistas/18-05-10-/diárioBordoAnaEmilia.doc`) é
idêntico ao diário de 01-06-10 — provável arquivo copiado para a pasta errada
ou pasta sem diário próprio; não investigado further.

Diferente do relatório 2026 (protocolo em papel formal, 5 usuários, 556 ações
codificadas) e das tabelas de 2011 (mensagens já formalizadas), os diários são
**observação de campo não estruturada** — a autora registrando o que via,
sessão a sessão, com professoras da rede pública (majoritariamente escola
"Maria José", Juazeiro-BA) e uma da rede particular (escola Cristal) testando
o protótipo em papel. Valor específico desta fonte: é o registro mais antigo
disponível, então funciona como confirmação (ou não) de que padrões vistos
depois (2011, 2026) já apareciam desde o início da coleta.

**Datas dos diários coincidem com sessões já usadas em `CatalogoProtocolosReaisReplay.java`**
(ex.: 08-06, 13-07, 14-07, 21-07 ↔ Jamilly; 01-06, 15-06, 19-07 ↔
FelipeWanderley) — **são as mesmas sessões**, não duas populações diferentes.
Os diários registram o nome real da professora (anotação de campo privada da
pesquisadora); as transcrições correspondentes anonimizam para sujeito
codificado (`S8`, `S9`...) para proteger a identificação dos participantes —
confirmado pela usuária em 2026-07-30, depois de eu checar (erradamente) que
os nomes das professoras não apareciam nas transcrições e ter suspeitado, sem
razão, de que fossem duas populações separadas. Ou seja: isto **é**
triangulação da mesma evidência por duas fontes (relato do pesquisador de
fora + fala do participante durante a tarefa), não replicação em amostra
diferente. Não existe (ainda) um mapeamento explícito diário-data ↔
código-de-sujeito neste repositório de skills — se precisar cruzar um diário
específico com o episódio replay correspondente, checar a data e, quando
necessário, confirmar com a usuária qual sujeito codificado corresponde a
qual professora nomeada.

**Achados que confirmam, de forma independente, coisas já documentadas nesta
skill**:

- **Confusão sinal↔operação** (quase toda sessão relatada — 01-06, 09-06,
  13-07, 14-07, 15-06, 20-07, 21-07, às vezes com a mesma frase repetida:
  "as professoras não conseguem perceber as diferenças entre o sinal do
  número relativo e a operação que será realizada"). Confirma o mesmo
  diagnóstico da Tabela 50 acima ("erro de sinal vs. erro de posição") 16
  anos antes do relatório 2026 chegar à mesma distinção de forma
  independente.
- **Referente/referido (comparação) e estado inicial (transformação) como as
  categorias mais difíceis** (14-07, 20-07, 21-07) — inclusive um relato rico
  em 21-07: diante de "Maria tem 15 a mais", a professora interpretava 15
  como elemento do conjunto dos naturais ("Ele não tem? Se ele tem"),
  associando o verbo "ter" a uma quantidade absoluta em vez de uma diferença.
  Confirma, com relato de campo detalhado, a mesma ambiguidade
  referente/referido que motivou a regra de exclusão desses passos em
  `CatalogoProtocolosReaisReplay.java` (ver skill de sessão de replay) — a
  ambiguidade não é um artefato dos protocolos posteriores, já existia desde
  o início da coleta.
- **Feedback visual (cores) não percebido** (13-07, 14-07, 20-07): cores
  usadas no protótipo para indicar posicionamento correto passaram
  despercebidas — uma professora disse explicitamente na entrevista "não ter
  percebido a presença das cores". É uma falha de comunicabilidade (De
  Souza) no sentido exato da seção abaixo, só que capturada 16 anos antes da
  conversa que introduziu o conceito nesta skill — dado empírico retroativo
  para o mesmo princípio.
- **"Mais dica" pedida como confirmação de sucesso, não como explicação**
  (01-06, 15-06): usuárias queriam que a interface dissesse "você acertou"
  ou desse a operação certa diretamente, não uma explicação conceitual. Em
  15-06 a autora já anota, em 2010: "a dica de dar explicações não foi
  efetiva... o questionamento foi o mais eficaz" — o mesmo princípio por
  trás da distinção "Feedback de questionamento" vs. "Automatização de
  passos com dica completa" da tabela de 2011 (seção acima), e por trás das
  camadas N1/N2 vs. N4 do relatório 2026.

**Achados novos, não documentados em nenhuma outra fonte desta skill**:

- **Viés de ordem do texto**: dois diários (09-06, 15-06) trazem parágrafo
  quase idêntico descrevendo usuárias que tentam "armar" o diagrama seguindo
  a ordem em que os elementos aparecem no texto do problema, não a estrutura
  semântica da situação — sugere um padrão de erro sistemático que nenhuma
  das tabelas posteriores nomeia explicitamente.
- **Protocolo "quantificar" (contagem um a um) rejeitado pelos usuários**,
  vs. "texto"/arrastar preferido (08-06, 09-06, 15-06: "visto como um
  processo lento"). Relevante para `gerard-log-acao-instrumental`, que
  documenta os protocolos de mouse (`SELECIONAR`/`POSICIONAR`/`TEXTO`/
  `QUANTIFICAR` etc.) do ponto de vista do esquema de captura, não da
  preferência do usuário — este é um dado de preferência empírica que
  aquela skill não tem.
- **Evolução do protótipo é rastreável dia a dia**: radio-button para forma
  de representação introduzido em 08-06; gráfico de barras no Excel como
  ponte pedagógica introduzido no encontro de extensão de 12-06 e já
  reaproveitado como "dica" em 14-07; primeira professora a aceitar testar o
  campo multiplicativo aparece em 20-07 (sessão marcada para 22-07) — é o
  primeiro sinal, em qualquer fonte revisada até agora, do início dos testes
  multiplicativos cujos dados brutos ficam preservados (sem uso pelos
  agentes, por falta de categoria em código) em `CampoMultiplicativoDadosFuturos.java`.
- **Variação individual extrema entre professoras**: a de 14-07 é descrita
  com compreensão aparentemente boa mas discurso "prolixo sem objetividade";
  a de 19-07 mostra o oposto — desatenção extrema, ansiedade, e chega a
  sabotar o próprio teste reinserindo dicas que deveriam ter sido retiradas
  ("colocar sozinha os indicativos no texto"). Nenhuma tabela posterior
  captura essa variabilidade de perfil de usuário — só o relato de campo.
- **Interferência constante do contexto escolar**: câmera desligada sem
  querer (02-06, perdendo 8 minutos de gravação), crianças entrando na sala
  repetidamente (09-06), sala sem mesa e suja (14-07), entrevistas
  remarcadas por falta de tempo (19-07, 20-07, 21-07). Não afeta as
  tabelas/regras diretamente, mas é contexto relevante para avaliar a
  confiabilidade das transcrições correspondentes, caso usadas em
  `CatalogoProtocolosReaisReplay.java` no futuro.

**Não usar como**: dado pronto para citação acadêmica formal sem revisão (é
diário de campo, não protocolo codificado) nem como substituto das tabelas
já formalizadas acima — o valor aqui é de triangulação (confirma padrões já
tabelados) e de contexto (mostra a origem em campo de padrões que só
apareceriam formalizados anos depois), não de dado quantitativo novo.

## Comunicabilidade (De Souza, 1999) como princípio de posicionamento das mensagens (2026-07-27)

⚠️ Fonte: conceito trazido pela usuária em conversa (não em documento
anexado) — o **Método de Avaliação de Comunicabilidade (MAC)**, de Clarisse
Sieckenius de Souza, dentro da **Engenharia Semiótica** (De Souza, 1999;
formalizado depois em *The Semiotic Engineering of Human-Computer
Interaction*, 2005). Trata o sistema como uma mensagem de metacomunicação do
designer para o usuário ("designer's deputy") e desloca o foco da
usabilidade pura para os **momentos de ruptura de comunicação** — quando o
usuário não entende essa mensagem e precisa "falar de volta" com a
interface. O método usa um vocabulário canônico de expressões-tipo ("Cadê?",
"E agora?", "Por que não?", "Ué, o que aconteceu?") para rotular esses
momentos observados em teste.

### Resultado de mestrado citado pela usuária (fonte a confirmar)

A usuária compartilhou uma tabela de "resultado do mestrado" (autoria/ano
exatos não confirmados nesta conversa — confirmar citação formal antes de
usar em texto acadêmico) cruzando Comunicação (proposta de artefato/mensagem)
× Discussão (achado do teste com usuários):

| Comunicação | Discussão |
|---|---|
| Mensagens de sucesso a cada etapa de resolução do problema | Impacto não testado. Usuários que sugeriram esse tipo de comunicação disseram que ficaram perdidos, sem saber se estavam fazendo a tarefa corretamente. |
| Botão com o rótulo "Qual o próximo passo?" | 3 de 5 usuários fizeram comentários espontâneos equivalentes durante a modelagem: "E agora?", "É para fazer o quê?", "Qual o próximo passo?" |
| Presença de ajudas rápidas em cada elemento do menu | Maioria externou insatisfação com a legenda não conter explicações; erraram por tentativa e erro, sem saber o significado da legenda. |
| Metáforas devem conter um botão com o rótulo "?" | Mesmo com as metáforas contendo explicações, os usuários externaram dúvida e pediram mais explicações. |
| "Tem mais alguma dica?" | Alguns usuários perguntaram se tinha mais dica; outros ficaram olhando a interface por um tempo, parecendo em dúvida sobre o que fazer. |

**Como isto se conecta ao já documentado nesta skill e no código**:

- **"Qual o próximo passo?"** — o vocabulário observado (E agora?/É para
  fazer o quê?/Qual o próximo passo?) é evidência empírica direta para o
  botão já adicionado como placeholder desabilitado em `Main.java`
  (`botaoAtalhoProximoPasso`, chave i18n `ui.hint.nextStep`) — reforça que
  desabilitá-lo até a automatização de passos ser desenhada de verdade (ver
  `gerard-scaffolding-interacao`, tipo 4) foi a decisão certa, não apenas
  cautela teórica.
- **"Tem mais alguma dica?"** — é o botão "Mais Dica" da imagem de
  referência original que motivou o painel de atalhos, deixado de fora por
  decisão da usuária em 2026-07-25/26. O achado mostra por que essa hesitação
  fazia sentido: o comportamento observado é ambíguo (parte pede mais dica,
  parte só fica olhando confusa) — não está claro que um botão "mais dica"
  resolveria a causa raiz da dúvida.
- **"Ajudas rápidas em cada elemento do menu" / "metáforas com botão '?'"**
  — é exatamente o mecanismo já implementado dos 3 botões "?" contextuais
  (`botaoAjudaTexto`/`botaoAjudaVergnaud`/`botaoAjudaComplementar`,
  `ScaffoldingAjudaContextual`, ver `gerard-scaffolding-interacao`). O
  achado é um **dado negativo relevante**: mesmo com o mecanismo presente e
  situado, os usuários continuaram com dúvida — a correlação espacial
  sozinha não bastou nesse teste. Não documentado antes nesta skill.

### Princípio de design articulado pela usuária: correlato situado, não central de ajuda

A partir da tabela acima, a usuária formulou o princípio orientador: os
rótulos/artefatos de comunicabilidade devem aparecer nos **locais
específicos da tela** correlatos ao pensamento que motivaria aquela
expressão no usuário — não centralizados num único menu de ajuda genérico.
Quando a dúvida nasce na cabeça do usuário, ele deve encontrar o artefato
que a traduz exatamente onde a dúvida nasceu, não precisar ir buscá-la em
outro lugar.

Isso já é, em parte, o princípio por trás dos 3 botões "?" contextuais
existentes (cada um posicionado junto à área que ele explica — texto,
diagrama de Vergnaud, representação complementar) — mas o achado da tabela
acima mostra que a correlação espacial sozinha não foi suficiente para essa
amostra de usuários. Levanta uma questão em aberto, não resolvida nesta
conversa: **onde** exatamente, na tela atual, ficaria o correlato de
"Qual o próximo passo?" — hoje esse botão está na faixa de atalhos de
categoria, no topo da tela (posição genérica), não perto de onde o usuário
provavelmente estaria quando esse pensamento surge (mais perto da área de
interação com o diagrama). Reposicioná-lo fica pendente de decisão futura da
usuária — o botão continua desabilitado, então não há urgência funcional.

### Como evitar botões com texto ao aplicar este princípio

A usuária levantou o problema prático: aplicar "um artefato por local
correlato" não pode significar "poluir a tela com vários botões de texto".
A solução já estabelecida no Gérard para exatamente esse problema é reusar o
padrão dos botões "?" contextuais: **ícone puro, sem rótulo de texto visível
(`criarBotaoAjudaContextual`/`criarIconeInterrogacaoContextual`,
`Main.java`), com o texto da expressão aparecendo só sob demanda** — no
hover ou clique, via popup (`mostrarMenuAjudaContextual`) — em vez de rótulo
permanente ocupando espaço na tela.

**Trade-off em aberto, não resolvido**: um ícone genérico "?" não diferencia
*qual* pergunta está naquele local — funciona quando só existe um tipo de
dúvida por correlato (situação atual), mas se cada local tivesse uma
expressão diferente da tabela acima ("E agora?" vs. "Cadê?" vs. "Por que
não?"), um "?" único apagaria essa diferenciação. Duas saídas possíveis,
nenhuma decidida: (a) glifos levemente distintos por tipo de dúvida — mesmo
espírito dos ícones de categoria já desenhados nesta faixa (ver
`criarIconeCategoriaComposicao` e vizinhos em `Main.java`) — ou (b) confiar
só na posição (o local já comunica qual pergunta é) e manter o "?" genérico.

## Indícios de reorganização após ajuda (relatório 2026, Tabelas 51/52)

Usados para decidir se o nível de ajuda deve ser mantido, intensificado ou
retirado — é o sinal de retroalimentação que o ZDP usa para regular sua
própria escolha de camada (N0–N7) ao longo da interação, e que também
alimenta o Modelo do Usuário (Agente Modelador):

| Resposta após ajuda | Interpretação | Implicação para o próximo nível |
|---|---|---|
| Manutenção do erro | Ajuda não alterou a hipótese/regra de ação | Reformular a ajuda, não repetir a mesma mensagem |
| Repetição do erro (em nova tentativa ou problema semelhante) | Possível teorema-em-ato falso persistente | Registrar recorrência; elevar nível de scaffolding |
| Correção local | Reorganização pontual, ainda dependente do suporte | Manter apoio temporário; observar estabilidade |
| Revisão com justificativa | Reorganização mais forte (ação + explicitação conceitual) | Reduzir gradualmente a ajuda |
| Estabilização após retirada de apoio | Maior autonomia, possível consolidação | Retirar scaffolding, manter só monitoramento |
| Oscilação entre acerto e erro | Reorganização instável, sem domínio consistente | Manter apoio intermediário; propor novas situações para verificar estabilidade |

**Cautela explícita do relatório**: um acerto isolado após ajuda é evidência
fraca de reorganização conceitual — pode ser tentativa e erro, memória da
mensagem anterior, ou ajuste local à interface. Só junto com justificativa
verbal coerente (não disponível automaticamente numa interface real, só no
protocolo com pesquisador presente) a evidência fica mais forte. **Isto é uma
limitação real para a implementação**: sem entrevista/think-aloud, o ZDP em
produção só tem a ação observável (certo/errado + recorrência), não a
justificativa verbal — então deve tratar acertos isolados com a mesma cautela
que o relatório recomenda, sem superestimar reorganização a partir de um
único acerto.

## Mecanismo proposto: força de regra ajustável pelos indícios de reorganização (decisão do usuário, 2026-07-25)

⚠️ Proposta de design discutida em conversa — **ainda não implementada**,
sem estrutura de dados nem código associado. Registrada aqui porque endereça
diretamente a lacuna nº4 (ver "Nota de honestidade" abaixo e a nota
correspondente em `../SKILL.md`).

**Problema que motivou a proposta**: a ação 2 do Agente Modelador
(`InferenciaRegrasModelador`, ver `agente-modelador.md`) mineraria regras
`regraDeAcao × invariante → suporte` via Apriori. Se o Agente ZDP passasse a
consultar essas regras para decidir estratégia (ideia discutida antes desta),
tratá-las como fixas a partir do momento em que são mineradas contrariaria a
Figura 2 de Vergnaud (situações/schemes/objetos/significante — setas 2 e
2bis): a relação entre invariante operatório e qualquer instância
semiótica/significante **não é um-para-um**, então uma regra minerada de um
corpus é, na melhor das hipóteses, uma hipótese — não uma lei.

**A proposta**: em vez de uma regra com força fixa, cada regra cadastrada no
Modelo do Usuário carrega uma força que **aumenta ou diminui** a cada nova
evidência de mudança na corretude das ações do usuário na mesma
tarefa/invariante, usando a escala de "Indícios de reorganização após ajuda"
(tabela acima) como sinal:

- **Enfraquece a regra**: manutenção do erro, repetição do erro, oscilação
  entre acerto e erro.
- **Fortalece a regra**: correção local (incremento pequeno — ver cautela
  abaixo), estabilização após retirada de apoio (incremento maior).

Dois refinamentos que vieram da própria cautela já documentada no relatório
(seção anterior) e do que já existe implementado no Gérard, não do relatório
em si:

1. **Incrementos pequenos, não saltos** — como um acerto isolado é evidência
   fraca (cautela do relatório), o ajuste de força por evento deveria ser
   pequeno o suficiente para que só a **recorrência** de evidência no mesmo
   sentido produza uma força alta ou baixa — nunca um único evento virando o
   status da regra sozinho.
2. **A limitação "sem justificativa verbal" do relatório não se aplica 100%
   ao Gérard** — existe `TelaArtefatoExplicativo`, onde a pesquisadora já
   registra `dificuldadeAutorrelatada`/`explicacaoElemento`/`explicacaoGeral`
   por ação (ver `DiagnosticoTarefa`). Quando esses campos estão
   preenchidos, o evento pode contar como o nível mais forte da escala
   ("revisão com justificativa"); quando não estão, cai para o nível mais
   fraco baseado só em certo/errado + recorrência.

**Por que isso resolve a tensão com o diagrama de Vergnaud**: a regra deixa
de ser um fato fixado no momento da mineração e passa a ser uma hipótese sob
revisão contínua — cada nova ação do usuário é uma chance de confirmar ou
enfraquecer a associação, em vez de presumir que ela vale para sempre.

**Questões técnicas em aberto, não resolvidas nesta conversa**:

- Onde essa "força" fica armazenada — campo novo em `ModeloUsuario`, ou
  associado a cada `AssociationRule` do Apriori (que já tem suporte/
  confiança/lift nativos — a força proposta seria um ajuste *sobre* essas
  métricas, ou substituiria elas)?
- **PART não tem o mesmo encaixe natural que o Apriori.** As regras do
  Apriori já vêm com métricas quantitativas por regra (suporte, confiança,
  lift) — a força proposta se soma a isso com naturalidade. O PART gera uma
  árvore/lista de regras sem uma "confiança por regra" exposta da mesma
  forma pela API do Weka; não está decidido se esse mecanismo se aplica ao
  PART, só ao Apriori, ou se precisa de tratamento diferente para cada um.
- Possível sobreposição com `probabilidadeSaberConteudo`/`internalizado`
  (ver "Entrada empírica para a ação 2" em `agente-modelador.md`, que já
  aponta a mesma tabela 51/52 como candidata a alimentar `internalizado`) —
  pode ser o mesmo mecanismo, pode ser dois mecanismos irmãos que
  precisariam ser desenhados juntos. Não decidido.

## Trabalho futuro: estender o freio de erros consecutivos (circuit-breaker) de CATEGORIA para POSICIONAR (2026-07-30)

**Não implementado — proposta registrada a pedido da usuária ("por isso
coloque para trabalho futuro"), para não implementar sem alinhamento.**

Hoje só a ação CATEGORIA tem um freio de erros consecutivos
(`gerard.agente.zdp.LimiteErrosConsecutivosCategoria`, limite=3, ver
`Main.java`/`clicarAtalhoCategoria`): depois de 3 erros seguidos, sai do
quiz e mostra as 3 definições juntas, usando a `MidiaPreferida` do modelo do
usuário. POSICIONAR (arrastar número pro diagrama) não tem equivalente — a
pessoa pode ficar clicando "tentar novamente" indefinidamente.

`C:\Users\cecomp\Downloads\transcriçõesDoutorado\analise_padroes_erros.md`
(análise de 20 transcrições de ação + 29 de entrevista do doutorado) dá
lastro empírico a estender o mesmo freio pra POSICIONAR:

- O ciclo "Tem certeza que...? Clique em ok para tentar novamente" aparece
  132 vezes em 17 dos 20 arquivos de ação — o mesmo tipo de ciclo repetitivo
  que motivou o freio de CATEGORIA.
- Dos 76 erros anotados pelos transcritores, posicionamento no diagrama é a
  2ª maior causa (22 ocorrências, 29%) — atrás só de categoria errada (25,
  33%).
- Nas entrevistas, os sujeitos atribuem os erros a não-entendimento
  conceitual ("não entendi", "achei que", "não sabia" — 50+22+17 menções),
  quase nunca a erro de clique/mira — reforça que o ciclo de tentativa-e-erro
  do sistema corrige a ação, mas não resolve a confusão conceitual que a
  gerou (ver seção 3, "Conclusão", do documento).

**Diferença-chave em relação ao freio de CATEGORIA, que trava a
implementação**: a reexplicação de CATEGORIA é fácil porque são só 3
definições fixas (composição/transformação/comparação) — dá pra mostrar as
3 juntas. POSICIONAR não tem uma explicação única: o erro é específico de
qual papel semântico (`papel.parte1`, `papel.referente`,
`papel.estadoInicial` etc.) a pessoa está errando dentro de qual diagrama.
O conteúdo da ajuda pós-freio precisaria ser selecionado por papel semântico
alvo, não uma tela fixa — isso ainda não tem desenho, só o diagnóstico do
gap.

## Nota de honestidade do próprio material

A operacionalização da ZDP e o uso de dados quantitativos que confirmem que
a instrução está de fato "na ZDP" ficam para trabalho futuro — mesmo na tese
original, essa parte é reconhecidamente incompleta. O relatório de 2026
avança bastante aqui (camadas N0–N7, regras condição→ação, escala de
indícios pós-ajuda), mas ele próprio insiste, na seção de limitações, que:
os dados vêm de protótipo em papel com intervenção humana simulando o
sistema; a validação foi formativa, não operacional; e as regras "devem ser
entendidas como uma contribuição exploratória... exigindo estudos
posteriores com... implementação computacional dos agentes e avaliação
empírica". Tratar como ponto de partida sólido para desenhar o ZDP, não como
especificação já validada.
