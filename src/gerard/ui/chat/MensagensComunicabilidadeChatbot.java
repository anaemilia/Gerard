package gerard.ui.chat;

/** Identifica as mensagens conversacionais sem atribuir função de scaffolding. */
public final class MensagensComunicabilidadeChatbot {
    public enum Area { TEXTO, VERGNAUD, COMPLEMENTAR }
    public enum Intencao { DUVIDA, CONTINUAR, PROXIMO_PASSO }

    public String chaveArea(Area area) {
        if (area == Area.VERGNAUD) return "ui.chat.area.vergnaud";
        if (area == Area.COMPLEMENTAR) return "ui.chat.area.complementary";
        return "ui.chat.area.text";
    }

    public String chaveMensagem(Area area, Intencao intencao) {
        String prefixo = "ui.chat.communicability.text";
        if (area == Area.VERGNAUD) prefixo = "ui.chat.communicability.vergnaud";
        else if (area == Area.COMPLEMENTAR) prefixo = "ui.chat.communicability.complementary";
        if (intencao == Intencao.CONTINUAR) return prefixo + ".continue";
        if (intencao == Intencao.PROXIMO_PASSO) return prefixo + ".next";
        return prefixo + ".doubt";
    }
}
