# Fotografia visual do estado da modelagem

A tela **Análise da modelagem** passou a exibir uma fotografia visual do diagrama de Vergnaud no momento em que a janela é aberta.

## Regras preservadas

- A fotografia é somente leitura.
- Abrir a tela não altera, restaura ou preenche o diagrama.
- A fotografia textual usada no log permanece inalterada.
- O registro continua vinculado ao `tentativa_id` atual.
- A captura mostra o estado efetivamente construído pelo usuário antes do preenchimento das explicações.
- A seção foi internacionalizada em português, inglês, francês e espanhol.

## Validação

- `ant clean jar`: BUILD SUCCESSFUL.
- 96 arquivos Java compilados.
- Permanecem somente os quatro avisos conhecidos da configuração Java 8.
