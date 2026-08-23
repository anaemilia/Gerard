# Agente Modelador

Este documento combina a responsabilidade vigente do único agente da
arquitetura-alvo com registros históricos explicitamente identificados. Ver o
aviso de status em `../SKILL.md`.

**Fontes**: material original + relatório de pesquisa "Análise de situações
interativas no Gerard..." (Queiroz, 2026, Univasf — fornecido pelo usuário em
2026-07-22). O relatório não muda o papel do Modelador, mas dá conteúdo
empírico concreto para os campos que a ação 2 (inferência de regras) precisa
consumir — ver "Entrada empírica para a ação 2" abaixo.

## Papel

Transforma registros factuais em casos, aprende regras explicáveis e publica
versões do Modelo do Usuário.

Decisão vigente da usuária em 2026-08-11: o Modelador concentra a
"inteligência" de aprendizagem. Ele não escolhe diretamente a ajuda na
interface. Cada proprietário semântico aplica as regras publicadas ao próprio
repertório local.

As menções aos agentes Monitor e ZDP mantidas nas seções históricas abaixo
registram o desenho anterior e o estado do código; não lhes atribuem
responsabilidade na arquitetura-alvo.

## Arquitetura: Agente Reativo Simples

Recebe fatos concluídos, armazena casos e periodicamente publica uma nova
versão do modelo. A publicação não altera a fotografia já carregada por uma
sessão em andamento.

## Percepções

- **Ações instrumentais e interações factualmente registradas**, incluindo
  C/E quando aplicável, diagnósticos, apoios decididos e apoios efetivamente
  exibidos.
- **Identificação do usuário** — armazenada junto com o novo caso.
- **Atribuições analíticas do pesquisador**, quando existirem, preservando a
  autoria humana e sem fabricá-las a partir de C/E.

## Ações

1. Normaliza e armazena o novo caso na base de dados "Modelo do Usuário" —
   ver `gerard-modelo-usuario` para o esquema completo das dimensões.
2. Periodicamente, roda os algoritmos de aprendizagem de máquina para inferir regras a partir dos novos casos:
   - **J48.PART** — indução de regras baseada em árvore de decisão.
   - **APRIORI** — mineração de regras de associação.
   - Combinados via AND.
   - Entrada principal para essas regras: a dimensão "Diagnóstico da tarefa" (Tarefa, Suporte, Internalização, Probabilidade de saber o conteúdo) — é dela que vêm as ações fundamentais geradas por cada usuário, base do aprendizado.
3. Publica uma nova versão do Modelo do Usuário, com proveniência das regras,
   algoritmo, data e métricas disponíveis.
4. Torna a versão elegível para o próximo login. Não altera o contexto de
   sessões já iniciadas.

### Fronteira operacional de publicação — implementada na P2.4A.1

`RepositorioRegrasInferidas` e
`RepositorioRegrasAdaptativasPublicadas` possuem autoridades diferentes. O
primeiro conserva as saídas textuais de PART/Apriori como experimentais. O
segundo aceita somente `RegraAdaptativaPublicada` já explicitada, com usuário,
proprietário semântico, escopo, condições, código de ajuda, versão,
proveniência, algoritmo, data, métricas disponíveis e estado `PUBLICADA`.

`AgenteModelador.publicarRegrasAdaptativas` é o único ato de escrita
operacional adicionado nesta fase. Ele não converte o texto do Weka nem decide
como um padrão estatístico corresponde a um scaffolding; essa correspondência
precisa chegar validada. O catálogo é substituído atomicamente por usuário e é
lido por `SessaoAdaptativaUsuario` somente no login. A fotografia já ativa não
é modificada por uma publicação posterior.

Objetivo: manter e publicar um modelo explicável, reproduzível e consultável
por projeções locais de leitura.

## Entrada empírica para a ação 2 (relatório 2026)

