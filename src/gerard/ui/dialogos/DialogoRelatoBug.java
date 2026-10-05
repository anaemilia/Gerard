package gerard.ui.dialogos;

import gerard.i18n.ServicoLocalizacao;
import gerard.suporte.PreparadorEmailRelatoBug;
import gerard.suporte.RegistroRelatoBug;
import gerard.ui.UITemaGerard;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/** Diálogo de relato de bug: pede a descrição, registra o relato e prepara o e-mail. Os dados da situação vêm prontos. */
public final class DialogoRelatoBug {
    private DialogoRelatoBug() {
    }

    public static void mostrar(Component pai, ServicoLocalizacao localizacao, DadosRelatoBug dados) {
        final JTextArea descricao = new JTextArea(7, 46);
        descricao.setLineWrap(true);
        descricao.setWrapStyleWord(true);
        descricao.setFont(new Font("Arial", Font.PLAIN, 14));
        descricao.setToolTipText(localizacao.texto("ui.bug.description.tooltip"));
        descricao.getAccessibleContext().setAccessibleName(localizacao.texto("ui.bug.description.label"));
        descricao.getAccessibleContext().setAccessibleDescription(localizacao.texto("ui.bug.description.tooltip"));

        JScrollPane rolagem = new JScrollPane(descricao);
        rolagem.setPreferredSize(new Dimension(520, 150));

        JLabel instrucao = new JLabel("<html>" + localizacao.texto("ui.bug.instruction") + "</html>");
        instrucao.setFont(gerard.ui.UITemaGerard.FONTE_DIALOGO);

        JPanel conteudo = new JPanel(new BorderLayout(0, 10));
        conteudo.setBorder(BorderFactory.createEmptyBorder(8, 8, 6, 8));
        conteudo.add(instrucao, BorderLayout.NORTH);
        conteudo.add(rolagem, BorderLayout.CENTER);

        Object[] opcoes = {
            localizacao.texto("ui.bug.submit"),
            localizacao.texto("analise.cancel")
        };
        int resposta = JOptionPane.showOptionDialog(
                pai,
                conteudo,
                localizacao.texto("ui.bug.title"),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                opcoes,
                opcoes[0]);

        if (resposta != JOptionPane.OK_OPTION) {
            pai.requestFocusInWindow();
            return;
        }

        String relato = descricao.getText() == null ? "" : descricao.getText().trim();
        if (relato.length() == 0) {
            JOptionPane.showMessageDialog(
                    pai,
                    localizacao.texto("ui.bug.required"),
                    localizacao.texto("ui.bug.title"),
                    JOptionPane.WARNING_MESSAGE);
            pai.requestFocusInWindow();
            return;
        }

        try {
                String situacaoId = dados.getSituacaoId();
                String idiomaSituacao = dados.getIdiomaSituacao();
                String enunciadoAtual = dados.getEnunciadoAtual();
                String categoria = dados.getCategoria();
                String representacoes = dados.getRepresentacoes();
                String idiomaInterface = dados.getIdiomaInterface();
            java.io.File arquivo = RegistroRelatoBug.registrar(
                    relato,
                    situacaoId,
                    categoria,
                    representacoes,
                    idiomaInterface,
                    idiomaSituacao,
                    enunciadoAtual);
            PreparadorEmailRelatoBug.MensagemPreparada email =
                    PreparadorEmailRelatoBug.preparar(
                            relato,
                            situacaoId,
                            categoria,
                            representacoes,
                            idiomaInterface,
                            idiomaSituacao,
                            enunciadoAtual);
            PreparadorEmailRelatoBug.ResultadoAbertura resultadoEmail =
                    PreparadorEmailRelatoBug.abrirMensagem(email);
            String chaveMensagemEmail;
            int tipoMensagemEmail;
            switch (resultadoEmail) {
                case GMAIL_WEB:
                    chaveMensagemEmail = "ui.bug.gmailOpened";
                    tipoMensagemEmail = JOptionPane.INFORMATION_MESSAGE;
                    break;
                case CLIENTE_PADRAO:
                    chaveMensagemEmail = "ui.bug.emailClientOpened";
                    tipoMensagemEmail = JOptionPane.INFORMATION_MESSAGE;
                    break;
                default:
                    chaveMensagemEmail = "ui.bug.emailUnavailable";
                    tipoMensagemEmail = JOptionPane.WARNING_MESSAGE;
                    break;
            }
            JOptionPane.showMessageDialog(
                    pai,
                    localizacao.formatar(
                            chaveMensagemEmail,
                            arquivo.getAbsolutePath(),
                            PreparadorEmailRelatoBug.DESTINATARIO),
                    localizacao.texto("ui.bug.title"),
                    tipoMensagemEmail);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    pai,
                    localizacao.formatar("ui.bug.error", ex.getMessage()),
                    localizacao.texto("ui.bug.title"),
                    JOptionPane.ERROR_MESSAGE);
        }
        pai.requestFocusInWindow();
    }
}
