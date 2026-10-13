# Maturidade da API do Gérard — Modelo de Richardson (2026-10-13)

Escopo: as 26 rotas registradas (25 de API e a raiz estática) em `ServidorPrototipoWeb` (cliente: `web-poc/src/api.ts`).
Base da avaliação: leitura do código do servidor e do cliente, e um teste local com dois clientes simultâneos
(sem teste de carga e sem teste de segurança).

## Resultado

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
