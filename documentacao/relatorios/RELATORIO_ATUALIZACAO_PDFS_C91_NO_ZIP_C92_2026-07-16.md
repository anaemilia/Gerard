# Atualização dos PDFs até o corpus C91

**Data:** 2026-07-16  
**Ciclo documental:** C92  
**Escopo:** atualização do artigo, da análise de complexidade e do pacote NetBeans.

## Artigo de co-design

O artigo foi atualizado de C69 para C91, preservando sua estrutura de resumo, fundamentação, metodologia, resultados, discussão, ameaças à validade e conclusões.

Principais atualizações:

- corpus de 91 ciclos consistentes;
- classificação em 31 ciclos estruturais e de generalização, 22 de consistência teórica e metodológica e 38 de refinamento visual e operacional;
- linha do tempo gerada a partir de 91 registros no CSV;
- atualização dos resultados e da discussão com os ciclos C70 a C91;
- curadoria semântica somente na versão original;
- remoção do campo redundante `subtipo`;
- bloqueio dinâmico da semântica ao selecionar outra língua;
- estabilização do fechamento e da persistência da curadoria;
- relato de falhas com contexto e integração com o Gmail Web;
- feedback multissensorial para posicionamento semântico incorreto;
- ajuda contextual nas áreas do texto, de Vergnaud e do diagrama complementar;
- regressão documental aprovada para oito decisões metodológicas ativas.

## Análise de complexidade

A análise foi ampliada de C68 para C91. Foram adicionados os impactos de:

- distribuição da composição de coleções;
- atualização dinâmica de personagens;
- controle de menus em construção;
- serialização do relato de bug;
- fotografias e indicador vetorial de submenu;
- persistência e curadoria multilíngue;
- codificação da URL do Gmail;
- feedback multissensorial;
- ajuda contextual por área.

A análise preserva os limites assintóticos principais: `O(q)` para coleções simples e `O(q log q)` para comparação com pareamento cromático. Os novos recursos acrescentam custos constantes por evento ou lineares no tamanho das mensagens.

## Verificações

- compilação do artigo em LuaLaTeX: aprovada;
- regressão documental do artigo: aprovada;
- compilação da análise de complexidade em LuaLaTeX: aprovada;
- renderização e inspeção visual dos dois PDFs: aprovadas;
- compilação Ant/NetBeans do projeto: aprovada;
- regressão geral do Gérard: aprovada;
- testes de feedback multissensorial e ajuda contextual: aprovados.
