---
name: gerard-scaffolding-interacao
description: Taxonomia aberta dos tipos de scaffolding do Gérard — (1) estilo de interação (protocolo de mouse: arrastar, proximidade, atração magnética; cores de significado), (2) mensagens (informativa e questionamento), (3) material concreto (quadradinhos), (4) automatização de passos (ainda não implementado). Use sempre que for criar, revisar ou discutir feedback de erro/acerto, protocolos de arraste/posicionamento, ou qualquer dica/scaffold oferecido ao usuário no Gérard. Lista extensível — podem surgir novos tipos. A maior parte deste domínio já é comportamento validado (ver "Status de verificação"); tratar como referência de convenções estabelecidas, não como espaço livre para redesenhar.
---

# Categorização de scaffolding — Gérard

## Status de verificação (2026-07-20)

8 das 9 regras abaixo foram conferidas linha a linha contra o código atual e se confirmam. A única exceção é a seção 2 ("duas categorias de mensagem"): o código tem uma classe dedicada para questionamento, mas **não** tem uma estrutura equivalente para "mensagem informativa" — corrigido na seção correspondente. Não trate versões anteriores deste texto como válidas.

## Escopo desta skill

O scaffolding do Gérard é um conjunto aberto de tipos de apoio pedagógico. Por enquanto são estes quatro, mas a lista pode crescer — não tratar como taxonomia fechada:

1. Estilo de interação (protocolo de mouse + cores de significado do feedback)
2. Mensagens (informativa e questionamento)
3. Material concreto (quadradinhos arrastáveis e manipuláveis)
4. Automatização de passos (ainda não implementado — ver seção 4)

**Fora do escopo**: a paleta neutra/tokens visuais e a consistência entre Windows e mobile — isso pertence à skill de identidade visual. Esta skill decide *o que* uma cor significa (ex.: erro=vermelho); a de identidade visual decide *o tom exato* dessa cor e sua aplicação cross-platform.

## 1. Estilo de interação

Protocolo de mouse:
- Arrastar
- Proximidade (incluindo proximidade de cores)
- Atração magnética

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

## 2. Mensagens — corrigido

Duas categorias de **uso**, mas só uma delas é uma categoria **estrutural** no código:

- **Questionamento**: tem uma classe dedicada — `ScaffoldingQuestionamento.criarPerguntaConfirmacao` gera perguntas explícitas (frases terminadas em "?", ex. `mensagens_pt.properties:220`).
- **Informativa**: comunica um fato/estado, mas **não** tem uma classe/estrutura própria equivalente — é apenas string localizada avulsa (ex.: "Foi atingido o limite..." em `mensagens_pt.properties:127,131`), sem um mecanismo que a distinga estruturalmente de qualquer outro texto da interface.

Ao criar uma nova mensagem informativa, não presuma que existe um padrão de classe já estabelecido a seguir — verifique caso a caso como mensagens informativas existentes são implementadas antes de inventar uma abstração nova.

Preferir textos curtos; evitar linguagem natural extensa como mecanismo primário de feedback — o feedback adaptativo do Gérard é baseado em pequenas mensagens, tips, cores, posicionamento e estilo de interação, não em diálogo em linguagem natural.

## 3. Material concreto

Quadradinhos arrastáveis e manipuláveis — representação concreta que pode ser removida em versões simplificadas (ex.: versão mobile, que mantém só a modelagem/representação formal).

## 4. Automatização de passos — AINDA NÃO IMPLEMENTADO

Tipo de scaffolding planejado para o futuro, ainda sem opções na tela. Confirmado: existe apenas um rótulo de legenda não utilizado (`pesq.d3.scaffold.type.automation`, `mensagens_pt.properties:562`) para a visualização D3 do pesquisador; nenhum código em `gerard.pesquisador` ou em qualquer outro lugar do projeto implementa, referencia ou dispara essa automação.

Princípio já definido, documentado na literatura da área, e que vale para a interface do Gérard como um todo (não só para scaffolding): em hipótese alguma a interface pode automatizar passos cuja ordem não tenha vindo de autorização explícita do pesquisador.

Ao trabalhar neste tipo de scaffolding no futuro, tratar como requisito a ser desenhado sob orientação direta do usuário sobre como cada automação deve ser autorizada — não como comportamento já validado.

## Este conjunto pode crescer

Os quatro tipos acima são os mapeados até agora. Se surgir um novo tipo de apoio pedagógico que não se encaixe claramente em nenhum dos quatro, sinalizar isso ao usuário em vez de forçar o encaixe — pode ser um quinto tipo a documentar.

## Regra de segurança

Antes de alterar qualquer protocolo de feedback já implementado (tremor/som, timing, cores de significado), confirme com o usuário — parte deste comportamento já foi testado e ajustado a partir de experiência real (ex.: o susto causado por mensagens de sucesso imediatas). Não presuma que uma mudança é melhoria sem validação.
