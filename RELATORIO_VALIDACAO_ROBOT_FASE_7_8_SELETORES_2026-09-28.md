# Validação Robot dirigida — Fase 7.8 (seletores de operação das Relações)

Data: 2026-09-28 (execução registrada em 2026-09-29 00:28 UTC)
Escopo: validar, pelo protocolo real **apontar-e-clicar** (`java.awt.Robot` →
AWT → `Main.TelaGerard.mousePressed` → `CasoDeUsoSelecaoOperacoesRelacoes`), o
comportamento que o relatório da Fase 7.8 declarou preservado mas que tinha
sido verificado só isoladamente.

## Ambiente

- VM Linux local (Cowork), OpenJDK 11.0.32 (javac via módulo `jdk.compiler`),
  Xvfb 1600×1000×24. Build feito fora da pasta do repositório
  (`$HOME/gb/classes`), sem tocar em `build/` nem `dist/`.
- 601 fontes de produção compiladas sem erro (`-source 8 -target 8`).
- Logs do app em `~/Gerard/logs` **da VM** — nenhum dado real da usuária
  (Windows) foi lido ou alterado.
- Curadoria: a embarcada em `src/gerard/campoaditivo/dados/situacoes_vergnaud.tsv`.

## Harness

`tests/graphical/TesteRobotSeletoresOperacaoRelacoes.java` (novo, pacote
padrão, fora do JAR). Resposta esperada obtida do mesmo oráculo de domínio da
tela (`AvaliacaoEscolhaOperacaoRelacao.determinarOperacaoCorreta`); áreas dos
botões lidas por reflexão só no teste — nenhum getter novo em produção.
Contagem de registros feita no TSV real da sessão
(`gerard_interacao_*.tsv`, linhas com `relacao.operacao.`).

Comando:

```text
xvfb-run -a -s "-screen 0 1600x1000x24" java -cp "<tclasses>:<classes>:lib/*" \
  TesteRobotSeletoresOperacaoRelacoes <dir_evidencias>
```

## Resultado

**36 verificações, 0 falhas, código de saída 0.**

| Cenário | Situação curada | Verificado |
|---|---|---|
| TR — Transformação de relação | `PO_TRANSFORMACAO_RELACAO_bonecas_620955739` (subtração) | 1º seletor ativo, 2º inativo; erro → escolha registrada, não correta, 1 registro E; acerto → 1 registro C |
| CR — Composição de relações | `PO_COMPOSICAO_RELACOES_idades_642701604` (soma) | idem |
| CT — Composição de transformações | `PO_COMPOSICAO_TRANSFORMACOES_bolas_509261012` (soma/soma) | 2º seletor **não** recebe clique antes do 1º correto (nem após 1º errado), sem registro; após acerto do 1º, 2º recebe erro e acerto, 1 registro cada, proprietário `relacao.operacao.estadoTransformacao`; 1º permanece correto |
| Global | — | nenhuma exceção não tratada; `MouseListener` único antes e depois |

Evidências (capturas de tela por passo, `harness.log`, `stdout.txt`,
`SHA256SUMS.txt`): `documentacao/relatorios/evidencias/robot_fase_7_8_20260929_002834/`.
As capturas mostram o balão de explicação só após o erro, o radiobutton
azul após o acerto e o segundo seletor visível só depois do primeiro correto.

## Achados colaterais (não corrigidos — fora do escopo da fase)

1. **"Nova situação-problema" sorteia entre todas as categorias** (fluxo de
   adivinhação da categoria, `sortearDentroDoGrupo(Grupo.TODAS)`). Durante o
   sorteio, situações de Relações com operação curada (ex.: TR bonecas)
   apareceram **sem** seletor ativo, porque a tela ainda está na categoria
   anterior até a adivinhação ser confirmada. O harness passou a reescolher a
   categoria pelo menu. Vale confirmar se o seletor deve aparecer após a
   confirmação da categoria adivinhada (não exercitado aqui).
2. **Cobertura curada baixa**: entre as situações validadas, só 1 de 4 (TR),
   1 de 2 (CR) e 1 de 5 (CT, com as duas operações) têm `operacao_relacao`
   / `operacao_estado_transformacao` preenchidos; nas demais o seletor
   corretamente não aparece.
3. **Layout em CT (1600×1000)**: o primeiro seletor é desenhado acima dos
   círculos superiores e invade a caixa do enunciado; o estado inicial fica
   cortado à esquerda. Geometria anterior à Fase 7.8 (a fase não alterou
   `ativar`); registrar para revisão visual em Windows.

## Conclusão

A extração da Fase 7.8 está agora validada também por interação Robot real
nas três categorias de Relações: prioridade do primeiro seletor, liberação
ordenada do segundo, um único registro C/E por escolha consumida, feedback de
erro local e ausência de exceções. A validação foi feita em Linux/Xvfb; a
execução do mesmo harness no Windows da usuária continua recomendada para
confirmar a parte visual.
