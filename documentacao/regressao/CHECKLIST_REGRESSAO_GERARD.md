# Checklist de regressão do Gerard

Este checklist deve ser executado antes de entregar qualquer nova versão do projeto. O objetivo é evitar que uma correção localizada danifique funcionalidades já implementadas.

## 1. Compilação

- Executar `ant clean jar`.
- Confirmar `BUILD SUCCESSFUL`.
- Confirmar a geração de `dist/GerardNetBeans_D3_Leitura_Redes_Transicoes.jar`.

## 2. Internacionalização

- Conferir se as chaves de `ServicoLocalizacao` existem em português, inglês e francês.
- Conferir se nenhum texto visível aparece como nome de componente ou chave interna, por exemplo `ui.button.restoreElements`.
- Conferir textos críticos:
  - botão de idioma;
  - botão de tipo de situação;
  - botões de restauração;
  - menus e submenus;
  - enunciado da situação-problema;
  - textos do eixo x;
  - textos do Diagrama de Venn;
  - dicas e tooltips.

## 3. Preservação do estado da tela

Ao trocar o idioma, a tela deve preservar:

- enunciado correspondente à mesma situação, quando houver tradução por contexto e valores;
- valores já posicionados no diagrama de Vergnaud;
- valores já posicionados no eixo dos inteiros;
- quadradinhos do Diagrama de Venn;
- itens arrastáveis já usados;
- estado da modelagem feita pelo estudante.

A troca de idioma deve atualizar somente os textos visíveis internacionalizáveis, sem sortear nova situação-problema e sem limpar a tela.

## 4. Sincronização entre representações

Verificar se permanecem sincronizados:

- diagrama de Vergnaud;
- eixo dos inteiros;
- Diagrama de Venn;
- número relativo escolhido no menu de sinal;
- alterações feitas por arraste;
- alterações feitas por duplo clique/edição textual.

Mudanças em uma representação não devem deixar as outras com valores obsoletos.

## 5. Eixo dos inteiros

Conferir:

- ponto azul arrastável;
- tooltip do ponto azul;
- textos do eixo em português, inglês e francês;
- atualização do valor no eixo após edição no diagrama;
- atualização do diagrama/Venn após mudança no eixo.

## 6. Botões de restauração

Conferir:

- `Restaurar itens` deve restaurar itens arrastáveis sem destruir a situação atual.
- `Restaurar diagrama` deve limpar a modelagem do diagrama quando essa ação for intencional.
- Os textos desses botões devem ser traduzidos em português, inglês e francês.

## 7. Logs e visão do pesquisador

Conferir:

- separação entre agente `S` e agente `C`;
- preservação do campo C/E para ações do sujeito;
- geração dos logs;
- leitura dos logs pela visão do pesquisador;
- visualizações D3;
- matriz de efetividade do feedback/scaffolding;
- redes de transições.

Nenhuma rotina deve apagar logs antigos automaticamente sem propriedade explícita e backup.

## 8. Teste automatizado incluído

Executar:

```bash
scripts/verificar_regressao_gerard.sh
```

Esse script verifica compilação, nome do JAR, paridade de chaves de internacionalização, chaves críticas, referências estáticas às rotinas de sincronização, proteção dos logs, D3 e arquivo de situações-problema.

## 9. Teste manual mínimo

Após o teste automatizado, abrir a aplicação e executar pelo menos este fluxo:

1. Sortear uma situação-problema.
2. Arrastar valores para o diagrama de Vergnaud.
3. Posicionar valor no eixo dos inteiros.
4. Conferir atualização do Diagrama de Venn.
5. Trocar idioma para inglês.
6. Conferir se o estado foi preservado e os textos foram traduzidos.
7. Trocar idioma para francês.
8. Conferir novamente preservação do estado e tradução.
9. Usar os dois botões de restauração.
10. Abrir a visão do pesquisador e conferir se as visualizações D3 continuam carregando.

## Curadoria humana das situações-problema

- Confirmar que a aba Curadoria abre ao lado da aba Gerard.
- Confirmar que a tabela permite editar categoria de Vergnaud e metadados.
- Confirmar que o botão “Salvar e aplicar metadados curados” grava `~/Gerard/curadoria/situacoes_vergnaud_curadas.tsv`.
- Confirmar que, após salvar, o Gerard usa a categoria curada como fonte autoritativa.
- Confirmar que o repositório não corrige automaticamente a categoria validada em tempo de execução.
- Confirmar que a auditoria automática é apenas indicativa e não altera os metadados curados.

## Curadoria detalhada por situação-problema

- Confirmar que clicar no texto/enunciado de uma linha da aba Curadoria abre uma janela em frente com a curadoria específica daquela situação-problema.
- Confirmar que o enunciado aparece no topo da janela, com quebra automática de linha, sem barra de rolagem horizontal.
- Confirmar que os campos aparecem verticalmente: `categoria_vergnaud`, `subcategoria`, `representacao_visual`, `quantidade_1`, `quantidade_2`, `resultado`, `termo_desconhecido`, `estado_inicial`, `transformacao`, `estado_final`, `validada` e `idioma`.
- Confirmar que “Aplicar na tabela” atualiza a linha sem fechar ou alterar outras situações.
- Confirmar que “Aplicar e salvar curadoria” atualiza a linha, grava os metadados curados e recarrega o Gerard preservando a lógica de fonte da verdade pela curadoria humana.
- Confirmar que a tela principal continua exibindo apenas situações curadas.

