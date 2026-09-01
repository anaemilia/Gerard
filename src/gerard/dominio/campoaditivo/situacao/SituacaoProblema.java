package gerard.dominio.campoaditivo.situacao;

import gerard.dominio.campoaditivo.PapelQuantitativo;
import gerard.semantica.numero.NumeroInteiro;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Agregado semanticamente rico que coordena estrutura formal e narrativa.
 * A sintaxe de texto, quadrinhos, animação ou vídeo permanece externa.
 */
public final class SituacaoProblema {
    private final String id;
    private final StatusCuradoriaSituacao statusCuradoria;
    private final List<String> referenciasCuradas;
    private final EstruturaAditiva estrutura;
    private final NarrativaCurada narrativa;
    private final List<CorrespondenciaPapelNarrativa> correspondencias;

    public SituacaoProblema(
            String id,
            StatusCuradoriaSituacao statusCuradoria,
            List<String> referenciasCuradas,
            EstruturaAditiva estrutura,
            NarrativaCurada narrativa,
            List<CorrespondenciaPapelNarrativa> correspondencias) {
        this.id = FamiliaObjeto.obrigatorio(id, "id da situação não pode ser vazio");
        if (statusCuradoria == null || estrutura == null || narrativa == null
                || correspondencias == null) {
            throw new IllegalArgumentException(
                    "status, estrutura, narrativa e correspondências são obrigatórios");
        }
        this.statusCuradoria = statusCuradoria;
        this.referenciasCuradas = copiarReferencias(referenciasCuradas);
        this.estrutura = estrutura;
        this.narrativa = narrativa;
        this.correspondencias = Collections.unmodifiableList(
                new ArrayList<>(correspondencias));
    }

    public String getId() { return id; }
    public StatusCuradoriaSituacao getStatusCuradoria() { return statusCuradoria; }
    public List<String> getReferenciasCuradas() { return referenciasCuradas; }
    public EstruturaAditiva getEstrutura() { return estrutura; }
    public NarrativaCurada getNarrativa() { return narrativa; }
    public List<CorrespondenciaPapelNarrativa> getCorrespondencias() {
        return correspondencias;
    }

    public ResultadoValidacaoSituacao validar() {
        List<DiagnosticoSituacao> diagnosticos = new ArrayList<>();
        ResultadoValidacaoSituacao resultadoEstrutura = estrutura.validar();
        ResultadoValidacaoSituacao resultadoNarrativa = narrativa.validar();
        diagnosticos.addAll(resultadoEstrutura.getDiagnosticos());
        diagnosticos.addAll(resultadoNarrativa.getDiagnosticos());

        Set<String> papeisMapeados = new HashSet<>();
        for (CorrespondenciaPapelNarrativa correspondencia : correspondencias) {
            String chave = correspondencia.getChavePapel();
            PapelQuantitativo papel = estrutura.papel(chave);
            if (papel == null) {
                diagnosticos.add(new DiagnosticoSituacao(
                        "situacao.correspondencia.papel_ausente", chave));
                continue;
            }
            if (!papeisMapeados.add(chave)) {
                diagnosticos.add(new DiagnosticoSituacao(
                        "situacao.correspondencia.papel_duplicado", chave));
                continue;
            }
            if (resultadoNarrativa.ehValida() && papel.estaPreenchido()) {
                try {
                    NumeroInteiro valorNarrativo =
                            correspondencia.getReferencia().resolver(narrativa);
                    if (papel.valorAtual().valorOuNull().intValue()
                            != valorNarrativo.intValue()) {
                        diagnosticos.add(new DiagnosticoSituacao(
                                "situacao.correspondencia.valor_divergente", chave));
                    }
                } catch (IllegalStateException inconsistencia) {
                    diagnosticos.add(new DiagnosticoSituacao(
                            "situacao.correspondencia.referencia_invalida",
                            chave + ":" + inconsistencia.getMessage()));
                }
            }
        }

        for (String chavePapel : estrutura.getPapeis().keySet()) {
            if (!papeisMapeados.contains(chavePapel)) {
                diagnosticos.add(new DiagnosticoSituacao(
                        "situacao.correspondencia.papel_sem_mapeamento", chavePapel));
            }
        }
        return new ResultadoValidacaoSituacao(diagnosticos);
    }

    public SequenciaNarrativa criarSequenciaNarrativa() {
        ResultadoValidacaoSituacao resultado = validar();
        if (!resultado.ehValida()) {
            throw new IllegalStateException(
                    "situação inconsistente; a mídia não deve reinterpretar os diagnósticos");
        }
        return narrativa.criarSequenciaNarrativa();
    }

    private static List<String> copiarReferencias(List<String> referencias) {
        ArrayList<String> copia = new ArrayList<>();
        if (referencias != null) {
            for (String referencia : referencias) {
                copia.add(FamiliaObjeto.obrigatorio(
                        referencia, "referência curada não pode ser vazia"));
            }
        }
        return Collections.unmodifiableList(copia);
    }
}
