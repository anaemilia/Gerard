package gerard.campoaditivo.modelo;

import gerard.dominio.campoaditivo.ajuda.HistorinhaAjudaVisual;
import gerard.dominio.campoaditivo.ajuda.RepertorioAjudaVisual;

public enum TipoSituacaoAditiva {
    COMPOSICAO_MEDIDAS("tipo.composicao_medidas", "CM"),
    TRANSFORMACAO_MEDIDAS(
            "tipo.transformacao_medidas", "TM",
            RepertorioAjudaVisual.criar(
                    "ajuda.visual.transformacao_medidas",
                    new HistorinhaAjudaVisual(
                            "andrea_boneca",
                            "PO_TRANSFORMACAO_MEDIDAS_dinheiro_e_brinquedos_188709799",
                            "transformacao_medidas/01_andrea_boneca_historinha"),
                    new HistorinhaAjudaVisual(
                            "maria_e_as_figurinhas",
                            "PO_TRANSFORMACAO_MEDIDAS_figurinhas_1595370160",
                            "transformacao_medidas/02_maria_e_as_figurinhas_historinha"),
                    new HistorinhaAjudaVisual(
                            "maria_na_feira",
                            "PO_TRANSFORMACAO_MEDIDAS_frutas_e_verduras_365604840",
                            "transformacao_medidas/03_maria_na_feira_historinha"),
                    new HistorinhaAjudaVisual(
                            "nadia_e_os_morangos",
                            "PO_TRANSFORMACAO_MEDIDAS_frutas_93128185",
                            "transformacao_medidas/04_nadia_e_os_morangos_historinha"),
                    new HistorinhaAjudaVisual(
                            "leandro_e_as_balas",
                            "PO_TRANSFORMACAO_MEDIDAS_balas_2108939476",
                            "transformacao_medidas/05_leandro_e_as_balas_historinha"),
                    new HistorinhaAjudaVisual(
                            "paulo_e_as_bolas",
                            "PO_TRANSFORMACAO_MEDIDAS_bolas_163355672",
                            "transformacao_medidas/06_paulo_e_as_bolas_historinha"),
                    new HistorinhaAjudaVisual(
                            "lucas_e_as_figurinhas_do_tio",
                            "PO_TRANSFORMACAO_MEDIDAS_figurinhas_1775031040",
                            "transformacao_medidas/07_lucas_e_as_figurinhas_do_tio_historinha"))),
    COMPARACAO_MEDIDAS(
            "tipo.comparacao_medidas", "COP",
            RepertorioAjudaVisual.criar(
                    "ajuda.visual.comparacao_medidas",
                    new HistorinhaAjudaVisual(
                            "paulo_e_jose",
                            "PO_COMPARACAO_MEDIDAS_bolas_487868670",
                            "comparacao_medidas/01_paulo_e_jose_historinha"),
                    new HistorinhaAjudaVisual(
                            "marcus_e_jardel",
                            "PO_COMPARACAO_MEDIDAS_carrinhos_1292854684",
                            "comparacao_medidas/02_marcus_e_jardel_historinha"),
                    new HistorinhaAjudaVisual(
                            "ingrid_e_ligianne",
                            "PO_COMPARACAO_MEDIDAS_dinheiro_667564143",
                            "comparacao_medidas/03_ingrid_e_ligianne_historinha"),
                    new HistorinhaAjudaVisual(
                            "jammes_e_gisele",
                            "PO_COMPARACAO_MEDIDAS_cds_1222854862",
                            "comparacao_medidas/04_jammes_e_gisele_historinha"),
                    new HistorinhaAjudaVisual(
                            "claudia_e_joana",
                            "PO_COMPARACAO_MEDIDAS_dinheiro_2123535490",
                            "comparacao_medidas/05_claudia_e_joana_historinha"),
                    new HistorinhaAjudaVisual(
                            "claudenice_e_nadia",
                            "PO_COMPARACAO_MEDIDAS_dinheiro_1527166754",
                            "comparacao_medidas/06_claudenice_e_nadia_historinha"))),
    COMPOSICAO_TRANSFORMACOES(
            "tipo.composicao_transformacoes", "CT",
            RepertorioAjudaVisual.criar(
                    "ajuda.visual.composicao_transformacoes",
                    new HistorinhaAjudaVisual(
                            "joao_bilas_duas_partidas",
                            "PO_COMPOSICAO_TRANSFORMACOES_bolas_509261012",
                            "composicao_transformacoes/01_joao_bilas_historinha"),
                    new HistorinhaAjudaVisual(
                            "manuel_figurinhas_dois_ganhos",
                            "PO_COMPOSICAO_TRANSFORMACOES_figurinhas_2105853102",
                            "composicao_transformacoes/02_manuel_figurinhas_historinha"),
                    new HistorinhaAjudaVisual(
                            "geisa_chocolates_duas_perdas",
                            "PO_TRANSFORMACAO_COMPOSTA_DOIS_PASSOS_guloseimas_400916186",
                            "composicao_transformacoes/03_geisa_chocolates_historinha"),
                    new HistorinhaAjudaVisual(
                            "vovo_rosas_duas_perdas",
                            "PO_COMPOSICAO_TRANSFORMACAO_MEDIDAS_flores_422431114",
                            "composicao_transformacoes/04_vovo_rosas_historinha"))),
    TRANSFORMACAO_RELACAO(
            "tipo.transformacao_relacao", "TR",
            RepertorioAjudaVisual.criar(
                    "ajuda.visual.transformacao_relacao",
                    new HistorinhaAjudaVisual(
                            "julia_maria_bonecas",
                            "PO_TRANSFORMACAO_RELACAO_bonecas_620955739",
                            "transformacao_relacao/01_julia_maria_bonecas_historinha"),
                    new HistorinhaAjudaVisual(
                            "ana_bia_figurinhas",
                            "PO_TRANSFORMACAO_RELACAO_figurinhas_1283157032",
                            "transformacao_relacao/02_ana_bia_figurinhas_historinha"))),
    COMPOSICAO_RELACOES(
            "tipo.composicao_relacoes", "CR",
            RepertorioAjudaVisual.criar(
                    "ajuda.visual.composicao_relacoes",
                    new HistorinhaAjudaVisual(
                            "carlos_joao_pedro_dinheiro",
                            "PO_COMPOSICAO_RELACOES_dinheiro_161113042",
                            "composicao_relacoes/01_carlos_joao_pedro_dinheiro_historinha"),
                    new HistorinhaAjudaVisual(
                            "maria_joao_carla_idades",
                            "PO_COMPOSICAO_RELACOES_idades_642701604",
                            "composicao_relacoes/02_maria_joao_carla_idades_historinha")));

