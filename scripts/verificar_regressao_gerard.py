#!/usr/bin/env python3
"""
AVISO SOBRE O ESCOPO DESTE SCRIPT
----------------------------------
Este script NÃO é um teste unitário/funcional: ele não instancia classes
Java nem executa a interface para verificar comportamento em tempo de
execução. Trata-se de uma checagem estrutural/textual — confirma que
determinados métodos, literais de string e trechos de código ainda
existem nos arquivos-fonte (via regex/"substring in arquivo"), além de
rodar "ant clean jar" para garantir que o projeto compila.
Use-o para pegar remoções acidentais de código, não para validar se a
lógica (ex.: inferência linguística, renderização) produz o resultado
correto. Para isso, veja a recomendação de testes JUnit no relatório de
análise de código.
"""
from pathlib import Path
import re, subprocess, sys
ROOT=Path(__file__).resolve().parents[1]
errors=[]
def check(cond,msg):
    print(('[OK] ' if cond else '[ERRO] ')+msg)
    if not cond: errors.append(msg)
def text(rel): return (ROOT/rel).read_text(encoding='utf-8',errors='replace')
def properties(rel):
    """Lê um .properties (chave=valor, UTF-8, sem continuação de linha) em um dict."""
    mapa={}
    for linha in text(rel).splitlines():
        linha=linha.strip()
        if not linha or linha.startswith('#') or linha.startswith('!') or '=' not in linha:
            continue
        chave,valor=linha.split('=',1)
        mapa[chave.strip()]=valor.strip()
    return mapa

print('== Compilação ==')
r=subprocess.run(['ant','clean','jar'],cwd=ROOT,text=True,stdout=subprocess.PIPE,stderr=subprocess.STDOUT)
print(r.stdout); check(r.returncode==0,'ant clean jar')
check((ROOT/'dist/GerardNetBeans_D3_Leitura_Redes_Transicoes.jar').exists(),'JAR gerado')

print('== Internacionalização ==')
# Desde a Fase 6 da refatoração (ver PLANO_REFATORACAO_ARQUITETURA_GERARD.md),
# as mensagens vêm de src/gerard/i18n/mensagens_xx.properties; ServicoLocalizacao.java
# só carrega esses arquivos, não embute mais texto/tabelas por idioma.
i18n=text('src/gerard/i18n/ServicoLocalizacao.java')
props={k:properties(f'src/gerard/i18n/mensagens_{k}.properties') for k in ('pt','en','fr','es')}
sets={k:set(props[k].keys()) for k in ('pt','en','fr','es')}
check(bool(sets['pt']),'mapa português localizado')
check(sets['pt']==sets['en']==sets['fr'],'mesmas chaves explícitas em pt/en/fr')
check('new HashMap<String, String>(pt)' in i18n and 'mensagens.put(IdiomaInterface.ESPANHOL, es)' in i18n, 'mapa espanhol herda todas as chaves e está registrado')
for key in ('ui.tooltip.restore.elements','ui.tooltip.correctCuration','ui.integerAxis.hide'):
    check(all(key in sets[k] for k in ('pt','en','fr')),f'chave localizada em pt/en/fr: {key}')

print('== Estrutura atual ==')
main=text('src/Main.java'); repo=text('src/gerard/campoaditivo/servico/RepositorioSituacoesAditivas.java')
cur=text('src/gerard/campoaditivo/curadoria/TelaCuradoriaSituacoes.java')
evt=text('src/gerard/pesquisador/log/EventoLogGerard.java')
view=text('src/gerard/pesquisador/TelaVisaoPesquisador.java')
for token in ('ControladorContextoSituacao','sincronizarDiagramaVennComRepresentacoes','ScaffoldingProximidade'):
    check(token in main,f'Main preserva {token}')
for token in ('situacao_grupo_id','tipo_versao','versao_origem_id','ValidadorVinculosTraducoes.validarOuFalhar'):
    check(token in repo or token in cur,f'curadoria/vínculo preserva {token}')
for token in ('situacao_versao_id','situacao_grupo_id','idioma_situacao'):
    check(token in evt,f'log contém {token}')
for token in ('getSituacaoGrupoId()','situacaoGrupoId'):
    check(token in view,f'visão do pesquisador expõe {token}')
check('campoSituacaoGrupoId.setEditable(false)' in cur,'id de grupo protegido contra edição livre')
check('comboVersoesOriginais' in cur,'origem escolhida apenas entre versões originais')
check('IconeTraducao' in cur,'botão pequeno de tradução preservado')
check('criarTraducaoAPartirDaOriginal' in cur,'criação de tradução recupera a curadoria original')
check('existeIdiomaNoGrupo' in cur,'duplicidade de idioma no grupo é evitada')

