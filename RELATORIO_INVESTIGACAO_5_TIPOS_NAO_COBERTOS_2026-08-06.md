# Investigação — os 5 tipos de TipoSituacaoAditiva fora do piloto

Data: 2026-08-06. Somente leitura — nenhum código tocado, nenhum commit. Pergunta: para `EstadoSemanticoCompartilhado`, os 5 tipos não cobertos pela Fase A/B1 se reduzem a aplicações das 3 classes ricas já existentes, ou precisam de classes novas?

---

## 1. Achado principal: `EstadoSemanticoCompartilhado` só vê 3 "formatos de domínio", não 8 tipos

`CatalogoEsquemasCategoriasAditivas` registra, para cada `TipoSituacaoAditiva`, quais 3 papéis semânticos ocupam as posições 0/1/2 do estado compartilhado. Cruzando isso com os domínios (`NATURAIS`/`INTEIROS`) de cada papel em `CatalogoPapeisSemanticos`, os 8 tipos caem em só 3 formatos:

| Formato (dominio 0,1,2) | Tipos | Papéis nas posições 0/1/2 |
|---|---|---|
| N, N, N | `COMPOSICAO_MEDIDAS` | parte1, parte2, todo |
| N, Z, N | `TRANSFORMACAO_MEDIDAS`, `COMPARACAO_MEDIDAS`, `COMPOSICAO_TRANSFORMACAO_MEDIDAS`\*, `TRANSFORMACAO_COMPOSTA_DOIS_PASSOS`\* | estadoInicial/transformação/estadoFinal (ou equivalente) |
| Z, Z, Z | `COMPOSICAO_TRANSFORMACOES`, `TRANSFORMACAO_RELACAO`, `COMPOSICAO_RELACOES` | transformação1/2/final, relação inicial/transformação/final, relação1/2/final |

\* `COMPOSICAO_TRANSFORMACAO_MEDIDAS` e `TRANSFORMACAO_COMPOSTA_DOIS_PASSOS` são compostos por duas categorias simples internamente (`CategoriaComposta`), mas o esquema registrado para o estado compartilhado de 3 posições usa só os papéis de UM dos dois passos (o primeiro): `obterDominioCompartilhado(indice)` sempre lê essa mesma lista fixa de 3 papéis, mesmo quando `atualizarIndicesEstadoCompartilhado(...)` em `Main.java` desloca a "janela" de elementos do Vergnaud (índices `[0,1,2]` vs. `[3,4,5]`) para o segundo passo de uma transformação composta. Isso funciona sem inconsistência porque os dois passos de `TRANSFORMACAO_COMPOSTA_DOIS_PASSOS` têm o mesmo formato N,Z,N (estadoIntermediário também é `NATURAIS`) — mas é um acoplamento que vale a pena registrar, não algo que eu validaria como "correto por design" sem confirmação sua.

`resolverRelacaoAditiva()` nunca lê `tipo` para decidir a fórmula (só para o domínio) — por isso uma única álgebra genérica (soma/subtração de índice) já atendia aos 8 tipos antes de qualquer mudança, achado já registrado na investigação anterior. A implicação nova aqui: **a aritmética é idêntica nos 8 tipos**; o que varia é só qual posição aceita valor negativo.

## 2. Dois dos cinco tipos não cobertos não são alcançáveis pela interface hoje

`Main.java`, comentário na construção do menu Categoria (por volta da linha 3208-3214): o grupo "Transformações compostas" tem `COMPOSICAO_TRANSFORMACOES` habilitado, mas `COMPOSICAO_TRANSFORMACAO_MEDIDAS` e `TRANSFORMACAO_COMPOSTA_DOIS_PASSOS` usam `criarItemCategoriaEmConstrucao(...)` — item de menu desabilitado (`setEnabled(false)`), tooltip "Em construção". Nenhum ícone de atalho os abre. Também ficam de fora do sorteio de "Nova situação-problema" (linha ~3289-3293, junto com o comentário explícito "continuam de fora por serem 'Em construção' em todo lugar").

