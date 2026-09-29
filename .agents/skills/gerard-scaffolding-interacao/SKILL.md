---
name: gerard-scaffolding-interacao
description: Exemplos teóricos e repertório operacional de scaffolding do Gérard, incluindo estilo de interação, mensagens, material concreto, automatização de passos e mostrar modelo completo. Use sempre que for criar, revisar ou discutir feedback de erro/acerto, protocolos de arraste/posicionamento ou qualquer apoio oferecido ao usuário. A literatura não é reduzida a uma taxonomia fechada, mas o repertório operacional atual tem exatamente seis códigos. Esta skill nunca deve ser interpretada isoladamente: confronte-a com as skills de domínio, localidade, consistência, ajuda adaptativa, interação e registro pertinentes ao caso.
---

# Exemplos e regras de scaffolding — Gérard

## Estado verificado da implementação

Ao auditar ou alterar o piloto adaptativo, a atração magnética, os consumidores
reais ou divergências entre códigos e comportamentos legados, leia
[`references/estado-implementacao.md`](references/estado-implementacao.md).
Esse histórico datado não redefine o repertório nem precisa ser carregado para
uma discussão exclusivamente conceitual sobre scaffolding.

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
2. Mensagens com diferentes finalidades pedagógicas;
3. Ajuda utilizando metáforas de materiais concretos — os quadradinhos
   manipuláveis já implementados;
4. Automatização de passos por `AG_AE` e atração magnética;
5. Mostrar modelo completo.

**Fora do escopo**: a paleta neutra/tokens visuais e a consistência entre Windows e mobile — isso pertence à skill de identidade visual. Esta skill decide *o que* uma cor significa (ex.: erro=vermelho); a de identidade visual decide *o tom exato* dessa cor e sua aplicação cross-platform.

Esta skill possui a definição dos apoios. Ela não aprende regras e não
centraliza a seleção: cada repertório pertence ao proprietário semântico
correspondente; o objeto devolve uma decisão abstrata e a representação a
materializa em sua própria sintaxe.

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

Feedback de erro e sucesso preserva momento, modalidade, significado das cores
e ausência de correção automática. Ao alterar tremor, som, tips, selos, timing
ou fluxos de destino ocupado, leia
[`references/feedback-implementado.md`](references/feedback-implementado.md).

## 2. Mensagens — exemplos teóricos abertos

Ao criar ou revisar mensagens, pistas, explicações, marcadores de curadoria ou
materialização em mídia, leia
[`references/mensagens.md`](references/mensagens.md). Os exemplos permanecem
abertos e não ampliam os seis códigos operacionais.

## 3. Ajuda utilizando metáforas de materiais concretos

No Gérard, essa ajuda corresponde aos quadradinhos arrastáveis e manipuláveis.
`AG_AC` existe incorporado a esse material e não constitui affordance isolada.
Ao alterar contagem, adição/remoção, versão simplificada ou distribuição entre
interação, representação, sincronização e registro, leia
[`references/material-concreto.md`](references/material-concreto.md).

## 4. Automatização de passos — `AG_AE` por atração magnética

Ao alterar disponibilidade, ordem autorizada ou distinção entre `AG_AE`, pista
visual e `AG_AC`, leia
[`references/automatizacao-passos.md`](references/automatizacao-passos.md).

## 5. Mostrar modelo completo

Ao distinguir esse apoio da automatização, definir seu registro factual ou
avaliar sua implementação, leia
[`references/mostrar-modelo-completo.md`](references/mostrar-modelo-completo.md).
Sua definição teórica não comprova disponibilidade no produto.

## Outros scaffoldings podem existir

Não forçar um novo apoio a caber em um dos exemplos acima. Analisá-lo junto às demais skills pertinentes, preservar sua definição teórica e documentá-lo pelo conhecimento que efetivamente contém.

## Regra de segurança

Antes de alterar qualquer protocolo de feedback já implementado (tremor/som, timing, cores de significado), confirme com o usuário — parte deste comportamento já foi testado e ajustado a partir de experiência real (ex.: o susto causado por mensagens de sucesso imediatas). Não presuma que uma mudança é melhoria sem validação.
