# Maturidade da API do Gérard — Modelo de Richardson (2026-10-13)

Escopo: as 26 rotas registradas (25 de API e a raiz estática) em `ServidorPrototipoWeb` (cliente: `web-poc/src/api.ts`).
Base da avaliação: leitura do código do servidor e do cliente, e um teste local com dois clientes simultâneos
(sem teste de carga e sem teste de segurança).

## Andamento (2026-10-13)

| Item | Situação |
|---|---|
| P0 isolamento por usuário | **feito**: sessão por visitante (cookie `gerard_sessao`), log num arquivo só com usuário e contexto por visitante, índice de tentativas em memória. 100 usuários simultâneos: 0 erros, 0 julgamentos cruzados, p99 10 ms (`scripts/benchmark/CargaMultiusuarioGerard.java`). |
| P1 nível 1: recursos | **feito**: a tentativa é um recurso (`/api/v1/tentativas/{id}/...`, ver `RotasTentativa`); a pessoa também (`/api/v1/usuarios/{id}`). Rotas com verbo removidas (23, mais `GET /api/situacao`, que sorteava). `TesteRecursosTentativaWeb` (HTTP), `e2e_recursos_tentativa_web.mjs` e os e2e existentes (mouse real). |
| P2 nível 2: verbos e status | **feito para a tentativa**: `GET` só lê (com ETag e `If-None-Match`), `PUT` onde repetir dá o mesmo (`idioma`, `eixo/visibilidade`), `POST` onde cada chamada é um evento; 201 + `Location`, 404/410/409/422/400/415/405 (com `Allow`)/428/412/429 (com `Retry-After`); erros em `application/problem+json` com `type` dereferenciável (`GET /api/v1/problemas/{slug}`); `ETag`/`If-Match` em toda alteração. A tabela rota × verbo × status é o `TesteRecursosTentativaWeb` (33 linhas, HTTP real). |
| P3 nível 3: hipermídia | **feito**: a entrada `GET /api/v1` (`application/hal+json`) publica os 10 links de onde tudo parte; o estado da tentativa traz `_links` (`self`, `tentativas`, `idioma`, `ajudas`), cada figura com papel traz os recursos do papel (`posicionamento`, `valor`, `confirmacao_valor`, `quadradinhos`, `sinal`, `eixo_visibilidade`, `eixo_projecao`), cada pessoa traz `editar`; `acoes_disponiveis` segue como a lista do que é possível agora (ação com método, href e corpo sugerido). O React só conhece `/api/v1` e segue links. Prova: `TesteHipermidiaWeb` e os e2e de mouse real com o recurso das tentativas renomeado no servidor (`-Dgerard.api.recurso.tentativas=percursos`): nada no cliente quebra. |
| P4 contrato e operação | **feito, menos o teste de carga no Render**: contrato OpenAPI 3.1 servido em `GET /api/v1/openapi.json` e guardado em `documentacao/api/openapi.json` (a tabela de rotas da tentativa gera as rotas do contrato; `TesteContratoOpenApiWeb` confere servido = repositório e que as 33 operações documentadas existem); `/api/pesquisa/log` e `/api/curadoria/situacoes` passam a `/api/v1/...` e os caminhos antigos seguem até 2027-01-11 com `Deprecation`, `Sunset` e `Link rel=successor-version`; dois perfis (visitante: cookie de sessão; pesquisador: `Authorization: Bearer`, token em `PESQUISADOR_TOKEN`; `X-Curadoria-Token` aceito e marcado como depreciado); `X-Request-Id`, log JSON por requisição sem segredo e `GET /api/v1/metricas` (só pesquisador); CORS explícito (`CORS_ORIGENS`; sem lista, só mesma origem). `TesteOperacaoWeb`. |

Semântica de cada verbo nas rotas da tentativa (a fonte de verdade é o teste):

| Rota | Verbo | Altera | Por quê |
|---|---|---|---|
| `/tentativas` | POST | cria | cada sorteio é uma tentativa nova (201, `Location`) |
| `/tentativas/{id}` | GET | não | leitura segura; `ETag`, 304 com `If-None-Match` |
| `.../classificacao`, `.../classificacao/confirmacao` | POST | sim | cada escolha é um evento registrado e conta rejeição |
| `.../papeis/{p}/posicionamento`, `.../valor`, `.../valor/confirmacao`, `.../quadradinhos`, `.../sinal` | POST | sim | cada chamada é um evento do log; não são idempotentes |
| `.../operacoes`, `.../conclusao`, `.../ajudas`, `.../reinicio` | POST | sim | idem |
| `.../idioma` | PUT | sim | grava o idioma; repetir dá o mesmo estado |
| `.../papeis/{p}/eixo/visibilidade` | PUT | sim | grava revelado ou oculto |
| `.../papeis/{p}/eixo/projecao` | POST | não | cálculo puro; não exige nem avança a versão |