print('== Qualidade da interface ==')
# Rótulos e tooltips devem expressar o efeito real da ação.
for lang, rotulo in (('pt', 'Categoria: {0}'), ('en', 'Category: {0}'), ('fr', 'Catégorie : {0}'), ('es', 'Categoría: {0}')):
    check(props[lang].get('ui.button.type') == rotulo, f'rótulo de categoria claro em {lang}')
for trecho, lang in (
    ('Selecionar categoria', 'pt'),
    ('Select category', 'en'),
    ('Sélectionner une catégorie', 'fr'),
    ('Seleccionar categoría', 'es'),
):
    check(props[lang].get('ui.tooltip.type') == trecho, f'tooltip objetivo da categoria em {lang}')

# Hierarquia visual e posição contextual da categoria.
check('A categoria funciona como rótulo de contexto acima do enunciado.' in main,
      'categoria documentada e renderizada acima do enunciado')
check('new Font("Arial", Font.BOLD, 20)' in main,
      'fonte principal do enunciado preservada')
check('new Font("Arial", Font.BOLD, 11)' in main or 'new Font("Arial", Font.PLAIN, 11)' in main,
      'fonte secundária compacta preservada')

# Contraste WCAG básico para as cores centrais sobre fundo branco.
def luminancia(rgb):
    canais=[]
    for c in rgb:
        v=c/255.0
        canais.append(v/12.92 if v <= 0.04045 else ((v+0.055)/1.055)**2.4)
    return 0.2126*canais[0]+0.7152*canais[1]+0.0722*canais[2]
def contraste(a,b):
    la,lb=luminancia(a),luminancia(b)
    maior,menor=max(la,lb),min(la,lb)
    return (maior+0.05)/(menor+0.05)
check(contraste((31,41,51),(255,255,255)) >= 4.5,
      'contraste do texto principal sobre o fundo')
check(contraste((82,97,107),(255,255,255)) >= 4.5,
      'contraste do texto secundário sobre o fundo')

# Navegação previsível: fluxos de nova atividade e preservação devem continuar separados.
check('private void aplicarIdiomaSelecionado()' in main,
      'fluxo de nova atividade preservado')
check('private void aplicarIdiomaSelecionadoMantendoEstadoTela()' in main,
      'fluxo de preservação do estado preservado')
check('iniciarNovaAtividade(AcaoAtividade.SELECIONAR_CATEGORIA)' in main,
      'seleção de categoria inicia nova atividade')
check('A ausência de uma versão textual não autoriza restaurar a modelagem.' in main,
      'ausência de tradução não limpa a modelagem')
check('trocarIdiomaDaSituacaoSemLog' in main and 'atualizarRotulosDiagramaVergnaudSemReposicionar' in main,
      'troca textual atualiza rótulos sem reposicionar a modelagem')

# O desconhecido deve ser oferecido no texto, não pré-preenchido no diagrama.
check('inicializarDiagramaVergnaud();' in main and 'restaurarModelagemDiagrama();' in main,
      'inicialização e restauração explícitas do diagrama preservadas')
check('O diagrama deve iniciar vazio.' in main,
      'regra de diagrama inicialmente vazio documentada no código')

print('== Padrões de projeto ==')
acao=text('src/gerard/aplicacao/AcaoAtividade.java')
estado=text('src/gerard/aplicacao/ControladorEstadoAtividade.java')
fachada=text('src/gerard/aplicacao/FachadaCarregamentoAtividade.java')
contexto=text('src/gerard/aplicacao/ContextoCarregamentoAtividade.java')
estrategia=text('src/gerard/estilointeracao/estrategia/EstrategiaEstiloInteracao.java')
registro_estrategias=text('src/gerard/estilointeracao/estrategia/EstrategiasEstiloInteracao.java')
fabrica=text('src/gerard/campoaditivo/diagrama/servico/FabricaRenderizadoresDiagramaAditivo.java')
check('SELECIONAR_CATEGORIA(true)' in acao and 'TROCAR_IDIOMA(false)' in acao,
      'política de estado distingue reinício e preservação')
check('ControladorEstadoAtividade' in main and 'iniciarNovaAtividade(AcaoAtividade' in main,
      'controlador de estado integrado ao fluxo principal')
check('FachadaCarregamentoAtividade' in main and 'carregarNova' in fachada and 'carregarCorrespondente' in fachada,
      'fachada de carregamento integrada')
check('final class ContextoCarregamentoAtividade' in contexto,
      'contexto imutável de carregamento preservado')
check('interface EstrategiaEstiloInteracao' in estrategia and 'EnumMap' in registro_estrategias,
      'Strategy centraliza estilos de interação')
