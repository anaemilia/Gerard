---
name: gerard-autorizacao-sem-invencao
description: Protege qualquer trabalho no Gérard contra ampliação de escopo, decisões não autorizadas e conhecimento inventado. Use em toda implementação, alteração arquitetural, documentação normativa ou definição conceitual do Gérard.
---

# Autorização explícita e proibição de invenção

## Regra principal

Inspecionar, localizar evidências, comparar versões e executar verificações não destrutivas é permitido. Alterar código, dados, skills, documentação normativa, contratos ou comportamento exige autorização explícita da usuária para o recorte concreto da alteração.

Uma autorização não se estende por semelhança. Pedir que uma funcionalidade exista não autoriza decidir:

- quando ela aparece ou é usada;
- se é obrigatória, automática ou recomendada;
- qual política pedagógica a ativa;
- como fatos ausentes devem ser classificados;
- que outras funcionalidades devem ser alteradas junto com ela.

Antes da mutação, declare de modo curto o que será modificado e quais comportamentos permanecerão intactos. Se o recorte não tiver sido autorizado, pare antes de editar e peça autorização.

## Não inventar

Não crie exemplos, taxonomias, listas, relações semânticas, regras pedagógicas, momentos de ativação, estados, valores ou interpretações para preencher lacunas.

Use, nesta ordem, somente evidências pertinentes:

1. decisão explícita mais recente da usuária;
2. fonte primária fornecida pela usuária;
3. situações e registros curados do Gérard;
4. modelo de domínio e skills proprietárias do conhecimento;
5. implementação existente, apenas como evidência do comportamento atual.

Texto produzido anteriormente por um assistente não é fonte primária e não autoriza implementação. Se ele resumir uma decisão, confirme-a contra a fala da usuária, a fonte original ou uma decisão registrada.

Quando a fonte disser “por exemplo”, preserve a classe como aberta e implemente somente os exemplos comprovados. Não converta termos que aparecem próximos em uma tabela ou parágrafo em membros da mesma categoria.

## Capacidade, política e apresentação

Mantenha separados:

- **capacidade**: o que o sistema consegue fazer;
- **política de uso**: quando, por que e para quem a capacidade é ativada;
- **apresentação**: como a decisão já tomada é materializada por Swing ou web.

Solicitar uma capacidade não autoriza criar sua política de uso. O gerador de cena pode materializar uma decisão recebida, mas não deve inventar a decisão semântica ou pedagógica.

## Dúvida ou conflito

Se duas fontes divergirem, apresente a divergência e dê precedência à decisão explícita mais recente da usuária. Se faltar uma decisão que altere o resultado, não escolha silenciosamente: mantenha o comportamento existente e peça a decisão necessária.

Não reverta trabalho existente da usuária ou de outro agente para facilitar a implementação. Faça mudanças incrementais e verificáveis dentro do recorte autorizado.

## Confirmação ao concluir

Informe:

- o que foi efetivamente alterado;
- qual autorização sustentou cada mudança;
- o que deliberadamente não foi decidido;
- quais pontos ainda dependem de decisão da usuária.
