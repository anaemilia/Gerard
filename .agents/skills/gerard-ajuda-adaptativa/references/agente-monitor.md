# Agente Monitor — referência histórica supersedida

> **Decisão da usuária em 2026-08-14:** o Agente Monitor foi retirado da
> arquitetura-alvo. O resultado factual de validação, incluindo C/E quando
> aplicável, e o log da ação são produzidos e possuídos pelo objeto semântico
> ou pela relação estrutural que possui a regra. O log do gesto pertence ao
> objeto rico da representação envolvido. A infraestrutura apenas transporta
> e persiste esses registros e os disponibiliza ao Agente Modelador. O
> conteúdo abaixo preserva o
> desenho anterior e orienta somente a migração do código legado.

> Uma ação mantém um único `action_id`. Se vários objetos semânticos
> participarem, o menor proprietário relacional ou agregado registra a ação
> uma vez e referencia os participantes; não se cria um log por objeto.

`AgenteMonitor.java` ainda calcula ou delega parte dos vereditos. Essa é uma
responsabilidade legada a migrar um fluxo por vez. Os trechos históricos
abaixo descrevem a arquitetura anterior. Gestos sem comando semântico não
entram no fluxo factual; ver `gerard-log-gestos-interacao`.

⚠️ Proposta teórica — ver aviso de status em `../SKILL.md`.

## Papel histórico supersedido

Avalia a ação instrumental do usuário como certa ou errada, contra a Ontologia do domínio, e monta a percepção para o Agente ZDP.

## Arquitetura: Agente Reativo Simples

O ambiente pode ser modificado enquanto o Monitor delibera, mas isso é **irrelevante** para ele — só precisa avaliar a ação já ocorrida. Por isso não precisa de estado interno complexo: Sensores → "Qual o estado atual?" → Regras condição-ação → "O que devo fazer agora" → Atuadores.

O Monitor possui apenas: o estado atual do mundo, as regras condição-ação (ontologia do domínio), e o módulo que verifica qual ação deve ser executada.

## Percepções

O Monitor recebe como percepção a **ação instrumental já registrada** — ver `gerard-log-acao-instrumental` para o esquema completo de captura (Usuário, Problema, Tentativa, Tarefa, Tarefa de Interação, Instrumento, Artefato, Representação, Invariantes, Regras). Este arquivo não repete esse esquema — foca no que o Monitor *faz* com a percepção, não em como ela é capturada.

O campo **Tarefa de Interação** do log registra qual dos seis protocolos de mouse de Shneiderman (1998) foi usado — são os canais de entrada que o Monitor reconhece como percepção:

- **Selecionar** — um item é escolhido a partir de um conjunto de itens.
- **Posicionar** — o mouse é posicionado em um ponto em um espaço de uma ou mais dimensões.
- **Orientar** — um ponto é escolhido em um espaço de duas ou mais dimensões.
- **Quantificar** — um valor numérico é especificado.
- **Caminho** — tarefas de posicionar e orientar são realizadas sequencialmente.
- **Texto** — textos em um espaço de duas dimensões são modificados, movidos e editados.

> **Nota de fronteira**: `gerard-scaffolding-interacao` também descreve protocolos de mouse (arrastar, proximidade, atração magnética), mas com um ângulo diferente — é o que é **oferecido ao usuário** como interação, não o que o Agente Monitor **recebe como entrada** para avaliar. Textos propositalmente distintos; não unificar.

Exemplo do material (tarefa "identificar os cardinais do conjunto", usando os protocolos de Shneiderman): usuário usa Selecionar sobre o número identificado, depois Caminho (posicionar + orientar sequencialmente) para levá-lo até o modelo; repete até inserir o resultado via Quantificar.

## Ações

1. Recebe a ação instrumental já logada (ver `gerard-log-acao-instrumental`).
2. Compara a ação com as ações corretamente descritas na Ontologia do domínio (invariantes verdadeiros + regras de ação verdadeiras, legenda de Vergnaud).
3. Preenche o veredito de avaliação (certo/errado — campo C/E do log).
4. Monta a "Ação Instrumental Avaliada" e repassa ao Agente ZDP.

Objetivo: monitorar as ações na interface e produzir o veredito de avaliação — não é responsável por como a ação é capturada, só por como ela é julgada.
