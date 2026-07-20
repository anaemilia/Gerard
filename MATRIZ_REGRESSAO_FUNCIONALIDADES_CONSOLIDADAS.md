# Matriz de regressão das funcionalidades consolidadas do Gérard

Esta matriz define contratos que não podem ser removidos ou alterados incidentalmente por novas funcionalidades. Compilação bem-sucedida não substitui a verificação dos fluxos completos.

## Arraste dos elementos matemáticos imersos no texto

- O texto original permanece no enunciado durante e depois do arraste.
- Números naturais, valores assinados, decimais e interrogação usam o mesmo fluxo de proxy semântico.
- O proxy percorre toda a tela com física de mola e não fica restrito à área do texto.
- Soltura correta incorpora o item ao papel correspondente no diagrama.
- Soltura fora do diagrama descarta apenas o proxy, sem modificar o texto.
- Soltura em papel semanticamente incorreto produz feedback, mas o item incorreto permanece no diagrama.
- Após o feedback, o item incorreto continua selecionável e manipulável para outra posição.

## Feedback de posicionamento incorreto

- Deve existir tip contextual de pergunta ou informação.
- Deve existir som sutil.
- Deve existir tremor leve.
- O tremor restaura a posição base do item ao terminar.
- O feedback não altera os dados curados.
- O feedback não sincroniza uma associação semanticamente incorreta com as demais representações.
- O feedback não remove o item incorreto do diagrama.

## Primeiro posicionamento e representações

- Com Vergnaud semanticamente vazio, operações semânticas das representações ficam bloqueadas.
- O painel flutuante do eixo continua movimentável e ocultável, pois isso é interação de componente de tela.
- A navegação do ponto azul e a mudança do valor no eixo só ficam disponíveis após o primeiro posicionamento semântico.
- Controles `+` e `−` ficam bloqueados enquanto Vergnaud estiver semanticamente vazio.

## Quantidades e relações

- Quantidades cardinais nunca podem ser negativas.
- Somente transformação e valor relativo podem receber sinal.
- `+` acrescenta unidades até o limite curado.
- `−` retira unidades até zero.
- Alterações que quebram a relação aditiva devem ser bloqueadas.

## Sincronização

- Texto, Vergnaud, barras, coleções e eixo compartilham o mesmo estado semântico.
- Dados curados servem como referência e não são modificados pelas ações do usuário.
- Preenchimento por arraste e digitação recalcula os controles e representações. O item desconhecido permanece como `?` até ser preenchido pelo protocolo de mouse/texto.


## Conclusão da modelagem

- A conclusão só ocorre quando todos os elementos matemáticos do texto estão em papéis semanticamente corretos.
- Deve existir exatamente um item originalmente desconhecido, representado por `?`.
- A interrogação precisa ser posicionada primeiro no papel semântico correto.
- Em seguida, o próprio item `?` deve ser substituído por um número por meio do protocolo de mouse/texto.
- Um valor calculado ou sincronizado automaticamente não pode substituir a interrogação antes desse protocolo.
- Ao concluir, elementos, conectores e itens do diagrama de Vergnaud recebem destaque azul Gérard.
- A conclusão é apresentada em duas etapas: primeiro um selo discreto **“Modelagem concluída”** e, depois, o tip com a pergunta de continuidade.
- O tip exibe a pergunta de continuidade e os radio buttons Sim e Não.
- Sim inicia uma nova situação-problema pelo fluxo consolidado de Sortear.
- Não mantém a situação atual e o destaque azul, sem apagar a modelagem.
- O selo e o tip aparecem uma vez por conclusão e voltam a ficar disponíveis apenas após a modelagem deixar de estar concluída e ser concluída novamente.
- O tip não aparece simultaneamente ao selo; há uma transição curta e não modal entre os dois.
- Iniciar uma nova manipulação cancela a sequência pendente, remove temporariamente o destaque de conclusão e não retira itens nem altera dados curados.

## Aba Construir situação-problema

- Bloco correto na posição esperada aparece em azul Gérard.
- Bloco incorreto ou correto fora de ordem aparece em vermelho suave.
- A verificação é atualizada após adicionar, retirar, subir, descer, recomeçar e arrastar.
- A lista de blocos disponíveis permanece neutra.

