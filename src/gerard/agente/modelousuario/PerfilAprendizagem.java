package gerard.agente.modelousuario;

/**
 * Dimensão 4 do Modelo do Usuário (Quadro 5.60): orienta a construção de
 * mensagens e o estilo de interação oferecido pelo Agente ZDP. Ver
 * gerard-modelo-usuario/SKILL.md.
 */
public class PerfilAprendizagem {
    private MidiaPreferida midiaPreferida;
    private NivelEscolaridade nivelEscolaridade;

    public MidiaPreferida getMidiaPreferida() {
        return midiaPreferida;
    }

    public void setMidiaPreferida(MidiaPreferida midiaPreferida) {
        this.midiaPreferida = midiaPreferida;
    }

    public NivelEscolaridade getNivelEscolaridade() {
        return nivelEscolaridade;
    }

    public void setNivelEscolaridade(NivelEscolaridade nivelEscolaridade) {
        this.nivelEscolaridade = nivelEscolaridade;
    }
}
