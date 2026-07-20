# Relatório técnico — C155

## Solicitação

Tornar a conclusão da modelagem menos abrupta e aplicar o mesmo comportamento a todos os diagramas de Vergnaud de todas as categorias.

## Implementação

A apresentação da conclusão foi dividida em duas etapas:

1. **Confirmação visual discreta**: pequeno selo “Modelagem concluída”, abaixo do diagrama, com entrada gradual.
2. **Decisão de continuidade**: após cerca de 1,15 segundo, o selo desaparece e surge o tip “Podemos passar para a próxima tarefa?”, com Sim/Não.

## Abrangência

O fluxo é acionado pelo verificador central de conclusão e, portanto, vale para as oito categorias:

- Composição de medidas;
- Transformação de medidas;
- Composição seguida de transformação;
- Comparação de medidas;
- Composição de transformações;
- Transformação composta em dois passos;
- Transformação de uma relação;
- Composição de relações.

## Classes criadas

- `src/gerard/ui/conclusao/EstadoFeedbackConclusao.java`
- `src/gerard/ui/conclusao/SequenciadorFeedbackConclusao.java`
- `src/gerard/ui/conclusao/SeloConclusaoModelagem.java`

## Regras preservadas

- o diagrama só conclui com todos os papéis numéricos corretos;
- a interrogação não conclui;
- itens em papéis incorretos não concluem;
- a primeira colocação e os bloqueios anteriores continuam válidos;
- Sim continua acionando Sortear na mesma categoria;
- Não preserva a situação atual e o destaque azul;
- nova manipulação cancela o selo e o tip pendente;
- a sequência ocorre uma única vez por ciclo de conclusão;
- dados curados permanecem intactos.

## Internacionalização

- PT: “Modelagem concluída” / “Podemos passar para a próxima tarefa?”
- EN: “Modeling completed” / “Can we move on to the next task?”
- FR: “Modélisation terminée” / “Pouvons-nous passer à la tâche suivante ?”

## Validações automatizadas

- compilação completa de 252 fontes Java;
- conclusão numérica consolidada;
- cobertura das oito categorias;
- sequência selo → tip;
- cancelamento antes do tip;
- posicionamento do selo em diagramas simples e compostos;
- posicionamento do tip;
- textos em português, inglês e francês;
- Sim aciona Sortear e preserva a categoria;
- estado semântico compartilhado;
- arraste físico;
- bloqueio antes do primeiro posicionamento;
- roteiro visual por categoria;
- regressão estrutural completa;
- comparação SHA-256 dos 13 arquivos de dados curados: idênticos.

## Inspeção visual

Foi renderizada uma prévia headless do selo. A conferência final no Windows deve observar a suavidade temporal, o posicionamento em resoluções reais e a resposta dos radio buttons.
