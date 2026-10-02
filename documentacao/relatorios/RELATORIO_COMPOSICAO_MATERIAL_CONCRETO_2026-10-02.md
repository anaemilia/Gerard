# Quadradinhos na conclusão de Composição

Continuação da pendência 3 de `prompt-codex-pendencias-ajuda-visual.md`.
Escopo: observar o gatilho de material concreto e concluir a modelagem por
mouse real. Não se altera domínio, interação, geometria, feedback ou seleção
de ajuda. O domínio fornece o bloqueio; o seletor aplica a disponibilidade;
Swing/React desenham a representação. Os testes apenas observam os fatos.

## Resultado

A conclusão sem rejeições não libera os quadradinhos. Reproduzida no desktop
e web a mesma situação de Lucas, 12 figurinhas do Pokémon e 25 do Digimon,
id `PO_COMPOSICAO_MEDIDAS_figurinhas_755733109`, com resposta 37. As duas
interfaces concluíram de verdade por arraste, edição e confirmação, mantendo
o painel de material concreto vazio. Nenhuma correção de produção foi aplicada.

## Caminhos verificados

- `src/Main.java:8922`: `deveExibirDiagramaComplementar` fornece ao seletor o
  bloqueio da incógnita, sem consultar conclusão.
- `src/Main.java:9563`: o desenho do material retorna quando esse gatilho é
  falso. O painel reservado pode permanecer visível, mas não seus quadradinhos.
- `src/gerard/campoaditivo/representacao/SeletorRepresentacaoComplementar.java`:
  `deveExibir` exige categoria selecionada e escalada no limite.
- `src/gerard/aplicacao/portabilidade/ServicoAtividadeWebComposicao.java:124`:
  `material_concreto_disponivel` depende do bloqueio da incógnita.
- `web-poc/src/App.tsx:529`: o cliente usa essa disponibilidade para apresentar
  o material. A conclusão não é um gatilho alternativo.
- `src/Main.java:6606`: respostas Sim/Não à confirmação de valor rejeitado
  contam como tentativas próprias, por decisão de 2026-09-28. Contar apenas
  valores numéricos digitados não reconstrói a sequência de rejeições.

## Limite da investigação do print antigo

O print `03_composicao_erro.png` mostra valor 1 rejeitado e um diálogo de
confirmação na mesma narrativa. O print 04 isolado não informa o número de
tentativas anteriores, respostas ao diálogo ou restaurações. Não foi localizado
um log ou roteiro original que comprove a afirmação de zero rejeições daquela
rodada. Portanto, não é possível atribuir uma causa histórica certa à imagem,
nem classificá-la como liberação pela conclusão.

A regra vigente é a decisão de 2026-08-07: material concreto somente na terceira
rejeição consecutiva. O apoio já liberado pode continuar ao acertar; a conclusão
não o libera por si. A rodada limpa atual está consistente com essa regra.

## Evidências reproduzíveis

- `tests/graphical/TesteRobotComposicaoSemRejeicoes.java`: Robot com mouse e
  teclado reais, perfil temporário, zero falhas; conclusão, zero rejeições e
  disponibilidade falsa observados.
- `tests/web/e2e_composicao_sem_rejeicoes.mjs`: mouse e teclado no navegador,
  zero falhas; API somente observada, sem chamadas simuladas.
- `evidencias/composicao_sem_rejeicoes_20261002/`: captura desktop, captura web,
  logs e sequência HTTP real do roteiro web.

Não houve commit, push ou alteração no Render/C:\\gd. O resultado cobre esta
rodada limpa; não reconstrói o histórico perdido do print 04.
