# "Estado modificado" — alvo da incógnita recalculado a partir dos papéis-dado atuais

Data: 2026-08-06. Motivado por relato do usuário: ao mudar a Transformação de um problema (curada em 5) para 2 via o eixo x, o diagrama ficou internamente consistente (16+2=18), mas a pergunta de confirmação da incógnita ("Tem certeza que esse é o valor do Estado final?") comparava contra o curado original do problema (16+5=21), gerando uma divergência falsa do ponto de vista de quem está operando a partir do novo valor.

## Decisão de escopo (conversa, formalizada aqui)

O usuário esclareceu que a interface permite alterar um papel-dado (não só a incógnita) por qualquer representação, e que o estudante pode legitimamente querer continuar operando a partir desse novo valor — não é necessariamente um erro. Decisão explícita (`AskUserQuestion`): quando isso acontece, o alvo esperado da incógnita deve ser **recalculado** a partir dos papéis-dado atuais, não travado no curado original do problema.

## Investigação (só leitura, antes do código)

- `valorDigitadoCorrespondeAoCurado`/`confirmarValorIncognitaAceito`/`incognitaAguardandoConfirmacaoDeValor` têm exatamente 2 pontos de disparo em todo o código, ambos ligados a uma submissão explícita do estudante (`editarNumeroNatural`, duplo-clique + digitação; ou o menu de sinal equivalente para número relativo) — nunca disparados passivamente por preenchimento automático do sistema. Confirmado via grep: `registrarPreenchimentoPeloProtocoloMouseTexto()` só é chamado nesses 2 lugares.
- `obterValorCuradoParaPapel(papel)` já é agnóstico de papel (usa `SemanticaCuradaSituacao.buscar`), mas só era chamado com `obterPapelIncognitaAtual()` — nunca para os papéis-dado.
- `EstadoSemanticoCompartilhado.snapshot()` (já usado em ~10 pontos de `Main.java`) expõe o estado corrente a qualquer momento, incluindo o valor calculado no "primeiro preenchimento" — seja pela resolução rica (Fase B1, 6 dos 8 tipos) seja pelo algoritmo genérico de fallback (os 2 tipos "Em construção", hoje inalcançáveis pela UI, mas que também preenchem o Snapshot quando aplicável). Não é um cálculo novo — é o mesmo valor que já preenche a incógnita no diagrama.
- `obterIndiceIncognitaProtegidaNoEstadoCompartilhado()` já resolve o papel da incógnita para o índice (0/1/2) usado pelo Snapshot.

Toda a peça necessária já existia; a mudança foi só conectar o alvo da confirmação a ela em vez do curado fixo.

## O que foi feito

`Main.java`, `valorDigitadoCorrespondeAoCurado`: passou a comparar contra `obterValorAlvoParaPapel(papel)` em vez de `obterValorCuradoParaPapel(papel)` diretamente.

Novo método `obterValorAlvoParaPapel(String papel)`: quando `papel` é a incógnita atual e o Snapshot corrente tem um valor conhecido no índice correspondente, retorna esse valor (recalculado a partir dos papéis-dado atuais). Caso contrário — papel não é a incógnita, ou o Snapshot ainda não tem os papéis-dado necessários conhecidos —, cai em `obterValorCuradoParaPapel` (comportamento anterior, inalterado).

## Verificação

- Compilação completa: 435 arquivos, 0 erros.
- Não há harness automatizado para este ponto (depende de `ItemTextoArrastavel`/Swing/diálogo de confirmação — a mesma limitação já registrada para a extração do desenho de estilo de interação). Verificação por rastreamento manual dos cenários:
  1. **Sem desvio** (papéis-dado nos valores curados): Snapshot recalculado == curado — comportamento idêntico ao anterior, incluindo continuar acusando um erro genuíno do estudante.
  2. **Com desvio** (cenário relatado: Transformação 5→2): Snapshot recalculado = 18 (16+2); estudante que digita 18 é aceito — resolve o relato. Estudante que digita algo diferente de 18 continua sendo barrado — a validação não foi desligada, só o alvo mudou.
  3. **Papéis-dado ainda não conhecidos**: Snapshot não tem o índice da incógnita conhecido — cai no curado, sem mudança de comportamento.
  4. **Tipos "Em construção"**: inalcançáveis pela UI hoje — sem efeito prático; o método é agnóstico ao tipo, então não há necessidade de tratamento especial para eles.

## O que não foi feito

`obterValorCuradoParaPapel` (usado por `sinalEscolhidoCorrespondeAoCurado`, separado) não foi alterado. O conceito "estado modificado" foi implementado só como o recálculo do alvo da incógnita — não foi criado um novo enum/flag visível de auditoria distinguindo papéis-dado modificados dos curados; se isso vier a ser necessário para pesquisa (registrar que um papel-dado divergiu do curado, independente de a incógnita ter sido respondida), fica para uma iteração futura, a pedido.