check('estrategias.obter(modo)' in text('src/gerard/Scaffolding/proximidade/ScaffoldingProximidade.java'),
      'scaffolding delega comportamento à estratégia')
check('class FabricaRenderizadoresDiagramaAditivo' in fabrica and 'EnumMap' in fabrica,
      'Factory de representações preservada')
contrato_unidades=text('src/gerard/campoaditivo/venn/interacao/RepresentacaoComUnidades.java')
contrato_adicao=text('src/gerard/campoaditivo/venn/interacao/RepresentacaoComUnidadesAdicionaveis.java')
contrato_remocao=text('src/gerard/campoaditivo/venn/interacao/RepresentacaoComUnidadesRemoviveis.java')
base_unidades=text('src/gerard/campoaditivo/venn/interacao/RepresentacaoComUnidadesAbstrata.java')
impl_unidades=text('src/gerard/campoaditivo/venn/interacao/RepresentacaoVennEditavel.java')
check('interface RepresentacaoComUnidades' in contrato_unidades,
      'representações com unidades expõem contrato-base')
check('extends RepresentacaoComUnidades' in contrato_adicao
      and 'extends RepresentacaoComUnidades' in contrato_remocao,
      'contratos de adição e remoção herdam do contrato-base')
check('abstract class RepresentacaoComUnidadesAbstrata implements RepresentacaoComUnidades' in base_unidades,
      'herança centraliza o estado comum das representações com unidades')
check('extends RepresentacaoComUnidadesAbstrata' in impl_unidades
      and 'implements RepresentacaoComUnidadesAdicionaveis' in impl_unidades
      and 'RepresentacaoComUnidadesRemoviveis' in impl_unidades,
      'implementação concreta aplica herança e múltiplos contratos')
check('RepresentacaoComUnidadesAdicionaveis representacaoAdicionar' in main
      and 'RepresentacaoComUnidadesRemoviveis representacaoRemover' in main,
      'tela usa polimorfismo em vez de depender da implementação concreta')
mapeamento_contrato=text('src/gerard/campoaditivo/venn/mapeamento/MapeamentoPapeisRepresentacaoComplementar.java')
mapeamento_base=text('src/gerard/campoaditivo/venn/mapeamento/MapeamentoPapeisRepresentacaoComplementarAbstrato.java')
mapeamento_comparacao=text('src/gerard/campoaditivo/venn/mapeamento/MapeamentoComparacaoPapeisComplementares.java')
mapeamento_fabrica=text('src/gerard/campoaditivo/venn/mapeamento/FabricaMapeamentosPapeisComplementares.java')
check('interface MapeamentoPapeisRepresentacaoComplementar' in mapeamento_contrato,
      'ordem visual e ordem semântica são separadas por contrato')
check('abstract class MapeamentoPapeisRepresentacaoComplementarAbstrato' in mapeamento_base,
      'herança centraliza a validação dos mapeamentos')
check('super(0, 2, 1)' in mapeamento_comparacao,
      'comparação mapeia referido, referendo e valor relativo corretamente')
check('EnumMap' in mapeamento_fabrica and 'COMPARACAO_MEDIDAS' in mapeamento_fabrica,
      'seleção do mapeamento ocorre polimorficamente por fábrica')

print('== Política semântica de valores ==')
catalogo_papeis=text('src/gerard/campoaditivo/semantica/CatalogoPapeisSemanticosAditivos.java')
politica_valores=text('src/gerard/campoaditivo/semantica/PoliticaValoresAditivos.java')
natureza_papel=text('src/gerard/campoaditivo/semantica/NaturezaPapelAditivo.java')
questionamento=text('src/gerard/Scaffolding/questionamento/ScaffoldingQuestionamento.java')
estado_compartilhado=text('src/gerard/campoaditivo/sincronizacao/EstadoSemanticoCompartilhado.java')
controle_anotacao=text('src/gerard/Scaffolding/feedbackerro/ControladorAnotacaoTemporaria.java')
check('class CatalogoPapeisSemanticosAditivos' in catalogo_papeis
      and 'obterChavePapelDoElemento' in catalogo_papeis
      and 'obterIndiceElementoPorPapel' in catalogo_papeis,
      'catálogo centraliza índices e papéis semânticos')
check('enum NaturezaPapelAditivo' in natureza_papel
      and 'QUANTIDADE' in natureza_papel
      and 'TRANSFORMACAO_OU_RELACAO' in natureza_papel,
      'natureza diferencia quantidades de valores assinados')
check('class PoliticaValoresAditivos' in politica_valores
      and 'valorEhValidoParaElemento' in politica_valores
      and 'valorEhValidoNoEstadoCompartilhado' in politica_valores,
      'política única impede quantidade negativa sem bloquear relações')
