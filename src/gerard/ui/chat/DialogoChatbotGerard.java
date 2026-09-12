package gerard.ui.chat;

import gerard.i18n.ServicoLocalizacao;
import gerard.ui.UITemaGerard;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.Normalizer;
import java.util.Locale;

/** Janela de conversa deterministica que apresenta as mensagens de comunicabilidade existentes. */
public final class DialogoChatbotGerard extends JDialog {
    public interface OuvinteAcaoNeutra { void registrar(String categoria, String detalhes); }
    private final ServicoLocalizacao localizacao;
    private final MensagensComunicabilidadeChatbot mensagensComunicabilidade;
    private final OuvinteAcaoNeutra ouvinte;
    private final JTextArea conversa = new JTextArea();
    private final JTextField entrada = new JTextField();
    private MensagensComunicabilidadeChatbot.Area areaAtual = MensagensComunicabilidadeChatbot.Area.TEXTO;

    public DialogoChatbotGerard(Window dona, ServicoLocalizacao localizacao,
            MensagensComunicabilidadeChatbot mensagensComunicabilidade, OuvinteAcaoNeutra ouvinte) {
        super(dona, localizacao.texto("ui.chat.title"), ModalityType.MODELESS);
        this.localizacao = localizacao; this.mensagensComunicabilidade = mensagensComunicabilidade; this.ouvinte = ouvinte;
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setMinimumSize(new Dimension(620, 430));
        JPanel raiz = new JPanel(new BorderLayout(10, 10));
        raiz.setBackground(UITemaGerard.COR_FUNDO);
        raiz.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        JLabel titulo = new JLabel(localizacao.texto("ui.chat.heading"));
        titulo.setFont(new Font("Arial", Font.BOLD, 18)); titulo.setForeground(UITemaGerard.COR_TEXTO);
        raiz.add(titulo, BorderLayout.NORTH);
        conversa.setEditable(false); conversa.setLineWrap(true); conversa.setWrapStyleWord(true);
        conversa.setFont(new Font("Arial", Font.PLAIN, 14)); conversa.setForeground(UITemaGerard.COR_TEXTO);
        conversa.setBackground(UITemaGerard.COR_SUPERFICIE); conversa.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        conversa.getAccessibleContext().setAccessibleName(localizacao.texto("ui.chat.history"));
        JPanel corpoConversa = new JPanel(new BorderLayout(10, 0));
        corpoConversa.setOpaque(false);
        JLabel avatar = criarAvatarGerard();
        JPanel colunaAvatar = new JPanel(new BorderLayout());
        colunaAvatar.setOpaque(false);
        colunaAvatar.add(avatar, BorderLayout.NORTH);
        corpoConversa.add(colunaAvatar, BorderLayout.WEST);
        corpoConversa.add(new JScrollPane(conversa), BorderLayout.CENTER);
        raiz.add(corpoConversa, BorderLayout.CENTER);
        JPanel rodape = new JPanel(); rodape.setOpaque(false); rodape.setLayout(new BoxLayout(rodape, BoxLayout.Y_AXIS));
        JPanel areas = new JPanel(new GridLayout(1,3,6,0)); areas.setOpaque(false);
        areas.add(criarBotaoArea(MensagensComunicabilidadeChatbot.Area.TEXTO));
        areas.add(criarBotaoArea(MensagensComunicabilidadeChatbot.Area.VERGNAUD));
        areas.add(criarBotaoArea(MensagensComunicabilidadeChatbot.Area.COMPLEMENTAR));
        rodape.add(areas); rodape.add(Box.createVerticalStrut(8));
        JPanel envio = new JPanel(new BorderLayout(6,0)); envio.setOpaque(false);
        entrada.setFont(new Font("Arial", Font.PLAIN, 14));
        entrada.getAccessibleContext().setAccessibleName(localizacao.texto("ui.chat.input"));
        entrada.addActionListener(e -> enviar());
        JButton enviar = criarBotao(localizacao.texto("ui.chat.send")); enviar.addActionListener(e -> enviar());
        envio.add(entrada, BorderLayout.CENTER); envio.add(enviar, BorderLayout.EAST); rodape.add(envio);
        raiz.add(rodape, BorderLayout.SOUTH); setContentPane(raiz); setSize(680,500);
        escrever("Gérard", localizacao.texto("ui.chat.greeting"));
    }

