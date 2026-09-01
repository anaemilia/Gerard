# Notas sobre `protocolos_reais_replay.tsv`

Este arquivo documenta as decisões de inclusão/exclusão tomadas ao extrair
`protocolos_reais_replay.tsv` das fontes de pesquisa reais — texto que antes
vivia como comentário dentro de `CatalogoProtocolosReaisReplay.java` (código
Java), movido para cá quando o catálogo foi migrado para dado puro em
2026-07-30 (decisão da usuária: dado de pesquisa embutido em código é uma
dependência ruim). O TSV é lido por `RepositorioProtocolosReaisReplay`.

## Fonte geral

96 episódios, 16 sessões de pesquisa reais — 5 do mestrado (quadros de
análise da tarefa já tabulados) e 11 do doutorado (transcrições brutas,
protótipo mais antigo).

### Tabela de origem (id `hist_*` → arquivo-fonte)

Reconstruída em 2026-07-30 depois de os caminhos do mestrado terem se
perdido uma vez numa compactação de conversa — mantida aqui para não
depender de memória de sessão.

Os 11 arquivos do doutorado também existem, byte-a-byte idênticos
(conferido por checksum), reunidos sem subpastas em
`C:\Users\cecomp\Downloads\transcriçõesDoutorado\Ações e Entrevistas\` — mais
simples de navegar que a árvore espalhada em `D:\doutorado\Experimentos\...`,
onde a mesma sessão às vezes existe duplicada em 2-3 subpastas diferentes
(foi essa duplicação que causou a confusão original com a Jamile S8).

| id (`hist_*`) | Arquivo-fonte |
|---|---|
| `valderlangela` | `C:\Users\cecomp\Documents\aemq\Doutorado\quadros do mestrado\Valderlangela_com_coluna_problema.pdf` |
| `aldenira` | `C:\Users\cecomp\Documents\aemq\Doutorado\quadros do mestrado\Aldenira_com_coluna_problema.pdf` |
| `augusto` | `C:\Users\cecomp\Documents\aemq\Doutorado\quadros do mestrado\Augusto_com_coluna_problema.pdf` |
| `glaudionor` | `C:\Users\cecomp\Documents\aemq\Doutorado\quadros do mestrado\Glaudionor_com_coluna_problema.pdf` |
| `michele` | `C:\Users\cecomp\Documents\aemq\Doutorado\quadros do mestrado\Michele_com_coluna_problema.pdf` |
| `doutorado_felipewanderley_010610` | `D:\doutorado\Experimentos\transcrições\Transcrições Entregues\FelipeWanderley\01-06-10\Transcrição Experimento 01-06-10 (Video) - Por Felipe W..txt` |
| `doutorado_felipewanderley_professora_150610` | `...\FelipeWanderley\15-06-10\15-06-10.txt` |
| `doutorado_felipewanderley_professora_170710` | `...\FelipeWanderley\17-07-10\4-Transcrição Experimento 17-07-10 (Video) - Por Felipe W..txt` |
| `doutorado_felipewanderley_190710` | `...\FelipeWanderley\19-07-10\5-Transcrição Experimento 19-07-10 (Video) - Por Felipe W..txt` |
| `doutorado_jamile_s9_090610` | `...\BIA-felipe\Jamile\09-06-10\Transcrição 09-06.txt` |
| `doutorado_jamile_s8_080610` | `...\BIA-felipe\transcries\6-Transcrição Experimento 08-06-10 (video) - Felipe B. (Correção Felipe W.).txt` — **preferir esta versão corrigida**, não `transquição completa-08-06.txt` (ver nota abaixo) |
| `doutorado_experimentoII_felipeW` | `...\BIA-felipe\transcries\2-Transcrição Experimento II (ver data)(Video) - Por Felipe W..txt` |
| `doutorado_jamilly_080610` | `...\Jamilly\08-06-10\TESTE COM O USUARIO 08 06 10.txt` |
| `doutorado_jamilly_130710` | `...\Jamilly\13-07-10\TRANSCRIÇÕES AUDIO E VIDEO 13.07.10.txt` |
| `doutorado_jamilly_140710` | `...\Jamilly\14-07-10\TRANSCRIÇÕES AUDIO E VIDEO 14.07.10.txt` |
| `doutorado_jamilly_210710` | `...\Jamilly\21-07-10\TRANSCRIÇÕES 21.07.10 VIDEO.txt` |

(os 11 do doutorado ficam sob `D:\doutorado\Experimentos\transcrições\Transcrições Entregues\`)

### Reconciliação com a pasta achatada (2026-07-30)

`C:\Users\cecomp\Downloads\transcriçõesDoutorado\Ações e Entrevistas\` tem
**20 arquivos**, não 11 — reconciliados um a um por checksum/diff contra as
11 sessões já usadas:

- **11** batem exatamente com as sessões já catalogadas (mesmo checksum).
- **4** são do campo multiplicativo (S01/S04, "Divisão por partes/cotas",
  "Multiplicação") — fora de escopo, não é campo aditivo.
- **2** são duplicatas de fontes já usadas: `Exemplo-transcriç.txt` (=
  Augusto, já no quadro do mestrado) e `novoformato.txt` (recorte de
  15-06-10, já coberto).
- **2** são variantes quase-idênticas da sessão S8 (08-06-10), só com typo
  corrigido ou rótulos de falante removidos — não é dado novo. Uma delas
  (`6-Transcrição...Correção Felipe W.`) corrige um erro genuíno de
  transcrição (uma linha de prompt do sistema dizia "insere o quadrado
  sobre o numero 22" quando deveria dizer "32" — a ação real do S8 já
  usava 32 corretamente nos dois arquivos, então o catálogo já estava
  certo; a correção só importa pra não usar a fonte com o prompt errado
  daqui pra frente). Por isso a tabela acima já aponta pra essa versão
  corrigida como fonte preferida do S8.
- **1** (`video.txt`) é um recorte de outro problema da mesma S8 (Jammes/
  Gisele) que usa a mecânica antiga "setas do número natural" — já
  excluído por regra (sem caminho de código pra reproduzir).

Total: 11+4+2+2+1 = 20. Nenhum arquivo novo/desconhecido sobrou.

## Mestrado — 5 quadros (Valderlangela, Aldenira, Augusto, Glaudionor, Michele)

Cada quadro tem muito mais linhas do que aparece no TSV. Só os passos em que
a pessoa arrasta um número presente no ENUNCIADO para um elemento do
diagrama entraram — o único tipo de ação cujo par de papéis semânticos
corresponde exatamente ao contrato real de `DescritorPapelQuantitativo.avaliarPosicionamento`
(via `Main.avaliarQuestionamentoPosicionamento`). Quatro categorias de passo
dos quadros originais foram propositalmente deixadas de fora, porque não
havia caminho de código real para reproduzi-las neste nível de API (até
2026-07-30 — ver seção "Escolha de categoria" abaixo sobre o que mudou):

1. Entrada do resultado final computado (ex.: digitar "8" no estado inicial
   depois de calcular 4+4) — não é comparação de papel semântico entre dois
   elementos, é conferência de valor numérico digitado, validada por outro
   mecanismo.
2. Erros exclusivamente de sinal no número relativo/diferença — o sinal é
   resolvido por um menu da interface (`ScaffoldingNumeroRelativo`), sem
   avaliação pelo número e pelo papel semântico e encaminhamento factual ao `AgenteModelador`.
3. Manipulação interna das ajudas concretas (fábrica de quadradinhos, motor
   come-come) — mecânica própria da ajuda, não do diagrama principal.
4. Escolha da legenda/categoria (Composição/Transformação/Comparação) — até
   2026-07-30, essa era uma ação SELECIONAR avaliada por caminho de código
   diferente (`registrarAcaoNeutra`), não por `avaliarPosicionamento`.

Valderlangela aparece anonimizada como "Usuário 05" no quadro original.

Um caso (Glaudionor, "Erro proposital", problema 3, passo 10) foi mantido
mesmo sendo estranho de propósito: o quadro original marca a ação como "C"
(correta) mas a nota entre parênteses diz "o computador deveria ter agido
aqui, mas não o fez" — a própria pesquisadora registrou que o protótipo da
época deixou passar um erro que deveria ter sido sinalizado. Incluído
justamente para conferir se a avaliação factual atual relata esse caso como
divergente do veredito humano original (ver campo `correto` da linha
POSICIONAR correspondente e o relatório de divergências em
`TesteReplayProtocolosReais`).

## Escolha de categoria (linhas `tipoPasso=CATEGORIA`) — adicionado em 2026-07-30

Até 2026-07-30, a escolha de categoria (Composição/Transformação/Comparação)
não tinha caminho de código para ser avaliada como certo/errado — por isso
CatalogoProtocolosReaisReplay excluía esses passos de todas as 96 sessões,
mesmo eles existindo abundantemente nas transcrições brutas (ex.: "4 erros
consecutivos de categorização" já citados na fonte de ajuda adaptativa). Nesse mesmo
dia, a avaliação semântica da categoria foi criada, e as 11 sessões do
doutorado (que têm transcrição bruta, não quadro tabulado) foram
re-mineradas para extrair essas sequências. As 5 sessões do mestrado (só
quadro tabulado, não transcrição bruta) e a sessão "Jamile S8" (arquivo-fonte
não relocalizado a tempo) ficaram de fora dessa remineração — não têm linhas
`CATEGORIA` no TSV, só `POSICIONAR`.

Onde a escolha de categoria não teve nenhum "struggle" (acerto de primeira
tentativa), ainda assim uma linha `CATEGORIA` foi registrada (`correto=true`,
uma única linha) — importante para não confundir "sem dado" com "sem erro".

Casos deliberadamente excluídos ou simplificados durante a remineração:

- **FelipeWanderley 01-06-10, Problema 02 (Pedro/selos)**: excluído — a
  transcrição mistura, sem clareza causal, erro de categoria (composição)
  com erro de escolha do *elemento* dentro da categoria já certa
  (transformação), incluindo uma tentativa descartada por "ERRO NO
  PROCEDIMENTO/NOVO INICIO" antes de recomeçar do zero.
- **Jamilly 14-07-10, Problema 04 (Rafael/refrigerantes)**: excluído — trecho
  garbled entre a confirmação "sim" (composição) e a próxima pergunta
  explícita de categoria; pula direto para "Você escolheu transformação" sem
  clique explícito registrado, e menciona um "número 23" estranho a este
  problema (provável resíduo de outra questão colado no texto).
- Vários casos com pergunta de confirmação binária do sistema (ex.: "Na
  situação-problema existem partes que formam um todo?") registram "sim"/
  "não" que às vezes não parecem travar a categoria da forma esperada — não
  investiguei a causa (fora do escopo desta extração), só registrei o clique
  de categoria seguinte tal como aparece no texto.
- Onde a "luta" registrada era só sobre qual *elemento visual* representa a
  categoria já certa (chave/seta-lado/seta-cima), não sobre qual categoria
  escolher, não contei como struggle de categoria — só 1 linha CATEGORIA
  correta, mesmo que a tela de escolha tenha reaberto várias vezes.

## Doutorado — pasta Jamile

Só 2 dos arquivos renderam episódios confiáveis:
- `09-06-10/Transcrição 09-06.txt` (participante S9) — campo aditivo, log de
  ações passo a passo, mesmo protótipo antigo do Felipe Wanderley.
- `transquição completa-08-06.txt` (participante S8) — mesma coisa.

Ficaram de fora, com motivo:
- `09-06-10/Trabalho_completo02-02-11.txt` é cópia extraviada do arquivo do
  campo multiplicativo do Felipe (participante "S01", bolo/ovos/garrafas) —
  não é dado da Jamile, já preservado em `CampoMultiplicativoDadosFuturos`.
- `20-07-10/gravações 20-07.txt` (participante S20) é só a seção de
  entrevista/explicação pós-tarefa, sem log de ações — sem o log passo a
  passo não dá pra reconstruir pares de papel semântico com confiança.

**Pendência conhecida**: a remineração de escolha de categoria de 2026-07-30
procurou o participante "S8" em `Jamile/09-06-10/*` por engano (esses
arquivos só têm S9) — o arquivo certo é `transquição completa-08-06.txt`
(fora da pasta `Jamile/09-06-10/`). Sessão `hist_doutorado_jamile_s8_080610`
não tem linhas `CATEGORIA` no TSV por causa disso, não porque a transcrição
não tenha esse dado.

Jamile S8, Problema 4 (Composição, Lucas/figurinhas) ficou de fora do
POSICIONAR inteiro: a única ação de arraste registrada foi rejeitada por não
ter usado as "setas do número natural" exigidas (erro de método, não de
papel semântico).

## Doutorado — restante da pasta BIA-felipe

Varrida toda a pasta (raiz, "08-06/", "09-06-10/", "20-07-10/" soltos, e
"transcries/") comparando cada arquivo por conteúdo (diff byte a byte, não só
nome) contra o que já tinha sido processado. Quase tudo era duplicata das
pastas Felipe/ e Jamile/. O único conteúdo genuinamente novo foi
`transcries/2-Transcrição Experimento II...txt` — sujeito sem nome
identificado (só "SUJEITO"), campo aditivo, log de ações completo (sessão
`hist_doutorado_experimentoII_felipeW` no TSV).

Nele, Questões 6 e 7 (Comparação) tiveram os passos referente/referido
excluídos: o transcritor rotula os locais de forma inconsistente nessas duas
questões especificamente (o mesmo valor é aceito num local chamado
"referido" e, minutos depois, rejeitado no mesmo local chamado "referente")
— não dá pra confiar no papel sem ver o vídeo, então só o número relativo
(sem ambiguidade de rótulo) entrou. A Questão 3 (repetição da Questão 2)
ficou de fora inteira: só usa as "setas do número natural" (mecânica antiga,
sem par de papéis a extrair) — e, na remineração de categoria, a resposta
"SUJEITO: composição" apareceu sem a linha "Computador: Escolha no menu..."
antes dela no texto-fonte, então não há garantia de que foi de fato o
primeiro clique da tentativa (por isso Questão 3 não tem linha CATEGORIA
mesmo não estando no TSV por outro motivo).

## Doutorado — pasta FelipeWanderley, restante

Já processada a sessão 01-06-10 (`Transcrição Experimento 01-06-10...txt`,
participante "S(01-06-10)"). O resto da pasta 01-06-10 ("Gravação 0 e 1.txt"
e as "Transcrição - faixa N.txt") é de uma pessoa DIFERENTE — uma professora
("Professor (a)") — mas contém só a entrevista pós-tarefa, sem log de ações.
Essa mesma professora aparece de novo em duas sessões datadas depois
(15-06-10 e 17-07-10), essas sim com log de ações completo E cobrindo os
MESMOS problemas — por isso os fragmentos de 01-06-10 foram descartados como
redundantes/inferiores, em favor das duas sessões completas
(`hist_doutorado_felipewanderley_professora_150610` e `..._170710`).

A sessão 19-07-10 (`hist_doutorado_felipewanderley_190710`) é de um
participante diferente da "professora" acima — respostas de entrevista bem
mais simples/menos técnicas.

A Questão 4 de 15-06-10 (Ana/vestidos) é a mais rica das 96 sessões em erro
de categorização — 4 tentativas erradas (comparação x3, transformação x1)
antes de acertar composição. O caso dos quatro erros consecutivos de
categorização foi a motivação original para
construir a avaliação semântica da categoria.

## Doutorado — pasta Jamilly

`08-06-10/restante das gravações.txt` foi descartado por inteiro — conferido
contra `TESTE COM O USUARIO 08 06 10.txt` (mesma pasta), é a MESMA
participante ("S 08.06.10") com a MESMA explicação de ações, palavra por
palavra — uma repetição, não sessão nova. O próprio arquivo se identifica
como "RESTANTE DAS GRAVAÇÕES EM SEQUENCIA".

O Problema 4 de `TESTE COM O USUARIO 08 06 10.txt` tem uma divergência que a
própria pesquisadora sinalizou: "(OBS OS VALORES ESTÃO DIFERENTES NO VIDEO E
AUDIO)" — o log de ações (vídeo) é sobre composição (Lucas/figurinhas), mas
a "explicação" transcrita ali é sobre um problema de comparação diferente
(áudio trocado). Usei só o log de ações (vídeo) — inclusive na remineração
de categoria de 2026-07-30, onde a mesma divergência apareceu de novo (a
transcrição da entrevista menciona "comparação", mas o log de clique mostra
"composição"; mantive o log de clique).

`Jamilly 21-07-10`, Problema 1 (Ingrid/Ligiane) ficou de fora inteiro: a
sessão tem múltiplos reinícios do diagrama por confusão da participante e
termina sem uma mensagem clara de conclusão — não dá pra reconstruir com
confiança qual foi o estado final aceito. Por isso a numeração desta sessão
no TSV começa em "Problema 2".

## Verificação de integridade da migração (2026-07-30)

Antes de apagar `CatalogoProtocolosReaisReplay.java` (código Java
hardcoded), uma verificação automática comparou episódio por episódio, passo
por passo, os dados produzidos por esse código e os dados lidos de volta do
TSV recém-gerado: **96 episódios comparados, 0 divergências**. O TSV é
byte-a-byte equivalente ao que o código continha.

## Preenchimento de incógnita por digitação (`tipoPasso=TEXTO`) — adicionado em 2026-07-30

A usuária apontou que o catálogo só cobria dois dos quatro tipos de ação
observáveis nas transcrições reais (POSICIONAR e, desde mais cedo no mesmo
dia, CATEGORIA), faltando TEXTO — o preenchimento da incógnita do diagrama
por digitação de um resultado já calculado (ex.: "insira o resultado no
quadrado que representa uma interrogação" → sujeito digita 54). Essa lacuna
existia apesar de o código já ter a lógica de comparação necessária
(`Main.valorDigitadoCorrespondeAoCurado`) havia tempo — só faltava o caminho
de avaliação na `IncognitaQuantitativa`, criado nesse mesmo dia, e o tipo
`PassoTextoReplayHumano` no catálogo de replay.

Três agentes em paralelo re-mineraram as 11 sessões do doutorado (as 5
quadros do mestrado ficam de fora desta rodada — ver pendência abaixo) em
busca desse padrão. Resultado: **81 linhas TEXTO adicionadas** (337→418
linhas no arquivo). Confirmado depois via replay real
(`TesteReplayProtocolosReais`): 429 POSICIONAR + 87 CATEGORIA + 81 TEXTO =
597 diagnósticos gerados, dos quais 417 vêm de `hist_*` (dado humano real).

### Critério de inclusão/exclusão (mesmo princípio de sempre: na dúvida, excluir)

Cada agente foi instruído a marcar como "não é achado firme" qualquer
trecho ambíguo, e essas marcações foram respeitadas — nenhuma dessas linhas
entrou no TSV:

- **Mecanismo de entrada é seta incremental (spinner), não digitação
  livre de um valor já calculado** — ex.: Jamile S9 Problema 3 (repetição),
  Jamile S8 Problema 4, Jamilly 08-06-10 Q03, Experimento II Q3 e Q5 (esta
  última é justamente a sessão do Pedro/selos citada como exemplo-padrão,
  mas aqui resolvida clicando 394 vezes na seta em vez de digitar).
- **Valor final não bate com a aritmética do enunciado, sem explicação
  visível na transcrição** — Jamile S9 Problema 4 (Lucas/figurinhas, digitou
  32 quando o correto é 37), Jamile S8 Problema 3 (Ana/vestidos, digitou 8
  quando o correto é 7).
- **Sessão inteira degradada/confusa, sem uma sequência certo/errado
  limpa** — FelipeWanderley 15-06-10 Q05 e Q07, Jamile S9 Problema 7 (mesma
  sessão, contradição entre ação e entrevista), Jamilly 21-07-10 Problema 1
  (Ingrid/Ligiane).
- **Resultado aritmeticamente inconsistente mas aceito pelo sistema
  ("Parabéns")** — Jamilly 21-07-10 Problema 2 (Maria/figurinhas, digitou 10
  quando 32+22=54); mantive fora por não conseguir confirmar se é erro de
  transcrição do documento original ou comportamento real do sistema.
- **Gravação interrompida antes da confirmação final** — FelipeWanderley
  19-07-10 Q07, Experimento II Q7 (Jammes/Gisele — chega a 29 mas o arquivo
  termina sem "Parabéns").
- **Vídeo/trecho não encontrado** — Jamile S8 Problema 6 (Jammes/Gisele).

### Normalização de papel-alvo (Glaudenice/Claudenice ↔ Nádia/Nadia)

Nas ~12 ocorrências desse par de personagens espalhadas pelas sessões, o
padrão dominante e consistente é `referente=13` (27-14). Dois relatos
individuais (Jamile S8 Problema 5, Jamilly 08-06-10 Problemas 5 e 6)
rotularam o mesmo valor 13 como `referido` — normalizei essas três linhas
para `papel.referente`, por consistência com todas as outras ~9 ocorrências
do mesmo problema em outras sessões, e não por ter conferido a transcrição
original linha a linha de novo.

### Pendências

- **Os 5 quadros do mestrado (Valderlangela, Aldenira, Augusto, Glaudionor,
  Michele) ainda não foram re-minerados para TEXTO nem para CATEGORIA.**
  Valderlangela foi lida por completo (104 ações brutas identificadas na
  Questão 03/05/novo-06) mas os dados ainda não foram escritos no TSV. As
  outras 4 quadros nem foram lidas ainda nesta rodada.
- **Jamile S8**: os dados de TEXTO acima vieram de
  `transquição completa-08-06.txt`, mas o ponto de origem correto/corrigido
  já identificado é `6-Transcrição Experimento 08-06-10 (video) - Felipe B.
  (Correção Felipe W.).txt` (ver seção "Doutorado — pasta Jamile" acima). A
  correção conhecida entre os dois arquivos afeta uma contagem de ação
  diferente (22 vs 32), não necessariamente estes valores de TEXTO — mas
  isso não foi reconferido linha a linha contra o arquivo corrigido.
