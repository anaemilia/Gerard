# PROMPT PARA O CLAUDE — PRESERVAR A RODADA 5 COMO BASELINE E REGISTRAR NO GIT

Atue como engenheiro de configuração e controle de versão do projeto Gérard.

A Rodada 5 — unidade de análise A-B-C-D com explicações opcionais — foi concluída e deve ser preservada como uma baseline estável antes de qualquer novo upgrade.

Não implemente novas funcionalidades nesta etapa.

## Objetivo

1. Verificar o estado atual do projeto.
2. Preservar uma cópia integral da versão.
3. Registrar a versão no Git.
4. Criar uma branch e uma tag de baseline.
5. Documentar exatamente o que foi preservado.
6. Confirmar que o repositório pode retornar a essa versão futuramente.

## Identificação da baseline

Nome descritivo:

Rodada 5 — Unidade de Análise A-B-C-D com Explicações Opcionais

Branch sugerida:

```text
baseline/rodada-5-unidade-abcd
```

Tag sugerida:

```text
v0.5.0-rodada5-abcd
```

Mensagem de commit:

```text
baseline: preserva rodada 5 da unidade de análise A-B-C-D
```

Descrição da tag:

```text
Baseline estável da Rodada 5: unidade de análise A-B-C-D, seis protocolos, explicações opcionais, logs estruturados, schemas, cardinalidade e testes de regressão.
```

Se a branch ou tag já existir, não sobrescreva. Informe o conflito e use um sufixo seguro.

## Restrições

Nesta tarefa, não:

- alterar a lógica do Gérard;
- refatorar o código;
- corrigir limitações;
- adicionar o upgrade de mineração;
- integrar padrões explicativos ao ZDP;
- modificar J48, PART ou Apriori;
- mudar a tela de explicações;
- alterar os seis protocolos;
- modificar a arquitetura;
- colocar código solto na Main;
- apagar arquivos;
- usar `git reset --hard`;
- usar `git clean -fd`;
- usar force push;
- declarar push ou commit sem comprovação.

A baseline deve representar exatamente o estado concluído da Rodada 5.

## Verificação inicial

Execute e registre:

```bash
git rev-parse --show-toplevel
git status --short
git status
git branch --show-current
git log -5 --oneline
git remote -v
git tag --list
```

Verifique:

- se o diretório é realmente um repositório Git;
- a branch atual;
- alterações rastreadas e não rastreadas;
- arquivos gerados que não devem entrar no repositório;
- existência prévia da branch e da tag;
- remoto configurado e acessível.

Não descarte nenhuma alteração.

## Conteúdo que deve ser preservado

Inclua os arquivos necessários para reproduzir a Rodada 5, especialmente:

- classes da unidade de análise A-B-C-D;
- `AnalysisUnitAuditService`;
- `AnalysisUnitCardinalityReport`;
- `TesteUnidadeAnaliseABCD`;
- `OuvinteUnidadeAnalise`;
- alterações em `AgentAuditService`;
- instrumentação de `TelaArtefatoExplicativo`;
- alterações necessárias em `Main.java`;
- alterações em `TesteMonkeyGuiadoPorCasosReais`;
- schema da unidade de análise;
- testes e schemas das rodadas anteriores;
- relatório da Rodada 5;
- documentação técnica;
- configurações necessárias para compilação e execução.

Preserve também:

- distinção entre ações canônicas e reativas;
- idempotência;
- correção do disparo triplo;
- rastreamento do Robot;
- validação de schema;
- cardinalidade;
- auditoria de MONITOR, ZDP e MODELADOR;
- restauração das bases reais;
- testes das Rodadas 3 e 4.

## Arquivos que não devem entrar indiscriminadamente no Git

Examine o `.gitignore` e não adicione sem revisão:

- diretórios de compilação;
- arquivos `.class`;
- temporários;
- caches;
- logs repetidos;
- credenciais;
- arquivos locais da IDE;
- bases reais de usuários;
- arquivos com dados pessoais;
- diretórios temporários de teste.

Informe quais arquivos ficaram:

- versionados no Git;
- preservados apenas no ZIP externo;
- excluídos por segurança ou por serem artefatos gerados.

## Pacote externo da baseline

Crie uma cópia integral da versão atual fora do diretório de trabalho ou em uma pasta de releases.

Nome sugerido:

```text
Gerard_baseline_rodada5_unidade_abcd_2026-08-01.zip
```

Inclua:

- código-fonte;
- recursos;
- schemas;
- testes;
- documentação;
- relatório da Rodada 5;
- configurações necessárias;
- lista de arquivos;
- hash do commit, quando disponível.

Não inclua credenciais, tokens, chaves privadas, bases pessoais, temporários ou builds desnecessários.

Calcule e registre o SHA-256 do ZIP.

PowerShell:

```powershell
Get-FileHash Gerard_baseline_rodada5_unidade_abcd_2026-08-01.zip -Algorithm SHA256
```

Linux ou Git Bash:

