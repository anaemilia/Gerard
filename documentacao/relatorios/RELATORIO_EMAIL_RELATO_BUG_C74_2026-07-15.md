# Relatório de alteração — preparação de e-mail para relato de bug

**Data:** 2026-07-15  
**Ciclo:** C74  
**Tipo de intervenção:** refinamento funcional e operacional.

## Alteração

O botão **Reportar bug** mantém o registro local e passa a abrir o cliente de e-mail padrão do usuário com uma mensagem previamente preenchida para `ana.queiroz@univasf.edu.br`.

## Fluxo

1. o usuário descreve o que estava fazendo;
2. o Gérard salva uma cópia local em `Gerard/relatos_bug/relatos_bug.tsv`;
3. o cliente de e-mail padrão é aberto;
4. destinatário, assunto, descrição e contexto são preenchidos;
5. o usuário revisa a mensagem e realiza o envio.

## Contexto incluído

- data e hora;
- versão do Gérard;
- identificador da situação-problema;
- categoria;
- idioma da interface;
- idioma da situação;
- enunciado exibido.

## Contingência

Quando não houver cliente de e-mail compatível, o relato permanece salvo localmente e a interface informa o endereço para envio manual.
