package gerard.campoaditivo.conclusao;

/**
 * Define quais conteúdos podem representar um valor numérico concluído no
 * diagrama. Aceita naturais, inteiros assinados e decimais com ponto ou vírgula.
 * A interrogação e textos livres nunca concluem a modelagem.
 */
public final class PoliticaValorNumericoConclusao {

    public boolean ehNumero(String valor) {
        if (valor == null) {
            return false;
        }
        String normalizado = valor.trim();
        if (normalizado.length() == 0) {
            return false;
        }
        return normalizado.matches("[+-]?[0-9]+([,.][0-9]+)?");
    }
}
