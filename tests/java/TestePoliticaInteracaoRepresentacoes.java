import gerard.campoaditivo.sincronizacao.representacoes.EstadoPrimeiroPosicionamento;
import gerard.campoaditivo.sincronizacao.representacoes.PoliticaInteracaoRepresentacoes;

public class TestePoliticaInteracaoRepresentacoes {
    private static final class Estado implements EstadoPrimeiroPosicionamento {
        boolean preenchido;
        public boolean possuiConteudoSemanticoNoVergnaud() {
            return preenchido;
        }
    }

    public static void main(String[] args) {
        Estado estado = new Estado();
        PoliticaInteracaoRepresentacoes politica =
                new PoliticaInteracaoRepresentacoes(
                        estado, "ui.tooltip.representation.positionFirst");
        exigir(!politica.estaLiberada(),
                "As representações devem iniciar bloqueadas.");
        estado.preenchido = true;
        exigir(politica.estaLiberada(),
                "As representações devem ser liberadas após o primeiro preenchimento semântico.");
        exigir("ui.tooltip.representation.positionFirst".equals(
                politica.obterChaveMensagemBloqueio()),
                "A chave da mensagem deve ser preservada.");
        System.out.println("TestePoliticaInteracaoRepresentacoes: OK");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }
}
