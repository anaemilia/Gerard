package gerard.pesquisador.replay.robot;

/**
 * Resultado de UMA localização de componente por papel semântico, feita
 * imediatamente antes de um gesto (nunca reaproveitada de uma localização
 * anterior — ver {@code GestureCoordinateResolver}). Dados puros, sem
 * referência a {@code Main.TelaGerard} (que fica no localizador, de pacote
 * default, por causa da visibilidade de pacote dos campos de
 * {@code TelaGerard}).
 */
public final class ComponenteLocalizado {
    public static final String ORIGEM_TEXTO_ENUNCIADO = "texto_enunciado";
    public static final String ORIGEM_ITEM_ARRASTAVEL_DIAGRAMA = "item_arrastavel_diagrama";

    private final int x;
    private final int y;
    private final int largura;
    private final int altura;
    private final String origem;
    private final String componentId;
    private final int centroYPreCalculado;

    public ComponenteLocalizado(int x, int y, int largura, int altura, String origem, String componentId) {
        this.x = x;
        this.y = y;
        this.largura = largura;
        this.altura = altura;
        this.origem = origem;
        this.componentId = componentId;
        // ElementoTextoMovel usa y como BASELINE de texto (drawString): o
        // glifo visual vai de (y-altura) até y, então seu centro vertical é
        // y-altura/2. ItemTextoArrastavel/ElementoVergnaud usam y como
        // canto superior esquerdo de um retângulo preenchido
        // (fillRoundRect): centro é y+altura/2. Confundir os dois faz o
        // Robot clicar fora do componente — bug real encontrado e corrigido
        // na rodada 3 (2026-07-31): a primeira versão desta classe usava
        // sempre y+altura/2, quebrando TODO pickup a partir do texto do
        // enunciado (nao só o segundo arraste que a rodada 3 foi pedida
        // para investigar).
        this.centroYPreCalculado = ORIGEM_TEXTO_ENUNCIADO.equals(origem) ? (y - altura / 2) : (y + altura / 2);
    }

    public int centroX() {
        return x + largura / 2;
    }

    public int centroY() {
        return centroYPreCalculado;
    }

    public boolean visivel() {
        return largura > 0 && altura > 0;
    }

    public boolean contemPonto(int px, int py) {
        return px >= x && px <= x + largura && py >= y && py <= y + altura;
    }

    public String getOrigem() {
        return origem;
    }

    public String getComponentId() {
        return componentId;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getLargura() { return largura; }
    public int getAltura() { return altura; }
}
