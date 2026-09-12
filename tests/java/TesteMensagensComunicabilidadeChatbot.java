import gerard.ui.chat.MensagensComunicabilidadeChatbot;

public class TesteMensagensComunicabilidadeChatbot {
    public static void main(String[] args) {
        MensagensComunicabilidadeChatbot mensagens = new MensagensComunicabilidadeChatbot();
        String chave = mensagens.chaveMensagem(
                MensagensComunicabilidadeChatbot.Area.VERGNAUD,
                MensagensComunicabilidadeChatbot.Intencao.DUVIDA);
        if (!"ui.chat.communicability.vergnaud.doubt".equals(chave)) {
            throw new AssertionError("A mensagem do chat deve pertencer ao catálogo de comunicabilidade: " + chave);
        }
        if (chave.startsWith("ui.help.")) {
            throw new AssertionError("O chatbot não pode usar diretamente uma mensagem de scaffolding.");
        }
        if (!mensagens.chaveArea(MensagensComunicabilidadeChatbot.Area.TEXTO).startsWith("ui.chat.")) {
            throw new AssertionError("Até os rótulos de área devem pertencer ao chat.");
        }
        System.out.println("OK - mensagens de comunicabilidade separadas do scaffolding");
    }
}
