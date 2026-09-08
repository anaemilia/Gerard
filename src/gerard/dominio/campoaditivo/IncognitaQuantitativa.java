package gerard.dominio.campoaditivo;

import gerard.adaptacao.ContextoAdaptativoUsuario;
import gerard.adaptacao.DecisaoAjuda;
import gerard.adaptacao.EscopoProprietarioSemantico;
import gerard.adaptacao.ItemRepertorioAjuda;
import gerard.adaptacao.ProprietarioRepertorioAjuda;
import gerard.adaptacao.RegraAdaptativaPublicada;
import gerard.adaptacao.RepertorioAjuda;
import gerard.adaptacao.modelousuario.DiagnosticoTarefaProjetado;
import gerard.adaptacao.modelousuario.DimensaoModeloUsuario;
import gerard.adaptacao.modelousuario.HistoricoDiagnosticosProjetado;
import gerard.adaptacao.modelousuario.NiveisTarefasProjetados;
import gerard.adaptacao.modelousuario.ProjecaoModeloUsuario;
import gerard.agente.modelousuario.NivelComplexidadeTarefa;
import gerard.agente.modelousuario.NivelSuporte;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.dominio.atividade.ContextoAcaoInstrumental;
import gerard.dominio.atividade.RegistroAcaoInstrumental;
import gerard.dominio.atividade.ResultadoAvaliacaoAcaoInstrumental;
import gerard.dominio.atividade.TarefaInteracao;
import gerard.semantica.numero.ValorNumerico;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Proprietário semântico do papel designado como incógnita original.
 * Avalia as ações que atribuem valor à incógnita, produz seu registro único
 * e escolhe ajuda somente no próprio repertório. Não conhece Swing nem
 * persistência.
 */