## Critérios de entrega

- Comparar o diff com a última versão estável.
- Executar testes específicos da nova funcionalidade.
- Executar testes de contrato das funcionalidades acima.
- Executar a regressão estrutural completa.
- Comparar por hash os dados curados.
- Informar separadamente o que foi testado automaticamente e o que depende de inspeção visual.

## C135 — Continuidade pela mesma categoria

| Contrato | Resultado esperado | Verificação |
|---|---|---|
| Radio button **Sim** do tip de conclusão | Deve executar `botaoSortear.doClick()`; não deve abrir fluxo paralelo de nova atividade | Verificador estrutural e inspeção do listener |
| Categoria após escolher **Sim** | Deve permanecer em `tipoSituacaoSelecionada`; apenas outra situação da mesma categoria é carregada | Reutilização obrigatória do fluxo consolidado do botão Sortear |
| Reinicialização da modelagem | Deve ocorrer uma única vez dentro do fluxo consolidado de `aplicarIdiomaSelecionado()` | Ausência de chamada antecipada a `reiniciarConclusaoModelagem()` no listener do tip |
| Radio button **Não** | Deve manter situação atual e diagrama destacado | Testes de conclusão existentes |

## C135 — Continuidade pela mesma categoria

| Contrato | Resultado esperado | Verificação |
|---|---|---|
| Radio button **Sim** do tip de conclusão | Deve executar `botaoSortear.doClick()`; não deve iniciar uma atividade por um fluxo paralelo | Verificador estrutural e inspeção do listener |
| Categoria após escolher **Sim** | Deve permanecer em `tipoSituacaoSelecionada`; o botão Sortear carrega outra situação da mesma categoria | Reutilização do fluxo consolidado do botão Sortear |
| Reinicialização da modelagem | Deve ocorrer dentro de `aplicarIdiomaSelecionado()`, sem chamada antecipada no listener do tip | Inspeção estrutural do listener |
| Radio button **Não** | Deve manter a situação atual e o diagrama destacado | Testes de conclusão existentes |

## C136 — Incógnita e gestos estruturais

| Contrato | Resultado esperado | Verificação |
|---|---|---|
| Pickup da interrogação | A interrogação deve criar o mesmo proxy semântico dos números e atravessar a tela até qualquer papel permitido | Teste direto do marcador e fallback pelo vínculo semântico do texto |
| Unicidade do elemento textual | Somente a mesma ocorrência textual já posicionada pode bloquear novo arraste; outro número ou outro papel não pode bloquear a incógnita | Identidade por `tokenSemanticoId`, sem comparação exclusiva por valor ou papel |
| Interrogação após outros posicionamentos | A presença de estado inicial e estado final no diagrama não pode impedir o arraste da incógnita para transformação | Teste com número `6` já posicionado e marcador `?` ainda disponível |
| Duplo clique em figura estrutural | O segundo pressionamento deve ser reservado à edição e não pode iniciar reposicionamento | `PoliticaGestoEstrutural` e guarda antes da seleção do elemento/conector |
| Oscilação do mouse durante clique | Deslocamentos inferiores ao limiar não movem círculo, retângulo ou conector | `ControladorLimiarArrasteEstrutural` com limiar de 6 pixels |
| Arraste estrutural intencional | O reposicionamento continua disponível após ultrapassar o limiar | Teste unitário do limiar e integração no movimento estrutural |

## C137 — contrato de conclusão exclusivamente numérica

> Histórico: a exigência numérica permanece, mas o modo de preenchimento da incógnita foi especializado pela C158.

- O destaque azul e o tip de conclusão só aparecem quando **todos os elementos semânticos do diagrama** possuem valores numéricos.
- Uma interrogação, mesmo corretamente posicionada, mantém a modelagem incompleta até ser substituída por um número.
- São aceitos números naturais, inteiros com sinal e decimais com ponto ou vírgula.
- Para valores conhecidos, permanecem válidos arraste e digitação direta. Para a incógnita, vale obrigatoriamente o ciclo `posicionar ? → preencher pelo protocolo mouse/texto`.
- Um item arrastado em papel semanticamente incorreto nunca ativa o estado azul.
- A verificação deve ser reexecutada após edição, escolha do sinal, sincronização reativa, soltura e exclusão.


