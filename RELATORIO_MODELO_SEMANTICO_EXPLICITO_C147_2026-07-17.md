# Relatório técnico — C147

## Objetivo

Reestruturar o núcleo semântico do Gérard para que as regras matemáticas pertençam aos próprios objetos de domínio, e não sejam repetidas ou inferidas separadamente pelo texto, diagrama de Vergnaud, gráfico de barras, eixo ou tábua de transformação.

A versão também incorpora classes explícitas para contexto, personagens, pistas linguísticas, elementos semânticos e categorias aditivas simples ou compostas.

## Princípio arquitetural

O fluxo consolidado passa a ser:

```text
papel semântico
      ↓
define o domínio numérico esperado
      ↓
objeto ValorNumerico valida o valor
      ↓
estado semântico compartilhado
      ↓
texto, Vergnaud, eixo, barras, coleções e tábua apenas projetam o mesmo valor
```

A aceitação de sinal deixa de ser uma decisão da categoria ou da representação gráfica.

## Domínios numéricos

Foram criados objetos de domínio para:

- naturais, incluindo zero (`NumeroNatural`, domínio `NATURAIS`);
- inteiros (`NumeroInteiro`, domínio `INTEIROS`);
- valor desconhecido (`ValorDesconhecido`), preservando o domínio esperado.

A regra consolidada é:

- transformação e valor relativo: universo dos inteiros;
- estados, partes, todo, referido e referendo: universo dos naturais;
- `?` não é convertido em zero e mantém o domínio do papel que ocupa.

## Novos módulos

### Número

```text
gerard.semantica.numero
├── DominioNumerico
├── ValorNumerico
├── NumeroNatural
├── NumeroInteiro
├── ValorDesconhecido
└── FabricaValoresNumericos
```

### Papéis semânticos

```text
gerard.semantica.papel
├── PapelSemantico
├── PapelQuantitativo
└── CatalogoPapeisSemanticos
```

### Contexto

```text
gerard.semantica.contexto
├── TipoReferenteContextual
├── UnidadeMedida
├── ReferenteContextual
└── ContextoSituacao
```

Objetos como bolas, carrinhos, figurinhas, reais, litros ou metros são dados de `ReferenteContextual`, e não subclasses Java distintas.

### Entidades e personagens

```text
gerard.semantica.entidade
├── EntidadeSemantica
├── Personagem
└── GrupoPersonagens
```

Nomes próprios são dados de `Personagem`; os papéis referido, referendo, possuidor ou agente permanecem vínculos semânticos da situação.

### Pistas linguísticas

```text
gerard.semantica.pista
├── TipoPistaLinguistica
├── PistaLinguistica
├── OcorrenciaPista
└── LexicoPistasAditivas
```

O léxico contempla português, inglês e francês. Palavras como “ontem”, “ganhou”, “perdeu”, “juntos”, “a mais que” e equivalentes são tratadas como evidências linguísticas; nenhuma palavra isolada determina a categoria.

### Elementos semânticos

```text
gerard.semantica.elemento
├── TipoElementoSemantico
├── ElementoSemantico
├── ElementoNumerico
├── ElementoEntidade
├── ElementoContextual
└── ElementoLinguistico
```

### Categorias aditivas

```text
gerard.semantica.categoria
├── ComponenteCategoria
├── RestricaoSemantica
├── RelacaoSemantica
├── CategoriaSimples
├── CategoriaComposta
├── EsquemaCategoriaAditiva
└── CatalogoEsquemasCategoriasAditivas
```

As categorias passam a ser esquemas compostos por papéis, relações e restrições. As categorias de vários passos usam `CategoriaComposta`, sem duplicar regras numéricas.

### Situação-problema concreta

```text
gerard.semantica.situacao
├── InstanciaSituacaoAditiva
└── FabricaInstanciaSemanticaAditiva
```

A situação concreta reúne esquema de categoria, valores, personagens, contexto e pistas linguísticas.

## Compatibilidade com o sistema existente

As classes antigas não foram apagadas. Elas foram mantidas como fachadas de compatibilidade:

- `CatalogoPapeisSemanticosAditivos` delega ao novo catálogo;
- `PoliticaValoresAditivos` delega aos domínios e esquemas semânticos;
- `CatalogoPerfisCategoriasAditivas` deriva capacidades dos papéis do esquema;
- `PoliticaSinalTransformacaoComplementar` consulta o domínio do papel;
- `EstadoSemanticoCompartilhado` armazena internamente `ValorNumerico`, preservando sua API pública consolidada.

A `Main.java` não recebeu novas responsabilidades desta refatoração.

## Estado compartilhado

O estado compartilhado agora conserva:

- valor numérico;
- domínio do valor;
- condição conhecido/desconhecido;
- origem da alteração;
- papel alterado.

Valores naturais negativos são rejeitados antes de chegarem às representações. Valores inteiros podem atravessar zero. As representações concretas podem desenhar a magnitude, mas o sinal permanece no valor semântico.

## Documentação acadêmica atualizada

### Artigo

O PDF do artigo recebeu a seção:

**“Atualização arquitetural C147: modelo semântico explícito”**

Foram discutidos os domínios numéricos, contextos, personagens, pistas linguísticas, categorias compostas e representações como projeções.

### Análise de complexidade

O PDF de complexidade recebeu a seção:

**“Complexidade do modelo semântico explícito em C147”**

Foram registrados:

- validação numérica e consulta de domínio em `O(1)`;
- busca de pistas em `O(pn)`, para `p` padrões e texto de comprimento `n`;
- composição/achatamento de categorias em `O(k + m)`;
- custos e riscos das fachadas de compatibilidade.

Os dois PDFs foram recompilados, inspecionados quanto a fontes incorporadas e renderizados para conferência visual das páginas alteradas.

## Testes e regressão

Resultados executados:

- compilação completa: 242 fontes Java, `BUILD SUCCESSFUL`;
- novo teste do modelo semântico explícito: 23 verificações aprovadas;
- política de valores aditivos: aprovada;
- perfis de categorias: 13 verificações e teste estrutural aprovados;
- estado semântico compartilhado: aprovado;
- tábua de transformação: 82 verificações e teste estrutural aprovados;
- sinal relativo sem quantidade cardinal negativa: aprovado;
- arraste físico: aprovado;
- conclusão da modelagem: 21 verificações aprovadas;
- posicionamento do tip de conclusão: 6 verificações aprovadas;
- regressão estrutural completa: aprovada.

## Integridade dos dados curados

Os arquivos curados da C146 e da C147 foram comparados por SHA-256. Permaneceram idênticos:

- `situacoes_vergnaud.tsv`;
- `situacoes_pdf_nao_aditivas_referencia.tsv`.

## Arquivos principais

- projeto completo C147;
- executável JAR C147;
- artigo atualizado;
- análise de complexidade atualizada;
- matriz de regressão atualizada;
- comparação dos dados curados;
- códigos SHA-256 dos artefatos.
