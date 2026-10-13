package gerard.infraestrutura.web;

import gerard.adaptacao.sessao.SessaoAdaptativaUsuario;
import gerard.aplicacao.portabilidade.ServicoSorteioAtividade;
import gerard.pesquisador.log.RegistradorAtividadeWebLogGerard;

/** O estado de um visitante do servidor: a tentativa em curso e o usuário que entrou. Nada aqui é compartilhado. */
final class SessaoWeb {
    private final String id;
    private final ServicoSorteioAtividade sorteios;
    private final SessaoAdaptativaUsuario usuario;
    private final RegistradorAtividadeWebLogGerard registro;
    private volatile long ultimoAcessoEmMs;
    // Versão da tentativa em curso: sobe a cada alteração e compõe o ETag (ver RecursosTentativa).
    private long versaoDaTentativa;
    // Os últimos ids de tentativa que outro sorteio substituiu (para responder 410 em vez de 404).
    private final java.util.LinkedHashSet<String> substituidas = new java.util.LinkedHashSet<String>();
    private final java.util.ArrayDeque<Long> requisicoesRecentes = new java.util.ArrayDeque<Long>();

    SessaoWeb(String id, ServicoSorteioAtividade sorteios, SessaoAdaptativaUsuario usuario,
            RegistradorAtividadeWebLogGerard registro) {
        this.id = id;
        this.sorteios = sorteios;
        this.usuario = usuario;
        this.registro = registro;
        this.ultimoAcessoEmMs = System.currentTimeMillis();
    }

    String id() {
        return id;
    }

    ServicoSorteioAtividade sorteios() {
        return sorteios;
    }

    SessaoAdaptativaUsuario usuario() {
        return usuario;
    }

    /** O registrador de log deste visitante (mesmo arquivo do processo, contexto próprio). */
    RegistradorAtividadeWebLogGerard registro() {
        return registro;
    }

    /** O ETag (forte) do estado atual da tentativa. */
    synchronized String etag(String tentativaId) {
        return "\"" + tentativaId + "-" + versaoDaTentativa + "\"";
    }

    synchronized void avancarVersao() {
        versaoDaTentativa++;
    }

    /** Uma tentativa nova começa na versão zero; a anterior passa a ser "substituída". */
    synchronized void trocarDeTentativa(String idAnterior) {
        versaoDaTentativa = 0;
        if (idAnterior != null) {
            substituidas.add(idAnterior);
            while (substituidas.size() > 50) {
                substituidas.remove(substituidas.iterator().next());
            }
        }
    }

    synchronized boolean foiSubstituida(String tentativaId) {
        return substituidas.contains(tentativaId);
    }

    /**
     * Limite de requisições por janela deslizante. Devolve 0 se pode passar, ou quantos segundos faltam para a
     * próxima vaga.
     */
    synchronized long segundosParaPoderRequisitar(int limite, long janelaMs) {
        long agora = System.currentTimeMillis();
        while (!requisicoesRecentes.isEmpty() && agora - requisicoesRecentes.peekFirst() >= janelaMs) {
            requisicoesRecentes.pollFirst();
        }
        if (requisicoesRecentes.size() >= limite) {
            long espera = janelaMs - (agora - requisicoesRecentes.peekFirst());
            return Math.max(1, (espera + 999) / 1000);
        }
        requisicoesRecentes.addLast(Long.valueOf(agora));
        return 0;
    }

    long ultimoAcessoEmMs() {
        return ultimoAcessoEmMs;
    }

    void registrarAcesso() {
        ultimoAcessoEmMs = System.currentTimeMillis();
    }
}
