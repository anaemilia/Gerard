# CHANGELOG C157 — grandezas quantitativas contextuais

## Objetivo
Separar genericamente números que representam contagem de elementos de números que representam dinheiro, preservando os contratos já consolidados do Gérard.

## Novo domínio semântico
Foi criado o pacote `gerard.semantica.quantidade`, com os seguintes componentes:

- `TipoGrandezaQuantitativa`
- `UnidadeQuantitativa`
- `GrandezaQuantitativa`
- `GrandezaContagem`
- `GrandezaMonetaria`
- `PerfilQuantidadeSituacao`
- `ResolvedorPerfilQuantidadeSituacao`
- `QuantidadeSemantica`
- `ConversorTextoParaQuantidadeSemantica`
- `EscalaVisualQuantidade`
- `PoliticaEscalaVisualQuantidade`
- `ServicoQuantidadeContextual`

## Regras

### Contagem
- valores discretos;
- sem parte fracionária;
- passo padrão igual a 1;
- um quadradinho por elemento.

### Dinheiro
- decimal exato por `BigDecimal`;
- até duas casas decimais no modelo da grandeza;
- exibição com duas casas decimais;
- agrupamento visual somente por escala exata comum;
- legenda explícita quando um quadradinho representa mais de uma unidade monetária.

### Papel semântico
A grandeza não decide o sinal. O papel continua sendo a fonte dessa regra:

- estados, partes, todo, referido e referendo: não negativos;
- transformação e valor relativo: assinados.

## Compatibilidade
O `ConversorTextoParaInteiroSemantico` foi mantido como fachada. Valores monetários integrais, como `25,00`, continuam entrando no estado compartilhado como 25. Valores com centavos não nulos não são truncados.

## Processo de transformação
O plano de unidades passou a transportar:

- tipo da grandeza;
- valor representado por quadradinho;
- legenda da escala;
- valores formatados por contexto.

A manipulação concreta converte novamente a quantidade de quadradinhos pela mesma escala, evitando divergência entre representação e modelo.
