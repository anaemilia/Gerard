---
name: gerard-identidade-visual
description: Manutenção da identidade visual do Gérard entre a versão Windows (Java/Swing) e a versão mobile — paleta neutra, tipografia e consistência de estilo entre plataformas. Use ao trabalhar em qualquer tela, componente visual ou decisão de estilo do Gérard em qualquer uma das duas plataformas. NÃO cobre cores de significado de feedback (sucesso/erro) — isso pertence à skill de scaffolding/interação (gerard-scaffolding-interacao).
---

# Identidade visual entre Windows e mobile — Gérard

## Status de verificação (2026-07-20)

Ao auditar plataformas, paleta implementada ou cobertura visual da curadoria,
leia [`references/estado-verificado.md`](references/estado-verificado.md).
Evidência de implementação não redefine os princípios abaixo.

## Escopo desta skill

Cobre apenas a paleta neutra/tokens visuais e a consistência de estilo entre as duas plataformas (Windows/Swing e mobile).

**Fora do escopo, mesmo que envolva cor**: cores de significado usadas em feedback de erro/acerto (azul=sucesso, vermelho=erro). Essas pertencem à skill `gerard-scaffolding-interacao` — esta skill nunca decide o que uma cor significa pedagogicamente, só como as cores neutras se apresentam visualmente.

**Atenção prática, não só conceitual**: essa separação de responsabilidade é de *documentação*, não de *arquivo*. `COR_SUCESSO`, `COR_SUCESSO_FUNDO`, `COR_SUCESSO_TEXTO`, `COR_ERRO`, `COR_ERRO_FUNDO` e `COR_ERRO_TEXTO` estão definidas na **mesma classe** `UITemaGerard.java` (linhas 85-104) junto com toda a paleta neutra (linhas 36-78) — não há separação por arquivo ou pacote entre as duas responsabilidades. Ao editar `UITemaGerard.java` para ajustar a paleta neutra, tome cuidado para não tocar nas constantes de feedback por engano; elas pertencem conceitualmente à outra skill mesmo estando fisicamente no mesmo arquivo.

## Princípios

1. **Tom neutro frio como base em toda a tela — atualizado em 2026-09-30**: até 2026-09-29 a paleta era um neutro **quente** (bege/taupe): `COR_FUNDO = 0xE7E4DC`, `COR_SUPERFICIE = 0xFCFBF8`, `COR_PRIMARIA = 0x332E28`. Em 2026-09-30, para alinhar com o Design System do site institucional (`prompt-claude-code-cores.md`), a pesquisadora decidiu migrar para um neutro **frio** (cinza-azulado), atualizando `UITemaGerard.java` no lugar (sem criar classes novas) para não deixar dois sistemas de cor em paralelo: `COR_FUNDO = 0xF7F9FC`, `COR_SUPERFICIE = 0xFFFFFF`, `COR_PRIMARIA = 0x0E1726` (quase preto azulado, não mais marrom). Ao descrever ou replicar essa paleta agora, use "neutro frio"/"cinza-azulado", não mais "bege"/"neutro quente".

2. **Reservar cores apenas para feedbacks, sinais e informações ao usuário. O que não carrega informação permanece no tom neutro.** Até 2026-09-29 isso incluía uma regra adicional, hoje revogada: "o azul fica reservado exclusivamente ao feedback de sucesso — não deve ser aplicado a elementos decorativos ou de marca". Em 2026-09-30 essa exclusividade foi deliberadamente ampliada (decisão da pesquisadora, "Opção A" de `prompt-claude-code-cores.md`): o mesmo azul institucional (`COR_ACAO` em `UITemaGerard.java`) agora serve tanto para ações/foco/seleção/botões primários quanto para `COR_SUCESSO` — os dois papéis foram intencionalmente unificados, não são mais dois usos em conflito. O que continua valendo: essa cor nunca é usada decorativamente na área da tarefa/diagrama (que permanece neutra), e o sucesso nunca depende só da cor — sempre acompanhado de texto/ícone.

3. Usar cores com significado cultural já reconhecido (para o público brasileiro/ocidental) em vez de cores arbitrárias — mas a definição desse significado pedagógico é responsabilidade da skill `gerard-scaffolding-interacao`, não desta.

## Observação sobre a "suavidade" percebida (não verificável em código)

Parte da suavidade percebida no protótipo mobile pode estar relacionada ao próprio uso do dedo/touch em vez do mouse com clique (Windows), não só à tecnologia de renderização (Swing vs. tecnologia mobile). Ao replicar uma sensação visual entre plataformas, considerar essa diferença de input, não só cor/estilo. **Esta observação vem do relato do usuário sobre o protótipo mobile — não há código mobile neste repositório para conferir.**

## Acessibilidade de controles visuais

Quando um ícone substitui um rótulo textual, preserve o nome localizado como
`tooltip` e `accessibleName`; use `accessibleDescription` quando o nome não
explicar a ação. Controles interativos devem continuar alcançáveis pelo
teclado e não podem depender somente da cor para comunicar estado ou função.

Esse padrão já aparece parcialmente em `TelaMontagemSituacao`,
`PainelValorComSinalCuradoria` e `SeloConclusaoModelagem`. A presença nesses
componentes não prova cobertura global: ao alterar uma tela, verifique
explicitamente foco, teclado, tooltip, nome e descrição acessíveis.

## Regra de segurança

Antes de alterar a paleta neutra em uma plataforma, verificar se a mudança precisa ser espelhada na outra para manter consistência — mas sem tocar em nenhuma cor de significado de feedback (isso está fora do escopo desta skill). Lembre-se de que, no lado Windows, paleta neutra e cores de feedback vivem no mesmo arquivo (`UITemaGerard.java`) — confira linha por linha quais constantes está tocando antes de editar.
