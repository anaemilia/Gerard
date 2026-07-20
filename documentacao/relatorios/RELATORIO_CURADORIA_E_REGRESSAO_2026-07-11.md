# Relatório de alteração — curadoria humana das situações-problema

## Objetivo

Eliminar a dependência de categorização automática das situações-problema em tempo de execução. A categoria de Vergnaud e os metadados passam a ser dados autorais, validados por humanos na nova aba **Curadoria**.

## Alterações implementadas

1. Inclusão da aba **Curadoria** ao lado da aba principal **Gerard**.
2. Criação da tela `gerard.campoaditivo.curadoria.TelaCuradoriaSituacoes`.
3. Ampliação do modelo `SituacaoProblemaAditiva` para armazenar metadados curados:
   - id;
   - validada;
   - idioma;
   - categoria de Vergnaud;
   - contexto;
   - enunciado;
   - fonte;
   - subtipo;
   - estado inicial;
   - transformação;
   - estado final;
   - quantidade 1;
   - quantidade 2;
   - resultado;
   - termo desconhecido;
   - representação visual;
   - observações.
4. O repositório `RepositorioSituacoesAditivas` passou a ler primeiro o arquivo curado do usuário:
   - `~/Gerard/curadoria/situacoes_vergnaud_curadas.tsv`.
5. Se o arquivo curado não existir, o Gerard continua usando o arquivo empacotado:
   - `src/gerard/campoaditivo/dados/situacoes_vergnaud.tsv`.
6. A categoria registrada no arquivo curado passa a ser autoritativa. O repositório não chama mais o classificador automático para corrigir categoria em tempo de execução.
7. A aba Curadoria permite:
   - editar metadados;
   - validar linhas selecionadas;
   - salvar e aplicar os metadados curados;
   - criar nova linha;
   - remover linha;
   - gerar auditoria comparando categoria curada com sugestão automática não autoritativa.
8. A auditoria é salva em:
   - `~/Gerard/curadoria/auditoria_curadoria_situacoes.tsv`.

## Procedimento de regressão executado

Comando executado:

```bash
bash scripts/verificar_regressao_gerard.sh
```

Resultado:

- Compilação: `BUILD SUCCESSFUL`.
- JAR gerado: `dist/GerardNetBeans_D3_Leitura_Redes_Transicoes.jar`.
- Erros críticos: 0.
- Avisos: 1.

Aviso persistente:

- As quantidades de situações por idioma permanecem diferentes: português 66, inglês 72, francês 72. Isso não impede a execução, mas deve ser revisado caso a paridade de traduções se torne obrigatória.

## Funcionalidades verificadas

- Compilação do projeto.
- Internacionalização existente.
- Estado da tela e sincronização entre representações.
- Extração contextual de numerais.
- Classificação automática mantida apenas como recurso auxiliar de auditoria.
- Nova aba de curadoria.
- Persistência e recarregamento dos metadados curados.
- Preservação da categoria autoritativa validada por humano.
- Diagrama de composição de coleções.
- Logs e visualizações D3.

## Observação metodológica

Com esta alteração, a categorização automática deixa de ser o centro operacional da aplicação. O Gerard passa a privilegiar a curadoria humana dos metadados, tornando a classificação das situações-problema um dado validado e auditável. Isso reduz o risco de uma classificação incorreta comprometer toda a lógica posterior da interface.
