---
name: gerard-ajuda-adaptativa
description: Arquitetura da ajuda adaptativa do Gérard: o Agente Modelador aprende e publica o Modelo do Usuário; cada objeto semanticamente rico avalia fatos e seleciona ajudas no próprio repertório. Use ao alterar avaliação C/E, J48/PART, Apriori, regras publicadas, Modelo do Usuário ou scaffolding adaptativo.
---

# Ajuda adaptativa do Gérard

## Arquitetura vigente

Há um único agente: o **Agente Modelador**.

- O proprietário semântico avalia a ação em seu próprio escopo.
- O proprietário produz um único RegistroFactualAcaoInstrumental.
- A infraestrutura persiste esse mesmo registro e o encaminha ao Modelador.
- O Modelador aprende com J48/PART e Apriori, atualiza o Modelo do Usuário e publica regras explicáveis.
- O proprietário semântico consulta somente a projeção necessária do Modelo do Usuário e seleciona uma ajuda dentro de seu repertório local.
- A interface fornece fatos observáveis e materializa a decisão; ela não recalcula o veredito nem escolhe a ajuda.
- A análise do pesquisador consome fatos e não produz diagnósticos psicológicos automaticamente.

Não introduza um agente intermediário para avaliar ações, contar erros, escolher níveis de ajuda ou coordenar os objetos ricos. Contagens e sequências pertencem ao agregado semântico que possui a tentativa; seleção de ajuda pertence ao proprietário do repertório.

## Modelo do Usuário

A sessão carrega uma fotografia versionada no login. Essa fotografia permanece imutável durante a sessão. As ações da sessão corrente fornecem contexto situacional local, mas não reescrevem silenciosamente a fotografia carregada.

Perfis são preferências integrantes do Modelo do Usuário; não constituem sozinhos o critério de decisão. A decisão contextual combina:

1. conhecimento e estado do proprietário semântico;
2. projeção pertinente do Modelo do Usuário;
3. fatos da tentativa ou situação interativa corrente;
4. regras explicáveis publicadas pelo Modelador.

## Invariantes de implementação

- Uma ação semanticamente constituída possui um action_id, mesmo quando envolve vários objetos.
- Gesto físico e ação instrumental permanecem em logs separados.
- Soltar fora de qualquer elemento encerra um gesto, mas não constitui automaticamente uma ação instrumental.
- Reavaliações internas de consistência não criam outra ação nem outro caso no Modelo do Usuário.
- O suporte aplicado deve ser registrado como fato, com o mesmo action_id e, quando houver, rejection_sequence_id.
- O repertório operacional atual de scaffolding é definido por gerard-scaffolding-interacao; não o duplique aqui.

## Consulta obrigatória

Confronte esta skill, conforme o escopo da alteração, com:

- gerard-domain-model-first;
- gerard-knowledge-locality-principle;
- gerard-knowledge-oriented-domain-objects;
- gerard-modelo-usuario;
- gerard-log-acao-instrumental;
- gerard-log-gestos-interacao;
- gerard-semantic-event-logging;
- gerard-scaffolding-interacao;
- gerard-handlers-de-interacao;
- gerard-consistencia-estado.

Para alterar aprendizagem, publicação de regras ou persistência do Modelo do Usuário, leia integralmente [references/agente-modelador.md](references/agente-modelador.md).

## Verificação mínima

Ao concluir uma alteração:

1. confirme que o objeto ou agregado proprietário produz o veredito factual;
2. confirme que log e Modelador recebem a mesma instância do registro;
3. confirme que a interface apenas compõe contexto e materializa resultados;
4. confirme que reavaliações não incrementam sequências nem armazenam casos;
5. compile a árvore completa e execute a regressão do projeto.
