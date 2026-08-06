# Confirmação — senha na abertura de `TelaArtefatoExplicativo`

Data: 2026-08-05. Somente leitura — nenhum código foi alterado.

## Contexto

Você esclareceu que a senha pedida na abertura da tela de explicações (`TelaArtefatoExplicativo`) tem o propósito de garantir que o pesquisador esteja presente ao lado do participante durante toda a interação registrada ali — incluindo a parte de autorrelato (`explicacaoElemento`/`dificuldade`), não só a atribuição de invariante. Isso é relevante para a pendência ainda aberta sobre o agente `"P"` em `registrarExplicacaoMatematica` (ver `RELATORIO_ORIGEM_AGENTE_P_2026-08-05.md`), e pediu confirmação do estado atual antes de reescrever a nota de `REFERENCE.md` §4.8.1 de novo.

## 1. A senha já está implementada

`Main.java:2154-2158`, dentro de `abrirArtefatoExplicativo()` — o único método que chama `TelaArtefatoExplicativo.mostrar(...)` (confirmado: essa é a única chamada a esse método em todo o repositório, `Main.java:2189`):

```java
private void abrirArtefatoExplicativo() {
    if (!autenticarPesquisador()) {
        requestFocusInWindow();
        return;
    }
```

Reaproveita o mesmo `autenticarPesquisador()` (`Main.java:1870-1885`) já usado pela Visão Pesquisador (`Main.java:1684`), que compara contra `SENHA_VISAO_PESQUISADOR` (`Main.java:1868`) — mesma senha fixa, mesmo comentário original ("portão leve... decisão do usuário em 2026-07-23").

## 2. Exigida toda vez, sem exceção

`autenticarPesquisador()` é a primeira linha do método, executada antes de qualquer outra checagem (antes de `situacaoProblemaAtual == null`, antes de checar posicionamento no diagrama). Sem cache de autenticação prévia, sem bypass por categoria, sem exceção de "só na primeira vez". Toda vez que o botão "A" (`criarBotaoArtefatoExplicativo`, `Main.java:2141-2152`) é clicado, a senha é pedida de novo. Se falhar ou for cancelada, `return` imediato — `TelaArtefatoExplicativo.mostrar(...)` (linha 2189) nunca é alcançado, a tela nem chega a ser construída.

## 3. Não aplicável

A senha já existe (item 1) — não há pendência de implementação a registrar.

## Confirmação da premissa

A senha protege a abertura da tela inteira, antes de qualquer interação — cobre tanto o autorrelato do participante quanto a atribuição de invariante, porque as duas só ficam acessíveis depois que o pesquisador já autenticou. Consistente com a leitura de que o pesquisador está garantidamente presente durante toda a interação registrada nessa tela, não só na parte de invariante.

Isso é relevante para a pendência do agente `"P"`: se o pesquisador está presente durante a tela inteira (autorrelato + invariante), o argumento a favor de `"P"` cobrir a linha toda — não só a metade de invariante — fica mais forte do que parecia na nota original de achado 3. A decisão entre reverter para `"S"` ou reescrever a nota continua em aberto; não decidi nem apliquei nada aqui.
