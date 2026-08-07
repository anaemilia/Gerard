# Relatório: limite de 2 dígitos para valores curados (curadoria)

Data: 2026-08-06

## Contexto

Passo 2, separado da guarda de estouro de int (passos anteriores:
`RELATORIO_GUARDA_ESTOURO_INTEIRO_2026-08-06.md` no piloto,
`RELATORIO_GUARDA_ESTOURO_PRODUCAO_2026-08-06.md` em
`EstadoSemanticoCompartilhado`). A usuária pediu, especificamente:
"Só aceitar situações curadas com até dois dígitos. Durante a
curadoria, não deixar o pesquisador aceitar mais de dois dígitos."

Ou seja: não um teto embutido em `DominioNumerico` (que afetaria também
valores calculados/digitados durante a atividade do estudante), mas um
**gate na curadoria** — o ponto de entrada dos dados no sistema. Mais
barato recusar aqui do que descobrir o problema depois.

## O que mudou

### `ValidadorTraducaoCurada.java` (função pura, testável sem Swing)

Novo `LIMITE_MAGNITUDE_VALOR_CURADO = 99` e
`localizarValoresAcimaDoLimiteDeDigitos(LinhaSituacao linha)`, que
verifica os mesmos 9 campos numéricos curados já usados por
`localizarValoresNumericosAusentes` (estado inicial, transformação,
estado final, quantidade 1, quantidade 2, resultado, referido,
referendo, valor relativo) e devolve os rótulos dos que excedem 99 em
magnitude. Campos vazios ou com "?" (a marcação do termo desconhecido)
são ignorados, mesmo padrão de `adicionarSeAusente`.

### `TelaCuradoriaSituacoes.java` — dois pontos de entrada, um gate

Investigação encontrou 3 formas de marcar uma situação como "validada"
no código: (1) o diálogo de edição detalhada (checkbox "Validada" +
botão "Salvar e fechar"), (2) o clique direto na coluna "validada" da
tabela, (3) o botão "Validar selecionadas" (ação em lote). Um quarto
método, `confirmarValidacaoAntesDeFechar` (diálogo "Validar esta versão
e fechar"), existe no código mas **não é chamado em nenhum lugar** —
código morto, não precisou de gate.

- **Diálogo de edição** (`validarAntesDeSalvarCuradoriaDetalhada`):
  quando `linha.validada` é `true`, verifica os campos excedentes e, se
  houver, bloqueia o salvamento com uma mensagem de aviso e um registro
  em `RegistroErrosCuradoria` — mesmo padrão já usado pelo gate de "?"
  em vez de número (`papeisComInterrogacao`) logo acima no mesmo
  método.
- **Tabela** (clique direto + lote): os dois passam pelo mesmo
  `ModeloTabelaSituacoes.setValueAt`, então um único
  `TableModelListener` (instalado no construtor,
  `instalarGuardaDeLimiteDeDigitosNaColunaValidada`) intercepta
  qualquer mudança para `validada=true` na coluna 2; se os campos
  excederem o limite, reverte para `false`, atualiza a célula e mostra
  o mesmo aviso. A checagem em si evita reentrância infinita: reverter
  para `false` dispara outro evento, mas nessa segunda passada a
  condição "validada == true" não vale mais.

## Efeito sobre dados já existentes

Não retroativo por design — o gate só intercepta ações de validação
feitas pela interface, não reescreve `situacoes_vergnaud.tsv` sozinho.
A única situação curada com mais de 2 dígitos hoje é `SP_0008` ("Pedro
coleciona selos... ficando com 417 selos"), nas 3 versões linguísticas
(PT/EN/FR) — todas já estão com `validada=false` no catálogo atual, então
o novo gate simplesmente impede que passem a `true` até serem revisadas
(reduzir o valor, ou remover o teto se a pesquisadora decidir que este
caso deve ser exceção — decisão dela, não tomada aqui).

## Verificação

- Projeto completo compilado (435 arquivos): **0 erros**.
- `localizarValoresAcimaDoLimiteDeDigitos` verificada com uma checagem
  dirigida (fora do harness permanente, já que não existe suíte de
  curadoria neste repositório): 99 e -99 não excedem (limite inclusivo),
  "?" e vazio são ignorados, 0 não excede, 417 e 100 excedem —
  resultado exatamente como esperado.
- **Não verificado**: comportamento da interface Swing em si (diálogo
  de aviso, reversão do checkbox na tabela) — não há harness de GUI
  neste ambiente, mesmo padrão de risco documentado nos relatórios
  anteriores desta sessão sobre mudanças de UI.

## Escopo

Só `gerard.campoaditivo.curadoria` (`ValidadorTraducaoCurada.java`,
`TelaCuradoriaSituacoes.java`) — ferramenta de curadoria usada pela
pesquisadora, não o caminho de produção do estudante. Nenhuma mudança em
`Main.java`, `DominioNumerico` ou `EstadoSemanticoCompartilhado`.
