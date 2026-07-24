package gerard.agente.monitor;

import gerard.Scaffolding.questionamento.ResultadoQuestionamento;
import gerard.Scaffolding.questionamento.ScaffoldingQuestionamento;
import java.util.ArrayList;
import java.util.List;

/**
 * Extração nomeada do ponto único onde o Gérard já decide se uma ação do
 * usuário está certa ou errada (ver gerard-ajuda-adaptativa/references/
 * agente-monitor.md — proposta teórica cujo papel é justamente este).
 *
 * Esta classe NÃO reimplementa a comparação: delega inteiramente para
 * ScaffoldingQuestionamento.avaliarPosicionamento, que já existia e continua
 * inalterada. A única coisa nova é notificar observadores (ex.: um
 * indicador visual) a cada veredito aplicável, sem alterar o resultado nem
 * o comportamento de feedback já consolidado (tremor/som/cor).
 */
public final class AgenteMonitor {
    private final ScaffoldingQuestionamento scaffoldingQuestionamento;
    private final List<OuvinteVeredictoAgenteMonitor> ouvintes =
            new ArrayList<OuvinteVeredictoAgenteMonitor>();

    public AgenteMonitor(ScaffoldingQuestionamento scaffoldingQuestionamento) {
        this.scaffoldingQuestionamento = scaffoldingQuestionamento;
    }

    public void adicionarOuvinte(OuvinteVeredictoAgenteMonitor ouvinte) {
        if (ouvinte != null) {
            ouvintes.add(ouvinte);
        }
    }

    public ResultadoQuestionamento avaliarPosicionamento(
            String chavePapelNumeral,
            String chavePapelAlvo,
            String papelDoElementoNoDiagrama,
            String categoriaEscolhida) {
        ResultadoQuestionamento resultado = scaffoldingQuestionamento.avaliarPosicionamento(
                chavePapelNumeral, chavePapelAlvo, papelDoElementoNoDiagrama, categoriaEscolhida);
        if (resultado != null && resultado.isAplicavel()) {
            notificar(resultado.isCorreto());
        }
        return resultado;
    }

    /**
     * Percebe uma ação instrumental que não é avaliável como certo/errado
     * (ex.: Selecionar um valor do enunciado, antes de qualquer
     * posicionamento — não há papel-alvo ainda para comparar). Usa o mesmo
     * canal de notificação de avaliarPosicionamento (o LED não distingue
     * certo/errado de qualquer forma), mas sem produzir veredito: o
     * argumento de aoAvaliar aqui não representa um julgamento, só mantém a
     * mesma assinatura de notificação.
     */
    public void perceberAcao() {
        notificar(true);
    }

    private void notificar(boolean correto) {
        for (OuvinteVeredictoAgenteMonitor ouvinte : ouvintes) {
            ouvinte.aoAvaliar(correto);
        }
    }
}
