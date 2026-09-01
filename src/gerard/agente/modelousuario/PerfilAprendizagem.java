package gerard.agente.modelousuario;

/**
 * Dimensão 4 do Modelo do Usuário (Quadro 5.60): registra preferências de
 * mensagem e de estilo de interação. É uma dimensão do modelo, não uma regra
 * autônoma de escolha de ajuda. Ver gerard-modelo-usuario/SKILL.md.
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
