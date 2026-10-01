# Inversão de dependência das portas web — 2026-09-30

Conclui o item 1 e o item 2 de `LEVANTAMENTO_PENDENCIAS_ESTADO_ATUAL_2026-09-29.md`.
Não altera regra de domínio, texto de interface, fluxo de tarefa nem protocolo de
interação: só muda **quem instancia** dois adaptadores.

## Registro exigido pela spec de localidade

| | Ajuda contextual ("E agora?") | Questionamento de posicionamento |
|---|---|---|
| Proprietário do conhecimento | `ScaffoldingQuestionamento` e as mensagens `ui.help.*` / `ui.question.*` (scaffolding portátil, sem Swing) | `ScaffoldingQuestionamento` (compatibilidade origem/destino) |
| Porta (aplicação) | `PortaAjudaContextual` | `PortaQuestionamentoPosicionamento` |
| Entrada | área e intenção, por nome | chaves do papel de origem e do alvo, papel do elemento no diagrama, categoria escolhida |
| Saída | projeção da área, nome da área, rótulo da opção e mensagem | `ResultadoQuestionamentoPosicionamento` (aplicável, correto, mensagem) |
| Adaptador concreto (infraestrutura) | `gerard.infraestrutura.web.scaffolding.AdaptadorAjudaContextualWeb` | `gerard.infraestrutura.web.scaffolding.AdaptadorQuestionamentoPosicionamentoWeb` |
| Consumidor (aplicação) | `ServicoSorteioAtividadeWeb` | `AvaliadorOrigemDestinoWeb`, usado pelos 6 `ServicoAtividadeWeb*` |
| Raiz de composição | `ServidorPrototipoWeb` | `ServidorPrototipoWeb` |
| Valor neutro na aplicação | `PortaAjudaContextual.NENHUMA` (não projeta nada nem fabrica texto) | `PortaQuestionamentoPosicionamento.NAO_APLICAVEL` (não questiona nenhuma soltura) |

## O que mudou

- `AvaliadorOrigemDestinoWeb` deixou de ser utilitário estático: recebe a porta por construtor.
- Os 6 serviços (`Composicao`, `TransformacaoMedidas`, `ComparacaoMedidas`,
  `TransformacaoRelacao`, `ComposicaoTransformacoes`, `ComposicaoRelacoes`) ganharam um
  construtor com `PortaQuestionamentoPosicionamento`; os construtores anteriores
  delegam com o valor neutro.
- `ServicoSorteioAtividadeWeb` recebe as duas portas e repassa a de questionamento aos
  serviços que cria. Deixou de importar infraestrutura.
- `ServidorPrototipoWeb` (infraestrutura) entrega as implementações reais.
- O precedente seguido é o de `PortaRegistroAtividadeWeb.NENHUMA`.

## Verificação

- `scripts/verificar_localidade_arquitetural.py` passou a rejeitar
  `gerard.aplicacao.* -> gerard.infraestrutura.*` (autoteste incluído; sem entrada em
  linha de base). Reintroduzindo em memória a importação antiga, a regra a rejeita.
- `TesteInversaoPortasWeb` (novo): nenhuma fonte de `gerard/aplicacao` importa ou
  instancia infraestrutura; sem portas a aplicação não fabrica mensagem; com os
  adaptadores reais a ajuda responde e a origem incompatível é questionada.
- Testes web: 18/18 antes e 19/19 depois (inclui o novo).
- Suíte de `tests/java` sem `Robot`: 151/151 aprovados. Dois testes de Swing
  (`TesteAbaMontagem`, `TesteDialogoCuradoriaNarrativaRica`) exigem display e falham
  com `HeadlessException` em modo headless também na linha de base; passam com display.
- Servidor real com a composição nova, navegador Chrome, eventos reais de mouse:
  soltar o "?" na caixa de Parte 1 é rejeitado com a mensagem do scaffolding, e a
  caixa permanece vazia.

## Efeito colateral deliberado e risco

Os construtores sem porta usam o valor neutro; portanto um serviço construído sem a
raiz de composição **não valida** origem/destino. Em produção isso não ocorre
(`ServidorPrototipoWeb` é o único construtor de `ServicoSorteioAtividadeWeb` em
`src/`); os testes que dependem da validação passam o adaptador real
(`TesteServicoAtividadeWebComposicao`, `TesteModelagemWebPersisteNaTentativa`).

## Achado fora de escopo (não alterado)

O aviso de soltura incompatível aparece no web com `<b>` literal ("…ao <b>Parte 1</b>
da <b>Composição de medidas</b>?"): `AvisoPosicionamentoFigura.tsx` mostra a mensagem
como texto simples, e o desktop a exibe como HTML. É apresentação, anterior a este
trabalho (`fd0cee4`); a correção depende de autorização.

## Pendente

Revalidar com Playwright os fluxos de ajuda contextual e de posicionamento incorreto
(item 3 do levantamento): esta rodada usou Chrome com CDP para o soltar incompatível;
o fluxo completo do menu "E agora?" no navegador não foi repetido.
