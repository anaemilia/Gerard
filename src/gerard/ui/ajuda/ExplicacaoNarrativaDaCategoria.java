package gerard.ui.ajuda;

import gerard.agente.modelousuario.MidiaPreferida;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.dominio.campoaditivo.ajuda.FormatoAjudaNarrativaVisual;

/**
 * Dono único da decisão "o que a explicação de uma categoria mostra para a mídia preferida do
 * participante": a categoria possui o repertório; a mídia escolhe o formato (vídeo -> animação,
 * história em quadrinhos -> storyboard); sem formato disponível para a mídia, a explicação cai para o
 * texto e avisa que o formato está em construção. A tela apenas desenha o resultado.
 */
public final class ExplicacaoNarrativaDaCategoria {
    private final PainelAjudaNarrativaVisualCategoria painel;
    private final boolean formatoPendente;

    private ExplicacaoNarrativaDaCategoria(PainelAjudaNarrativaVisualCategoria painel, boolean formatoPendente) {
        this.painel = painel;
        this.formatoPendente = formatoPendente;
    }

    public static ExplicacaoNarrativaDaCategoria para(TipoSituacaoAditiva categoria, MidiaPreferida midia) {
        MidiaPreferida efetiva = midia == null ? MidiaPreferida.LINGUAGEM_NATURAL : midia;
        FormatoAjudaNarrativaVisual formato = efetiva == MidiaPreferida.VIDEO
                ? FormatoAjudaNarrativaVisual.ANIMACAO
                : efetiva == MidiaPreferida.HISTORIA_EM_QUADRINHOS
                        ? FormatoAjudaNarrativaVisual.HISTORIA_EM_QUADRINHOS : null;
        PainelAjudaNarrativaVisualCategoria painel = formato == null ? null
                : PainelAjudaNarrativaVisualCategoria.criarSeDisponivel(
                        categoria.selecionarRepertorioAjudaVisual(), formato);
        boolean pendente = efetiva != MidiaPreferida.LINGUAGEM_NATURAL && painel == null;
        return new ExplicacaoNarrativaDaCategoria(painel, pendente);
    }

    /** Painel da narrativa visual, ou {@code null} quando não há (texto ou formato em construção). */
    public PainelAjudaNarrativaVisualCategoria getPainel() { return painel; }

    /** Verdadeiro quando a mídia preferida ainda não tem formato disponível: mostrar o aviso de construção. */
    public boolean isFormatoPendente() { return formatoPendente; }
}
