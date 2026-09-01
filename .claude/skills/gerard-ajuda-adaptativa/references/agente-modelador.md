# Agente Modelador

## Estatuto arquitetural

O Agente Modelador é o único agente da arquitetura do Gérard. Sua autoridade
é a aprendizagem computacional sobre registros factuais concluídos. Ele mantém
o Modelo do Usuário, executa J48/PART e Apriori e publica regras adaptativas
explicáveis e versionadas.

O Modelador não reavalia a correção de uma ação e não escolhe diretamente uma
ajuda. A avaliação factual pertence ao objeto semanticamente rico ou à relação
estrutural que possui a regra. A escolha da ajuda pertence ao proprietário
semântico do conhecimento afetado, dentro de seu repertório local.

## Entradas

- um único registro factual por ação instrumental semanticamente constituída;
- ações neutras que interessem à aprendizagem, sem fabricar C/E;
- identificação de participante, sessão, tentativa e ação;
- tarefa de interação e regra de ação observável;
- papel, relação, categoria e demais dimensões factuais disponíveis;
- ajuda efetivamente materializada;
- atribuições analíticas humanas, somente quando acompanhadas de proveniência.

Gestos físicos permanecem no log de gestos. Eles só entram como ação
instrumental quando o protocolo correspondente estiver semanticamente
constituído.

## Processamento

1. Receber o mesmo registro factual produzido pelo proprietário semântico.
2. Bloquear duplicatas pelo `action_id`.
3. Converter o registro em caso do Modelo do Usuário sem mudar seu veredito.
4. Executar J48/PART e Apriori quando houver dados e critérios suficientes.
5. Publicar uma nova versão explicável do modelo e de suas regras.

Uma sessão usa a fotografia carregada no login. A publicação de uma versão
nova orienta sessões posteriores; o contexto da situação interativa continua
permitindo decisões pontuais durante a sessão.

## Saídas

- casos persistidos e rastreáveis;
- versão do Modelo do Usuário;
- regras adaptativas publicadas com origem, versão e condições;
- dados de auditoria da aprendizagem computacional.

## Limites

- Não produzir hipótese sobre conceito-em-ação, teorema-em-ação ou outro
  invariante operatório. Esse preenchimento pertence ao pesquisador humano.
- Não transformar preferência de perfil em regra única de decisão.
- Não possuir repertórios de scaffolding que pertencem aos objetos semânticos.
- Não depender de Swing, AWT, coordenadas ou detalhes de apresentação.

## Base empírica e teórica

As dimensões do Modelo do Usuário e os dados de tarefa são fundamentados no
material de mestrado e doutorado fornecido pela pesquisadora. Nível de tarefa,
partes/fases do conhecimento, perfil do participante, preferências de
aprendizagem e diagnóstico da tarefa são dimensões do modelo; nenhuma delas,
isoladamente, decide a intervenção.

As classes de ajuda são tratadas pelo repertório operacional definido em
`gerard-scaffolding-interacao`. O esquema do registro factual está em
`gerard-log-acao-instrumental`, e o esquema versionado do modelo está em
`gerard-modelo-usuario`.
