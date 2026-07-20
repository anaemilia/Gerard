# Relatório de atualização — sincronização do Diagrama de Venn

## Alteração implementada

Foi adicionada sincronização automática entre o Diagrama de Venn, o diagrama de Vergnaud e o eixo dos inteiros.

## Comportamento incluído

- O Diagrama de Venn passou a criar automaticamente os quadradinhos correspondentes aos valores da situação-problema.
- Os quadradinhos são reconstruídos quando os valores do diagrama de Vergnaud são alterados por:
  - arraste de itens numéricos do enunciado para o diagrama;
  - edição direta de valores no diagrama;
  - escolha de sinal em número relativo;
  - movimentação do ponto no eixo dos inteiros.
- A reconstrução usa assinatura interna para evitar reinicialização desnecessária durante o arraste manual dos quadradinhos.
- Em situações compostas, o Venn usa a parte de transformação atualmente representada no diagrama para manter coerência com a relação estado inicial — transformação — estado final.
- A descrição do Diagrama de Venn foi atualizada e internacionalizada em português, inglês e francês.

## Preservação das funcionalidades anteriores

- A edição por duplo clique nos quadradinhos do Venn foi mantida.
- O arraste manual dos quadradinhos do Venn foi mantido.
- A sincronização já existente entre número relativo, menu de sinal e eixo dos inteiros foi preservada.
- A compilação com Ant/NetBeans foi executada com sucesso.

## Teste realizado

Comando executado:

```bash
ant clean jar
```

Resultado: `BUILD SUCCESSFUL`.
