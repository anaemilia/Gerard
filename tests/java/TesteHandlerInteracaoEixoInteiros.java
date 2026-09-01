import gerard.Scaffolding.grafico.ScaffoldingGraficoInteiros;
import gerard.interacao.arraste.AlvoInteracaoEixoInteiros;
import gerard.interacao.arraste.HandlerInteracaoEixoInteiros;
import gerard.ui.vergnaud.AdaptadorInteracaoEixoInteiros;
import gerard.ui.vergnaud.FonteGeometriaInteracaoEixoInteiros;
import java.awt.Rectangle;

public final class TesteHandlerInteracaoEixoInteiros {

    public static void main(String[] args) {
        testarBloqueioSemanticoSemConhecerPolitica();
        testarCicloPortatilDeManipulacao();
        testarAdaptadorDaRepresentacaoDesktop();
        System.out.println("Teste aprovado: handler do eixo preserva bloqueio contextual, "
                + "ciclo pressionar-mover-concluir e adaptacao da geometria desktop.");
    }

    private static void testarBloqueioSemanticoSemConhecerPolitica() {
        AlvoFalso alvo = new AlvoFalso(
                AlvoInteracaoEixoInteiros.NaturezaInteracao.VALOR_SEMANTICO,
                AlvoInteracaoEixoInteiros.ModoManipulacao.PONTO_CONTROLE,
                false);
        HandlerInteracaoEixoInteiros handler =
                new HandlerInteracaoEixoInteiros();

        HandlerInteracaoEixoInteiros.ResultadoPressionamento resultado =
                handler.iniciar(alvo, 30, 40, false);

        exigir(resultado.foiBloqueado() && !resultado.foiConsumido(),
                "A alteracao semantica deveria ser bloqueada pelo contexto recebido.");
        exigir(alvo.pressionamentos == 0 && !handler.estaAtivo(),
                "O alvo nao pode ser modificado quando o contexto bloqueia a alteracao.");
    }

    private static void testarCicloPortatilDeManipulacao() {
        AlvoFalso alvo = new AlvoFalso(
                AlvoInteracaoEixoInteiros.NaturezaInteracao.COMPONENTE_VISUAL,
                AlvoInteracaoEixoInteiros.ModoManipulacao.PAINEL,
                false);
        HandlerInteracaoEixoInteiros handler =
                new HandlerInteracaoEixoInteiros();

        HandlerInteracaoEixoInteiros.ResultadoPressionamento resultado =
                handler.iniciar(alvo, 10, 20, false);
        exigir(resultado.foiConsumido() && !resultado.foiBloqueado()
                        && resultado.getModoManipulacao()
                        == AlvoInteracaoEixoInteiros.ModoManipulacao.PAINEL,
                "Mover o componente visual deve permanecer permitido.");
        exigir(handler.estaAtivo() && handler.mover(50, 60),
                "O handler deveria manter e mover o alvo ativo.");
        exigir(alvo.ultimoX == 50 && alvo.ultimoY == 60,
                "As coordenadas portateis deveriam chegar ao alvo.");
        exigir(handler.concluir() && alvo.finalizacoes == 1
                        && !handler.estaAtivo(),
                "A conclusao deveria encerrar exatamente uma manipulacao.");
        exigir(!handler.mover(70, 80) && !handler.concluir(),
                "Um protocolo concluido nao pode continuar consumindo gestos.");

        AlvoFalso ocultavel = new AlvoFalso(
                AlvoInteracaoEixoInteiros.NaturezaInteracao.COMPONENTE_VISUAL,
                AlvoInteracaoEixoInteiros.ModoManipulacao.NENHUM,
                true);
        resultado = handler.iniciar(ocultavel, 1, 2, false);
        exigir(resultado.foiConsumido() && resultado.foiOcultado()
                        && !handler.estaAtivo(),
                "Ocultar deve consumir o clique sem iniciar um arraste.");
    }

    private static void testarAdaptadorDaRepresentacaoDesktop() {
        ScaffoldingGraficoInteiros grafico = new ScaffoldingGraficoInteiros();
        grafico.mostrar(new Rectangle(600, 350, 50, 50), "6");
        grafico.registrarEscolha("6", "+");
        AdaptadorInteracaoEixoInteiros adaptador =
                new AdaptadorInteracaoEixoInteiros(grafico,
                        new FonteGeometriaInteracaoEixoInteiros() {
                            @Override
                            public int obterLarguraTela() {
                                return 1240;
                            }

                            @Override
                            public int obterAlturaTela() {
                                return 760;
                            }

                            @Override
                            public Rectangle obterAreaDiagrama() {
                                return new Rectangle(0, 210, 1240, 550);
                            }
                        });

        Rectangle painel = adaptador.obterAreaVisualPainel();
        Rectangle ponto = adaptador.obterAreaVisualPontoControle();
        exigir(adaptador.identificarNatureza(
                        painel.x + 20, painel.y + 18)
                        == AlvoInteracaoEixoInteiros.NaturezaInteracao.COMPONENTE_VISUAL,
                "O adaptador deveria traduzir o cabecalho como componente visual.");
        exigir(adaptador.identificarNatureza(
                        ponto.x + ponto.width / 2, ponto.y + ponto.height / 2)
                        == AlvoInteracaoEixoInteiros.NaturezaInteracao.VALOR_SEMANTICO,
                "O adaptador deveria traduzir o ponto como valor semantico.");
        exigir(adaptador.processarPressionamento(
                        painel.x + 20, painel.y + 18)
                        && adaptador.obterModoManipulacao()
                        == AlvoInteracaoEixoInteiros.ModoManipulacao.PAINEL,
                "O adaptador deveria preservar o inicio do arraste do painel.");
        adaptador.finalizarManipulacao();
        exigir(!grafico.estaArrastando(),
                "Finalizar pelo adaptador deveria limpar o estado visual de arraste.");
    }

    private static final class AlvoFalso
            implements AlvoInteracaoEixoInteiros {
        private final NaturezaInteracao natureza;
        private final ModoManipulacao modo;
        private final boolean ocultado;
        private int pressionamentos;
        private int finalizacoes;
        private int ultimoX;
        private int ultimoY;

        private AlvoFalso(NaturezaInteracao natureza,
                ModoManipulacao modo, boolean ocultado) {
            this.natureza = natureza;
            this.modo = modo;
            this.ocultado = ocultado;
        }

        @Override
        public NaturezaInteracao identificarNatureza(int posicaoX, int posicaoY) {
            return natureza;
        }

        @Override
        public boolean processarPressionamento(int posicaoX, int posicaoY) {
            pressionamentos++;
            return true;
        }

        @Override
        public ModoManipulacao obterModoManipulacao() {
            return modo;
        }

        @Override
        public boolean foiOcultadoPorInteracao() {
            return ocultado;
        }

        @Override
        public boolean houveAlteracaoValorPorInteracao() {
            return false;
        }

        @Override
        public void moverPara(int posicaoX, int posicaoY) {
            ultimoX = posicaoX;
            ultimoY = posicaoY;
        }

        @Override
        public void finalizarManipulacao() {
            finalizacoes++;
        }
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
