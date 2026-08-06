# Confirmação — item 2 da fila de decisões da Revisão 5 (Seção 31.2)

Data: 2026-08-05. Somente confirmação — nenhuma mudança de código ou comportamento, nenhuma recompilação, nenhum harness rodado.

## Pergunta

Item 2 da Revisão 5 (Seção 31.2): classificação de `SugestorInvarianteOperatorio.sugerirCodigo` como processo `SISTEMA` ou `INFERENCIA_COMPUTACIONAL`. Verificar se já foi resolvido pela correção normativa 2.2/2.3 (`REFERENCE.md` §4.8.1), aplicada antes da própria Revisão 5 ter rodado.

## 1. Estado atual de `REFERENCE.md` §4.8.1

`.claude/skills/gerard-semantic-model/REFERENCE.md:224-232`:

```
- **Sugestão do sistema**: `SugestorInvarianteOperatorio.sugerirCodigo` propõe
  um código candidato para apoiar o pesquisador. É auxiliar e não-vinculante:
  enquanto não for adotada, essa sugestão não registra origem no evento
  correspondente — o campo `origem` fica vazio.
- **Atribuição do pesquisador**: na visão do pesquisador, um combobox permite
  selecionar um invariante operatório já existente ou criar um novo, e
  relacioná-lo à ação. Esse é o evento que registra que um invariante foi
  mobilizado; sua origem é sempre `PESQUISADOR`, adotando ou não uma sugestão
  do sistema.
```

Confirma exatamente o que a correção 2.2/2.3 estabeleceu: `sugerirCodigo` é auxiliar/não-vinculante, sem origem própria até ser adotada; o evento de mobilização do invariante é sempre `PESQUISADOR`.

## 2. Confirmação lógica — resolvido por ausência de objeto

`SugestorInvarianteOperatorio.java` (`src/gerard/agente/modelador/SugestorInvarianteOperatorio.java:1,29`) está no pacote de produção `gerard.agente.modelador` — não em `gerard.dominio.campoaditivo` (piloto) — e não tem nenhuma referência a `OrigemAcao` (nem import, nem uso). `sugerirCodigo` não só não recebe classificação `SISTEMA`/`INFERENCIA_COMPUTACIONAL` na prática hoje — está num pacote que sequer tem acesso a esse enum. A pergunta "qual classificação?" não se aplica: não há campo `OrigemAcao` nesse mecanismo para classificar.

**Item 2 da Seção 31 da Revisão 5: resolvido por ausência de objeto**, antes mesmo da própria Revisão 5 rodar — pela correção normativa 2.2/2.3 já registrada em `REFERENCE.md` §4.8.1.

## 3. Relatório da Revisão 5 no repositório

**Não existe.** Busca em todo o repo root por qualquer `.md` com "revisao"/"revisão" no nome — só `RELATORIO_AUDITORIA_ARQUITETURAL_REVISAO4_2026-08-04.md` (Revisão 4) foi encontrado. Nenhum arquivo de Revisão 5 foi salvo no repositório; nenhum novo arquivo foi criado, conforme instruído.
