# Relatório de regressão — Venn para composição de medidas

## Alteração solicitada

Foi atualizada a representação do lado do Diagrama de Venn para casos de **composição de medidas**. O diagrama anterior é substituído por uma representação específica com duas coleções à esquerda, uma coleção-resultado à direita, seta de composição, quadradinhos arrastáveis e contagens sincronizadas pela posição atual dos quadradinhos.

## Arquivos modificados

- `src/Main.java`
- `src/gerard/campoaditivo/venn/servico/GeradorCenaDiagramaVenn.java`
- `src/gerard/i18n/ServicoLocalizacao.java`
- `scripts/verificar_regressao_gerard.py`

## Comportamento implementado

1. Para `COMPOSICAO_MEDIDAS`, o lado do Venn passa a usar o modelo visual solicitado: duas coleções circulares à esquerda, uma coleção maior à direita e uma seta de composição.
2. Os quadradinhos das coleções de origem continuam arrastáveis e podem ser movidos entre as áreas do diagrama.
3. O `mouseover` sobre os quadradinhos exibe uma dica internacionalizável informando que eles podem ser arrastados.
4. As quantidades exibidas no diagrama são calculadas pela posição atual dos quadradinhos. Ao mover um quadradinho de uma coleção para outra, as contagens são recalculadas no próximo repaint.
5. Na parte inferior do diagrama, aparece uma expressão dinâmica no formato `quantidade1 + quantidade2 = quantidadeResultado`, também baseada na posição atual dos quadradinhos.
6. Para os demais tipos de situação-problema, a estrutura anterior do Diagrama de Venn é preservada.

## Internacionalização adicionada

Foi incluída a chave:

- `ui.tooltip.vennSquareDrag`

Com versões em português, inglês e francês.

## Regressão executada

Comando executado:

```bash
scripts/verificar_regressao_gerard.py
```

Resultado:

- Compilação Ant: `BUILD SUCCESSFUL`
- JAR gerado: `dist/GerardNetBeans_D3_Leitura_Redes_Transicoes.jar`
- Chaves de internacionalização: 547 em português, 547 em inglês e 547 em francês
- Erros críticos: 0
- Avisos: 1

## Aviso mantido

O arquivo de situações-problema ainda possui quantidades diferentes por idioma: 66 em português, 72 em inglês e 72 em francês. Esse aviso já existia e não impede a execução.

## Funcionalidades conferidas por verificação estática

- Troca de idioma preservando estado da tela.
- Internacionalização de textos críticos.
- Sincronização entre diagrama de Vergnaud, eixo e Venn.
- Botões de restauração com textos localizados.
- Tooltip do ponto azul do eixo.
- Tooltip dos quadradinhos do Venn.
- Proteção da limpeza automática de logs.
- Visualizações D3 e separação S/C no log.
