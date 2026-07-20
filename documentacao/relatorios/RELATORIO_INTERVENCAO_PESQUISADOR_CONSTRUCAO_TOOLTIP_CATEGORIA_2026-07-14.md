# Relatório de intervenção do pesquisador — nomenclatura da construção e definição da categoria sob demanda

Data: 14 de julho de 2026  
Ciclo: C35  
Classificação: refinamento visual e operacional com fundamento pedagógico

## Situação observada

A nova tarefa havia sido nomeada **Montar situação-problema**. Embora funcional, o verbo “montar” podia induzir uma leitura predominantemente mecânica da atividade, como se o objetivo fosse apenas encaixar ou ordenar frases. A intenção pedagógica é mais ampla: interpretar o diagrama, reconhecer a categoria semântica, selecionar os blocos compatíveis e construir um enunciado coerente.

Ao mesmo tempo, a definição da categoria permanecia escrita continuamente logo abaixo de seu nome dentro do diagrama. O pesquisador determinou que essa informação fosse mantida como apoio, mas disponibilizada somente quando solicitada pela ação do usuário.

## Intervenção do pesquisador

Foram estabelecidas duas decisões:

1. substituir a nomenclatura visível **Montar situação-problema** por **Construir situação-problema**;
2. retirar a definição permanente da área do diagrama e apresentá-la no `onmouseover` do nome da categoria.

A definição continua sendo a definição localizada e teoricamente vinculada à categoria, por exemplo:

> Vergnaud: uma medida inicial é modificada por uma transformação até uma medida final.

## Justificativa

O verbo **construir** representa melhor a atividade cognitiva requerida. O usuário não deve apenas ordenar blocos, mas relacionar o diagrama, os papéis semânticos e a estrutura textual.

A apresentação da definição sob demanda aplica divulgação progressiva: o apoio permanece acessível, mas não ocupa continuamente a representação nem antecipa mais informação do que o usuário necessita. A área sensível foi limitada ao nome da categoria; mover o mouse em outras partes do diagrama não mostra a definição.

## Implementação

- `ServicoLocalizacao` passou a exibir, em português:
  - aba: **Construir situação-problema**;
  - título: **Construa a situação-problema**;
  - instrução com os verbos **organizar** e **construir**.
- Inglês e francês foram mantidos coerentes com o mesmo conceito de construção.
- `RenderizadorSwingDiagramaAditivo` recebeu uma sobrecarga que permite ocultar a descrição sem alterar o comportamento das demais telas.
- `PainelDiagramaPreenchido`:
  - renderiza somente o nome da categoria;
  - calcula a área real ocupada pelo título, inclusive após redimensionamento;
  - mostra a definição localizada apenas quando o ponteiro está nessa área;
  - usa cursor de mão como indicação discreta de interatividade.
- Os registros novos da atividade passaram a utilizar a nomenclatura `CONSTRUCAO` nos identificadores e nos textos humanos.
- Os nomes internos históricos do pacote e das classes (`montagem`, `TelaMontagemSituacao` etc.) foram preservados para evitar uma refatoração nominal sem benefício funcional e reduzir risco de regressão.

## Preservação teórica

Nenhum elemento foi adicionado ao diagrama formal de Vergnaud. O tooltip pertence à camada de interface e somente apresenta a definição já existente no catálogo de localização. A representação gráfica permanece fiel ao modelo documentado.
