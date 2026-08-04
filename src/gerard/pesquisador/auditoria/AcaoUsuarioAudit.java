package gerard.pesquisador.auditoria;

/**
 * Ação instrumental do usuário que disparou um evento de auditoria. Papéis
 * semânticos (papelOrigem/papelDestino) são a referência principal; as
 * coordenadas são só informação complementar (posição na tela no instante
 * do arraste), nunca usadas pra decidir nada.
 */
public final class AcaoUsuarioAudit {
    private final String tipo;
    private final String elemento;
    private final String valor;
    private final String papelOrigem;
    private final String papelDestino;
    private final String artefatoOrigem;
    private final String artefatoDestino;
    private final Integer coordenadaOrigemX;
    private final Integer coordenadaOrigemY;
    private final Integer coordenadaDestinoX;
    private final Integer coordenadaDestinoY;
    private final String estadoInterfaceAntes;
    private final String estadoInterfaceDepois;
    private final String resultadoEsperado;
    private final String resultadoObservado;

    public AcaoUsuarioAudit(String tipo, String elemento, String valor, String papelOrigem, String papelDestino,
            String artefatoOrigem, String artefatoDestino, Integer coordenadaOrigemX, Integer coordenadaOrigemY,
            Integer coordenadaDestinoX, Integer coordenadaDestinoY, String estadoInterfaceAntes,
            String estadoInterfaceDepois, String resultadoEsperado, String resultadoObservado) {
        this.tipo = tipo;
        this.elemento = elemento;
        this.valor = valor;
        this.papelOrigem = papelOrigem;
        this.papelDestino = papelDestino;
        this.artefatoOrigem = artefatoOrigem;
        this.artefatoDestino = artefatoDestino;
        this.coordenadaOrigemX = coordenadaOrigemX;
        this.coordenadaOrigemY = coordenadaOrigemY;
        this.coordenadaDestinoX = coordenadaDestinoX;
        this.coordenadaDestinoY = coordenadaDestinoY;
        this.estadoInterfaceAntes = estadoInterfaceAntes;
        this.estadoInterfaceDepois = estadoInterfaceDepois;
        this.resultadoEsperado = resultadoEsperado;
        this.resultadoObservado = resultadoObservado;
    }

    /**
     * Construtor de conveniência pros pontos de chamada em Main.java: só o
     * essencial (tipo, elemento, valor, papéis, coordenadas). Artefatos e
     * estado da interface antes/depois ficam null — Main.java não monta um
     * snapshot desses hoje nesses pontos.
     */
    public AcaoUsuarioAudit(String tipo, String elemento, String valor, String papelOrigem, String papelDestino,
            Integer coordenadaOrigemX, Integer coordenadaOrigemY, Integer coordenadaDestinoX,
            Integer coordenadaDestinoY) {
        this(tipo, elemento, valor, papelOrigem, papelDestino, null, null, coordenadaOrigemX, coordenadaOrigemY,
                coordenadaDestinoX, coordenadaDestinoY, null, null, null, null);
    }

    public String getTipo() { return tipo; }
    public String getElemento() { return elemento; }
    public String getValor() { return valor; }
    public String getPapelOrigem() { return papelOrigem; }
    public String getPapelDestino() { return papelDestino; }
    public String getArtefatoOrigem() { return artefatoOrigem; }
    public String getArtefatoDestino() { return artefatoDestino; }
    public Integer getCoordenadaOrigemX() { return coordenadaOrigemX; }
    public Integer getCoordenadaOrigemY() { return coordenadaOrigemY; }
    public Integer getCoordenadaDestinoX() { return coordenadaDestinoX; }
    public Integer getCoordenadaDestinoY() { return coordenadaDestinoY; }
    public String getEstadoInterfaceAntes() { return estadoInterfaceAntes; }
    public String getEstadoInterfaceDepois() { return estadoInterfaceDepois; }
    public String getResultadoEsperado() { return resultadoEsperado; }
    public String getResultadoObservado() { return resultadoObservado; }
}
