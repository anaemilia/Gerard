package gerard.pesquisador.log;

import gerard.dominio.campoaditivo.RegistroAcaoRestauracaoModelagem;

/**
 * Persiste, sem reinterpretar, a restauração constituída por
 * TentativaModelagemAditiva. Único formato de log da restauração, usado pelo
 * desktop e pela web; cada plataforma só descreve o próprio instrumento.
 */
public final class PersistidorRestauracaoModelagemLogGerard {

    private final LoggerInteracaoGerard logger;

    public PersistidorRestauracaoModelagemLogGerard(LoggerInteracaoGerard logger) {
        if (logger == null) {
            throw new IllegalArgumentException("logger obrigatorio");
        }
        this.logger = logger;
    }

    public void persistir(RegistroAcaoRestauracaoModelagem registro,
            String tarefa, String instrumentoOrganizacao,
            String instrumentoArtefato, String funcaoDoArtefato,
            String objeto, String regras, String origemEvento) {
        String detalhes = "tipo_restauracao=" + registro.getTipo().name()
                + "; tentativa_id=" + registro.getTentativaId()
                + "; papeis_participantes=" + registro.getPapeisParticipantes()
                + "; sequencias_rejeicao_encerradas="
                + registro.getSequenciasRejeicaoEncerradas();
        logger.registrarUsuarioComIdentidade(
                "SELECIONAR", tarefa, "-", instrumentoOrganizacao,
                instrumentoArtefato, funcaoDoArtefato, objeto, regras,
                origemEvento, detalhes, registro.getActionId(),
                registro.getRejectionSequenceId());
    }
}
