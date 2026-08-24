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
import os, re, shutil, subprocess, sys
ROOT=Path(__file__).resolve().parents[1]
errors=[]
def check(cond,msg):
    print(('[OK] ' if cond else '[ERRO] ')+msg)
    if not cond: errors.append(msg)
def text(rel): return (ROOT/rel).read_text(encoding='utf-8',errors='replace')

def linhas_metodo_java(codigo, assinatura):
    """Conta o método de TelaGerard, ignorando chaves em comentários e literais."""
    padrao=re.compile(r'(?m)^ {8}'+re.escape(assinatura)+r'\s*\{')
    encontrado=padrao.search(codigo)
    if not encontrado:
        return None

    inicio=encontrado.start()
    pos_chave=codigo.find('{',encontrado.start(),encontrado.end())
    profundidade=0
    estado='codigo'
    escape=False
    i=pos_chave
    while i < len(codigo):
        atual=codigo[i]
        proximo=codigo[i+1] if i+1 < len(codigo) else ''
        if estado == 'linha':
            if atual == '\n': estado='codigo'
        elif estado == 'bloco':
            if atual == '*' and proximo == '/':
                estado='codigo'; i+=1
        elif estado in ('string','char'):
            if escape:
                escape=False
            elif atual == '\\':
                escape=True
            elif (estado == 'string' and atual == '"') or (estado == 'char' and atual == "'"):
                estado='codigo'
        elif atual == '/' and proximo == '/':
            estado='linha'; i+=1
        elif atual == '/' and proximo == '*':
            estado='bloco'; i+=1
        elif atual == '"':
            estado='string'
        elif atual == "'":
            estado='char'
        elif atual == '{':
            profundidade+=1
        elif atual == '}':
            profundidade-=1
            if profundidade == 0:
                return codigo[inicio:i+1].count('\n')+1
        i+=1
    return None

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

def localizar_ant():
    """Retorna (comando, origem) para um Ant instalado ou embarcado em IDE."""
    ant=shutil.which('ant') or shutil.which('ant.bat')
    if ant:
        return [ant], 'PATH'

    ant_home=os.environ.get('ANT_HOME')
    if ant_home:
        for nome in ('ant.bat','ant'):
            executavel=Path(ant_home)/'bin'/nome
            if executavel.is_file():
                return [str(executavel)], 'ANT_HOME'

    java=shutil.which('java')
    if not java:
        return None, None

    raizes=[]
    for variavel in ('ProgramFiles','ProgramFiles(x86)','LOCALAPPDATA'):
        valor=os.environ.get(variavel)
        if valor:
            raizes.append(Path(valor))

    padroes=(
        'JetBrains/*/plugins/gradle-plugin/lib/ant/ant-launcher.jar',
        'JetBrains/Toolbox/apps/**/plugins/gradle-plugin/lib/ant/ant-launcher.jar',
        'Programs/JetBrains/*/plugins/gradle-plugin/lib/ant/ant-launcher.jar',
    )
    for raiz in raizes:
        for padrao in padroes:
            for launcher in sorted(raiz.glob(padrao),reverse=True):
                diretorio_ant=launcher.parent
                if (diretorio_ant/'ant.jar').is_file():
                    return ([java,'-cp',str(diretorio_ant/'*'),
                             'org.apache.tools.ant.launch.Launcher'],
                            f'Ant embarcado: {diretorio_ant}')
    return None, None

print('== Compilação ==')
comando_ant,origem_ant=localizar_ant()
if comando_ant:
    print(f'Ant localizado via {origem_ant}')
    r=subprocess.run(comando_ant+['-noinput','clean','jar'],cwd=ROOT,text=True,
                     stdout=subprocess.PIPE,stderr=subprocess.STDOUT)
    print(r.stdout); check(r.returncode==0,'ant clean jar')
else:
    check(False,'ant clean jar (Ant não encontrado no PATH, ANT_HOME ou IDE)')
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

print('== Main compositora e roteadora: ratchet dos protocolos de interação ==')
# Estes limites são a fotografia da versão arquitetural corrente. Eles não
# medem o tamanho total de Main.java: novas categorias podem acrescentar UI sem
# justificar que a mecânica particular volte aos protocolos centrais. Cada
# extração deve reduzir o método e, na mesma alteração, reduzir este limite.
LIMITES_PROTOCOLOS_MAIN = {
    'public void mousePressed(MouseEvent e)': 457,
    'public void mouseDragged(MouseEvent e)': 20,
    'private void processarMovimentoArraste(int x, int y)': 69,
    'public void mouseReleased(MouseEvent e)': 128,
    'public void mouseClicked(MouseEvent e)': 34,
    'public void mouseMoved(MouseEvent e)': 229,
}
for assinatura, limite in LIMITES_PROTOCOLOS_MAIN.items():
    linhas=linhas_metodo_java(main,assinatura)
    check(linhas is not None,f'protocolo localizado para o ratchet: {assinatura}')
    if linhas is None:
        continue
    print(f'[INFO] {assinatura}: {linhas} linha(s); limite atual: {limite}')
    check(linhas<=limite,
          f'{assinatura} não volta a concentrar mecânica particular '
          f'({linhas}/{limite} linhas)')
    check(linhas==limite,
          f'limite de {assinatura} acompanha toda redução já obtida; '
          f'se o método foi extraído, reduza o limite de {limite} para {linhas}')
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
conversor_valores=text('src/gerard/campoaditivo/sincronizacao/ConversorValoresEstadoAditivo.java')
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
check('ConversorValoresEstadoAditivo' in estado_compartilhado
      and 'conversorValores.normalizarEntrada' in estado_compartilhado
      and 'politicaValores.valorEhValidoNoEstadoCompartilhado' in conversor_valores,
      'estado compartilhado delega validade à política semântica via conversor')
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
      and 'item != handlerItemTextoArrastavel.obterItemAtivo()' in main
      and 'quadradinho != handlerQuadradinhoVenn.obterQuadradinhoAtivo()' in main,
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

print('== Handler local do item textual arrastável ==')
handler_item_texto=text('src/gerard/interacao/arraste/HandlerInteracaoItemTextoArrastavel.java')
check('class HandlerInteracaoItemTextoArrastavel' in handler_item_texto
      and 'ResultadoSoltura concluir()' in handler_item_texto
      and 'ItemTextoArrastavel moverPara' in handler_item_texto,
      'handler encapsula estado e mecânica local do gesto do item textual')
check('handlerItemTextoArrastavel.iniciar' in main
      and 'handlerItemTextoArrastavel.moverPara' in main
      and 'handlerItemTextoArrastavel.concluir' in main,
      'tela encaminha início, movimento e conclusão ao handler local')
check('ItemTextoArrastavel itemSelecionado = null' not in main
      and 'xDoItemNoPickup' not in main
      and 'yDoItemNoPickup' not in main,
      'tela não duplica o estado mecânico pertencente ao handler')

print('== Separação factual entre gesto e ação instrumental ==')
resumo_gesto=text('src/gerard/interacao/ResumoGestoArraste.java')
registro_gesto=text('src/gerard/interacao/RegistroGestoInteracao.java')
publicador_gesto=text('src/gerard/interacao/PublicadorGestoInteracao.java')
logger_gesto=text('src/gerard/pesquisador/log/LoggerGestosInteracaoGerard.java')
item_arrastavel=text('src/gerard/campoaditivo/diagrama/elementos/ItemTextoArrastavel.java')
check('ResumoGestoArraste gesto' in handler_item_texto
      and 'getGestoConcluido()' in handler_item_texto
      and 'import java.awt.' not in handler_item_texto
      and 'import javax.swing.' not in handler_item_texto
      and 'import gerard.campoaditivo.diagrama.elementos.ElementoVergnaud' not in handler_item_texto,
      'handler portátil produz somente observações físicas do arraste')
check('produzirRegistroGestoArraste' in item_arrastavel
      and 'new RegistroGestoInteracao' in item_arrastavel,
      'objeto representacional participante produz o registro factual do próprio gesto')
check('interface PublicadorGestoInteracao' in publicador_gesto
      and 'implements PublicadorGestoInteracao' in logger_gesto
      and 'gerard_gestos_' in logger_gesto,
      'porta separada persiste gestos sem possuir sua interpretação')
cabecalho_gesto=logger_gesto.split('public static String cabecalhoTsv()',1)[1].split('}',1)[0]
check(all(token not in cabecalho_gesto
          for token in ('action_id', 'diagnostico', 'rejection_sequence_id', '\\tce\\t')),
      'esquema TSV de gestos não contém avaliação nem correlação de ação')
check('iniciarRastreamentoGranular(x, y, novo.valor' not in main
      and 'iniciarRastreamentoGranular(x, y, itemSelecionado.valor' not in main,
      'ItemTextoArrastavel não alimenta mais o rastreador legado do log de ações')
trecho_log_soltura=main.split('private void registrarLogSolturaItem(',1)[1].split(
        'private String descreverElementoVergnaudParaLog',1)[0]
check('!houveMovimento' in trecho_log_soltura
      and '!resultadoPosicionamento.isAplicavel()' in trecho_log_soltura
      and 'if (elemento == null)' in trecho_log_soltura
      and 'avaliarQuestionamentoPosicionamento' not in trecho_log_soltura,
      'ação POSICIONAR só é registrada uma vez quando o comando semântico foi constituído')

print('== Handler local do elemento textual ==')
handler_elemento_texto=text('src/gerard/interacao/arraste/HandlerInteracaoElementoTextoMovel.java')
geometria_enunciado=text('src/gerard/ui/enunciado/GeometriaAreaEnunciado.java')
no_geometria=text('src/gerard/ui/geometria/NoGeometriaRepresentacao.java')
limites_movimento=text('src/gerard/interacao/geometria/LimitesMovimento.java')
check('class HandlerInteracaoElementoTextoMovel' in handler_elemento_texto
      and 'moverLivrePara' in handler_elemento_texto
      and 'moverDentroDosLimites' in handler_elemento_texto
      and 'LimitesMovimento limites' in handler_elemento_texto,
      'handler textual recebe a restrição geométrica sem conhecer o layout')
check('handlerElementoTextoMovel.iniciar' in main
      and 'handlerElementoTextoMovel.moverLivrePara' in main
      and 'handlerElementoTextoMovel.moverDentroDosLimites' in main
      and 'handlerElementoTextoMovel.concluir' in main,
      'tela roteia o protocolo textual pelo handler local')
check('ElementoTextoMovel elementoTextoSelecionado = null' not in main
      and 'TOPO_AREA_ENUNCIADO' not in main
      and 'BASE_AREA_ENUNCIADO' not in main,
      'tela não duplica seleção nem limites do enunciado')
check('NoGeometriaRepresentacao pai' in no_geometria
      and 'getPai()' in no_geometria
      and 'while (ancestral != null)' in no_geometria
      and 'cardEnunciado.obterLimitesAbsolutos()' in geometria_enunciado,
      'geometria do enunciado deriva de árvore enraizada com apontador para o pai')
check('GeometriaAreaEnunciado' not in handler_elemento_texto
      and 'Main' not in handler_elemento_texto
      and 'getWidth()' not in handler_elemento_texto
      and 'java.awt' not in handler_elemento_texto
      and 'javax.swing' not in handler_elemento_texto,
      'handler permanece independente da tela e da árvore de layout')
check('class LimitesMovimento' in limites_movimento
      and 'limitarX' in limites_movimento
      and 'limitarY' in limites_movimento
      and 'java.awt' not in limites_movimento
      and 'javax.swing' not in limites_movimento
      and 'LimitesMovimento obterLimitesMovimento' in geometria_enunciado,
      'representação produz limites portáteis para o protocolo textual')

print('== P4.2: protocolo portátil de arraste incremental do conector de Vergnaud ==')
handler_vergnaud=text('src/gerard/interacao/arraste/HandlerInteracaoArrasteIncremental.java')
alvo_incremental=text('src/gerard/interacao/arraste/AlvoMovelIncremental.java')
adaptador_vergnaud=text('src/gerard/ui/vergnaud/AdaptadorMovimentoConectorVergnaud.java')
check('class HandlerInteracaoArrasteIncremental' in handler_vergnaud
      and 'AlvoMovelIncremental' in alvo_incremental
      and 'LimitesMovimento limites' in handler_vergnaud,
      'handler portátil recebe alvo e restrição geométrica sem conhecer a representação concreta')
