## Atualização mais recente - 17/07/2026 — C133

- **Posicionamento incorreto permanece manipulável**: números, valores assinados e interrogação soltos em papel semântico incorreto permanecem no diagrama para nova tentativa.
- **Feedback consolidado preservado**: o item continua recebendo tip contextual persistente, som sutil e tremor leve, sem ser removido ao final da animação.
- **Descarte restrito**: somente proxies soltos fora da área do diagrama são descartados; erro semântico não implica remoção visual.
- **Regressão formalizada**: criada `MATRIZ_REGRESSAO_FUNCIONALIDADES_CONSOLIDADAS.md` e adicionados contratos automáticos ao verificador estrutural.
- **Curadoria preservada**: dados da C132 e C133 mantêm hashes idênticos.

## Atualização mais recente - 17/07/2026 — C132

- **Feedback multissensorial restaurado para proxies textuais**: posicionamentos semanticamente incorretos voltam a produzir som sutil, tremor leve e tip contextual.
- **Descarte posterior ao feedback**: o proxy permanece visível durante a animação e só é removido depois que sua posição original é restaurada.
- **Texto e estado preservados**: o elemento matemático continua no enunciado e nenhuma representação semântica é alterada pela tentativa incorreta.
- **Arquitetura**: a coordenação foi concentrada em `ScaffoldingFeedbackProxyPosicionamento`, com estado transitório mantido por `SessaoArrasteTextoParaDiagrama`.
- **Regressão**: testes específicos e regressão estrutural completa aprovados, incluindo a verificação passo a passo da aba Construir da C131.

## Atualização mais recente - 16/07/2026 — C121

- **Drag-and-drop com mola e momento**: o elemento persegue o cursor com pequeno atraso elástico, massa, inércia e amortecimento, sem teletransporte visual.
- **Precisão preservada**: a soltura conclui exatamente na coordenada do mouse antes da validação semântica.
- **C120 preservada**: bloqueio inicial de `+` e `−`, limites curados, piso zero, consistência aditiva, sincronização entre representações e imutabilidade da curadoria foram mantidos.
- **Arquitetura**: novo `ControladorArrasteMolaMomento` sob o contrato já existente de arraste elástico.
- **Regressão**: testes de física, interface e controles quantitativos aprovados.

Arquivo central: `documentacao/relatorios/RELATORIO_DRAG_MOLA_MOMENTO_C121_2026-07-16.md`.

## Atualização mais recente - 16/07/2026 — C120

- **Controles quantitativos por estado semântico**: `+` e `−` permanecem inabilitados enquanto o diagrama de Vergnaud estiver vazio, independentemente do modo de entrada.
- **Limites positivos**: unidades podem ser acrescentadas até os valores curados e retiradas até zero, sem admitir quantidades negativas.
- **Sincronização compartilhada**: texto, diagrama de Vergnaud, quadradinhos de composição e quadradinhos das barras são atualizados pela mesma alteração de estado.
- **Curadoria preservada**: os dados curados funcionam como limites e referência semântica, mas não são modificados pela interação.
- **Regressão**: novos testes cobrem preenchimento manual, textual e automático, limites por categoria e preservação da curadoria.

Arquivo central: `documentacao/relatorios/RELATORIO_CONTROLES_QUANTIDADES_POSITIVAS_C120_2026-07-16.md`.

## Atualização mais recente - 16/07/2026

- **C90 - feedback multissensorial de erro**: tremor leve, som sutil e tooltip contextual para posicionamento semântico incompatível, com remoção após a correção.
- **C91 - ajuda contextual “E agora?”**: interrogações nas áreas do texto, do diagrama de Vergnaud e do diagrama complementar oferecem dúvida, continuidade e próximo passo.
- **C92 - documentação**: artigo de co-design e análise de complexidade atualizados até C91, com PDFs canônicos, fontes e cópias versionadas incluídas no pacote.

## Atualização mais recente - 15/07/2026

- **Correspondência cromática C47**: nas barras de comparação, a mesma quantidade de unidades, contada de baixo para cima, recebe vermelho suave; unidades excedentes permanecem neutras.

# Índice do histórico de mudanças do GERARD

Este arquivo é um índice de apoio, gerado a partir dos mais de 120 arquivos individuais de histórico (`ARQUITETURA_*.txt`, `RELATORIO_*.md`, `AJUSTE_*.txt`, `ATUALIZACAO_*.txt`, `ALTERACAO_*.txt`) mantidos na raiz do projeto. Cada linha abaixo mostra o nome do arquivo original e o resumo declarado na primeira linha de conteúdo dele — os arquivos originais **não foram removidos nem alterados**, este índice é apenas um ponto de partida para localizar rapidamente um assunto sem abrir arquivo por arquivo. Para a visão narrativa consolidada, veja `LEIA-ME.txt`.

## Atualização mais recente - 15/07/2026

