# Guia de Migração das Skills Anteriores

## 1. Substituições terminológicas

| Forma anterior | Forma revisada |
|---|---|
| objeto representa um conceito | objeto representa um elemento semanticamente definido |
| elemento do diagrama é objeto de domínio | elemento semanticamente significativo corresponde ao domínio |
| InvarianteOperatorio para equação formal | RelacaoEstrutural... |
| posição do objeto | posição semântica no esquema ou coordenada na camada visual |
| ação revela invariante | ação pode integrar evidências para hipótese revisável |
| invariante operatório = proposição | teorema-em-ação = proposição; conceito-em-ação = objeto, predicado ou categoria relevante |

## 2. Renomeações recomendadas no piloto

- `InvarianteOperatorio` → `RelacaoEstruturalComposicao`.
- `InvarianteOperatorioTransformacao` → `RelacaoEstruturalTransformacao`.
- `explicar()` → `descreverRelacao()`.
- `verificar()` → `verificarConsistencia()`.
- `resolverIncognita()` → `calcularValorAusente()`.

## 3. Evento e cálculo

Uma relação estrutural deve preferencialmente retornar um resultado de cálculo sem modificar diretamente o papel. Outra camada decide se o valor será sugerido, aplicado ou comparado.

Se houver aplicação automática de uma relação estrutural, o evento deve
registrar origem `SISTEMA`, conforme o critério definido em
`REFERENCE.md §4.8`.

## 4. Hipóteses cognitivas

Criar estrutura separada para hipóteses sobre:

- esquema;
- teorema-em-ação;
- conceito-em-ação.

Não inserir essas hipóteses em objetos gráficos ou em eventos factuais.

## 5. Ordem recomendada

1. Adotar o modelo semântico de referência.
2. Renomear classes e testes conceitualmente incorretos.
3. Corrigir eventos e origem das ações.
4. Desacoplar descritores semânticos de classes do diagrama.
5. Reexecutar build e regressão.
6. Criar novo baseline sem apagar o histórico anterior.
