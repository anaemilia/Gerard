package gerard.dominio.campoaditivo;

import gerard.aplicacao.FluxoConfirmacaoValorIncognita;
import gerard.aplicacao.ProtocoloValorIncognita;
import gerard.aplicacao.adaptacao.ConfirmacaoMaterializacaoAjuda;
import gerard.aplicacao.adaptacao.ResultadoExecucaoAjudaIncognita;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.dominio.atividade.ContextoAcaoInstrumental;
import gerard.dominio.atividade.RegistroAcaoInstrumental;
import gerard.dominio.atividade.ResultadoAvaliacaoAcaoInstrumental;
import gerard.dominio.atividade.TarefaInteracao;
import gerard.dominio.campoaditivo.evento.PublicadorEventoDominio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A sequência da confirmação do valor da incógnita (avaliar, persistir, feedback de erro, ajuda, limite,
 * pergunta) vive numa classe só, sem Swing. Confere a ORDEM e os desvios em todos os desfechos, com um alvo de
 * mentira que só anota os passos pedidos. Fica no pacote do domínio para construir os resultados da tentativa.
 */
public class TesteFluxoConfirmacaoValorIncognita {
    static int verificacoes;
    static final IncognitaQuantitativa INCOGNITA = new IncognitaQuantitativa(
            "papel.todo", TipoSituacaoAditiva.COMPOSICAO_MEDIDAS, PapelQuantitativo.todo(PublicadorEventoDominio.NENHUM));

    static final class AlvoRegistrador implements FluxoConfirmacaoValorIncognita.Alvo {
        final List<String> passos = new ArrayList<String>();
        boolean incognitaPreenchida = true;
        IncognitaQuantitativa incognita = INCOGNITA;
        RegistroAcaoInstrumental registro;
        ResultadoExecucaoAjudaIncognita ajuda;
        ProtocoloValorIncognita protocoloAvaliado;
        IdentidadeAcaoInstrumentalPapel identidadeUsada;

        public boolean ehIncognitaPreenchidaPeloProtocolo() { passos.add("guarda"); return incognitaPreenchida; }
        public String prepararPapelDaIncognita() { passos.add("prepararPapel"); return "papel.todo"; }
        public IncognitaQuantitativa incognitaAtual() { passos.add("incognita"); return incognita; }
        public IdentidadeAcaoInstrumentalPapel iniciarAcaoDoUsuario() { passos.add("novaIdentidade"); return identidade("NOVA"); }
        public void registrarPapeisDadoModificadosSeHouver() { passos.add("papeisDado"); }
        public RegistroAcaoInstrumental avaliar(ProtocoloValorIncognita p, IncognitaQuantitativa i,
                IdentidadeAcaoInstrumentalPapel id, String papel) {
            passos.add("avaliar"); protocoloAvaliado = p; identidadeUsada = id; return registro;
        }
        public void persistir(RegistroAcaoInstrumental r) { passos.add("persistir"); }
        public void aplicarFeedbackVisualErro() { passos.add("feedbackErro"); }
        public void limparFeedbackVisualErro() { passos.add("limparFeedbackErro"); }
        public ResultadoExecucaoAjudaIncognita executarAjuda(RegistroAcaoInstrumental r, ResultadoRegistroTentativaPapel t) {
            passos.add("ajuda"); return ajuda;
        }
        public void processarLimiteTentativasAtingido(ResultadoRegistroTentativaPapel t) { passos.add("limite"); }
        public void perguntarConfirmacao(ProtocoloValorIncognita p, RegistroAcaoInstrumental r, IncognitaQuantitativa i, String papel) {
            passos.add("pergunta");
        }
    }

    static IdentidadeAcaoInstrumentalPapel identidade(String id) {
        return new IdentidadeAcaoInstrumentalPapel(id, "papel.todo", OrigemAcao.ORIGEM_USUARIO);
    }

    static ResultadoRegistroTentativaPapel resultado(boolean limite, int rejeicoes) {
        return new ResultadoRegistroTentativaPapel(true, false, false, limite, rejeicoes, "ACAO", "SEQ");
    }

    static RegistroAcaoInstrumental registro(ResultadoAvaliacaoAcaoInstrumental resultado, ResultadoRegistroTentativaPapel rt) {
        DiagnosticoErroPapel diag = resultado == ResultadoAvaliacaoAcaoInstrumental.ERRADA
                ? new DiagnosticoErroPapel(TipoErroPapel.VALOR_FORA_DO_DOMINIO, "k", "k", "k") : null;
        return new RegistroAcaoInstrumental(identidade("ACAO"), TarefaInteracao.TEXTO, TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                "papel.todo", "papel.todo", resultado, diag, null, null, "regra.teste",
                new ContextoAcaoInstrumental("t", "o", "a", "f", "papel.todo", "EDICAO_ITEM", "valor=1", "m",
                        Collections.<String>emptyList()),
                rt);
    }

    static ResultadoExecucaoAjudaIncognita ajudaMaterializada() {
        return ResultadoExecucaoAjudaIncognita.materializada(null, Collections.singletonList(
                new ConfirmacaoMaterializacaoAjuda("AG_EME", ModalidadeEntregaScaffolding.VISUAL, "criterio", "d")));
    }

    static final String INICIO = "guarda,prepararPapel,incognita,papeisDado,avaliar,persistir";

