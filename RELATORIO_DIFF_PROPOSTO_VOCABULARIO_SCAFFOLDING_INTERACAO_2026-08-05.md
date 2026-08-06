# Diff proposto — item 5 da Revisão 5 (Seção 31.5, P2-B): vocabulário de modalidade de interação e eixos de Scaffolding

Data: 2026-08-05. **Aplicado** — em `REFERENCE.md:238-265`, conferido linha a linha contra o texto abaixo, sem divergência. Nenhuma recompilação nem harness necessários (só documentação). Nenhum commit, nenhum push.

## Contexto da decisão

Vocabulário normativo para modalidade de interação e repertório de Scaffolding, hoje inexistente — terminologia alinhada à literatura (IHC e Scaffolding pedagógico), não inventada.

Fontes: taxonomia de modalidade de interação em IHC ("A Human-Centered Taxonomy of Interaction Modalities and Devices"); tipos funcionais de Scaffolding (Hannafin, Land & Oliver, 1999: conceitual, metacognitivo, procedimental, estratégico); manipulativos virtuais na educação matemática (feedback háptico).

Documentação apenas — nenhum código Java muda.

## Diff — `REFERENCE.md` §4.8

Inserção após a linha 236 ("...separada e ainda em aberto — não resolvida por este registro."), antes de "### 4.8.1":

```diff
 seleção de qual elemento do repertório usar em cada situação é uma decisão
 separada e ainda em aberto — não resolvida por este registro.
 
+Vocabulário de modalidade de interação (já citado em
+`gerard-semantic-event-logging/SKILL.md:41` como "modalidade de
+interação", nunca definido até aqui -- aberto, extensível, terminologia
+alinhada à taxonomia de modalidades de IHC): `MOUSE`, `TOQUE` (dedo, ex.
+dispositivos móveis), `TECLADO`, chamada programática, `NAO_INFORMADO`.
+Novas modalidades podem ser adicionadas conforme novos dispositivos forem
+suportados, sem quebrar as já existentes.
+
+O repertório de Scaffolding de cada objeto semântico (parágrafo anterior)
+é organizado em dois eixos independentes, alinhados à literatura de
+Scaffolding pedagógico:
+
+- **Tipo funcional** (Hannafin, Land & Oliver, 1999): conceitual (ajuda a
+  entender um conceito), metacognitivo (ajuda a refletir sobre o próprio
+  raciocínio), procedimental (ajuda a saber usar a interface/recursos),
+  estratégico (sugere uma abordagem para resolver a tarefa).
+- **Modalidade de entrega**: visual (mensagem/tela de ajuda), sonora
+  (som), háptica (vibração), manipulativa (material concreto -- diagramas
+  com quadradinhos manipuláveis, alinhado ao conceito de manipulativos
+  virtuais da educação matemática), guiada por movimento (atração
+  magnética -- altera a dinâmica do posicionamento para orientar o
+  participante).
+
+Ambos os eixos são abertos e extensíveis -- não são enums fechados.
+Scaffolding, nesse sentido, não se limita a uma resposta reativa a erro --
+é suporte pedagógico ajustável (pode ser proativo, reativo, ou diminuir
+conforme o participante ganha autonomia), consistente com a literatura de
+zona de desenvolvimento proximal.
+
 ### 4.8.1 Mobilização do invariante operatório: sugestão do sistema e atribuição do pesquisador
```

## Status

Aplicado em 2026-08-05. Nenhum commit, nenhum push sem pedido explícito.
