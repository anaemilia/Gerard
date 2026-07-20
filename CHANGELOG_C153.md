# CHANGELOG C153

## Objetivo
Aplicar a correção de rótulos/papéis semânticos de forma consistente **para todas as categorias**, com ênfase nas categorias compostas que ainda não estavam totalmente cobertas.

## Ajustes realizados

### 1. Catálogo semântico dos diagramas
Arquivo: `src/gerard/campoaditivo/semantica/CatalogoPapeisSemanticosAditivos.java`

- Adicionado mapeamento explícito para `TRANSFORMACAO_COMPOSTA_DOIS_PASSOS`.
- Agora o catálogo reconhece corretamente:
  - `papel.estadoInicial`
  - `papel.transformacao1`
  - `papel.estadoIntermediario`
  - `papel.transformacao2`
  - `papel.estadoFinal`
- Isso evita fallback genérico (`papel.valor`) nessa categoria.

### 2. Definições visuais padrão por categoria
Arquivo: `src/gerard/campoaditivo/servico/CatalogoDefinicoesAditivas.java`

- `COMPOSICAO_TRANSFORMACAO_MEDIDAS`:
  - definição padrão ajustada para `Parte 1`, `Parte 2`, `Todo`.
- `TRANSFORMACAO_COMPOSTA_DOIS_PASSOS`:
  - definição padrão ajustada para `Estado inicial`, `Transformação 1`, `Estado intermediário`.

### 3. Aplicação dos rótulos curados
Arquivo: `src/gerard/campoaditivo/curadoria/SemanticaCuradaSituacao.java`

- A aplicação de rótulos deixou de depender cegamente dos três primeiros itens mapeados em todos os casos.
- Para `TRANSFORMACAO_COMPOSTA_DOIS_PASSOS`, a interface agora usa explicitamente:
  - `Estado inicial`
  - `Transformação 1`
  - `Estado intermediário`
- Nas demais categorias, mantém-se a aplicação consistente dos três papéis visíveis da categoria.

### 4. Internacionalização
Arquivo: `src/gerard/i18n/ServicoLocalizacao.java`

- Adicionada a chave `papel.estadoIntermediario` para:
  - Português
  - Inglês
  - Francês
  - Espanhol (legado existente no arquivo)

## Resultado esperado
- O sistema passa a ter cobertura semântica mais consistente **em todas as categorias**.
- As categorias compostas deixam de cair em rótulos genéricos ou incorretos por ausência de mapeamento.
- A categoria `Transformação composta em dois passos` passa a exibir rótulos estruturais corretos já no fallback padrão e na aplicação curada.