    public static void main(String[] a) {
        // 1) fora do alvo
        AlvoRegistrador fora = new AlvoRegistrador();
        fora.incognitaPreenchida = false;
        exigir(FluxoConfirmacaoValorIncognita.executar(fora, ProtocoloValorIncognita.TEXTO, null)
                && "guarda".equals(join(fora.passos)), "fora do alvo: nada a conferir (" + join(fora.passos) + ")");

        // 2) sem incógnita semântica
        AlvoRegistrador sem = new AlvoRegistrador();
        sem.incognita = null;
        exigir(FluxoConfirmacaoValorIncognita.executar(sem, ProtocoloValorIncognita.QUANTIFICAR, null)
                && "guarda,prepararPapel,incognita".equals(join(sem.passos)), "sem incógnita: libera sem avaliar");

        // 3) sem critério aplicável: libera e limpa o feedback de erro
        AlvoRegistrador semCriterio = new AlvoRegistrador();
        semCriterio.registro = registro(ResultadoAvaliacaoAcaoInstrumental.NAO_APLICAVEL, null);
        exigir(FluxoConfirmacaoValorIncognita.executar(semCriterio, ProtocoloValorIncognita.TEXTO, null)
                && (INICIO + ",novaIdentidade").equals(INICIO + ",novaIdentidade") // ordem checada abaixo
                && join(semCriterio.passos).equals("guarda,prepararPapel,incognita,novaIdentidade,papeisDado,avaliar,persistir,limparFeedbackErro"),
                "sem critério: " + join(semCriterio.passos));

        // 4) correto: libera
        AlvoRegistrador certo = new AlvoRegistrador();
        certo.registro = registro(ResultadoAvaliacaoAcaoInstrumental.CORRETA, null);
        exigir(FluxoConfirmacaoValorIncognita.executar(certo, ProtocoloValorIncognita.QUANTIFICAR, identidade("DADA"))
                && join(certo.passos).equals("guarda,prepararPapel,incognita,papeisDado,avaliar,persistir,limparFeedbackErro"),
                "correto (identidade já dada, sem criar outra): " + join(certo.passos));
        exigir("DADA".equals(certo.identidadeUsada.getActionId()), "a identidade fornecida é a usada na avaliação");
        exigir(certo.protocoloAvaliado == ProtocoloValorIncognita.QUANTIFICAR, "o protocolo chega à avaliação");

        // 5) errado sem resultado da tentativa: feedback de erro e pergunta (sem ajuda nem limite)
        AlvoRegistrador errado = new AlvoRegistrador();
        errado.registro = registro(ResultadoAvaliacaoAcaoInstrumental.ERRADA, null);
        exigir(!FluxoConfirmacaoValorIncognita.executar(errado, ProtocoloValorIncognita.TEXTO, identidade("X"))
                && join(errado.passos).equals("guarda,prepararPapel,incognita,papeisDado,avaliar,persistir,feedbackErro,pergunta"),
                "errado: " + join(errado.passos));

        // 6) errado com tentativa: a ajuda do repertório roda; sem materializar e sem limite, pergunta
        AlvoRegistrador comAjuda = new AlvoRegistrador();
        comAjuda.registro = registro(ResultadoAvaliacaoAcaoInstrumental.ERRADA, resultado(false, 1));
        comAjuda.ajuda = ResultadoExecucaoAjudaIncognita.contextoIndisponivel();
        exigir(!FluxoConfirmacaoValorIncognita.executar(comAjuda, ProtocoloValorIncognita.TEXTO, identidade("X"))
                && join(comAjuda.passos).equals("guarda,prepararPapel,incognita,papeisDado,avaliar,persistir,feedbackErro,ajuda,pergunta"),
                "errado com tentativa (ajuda não materializada): " + join(comAjuda.passos));

        // 7) ajuda materializada: não pergunta e não processa limite
        AlvoRegistrador materializada = new AlvoRegistrador();
        materializada.registro = registro(ResultadoAvaliacaoAcaoInstrumental.ERRADA, resultado(true, 3));
        materializada.ajuda = ajudaMaterializada();
        exigir(!FluxoConfirmacaoValorIncognita.executar(materializada, ProtocoloValorIncognita.TEXTO, identidade("X"))
                && join(materializada.passos).equals("guarda,prepararPapel,incognita,papeisDado,avaliar,persistir,feedbackErro,ajuda"),
                "ajuda materializada encerra sem pergunta nem limite: " + join(materializada.passos));

        // 8) 3ª rejeição (limite atingido agora) sem ajuda materializada: reposiciona, sem diálogo
        AlvoRegistrador limite = new AlvoRegistrador();
        limite.registro = registro(ResultadoAvaliacaoAcaoInstrumental.ERRADA, resultado(true, 3));
        limite.ajuda = ResultadoExecucaoAjudaIncognita.contextoIndisponivel();
        exigir(!FluxoConfirmacaoValorIncognita.executar(limite, ProtocoloValorIncognita.QUANTIFICAR, identidade("X"))
                && join(limite.passos).equals("guarda,prepararPapel,incognita,papeisDado,avaliar,persistir,feedbackErro,ajuda,limite"),
                "limite atingido: processa o limite e não pergunta: " + join(limite.passos));

        // 9) os dois protocolos diferem só no que descrevem e em carregar identificadores no feedback
        exigir(ProtocoloValorIncognita.TEXTO.feedbackCarregaIdentificadores()
                        && !ProtocoloValorIncognita.QUANTIFICAR.feedbackCarregaIdentificadores()
                        && ProtocoloValorIncognita.TEXTO.getTarefaDeInteracao() == TarefaInteracao.TEXTO
                        && ProtocoloValorIncognita.QUANTIFICAR.getTarefaDeInteracao() == TarefaInteracao.QUANTIFICAR,
                "os protocolos TEXTO e QUANTIFICAR preservam suas diferenças");

        System.out.println("APROVADO: a sequência da confirmação do valor da incógnita tem um dono só (" + verificacoes + " verificações).");
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
