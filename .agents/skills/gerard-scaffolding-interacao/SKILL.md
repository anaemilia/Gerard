---
name: gerard-scaffolding-interacao
description: Exemplos teóricos e repertório operacional de scaffolding do Gérard, incluindo estilo de interação, mensagens, material concreto, automatização de passos e mostrar modelo completo. Use sempre que for criar, revisar ou discutir feedback de erro/acerto, protocolos de arraste/posicionamento ou qualquer apoio oferecido ao usuário. A literatura não é reduzida a uma taxonomia fechada, mas o repertório operacional atual tem exatamente seis códigos. Esta skill nunca deve ser interpretada isoladamente: confronte-a com as skills de domínio, localidade, consistência, ajuda adaptativa, interação e registro pertinentes ao caso.
---

# Exemplos e regras de scaffolding — Gérard

## Status de verificação (2026-07-20)

8 das 9 regras abaixo foram conferidas linha a linha contra o código atual e se confirmam. A única exceção é a seção 2 ("duas categorias de mensagem"): o código tem uma classe dedicada para questionamento, mas **não** tem uma estrutura equivalente para "mensagem informativa" — corrigido na seção correspondente. Não trate versões anteriores deste texto como válidas.

## Regra de interpretação integrada

Não usar esta skill como fonte autossuficiente de uma decisão. Antes de planejar ou implementar um scaffolding, identificar quais conhecimentos o caso cruza e ler integralmente as skills correspondentes:

- significado e limites semânticos: `gerard-domain-model-first`, `gerard-knowledge-locality-principle` e o modelo semântico normativo;
- aprendizagem das regras de ajuda: `gerard-ajuda-adaptativa` e a referência
  do Modelador; aplicação: proprietário semântico do repertório local usando
  uma projeção imutável do Modelo do Usuário;
- propagação entre representações: `gerard-consistencia-estado`;
- protocolo e localização do código de interação: `gerard-handlers-de-interacao` e `gerard-posicionamento-relativo`;
- fatos que precisam ser registrados: `gerard-log-acao-instrumental` e `gerard-semantic-event-logging`;
- efeitos sobre o modelo do usuário: `gerard-modelo-usuario`;
- aparência visual: `gerard-identidade-visual`, sem transferir para ela o significado pedagógico das cores.

Produzir a decisão somente depois de separar: conhecimento semântico, sintaxe de cada representação, mecânica de interação, estratégia pedagógica, evento factual e interpretação exclusiva do pesquisador. Se as skills parecerem conflitantes, não escolher uma isoladamente: confrontar seus escopos, o status de verificação e as decisões explícitas mais recentes da usuária.

## Escopo desta skill

As categorias teóricas de scaffolding são exemplos abertos e não formam uma
taxonomia fechada. Isso não autoriza criar códigos operacionais adicionais. Por
decisão explícita mais recente da usuária (2026-08-13), o repertório operacional
atual do Gérard possui exatamente seis códigos; sinônimos ou novos códigos sem
decisão explícita são redundância:

1. `AG_EMLQ` — exibir mensagens de questionamento;
2. `AG_EMS` — exibir mensagem de sucesso;
3. `AG_EME` — exibir mensagem explicativa;
4. `AG_EMCME` — exibir material concreto e mensagem explicativa sobre o uso do material;
5. `AG_AC` — automatizar a contagem por meio dos quadradinhos do material concreto;
6. `AG_AE` — automatizar passos da modelagem usando atração magnética para
   atrair o objeto semântico para sua posição correta.

Desde a P2.4A.1, `CodigosAjudaAdaptativa` valida esse vocabulário fechado na
entrada de uma regra publicada. Essa validação não substitui o repertório
local: cada proprietário semântico continua aceitando somente o subconjunto de
ajudas que ele próprio possui.

Os grupos teóricos abaixo organizam esses apoios e outros exemplos da
literatura, sem ampliar automaticamente o repertório operacional:

1. Estilo de interação (protocolo de mouse + cores de significado do feedback)
2. Mensagens com diferentes finalidades pedagógicas — ver seção 2
3. Ajuda utilizando metáforas de materiais concretos — os quadradinhos manipuláveis já implementados; ver seção 3
4. Automatização de passos (AG_AE por atração magnética — ver seção 4)
5. Mostrar modelo completo (ver seção 5)

**Fora do escopo**: a paleta neutra/tokens visuais e a consistência entre Windows e mobile — isso pertence à skill de identidade visual. Esta skill decide *o que* uma cor significa (ex.: erro=vermelho); a de identidade visual decide *o tom exato* dessa cor e sua aplicação cross-platform.

