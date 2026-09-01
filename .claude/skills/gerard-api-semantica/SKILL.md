---
name: gerard-api-semantica
description: Orienta contratos HTTP/JSON, versionamento, compatibilidade e avaliação de maturidade da API semântica do Gérard para clientes web e mobile, sem transferir regras do domínio ao cliente.
---

# API semântica do Gérard

## Definição e limite

**API semântica do Gérard** é a fronteira HTTP/JSON que publica estados,
comandos e decisões já produzidos pela aplicação e pelo domínio para
adaptadores externos, como web e mobile.

A denominação não atribui semântica ao transporte. A API:

- transporta projeções versionadas e recebe comandos semanticamente identificados;
- não interpreta situações, não classifica categorias e não calcula relações;
- não transforma o cliente em proprietário de regras matemáticas ou pedagógicas;
- não substitui os objetos e relações estruturais proprietários do conhecimento.

Antes de modificar essa fronteira, leia `gerard-domain-model-first`,
`gerard-knowledge-locality-principle` e `gerard-consistencia-estado`. Consulte
`gerard-semantic-event-logging` quando comandos ou respostas produzirem fatos
da atividade.

## Contratos

- Versione o significado do documento, não apenas a URL.
- Preserve identidades semânticas estáveis entre Swing, web e mobile.
- Faça o servidor informar estado, ações disponíveis e resultados factuais.
- Trate JSON, HTTP, serialização e códigos de status como infraestrutura.
- Não aceite fallbacks semânticos no cliente quando um campo estiver ausente.
- Mudanças incompatíveis exigem nova versão e período explícito de compatibilidade.

## Avaliação de maturidade

Avalie separadamente:

1. **maturidade REST**, pelo Richardson Maturity Model:
   - RMM 0: HTTP como túnel;
   - RMM 1: recursos identificados;
   - RMM 2: métodos e códigos HTTP coerentes;
   - RMM 3: controles hipermídia das transições permitidas.
2. **maturidade semântica multiplataforma**, pela escala operacional do Gérard:
   - G0: semântica duplicada no cliente;
   - G1: projeção semântica, com ciclo de comandos parcial;
   - G2: comandos avaliados no servidor e resultados factuais versionados;
   - G3: protocolo completo nas categorias suportadas;
   - G4: conformidade entre clientes e ciclo de vida de versões.

HATEOAS não é objetivo automático. Priorize o próximo incremento que reduza
duplicação semântica e torne o protocolo observável e compatível.

Esta escala G0–G4 é uma convenção operacional interna do Gérard, não um padrão
externo. Para critérios detalhados, evidências e fotografia atual, leia
[references/maturidade.md](references/maturidade.md).

## Verificação mínima

- contratos e erros possuem testes executáveis;
- respostas não expõem classes Swing/AWT nem dependem de `Main`;
- o cliente apenas materializa decisões recebidas;
- ações preservam identidade, origem e correlação factual;
- a documentação distingue recurso implementado de intenção futura.
