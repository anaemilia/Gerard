# Relatório: teste do macaco (TesteMonkeySemiGuiado) executado de verdade

Data: 2026-08-07

## Contexto

Pedido direto da usuária: "vc consegue propor e realizar aquele teste do
macaco?", depois de eu ter avaliado que a robustez ganha nesta sessão
(Fase B2, N=3 tentativas, reinclusão de Relações) era só de lógica de
domínio — nunca tinha sido verificada interativamente, via GUI real. Em
seguida, "habilite as relações. O teste tem que ser completo" — pedido
explícito para o teste também exercitar o grupo Relações, não só Medidas.

## Como rodei

Sem display real neste ambiente, mas o sandbox tem Xvfb pré-instalado.
Rodei a aplicação real (`Main`) via `TesteMonkeySemiGuiado`, sob
`Xvfb :99`, com `java.awt.Robot` — mesmos `MouseListener`/`KeyListener`/
`ActionListener` que um clique humano acionaria. Nenhuma mudança em
`Main.java` para isso: só chamadas necessárias (instanciar `Main`, como o
próprio harness já fazia antes desta sessão) e o teste em si, que é
temporário/de desenvolvimento (não entra no instalador).

## Mudança feita: `TesteMonkeySemiGuiado.java`

Único arquivo tocado. Antes, a seleção de categoria (inicial e — nova
nesta sessão — no meio do teste) só considerava o grupo "Medidas" (índice
0 de `menuCategoria`). Estendido para `todasOpcoesDeCategoriaHabilitadas`:
percorre TODOS os subgrupos (Medidas, Transformações, Relações) e coleta
os itens habilitados — os "Em construção" ficam de fora naturalmente, por
já estarem desabilitados. Adicionado `talvezTrocarCategoria` (8% de chance
por iteração) para trocar de categoria no meio do teste, não só uma vez no
início — sem isso, uma rodada cuja seleção inicial caísse em Medidas nunca
chegava a exercitar Relações.

## Achado ao rodar a primeira vez: dado de curadoria ausente no sandbox

Primeira rodada (seed=42, 75s): 66 iterações, **0 erros**, mas nenhuma
única interação real de diagrama (nenhum arraste, nenhuma digitação de
incógnita) — só cliques repetidos em "Nova situação-problema". Investigado
via screenshot + leitura de `RepositorioSituacoesAditivas`: toda situação
sorteada caía em "Sorteie novamente... — nenhuma situação-problema
curada". Causa: `situacoes_vergnaud.tsv` (o arquivo rastreado no
repositório, usado como fallback de classpath) tem **as 210 linhas com
`validada=false`** — provavelmente um snapshot bruto, não o dado real que
a usuária curou na sua máquina ao longo do tempo. O carregamento prioriza
`$HOME/Gerard/curadoria/situacoes_vergnaud_curadas.tsv`, que não existe
neste sandbox (só existe na máquina real da usuária).

Corrigido **só para viabilizar o teste, só neste sandbox**: copiei
`distribuicao_windows/app/Gerard_seed/curadoria/situacoes_vergnaud_curadas.tsv`
(o arquivo-semente que o próprio instalador já usa para isso) para
`$HOME/Gerard/curadoria/` do sandbox. Não é arquivo inventado por mim, não
toca o repositório nem a máquina real da usuária — só imita o que o
instalador já faz num primeiro uso. Esse arquivo tem 33 linhas
`validada=true`, todas em Medidas (nenhuma em Relações — ver limitação
abaixo).

## Segunda rodada (seed=7, 90s) — resultado final

**82 iterações, 0 erros capturados, nenhuma "EXCECAO NAO TRATADA".**

- Categoria trocou várias vezes durante o teste, incluindo para os 3
  tipos de Relações (`TR - Transformação de uma relação`,
  `CT - Composição de transformações`, e `CR - Composição de relações`
  apareceu no log de interação como `categoriaSorteada`) — confirmando
  que a reinclusão no sorteio (commit `573ed32`) está ativa e não quebra
  nada ao ser sorteada.
- Quando caiu em `TM - Transformação de medidas` (que tem dado validado),
  o teste arrastou valores reais para o diagrama (`arrastar valor=32
  papel=papel.estadoInicial`, `arrastar valor=22
  papel=papel.transformacao`) — confirma que a Fase B2 (Main chamando o
  piloto) funciona numa interação real de arraste, não só nos harnesses
  isolados.
- Log de atividade dos 3 agentes (Monitor/ZDP/Modelador) registrou 14
  eventos reais (`CORRETO`/`ERRADO`/`QUESTIONAMENTO_LEVE`/
  `AJUDA_ESPECIFICA`/`PARCIAL`) — evidência de que a integração
  ObjetoTelaGerard ↔ agentes de Ajuda Adaptativa também segue viva.
- Screenshots tirados durante a rodada confirmam visualmente os 6 ícones
  (Medidas + Relações) desenhados corretamente, incluindo a seta de
  Comparação de Medidas já corrigida nesta sessão.

## Limitação que ficou clara, não resolvida aqui

Nenhuma fonte de dado rastreada neste repositório (nem o TSV do
classpath, nem o seed do instalador) tem **situação validada em
Relações**. Ou seja: mesmo com os ícones habilitados e a lógica de
domínio pronta (Fase B1/B2), sortear ou selecionar uma categoria de
Relações sempre cai em "sem situação curada" *neste sandbox* — porque
falta dado, não porque falta código. Isso não é um bug desta sessão nem
foi causado por nenhuma mudança feita aqui. Não inventei dado de
curadoria para contornar isso (fugiria do escopo pedido e arriscaria
fabricar conteúdo pedagógico incorreto sem revisão). Na máquina real da
usuária, se o arquivo `curadoria/situacoes_vergnaud_curadas.tsv` dela já
tiver situações de Relações validadas (via aba Validar), o caminho feliz
completo (ícone certo confirma a categoria sorteada) deve funcionar —
mas isso só ela pode confirmar rodando lá, não é verificável aqui.

## Verificação

- Compilação completa (436 arquivos): 0 erros.
- Teste do macaco, 2 rodadas (75s + 90s): 0 erros capturados, 0 exceções
  não tratadas, interações reais confirmadas em Medidas (arraste) e
  navegação real confirmada em todas as categorias incluindo Relações.
- Nenhuma mudança em `Main.java` nem em nenhum outro arquivo de produção
  — só `TesteMonkeySemiGuiado.java` (ferramenta de teste, fora do
  instalador).

## Escopo

Só `src/TesteMonkeySemiGuiado.java`. O ajuste de dado de curadoria foi
só no sandbox de teste (`$HOME/Gerard/curadoria`), não é parte do
repositório e não persiste em lugar nenhum que afete a usuária.
