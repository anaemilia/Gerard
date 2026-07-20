package gerard.desktop.composicaomedidas;

/**
 * Uma situação-problema de composição de medidas já pronta para uso na
 * tela: enunciado original, os três números localizados no texto (parte 1,
 * parte 2 e todo) e qual deles é a incógnita.
 */
public final class SituacaoComposicaoMedidas {

    public final String id;
    public final String enunciado;
    public final NumeroTextoExtraido parte1;
    public final NumeroTextoExtraido parte2;
    public final NumeroTextoExtraido todo;

    public SituacaoComposicaoMedidas(String id, String enunciado,
            NumeroTextoExtraido parte1, NumeroTextoExtraido parte2, NumeroTextoExtraido todo) {
        this.id = id;
        this.enunciado = enunciado;
        this.parte1 = parte1;
        this.parte2 = parte2;
        this.todo = todo;
    }

    public NumeroTextoExtraido obterIncognita() {
        if (parte1.ehIncognita()) return parte1;
        if (parte2.ehIncognita()) return parte2;
        return todo;
    }
}