## C138 — regra de conclusão numérica em todos os diagramas

- A mesma regra de conclusão exclusivamente numérica deve valer para **Composição**, **Comparação** e **Transformação**.
- Em **Composição**, o destaque azul e o tip só aparecem quando `parte 1`, `parte 2` e `todo` estiverem preenchidos com números.
- Em **Comparação**, o destaque azul e o tip só aparecem quando `referido`, `valor relativo/relação` e `referendo` estiverem preenchidos com números.
- Em **Transformação**, o destaque azul e o tip só aparecem quando `estado inicial`, `transformação` e `estado final` estiverem preenchidos com números.
- A regra vale tanto para preenchimento por mouse quanto por teclado.
- A presença de `?` em qualquer um desses papéis mantém a modelagem incompleta.
- O posicionamento semanticamente incorreto continua impedindo a conclusão, mesmo que todos os campos tenham valores numéricos.

## C139 — testes visuais guiados por categoria

A execução manual deve seguir o arquivo `ROTEIRO_TESTE_VISUAL_CONCLUSAO_POR_CATEGORIA.md`.

| Identificador | Categoria | Contrato visual principal |
|---|---|---|
| C139-VIS-COMP-01 | Composição | O estado azul aparece somente com parte 1, parte 2 e todo numéricos e corretos por arraste. |
| C139-VIS-COMP-02 | Composição | Os valores conhecidos podem ser digitados diretamente; a incógnita exige posicionar `?` e preenchê-lo pelo protocolo mouse/texto. |
| C139-VIS-COMP-03 | Composição | Erro produz tip, som e tremor; o item permanece manipulável e pode ser corrigido. |
| C139-VIS-COMPAr-01 | Comparação | Referido, valor relativo e referendo devem estar numericamente completos e corretos. |
| C139-VIS-COMPAr-02 | Comparação | Valor relativo informado por teclado/eixo participa da mesma regra de conclusão. |
| C139-VIS-COMPAr-03 | Comparação | A interrogação impede a conclusão até ser posicionada corretamente e preenchida pelo protocolo mouse/texto. |
| C139-VIS-TRANS-01 | Transformação | Estado inicial, transformação e estado final numéricos ativam o destaque azul. |
| C139-VIS-TRANS-02 | Transformação | Valores conhecidos podem ser digitados; o item desconhecido não pode ser resolvido automaticamente antes do protocolo mouse/texto. |
| C139-VIS-TRANS-03 | Transformação | Um item em papel incorreto não é removido e continua reposicionável. |
| C139-VIS-CONT-01 | Todas | A opção Não mantém situação e destaque. |
| C139-VIS-CONT-02 | Todas | A opção Sim chama Sortear e mantém a categoria. |

Esses casos são obrigatórios antes de declarar uma versão visualmente estável. Testes automáticos não substituem a conferência do som, tremor, posicionamento do tip, intensidade das cores e resposta dos radio buttons.

## C140 — posição do tip de conclusão

- O tip com a pergunta **“Podemos passar para a próxima tarefa?”** deve aparecer abaixo da área visual efetivamente ocupada pelo diagrama de Vergnaud.
- A regra deve valer igualmente para os diagramas de **Composição**, **Comparação** e **Transformação**.
- O cálculo deve considerar figuras, rótulos, subtítulos, conectores, curvas e legendas.
- O tip não pode cobrir o diagrama nem ser posicionado sobre o eixo flutuante apenas por usar o topo do painel como referência.
- O tip deve permanecer dentro dos limites laterais e inferiores do painel de Vergnaud.
- Os radio buttons **Sim** e **Não**, o destaque azul e o fluxo consolidado de Sortear devem permanecer inalterados.

## C141 — aproveitamento compacto do painel do eixo

- O painel flutuante deve manter somente o espaço necessário para título, olhinho, rótulos laterais, valor selecionado, eixo, marcas e instrução.
- A altura do painel não deve crescer conforme o valor da escala.
- Escalas pequenas e médias devem reduzir a largura do painel sem comprometer a leitura das marcas.
- Escalas maiores podem usar até o limite máximo disponível, sem ultrapassar a tela.
- O olhinho permanece integralmente dentro do cabeçalho.
- A redução do painel não pode retirar a flutuação, a ocultação, a navegação do ponto azul nem a classificação entre componente de tela e valor semântico.