- **Nova aba Construir situação-problema**: construção de um enunciado a partir de um diagrama preenchido.
- **Blocos semanticamente próximos**: trechos de outras categorias preservam os mesmos valores, personagens, contexto e vocabulário.
- **Intervenção do pesquisador**: adoção de distância semântica controlada para evitar resolução por pistas superficiais e o efeito de “jogo dos sete erros”.
- **Rastreabilidade**: tentativa independente no logger, documentação arquitetural, relatório da intervenção, testes específicos e atualização do artigo de co-design como ciclo C33.
- **Nomenclatura e ajuda contextual C35**: o verbo “construir” substitui “montar”, e a definição da categoria aparece somente no onmouseover do nome, preservando apoio sob demanda.
- **Gráfico de barras manipulável C36**: em comparação de medidas, o diagrama de Venn foi substituído por barras manipuláveis sincronizadas com a curadoria e com a modelagem.
- **Segmento graduado e personagens C41**: o gráfico de comparação passou a exibir apenas os diagramas, com eixo do valor relativo de 0 a n, ponto de controle sincronizado e personagens sob referido/referendo.
- **Correção semântica C37**: o gráfico de comparação passou a usar `referido`, `valor_relativo` e `referendo` diretamente da curadoria, preservando “?” no papel desconhecido e representando a extensão calculada da barra.
- **Curadoria sem identificadores técnicos C38**: os campos `id da versão`, `situação_grupo_id` e `versão_origem_id` foram retirados da interface e preservados apenas internamente.
- **Participantes e objetos curados C39**: inclusão dos campos `personagem_1`, `personagem_2` e `personagem_3`, persistidos por versão linguística e compatíveis com arquivos anteriores.
- **Legibilidade da descrição C40**: a descrição do gráfico de comparação passou a usar quebra automática de linha e limite de duas linhas, evitando corte lateral do texto.

Arquivos centrais: `documentacao/alteracoes/NOVA_ABA_MONTAGEM_SITUACAO_PROBLEMA_2026-07-14.md`, `documentacao/arquitetura/ARQUITETURA_MONTAGEM_SITUACAO_BLOCOS_SEMANTICOS.txt` e `documentacao/relatorios/RELATORIO_INTERVENCAO_PESQUISADOR_BLOCOS_SEMANTICAMENTE_PROXIMOS_2026-07-14.md`.


## Notas de arquitetura (68)

