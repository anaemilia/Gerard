# Confirmação de valor na situação dos morangos

Relato da usuária: o diálogo aparece e some na sequência, possivelmente
afetando a contagem. Reprodução local isolada em 2026-10-02, sobre a versão
publicada f1dd16b; sem alteração de produção.

Situação: `PO_TRANSFORMACAO_MEDIDAS_frutas_93128185`, Nádia, 10 morangos,
transformação -3, resultado esperado 7. Mouse real arrastou os três papéis,
selecionou sinal negativo e abriu a edição por duplo clique. Teclado enviou
valor 1 com um Enter. Após 6,5 segundos, a confirmação continuava visível:
uma submissão e zero chamadas de resposta à confirmação.

O clique explícito em Não produziu uma chamada de confirmação. Em seguida,
nova proposta errada, valor 2, atingiu o limite e não apresentou outra
pergunta. Nenhuma confirmação adicional foi registrada. O novo duplo clique,
valor correto 7 e Enter concluíram a atividade. Capturas e sequência HTTP
ficaram em `evidencias/confirmacao_morangos_20261002/`; roteiro em
`tests/web/e2e_confirmacao_morangos.mjs`.

A sequência observada é consistente com a decisão de 2026-09-28:
submissão errada e resposta Sim/Não são tentativas distintas. Assim, valor
1 rejeitado + Não + valor 2 rejeitado alcançam três tentativas, embora apenas
dois números errados tenham sido digitados.

Não se reproduziu fechamento espontâneo nem confirmação automática nesta
sequência. Isso não invalida o relato da usuária: falta identificar o gesto
exato que diferencia sua execução. Foi solicitada informação sobre Enter
único/repetido e se houve resposta explícita ao diálogo. Não foi aplicada uma
correção presumida nem alterada a regra de contagem.

## Correção autorizada posteriormente pela usuária: “corrija”

Recorte: adaptar os eventos de teclado e o ciclo HTTP da edição web.
Proprietários: `EdicaoValorFigura` observa Enter/clique; `App` sequencia o
envio pela API e mantém o passo visual; o domínio conserva a avaliação e a
contagem. Entradas: gestos explícitos e resultado HTTP. Saídas: uma proposta
ou resposta por envio e a pergunta preservada enquanto o envio não concluir.
Nenhuma mudança no backend, texto, geometria ou política pedagógica.

- Enter repetido não envia outras propostas. Um bloqueio síncrono cobre o
  intervalo anterior ao próximo render React e o envio de Sim/Não.
- Não se admite outra proposta enquanto a anterior aguarda confirmação.
- A resposta não apaga o diálogo antes de o HTTP terminar. Falha de rede
  preserva a pergunta e permite tentar a mesma opção novamente.
- Radio usa ativação por clique, inclusive teclado, para permitir a mesma
  resposta após falha de rede; não exige trocar a opção para tentar novamente.

Verificação por mouse/teclado reais em localhost, com respostas de proposta
atrasadas em 600 ms e uma confirmação interrompida antes de chegar ao servidor:
Enter mantido produziu uma submissão; após 6,5 s havia uma pergunta e zero
confirmações automáticas; falha de rede conservou a pergunta; novo clique em
Não enviou uma confirmação; proposta seguinte alcançou o limite; valor 7
concluiu a modelagem. Build TypeScript/Vite, grafo e localidade aprovados.
Evidências em `evidencias/confirmacao_morangos_20261002/correcao/`.

Limite: a sequência exata do fechamento espontâneo relatado pela usuária não
foi reproduzida. Foram corrigidas as falhas de proteção do envio e de
preservação da confirmação em erro HTTP, verificadas pelo roteiro acima.
