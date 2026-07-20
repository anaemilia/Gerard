# Relatório técnico — C122

## Correção: sinal do valor relativo sem quantidade negativa

**Data:** 16 de julho de 2026  
**Base:** C121 — arraste com massa, mola, amortecimento e momento  
**Versão resultante:** C122

## Problema observado

Na comparação de medidas, ao selecionar o sinal negativo do valor relativo, o sistema propagava diretamente a relação aritmética para os demais papéis. No caso reproduzido na interface, o referido era 6 e o valor relativo passou a -8; a atualização genérica calculou o referendo como -2. Isso confundia uma **relação assinada** com uma **quantidade de medida**.

A regra de domínio correta é:

- transformação e valor relativo podem ser positivos ou negativos;
- estado inicial, estado final, referido, referendo, parte 1, parte 2 e todo representam quantidades e devem permanecer maiores ou iguais a zero;
- a escolha de sinal não pode modificar os dados curados;
- uma escolha incompatível deve ser rejeitada antes de ser propagada às representações.

## Causa

A rotina de consistência tratava diferentes categorias por uma relação genérica entre três termos e aceitava que o termo derivado recebesse valor negativo. A validação ocorria depois de parte da atualização visual, o que permitia que um sinal válido para o valor relativo produzisse um estado inválido em um papel cardinal.

## Correção aplicada

A C122 introduz validação preventiva e dependente da categoria:

1. o novo sinal é testado antes de alterar o modelo compartilhado;
2. a quantidade dependente é calculada sem publicar o resultado;
3. quando o resultado seria negativo, a alteração é bloqueada;
4. o último valor relativo seguro é restaurado;
5. nenhuma quantidade negativa é enviada ao texto, ao diagrama de Vergnaud, ao diagrama de coleções ou ao gráfico de barras;
6. o estado semântico compartilhado possui uma segunda barreira que descarta valores negativos recebidos para papéis cardinais;
7. categorias de relações continuam aceitando valores assinados quando o sinal pertence ao próprio elemento relacional.

A mensagem de bloqueio foi localizada em português, inglês e francês.

## Invariantes preservados da C120 e da C121

- com Vergnaud semanticamente vazio, `+` e `−` permanecem inabilitados;
- os controles são recalculados após arraste, digitação e preenchimento automático;
- cada controle opera independentemente;
- `+` cria unidades somente até o limite curado ou derivado;
- `−` remove unidades até zero, sem produzir quantidade negativa;
- operações que romperiam a relação aditiva são bloqueadas;
- texto, Vergnaud e representação complementar são projeções do mesmo estado semântico;
- os dados curados permanecem imutáveis;
- a física de arraste continua exclusivamente visual, com soltura exata antes da validação.

## Principais arquivos alterados

- `src/Main.java`
- `src/gerard/Scaffolding/reacao/ScaffoldingReacaoRepresentacoes.java`
- `src/gerard/campoaditivo/sincronizacao/EstadoSemanticoCompartilhado.java`
- `src/gerard/i18n/ServicoLocalizacao.java`
- `scripts/testes/TesteSinalRelativoSemQuantidadeNegativa.java`
- `scripts/testar_sinal_relativo_sem_quantidade_negativa.sh`

## Verificações executadas

A compilação completa pelo Ant/NetBeans foi concluída. Foram executados os seguintes testes:

- `testar_sinal_relativo_sem_quantidade_negativa.sh`;
- `testar_regra_controles_quantidades_positivas.sh`;
- `testar_sincronizacao_controles_por_categoria.sh`;
- `testar_bloqueio_adicao_antes_vergnaud.sh`;
- `testar_arraste_fisico.sh`;
- `testar_arraste_fisico_ui.sh`.

Casos específicos verificados:

- `6 + (-8) = -2` é rejeitado antes da propagação, pois produziria uma quantidade negativa;
- `9 + (-6) = 3` é aceito quando a relação assinada preserva quantidades não negativas;
- valores negativos injetados em papéis de medida são removidos pelo estado compartilhado;
- relações assinadas permanecem válidas nas categorias em que o sinal pertence ao elemento relacional;
- os arquivos de dados curados apresentam os mesmos hashes da C121.

A compilação apresenta apenas os avisos já existentes sobre alvo Java 8 e uso de API depreciada em uma classe não relacionada à correção.

## Documentação

O artigo foi atualizado para o corpus C122, com 113 ciclos, e a análise de complexidade registra o custo constante da validação preventiva. Os dois PDFs foram compilados, renderizados e inspecionados. A tabela extensa do artigo foi ajustada para evitar sobreposição entre colunas.

## Validação visual restante

A suíte automatizada reproduz a regra e o fluxo da interface. Ainda é recomendável executar a C122 no ambiente Windows de uso do Gérard para conferir a leitura da mensagem localizada e a resposta visual do radio button em escala e configuração gráfica reais.
