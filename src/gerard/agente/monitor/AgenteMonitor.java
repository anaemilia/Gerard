package gerard.agente.monitor;

import gerard.Scaffolding.questionamento.ResultadoQuestionamento;
import gerard.Scaffolding.questionamento.ScaffoldingQuestionamento;
import gerard.agente.conhecimento.ConclusaoVencedora;
import gerard.agente.conhecimento.LeitorBaseConhecimentoGerard;
import gerard.agente.conhecimento.MotorRegrasConhecimento;
import gerard.agente.conhecimento.RegraConhecimento;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Extração nomeada do ponto único onde o Gérard já decide se uma ação do
 * usuário está certa ou errada (ver gerard-ajuda-adaptativa/references/
 * agente-monitor.md — proposta teórica cujo papel é justamente este).
 *
 * Esta classe NÃO reimplementa a comparação: delega inteiramente para
 * ScaffoldingQuestionamento.avaliarPosicionamento, que já existia e continua
 * inalterada. A única coisa nova é notificar observadores (ex.: um
 * indicador visual) a cada veredito aplicável, sem alterar o resultado nem
 * o comportamento de feedback já consolidado (tremor/som/cor).
 */
public final class AgenteMonitor {
    private final ScaffoldingQuestionamento scaffoldingQuestionamento;
    private final List<OuvinteVeredictoAgenteMonitor> ouvintes =
            new ArrayList<OuvinteVeredictoAgenteMonitor>();
    private final List<OuvinteAuditoriaAgenteMonitor> ouvintesAuditoria =
            new ArrayList<OuvinteAuditoriaAgenteMonitor>();

    // Base de conhecimento integrada (ver dados/base_conhecimento_gerard_jsons/
    // base_conhecimento_gerard/) — carregada uma vez, consultada pelos
    // métodos abaixo. Aditivo: nenhum método de avaliação existente
    // (avaliarPosicionamento/avaliarCategoria/avaliarValorIncognita) foi
    // alterado por causa disso.
    private final MotorRegrasConhecimento motorRegrasConhecimento = new MotorRegrasConhecimento();
    private final List<RegraConhecimento> regrasDominio = new LeitorBaseConhecimentoGerard().lerDominio();

    public AgenteMonitor(ScaffoldingQuestionamento scaffoldingQuestionamento) {
        this.scaffoldingQuestionamento = scaffoldingQuestionamento;
    }

    public void adicionarOuvinte(OuvinteVeredictoAgenteMonitor ouvinte) {
        if (ouvinte != null) {
            ouvintes.add(ouvinte);
        }
    }

    public void removerOuvinte(OuvinteVeredictoAgenteMonitor ouvinte) {
        ouvintes.remove(ouvinte);
    }

    /** Canal adicional pro log estruturado de auditoria — ver OuvinteAuditoriaAgenteMonitor. */
    public void adicionarOuvinteAuditoria(OuvinteAuditoriaAgenteMonitor ouvinte) {
        if (ouvinte != null) {
            ouvintesAuditoria.add(ouvinte);
        }
    }

    public void removerOuvinteAuditoria(OuvinteAuditoriaAgenteMonitor ouvinte) {
        ouvintesAuditoria.remove(ouvinte);
    }

    public ResultadoQuestionamento avaliarPosicionamento(
            String chavePapelNumeral,
            String chavePapelAlvo,
            String papelDoElementoNoDiagrama,
            String categoriaEscolhida) {
        return avaliarPosicionamento(chavePapelNumeral, chavePapelAlvo, papelDoElementoNoDiagrama,
                categoriaEscolhida, null);
    }

