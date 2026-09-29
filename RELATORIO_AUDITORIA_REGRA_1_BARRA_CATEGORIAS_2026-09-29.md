# Auditoria da Regra 1 — `BarraCategorias.tsx`

Data: 2026-09-29.

Escopo: textos fixos dos diálogos de relato de problema, usuário e upload de
curadoria. A auditoria compara o texto literal com `src/Main.java` e
`src/gerard/i18n/mensagens_pt.properties`. Texto dinâmico vindo de respostas
do servidor não foi classificado como texto fixo do componente.

## Relato de problema

Todos os textos possuem fonte literal permitida:

| Texto no diálogo | Fonte |
|---|---|
| `Reportar problema` | `ui.bug.title` |
| instrução iniciada por `Descreva o que você estava fazendo...` | `ui.bug.instruction` |
| `Descrição do problema` | `ui.bug.description.label` |
| `Descreva o problema antes de registrar o relato.` | `ui.bug.required` |
| `Preparar e-mail` | `ui.bug.submit` |
| `Cancelar` | `ui.userDialog.cancel` |
| `Fechar` | `ui.dialog.categoryExplanation.close` |

## Usuário

Os títulos, instruções, campos, opções e ações correspondem literalmente às
chaves `ui.tooltip.user` e `ui.userDialog.*`: `Usuário`, `Quem está usando o
Gérard agora?`, `Usuários cadastrados`, `Cadastrar novo usuário`, `Meus
dados`, `Edite seus dados cadastrados.`, `Nome`, `Idade`, `Sexo`, `Masculino`,
`Feminino`, `Outro`, `Mídia preferida`, `Som`, `Gráfico`, `Linguagem natural`,
`Vídeo`, `História em quadrinhos`, `Nível de escolaridade`, `1º grau`, `2º
grau`, `Graduação`, `Pós-graduação`, `Foto`, `Entrar`, `Salvar`, `Cadastrar e
entrar` e `Cancelar`.

Duas divergências literais foram removidas sem criar conteúdo:

- `Escolher foto…` passou a `Escolher foto...`, igual a
  `ui.userDialog.photo.choose`;
- o texto alternativo inventado `Foto escolhida` passou a `Foto`, igual a
  `ui.userDialog.photo.placeholder`.

## Upload de curadoria

Não foi encontrada fonte literal permitida para estes textos. Eles foram
preservados, sem substituição aproximada e sem criação de chaves, e dependem
de decisão da pesquisadora:

1. `Atualizar curadoria`;
2. `Envia o arquivo de situações curadas (.tsv) para substituir, só em memória neste servidor, as situações usadas no sorteio. Vale até o próximo deploy/restart.`;
3. `Token`;
4. `Arquivo (.tsv)`;
5. `Selecionado:`;
6. `Não foi possível ler o arquivo selecionado.`;
7. `Curadoria atualizada: {total_situacoes} situações carregadas ({total_validadas} validadas).`.

`Fechar` possui fonte em `ui.dialog.categoryExplanation.close`. `Enviar`
existe literalmente em `ui.chat.send`, mas a chave pertence ao chat; ela não
foi promovida silenciosamente a chave de curadoria. As mensagens de erro
devolvidas pelo servidor também não legitimam os sete textos fixos acima.

## Resultado

Relato de problema: conforme. Usuário: conforme após as duas substituições
literais. Upload de curadoria: auditado, mas não pode ser declarado conforme
até a pesquisadora decidir se cria chaves próprias, aponta uma fonte desktop
ou remove o diálogo/texto.