- **ARQUITETURA_AJUSTE_INTERROGACAO_SEM_POSITIVO.txt** — Ajuste: preenchimento de interrogação sem sinal positivo explícito
- **ARQUITETURA_AREA_INVISIVEL_DIAGRAMAS_VERGNAUD.txt** — ÁREA CENTRAL INVISÍVEL PARA DIAGRAMAS DE VERGNAUD
- **ARQUITETURA_BOTAO_RESTAURAR_DIAGRAMA.txt** — Alteração: botão Restaurar na área do diagrama
- **ARQUITETURA_CAMPO_ADITIVO.txt** — MÓDULO DE CAMPO ADITIVO - ORGANIZAÇÃO
- **ARQUITETURA_CENTRALIZACAO_VERGNAUD.txt** — CENTRALIZAÇÃO VERTICAL DO DIAGRAMA DE VERGNAUD
- **ARQUITETURA_CLASSIFICADOR_HIBRIDO_VERGNAUD.txt** — Classificador híbrido de categorias de Vergnaud
- **ARQUITETURA_COMPOSICAO_RELACOES_CORRIGIDA.txt** — AJUSTE DA CATEGORIA COMPOSIÇÃO DE RELAÇÕES
- **ARQUITETURA_CONTAGEM_QUESTOES.txt** — RÓTULO DE CONTAGEM DE QUESTÕES
- **ARQUITETURA_CORRECAO_TIPS_INTERPRETACAO.txt** — Correção dos tips na interpretação linguística
- **ARQUITETURA_D3_EXPLICACOES_DINAMICAS.txt** — Atualização: explicações dinâmicas das visualizações D3
- **ARQUITETURA_D3_LEITURA_REDES_TRANSICOES.txt** — Atualização: leitura interpretativa das redes de transição no D3.js
- **ARQUITETURA_D3_MATRIZ_EFETIVIDADE.txt** — Atualização D3 - Matriz de efetividade do feedback/scaffolding
- **ARQUITETURA_D3_TOOLTIP_ENUNCIADO_MAPA_CALOR.txt** — Atualização: os elementos/células do mapa de calor D3.js exibem, no onmouseover, a quantidade de eventos e o enunciado da situação-problema 
- **ARQUITETURA_DIAGRAMAS_ESPECIFICOS.txt** — MÓDULO DE DIAGRAMAS ESPECÍFICOS DO CAMPO ADITIVO
- **ARQUITETURA_DIAGRAMAS_VAZIOS.txt** — DIAGRAMAS DE VERGNAUD SEM PREENCHIMENTO AUTOMÁTICO
- **ARQUITETURA_DIAGRAMAS_VAZIOS_SUBTIPOS.txt** — DIAGRAMAS VAZIOS COM SUBTIPOS PRESERVADOS
- **ARQUITETURA_DIAGRAMAS_VERGNAUD.txt** — MÓDULO DE DIAGRAMAS DE VERGNAUD
- **ARQUITETURA_ELEMENTOS_VERGNAUD_INDIVIDUAIS.txt** — INTERAÇÃO INDIVIDUAL COM OS ELEMENTOS DO DIAGRAMA DE VERGNAUD
- **ARQUITETURA_ESCOPO_QUATRO_IDIOMAS.txt** — ARQUITETURA — ESCOPO LINGUÍSTICO CONTROLADO
- **ARQUITETURA_FEEDBACK_ESTILOS_PROXIMIDADE_EXPLICITOS.txt** — ALTERAÇÃO: feedback explícito dos estilos de interação por proximidade
- **ARQUITETURA_FEEDBACK_TESTE_ALVO.txt** — ALTERAÇÃO: CATEGORIAS DE FEEDBACK VISUAL DE ALVO
- **ARQUITETURA_FILTRO_NUMERAIS_CONTEXTUAIS.txt** — Filtro contextual de numerais na interpretação linguística
- **ARQUITETURA_I18N_COMPLETA.txt** — MÓDULO DE INTERNACIONALIZAÇÃO FLEXÍVEL
- **ARQUITETURA_INSERCAO_SITUACOES_PDF_QUEIROZ_GOMES.txt** — Inserção de situações-problema do PDF Queiroz/Gomes
- **ARQUITETURA_INTERPRETACAO_PAPEIS_NUMERICOS.txt** — MÓDULO DE INTERPRETAÇÃO DOS PAPÉIS NUMÉRICOS
- **ARQUITETURA_INTERPRETADOR.txt** — ARQUITETURA DO MODULO DE INTERPRETACAO LINGUISTICA
- **ARQUITETURA_LOGS_INICIAIS_QUADROS.txt** — Logs iniciais da Visão de pesquisador
- **ARQUITETURA_LOG_FIEL_AGENTES.txt** — Atualização: log fiel à coluna Agente da ação dos quadros anexados
- **ARQUITETURA_LOG_FIEL_AGENTES_CS_V7.txt** — Atualização do log fiel aos quadros anexados
- **ARQUITETURA_LOG_QUADRO_VALDERLANGELA_DOC.txt** — Atualização: inclusão do quadro de análise instrumental de Valderlangela no log do Gerard
- **ARQUITETURA_LOG_VISAO_PESQUISADOR.txt** — Visão de pesquisador com dados gerados em tempo de uso
- **ARQUITETURA_MENU_INTERNO.txt** — MENU INTERNO VERTICAL POR MOUSE OVER
- **ARQUITETURA_MENU_SOBRE_INTERPRETACAO.txt** — MENU SOBRE COM INTERPRETAÇÃO LINGUÍSTICA
- **ARQUITETURA_MOUSEOVER_PALAVRAS_NUMEROS.txt** — MOUSE OVER SEPARADO PARA PALAVRAS E VALORES
- **ARQUITETURA_MOUSEOVER_PAPEIS.txt** — MOUSE OVER COM PAPÉIS EXCLUSIVOS DOS NÚMEROS E DA INTERROGAÇÃO
- **ARQUITETURA_MOUSEOVER_TEXTO_SIMPLES.txt** — AJUSTE DO MOUSE OVER DO TEXTO
- **ARQUITETURA_NUMERAIS_RENDERIZADOS.txt** — Ajuste realizado:
- **ARQUITETURA_PROPORCAO_DIAGRAMAS_MAXIMIZACAO.txt** — Ajuste: proporção das áreas dos diagramas na maximização
- **ARQUITETURA_QUATRO_PILARES_GERARD.txt** — ARQUITETURA CONCEITUAL DO GÉRARD — QUATRO PILARES
- **ARQUITETURA_REALCE_ALVO_PROXIMIDADE_INTERPRETACAO.txt** — REALCE TEMPORÁRIO DO ALVO POR PROXIMIDADE COM BASE NA INTERPRETAÇÃO LINGUÍSTICA
- **ARQUITETURA_REDESIGN_INTERFACE.txt** — REDESIGN VISUAL DA INTERFACE
- **ARQUITETURA_REDESIGN_SUAVE.txt** — REDESIGN SUAVE DA INTERFACE
- **ARQUITETURA_RENDER_CENTRAL_VERGNAUD.txt** — RENDERIZAÇÃO CENTRAL DOS DIAGRAMAS DE VERGNAUD
- **ARQUITETURA_ROTULO_CONTAGEM_REMOVIDO.txt** — REMOÇÃO TEMPORÁRIA DO RÓTULO DE CONTAGEM
- **ARQUITETURA_SCAFFOLDING_QUESTIONAMENTO.txt** — Arquitetura - Scaffolding.questionamento
- **ARQUITETURA_SEM_CONTAGEM_QUESTOES.txt** — REMOÇÃO DA CONTAGEM DE QUESTÕES
- **ARQUITETURA_SEM_TRACEJADO_DIAGRAMAS.txt** — AJUSTE VISUAL DOS DIAGRAMAS
- **ARQUITETURA_SITUACOES_ALEATORIAS_VERGNAUD.txt** — BANCO DE SITUAÇÕES-PROBLEMA POR CATEGORIA DE VERGNAUD
- **ARQUITETURA_SUBMENU_COMPACTO.txt** — AJUSTE DO SUBMENU DE INTERPRETAÇÃO LINGUÍSTICA
- **ARQUITETURA_SUBMENU_GERARD_VERGNAUD_CORRIGIDO.txt** — Correção do submenu Gérard Vergnaud
- **ARQUITETURA_SUBMENU_INTERPRETACAO.txt** — SUBMENU DE INTERPRETAÇÃO LINGUÍSTICA
- **ARQUITETURA_SUBMENU_LEGENDA_VERGNAUD.txt** — Submenu Legenda no menu Sobre
- **ARQUITETURA_SUBMENU_REGISTRO_GERARD.txt** — SUBMENU DE REGISTRO DO GERARD
- **ARQUITETURA_SUBTIPOS_INCOGNITAS.txt** — REFINAMENTO DOS SUBTIPOS / INCÓGNITAS POR CATEGORIA DE VERGNAUD
- **ARQUITETURA_TABELA4_I18N.txt** — Correção: internacionalização do conteúdo da Tabela 4 na Visão de pesquisador.
- **ARQUITETURA_TEXTO_CATEGORIA_ENUNCIADO.txt** — TEXTO DA CATEGORIA NA ÁREA DO ENUNCIADO
- **ARQUITETURA_TEXTO_VERGNAUD_ENUNCIADO.txt** — TEXTO DE VERGNAUD ABAIXO DO ENUNCIADO
- **ARQUITETURA_TIP_DESCRICAO_CATEGORIA.txt** — TIP DA DESCRIÇÃO DA CATEGORIA NO TÍTULO DO ENUNCIADO
- **ARQUITETURA_TIP_QUEBRA_LINHA.txt** — TIP COM QUEBRA DE LINHA
- **ARQUITETURA_TRANSFORMACAO_COMPOSTA_EM_PASSOS.txt** — Ajuste realizado:
- **ARQUITETURA_VENN_DINAMICO.txt** — MÓDULO DE DIAGRAMA DE VENN DINÂMICO
- **ARQUITETURA_VINCULO_TRADUCOES_ARQUIVOS.txt** — VÍNCULO ENTRE SITUAÇÃO-PROBLEMA E TRADUÇÕES — SEM BANCO DE DADOS
- **ARQUITETURA_VISAO_PESQUISADOR.txt** — Visão de pesquisador no Gerard
- **ARQUITETURA_VISAO_PESQUISADOR_ABAS_FILTRADAS.txt** — Visão de pesquisador - abas filtradas
- **ARQUITETURA_VISAO_PESQUISADOR_TABELAS_ESTUDO.txt** — Visão de pesquisador baseada nas tabelas do estudo
- **ARQUITETURA_VISAO_PESQUISADOR_TABELAS_SINTETICAS.txt** — Visão de pesquisador - tabelas sintéticas do estudo
- **ARQUITETURA_ZONAS_SEMANTICAS_POR_CATEGORIA.txt** — ZONAS SEMÂNTICAS POR CATEGORIA DE VERGNAUD
- **ARQUITETURA_ZONAS_SEMANTICAS_VERGNAUD.txt** — ZONAS SEMÂNTICAS DOS ELEMENTOS DO DIAGRAMA DE VERGNAUD