    /**
     * Sobrecarga que também recebe a categoria como {@link TipoSituacaoAditiva}
     * (correção 2026-07-31, pedida pela usuária): a versão só-com-String acima
     * não conseguia consultar as regras de domínio (R-DOM-*) porque
     * categoriaEscolhida chega como texto localizado (descricaoTipo). O
     * veredito continua vindo do mesmo lugar de sempre
     * (ScaffoldingQuestionamento.avaliarPosicionamento, inalterado) — esta
     * sobrecarga só enriquece rules_fired com as regras de domínio que se
     * aplicam à categoria, marcadas usedInFinalDecision=false (a decisão
     * final continua sendo a comparação determinística de papéis; as regras
     * de domínio aqui descrevem o CONTEXTO — quais papéis são válidos nesta
     * categoria — não decidem o veredito em si).
     */
    public ResultadoQuestionamento avaliarPosicionamento(
            String chavePapelNumeral,
            String chavePapelAlvo,
            String papelDoElementoNoDiagrama,
            String categoriaEscolhida,
            TipoSituacaoAditiva categoriaEnum) {
        long inicio = System.currentTimeMillis();
        ResultadoQuestionamento resultado = scaffoldingQuestionamento.avaliarPosicionamento(
                chavePapelNumeral, chavePapelAlvo, papelDoElementoNoDiagrama, categoriaEscolhida);
        boolean aplicavel = resultado != null && resultado.isAplicavel();
        if (aplicavel) {
            notificar(resultado.isCorreto());
        }
        if (!ouvintesAuditoria.isEmpty()) {
            boolean correto = aplicavel && resultado.isCorreto();
            String justificativa;
            List<gerard.agente.conhecimento.RuleActivationAudit> regrasAtivadas;
            if (categoriaEnum == null) {
                justificativa = "Comparação determinística entre o papel semântico do numeral e o papel do "
                        + "destino (ScaffoldingQuestionamento.papeisCompativeis). Regras de domínio (R-DOM-*) não "
                        + "puderam ser consultadas aqui: nenhuma TipoSituacaoAditiva foi informada a esta chamada "
                        + "(quem chamou usou a sobrecarga só-com-texto).";
                regrasAtivadas = Collections.<gerard.agente.conhecimento.RuleActivationAudit>emptyList();
            } else {
                justificativa = "Comparação determinística entre o papel semântico do numeral e o papel do "
                        + "destino (ScaffoldingQuestionamento.papeisCompativeis) — regras de domínio abaixo "
                        + "descrevem os papéis válidos para " + categoriaEnum.name() + " como contexto, mas não "
                        + "decidem o veredito (a comparação de papéis continua sendo a fonte da decisão).";
                regrasAtivadas = construirRegrasDominioContexto(categoriaEnum);
            }
            MonitorAuditData dados = new MonitorAuditData(
                    "POSICIONAR", chavePapelNumeral, chavePapelAlvo,
                    categoriaEnum == null ? categoriaEscolhida : categoriaEnum.name(), null,
                    !aplicavel ? null : (correto ? "C" : "E"),
                    !aplicavel || correto ? null : "posicionamento_semantico",
                    chavePapelNumeral, chavePapelAlvo, null, 1.0,
                    justificativa,
                    regrasAtivadas,
                    !aplicavel ? "MONITOR_ACAO_NAO_APLICAVEL" : (correto ? "MONITOR_ACAO_CORRETA" : "MONITOR_ACAO_INCORRETA"),
                    correto, aplicavel && !correto, aplicavel, aplicavel,
                    System.currentTimeMillis() - inicio, null);
            notificarAuditoria(dados);
        }
        return resultado;
    }

    private List<gerard.agente.conhecimento.RuleActivationAudit> construirRegrasDominioContexto(
            TipoSituacaoAditiva categoria) {
        List<gerard.agente.conhecimento.RuleActivationAudit> regras =
                new ArrayList<gerard.agente.conhecimento.RuleActivationAudit>();
        for (RegraConhecimento regra : motorRegrasConhecimento.regrasQueBatem(fatosCategoria(categoria), regrasDominio)) {
            regras.add(gerard.agente.conhecimento.RuleActivationAudit.deRegraQueBateu(regra, false,
                    "Descreve o contexto (papéis válidos/invariante aditivo da categoria); o veredito de "
                            + "certo/errado continua vindo da comparação determinística de papéis, não desta regra."));
        }
        return regras;
    }

    /**
     * Avalia a escolha da categoria (comparação/transformação/composição)
     * de uma situação-problema já apresentada — a primeira ação avaliável do
     * usuário, antes de qualquer posicionamento no diagrama (ver
     * gerard-ajuda-adaptativa/references/agente-zdp.md, seção "Diários de
     * bordo do doutorado": os diários de 2010 já registravam erro de
     * categorização como distinto do erro de posicionamento, e esse tipo de
     * ação não tinha veredito certo/errado em lugar nenhum até agora).
     * Comparação direta (sem ScaffoldingQuestionamento): não há papel-alvo
     * dentro do diagrama a checar aqui, só a categoria escolhida contra a
     * categoria real da situação-problema.
     */
    public boolean avaliarCategoria(TipoSituacaoAditiva categoriaEscolhida, TipoSituacaoAditiva categoriaCorreta) {
        long inicio = System.currentTimeMillis();
        boolean correto = categoriaEscolhida != null && categoriaEscolhida == categoriaCorreta;
        notificar(correto);
        if (!ouvintesAuditoria.isEmpty()) {
            MonitorAuditData dados = new MonitorAuditData(
                    "SELECIONAR_CATEGORIA",
                    String.valueOf(categoriaEscolhida), null, String.valueOf(categoriaCorreta), null,
                    correto ? "C" : "E", correto ? null : "categoria_semantica",
                    String.valueOf(categoriaCorreta), String.valueOf(categoriaEscolhida), null, 1.0,
                    "Comparação direta entre a categoria escolhida e a categoria real da situação-problema "
                            + "(sem ScaffoldingQuestionamento — não há papel-alvo dentro do diagrama a checar aqui). "
                            + "Regras de domínio (R-DOM-*) validam papéis DENTRO de uma categoria já certa, não a "
                            + "escolha da categoria em si — nenhuma regra da base se aplica diretamente a esta decisão.",
                    Collections.<gerard.agente.conhecimento.RuleActivationAudit>emptyList(),
                    correto ? "MONITOR_ACAO_CORRETA" : "MONITOR_ACAO_INCORRETA",
                    correto, !correto, true, true,
                    System.currentTimeMillis() - inicio, null);
            notificarAuditoria(dados);
        }
        return correto;
    }

