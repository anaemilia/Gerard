---
name: gerard-ajuda-adaptativa
description: Proposta teórica (tese/pesquisa do usuário) da arquitetura de Ajuda Adaptativa do Gérard — uma sociedade de três agentes de tutoramento (Monitor, ZDP, Modelador). Use como ponto de entrada sempre que for discutir ou implementar observação do usuário, avaliação de ações, decisão de estratégia pedagógica ou manutenção do modelo do usuário. Para detalhes de um agente específico, leia o arquivo correspondente em references/. NÃO é comportamento validado em produção — é especificação a confrontar com o código real.
---

# Ajuda Adaptativa — Gérard

## ⚠️ Status: proposta teórica, NÃO comportamento validado

Todo o conteúdo desta skill (raiz + `references/`) vem do material de pesquisa/tese do usuário (RFAs e Figuras 5.77 a 5.85). É uma **especificação de design**, não uma descrição do que já está implementado.

**Checado em 2026-07-20**: busquei no código por `AgenteMonitor`, `AgenteZDP`, `AgenteModelador`, `ModeloDoUsuario`/`ModeloUsuario` e qualquer arquitetura de threads produtor-consumidor equivalente — nada encontrado em `src/`. Confirma que nada desta arquitetura existe implementado, nem parcialmente, no código atual do Gérard.

**Atualizado em 2026-07-20**: os três arquivos de `references/` (`agente-monitor.md`, `agente-zdp.md`, `agente-modelador.md`) estão completos. Duas skills companheiras também foram instaladas: `gerard-log-acao-instrumental` (esquema de captura do log, Quadro 4.55 — dona do formato que o Agente Monitor consome) e `gerard-modelo-usuario` (esquema das dimensões do Modelo do Usuário, Quadro 5.60 — dona da estrutura que o Agente Modelador escreve e o Agente ZDP consulta). Ver a seção "Relação com outras skills" abaixo.

**Atualizado em 2026-07-22**: `agente-zdp.md` e `agente-modelador.md` incorporaram material do relatório de pesquisa "Análise de situações interativas no Gerard..." (Queiroz, 2026, Univasf), que a lacuna nº4 abaixo já apontava como necessário. Esse relatório dá camadas progressivas de ajuda (N0–N7), regras condição→ação e uma escala de indícios de reorganização pós-ajuda — a lacuna nº4 fica **parcialmente** endereçada (ver detalhe na nota atualizada abaixo). No código: `AgenteMonitor` e a ação 1 do `AgenteModelador` (armazenar caso) seguem implementados; `AgenteZDP` ainda não existe — é o próximo a ser modelado.

## Regras obrigatórias

1. Nunca presumir que algo aqui já existe no código sem verificar.
2. Nunca reescrever código existente para "bater" com esta especificação sem confirmação explícita do usuário.
3. Ao encontrar divergência entre esta skill e o código real, reportar ao usuário — não corrigir silenciosamente em nenhuma direção.
4. É material de referência para design e implementação futura sob orientação direta do usuário — não uma ordem de serviço.

## Motivação (RFAs)

- RFA 1.1 — deve haver um agente responsável por observar o usuário.
- RFA 1.2 — o agente deve conhecer a forma correta de resolução do problema.
- RFA 1.3 — o agente deve deliberar sobre a melhor forma de ajuda.
- RFA 1.4 — o agente deve conhecer o que o usuário sabe e não sabe.
- RFA 1.5 — o agente deve possuir um conjunto de conteúdos pedagógicos.
- RFA 1.6 — o agente deve tratar situações em que o próximo estado do ambiente não é totalmente determinado pelo estado atual e pela ação do agente.

## A Ajuda Adaptativa é formada por três agentes

| Agente | Papel | Arquitetura | Detalhes |
|---|---|---|---|
| Agente Monitor | Avalia a ação do usuário como certa/errada | Reativo simples | `references/agente-monitor.md` |
| Agente ZDP | Decide a estratégia pedagógica (Zona de Desenvolvimento Proximal) | Baseado em modelo | `references/agente-zdp.md` |
| Agente Modelador | Mantém o modelo do usuário atualizado | Reativo simples | `references/agente-modelador.md` |

Leia o arquivo do agente relevante antes de trabalhar nele — cada um tem percepções, ações e granularidade própria (ex.: os protocolos de mouse são a folha de granularidade do Agente Monitor).

## Comunicação entre os agentes

Agentes colaborativos, usando concorrência cooperativa. Cada agente é operacionalizado como uma Thread produtor-consumidor. Sem protocolo de comunicação direta identificado — colaboram via dados compartilhados.

## Repositórios de dados compartilhados

