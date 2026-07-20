# Regressão de usabilidade e coerência dos quatro pilares

## Objetivo

Incorporar ao processo permanente de regressão do Gérard os critérios de legibilidade, contraste, tamanho de fonte, clareza dos rótulos, organização visual, navegação previsível, objetividade dos tooltips, preservação do estado e consistência entre idiomas e representações.

## Alteração funcional aplicada

O controle anteriormente rotulado como `Tipo` passou a ser apresentado como `Categoria` em português, inglês e francês. O tooltip também foi tornado explícito: a seleção define a categoria de treinamento e inicia uma nova situação curada.

Essa mudança reduz ambiguidade porque o controle não escolhe apenas uma forma visual; ele inicia uma atividade pertencente à categoria selecionada.

## Critérios incorporados

Os nove critérios foram incluídos no `CHECKLIST_REGRESSAO_GERARD.md` como condições permanentes. O script automatizado passou a verificar:

- paridade das chaves de internacionalização;
- rótulo `Categoria` nos quatro idiomas;
- tooltip objetivo da seleção de categoria;
- presença da categoria acima do enunciado;
- hierarquia mínima de fontes do enunciado e da categoria;
- contraste mínimo entre texto principal, texto secundário e fundo;
- separação entre fluxo de nova atividade e fluxo de preservação de estado;
- ausência de preenchimento automático explícito do item desconhecido no diagrama;
- preservação das rotinas compartilhadas de sincronização.

## Limite do teste automatizado

Sobreposição, leitura em diferentes resoluções, compreensão dos tooltips e previsibilidade percebida ainda exigem teste manual. Esses itens foram acrescentados ao fluxo mínimo obrigatório.

## Resultado esperado

A interface deve permanecer coerente com quatro pilares independentes: modelagem, representações, estilos de interação e exibição textual. Uma alteração em um pilar não pode apagar ou corromper o estado de outro, exceto nas ações explicitamente definidas como início de nova atividade.
