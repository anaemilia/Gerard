# Relatório de alteração — painéis iniciais preservados sem conteúdo educativo

**Data:** 2026-07-16  
**Ciclo:** C94

## Correção
A inicialização sem categoria foi ajustada para preservar a estrutura visual da tela. Permanecem visíveis:

- o painel superior destinado ao enunciado;
- o painel inferior esquerdo destinado ao diagrama de Vergnaud;
- o painel inferior direito destinado à representação complementar;
- a separação visual entre as áreas.

Até que o usuário selecione uma categoria, esses painéis permanecem vazios. Não são mostrados enunciados, títulos educativos, formas, conectores, valores, interrogações contextuais ou diagramas.

Após a seleção da categoria, o sistema carrega o enunciado e as representações correspondentes, preservando o comportamento implementado no ciclo C93.

## Verificações
- compilação Ant/NetBeans;
- teste automatizado da inicialização sem categoria;
- verificação de presença visual dos três painéis;
- confirmação de ausência dos conteúdos educativos antes da seleção;
- confirmação de carregamento após a escolha de categoria.