Esta skill possui a definição dos apoios. Ela não aprende regras e não
centraliza a seleção: cada repertório pertence ao proprietário semântico
correspondente; o objeto devolve uma decisão abstrata e a representação a
materializa em sua própria sintaxe.

**Estado do piloto P2.3C (2026-08-13):** o repertório local de
`IncognitaQuantitativa` referencia os três apoios já aprovados para a sequência
de rejeições: `AG_EMLQ` (metacognitivo/visual), `AG_EME`
(conceitual/visual) e `AG_EMCME` (procedimental/manipulativa e visual). Esse
repertório é apenas o subconjunto pertencente à incógnita, não um sétimo
catálogo. A interface já materializa decisões publicadas desse proprietário e
registra separadamente decisão e apresentação; na ausência de regra publicada,
o fallback legado permanece explícito.

## 1. Estilo de interação

Protocolo de mouse:
- Arrastar
- Proximidade (incluindo proximidade de cores)
- Atração magnética

Esses três itens descrevem mecanismos do estilo de interação. Não os
confundir com os seis valores de “Tarefa de Interação” de Shneiderman
(`SELECIONAR`, `POSICIONAR`, `ORIENTAR`, `QUANTIFICAR`, `CAMINHO`, `TEXTO`),
cujo vocabulário e registro pertencem a `gerard-log-acao-instrumental`.
Tampouco converter automaticamente um gesto de mouse nesses valores: a
fronteira entre gesto físico e ação semanticamente constituída pertence a
`gerard-log-gestos-interacao` e `gerard-handlers-de-interacao`.

Cores de significado (convenções culturais já estabelecidas, não arbitrárias) — confirmado em `gerard/ui/UITemaGerard.java`:
- Azul → sucesso (`COR_SUCESSO = new Color(74,130,201)`, `UITemaGerard.java:85`)
- Vermelho → erro (`COR_ERRO = new Color(200,40,40)`, `UITemaGerard.java:98`)

### Feedback de erro

- Ocorre apenas ao soltar a peça, nunca durante o arrasto — princípio: não pode quebrar a hipótese do usuário em tempo de execução; ele precisa poder corroborar ou refutar a hipótese inicial. Confirmado: `mouseDragged` (`Main.java:8977-8985`) só atualiza o *texto* de um tip já ativo (`atualizarQuestionamentoPersistenteDuranteMovimento`, `Main.java:9472-9485`); o disparo de erro (`sinalizarErro`) só acontece a partir de `mouseReleased` via `processarQuestionamentoPosicionamento` (`Main.java:9070`).
- Padrão atual: tremor (vibração) + som. Evitar mensagens de texto extensas para não poluir a interface pequena (especialmente mobile). Confirmado: `ScaffoldingFeedbackMultissensorialErro.java:48,57-64` (deslocamento via `Timer`) e linha 161-166 (`Toolkit.getDefaultToolkit().beep()`); nenhum diálogo é usado.
- Comportamento pós-erro: a peça permanece na posição incorreta, aguardando o usuário arrastá-la para o local certo — não há reversão/correção automática em lugar nenhum. Confirmado: `pararTremor()` (`ScaffoldingFeedbackMultissensorialErro.java:101-133`) só restaura a posição-base pré-tremor (a própria posição de soltura), nunca uma posição "correta".
- Se necessário, um tip textual curto e fixo perto da peça pode acompanhar o erro (não usar ícone/seta — pode confundir com a seta da própria representação de Vergnaud). Confirmado: `desenharAnotacaoMouseOver` (`Main.java:5021-5127`) renderiza só um retângulo arredondado + texto quebrado, posicionado ao lado do item; as setas existentes no código (`desenharSetaCurta`, `Main.java:5005`) são de outro mecanismo (proximidade/encaixe), não do tip de erro.
- **Bug conhecido a evitar reintroduzir**: ao arrastar uma peça para uma caixa/posição já ocupada, ela não deve simplesmente desaparecer sem feedback — precisa do mesmo padrão de tremor/som. Confirmado, mas **essa garantia está implementada em dois módulos separados, não um só**: `Main.java:7385-7402` (`registrarLimiteQuantidadeAtingido`, para o diagrama Venn) e `PainelComposicaoMedidasDesktop.java:354-361/448-449` (`dispararErro`, para a tela de composição de medidas). Se for mexer em qualquer um dos dois fluxos de soltura, cheque os dois — corrigir um não garante que o outro também está certo.

### Feedback de sucesso

