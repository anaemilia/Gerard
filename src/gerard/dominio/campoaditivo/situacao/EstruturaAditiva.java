package gerard.dominio.campoaditivo.situacao;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.dominio.campoaditivo.EstadoConsistencia;
import gerard.dominio.campoaditivo.PapelQuantitativo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Estrutura formal da situação: categoria, papéis, relações vinculadas e o
 * papel que era desconhecido no enunciado original.
 */
public final class EstruturaAditiva {
    private final TipoSituacaoAditiva categoria;
    private final Map<String, PapelQuantitativo> papeis;
    private final List<RelacaoEstruturalVinculada> relacoes;
    private final List<CriterioOperacaoModelagem> criteriosOperacao;
    private final String chavePapelDesconhecidoOriginal;

    public EstruturaAditiva(
            TipoSituacaoAditiva categoria,
            List<PapelQuantitativo> papeis,
            List<RelacaoEstruturalVinculada> relacoes,
            String chavePapelDesconhecidoOriginal) {
        this(categoria, papeis, relacoes,
                Collections.<CriterioOperacaoModelagem>emptyList(),
                chavePapelDesconhecidoOriginal);
    }

    public EstruturaAditiva(
            TipoSituacaoAditiva categoria,
            List<PapelQuantitativo> papeis,
            List<RelacaoEstruturalVinculada> relacoes,
            List<CriterioOperacaoModelagem> criteriosOperacao,
            String chavePapelDesconhecidoOriginal) {
        if (categoria == null || papeis == null || papeis.isEmpty() || relacoes == null) {
            throw new IllegalArgumentException("categoria, papéis e relações são obrigatórios");
        }
        if (criteriosOperacao == null) {
            throw new IllegalArgumentException(
                    "critérios de operação não podem ser nulos");
        }
        this.categoria = categoria;
        this.papeis = indexarPapeis(papeis);
        this.relacoes = Collections.unmodifiableList(new ArrayList<>(relacoes));
        this.criteriosOperacao = Collections.unmodifiableList(
                new ArrayList<>(criteriosOperacao));
        this.chavePapelDesconhecidoOriginal = FamiliaObjeto.obrigatorio(
                chavePapelDesconhecidoOriginal,
                "papel desconhecido original não pode ser vazio");
        exigirVinculosLocais();
    }

    public TipoSituacaoAditiva getCategoria() { return categoria; }
    public Map<String, PapelQuantitativo> getPapeis() { return papeis; }
    public List<RelacaoEstruturalVinculada> getRelacoes() { return relacoes; }
    public List<CriterioOperacaoModelagem> getCriteriosOperacao() {
        return criteriosOperacao;
    }
    public String getChavePapelDesconhecidoOriginal() {
        return chavePapelDesconhecidoOriginal;
    }

    public PapelQuantitativo papel(String chave) { return papeis.get(chave); }

    public ResultadoValidacaoSituacao validar() {
        List<DiagnosticoSituacao> diagnosticos = new ArrayList<>();
        if (!papeis.containsKey(chavePapelDesconhecidoOriginal)) {
            diagnosticos.add(new DiagnosticoSituacao(
                    "estrutura.incognita.papel_ausente",
                    chavePapelDesconhecidoOriginal));
        }
        for (PapelQuantitativo papel : papeis.values()) {
            if (!papel.estaPreenchido()) {
                diagnosticos.add(new DiagnosticoSituacao(
                        "estrutura.papel.valor_curado_ausente", papel.getChave()));
            }
        }
        for (RelacaoEstruturalVinculada relacao : relacoes) {
            EstadoConsistencia estado = relacao.verificarConsistencia();
            if (estado != EstadoConsistencia.CONSISTENTE) {
                diagnosticos.add(new DiagnosticoSituacao(
                        "estrutura.relacao." + estado.name().toLowerCase(),
                        relacao.getChave()));
            }
        }
        return new ResultadoValidacaoSituacao(diagnosticos);
    }

    private static Map<String, PapelQuantitativo> indexarPapeis(
            List<PapelQuantitativo> papeis) {
        LinkedHashMap<String, PapelQuantitativo> indice = new LinkedHashMap<>();
        for (PapelQuantitativo papel : papeis) {
            if (papel == null) {
                throw new IllegalArgumentException("papel da estrutura não pode ser nulo");
            }
            if (indice.put(papel.getChave(), papel) != null) {
                throw new IllegalArgumentException(
                        "papel duplicado na estrutura: " + papel.getChave());
            }
        }
        return Collections.unmodifiableMap(indice);
    }

    private void exigirVinculosLocais() {
        for (RelacaoEstruturalVinculada relacao : relacoes) {
            if (relacao == null) {
                throw new IllegalArgumentException("relação vinculada não pode ser nula");
            }
            for (PapelQuantitativo papel : relacao.getPapeis()) {
                if (papeis.get(papel.getChave()) != papel) {
                    throw new IllegalArgumentException(
                            "relação referencia papel externo à estrutura: "
                                    + papel.getChave());
                }
            }
        }
        Set<String> chavesCriterios = new java.util.HashSet<>();
        for (CriterioOperacaoModelagem criterio : criteriosOperacao) {
            if (criterio == null) {
                throw new IllegalArgumentException(
                        "critério de operação não pode ser nulo");
            }
            if (!chavesCriterios.add(criterio.getChave())) {
                throw new IllegalArgumentException(
                        "critério de operação duplicado: " + criterio.getChave());
            }
            for (String chavePapel : criterio.getChavesPapeisEnvolvidos()) {
                if (!papeis.containsKey(chavePapel)) {
                    throw new IllegalArgumentException(
                            "critério de operação referencia papel externo à estrutura: "
                                    + chavePapel);
                }
            }
        }
    }
}