    public void abrirPertoDe(Component referencia) {
        if (!isVisible()) { setLocationRelativeTo(referencia); setVisible(true); }
        toFront(); entrada.requestFocusInWindow();
    }

    private JButton criarBotaoArea(final MensagensComunicabilidadeChatbot.Area area) {
        JButton b = criarBotao(localizacao.texto(mensagensComunicabilidade.chaveArea(area)));
        b.addActionListener(e -> { areaAtual=area; registrar("NEUTRA_OPERACIONAL", "selecionar_area="+area.name()); responder(MensagensComunicabilidadeChatbot.Intencao.DUVIDA); });
        return b;
    }

    private JLabel criarAvatarGerard() {
        java.net.URL recurso = DialogoChatbotGerard.class.getResource("/gerard/imagens/gerard_vergnaud.png");
        JLabel avatar = new JLabel();
        avatar.setPreferredSize(new Dimension(64, 64));
        avatar.setBorder(BorderFactory.createLineBorder(UITemaGerard.COR_BORDA));
        if (recurso != null) {
            Image imagem = new ImageIcon(recurso).getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
            avatar.setIcon(new ImageIcon(imagem));
        }
        String descricao = localizacao.texto("ui.chat.avatar");
        avatar.setToolTipText(descricao);
        avatar.getAccessibleContext().setAccessibleName(descricao);
        avatar.getAccessibleContext().setAccessibleDescription(descricao);
        return avatar;
    }
    private JButton criarBotao(String texto) {
        JButton b=new JButton(texto); b.setFont(new Font("Arial",Font.BOLD,12)); b.setForeground(UITemaGerard.COR_TEXTO);
        b.setBackground(UITemaGerard.COR_SUPERFICIE); b.setFocusPainted(true); b.setBorder(BorderFactory.createLineBorder(UITemaGerard.COR_BORDA)); return b;
    }
    private void enviar() {
        String texto=entrada.getText()==null?"":entrada.getText().trim(); if(texto.length()==0)return;
        entrada.setText(""); escrever(localizacao.texto("ui.chat.you"),texto); String n=normalizar(texto);
        if(contem(n,"complement","venn","quadrad","grafico","eixo")) areaAtual=MensagensComunicabilidadeChatbot.Area.COMPLEMENTAR;
        else if(contem(n,"diagrama","vergnaud","arrast","modelo","sinal","menos","digitar","valor")) areaAtual=MensagensComunicabilidadeChatbot.Area.VERGNAUD;
        else if(contem(n,"texto","enunciado","leitura","reler","personagem")) areaAtual=MensagensComunicabilidadeChatbot.Area.TEXTO;
        MensagensComunicabilidadeChatbot.Intencao i=MensagensComunicabilidadeChatbot.Intencao.DUVIDA; String c="NEUTRA_DUVIDA";
        if(contem(n,"proximo","passo","agora")){i=MensagensComunicabilidadeChatbot.Intencao.PROXIMO_PASSO;c="NEUTRA_SOLICITACAO_AJUDA";}
        else if(contem(n,"continu","voltar","retomar")){i=MensagensComunicabilidadeChatbot.Intencao.CONTINUAR;c="NEUTRA_TRANSICAO";}
        else if(contem(n,"dica","ajuda","help","aide","ayuda"))c="NEUTRA_SOLICITACAO_AJUDA";
        else if(contem(n,"li o","reli","leitura","read","lu l","lei el"))c="NEUTRA_METACOGNITIVA";
        registrar(c,"area="+areaAtual.name()+"; texto="+resumir(texto)); responder(i);
    }
    private void responder(MensagensComunicabilidadeChatbot.Intencao i){escrever("Gérard",localizacao.texto(mensagensComunicabilidade.chaveMensagem(areaAtual,i)));}
    private void escrever(String autor,String texto){if(conversa.getDocument().getLength()>0)conversa.append("\n\n");conversa.append(autor+": "+texto);conversa.setCaretPosition(conversa.getDocument().getLength());}
    private void registrar(String c,String d){if(ouvinte!=null)ouvinte.registrar(c,d);}
    private static boolean contem(String t,String...xs){for(String x:xs)if(t.contains(x))return true;return false;}
    private static String normalizar(String t){return Normalizer.normalize(t,Normalizer.Form.NFD).replaceAll("\\p{M}+","").toLowerCase(Locale.ROOT);}
    private static String resumir(String t){String s=t.replace('\n',' ').replace('\r',' ').trim();return s.length()<=160?s:s.substring(0,160);}
}