## Curadoria de comparação de medidas

- Confirmar que a tela específica de curadoria possui campos próprios para `referido`, `referendo` e `valor_relativo`.
- Confirmar que `termo_desconhecido` é selecionado por combo box, não digitado livremente, com as opções: `transformação`, `estado_inicial`, `estado_final`, `referido`, `referendo`, `valor_relativo`, `parte_1`, `parte_2` e `todo`.
- Confirmar que, em comparação de medidas, o Gerard usa `referendo`, `valor_relativo` e `referido` curados antes de qualquer campo genérico.
- Confirmar que o arquivo de curadoria preserva esses campos no TSV e mantém compatibilidade com arquivos curados antigos.

## 10. Qualidade da interface e coerência entre os quatro pilares

Estes critérios devem ser avaliados em toda nova versão. Eles não são melhorias opcionais; são condições permanentes de qualidade da interface.

### 10.1 Boa legibilidade

- O enunciado deve ser o elemento textual principal e permanecer legível sem aproximação da tela.
- Categoria, status e informações auxiliares devem ter hierarquia visual secundária.
- Não deve haver sobreposição entre categoria, enunciado, botões contextuais, diagramas ou mensagens.
- Textos longos devem respeitar a área disponível e usar quebra de linha quando necessária.

### 10.2 Contraste adequado

- Texto principal e texto secundário devem manter contraste suficiente sobre o fundo.
- Números, interrogação, categoria e estados de foco não podem depender exclusivamente da cor para transmitir significado.
- Elementos desabilitados devem continuar identificáveis.
- A regressão automatizada deve conferir as combinações de cores centrais declaradas em `Main.java`.

### 10.3 Tamanho de fonte suficiente

- O enunciado deve usar fonte maior que rótulos auxiliares.
- Categoria e status podem usar fonte menor, mas não devem ficar ilegíveis.
- Nenhuma alteração deve reduzir silenciosamente as fontes principais.

### 10.4 Rótulos claros

- O controle de seleção deve ser identificado como `Categoria`, e não como termo genérico ou ambíguo.
- Os nomes dos comandos devem corresponder ao efeito real da ação.
- O rótulo da categoria deve aparecer acima do enunciado e fora da área de elementos móveis.

### 10.5 Organização visual

- Categoria, enunciado, comandos, representações e feedback devem ocupar áreas visualmente distintas.
- Controles contextuais devem permanecer próximos da área que modificam.
- Informações auxiliares não devem competir visualmente com o enunciado.

### 10.6 Navegação previsível

- Selecionar outra categoria, sortear outra situação ou restaurar explicitamente a atividade inicia nova modelagem.
- Trocar idioma, abrir/fechar a curadoria ou alterar o estilo de interação preserva a modelagem atual.
- Ações que preservam estado não podem chamar o fluxo reservado à nova atividade.

### 10.7 Tooltips objetivos

- Cada tooltip deve informar o efeito imediato da ação.
- O tooltip da categoria deve informar que a seleção inicia uma nova situação curada.
- Tooltips não devem usar nomes técnicos internos nem atribuir papel numérico a texto comum.

### 10.8 Preservação do estado da atividade

- O estado construído pelo usuário deve permanecer separado da camada textual, do idioma e do estilo de interação.
- Somente nova categoria, novo sorteio e restauração explícita podem limpar a modelagem.
- Nenhum valor, sinal ou interrogação deve ser introduzido automaticamente em um diagrama vazio.

### 10.9 Consistência entre idiomas e representações

- Original e traduções devem usar as mesmas regras de exibição e associação semântica.
- Valores conhecidos devem permanecer em algarismos nas traduções.
- O item desconhecido deve ser representado por `?` no enunciado e só entrar no diagrama por ação do usuário.
- Texto, diagrama de Vergnaud, Venn, eixo e logs devem preservar os mesmos papéis semânticos.

### 10.10 Fluxo manual obrigatório de interface

1. Selecionar uma categoria e confirmar que uma nova situação curada é carregada com diagramas vazios.
2. Posicionar elementos no diagrama.
3. Trocar o idioma e confirmar preservação integral da modelagem.
4. Alterar o estilo de interação e confirmar preservação integral da modelagem.
5. Abrir e fechar a curadoria e confirmar preservação integral da modelagem.
6. Sortear outra situação e confirmar limpeza da modelagem anterior.
7. Restaurar explicitamente a atividade e confirmar diagramas vazios.
8. Conferir categoria acima do enunciado, hierarquia de fontes, contraste e tooltips.


## Padrões de projeto e responsabilidades consolidadas

- [ ] Strategy: cada estilo de interação mantém o comportamento anterior em todas as representações.
- [ ] State simplificado: Sortear, selecionar Categoria e Restaurar reiniciam a modelagem.
- [ ] State simplificado: trocar idioma, estilo, texto, curadoria ou representação preserva a modelagem.
- [ ] Facade: o carregamento de nova situação e de tradução correspondente usa a mesma sequência centralizada.
- [ ] Factory: a representação criada continua compatível com a categoria curada.
- [ ] Nenhum padrão introduz preenchimento automático de números, sinais ou interrogação nos diagramas.
- [ ] A refatoração não altera logs, internacionalização, curadoria, mobilidade textual ou vínculos entre traduções.