## Relatórios de mudança/regressão (39)

- **RELATORIO_AJUSTE_DIALOGO_MENU_CATEGORIA_2026-07-14.md** — Ajuste do diálogo de valor e do menu de categorias
- **RELATORIO_CATEGORIA_ACIMA_ENUNCIADO_2026-07-13.md** — Ajuste visual da categoria do enunciado
- **RELATORIO_COMPARACAO_MENSAGEM_CENTRAL_2026-07-14.md** — RELATÓRIO — Reforço da mensagem central na tela comparativa
- **RELATORIO_CONSOLIDACAO_AVISOS_CURADORIA_2026-07-12.md** — Consolidação dos avisos de curadoria
- **RELATORIO_CORRECAO_DIALOGO_VALOR_SEM_SINAL_2026-07-13.md** — Correção do diálogo de substituição da interrogação
- **RELATORIO_CORRECAO_PROTOCOLLOS_TELA_EXPLICACOES_2026-07-13.md** — Correção dos protocolos na tela de explicações
- **RELATORIO_CORRECAO_PROTOCOLOS_TEXTO_2026-07-13.md** — Correção dos protocolos por elemento semântico
- **RELATORIO_CORRECAO_SELECAO_CATEGORIA.md** — Correção do fluxo de seleção de categoria
- **RELATORIO_CURADORIA_E_REGRESSAO_2026-07-11.md** — Relatório de alteração — curadoria humana das situações-problema
- **RELATORIO_CURADORIA_REORGANIZADA_2026-07-12.md** — Relatório de atualização — Curadoria reorganizada
- **RELATORIO_ESTADO_IDIOMA_PRESERVADO.txt** — Alteração realizada: preservação do estado da tela ao trocar idioma
- **RELATORIO_FOTOGRAFIA_ESTADO_MODELAGEM_2026-07-13.md** — Fotografia visual do estado da modelagem
- **RELATORIO_INVARIANTES_SIMBOLICOS_MAURICIO_2026-07-13.md** — Invariantes simbólicos na tela de análise
- **RELATORIO_PADROES_PROJETO_2026-07-13.md** — Relatório de aplicação gradual de padrões de projeto no Gérard
- **RELATORIO_PILAR_ESTILO_INTERACAO_2026-07-13.md** — Relatório — Pilar independente de estilo de interação
- **RELATORIO_PRESERVACAO_ESTADO_ENTRE_PILARES_2026-07-13.md** — Relatório de preservação de estado entre pilares
- **RELATORIO_REGRESSAO_CURADORIA_CATEGORIAS_COLORIDAS.md** — Relatório de regressão — curadoria com categorias coloridas
- **RELATORIO_REGRESSAO_CURADORIA_COMPARACAO_MEDIDAS.md** — Relatório de regressão - curadoria de comparação de medidas
- **RELATORIO_REGRESSAO_CURADORIA_DETALHADA_2026-07-12.md** — Relatório de regressão — Curadoria detalhada por situação-problema
- **RELATORIO_REGRESSAO_CURADORIA_SINAIS_2026-07-12.md** — Relatório de alteração — curadoria de sinais
- **RELATORIO_REGRESSAO_GERARD_2026-07-11.md** — Relatório de regressão — Gerard
- **RELATORIO_REGRESSAO_GERARD_2026-07-11_ARTIGO_INDEFINIDO.md** — Relatório de regressão - correção de artigo indefinido no enunciado
- **RELATORIO_REGRESSAO_GERARD_2026-07-11_TRANSFORMACAO_FRUTAS.md** — Relatório de regressão — correção de classificação: frutas
- **RELATORIO_REGRESSAO_GERARD_2026-07-11_VENN_COMPOSICAO.md** — Relatório de regressão — Venn para composição de medidas
- **RELATORIO_REGRESSAO_GERARD_COMPOSICAO_COLECOES.md** — Relatório de regressão — diagrama de composição de coleções
- **RELATORIO_REGRESSAO_GERARD_CORRECAO_VENN_RESULTADO.md** — Relatório de regressão — correção do resultado no Venn de composição
- **RELATORIO_REGRESSAO_GERARD_CORRECAO_VENN_RESULTADO_VAZIO.md** — Relatório de regressão — correção da contagem no resultado vazio do Venn
- **RELATORIO_REGRESSAO_GERARD_CURADORIA_FONTE_VERDADE.md** — Relatório de alteração e regressão — Curadoria como fonte da verdade
- **RELATORIO_REGRESSAO_INTERROGACAO_PAPEL_SEMANTICO.md** — Regressão — interrogação e papel semântico
- **RELATORIO_REGRESSAO_MENSAGEM_AUSENCIA_2026-07-11.md** — Relatório de regressão — mensagem de ausência de situações curadas
- **RELATORIO_REGRESSAO_MENSAGEM_SISTEMA_NAO_ARASTAVEL.md** — Relatório de regressão — mensagem de sistema não arrastável
- **RELATORIO_REGRESSAO_SOMENTE_CURADAS_2026-07-11.md** — Relatório de regressão — exibição apenas de situações-problema curadas
- **RELATORIO_REGRESSAO_USABILIDADE_QUATRO_PILARES_2026-07-13.md** — Regressão de usabilidade e coerência dos quatro pilares
- **RELATORIO_SIMPLIFICACAO_ESCOPO_LINGUISTICO_2026-07-13.md** — Simplificação do escopo linguístico
- **RELATORIO_SINCRONIZACAO_VENN.md** — Relatório de atualização — sincronização do Diagrama de Venn
- **RELATORIO_TELA_EXPLICACOES_DELIMITADA_2026-07-13.md** — Ajuste da tela de Análise da Modelagem
- **RELATORIO_TELA_EXPLICACOES_TAREFAS_LOG_UNICO_2026-07-13.md** — Tela de explicações: tarefas matemática e de interação
- **RELATORIO_TENTATIVAS_ARTEFATO_EXPLICATIVO_2026-07-13.md** — Tentativas e artefato explicativo
- **RELATORIO_TENTATIVA_POR_SITUACAO_2026-07-13.md** — Tentativa por situação