Formato adotado no P3: HAL para `_links` (rel → `{href, metodo?, templated?}`; o `metodo` é uma extensão, só presente quando não é GET) e,
ao lado, `acoes_disponiveis` com o corpo sugerido de cada ação, no papel que o Siren dá às *actions*. Os `href` das ações e dos links
saem do mesmo lugar (`RotasTentativa`), então não divergem.

Política de depreciação (P4): um caminho que sai de uso continua atendendo até a data em `Sunset`, sempre com `Deprecation: true` e
`Link: <novo>; rel="successor-version"`; o contrato marca a operação como `deprecated`. Depois da data, o caminho é removido e o
contrato e o teste mudam juntos. Hoje: `/api/pesquisa/log` e `/api/curadoria/situacoes` até 2027-01-11.

Cobertura por mouse real das rotas da API (`tests/web`): classificação, posicionamento, valor, confirmação, conclusão, reinício, idioma e
ajuda (e2e anteriores e `e2e_recursos_tentativa_web`); e, em `e2e_rotas_restantes_web` (14 verificações), quadradinhos, sinal,
eixo (visibilidade nos dois sentidos e projeção) e a pessoa (cadastrar, entrar e editar pelo link `editar`). Sem mouse real: `operacoes`
(coberta pelos e2e de registro no limite da operação), relatos e sincronizações.

Não feito no P4: o teste de carga **no Render**. Rodá-lo contra o serviço publicado gravaria eventos falsos no banco de pesquisa
(`pesquisa_log.db`) e usaria o serviço real; a carga foi medida local (100 e 300 usuários, ver `scripts/benchmark/CargaMultiusuarioGerard.java`).
Para medir no Render é preciso um serviço de homologação com banco próprio. Também fora: contas individuais de pesquisador, rotação de
token e papéis além de visitante e pesquisador.

Fora do P2 de propósito: `usuarios`, `sessao`, `relatos`, `pesquisa/log` e `curadoria` seguem com o formato de erro novo
(problem+json) mas sem `ETag`/`If-Match` (não há duas abas disputando a mesma pessoa). Uma aba que fica para trás depois de
outro sorteio recebe 410 e a tela não mostra nada novo (não há texto de interface definido para isso; não inventei um).

Pontos que o P1 deixou de propósito para os níveis seguintes: os verbos ainda são quase todos `POST` (o P2 troca por `PUT`/`GET`
conforme a idempotência), os status continuam os de antes (400/422 genéricos, com 404 só para tentativa inexistente ou
substituída e rota desconhecida) e o cliente ainda monta algumas URIs (o P3 passa a seguir links). `/api/pesquisa/log` e
`/api/curadoria/situacoes` ficaram onde estavam (já eram recursos e têm clientes fora do repositório); entram na versão
`/api/v1` no P4, com depreciação.

## Resultado (antes do P0 e do P1)

| Nível | Exigência | Situação | Evidência |
|---|---|---|---|
| 0 | Um endpoint HTTP, corpo JSON | **atendido** | `/api/*` com JSON |
| 1 | Recursos com identidade (URIs são substantivos) | **não atendido** | 19 das 25 rotas de API (fora a raiz estática `/`) são verbos/ações (`/api/acoes/soltar-numero`, `/api/reiniciar`, `/api/acoes/escolher-sinal`). As 6 que parecem recursos (`/api/situacao`, `/api/usuarios`, `/api/sessao/usuario`, `/api/gestos`, `/api/curadoria/situacoes`, `/api/pesquisa/log`) não têm `/{id}`. A tentativa não é um recurso endereçável. |
| 2 | Verbos HTTP e códigos de status com a semântica correta | **parcial** | Mudança de estado só por `POST` (23 handlers). `GET /api/situacao` **não é leitura**: sorteia de novo e altera o estado. Erros 400 genéricos (22 respostas) com `{"erro": "texto"}`, sem código estável. Não há 404 para recurso inexistente (só da rota), 409, 410 nem 429. Só `/api/usuarios` usa `PUT`. Sem idempotência declarada. |
| 3 | Hipermídia (HATEOAS) | **parcial, informal** | Cada estado publica `acoes_disponiveis` (`id`, `metodo`, `href`, `corpo`), e parte do cliente segue o `href`. Mas `api.ts` tem 16 URLs fixas no código. Não há formato padrão (HAL, JSON:API, Siren), nem `links` por recurso. |