## C142 — compactação vertical adaptativa do eixo

- Sem valor selecionado, o painel do eixo não deve reservar espaço para o número azul nem para a instrução ausente.
- Com valor selecionado, o painel pode crescer somente o necessário para exibir número, ponto de controle e instrução.
- A margem entre o cabeçalho, os rótulos laterais, o eixo, as marcas e a borda inferior deve permanecer mínima e legível.
- O olhinho continua visível e funcional dentro do cabeçalho reduzido.
- A alteração de altura não pode afetar a flutuação, a navegação semântica, o clique no eixo ou a sincronização do valor relativo.


## C143 — tabuleiro concreto de transformação de medidas

- A categoria **Transformação de medidas** deve exibir, ao lado do diagrama formal, uma representação concreta organizada em `Antes → Mudança → Depois`.
- Estado inicial, transformação e estado final devem permanecer vinculados aos índices semânticos `0`, `1` e `2` do estado compartilhado.
- Antes do primeiro posicionamento no diagrama de Vergnaud, o tabuleiro permanece vazio e suas operações semânticas ficam bloqueadas.
- Estado inicial e estado final são quantidades cardinais e nunca podem ficar negativos.
- A zona de mudança representa a magnitude da transformação com unidades concretas e preserva seu sinal positivo ou negativo.
- Uma transformação negativa manipulada pelas unidades deve continuar negativa; aumentar ou reduzir unidades altera apenas sua magnitude.
- Arrastar unidades e usar os controles `+` e `−` deve atualizar texto, Vergnaud, eixo e demais representações pelo fluxo consolidado de sincronização.
- Digitação direta em qualquer zona do tabuleiro deve reconstruir as unidades e sincronizar o valor correspondente.
- Nenhuma unidade pode ser ocultada para simplificar a visualização; tamanho e espaçamento devem se adaptar à quantidade.
- Composição deve continuar usando coleções e comparação deve continuar usando barras, sem alteração das respectivas regras.
- Som, tremor, tips, proxy textual, item incorreto manipulável, eixo flutuante, conclusão azul e aba Construir devem permanecer funcionais.
- Os dados curados não podem ser modificados pela introdução do tabuleiro.

## C146 — perfis de categoria e sincronização concreta da transformação

### Grupos de categorias

- Toda categoria deve pertencer explicitamente a `COM_RELACAO_ASSINADA` ou `SEM_RELACAO_ASSINADA`.
- **Composição de medidas** pertence ao grupo sem relação assinada.
- **Transformação de medidas** pertence ao grupo com relação assinada; somente `transformação` pode receber sinal.
- **Comparação de medidas** pertence ao grupo com relação assinada; somente o valor relativo/diferença pode receber sinal.
- Categorias compostas usam o mesmo perfil para generalizar papéis numerados, sem condicionais repetidas na interface.
- Partes, todo, estados, referido e referendo continuam não negativos.
- O estado semântico compartilhado deve consultar o perfil da categoria para decidir quais índices aceitam sinal.

### Tábua/Máquina de transformação

- Assim que um número for digitado ou posicionado no diagrama de Vergnaud, a zona correspondente deve criar exatamente a mesma quantidade de quadradinhos.
- O estado inicial e o estado final usam a quantidade cardinal integral.
- A transformação usa a magnitude em quadradinhos e preserva o sinal no estado semântico e na apresentação `Entraram`/`Saíram`.
- Valores desconhecidos não criam quadradinhos.
- A reconstrução do tabuleiro deve ser feita a partir do `EstadoSemanticoCompartilhado.Snapshot`, nunca por uma cópia visual antiga.
- Alterações por teclado, arraste, eixo ou controles `+`/`−` devem convergir para o mesmo plano de unidades concretas.
- O layout retangular próprio do tabuleiro deve ser preservado para qualquer quantidade.

## C147 - modelo semântico explícito e universos numéricos

