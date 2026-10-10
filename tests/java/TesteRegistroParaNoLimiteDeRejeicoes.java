package gerard.dominio.campoaditivo;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.dominio.atividade.ContextoAcaoInstrumental;
import gerard.dominio.atividade.RegistroAcaoInstrumental;
import gerard.dominio.atividade.ResultadoAvaliacaoAcaoInstrumental;
import gerard.dominio.atividade.TarefaInteracao;

import java.util.Collections;

/**
 * Regra da usuária (2026-10-10): ao atingir o limite de rejeições consecutivas da incógnita o registro factual
 * para. A ação que atinge o limite (o terceiro erro) ainda é constituída; as seguintes não. Restaurar reabre.
 * Confere a decisão no agregado que a possui, pela mesma porta que o logger e o publicador de gestos consultam.
 */
public class TesteRegistroParaNoLimiteDeRejeicoes {
    static int verificacoes;

    static RegistroAcaoInstrumental erro(String id, int rejeicoes, boolean limite) {
        DiagnosticoErroPapel diag = new DiagnosticoErroPapel(TipoErroPapel.VALOR_FORA_DO_DOMINIO, "k", "k", "k");
        return new RegistroAcaoInstrumental(
                new IdentidadeAcaoInstrumentalPapel(id, "papel.todo", OrigemAcao.ORIGEM_USUARIO),
                TarefaInteracao.TEXTO, TipoSituacaoAditiva.COMPOSICAO_MEDIDAS, "papel.todo", "papel.todo",
                ResultadoAvaliacaoAcaoInstrumental.ERRADA, diag, null, null, "regra.teste",
                new ContextoAcaoInstrumental("t", "o", "a", "f", "papel.todo", "EDICAO_ITEM", "valor=1", "m",
                        Collections.<String>emptyList()),
                new ResultadoRegistroTentativaPapel(true, false, false, limite, rejeicoes, id, "SEQ"));
    }

    public static void main(String[] args) {
        TentativaModelagemAditiva t = new TentativaModelagemAditiva("T1");
        TentativaCorrente corrente = new TentativaCorrente();
        corrente.definir(t);
        exigir(corrente.admiteRegistroFactual(), "no início o registro é admitido");
        exigir(t.constituir(erro("A1", 1, false)).isPresent(), "1º erro é constituído");
        exigir(t.constituir(erro("A2", 2, false)).isPresent(), "2º erro é constituído");
        exigir(corrente.admiteRegistroFactual(), "antes do limite o registro segue admitido");
        exigir(t.constituir(erro("A3", 3, true)).isPresent(), "o 3º erro (que atinge o limite) é constituído");
        exigir(!corrente.admiteRegistroFactual() && !t.admiteRegistroFactual(),
                "após o limite a porta do logger e dos gestos fecha");
        exigir(!t.constituir(erro("A4", 3, false)).isPresent(), "depois do limite nenhuma ação é constituída");
        exigir(!t.admiteExploracao(), "parar o registro não é exploração (não houve conclusão)");
        t.restaurar(TipoRestauracaoModelagem.values()[0], OrigemAcao.ORIGEM_USUARIO);
        exigir(t.admiteRegistroFactual(), "restaurar reabre o registro");
        exigir(new TentativaModelagemAditiva("T2").admiteRegistroFactual(), "nova tentativa (sortear) nasce admitindo registro");
        System.out.println("OK " + verificacoes + " verificações");
    }

    static void exigir(boolean ok, String msg) {
        verificacoes++;
        if (!ok) { System.out.println("[FALHA] " + msg); System.exit(1); }
    }
}
