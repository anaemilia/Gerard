# Relatório de alteração — curadoria de sinais

## Alteração solicitada

Foi acrescentada à curadoria humana a possibilidade de registrar explicitamente o sinal da transformação e o sinal do valor relativo.

## Campos adicionados

- `sinal_transformacao`
- `sinal_valor_relativo`

Os campos foram incluídos na tela detalhada de curadoria como `ComboBox`, com as opções:

- vazio
- positivo
- negativo
- neutro
- não_aplicável

## Efeito na lógica

A fonte da verdade continua sendo a curadoria humana. O Gerard não precisa inferir se a transformação ou o valor relativo é positivo ou negativo a partir do texto do enunciado.

Quando a situação curada é usada na tela principal:

- `transformacao` recebe o sinal de `sinal_transformacao`, quando aplicável;
- `valor_relativo` recebe o sinal de `sinal_valor_relativo`, quando aplicável;
- o construtor curado preserva os papéis de Vergnaud sem depender de classificação automática;
- arquivos de curadoria antigos continuam compatíveis.

## Persistência

O TSV de curadoria passa a usar 22 campos, incluindo os dois novos sinais. O repositório continua lendo os formatos anteriores com 20 e 17 campos.

## Regressão executada

Comando executado:

```bash
bash scripts/verificar_regressao_gerard.sh
```

Resultado:

- `BUILD SUCCESSFUL`
- erros críticos: 0
- aviso mantido: diferença conhecida na quantidade de situações-problema por idioma no TSV empacotado.

## Funcionalidades conferidas

- compilação Ant;
- aba de curadoria;
- tela detalhada de curadoria;
- campos de comparação de medidas;
- combo do termo desconhecido;
- novos combos de sinal;
- persistência e recarregamento de metadados curados;
- uso da curadoria como fonte da verdade;
- exibição apenas de situações curadas;
- proteção de mensagens de sistema contra interpretação matemática;
- sincronização entre representações;
- diagrama de composição de coleções;
- logs e visualizações D3.