- A aceitação de sinal pertence ao objeto numérico associado ao papel semântico, e não ao texto, Vergnaud, eixo, barras, tabuleiro ou categoria visual.
- `NumeroNatural` representa N0 e rejeita valores negativos no construtor.
- `NumeroInteiro` representa Z e preserva valores negativos, zero e positivos.
- `ValorDesconhecido` mantém o universo esperado do papel sem revelar a resposta.
- Transformações e números/valores relativos utilizam o universo dos inteiros.
- Estados, partes, todo, referido e referendo utilizam o universo dos naturais.
- A forma `+n` é apresentação; o valor semântico armazenado continua sendo o inteiro `n`.
- Texto, Vergnaud, eixo, barras e tabuleiro consultam o mesmo valor do estado compartilhado.
- Contextos como bolas, carrinhos, figurinhas ou reais são dados de `ReferenteContextual`, e não subclasses específicas.
- Nomes de participantes são instâncias de `Personagem`, sem papel matemático fixo.
- Palavras-pista são evidências linguísticas localizadas e nunca classificam uma situação isoladamente.
- Categorias simples agregam papéis, restrições e relações; categorias de vários passos utilizam Composite.
- Perfis legados de categoria são derivados dos papéis e universos do esquema, não constituem fonte da verdade matemática.
- As fachadas antigas permanecem funcionais durante a migração incremental.
- A regressão deve executar `scripts/testar_modelo_semantico_explicito.sh`.


## C148 — processo de transformação com canal, funis e quadradinhos

- A representação complementar de **Transformação de medidas** passa a ser um processo `Antes → canal → Depois`.
- Estado inicial e estado final permanecem contêineres de quantidades naturais representadas pelos quadradinhos já utilizados no Gérard.
- A transformação positiva (`> 0`) deve exibir um funil superior de **inserção**; seus quadradinhos ficam acima do canal.
- A transformação negativa (`< 0`) deve exibir um funil inferior de **retirada**; seus quadradinhos ficam abaixo do canal.
- A transformação nula (`0`) ou ainda desconhecida mantém apenas o canal, sem quadradinhos na transformação.
- A escolha entre retirada, neutralidade e inserção deriva do objeto `NumeroInteiro`, não de regras duplicadas nas representações.
- A quantidade de quadradinhos da transformação é sempre `abs(valor)`, enquanto o sinal permanece no valor semântico compartilhado.
- Qualquer alteração por Vergnaud, texto, eixo ou controles `+`/`−` deve reconstruir imediatamente o processo e sincronizar todas as representações.
- Estados inicial e final continuam pertencendo aos naturais e não podem assumir valores negativos.
- Canal, funis, distribuição dos quadradinhos e política visual ficam fora da `Main.java`; a tela apenas coordena e delega.

## C149 — Processo de transformação sem view legada

- [x] Transformação de medidas seleciona exclusivamente `PROCESSO_TRANSFORMACAO`.
- [x] Não existem `RenderizadorTabuleiroTransformacao`, `LayoutTabuleiroTransformacao` ou `EstadoTabuleiroTransformacao` no código-fonte.
- [x] A zona semântica central não desenha contêiner retangular.
- [x] Valor positivo mostra funil superior e quadradinhos de inserção.
- [x] Valor negativo mostra funil inferior e quadradinhos de retirada.
- [x] Zero/desconhecido mantém o canal sem funil materializado.
- [x] Estado inicial e final permanecem naturais; transformação permanece inteira.
- [x] Alterações continuam sincronizadas entre texto, Vergnaud, eixo e processo concreto.

## C150 — funil estrutural, controles contextuais e correção do tip semântico

