---
name: gerard-modelo-usuario
description: Esquema das dimensões do Modelo do Usuário do Gérard (Quadro 5.60 do material de pesquisa) — o que é armazenado sobre cada usuário e usado pelo Agente Modelador (J48.PART + APRIORI) para aprender regras. Use ao criar, revisar ou estender qualquer estrutura de dados relacionada ao perfil/modelo do usuário no Gérard. Esta skill é dona do esquema; NÃO decide como as regras são inferidas (ver gerard-ajuda-adaptativa/references/agente-modelador.md) nem como a estratégia pedagógica é escolhida (ver agente-zdp.md).
---

# Modelo do Usuário — Gérard

## ⚠️ Status: proposta teórica, do material de pesquisa (Quadro 5.60)

Não presumir que todos esses campos já existem no código atual. Confirmado em 2026-07-20: uma busca por `ModeloUsuario`/`ModeloDoUsuario` e termos relacionados não encontrou nenhum código correspondente em `src/` — nenhuma das cinco dimensões abaixo está implementada ainda. Antes de estender qualquer estrutura de dados do usuário, comparar com o que já existe e reportar o que falta.

Algumas colunas do quadro citam sistemas de referência externos ("Ecolab", "Cenários AnimalWatch") como fonte/inspiração teórica — não são componentes do Gérard nem precisam ser implementados; servem só de proveniência acadêmica de onde a ideia daquele campo veio.

## Dimensões do Modelo do Usuário

### 1. Nível de tarefas (complexidade)

Tarefas variam de mais fáceis a mais complexas, dentro das categorias de estruturas aditivas usadas no Gérard (composição, transformação, comparação). Referência: organização de tarefas por grau de complexidade (Magina et al., 2000).

### 2. Partes do conhecimento e fases

Em qual categoria de estruturas aditivas o usuário tem maior domínio, e qual ele domina menos.

### 3. Perfil do aluno

- Nome, id, idade, sexo.
- Identificam unicamente o usuário; podem ser usados para mensagens privativas (ex.: usando o nome na mensagem).

### 4. Perfil da aprendizagem

- **Mídia preferida**: som, gráfico, ou linguagem natural.
- **Nível de escolaridade**: 1º grau, 2º grau, graduação, pós-graduação.
- Uso: orienta a construção de mensagens e o estilo de interação — usuários com maior grau de instrução podem receber mensagens mais abstratas; usuários com menor grau de instrução podem receber mensagens mais contextualizadas com o problema em resolução.

### 5. Diagnóstico da tarefa

- **Tarefa** (Ação Instrumental) — ver `gerard-log-acao-instrumental` para o esquema completo dessa referência; não duplicar aqui.
- **Suporte**: nenhum | parcial | total — o suporte recebido pelo usuário anteriormente ao tentar realizar a mesma tarefa. Objetivo: diminuir essa ajuda ao longo do tempo.
- **Internalização**: 0 ou 1 — quando o valor é 1, a ajuda para aquele conteúdo é removida totalmente.
- **Probabilidade de saber o conteúdo** — obtida via teorema de Bayes. Dependendo dessa probabilidade, o atributo Internalização vai para 1 ou 0.
- **Dificuldade autorrelatada, explicação do elemento e explicação geral** (extensão fora do Quadro 5.60, decidida com o usuário em 2026-07-23): campos escritos pelo próprio usuário no Artefato Explicativo (`TelaArtefatoExplicativo`) — dificuldade (fácil/intermediária/difícil) e justificativa por elemento, mais uma explicação geral da estratégia da tentativa. É o análogo escrito da entrevista pós-tarefa usada na tese de Queiroz (2012), que motivou a extensão. `AgenteModelador.registrarExplicacaoNoUltimoDiagnostico` complementa, com esses campos, o caso mais recente já armazenado para a mesma tarefa — não cria um caso novo. Opcional (o artefato é preenchido depois da ação, nem toda ação tem autorrelato) e a explicação geral se repete em todos os casos da mesma tentativa (replicação aceita por ora, não normalizada).
- **Nível conceitual estimado e curado** (extensão decidida com o usuário em 2026-07-23, a partir de Vergnaud 1998 — "The role of language and symbols in representation", seção 3): `nivelConceitualEstimado` é um palpite automático (`AnalisadorNivelConceitual`, casamento de padrão sobre `explicacaoElemento`) — sinal fraco, nunca usar direto em inferência de regras. `nivelConceitualCurado` é nulo até um pesquisador confirmar/corrigir na aba "Curadoria - Nível Conceitual" da Visão de Pesquisador; só esse campo deve alimentar `InferenciaRegrasModelador`.
- **Invariante operatório** (`invarianteOrigem`/`invarianteCodigo`/`invarianteSimbolico`/`invarianteObservacao`, extensão decidida com o usuário em 2026-07-23): ao contrário do nível conceitual, **não** é palpite automático — é atribuído diretamente pelo pesquisador no Artefato Explicativo, escolhendo de um catálogo fechado (ex.: "Todo = Parte₁ ∪ Parte₂") ou registrando uma forma simbólica nova. Por ser decisão humana direta, entra sem estágio de curadoria como insumo de `InferenciaRegrasModelador` (atributo `invarianteCodigo`, junto de tarefa/regraDeAcao/suporte) — não confundir com o campo teórico "invariante" que a tese original também menciona: nenhum agente calcula automaticamente qual invariante uma ação mobiliza, isso continua de fora de propósito.

## Relação com os agentes

- O **Agente Modelador** escreve/atualiza este modelo (ver `gerard-ajuda-adaptativa/references/agente-modelador.md`), usando J48.PART + APRIORI para inferir novas regras a partir dos casos acumulados aqui.
- O **Agente ZDP** consulta este modelo para decidir a estratégia pedagógica (ver `agente-zdp.md`).

Este arquivo não decide como as regras são inferidas nem como a estratégia é escolhida — só documenta o que é armazenado.
