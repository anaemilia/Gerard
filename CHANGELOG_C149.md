# C149 — Ativação exclusiva do processo de transformação

- Removeu do código-fonte a view legada de três contêineres da transformação.
- A categoria `TRANSFORMACAO_MEDIDAS` usa exclusivamente o processo com:
  - contêiner natural do estado inicial;
  - canal horizontal de passagem;
  - funil superior para inserção positiva;
  - funil inferior para retirada negativa;
  - contêiner natural do estado final;
  - quadradinhos já utilizados pelo Gérard.
- A zona central permanece apenas como região semântica/interativa transparente.
- A política de sinal foi movida para o pacote ativo `transformacao.processo`.
- Preservadas sincronização, controles, arraste, eixo, texto, Vergnaud e conclusão.
