import gerard.campoaditivo.diagrama.elementos.ItemTextoArrastavel;
import gerard.campoaditivo.diagrama.elementos.MarcadorTexto;
import gerard.interacao.arraste.SessaoArrasteTextoParaDiagrama;

public class TesteProxyArrasteTextoSemantico {
    public static void main(String[] args) {
        MarcadorTexto marcador = new MarcadorTexto(
                120, 80, 26, 24, "+6", false, "papel.valorRelativo");
        SessaoArrasteTextoParaDiagrama sessao =
                new SessaoArrasteTextoParaDiagrama();

        ItemTextoArrastavel proxy = sessao.iniciarPorMarcador(marcador);
        exigir(proxy != null, "O proxy deveria ser criado.");
        exigir(proxy.x == 120 && proxy.y == 80,
                "O proxy deveria iniciar sobre o marcador.");
        exigir(marcador.x == 120 && marcador.y == 80,
                "O marcador textual não pode ser movido.");
        exigir(sessao.ehProxyAtivo(proxy),
                "O item criado deveria ser reconhecido como proxy ativo.");
        exigir(sessao.devePersistirAoSoltar(proxy, true),
                "Uma soltura válida deve promover o proxy ao diagrama.");
        exigir(!sessao.deveDescartarAoSoltar(proxy, true),
                "Uma soltura válida não deve descartar o proxy.");

        sessao.confirmarPersistencia(proxy);
        exigir(!sessao.ehProxyAtivo(proxy),
                "Após a promoção, a sessão transitória deve ser limpa.");

        ItemTextoArrastavel segundo = sessao.iniciarPorMarcador(marcador);
        exigir(sessao.deveDescartarAoSoltar(segundo, false),
                "Uma soltura fora do diagrama deve descartar o proxy.");
        sessao.descartarProxy(segundo);
        exigir(!sessao.ehProxyAtivo(segundo),
                "Após o descarte, não deve restar proxy ativo.");

        System.out.println("TesteProxyArrasteTextoSemantico: OK");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
