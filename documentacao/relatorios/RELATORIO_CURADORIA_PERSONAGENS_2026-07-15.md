# Relatório de alteração — participantes e objetos na curadoria

**Data:** 2026-07-15  
**Ciclo:** C39  
**Tipo de intervenção:** consistência teórico-metodológica e ampliação dos metadados linguísticos por solicitação explícita do pesquisador.

## Problema observado
A curadoria registrava os valores e os papéis semânticos da situação-problema, mas não possuía campos explícitos para indicar os nomes das pessoas, grupos, animais ou objetos envolvidos no enunciado. Sem esse registro, módulos posteriores poderiam depender de extração automática do texto, inclusive na construção dos blocos semanticamente próximos.

## Alteração implementada
Foram acrescentados ao formulário detalhado os campos:

- `personagem_1`;
- `personagem_2`;
- `personagem_3`.

Os campos são livres e podem receber nomes próprios ou denominações de objetos e coleções, por exemplo: `Carlos`, `José`, `Bolinhas` ou `Carrinhos`. Quando houver mais de três elementos relevantes, o pesquisador pode agrupar elementos relacionados no mesmo campo, separados por vírgula.

## Persistência
Os três campos foram incorporados:

- ao modelo `SituacaoProblemaAditiva`;
- à linha interna da tela de curadoria;
- ao formato TSV da curadoria;
- à leitura retrocompatível de arquivos anteriores;
- à criação, cópia e restauração de registros na interface.

Arquivos antigos, sem essas três colunas, continuam sendo carregados com os campos vazios. Na próxima gravação, o arquivo passa a usar o novo formato.

## Decisão linguística
Os participantes e objetos são vinculados à versão linguística da situação-problema. Eles não são forçados a permanecer idênticos entre português, inglês e francês, permitindo que denominações de objetos sejam adequadas a cada tradução.

## Verificações
- compilação Ant/NetBeans: aprovada;
- inicialização da aplicação em ambiente gráfico virtual: aprovada, sem exceção imediata;
- serialização com 28 colunas e preservação dos três campos: aprovada;
- compatibilidade de leitura com linhas antigas de 25 colunas: preservada;
- campos técnicos de `_id` continuam ocultos na interface.
