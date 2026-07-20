# Relatório de regressão — mensagem de ausência de situações curadas

## Alteração realizada

A mensagem de ausência de situação-problema curada deixou de ser tratada como enunciado matemático.

Antes, quando aparecia a mensagem:

> Nenhuma situação-problema curada está disponível para esta categoria. Valide uma situação na aba Curadoria e salve os metadados curados.

O Gerard enviava esse texto ao interpretador linguístico. Por isso, termos como "uma situação" podiam ser lidos como elementos da legenda de Vergnaud ou como quantidade manipulável.

## Correção implementada

Foi criado um estado interno de sistema:

```java
boolean textoProblemaEhMensagemSistema
```

Quando não existe situação curada disponível, esse estado é ativado. Nesse caso:

- o texto de ausência é exibido normalmente na tela;
- o texto não é enviado ao interpretador linguístico;
- `resultadoInterpretacao` fica `null`;
- números e interrogações presentes em mensagens de sistema não são animados;
- mensagens de sistema não geram elementos matemáticos arrastáveis;
- a ausência permanece identificada como `SEM_SITUACAO_CURADA`.

## Arquivos modificados

- `src/Main.java`
- `scripts/verificar_regressao_gerard.py`

## Regressão executada

Comando executado:

```bash
bash scripts/verificar_regressao_gerard.sh
```

Resultado:

- `BUILD SUCCESSFUL`
- 106 verificações OK
- 0 erros críticos
- 1 aviso conhecido sobre diferença na quantidade de situações-problema por idioma no TSV empacotado.

## Funcionalidades conferidas

- Compilação do projeto.
- Internacionalização.
- Exibição apenas de situações curadas.
- Mensagem de ausência sem interpretação matemática.
- Aba Curadoria.
- Persistência dos metadados curados.
- Diagrama de composição de coleções.
- Eixo dos inteiros.
- Sincronização entre representações.
- Logs e visualizações D3.

