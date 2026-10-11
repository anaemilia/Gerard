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

    static gerard.dominio.campoaditivo.situacao.ResultadoEscolhaOperacaoModelagem escolha(boolean correta) {
        return gerard.campoaditivo.curadoria.sinal.AvaliacaoEscolhaOperacaoRelacao.registrarEscolha(
                TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES,
                gerard.campoaditivo.curadoria.sinal.AvaliacaoEscolhaOperacaoRelacao.TipoOperacaoSeletor.ENTRE_TRANSFORMACOES,
                correta ? gerard.campoaditivo.curadoria.sinal.OpcaoOperacaoCuradoria.SOMA
                        : gerard.campoaditivo.curadoria.sinal.OpcaoOperacaoCuradoria.SUBTRACAO,
                gerard.campoaditivo.curadoria.sinal.OpcaoOperacaoCuradoria.SOMA);
    }

    public static void main(String[] args) {
        TentativaModelagemAditiva t = new TentativaModelagemAditiva("T1");
        TentativaCorrente corrente = new TentativaCorrente();
        corrente.definir(t);
        exigir(corrente.admiteRegistroFactual(), "no início o registro é admitido");
        exigir(t.constituir(erro("A1", 1, false)).isPresent(), "1º erro é constituído");
        exigir(t.constituir(erro("A2", 2, false)).isPresent(), "2º erro é constituído");
        exigir(corrente.admiteRegistroFactual(), "antes do limite o registro segue admitido");
        RegistroAcaoInstrumental terceiro = erro("A3", 3, true);
        exigir(t.constituir(terceiro).isPresent() && t.constituir(terceiro).isPresent(),
                "o 3º erro (que atinge o limite) é constituído, inclusive na 2ª porta que recebe a mesma ação");
        exigir(!corrente.admiteRegistroFactual() && !t.admiteRegistroFactual(),
                "após o limite a porta do logger e dos gestos fecha");
        exigir(corrente.admiteRegistroFactual("A3") && !corrente.admiteRegistroFactual("A4")
                        && !corrente.admiteRegistroFactual(null),
                "o logger (porta por action_id) ainda admite a ação do limite e recusa as demais");
        exigir(!t.constituir(erro("A4", 3, false)).isPresent(), "depois do limite nenhuma ação é constituída");
        exigir(!t.admiteExploracao(), "parar o registro não é exploração (não houve conclusão)");
        t.restaurar(TipoRestauracaoModelagem.values()[0], OrigemAcao.ORIGEM_USUARIO);
        exigir(t.admiteRegistroFactual(), "restaurar reabre o registro");
        exigir(new TentativaModelagemAditiva("T2").admiteRegistroFactual(), "nova tentativa (sortear) nasce admitindo registro");

        // Escolha de operação errada três vezes: inicia historinha/material concreto; nada é registrado nesse intervalo.
        TentativaModelagemAditiva o = new TentativaModelagemAditiva("T3");
        exigir(o.constituir(escolha(false)).isPresent() && o.constituir(escolha(false)).isPresent(),
                "1ª e 2ª escolhas erradas são constituídas");
        exigir(o.admiteRegistroFactual() && !o.estaNoLimiteAjudaVisual(), "antes do limite segue admitido");
        gerard.dominio.campoaditivo.situacao.ResultadoEscolhaOperacaoModelagem terceira = escolha(false);
        exigir(o.constituir(terceira).isPresent() && o.constituir(terceira).isPresent(),
                "a 3ª escolha errada (que atinge o limite) é constituída, inclusive na 2ª porta (persistir/encaminhar)");
        exigir(o.estaNoLimiteAjudaVisual() && !o.admiteRegistroFactual(),
                "no limite entra a ajuda visual e a porta do registro fecha");
        exigir(o.admiteRegistroFactual(terceira.getActionId()) && !o.admiteRegistroFactual("outra"),
                "a porta por action_id admite a 3ª escolha e recusa as seguintes");
        exigir(!o.constituir(escolha(false)).isPresent() && !o.constituir(escolha(true)).isPresent(),
                "durante a historinha/material concreto nenhuma ação é constituída");
        o.restaurar(TipoRestauracaoModelagem.DIAGRAMA_COMPLETO, OrigemAcao.ORIGEM_USUARIO);
        exigir(o.admiteRegistroFactual() && o.constituir(escolha(false)).isPresent(), "restaurar reabre o registro");
        System.out.println("OK " + verificacoes + " verificações");
    }

    static void exigir(boolean ok, String msg) {
        verificacoes++;
        if (!ok) { System.out.println("[FALHA] " + msg); System.exit(1); }
    }
}
