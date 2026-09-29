import gerard.campoaditivo.curadoria.sinal.AvaliacaoEscolhaOperacaoRelacao;
import gerard.campoaditivo.curadoria.sinal.OpcaoOperacaoCuradoria;
import gerard.dominio.campoaditivo.situacao.ResultadoEscolhaOperacaoModelagem;
import gerard.aplicacao.interacao.CasoDeUsoSelecaoOperacoesRelacoes;
import gerard.aplicacao.interacao.PortaEncaminhamentoAcaoInstrumental;
import gerard.aplicacao.interacao.PortaFeedbackEscolhaOperacaoRelacao;
import gerard.aplicacao.interacao.PortaLimpezaFocoAposEscolhaOperacao;
import gerard.aplicacao.interacao.PortaPersistenciaAcaoInstrumental;
import gerard.aplicacao.interacao.PortaReavaliacaoConclusaoAposEscolhaOperacao;
import gerard.dominio.atividade.RegistroFactualAcaoInstrumental;
import gerard.interacao.selecao.AlvoSeletoresOperacaoRelacoes;
import gerard.interacao.selecao.HandlerInteracaoSeletoresOperacaoRelacoes;

public final class TesteHandlerInteracaoSeletoresOperacaoRelacoes {

    public static void main(String[] args) {
        testarPrimeiraEscolhaTemPrioridade();
        testarSegundaEscolhaBloqueadaAtePrimeiraCorreta();
        testarSegundaEscolhaDepoisDaPrimeiraCorreta();
        testarRegistroFactualProduzidoPeloProprietario();
        testarCasoDeUsoSomenteSequenciaPortas();
        System.out.println("Teste aprovado: seletores preservam prioridade e ordem pedagógica.");
    }

    private static void testarPrimeiraEscolhaTemPrioridade() {
        AlvoFalso alvo = new AlvoFalso();
        alvo.resultadoPrimeiro = resultado(
                AvaliacaoEscolhaOperacaoRelacao.TipoOperacaoSeletor.ENTRE_TRANSFORMACOES,
                false);
        alvo.primeiraCorreta = true;

        ResultadoEscolhaOperacaoModelagem resultado = novoHandler().processar(alvo, 10, 20);

        exigir(resultado == alvo.resultadoPrimeiro,
                "A primeira escolha consumida deve ter prioridade.");
        exigir(alvo.chamadasPrimeiro == 1 && alvo.chamadasSegundo == 0,
                "O segundo seletor não pode receber o mesmo clique.");
    }

    private static void testarSegundaEscolhaBloqueadaAtePrimeiraCorreta() {
        AlvoFalso alvo = new AlvoFalso();
        alvo.resultadoSegundo = resultado(
                AvaliacaoEscolhaOperacaoRelacao.TipoOperacaoSeletor
                        .ENTRE_ESTADO_E_TRANSFORMACAO,
                true);

        ResultadoEscolhaOperacaoModelagem resultado = novoHandler().processar(alvo, 30, 40);

        exigir(resultado == null && alvo.chamadasSegundo == 0,
                "A segunda escolha deve permanecer indisponível antes da primeira correta.");
    }

    private static void testarSegundaEscolhaDepoisDaPrimeiraCorreta() {
        AlvoFalso alvo = new AlvoFalso();
        alvo.primeiraCorreta = true;
        alvo.resultadoSegundo = resultado(
                AvaliacaoEscolhaOperacaoRelacao.TipoOperacaoSeletor
                        .ENTRE_ESTADO_E_TRANSFORMACAO,
                true);

        ResultadoEscolhaOperacaoModelagem resultado = novoHandler().processar(alvo, 50, 60);

        exigir(resultado == alvo.resultadoSegundo && alvo.chamadasSegundo == 1,
                "A segunda escolha deve ser processada depois da primeira correta.");
    }

    private static void testarRegistroFactualProduzidoPeloProprietario() {
        ResultadoEscolhaOperacaoModelagem resultado = resultado(
                AvaliacaoEscolhaOperacaoRelacao.TipoOperacaoSeletor
                        .ENTRE_ESTADO_E_TRANSFORMACAO,
                false);
        exigir(resultado.getActionId() != null
                        && resultado.getActionId().length() > 0,
                "O proprietário deve identificar cada ação constituída.");
        exigir("E".equals(resultado.getResultado().getCodigoCe())
                        && "SOMA".equals(resultado.getValorPropostoFactual())
                        && "SUBTRACAO".equals(resultado.getValorEsperadoFactual()),
                "C/E e valores factuais devem nascer no proprietário semântico.");
        exigir("OPERACAO_ESTADO_TRANSFORMACAO_ALUNO".equals(
                        resultado.getContexto().getOrigemEvento()),
                "A infraestrutura deve receber o contexto pronto, sem inferi-lo.");
        exigir(resultado.getFeedback()
                        == ResultadoEscolhaOperacaoModelagem.Feedback.SOM_ERRO,
                "A decisão abstrata de feedback deve nascer com o resultado factual.");
    }

