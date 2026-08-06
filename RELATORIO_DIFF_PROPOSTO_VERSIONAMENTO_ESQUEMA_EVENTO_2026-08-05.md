# Diff proposto — item 7 da Revisão 5 (Seção 26, Seção 31.7): formato de versionamento de esquema de evento

Data: 2026-08-05. **Proposto, nenhum aplicado ainda** — aguardando aprovação.

## Contexto da decisão

Formato de versionamento de esquema de evento. Documentação apenas — nenhum código Java muda.

Decisão: versionamento embutido no campo "tipo" do envelope (sufixo ".vN"), seguindo a convenção recomendada pela própria especificação CloudEvents (já referenciada na decisão do item 6, arquitetura envelope + payload) — "type-based versioning", em vez de um campo de versão separado. Mantém consistência com a fonte já adotada, sem introduzir uma convenção nova.

## Diff — `REFERENCE.md` §4.8

Inserção após a linha 287 ("...implementar esta reestruturação."), antes de "### 4.8.1":

```diff
 Esta é uma decisão de arquitetura-alvo, não uma implementação —
 `EventoPapelQuantitativo` continua como está até uma decisão explícita de
 implementar esta reestruturação.
 
+Versionamento de esquema: versão embutida no campo "tipo" do envelope
+(sufixo `.vN`, ex.: `papel_quantitativo.posicionado.v1`), seguindo a
+convenção "type-based versioning" recomendada pela especificação
+CloudEvents já referenciada na decisão de arquitetura de evento --
+consumidores podem rotear versões diferentes do mesmo tipo de evento para
+tratamentos diferentes. Não há campo de versão separado.
+
+Esta é uma decisão de formato-alvo, não uma implementação --
+`EventoPapelQuantitativo` continua como está até uma decisão explícita de
+implementar a reestruturação envelope + payload (decisão anterior).
+
 ### 4.8.1 Mobilização do invariante operatório: sugestão do sistema e atribuição do pesquisador
```

## Status

Não aplicado. Depois de aplicado: não é necessário recompilar nem rerodar harness — é só `REFERENCE.md`, nenhum código muda. Nenhum commit, nenhum push sem pedido explícito.