## Ajustes pontuais (9)

- **AJUSTE_AREA_DELIMITADORA_ELEVADA.txt** — Ajuste realizado:
- **AJUSTE_AREA_E_TRANSFORMACAO_SEM_PREENCHIMENTO.txt** — Ajustes desta versão:
- **AJUSTE_AREA_VISIVEL_VERGNAUD_SEM_INVISIVEL.txt** — Ajustes desta versão:
- **AJUSTE_ESPACO_DIAGRAMAS.txt** — Ajuste realizado:
- **AJUSTE_ESPACO_DIAGRAMAS_CORRECAO.txt** — Correção adicional:
- **AJUSTE_PROPAGACAO_ESTADO_FINAL_P1_P2.txt** — Ao confirmar a edição de texto em um elemento do diagrama de transformação composta, se o elemento editado for o estado final de um passo, o
- **AJUSTE_SCAFFOLDING_NUMERO_RELATIVO.txt** — Feature implementada: quando um número ou interrogação é inserido em um círculo de número relativo por soltura ou por edição de texto, é exi
- **AJUSTE_SUBIDA_DIAGRAMAS_COMPOSTOS.txt** — Ajuste adicional: o bloco de diagramas da transformação composta em mais de um passo foi deslocado 20 pixels para cima, preservando a centra
- **AJUSTE_SUBIDA_VERTICAL_PASSOS_EXTRA.txt** — Ajuste aplicado: os diagramas de cada passo da transformação composta em dois passos foram elevados verticalmente em 18 pixels dentro de cad

