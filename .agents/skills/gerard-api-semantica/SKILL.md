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

Ao classificar a API, comparar clientes ou priorizar o próximo incremento,
leia [references/maturidade.md](references/maturidade.md). A avaliação separa
maturidade REST e maturidade semântica multiplataforma; não transforma
HATEOAS em objetivo automático nem transfere regras para o cliente.

## Verificação mínima

- contratos e erros possuem testes executáveis;
- respostas não expõem classes Swing/AWT nem dependem de `Main`;
- o cliente apenas materializa decisões recebidas;
- ações preservam identidade, origem e correlação factual;
- a documentação distingue recurso implementado de intenção futura.
