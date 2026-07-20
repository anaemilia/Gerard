package gerard.interpretacao.modelo;

import gerard.i18n.ServicoLocalizacao;

public class SubtipoVergnaud {
    private final String chaveSubtipo;
    private final String chaveIncognita;
    private final int indiceFiguraIncognita;

    public SubtipoVergnaud(String chaveSubtipo, String chaveIncognita, int indiceFiguraIncognita) {
        this.chaveSubtipo = chaveSubtipo;
        this.chaveIncognita = chaveIncognita;
        this.indiceFiguraIncognita = indiceFiguraIncognita;
    }

    public String getChaveSubtipo() {
        return chaveSubtipo;
    }

    public String getChaveIncognita() {
        return chaveIncognita;
    }

    public int getIndiceFiguraIncognita() {
        return indiceFiguraIncognita;
    }

    public boolean temIncognitaFigura() {
        return indiceFiguraIncognita >= 0 && chaveIncognita != null && chaveIncognita.trim().length() > 0;
    }

    public String getDescricaoLocalizada() {
        return ServicoLocalizacao.getInstancia().texto(chaveSubtipo);
    }

    public String getIncognitaLocalizada() {
        return chaveIncognita == null ? "" : ServicoLocalizacao.getInstancia().texto(chaveIncognita);
    }
}