- [x] A estrutura do funil é visível mesmo quando a transformação ainda é desconhecida ou igual a zero.
- [x] A presença estrutural do funil não antecipa valor nem cria quadradinhos antes de uma quantidade conhecida.
- [x] Antes do primeiro posicionamento semântico no diagrama de Vergnaud, os controles `+` e `−` permanecem desabilitados.
- [x] Após o primeiro posicionamento, os controles da transformação passam a operar pelo estado semântico compartilhado.
- [x] Os controles `+` e `−` da transformação ficam ancorados junto ao funil ativo, não soltos no topo do painel.
- [x] Transformação positiva usa o funil superior; transformação negativa usa o funil inferior.
- [x] O primeiro `+` a partir do estado desconhecido/neutro produz valor positivo e quadradinhos de inserção.
- [x] O primeiro `−` a partir do estado desconhecido/neutro produz valor negativo e quadradinhos de retirada.
- [x] Estados inicial e final continuam pertencendo aos naturais e não podem ficar negativos.
- [x] O valor da transformação continua pertencendo aos inteiros e pode atravessar o zero.
- [x] Um tip de incompatibilidade semântica desaparece quando o mesmo item é reposicionado corretamente.
- [x] O valor `6` do exemplo de Leandro é reconhecido como `estado_final` e aceito no quadrado do estado final.
- [x] Texto, Vergnaud, eixo dos inteiros e processo concreto continuam sincronizados.
- [x] Item incorreto continua no diagrama, com som, tremor e tip, até ser corrigido pelo usuário.

## C151 — quadradinhos compactos no funil

- [x] Os quadradinhos dos estados inicial e final mantêm o tamanho consolidado.
- [x] Os quadradinhos da transformação usam limite menor para permanecer dentro do funil.
- [x] A redução visual não altera magnitude, sinal ou sincronização semântica.

## C152 — rótulos semânticos obrigatórios e localização do processo

- [x] Nenhuma figura do diagrama formal pode perder o rótulo do papel semântico por ausência de dado visual.
- [x] Na composição de medidas, as três figuras devem mostrar `Parte 1`, `Parte 2` e `Todo` — localizados no idioma ativo.
- [x] O normalizador preserva rótulos curados não vazios e recupera apenas rótulos ausentes ou chaves não resolvidas.
- [x] A mesma garantia é aplicada ao diagrama formal e à representação complementar.
- [x] Os rótulos de fluxo do processo de transformação não podem ser textos fixos em português.
- [x] Inserção usa `Entraram`, `Entered` ou `Sont entrés`, conforme o idioma ativo.
- [x] Retirada usa `Saíram`, `Left` ou `Sont sortis`, conforme o idioma ativo.
- [x] A correção deve permanecer fora da `Main.java`.
- [x] A regressão deve executar `scripts/testar_rotulos_semanticos_i18n.sh`.

## C154 — Comparação entre categorias no menu

- [ ] A opção **Comparação entre categorias** aparece habilitada no menu de categorias.
- [ ] A opção não apresenta o estado ou tooltip **Em construção**.
- [ ] Um clique abre a tela comparativa de composição, transformação e comparação de medidas.
- [ ] Abrir ou fechar a tela comparativa não limpa nem altera a modelagem corrente da atividade principal.
- [ ] Transformações compostas e Relações mantêm o comportamento anterior.


## C155 — conclusão progressiva padronizada em todas as categorias

- Todos os diagramas de Vergnaud das oito categorias usam a mesma sequência de conclusão.
- Ao completar todos os papéis numéricos corretos, o diagrama recebe o destaque azul e exibe primeiro o selo discreto **“Modelagem concluída”**.
- O selo deve entrar suavemente, permanecer por aproximadamente 1,15 segundo e desaparecer antes do tip.
- Somente depois do selo deve aparecer o tip **“Podemos passar para a próxima tarefa?”**, com radio buttons **Sim** e **Não**.
- O selo e o tip devem ocupar a mesma região abaixo da área visual real do diagrama, sem cobrir figuras, conectores ou rótulos.
- Composição de medidas, transformação de medidas, composição seguida de transformação, comparação de medidas, composição de transformações, transformação composta em dois passos, transformação de relação e composição de relações seguem o mesmo contrato.
- Se a modelagem deixar de estar concluída durante a espera, o temporizador deve ser cancelado e o tip não pode aparecer.
- A opção **Sim** continua chamando o fluxo consolidado de Sortear e preservando a categoria.
- A opção **Não** mantém a situação atual e o destaque azul.
- A sequência deve ser executada uma única vez por ciclo de conclusão, sem reaparecer em cada `repaint`.