## Atualizações de funcionalidade (4)

- **ATUALIZACAO_PASSOS_E_DETECCAO.txt** — Atualizações adicionais implementadas:
- **ATUALIZACAO_SCAFFOLDING_MENUS.txt** — Atualizacao: reorganizacao do menu de radio buttons do numero relativo.
- **ATUALIZACAO_SCAFFOLDING_PROXIMIDADE.txt** — Atualizacao: modularizacao dos modos de feedback por proximidade
- **ATUALIZACAO_SUBMENU_EM_RECIFE.txt** — Atualização - Submenu Em Recife

## Alterações de funcionalidade (2)

- **ALTERACAO_CURADORIA_SALVAMENTO_AUTOMATICO.txt** — ALTERAÇÃO — CURADORIA ESPECÍFICA COM SALVAMENTO AUTOMÁTICO
- **ALTERACAO_INTERROGACAO_DIAGRAMA.txt** — ALTERAÇÃO — ITEM DESCONHECIDO E DIAGRAMA VAZIO

## Alterações de funcionalidade (1)

- **ALTERACOES_DIAGRAMAS_OFICIAIS_VERGNAUD.txt** — ALTERAÇÕES - DIAGRAMAS OFICIAIS DE VERGNAUD

## Notas avulsas (1)

- **LEIA_CORRECAO_IMAGEM_DUPLO_CLIQUE.txt** — Pacote corrigido a partir da última versão estável enviada.

## Checklists (1)

- **CHECKLIST_REGRESSAO_GERARD.md** — Checklist de regressão do Gerard


## C34 — Distribuicao Windows automatizada

- Instalador autocontido de um clique para Windows.
- Download e verificacao do JRE 21, atalhos, icone e desinstalacao por usuario.
- Documentacao da intervencao do pesquisador em `documentacao/C34_DISTRIBUICAO_WINDOWS_INSTALADOR_AUTOMATICO.md`.

## Correção da montagem — Novo diagrama

- **RELATORIO_CORRECAO_NOVO_DIAGRAMA_VARIACAO_C33_2026-07-14.md** — evita repetição imediata, alterna categoria quando possível e acrescenta catálogo mínimo semanticamente controlado.

## C35 — Construir situação-problema e definição da categoria sob demanda

- **RELATORIO_INTERVENCAO_PESQUISADOR_CONSTRUCAO_TOOLTIP_CATEGORIA_2026-07-14.md** — substitui “montar” por “construir” e registra a decisão pedagógica do pesquisador.
- **RELATORIO_REGRESSAO_CONSTRUCAO_TOOLTIP_CATEGORIA_2026-07-14.md** — valida a nomenclatura, o tooltip restrito ao nome da categoria e a ausência de regressões.

- **Ponto manipulável e cartão relativo C42**: o ponto azul do segmento de comparação tornou-se arrastável e o cartão do valor relativo foi restaurado com sincronização visual.

- **Arraste contínuo e Referendo sincronizado C45**: o ponto azul acompanha continuamente o mouse; a barra do Referendo muda de quantidade ao cruzar cada valor inteiro do eixo.

- **Remoção do Venn em transformação C49**: na categoria Transformação de medidas, permanece apenas o diagrama de Vergnaud, ampliado para toda a área de diagramas.

- **Generalização semântica pela curadoria C52**: centralização dos papéis, valores, sinais, termo desconhecido e participantes/objetos na classe `SemanticaCuradaSituacao`, aplicada às categorias aditivas.

- **C55 — estabilidade do eixo dos inteiros**: reduzir o valor relativo desloca apenas o ponto azul, sem recalcular a escala do eixo.

- **C67 - estado semântico compartilhado:** todas as ações de modelagem, edição, arraste, exclusão, eixos e protocolos passam a convergir para um estado semântico único, usado para atualizar consistentemente Vergnaud e as representações complementares.

## C73 — Relato de problemas pelo usuário
- botão discreto **Reportar bug** no menu principal;
- campo de texto para descrever o que estava sendo feito quando ocorreu o problema;
- registro local separado dos logs de interação e da curadoria;
- inclusão automática do contexto da atividade no arquivo de relato.

## C80 — Alteração do destinatário dos relatos de bug
- o e-mail preparado pelo botão **Reportar bug** passa a ser destinado a `anaemilia@gmail.com`;
- o registro local e o contexto automático foram preservados.

