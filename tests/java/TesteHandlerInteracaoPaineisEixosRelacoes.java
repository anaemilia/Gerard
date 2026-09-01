import gerard.campoaditivo.diagrama.elementos.ElementoVergnaud;
import gerard.campoaditivo.diagrama.modelo.TipoFiguraDiagrama;
import gerard.interacao.arraste.AlvoInteracaoPaineisEixosRelacoes;
import gerard.interacao.arraste.HandlerInteracaoPaineisEixosRelacoes;
import gerard.ui.vergnaud.AdaptadorInteracaoPaineisEixosRelacoes;
import gerard.ui.vergnaud.FonteGeometriaInteracaoPaineisEixosRelacoes;
import gerard.ui.vergnaud.PaineisEixosRelacoes;
import java.awt.Rectangle;
import java.util.Arrays;

public final class TesteHandlerInteracaoPaineisEixosRelacoes {

    private static final int LARGURA_TELA = 1240;
    private static final int ALTURA_TELA = 760;

    public static void main(String[] args) {
        testarBloqueioSemanticoSemConhecerPolitica();
        testarCicloPortatilDeManipulacao();
        testarAdaptadorDesktopDosPaineis();
        System.out.println("Teste aprovado: handler dos paineis de Relacoes preserva "
                + "bloqueio contextual, ciclo portatil e adaptacao desktop.");
    }

    private static void testarBloqueioSemanticoSemConhecerPolitica() {
        AlvoFalso alvo = new AlvoFalso(
                AlvoInteracaoPaineisEixosRelacoes.NaturezaInteracao.VALOR_SEMANTICO,
                AlvoInteracaoPaineisEixosRelacoes.ModoManipulacao.PONTO_CONTROLE,
                false);
        HandlerInteracaoPaineisEixosRelacoes handler =
                new HandlerInteracaoPaineisEixosRelacoes();

        HandlerInteracaoPaineisEixosRelacoes.ResultadoPressionamento resultado =
                handler.iniciar(alvo, 30, 40, false);

        exigir(resultado.foiBloqueado() && !resultado.foiConsumido(),
                "A alteracao semantica deveria ser bloqueada pelo contexto recebido.");
        exigir(alvo.pressionamentos == 0 && !handler.estaAtivo(),
                "O alvo nao pode mudar quando o contexto bloqueia a alteracao.");
    }

    private static void testarCicloPortatilDeManipulacao() {
        AlvoFalso alvo = new AlvoFalso(
                AlvoInteracaoPaineisEixosRelacoes.NaturezaInteracao.COMPONENTE_VISUAL,
                AlvoInteracaoPaineisEixosRelacoes.ModoManipulacao.PAINEL,
                false);
        HandlerInteracaoPaineisEixosRelacoes handler =
                new HandlerInteracaoPaineisEixosRelacoes();

        HandlerInteracaoPaineisEixosRelacoes.ResultadoPressionamento resultado =
                handler.iniciar(alvo, 10, 20, false);
        exigir(resultado.foiConsumido() && !resultado.foiBloqueado()
                        && resultado.getModoManipulacao()
                        == AlvoInteracaoPaineisEixosRelacoes.ModoManipulacao.PAINEL,
                "Mover o painel visual deve permanecer permitido.");
        exigir(handler.estaAtivo() && handler.mover(50, 60),
                "O handler deveria manter e mover o alvo ativo.");
        exigir(alvo.ultimoX == 50 && alvo.ultimoY == 60,
                "As coordenadas neutras deveriam chegar ao alvo.");
        exigir(handler.concluir() && alvo.finalizacoes == 1
                        && !handler.estaAtivo(),
                "A conclusao deveria encerrar exatamente uma manipulacao.");
        exigir(!handler.mover(70, 80) && !handler.concluir(),
                "Um protocolo concluido nao pode continuar consumindo gestos.");

        AlvoFalso ocultavel = new AlvoFalso(
                AlvoInteracaoPaineisEixosRelacoes.NaturezaInteracao.COMPONENTE_VISUAL,
                AlvoInteracaoPaineisEixosRelacoes.ModoManipulacao.NENHUM,
                true);
        resultado = handler.iniciar(ocultavel, 1, 2, false);
        exigir(resultado.foiConsumido() && resultado.foiOcultado()
                        && !handler.estaAtivo(),
                "Ocultar deve consumir o clique sem iniciar arraste.");
    }

