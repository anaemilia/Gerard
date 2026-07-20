# Tela de explicações: tarefas matemática e de interação

## Alterações

- A janela foi reorganizada em três blocos: **Tarefa matemática**, **Tarefa de interação** e **Preenchimento do pesquisador**.
- A Tarefa matemática mostra cada elemento semântico, seu papel, tooltip, justificativa e escolha opcional Fácil/Difícil.
- A explicação geral permanece disponível e é repetida em cada registro elementar salvo.
- A Tarefa de interação exibe automaticamente, por elemento, os protocolos já registrados na tentativa: SELECIONAR, POSICIONAR, ORIENTACAO, CAMINHO, QUANTIFICAR e TEXTO.
- O estilo de interação e a representação atual aparecem como informações contextuais.
- O campo Invariante operatório permanece reservado ao pesquisador.
- A fotografia da modelagem é associada a cada explicação elementar.

## Log único

O salvamento da tela não usa mais o repositório paralelo no fluxo da interface. Cada elemento gera uma linha no mesmo arquivo `gerard_interacao_<sessao>.tsv`, com:

- tentativa atual;
- tarefa `TAREFA_MATEMATICA_EXPLICACAO`;
- elemento;
- papel semântico;
- justificativa específica;
- Fácil/Difícil;
- explicação geral repetida;
- invariante operatório repetido;
- fotografia da modelagem.

A menor granularidade é preservada: uma linha qualitativa por elemento semântico da tentativa.

## Preservação funcional

- A janela não restaura, não preenche e não reposiciona diagramas.
- A tentativa atual é preservada.
- Os quatro idiomas continuam ativos.
- O log automático de interação permanece compatível com o cabeçalho existente.
- O resumo da tarefa de interação é somente leitura.

## Verificação

- Compilação Ant: `BUILD SUCCESSFUL`.
- 96 arquivos Java compilados.
- Permanecem somente os quatro avisos conhecidos de `source/target 8`.
