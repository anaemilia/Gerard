# CHANGELOG C154

## Objetivo
Habilitar a opção **Comparação entre categorias** no menu de categorias e conectá-la à tela comparativa já existente.

## Alterações

- A opção deixou de usar o estado visual e funcional “Em construção”.
- O item agora é habilitado, usa cursor de ação e abre a tela `PainelComparacaoCategorias` em diálogo próprio.
- As opções **Transformações compostas** e **Relações** permanecem inalteradas.
- A regra de habilitação e acionamento foi colocada fora da `Main.java`, em:
  - `src/gerard/ui/menu/ConfiguradorOpcaoComparacaoCategorias.java`
- A `Main.java` permanece responsável apenas pela composição do menu e pelo fornecimento da operação de abertura já existente.

## Contrato de regressão

1. “Comparação entre categorias” deve aparecer habilitada.
2. O item não deve exibir o tooltip “Em construção”.
3. Um clique deve abrir a tela comparativa.
4. A criação/recriação dos menus não deve alterar as categorias selecionadas nem limpar a modelagem atual.