| Identificador | Categoria | Contrato |
|---|---|---|
| C155-VIS-CM | Composição de medidas | Selo antes do tip, sem cobrir o diagrama. |
| C155-VIS-TM | Transformação de medidas | Mesma sequência progressiva após os três valores corretos. |
| C155-VIS-CMT | Composição seguida de transformação | Todos os papéis compostos participam antes da sequência. |
| C155-VIS-COP | Comparação de medidas | Referido, valor relativo e referendo acionam a mesma sequência. |
| C155-VIS-CT | Composição de transformações | Transformações 1, 2 e final usam o mesmo feedback. |
| C155-VIS-TCP | Transformação composta em dois passos | A sequência só inicia após os dois passos completos. |
| C155-VIS-TR | Transformação de uma relação | Relação inicial, transformação e relação final usam o mesmo feedback. |
| C155-VIS-CR | Composição de relações | Relações 1, 2 e final usam o mesmo feedback. |

## C156 — valores monetários integrais e materialização dos quadradinhos

- [x] Valores textuais como `25,00`, `18.00`, `1.250,00` e `1,250.00` são reconhecidos como inteiros quando a parte decimal é zero.
- [x] O sinal da transformação é preservado nas formas `-18,00`, `−18,00` e `+18,00`.
- [x] Valores com parte fracionária não nula, como `25,50`, não são truncados nem convertidos silenciosamente.
- [x] Ao posicionar `25,00` no estado inicial do Vergnaud, o processo concreto materializa 25 quadradinhos.
- [x] Ao posicionar `-18,00` na transformação, o funil de retirada materializa 18 quadradinhos e preserva o valor semântico negativo.
- [x] A relação `25 + (-18) = 7` permanece consistente no estado compartilhado e na representação complementar.
- [x] A regra do primeiro posicionamento continua válida: nenhuma unidade é antecipada antes de um posicionamento semântico válido no Vergnaud.
- [x] A conversão numérica fica em `gerard.semantica.numero.ConversorTextoParaInteiroSemantico`; a `Main.java` apenas delega.
- [x] A regressão deve executar `scripts/testar_sincronizacao_decimais_transformacao.sh`.

## C157 — grandezas quantitativas contextuais: contagem e dinheiro

- [x] O valor numérico, a grandeza, a unidade e o papel semântico são conceitos separados.
- [x] Quantidades de elementos usam a grandeza `CONTAGEM` e não aceitam parte fracionária.
- [x] Quantidades monetárias usam a grandeza `MONETARIA`, preservam precisão decimal exata e não utilizam `double`.
- [x] O papel semântico continua determinando o sinal: estados e medidas permanecem não negativos; transformação e valor relativo podem ser negativos.
- [x] Metadados explícitos `GRANDEZA=CONTAGEM` ou `GRANDEZA=MONETARIA` têm prioridade sobre pistas linguísticas.
- [x] Registros legados podem usar pistas como `R$`, `real`, `reais`, `dólar` e `euro` apenas como fallback de compatibilidade.
- [x] Valores monetários integrais como `25,00` continuam compatíveis com o estado inteiro consolidado.
- [x] Centavos não nulos não são truncados pela ponte legada.
- [x] Contagem mantém uma unidade visual por elemento.
- [x] Dinheiro só agrupa quadradinhos quando existe um divisor comum exato entre as quantidades conhecidas.
- [x] Quando houver agrupamento monetário, a interface mostra legenda explícita, por exemplo `■ = R$ 10,00`.
- [x] Adicionar ou remover um quadradinho agrupado altera o valor semântico pela mesma escala mostrada na legenda.
- [x] Valores monetários exibidos no processo mantêm duas casas decimais.
- [x] A regra do primeiro posicionamento, os sinais, a não negatividade, a sincronização e a conclusão progressiva permanecem válidos.
- [x] A lógica de grandezas fica em `gerard.semantica.quantidade`; a `Main.java` somente coordena os serviços.
- [x] A regressão executa `scripts/testar_grandezas_quantitativas_contextuais.sh`.


## C158 — ciclo generalizado da incógnita

