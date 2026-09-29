# Fase 7.13 — Revelação pela lupa de Relações (2026-09-29)

## Autorização

Recorte proposto com diff prévio e autorizado explicitamente pela usuária em
2026-09-29 ("sim").

## Achado da inspeção

A mecânica da lupa já estava localizada antes desta fase:

| Conhecimento ou efeito | Proprietário | Situação |
|---|---|---|
| Hit-test da lupa de cada papel | `PaineisEixosRelacoes.processarPressionamentoLupa` (representação desktop) | já localizado |
| Transição fechado → revelado e guarda `podeRevelar()` | `ControleVisibilidadeEixoPapel` (portátil; também usado pela web) | já localizado |
| Posição inicial do painel | `prepararPainelEixoRelacao` (tela) | compartilhado com a ativação |
| Log `LUPA_EIXO_RELACAO`, limpeza de foco, repaint | inline em `mousePressed` | apresentação |

Criar um handler portátil dividiria um proprietário coerente (a sequência
"pode revelar e o clique cai na lupa → revelar" depende da área concreta da
lupa) — seria deslocamento sem ganho de localidade. Por isso **nenhum handler,
porta ou tipo novo foi criado**.

## O que mudou

Somente a apresentação saiu do corpo de `mousePressed` para o método
`apresentarRevelacaoEixoRelacaoPelaLupa`, com o mesmo conteúdo e a mesma
ordem: preparar o painel, registrar o log, limpar foco, repaint.
`mousePressed` passou a apenas rotear.

**Esta redução é organização do roteamento, não componentização de
mecânica.** O ratchet de `mousePressed` caiu de 188 para 174, mas isso não é
apresentado como prova de localidade: a mecânica já estava no lugar certo.

Arquivos: `src/Main.java`, `scripts/verificar_regressao_gerard.py` (ratchet e
bloco "Fase 7.13"), `tests/graphical/TesteRobotLupaEixoRelacao.java` (novo).

## Validação

- Build: 617 fontes. Suíte Java: 151/151.
- `verificar_regressao_gerard.py`: bloco "Fase 7.13" 3/3; única falha, Ant
  ausente na VM (ambiental). `verificar_localidade_arquitetural.py`: aprovado.
- Robot real, serial, Linux/Xvfb 1600×1000, A/B (Fase 7.12 × Fase 7.13) nas
  mesmas situações, traços idênticos, zero falhas nos quatro casos:
  `PO_TRANSFORMACAO_RELACAO_figurinhas_1283157032` e
  `PO_COMPOSICAO_RELACOES_dinheiro_161113042`. Verifica: clicar na lupa
  revela só aquele painel e grava exatamente um log `LUPA_EIXO_RELACAO`; um
  segundo clique no mesmo ponto não grava outro log nem revela outro painel.
  Evidências: `documentacao/relatorios/evidencias/robot_lupa_eixo_relacao_20260929/`.
  Uma execução exploratória anterior (sem situação fixa, só para validar o
  harness) não integra a evidência.

## Pendente

Repetir o Robot no Windows. Com esta fase termina a sequência de extrações de
`mousePressed` registrada no levantamento; novas extrações exigem nova
análise e autorização.