- **Modelo do Usuário** — lido/escrito pelo Agente Modelador; consultado pelo Agente ZDP.
- **Ontologia do domínio** — legenda de Vergnaud (1986), invariantes verdadeiros e regras de ação verdadeiras; consultada pelo Agente Monitor.
- **Conteúdo Pedagógico** — Mensagens, Automatização de passos, Mostrar Modelo Completo, entre outras; consultado pelo Agente ZDP.

## Propriedades formais do ambiente (Russell & Norvig)

- **Estratégico**: Agente ZDP e o próprio usuário podem modificar o ambiente.
- **Completamente observável**: sensores do Monitor têm acesso total ao estado.
- **Episódico**: cada episódio = percepção + ação.
- **Dinâmico**: usuário pode agir enquanto um agente delibera.
- **Discreto**: número finito de estados. Fórmula (não tratar como constante): `nº_operações × (2 × posições_por_operação) × última_ação × presença_de_ajuda`. No exemplo do material: 3×(2×2×2)×1×2 = 48 — muda se mais elementos forem inseridos na tela.

## Questões em aberto (observações do Claude, NÃO do material de pesquisa)

Estas notas são análise feita ao documentar o material — não estão na tese, não têm validação, e não devem ser tratadas como parte da especificação. Servem só para orientar discussão futura com o usuário.

1. **Sincronização entre threads**: os agentes são threads produtor-consumidor cooperativas sem protocolo de comunicação direto, mas leem/escrevem nos mesmos repositórios compartilhados (Modelo do Usuário, Ontologia, Conteúdo Pedagógico). Sem alguma forma de sincronização, há risco de condição de corrida — especialmente porque o ambiente é classificado como Dinâmico.
2. **Validade temporal da avaliação do Monitor**: o Monitor ignora deliberadamente o que muda depois da ação avaliada. Não está claro se há garantia de que a ação avaliada ainda é válida no momento em que a avaliação termina (ex.: usuário desfaz a ação durante a avaliação).
3. **Cardinalidade de estados é por tela, não do sistema**: o cálculo de 48 estados (Fig. 5.81) foi feito para uma configuração específica de tela. Com a intenção de 3 apps/telas separados (composição, transformação, comparação), cada um provavelmente tem cardinalidade própria — não existe um número único do sistema.
4. **Lacuna na operacionalização da ZDP — parcialmente endereçada em 2026-07-22**: o material original admite que falta um mecanismo verificável (com dados quantitativos) para confirmar que a ajuda oferecida está de fato na Zona de Desenvolvimento Proximal. O relatório de pesquisa 2026 (ver `agente-zdp.md`) dá camadas de ajuda concretas (N0–N7) e regras condição→ação derivadas de 556 ações observadas — mas o próprio relatório reconhece, na sua seção de limitações, que a validação foi formativa (protótipo em papel, intervenção humana simulando o sistema), não operacional. A pergunta original ("como confirmar quantitativamente que a ajuda está na ZDP") continua sem resposta definitiva; o que mudou é que agora há uma proposta concreta de *quando* e *qual* ajuda oferecer, não apenas o princípio abstrato.
5. **Classificação "Multiagente" do ambiente nunca foi confirmada no material** — pode estar implícita, ou pode ser uma lacuna na fundamentação teórica (3 agentes + usuário sugerem que deveria estar presente ao lado de Estratégico/Observável/Episódico/Dinâmico/Discreto).

## Relação com outras skills do Gérard

- `gerard-consistencia-estado` — propagação de estado já implementada; esta skill é a camada de decisão *acima* disso, ainda não implementada.
- `gerard-scaffolding-interacao` — dona da taxonomia de tipos de scaffolding do ponto de vista do que é *oferecido* ao usuário. Esta skill (Ajuda Adaptativa) trata dos mesmos protocolos do ponto de vista do que é *percebido* pelos agentes como entrada — são ângulos complementares, propositalmente não unificados.
- `gerard-log-acao-instrumental` — dona do esquema de captura do log (Quadro 4.55) que o Agente Monitor consome como percepção. Confirmado contra o código real: `EventoLogGerard.java` já usa os seis termos de Shneiderman ("SELECIONAR", "ORIENTACAO", "CAMINHO", "POSICIONAR", "TEXTO", "QUANTIFICAR") como valores do campo Tarefa de Interação, e tem mais campos que o quadro teórico, não menos.
- `gerard-modelo-usuario` — dona do esquema das 5 dimensões do Modelo do Usuário (Quadro 5.60) que o Agente Modelador escreve e o Agente ZDP consulta. Nenhuma dessas dimensões existe implementada no código ainda (confirmado por busca em `src/`).
