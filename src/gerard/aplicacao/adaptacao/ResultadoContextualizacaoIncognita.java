package gerard.aplicacao.adaptacao;

import gerard.adaptacao.ContextoAdaptativoUsuario;
import gerard.dominio.campoaditivo.IncognitaQuantitativa;
import java.util.Optional;

/**
 * Resultado explícito da ligação entre a incógnita da situação corrente e a
 * fotografia do usuário. Ausência de contexto nunca é convertida em perfil ou
 * valor padrão.
 */
public final class ResultadoContextualizacaoIncognita {

    public enum Estado {
        DISPONIVEL,
        SEM_FOTOGRAFIA_ATIVA,
        SEM_SITUACAO,
        SEM_DESIGNACAO_INCOGNITA,
        DESIGNACAO_INCONSISTENTE
    }

    private final Estado estado;
    private final IncognitaQuantitativa incognita;
    private final ContextoAdaptativoUsuario contexto;
    private final String detalhe;

    private ResultadoContextualizacaoIncognita(
            Estado estado,
            IncognitaQuantitativa incognita,
            ContextoAdaptativoUsuario contexto,
            String detalhe) {
        this.estado = estado;
        this.incognita = incognita;
        this.contexto = contexto;
        this.detalhe = detalhe == null ? "" : detalhe.trim();
    }

    public static ResultadoContextualizacaoIncognita disponivel(
            IncognitaQuantitativa incognita,
            ContextoAdaptativoUsuario contexto) {
        if (incognita == null || contexto == null) {
            throw new IllegalArgumentException(
                    "incógnita e contexto são obrigatórios no resultado disponível");
        }
        return new ResultadoContextualizacaoIncognita(
                Estado.DISPONIVEL, incognita, contexto, "");
    }

    public static ResultadoContextualizacaoIncognita indisponivel(
            Estado estado,
            String detalhe) {
        return indisponivel(estado, null, detalhe);
    }

    /**
     * Ausência de fotografia não apaga o objeto semântico já resolvido. O
     * contexto adaptativo pode faltar enquanto a incógnita continua apta a
     * interpretar e registrar a ação corrente.
     */
    public static ResultadoContextualizacaoIncognita indisponivel(
            Estado estado,
            IncognitaQuantitativa incognita,
            String detalhe) {
        if (estado == null || estado == Estado.DISPONIVEL) {
            throw new IllegalArgumentException(
                    "estado indisponível deve identificar a causa factual");
        }
        return new ResultadoContextualizacaoIncognita(
                estado, incognita, null, detalhe);
    }

    public Estado getEstado() { return estado; }
    public boolean estaDisponivel() { return estado == Estado.DISPONIVEL; }
    public Optional<IncognitaQuantitativa> getIncognita() {
        return Optional.ofNullable(incognita);
    }
    public Optional<ContextoAdaptativoUsuario> getContexto() {
        return Optional.ofNullable(contexto);
    }
    public String getDetalhe() { return detalhe; }
}
