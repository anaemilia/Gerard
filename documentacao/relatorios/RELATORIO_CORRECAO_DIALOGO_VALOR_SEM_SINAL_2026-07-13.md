# Correção do diálogo de substituição da interrogação

## Regra aplicada

O diálogo de entrada solicita somente o valor inteiro sem sinal. Quando o papel semântico admite sinal, a escolha positiva ou negativa continua sendo feita exclusivamente pelo menu de radiobutton do scaffolding já existente.

## Alterações

- Português: removida a menção a `+4` e `-4`; exemplo alterado para `14`.
- Inglês: removida a solicitação de valor com sinal.
- Francês: removida a solicitação de valor com sinal.
- Espanhol: adicionadas mensagens próprias sem menção a sinal.
- A validação do diálogo passou de `[+-]?[0-9]+` para `[0-9]+`.
- Os fluxos existentes de escolha de sinal por radiobutton não foram alterados.
- As demais edições numéricas dos diagramas não foram modificadas.

## Verificação

- `ant clean jar`: BUILD SUCCESSFUL.
- 96 arquivos Java compilados.
- Permanecem somente os quatro avisos conhecidos da configuração Java 8.
