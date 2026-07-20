package gerard.campoaditivo.curadoria;

import gerard.i18n.ServicoLocalizacao;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.border.Border;

/**
 * Aviso visual não modal para a ausência do papel ocupado pela incógnita.
 *
 * A classe apenas observa o combo da curadoria. Ela não infere papéis, não
 * altera valores semânticos e não interfere nas regras de salvamento.
 */
public final class AvisoTermoDesconhecidoVazio {
    private static final Color COR_AVISO = new Color(180, 83, 9);
    private static final Color COR_BORDA_AVISO = new Color(245, 158, 11);

    private final JComboBox<String> campo;
    private final JLabel aviso;
    private final Border bordaOriginal;
    private final ServicoLocalizacao localizacao;
    private final boolean estavaVazioAoAbrir;
    private final String valorPreenchidoAutomaticamente;
    private boolean semanticaHerdada;
    private boolean usuarioAlterouSelecao;

    public AvisoTermoDesconhecidoVazio(JComboBox<String> campo,
            boolean estavaVazioAoAbrir,
            String valorPreenchidoAutomaticamente,
            ServicoLocalizacao localizacao) {
        if (campo == null) {
            throw new IllegalArgumentException("O combo termo_desconhecido é obrigatório.");
        }
        this.campo = campo;
        this.estavaVazioAoAbrir = estavaVazioAoAbrir;
        this.valorPreenchidoAutomaticamente = limpar(valorPreenchidoAutomaticamente);
        this.localizacao = localizacao == null
                ? ServicoLocalizacao.getInstancia() : localizacao;
        this.bordaOriginal = campo.getBorder();
        this.aviso = new JLabel();
        this.aviso.setFont(new Font("Arial", Font.PLAIN, 11));
        this.aviso.setForeground(COR_AVISO);
        this.aviso.setBorder(BorderFactory.createEmptyBorder(0, 2, 2, 0));

        campo.addActionListener(e -> {
            usuarioAlterouSelecao = true;
            atualizar();
        });
        atualizar();
    }

    public JLabel getComponente() {
        return aviso;
    }

    public void definirSemanticaHerdada(boolean semanticaHerdada) {
        this.semanticaHerdada = semanticaHerdada;
        atualizar();
    }

    public boolean termoEstaVazio() {
        return limpar(campo.getSelectedItem() == null
                ? "" : campo.getSelectedItem().toString()).length() == 0;
    }

    public boolean isAvisoVisivel() {
        return aviso.isVisible();
    }

    public String getTextoAviso() {
        return aviso.getText();
    }

    public void atualizar() {
        boolean vazio = termoEstaVazio();
        boolean mostrarPreenchimentoAutomatico = estavaVazioAoAbrir
                && !valorPreenchidoAutomaticamente.isEmpty()
                && !usuarioAlterouSelecao;
        boolean mostrar = !semanticaHerdada
                && (vazio || mostrarPreenchimentoAutomatico);

        if (vazio) {
            aviso.setText(localizacao.texto(
                    "curadoria.termoDesconhecido.avisoVazio"));
            campo.setBorder(BorderFactory.createLineBorder(
                    COR_BORDA_AVISO, 1));
            campo.setToolTipText(localizacao.texto(
                    "curadoria.termoDesconhecido.tooltipVazio"));
        } else if (mostrarPreenchimentoAutomatico) {
            aviso.setText(localizacao.formatar(
                    "curadoria.termoDesconhecido.avisoPreenchidoAutomaticamente",
                    valorPreenchidoAutomaticamente));
            campo.setBorder(bordaOriginal);
            campo.setToolTipText(localizacao.texto(
                    "curadoria.termoDesconhecido.tooltipRevisar"));
        } else {
            aviso.setText(" ");
            campo.setBorder(bordaOriginal);
            campo.setToolTipText(localizacao.texto(
                    "curadoria.termoDesconhecido.tooltipPreenchido"));
        }
        aviso.setVisible(mostrar);
    }

    private String limpar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
