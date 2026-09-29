# Estado verificado da migração do log instrumental

Leia esta referência ao alterar um protocolo já migrado ou as identidades de
ação e sequência. O esquema factual e a propriedade do registro permanecem no
`SKILL.md`.

## P2.5A — primeira migração (2026-08-15)

`RegistroAcaoInstrumental` é o valor factual comum da primeira migração. No
protocolo `TEXTO` da incógnita, `IncognitaQuantitativa` o produz com identidade,
protocolo, proprietário, alvo, categoria, resultado, diagnóstico, valores,
regra semântica, contexto instrumental e participantes.
`LoggerInteracaoGerard` grava no máximo uma linha por `action_id`; reenvios são
idempotentes e eventos correlatos não viram novas ações.

O caso entregue ao Modelador preserva `action_id`, avaliação, tipo de erro e
participantes em colunas finais de `diagnosticos_tarefa.tsv`. Arquivos antigos
com 11 ou 15 colunas continuam válidos. Esse estado vale somente para `TEXTO`
da incógnita. A P4.1 ligou o registro e a incógnita ao fluxo real em
2026-08-25.

## P3.1 — sequência de rejeições (2026-08-24)

`PapelQuantitativo` emite novo `action_id` para cada submissão constituída da
incógnita. Rejeições consecutivas usam um `rejection_sequence_id` separado:
três rejeições são três ações e uma sequência. O identificador da sequência
nunca integra o log factual de gestos.

`EventoLogGerard` acrescenta as duas identidades ao final do TSV. Eventos de
limite e apresentação da terceira rejeição carregam a ação e a sequência, mas
não criam outra ação. O logger apenas transporta as identidades produzidas pelo
proprietário semântico.

## P3.2 — restauração (2026-08-24)

“Restaurar elementos fora do diagrama” e “Restaurar diagrama” são ações
instrumentais distintas, cada uma com novo `action_id`. Como coordenam a
tentativa e podem envolver vários papéis, `TentativaModelagemAditiva` produz um
único `RegistroAcaoRestauracaoModelagem`; o botão Swing não é proprietário.

A restauração encerra a sequência anterior, mas não é rejeição dela. Sua linha
deixa `rejection_sequence_id` vazio e preserva as sequências encerradas no
contexto factual. A restauração não recebe C/E matemático.

## P5.1 — classificação da situação (2026-08-25)

`RegistroFactualAcaoInstrumental` permite que logger e Modelador recebam
registros de proprietários diferentes sem reinterpretá-los. A classificação
usa `RegistroAcaoClassificacaoCategoria`, não um papel quantitativo fictício.

`TentativaClassificacaoCategoriaAditiva` produz uma ação para cada escolha e
outra para cada resposta à confirmação. Clique errado e concordância com uma
definição errada são rejeições distintas. Ações corretas não carregam
sequência; discordar corretamente preserva internamente a sequência enquanto a
classificação estiver pendente. Persistência e apresentação não calculam C/E.

## P5.2 — seleção de sinal (2026-08-25)

Cada clique em `+` ou `-` constitui uma ação `SELECIONAR`.
`PapelQuantitativo` produz `RegistroAcaoEscolhaSinalPapelQuantitativo`, e o
`NumeroInteiro` esperado fornece a correspondência. Logger e Modelador não
recalculam C/E.

Toda seleção recebe novo `action_id`. Somente sinais divergentes consecutivos
do mesmo papel compartilham `rejection_sequence_id`; um acerto encerra a
sequência. Não há bloqueio após três erros. O diagnóstico é
`SINAL_DIVERGENTE_DO_PAPEL`. Papéis sem valor normativo não fabricam
diagnóstico e seguem o caminho de compatibilidade sem critério.

## Decisão da usuária — resposta à confirmação da incógnita (2026-09-28)

