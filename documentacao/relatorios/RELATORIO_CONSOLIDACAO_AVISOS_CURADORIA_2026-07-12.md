# Consolidação dos avisos de curadoria

## Alterações

- Mensagem ajustada para: "Uma versão original não pode manter referência à origem."
- Vínculos inválidos exibem uma janela explicativa antes do bloqueio do salvamento.
- Divergências conceituais entre traduções exibem uma janela com as diferenças detectadas.
- O pesquisador pode voltar para corrigir, cancelar a alteração, aplicar os metadados da versão atual às versões vinculadas ou usar a versão original como referência.
- A harmonização copia apenas metadados conceituais. Enunciado, idioma, fonte e demais dados linguísticos são preservados.
- Erros e decisões de curadoria são registrados separadamente em `erros_curadoria.tsv`, no diretório de curadoria do usuário.
- O log de interação do aprendiz não recebe eventos de detecção de erros de curadoria.

## Verificações

- `ant clean jar`: aprovado.
- `scripts/verificar_regressao_gerard.py`: aprovado.
- 210 versões linguísticas e 72 grupos conceituais permaneceram coerentes.
