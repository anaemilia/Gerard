package gerard.interacao.arraste;

/**
 * Protocolo portatil dos paineis de eixo das Relacoes. Nao conhece Swing,
 * AWT, componentes visuais concretos, layout nem regras matematicas.
 *
 * A autorizacao para alterar um valor semantico chega como contexto. A
 * sincronizacao entre representacoes e a confirmacao da incognita permanecem
 * fora deste handler, com seus proprietarios atuais.
 */
public final class HandlerInteracaoPaineisEixosRelacoes {

    public static final class ResultadoPressionamento {
        private static final ResultadoPressionamento NAO_CONSUMIDO =
                new ResultadoPressionamento(false, false, false,
                        AlvoInteracaoPaineisEixosRelacoes.NaturezaInteracao.NENHUMA,
                        AlvoInteracaoPaineisEixosRelacoes.ModoManipulacao.NENHUM);

        private final boolean consumido;
        private final boolean bloqueado;
        private final boolean ocultado;
        private final AlvoInteracaoPaineisEixosRelacoes.NaturezaInteracao natureza;
        private final AlvoInteracaoPaineisEixosRelacoes.ModoManipulacao modoManipulacao;

        private ResultadoPressionamento(boolean consumido, boolean bloqueado,
                boolean ocultado,
                AlvoInteracaoPaineisEixosRelacoes.NaturezaInteracao natureza,
                AlvoInteracaoPaineisEixosRelacoes.ModoManipulacao modoManipulacao) {
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

        public AlvoInteracaoPaineisEixosRelacoes.NaturezaInteracao getNatureza() {
            return natureza;
        }

        public AlvoInteracaoPaineisEixosRelacoes.ModoManipulacao getModoManipulacao() {
            return modoManipulacao;
        }
    }

    private AlvoInteracaoPaineisEixosRelacoes alvoAtivo;

    public ResultadoPressionamento iniciar(
            AlvoInteracaoPaineisEixosRelacoes alvo,
            int posicaoX, int posicaoY, boolean alteracaoSemanticaLiberada) {
        cancelar();
        if (alvo == null) {
            return ResultadoPressionamento.NAO_CONSUMIDO;
        }

        AlvoInteracaoPaineisEixosRelacoes.NaturezaInteracao natureza =
                alvo.identificarNatureza(posicaoX, posicaoY);
        if (natureza
                == AlvoInteracaoPaineisEixosRelacoes.NaturezaInteracao.NENHUMA) {
            return ResultadoPressionamento.NAO_CONSUMIDO;
        }
        if (natureza
                == AlvoInteracaoPaineisEixosRelacoes.NaturezaInteracao.VALOR_SEMANTICO
                && !alteracaoSemanticaLiberada) {
            return new ResultadoPressionamento(false, true, false, natureza,
                    AlvoInteracaoPaineisEixosRelacoes.ModoManipulacao.NENHUM);
        }
        if (!alvo.processarPressionamento(posicaoX, posicaoY)) {
            return ResultadoPressionamento.NAO_CONSUMIDO;
        }

        AlvoInteracaoPaineisEixosRelacoes.ModoManipulacao modo =
                alvo.obterModoManipulacao();
        if (modo != AlvoInteracaoPaineisEixosRelacoes.ModoManipulacao.NENHUM) {
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
        AlvoInteracaoPaineisEixosRelacoes alvoConcluido = alvoAtivo;
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
