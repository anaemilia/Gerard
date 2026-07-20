# Relatório técnico — C143

## Objetivo

Introduzir, para **Transformação de medidas**, uma representação concreta manipulável equivalente ao papel desempenhado pelas coleções na composição e pelas barras na comparação.

## Representação implementada

Foi criado o **Tabuleiro de transformação**, apresentado como uma máquina em três zonas:

- **Antes** — estado inicial;
- **Mudança** — transformação quantificada;
- **Depois** — estado final.

Cada zona é vinculada ao estado semântico compartilhado. A transformação positiva aparece como **Entraram**; a negativa, como **Saíram**. A zona central mostra a magnitude por unidades concretas sem perder o sinal matemático.

## Arquitetura

As novas responsabilidades foram colocadas fora da `Main`:

```text
gerard.campoaditivo.representacao
├── SeletorRepresentacaoComplementar
└── TipoRepresentacaoComplementar

gerard.campoaditivo.transformacao.concreto
├── EstadoTabuleiroTransformacao
├── LayoutTabuleiroTransformacao
├── LayoutUnidadesTabuleiroTransformacao
├── PoliticaSinalTransformacaoComplementar
└── RenderizadorTabuleiroTransformacao
```

A tela principal apenas seleciona a representação, delega layout e desenho e reutiliza o fluxo consolidado de sincronização.

## Comportamentos

- O tabuleiro aparece ao lado do diagrama formal em Transformação de medidas.
- Nenhum valor curado é antecipado antes da modelagem do usuário.
- As três zonas utilizam unidades manipuláveis.
- Os controles `+` e `−` permanecem bloqueados até o primeiro posicionamento em Vergnaud.
- Estados inicial e final permanecem não negativos.
- A transformação pode ser positiva ou negativa; a manipulação concreta altera sua magnitude preservando o sinal.
- Mouse e teclado atualizam texto, Vergnaud, tabuleiro e eixo pelo mesmo estado compartilhado.
- Os limites curados, o piso zero e a consistência aditiva continuam aplicados.

## Funcionalidades consolidadas verificadas

Foram preservados e testados:

- arraste por proxy de números, sinais e interrogação;
- item incorreto permanecendo no diagrama;
- tip, som e tremor leve;
- física de mola;
- eixo flutuante e navegação semântica;
- controles de unidades;
- quantidades não negativas;
- conclusão exclusivamente numérica;
- radio button Sim chamando Sortear na mesma categoria;
- composição por coleções;
- comparação por barras;
- aba Construir;
- internacionalização em português, inglês e francês;
- dados curados inalterados.

## Validação automática

- compilação completa: 201 arquivos Java;
- `scripts/testar_tabuleiro_transformacao.sh`: 76 verificações;
- `scripts/testar_sincronizacao_controles_por_categoria.sh`: aprovado;
- `scripts/verificar_regressao_gerard.sh`: aprovado;
- testes de proxy, feedback, conclusão, eixo, unidades, sincronização textual e montagem: aprovados;
- dados curados da C142 e C143: idênticos por SHA-256.

## Validação visual

Foi gerada uma prévia do painel para inspeção de layout. A execução final no Windows deve conferir o arraste entre as três zonas, os controles, o som, a intensidade do tremor e o comportamento em diferentes resoluções.
