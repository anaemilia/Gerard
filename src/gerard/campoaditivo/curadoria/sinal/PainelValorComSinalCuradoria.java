package gerard.campoaditivo.curadoria.sinal;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.i18n.ServicoLocalizacao;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.text.AbstractDocument;

/**
 * Componente coeso da curadoria para magnitude + escolha obrigatória do sinal.
 */
public final class PainelValorComSinalCuradoria extends JPanel {
    private static final Color COR_ERRO = new Color(183, 28, 28);

    private final TipoSituacaoAditiva tipo;
    private final PapelSinalCuradoria papel;
    private final ModoPersistenciaSinalCuradoria modoPersistencia;
    private final ServicoLocalizacao localizacao;
    private final PoliticaSinalCuradoria politica;
    private final JTextField campoMagnitude;
    private final JComboBox<OpcaoSinalCuradoria> seletorSinal;
    private final Border bordaPadrao;
    private boolean herdado;

    public PainelValorComSinalCuradoria(TipoSituacaoAditiva tipo,
            PapelSinalCuradoria papel, ModoPersistenciaSinalCuradoria modoPersistencia,
            JTextField campoMagnitude, String sinalAtual,
            ServicoLocalizacao localizacao) {
        super(new BorderLayout(6, 0));
        this.tipo = tipo;
        this.papel = papel;
        this.modoPersistencia = modoPersistencia == null
                ? ModoPersistenciaSinalCuradoria.METADADO_SEPARADO : modoPersistencia;
        this.localizacao = localizacao == null
                ? ServicoLocalizacao.getInstancia() : localizacao;
        this.politica = new PoliticaSinalCuradoria();
        this.campoMagnitude = campoMagnitude == null ? new JTextField() : campoMagnitude;
        String valorOriginal = this.campoMagnitude.getText();
        this.campoMagnitude.setText(PoliticaSinalCuradoria.magnitudeSemSinal(
                valorOriginal));
        if (this.campoMagnitude.getDocument() instanceof AbstractDocument) {
            ((AbstractDocument) this.campoMagnitude.getDocument())
                    .setDocumentFilter(new FiltroMagnitudeSemSinal());
        }

        this.seletorSinal = new JComboBox<OpcaoSinalCuradoria>(
                OpcaoSinalCuradoria.values());
        this.seletorSinal.setFont(new Font("Arial", Font.PLAIN, 12));
        this.seletorSinal.setPreferredSize(new Dimension(176,
                this.seletorSinal.getPreferredSize().height));
        this.seletorSinal.setRenderer(new RenderizadorOpcaoSinal());
        this.seletorSinal.setSelectedItem(
                OpcaoSinalCuradoria.aPartirDoEstado(sinalAtual, valorOriginal));
        this.seletorSinal.addActionListener(e -> removerDestaqueErro());

        setOpaque(false);
        add(this.campoMagnitude, BorderLayout.CENTER);
        add(this.seletorSinal, BorderLayout.EAST);
        this.bordaPadrao = getBorder();
        configurarAcessibilidade();
    }

    private void configurarAcessibilidade() {
        String descricaoPapel = papel.descricao(localizacao);
        campoMagnitude.setToolTipText(localizacao.formatar(
                "curadoria.sinal.magnitudeTooltip", descricaoPapel));
        seletorSinal.setToolTipText(localizacao.formatar(
                "curadoria.sinal.seletorTooltip", descricaoPapel));
        campoMagnitude.getAccessibleContext().setAccessibleName(
                localizacao.formatar("curadoria.sinal.magnitudeAcessivel", descricaoPapel));
        campoMagnitude.getAccessibleContext().setAccessibleDescription(
                campoMagnitude.getToolTipText());
        seletorSinal.getAccessibleContext().setAccessibleName(
                localizacao.formatar("curadoria.sinal.seletorAcessivel", descricaoPapel));
        seletorSinal.getAccessibleContext().setAccessibleDescription(
                seletorSinal.getToolTipText());
    }

    public JTextField getCampoMagnitude() {
        return campoMagnitude;
    }

    public JComboBox<OpcaoSinalCuradoria> getSeletorSinal() {
        return seletorSinal;
    }

    public PapelSinalCuradoria getPapel() {
        return papel;
    }

    public ModoPersistenciaSinalCuradoria getModoPersistencia() {
        return modoPersistencia;
    }

    public String obterMagnitude() {
        return PoliticaSinalCuradoria.magnitudeSemSinal(campoMagnitude.getText());
    }

    public OpcaoSinalCuradoria obterOpcaoSelecionada() {
        Object selecionado = seletorSinal.getSelectedItem();
        return selecionado instanceof OpcaoSinalCuradoria
                ? (OpcaoSinalCuradoria) selecionado
                : OpcaoSinalCuradoria.NAO_SELECIONADO;
    }

    public String obterSinalCanonico() {
        return obterOpcaoSelecionada().getValorCanonico();
    }

    public String obterValorAssinado() {
        return PoliticaSinalCuradoria.aplicarSinal(obterMagnitude(),
                obterOpcaoSelecionada());
    }

    public ResultadoValidacaoSinalCuradoria validarEscolha() {
        if (herdado) {
            return ResultadoValidacaoSinalCuradoria.valido();
        }
        return politica.validar(tipo, papel, obterOpcaoSelecionada(),
                obterMagnitude());
    }

    public void destacarErro() {
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_ERRO, 2),
                BorderFactory.createEmptyBorder(1, 1, 1, 1)));
        seletorSinal.requestFocusInWindow();
    }

    public void removerDestaqueErro() {
        setBorder(bordaPadrao);
    }

    public void definirHerdado(boolean herdado, String dica) {
        this.herdado = herdado;
        campoMagnitude.setEditable(!herdado);
        campoMagnitude.setFocusable(!herdado);
        seletorSinal.setEnabled(!herdado);
        if (herdado) {
            campoMagnitude.setToolTipText(dica);
            seletorSinal.setToolTipText(dica);
        } else {
            configurarAcessibilidade();
        }
    }

    public void normalizarCampoExibido() {
        campoMagnitude.setText(obterMagnitude());
    }

    public String obterValorParaPersistencia() {
        if (modoPersistencia == ModoPersistenciaSinalCuradoria.EMBUTIDO_NO_VALOR) {
            return obterValorAssinado();
        }
        return obterMagnitude();
    }

    private final class RenderizadorOpcaoSinal extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            Component componente = super.getListCellRendererComponent(list, value,
                    index, isSelected, cellHasFocus);
            if (value instanceof OpcaoSinalCuradoria) {
                setText(((OpcaoSinalCuradoria) value).rotulo(localizacao));
            }
            return componente;
        }
    }
}
