# Simplificação do escopo linguístico

A arquitetura geral de internacionalização foi preservada, mas o código residual de sistemas de escrita removidos foi reduzido.

## Alterações
- catálogo fechado em pt-BR, en, fr e es;
- descarte de entradas antigas fora do escopo;
- orientação fixada em LTR;
- sistema de escrita fixado em Latn;
- símbolo desconhecido canônico mantido como `?`;
- remoção de documentos arquiteturais obsoletos sobre RTL e símbolos alternativos;
- remoção da classe interna sem uso `IdiomaCatalogo`;
- manutenção das APIs antigas de direção/script apenas para compatibilidade.

## Invariantes preservados
- troca de idioma preserva a modelagem;
- seleção de categoria, sorteio e restauração iniciam nova modelagem;
- diagramas começam vazios;
- o item desconhecido recebe o estilo do papel semântico;
- Strategy, State simplificado, Facade e Factory permanecem ativos.