    private final String chaveDescricao;
    private final String sigla;
    private final RepertorioAjudaVisual repertorioAjudaVisual;

    TipoSituacaoAditiva(String chaveDescricao, String sigla) {
        this(chaveDescricao, sigla, RepertorioAjudaVisual.vazio());
    }

    TipoSituacaoAditiva(
            String chaveDescricao,
            String sigla,
            RepertorioAjudaVisual repertorioAjudaVisual) {
        this.chaveDescricao = chaveDescricao;
        this.sigla = sigla;
        this.repertorioAjudaVisual = repertorioAjudaVisual;
    }

    public String getChaveDescricao() {
        return chaveDescricao;
    }

    public String getSigla() {
        return sigla;
    }

    /**
     * A própria categoria seleciona seu repertório local quando a camada de
     * aplicação solicita ajuda narrativa visual. O mesmo conteúdo pode ser
     * materializado como animação ou história em quadrinhos sem depender de
     * Swing.
     */
    public RepertorioAjudaVisual selecionarRepertorioAjudaVisual() {
        return repertorioAjudaVisual;
    }

    /** Categorias cuja situação envolve uma transformação (aciona a ajuda visual por si). */
    public boolean envolveTransformacao() {
        return this == TRANSFORMACAO_MEDIDAS || this == COMPOSICAO_TRANSFORMACOES
                || this == TRANSFORMACAO_RELACAO;
    }

    /** Política independente da disponibilidade de conteúdo (decisão de 2026-10-02). */
    public boolean deveAcionarHistorinha(boolean possuiNumeroRelativo, boolean escaladaNoLimite) {
        return gerard.dominio.campoaditivo.ajuda.PoliticaApoioVisual.acionado(
                this, possuiNumeroRelativo, escaladaNoLimite);
    }

    /** Resolve o repertório depois da decisão; lista vazia não revoga o acionamento. */
    public RepertorioAjudaVisual selecionarRepertorioAjudaVisual(
            boolean possuiNumeroRelativo, boolean escaladaNoLimite) {
        return deveAcionarHistorinha(possuiNumeroRelativo, escaladaNoLimite)
                ? repertorioAjudaVisual : RepertorioAjudaVisual.vazio();
    }

    public boolean possuiAjudaVisual() {
        return !repertorioAjudaVisual.estaVazio();
    }

    public static TipoSituacaoAditiva deCodigoPersistido(String codigo) {
        if ("COMPOSICAO_TRANSFORMACAO_MEDIDAS".equals(codigo)
                || "TRANSFORMACAO_COMPOSTA_DOIS_PASSOS".equals(codigo)) {
            return COMPOSICAO_TRANSFORMACOES;
        }
        return valueOf(codigo);
    }
}