check('handlerConectorVergnaud.iniciar(' in main
      and 'handlerConectorVergnaud.mover' in main
      and 'handlerConectorVergnaud.finalizarLimiar' in main
      and 'handlerConectorVergnaud.cancelar' in main,
      'tela compõe e roteia pickup, movimento e soltura do conector pelo protocolo portátil')
check('ElementoVergnaud elementoVergnaudSelecionado' not in main
      and 'ConectorVergnaud conectorVergnaudSelecionado' not in main
      and 'ControladorLimiarArrasteEstrutural controladorLimiarArrasteEstrutural' not in main,
      'tela não duplica seleção do diagrama de Vergnaud nem o limiar de arraste estrutural')
check('Main' not in handler_vergnaud
      and 'getWidth()' not in handler_vergnaud
      and 'encontrarElementoVergnaud' not in handler_vergnaud
      and 'encontrarConectorVergnaud' not in handler_vergnaud
      and 'java.awt' not in handler_vergnaud
      and 'javax.swing' not in handler_vergnaud
      and 'ConectorVergnaud' not in handler_vergnaud
      and 'ElementoVergnaud' not in handler_vergnaud,
      'handler permanece independente de tela, hit-testing, Swing/AWT e classes visuais concretas')
check('implements AlvoMovelIncremental' in adaptador_vergnaud
      and 'java.awt.Rectangle' in adaptador_vergnaud
      and 'AdaptadorMovimentoConectorVergnaud.traduzir(' in main,
      'adaptador da representação desktop possui a tradução Rectangle/LimitesMovimento')
check(main.count('encontrarConectorVergnaud(x, y)') >= 1,
      'tela continua responsável por decidir qual conector foi alvo do pickup — o hit-test de elemento '
      '(x, y) para pickup foi removido junto com o arraste (2026-08-18); encontrarElementoVergnaud '
      'continua existindo só para a edição de texto por duplo clique e para achar o elemento sob um item')

print('== Fase 7.5: handler do quadradinho do diagrama de Venn ==')
handler_quadradinho=text('src/gerard/interacao/arraste/HandlerInteracaoQuadradinhoVenn.java')
check('class HandlerInteracaoQuadradinhoVenn' in handler_quadradinho
      and 'public boolean iniciar' in handler_quadradinho
      and 'public boolean mover' in handler_quadradinho
      and 'public ResultadoSoltura concluir' in handler_quadradinho
      and 'class ResultadoSoltura' in handler_quadradinho,
      'handler do quadradinho do Venn expõe pickup, movimento e soltura com resultado próprio')
check('Main' not in handler_quadradinho
      and 'getWidth()' not in handler_quadradinho
      and 'encontrarQuadradinhoVenn' not in handler_quadradinho,
      'handler do quadradinho permanece independente da tela e do hit-testing')
check('handlerQuadradinhoVenn.iniciar' in main
      and 'handlerQuadradinhoVenn.mover' in main
      and 'handlerQuadradinhoVenn.concluir' in main
      and 'handlerQuadradinhoVenn.cancelar' in main
      and 'handlerQuadradinhoVenn.estaAtivo' in main,
      'tela roteia pickup, movimento e soltura do quadradinho do Venn pelo handler local')
check('quadradinhoVennSelecionado' not in main
      and 'deslocamentoVennX' not in main
      and 'deslocamentoVennY' not in main
      and 'indiceCirculoVennOrigemArraste' not in main,
      'tela não duplica seleção, deslocamento nem índice de origem do quadradinho do Venn')
check('quadradinhoVennFocado' in main,
      'hover/foco do quadradinho (fora de um arraste) continua na tela, fora do handler')
check(main.count('encontrarQuadradinhoVenn(x, y)') >= 1,
      'tela continua responsável por decidir qual quadradinho foi alvo do pickup')

print('== Item 4: material concreto próprio de Relações (painéis de eixo por papel) ==')
paineis_relacoes=text('src/gerard/ui/vergnaud/PaineisEixosRelacoes.java')
check('class PaineisEixosRelacoes' in paineis_relacoes
      and 'class Painel' in paineis_relacoes
      and 'public void ativar' in paineis_relacoes
      and 'public void desativar' in paineis_relacoes
      and 'public boolean estaAtivo' in paineis_relacoes
      and 'public List<Painel> obterPaineis' in paineis_relacoes,
      'coordenador de painéis de eixo das Relações expõe ciclo de vida (ativar/desativar) e a lista de painéis')
check('public boolean processarPressionamento' in paineis_relacoes
      and 'public void arrastarPara' in paineis_relacoes
      and 'public void finalizarArraste' in paineis_relacoes
      and 'public void desenhar' in paineis_relacoes
      and 'public boolean contemPontoControle' in paineis_relacoes
      and 'public ScaffoldingGraficoInteiros.NaturezaInteracao identificarNaturezaInteracao' in paineis_relacoes,
      'coordenador expõe pressionamento, arraste, desenho e detecção de ponto de controle agregados')
check('new ScaffoldingGraficoInteiros()' in paineis_relacoes,
      'painel reaproveita a classe já existente (instâncias novas, não uma reimplementação)')
check('paineisEixosRelacoes.ativar' in main
      and main.count('paineisEixosRelacoes.desativar()') >= 3
      and 'paineisEixosRelacoes.desenhar' in main
      and 'paineisEixosRelacoes.processarPressionamento' in main
      and 'paineisEixosRelacoes.arrastarPara' in main
      and 'paineisEixosRelacoes.finalizarArraste' in main
      and 'paineisEixosRelacoes.estaArrastando' in main,
      'tela ativa/desativa e roteia pickup, arraste e desenho dos painéis de Relações pelo coordenador')
check('private boolean devemExibirPaineisEixosRelacoes' in main
      and 'if (!categoriaSelecionadaParaAtividade || elementosVergnaud == null) {' in main
      and main.count('if (ehElementoNumeroRelativo(elemento)) {\n                    return true;\n                }') >= 1,
      'visibilidade dos painéis de eixo depende só de existir um elemento número relativo no diagrama '
      'atual (regra generalizada 2026-08-18: "todo número relativo ou transformação carrega uma lupa. '
      'Essa é a regra") — critério estrutural (ehElementoNumeroRelativo/TipoFiguraDiagrama.ELIPSE), não '
      'mais uma lista fixa de 2 categorias (TRANSFORMACAO_RELACAO/COMPOSICAO_RELACOES) — não espera '
      'nenhuma tentativa rejeitada, diferente de quadradinhos/barras/processo')
check('elemento.tipo == TipoFiguraDiagrama.ELIPSE' in paineis_relacoes,
      'PaineisEixosRelacoes.ativar só cria painel para elementos elipse (número relativo) — filtra fora '
      'as âncoras de medida (quadrado) que hoje entram na mesma lista de elementosVergnaud em cenas '
      'compostas (ex.: Composição de Transformações)')
check('scaffoldingGraficoInteiros' in main
      and 'itemGraficoInteiros' in main
      and 'numeroRelativoGraficoInteiros' in main
      and 'apresentadorGraficoInteiros' in main,
      'mecanismo já existente de escolha de sinal sob demanda (Comparação de Medidas e outros usos) '
      'continua presente e intocado, sem ser substituído pelo coordenador novo')
check(main.count(
        'private void mostrarGraficoInteirosNumeroRelativo(ItemTextoArrastavel item, '
        'ElementoVergnaud numeroRelativo, String valorBase) {\n'
        '            if (numeroRelativo == null) {\n'
        '                return;\n'
        '            }') == 1
      and main.count(
        'private void registrarEscolhaGraficoInteiros(ItemTextoArrastavel item, ElementoVergnaud '
        'numeroRelativo, String valorBase, String sinal) {\n'
        '            if (numeroRelativo == null) {\n'
        '                return;\n'
        '            }') == 1,
      'assinatura original dos dois pontos de entrada do eixo antigo preservada (guard novo é aditivo)')
check(main.count('if (devemExibirPaineisEixosRelacoes()) {\n                return;\n            }') == 2,
      'eixo único antigo é suprimido em qualquer diagrama com número relativo (regra generalizada '
      '2026-08-18), já que os painéis novos por papel cobrem o mesmo lugar em qualquer categoria — só o '
      'menu de escolha de sinal continua aparecendo à parte; categorias só com medidas (ex.: Composição '
      'de Medidas) continuam sem nenhum painel, pois não há elemento elipse')
check('private void atualizarValorPainelEixoRelacao(PaineisEixosRelacoes.Painel painel)' in main
      and 'private void atualizarPaineisEixosRelacoesComValoresAtuais()' in main
      and 'painel.grafico.estaArrastando()' in main
      and 'atualizarPaineisEixosRelacoesComValoresAtuais();' in main,
      'painéis de Relações se atualizam quando o valor do papel muda por qualquer outro caminho '
      '(bug relatado 2026-08-17: "eixos não mudam com a mudança dos elementos no diagrama"), sem '
      'sobrescrever o painel que estiver sendo arrastado no momento')
check('private void informarBloqueioQuantidadeNegativa(' in main
      and main.count('informarBloqueioQuantidadeNegativa(') >= 6,
      'bloqueio de quantidade negativa ganhou versão com âncora explícita, reaproveitada pelos '
      'painéis de Relações sem alterar os chamadores já existentes do overload sem parâmetros')
teste_paineis_relacoes=text('tests/java/TestePaineisEixosRelacoes.java')
check('class TestePaineisEixosRelacoes' in teste_paineis_relacoes
      and 'testarCicloDeVidaEIdempotencia' in teste_paineis_relacoes
      and 'testarIsolamentoEntreDoisPaineis' in teste_paineis_relacoes,
      'teste do coordenador cobre ciclo de vida, idempotência e isolamento entre painéis simultâneos')
check('testarVisibilidadePorLupa' in teste_paineis_relacoes
      and 'revelarPainel' in teste_paineis_relacoes,
      'teste do coordenador também cobre visibilidade individual por lupa (revelação independente '
      'por papel, bloqueio de interação antes de revelar) — correção 2026-08-17')

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
      and 'elementos' in destaque_conclusao and 'itens' in destaque_conclusao
      and 'for (ConectorVergnaud' not in destaque_conclusao,
      'destaque azul abrange valores e não colore conectores do Vergnaud')
check('JRadioButton' in tip_conclusao and 'opcaoSim' in tip_conclusao
      and 'opcaoNao' in tip_conclusao,
      'tip de conclusão usa radio buttons Sim e Não')
check('ui.completion.congratulations' in main
      and 'itemNovaSituacao.doClick()' in main,
      'escolha Sim aciona o item consolidado de nova situação')
