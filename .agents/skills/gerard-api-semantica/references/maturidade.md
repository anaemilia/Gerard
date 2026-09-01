# Maturidade da API semântica do Gérard

## Eixo 1 — Richardson Maturity Model

- **RMM 0 — HTTP como túnel:** um ponto genérico recebe operações indistintas.
- **RMM 1 — recursos:** URIs distinguem recursos ou capacidades.
- **RMM 2 — semântica HTTP:** métodos, códigos de status e representações são
  usados coerentemente.
- **RMM 3 — hipermídia:** a resposta publica controles ou links das transições
  atualmente permitidas.

O nível mede estilo REST, não correção matemática, segurança ou qualidade total.

## Eixo 2 — maturidade semântica multiplataforma do Gérard

Esta é uma escala operacional interna, não um padrão externo:

- **G0 — semântica duplicada:** o cliente interpreta ou recalcula regras.
- **G1 — projeção semântica:** o servidor publica identidades, papéis e estado,
  mas o ciclo de comandos ainda é parcial.
- **G2 — comandos semanticamente avaliados:** o servidor avalia comandos,
  devolve resultado factual e mantém contrato versionado para o recorte coberto.
- **G3 — protocolo completo:** todas as categorias suportadas publicam estado,
  ações permitidas e transições, preservando consistência e eventos.
- **G4 — conformidade multiplataforma:** testes de contrato verificam equivalência
  semântica entre clientes, compatibilidade de versões, observabilidade e ciclo
  explícito de descontinuação.

Uma API pode estar em níveis diferentes por capacidade. Não anuncie como nível
global aquilo que só foi verificado em uma categoria.

## Fotografia verificada — 2026-08-31

### REST

**RMM 2 parcial.** Há recursos/endpoints específicos, JSON, distinção de métodos
HTTP e respostas como `405` e `422`. Ainda não existem controles hipermídia que
publiquem as próximas ações permitidas.

### Semântica multiplataforma

- **Sorteio e projeção das seis categorias: G1.** O servidor publica situação
  curada, papéis, categoria, diagrama abstrato e enunciado materializado.
- **Composição de Medidas: G2 no ciclo já implementado.** Posicionamento e
  validação são executados pelo domínio Java e retornam resultado factual.
- **Demais cinco categorias: G1.** Ainda faltam coordenadores/adaptadores de
  comando próprios; projeção existente não autoriza declarar interação completa.
- **API como um todo: entre G1 e G2, por capacidade.** G3 e G4 ainda não foram
  demonstrados.

## Próximas evidências de maturidade

1. Publicar no estado as ações atualmente disponíveis, decididas no servidor.
2. Completar comandos das demais categorias sem condicionais semânticas no cliente.
3. Criar testes de contrato comuns para web e futuro cliente mobile.
4. Definir política de compatibilidade e descontinuação das versões JSON.
5. Correlacionar comandos da API com eventos factuais sem duplicar ações.

