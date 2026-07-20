package gerard.Scaffolding.ajudacontextual;

/**
 * Define as áreas da interface e as intenções de ajuda do recurso "E agora?".
 * A classe mantém a decisão pedagógica separada da apresentação Swing.
 */
public final class ScaffoldingAjudaContextual {

    public enum Area {
        TEXTO,
        VERGNAUD,
        COMPLEMENTAR
    }

    public enum Intencao {
        DUVIDA,
        CONTINUAR,
        PROXIMO_PASSO
    }

    public String obterChaveMensagem(Area area, Intencao intencao) {
        if (area == null || intencao == null) {
            return "ui.help.choose";
        }

        switch (area) {
            case TEXTO:
                return chavePorIntencao("ui.help.text", intencao);
            case VERGNAUD:
                return chavePorIntencao("ui.help.vergnaud", intencao);
            case COMPLEMENTAR:
                return chavePorIntencao("ui.help.complementary", intencao);
            default:
                return "ui.help.choose";
        }
    }

    public String obterChaveArea(Area area) {
        if (area == null) {
            return "ui.help.area.text";
        }
        switch (area) {
            case TEXTO:
                return "ui.help.area.text";
            case VERGNAUD:
                return "ui.help.area.vergnaud";
            case COMPLEMENTAR:
                return "ui.help.area.complementary";
            default:
                return "ui.help.area.text";
        }
    }

    public String obterChaveOpcao(Intencao intencao) {
        if (intencao == null) {
            return "ui.help.option.doubt";
        }
        switch (intencao) {
            case DUVIDA:
                return "ui.help.option.doubt";
            case CONTINUAR:
                return "ui.help.option.continue";
            case PROXIMO_PASSO:
                return "ui.help.option.next";
            default:
                return "ui.help.option.doubt";
        }
    }

    private String chavePorIntencao(String prefixo, Intencao intencao) {
        switch (intencao) {
            case DUVIDA:
                return prefixo + ".doubt";
            case CONTINUAR:
                return prefixo + ".continue";
            case PROXIMO_PASSO:
                return prefixo + ".next";
            default:
                return "ui.help.choose";
        }
    }
}