```bash
sha256sum Gerard_baseline_rodada5_unidade_abcd_2026-08-01.zip
```

## Documento da baseline

Crie:

```text
BASELINE_RODADA_5_UNIDADE_ABCD.md
```

Registre:

- nome;
- data;
- branch;
- tag;
- hash do commit;
- objetivo;
- arquivos criados e modificados;
- compilação;
- testes;
- resultados;
- limitações;
- arquivos não versionados;
- localização do ZIP;
- SHA-256;
- instruções de restauração.

Registre os resultados reais conhecidos:

- compilação completa sem erros;
- 17 testes da unidade A-B-C-D aprovados;
- 14 episódios;
- 45 passos;
- 0 divergências;
- 58 unidades de análise;
- 58 ações de protocolo;
- 352 eventos técnicos;
- cardinalidades consistentes;
- testes anteriores preservados.

Registre também as limitações:

1. ORIENTAR, CAMINHO e QUANTIFICAR ainda não chegam como `protocol_type` de ações B avaliadas diretamente pelos agentes;
2. reabrir uma explicação para uma unidade já fechada não atualiza a linha anterior de `unidades_analise.jsonl`;
3. o replay Robot não abre a tela de explicações;
4. `explicacoes_usuario.jsonl` pode permanecer vazio no replay Robot;
5. a cardinalidade mínima das respostas depende da lógica do serviço;
6. o upgrade de base de conhecimento e mineração periódica ainda não faz parte desta baseline.

## Criação da branch

Registre o commit-base:

```bash
git rev-parse HEAD
```

Crie a branch:

```bash
git switch -c baseline/rodada-5-unidade-abcd
```

Se necessário:

```bash
git checkout -b baseline/rodada-5-unidade-abcd
```

## Preparação do commit

Antes de adicionar:

```bash
git status --short
```

Adicione arquivos de forma controlada. Não use `git add .` sem revisar tudo.

Depois:

```bash
git diff --cached --stat
git diff --cached --name-status
```

Confirme que não há credenciais, dados pessoais, arquivos temporários, builds, logs excessivos ou exclusões acidentais.

Para retirar algo apenas do stage:

```bash
git restore --staged CAMINHO_DO_ARQUIVO
```

## Commit

```bash
git commit -m "baseline: preserva rodada 5 da unidade de análise A-B-C-D"
```

Registre:

```bash
git rev-parse HEAD
git show --stat --oneline HEAD
git status
```

## Tag anotada

```bash
git tag -a v0.5.0-rodada5-abcd -m "Baseline estável da Rodada 5: unidade de análise A-B-C-D com explicações opcionais"
```

Confirme:

```bash
git show v0.5.0-rodada5-abcd --no-patch
git rev-list -n 1 v0.5.0-rodada5-abcd
git rev-parse HEAD
```

A tag deve apontar exatamente para o commit da baseline.

## Envio ao remoto

Só faça push se houver remoto acessível, credenciais válidas e nenhum risco de sobrescrever trabalho.

```bash
git push -u origin baseline/rodada-5-unidade-abcd
git push origin v0.5.0-rodada5-abcd
```

Não use force push.

Se o remoto não estiver disponível, preserve branch, commit e tag localmente, registre o erro e apresente os comandos pendentes.

## Verificação final

Execute:

```bash
git status
git branch --show-current
git log -3 --oneline --decorate
git tag --points-at HEAD
git remote -v
```

Se houve push:

```bash
git ls-remote --heads origin baseline/rodada-5-unidade-abcd
git ls-remote --tags origin v0.5.0-rodada5-abcd
```

Confirme:

- branch existente;
- tag existente;
- ambas apontando para o commit correto;
- commit contendo os arquivos esperados;
- nenhuma funcionalidade nova;
- ZIP externo criado;
- SHA-256 registrado;
- documento da baseline criado;
- restauração possível.

## Restauração

Para abrir a branch:

```bash
git switch baseline/rodada-5-unidade-abcd
```

Para criar uma nova branch a partir da tag:

```bash
git switch -c restauracao/rodada-5 v0.5.0-rodada5-abcd
```

Não recomende trabalhar diretamente em detached HEAD.

## Relatório final

Entregue:

1. diretório raiz;
2. branch original;
3. branch criada;
4. hash do commit;
5. tag criada;
6. hash da tag;
7. remoto;
8. resultado do push da branch;
9. resultado do push da tag;
10. arquivos incluídos;
11. arquivos preservados apenas no ZIP;
12. arquivos excluídos por segurança;
13. caminho do ZIP;
14. SHA-256;
15. resultado de `git status`;
16. resultado de `git log`;
17. verificação remota;
18. limitações;
19. comandos de restauração;
20. confirmação de que nenhuma nova funcionalidade foi implementada.

Classifique cada item como:

- executado com sucesso;
- executado com ressalvas;
- não executado;
- não disponível;
- falhou.

Não invente comandos, commits, hashes, pushes ou resultados.
