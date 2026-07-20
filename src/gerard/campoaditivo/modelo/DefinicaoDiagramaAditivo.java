package gerard.campoaditivo.modelo;

public class DefinicaoDiagramaAditivo {
    private final String titulo;
    private final String rotulo1;
    private final String rotulo2;
    private final String rotulo3;

    public DefinicaoDiagramaAditivo(String titulo, String rotulo1, String rotulo2, String rotulo3) {
        this.titulo = titulo;
        this.rotulo1 = rotulo1;
        this.rotulo2 = rotulo2;
        this.rotulo3 = rotulo3;
    }

    public String getTitulo() { return titulo; }
    public String getRotulo1() { return rotulo1; }
    public String getRotulo2() { return rotulo2; }
    public String getRotulo3() { return rotulo3; }
}