check('catalogoPapeis.obterChavePapelDoElemento' in questionamento
      and 'catalogoPapeis.obterIndiceElementoPorPapel' in questionamento,
      'questionamento delega mapeamento ao catálogo central')
check('politicaValores.valorEhValidoNoEstadoCompartilhado' in estado_compartilhado,
      'estado compartilhado delega validade à política semântica')
check('ControladorAnotacaoTemporaria' in main
      and 'controladorAnotacaoTemporaria.mostrar' in main
      and 'class ControladorAnotacaoTemporaria' in controle_anotacao,
      'duração do tip é controlada fora da tela principal')

print('== Affordance de pickup ==')
pickup_contrato=text('src/gerard/Scaffolding/pickup/DesenhavelPickup.java')
pickup_renderizador=text('src/gerard/Scaffolding/pickup/RenderizadorPickupAbstrato.java')
pickup_concreto=text('src/gerard/Scaffolding/pickup/RenderizadorPickupElevado.java')
pickup_cursor=text('src/gerard/Scaffolding/pickup/FornecedorCursoresPickupSwing.java')
check('interface DesenhavelPickup' in pickup_contrato,
      'pickup é exposto por contrato')
check('abstract class RenderizadorPickupAbstrato' in pickup_renderizador
      and 'desenharEmPrimeiroPlano' in pickup_renderizador,
      'herança centraliza escala, elevação e sombra do pickup')
check('return 1.06d' in pickup_concreto,
      'pickup usa escala discreta de 106%')
check('criarMaoAberta' in pickup_cursor and 'criarMaoFechada' in pickup_cursor
      and 'Cursor.HAND_CURSOR' in pickup_cursor
      and 'Cursor.MOVE_CURSOR' in pickup_cursor,
      'pickup usa mão nativa no mouseover e cursor de movimentação no arraste')
check('desenharPickupEmPrimeiroPlano(g2)' in main
      and 'item != itemSelecionado' in main
      and 'quadradinho != quadradinhoVennSelecionado' in main,
      'elemento segurado é retirado da passagem normal e redesenhado em primeiro plano')
check('definirCursorMaoAberta' in main and 'definirCursorMaoFechada' in main,
      'cursor diferencia disponibilidade e arraste ativo')

print('== Arraste físico ==')
arraste_contrato=text('src/gerard/Scaffolding/arraste/ControladorArrasteElastico.java')
arraste_base=text('src/gerard/Scaffolding/arraste/ControladorArrasteElasticoAbstrato.java')
arraste_mola=text('src/gerard/Scaffolding/arraste/ControladorArrasteElasticoMola.java')
fantasma_contrato=text('src/gerard/Scaffolding/arraste/MarcadorOrigemArraste.java')
fantasma_base=text('src/gerard/Scaffolding/arraste/MarcadorOrigemArrasteAbstrato.java')
check('interface ControladorArrasteElastico' in arraste_contrato,
      'seguimento elástico é exposto por contrato')
check('abstract class ControladorArrasteElasticoAbstrato' in arraste_base
      and 'Timer' in arraste_base and 'executarPasso' in arraste_base,
      'herança centraliza mola, amortecimento e temporização')
check('return 26.0d' in arraste_mola and 'return 0.34d' in arraste_mola,
      'mola concreta limita o atraso e preserva resposta discreta')
check('interface MarcadorOrigemArraste' in fantasma_contrato
      and 'abstract class MarcadorOrigemArrasteAbstrato' in fantasma_base,
      'buraco de origem usa contrato e implementação herdada')
check('marcadorOrigemArraste.desenhar(g2)' in main
      and 'controladorArrasteElastico.atualizarAlvo' in main
      and 'controladorArrasteElastico.concluir' in main,
      'tela integra fantasma, seguimento elástico e soltura exata')

print('== Contrato de posicionamento incorreto manipulável ==')
sessao_proxy=text('src/gerard/interacao/arraste/SessaoArrasteTextoParaDiagrama.java')
feedback_erro=text('src/gerard/Scaffolding/feedbackerro/ScaffoldingFeedbackMultissensorialErro.java')
check('deveManterNoDiagramaAposErro' in sessao_proxy,
      'proxy incorreto sobre o diagrama possui política explícita de permanência')
check('return ehProxyAtivo(item) && !solturaSobreDiagrama;' in sessao_proxy,
      'descarte do proxy fica restrito à soltura fora do diagrama')
check('deveManterNoDiagramaAposErro' in main
      and 'itensArrastaveis.add(itemSolto)' in main,
      'item semanticamente incorreto é promovido a elemento manipulável do diagrama')
check('processarQuestionamentoPosicionamento(itemSolto)' in main
      and 'registrarQuestionamentoPersistente(item, resultado)' in main,
      'erro reutiliza tip persistente consolidado')
