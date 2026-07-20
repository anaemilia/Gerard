# Relatório de alteração e regressão — Curadoria como fonte da verdade

## Objetivo da alteração

A lógica de execução do Gerard foi ajustada para que a fonte da verdade seja a curadoria humana. A tela principal não deve mais classificar textos automaticamente para decidir categoria, papéis, quantidades, incógnita ou representação. A categorização automática permanece apenas como apoio/auditoria na aba Curadoria.

## Alterações realizadas

1. Criada a classe `gerard.campoaditivo.curadoria.ConstrutorResultadoCurado`.
   - Constrói o `ResultadoInterpretacao` a partir dos metadados curados.
   - Usa `SituacaoProblemaAditiva.getTipo()` como categoria autoritativa.
   - Usa campos curados como `estado_inicial`, `transformacao`, `estado_final`, `quantidade_1`, `quantidade_2`, `resultado`, `termo_desconhecido` e `representacao_visual`.
   - Não executa classificação linguística do texto.

2. Alterada a tela principal em `Main.java`.
   - Removidas chamadas a `interpretadorLinguistico.interpretar(...)` na execução com o aluno.
   - A interface agora usa `construtorResultadoCurado.construir(situacaoProblemaAtual)`.
   - O painel antes chamado de interpretação linguística foi reposicionado como visualização de metadados curados/assistente de curadoria.

3. Ajustado `RepositorioSituacoesAditivas`.
   - A busca por situação correspondente em outro idioma prioriza valores dos metadados curados.
   - A extração a partir do texto fica apenas como fallback de compatibilidade.

4. Atualizada a internacionalização.
   - “Interpretação linguística” foi substituída por “Assistente de curadoria”/“Metadados curados” na interface.
   - O campo “Confiança” passou a indicar “Fonte: Curadoria humana”.
   - Incluídas traduções em português, inglês e francês.

5. Atualizado o procedimento de regressão.
   - Incluída verificação específica: “Fonte da verdade: curadoria humana”.
   - O script agora falha se a tela principal voltar a chamar o interpretador linguístico para classificar situações em tempo de execução.
   - Incluído teste que confirma que uma situação com texto ambíguo mantém a categoria curada, mesmo quando o texto poderia induzir outra classificação automática.

## Procedimento de regressão executado

Comando executado:

```bash
bash scripts/verificar_regressao_gerard.sh
```

Resultado:

- Compilação: `BUILD SUCCESSFUL`.
- Erros críticos: 0.
- Avisos: 1.

Aviso remanescente: o TSV empacotado continua com diferença de quantidade entre idiomas: 66 situações em português, 72 em inglês e 72 em francês. Esse aviso já existia e não impede a execução.

## Funcionalidades conferidas

- Exibição somente de situações curadas.
- Mensagem de ausência sem interpretação matemática.
- Curadoria como fonte autoritativa dos metadados.
- Internacionalização em português, inglês e francês.
- Diagrama de composição de coleções.
- Sincronização entre representações.
- Botões de restauração.
- Eixo dos inteiros.
- Logs e visualizações D3.

## Síntese

O foco do projeto foi deslocado corretamente: a questão principal deixa de ser classificar textos automaticamente e passa a ser analisar a interação do sujeito com representações formais já validadas.