    /**
     * Avalia a resposta ao diálogo Sim/Não que segue um clique de categoria
     * errado (ver Main.mostrarQuestionamentoCategoriaErrada) — pergunta se a
     * definição da categoria ERRADA clicada se aplica à situação-problema.
     * Sinal distinto de avaliarCategoria: a pessoa pode acertar o
     * reconhecimento no clique (percebe que errou, tenta outro ícone) ou
     * pode insistir respondendo "Sim" (concorda que a definição errada se
     * aplica), um segundo tipo de erro consecutivo — não é o mesmo evento
     * repetido, é a pessoa reafirmando a crença errada quando questionada
     * sobre ela. Concordar com a categoria errada é o erro; discordar é o
     * reconhecimento correto.
     */
    public boolean avaliarConfirmacaoCategoriaErrada(boolean concordouComCategoriaErrada) {
        boolean correto = !concordouComCategoriaErrada;
        notificar(correto);
        return correto;
    }

    /**
     * Avalia o valor digitado numa incógnita (preenchimento de resultado
     * calculado, ex.: digitar "8" depois de calcular 4+4) — terceiro tipo de
     * ação avaliável, distinto de avaliarPosicionamento (arrastar um número
     * do enunciado) e avaliarCategoria. A comparação em si já existia em
     * Main.valorDigitadoCorrespondeAoCurado desde antes; esta classe só
     * passou a ser notificada dela em 2026-07-30, quando a usuária apontou
     * que os quadros do mestrado já tinham C/E codificado pelo próprio
     * pesquisador para esse tipo de ação e nada no pipeline dos três agentes
     * usava isso — mesmo padrão de lacuna já fechado para categoria.
     */
    public boolean avaliarValorIncognita(boolean correto) {
        long inicio = System.currentTimeMillis();
        notificar(correto);
        if (!ouvintesAuditoria.isEmpty()) {
            MonitorAuditData dados = new MonitorAuditData(
                    "TEXTO", null, null, null, null,
                    correto ? "C" : "E", correto ? null : "calculo_ou_digitacao",
                    null, null, null, 1.0,
                    "Comparação feita por quem chama (Main.valorDigitadoCorrespondeAoCurado, contra o valor "
                            + "curado da situação) — AgenteMonitor só recebe o booleano já decidido, sem o valor "
                            + "digitado nem o papel da incógnita nesta assinatura.",
                    Collections.<gerard.agente.conhecimento.RuleActivationAudit>emptyList(),
                    correto ? "MONITOR_ACAO_CORRETA" : "MONITOR_ACAO_INCORRETA",
                    correto, !correto, true, true,
                    System.currentTimeMillis() - inicio, null);
            notificarAuditoria(dados);
        }
        return correto;
    }

    /**
     * Avalia a escolha do sinal (+/-) de um número relativo — quarto tipo de
     * ação avaliável, distinto dos três acima. A comparação em si (sinal
     * escolhido vs. sinal do valor curado do papel) já é feita por quem
     * chama (ver Main.sinalEscolhidoCorrespondeAoCurado), no mesmo padrão de
     * avaliarValorIncognita/Main.valorDigitadoCorrespondeAoCurado; esta
     * classe só passou a ser notificada em 2026-07-31. Antes disso, o menu
     * de sinal (ScaffoldingNumeroRelativo) resolvia a escolha sozinho, sem
     * notificar AgenteMonitor/AgenteZDP/AgenteModelador — mesma lacuna já
     * fechada para categoria e incógnita.
     */
    public boolean avaliarSinalNumeroRelativo(boolean correto) {
        long inicio = System.currentTimeMillis();
        notificar(correto);
        if (!ouvintesAuditoria.isEmpty()) {
            MonitorAuditData dados = new MonitorAuditData(
                    "SELECIONAR_SINAL", null, null, null, null,
                    correto ? "C" : "E", correto ? null : "sinal_numero_relativo",
                    null, null, null, 1.0,
                    "Comparação feita por quem chama (Main.sinalEscolhidoCorrespondeAoCurado, contra o sinal "
                            + "do valor curado do papel) — AgenteMonitor só recebe o booleano já decidido. "
                            + "R-DOM-SINAL-* (regras de sinal ganho/perda) ainda não são consultadas aqui: "
                            + "exigiriam expor evento_semantico (ganho/perda) a este método, mudança maior e "
                            + "separada.",
                    Collections.<gerard.agente.conhecimento.RuleActivationAudit>emptyList(),
                    correto ? "MONITOR_ACAO_CORRETA" : "MONITOR_ACAO_INCORRETA",
                    correto, !correto, true, true,
                    System.currentTimeMillis() - inicio, null);
            notificarAuditoria(dados);
        }
        return correto;
    }