O relatório (Tabela 37, "Informações utilizadas para modelagem do
comportamento do agente") confirma e detalha as mesmas cinco dimensões já
presentes em `ModeloUsuario`/`DiagnosticoTarefa` no código atual — não propõe
dimensões novas, mas dá o dado empírico que faltava para decidir *como*
`suporte`, `internalizado` e `probabilidadeSaberConteudo` deveriam ser
preenchidos quando a ação 2 for implementada:

- **`NivelSuporte`** — o relatório usa três níveis observados (ajuda parcial:
  questionamento/dica/explicação; ajuda total: automatização/modelo
  completo/material concreto — ver `gerard-scaffolding-interacao`), que já
  batem com o enum existente. As camadas N0–N7 preservadas na referência
  histórica `agente-zdp.md` são evidência anterior sobre gradações de ajuda;
  não atribuem decisão a um Agente ZDP na arquitetura-alvo.
- **`internalizado`** — o relatório não usa esse termo, mas a "escala
  interpretativa dos indícios de reorganização da ação após ajuda" (Tabela 51
  do relatório, reproduzida em `agente-zdp.md`) é o candidato mais direto para
  alimentar esse campo: "estabilização após retirada de apoio" é o caso que
  mais se aproxima de `internalizado = true`; "manutenção do erro" e
  "repetição do erro" são o oposto.
- **`probabilidadeSaberConteudo`** — o relatório reafirma o cálculo por
  teorema de Bayes (igual ao material original), mas não fornece a fórmula
  operacional nem os priors — continua sendo trabalho de design em aberto,
  não algo que o relatório resolve.

Isso **não** muda a decisão já tomada de não implementar a ação 2 sem
confirmação explícita sobre a dependência de ML (Weka) — só documenta que,
quando essa confirmação vier, há agora material empírico mais rico para
calibrar as regras J48.PART/APRIORI do que havia antes.

## Regras com força ajustável — proposta histórica não vigente

⚠️ Esta proposta de 2026-07-25 antecede a retirada do Agente ZDP. O desenho
completo foi preservado em `agente-zdp.md` somente como histórico. Ele
atribuía ao ZDP o fortalecimento ou enfraquecimento de regras conforme indícios
de reorganização pós-ajuda. Essa atribuição não vale para a arquitetura-alvo.
Qualquer retomada exigirá nova decisão explícita, fundamentação e localização
entre a aprendizagem/publicação do Modelador e a seleção do proprietário
semântico; não deve ser implementada por transposição automática do legado.

Ainda não decidido: se esse mecanismo é o mesmo que calcularia
`probabilidadeSaberConteudo`/`internalizado` (ver "Entrada empírica para a
ação 2" acima, que já aponta a mesma Tabela 51/52 como candidata a
`internalizado`), ou um mecanismo separado que precisaria ser desenhado em
conjunto. Também não decidido onde a força ficaria armazenada, nem se o
mecanismo se aplica às regras do PART além das do Apriori (o Apriori já tem
suporte/confiança/lift nativos por regra; o PART não expõe o mesmo tipo de
métrica por regra na API do Weka).

## Correção: o que o Apriori deve associar (fonte: trecho original da tese, 2026-07-22)

O usuário colou um trecho do texto original (citando Rosatteli e Tedesco,
2003) que esclarece o desenho pretendido do PART e do Apriori — e corrige uma
suposição errada da primeira implementação (2026-07-22, ver histórico):

> "(1) O J48.PART é uma versão mais recente do algoritmo de árvore de decisão
> C4.5 (Quinlan, 1993). Seus resultados são regras utilizadas para aprender o
> atributo identificado como atributo classe. (2) No processo de extração de
> regras de associação estamos interessados em predizer qualquer atributo e
> não apenas o atributo classe... Queremos observar a regularidade de
> associações entre a mobilização de regras sobre o uso da interface e os
> invariantes que emergiram no teste de usabilidade."