- A regra vale para todas as categorias e para qualquer papel que a curadoria defina como desconhecido.
- A fase `AGUARDANDO_PREENCHIMENTO_INCOGNITA` ocorre quando todos os demais papéis estão corretos e o `?` está no papel adequado.
- A fase `CONCLUIDA` só ocorre depois que o item originalmente desconhecido recebe um número pelo protocolo de mouse/texto.
- O estado semântico compartilhado recebe o índice protegido da incógnita e não resolve esse índice automaticamente antes da liberação.
- Texto, Vergnaud, eixo, barras, coleções e processo de transformação preservam a incógnita até a ação explícita do usuário.
- Digitação direta em um elemento vazio não substitui o ciclo obrigatório do item `?`.
- A regra do primeiro posicionamento e os bloqueios das representações complementares permanecem inalterados.

## C159 — Consistência entre valores curados e modelo semântico

- [x] O símbolo `?` armazenado em um campo quantitativo curado identifica esse papel como desconhecido, mesmo quando `termo_desconhecido` estiver vazio em um registro legado.
- [x] Quando existe exatamente um campo com `?` e `termo_desconhecido` está vazio, a curadoria harmoniza automaticamente o combo com o mesmo papel, sem inferir pelo texto do enunciado.
- [x] `termo_desconhecido` e o campo que contém `?` devem indicar o mesmo papel semântico.
- [x] Divergência entre `termo_desconhecido` e o campo com `?` bloqueia o salvamento e solicita correção ao curador.
- [x] Mais de um campo quantitativo com `?` bloqueia o salvamento; a situação deve possuir uma única incógnita.
- [x] O modelo semântico nunca trata `?` como valor conhecido.
- [x] A regra vale para composição de medidas, transformação de medidas, composição seguida de transformação, comparação de medidas, composição de transformações, transformação composta em dois passos, transformação de relação e composição de relações.
- [x] As regras de domínio numérico, sinal, primeiro posicionamento, protocolo de mouse/texto, sincronização e conclusão permanecem inalteradas.

## C160 — aviso de termo desconhecido vazio

- [x] O campo `termo_desconhecido` vazio recebe aviso visual não modal.
- [x] O aviso desaparece após seleção explícita.
- [x] Harmonização automática de um único `?` continua funcionando e passa a ser informada ao curador.
- [x] Traduções com semântica herdada não exibem aviso indevido.
- [x] Nenhuma regra foi adicionada à `Main.java`.

## C161 — Janela principal maximizada e comparação responsiva

- [x] O Gérard inicia maximizado, preservando barra de título, minimizar, maximizar, restaurar e fechar.
- [x] O modo usado é `JFrame.MAXIMIZED_BOTH`, sem tela cheia exclusiva.
- [x] A janela restaurada mantém tamanho padrão e mínimo utilizável.
- [x] A tela “Comparação entre categorias” é dimensionada a partir da janela principal.
- [x] O diálogo comparativo não ultrapassa 90% da área útil do monitor.
- [x] O diálogo é centralizado sobre a janela principal e limitado à área útil do monitor.
- [x] O último tamanho escolhido pelo usuário é reaproveitado durante a sessão, respeitando os limites do monitor.
- [x] A grade comparativa mantém suas barras de rolagem internas em áreas menores.
- [x] As regras de dimensionamento permanecem fora da `Main.java`.

## C162 — distribuição trapezoidal dos quadradinhos no funil

- [x] Os quadradinhos da transformação são posicionados pela geometria real do funil, não por sua caixa retangular envolvente.
- [x] Cada linha calcula a largura interna disponível entre as bordas inclinadas.
- [x] As unidades ficam centralizadas e formam linhas progressivamente menores em direção ao gargalo.
- [x] No caso `+5`, a distribuição visual esperada é `2–2–1`.
- [x] Nenhum quadradinho atravessa as bordas inclinadas ou ocupa a haste do funil.
- [x] A regra é aplicada tanto à inserção positiva quanto à retirada negativa.
- [x] Magnitudes maiores preservam todas as unidades, reduzindo o tamanho quando necessário.
- [x] Os quadradinhos dos estados inicial e final mantêm o layout consolidado.
- [x] A sincronização, os controles `+`/`−`, o primeiro posicionamento e a conclusão permanecem inalterados.
- [x] A implementação fica em `LayoutUnidadesProcessoTransformacao`, sem nova regra na `Main.java`.
- [x] A regressão executa `scripts/testar_distribuicao_funil_trapezoidal.sh`.
