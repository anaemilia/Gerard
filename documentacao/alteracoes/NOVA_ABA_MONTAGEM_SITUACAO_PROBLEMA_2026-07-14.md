# Nova aba: construção da situação-problema a partir do diagrama

Data: 14 de julho de 2026

## Objetivo

Foi acrescentada ao Gérard a aba **Construir situação-problema**. Nessa atividade, o usuário recebe um diagrama de Vergnaud já preenchido e deve construir um enunciado coerente selecionando e ordenando blocos de texto.

A atividade não se limita a reorganizar as frases do enunciado original. O conjunto disponível contém:

- blocos literais da situação-problema curada;
- blocos não compatíveis provenientes de outras estruturas aditivas;
- os mesmos valores numéricos do diagrama;
- personagens, objetos, contexto e construção linguística deliberadamente próximos.

A diferença decisiva deve estar na **relação semântica entre as quantidades** e nos papéis de Vergnaud, e não em pistas superficiais.

## Funcionamento implementado

1. O sistema seleciona uma situação validada na curadoria.
2. A atividade é disponibilizada somente quando os três valores do diagrama estão preenchidos; situações contendo `?` em um dos valores não são usadas nessa aba.
3. O diagrama é apresentado de forma preenchida e não editável.
4. O enunciado curado é segmentado em blocos corretos, preservando literalmente seu texto.
5. São criados de três a quatro blocos não compatíveis, mantendo distância semântica controlada em relação aos corretos.
6. Todos os blocos recebem o mesmo tratamento visual. A interface não revela categoria, papel semântico nem condição de acerto.
7. O usuário pode mover os blocos por arraste ou por botões, retirar blocos, reordená-los, recomeçar e validar.
8. A validação verifica simultaneamente a seleção e a ordem dos blocos.

## Escopo inicial

A primeira versão atende às categorias nucleares:

- composição de medidas;
- transformação de medidas;
- comparação de medidas.

A arquitetura foi isolada no pacote `gerard.campoaditivo.montagem`, permitindo ampliação posterior para outras categorias sem concentrar a lógica em `Main.java`.

## Registro da atividade

A aba mantém uma tentativa própria no logger. São registrados:

- início da atividade;
- situação e categoria de origem;
- critério de distância semântica controlada;
- inclusão, retirada e reordenação dos blocos;
- número de movimentos;
- número de validações;
- resultado da validação.

Ao sair da aba, o contexto de tentativa anteriormente ativo no Gérard é restaurado. Dessa forma, as ações da construção não são vinculadas indevidamente à tentativa de modelagem realizada na aba principal.

## Internacionalização

Todos os componentes novos foram incluídos no serviço de localização nos três idiomas adotados pelo projeto:

- português;
- inglês;
- francês.

## Arquivos principais

- `src/gerard/campoaditivo/montagem/BlocoTextoMontagem.java`
- `src/gerard/campoaditivo/montagem/ConjuntoBlocosMontagem.java`
- `src/gerard/campoaditivo/montagem/GeradorBlocosMontagem.java`
- `src/gerard/campoaditivo/montagem/PainelDiagramaPreenchido.java`
- `src/gerard/campoaditivo/montagem/TelaMontagemSituacao.java`
- `src/Main.java`
- `src/gerard/pesquisador/log/LoggerInteracaoGerard.java`
- `src/gerard/i18n/ServicoLocalizacao.java`

## Correção posterior: variedade do botão Novo diagrama

A seleção inicial dependia apenas das situações completamente validadas e preenchidas na curadoria. Quando existia somente uma situação elegível, o botão **Novo diagrama** recarregava inevitavelmente o mesmo exercício.

A correção passou a:

- impedir a repetição imediata da mesma situação;
- priorizar mudança de categoria quando houver alternativa;
- combinar as situações validadas com um catálogo mínimo de referência nas três línguas do projeto;
- preservar a curadoria como fonte da verdade, sem promover automaticamente situações não validadas;
- manter os exemplos de referência sob o critério de distância semântica controlada definido pelo pesquisador.

O catálogo de referência contém composição, transformação e comparação de medidas com os mesmos valores 4, 7 e 11, personagens e objeto próximos. A diferença relevante permanece na relação semântica entre as quantidades.
