package gerard.interacao.arraste;

/**
 * Protocolo portatil da interacao com um eixo dos inteiros. Nao conhece
 * Swing/AWT, tela, layout, coordenadas fixas nem o objeto visual concreto.
 *
 * A autorizacao para alterar o valor semantico e recebida como contexto; a
 * decisao de liberar a representacao continua pertencendo ao fluxo de
 * modelagem que invoca este handler.
 */
public final class HandlerInteracaoEixoInteiros {

    public static final class ResultadoPressionamento {
        private static final ResultadoPressionamento NAO_CONSUMIDO =
                new ResultadoPressionamento(false, false, false,
                        AlvoInteracaoEixoInteiros.NaturezaInteracao.NENHUMA,
                        AlvoInteracaoEixoInteiros.ModoManipulacao.NENHUM);

        private final boolean consumido;
        private final boolean bloqueado;
        private final boolean ocultado;
        private final AlvoInteracaoEixoInteiros.NaturezaInteracao natureza;
        private final AlvoInteracaoEixoInteiros.ModoManipulacao modoManipulacao;

        private ResultadoPressionamento(boolean consumido, boolean bloqueado,
                boolean ocultado,
                AlvoInteracaoEixoInteiros.NaturezaInteracao natureza,
                AlvoInteracaoEixoInteiros.ModoManipulacao modoManipulacao) {
            this.consumido = consumido;
            this.bloqueado = bloqueado;
            this.ocultado = ocultado;
            this.natureza = natureza;
            this.modoManipulacao = modoManipulacao;
        }

        public boolean foiConsumido() {
            return consumido;
        }

        public boolean foiBloqueado() {
            return bloqueado;
        }

        public boolean foiOcultado() {
            return ocultado;
        }

        public AlvoInteracaoEixoInteiros.NaturezaInteracao getNatureza() {
            return natureza;
        }

        public AlvoInteracaoEixoInteiros.ModoManipulacao getModoManipulacao() {
            return modoManipulacao;
        }
    }

    private AlvoInteracaoEixoInteiros alvoAtivo;

    public ResultadoPressionamento iniciar(AlvoInteracaoEixoInteiros alvo,
            int posicaoX, int posicaoY, boolean alteracaoSemanticaLiberada) {
        cancelar();
        if (alvo == null) {
            return ResultadoPressionamento.NAO_CONSUMIDO;
        }

        AlvoInteracaoEixoInteiros.NaturezaInteracao natureza =
                alvo.identificarNatureza(posicaoX, posicaoY);
        if (natureza == AlvoInteracaoEixoInteiros.NaturezaInteracao.NENHUMA) {
            return ResultadoPressionamento.NAO_CONSUMIDO;
        }
        if (natureza == AlvoInteracaoEixoInteiros.NaturezaInteracao.VALOR_SEMANTICO
                && !alteracaoSemanticaLiberada) {
            return new ResultadoPressionamento(false, true, false, natureza,
                    AlvoInteracaoEixoInteiros.ModoManipulacao.NENHUM);
        }
        if (!alvo.processarPressionamento(posicaoX, posicaoY)) {
            return ResultadoPressionamento.NAO_CONSUMIDO;
        }

        AlvoInteracaoEixoInteiros.ModoManipulacao modo =
                alvo.obterModoManipulacao();
        if (modo != AlvoInteracaoEixoInteiros.ModoManipulacao.NENHUM) {
            alvoAtivo = alvo;
        }
        return new ResultadoPressionamento(true, false,
                alvo.foiOcultadoPorInteracao(), natureza, modo);
    }

    public boolean mover(int posicaoX, int posicaoY) {
        if (alvoAtivo == null) {
            return false;
        }
        alvoAtivo.moverPara(posicaoX, posicaoY);
        return true;
    }

    public boolean concluir() {
        if (alvoAtivo == null) {
            return false;
        }
        AlvoInteracaoEixoInteiros alvoConcluido = alvoAtivo;
        alvoAtivo = null;
        alvoConcluido.finalizarManipulacao();
        return true;
    }

    public void cancelar() {
        if (alvoAtivo != null) {
            alvoAtivo.finalizarManipulacao();
        }
        alvoAtivo = null;
    }

    public boolean estaAtivo() {
        return alvoAtivo != null;
    }
}
