# Catálogo de mensagens de erro e aviso exibidas ao usuário

Este documento lista as mensagens de erro, aviso e validação que o Gerard exibe durante o uso, organizadas por área da aplicação. O texto-fonte de todas elas vive em `src/gerard/i18n/mensagens_pt.properties` (com equivalentes em `mensagens_en.properties`, `mensagens_es.properties` e `mensagens_fr.properties`).

**Escopo:** este catálogo cobre mensagens que aparecem como retorno direto de uma ação do usuário (diálogo, aviso inline, tooltip de erro) — não inclui os rótulos, títulos de coluna e textos explicativos do painel de pesquisador (`pesq.tab.*`, `pesq.col.*`, `pesq.d3.explain.*`, `pesq.chart.*`, etc.), que são conteúdo descritivo de uma ferramenta de análise de dados, não mensagens de erro em si — ainda que muitas delas *falem sobre* erros como fenômeno estudado (frequência de erro, padrões de erro/feedback no log). As poucas exceções técnicas desse painel (falha ao rodar inferência, falha ao abrir pasta, falha ao carregar visualização) estão listadas na seção 7, por serem falhas reais de execução, não texto analítico.

---

## 1. Interpretação da situação-problema (todo usuário)

Fonte: `gerard.interpretacao.servico.InterpretadorLinguistico` — avisos gerados ao interpretar o enunciado digitado/selecionado.

| Chave | Mensagem |
|---|---|
| `warning.category_undefined` | Categoria não identificada pelas regras atuais. |
| `warning.incomplete_relation` | Situação possivelmente incompleta: há relação, mas poucos valores numéricos. |
| `warning.incomplete_problem` | Problema possivelmente incompleto: foram encontrados menos de dois valores numéricos. |
| `warning.multiple_categories` | Há pistas de mais de uma categoria; a interpretação deve ser confirmada pelo usuário. |
| `ui.problem.noCuratedProblem` | Nenhuma situação-problema curada está disponível para esta categoria. Valide uma situação na aba Curadoria e salve os metadados curados. |
| `ui.problemLanguage.unavailable` | Esta situação-problema não possui outras versões linguísticas validadas. |

---

## 2. Interação com o diagrama (arraste, valores, sinal)

Validações disparadas ao posicionar elementos ou editar valores no diagrama de Vergnaud e representações complementares (Venn, eixo dos inteiros, barras de comparação).

| Chave | Mensagem |
|---|---|
| `ui.warning.negativeQuantity.title` | Quantidade inválida |
| `ui.warning.negativeQuantity.message` | Essa escolha produziria uma quantidade negativa. Somente a transformação ou o valor relativo pode ter sinal; referido, referendo, estados, partes e todo devem permanecer em zero ou acima. |
| `ui.tooltip.negativeQuantity` | Essa mudança no valor relativo tornará uma quantidade negativa. Quantidades não podem ser negativas. |
| `ui.dialog.invalidValue` | Valor inválido. Digite apenas um número inteiro, como 14. |
| `ui.dialog.invalidTitle` | Entrada inválida |
| `ui.compare.invalidValue` | Valor inválido. |
| `ui.question.semanticMismatch` | Tem certeza que esse número corresponde ao {0} da {1}? |
| `ui.question.valueMismatch` | Tem certeza que esse é o valor do {0}? |
| `ui.tooltip.venn.semanticLimitReached` | Foi atingido o limite da quantidade curada para esta representação. |
| `ui.tooltip.venn.minimumReached` | A quantidade não pode ser reduzida abaixo de zero. |
| `ui.tooltip.venn.integerLimitReached` | Foi atingido o limite para este valor. |
| `ui.unknown.placeBeforeEdit` | Posicione primeiro a interrogação no papel correto e depois use o protocolo de mouse/texto para preenchê-la. |

---

## 3. Aba "Construir situação-problema" (montagem)

| Chave | Mensagem |
|---|---|
| `montagem.no.curated` | Nenhuma situação curada e completa está disponível para esta atividade. |
| `montagem.no.curated.help` | A construção requer uma situação validada de composição, transformação ou comparação, com os três valores do diagrama preenchidos. |
| `montagem.feedback.review` | Revise a seleção e a ordem dos blocos. Valores iguais podem representar relações diferentes. |

*(`montagem.feedback.restarted` e `montagem.feedback.correct` são feedback neutro/de sucesso, não de erro.)*

---

## 4. Cadastro e seleção de usuário

| Chave | Mensagem |
|---|---|
| `ui.userDialog.selectFirst` | Selecione um usuário na lista. |
| `ui.userDialog.nameRequired` | Digite o nome para cadastrar. |
| `ui.userDialog.photo.invalid` | Não foi possível abrir esse arquivo como imagem. |

---

## 5. Reportar bug

