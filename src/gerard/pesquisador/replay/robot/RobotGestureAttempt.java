package gerard.pesquisador.replay.robot;

/**
 * Registro técnico de UMA tentativa de gesto conduzido por Robot — desde a
 * (re)localização do componente até o despacho (ou não) da avaliação.
 * Preenchido progressivamente por {@code GestureCoordinateResolver}/quem
 * conduz o Robot; nunca resumido/perdido — mesmo um pickup fracassado vira
 * um registro completo (status={@link RobotGestureStatus#PICKUP_FAILED}).
 */
public final class RobotGestureAttempt {
    private final int numeroTentativa;
    private String timestampMousePressed;
    private String timestampMouseReleased;

    private boolean coordenadasRecalculadasAntesDoGesto;
    private ComponenteLocalizado origemLocalizada;
    private ComponenteLocalizado destinoLocalizado;

    private boolean componenteEncontrado;
    private boolean itemSelecionadoNoPickup;
    private String papelItem;
    private String valorItem;

    private int amostrasMouseDragged;
    private Integer ultimoPontoArrasteX;
    private Integer ultimoPontoArrasteY;
    private String alvoDetectadoDuranteArraste;

    private boolean itemSelecionadoNaSoltura;
    private boolean alvoDetectadoNaSoltura;
    private String alvoIdNaSoltura;
    private boolean avaliacaoDisparada;

    private RobotGestureStatus status = RobotGestureStatus.STARTED;
    private String motivoFalha;
    private String excecao;

    public RobotGestureAttempt(int numeroTentativa) {
        this.numeroTentativa = numeroTentativa;
    }

    public int getNumeroTentativa() { return numeroTentativa; }

    public void registrarMousePressed(String timestamp, boolean coordenadasRecalculadas,
            ComponenteLocalizado origemLocalizada, boolean componenteEncontrado,
            boolean itemSelecionadoNoPickup, String papelItem, String valorItem) {
        this.timestampMousePressed = timestamp;
        this.coordenadasRecalculadasAntesDoGesto = coordenadasRecalculadas;
        this.origemLocalizada = origemLocalizada;
        this.componenteEncontrado = componenteEncontrado;
        this.itemSelecionadoNoPickup = itemSelecionadoNoPickup;
        this.papelItem = papelItem;
        this.valorItem = valorItem;
        if (!componenteEncontrado || !itemSelecionadoNoPickup) {
            this.status = RobotGestureStatus.PICKUP_FAILED;
            this.motivoFalha = !componenteEncontrado
                    ? "componente_nao_localizado_por_papel_semantico"
                    : "item_nao_selecionado_apos_mouse_pressed";
        } else {
            this.status = RobotGestureStatus.DRAGGING;
        }
    }

    public void registrarMouseDragged(int amostras, Integer ultimoX, Integer ultimoY, String alvoDetectado) {
        this.amostrasMouseDragged = amostras;
        this.ultimoPontoArrasteX = ultimoX;
        this.ultimoPontoArrasteY = ultimoY;
        this.alvoDetectadoDuranteArraste = alvoDetectado;
    }

    public void registrarMouseReleased(String timestamp, ComponenteLocalizado destinoLocalizado,
            boolean itemSelecionadoNaSoltura, boolean alvoDetectadoNaSoltura, String alvoIdNaSoltura,
            boolean avaliacaoDisparada) {
        this.timestampMouseReleased = timestamp;
        this.destinoLocalizado = destinoLocalizado;
        this.itemSelecionadoNaSoltura = itemSelecionadoNaSoltura;
        this.alvoDetectadoNaSoltura = alvoDetectadoNaSoltura;
        this.alvoIdNaSoltura = alvoIdNaSoltura;
        this.avaliacaoDisparada = avaliacaoDisparada;
        if (status == RobotGestureStatus.PICKUP_FAILED) {
            return;
        }
        if (!itemSelecionadoNaSoltura) {
            status = RobotGestureStatus.DROP_FAILED;
            motivoFalha = "item_nao_selecionado_na_soltura";
        } else if (!alvoDetectadoNaSoltura) {
            status = RobotGestureStatus.DROP_FAILED;
            motivoFalha = "alvo_nao_detectado_na_soltura";
        } else if (!avaliacaoDisparada) {
            status = RobotGestureStatus.EVALUATION_NOT_DISPATCHED;
            motivoFalha = "avaliarQuestionamentoPosicionamento_nao_despachou_avaliacao";
        } else {
            status = RobotGestureStatus.COMPLETED;
        }
    }

    public void registrarCancelamento(String motivo) {
        status = RobotGestureStatus.CANCELLED;
        motivoFalha = motivo;
    }

    public void registrarExcecao(Throwable erro) {
        status = RobotGestureStatus.EXCEPTION;
        motivoFalha = "excecao_durante_gesto";
        excecao = erro == null ? null : (erro.getClass().getName() + ": " + erro.getMessage());
    }

    public RobotGestureStatus getStatus() { return status; }
    public String getMotivoFalha() { return motivoFalha; }
    public String getExcecao() { return excecao; }
    public boolean isCoordenadasRecalculadasAntesDoGesto() { return coordenadasRecalculadasAntesDoGesto; }
    public boolean isComponenteEncontrado() { return componenteEncontrado; }
    public boolean isItemSelecionadoNoPickup() { return itemSelecionadoNoPickup; }
    public boolean isAvaliacaoDisparada() { return avaliacaoDisparada; }
    public ComponenteLocalizado getOrigemLocalizada() { return origemLocalizada; }
    public ComponenteLocalizado getDestinoLocalizado() { return destinoLocalizado; }
    public String getTimestampMousePressed() { return timestampMousePressed; }
    public String getTimestampMouseReleased() { return timestampMouseReleased; }
    public String getPapelItem() { return papelItem; }
    public String getValorItem() { return valorItem; }
    public int getAmostrasMouseDragged() { return amostrasMouseDragged; }
    public String getAlvoIdNaSoltura() { return alvoIdNaSoltura; }

    public String resumoLinhaUnica() {
        return "tentativa=" + numeroTentativa
                + " status=" + status
                + " coordenadasRecalculadas=" + coordenadasRecalculadasAntesDoGesto
                + " componenteEncontrado=" + componenteEncontrado
                + " itemSelecionadoPickup=" + itemSelecionadoNoPickup
                + " amostrasArraste=" + amostrasMouseDragged
                + " itemSelecionadoSoltura=" + itemSelecionadoNaSoltura
                + " alvoDetectadoSoltura=" + alvoDetectadoNaSoltura
                + " alvoId=" + alvoIdNaSoltura
                + " avaliacaoDisparada=" + avaliacaoDisparada
                + (motivoFalha != null ? (" motivoFalha=" + motivoFalha) : "")
                + (excecao != null ? (" excecao=" + excecao) : "");
    }
}
