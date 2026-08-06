# Diff proposto — item 6 da Revisão 5 (Seção 31.6): arquitetura de evento envelope + payload

Data: 2026-08-05. **Proposto, nenhum aplicado ainda** — aguardando aprovação.

## Contexto da decisão

Arquitetura de evento entre as quatro comparadas na Seção 19.1 da Revisão 5. Documentação apenas — nenhum código Java muda, `EventoPapelQuantitativo` não é alterado nesta execução.

Decisão: envelope + payload, com estrutura baseada em três padrões existentes (não inventada) — xAPI/Experience API e Caliper Analytics (1EdTech/IMS Global, específicos de eventos de aprendizagem) e CloudEvents (CNCF, especificação geral de arquitetura orientada a eventos). Os três convergem para núcleo fixo + extensão variável por tipo de evento.

## Diff — `REFERENCE.md` §4.8

Inserção após a linha 265 ("...consistente com a literatura de zona de desenvolvimento proximal."), antes de "### 4.8.1":

```diff
 é suporte pedagógico ajustável (pode ser proativo, reativo, ou diminuir
 conforme o participante ganha autonomia), consistente com a literatura de
 zona de desenvolvimento proximal.
 
+Arquitetura de evento: adotada a estrutura envelope + payload (Seção 19.1
+da Revisão 5), com referência a três padrões existentes -- xAPI/Experience
+API e Caliper Analytics (1EdTech/IMS Global, específicos de eventos de
+aprendizagem: estrutura Ator-Verbo-Objeto/Ação com contexto e resultado
+opcionais) e CloudEvents (CNCF, especificação geral de arquitetura
+orientada a eventos: envelope fixo com `data` variável). Nenhuma estrutura
+nova foi inventada para esta decisão.
+
+Envelope (núcleo fixo, presente em todo evento): `event_id`, `action_id`
+(Seção anterior, cardinalidade 1:N), tipo semântico do evento, origem da
+ação (`OrigemAcao`), timestamp.
+
+Payload (variável, específico por tipo de evento): estado anterior/
+posterior, valor proposto ou calculado, resultado da validação,
+diagnóstico factual (quando houver rejeição), modalidade de interação
+(parágrafo anterior), estilo de Scaffolding utilizado, quando houver
+(parágrafo anterior).
+
+Esta é uma decisão de arquitetura-alvo, não uma implementação --
+`EventoPapelQuantitativo` continua como está até uma decisão explícita de
+implementar esta reestruturação.
+
 ### 4.8.1 Mobilização do invariante operatório: sugestão do sistema e atribuição do pesquisador
```

## Status

Não aplicado. Depois de aplicado: não é necessário recompilar nem rerodar harness — é só `REFERENCE.md`, nenhum código muda. Nenhum commit, nenhum push sem pedido explícito.
