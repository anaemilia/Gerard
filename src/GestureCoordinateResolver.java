import gerard.pesquisador.replay.robot.ComponenteLocalizado;
import java.awt.Point;

/**
 * Orquestra a (re)localização de origem+destino de um gesto de
 * posicionamento imediatamente antes de executá-lo — ver pacote de
 * correção rodada 3: "1. localizar novamente o componente por identificador
 * semântico; 2. obter seus limites atuais; 3. recalcular o centro; 4.
 * validar visibilidade; 5. validar que o ponto pertence ao componente; 6.
 * só então executar mousePressed."
 *
 * Deve ser chamado de dentro do EDT ({@code SwingUtilities.invokeAndWait}) —
 * lê campos de {@code Main.TelaGerard} diretamente, sem sincronização
 * própria.
 */
final class GestureCoordinateResolver {

    private GestureCoordinateResolver() {
    }

    static final class Resolucao {
        final ComponenteLocalizado origem;
        final ComponenteLocalizado destino;
        final Point origemTela;
        final boolean valido;
        final String motivoInvalido;

        Resolucao(ComponenteLocalizado origem, ComponenteLocalizado destino, Point origemTela,
                boolean valido, String motivoInvalido) {
            this.origem = origem;
            this.destino = destino;
            this.origemTela = origemTela;
            this.valido = valido;
            this.motivoInvalido = motivoInvalido;
        }

        Point pontoOrigemAbsoluto() {
            return new Point(origemTela.x + origem.centroX(), origemTela.y + origem.centroY());
        }

        Point pontoDestinoAbsoluto() {
            return new Point(origemTela.x + destino.centroX(), origemTela.y + destino.centroY());
        }
    }

    static Resolucao resolverPosicionamento(Main.TelaGerard tela, String papelOrigem, String papelDestino) {
        Point origemTela = tela.isShowing() ? tela.getLocationOnScreen() : null;
        if (origemTela == null) {
            return new Resolucao(null, null, null, false, "tela_nao_visivel");
        }
        ComponenteLocalizado origem = SemanticComponentLocator.localizarOrigem(tela, papelOrigem);
        if (origem == null) {
            return new Resolucao(null, null, origemTela, false,
                    "componente_origem_nao_localizado_papel_" + papelOrigem);
        }
        if (!origem.visivel()) {
            return new Resolucao(origem, null, origemTela, false, "componente_origem_sem_area_visivel");
        }
        ComponenteLocalizado destino = SemanticComponentLocator.localizarDestino(tela, papelDestino);
        if (destino == null) {
            return new Resolucao(origem, null, origemTela, false,
                    "componente_destino_nao_localizado_papel_" + papelDestino);
        }
        if (!destino.visivel()) {
            return new Resolucao(origem, destino, origemTela, false, "componente_destino_sem_area_visivel");
        }
        return new Resolucao(origem, destino, origemTela, true, null);
    }
}
