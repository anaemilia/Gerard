---
name: gerard-paridade-web-desktop
description: Verificação constante de paridade entre o Gérard desktop (Swing) e a web (API + React). Use SEMPRE que resolver, corrigir ou implementar qualquer coisa de modelagem, interação, log, ajuda ou conclusão em um dos dois lados — Main.java, handlers, src/gerard/ui, ServicoSorteioAtividadeWeb, ServidorPrototipoWeb, web-poc — porque o mesmo comportamento tem que ser verificado no outro cliente antes de declarar a questão resolvida. A web precisa ter toda a parte de modelagem que existe no desktop.
---

# Paridade web ↔ desktop

## A regra (pedido da usuária, 2026-10-10)

Desktop e web são dois clientes do **mesmo domínio**. Toda questão resolvida em um
tem que ser **verificada no outro** antes de dizer "resolvido". A web deve ter toda
a parte de modelagem que existe no desktop.

Resolver só de um lado e seguir adiante é falha de processo, mesmo que o teste daquele
lado passe.

## Procedimento (sem pular etapa)

1. **Classifique onde o comportamento vive**, em uma destas camadas:
   - *Domínio* (objeto rico: `TentativaModelagemAditiva`, `PapelQuantitativo`,
     `EscolhaConclusaoModelagem`…): corrige-se **uma vez**; os dois clientes herdam.
   - *Projeção/API* (`ServicoSorteioAtividadeWeb`, `Projetor*Web`, gerador de cena): o
     servidor decide o que aparece e publica; vale para a web e, pela cena, para o desktop.
   - *Realização* (Swing em `Main`/`ui`, React em `web-poc/src`): só materializa o que o
     domínio e a API mandaram. Decisão na realização é defeito (ver
     `gerard-tela-via-gerador-de-cena` e `gerard-domain-model-first`).
2. **Se está no domínio:** localize os consumidores nos dois lados e confira que **ambos**
   passam pelo ponto corrigido (desktop: `Main`, handlers; web: `EscopoTentativaWeb.persistir`,
   `ServicoAtividadeWeb*`). Cuidado com **várias portas para a mesma ação** (persistir,
   encaminhar, logger): um fechamento só em uma delas perde a ação do limite — já aconteceu
   (2026-10-10). Verifique o resultado final no **log real**, não só no `constituir`.
3. **Se está na realização:** olhe se o outro cliente tem o equivalente. Se não tem, **não
   invente**: registre a lacuna na matriz (`references/matriz-paridade.md`), diga qual
   objeto rico/rota/estado falta e proponha o corte. O texto de qualquer tela vem de
   `mensagens_pt.properties` ou do código existente (Regra 1 do `CLAUDE.md`).
4. **Evidência por ação real (Regra 2), nos dois lados:**
   - Desktop: mouse real (`java.awt.Robot`, `tests/graphical`) com captura/log.
   - Web: mouse real no navegador (Playwright em `tests/web/*.mjs` ou as ferramentas de
     navegador) **e** a sequência de chamadas HTTP que a interação disparou. Teste de
     serviço Java (`tests/java`) prova a regra, **não** prova a interação: diga isso.
   - Se um lado não puder ser exercitado no ambiente, declare e não infira sucesso.
5. **Atualize a matriz** e termine a resposta com o bloco abaixo.

## Bloco obrigatório ao concluir qualquer questão

```
Paridade:
- Desktop: <resolvido/verificado/lacuna> — <evidência: teste, captura, log>
- Web:     <resolvido/verificado/lacuna> — <evidência: teste, HTTP, captura>
- Camada comum (domínio/API): <onde a decisão vive>
- Lacuna aberta: <nenhuma | o que falta e por quê>
```

## Armadilhas já encontradas

- Venn e historinhas são **mutuamente excludentes** por categoria; só composição de medidas
  mostra o Venn com o acervo atual. Comparação não existe sem número relativo, e as barras da
  comparação ficam para uso futuro (não é código morto).
- A escolha Sim/Não/Próxima tarefa depois do azul é decisão de interface e **não** gera log.
- O registro factual para no limite de 3 rejeições (incógnita **ou** escolha de operação) e
  descarta o que vem **depois** da ação que atinge o limite; restaurar/sortear reabre.
- A web recebe o estado já decidido; React não calcula conteúdo, presença nem estado.
- Teste que olha só a camada de serviço não vê a diferença entre portas; confirme no log.

## Relacionadas

`gerard-tela-via-gerador-de-cena`, `gerard-api-semantica`, `gerard-domain-model-first`,
`gerard-handlers-de-interacao`, `gerard-log-acao-instrumental`.
