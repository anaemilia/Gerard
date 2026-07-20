# Relatório de alteração — inicialização sem texto e sem diagramas

**Data:** 2026-07-16  
**Ciclo:** C93  
**Tipo de intervenção:** fluxo inicial da atividade.

## Regra implementada
O Gérard passa a iniciar somente com o cabeçalho e os controles gerais da interface. Nenhuma situação-problema, diagrama de Vergnaud ou diagrama complementar é renderizado antes da escolha explícita de uma categoria pelo usuário.

## Comportamento
- ao abrir o sistema, a área de trabalho permanece vazia;
- o botão **Sortear outra situação** permanece desabilitado enquanto nenhuma categoria tiver sido escolhida;
- a troca do idioma da interface não carrega automaticamente uma situação-problema;
- ao selecionar uma categoria, o sistema carrega o enunciado curado e inicializa os diagramas correspondentes;
- após a primeira escolha, **Sortear outra situação** passa a operar dentro da categoria selecionada;
- controles contextuais de texto, curadoria, idioma da situação, ajuda e restauração permanecem ocultos até existir uma atividade carregada.

## Consistência
A categoria padrão interna foi preservada apenas para compatibilidade com rotinas existentes, mas não é exibida nem usada para carregar conteúdo na inicialização.

## Verificações executadas
- compilação `ant clean jar`: aprovada;
- teste em interface Swing virtual: tela inicial sem texto, sem elementos de Vergnaud e sem diagrama complementar;
- troca do idioma da interface antes da categoria: não carrega conteúdo;
- seleção de **Composição de medidas**: enunciado, diagrama de Vergnaud e diagrama complementar carregados;
- regressão estrutural geral, ajuda contextual e feedback multissensorial: aprovados.
