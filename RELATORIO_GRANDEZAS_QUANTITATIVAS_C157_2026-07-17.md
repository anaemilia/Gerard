# Relatório técnico — C157

## Objetivo

Criar uma regra genérica para distinguir números que representam **contagem de elementos** de números que representam **dinheiro**, sem duplicar regras nas representações e sem retirar as funcionalidades já consolidadas do Gérard.

## Princípio adotado

A quantidade passou a ser tratada como composição de quatro aspectos independentes:

1. valor numérico;
2. grandeza quantitativa;
3. unidade contextual;
4. papel semântico.

O papel semântico continua determinando o domínio do sinal:

- estados, partes, todo, referido e referendo permanecem não negativos;
- transformação e valor relativo continuam podendo ser negativos.

A grandeza determina precisão, fração, passo e política visual:

- `CONTAGEM`: discreta, sem fração e uma unidade por elemento;
- `MONETARIA`: decimal exata, até duas casas e unidade monetária explícita.

## Novas classes

Pacote `gerard.semantica.quantidade`:

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

## Resolução contextual

A resolução segue esta ordem:

1. metadado explícito da situação, como `GRANDEZA=CONTAGEM` ou `GRANDEZA=MONETARIA`;
2. unidade explícita, como `MOEDA=BRL`, `MOEDA=USD` ou `MOEDA=EUR`;
3. fallback para registros legados por pistas como `R$`, `real`, `reais`, `dólar` e `euro`;
4. ausência de evidência monetária: contagem.

As pistas linguísticas não substituem a curadoria. Elas existem apenas para manter compatibilidade com situações antigas.

## Representação concreta

### Contagem

- um quadradinho representa um elemento;
- não há escala agrupada;
- os controles continuam operando de uma em uma unidade.

### Dinheiro

- valores monetários são modelados com `BigDecimal`;
- quando as quantidades possuem divisor comum exato e a quantidade de quadradinhos seria excessiva, o processo usa agrupamento;
- a escala é exibida, por exemplo: `■ = R$ 10,00`;
- adicionar ou remover um quadradinho altera o valor semântico pela mesma escala;
- valores exibidos no processo mantêm duas casas decimais.

Exemplo agrupado:

- R$ 250,00 → 25 quadradinhos;
- −R$ 180,00 → 18 quadradinhos no funil de retirada;
- R$ 70,00 → 7 quadradinhos;
- legenda: `■ = R$ 10,00`.

## Compatibilidade com o estado consolidado

O estado compartilhado atual permanece inteiro. A classe `ConversorTextoParaInteiroSemantico` foi preservada como fachada e passou a delegar à nova arquitetura.

- `25,00` continua sendo aceito como 25;
- `18,00` continua sendo aceito como 18;
- `25,50` não é truncado para 25.

A nova camada semântica preserva `25,50` exatamente como valor monetário. Porém, enquanto o estado compartilhado consolidado permanecer inteiro, valores com centavos não nulos não são publicados nessa ponte legada. Essa decisão evita perda silenciosa de precisão e não altera os fluxos existentes.

## Alterações no processo de transformação

`PlanoUnidadesProcessoTransformacao` passou a transportar:

- grandeza contextual;
- valor por unidade visual;
- legenda da escala;
- valores formatados;
- conversão inversa de quadradinhos para o valor semântico.

`SincronizadorUnidadesProcessoTransformacao` resolve o perfil da situação e cria o plano correspondente. O renderizador apenas apresenta o plano.

## Funcionalidades preservadas

- regra do primeiro posicionamento no diagrama de Vergnaud;
- bloqueio das representações antes do primeiro posicionamento;
- naturais não negativos para medidas e estados;
- inteiros assinados para transformação e valor relativo;
- funil estrutural, entrada e retirada;
- controles `+` e `−`;
- sincronização entre texto, Vergnaud, eixo e representação complementar;
- arraste físico e feedback de erro;
- conclusão progressiva nas oito categorias;
- curadoria e dados existentes.

## Validação executada

- compilação de 265 fontes Java;
- 20 verificações específicas de grandezas contextuais;
- 13 verificações de valores monetários integrais e quadradinhos;
- 32 verificações do processo de transformação;
- estado semântico compartilhado;
- bloqueio antes do primeiro posicionamento;
- arraste físico;
- conclusão da modelagem e conclusão progressiva;
- regressão estrutural consolidada;
- inspeção visual da representação monetária agrupada;
- comparação de 13 arquivos TSV/CSV: todos idênticos entre C156 e C157.
