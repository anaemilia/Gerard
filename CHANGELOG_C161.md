# CHANGELOG C161

## Janela principal

O Gérard passa a iniciar maximizado com `JFrame.MAXIMIZED_BOTH`. A barra de título e os controles normais do sistema operacional são preservados. O tamanho restaurado padrão continua sendo 1240 × 760, com mínimo de 960 × 620.

A configuração foi isolada em:

- `gerard.ui.janela.ConfiguradorJanelaPrincipal`

## Comparação entre categorias

A janela comparativa deixou de usar tamanho fixo de 1280 × 760. Seu tamanho agora considera:

- 92% da largura da janela principal;
- 85% da altura da janela principal;
- limite máximo de 90% da área útil do monitor;
- tamanho mínimo apenas quando a área do monitor permite;
- centralização sobre a janela principal;
- reaproveitamento do último tamanho escolhido na sessão.

O dimensionamento foi isolado em:

- `gerard.ui.janela.DimensionadorJanelaComparacaoCategorias`

A `Main.java` apenas compõe e aciona esses serviços de apresentação.
