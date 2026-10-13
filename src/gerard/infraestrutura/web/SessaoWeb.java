package gerard.infraestrutura.web;

import gerard.adaptacao.sessao.SessaoAdaptativaUsuario;
import gerard.aplicacao.portabilidade.ServicoSorteioAtividade;

/** O estado de um visitante do servidor: a tentativa em curso e o usuário que entrou. Nada aqui é compartilhado. */
final class SessaoWeb {
    private final String id;
    private final ServicoSorteioAtividade sorteios;
    private final SessaoAdaptativaUsuario usuario;
    private volatile long ultimoAcessoEmMs;

    SessaoWeb(String id, ServicoSorteioAtividade sorteios, SessaoAdaptativaUsuario usuario) {
        this.id = id;
        this.sorteios = sorteios;
        this.usuario = usuario;
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

    long ultimoAcessoEmMs() {
        return ultimoAcessoEmMs;
    }

    void registrarAcesso() {
        ultimoAcessoEmMs = System.currentTimeMillis();
    }
}