| Chave | Mensagem |
|---|---|
| `ui.bug.required` | Descreva o problema antes de registrar o relato. |
| `ui.bug.error` | Não foi possível registrar o relato: {0} |
| `ui.bug.emailUnavailable` | O relato foi salvo localmente, mas não foi possível abrir um aplicativo de e-mail. Envie-o manualmente para {1}. Cópia local: {0} |

---

## 6. Análise da modelagem (formulário do pesquisador durante a sessão)

| Chave | Mensagem |
|---|---|
| `analise.unavailable` | Não há uma situação curada ativa para analisar. |
| `analise.requires.positioning` | A análise da modelagem fica disponível após pelo menos um posicionamento no diagrama de Vergnaud. |
| `analise.error` | Não foi possível salvar a análise: {0} |

---

## 7. Curadoria e acesso do pesquisador (área restrita)

Fonte: `gerard.campoaditivo.curadoria.sinal.PoliticaSinalCuradoria`, `gerard.campoaditivo.curadoria.AvisoTermoDesconhecidoVazio`, `gerard.pesquisador.TelaVisaoPesquisador`, `gerard.pesquisador.visualizacao.PainelD3WebView`, `gerard.pesquisador.log.LoggerInteracaoGerard`.

| Chave | Mensagem |
|---|---|
| `ui.researcher.password.invalid` | Senha incorreta. A curadoria permanece bloqueada. |
| `pesq.password.wrong` | Senha incorreta. |
| `curadoria.idioma.invalido` | Idioma inválido |
| `curadoria.idioma.jaCadastrado` | O idioma {0} já está cadastrado e foi selecionado. |
| `curadoria.traducao.avisoNaoValidada` | A tradução em {0} ainda não foi marcada como validada.\nComo deseja inseri-la? |
| `curadoria.semantica.somenteOriginal` | Os dados semânticos são herdados da versão original; nesta tradução, edite apenas o enunciado e sua validação. |
| `curadoria.termoDesconhecido.avisoVazio` | Atenção: o termo desconhecido está vazio. Selecione o papel ocupado pela interrogação (?). |
| `curadoria.termoDesconhecido.avisoPreenchidoAutomaticamente` | Atenção: o termo desconhecido estava vazio e foi preenchido automaticamente como '{0}'. Revise a seleção. |
| `curadoria.sinal.obrigatorio` | Escolha o sinal de {0} antes de salvar. |
| `curadoria.sinal.zeroExigeNeutro` | O valor zero de {0} deve usar a opção "0 Neutro". |
| `curadoria.sinal.neutroExigeZero` | A opção "0 Neutro" de {0} só pode ser usada quando a magnitude for zero ou desconhecida (?). |
| `curadoria.sinal.revisarAntesSalvar` | Escolha os sinais obrigatórios para concluir o salvamento. |
| `pesq.model.error` | Erro ao rodar inferência: {0} |
| `pesq.model.noUsers` | Nenhum perfil cadastrado ainda - cadastre um usuário no botão de usuário da tela principal. |
| `pesq.error.openFolder` | Não foi possível abrir a pasta: {0} |
| `pesq.d3.fallback.title` | Visualização D3 em WebView indisponível |
| `pesq.d3.fallback.message` | O Gerard tentou carregar as visualizações D3.js dentro da janela por meio do JavaFX WebView. Este ambiente de execução não disponibilizou JavaFX/WebView no classpath. Os dados e o HTML D3 foram gerados normalmente; use o botão abaixo para abrir a mesma visualização no navegador. |
| `pesq.d3.fallback.browserUnsupported` | O HTML D3 foi gerado, mas este ambiente não permitiu abrir o navegador automaticamente. Abra manualmente o arquivo: {0} |
| `pesq.d3.fallback.openError` | Não foi possível abrir a visualização D3 no navegador. Detalhes: {0} |
| `pesq.log.error.seed` | Nao foi possivel criar log inicial dos quadros: {0} |
| `pesq.log.error.init` | Nao foi possivel inicializar log do Gerard: {0} |
| `pesq.log.error.write` | Nao foi possivel registrar log do Gerard: {0} |
| `pesq.log.error.read` | Nao foi possivel ler log {0}: {1} |

---

## Observações

- Todas as mensagens existem em 4 idiomas (`mensagens_pt/en/es/fr.properties`); este catálogo documenta apenas a versão em português — as demais seguem a mesma chave.
- As mensagens de log técnico (`pesq.log.error.*`) usam "Nao" e "possivel" sem acento no arquivo-fonte — mantido aqui fielmente como está no `.properties`, não é erro de transcrição deste catálogo.
- Diferente da versão anterior do Gerard (a que foi estabilizada para o repositório institucional da UNIVASF), aqui não há mensagens de "apague o banco manualmente" — as falhas de log/persistência (seção 7) reportam a exceção original via `{0}`, sem instruir o usuário a apagar arquivos.
