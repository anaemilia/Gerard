# Refatoração validada dos elementos gráficos

Data: 14/07/2026

## Alterações consolidadas

- dez classes gráficas foram mantidas fora de `Main.java`, no pacote `gerard.campoaditivo.diagrama.elementos`;
- o padrão visual foi centralizado em `gerard.ui.UITemaGerard`, preservando 12 pt nos botões da barra principal e 14 pt em negrito nos itens dos menus;
- as decisões recentes dos menus foram preservadas: ausência de setas textuais, abertura por `onmouseover`, realce visual e clique alternativo;
- foi criado `scripts/testar_elementos_diagrama.py`, que compila o projeto e executa um teste headless das classes extraídas;
- o teste verifica construção, detecção geométrica, movimento e desenho em `BufferedImage`.

## Evidências de validação

- `ant clean jar`: BUILD SUCCESSFUL;
- 107 arquivos Java compilados;
- teste headless: 15 verificações aprovadas;
- `scripts/verificar_regressao_gerard.py`: aprovado;
- vínculos e metadados: 210 versões e 72 grupos conceituais coerentes;
- integridade do ZIP: sem erros.

## Limite da evidência

O teste headless reduz o risco de regressão das classes gráficas extraídas, mas não substitui uma inspeção humana completa da interface em execução. A versão deve continuar sendo observada em uso para confirmar posicionamento, interação por mouse e legibilidade em diferentes resoluções.