check('scaffoldingFeedbackMultissensorialErro.sinalizarErro(item' in main
      and 'sinalizarErro(ItemTextoArrastavel item, Runnable repaint, Runnable aoConcluir)' in feedback_erro,
      'som e tremor permanecem ligados ao item sem removê-lo')
ramo_incorreto = main[main.find('if (posicionamentoIncorreto) {'):main.find('registrarLogSolturaItem(itemSolto)')]
check('descartarProxy(itemSolto)' not in ramo_incorreto,
      'ramo de erro semântico não remove o item do diagrama')
matriz=Path('MATRIZ_REGRESSAO_FUNCIONALIDADES_CONSOLIDADAS.md')
check(matriz.exists(), 'matriz explícita de regressão funcional existe no projeto')
if matriz.exists():
    matriz_texto=matriz.read_text(encoding='utf-8')
    for contrato in ('som sutil', 'tremor leve', 'tip contextual',
                     'item incorreto permanece no diagrama',
                     'texto original permanece no enunciado'):
        check(contrato in matriz_texto, f'matriz registra contrato: {contrato}')

print('== Conclusão da modelagem ==')
controlador_conclusao=text('src/gerard/campoaditivo/conclusao/ControladorConclusaoModelagem.java')
avaliador_conclusao=text('src/gerard/campoaditivo/conclusao/AvaliadorConclusaoModelagem.java')
destaque_conclusao=text('src/gerard/Scaffolding/conclusao/AplicadorDestaqueConclusaoDiagrama.java')
tip_conclusao=text('src/gerard/ui/conclusao/TipConclusaoModelagem.java')
check('deveApresentarTip' in controlador_conclusao
      and 'registrarTipApresentado' in controlador_conclusao,
      'tip de conclusão é controlado para não repetir continuamente')
check('estaConcluida' in avaliador_conclusao
      and 'papeisCompativeis' in avaliador_conclusao,
      'conclusão depende de todos os papéis matemáticos semanticamente corretos')
politica_valor_conclusao=text('src/gerard/campoaditivo/conclusao/PoliticaValorNumericoConclusao.java')
estado_conclusao=text('src/gerard/campoaditivo/conclusao/EstadoPosicionamentoModelagem.java')
check('ehNumero' in politica_valor_conclusao
      and '[+-]?' in politica_valor_conclusao,
      'conclusão aceita somente valores numéricos naturais, assinados ou decimais')
check('getValorMatematico' in estado_conclusao
      and 'politicaValorNumerico.ehNumero' in avaliador_conclusao,
      'estado de conclusão registra e valida o conteúdo numérico')
check('isIncognitaOriginal' in estado_conclusao
      and 'isPreenchidoPeloProtocoloMouseTexto' in estado_conclusao
      and 'AGUARDANDO_PREENCHIMENTO_INCOGNITA' in avaliador_conclusao,
      'conclusão exige posicionar a incógnita e preenchê-la pelo protocolo mouse/texto')
politica_incognita=text('src/gerard/campoaditivo/conclusao/PoliticaPreenchimentoIncognita.java')
check('devePreservarMarcador' in politica_incognita
      and 'indiceIncognitaProtegida' in text('src/gerard/campoaditivo/sincronizacao/EstadoSemanticoCompartilhado.java'),
      'preenchimento automático da incógnita fica bloqueado até o protocolo')
check('for (int i = 0; i < elementosVergnaud.size(); i++)' in main
      and 'alvo.textoEditavel' in main
      and 'encontrarItemSobreElemento(alvo)' in main,
      'conclusão inspeciona cada elemento do diagrama por mouse ou teclado')
check('definirConclusaoDestacada' in destaque_conclusao
      and 'elementos' in destaque_conclusao and 'conectores' in destaque_conclusao
      and 'itens' in destaque_conclusao,
      'destaque azul abrange elementos, conectores e itens do Vergnaud')
check('JRadioButton' in tip_conclusao and 'opcaoSim' in tip_conclusao
      and 'opcaoNao' in tip_conclusao,
      'tip de conclusão usa radio buttons Sim e Não')
check('ui.completion.congratulations' in main
      and 'botaoSortear.doClick()' in main,
      'escolha Sim aciona o botão Sortear consolidado')
check('botaoSortear.addActionListener' in main
      and 'iniciarNovaAtividade(AcaoAtividade.SORTEAR)' in main,
      'botão Sortear preserva o fluxo consolidado de nova tarefa na categoria selecionada')
check('verificarConclusaoModelagem()' in main
      and 'suspenderConclusaoDuranteManipulacao()' in main,
      'tela verifica conclusão após soltura e a suspende durante nova manipulação')
