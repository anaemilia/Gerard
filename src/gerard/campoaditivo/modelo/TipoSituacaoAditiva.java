package gerard.campoaditivo.modelo;

import gerard.dominio.campoaditivo.ajuda.HistorinhaAjudaVisual;
import gerard.dominio.campoaditivo.ajuda.RepertorioAjudaVisual;

public enum TipoSituacaoAditiva {
    COMPOSICAO_MEDIDAS("tipo.composicao_medidas", "CM"),
    TRANSFORMACAO_MEDIDAS("tipo.transformacao_medidas", "TM"),
    COMPARACAO_MEDIDAS("tipo.comparacao_medidas", "COP"),
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
