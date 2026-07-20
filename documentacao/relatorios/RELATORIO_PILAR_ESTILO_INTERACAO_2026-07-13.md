# Relatório — Pilar independente de estilo de interação

## Alteração arquitetural

- Criado o pacote `gerard.estilointeracao`.
- Criado `EstiloInteracao.java` como fonte única dos estilos de interação.
- Removida a propriedade conceitual dos estilos do pacote `Scaffolding.proximidade`.
- `ScaffoldingProximidade` passou a apenas aplicar o estilo selecionado sobre a representação.
- `Main.java` passou a consultar `EstiloInteracao`, sem vincular o estilo a um idioma ou diagrama específico.
- Incluído `ARQUITETURA_QUATRO_PILARES_GERARD.txt`.

## Invariantes preservados

- Troca de idioma não restaura o diagrama.
- Troca de representação não restaura o diagrama.
- Troca de estilo de interação não restaura o diagrama.
- Abertura ou fechamento da curadoria não restaura o diagrama.
- Somente `Sortear` e `Restaurar` reiniciam a modelagem.
- Nenhum valor é colocado automaticamente no diagrama.

## Verificação

- Compilação Ant: `BUILD SUCCESSFUL`.
- 83 arquivos-fonte compilados.
- JAR gerado em `dist/GerardNetBeans_D3_Leitura_Redes_Transicoes.jar`.
