package gerard.pesquisador.auditoria;

/**
 * Perfil REAL persistido do usuário (o que ModeloUsuario/PerfilAluno/
 * PerfilAprendizagem de fato guardam) — separado de propósito dos
 * contadores agregados que o serviço de auditoria calcula (ver
 * {@code ProfileSnapshot} em gerard.agente.modelador, usado aqui como
 * "technical_evaluation_counters"/"canonical_user_action_counters"). A
 * usuária pediu explicitamente pra não confundir os dois — hoje não há
 * nível global/diagnóstico/estratégia/confiança persistidos, então esta
 * classe sempre vem com {@code available=false}.
 */
public final class UserProfileSnapshot {
    private final boolean available;
    private final String unavailableReason;

    private UserProfileSnapshot(boolean available, String unavailableReason) {
        this.available = available;
        this.unavailableReason = unavailableReason;
    }

    public static UserProfileSnapshot indisponivel(String motivo) {
        return new UserProfileSnapshot(false, motivo);
    }

    public boolean isAvailable() {
        return available;
    }

    public String getUnavailableReason() {
        return unavailableReason;
    }
}
