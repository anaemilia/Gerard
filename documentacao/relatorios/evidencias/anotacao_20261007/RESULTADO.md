# Passo 1 do desenho — anotação flutuante: decisão, reconciliação e desenho separados (2026-10-07)

`desenharAnotacaoMouseOver` (155 linhas, decidia a prioridade das 5 fontes e encerrava a dica de posicionamento dentro do
paint) virou: `DecisaoAnotacaoMouseOver` (decisão pura, teste Java com as 32 combinações), `reconciliarAnotacaoDoMouseOver`
(validade por fonte + encerramento da dica; roda antes do desenho, ainda dentro de paintComponent),
`obterMensagemAnotacao`, `obterAncoraAnotacao` e `desenharBalaoAnotacao` (só texto e ponto).

Prova: `TesteMacacoAnotacaoMouseOver` (mouse real; 3 situações; 212 capturas da janela: mouse-over nas palavras e peças,
"Ver dica" x3, arrasto de palavras a elementos certos/errados, peças depois de modelar). Antigo = cópia do commit 5a2d15c.

- Capturas pixel a pixel, código antigo x novo, MASCARANDO a faixa do texto da situação-problema (y 205–245): 212 de 212
  idênticas.
- Sem máscara todas diferem, e só nessa faixa (bbox x≈194–861, y≈215–233): a linha do texto varia 1 px entre execuções
  também no código antigo (duas gravações do código antigo: 2 de 213 hashes iguais). Não é efeito da mudança.

Limites: a faixa mascarada não foi comparada (ruído conhecido); o robot não provoca as fontes "limite de quantidade" e
"sinal divergente" (cobertas só pelo teste Java da decisão); PNGs não versionados (~20 MB).
Pendente: decidir se o encerramento da dica sai do ciclo de pintura para os eventos (muda o momento do fechamento).
