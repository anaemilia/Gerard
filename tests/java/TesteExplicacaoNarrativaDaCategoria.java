import gerard.agente.modelousuario.MidiaPreferida;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.ui.ajuda.ExplicacaoNarrativaDaCategoria;

/** A mídia preferida escolhe o formato da explicação da categoria; a decisão não mora na tela. */
public final class TesteExplicacaoNarrativaDaCategoria {
    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "true");
        TipoSituacaoAditiva tm = TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS;
        ExplicacaoNarrativaDaCategoria texto = ExplicacaoNarrativaDaCategoria.para(tm, MidiaPreferida.LINGUAGEM_NATURAL);
        exigir(texto.getPainel() == null && !texto.isFormatoPendente(), "linguagem natural: só texto, sem aviso");
        ExplicacaoNarrativaDaCategoria nulo = ExplicacaoNarrativaDaCategoria.para(tm, null);
        exigir(nulo.getPainel() == null && !nulo.isFormatoPendente(), "sem mídia = linguagem natural");
        ExplicacaoNarrativaDaCategoria som = ExplicacaoNarrativaDaCategoria.para(tm, MidiaPreferida.SOM);
        exigir(som.getPainel() == null && som.isFormatoPendente(), "som: formato em construção");
        ExplicacaoNarrativaDaCategoria video = ExplicacaoNarrativaDaCategoria.para(tm, MidiaPreferida.VIDEO);
        exigir(video.getPainel() != null && !video.isFormatoPendente(), "vídeo: animação da própria categoria");
        ExplicacaoNarrativaDaCategoria quadrinhos = ExplicacaoNarrativaDaCategoria.para(tm, MidiaPreferida.HISTORIA_EM_QUADRINHOS);
        exigir(quadrinhos.getPainel() != null && !quadrinhos.isFormatoPendente(), "quadrinhos: storyboard da categoria");
        System.out.println("APROVADO: a mídia preferida decide o formato da explicação, fora da tela.");
    }
    private static void exigir(boolean ok, String msg) { if (!ok) throw new AssertionError(msg); }
}
