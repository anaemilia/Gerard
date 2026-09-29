# Resposta à confirmação do valor rejeitado + falhas web encontradas — 2026-09-28

## 1. Resposta Sim/Não como tentativa (decisão da usuária)

Decisões literais: *"registre tudo, pois cada resposta sim ou não denota uma
tentativa e precisa ser logada. A ação original tem uma relação 1->n com
tentativas, finaliza quando ele acertar"*; complemento: a resposta **conta
para o limite de três rejeições**.

| Conhecimento/efeito | Proprietário | Entrada | Saída |
|---|---|---|---|
| Contar a resposta na sequência e no limite | `PapelQuantitativo.registrarRespostaConfirmacaoValorRejeitado` | identidade, Sim/Não, valor | `ResultadoRegistroTentativaPapel` |
| Registro factual da resposta | `IncognitaQuantitativa.avaliarRespostaConfirmacao` | idem + esperado | `RegistroAcaoInstrumental` (`SELECIONAR`, E) |
| Entregar a resposta observada | desktop: `registrarRespostaConfirmacaoValorIncognita`; web: rota `/api/acoes/responder-confirmacao-valor` + `RespostaConfirmacaoValorWeb` | clique Sim/Não | — |

- Sem valor rejeitado (sequência fechada) a resposta não é registrada.
- `TipoErroPapel` ganhou `CONFIRMOU_VALOR_REJEITADO` e `RETIROU_VALOR_REJEITADO`.
- C/E = E também para "Não": é a única forma de a resposta permanecer na
  sequência que encerra no acerto, como decidido. Se a pesquisadora quiser
  outro código C/E para a retirada, a mudança fica isolada em
  `IncognitaQuantitativa.avaliarRespostaConfirmacao`.
- Fechar o diálogo desktop sem responder não é resposta e não é registrado.
- Web: com o limite atingido a pergunta não reaparece (antes reaparecia na
  3ª rejeição); o snapshot traz a escalada para o material concreto, como
  `processarLimiteTentativasAtingido` no desktop.

Consequência prática: valor errado → resposta → valor errado já esgota as
três tentativas (antes eram três valores).

Evidências (`documentacao/relatorios/evidencias/resposta_confirmacao_valor_20260929/`):
- `TesteRespostaConfirmacaoValorRejeitado` (novo): aprovado;
- desktop, Robot real: `desktop_log_tres_tentativas.tsv` — valor (E), resposta
  `resposta=SIM`/`CONFIRMOU_VALOR_REJEITADO` (E), valor (E): três `action_id`,
  um `rejection_sequence_id`, escalada ao material concreto;
- web, Playwright com cliques e arrastes reais: caminho **Não** (1 → 2 → 3,
  `limite_atingido=true`) e caminho **Sim** (idem); capturas `web_*.png`.

## 2. Falha funcional web encontrada e corrigida

**Classificação de uma situação invisível.** O commit `dfef18a` passou a
iniciar sem situação exibida, mantendo o snapshot inicial "apenas para
habilitar os controles de sorteio" — mas os seis botões de categoria
continuavam habilitados. Clicar num deles antes do sorteio classificava a
situação oculta: registrava uma ação C/E sobre um enunciado que o usuário não
viu e, se a escolha divergisse, levava o servidor a
`AGUARDANDO_CONFIRMACAO_CATEGORIA`. A pergunta fica dentro da área da
atividade, que estava oculta, e os sorteios eram desabilitados — **a tela
ficava vazia e travada**. Reproduzido com cliques reais (Playwright) e pela
sequência HTTP.

Correção (`App.tsx`): categorias só habilitadas após o primeiro sorteio;
`escolherCategoria` ignora cliques antes disso. Revalidado: antes do sorteio
as seis categorias ficam desabilitadas e nenhum POST é enviado; depois do
sorteio a classificação funciona.

## 3. Conteúdo de interface inventado (Regra 1 do `CLAUDE.md`)

O diálogo web de classificação mostrava "Confirme sua escolha", "Sim",
"Não" e "A definição da categoria COMPARACAO_MEDIDAS se aplica a esta
situação?" (nome de enum cru), textos sem fonte. Agora usa as mesmas chaves
do desktop (`mostrarQuestionamentoCategoriaErrada` /
`mostrarDialogoConfirmacaoSimNao`): `ui.question.category.<tipo>`,
`ui.dialog.confirm`, `ui.completion.yes/no`, enviadas pelo servidor.
Revalidado na interface.

## Verificações

- 606 fontes compiladas; 145 testes Java, 145 aprovados em execução
  individual. `TesteRevelarOcultarEixoWeb` é intermitente **também no build
  anterior** (1 falha em 5 execuções: depende de sortear uma situação
  específica em 60 tentativas) — pendência própria;
- `verificar_regressao_gerard.py`: 280 OK, única falha ambiental (Ant);
  novo bloco de verificação da decisão;
- `tsc --noEmit` do frontend aprovado; checker de localidade aprovado, sem
  dívida nova (as 3 ligações web registradas continuam na linha de base).

## Observado, não alterado

- Na escalada web de Composição de medidas, o "?" do enunciado e a caixa
  Todo passam a mostrar "0" (rascunho da contagem), e a coluna de
  quadradinhos da Parte 2 parece cortada (12 unidades, menos visíveis).
- Eixo dos inteiros com duas fontes de verdade (cliente × rota do domínio sem
  consumidor) continua pendente de decisão.