Ou seja: **hoje, na tela real, um usuário não consegue chegar a esses dois tipos por nenhum caminho de UI.** Isso muda a urgência de estendê-los — não é risco de produção ativo, é código alcançável só por replay de protocolo antigo ou teste direto.

Os outros 3 tipos não cobertos (`COMPOSICAO_TRANSFORMACOES`, `TRANSFORMACAO_RELACAO`, `COMPOSICAO_RELACOES`) **são** alcançáveis — "grupo Relações... não é mais 'Em construção'" (comentário na linha ~3223-3224), abertos tanto pelo menu quanto por ícones de atalho.

## 3. Resposta à pergunta original: nem "reduz automaticamente", nem "precisa de classe nova por obrigação aritmética" — é uma escolha de honestidade semântica

Numericamente, qualquer uma das 3 classes ricas já calcularia o valor certo para qualquer um dos 8 tipos — `calcularValorAusente()` não valida domínio (isso acontece depois, na escrita, dentro de `EstadoSemanticoCompartilhado`), só faz soma/subtração. Então, em tese, dava para reaproveitar `RelacaoEstruturalTransformacao` (por exemplo) para calcular qualquer um dos 5 tipos não cobertos sem gerar nenhum resultado numericamente errado.

Mas o próprio código deste pacote é insistente, em vários lugares (javadoc de `RelacaoEstruturalComposicao`/`Comparacao`, a "distinção conceitual obrigatória" repetida em cada classe), que não conflar conceitos diferentes é o ponto central do princípio da localidade do conhecimento aqui — não só "dar o resultado certo". Nessa régua, os 5 tipos se dividem em dois grupos bem diferentes:

- **`COMPOSICAO_TRANSFORMACAO_MEDIDAS` e `TRANSFORMACAO_COMPOSTA_DOIS_PASSOS`**: o que o estado compartilhado rastreia para eles, em qualquer um dos passos, **é literalmente uma Transformação de Medidas** (EstadoInicial + Transformação = EstadoFinal/EstadoIntermediário) — não é uma analogia numérica, é a mesma relação, só usada dentro de uma situação maior. Reaproveitar `RelacaoEstruturalTransformacao` aqui não seria emprestar uma classe por conveniência — seria reconhecer que já é o mesmo conceito. Baixo risco, alto encaixe semântico — mas de valor prático baixo agora, já que nenhum dos dois é alcançável pela UI.
- **`COMPOSICAO_TRANSFORMACOES`, `TRANSFORMACAO_RELACAO`, `COMPOSICAO_RELACOES`**: são a categoria "Relações" de Vergnaud — relações entre números relativos, não entre medidas. Nenhuma das 3 classes ricas atuais (todas nomeadas "...DeMedidas") representa esse conceito. Usar uma delas aqui só por coincidência numérica seria exatamente o tipo de conflação que este pacote foi desenhado para evitar. Esses 3 tipos **são** alcançáveis pela UI hoje — são os que mais importariam se a decisão for estender a delegação.

## Recomendação (não uma decisão — fica com você)

Não estenderia a delegação B1 para nenhum dos 5 agora, mas por razões diferentes em cada grupo:

- Os 2 "Em construção": não há usuário para proteger nem bug de produção para prevenir — esperaria vocês decidirem terminar essas categorias antes de investir em delegação para elas.
- Os 3 "Relações": estender exigiria criar 3 classes ricas novas (`RelacaoEstruturalComposicaoDeTransformacoes`, `RelacaoEstruturalTransformacaoDeRelacao`, `RelacaoEstruturalComposicaoDeRelacoes`), mesmo padrão de código das 3 já existentes, com nomes e javadoc próprios — trabalho real, não mecânico, e uma decisão de escopo (Fase D?) separada de B1.

O algoritmo genérico atual já trata os 5 corretamente hoje (nada quebrado, nada bloqueado) — esta investigação não encontrou urgência, só documentou a lacuna.

## O que não foi feito

Nenhum código alterado, nenhum commit.
