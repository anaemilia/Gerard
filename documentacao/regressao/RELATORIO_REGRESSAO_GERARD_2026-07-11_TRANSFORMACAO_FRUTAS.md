# Relatório de regressão — correção de classificação: frutas

## Alteração realizada

A situação-problema:

> Maria tinha 4 maçãs, comprou 6 bananas. Com quantas frutas ficou?

foi corrigida de `COMPOSICAO_MEDIDAS` para `TRANSFORMACAO_MEDIDAS`.

A correção foi aplicada nos arquivos de dados e no conjunto de treino, incluindo as traduções correspondentes em inglês e francês quando presentes no arquivo de situações.

## Arquivos alterados

- `dados/situacoes_vergnaud.tsv`
- `src/gerard/campoaditivo/dados/situacoes_vergnaud.tsv`
- `src/gerard/interpretacao/classificacao/exemplos_treino_vergnaud.tsv`
- `src/gerard/campoaditivo/servico/ClassificadorTipoSituacaoAditiva.java`
- `scripts/verificar_regressao_gerard.py`

## Ajuste de classificação

O adaptador `ClassificadorTipoSituacaoAditiva` agora corrige uma categoria manual/dados quando o modelo e a regra linguística apontam para a mesma categoria, com confiança de regra suficiente. Isso evita que uma linha originalmente rotulada de forma inconsistente permaneça classificada como composição quando há pistas explícitas de transformação, como `tinha`, `comprou` e `ficou`.

## Teste novo incluído na regressão

Foi incluído o teste `verificar_classificacao_situacoes_criticas`, que verifica explicitamente:

- `Maria tinha 4 maçãs, comprou 6 bananas. Com quantas frutas ficou?` → `TRANSFORMACAO_MEDIDAS`;
- `Numa cesta há sete laranjas e oito bananas. Quantas frutas tinha na cesta?` → `COMPOSICAO_MEDIDAS`;
- `O pai de Adriana tem um chaveiro com 3 chaves de prata e 2 chaves de ouro...` → `COMPOSICAO_MEDIDAS`;
- `Raquel tinha quatro livros, sua mãe lhe deu mais cinco...` → `TRANSFORMACAO_MEDIDAS`.

## Resultado da regressão

Comando executado:

```bash
bash scripts/verificar_regressao_gerard.sh
```

Resultado:

- Compilação: `BUILD SUCCESSFUL`;
- JAR gerado: `dist/GerardNetBeans_D3_Leitura_Redes_Transicoes.jar`;
- Internacionalização: 547 chaves em português, inglês e francês;
- Sincronização entre representações: verificações estáticas preservadas;
- Extração contextual de numerais: aprovada;
- Classificação de situações críticas: aprovada;
- Logs e D3: verificações preservadas;
- Erros críticos: 0.

Permanece apenas o aviso já existente: o arquivo de situações possui 66 registros em português e 72 em inglês/francês. Esse aviso não impede a execução.
