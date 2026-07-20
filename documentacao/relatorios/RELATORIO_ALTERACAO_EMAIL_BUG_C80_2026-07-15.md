# Relatório de alteração — destinatário dos relatos de bug

**Data:** 2026-07-15  
**Ciclo:** C80  
**Tipo de intervenção:** configuração operacional localizada.

## Alteração implementada
O destinatário utilizado pelo botão **Reportar bug** foi alterado de `ana.queiroz@univasf.edu.br` para `anaemilia@gmail.com`.

## Comportamento esperado
Ao concluir o formulário de relato, o Gérard abre o cliente de e-mail padrão com:

- destinatário: `anaemilia@gmail.com`;
- assunto e corpo preenchidos automaticamente;
- contexto da atividade e representações exibidas;
- cópia local preservada em `Gerard/relatos_bug/relatos_bug.tsv`.

O envio final continua sob controle do usuário.

## Verificações
- compilação Ant/NetBeans;
- teste automatizado do URI `mailto`;
- regressão geral do projeto.
