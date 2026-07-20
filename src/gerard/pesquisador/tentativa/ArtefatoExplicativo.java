package gerard.pesquisador.tentativa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Registro qualitativo vinculado a uma tentativa específica. */
public final class ArtefatoExplicativo {
    private final String tentativaId;
    private final String usuarioId;
    private final String problemaId;
    private final String situacaoId;
    private final String situacaoGrupoId;
    private final String idioma;
    private final String categoria;
    private final String explicacaoGeral;
    private final String invarianteOperatorio;
    private final String fotografiaModelagem;
    private final List<RespostaElementoModelagem> respostas;

    public ArtefatoExplicativo(String tentativaId, String usuarioId, String problemaId,
            String situacaoId, String situacaoGrupoId, String idioma, String categoria,
            String explicacaoGeral, String invarianteOperatorio, String fotografiaModelagem,
            List<RespostaElementoModelagem> respostas) {
        this.tentativaId = limpar(tentativaId);
        this.usuarioId = limpar(usuarioId);
        this.problemaId = limpar(problemaId);
        this.situacaoId = limpar(situacaoId);
        this.situacaoGrupoId = limpar(situacaoGrupoId);
        this.idioma = limpar(idioma);
        this.categoria = limpar(categoria);
        this.explicacaoGeral = limpar(explicacaoGeral);
        this.invarianteOperatorio = limpar(invarianteOperatorio);
        this.fotografiaModelagem = limpar(fotografiaModelagem);
        this.respostas = respostas == null
                ? new ArrayList<RespostaElementoModelagem>()
                : new ArrayList<RespostaElementoModelagem>(respostas);
    }

    private static String limpar(String valor) { return valor == null ? "" : valor.trim(); }
    public String getTentativaId() { return tentativaId; }
    public String getUsuarioId() { return usuarioId; }
    public String getProblemaId() { return problemaId; }
    public String getSituacaoId() { return situacaoId; }
    public String getSituacaoGrupoId() { return situacaoGrupoId; }
    public String getIdioma() { return idioma; }
    public String getCategoria() { return categoria; }
    public String getExplicacaoGeral() { return explicacaoGeral; }
    public String getInvarianteOperatorio() { return invarianteOperatorio; }
    public String getFotografiaModelagem() { return fotografiaModelagem; }
    public List<RespostaElementoModelagem> getRespostas() { return Collections.unmodifiableList(respostas); }
}
