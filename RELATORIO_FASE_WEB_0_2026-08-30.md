# Relatório — Fase Web 0: prova de independência da renderização

Data: 2026-08-30

## Objetivo

Criar uma realização web mínima, somente de leitura, para verificar se uma
`SituacaoProblema` rica pode alimentar outra tecnologia de apresentação sem
transferir regras semânticas para o navegador e sem alterar o comportamento
da aplicação Swing.

## Recorte implementado

- `ProjetorSituacaoProblemaPortatil` produz o contrato versionado
  `gerard.situacao-problema.portatil.v1`.
- A projeção preserva identidades nominais, categoria canônica, papel da
  incógnita original, valores, domínios numéricos, descritores abstratos,
  relações, critérios de operação, correspondências e narrativa.
- O projetor rejeita agregados semanticamente inconsistentes. Ele não
  recalcula relações, não corrige a curadoria e não conhece Swing ou AWT.
- `web-poc` consome uma amostra JSON e realiza `COMPOSICAO_MEDIDAS` com
  HTML/SVG, usando geometria própria do navegador e a paleta neutra quente.
- O JavaScript valida somente a versão e a integridade mínima do contrato.
  Ele não avalia a relação aditiva nem escolhe ajuda pedagógica.

## Natureza da amostra

`web-poc/dados/situacao-exemplo.json` é um exemplo técnico explicitamente
marcado como `CANDIDATA_NAO_CURADA`. Ele não foi incluído no catálogo curado e
não é apresentado como situação validada pelo pesquisador. O teste contratual
reconstrói o agregado com declarações nominais e confirma que o JSON corresponde
exatamente à projeção Java.

## Verificações executadas

- sintaxe de `web-poc/app.js`: aprovada por `node --check`;
- contrato Java isolado: aprovado;
- três recursos HTTP locais (`/`, `/app.js` e o JSON): resposta 200;
- `scripts/verificar_regressao_gerard.py`: aprovado, sem falhas;
- build completo: 534 fontes Java compiladas;
- linha de base: 102 testes compilados, 97 executados e aprovados, 5 testes
  gráficos não executados por exigirem display, 0 reprovados;
- `TesteContratoWebSituacaoProblema` participou da linha de base e foi
  aprovado.

A inspeção visual automatizada no navegador local não foi concluída porque a
conexão de controle do navegador foi bloqueada pelo ambiente (`EPERM`). O
relatório não apresenta essa inspeção como realizada.

## Necessidades antecipadas pelo protótipo

A fronteira HTTP/JSON passa a ser denominada **API semântica do Gérard**:
ela publica estados, comandos e decisões produzidos pelo domínio, sem possuir
ou reinterpretar esse conhecimento. Os critérios de maturidade e a fotografia
atual ficam em `.agents/skills/gerard-api-semantica/references/maturidade.md`.

1. O próximo contrato deve ser produzido a partir de um sidecar rico realmente
   promovido pelo pesquisador, e não de uma amostra técnica.
2. A camada de transporte precisa de um ponto de publicação real; JSON continua
   sendo infraestrutura, nunca fonte semântica.
3. O gerador de cena desktop ainda usa `java.awt.Rectangle`; a web confirmou a
   necessidade de uma geometria neutra ou de geradores próprios por plataforma.
4. Rótulos finais e textos da página precisam passar pela mesma política de
   localização antes de ampliar o protótipo para outros idiomas.
5. A fase interativa deverá traduzir `PointerEvent` para comandos neutros e
   preservar a distinção entre gesto e ação instrumental.
6. A equivalência entre Swing e web deve comparar identidades, valores,
   relações, decisões e eventos, nunca igualdade de pixels.

## Arquivos novos

- `src/gerard/aplicacao/portabilidade/ProjetorSituacaoProblemaPortatil.java`
- `tests/java/TesteContratoWebSituacaoProblema.java`
- `web-poc/index.html`
- `web-poc/styles.css`
- `web-poc/app.js`
- `web-poc/dados/situacao-exemplo.json`
- `web-poc/README.md`

Nenhum arquivo da aplicação Swing foi alterado pela Fase Web 0.
