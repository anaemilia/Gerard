# Relatório C88 — bloqueio dinâmico da curadoria por idioma

Data: 15 de julho de 2026.

## Problema observado

Ao abrir a versão original e selecionar outro idioma no editor de tradução, os campos de valores e os demais dados semânticos continuavam editáveis. O bloqueio da C86 dependia apenas do tipo da linha aberta (`traducao`) e não reagia à troca de idioma feita no seletor superior.

## Correção

A tela passou a atualizar dinamicamente o modo de edição quando o idioma do editor de tradução é alterado.

Quando o idioma selecionado é diferente do idioma da versão aberta:

- somente o texto da tradução, sua validação e o seletor de idioma permanecem ativos;
- o enunciado e a validação da versão original ficam protegidos;
- `idioma da versão`, fonte, personagens, valores, sinais, termo desconhecido, representação visual e observações ficam somente para leitura;
- os campos herdados recebem aparência visual de bloqueio e tooltip explicativo;
- ao retornar ao idioma da versão original, os campos da original voltam ao modo editável.

As versões cujo `tipo_versao` já é `traducao` continuam com a semântica permanentemente herdada da original.

## Verificações

- compilação Ant/NetBeans concluída;
- teste estrutural `testar_bloqueio_dinamico_idioma_curadoria.sh` aprovado;
- teste automatizado da interface Swing, com troca de idioma, bloqueio e reabertura dos campos, aprovado por `testar_bloqueio_dinamico_idioma_curadoria_ui.sh`;
- testes da curadoria semântica somente na original, remoção de subtipo e fechamento sem travamento aprovados;
- regressão geral aprovada, incluindo vínculos das 210 versões e dos 72 grupos conceituais.

A janela foi exercitada por teste automatizado Swing; não foi realizada inspeção visual manual completa.
