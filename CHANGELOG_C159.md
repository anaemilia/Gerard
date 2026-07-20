# CHANGELOG C159 — Consistência da incógnita curada

## Problema corrigido

Em registros nos quais o próprio campo semântico continha `?`, mas o campo `termo_desconhecido` estava vazio, a interface visual mostrava a interrogação, porém o modelo semântico podia tratá-la como valor conhecido. O caso observado foi:

- estado inicial: `10`;
- transformação: `?` com sinal negativo;
- estado final: `6`;
- termo desconhecido: vazio.

## Solução

Foi criado `ResolvedorIncognitaCurada`, fonte única para conciliar as duas formas explícitas de curadoria da incógnita:

1. seleção em `termo_desconhecido`;
2. símbolo `?` no valor do papel correspondente.

A solução não infere o papel pelo enunciado. A verdade continua vindo dos metadados curados.

## Regras

- Um único campo com `?` e termo vazio: o termo é harmonizado automaticamente.
- Termo e `?` no mesmo papel: situação consistente.
- Termo e `?` em papéis diferentes: salvamento bloqueado.
- Mais de um campo com `?`: salvamento bloqueado.
- `?` nunca é publicado como valor conhecido no `ResultadoInterpretacao`.
- A regra é aplicada às oito categorias aditivas.

## Arquivos principais

- `src/gerard/campoaditivo/curadoria/ResolvedorIncognitaCurada.java`
- `src/gerard/campoaditivo/curadoria/SemanticaCuradaSituacao.java`
- `src/gerard/campoaditivo/curadoria/ConstrutorResultadoCurado.java`
- `src/gerard/campoaditivo/curadoria/TelaCuradoriaSituacoes.java`
- `scripts/testar_consistencia_incognita_curada.sh`
