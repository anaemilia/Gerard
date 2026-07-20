package gerard.semantica.numero;

/** Fonte única para criação e validação dos valores matemáticos. */
public final class FabricaValoresNumericos {

    public ValorNumerico conhecido(DominioNumerico dominio, int valor) {
        DominioNumerico efetivo = dominio == null ? DominioNumerico.NATURAIS : dominio;
        if (efetivo == DominioNumerico.INTEIROS) {
            return new NumeroInteiro(valor);
        }
        return new NumeroNatural(valor);
    }

    public ValorNumerico desconhecido(DominioNumerico dominio) {
        return new ValorDesconhecido(dominio);
    }

    public ValorNumerico criar(DominioNumerico dominio, Integer valor, boolean conhecido) {
        return conhecido && valor != null
                ? conhecido(dominio, valor.intValue())
                : desconhecido(dominio);
    }
}