check('itemNovaSituacao.addActionListener' in main
      and 'sortearNovaSituacao()' in main,
      'item Nova situação preserva o fluxo consolidado de sorteio')
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
check(props['pt'].get('ui.tab.assembly') == 'Construir'
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
r=subprocess.run([sys.executable,str(ROOT/'scripts/verificar_curadoria_canonica.py')],cwd=ROOT,text=True,stdout=subprocess.PIPE,stderr=subprocess.STDOUT)
print(r.stdout); check(r.returncode==0,'integridade da fonte canônica das situações curadas')

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
check('sincronizarElementosSemanticosDoTexto(estado)' in main,
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

print('== Ajustes de 2026-08-16 (levantamento de pendências de 2026-08-11) ==')
check('private static final boolean EXIBIR_DIAGRAMA_COMPLEMENTAR_SEMPRE_PARA_TESTES'
      not in main
      and '|| EXIBIR_DIAGRAMA_COMPLEMENTAR_SEMPRE_PARA_TESTES' not in main,
      'flag temporária de teste do diagrama complementar foi removida (item 2)')
check('registrarLogConsistenciaAutomaticaImediatamente' in main
      and 'flushLogConsistenciaAutomaticaPendenteDoArrasteComparacao' in main
      and 'logConsistenciaAutomaticaPendenteArrasteComparacao' in main,
      'log CONSISTENCIA_AUTOMATICA represa durante o arraste do controle de'
      ' Comparação e só é escrito ao soltar o mouse (item 3)')
check(main.count('flushLogConsistenciaAutomaticaPendenteDoArrasteComparacao()') >= 3,
      'flush do log represado é chamado tanto no reset defensivo de mousePressed'
      ' quanto na soltura normal do controle de Comparação')

print('== Item 1 (AG_EME): explicação conceitual do papel no objeto rico ==')
descritor_papel = text('src/gerard/dominio/campoaditivo/DescritorRepresentacaoPapel.java')
papel_quantitativo = text('src/gerard/dominio/campoaditivo/PapelQuantitativo.java')
catalogo_explicacoes = text('src/gerard/dominio/campoaditivo/CatalogoExplicacoesConceituaisPapel.java')
fabrica_transformacao_medidas = text('src/gerard/dominio/campoaditivo/FabricaPapeisTransformacaoMedidas.java')
fabrica_comparacao_medidas = text('src/gerard/dominio/campoaditivo/FabricaPapeisComparacaoMedidas.java')
fabrica_composicao_transformacoes = text('src/gerard/dominio/campoaditivo/FabricaPapeisComposicaoDeTransformacoes.java')
fabrica_transformacao_relacao = text('src/gerard/dominio/campoaditivo/FabricaPapeisTransformacaoDeRelacao.java')
fabrica_composicao_relacoes = text('src/gerard/dominio/campoaditivo/FabricaPapeisComposicaoDeRelacoes.java')

check('private final String chaveExplicacaoConceitual' in descritor_papel
      and 'getChaveExplicacaoConceitual' in descritor_papel,
      'DescritorRepresentacaoPapel carrega a chave de explicação conceitual do papel')
check('explicacao.papel.parte' in papel_quantitativo
      and 'explicacao.papel.todo' in papel_quantitativo,
      'PapelQuantitativo.parte1/parte2/todo já fornecem chave de explicação conceitual')
for fabrica, chaves in (
        (fabrica_transformacao_medidas, ('explicacao.papel.estadoInicial',
                                          'explicacao.papel.transformacao', 'explicacao.papel.estadoFinal')),
        (fabrica_comparacao_medidas, ('explicacao.papel.referido',
                                       'explicacao.papel.referendo', 'explicacao.papel.diferenca')),
        (fabrica_composicao_transformacoes, ('explicacao.papel.transformacao',
                                              'explicacao.papel.transformacaoFinal')),
        (fabrica_transformacao_relacao, ('explicacao.papel.relacaoInicial',
                                          'explicacao.papel.transformacao', 'explicacao.papel.relacaoFinal')),
        (fabrica_composicao_relacoes, ('explicacao.papel.relacao', 'explicacao.papel.relacaoFinal'))):
    for chave in chaves:
        check(chave in fabrica, f'fábrica de papéis fornece chave de explicação {chave}')
check('class CatalogoExplicacoesConceituaisPapel' in catalogo_explicacoes
      and 'obterChaveExplicacao' in catalogo_explicacoes
      and 'CHAVE_EXPLICACAO_GENERICA' in catalogo_explicacoes,
      'catálogo coordenador resolve chave de papel -> chave de explicação, com fallback genérico')
check("papel.diferenca" in catalogo_explicacoes and "papel.referente" in catalogo_explicacoes,
      'catálogo trata os sinônimos vivos sem fábrica própria (diferenca/valorRelativo, referente/referendo)')
check('CatalogoExplicacoesConceituaisPapel' in main
      and 'obterChaveExplicacao' in main
      and 'explicacaoConceitual' in main,
      'mostrarDicaOperacaoIncognita consulta o catálogo e usa a explicação conceitual')
chaves_explicacao = ('explicacao.papel.generica', 'explicacao.papel.parte', 'explicacao.papel.todo',
                      'explicacao.papel.estadoInicial', 'explicacao.papel.transformacao',
                      'explicacao.papel.estadoFinal', 'explicacao.papel.transformacaoFinal',
                      'explicacao.papel.referido', 'explicacao.papel.referendo',
                      'explicacao.papel.diferenca', 'explicacao.papel.relacaoInicial',
                      'explicacao.papel.relacaoFinal', 'explicacao.papel.relacao')
for chave in chaves_explicacao:
    check(all(chave in sets[k] for k in ('pt', 'en', 'fr')) and chave in props['es'],
          f'explicação conceitual localizada em pt/en/es/fr: {chave}')

print('== Item 24 (2026-08-18): necessitaRepresentacaoDeSinal() no objeto rico (piloto isolado) ==')
check('public boolean necessitaRepresentacaoDeSinal()' in papel_quantitativo
      and 'return dominio == DominioNumerico.INTEIROS;' in papel_quantitativo,
      'PapelQuantitativo responde por si mesmo se precisa de representação de sinal (lupa/eixo) — '
      'decisão da usuária: "a pergunta \'eu preciso de lupa?\' vira comportamento do objeto, não '
      'inferência de quem olha de fora" — fonte de verdade é o domínio numérico (INTEIROS vs '
      'NATURAIS), não a forma visual (elipse/quadrado)')
teste_transformacao_relacao = text('tests/java/TestePilotoTransformacaoDeRelacao.java')
check('necessitaRepresentacaoDeSinal()' in teste_transformacao_relacao
      and teste_transformacao_relacao.count('.necessitaRepresentacaoDeSinal())') >= 4,
      'harness do piloto cobre o método: os 3 papéis INTEIROS de Transformação de Relação (true) e um '
      'papel NATURAIS de outro esquema, Parte1 (false)')

print('== Correção 2026-08-17: renderizador correto de Composição de Transformações ==')
fabrica_renderizadores = text('src/gerard/campoaditivo/diagrama/servico/FabricaRenderizadoresDiagramaAditivo.java')
check(fabrica_renderizadores.count(
        'renderizadores.put(TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES,') == 1
      and 'new RenderizadorComposicaoTransformacoes()' in fabrica_renderizadores,
      'só existe UM registro de COMPOSICAO_TRANSFORMACOES na fábrica de renderizadores, apontando '
      'para o renderizador correto (6 elementos + seta curva) — bug relatado 2026-08-17 '
      '("o diagrama está errado" / "antigamente funcionava"): um segundo put() duplicado para a '
      'mesma chave sobrescrevia silenciosamente esse renderizador pelo genérico de Transformação '
      'de Medidas (3 elementos), truncando o diagrama')
check('new RenderizadorTransformacaoMedidas()' not in fabrica_renderizadores
      or fabrica_renderizadores.count('renderizadores.put(TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,') == 1,
      'o renderizador genérico de Transformação de Medidas continua registrado só para sua própria '
      'categoria, não mais reaproveitado por engano em Composição de Transformações')

print('== Correção 2026-08-17: reexplicação de categoria mostra só a categoria real ==')
check('private void acionarTimeoutCategoria(TipoSituacaoAditiva categoriaReal)' in main
      and 'private void mostrarExplicacaoCategorias(TipoSituacaoAditiva categoriaReal)' in main
      and 'private Icon obterIconeParaCategoria(TipoSituacaoAditiva tipo)' in main,
      'reexplicação de categoria (timeout após 3 erros) recebe a categoria real da situação sorteada '
      'em vez de assumir sempre as 3 de Medidas — bug relatado 2026-08-17 ("essa explicação aparece '
      'no terceiro erro da categoria de relações"), com print mostrando CM/TM/COP numa situação de '
      'Relações')
check(main.count('criarLinhaExplicacaoCategoria(\n                    TipoSituacaoAditiva.COMPOSICAO_MEDIDAS') == 0,
      'as 3 linhas fixas de explicação de Medidas foram substituídas por uma linha só, da categoria real '
      '("cada categoria deve vir apenas com sua explicação curta")')
textos_reexplicacao_plural = {
    'pt': 'cada categoria', 'en': 'each category',
    'es': 'cada categoría', 'fr': 'chaque catégorie',
}
for chave_intro in ('ui.dialog.categoryExplanation.title', 'ui.dialog.categoryExplanation.intro'):
    check(all(textos_reexplicacao_plural[k] not in props[k].get(chave_intro, '')
              for k in ('pt', 'en', 'es', 'fr')),
          f'texto do diálogo de reexplicação ajustado para singular (uma categoria só): {chave_intro}')

print('== Correção 2026-08-17: lupa revela cada eixo de Relações sob demanda ==')
paineis_relacoes_lupa = paineis_relacoes  # já lido/definido na seção do Item 4
check('private boolean revelado' in paineis_relacoes_lupa
      and 'public boolean estaRevelado()' in paineis_relacoes_lupa
      and 'public Painel processarPressionamentoLupa' in paineis_relacoes_lupa
      and 'public void desenharLupas' in paineis_relacoes_lupa
      and 'public Painel encontrarComOcultacaoPorInteracao' in paineis_relacoes_lupa
      and 'public void ocultarRevelacao' in paineis_relacoes_lupa,
      'cada papel de Relações tem visibilidade individual controlada por lupa (revelado/'
      'processarPressionamentoLupa/desenharLupas) — decisão da usuária 2026-08-17: "eixos aparecendo '
      'logo no início deixou a tela muito poluída"')
check(paineis_relacoes_lupa.count('painel.revelado') >= 8,
      'métodos de desenho/interação do coordenador (desenhar, processarPressionamento, '
      'contemPontoControle, contemAlgumPainel, contemBotaoEsconder, identificarNaturezaInteracao, '
      'estaArrastando, encontrarArrastando) só consideram papéis já revelados pela lupa')
check('prepararPainelEixoRelacao' in main
      and main.count('prepararPainelEixoRelacao(') >= 3,
      'preparação de valor+posição do painel (ativação e revelação por lupa) reaproveita o mesmo '
      'método em vez de duplicar a lógica — localidade do conhecimento')
check('paineisEixosRelacoes.processarPressionamentoLupa' in main
      and 'paineisEixosRelacoes.desenharLupas' in main
      and 'paineisEixosRelacoes.contemLupa' in main
      and 'paineisEixosRelacoes.encontrarComOcultacaoPorInteracao' in main
      and 'paineisEixosRelacoes.ocultarRevelacao' in main,
      'tela aciona a lupa no mousePressed, desenha as lupas a cada repaint, mostra tooltip no hover, '
      'e reexibe a lupa quando o painel é escondido pelo próprio botão')

print('== Correção 2026-08-18: aviso (som+tremor+tip) quando o sinal do número relativo diverge do curado ==')
check('sinalizarErro(ElementoVergnaud elemento, Runnable repaint)' in feedback_erro
      and 'import gerard.campoaditivo.diagrama.elementos.ElementoVergnaud;' in feedback_erro,
      'ScaffoldingFeedbackMultissensorialErro ganhou uma variante de tremor+som para ElementoVergnaud '
      '(sem ItemTextoArrastavel associado), usada no menu de sinal acionado a partir do próprio círculo')
check('elementoEmTremor' in feedback_erro
      and feedback_erro.count('elementoEmTremor') >= 5,
      'pararTremor/executarEtapaTremor tratam elementoEmTremor do mesmo jeito que itemEmTremor/'
      'agrupamentoEmTremor, sem duplicar a máquina de estados do tremor')
check('private void informarSuspeitaSinalIncorretoNumeroRelativo(' in main,
      'novo método central para o aviso não bloqueante de sinal divergente — bug relatado 2026-08-18: '
      '"o sinal do primeiro número nos dados curados é negativo, mas o sinal do primeiro número '
      'relativo foi colocado positivo e não houve feedback de erro" (sinalEscolhidoCorrespondeAoCurado '
      'já calculava a divergência, mas só alimentava o log de pesquisa C/E, sem nenhum aviso visível)')
check(main.count('if (sinalCorreto != null && !sinalCorreto.booleanValue()) {\n'
                  '                                informarSuspeitaSinalIncorretoNumeroRelativo(') == 2,
      'os dois pontos que abrem o menu de escolha de sinal (solicitarSinalNumeroRelativoParaTexto e '
      'solicitarSinalNumeroRelativoParaItem) acionam o aviso quando o sinal escolhido diverge do curado')
check('ui.tooltip.relativeSign.confirm' in main,
      'Main.java usa a nova chave de i18n do aviso de sinal divergente')
for lang in ('pt', 'en', 'es', 'fr'):
    check('ui.tooltip.relativeSign.confirm' in sets[lang]
          and '{0}' in props[lang].get('ui.tooltip.relativeSign.confirm', ''),
          f'ui.tooltip.relativeSign.confirm presente e parametrizado com o sinal escolhido ({lang})')

print('== Correção 2026-08-18 (revisão): aviso de sinal divergente fica na tela até o menu ser reaberto ==')
check('boolean mostrarSinalDivergentePersistente' in main
      and 'ItemTextoArrastavel itemSinalDivergentePersistente' in main
      and 'ElementoVergnaud elementoSinalDivergentePersistente' in main
      and 'private void limparSinalDivergentePersistente()' in main,
      'aviso de sinal divergente usa o mesmo mecanismo persistente de mostrarLimiteQuantidadeQuestionado '
      '(desenharAnotacaoMouseOver) em vez do tooltip de 2600ms — a usuária achou que sumia rápido demais '
      '("deixe na tela até que seja corrigido")')
check('boolean usarSinalDivergentePersistente' in main
      and main.count('usarSinalDivergentePersistente') >= 5,
      'desenharAnotacaoMouseOver ganhou o ramo usarSinalDivergentePersistente na mesma cadeia de '
      'prioridade dos avisos persistentes já existentes (questionamento > limite de quantidade > sinal '
      'divergente > dica de posicionamento > tooltip comum)')
check(main.count('limparSinalDivergentePersistente();') >= 5,
      'aviso de sinal divergente é limpo ao reabrir o menu de sinal do mesmo item/elemento '
      '(sinalEscolhido) e ao carregar uma nova situação (mesmos pontos que já limpavam '
      'limparQuestionamentoPersistente)')

print('== Correção 2026-08-18: elementos semânticos do diagrama de Vergnaud não são mais arrastáveis ==')
check('iniciarElemento(' not in handler_vergnaud
      and 'obterElementoAtivo' not in handler_vergnaud,
      'mousePressed não inicia mais o reposicionamento de um ElementoVergnaud (círculo/retângulo do '
      'diagrama) — decisão da usuária 2026-08-18: "isso foi um requisito muito antigo que, agora, não '
      'faz mais sentido", aplicada a todas as categorias, já que o mousePressed é compartilhado por '
      'todas elas')
check('handlerConectorVergnaud.iniciar(' in main,
      'conectores (setas) continuam arrastáveis normalmente — só os elementos semânticos foram '
      'desligados, o pedido não mencionou conectores')
check('encontrarElementoVergnaud(x, y) != null' not in main,
      'affordance de cursor (mão aberta) não considera mais um ElementoVergnaud como algo arrastável '
      '(pontoSobreElementoArrastavel)')
check("encontrarElementoVergnaud(e.getX(), e.getY()) != null || encontrarConectorVergnaud(e.getX(), e.getY()) != null" not in main
      and 'encontrarConectorVergnaud(e.getX(), e.getY()) != null) {' in main,
      'affordance de cursor no mouseMoved também não considera mais um ElementoVergnaud arrastável '
      '(mantém só o conector)')
check('private void editarTextoElementoVergnaud' in main
      and 'ElementoVergnaud elemento = encontrarElementoVergnaud(e.getX(), e.getY());' in main
      and 'editarTextoElementoVergnaud(elemento);' in main,
      'edição de texto por duplo clique no elemento continua intacta — mouseClicked nunca dependeu do '
      'mousePressed para isso, é um caminho independente')

print('== Correção 2026-08-18: números no enunciado nunca mostram o sinal curado do papel ==')
check("return Integer.toString(Math.abs(valor));" in impl_texto
      and impl_texto.count('startsWith(') == 0,
      'SincronizadorElementosSemanticosTextoAditivo.formatarValor sempre devolve a magnitude — bug '
      'relatado 2026-08-18 ("Julia tem -3 bonecas a mais que Maria..."): a exceção antiga "preserva o '
      'sinal quando o texto original trazia sinal explícito" na prática comparava o valor CANÔNICO do '
      'papel curado (com sinal, ex. "-3"), não o texto literalmente digitado no enunciado (que é só '
      '"3") — então o sinal do papel curado vazava de volta pra frase em qualquer situação de Relações')
teste_sincronizacao_texto = text('tests/java/TesteSincronizacaoElementosSemanticosTexto.java')
check('relacaoInicialComSinalCurado.vincularSemantica("papel.relacaoInicial", 0, 1, "-3")' in teste_sincronizacao_texto
      and '"3".equals(relacaoInicialComSinalCurado.valor)' in teste_sincronizacao_texto
      and '"5".equals(transformacaoComSinalCurado.valor)' in teste_sincronizacao_texto,
      'teste de sincronização cobre o caso exato do bug: papel curado com sinal, enunciado sem sinal, '
      'valor exibido deve ser só a magnitude')

print('== Correção 2026-08-18: campo Operação (soma/subtração) na curadoria de Relações ==')
op_enum = text('src/gerard/campoaditivo/curadoria/sinal/OpcaoOperacaoCuradoria.java')
check('public Integer aplicar(int a, int b)' in op_enum
      and 'a + b' in op_enum and 'a - b' in op_enum,
      'OpcaoOperacaoCuradoria.aplicar calcula soma (a+b) ou subtração (a-b) — o resultado nunca é '
      'digitado à mão pela curadora: "O -2 não é colocado previamente. Ele surge como resultado da '
      'operação."')
sit_modelo = text('src/gerard/campoaditivo/modelo/SituacaoProblemaAditiva.java')
check('getOperacaoRelacao()' in sit_modelo and 'operacaoRelacao' in sit_modelo,
      'SituacaoProblemaAditiva ganhou o campo operacaoRelacao (round-trip da escolha de operação), '
      'threaded por uma nova sobrecarga de construtor no padrão já usado para os campos mais recentes')
check('operacao_relacao' in repo,
      'RepositorioSituacoesAditivas lê/escreve a nova coluna final operacao_relacao do TSV (coluna '
      'opcional/trailing, mesmo esquema defensivo já usado para personagem_1-3/fragmento_texto_1-6)')
check(cur.count('adicionarCampo(formulario, gbc, y, "operacao", campoOperacaoRelacao)') == 2,
      'campo Operação (rótulo genérico "operacao") aparece nas 2 categorias que só têm uma operação: '
      'Transformação de Relação e Composição de Relações')
check('adicionarCampo(formulario, gbc, y, "operacao_transformacao", campoOperacaoRelacao)' in cur,
      'Item 30 (2026-08-23): em Composição de Transformações o mesmo campoOperacaoRelacao passa a ter '
      'rótulo "operacao_transformacao" — distingue da nova segunda operação (estado_inicial × '
      'transformação_resultante), pedido da usuária: "vai ter que diferenciar dois tipos de operações"')
check(cur.count('campoOperacaoRelacao);') >= 2,
      'os dois pontos que chamam aplicarCamposDaCuradoriaDetalhada (salvar e adicionar tradução) '
      'passam o novo campoOperacaoRelacao')
check('private String calcularResultadoOperacaoRelacao(' in cur,
      'nas duas categorias de composição, o papel resultante é calculado a partir dos dois papéis-dado '
      '+ operação escolhida ao salvar')
check('final PainelValorComSinalCuradoria painelResultantePorOperacao =\n'
      '                composicaoRelacoes ? painelSinalRelacaoResultante\n'
      '                : composicaoTransformacoes ? painelSinalTransformacaoResultante\n'
      '                : null;' in cur
      and 'linha.estadoFinal = controladorSinais.obterValorParaPersistencia(\n'
      '                    PapelSinalCuradoria.RELACAO_FINAL,\n'
      '                    campoEstadoFinal.getText());' in cur
      and '!SimboloDesconhecido.eh(linha.estadoFinal)' not in cur
      and cur.count('!SimboloDesconhecido.eh(linha.resultado)') == 3,
      'em Transformação de Relação, relacao_final permanece editável e preserva o valor informado pelo '
      'pesquisador; o cálculo automático fica restrito aos resultantes das duas categorias de composição '
      '(2 ocorrências) mais a segunda operação do Item 30, que também depende de linha.resultado já '
      'estar calculado (3ª ocorrência)')
check('painelResultantePorOperacao.definirHerdado(bloqueado, dicaOperacaoCalculada)' in cur,
      'somente os resultantes das duas categorias de composição ficam somente-leitura enquanto uma '
      'operação válida estiver escolhida; relacao_final de Transformação de Relação fica fora do bloqueio')
check('if (!semanticaHerdada) {\n                atualizarValoresCalculados.run();' in cur,
      'o bloqueio por operação não briga com o bloqueio por tradução herdada: quando a linha é uma '
      'tradução herdada, o mecanismo antigo (controladorSinais.definirSemanticaHerdada) continua tendo '
      'prioridade sobre o campo resultante — desde o Item 30, o disparo passa por '
      'atualizarValoresCalculados, que recalcula as duas operações em sequência')
for lang in ('pt', 'en', 'es', 'fr'):
    check('curadoria.operacao.selecione' in sets[lang]
          and 'curadoria.operacao.soma' in sets[lang]
          and 'curadoria.operacao.subtracao' in sets[lang]
          and 'curadoria.operacao.calculadoTooltip' in sets[lang],
          f'chaves de i18n do campo Operação presentes ({lang})')

print('== Item 22 (2026-08-18): seletor soma/subtração no diagrama do aluno ==')
seletor_op = text('src/gerard/ui/vergnaud/SeletorOperacaoRelacaoAluno.java')
check('public void ativar(TipoSituacaoAditiva tipo, SituacaoProblemaAditiva situacao' in seletor_op
      and 'public boolean processarPressionamento(int mouseX, int mouseY)' in seletor_op
      and 'public boolean respondeuCorretamente()' in seletor_op
      and 'public void desenhar(Graphics2D g2, ServicoLocalizacao localizacao)' in seletor_op,
      'SeletorOperacaoRelacaoAluno tem a API mínima: ativar por situação, processar clique, saber se '
      'a escolha do aluno bateu com a curada, e desenhar')
check('OpcaoOperacaoCuradoria.aPartirDoEstado(operacaoCurada)' in seletor_op
      and 'situacao.getOperacaoEstadoTransformacao()\n                : situacao.getOperacaoRelacao();' in seletor_op,
      'a resposta certa vem da mesma operação curada em TelaCuradoriaSituacoes (item 21), sem duplicar '
      'lógica de cálculo — só lê o que já foi decidido na curadoria')
check("if (!escolhaCorreta.isEscolhaValida())" in seletor_op,
      'situações antigas, sem operação curada, não ativam o seletor — nada a avaliar')
check('static boolean aplicavel(TipoSituacaoAditiva tipo)' in seletor_op
      and seletor_op.count('TipoSituacaoAditiva.TRANSFORMACAO_RELACAO') >= 1
      and seletor_op.count('TipoSituacaoAditiva.COMPOSICAO_RELACOES') >= 1
      and seletor_op.count('TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES') >= 1,
      'presente nas 3 categorias — decisão da usuária: "coloque nas três, pois essa base bruta de '
      'situações curadas pode aumentar"')
check('preencherPersonagensCurados(loc.texto(chaveExplicacao), situacao)' in seletor_op
      and '.replace("{Personagem_1}", personagem1)' in seletor_op
      and '.replace("{Personagem_2}", personagem2)' in seletor_op
      and '.replace("{Personagem_3}", personagem3)' in seletor_op
      and 'loc.formatar(chaveExplicacao' not in seletor_op,
      'a explicação substitui cada marcador nomeado pelo campo curado homônimo, sem associar personagens '
      'pela posição no diagrama nem inferir seus papéis')
chaves_explicacao_operacao = (
    'operacao.explicacao.transformacaoRelacao.soma',
    'operacao.explicacao.transformacaoRelacao.subtracao',
    'operacao.explicacao.composicaoRelacoes.soma',
    'operacao.explicacao.composicaoRelacoes.subtracao',
    'operacao.explicacao.composicaoTransformacoes.soma',
    'operacao.explicacao.composicaoTransformacoes.subtracao',
)
for lang in ('pt', 'en', 'es', 'fr'):
    explicacoes = [props[lang].get(chave, '') for chave in chaves_explicacao_operacao]
    explicacao_transformacao_relacao = props[lang].get(
        'operacao.explicacao.transformacaoRelacao.subtracao', '')
    check(all('{0}' not in explicacao and '{1}' not in explicacao and '{2}' not in explicacao
              for explicacao in explicacoes)
          and all(marcador in explicacao_transformacao_relacao
                  for marcador in ('{Personagem_1}', '{Personagem_2}', '{Personagem_3}')),
          f'explicações de operação usam campos de personagem nomeados, sem marcadores posicionais ({lang})')
check("mostrarExplicacao = escolha != escolhaCorreta;" in seletor_op,
      'explicação só aparece quando o aluno erra — quando acerta fica silencioso, mesma convenção do '
      'aviso de sinal divergente (item 18)')
check('public void emitirApenasSom()' in feedback_erro,
      'ScaffoldingFeedbackMultissensorialErro ganhou uma variante só de som (sem tremor) para widgets '
      'fixos como o seletor de operação, que não tem uma posição x "tremível" própria')
check('import gerard.ui.vergnaud.SeletorOperacaoRelacaoAluno;' in main
      and 'final SeletorOperacaoRelacaoAluno seletorOperacaoRelacaoAluno = new SeletorOperacaoRelacaoAluno();' in main,
      'Main.java declara e instancia o seletor, mesmo padrão de paineisEixosRelacoes (item 4)')
check(main.count('seletorOperacaoRelacaoAluno.desativar();') >= 2,
      'seletor é desativado nos mesmos pontos de reset de estado que já desativam paineisEixosRelacoes, '
      'para não sobreviver a uma troca de situação/categoria')
check('seletorOperacaoRelacaoAluno.ativar(\n'
      '                    tipoSituacaoSelecionada, situacaoProblemaAtual, elementosVergnaud,\n'
      '                    conectoresVergnaud, SeletorOperacaoRelacaoAluno.TipoOperacaoSeletor.ENTRE_TRANSFORMACOES,\n'
      '                    localizacao);' in main,
      'seletor é (re)ativado ao final de inicializarDiagramaVergnaud, com os elementos e conectores já '
      'posicionados/centralizados na tela — mesma fonte de coordenadas do resto do diagrama')
check('seletorOperacaoRelacaoAluno.desenhar(g2, localizacao);' in main,
      'seletor é desenhado a cada repaint, junto com os painéis de eixo e lupas — mesmo ponto de pintura')
check('if (seletorOperacaoRelacaoAluno.processarPressionamento(x, y)) {' in main
      and 'scaffoldingFeedbackMultissensorialErro.emitirApenasSom();' in main
      and 'OPERACAO_RELACAO_ALUNO' in main,
      'clique nos botões é tratado no mousePressed, com som de erro quando a escolha diverge da curada '
      'e registro de log de pesquisa (C/E) da tentativa')
for chave in (
        'operacao.explicacao.transformacaoRelacao.soma',
        'operacao.explicacao.transformacaoRelacao.subtracao',
        'operacao.explicacao.composicaoRelacoes.soma',
        'operacao.explicacao.composicaoRelacoes.subtracao',
        'operacao.explicacao.composicaoTransformacoes.soma',
        'operacao.explicacao.composicaoTransformacoes.subtracao'):
    for lang in ('pt', 'en', 'es', 'fr'):
        check(chave in sets[lang], f'{chave} presente ({lang})')

print('== Item 22b (2026-08-23): posição do seletor relativa à geometria real do diagrama ==')
check('public void ativar(TipoSituacaoAditiva tipo, SituacaoProblemaAditiva situacao,\n'
      '            List<ElementoVergnaud> elementos, List<ConectorVergnaud> conectores,\n'
      '            TipoOperacaoSeletor papel, ServicoLocalizacao localizacao) {' in seletor_op
      and 'import gerard.campoaditivo.diagrama.elementos.ConectorVergnaud;' in seletor_op,
      'ativar() recebe também os conectores do diagrama — a posição deixa de vir só dos 3 elementos '
      'e passa a usar a mesma geometria que o diagrama já desenha (parâmetro papel acrescentado no '
      'Item 30, para diferenciar as duas operações de Composição de Transformações)')
check('int meioX = (conectorParaRelacaoFinal.x1 + conectorParaRelacaoFinal.x2) / 2;' in seletor_op
      and 'int meioY = (conectorParaRelacaoFinal.y1 + conectorParaRelacaoFinal.y2) / 2;' in seletor_op
      and 'conectorParaRelacaoFinal.temAlvo()' in seletor_op,
      '"a localização de soma e subtração tem que ser em relação ao diagrama" — em Transformação de '
      'Relação e Composição de Relações a posição vem do próprio segmento (seta ou haste da chave) que '
      'o diagrama já desenha em direção à relação final, não de um centróide genérico desalinhado do '
      'layout real (bug reportado: colisão com a lupa da Relação 2 em Composição de Relações)')
check('public void reposicionar(int dx, int dy)' in seletor_op
      and 'centroX += dx;' in seletor_op and 'centroY += dy;' in seletor_op
      and 'areaSoma.translate(dx, dy);' in seletor_op and 'areaSubtracao.translate(dx, dy);' in seletor_op,
      'seletor ganha reposicionar(dx,dy) — sem isso, ao redimensionar a janela o resto do diagrama '
      'acompanhava a nova área mas o seletor ficava para trás, "fixo" (bug reportado pela usuária)')
check('seletorOperacaoRelacaoAluno.reposicionar(dx, dy);' in main,
      'reposicionarDiagramaVergnaudParaAreaAtual (chamado no redimensionamento da janela) também '
      'translada o seletor, no mesmo bloco que já translada elementosVergnaud e conectoresVergnaud')

print('== Item 22c (2026-08-23): acima do segmento (não sobre) e botões mais próximos ==')
check('centroY -= ELEVACAO_ACIMA_DO_SEGMENTO;' in seletor_op,
      '"era para ser em cima e não sobre" — o seletor fica elevado acima do segmento/haste em vez de '
      'centralizado sobre ele')
check('meioX += DESLOCAMENTO_TRACO_CHAVE;' in seletor_op
      and 'DESLOCAMENTO_TRACO_CHAVE = 18' in seletor_op,
      'o ponto usado para centralizar o seletor na Composição de Relações agora corresponde ao traço '
      'vertical real desenhado por ConectorVergnaud.desenharChaveVertical (x+18), não ao x1 bruto do '
      'conector — sem essa correção o rótulo "Soma" caía em cima da linha vertical da chave')
check('ESPACAMENTO_BOTOES = 70' in seletor_op,
      'espaço entre os botões Soma e Subtração reduzido (estava com espaço em excesso, reportado pela '
      'usuária) — mantido o suficiente para os dois rótulos não se tocarem')

print('== Item 23 (2026-08-18): lupa generalizada para todo número relativo ==')
check('private boolean ehElementoNumeroRelativo(ElementoVergnaud elemento)' in main
      and 'elemento.tipo == TipoFiguraDiagrama.ELIPSE' in main,
      'critério estrutural já existente (usado pelo menu de sinal) reaproveitado para decidir quem '
      'ganha painel/lupa — localidade do conhecimento: a própria figura já sabe se é número relativo')
check('for (ElementoVergnaud elemento : elementosVergnaud) {\n'
      '                if (ehElementoNumeroRelativo(elemento)) {\n'
      '                    return true;\n'
      '                }\n            }\n            return false;' in main,
      'devemExibirPaineisEixosRelacoes não filtra mais por TipoSituacaoAditiva — qualquer categoria '
      'com pelo menos um elemento elipse no diagrama atual ganha os painéis')
check(paineis_relacoes.count('elemento.tipo == TipoFiguraDiagrama.ELIPSE') == 1,
      'PaineisEixosRelacoes.ativar filtra por elipse ao criar os Painel, para não dar lupa a uma âncora '
      'de medida (quadrado) que porventura esteja na mesma lista de elementos')
check('import gerard.campoaditivo.diagrama.modelo.TipoFiguraDiagrama;' in paineis_relacoes,
      'PaineisEixosRelacoes importa TipoFiguraDiagrama para o novo filtro')

print('== Item 27 (2026-08-23): rótulos corretos em Composição de Transformações ==')
semantica_curada = text('src/gerard/campoaditivo/curadoria/SemanticaCuradaSituacao.java')
renderizador_composicao_transf = text('src/gerard/campoaditivo/diagrama/servico/RenderizadorComposicaoTransformacoes.java')
check('rotulo1 = loc.texto("papel.estadoInicial");' not in semantica_curada
      and 'rotulo3 = loc.texto("papel.estadoIntermediario");' not in semantica_curada,
      '"quadrado é estado, inicial, intermediário e final. Círculo é transformação, primeira e segunda" '
      '— removido o desvio que rotulava os 3 círculos de transformação com nomes de estado '
      '("Estado inicial"/"Transformação 1"/"Estado intermediário"), bug reportado por screenshot')
check('if (papeis.size() >= 3) {\n'
      '            rotulo1 = papeis.get(0).getRotulo();' in semantica_curada,
      'Composição de Transformações passa a usar o mesmo caminho das demais categorias — papeis já '
      'traz "Transformação 1"/"Transformação 2"/"Transformação final" (mapear()) para os 3 círculos')
check('medida(area.x + 51, area.y + 177, loc.texto("papel.estadoInicial"), 0)' in renderizador_composicao_transf
      and 'medida(area.x + 378, area.y + 177, loc.texto("papel.estadoIntermediario"), 0)' in renderizador_composicao_transf
      and 'medida(area.x + 705, area.y + 177, loc.texto("papel.estadoFinal"), 0)' in renderizador_composicao_transf,
      'os 3 quadrados de estado (medida) ganham rótulo próprio — antes ficavam com "" (sem rótulo '
      'algum), diferente de todo outro renderizador que usa medida() com um rótulo real')

print('== Item 28 (2026-08-23): campos estado_inicial/estado_intermediario/estado_final na curadoria ==')
repositorio = text('src/gerard/campoaditivo/servico/RepositorioSituacoesAditivas.java')
check('private final String estadoIntermediario;' in sit_modelo
      and 'public String getEstadoIntermediario() { return estadoIntermediario; }' in sit_modelo,
      'SituacaoProblemaAditiva ganha o campo estado_intermediario (só relevante para Composição de '
      'Transformações, onde há dois estados internos além do inicial)')
check(sit_modelo.count('String operacaoRelacao, String estadoIntermediario) {') == 1,
      'novo construtor completo acrescenta estadoIntermediario ao final, mesmo padrão usado para '
      'introduzir operacaoRelacao — o overload anterior delega pra este com "", preservando os '
      'chamadores existentes sem alteração')
check('estado_intermediario' in repositorio.split('CABECALHO_CURADORIA = "')[1].split('"')[0],
      'coluna estado_intermediario presente no cabeçalho do TSV de curadoria')
check('String estadoIntermediario = partes.length > 35 ? valor(partes, 35) : "";' in repositorio,
      'leitura do TSV recupera estado_intermediario da nova coluna (35), com fallback vazio para '
      'linhas antigas mais curtas')
check('campo(s.getEstadoIntermediario())' in repositorio,
      'escrita do TSV persiste estado_intermediario')
check('final JTextField campoEstadoIntermediario = campoTexto(linha.estadoIntermediario);' in cur,
      'formulário de curadoria ganha o campo de texto para estado_intermediario')
check('y = adicionarCampo(formulario, gbc, y, "estado_inicial", campoEstadoInicial);\n'
      '            y = adicionarCampo(formulario, gbc, y, "transformacao_1", painelSinalTransformacao1);\n'
      '            y = adicionarCampo(formulario, gbc, y, "estado_intermediario", campoEstadoIntermediario);\n'
      '            y = adicionarCampo(formulario, gbc, y, "transformacao_2", painelSinalTransformacao2);' in cur,
      '"quadrado é estado, inicial, intermediário e final. Círculo é transformação, primeira e segunda" '
      '— formulário de Composição de Transformações mostra os 3 primeiros estados/transformações '
      'intercalados (estado_inicial, transformacao_1, estado_intermediario, transformacao_2), '
      'reaproveitando estado_inicial/estado_intermediario já existentes; estado_final passa a vir depois '
      '(Item 30 — ver bloco reordenado), por ser um valor derivado nesta categoria')
check('linha.estadoIntermediario = campoEstadoIntermediario.getText().trim();' in cur,
      'estado_intermediario é salvo (campo simples, sem painel de sinal — é medida, não número relativo)')
check('if (SimboloDesconhecido.eh(linha.estadoIntermediario)) encontrados.add("estado_intermediario");' in cur,
      'estado_intermediario participa do aviso de "?" digitado diretamente, mesma regra dos demais '
      'campos numéricos curados')
check(cur.count('estadoIntermediario = origem.estadoIntermediario;') == 3,
      'os 3 auxiliares de cópia entre linhas (copiarMetadadosConceituais, copiarLinha, restaurarLinha) '
      'propagam estado_intermediario, mesmo padrão de operacaoRelacao')
check('&& t != TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES) {\n            linha.estadoInicial = "";' in cur
      and '&& t != TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES) {\n            linha.estadoFinal = "";' in cur,
      'limparCamposSemanticosNaoAplicaveis não apaga mais estado_inicial/estado_final de Composição de '
      'Transformações ao salvar — bug que teria zerado os campos recém-adicionados nesta mesma tela')
check('if (t != TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES) {\n            linha.estadoIntermediario = "";' in cur,
      'estado_intermediario é limpo em qualquer categoria que não seja Composição de Transformações')

print('== Item 29 (2026-08-23): resultante recalcula ao vivo, não só ao salvar ==')
check('private void adicionarOuvinteTexto(JTextField campo, Runnable acao) {' in cur,
      'novo auxiliar dispara uma ação a cada alteração de texto de um campo — mesmo padrão já usado '
      'para id/situacao_grupo_id')
check('final PainelValorComSinalCuradoria entradaAPorOperacao =' in cur
      and 'final PainelValorComSinalCuradoria entradaBPorOperacao =' in cur,
      'os dois papéis-dado da operação (relacao_1/2 ou transformacao_1/2, conforme a categoria) ficam '
      'nomeados para alimentar tanto o bloqueio quanto o recálculo da prévia')
check('painelResultantePorOperacao.getCampoMagnitude().setText(\n'
      '                        String.valueOf(Math.abs(resultado)));\n'
      '                painelResultantePorOperacao.getSeletorSinal().setSelectedItem(\n'
      '                        sinalDoInteiro(resultado));' in cur,
      '"a tranformação resultante deveria dar -7" — o campo resultante mostrava um valor salvo antigo '
      'porque só era recalculado ao fechar o diálogo; agora aplicarBloqueioResultantePorOperacao também '
      'atualiza a prévia exibida (magnitude + sinal) sempre que reexecutado')
check('private static OpcaoSinalCuradoria sinalDoInteiro(int valor) {' in cur,
      'sinal do resultante vem direto do valor calculado (>0/<0/==0), nunca de inferência a partir de '
      'texto — Integer.toString(3) não carrega "+", então um resultado positivo sem prefixo explícito '
      'caía em NAO_SELECIONADO ("Selecione o sinal...") tanto na prévia quanto no valor salvo (bug '
      'reportado: Subtração de -2 e -5 dá +3, mas o seletor de sinal ficava vazio)')
check('return PoliticaSinalCuradoria.aplicarSinal(\n'
      '                    String.valueOf(Math.abs(resultado)), sinalDoInteiro(resultado));' in cur,
      'o valor gravado (linha.resultado, o que vai pro TSV) também usa o formato assinado canônico '
      '("+3", não "3") — mesma correção aplicada tanto na prévia quanto na gravação definitiva')
check('entradaAPorOperacao.getSeletorSinal().addActionListener(e -> atualizarValoresCalculados.run());\n'
      '            adicionarOuvinteTexto(entradaAPorOperacao.getCampoMagnitude(), atualizarValoresCalculados);' in cur
      and 'entradaBPorOperacao.getSeletorSinal().addActionListener(e -> atualizarValoresCalculados.run());\n'
      '            adicionarOuvinteTexto(entradaBPorOperacao.getCampoMagnitude(), atualizarValoresCalculados);' in cur,
      'a prévia recalcula ao editar a magnitude OU o sinal de qualquer um dos dois papéis-dado, não só '
      'ao trocar a operação — desde o Item 30, o disparo passa por atualizarValoresCalculados (roda as '
      'duas operações em sequência) em vez de chamar aplicarBloqueioResultantePorOperacao diretamente')
check('import gerard.campoaditivo.curadoria.sinal.OpcaoSinalCuradoria;' in cur
      and 'import gerard.campoaditivo.curadoria.sinal.PoliticaSinalCuradoria;' in cur,
      'imports das classes de sinal usadas para montar o texto assinado da prévia')

print('== Item 30 (2026-08-23): segunda operação — estado_inicial x transformação -> estado_final ==')
check('private final String operacaoEstadoTransformacao;' in sit_modelo
      and 'public String getOperacaoEstadoTransformacao() { return operacaoEstadoTransformacao; }' in sit_modelo,
      '"vai ter que diferenciar dois tipos de operações" — SituacaoProblemaAditiva ganha o campo '
      'operacao_estado_transformacao, distinto de operacaoRelacao (que passa a significar só a operação '
      'entre transformação_1 e transformação_2)')
check(sit_modelo.count('String operacaoRelacao, String estadoIntermediario,\n            String operacaoEstadoTransformacao) {') == 1,
      'novo construtor completo acrescenta operacaoEstadoTransformacao ao final, mesmo padrão usado para '
      'estadoIntermediario e operacaoRelacao — o overload anterior (sem esse parâmetro) delega pra este '
      'com "", preservando os chamadores existentes sem alteração')
check('fragmentoTexto5, fragmentoTexto6, operacaoRelacao, estadoIntermediario, "");' in sit_modelo,
      'o overload anterior (Item 28) delega para o novo construtor completo com operacaoEstadoTransformacao '
      'vazio — nenhum chamador existente precisa mudar')

check('operacao_estado_transformacao' in repositorio.split('CABECALHO_CURADORIA = "')[1].split('"')[0],
      'coluna operacao_estado_transformacao presente no cabeçalho do TSV de curadoria (37ª coluna)')
check('String operacaoEstadoTransformacao = partes.length > 36 ? valor(partes, 36) : "";' in repositorio,
      'leitura do TSV recupera operacao_estado_transformacao da nova coluna (36), com fallback vazio para '
      'linhas antigas mais curtas')
check('operacaoRelacao, estadoIntermediario, operacaoEstadoTransformacao);' in repositorio,
      'parseLinhaSituacao propaga operacaoEstadoTransformacao ao reconstruir SituacaoProblemaAditiva')
check('s.getEstadoIntermediario(), s.getOperacaoEstadoTransformacao());' in repositorio,
      'copiarComVinculo (usado ao gerar uma nova versão/tradução) propaga operacaoEstadoTransformacao')
check('+ "\\t" + campo(s.getOperacaoEstadoTransformacao());' in repositorio,
      'formatarLinhaCuradoria grava operacaoEstadoTransformacao na 37ª coluna do TSV')

check('String operacaoEstadoTransformacao;' in cur,
      'LinhaSituacao (modelo de tela da curadoria) ganha o campo operacaoEstadoTransformacao')
check('l.operacaoEstadoTransformacao = s.getOperacaoEstadoTransformacao();' in cur
      and 'l.operacaoEstadoTransformacao = "";' in cur
      and 'l.estadoIntermediario, l.operacaoEstadoTransformacao));' in cur,
      'ModeloTabelaSituacoes.substituir/adicionarLinha/paraSituacoes leem, inicializam e devolvem '
      'operacaoEstadoTransformacao, mesmo padrão já usado para estadoIntermediario')
check(cur.count('operacaoEstadoTransformacao = origem.operacaoEstadoTransformacao;') == 3,
      'os 3 auxiliares de cópia entre linhas (copiarMetadadosConceituais, copiarLinha, restaurarLinha) '
      'propagam operacaoEstadoTransformacao, mesmo padrão de estadoIntermediario/operacaoRelacao')
check('if (t != TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES) {\n            linha.operacaoEstadoTransformacao = "";' in cur,
      'limparCamposSemanticosNaoAplicaveis apaga operacaoEstadoTransformacao em qualquer categoria que não '
      'seja Composição de Transformações — a segunda operação só existe ali')

check('final JComboBox<OpcaoOperacaoCuradoria> campoOperacaoEstadoTransformacao =\n'
      '                new JComboBox<OpcaoOperacaoCuradoria>(OpcaoOperacaoCuradoria.values());' in cur,
      'novo combo de operação (soma/subtração) entre estado_inicial e transformação_resultante, mesma '
      'API do campoOperacaoRelacao já existente')
check('final Runnable aplicarBloqueioEstadoFinalPorOperacao = () -> {' in cur
      and 'if (!composicaoTransformacoes) return;' in cur
      and 'configurarCampoHerdado(campoEstadoFinal, dicaOperacaoEstadoCalculada, bloqueado);' in cur,
      'estado_final (campo simples, sem painel de sinal nesta categoria) fica somente-leitura e é '
      'recalculado enquanto a segunda operação estiver válida — usa configurarCampoHerdado (mesmo '
      'mecanismo do modo tradução) em vez de PainelValorComSinalCuradoria.definirHerdado, já que não há '
      'painel de sinal para estado_final em Composição de Transformações')
check('int transformacaoResultante =\n'
      '                        Integer.parseInt(painelSinalTransformacaoResultante.obterValorAssinado());' in cur
      and 'Integer resultado = operacao.aplicar(estadoInicial, transformacaoResultante);' in cur,
      'a prévia de estado_final usa a transformação_resultante JÁ CALCULADA pela primeira operação — '
      'não duplica a lógica de soma/subtração entre transformação_1 e transformação_2')
check('final Runnable atualizarValoresCalculados = () -> {\n'
      '            aplicarBloqueioResultantePorOperacao.run();\n'
      '            aplicarBloqueioEstadoFinalPorOperacao.run();\n'
      '        };' in cur,
      'as duas operações recalculam em sequência — a segunda depende do resultado da primeira '
      '(transformação_resultante), então qualquer gatilho de uma delas atualiza as duas prévias')
check(cur.count('atualizarValoresCalculados.run()') >= 5,
      'atualizarValoresCalculados substitui aplicarBloqueioResultantePorOperacao.run() em todos os '
      'gatilhos existentes (operação 1, papéis-dado A/B, tradução herdada) e ganha dois gatilhos novos '
      '(operação 2 e estado_inicial)')

check('y = adicionarCampo(formulario, gbc, y, "operacao_transformacao", campoOperacaoRelacao);\n'
      '            y = adicionarCampo(formulario, gbc, y, "transformacao_resultante", painelSinalTransformacaoResultante);\n'
      '            y = adicionarCampo(formulario, gbc, y, "operacao_estado_transformacao", campoOperacaoEstadoTransformacao);\n'
      '            y = adicionarCampo(formulario, gbc, y, "estado_final", campoEstadoFinal);' in cur,
      'formulário de Composição de Transformações reordenado: estado_final passa a aparecer DEPOIS de '
      'transformacao_resultante e da nova operação — reflete que agora é um valor derivado, não mais '
      'curadoria direta nesta categoria')

check('JComboBox<OpcaoOperacaoCuradoria> campoOperacaoRelacao, JTextField campoEstadoIntermediario,\n'
      '            JComboBox<OpcaoOperacaoCuradoria> campoOperacaoEstadoTransformacao) {' in cur,
      'aplicarCamposDaCuradoriaDetalhada ganha o parâmetro campoOperacaoEstadoTransformacao')
check(cur.count('campoOperacaoRelacao, campoEstadoIntermediario, campoOperacaoEstadoTransformacao);') == 2,
      'os dois pontos que chamam aplicarCamposDaCuradoriaDetalhada (salvar e adicionar tradução) passam '
      'o novo campoOperacaoEstadoTransformacao')
check('linha.operacaoEstadoTransformacao = operacaoEstadoTransformacao.getValorCanonico();' in cur
      and 'Integer estadoFinalCalculado = operacaoEstadoTransformacao.aplicar(\n'
      '                            estadoInicialValor, transformacaoResultanteValor);' in cur,
      'ao salvar, estado_final é recalculado a partir de estado_inicial e da transformacao_resultante já '
      'persistida (linha.resultado) — mesma fonte de verdade usada pela prévia ao vivo')

check('public enum TipoOperacaoSeletor {\n'
      '        ENTRE_TRANSFORMACOES,\n'
      '        ENTRE_ESTADO_E_TRANSFORMACAO\n'
      '    }' in seletor_op,
      'SeletorOperacaoRelacaoAluno ganha um enum para diferenciar as duas operações de Composição de '
      'Transformações — cada uma usa sua própria instância da classe (sem estado compartilhado)')
check('TipoOperacaoSeletor papel, ServicoLocalizacao localizacao) {' in seletor_op
      and 'boolean papelEstadoTransformacao = papelEfetivo == TipoOperacaoSeletor.ENTRE_ESTADO_E_TRANSFORMACAO;' in seletor_op
      and 'if (papelEstadoTransformacao && tipo != TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES) {' in seletor_op,
      'a segunda operação só ativa em Composição de Transformações — nas outras duas categorias, que só '
      'têm uma operação, ativar() com ENTRE_ESTADO_E_TRANSFORMACAO é no-op')
check('String operacaoCurada = papelEstadoTransformacao\n'
      '                ? situacao.getOperacaoEstadoTransformacao()\n'
      '                : situacao.getOperacaoRelacao();' in seletor_op,
      'cada instância lê o campo curado correspondente ao seu papel — a resposta certa nunca se mistura '
      'entre as duas operações')
check('centroX = left(e2) - DESLOCAMENTO_ESQUERDA_ESTADO_TRANSFORMACAO;\n'
      '            centroY = centroY(e2);' in seletor_op,
      '"radiobutton de operações entre estado inicial e transformação do lado esquerdo do círculo '
      'inferior" — segunda operação fica à esquerda de e2 (transformação resultante)')
check('centroX = (centroX(e0) + centroX(e1)) / 2;\n'
      '                centroY = Math.min(top(e0), top(e1)) - ELEVACAO_ACIMA_DO_SEGMENTO;' in seletor_op,
      '"radiobutton de soma e subtração entre tranformação a cima dos dois círculos superiores" — primeira '
      'operação (entre transformação_1 e transformação_2) fica acima de e0/e1 (t1/t2), não mais no vão '
      'abaixo deles como antes do Item 30')
check('if (papel == TipoOperacaoSeletor.ENTRE_ESTADO_E_TRANSFORMACAO) {\n'
      '            // Só existe para Composição de Transformações (ver ativar()).\n'
      '            return soma ? "operacao.explicacao.composicaoTransformacoes.estadoInicialTransformacao.soma"' in seletor_op,
      'chaveExplicacao() ganha um ramo próprio para a segunda operação, com chaves de i18n distintas das '
      'da primeira operação')

check('final SeletorOperacaoRelacaoAluno seletorOperacaoEstadoTransformacaoAluno = new SeletorOperacaoRelacaoAluno();' in main,
      'Main.java instancia uma SEGUNDA SeletorOperacaoRelacaoAluno, independente da primeira — cada '
      'operação de Composição de Transformações tem seu próprio widget/estado')
check(main.count('seletorOperacaoEstadoTransformacaoAluno.desativar();') >= 2,
      'a segunda instância é desativada nos mesmos pontos de reset que a primeira')
check('seletorOperacaoEstadoTransformacaoAluno.ativar(\n'
      '                    tipoSituacaoSelecionada, situacaoProblemaAtual, elementosVergnaud,\n'
      '                    conectoresVergnaud, SeletorOperacaoRelacaoAluno.TipoOperacaoSeletor.ENTRE_ESTADO_E_TRANSFORMACAO,\n'
      '                    localizacao);' in main,
      'a segunda instância é ativada com o papel ENTRE_ESTADO_E_TRANSFORMACAO, junto com a primeira '
      '(ENTRE_TRANSFORMACOES) em inicializarDiagramaVergnaud')
check('seletorOperacaoEstadoTransformacaoAluno.desenhar(g2, localizacao);' in main,
      'a segunda instância é desenhada a cada repaint, junto com a primeira')
check('seletorOperacaoEstadoTransformacaoAluno.reposicionar(dx, dy);' in main,
      'a segunda instância também acompanha o redimensionamento da janela')
check('seletorOperacaoEstadoTransformacaoAluno.processarPressionamento(x, y)) {' in main
      and 'OPERACAO_ESTADO_TRANSFORMACAO_ALUNO' in main,
      'clique na segunda instância é tratado no mousePressed, com seu próprio marcador de log de '
      'pesquisa — distinto de OPERACAO_RELACAO_ALUNO (primeira operação); desde o Item 32, só é '
      'processado depois do primeiro seletor estar correto (ver seção própria)')

chaves_explicacao_estado_transformacao = (
    'operacao.explicacao.composicaoTransformacoes.estadoInicialTransformacao.soma',
    'operacao.explicacao.composicaoTransformacoes.estadoInicialTransformacao.subtracao',
)
for chave in chaves_explicacao_estado_transformacao:
    for lang in ('pt', 'en', 'es', 'fr'):
        check(chave in sets[lang], f'{chave} presente ({lang})')
for lang in ('pt', 'en', 'es', 'fr'):
    explicacoes_estado_transformacao = [props[lang].get(chave, '') for chave in chaves_explicacao_estado_transformacao]
    check(all('{0}' not in explicacao and '{1}' not in explicacao and '{2}' not in explicacao
              for explicacao in explicacoes_estado_transformacao)
          and all('{Personagem_1}' in explicacao for explicacao in explicacoes_estado_transformacao),
          f'explicações da segunda operação usam campo de personagem nomeado, sem marcadores posicionais ({lang})')

print('== Item 31 (2026-08-23): conclusão (azulzinho) só depois da operação correta ==')
check('private boolean operacoesDeSomaSubtracaoRespondidasCorretamente() {' in main
      and 'if (seletorOperacaoRelacaoAluno.estaAtivo()\n'
      '                    && !seletorOperacaoRelacaoAluno.respondeuCorretamente()) {' in main
      and 'if (seletorOperacaoEstadoTransformacaoAluno.estaAtivo()\n'
      '                    && !seletorOperacaoEstadoTransformacaoAluno.respondeuCorretamente()) {' in main,
      '"só deixe azulzinho depois que for escolhida as operações corretamente" — novo método '
      'combina os dois seletores (operação entre transformações e, em Composição de '
      'Transformações, operação entre estado e transformação); seletor inativo (categoria sem '
      'operação, ou situação sem operação curada) não bloqueia nada, sem código específico por '
      'categoria — cobre as 3 categorias com radiobutton soma/subtração automaticamente')
check('boolean modelagemPlenamenteConcluidaAnteriormente = false;' in main,
      'novo campo rastreia a transição para "plenamente concluída" (papéis + operação) de forma '
      'independente da fase interna de ControladorConclusaoModelagem, que só conhece papéis/'
      'posicionamentos e não recalcularia CONCLUIDA_AGORA quando o único gatilho foi a escolha da '
      'operação com os papéis já corretos antes')
check('boolean concluida = controladorConclusaoModelagem.isConcluida()\n'
      '                    && operacoesDeSomaSubtracaoRespondidasCorretamente();' in main
      and 'boolean acabouDeConcluirPlenamente = concluida && !modelagemPlenamenteConcluidaAnteriormente;' in main
      and 'modelagemPlenamenteConcluidaAnteriormente = concluida;' in main,
      'a condição de conclusão exibida (destaque azul + sequência de tip) combina papéis/'
      'posicionamentos com a operação correta; o gatilho do tip usa a transição própria '
      '(acabouDeConcluirPlenamente), não mais a transição bruta do controlador')
check('private void suspenderConclusaoDuranteManipulacao() {\n'
      '            if (!modelagemPlenamenteConcluidaAnteriormente) return;\n'
      '            controladorConclusaoModelagem.reiniciar();\n'
      '            modelagemPlenamenteConcluidaAnteriormente = false;' in main,
      'suspenderConclusaoDuranteManipulacao passa a guardar pela condição combinada (o mesmo '
      'estado que controla o azul exibido), não mais só pelos papéis — evita reiniciar o '
      'controlador sem necessidade quando o azul nunca chegou a aparecer (operação ainda pendente)')
check('private void reiniciarConclusaoModelagem() {\n'
      '            controladorConclusaoModelagem.reiniciar();\n'
      '            modelagemPlenamenteConcluidaAnteriormente = false;' in main,
      'reiniciarConclusaoModelagem (nova situação/categoria) também zera o rastreamento da '
      'condição combinada, evitando um "já concluiu antes" falso na próxima situação')
check(main.count('verificarConclusaoModelagem();') >= 2
      and 'seletorOperacaoRelacaoAluno.obterEscolhaAluno().name(),\n'
      '                        "OBJ4",\n'
      '                        correta\n'
      '                                ? "O aluno escolheu a operação (soma/subtração) que combina os dois '
      'papéis curados."\n'
      '                                : "O aluno escolheu uma operação diferente da curada — explicação '
      'exibida perto do seletor.",\n'
      '                        "OPERACAO_RELACAO_ALUNO",\n'
      '                        correta ? "CORRETO" : "INCORRETO"\n'
      '                );\n'
      '                itemFocado = null;\n'
      '                quadradinhoVennFocado = null;\n'
      '                // Reavalia a conclusão' in main,
      'clicar em qualquer um dos dois seletores de operação reavalia a conclusão da modelagem '
      '(antes só reavaliava ao posicionar/mover um item do diagrama) — sem isso, escolher a '
      'operação certa depois de todos os papéis já corretos nunca disparava o azul')

print('== Item 32 (2026-08-23): ordem entre as duas operações de Composição de Transformações ==')
check('if (seletorOperacaoRelacaoAluno.respondeuCorretamente()) {\n'
      '                seletorOperacaoEstadoTransformacaoAluno.desenhar(g2, localizacao);\n'
      '            }' in main,
      '"a primeira operação é sempre a das transformações, a última é a final" — o segundo '
      'seletor (estado_inicial x transformação_resultante) só é desenhado depois que o primeiro '
      '(transformação_1 x transformação_2) estiver respondido corretamente; nas outras duas '
      'categorias (uma operação só) isso não muda nada, pois o segundo seletor nunca fica ativo '
      'nelas')
check('if (seletorOperacaoRelacaoAluno.respondeuCorretamente()\n'
      '                    && seletorOperacaoEstadoTransformacaoAluno.processarPressionamento(x, y)) {' in main,
      'o clique no segundo seletor só é processado depois do primeiro estar correto — mesma ordem '
      'do desenho, evita reagir a um clique numa área que não está sendo mostrada')

print('== Item 33 (2026-08-23): os 6 elementos de Composição de Transformações são semânticos ==')
catalogo_papeis = text('src/gerard/campoaditivo/semantica/CatalogoPapeisSemanticosAditivos.java')
resolvedor_incognita = text('src/gerard/campoaditivo/curadoria/ResolvedorIncognitaCurada.java')
check('if ("papel.estadoInicial".equals(chavePapel)) return 3;\n'
      '                if ("papel.estadoIntermediario".equals(chavePapel)) return 4;\n'
      '                if ("papel.estadoFinal".equals(chavePapel)) return 5;' in catalogo_papeis
      and 'if (indiceElemento == 3) return "papel.estadoInicial";\n'
      '                if (indiceElemento == 4) return "papel.estadoIntermediario";\n'
      '                if (indiceElemento == 5) return "papel.estadoFinal";' in catalogo_papeis,
      '"deixe todos os elementos como elementos semânticos" — os 3 quadrados de estado (índices '
      '3-5 de elementosVergnaud) ganham papel próprio nos dois sentidos do catálogo; antes caíam '
      'em "papel.valor", que papelValidoParaConclusao rejeita, então o estado inicial aparecia '
      'solto no enunciado, sem vínculo semântico (bug reportado por screenshot)')
check('adicionar(papeis, loc, "papel.estadoInicial", situacao.getEstadoInicial(), situacao.getPersonagem1(), desconhecido);\n'
      '            adicionar(papeis, loc, "papel.estadoIntermediario", situacao.getEstadoIntermediario(), "", desconhecido);\n'
      '            adicionar(papeis, loc, "papel.estadoFinal", situacao.getEstadoFinal(), situacao.getPersonagem3(), desconhecido);' in semantica_curada,
      'os 3 estados entram na semântica curada da categoria, com os valores dos campos de curadoria '
      'criados nos Itens 28/30 — é isso que dá aos números do enunciado um papel para marcar')
_ramo_composicao_transf = semantica_curada.split(
    'tipo == TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES')[1].split(
    'tipo == TipoSituacaoAditiva.TRANSFORMACAO_RELACAO')[0]
check(_ramo_composicao_transf.index('"papel.transformacao1"')
      < _ramo_composicao_transf.index('"papel.transformacao2"')
      < _ramo_composicao_transf.index('"papel.transformacaoFinal"')
      < _ramo_composicao_transf.index('"papel.estadoInicial"'),
      'DENTRO do ramo de Composição de Transformações, os 3 estados ficam DEPOIS das 3 '
      'transformações — aplicarRotulos() usa papeis.get(0/1/2) para rotular os 3 círculos da cena, '
      'então a ordem dos três primeiros não pode mudar (regressão do Item 27)')
check('if (eh(t, "estadoinicial", "inicial")) return "papel.estadoInicial";' in resolvedor_incognita
      and 'if (eh(t, "estadointermediario", "intermediario")) return "papel.estadoIntermediario";' in resolvedor_incognita
      and 'if (eh(t, "estadofinal", "final")) return "papel.estadoFinal";' in resolvedor_incognita
      and 'if ("papel.estadoIntermediario".equals(c)) return "estado_intermediario";' in resolvedor_incognita,
      'qualquer um dos 3 estados pode ser a incógnita curada desta categoria — mapeamento nos dois '
      'sentidos (termo da curadoria <-> chave de papel)')
check('add(r, "papel.estadoInicial", estadoInicial);\n'
      '                add(r, "papel.estadoIntermediario", estadoIntermediario);\n'
      '                add(r, "papel.estadoFinal", estadoFinal);' in resolvedor_incognita,
      'um "?" digitado em qualquer um dos 3 estados é reconhecido como a incógnita, mesma regra já '
      'usada para as 3 transformações')
check('String referido, String referendo, String valorRelativo,\n'
      '            String estadoIntermediario) {' in resolvedor_incognita
      and 'referido, referendo, valorRelativo, "");' in resolvedor_incognita,
      'nova sobrecarga de resolver() acrescenta estado_intermediario ao final, mesmo padrão de '
      'delegação usado no modelo — a sobrecarga anterior delega com "" e nenhum chamador existente '
      'precisa mudar')
check('linha.referido, linha.referendo, linha.valorRelativo,\n'
      '                linha.estadoIntermediario);' in cur,
      'a curadoria passa estado_intermediario ao resolver a incógnita, senão um "?" curado nesse '
      'campo seria silenciosamente ignorado')
check('opcoes = new String[] { "", "estado_inicial", "transformacao_1",\n'
      '                "estado_intermediario", "transformacao_2",\n'
      '                "transformacao_resultante", "estado_final" };' in cur,
      'o combo termo_desconhecido da categoria oferece os 6 papéis (antes só as 3 transformações), '
      'em ordem que segue a história do problema')

print('== Item 34 (2026-08-23): a conclusão cobra os papéis que a curadoria definiu ==')
check('public boolean isExigidoNaModelagem() {\n'
      '            return desconhecido || valor.length() > 0;\n'
      '        }' in semantica_curada,
      'localidade do conhecimento: o próprio PapelCurado responde se a modelagem pode cobrá-lo — '
      'exigível quando tem valor curado OU é a incógnita declarada (campo vazio de propósito). Um '
      'papel sem valor e que não é a incógnita não foi curado')
check('public static boolean papelExigidoNaModelagem(SituacaoProblemaAditiva situacao,\n'
      '            ServicoLocalizacao localizacao, String chave) {\n'
      '        PapelCurado papel = buscar(situacao, localizacao, chave);\n'
      '        return papel != null && papel.isExigidoNaModelagem();\n'
      '    }' in semantica_curada,
      'a consulta delega ao papel curado em vez de reimplementar a regra — quem pergunta não '
      'inspeciona campo nenhum da situação')
check('return SemanticaCuradaSituacao.papelExigidoNaModelagem(\n'
      '                    situacaoProblemaAtual, localizacao, papel.trim());' in main
      and 'situacaoProblemaAtual.getEstadoInicial()' not in main.split(
          'private boolean papelValidoParaConclusao')[1].split('}')[0],
      'Main pergunta a SemanticaCuradaSituacao (dona do conhecimento curado) e NÃO lê campos da '
      'situação nem tem regra por categoria dentro de papelValidoParaConclusao — sem isso, a cena '
      'de Composição de Transformações (6 figuras) cobraria papéis que a curadoria não definiu, '
      'tornando ~90% das situações da categoria impossíveis de concluir')
check('if (!papelDeVerdade || situacaoProblemaAtual == null) {\n'
      '                return papelDeVerdade;\n'
      '            }' in main,
      'sem situação curada carregada (problema digitado livremente) o comportamento anterior é '
      'preservado integralmente — o filtro novo só age quando há curadoria para consultar')
check('if (esperados.isEmpty() || posicionamentos == null' in text(
      'src/gerard/campoaditivo/conclusao/AvaliadorConclusaoModelagem.java'),
      'situação sem nenhum papel curado continua INCOMPLETA (lista de esperados vazia), não passa a '
      'concluir sozinha — guarda que já existia no avaliador e que este item depende de preservar')

print('== Item 35 (2026-08-23): o campo que é a incógnita nunca é travado nem sobrescrito ==')
check('final java.util.function.Predicate<String> ehIncognitaAtual = chavePapel -> {' in cur
      and 'new ResolvedorIncognitaCurada().chaveSemanticaDoTermo(\n'
      '                    termo == null ? "" : termo.toString(), tipoSemantico);' in cur,
      '"qualquer campo pode ser incógnita" — predicado único responde se um papel é a incógnita '
      'escolhida, lendo a seleção VIVA do combo e delegando a tradução termo -> chave de papel a '
      'ResolvedorIncognitaCurada; a tela não reimplementa esse mapeamento')
check('&& !(chavePapelResultante != null && ehIncognitaAtual.test(chavePapelResultante));' in cur,
      'transformacao_resultante / relacao_resultante deixam de ser travados quando são a incógnita '
      'curada (ex.: figurinhas, com termo_desconhecido = transformacao_resultante) — antes o '
      'cálculo automático travava e sobrescrevia justamente o campo que o pesquisador precisa '
      'deixar em aberto')
check('&& !ehIncognitaAtual.test("papel.estadoFinal");' in cur,
      'estado_final também destrava quando é a incógnita — motivo original do pedido: "esse foi o '
      'motivo para eu não querer bloquear o campo. Ele pode ser uma incógnita"')
check('campoTermoDesconhecido.addActionListener(e -> atualizarValoresCalculados.run());' in cur,
      'trocar a incógnita no combo libera/retoma o bloqueio na hora, sem precisar reabrir o diálogo')
check('boolean resultanteEhIncognita = chaveIncognitaAoSalvar != null' in cur
      and 'if (operacaoRelacao.isEscolhaValida() && !resultanteEhIncognita) {' in cur
      and '&& !estadoFinalEhIncognita' in cur,
      'a mesma proteção vale no salvamento — sem ela, fechar o diálogo apagaria o campo deixado em '
      'aberto, mesmo com o formulário mostrando-o destravado')
check('final JComboBox<String> campoTermoDesconhecido = comboTermoDesconhecido(tipoSemantico, linha.termoDesconhecido);' in cur
      and cur.index('final JComboBox<String> campoTermoDesconhecido')
          < cur.index('final Runnable aplicarBloqueioResultantePorOperacao'),
      'o combo da incógnita é declarado ANTES dos cálculos automáticos, que dependem dele — '
      'ordem necessária para os dois Runnables poderem consultá-lo')
check('public boolean incognitaSemValorCurado() {\n'
      '            return possuiIncognita() && valorCuradoDaIncognita.length() == 0;\n'
      '        }' in resolvedor_incognita
      and 'public String getValorCuradoDaIncognita() { return valorCuradoDaIncognita; }' in resolvedor_incognita,
      '"vazio apenas antes da finalização, após tem que estar preenchido corretamente" — quem sabe '
      'o valor curado da incógnita é o resolvedor, que já a identifica; a tela não redescobre isso')
check('if (resolucaoIncognita.incognitaSemValorCurado()) {' in cur
      and 'RegistroErrosCuradoria.registrar("INCOGNITA_SEM_VALOR_CURADO",' in cur,
      'salvar com a incógnita declarada mas vazia é bloqueado, no mesmo padrão das validações já '
      'existentes (mensagem + RegistroErrosCuradoria + VOLTAR_E_CORRIGIR) — sem o valor curado, '
      'AvaliadorConclusaoModelagem aceitaria qualquer número do aluno como certo')
check('Resultado(String chaveExplicita, String chaveEfetiva,\n'
      '                String termoCuradoriaEfetivo,\n'
      '                List<String> chavesMarcadasComInterrogacao,\n'
      '                boolean conflito) {\n'
      '            this(chaveExplicita, chaveEfetiva, termoCuradoriaEfetivo,\n'
      '                    chavesMarcadasComInterrogacao, conflito, "");' in resolvedor_incognita,
      'o construtor anterior de Resultado delega para o novo com "" — mesmo padrão de sobrecarga '
      'usado no modelo, nenhum chamador existente muda')

print('== Item 36 (2026-08-23): "Ver dica" avisa quando falta o seletor de operação ==')
check('private boolean existeSeletorOperacaoPendenteParaDica() {\n'
      '            return obterProximoPapelNaoResolvidoParaDica() == null\n'
      '                    && !operacoesDeSomaSubtracaoRespondidasCorretamente();\n'
      '        }' in main,
      '"e se o usuário não for notificado que tem que escolher a operação e ficar esperando '
      'infinitamente?" — novo predicado responde se não resta papel-dado pendente mas ainda falta '
      'responder (ou foi respondido errado) algum seletor de soma/subtração ativo')
check('boolean exibir = categoriaSelecionadaParaAtividade\n'
      '                    && !elementosVergnaud.isEmpty()\n'
      '                    && (obterProximoPapelNaoResolvidoParaDica() != null\n'
      '                            || existeSeletorOperacaoPendenteParaDica());' in main,
      'o botão "Ver dica" continua visível quando só falta responder o seletor de operação — antes '
      'ele simplesmente sumia nesse momento, sem avisar nada')
check('if (existeSeletorOperacaoPendenteParaDica()) {\n'
      '                    mostrarDicaSeletorOperacaoPendente();\n'
      '                }\n'
      '                return;' in main,
      'clicar em "Ver dica" quando só falta o seletor mostra a dica nova, em vez de não fazer nada')
check('private void mostrarDicaSeletorOperacaoPendente() {\n'
      '            JOptionPane.showMessageDialog(this,\n'
      '                    localizacao.texto("ui.hint.pendingOperationSelector"),' in main
      and 'registrarFeedbackExibido("AG_AE",\n'
      '                    gerard.dominio.campoaditivo.ModalidadeEntregaScaffolding.VISUAL,\n'
      '                    "dica: falta responder o seletor de soma/subtracao pendente");' in main,
      'mesmo padrão de exibição (JOptionPane) e de auditoria (registrarFeedbackExibido) já usado por '
      'mostrarDicaOperacaoIncognita — não introduz um terceiro estilo de dica')
for lang in ('pt', 'en', 'es', 'fr'):
    check(len(props[lang].get('ui.hint.pendingOperationSelector', '')) > 0,
          f'chave ui.hint.pendingOperationSelector presente e não vazia em {lang}')

if errors:
    print(f'REPROVADO: {len(errors)} falha(s) no total.'); sys.exit(1)
print('APROVADO: verificador de regressão completo, nenhuma falha registrada.')