public final class IncognitaQuantitativa
        implements ProprietarioRepertorioAjuda<FatosSelecaoAjudaIncognita> {

    public static final String CHAVE_PROPRIETARIO = "papel.incognita";
    public static final String CONDICAO_DIAGNOSTICO_FACTUAL = "diagnostico_factual";
    public static final String CONDICAO_ORDEM_REJEICAO = "ordem_rejeicao";
    public static final String CONDICAO_CATEGORIA = "categoria";
    public static final String CONDICAO_PAPEL_ALVO = "papel_alvo";
    public static final String CONDICAO_NIVEL_TAREFA = "nivel_tarefa";
    public static final String CONDICAO_SUPORTE_ANTERIOR = "suporte_anterior";

    private static final Set<DimensaoModeloUsuario> DIMENSOES_RELEVANTES =
            Collections.unmodifiableSet(EnumSet.of(
                    DimensaoModeloUsuario.NIVEL_TAREFAS,
                    DimensaoModeloUsuario.DIAGNOSTICO_TAREFA));

    private final String chavePapelDesignado;
    private final TipoSituacaoAditiva categoria;
    private final PapelQuantitativo fluxoTentativas;
    private final RepertorioAjuda repertorio;

    public IncognitaQuantitativa(
            String chavePapelDesignado,
            TipoSituacaoAditiva categoria,
            PapelQuantitativo fluxoTentativas) {
        this.chavePapelDesignado = obrigatorio(
                chavePapelDesignado, "papel designado não pode ser vazio");
        if (categoria == null || fluxoTentativas == null) {
            throw new IllegalArgumentException(
                    "categoria e fluxo de tentativas são obrigatórios");
        }
        this.categoria = categoria;
        this.fluxoTentativas = fluxoTentativas;
        this.repertorio = criarRepertorioLocal();
    }

    public String getChavePapelDesignado() { return chavePapelDesignado; }
    public TipoSituacaoAditiva getCategoria() { return categoria; }
    public PapelQuantitativo getFluxoTentativas() { return fluxoTentativas; }

    /** A correspondência numérica pertence à incógnita, não à interface. */
    public Boolean correspondeAoEsperado(
            ValorNumerico valorProposto,
            ValorNumerico valorEsperado) {
        if (valorProposto == null || valorEsperado == null
                || !valorProposto.ehConhecido() || !valorEsperado.ehConhecido()) {
            return null;
        }
        return Boolean.valueOf(
                valorProposto.valorOuNull().equals(valorEsperado.valorOuNull()));
    }

    /**
     * Constitui e avalia uma única ação TEXTO. A mesma decisão atualiza a
     * sequência de rejeições do papel e retorna todas as identidades para os
     * eventos derivados, sem criar uma ação adicional.
     */
    public RegistroAcaoInstrumental avaliarAcaoTexto(
            IdentidadeAcaoInstrumentalPapel identidade,
            ValorNumerico valorProposto,
            ValorNumerico valorEsperado,
            ContextoAcaoInstrumental contextoInstrumental) {
        return avaliarAcao(
                identidade,
                TarefaInteracao.TEXTO,
                valorProposto,
                valorEsperado,
                contextoInstrumental);
    }

    /**
     * Constitui e avalia uma única ação que atribui valor à incógnita. O
     * protocolo informa apenas a tarefa instrumental observada; a comparação,
     * o diagnóstico e a sequência de rejeições continuam pertencendo à
     * incógnita.
     */
    public RegistroAcaoInstrumental avaliarAcao(
            IdentidadeAcaoInstrumentalPapel identidade,
            TarefaInteracao tarefaInteracao,
            ValorNumerico valorProposto,
            ValorNumerico valorEsperado,
            ContextoAcaoInstrumental contextoInstrumental) {
        if (tarefaInteracao == null) {
            throw new IllegalArgumentException("tarefa de interação é obrigatória");
        }
        ResultadoAvaliacaoAcaoInstrumental resultado;
        DiagnosticoErroPapel diagnostico = null;
        String regra;

        Boolean corresponde = correspondeAoEsperado(valorProposto, valorEsperado);
        if (corresponde == null) {
            resultado = ResultadoAvaliacaoAcaoInstrumental.NAO_APLICAVEL;
            regra = "regra.incognita.semCriterioDisponivel";
        } else if (!fluxoTentativas.aceita(valorProposto)) {
            resultado = ResultadoAvaliacaoAcaoInstrumental.ERRADA;
            diagnostico = diagnosticoValorForaDoDominio();
            regra = "regra.papel.dominioNumerico";
        } else if (corresponde.booleanValue()) {
            resultado = ResultadoAvaliacaoAcaoInstrumental.CORRETA;
            regra = "regra.incognita.correspondenciaValorEsperado";
        } else {
            resultado = ResultadoAvaliacaoAcaoInstrumental.ERRADA;
            diagnostico = diagnosticoValorIncorreto();
            regra = "regra.incognita.correspondenciaValorEsperado";
        }

        ResultadoRegistroTentativaPapel resultadoTentativa = null;
        if (resultado != ResultadoAvaliacaoAcaoInstrumental.NAO_APLICAVEL) {
            Optional<DiagnosticoErroPapel> diagnosticoTentativa = diagnostico == null
                    ? Optional.<DiagnosticoErroPapel>empty()
                    : Optional.of(diagnostico);
            resultadoTentativa = fluxoTentativas.registrarTentativaComIdentidade(
                    identidade,
                    diagnosticoTentativa,
                    ContextoAcao.NAO_INFORMADO,
                    valorProposto);
        }

        return new RegistroAcaoInstrumental(
                identidade,
                tarefaInteracao,
                categoria,
                CHAVE_PROPRIETARIO,
                chavePapelDesignado,
                resultado,
                diagnostico,
                valorProposto,
                valorEsperado,
                regra,
                contextoInstrumental,
                resultadoTentativa);
    }

    @Override
    public String chaveProprietarioSemantico() { return CHAVE_PROPRIETARIO; }

    @Override
    public EscopoProprietarioSemantico escopoAdaptativo() {
        return EscopoProprietarioSemantico.PAPEL;
    }

    @Override
    public Set<DimensaoModeloUsuario> dimensoesModeloUsuarioRelevantes() {
        return DIMENSOES_RELEVANTES;
    }

    @Override
    public RepertorioAjuda repertorioAjuda() { return repertorio; }

    @Override
    public DecisaoAjuda selecionarAjuda(
            FatosSelecaoAjudaIncognita fatos,
            ContextoAdaptativoUsuario contexto) {
        if (fatos == null) {
            throw new IllegalArgumentException("fatos da seleção são obrigatórios");
        }
        validarContexto(contexto);
        RegraAdaptativaPublicada aplicavel = null;
        for (RegraAdaptativaPublicada regra : contexto.getRegras()) {
            // Toda rejeição da incógnita mantém o questionamento. O ordinal e
            // o subtipo factual não promovem pergunta para explicação.
            if (!"AG_EMLQ".equals(regra.getCodigoAjudaRecomendada())) {
                continue;
            }
            if (regraSeAplica(regra, fatos, contexto.getProjecaoModeloUsuario())) {
                if (aplicavel != null) {
                    throw new IllegalStateException(
                            "regras simultaneamente aplicáveis à incógnita: "
                                    + aplicavel.getId() + " e " + regra.getId());
                }
                aplicavel = regra;
            }
        }
        String rastreabilidade = fatos.descreverParaRastreabilidade();
        return aplicavel == null
                ? DecisaoAjuda.semRegraAplicavel(contexto, rastreabilidade, repertorio)
                : DecisaoAjuda.aplicar(contexto, rastreabilidade, aplicavel, repertorio);
    }

    private void validarContexto(ContextoAdaptativoUsuario contexto) {
        if (contexto == null
                || !CHAVE_PROPRIETARIO.equals(contexto.getProprietarioSemantico())
                || !escopoAdaptativo().equals(contexto.getEscopo())) {
            throw new IllegalArgumentException("contexto não pertence à incógnita");
        }
        if (!contexto.getProjecaoModeloUsuario().getDimensoesSolicitadas()
                .equals(DIMENSOES_RELEVANTES)) {
            throw new IllegalArgumentException(
                    "a incógnita exige nível de tarefas e diagnóstico da tarefa");
        }
    }

    private boolean regraSeAplica(
            RegraAdaptativaPublicada regra,
            FatosSelecaoAjudaIncognita fatos,
            ProjecaoModeloUsuario projecao) {
        for (Map.Entry<String, String> condicao : regra.getCondicoes().entrySet()) {
            String observado = observar(condicao.getKey(), fatos, projecao);
            if (observado == null || !observado.equals(condicao.getValue())) {
                return false;
            }
        }
        return true;
    }

    private String observar(
            String condicao,
            FatosSelecaoAjudaIncognita fatos,
            ProjecaoModeloUsuario projecao) {
        if (CONDICAO_DIAGNOSTICO_FACTUAL.equals(condicao)) {
            return fatos.getDiagnostico().getTipo().name();
        }
        if (CONDICAO_ORDEM_REJEICAO.equals(condicao)) {
            return Integer.toString(fatos.getOrdinalRejeicao());
        }
        if (CONDICAO_CATEGORIA.equals(condicao)) { return categoria.name(); }
        if (CONDICAO_PAPEL_ALVO.equals(condicao)) { return chavePapelDesignado; }
        if (CONDICAO_NIVEL_TAREFA.equals(condicao)) {
            return observarNivelTarefa(projecao);
        }
        if (CONDICAO_SUPORTE_ANTERIOR.equals(condicao)) {
            return observarSuporteAnterior(projecao);
        }
        throw new IllegalArgumentException(
                "condição fora do vocabulário local da incógnita: " + condicao);
    }

    private String observarNivelTarefa(ProjecaoModeloUsuario projecao) {
        if (!projecao.getNiveisTarefas().estaPresente()) { return null; }
        NiveisTarefasProjetados niveis =
                projecao.getNiveisTarefas().getValor().get();
        NivelComplexidadeTarefa nivel = niveis.obter(categoria).orElse(null);
        return nivel == null ? null : nivel.name();
    }

    private String observarSuporteAnterior(ProjecaoModeloUsuario projecao) {
        if (!projecao.getDiagnosticosTarefa().estaPresente()) { return null; }
        HistoricoDiagnosticosProjetado historico =
                projecao.getDiagnosticosTarefa().getValor().get();
        List<DiagnosticoTarefaProjetado> diagnosticos = historico.getDiagnosticos();
        String tarefaEsperada = categoria.name() + ":" + chavePapelDesignado;
        for (int i = diagnosticos.size() - 1; i >= 0; i--) {
            DiagnosticoTarefaProjetado diagnostico = diagnosticos.get(i);
            if (!diagnostico.getTarefa().estaPresente()
                    || !tarefaEsperada.equals(diagnostico.getTarefa().getValor().get())) {
                continue;
            }
            if (!diagnostico.getSuporte().estaPresente()) { return null; }
            NivelSuporte suporte = diagnostico.getSuporte().getValor().get();
            return suporte.name();
        }
        return null;
    }

    private static RepertorioAjuda criarRepertorioLocal() {
        return new RepertorioAjuda(CHAVE_PROPRIETARIO, Arrays.asList(
                new ItemRepertorioAjuda(
                        "AG_EMLQ", "METACOGNITIVO", "VISUAL",
                        "ui.question.valueMismatch"),
                new ItemRepertorioAjuda(
                        "AG_EME", "CONCEITUAL", "VISUAL",
                        "ui.hint.chooseOperation"),
                new ItemRepertorioAjuda(
                        "AG_EMCME", "PROCEDIMENTAL", "MANIPULATIVA_E_VISUAL",
                        "ui.notice.attemptLimitReached")));
    }

    private static DiagnosticoErroPapel diagnosticoValorIncorreto() {
        return new DiagnosticoErroPapel(
                TipoErroPapel.VALOR_INCORRETO,
                "erro.papel.valorIncorreto",
                "feedback.papel.valorIncorreto",
                "correcao.papel.valorIncorreto");
    }

    private static DiagnosticoErroPapel diagnosticoValorForaDoDominio() {
        return new DiagnosticoErroPapel(
                TipoErroPapel.VALOR_FORA_DO_DOMINIO,
                "erro.papel.valorForaDoDominio",
                "feedback.papel.valorForaDoDominio",
                "correcao.papel.valorForaDoDominio");
    }

    private static String obrigatorio(String valor, String mensagem) {
        String normalizado = valor == null ? "" : valor.trim();
        if (normalizado.length() == 0) { throw new IllegalArgumentException(mensagem); }
        return normalizado;
    }
}
