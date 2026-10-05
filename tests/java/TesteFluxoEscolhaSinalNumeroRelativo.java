import gerard.aplicacao.FluxoEscolhaSinalNumeroRelativo;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.dominio.atividade.ContextoAcaoInstrumental;
import gerard.dominio.campoaditivo.RegistroAcaoEscolhaSinalPapelQuantitativo;
import gerard.dominio.campoaditivo.TentativaEscolhaSinalPapelQuantitativo;
import gerard.semantica.numero.NumeroInteiro;
import gerard.semantica.numero.OpcaoSinalNumeroInteiro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A sequência da escolha de sinal do número relativo (limpar aviso, proteger quantidades não negativas, aplicar,
 * avaliar, avisar/registrar, sincronizar, concluir, repintar) vive numa classe só, sem Swing. Confere a ORDEM e os
 * desvios nos quatro desfechos: bloqueio por quantidade negativa, sem critério curado, sinal errado e sinal certo.
 */
public class TesteFluxoEscolhaSinalNumeroRelativo {
    static int verificacoes;

    /** Alvo de mentira: só anota, na ordem, o que o fluxo pediu. */
    static final class AlvoRegistrador implements FluxoEscolhaSinalNumeroRelativo.Alvo {
        final List<String> passos = new ArrayList<String>();
        boolean preserva = true;
        RegistroAcaoEscolhaSinalPapelQuantitativo registro;

        public void limparAvisoDivergente() { passos.add("limparAviso"); }
        public int valorRelativoCandidato(String base, String sinal) { passos.add("candidato"); return -3; }
        public boolean preservaQuantidadesNaoNegativas(int v) { passos.add("preserva"); return preserva; }
        public void restaurarPositivoSeguro(String base) { passos.add("restaurarPositivo"); }
        public void informarBloqueioQuantidadeNegativa() { passos.add("bloqueio"); }
        public void aplicarSinal(String base, String sinal) { passos.add("aplicar"); }
        public RegistroAcaoEscolhaSinalPapelQuantitativo avaliarEscolha(String base, String sinal) { passos.add("avaliar"); return registro; }
        public void informarSuspeitaSinalIncorreto(String sinal) { passos.add("suspeita"); }
        public void registrarEscolhaSemCriterio(String base, String sinal) { passos.add("semCriterio"); }
        public void sincronizarRepresentacoes(String sinal) { passos.add("sincronizar"); }
        public void verificarConclusao() { passos.add("concluir"); }
        public void repintar() { passos.add("repintar"); }
    }

    public static void main(String[] a) {
        TentativaEscolhaSinalPapelQuantitativo tentativa = new TentativaEscolhaSinalPapelQuantitativo(
                "SIT-FLUXO", TipoSituacaoAditiva.TRANSFORMACAO_RELACAO, "papel.transformacao", new NumeroInteiro(3));
        ContextoAcaoInstrumental ctx = new ContextoAcaoInstrumental("t", "o", "a", "f", "papel.transformacao.sinal",
                "MENU_SINAL", "d", "m", Collections.<String>emptyList());

        // 1) quantidade negativa: restaura, informa e encerra — nada é aplicado, avaliado nem concluído
        AlvoRegistrador bloqueio = new AlvoRegistrador();
        bloqueio.preserva = false;
        FluxoEscolhaSinalNumeroRelativo.executar(bloqueio, "3", "-");
        exigirSequencia(bloqueio, "limparAviso,candidato,preserva,restaurarPositivo,bloqueio,repintar", "bloqueio por quantidade negativa");

        // 2) sem critério curado: registra como compatibilidade
        AlvoRegistrador semCriterio = new AlvoRegistrador();
        FluxoEscolhaSinalNumeroRelativo.executar(semCriterio, "3", "+");
        exigirSequencia(semCriterio, "limparAviso,candidato,preserva,aplicar,avaliar,semCriterio,sincronizar,concluir,repintar", "sem critério curado");

        // 3) sinal errado: convida a reconferir (não bloqueia: ainda sincroniza e conclui a verificação)
        AlvoRegistrador errado = new AlvoRegistrador();
        errado.registro = tentativa.avaliarEscolha(OpcaoSinalNumeroInteiro.MENOS, ctx);
        exigir(errado.registro.foiErrada(), "premissa: MENOS para +3 é erro");
        FluxoEscolhaSinalNumeroRelativo.executar(errado, "3", "-");
        exigirSequencia(errado, "limparAviso,candidato,preserva,aplicar,avaliar,suspeita,sincronizar,concluir,repintar", "sinal errado");

        // 4) sinal certo: sem aviso e sem registro de compatibilidade
        AlvoRegistrador certo = new AlvoRegistrador();
        certo.registro = tentativa.avaliarEscolha(OpcaoSinalNumeroInteiro.MAIS, ctx);
        exigir(!certo.registro.foiErrada(), "premissa: MAIS para +3 é acerto");
        FluxoEscolhaSinalNumeroRelativo.executar(certo, "3", "+");
        exigirSequencia(certo, "limparAviso,candidato,preserva,aplicar,avaliar,sincronizar,concluir,repintar", "sinal certo");

        System.out.println("APROVADO: a sequência da escolha de sinal tem um dono só (" + verificacoes + " verificações).");
    }

    static void exigirSequencia(AlvoRegistrador alvo, String esperado, String desfecho) {
        String atual = join(alvo.passos);
        exigir(esperado.equals(atual), desfecho + ": " + atual + " (esperado " + esperado + ")");
    }

    static String join(List<String> l) {
        StringBuilder b = new StringBuilder();
        for (String s : l) { if (b.length() > 0) b.append(','); b.append(s); }
        return b.toString();
    }

    static void exigir(boolean ok, String msg) {
        verificacoes++;
        if (!ok) { System.out.println("[FALHA] " + msg); System.exit(1); }
    }
}
