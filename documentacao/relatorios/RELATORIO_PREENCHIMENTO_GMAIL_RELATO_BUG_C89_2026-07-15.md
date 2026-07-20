# Relatório — preenchimento do Gmail no relato de bug

**Ciclo:** C89  
**Data:** 2026-07-15

## Problema observado

Ao acionar **Reportar bug**, o Windows encaminhava o `mailto:` ao Gmail, mas abria apenas a interface do serviço, sem preencher destinatário, assunto e corpo. O comportamento dependia da associação do protocolo `mailto:` no sistema operacional e não garantia que os parâmetros fossem repassados ao Gmail Web.

## Alteração aplicada

A abertura principal passou a usar diretamente o compositor do Gmail Web:

`https://mail.google.com/mail/?view=cm&fs=1&tf=1&to=...&su=...&body=...`

Os campos são codificados em UTF-8 e incluem:

- destinatário `anaemilia@gmail.com`;
- assunto do relato;
- descrição digitada pelo usuário;
- situação-problema, categoria e idiomas;
- representações exibidas;
- enunciado atual;
- versão do Gérard.

O `mailto:` foi mantido apenas como contingência para ambientes nos quais não seja possível abrir o navegador. A cópia local em `relatos_bug.tsv` permanece inalterada.

## Compatibilidade

Os métodos antigos de criação e abertura por `mailto:` foram preservados para evitar quebra de integrações existentes. A versão registrada no corpo do relato foi atualizada para **C89**.

## Verificação prevista

- compilação pelo Ant;
- teste automatizado da URL do Gmail, destinatário, assunto e corpo;
- teste do `mailto:` de contingência;
- regressão geral do projeto.

A abertura real da conta Gmail depende do navegador e da sessão autenticada no computador do usuário.
