# Agente Modelador

⚠️ Proposta teórica — ver aviso de status em `../SKILL.md`.

**Fontes**: material original + relatório de pesquisa "Análise de situações
interativas no Gerard..." (Queiroz, 2026, Univasf — fornecido pelo usuário em
2026-07-22). O relatório não muda o papel do Modelador, mas dá conteúdo
empírico concreto para os campos que a ação 2 (inferência de regras) precisa
consumir — ver "Entrada empírica para a ação 2" abaixo.

## Papel

Mantém o modelo do usuário atualizado.

## Arquitetura: Agente Reativo Simples

Não precisa tratar a mesma imprevisibilidade do Agente ZDP — só recebe, armazena e periodicamente modifica o modelo do usuário.

## Percepções

- **Estratégia Pedagógica** (recebida do Agente ZDP), derivada de percepção + modelo do usuário + conteúdo pedagógico disponível.
- **Identificação do usuário** — armazenada junto com o novo caso.

## Ações

1. Armazena o novo caso recebido na base de dados "Modelo do Usuário" — ver `gerard-modelo-usuario` para o esquema completo das 5 dimensões armazenadas (nível de tarefas, partes do conhecimento e fases, perfil do aluno, perfil da aprendizagem, diagnóstico da tarefa). Este arquivo não repete esse esquema — foca no que o Modelador *faz* com os dados, não em como eles são estruturados.
2. Periodicamente, roda os algoritmos de aprendizagem de máquina para inferir regras a partir dos novos casos:
   - **J48.PART** — indução de regras baseada em árvore de decisão.
   - **APRIORI** — mineração de regras de associação.
   - Combinados via AND.
   - Entrada principal para essas regras: a dimensão "Diagnóstico da tarefa" (Tarefa, Suporte, Internalização, Probabilidade de saber o conteúdo) — é dela que vêm as ações fundamentais geradas por cada usuário, base do aprendizado.
3. Gera o "Modelo do Usuário modificado".

Objetivo: manter o modelo do usuário atualizado.

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
  batem com o enum existente. As camadas N0–N7 do Agente ZDP (ver
  `agente-zdp.md`) são uma escala mais fina que pode informar como mapear
  cada nível de ZDP para um valor de `NivelSuporte`.
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

1. **`regraDeAcao` — implementado em 2026-07-22.** `DiagnosticoTarefa` ganhou
   o campo `regraDeAcao` (Tarefa de Interação de Shneiderman:
   POSICIONAR/SELECIONAR, mesmo vocabulário do log). `ConectorVereditoModelador`
   agora tem dois métodos: `registrarVeredito` (ações avaliáveis, POSICIONAR)
   e `registrarAcaoNeutra` (ações sem certo/errado, como SELECIONAR — sempre
   `suporte=NENHUM`, não passa pela camada do ZDP). Os dois pontos do código
   que chegam ao Modelador (posicionamento no diagrama e seleção de texto no
   enunciado) já preenchem esse campo.
2. **`invariante` — ainda de fora, por falta de quem o calcule.** Mesmo
   sendo "Tarefa" uma referência à Ação Instrumental completa (que inclui
   Invariantes no esquema do log), nada no código hoje decide qual
   invariante uma ação mobiliza — nem os campos `invariante_*` de
   `EventoLogGerard` são preenchidos em lugar nenhum, mesmo existindo na
   classe. Antes de adicionar esse campo ao Modelo do Usuário, alguém
   precisa decidir *quem* calcula esse valor (candidato natural: Agente
   Monitor, comparando a ação contra a ontologia de invariantes
   verdadeiros — ver `agente-monitor.md`) e como mapear papel→código de
   invariante (Tabela 1 do relatório 2026). Decisão explicitamente adiada
   pelo usuário em 2026-07-22.

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
