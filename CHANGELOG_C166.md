# C166 — alinhamento vertical do gráfico de barras

- O gráfico de barras da categoria `COMPARACAO_MEDIDAS` foi reposicionado verticalmente para iniciar na mesma altura visual do diagrama de Vergnaud.
- O alinhamento agora acompanha o centro vertical do painel, evitando que as barras desçam quando a janela é ampliada.
- Foram mantidos limites para preservar os números acima das barras e os rótulos abaixo delas em janelas compactas.
- A alteração ficou restrita ao gerador de cena da representação complementar, em `gerard.campoaditivo.venn.servico.GeradorCenaDiagramaVenn`.
- Não houve mudança na arquitetura do projeto.
- `Main.java` permaneceu byte a byte idêntico à versão C165.
- Os arquivos de situações curadas e exemplos de treinamento permaneceram inalterados.
