# Investigação — origem do agente "P" em `registrarExplicacaoMatematica`

Data: 2026-08-05. Somente leitura — nenhum código foi alterado. Pendência ainda aberta: reverter `"P"`→`"S"`, ou ajustar a nota de `REFERENCE.md` §4.8.1, decisão não tomada.

## 1. Quando, e existe documentação

A mudança foi feita **nesta mesma sessão**, em `2026-08-05T00:30:12Z`, por um `Edit` que troca `registrar("S", ...)` por `registrar("P", ...)` em `LoggerInteracaoGerard.java:761` — confirmado no histórico bruto da conversa (timestamp do `toolUseResult` do Edit). **Não existe commit**: o arquivo só tem o commit inicial (`05556ee`); a mudança está só no working tree, não commitada. **Não existe relatório em arquivo** documentando a decisão na hora em que foi tomada — busquei em toda a raiz do repositório e no scratchpad da sessão e não encontrei nenhum `.md` prévio com esse raciocínio; ele só existia na conversa, agora recuperado do transcript bruto (`.jsonl`) e registrado aqui pela primeira vez.

## 2. O que "P" significa e por que foi decidido

"P" = Pesquisador, decisão deliberada. Raciocínio original (recuperado do transcript): a linha gravada por `registrarExplicacaoMatematica` carrega duas autorias sob um único campo de agente — o autorrelato do participante (`explicacaoElemento`/`dificuldade`) e a atribuição de invariante, que é **sempre** uma decisão do pesquisador, nunca do participante (confirmado por você antes desta mudança: "SÓ QUEM ATRIBUI INVARIANTE É O PESQUISADOR."). Como o agente da linha inteira estava fixo em `"S"`, isso era lido por `TelaVisaoPesquisador` como "isto foi o participante" — concluído como **factualmente errado** para a porção de invariante, não apenas ambíguo. Proposta apresentada e aprovada com "sim"; aplicada em seguida.

**Tensão não resolvida na época**: a decisão trata a linha inteira como pertencente ao pesquisador — inclusive a parte que é autorrelato do participante (`explicacaoElemento`/`dificuldade`). Isso já tinha sido identificado na própria proposta original ("uma única linha de log carrega as duas autorias sob um único campo de agente — não dá pra marcar as duas coisas corretamente ao mesmo tempo com o esquema atual"), mas não resolvido — a linha toda foi marcada `"P"`, não só a metade do invariante.

## 3. O que `normalizarAgente` faz com "P"

Trata como **terceiro valor distinto**, não normaliza de volta para "S" nem "C". `EventoLogGerard.java:242-244`:
```java
if ("P".equals(upper) || upper.indexOf("PESQUISADOR") >= 0) {
    return "P";
}
```
Esse bloco fica entre o de `"C"` (linhas 239-241) e o de `"S"` (linha 245 em diante) — três ramos irmãos, mesma estrutura, retorno próprio.

## 4. O que acontece com as detecções causais em `TelaVisaoPesquisador` para uma linha "P"

**Ficam fora — silenciosamente, sem tratamento explícito para "P".** Busca em `TelaVisaoPesquisador.java`: 20 ocorrências de `"S".equals(...)`/`"C".equals(...)`, zero de `"P".equals(...)`. As cinco detecções causais C→S — linhas **1660, 1704, 1734, 1745, 1755** — todas usam igualdade estrita (ex. linha 1660: `"C".equals(ant.getAgenteDaAcao()) && "S".equals(atual.getAgenteDaAcao())`), nunca uma checagem negativa (`!"C".equals(...)`). Uma linha com `agenteDaAcao="P"` não bate em nenhuma dessas comparações — não é contada como `"S"` nem como `"C"` em nenhum dos contadores (linhas 356-358, 392-393, 1262-1266, 1682-1686) nem entra em nenhuma sequência causal C→S. Efeito confirmado exatamente como a proposta original previu: a linha de atribuição de invariante some das contagens/detecções de ação do participante, sem que nenhum dos 20 pontos de consumo precisasse ser editado.

## Pendência

Duas saídas possíveis, já registradas em `RELATORIO_VERIFICACAO_SUGESTAOADOTADA_2026-08-05.md`, seção 5:
1. Reverter `"P"`→`"S"` em `LoggerInteracaoGerard.registrarExplicacaoMatematica` e remover o branch `"P"` de `normalizarAgente`, alinhando o código à nota de `REFERENCE.md` §4.8.1 já aplicada.
2. Ajustar a nota para descrever `"P"` como está hoje — mas isso contraria sua instrução explícita de não alterar `registrarExplicacaoMatematica` nem o código de agente `"S"`.

Nenhuma das duas foi aplicada. Decisão seguindo pendente.
