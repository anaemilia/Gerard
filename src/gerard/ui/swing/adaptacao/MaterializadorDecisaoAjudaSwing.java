package gerard.ui.swing.adaptacao;

import gerard.adaptacao.DecisaoAjuda;
import gerard.adaptacao.ItemRepertorioAjuda;
import gerard.aplicacao.adaptacao.ConfirmacaoMaterializacaoAjuda;
import gerard.aplicacao.adaptacao.MaterializadorDecisaoAjuda;
import gerard.dominio.campoaditivo.ModalidadeEntregaScaffolding;
import gerard.i18n.ServicoLocalizacao;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Sintaxe Swing dos apoios do repertorio da incognita. A classe nao consulta
 * regras nem escolhe qual apoio aplicar: recebe uma {@link DecisaoAjuda} ja
 * concluida pelo proprietario semantico.
 */
public final class MaterializadorDecisaoAjudaSwing
        implements MaterializadorDecisaoAjuda {

    private final Component componentePai;
    private final ServicoLocalizacao localizacao;
    private final Runnable atualizadorMaterialConcreto;

    public MaterializadorDecisaoAjudaSwing(
            Component componentePai,
            ServicoLocalizacao localizacao,
            Runnable atualizadorMaterialConcreto) {
        if (componentePai == null || localizacao == null
                || atualizadorMaterialConcreto == null) {
            throw new IllegalArgumentException(
                    "componente, localizacao e atualizador do material sao obrigatorios");
        }
        this.componentePai = componentePai;
        this.localizacao = localizacao;
        this.atualizadorMaterialConcreto = atualizadorMaterialConcreto;
    }

    @Override
    public List<ConfirmacaoMaterializacaoAjuda> materializar(
            DecisaoAjuda decisao,
            String chavePapelAlvo) {
        if (decisao == null || !decisao.deveAplicarAjuda()
                || decisao.getAjuda() == null) {
            throw new IllegalArgumentException(
                    "a representacao exige uma decisao de ajuda aplicavel");
        }
        ItemRepertorioAjuda ajuda = decisao.getAjuda();
        String nomePapel = localizacao.texto(chavePapelAlvo);
        String mensagem = localizacao.formatar(
                ajuda.getChaveConteudo(), nomePapel);
        List<ConfirmacaoMaterializacaoAjuda> confirmacoes =
                new ArrayList<ConfirmacaoMaterializacaoAjuda>();

        if ("AG_EMLQ".equals(ajuda.getCodigo())) {
            JOptionPane.showConfirmDialog(
                    componentePai, mensagem,
                    localizacao.texto("ui.dialog.confirm"),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            confirmacoes.add(visual(ajuda,
                    "pergunta de confirmacao de valor divergente renderizada"));
            return confirmacoes;
        }
        if ("AG_EME".equals(ajuda.getCodigo())) {
            JOptionPane.showMessageDialog(
                    componentePai, mensagem,
                    localizacao.texto("ui.dialog.confirm"),
                    JOptionPane.INFORMATION_MESSAGE);
            confirmacoes.add(visual(ajuda,
                    "explicacao para revisar a operacao renderizada"));
            return confirmacoes;
        }
        if ("AG_EMCME".equals(ajuda.getCodigo())) {
            // A sequencia de rejeicoes ja tornou a representacao concreta
            // elegivel. Swing apenas atualiza sua projecao e confirma que a
            // affordance esta disponivel; nao possui a regra de ativacao.
            atualizadorMaterialConcreto.run();
            confirmacoes.add(new ConfirmacaoMaterializacaoAjuda(
                    ajuda.getCodigo(),
                    ModalidadeEntregaScaffolding.MANIPULATIVA,
                    "affordance ativa (modalidade interativa)",
                    "representacao concreta disponivel para manipulacao"));
            JOptionPane.showMessageDialog(
                    componentePai, mensagem,
                    localizacao.texto("ui.dialog.confirm"),
                    JOptionPane.INFORMATION_MESSAGE);
            confirmacoes.add(visual(ajuda,
                    "explicacao sobre o material concreto renderizada"));
            return confirmacoes;
        }
        throw new IllegalArgumentException(
                "a representacao Swing nao conhece o apoio decidido: "
                        + ajuda.getCodigo());
    }

    private ConfirmacaoMaterializacaoAjuda visual(
            ItemRepertorioAjuda ajuda,
            String detalhe) {
        return new ConfirmacaoMaterializacaoAjuda(
                ajuda.getCodigo(),
                ModalidadeEntregaScaffolding.VISUAL,
                "renderizado (modalidade passiva)",
                detalhe);
    }
}
