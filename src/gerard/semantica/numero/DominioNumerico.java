package gerard.semantica.numero;

/**
 * Universos numéricos utilizados no campo aditivo do Gérard.
 * NATURAIS representa N0: zero e inteiros positivos.
 */
public enum DominioNumerico {
    NATURAIS {
        @Override
        public boolean aceita(int valor) {
            return valor >= 0;
        }

        @Override
        public boolean aceitaSinalNegativo() {
            return false;
        }
    },
    INTEIROS {
        @Override
        public boolean aceita(int valor) {
            return true;
        }

        @Override
        public boolean aceitaSinalNegativo() {
            return true;
        }
    };

    public abstract boolean aceita(int valor);
    public abstract boolean aceitaSinalNegativo();
}
