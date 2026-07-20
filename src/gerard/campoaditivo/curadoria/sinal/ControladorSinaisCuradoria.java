package gerard.campoaditivo.curadoria.sinal;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.i18n.ServicoLocalizacao;
import java.awt.Component;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 * Coordena os seletores de sinal de uma única tela de curadoria.
 */
public final class ControladorSinaisCuradoria {
    private final TipoSituacaoAditiva tipo;
    private final ServicoLocalizacao localizacao;
    private final Map<PapelSinalCuradoria, PainelValorComSinalCuradoria> paineis =
            new EnumMap<PapelSinalCuradoria, PainelValorComSinalCuradoria>(
                    PapelSinalCuradoria.class);
    private final List<PainelValorComSinalCuradoria> ordemValidacao =
            new ArrayList<PainelValorComSinalCuradoria>();

    public ControladorSinaisCuradoria(TipoSituacaoAditiva tipo,
            ServicoLocalizacao localizacao) {
        this.tipo = tipo;
        this.localizacao = localizacao == null
                ? ServicoLocalizacao.getInstancia() : localizacao;
    }

    public PainelValorComSinalCuradoria registrar(PapelSinalCuradoria papel,
            ModoPersistenciaSinalCuradoria modo, JTextField campo,
            String sinalAtual) {
        PainelValorComSinalCuradoria painel = new PainelValorComSinalCuradoria(
                tipo, papel, modo, campo, sinalAtual, localizacao);
        paineis.put(papel, painel);
        ordemValidacao.add(painel);
        return painel;
    }

    public PainelValorComSinalCuradoria registrarSeAplicavel(
            PapelSinalCuradoria papel, ModoPersistenciaSinalCuradoria modo,
            JTextField campo, String sinalAtual) {
        if (!new PoliticaSinalCuradoria().exigeEscolha(tipo, papel)) {
            return null;
        }
        return registrar(papel, modo, campo, sinalAtual);
    }

    public PainelValorComSinalCuradoria obter(PapelSinalCuradoria papel) {
        return paineis.get(papel);
    }

    public boolean validarAntesDeSalvar(Component pai) {
        for (PainelValorComSinalCuradoria painel : ordemValidacao) {
            ResultadoValidacaoSinalCuradoria resultado = painel.validarEscolha();
            if (!resultado.isValido()) {
                painel.destacarErro();
                String mensagem = localizacao.formatar(
                        resultado.getChaveMensagem(),
                        painel.getPapel().descricao(localizacao));
                JOptionPane.showMessageDialog(pai, mensagem,
                        localizacao.texto("curadoria.sinal.tituloValidacao"),
                        JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }
        return true;
    }

    public void definirSemanticaHerdada(boolean herdada, String dica) {
        for (PainelValorComSinalCuradoria painel : ordemValidacao) {
            painel.definirHerdado(herdada, dica);
        }
    }

    public void normalizarCamposExibidos() {
        for (PainelValorComSinalCuradoria painel : ordemValidacao) {
            painel.normalizarCampoExibido();
        }
    }

    public String obterValorParaPersistencia(PapelSinalCuradoria papel,
            String valorAlternativo) {
        PainelValorComSinalCuradoria painel = paineis.get(papel);
        return painel == null
                ? (valorAlternativo == null ? "" : valorAlternativo.trim())
                : painel.obterValorParaPersistencia();
    }

    public String obterSinalCanonico(PapelSinalCuradoria papel) {
        PainelValorComSinalCuradoria painel = paineis.get(papel);
        return painel == null ? "" : painel.obterSinalCanonico();
    }

    public String obterMagnitude(PapelSinalCuradoria papel) {
        PainelValorComSinalCuradoria painel = paineis.get(papel);
        return painel == null ? "" : painel.obterMagnitude();
    }
}