## C89 — Preenchimento direto do Gmail nos relatos de bug
- o botão **Reportar bug** abre o compositor do Gmail Web no navegador;
- destinatário, assunto e corpo são enviados diretamente ao Gmail;
- o `mailto:` permanece como contingência;
- a cópia local do relato continua sendo gravada.

## C93 — Inicialização sem texto e sem diagramas
- a tela principal inicia com a área de trabalho vazia;
- nenhuma situação-problema ou representação é carregada antes da escolha explícita da categoria;
- a seleção de uma categoria carrega o enunciado e os diagramas correspondentes;
- o botão **Sortear** e os controles contextuais permanecem desabilitados ou ocultos até a escolha da categoria.

## C113 — Affordance de pickup
Cursores de mão aberta/fechada, escala discreta de 106%, elevação, sombra e prioridade visual para os elementos manipuláveis, sem alteração das regras semânticas ou de sincronização.

## C116 — Origem visível e seguimento elástico
- contorno fantasma tracejado permanece no local inicial enquanto o elemento está em trânsito;
- movimento acompanha o cursor por mola amortecida com atraso máximo discreto;
- a soltura conclui na coordenada exata antes da validação, preservando precisão e semântica;
- implementação por contratos, herança e polimorfismo no módulo `Scaffolding/arraste`.

## C117 — Enunciado sincronizado com os diagramas
- números, interrogação e marcadores deslocados na área do texto passam a refletir o estado semântico compartilhado;
- o valor original da incógnita permanece preservado para logs e validações;
- pontuação e deslocamentos realizados pelo usuário são mantidos;
- itens já posicionados nas representações formais continuam sob as regras próprias dos diagramas;
- implementação por contratos, herança e polimorfismo no módulo `campoaditivo/sincronizacao/texto`.


## C118 — Interrogação preservada no enunciado
- a sincronização textual continua atualizando somente os valores conhecidos;
- a interrogação original permanece `?`, mesmo quando o estado compartilhado já contém um resultado para seu papel semântico;
- a regra vale tanto para a interrogação na frase quanto para a interrogação deslocada dentro da área do enunciado;
- o papel semântico da incógnita continua vinculado ao estado compartilhado, sem transformar o marcador visual em resposta automática;
- implementação polimórfica pelo contrato `ElementoSemanticoTexto.representaIncognitaOriginal()`.

## C119 — Adição de quadradinhos condicionada à modelagem em Vergnaud
- o controle `+` permanece bloqueado até que os três elementos semânticos ativos sejam explicitamente posicionados no diagrama de Vergnaud;
- a interrogação conta como elemento posicionado e permanece `?`;
- valores inseridos por arraste ou edição direta liberam a etapa, enquanto valores derivados automaticamente não a liberam;
- o estado bloqueado é exibido em cinza, usa cursor padrão e apresenta orientação localizada em português, inglês e francês;
- o limite curado, o incremento unitário e a sincronização entre representações foram preservados;
- implementação por contratos, herança e polimorfismo no módulo `Scaffolding/venn`.

## C125 — Refatoração semântica e redução de responsabilidades
- mapeamento entre índices e papéis semânticos centralizado em `CatalogoPapeisSemanticosAditivos`;
- natureza dos valores explicitada por `NaturezaPapelAditivo`, distinguindo quantidades de transformações e relações;
- regra de não negatividade centralizada em `PoliticaValoresAditivos` e reutilizada pelo estado compartilhado, pelo scaffolding reativo e pela tela;
- `ScaffoldingQuestionamento` deixa de manter switches próprios e delega ao catálogo semântico;
- temporização do tip extraída para `ControladorAnotacaoTemporaria`;
- cálculo do elemento dependente da relação extraído para `ScaffoldingReacaoRepresentacoes`;
- teste específico acrescentado para todas as categorias, incluindo relações e transformações negativas válidas;
- dados curados, sincronização, controles de unidades, arraste físico e internacionalização preservados.

## C134 — Conclusão da modelagem com destaque azul e escolha de continuidade
- Detecta a conclusão quando todos os elementos matemáticos imersos no texto estão posicionados em papéis semânticos compatíveis.
- Aplica destaque azul Gérard aos elementos, conectores e itens do diagrama de Vergnaud.
- Exibe tip não modal com a mensagem “Parabéns! Podemos passar para a próxima tarefa?” e radio buttons Sim/Não.
- Sim reutiliza o fluxo consolidado de Sortear; Não mantém a situação e o destaque.
- A conclusão é suspensa durante nova manipulação e reavaliada na soltura.
- Acrescenta teste específico e contratos à matriz de regressão.

## C135 — Opção Sim aciona Sortear e preserva a categoria
- O radio button **Sim** do tip de conclusão passa a executar diretamente `botaoSortear.doClick()`.
- Foi removido o fluxo paralelo que chamava `iniciarNovaAtividade(...)` diretamente pelo tip.
- A nova tarefa permanece obrigatoriamente em `tipoSituacaoSelecionada`, reutilizando o comportamento consolidado do botão **Sortear**.
- A reinicialização da modelagem continua ocorrendo no fluxo único de carregamento da nova atividade.
- Foi acrescentado teste funcional que confirma o acionamento do botão e a preservação da categoria.

