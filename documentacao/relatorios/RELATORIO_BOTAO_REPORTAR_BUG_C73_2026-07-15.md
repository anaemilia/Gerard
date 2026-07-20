# Relatório de alteração — botão discreto para relatar problemas

**Data:** 2026-07-15  
**Ciclo:** C73  
**Tipo de intervenção:** refinamento visual e operacional.

## Alteração
Foi acrescentado ao menu principal um botão discreto denominado **Reportar bug**. O botão abre uma caixa de diálogo com um campo de texto multilinha para que o usuário descreva o que estava fazendo quando o problema ocorreu.

## Registro do relato
Ao confirmar o formulário, o Gérard registra localmente:

- data e hora;
- descrição fornecida pelo usuário;
- identificador da situação-problema atual;
- categoria selecionada;
- idioma da interface;
- idioma da situação;
- enunciado da atividade.

Os relatos são gravados em `Gerard/relatos_bug/relatos_bug.tsv`, dentro da pasta pessoal do usuário. O recurso não realiza envio pela internet.

## Preservação
A alteração não modifica a curadoria, o modelo semântico compartilhado, os diagramas, os logs de interação do aprendiz nem os documentos acadêmicos.
