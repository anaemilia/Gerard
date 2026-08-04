package gerard.agente.modelador;

/**
 * Incorporação de regra/padrão a partir de casos acumulados — pedido no
 * schema de referência, mas isso é a "ação 2" do Agente Modelador
 * ({@code InferenciaRegrasModelador}, PART + Apriori via
 * {@code AgenteModelador.inferirRegras}), que roda SOB DEMANDA (quem chama
 * decide quando, ex. fim de sessão), não a cada ação armazenada por
 * {@code armazenarCaso}. Por isso, no log por ação, esse bloco vem sempre
 * indisponível — a incorporação de padrão real fica registrada à parte,
 * quando/se {@code inferirRegras} for chamado (fora do escopo desta
 * rodada).
 */
public final class PatternIncorporationAudit {
    private final boolean incorporated;
    private final String motivoIndisponivel;

    private PatternIncorporationAudit(boolean incorporated, String motivoIndisponivel) {
        this.incorporated = incorporated;
        this.motivoIndisponivel = motivoIndisponivel;
    }

    public static PatternIncorporationAudit indisponivel(String motivo) {
        return new PatternIncorporationAudit(false, motivo);
    }

    public boolean isIncorporated() { return incorporated; }
    public String getMotivoIndisponivel() { return motivoIndisponivel; }
}
