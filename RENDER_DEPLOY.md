# Deploy da prova web do Gérard no Render

Este documento descreve como publicar a prova funcional web (`web-poc/` +
`gerard.infraestrutura.web.ServidorPrototipoWeb`) no Render, usando o
`Dockerfile` na raiz do repositório.

## O que é publicado

Uma única imagem Docker, em três etapas:

1. build do frontend (`web-poc/`) com Node — gera `web-poc/dist`;
2. build do backend Java (todo `src/`, mesmo `source`/`target` 1.8 do
   `build.xml`) — gera `build/classes`;
3. imagem final, só com JRE 11, as classes compiladas, os jars de `lib/` e
   `web-poc/dist`, rodando `gerard.infraestrutura.web.ServidorPrototipoWeb`.

Esse servidor Java único serve os endpoints `/api/*` e os arquivos estáticos
do frontend na mesma porta — não há um serviço de frontend separado.

## Passo a passo no painel do Render

1. Em https://dashboard.render.com, **New > Web Service**.
2. Conecte o repositório `anaemilia/Gerard` no GitHub e escolha a branch
   `web/deploy-render` (ou a branch para onde este trabalho for mesclado).
3. O Render detecta o `Dockerfile` na raiz automaticamente (Runtime: Docker).
   Não é preciso definir build/start command — estão no `Dockerfile`.
4. Plano: **Free** é suficiente para a prova funcional.
5. Deploy. O Render define a variável `PORT` sozinho; o servidor já lê essa
   variável (`System.getenv("PORT")`, com 8080 como padrão local).

Alternativa: usar **New > Blueprint** apontando para este repositório — o
`render.yaml` na raiz já descreve o serviço acima.

## Limitações conhecidas desta primeira publicação

- Plano Free do Render "dorme" o serviço após um período sem acesso; a
  primeira requisição depois disso demora mais (cold start).
- O sistema de arquivos do container é efêmero: qualquer escrita local (por
  exemplo, em `System.getProperty("user.home")`) some a cada novo deploy ou
  reinício. As situações-problema (`situacoes_vergnaud.tsv`) são lidas do
  classpath, então isso não afeta a leitura — só afetaria fluxos de
  curadoria que gravem arquivo local, que não fazem parte desta prova web.
- Esta imagem publica exatamente o escopo da prova funcional descrita em
  `web-poc/README.md` (ação completa de composição) — não o Gérard desktop
  completo.
