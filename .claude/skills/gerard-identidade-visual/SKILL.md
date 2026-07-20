---
name: gerard-identidade-visual
description: Manutenção da identidade visual do Gérard entre a versão Windows (Java/Swing) e a versão mobile — paleta neutra, tipografia e consistência de estilo entre plataformas. Use ao trabalhar em qualquer tela, componente visual ou decisão de estilo do Gérard em qualquer uma das duas plataformas. NÃO cobre cores de significado de feedback (sucesso/erro) — isso pertence à skill de scaffolding/interação (gerard-scaffolding-interacao).
---

# Identidade visual entre Windows e mobile — Gérard

## Status de verificação (2026-07-20)

O lado Windows/Swing foi conferido contra `gerard/ui/UITemaGerard.java`. O lado mobile **não pôde ser verificado** — este repositório não contém nenhum código mobile; trate as afirmações sobre o protótipo mobile como relato do usuário, não como algo conferido em código. Duas correções em relação ao rascunho original: a paleta não é "cinza", é neutro quente; e as cores de feedback vivem no mesmo arquivo da paleta neutra, não em módulos separados.

## Escopo desta skill

Cobre apenas a paleta neutra/tokens visuais e a consistência de estilo entre as duas plataformas (Windows/Swing e mobile).

**Fora do escopo, mesmo que envolva cor**: cores de significado usadas em feedback de erro/acerto (azul=sucesso, vermelho=erro). Essas pertencem à skill `gerard-scaffolding-interacao` — esta skill nunca decide o que uma cor significa pedagogicamente, só como as cores neutras se apresentam visualmente.

**Atenção prática, não só conceitual**: essa separação de responsabilidade é de *documentação*, não de *arquivo*. `COR_SUCESSO`, `COR_SUCESSO_FUNDO`, `COR_SUCESSO_TEXTO`, `COR_ERRO`, `COR_ERRO_FUNDO` e `COR_ERRO_TEXTO` estão definidas na **mesma classe** `UITemaGerard.java` (linhas 85-104) junto com toda a paleta neutra (linhas 36-78) — não há separação por arquivo ou pacote entre as duas responsabilidades. Ao editar `UITemaGerard.java` para ajustar a paleta neutra, tome cuidado para não tocar nas constantes de feedback por engano; elas pertencem conceitualmente à outra skill mesmo estando fisicamente no mesmo arquivo.

## Princípios

1. **Tom neutro suave como base em toda a tela — corrigido**: não é literalmente "cinza". A paleta confirmada em `UITemaGerard.java:36-78` é um neutro **quente** (bege/taupe), não cinza puro: `COR_FUNDO = new Color(0xE7,0xE4,0xDC)`, `COR_SUPERFICIE = new Color(0xFC,0xFB,0xF8)`, `COR_PRIMARIA = new Color(0x33,0x2E,0x28)` (um marrom escuro, não um cinza). O espírito "discreto/neutro" da regra está correto; o nome "cinza" não é preciso — ao descrever ou replicar essa paleta, use "neutro quente"/"bege-acinzentado", não "cinza".

2. **Reservar cores apenas para feedbacks, sinais e informações ao usuário. O que não carrega informação permanece no tom neutro.** Confirmado de forma direta: o próprio código documenta essa decisão de design. `UITemaGerard.java:44-47` tem o comentário "Cor 'primária' neutra (antes azul). O azul fica reservado exclusivamente ao feedback de sucesso — ver COR_SUCESSO" — ou seja, essa não é só uma convenção implícita, é uma migração de design já registrada no código (a cor "primária" do app já foi azul e virou neutra de propósito). `COR_SUCESSO` também tem o comentário explícito "Uso restrito a sinais ao usuário — não deve ser aplicada a elementos decorativos ou de marca" (`UITemaGerard.java:80-85`). Confirmei o uso de `COR_SUCESSO`/`COR_ERRO` em 11 arquivos do projeto — todos em contextos de feedback (conclusão de modelagem, diagrama, item arrastável), nenhum uso decorativo encontrado.

3. Usar cores com significado cultural já reconhecido (para o público brasileiro/ocidental) em vez de cores arbitrárias — mas a definição desse significado pedagógico é responsabilidade da skill `gerard-scaffolding-interacao`, não desta.

## Observação sobre a "suavidade" percebida (não verificável em código)

Parte da suavidade percebida no protótipo mobile pode estar relacionada ao próprio uso do dedo/touch em vez do mouse com clique (Windows), não só à tecnologia de renderização (Swing vs. tecnologia mobile). Ao replicar uma sensação visual entre plataformas, considerar essa diferença de input, não só cor/estilo. **Esta observação vem do relato do usuário sobre o protótipo mobile — não há código mobile neste repositório para conferir.**

## Regra de segurança

Antes de alterar a paleta neutra em uma plataforma, verificar se a mudança precisa ser espelhada na outra para manter consistência — mas sem tocar em nenhuma cor de significado de feedback (isso está fora do escopo desta skill). Lembre-se de que, no lado Windows, paleta neutra e cores de feedback vivem no mesmo arquivo (`UITemaGerard.java`) — confira linha por linha quais constantes está tocando antes de editar.
