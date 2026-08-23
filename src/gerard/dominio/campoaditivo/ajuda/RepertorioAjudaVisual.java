package gerard.dominio.campoaditivo.ajuda;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Repertório imutável de narrativas visuais pertencente à categoria. */
public final class RepertorioAjudaVisual {

    private static final RepertorioAjudaVisual VAZIO =
            new RepertorioAjudaVisual(
                    "ajuda.visual.vazio",
                    Collections.<HistorinhaAjudaVisual>emptyList());

    private final String chave;
    private final List<HistorinhaAjudaVisual> historinhas;

    private RepertorioAjudaVisual(String chave, List<HistorinhaAjudaVisual> historinhas) {
        this.chave = chave;
        this.historinhas = Collections.unmodifiableList(
                new ArrayList<HistorinhaAjudaVisual>(historinhas));
    }

    public static RepertorioAjudaVisual vazio() {
        return VAZIO;
    }

    public static RepertorioAjudaVisual criar(
            String chave,
            HistorinhaAjudaVisual... historinhas) {
        if (chave == null || chave.trim().length() == 0) {
            throw new IllegalArgumentException("chave não pode ser vazia");
        }
        List<HistorinhaAjudaVisual> itens = new ArrayList<HistorinhaAjudaVisual>();
        if (historinhas != null) {
            for (HistorinhaAjudaVisual historinha : historinhas) {
                if (historinha == null) {
                    throw new IllegalArgumentException("historinha não pode ser nula");
                }
                itens.add(historinha);
            }
        }
        return new RepertorioAjudaVisual(chave, itens);
    }

    public String getChave() {
        return chave;
    }

    public List<HistorinhaAjudaVisual> getHistorinhas() {
        return historinhas;
    }

    public boolean estaVazio() {
        return historinhas.isEmpty();
    }
}
