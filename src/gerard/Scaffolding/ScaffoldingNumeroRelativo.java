package gerard.Scaffolding;

import java.awt.Component;
import java.awt.Rectangle;
import gerard.Scaffolding.menus.MenuSinalNumeroRelativo;

/**
 * Scaffolding para a escolha explícita do sinal de números relativos.
 *
 * Este módulo concentra a interface de apoio exibida quando um número ou uma
 * interrogação é inserido em um elemento circular que representa relação ou
 * transformação.
 */
public class ScaffoldingNumeroRelativo {

    private final MenuSinalNumeroRelativo menuSinalNumeroRelativo = new MenuSinalNumeroRelativo();

    public interface AcaoSinalNumeroRelativo {
        void sinalEscolhido(String sinal);
    }

    public boolean ehNumeroOuInterrogacao(String valor) {
        if (valor == null) {
            return false;
        }
        String limpo = removerSinal(valor.trim());
        return limpo.matches("[0-9]+|\\?");
    }

    public boolean temSinal(String valor) {
        if (valor == null) {
            return false;
        }
        String texto = valor.trim();
        return texto.startsWith("+") || texto.startsWith("-");
    }

    public String removerSinal(String valor) {
        if (valor == null) {
            return "";
        }
        String texto = valor.trim();
        while (texto.startsWith("+") || texto.startsWith("-")) {
            texto = texto.substring(1).trim();
        }
        return texto;
    }

    public String aplicarSinal(String valor, String sinal) {
        String base = removerSinal(valor);
        if (base.length() == 0) {
            return "";
        }
        if (!"-".equals(sinal)) {
            sinal = "+";
        }
        return sinal + base;
    }

    public String aplicarSinalComPositivoImplicito(String valor, String sinal) {
        String base = removerSinal(valor);
        if (base.length() == 0) {
            return "";
        }
        if ("-".equals(sinal)) {
            return "-" + base;
        }
        return base;
    }

    public String removerSinalPositivo(String valor) {
        if (valor == null) {
            return "";
        }
        String texto = valor.trim();
        while (texto.startsWith("+")) {
            texto = texto.substring(1).trim();
        }
        return texto;
    }

    public void mostrarMenuEscolhaSinal(
            Component componente,
            Rectangle circulo,
            String valorAtual,
            final AcaoSinalNumeroRelativo acao
    ) {
        if (!ehNumeroOuInterrogacao(valorAtual) || acao == null) {
            return;
        }

        menuSinalNumeroRelativo.mostrar(
                componente,
                circulo,
                new MenuSinalNumeroRelativo.AcaoSinalNumeroRelativo() {
                    public void sinalEscolhido(String sinal) {
                        acao.sinalEscolhido(sinal);
                    }
                }
        );
    }
}
