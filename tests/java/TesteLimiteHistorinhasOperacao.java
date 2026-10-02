import gerard.campoaditivo.curadoria.sinal.AvaliacaoEscolhaOperacaoRelacao;
import gerard.campoaditivo.curadoria.sinal.AvaliacaoEscolhaOperacaoRelacao.TipoOperacaoSeletor;
import gerard.campoaditivo.curadoria.sinal.OpcaoOperacaoCuradoria;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.dominio.campoaditivo.TentativaModelagemAditiva;
import gerard.dominio.campoaditivo.OrigemAcao;
import gerard.dominio.campoaditivo.TipoRestauracaoModelagem;
import gerard.dominio.campoaditivo.situacao.ResultadoEscolhaOperacaoModelagem;

public final class TesteLimiteHistorinhasOperacao {
    public static void main(String[] args) {
        TentativaModelagemAditiva tentativa = new TentativaModelagemAditiva("historinhas");
        ResultadoEscolhaOperacaoModelagem primeira = escolha(false, false);
        tentativa.constituir(primeira);
        tentativa.constituir(primeira);
        exigir(!tentativa.estaNoLimiteAjudaVisual(), "mesmo action_id conta uma vez");
        tentativa.constituir(escolha(false, true));
        tentativa.constituir(escolha(false, false));
        exigir(!tentativa.estaNoLimiteAjudaVisual(), "seletores distintos não somam rejeições");
        tentativa.constituir(escolha(true, false));
        tentativa.constituir(escolha(false, false));
        tentativa.constituir(escolha(false, false));
        exigir(!tentativa.estaNoLimiteAjudaVisual(), "acerto encerra sequência antes do limite");
        tentativa.constituir(escolha(false, false));
        exigir(tentativa.estaNoLimiteAjudaVisual(), "terceira rejeição do mesmo seletor libera ajuda");
        tentativa.constituir(escolha(true, false));
        tentativa.encerrarPorConclusao();
        exigir(tentativa.estaNoLimiteAjudaVisual(), "conclusão conserva ajuda já liberada");
        tentativa.restaurar(TipoRestauracaoModelagem.DIAGRAMA_COMPLETO, OrigemAcao.ORIGEM_USUARIO);
        exigir(!tentativa.estaNoLimiteAjudaVisual(), "restauração encerra ajuda");
        // Regra de 2026-10-02: restaurar reabre a primeira modelagem, que volta a contar rejeições.
        tentativa.constituir(escolha(false, false));
        tentativa.constituir(escolha(false, false));
        tentativa.constituir(escolha(false, false));
        exigir(tentativa.estaNoLimiteAjudaVisual(), "após restaurar, a primeira modelagem reaberta conta rejeições");
        TentativaModelagemAditiva concluida = new TentativaModelagemAditiva("exploracao");
        concluida.encerrarPorConclusao();
        concluida.constituir(escolha(false, false));
        concluida.constituir(escolha(false, false));
        concluida.constituir(escolha(false, false));
        exigir(!concluida.estaNoLimiteAjudaVisual(), "exploração (após a conclusão) não conta rejeições");
        System.out.println("APROVADO: limite, deduplicação, seletores, acerto, restauração e exploração.");
    }
    private static ResultadoEscolhaOperacaoModelagem escolha(boolean correta, boolean segunda) {
        return AvaliacaoEscolhaOperacaoRelacao.registrarEscolha(
                TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES,
                segunda ? TipoOperacaoSeletor.ENTRE_ESTADO_E_TRANSFORMACAO : TipoOperacaoSeletor.ENTRE_TRANSFORMACOES,
                correta ? OpcaoOperacaoCuradoria.SOMA : OpcaoOperacaoCuradoria.SUBTRACAO,
                OpcaoOperacaoCuradoria.SOMA);
    }
    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }
}