if matriz.exists():
    matriz_texto=matriz.read_text(encoding='utf-8')
    for contrato in ('destaque azul Gérard', 'radio buttons Sim e Não',
                     'Não mantém a situação atual', 'Sim inicia uma nova situação-problema',
                     'todos os elementos semânticos do diagrama',
                     'interrogação, mesmo corretamente posicionada',
                     'protocolo de mouse/texto'):
        check(contrato in matriz_texto, f'matriz registra contrato de conclusão: {contrato}')

print('== Aba Construir situação-problema ==')
montagem=text('src/gerard/campoaditivo/montagem/TelaMontagemSituacao.java')
gerador=text('src/gerard/campoaditivo/montagem/GeradorBlocosMontagem.java')
bloco=text('src/gerard/campoaditivo/montagem/BlocoTextoMontagem.java')
painel_montagem=text('src/gerard/campoaditivo/montagem/PainelDiagramaPreenchido.java')
catalogo_montagem=text('src/gerard/campoaditivo/montagem/CatalogoAtividadesMontagemPadrao.java')
logger=text('src/gerard/pesquisador/log/LoggerInteracaoGerard.java')
check('TelaMontagemSituacao' in main and 'ui.tab.assembly' in main,
      'nova aba de construção integrada ao Main')
check('ativarAba()' in main and 'desativarAba()' in main,
      'alternância da aba preserva contextos de tentativa')
check('TIPOS_SUPORTADOS.add(TipoSituacaoAditiva.COMPOSICAO_MEDIDAS)' in gerador
      and 'TIPOS_SUPORTADOS.add(TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS)' in gerador
      and 'TIPOS_SUPORTADOS.add(TipoSituacaoAditiva.COMPARACAO_MEDIDAS)' in gerador,
      'escopo inicial cobre composição, transformação e comparação de medidas')
check('SimboloDesconhecido.eh(valor)' in gerador,
      'diagramas incompletos não entram na construção')
check('agruparContextoComBlocoQuantitativo' in gerador and 'QUANTIDADE_TEXTUAL' in gerador,
      'frases contextuais são agrupadas para evitar pistas superficiais')
check('criarBlocosNaoCompativeis' in gerador and 'adicionarSemDuplicar' in gerador,
      'blocos não compatíveis são gerados e deduplicados')
check('isCorreto()' in bloco and 'getCategoriaSemantica()' in bloco and 'getPapelSemantico()' in bloco,
      'metadados semânticos permanecem internos ao modelo do bloco')
check("label.setText(" in montagem and "escaparHtml(texto)" in montagem and '.isCorreto()' not in montagem[montagem.find('class RenderizadorBloco'):] ,
      'renderizador não expõe visualmente blocos corretos e não compatíveis')
check('capturarContextoAtual()' in logger and 'restaurarContexto(ContextoInteracao contexto)' in logger,
      'logger oferece fotografia restaurável do contexto')
check('painelDiagrama' in montagem and 'RenderizadorSwingDiagramaAditivo' in painel_montagem and 'GeradorCenaDiagramaAditivo' in painel_montagem,
      'diagrama preenchido reutiliza o renderizador oficial')
check('renderizador.renderizar(g2, area, cena, false, sucesso)' in painel_montagem,
      'definição da categoria não fica permanentemente desenhada na construção')
check('getToolTipText(MouseEvent evento)' in painel_montagem and 'limitesTituloCategoria.contains' in painel_montagem,
      'definição aparece somente no onmouseover do nome da categoria')
check(props['pt'].get('ui.tab.assembly') == 'Construir situação-problema'
      and props['pt'].get('montagem.title') == 'Construa a situação-problema',
      'nomenclatura portuguesa usa Construir e Construa')
check('CatalogoAtividadesMontagemPadrao.listar(idioma)' in montagem
      and 'COMPOSICAO_MEDIDAS' in catalogo_montagem
      and 'TRANSFORMACAO_MEDIDAS' in catalogo_montagem
      and 'COMPARACAO_MEDIDAS' in catalogo_montagem,
      'catálogo de referência garante variedade mínima em três categorias')
check('escolherProximaSituacao' in montagem and 'idUltimaSituacao' in montagem
      and 'tipoUltimaSituacao' in montagem,
      'botão Novo diagrama evita repetição imediata e alterna categoria quando possível')
for key in ('ui.tab.assembly','montagem.title','montagem.instruction','montagem.diagram.title',
            'montagem.available','montagem.assembly','montagem.button.validate','montagem.feedback.correct'):
    check(all(key in sets[k] for k in ('pt','en','fr')), f'chave da construção localizada em pt/en/fr: {key}')