    private static void testarCasoDeUsoSomenteSequenciaPortas() {
        AlvoFalso alvo = new AlvoFalso();
        alvo.resultadoPrimeiro = resultado(
                AvaliacaoEscolhaOperacaoRelacao.TipoOperacaoSeletor
                        .ENTRE_TRANSFORMACOES,
                true);
        PortasFalsas portas = new PortasFalsas();
        CasoDeUsoSelecaoOperacoesRelacoes casoDeUso =
                new CasoDeUsoSelecaoOperacoesRelacoes(
                        novoHandler(), alvo, portas, portas, portas, portas, portas);

        exigir(casoDeUso.processar(70, 80),
                "O caso de uso deveria consumir a escolha constituída.");
        exigir("feedback>persistencia>modelador>foco>conclusao".equals(
                        portas.ordem.toString()),
                "O caso de uso deve somente preservar a ordem das portas.");
        exigir(portas.registro == alvo.resultadoPrimeiro,
                "A persistência deve receber o mesmo fato produzido pelo proprietário.");
        exigir(portas.registroModelador == alvo.resultadoPrimeiro,
                "O Modelador deve receber exatamente o mesmo fato produzido pelo proprietário.");
    }

    private static HandlerInteracaoSeletoresOperacaoRelacoes novoHandler() {
        return new HandlerInteracaoSeletoresOperacaoRelacoes();
    }

    private static ResultadoEscolhaOperacaoModelagem resultado(
            AvaliacaoEscolhaOperacaoRelacao.TipoOperacaoSeletor tipo,
            boolean correta) {
        return AvaliacaoEscolhaOperacaoRelacao.registrarEscolha(
                gerard.campoaditivo.modelo.TipoSituacaoAditiva
                        .COMPOSICAO_TRANSFORMACOES,
                tipo,
                OpcaoOperacaoCuradoria.SOMA,
                correta ? OpcaoOperacaoCuradoria.SOMA
                        : OpcaoOperacaoCuradoria.SUBTRACAO);
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }

    private static final class AlvoFalso
            implements AlvoSeletoresOperacaoRelacoes {
        private ResultadoEscolhaOperacaoModelagem resultadoPrimeiro;
        private ResultadoEscolhaOperacaoModelagem resultadoSegundo;
        private boolean primeiraCorreta;
        private int chamadasPrimeiro;
        private int chamadasSegundo;

        @Override
        public ResultadoEscolhaOperacaoModelagem processarPrimeiraEscolha(
                int posicaoX, int posicaoY) {
            chamadasPrimeiro++;
            return resultadoPrimeiro;
        }

        @Override
        public boolean primeiraEscolhaEstaCorreta() {
            return primeiraCorreta;
        }

        @Override
        public ResultadoEscolhaOperacaoModelagem processarSegundaEscolha(
                int posicaoX, int posicaoY) {
            chamadasSegundo++;
            return resultadoSegundo;
        }
    }

    private static final class PortasFalsas
            implements PortaFeedbackEscolhaOperacaoRelacao,
            PortaPersistenciaAcaoInstrumental,
            PortaEncaminhamentoAcaoInstrumental,
            PortaLimpezaFocoAposEscolhaOperacao,
            PortaReavaliacaoConclusaoAposEscolhaOperacao {
        private final StringBuilder ordem = new StringBuilder();
        private RegistroFactualAcaoInstrumental registro;
        private RegistroFactualAcaoInstrumental registroModelador;

        @Override
        public void materializar(
                ResultadoEscolhaOperacaoModelagem.Feedback feedback) {
            acrescentar("feedback");
        }

        @Override
        public void persistir(RegistroFactualAcaoInstrumental registro) {
            this.registro = registro;
            acrescentar("persistencia");
        }

        @Override
        public void encaminhar(RegistroFactualAcaoInstrumental registro) {
            exigir(registroModelador == null,
                    "A ação deve ser encaminhada uma única vez ao Modelador.");
            registroModelador = registro;
            acrescentar("modelador");
        }

        @Override
        public void limparFoco() {
            acrescentar("foco");
        }

        @Override
        public void reavaliarConclusao() {
            acrescentar("conclusao");
        }

        private void acrescentar(String etapa) {
            if (ordem.length() > 0) {
                ordem.append('>');
            }
            ordem.append(etapa);
        }
    }
}
