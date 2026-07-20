package gerard.campoaditivo.diagrama.modelo;

public class ConectorDiagrama {
    /** Sentinela: nenhum ponto de destino além do próprio conector (haste não desenhada). */
    public static final int SEM_ALVO = Integer.MIN_VALUE;

    private final TipoConectorDiagrama tipo;
    private final int x1;
    private final int y1;
    private final int x2;
    private final int y2;
    private final String legenda;
    private final int xAlvo;
    private final int yAlvo;

    public ConectorDiagrama(TipoConectorDiagrama tipo, int x1, int y1, int x2, int y2, String legenda) {
        this(tipo, x1, y1, x2, y2, legenda, SEM_ALVO, SEM_ALVO);
    }

    /**
     * @param xAlvo,yAlvo ponto adicional (ex.: o centro da figura "Todo") até
     *        onde uma haste é desenhada a partir do meio da chave — usado
     *        para ligar visualmente as partes ao todo. SEM_ALVO quando a
     *        chave não deve se estender além do próprio intervalo.
     */
    public ConectorDiagrama(TipoConectorDiagrama tipo, int x1, int y1, int x2, int y2, String legenda,
                             int xAlvo, int yAlvo) {
        this.tipo = tipo;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.legenda = legenda;
        this.xAlvo = xAlvo;
        this.yAlvo = yAlvo;
    }

    public TipoConectorDiagrama getTipo() { return tipo; }
    public int getX1() { return x1; }
    public int getY1() { return y1; }
    public int getX2() { return x2; }
    public int getY2() { return y2; }
    public String getLegenda() { return legenda; }
    public int getXAlvo() { return xAlvo; }
    public int getYAlvo() { return yAlvo; }
    public boolean temAlvo() { return xAlvo != SEM_ALVO && yAlvo != SEM_ALVO; }
}