## C139 — roteiro visual de conclusão por categoria
- Inclusão de `ROTEIRO_TESTE_VISUAL_CONCLUSAO_POR_CATEGORIA.md`.
- Inclusão de casos visuais obrigatórios para composição, comparação e transformação.
- Inclusão de verificador estrutural do roteiro e da matriz de regressão.

## C143
- Tabuleiro concreto `Antes → Mudança → Depois` para Transformação de medidas.
- Unidades manipuláveis também na transformação, com preservação do sinal.
- Sincronização com texto, Vergnaud, eixo e controles consolidados.

## C147 - Modelo semântico explícito

- universos `NATURAIS` e `INTEIROS` incorporados aos objetos numéricos;
- incógnita preserva o domínio esperado;
- contexto, personagens e pistas linguísticas modelados como conceitos estáveis;
- categorias simples e compostas agregam papéis e relações;
- perfis anteriores passam a ser derivados;
- artigo e análise de complexidade atualizados.

## C148 - Processo de transformação

- canal de passagem entre estado inicial e estado final;
- funil superior para inserção e funil inferior para retirada;
- quadradinhos do Gérard substituem partículas circulares;
- comportamento visual derivado do número inteiro da transformação;
- sincronização com texto, Vergnaud, eixo e controles preservada.

- [C149](CHANGELOG_C149.md) — processo de transformação sem view legada.

## C150 — Funil estrutural e validação semântica

A representação de transformação passa a apresentar o funil antes da definição do valor, mantendo os controles bloqueados até o primeiro posicionamento no Vergnaud. Os controles da transformação ficam junto ao funil. O tip persistente de erro é removido quando o usuário corrige o item para o papel semântico adequado.

## C151 — Quadradinhos compactos no funil

- redução apenas dos quadradinhos da transformação;
- contêineres laterais preservados;
- magnitude, sinal e sincronização mantidos.

## C152 — Rótulos semânticos e internacionalização

- a segunda parte da composição não pode ficar sem papel semântico;
- normalização aplicada às cenas formal e complementar;
- Entraram/Saíram localizados em português, inglês e francês;
- teste específico integrado à regressão principal.

## C154 — Comparação entre categorias habilitada

- Opção do menu ativada e conectada à tela comparativa existente.
- Estado “Em construção” removido apenas dessa opção.
- Regra de habilitação/acionamento isolada em `gerard.ui.menu.ConfiguradorOpcaoComparacaoCategorias`.
- Teste automatizado específico adicionado.

## C155 — Conclusão progressiva padronizada

- selo discreto “Modelagem concluída” antes do tip;
- pausa não modal de aproximadamente 1,15 segundo;
- tip posterior com Sim/Não;
- cancelamento da sequência ao reabrir a modelagem;
- aplicação comum às oito categorias de Vergnaud;
- temporização, estado e componente visual isolados em `gerard.ui.conclusao`.

## C156 — sincronização de valores monetários integrais
- Valores como `25,00` e `-18,00` passam a alimentar o estado semântico compartilhado.
- O posicionamento no Vergnaud volta a criar imediatamente os quadradinhos no processo de transformação.
- Conversão numérica centralizada em `gerard.semantica.numero`.
- Valores fracionários não inteiros continuam rejeitados sem truncamento.

## C157 — grandezas quantitativas contextuais

- Separação entre contagem e dinheiro.
- Modelo monetário decimal exato.
- Escala monetária agrupada com legenda.
- Compatibilidade com o estado inteiro consolidado sem truncar centavos.
- Teste: `scripts/testar_grandezas_quantitativas_contextuais.sh`.

## C158 — ciclo generalizado da incógnita

- a interrogação deve ser posicionada no papel correto antes do preenchimento;
- a tarefa entra em fase de espera até o usuário usar o protocolo mouse/texto;
- cálculos e sincronizações automáticas preservam `?` antes dessa ação;
- a conclusão só ocorre após o item originalmente desconhecido receber um número pelo protocolo;
- regra aplicada a todos os papéis e categorias.

## C161 — Janela maximizada e comparação responsiva

- Gérard inicia maximizado, sem tela cheia exclusiva.
- Comparação entre categorias dimensionada proporcionalmente à janela principal e à área útil do monitor.
- Último tamanho do diálogo preservado durante a sessão.

## C162 — distribuição trapezoidal dos quadradinhos no funil

Os quadradinhos da transformação passam a respeitar as laterais inclinadas do funil, com linhas centralizadas e progressivamente menores em direção ao gargalo.

## C165 — escolha obrigatória do sinal na curadoria

- `transformacao` e `valor_relativo` exigem escolha explícita de sinal;
- magnitude e sinal são editados separadamente;
- `?` preservado com sinal semântico obrigatório;
- traduções herdam e bloqueiam a semântica;
- implementação isolada em `gerard.campoaditivo.curadoria.sinal`.
