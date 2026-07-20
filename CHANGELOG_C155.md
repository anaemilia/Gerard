# CHANGELOG C155 — conclusão progressiva e discreta

## Objetivo

Substituir o aparecimento abrupto do tip de conclusão por uma sequência visual em duas etapas, padronizada para todos os diagramas de Vergnaud de todas as categorias.

## Novo fluxo

1. O último papel numérico correto conclui a modelagem.
2. Elementos, conectores e itens do Vergnaud recebem o destaque azul consolidado.
3. Surge, abaixo do diagrama, um pequeno selo com a mensagem **“Modelagem concluída”**.
4. O selo entra suavemente e permanece por aproximadamente 1,15 segundo.
5. O selo desaparece.
6. Só então aparece o tip **“Podemos passar para a próxima tarefa?”**, com **Sim** e **Não**.

## Padronização

A sequência é única e centralizada, aplicando-se a:

- Composição de medidas;
- Transformação de medidas;
- Composição seguida de transformação;
- Comparação de medidas;
- Composição de transformações;
- Transformação composta em dois passos;
- Transformação de uma relação;
- Composição de relações.

## Cancelamento

Se a modelagem deixar de estar concluída durante a espera:

- o temporizador é cancelado;
- o selo é ocultado;
- o tip pendente não aparece;
- uma nova conclusão inicia a sequência novamente.

## Arquitetura

Novas classes em `gerard.ui.conclusao`:

- `EstadoFeedbackConclusao`;
- `SequenciadorFeedbackConclusao`;
- `SeloConclusaoModelagem`.

A `Main.java` apenas conecta a avaliação já consolidada aos componentes de apresentação. Temporização, estados da sequência e animação ficam fora da classe principal.

## Internacionalização

Novos textos localizados:

- Português: `Modelagem concluída`;
- Inglês: `Modeling completed`;
- Francês: `Modélisation terminée`.

A pergunta posterior deixou de repetir “Parabéns”, pois a confirmação já foi apresentada no selo.

## Contratos preservados

- conclusão apenas com todos os papéis numéricos corretos;
- `?` não conclui;
- item semanticamente incorreto não conclui;
- destaque azul do diagrama;
- Sim chama o fluxo consolidado de Sortear;
- Não mantém a tarefa e o destaque;
- primeira colocação e bloqueios anteriores permanecem;
- sincronização entre representações permanece;
- dados curados não são alterados.