- Usar tempo a favor da compreensão: dar um intervalo antes de exibir indicador/mensagem de avanço, evitando parecer imediatista. Confirmado: `SequenciadorFeedbackConclusao.java:19` define `ATRASO_PADRAO_DECISAO_MS = 1150` antes de `aoSolicitarDecisao()` (linhas 30-40).
- Indicador de avanço não deve ficar fixo na tela — só aparece quando necessário. Confirmado: é ocultado via `ocultar()` dentro de `aoCancelar()` (`Main.java:689-696`).
- Essas decisões de interface ainda estão em fase de teste com usuários; não tratar como definitivas sem confirmar com o usuário antes de codificar.

## 2. Mensagens — exemplos teóricos abertos

Exemplos de scaffolding realizado por mensagens:

- **Questionamento**: perguntar se o usuário tem certeza de que o passo executado está especificado no texto. O código possui uma classe dedicada, `ScaffoldingQuestionamento`, mas a existência dessa classe não reduz todo questionamento a um único formato.
- **Fornecer dica ou pista**: oferecer informação parcial que apoie a continuidade da modelagem.
- **Explicar passos da modelagem e a legenda utilizada**: tornar explícita a sintaxe da representação e o encadeamento dos passos.
- **Dar explicação textual**: apresentar uma explicação em linguagem natural como apoio pedagógico.

Esses itens são exemplos, não subtipos exaustivos nem classes obrigatórias. Ao criar ou revisar uma mensagem, confrontar sua finalidade com `gerard-ajuda-adaptativa`, `gerard-consistencia-estado`, `gerard-log-acao-instrumental` e `gerard-semantic-event-logging`. Não inferir a estratégia pedagógica apenas pelo formato textual da mensagem.

O código atual ainda possui mensagens informativas como strings localizadas sem uma estrutura equivalente à classe de questionamento. Isso é um estado de implementação, não uma definição teórica de quantos tipos de mensagem existem.

Quando uma mensagem explicativa usa personagens ou entidades informados na curadoria, o modelo textual deve nomear explicitamente os campos de origem, como `{Personagem_1}`, `{Personagem_2}` e `{Personagem_3}`. A representação substitui cada marcador pelo campo curado homônimo; não associa personagens pela posição no diagrama e não infere seus papéis. A adequação dos valores curados e da frase resultante permanece sob responsabilidade do pesquisador humano.

Uma explicação `AG_EME` pode ser materializada em linguagem natural, animação
ou história em quadrinhos quando essas formas estiverem disponíveis no
repertório local. História em quadrinhos é uma sintaxe visual de apresentação,
não um sétimo código de scaffolding. A necessidade e a função da ajuda são
decididas antes; a mídia preferida do perfil escolhe apenas a materialização.

## 3. Ajuda utilizando metáforas de materiais concretos

No Gérard, a ajuda utilizando metáforas de materiais concretos corresponde aos quadradinhos arrastáveis e manipuláveis já implementados. Nesta arquitetura, a categoria pode ser reduzida a esse mecanismo; não presumir outras metáforas de material concreto sem uma nova decisão explícita da usuária.

Os quadradinhos constituem uma representação concreta que pode ser removida em versões simplificadas (ex.: versão mobile, que mantém só a modelagem/representação formal).

**AG_AC (automatizar a contagem)** não é um item separado da seção 4 — a usuária esclareceu (2026-08-08) que só faz sentido existir incorporado a este material concreto, nunca como affordance isolada. O mecanismo já existente de adicionar/remover quadradinhos um de cada vez (`ControleAdicionarQuadradinhoVenn`/`ControleRemoverQuadradinhoVenn`, `Main.java: adicionarQuadradinhoAoAgrupamentoInterno`/`removerQuadradinhoDoAgrupamentoInterno`) já é essa automação: cada clique corresponde a exatamente uma unidade, manipulativo e progressivo — a classificação de AG_AC como "Guiada por movimento/manipulativa" em `TAREFA_PENDENTE_FLUXO_TENTATIVAS_E_SCAFFOLDING.md` descreve esse comportamento já existente, não um mecanismo novo a construir. Confirmado pela usuária: a aparência/funcionamento dos quadradinhos permanece exatamente como está hoje — nenhuma mudança de código foi autorizada nem é necessária.

Revisão de localidade do conhecimento feita em 2026-08-08 (`gerard-domain-model-first`/`gerard-knowledge-locality-principle`): o fluxo de adicionar/remover quadradinho é interação+representação (`Main.java` traduz o clique em comando, delega a sincronização a `sincronizarTodasAsRepresentacoesAPartirDoDiagramaComplementar` e o registro a `registrarAcaoGranular`); `CirculoVenn`/`QuadradinhoVenn` seguem o mesmo padrão leve de autodesenho já usado por todo o resto dos elementos do diagrama (`ElementoVergnaud`, `ConectorVergnaud`) — consistente com a convenção já estabelecida no projeto, não uma exceção a corrigir.