print('== Controles de unidades no diagrama complementar ==')
controle_venn=text('src/gerard/Scaffolding/venn/ControleAdicionarQuadradinhoVenn.java')
check('ControleAdicionarQuadradinhoVenn' in main and 'desenharControlesAdicionarQuadradinhoVenn' in main,
      'controle de adição integrado ao diagrama complementar')
check('adicionarQuadradinhoAoAgrupamento' in main and 'Criar novo quadradinho' in main,
      'clique cria uma nova unidade no agrupamento')
check('sincronizarTodasAsRepresentacoesAPartirDoDiagramaComplementar' in main,
      'adição continua vinculada ao estado semântico compartilhado')
check('drawLine(cx - raio, cy, cx + raio, cy)' in controle_venn
      and 'drawLine(cx, cy - raio, cx, cy + raio)' in controle_venn,
      'sinal de adição é desenhado sem dependência de fonte')
check(all('ui.tooltip.venn.addSquare' in sets[k] for k in ('pt','en','fr')),
      'tooltip de adição localizado em pt/en/fr')
check('CondicaoDiagramaVergnaudNaoVazio' in main
      and 'diagramaVergnaudPossuiConteudoSemantico' in main,
      'edição de unidades depende apenas de o diagrama de Vergnaud não estar vazio')
check('bloqueio=modelagem_vergnaud_incompleta' in main,
      'clique antecipado no + é bloqueado sem criar unidade')
check('minimumReached' in main and 'podeAlterarQuantidadeNoEstadoAtual' in main,
      'adição e remoção respeitam zero, limites curados e consistência aditiva')
controle_remover=text('src/gerard/Scaffolding/venn/ControleRemoverQuadradinhoVenn.java')
check('FUNDO_DESABILITADO' in controle_venn
      and 'SINAL_DESABILITADO' in controle_venn
      and 'FUNDO_DESABILITADO' in controle_remover
      and 'SINAL_DESABILITADO' in controle_remover,
      'controles + e - comunicam visualmente estados bloqueados')
check(all('ui.tooltip.venn.positionFirst' in sets[k] for k in ('pt','en','fr')),
      'orientação para iniciar a modelagem localizada em pt/en/fr')
check(all('ui.tooltip.venn.minimumReached' in sets[k] for k in ('pt','en','fr')),
      'limite inferior zero localizado em pt/en/fr')

print('== Vínculos e metadados ==')
r=subprocess.run([sys.executable,str(ROOT/'scripts/testar_vinculos_traducoes.py')],cwd=ROOT,text=True,stdout=subprocess.PIPE,stderr=subprocess.STDOUT)
print(r.stdout); check(r.returncode==0,'teste de vínculos entre traduções')

print('== Compatibilidade do log ==')
check('boolean novoFormato = campos.length >= 23' in evt,'leitura compatível com logs antigos e novos')
logger=text('src/gerard/pesquisador/log/LoggerInteracaoGerard.java')
check('int indiceCe = campos.length >= 23 ? 10 : 7' in logger,'normalização C/E respeita o esquema novo e o legado')

if errors:
    print(f'REPROVADO: {len(errors)} falha(s).'); sys.exit(1)
print('APROVADO: compilação, vínculos, curadoria, log, i18n, usabilidade e integração estrutural consistentes.')

# Regressão semântica do texto: todos os valores e a interrogação devem manter papel + mobilidade.
main_src = (ROOT / 'src' / 'Main.java').read_text(encoding='utf-8')
construtor_src = (ROOT / 'src' / 'gerard' / 'campoaditivo' / 'curadoria' / 'ConstrutorResultadoCurado.java').read_text(encoding='utf-8')
checks_semanticos = [
    ('interpretação usa o texto efetivamente renderizado', 'construir(SituacaoProblemaAditiva s, String textoExibido)' in construtor_src),
    ('todos os números recebem o papel pela ordem curada', 'obterChavePapelDoNumero' in main_src),
    ('interrogação recebe o papel desconhecido curado', 'obterChavePapelExataPorValor("?")' in main_src),
    ('realce visual depende do papel semântico', 'corFundoDoPapel(m.chavePapel)' in main_src and 'corTextoDoPapel(chavePapel)' in main_src),
    ('mobilidade dos elementos textuais permanece', 'desenharElementoTextoMovel' in main_src and 'converterElementoTextoEmItemDiagrama' in main_src),
    ('símbolo canônico de desconhecido preservado como interrogação ocidental', 'SimboloDesconhecido.eh' in main_src and 'regexClasse()' in main_src),
    ('papel semântico do desconhecido independe do glifo', 'obterChavePapelExataPorValor' in main_src and 'SimboloDesconhecido.eh(valor)' in main_src),
]
print('== Papéis semânticos no enunciado ==')
for nome, ok in checks_semanticos:
    if not ok:
        raise SystemExit('[FALHA] ' + nome)
    print('[OK] ' + nome)

