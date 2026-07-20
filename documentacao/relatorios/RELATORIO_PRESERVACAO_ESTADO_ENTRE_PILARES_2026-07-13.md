# Relatório de preservação de estado entre pilares

Data: 13/07/2026

## Regra aplicada

Somente duas ações podem restaurar a modelagem dos diagramas:

1. Sortear outra situação-problema.
2. Restaurar explicitamente a atividade pelo botão de restauração do diagrama.

As seguintes ações preservam o estado atual dos diagramas:

- troca do idioma da interface;
- troca do idioma da situação-problema;
- abertura, edição e fechamento da curadoria;
- troca do tipo de representação/legenda;
- atualização da exibição textual.

## Alterações

- O menu de tipo/representação passou a utilizar o fluxo de atualização que preserva a modelagem.
- O retorno da curadoria deixou de recorrer ao fluxo de nova situação quando a versão atual não é reencontrada.
- Na ausência de uma versão textual correspondente, somente a camada textual é atualizada; listas, posições e conteúdos dos diagramas são mantidos.
- O método que restaura toda a modelagem ficou documentado como operação exclusiva do botão Restaurar.
- O método que carrega uma nova situação e limpa a tela ficou documentado como exclusivo da inicialização e do botão Sortear.

## Verificações estáticas

- `aplicarIdiomaSelecionado()` é chamado apenas na inicialização e no botão Sortear.
- `restaurarModelagemDiagrama()` é chamado apenas pelo botão Restaurar do diagrama.
- O fluxo de troca de idioma não limpa `itensArrastaveis`, `elementosVergnaud`, `conectoresVergnaud`, `circulosVenn` ou `quadradinhosVenn`.
- O fluxo de retorno da curadoria não limpa essas estruturas.
- A inicialização dos diagramas compostos continua sem preenchimento automático.

## Compilação

Executado `ant clean jar` com resultado `BUILD SUCCESSFUL`.
