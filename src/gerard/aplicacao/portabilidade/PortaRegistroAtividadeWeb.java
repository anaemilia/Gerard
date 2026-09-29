package gerard.aplicacao.portabilidade;

import gerard.aplicacao.interacao.PortaPersistenciaAcaoInstrumental;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.dominio.atividade.RegistroFactualAcaoInstrumental;
import gerard.dominio.campoaditivo.RegistroAcaoRestauracaoModelagem;

/**
 * Porta de saída para registrar fatos já constituídos pela atividade web.
 * Estende a mesma porta de persistência de ações instrumentais do desktop
 * (decisão da usuária, 2026-09-29: a modelagem web precisa persistir).
 */
public interface PortaRegistroAtividadeWeb extends PortaPersistenciaAcaoInstrumental {

    PortaRegistroAtividadeWeb NENHUMA = new PortaRegistroAtividadeWeb() {
        public void registrarNovaSituacao(SituacaoProblemaAditiva situacao,
                String categoriaFallback, String enunciado) { }
        public void registrarAcaoGranularUsuario(String tipoAcao, String tarefa,
                String instrumentoOrganizacao, String instrumentoArtefato,
                String funcaoArtefato, String objeto, String origemEvento,
                String detalhes, String mudancaObservavel) { }
        public void persistir(RegistroFactualAcaoInstrumental registro) { }
        public void registrarRestauracaoModelagem(RegistroAcaoRestauracaoModelagem registro) { }
    };

    void registrarNovaSituacao(SituacaoProblemaAditiva situacao,
            String categoriaFallback, String enunciado);

    void registrarAcaoGranularUsuario(String tipoAcao, String tarefa,
            String instrumentoOrganizacao, String instrumentoArtefato,
            String funcaoArtefato, String objeto, String origemEvento,
            String detalhes, String mudancaObservavel);

    /** Restauração já constituída pela TentativaModelagemAditiva. */
    void registrarRestauracaoModelagem(RegistroAcaoRestauracaoModelagem registro);
}
