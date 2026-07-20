# C34 — Distribuicao Windows com instalacao automatica

## Demanda

Preparar o Gerard para uso por pessoas sem conhecimento tecnico, reduzindo a
instalacao a um duplo clique e evitando a instalacao manual do Java.

## Decisao de projeto

Foi criado um instalador por usuario em arquivo `Instalar_Gerard.bat`. Ele incorpora
o `Gerard.jar` e o icone da aplicacao, baixa uma execucao privada do Eclipse Temurin
JRE 21, verifica a integridade do pacote Java quando o servico fornece o SHA-256,
instala os arquivos em `%LOCALAPPDATA%\Gerard`, cria atalhos na Area de Trabalho e
no menu Iniciar, registra a desinstalacao e inicia o sistema com `javaw.exe`.

O instalador tenta acrescentar JavaFX 21 para as visualizacoes D3 internas. A falha
dessa etapa nao impede o uso do sistema, pois o Gerard preserva o mecanismo de
abertura das visualizacoes no navegador.

## Intervencao do pesquisador

A decisao explicita do pesquisador foi priorizar uma experiencia de distribuicao
adequada ao usuario leigo: um unico ponto de entrada, sem comandos, sem configuracao
manual do JDK e com icone criado automaticamente. Esta intervencao passa a integrar
a historia de co-design do artefato, pois altera as condicoes materiais de acesso e
uso do sistema, embora nao modifique a semantica das representacoes de Vergnaud.

## Artefatos

- `distribuicao_windows/Instalar_Gerard.bat` — instalador autocontido de um clique;
- `distribuicao_windows/GERAR_EXE_COM_JPACKAGE.bat` — construcao opcional de EXE em Windows;
- `distribuicao_windows/Gerard.ico` — icone dos atalhos;
- `distribuicao_windows/LEIA-ME_INSTALACAO_WINDOWS.txt` — orientacoes e limitacoes;
- `distribuicao_windows/SHA256SUMS.txt` — hashes dos arquivos de distribuicao.

## Integridade do aplicativo incorporado

`Gerard.jar`: `70AFD37FB09B10806314F2CBBE11F32DE84E04D25B8F14D683C6B2B6DECDCEFE`.

## Limite tecnico documentado

Um instalador `.exe` nativo do Windows nao pode ser produzido com `jpackage` em um
ambiente Linux, pois o empacotamento e especifico da plataforma. O projeto inclui o
script de construcao para ser executado em Windows. Para entrega imediata ao usuario
final, o arquivo `.bat` realiza a instalacao automatica por usuario.