    private static void testarAdaptadorDesktopDosPaineis() {
        final Rectangle areaDiagrama = new Rectangle(0, 210, 1240, 550);
        ElementoVergnaud elemento = new ElementoVergnaud(
                200, 260, 150, 100, TipoFiguraDiagrama.ELIPSE,
                "relacao", areaDiagrama, false);
        PaineisEixosRelacoes paineis = new PaineisEixosRelacoes();
        paineis.ativar(Arrays.asList(elemento));
        PaineisEixosRelacoes.Painel painel = paineis.obterPaineis().get(0);
        painel.apresentador.registrarEscolha(
                new Rectangle(elemento.x, elemento.y,
                        elemento.largura, elemento.altura),
                "5", "+");

        int tamanhoLupa = 22;
        int centroLupaX = elemento.x + elemento.largura - tamanhoLupa + 6
                + tamanhoLupa / 2;
        int centroLupaY = elemento.y - tamanhoLupa / 2 - 2
                + tamanhoLupa / 2;
        exigir(paineis.processarPressionamentoLupa(
                        centroLupaX, centroLupaY) == painel,
                "A pre-condicao deveria revelar o painel pela geometria da lupa.");

        AdaptadorInteracaoPaineisEixosRelacoes adaptador =
                new AdaptadorInteracaoPaineisEixosRelacoes(
                        paineis,
                        new FonteGeometriaInteracaoPaineisEixosRelacoes() {
                            @Override
                            public int obterLarguraTela() {
                                return LARGURA_TELA;
                            }

                            @Override
                            public int obterAlturaTela() {
                                return ALTURA_TELA;
                            }

                            @Override
                            public Rectangle obterAreaDiagrama() {
                                return new Rectangle(areaDiagrama);
                            }
                        });
        Rectangle ponto = painel.grafico.obterAreaVisualPontoControle();
        int centroX = ponto.x + ponto.width / 2;
        int centroY = ponto.y + ponto.height / 2;
        exigir(adaptador.identificarNatureza(centroX, centroY)
                        == AlvoInteracaoPaineisEixosRelacoes.NaturezaInteracao.VALOR_SEMANTICO,
                "O adaptador deveria traduzir o ponto como valor semantico.");

        HandlerInteracaoPaineisEixosRelacoes handler =
                new HandlerInteracaoPaineisEixosRelacoes();
        HandlerInteracaoPaineisEixosRelacoes.ResultadoPressionamento resultado =
                handler.iniciar(adaptador, centroX, centroY, true);
        exigir(resultado.foiConsumido()
                        && resultado.getModoManipulacao()
                        == AlvoInteracaoPaineisEixosRelacoes.ModoManipulacao.PONTO_CONTROLE,
                "O adaptador deveria iniciar o ponto de controle do painel correto.");
        Rectangle areaPontoAtual =
                painel.grafico.obterAreaVisualPontoControle();
        exigir(!areaPontoAtual.isEmpty()
                        && adaptador.obterAreaVisualPontoControle()
                                .equals(areaPontoAtual),
                "A area visual deve vir da representacao, sem coordenada copiada.");

        Rectangle areaPainel = painel.grafico.obterAreaVisualPainel();
        exigir(handler.mover(areaPainel.x + 12, centroY),
                "O handler deveria encaminhar o movimento pelo adaptador.");
        exigir(paineis.encontrarComAlteracaoPorInteracao() == painel,
                "O painel manipulado deveria manter sua alteracao pendente.");
        exigir(handler.concluir() && !paineis.estaArrastando(),
                "Concluir deveria finalizar a manipulacao na representacao.");
    }

    private static final class AlvoFalso
            implements AlvoInteracaoPaineisEixosRelacoes {
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
