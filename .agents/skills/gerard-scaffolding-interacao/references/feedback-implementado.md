# Feedback implementado

Leia esta referência ao alterar tremor, som, tips, selos, timing de sucesso ou
fluxos de soltura em destino ocupado. Finalidade pedagógica e seleção do apoio
permanecem no `SKILL.md` proprietário.

## Cores de significado

As convenções verificadas em `gerard/ui/UITemaGerard.java` são:

- azul para sucesso (`COR_SUCESSO = new Color(74,130,201)`);
- vermelho para erro (`COR_ERRO = new Color(200,40,40)`).

## Feedback de erro

- O erro ocorre ao soltar, nunca durante o arraste. `mouseDragged` apenas
  atualiza o texto de um tip já ativo; o disparo ocorre a partir de
  `mouseReleased` por `processarQuestionamentoPosicionamento`.
- O padrão atual combina tremor e som, sem diálogo extenso.
  `ScaffoldingFeedbackMultissensorialErro` realiza o deslocamento temporizado
  e o beep.
- Depois do erro, a peça permanece onde foi solta. `pararTremor()` restaura a
  posição-base do tremor, não uma posição semanticamente correta.
- Um tip curto e fixo pode acompanhar o erro. Não use ícone ou seta que possa
  ser confundido com a seta da representação de Vergnaud.
- Destino já ocupado deve produzir o mesmo feedback em vez de fazer a peça
  desaparecer. Essa garantia existe tanto no fluxo Venn de `Main` quanto em
  `PainelComposicaoMedidasDesktop`; alterar um não valida automaticamente o
  outro.
- Desde a decisão de 2026-09-06, `SeloErroModelagem` acrescenta um círculo
  vermelho vazio na primeira rejeição consecutiva da incógnita. Ele não
  substitui tremor e som. Compartilha geometria e posicionamento com
  `SeloConclusaoModelagem` por `PosicionadorSeloDiagrama`, mas não possui a
  marca interna. É ocultado ao corrigir a incógnita ou trocar a situação.

## Feedback de sucesso

Correção web de 2026-10-02, após relato da usuária de erro → Sim → ações
sem retorno: o cliente materializa a mensagem de limite já decidida e
localizada pelo servidor tanto na proposta quanto na confirmação. Esse
aviso permanece até resposta correta, restauração ou novo sorteio; não é
descartado pelo temporizador dos avisos de posicionamento. A disponibilidade
de historinhas não participa dessa decisão nem altera a contagem do domínio.

Verificação web em 2026-10-02, autorizada pela usuária para corrigir a
confirmação de valor: envio repetido de Enter não constitui outra proposta;
o passo Sim/Não é preservado durante o envio e em falha HTTP. O adaptador
permite tentar novamente a mesma opção após uma falha, sem inventar resposta
ou mudar a contagem do domínio. Roteiro e limites:
`documentacao/relatorios/RELATORIO_CONFIRMACAO_MORANGOS_2026-10-02.md`.

- `SequenciadorFeedbackConclusao` preserva o atraso validado de 1150 ms antes
  da solicitação de avanço.
- O indicador de avanço é ocultado no cancelamento e não permanece fixo.
- Essas decisões seguem em teste com usuários e não devem ser tratadas como
  definitivas sem nova confirmação.
