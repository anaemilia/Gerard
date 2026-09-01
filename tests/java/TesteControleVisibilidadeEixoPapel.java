import gerard.interacao.eixo.ControleVisibilidadeEixoPapel;

public final class TesteControleVisibilidadeEixoPapel {
    public static void main(String[] args) {
        ControleVisibilidadeEixoPapel controle = new ControleVisibilidadeEixoPapel();
        exigir(controle.podeRevelar() && !controle.estaRevelado(), "começa fechado");
        exigir(controle.revelar(), "revela uma vez");
        exigir(controle.estaRevelado() && !controle.revelar(), "revelar é idempotente");
        exigir(controle.ocultar(), "oculta uma vez");
        exigir(controle.podeRevelar() && !controle.ocultar(), "ocultar é idempotente");
        System.out.println("APROVADO: visibilidade do eixo é um protocolo portátil.");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }
}