**Classificação: nível 0 completo, nível 1 não atingido; níveis 2 e 3 só em parte.** Em termos práticos, é uma API RPC sobre
HTTP com uma ideia boa de hipermídia (`acoes_disponiveis`).

## Problema bloqueante fora do modelo

**Não há isolamento entre usuários.** O servidor tem uma única instância de `ServicoSorteioAtividade`
compartilhada por todas as requisições, e nenhuma rota lê o cookie. Teste local: A sorteia, B sorteia, A responde
com a categoria certa da situação dele e é julgado errado (7 de 7 rodadas com categorias diferentes).
Consequências: duas pessoas simultâneas se sobrescrevem; o log de pesquisa mistura as ações. Isso precisa ser resolvido
**antes** dos níveis abaixo, porque define o que é o recurso "tentativa".

## Pendências para chegar ao nível 3

Ordem de execução sugerida. Cada item tem um critério de aceite verificável.

### P0 — Isolamento por usuário (bloqueante)
1. Criar um identificador de tentativa na primeira chamada e devolvê-lo ao cliente.
2. Manter um `ServicoSorteioAtividade` por identificador, com expiração e limite de memória.
3. **Aceite:** teste com dois clientes simultâneos (como o de 2026-10-13) com 0 julgamentos cruzados; e2e de mouse real
   continua passando.

### P1 — Nível 1: recursos
4. Modelar os substantivos: `tentativas`, `situacoes`, `categorias`, `usuarios`, `sessoes`, `log-pesquisa`, `idiomas`.
5. Dar identidade à tentativa: `/api/v1/tentativas/{id}`; ações viram sub-recursos ou transições do recurso.
   Exemplos: `POST /tentativas` (sorteia), `PUT /tentativas/{id}/categoria`, `PUT /tentativas/{id}/papeis/{papel}`
   (posiciona ou engata), `PUT /tentativas/{id}/papeis/{papel}/valor`, `POST /tentativas/{id}/reinicio`.
6. Separar `gestos` e `relato-bug` como coleções (`POST /gestos`, `POST /relatos`).
7. **Aceite:** nenhuma URI com verbo; nenhuma URI de ação com `/acoes/`.

### P2 — Nível 2: verbos e status
8. `GET` só lê (hoje `GET /api/situacao` sorteia). `GET /tentativas/{id}` devolve o estado atual sem efeito.
9. Usar `POST` (criar), `PUT` (substituir de forma idempotente) e `DELETE` onde couber; documentar a idempotência
   de cada rota.
10. Códigos de status por situação: 201 + `Location` ao criar; 404 para tentativa inexistente; 409 para ação
    incompatível com o estado (ex.: ação fora de ordem); 410 para tentativa expirada; 415 e 422 para corpo inválido;
    429 para excesso de requisições.
11. Erros num formato único: `application/problem+json` (RFC 9457) com `type` estável, `title` e `detail`.
12. Cabeçalhos: `ETag` e `If-Match` nas transições de estado (evita duas abas sobrescreverem), `Cache-Control` correto.
13. **Aceite:** tabela rota × verbo × status esperados, com teste automático por linha.

### P3 — Nível 3: hipermídia
14. Adotar um formato (recomendo HAL ou Siren), mantendo `acoes_disponiveis` como fonte das transições.
15. Cada resposta traz `_links` (`self`, `tentativa`, `proxima`, e as transições permitidas agora).
16. O React passa a **seguir links** e a não montar URLs: eliminar as 16 URLs fixas de `api.ts` (exceto a raiz
    de entrada da API).
17. **Aceite:** trocar o caminho de uma rota no servidor não quebra o cliente; e2e de mouse real passa.

### P4 — Contrato e operação
18. Publicar o contrato em **OpenAPI 3.1** (hoje só existe em `contratos.ts` e no código do servidor), gerado ou
    verificado contra o servidor por teste.
19. Versionar a URL (`/api/v1`) além do `schema` já presente nos payloads; política de depreciação.
20. Autenticação e autorização por tentativa e por perfil (pesquisador, aluno); hoje há token só em `/api/curadoria/*`
    e `/api/pesquisa/log`.
21. Observabilidade: `X-Request-Id`, log estruturado, métricas básicas.
22. Teste de carga mínimo no Render (N usuários simultâneos) e política de CORS explícita.

## Observações
- O desktop (Swing) não usa HTTP; ele deve chamar a mesma camada de aplicação em processo. Esta lista vale só para o cliente web.
- As mudanças de P1 a P3 alteram o contrato e exigem ajuste do React e dos testes de navegador (mouse real) na mesma entrega.
- Itens P0 e P2 (item 8) corrigem comportamento atual; os demais são evolução do contrato.