    /**
     * Percebe uma ação instrumental que não é avaliável como certo/errado
     * (ex.: Selecionar um valor do enunciado, antes de qualquer
     * posicionamento — não há papel-alvo ainda para comparar). Usa o mesmo
     * canal de notificação de avaliarPosicionamento (o LED não distingue
     * certo/errado de qualquer forma), mas sem produzir veredito: o
     * argumento de aoAvaliar aqui não representa um julgamento, só mantém a
     * mesma assinatura de notificação.
     */
    public void perceberAcao() {
        notificar(true);
    }

    /**
     * Papéis semânticos válidos para uma categoria, segundo as regras de
     * domínio da base de conhecimento integrada (R-DOM-COMP-001,
     * R-DOM-TRANS-001, R-DOM-COMP-003 — ver regras_dominio.jsonl). Só cobre
     * composição/transformação/comparação simples: as categorias compostas
     * sem regra de domínio específica não produzem avaliação automática
     * correspondente na base ainda e devolvem lista vazia — não é erro, é o
     * escopo atual da base.
     */
    @SuppressWarnings("unchecked")
    public List<String> papeisValidosPara(TipoSituacaoAditiva categoria) {
        ConclusaoVencedora vencedora = motorRegrasConhecimento.melhorConclusao(
                fatosCategoria(categoria), regrasDominio, "papeis_validos");
        if (vencedora == null || !(vencedora.getValor() instanceof List)) {
            return Collections.emptyList();
        }
        List<String> papeis = new ArrayList<String>();
        for (Object item : (List<Object>) vencedora.getValor()) {
            papeis.add(String.valueOf(item));
        }
        return Collections.unmodifiableList(papeis);
    }

    /**
     * Invariante aditivo da categoria (ex. "parte_1 + parte_2 = todo"),
     * segundo R-DOM-COMP-002/R-DOM-TRANS-002/R-DOM-COMP-004. Null se a
     * categoria não tiver regra de domínio correspondente.
     */
    public String invarianteAditivoPara(TipoSituacaoAditiva categoria) {
        ConclusaoVencedora vencedora = motorRegrasConhecimento.melhorConclusao(
                fatosCategoria(categoria), regrasDominio, "relacao_aditiva");
        return vencedora == null ? null : String.valueOf(vencedora.getValor());
    }

    private Map<String, Object> fatosCategoria(TipoSituacaoAditiva categoria) {
        Map<String, Object> fatos = new LinkedHashMap<String, Object>();
        String valor = valorCategoriaParaRegras(categoria);
        if (valor != null) {
            fatos.put("categoria", valor);
        }
        return fatos;
    }

    /**
     * Mapeia o enum interno para o vocabulário das regras da base de
     * conhecimento (ver ontologia_gerard.json, "categorias"). Só as 3
     * categorias simples têm regra de domínio correspondente hoje.
     */
    private String valorCategoriaParaRegras(TipoSituacaoAditiva categoria) {
        if (categoria == null) {
            return null;
        }
        switch (categoria) {
            case COMPOSICAO_MEDIDAS:
                return "composicao";
            case TRANSFORMACAO_MEDIDAS:
                return "transformacao";
            case COMPARACAO_MEDIDAS:
                return "comparacao";
            default:
                return null;
        }
    }

    private void notificar(boolean correto) {
        for (OuvinteVeredictoAgenteMonitor ouvinte : ouvintes) {
            ouvinte.aoAvaliar(correto);
        }
    }

    private void notificarAuditoria(MonitorAuditData dados) {
        for (OuvinteAuditoriaAgenteMonitor ouvinte : ouvintesAuditoria) {
            ouvinte.aoAvaliar(dados);
        }
    }
}