Pergunta `ui.question.valueMismatch` ("Tem certeza que esse é o valor do
X?"). Decisão literal: *"registre tudo, pois cada resposta sim ou não denota
uma tentativa e precisa ser logada. A ação original tem uma relação 1->n com
tentativas, finaliza quando ele acertar."*

Consequências normativas, para desktop e web igualmente:

- a resposta **Sim** e a resposta **Não** são ambas registradas; nenhuma
  plataforma pode descartar a resposta (o desktop ignorava `opcao`; o web
  não registrava a negação);
- cada resposta é uma tentativa com `action_id` próprio, correlacionada à
  ação original pela identidade de sequência já existente
  (`rejection_sequence_id`), no mesmo padrão da P5.1 (uma ação por escolha e
  outra por resposta à confirmação);
- a sequência (ação original) só se encerra no acerto; restauração continua
  encerrando sem ser rejeição (P3.2);
- o registro é constituído pelo proprietário semântico da incógnita
  (`PapelQuantitativo`/tentativas), nunca pelo diálogo Swing nem pelo
  componente React, que só entregam o fato observado (Sim/Não).

Complemento da usuária (2026-09-28): **cada resposta conta para o limite de
três rejeições consecutivas**. Assim, valor errado → resposta → valor errado
já atinge o limite e escala para o material concreto.

Implementado em 2026-09-28:

- `PapelQuantitativo.registrarRespostaConfirmacaoValorRejeitado`: sem
  sequência aberta não registra; senão registra na mesma sequência com
  `TipoErroPapel.CONFIRMOU_VALOR_REJEITADO` ou `RETIROU_VALOR_REJEITADO`;
- `IncognitaQuantitativa.avaliarRespostaConfirmacao` constitui o
  `RegistroAcaoInstrumental` (`SELECIONAR`, C/E = E para permanecer na
  sequência, regra `regra.incognita.respostaConfirmacaoValorRejeitado`);
- desktop: as duas perguntas `ui.question.valueMismatch` deixam de ignorar
  `opcao`; fechar o diálogo sem responder não é registrado; limite atingido
  na resposta chama `processarLimiteTentativasAtingido`;
- web: rota `/api/acoes/responder-confirmacao-valor`, comum às cinco
  categorias com incógnita (`RespostaConfirmacaoValorWeb`); a pergunta não
  reaparece quando o limite é atingido.

Evidências: `TesteRespostaConfirmacaoValorRejeitado`; log real do desktop
(três linhas, três `action_id`, um `rejection_sequence_id`, `resposta=SIM`);
Playwright na web (caminhos Não e Sim até a escalada).

## Modelagem web persiste pela tentativa (decisão da usuária, 2026-09-29)

"Tem que persistir, pois não ficou azulzinho." A web passa a produzir os
mesmos registros dos proprietários do desktop e a entregá-los à mesma
persistência:

| Ação web | Proprietário que constitui o registro |
|---|---|
| Classificar categoria | `TentativaClassificacaoCategoriaAditiva` |
| Posicionar papel / engatar "?" | `DescritorPapelQuantitativo.avaliarPosicionamento` (via `AvaliadorOrigemDestinoWeb`) |
| Valor da incógnita | `IncognitaQuantitativa.avaliarAcao` (valor esperado da relação estrutural) |
| Resposta Sim/Não | `IncognitaQuantitativa.avaliarRespostaConfirmacao` |
| Sinal do número relativo | `TentativaEscolhaSinalPapelQuantitativo` |
| Operação soma/subtração | `CriterioOperacaoModelagem` (via `AvaliacaoEscolhaOperacaoRelacao.registrarEscolha`) |
| Reiniciar modelagem | `TentativaModelagemAditiva.restaurar` |

Roteamento: `EscopoTentativaWeb` (contraparte de
`Main.persistirAcaoDaTentativa`) entrega só o que a `TentativaModelagemAditiva`
constitui; `PortaRegistroAtividadeWeb` estende `PortaPersistenciaAcaoInstrumental`
e `RegistradorAtividadeWebLogGerard` usa `PersistidorAcaoInstrumentalLogGerard`.
A restauração usa `PersistidorRestauracaoModelagemLogGerard`, agora único para
desktop e web. Origens de evento `WEB_*` distinguem a plataforma. Caminhos
antigos removidos (Regra 3): `diagnosticarValorProposto` nos serviços web, a
comparação local de sinal (`Integer.signum`) e o `action_id` aleatório das
operações avaliadas.

## Restauração web preserva sequências abertas (2026-09-29)

`EscopoTentativaWeb` conserva a fotografia dos `PapelQuantitativo` recebidos
na chamada mais recente de `incorporar` e a entrega a
`TentativaModelagemAditiva.restaurar`. A tentativa captura
`rejection_sequence_id`, constitui um único
`RegistroAcaoRestauracaoModelagem`, encerra localmente as sequências e só
depois o serviço da categoria recria os papéis. A infraestrutura continua
apenas persistindo o registro constituído.

Evidência: `TesteRestauracaoWebEncerraSequencia` rejeita um valor pelo fluxo
web, reinicia e verifica a identidade em
`sequencias_rejeicao_encerradas` e o papel em `papeis_participantes`.
