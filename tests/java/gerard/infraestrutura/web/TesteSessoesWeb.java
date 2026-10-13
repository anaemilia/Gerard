package gerard.infraestrutura.web;

import gerard.aplicacao.portabilidade.ServicoSorteioAtividade;

/** O registro de sessões: expira as ociosas e respeita o limite saindo a mais ociosa. */
public class TesteSessoesWeb {
    static int falhas;

    static void conferir(boolean ok, String msg) {
        if (!ok) {
            falhas++;
        }
        System.out.println((ok ? "[OK] " : "[FALHA] ") + msg);
    }

    static SessaoWeb nova() {
        return new SessaoWeb(SessoesWeb.novoId(), new ServicoSorteioAtividade(), null);
    }

    public static void main(String[] args) throws Exception {
        SessoesWeb limitado = new SessoesWeb(TesteSessoesWeb::nova, 60_000L, 2);
        SessaoWeb primeira = limitado.criar();
        Thread.sleep(5);
        SessaoWeb segunda = limitado.criar();
        Thread.sleep(5);
        segunda.registrarAcesso();
        limitado.criar();
        conferir(limitado.quantidade() == 2, "o total respeita o limite (" + limitado.quantidade() + ")");
        conferir(!limitado.contem(primeira.id()) && limitado.contem(segunda.id()),
                "ao estourar o limite sai a sessão mais ociosa");

        SessoesWeb curto = new SessoesWeb(TesteSessoesWeb::nova, 20L, 10);
        SessaoWeb efemera = curto.criar();
        Thread.sleep(60);
        curto.expirar();
        conferir(!curto.contem(efemera.id()) && curto.quantidade() == 0, "sessão ociosa além do tempo expira");

        SessoesWeb vivo = new SessoesWeb(TesteSessoesWeb::nova, 60_000L, 10);
        SessaoWeb ativa = vivo.criar();
        vivo.expirar();
        conferir(vivo.contem(ativa.id()), "sessão recente não expira");

        System.out.println(falhas == 0 ? "APROVADO: registro de sessões" : "REPROVADO: " + falhas + " falha(s)");
        System.exit(falhas == 0 ? 0 : 1);
    }
}
