# P2 — fonte canônica e proteção das situações curadas

Data: 2026-08-24

## Decisão preservada

O arquivo produzido pela curadoria humana é a autoridade sobre cada situação-problema. A infraestrutura pode carregar, persistir, empacotar e verificar esses dados, mas não pode inferir personagens, recalcular valores, trocar categorias ou reinterpretar campos curados.

As seis categorias canônicas permanecem:

- `COMPOSICAO_MEDIDAS`: 56 situações;
- `TRANSFORMACAO_MEDIDAS`: 86 situações;
- `COMPARACAO_MEDIDAS`: 20 situações;
- `COMPOSICAO_TRANSFORMACOES`: 32 situações;
- `TRANSFORMACAO_RELACAO`: 8 situações;
- `COMPOSICAO_RELACOES`: 8 situações.

## Fonte protegida

- fonte humana ativa: `C:\Users\cecomp\Gerard\curadoria\situacoes_vergnaud_curadas.tsv`;
- backup imutável atual: `C:\Users\cecomp\Gerard\curadoria\backups\situacoes_vergnaud_curadas_2026-08-24_29686D1B.tsv`;
- fonte canônica versionada: `src/gerard/campoaditivo/dados/situacoes_vergnaud.tsv`;
- SHA-256: `29686D1BF0B3CA334C7B4034748C19BA2EC91478CF4D9B2A13A2448C67DB4ECE`;
- tamanho: 70.359 bytes;
- conteúdo: 210 situações, das quais 40 estão validadas;
- esquema atual: 37 colunas.

O backup de 2026-08-23, referente à fotografia anterior de 35 colunas, foi mantido como histórico. A fotografia atual incorpora os campos `estado_intermediario` e `operacao_estado_transformacao` introduzidos pela versão arquitetural mais recente.

## Proteções implementadas

1. A cópia versionada redundante `dados/situacoes_vergnaud.tsv` foi removida. Ela permanece recuperável pelo histórico Git.
2. Carregadores, fallbacks e testes usam somente o recurso canônico sob `src`.
3. `scripts/verificar_curadoria_canonica.py` exige:
   - o cabeçalho exato de 37 colunas;
   - 210 ids únicos;
   - somente as seis categorias e suas contagens registradas;
   - igualdade com o hash do manifesto;
   - ausência da cópia versionada redundante.
4. `TesteRoundTripCuradoriaCanonica` lê e regrava uma cópia temporária, comparando literalmente os 37 campos de cada uma das 210 situações.
5. Para `TRANSFORMACAO_RELACAO`, o teste destaca a preservação de `estado_final` (a `relacao_final` da curadoria) e `operacao_relacao`.
6. Para `COMPOSICAO_TRANSFORMACOES`, o teste destaca a preservação de `estado_intermediario` e `operacao_estado_transformacao`.

## Validação

- verificador arquitetural completo: aprovado;
- compilação da aplicação: 472 fontes, aprovada;
- compilação dos testes: 84 testes Java, aprovada;
- testes executados: 80 aprovados e nenhum reprovado;
- testes gráficos: 4 compilados e não executados por exigirem ambiente com display;
- teste de round-trip dos 37 campos: aprovado.

Durante a validação, quatro testes legados foram alinhados à versão atual: um passou a chamar a nova assinatura do seletor de operação, e três passaram a localizar a situação pelo código canônico de idioma `pt-BR`, em vez do código legado `PORTUGUES`. Nenhuma dessas alterações muda o comportamento da aplicação.
