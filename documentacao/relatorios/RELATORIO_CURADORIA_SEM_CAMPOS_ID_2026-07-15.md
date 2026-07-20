# Relatório de alteração — ocultação de identificadores técnicos na curadoria

**Data:** 2026-07-15  
**Ciclo:** C38  
**Tipo de intervenção:** refinamento de interface e redução de carga cognitiva por solicitação explícita do pesquisador.

## Problema observado
A tela detalhada de curadoria apresentava os campos `id da versão`, `situação_grupo_id` e `versão_origem_id`. Esses valores são necessários para a persistência e para o vínculo interno entre versões linguísticas, mas não constituem informação relevante para o usuário da curadoria. Sua exibição dificultava a leitura e introduzia termos técnicos sem função pedagógica direta.

## Alteração implementada
Os três campos foram retirados do formulário visível:

- `id da versão`;
- `situação_grupo_id`;
- `versão_origem_id`.

Os identificadores não foram excluídos do modelo, do repositório ou dos arquivos de dados. Permanecem administrados automaticamente pelo sistema para preservar:

- o agrupamento conceitual das versões linguísticas;
- o vínculo entre original e tradução;
- a atualização e o salvamento da curadoria;
- a compatibilidade com registros já existentes.

## Princípio de interface
A interface passa a apresentar somente informações que o usuário precisa compreender, revisar ou decidir. Metadados técnicos de infraestrutura permanecem encapsulados na camada interna.

## Verificações
- teste específico de ausência dos campos de ID no formulário: aprovado;
- preservação dos campos internos de vínculo: aprovada;
- compilação Ant/NetBeans: aprovada;
- teste de vínculos entre traduções: aprovado.
