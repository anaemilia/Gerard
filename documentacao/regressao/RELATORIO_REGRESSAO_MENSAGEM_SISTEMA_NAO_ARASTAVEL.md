# Relatório de regressão — mensagem de sistema não arrastável

## Alteração realizada

A mensagem exibida quando não há situação-problema curada disponível continua aparecendo na tela principal, mas passou a ser tratada somente como mensagem de sistema estática.

Ela não é mais selecionável, arrastável nem destacada como elemento móvel do enunciado.

## Arquivos alterados

- `src/Main.java`
- `scripts/verificar_regressao_gerard.py`

## Ajustes técnicos

- `encontrarElementoTextoMovel(int x, int y)` agora retorna `null` quando `textoProblemaEhMensagemSistema` é verdadeiro.
- `desenharElementoTextoMovel(...)` não aplica realce de foco/seleção quando o texto exibido é mensagem de sistema.
- O procedimento de regressão passou a verificar que mensagens de ausência não criam alvo de clique/arraste no enunciado.

## Verificação executada

Comando executado:

```bash
bash scripts/verificar_regressao_gerard.sh
```

Resultado:

- `BUILD SUCCESSFUL`
- 118 verificações OK
- 0 erros críticos
- 1 aviso conhecido: diferença na quantidade de situações-problema por idioma no TSV empacotado.

## Funcionalidades verificadas

- Compilação do projeto.
- Internacionalização.
- Exibição somente de situações curadas.
- Mensagem de ausência sem interpretação matemática.
- Mensagem de ausência sem clique/arraste.
- Curadoria humana como fonte da verdade.
- Diagrama de composição de coleções.
- Sincronização entre representações.
- Eixo dos inteiros.
- Logs e visualizações D3.
