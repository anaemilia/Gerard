package gerard.aplicacao.portabilidade;

import gerard.aplicacao.interacao.PortaPersistenciaAcaoInstrumental;
import gerard.dominio.atividade.RegistroFactualAcaoInstrumental;
import gerard.dominio.campoaditivo.PapelQuantitativo;
import gerard.dominio.campoaditivo.OrigemAcao;
import gerard.dominio.campoaditivo.RegistroAcaoRestauracaoModelagem;
import gerard.dominio.campoaditivo.TentativaModelagemAditiva;
import gerard.dominio.campoaditivo.TipoRestauracaoModelagem;

import java.util.ArrayList;
import java.util.List;

/**
 * Contraparte web de Main.persistirAcaoDaTentativa (decisão da usuária,
 * 2026-09-29: a modelagem web precisa persistir). Não decide nada: a
 * {@link TentativaModelagemAditiva} da situação sorteada é quem constitui (ou
 * não, após a conclusão) cada registro produzido pelos proprietários
 * semânticos; aqui só se entrega o que ela constituiu à mesma porta de
 * persistência usada pelo desktop.
 */
public final class EscopoTentativaWeb {

    private static final PortaPersistenciaAcaoInstrumental SEM_PERSISTENCIA =
            new PortaPersistenciaAcaoInstrumental() {
                public void persistir(RegistroFactualAcaoInstrumental registro) { }
            };

    private final TentativaModelagemAditiva tentativa;
    private final PortaPersistenciaAcaoInstrumental persistencia;
    private final List<PapelQuantitativo> participantesAtuais =
            new ArrayList<PapelQuantitativo>();

    public EscopoTentativaWeb(TentativaModelagemAditiva tentativa,
            PortaPersistenciaAcaoInstrumental persistencia) {
        if (tentativa == null) {
            throw new IllegalArgumentException("tentativa é obrigatória");
        }
        this.tentativa = tentativa;
        this.persistencia = persistencia == null ? SEM_PERSISTENCIA : persistencia;
    }

    /** Escopo sem persistência, para usos isolados (testes, serviço avulso). */
    public static EscopoTentativaWeb isolado(String tentativaId) {
        return new EscopoTentativaWeb(new TentativaModelagemAditiva(tentativaId), null);
    }

    public void incorporar(PapelQuantitativo... papeis) {
        participantesAtuais.clear();
        if (papeis == null) {
            return;
        }
        for (PapelQuantitativo papel : papeis) {
            if (papel == null) {
                continue;
            }
            participantesAtuais.add(papel);
            tentativa.incorporar(papel);
        }
    }

    public RegistroAcaoRestauracaoModelagem restaurar(
            TipoRestauracaoModelagem tipo, OrigemAcao origem) {
        return tentativa.restaurar(tipo, origem,
                participantesAtuais.toArray(new PapelQuantitativo[0]));
    }

    public void persistir(RegistroFactualAcaoInstrumental registro) {
        if (tentativa.constituir(registro).isPresent()) {
            persistencia.persistir(registro);
        }
    }

    public TentativaModelagemAditiva getTentativa() {
        return tentativa;
    }
}
