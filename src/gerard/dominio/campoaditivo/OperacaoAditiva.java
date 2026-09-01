package gerard.dominio.campoaditivo;

/** Operação binária explicitamente curada entre dois papéis quantitativos. */
public enum OperacaoAditiva {
    SOMA("+") {
        public int aplicar(int primeiro, int segundo) {
            return Math.addExact(primeiro, segundo);
        }

        public int isolarPrimeiro(int resultado, int segundo) {
            return Math.subtractExact(resultado, segundo);
        }

        public int isolarSegundo(int resultado, int primeiro) {
            return Math.subtractExact(resultado, primeiro);
        }
    },
    SUBTRACAO("-") {
        public int aplicar(int primeiro, int segundo) {
            return Math.subtractExact(primeiro, segundo);
        }

        public int isolarPrimeiro(int resultado, int segundo) {
            return Math.addExact(resultado, segundo);
        }

        public int isolarSegundo(int resultado, int primeiro) {
            return Math.subtractExact(primeiro, resultado);
        }
    };

    private final String simbolo;

    OperacaoAditiva(String simbolo) {
        this.simbolo = simbolo;
    }

    public String getSimbolo() { return simbolo; }

    public abstract int aplicar(int primeiro, int segundo);

    public abstract int isolarPrimeiro(int resultado, int segundo);

    public abstract int isolarSegundo(int resultado, int primeiro);
}