## 4. Automatização de passos — AG_AE por atração magnética

Tipo de scaffolding antes só planejado. O rótulo de legenda não utilizado (`pesq.d3.scaffold.type.automation`, `mensagens_pt.properties:562`, visualização D3 do pesquisador) continua sem código próprio — é um item separado.

**AG_AE (automatizar passos da modelagem) — implementado por atração
magnética adaptativa na P2.4A (2026-08-13)**: a mecânica validada permanece em
`ScaffoldingProximidade`, `aplicarAtracaoMagnetica` e
`centralizarItemNoElemento`, sempre usando a geometria real do item e do alvo.
Sua disponibilidade, porém, deixou de ser permanente. O
`PapelQuantitativoPosicionavel` decide localmente a partir do diagnóstico
factual, da fotografia do Modelo do Usuário e de regra publicada pelo
Modelador; o `MaterializadorAtracaoMagneticaAdaptativa` conserva a ativação
temporária por chave de papel. `Main` somente consulta essa ativação antes de
executar atração ou centralização.

Sem regra aplicável, o movimento magnético fica desligado. Quando `AG_AE` é
selecionado, somente o objeto semanticamente vinculado ao papel decidido recebe
a affordance. O ciclo termina no posicionamento correto desse papel ou na
restauração/troca da atividade. A P2.4A alterou exclusivamente a disponibilidade
da mecânica; não alterou sua geometria nem os demais efeitos visuais do estilo
de proximidade.

O botão “Ver dica” e seus registros ainda aparecem no legado com o código
`AG_AE`, mas oferecem uma pista visual sob demanda, não a automatização por
atração magnética definida pela usuária. Esse uso do código é uma divergência
de classificação a corrigir sem duplicar nem reimplementar a atração já
existente. A P2.3C não altera esse protocolo de arraste.

Princípio já definido, documentado na literatura da área, e que vale para a interface do Gérard como um todo (não só para scaffolding): em hipótese alguma a interface pode automatizar passos cuja ordem não tenha vindo de autorização explícita do pesquisador.

A migração preservou a mecânica e alterou somente sua disponibilidade: ela é
uma materialização temporária de uma decisão `AG_AE`, não uma disponibilidade
permanente da tela. Não confundi-la com uma simples pista visual. (AG_AC não
pertence mais a esta seção — ver seção 3.)

## 5. Mostrar modelo completo — DEFINIÇÃO TEÓRICA (2026-08-09)

Tipo de scaffolding que apresenta ao participante o modelo já completo, podendo eliminar ou reduzir deliberadamente o obstáculo de coordenar sistemas de representação com sintaxes diferentes.

Não confundir com **automatização de passos**:

- **Mostrar modelo completo** apresenta o estado/modelo final completo e suas correspondências.
- **Automatização de passos** realiza, antecipa ou guia um ou mais passos do processo de construção.

Ambos podem reduzir a dificuldade representacional e são recursos pedagógicos legítimos quando acionados por uma estratégia de scaffolding explicitamente autorizada. Essa redução não deve ser proibida pela arquitetura nem surgir acidentalmente como efeito colateral da sincronização de estado.

Quando utilizado, o sistema deve registrar factualmente o tipo de scaffolding exibido, a estratégia que o acionou, as representações afetadas, o estado anterior e o posterior e a origem `SISTEMA`. O registro da exposição ao scaffold não autoriza o sistema a concluir que o participante construiu a correspondência entre as representações; qualquer hipótese analítica permanece exclusiva do pesquisador humano.

**Status de implementação:** esta seção registra a definição teórica confirmada pela usuária em 2026-08-09. Não presumir que exista hoje uma implementação validada de “mostrar modelo completo”; auditar e obter autorização específica antes de criar ou alterar esse comportamento.

## Outros scaffoldings podem existir

Não forçar um novo apoio a caber em um dos exemplos acima. Analisá-lo junto às demais skills pertinentes, preservar sua definição teórica e documentá-lo pelo conhecimento que efetivamente contém.

## Regra de segurança

Antes de alterar qualquer protocolo de feedback já implementado (tremor/som, timing, cores de significado), confirme com o usuário — parte deste comportamento já foi testado e ajustado a partir de experiência real (ex.: o susto causado por mensagens de sucesso imediatas). Não presuma que uma mudança é melhoria sem validação.