Ou seja:
- **PART** tem um atributo-classe fixo (correto manter `suporte` como classe,
  como já implementado — é consistente com o texto).
- **Apriori** não deveria ter classe fixa nenhuma — deve minerar associações
  livres entre **regra de ação** (como o usuário usou o artefato — ver
  `Instrumento`/`Regra` na Tabela 10/22 do relatório 2026 e no esquema de log
  em `gerard-log-acao-instrumental`) e **invariante operatório** (o
  teorema-em-ato verdadeiro ou falso mobilizado — ver Tabela 1 do relatório
  2026 e `T.e.A.` na Tabela 22). **Não** é a mesma coisa que `tarefa ×
  suporte`.

**Decisão do usuário em 2026-07-22**: "o modelo do usuário é a fonte da
verdade para o agente modelador" — ou seja, estender o esquema de
`ModeloUsuario`/`DiagnosticoTarefa` diretamente (não desviar para ler o log
de ação instrumental direto). Consequência prática, em duas partes:

1. **`regraDeAcao` — registro histórico da implementação de 2026-07-22.** `DiagnosticoTarefa` ganhou
   o campo `regraDeAcao` (Tarefa de Interação de Shneiderman:
   POSICIONAR/SELECIONAR, mesmo vocabulário do log). `ConectorVereditoModelador`
   agora tem dois métodos: `registrarVeredito` (ações avaliáveis, POSICIONAR)
   e `registrarAcaoNeutra` (ações sem certo/errado, como SELECIONAR — sempre
   `suporte=NENHUM`). Na arquitetura daquela versão, o fluxo atravessava uma
   camada ZDP; essa passagem é legado, não responsabilidade vigente. Os dois pontos do código
   que chegam ao Modelador (posicionamento no diagrama e seleção de texto no
   enunciado) já preenchem esse campo.
2. **`invariante` — atribuição exclusiva do pesquisador humano.** Mesmo
   sendo "Tarefa" uma referência à Ação Instrumental completa (que inclui
   Invariantes no esquema do log), nada no código hoje decide qual
   invariante uma ação mobiliza — nem os campos `invariante_*` de
   `EventoLogGerard` são preenchidos em lugar nenhum, mesmo existindo na
   classe. A hipótese antiga de atribuí-lo ao Monitor foi supersedida: somente
   o pesquisador humano pode preencher esse campo, com protocolo teórico e
   evidências explícitas. O Modelador pode consumir a atribuição humana com
   sua proveniência, mas nenhum agente a calcula a partir de C/E.

A versão atual de `InferenciaRegrasModelador` mina `tarefa × regraDeAcao ×
suporte` no Apriori (livre, sem classe fixa) e `suporte` como classe do
PART — mais próxima do desenho da tese do que a versão anterior
(`tarefa × suporte` só), mas ainda não é a associação regra-de-ação ×
invariante que o trecho da tese descreve.

## Por que J48.PART + APRIORI, e não algoritmos mais recentes

J48 (C4.5), PART e Apriori são de fins dos anos 90/1994 — mais antigos que alternativas como Random Forest, Gradient Boosting ou FP-Growth. **Não trocar por essas alternativas sem confirmação explícita do usuário.** O motivo não é desempenho bruto, é **explicabilidade**:

- J48/PART geram regras "se-então" legíveis e rastreáveis até os casos originais — cada regra carrega sua própria justificativa.
- Apriori entrega métricas explícitas por regra (suporte, confiança, lift), mostrando exatamente a força da relação entre elementos.
- Modelos como Random Forest/Gradient Boosting são caixas-pretas: qualquer explicação (ex. via SHAP) é uma reconstrução aproximada do que o modelo fez, não a lógica real de decisão.

Para o pesquisador, o que importa não é só a acurácia estatística das regras, mas a **significância pedagógica** de cada uma — entender por que ela foi gerada e sua relação com outros elementos associados. Regra explícita > reconstrução aproximada, mesmo que estatisticamente "melhor".