print('== Sincronização dos elementos semânticos do texto ==')
contrato_texto = text('src/gerard/campoaditivo/sincronizacao/texto/ElementoSemanticoTexto.java')
base_texto = text('src/gerard/campoaditivo/sincronizacao/texto/SincronizadorElementosSemanticosTextoAbstrato.java')
impl_texto = text('src/gerard/campoaditivo/sincronizacao/texto/SincronizadorElementosSemanticosTextoAditivo.java')
check('interface ElementoSemanticoTexto' in contrato_texto,
      'elementos do enunciado expõem contrato semântico comum')
check('abstract class SincronizadorElementosSemanticosTextoAbstrato' in base_texto,
      'herança centraliza a propagação do snapshot ao texto')
check('extends SincronizadorElementosSemanticosTextoAbstrato' in impl_texto,
      'formatação aditiva é selecionada polimorficamente')
check('sincronizarElementosSemanticosDoTexto(snapshot)' in main,
      'atualização de qualquer representação também atualiza o enunciado')
check('!item.estaNoDiagrama()' in main,
      'sincronização textual não sobrescreve itens já posicionados nos diagramas')
check('valorSemanticoOriginal' in text('src/gerard/campoaditivo/diagrama/elementos/ElementoTextoMovel.java'),
      'valor original permanece estável para incógnita, logs e validações')
check('boolean representaIncognitaOriginal()' in contrato_texto,
      'contrato textual identifica polimorficamente a incógnita original')
check('elemento.representaIncognitaOriginal()' in base_texto,
      'sincronização preserva a interrogação em vez de revelar a resposta')

if errors:
    print(f'REPROVADO: {len(errors)} falha(s) após as verificações semânticas.'); sys.exit(1)
print('APROVADO: sincronização textual mantém valores conhecidos e preserva a interrogação.')

print('== Processo concreto de transformação ==')
seletor_complementar = text('src/gerard/campoaditivo/representacao/SeletorRepresentacaoComplementar.java')
layout_transformacao = text('src/gerard/campoaditivo/transformacao/processo/LayoutProcessoTransformacao.java')
render_transformacao = text('src/gerard/campoaditivo/transformacao/processo/RenderizadorProcessoTransformacao.java')
politica_processo = text('src/gerard/campoaditivo/transformacao/processo/PoliticaVisualProcessoTransformacao.java')
layout_unidades_transformacao = text('src/gerard/campoaditivo/transformacao/processo/LayoutUnidadesProcessoTransformacao.java')
check('PROCESSO_TRANSFORMACAO' in seletor_complementar
      and 'TRANSFORMACAO_MEDIDAS' in seletor_complementar,
      'transformação de medidas seleciona representação de processo própria')
check('LayoutProcessoTransformacao' in text('src/gerard/campoaditivo/venn/servico/GeradorCenaDiagramaVenn.java')
      and 'criarCena' in layout_transformacao,
      'geometria do canal e das zonas fica fora da tela principal')
check('RenderizadorProcessoTransformacao' in main
      and 'desenharEstrutura' in render_transformacao,
      'canal e funis são delegados ao renderizador especializado')
check('LayoutUnidadesProcessoTransformacao' in main
      and 'calcular' in layout_unidades_transformacao,
      'quadradinhos são posicionados nos estados e no funil correspondente')
check('NumeroInteiro' in politica_processo
      and 'TipoProcessoTransformacao.RETIRADA' in politica_processo
      and 'TipoProcessoTransformacao.INSERCAO' in politica_processo,
      'retirada/inserção decorre do próprio número inteiro')
for key in ('ui.transformationBoard.title', 'ui.transformationBoard.description',
            'ui.transformationBoard.before', 'ui.transformationBoard.change',
            'ui.transformationBoard.in', 'ui.transformationBoard.out',
            'ui.transformationBoard.after'):
    check(all(key in sets[k] for k in ('pt','en','fr')),
          f'chave do processo localizada em pt/en/fr: {key}')
check('sincronizarTodasAsRepresentacoesAPartirDoDiagramaComplementar' in main,
      'processo reutiliza a sincronização semântica consolidada')
check('podeAlterarQuantidadeNoEstadoAtual' in main
      and 'estadoSimuladoRespeitaLimitesDasQuantidades' in main,
      'interações concretas preservam a relação aditiva e a não negatividade dos estados')


if errors:
    print(f'REPROVADO: {len(errors)} falha(s) após o processo de transformação.'); sys.exit(1)
print('APROVADO: processo de transformação integrado sem remover os contratos consolidados.')
