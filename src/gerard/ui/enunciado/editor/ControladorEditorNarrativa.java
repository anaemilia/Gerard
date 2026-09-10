package gerard.ui.enunciado.editor;

import gerard.campoaditivo.diagrama.modelo.VocabularioTextoNarrativo;
import gerard.interpretacao.modelo.SegmentoTextoSemantico;
import java.util.ArrayList;
import java.util.List;

/**
 * Estado do rascunho do editor de enunciado — porte direto da lógica de
 * estado de EditorNarrativa.tsx (protótipo web: {@code pecas},
 * {@code sacoComum}, {@code sacoOrganizadores}, {@code inserir},
 * {@code retirar}, {@code adicionarComum}) para o desktop, sem nenhuma
 * dependência de Swing — só listas e regras de negócio. Quem desenha e capta
 * gestos do mouse (estilo {@code ElementoTextoMovel}, dentro da própria
 * cena) é {@link GeometriaEditorNarrativa} e {@link RenderizadorEditorNarrativa};
 * Main.java só guarda uma instância desta classe enquanto o modo de edição
 * está aberto.
 *
 * "Rascunho em memória": nada aqui é persistido ou reinterpretado — fechar o
 * editor descarta esta instância inteira, exatamente como no protótipo web.
 */
public final class ControladorEditorNarrativa {
    private final List<PecaPalavraRascunho> frase = new ArrayList<PecaPalavraRascunho>();
    private final List<PecaPalavraRascunho> comuns = new ArrayList<PecaPalavraRascunho>();
    private final List<PecaPalavraRascunho> organizadores = new ArrayList<PecaPalavraRascunho>();
    private int proximaSequenciaFrase = 0;
    private int proximaSequenciaComum = 0;

    public ControladorEditorNarrativa(List<SegmentoTextoSemantico> elementos,
            VocabularioTextoNarrativo vocabulario) {
        if (elementos != null) {
            for (int i = 0; i < elementos.size(); i++) {
                frase.add(PecaPalavraRascunho.apartirDeSegmento(elementos.get(i), i));
            }
        }
        if (vocabulario != null) {
            List<String> candidatos = vocabulario.getCandidatosOrganizadoresInformacao();
            for (int i = 0; i < candidatos.size(); i++) {
                organizadores.add(PecaPalavraRascunho.apartirDeOrganizadorCandidato(candidatos.get(i), i));
            }
        }
    }

    public List<PecaPalavraRascunho> getFrase() { return frase; }
    public List<PecaPalavraRascunho> getComuns() { return comuns; }
    public List<PecaPalavraRascunho> getOrganizadores() { return organizadores; }

    public List<PecaPalavraRascunho> obterSaco(String nome) {
        if ("FRASE".equals(nome)) return frase;
        if ("COMUM".equals(nome)) return comuns;
        if ("ORGANIZADORES".equals(nome)) return organizadores;
        return null;
    }

    /** Reordena uma peça já presente na frase (arrastar dentro da própria frase). */
    public void moverDentroDaFrase(PecaPalavraRascunho peca, int indiceDestino) {
        int indiceAtual = frase.indexOf(peca);
        if (indiceAtual < 0) {
            return;
        }
        frase.remove(indiceAtual);
        int destino = indiceAtual < indiceDestino ? indiceDestino - 1 : indiceDestino;
        destino = Math.max(0, Math.min(destino, frase.size()));
        frase.add(destino, peca);
    }

    /** Insere uma cópia de uma peça de saco (comuns ou organizadores) dentro da frase. A peça do saco continua lá. */
    public void inserirDoSacoNaFrase(PecaPalavraRascunho pecaDoSaco, int indiceDestino) {
        if (pecaDoSaco == null || pecaDoSaco.getValor() == null || pecaDoSaco.getValor().trim().length() == 0) {
            return;
        }
        int destino = Math.max(0, Math.min(indiceDestino, frase.size()));
        frase.add(destino, pecaDoSaco.copiaParaFrase(proximaSequenciaFrase++));
    }

    /**
     * Remove uma peça da frase — equivalente ao botão "×" do protótipo web,
     * aqui disparado por um clique parado sobre a peça. Devolve a peça ao
     * seu saco de destino, se ela ainda não estiver lá (mesmo critério do
     * web: compara pelo valor, não pelo id).
     */
    public void retirarDaFrase(PecaPalavraRascunho peca) {
        if (peca == null || !peca.isManipulavel() || !frase.remove(peca)) {
            return;
        }
        if ("COMUM".equals(peca.getSacoDestino()) && !contemValor(comuns, peca.getValor())) {
            comuns.add(peca);
        } else if ("ORGANIZADORES".equals(peca.getSacoDestino()) && !contemValor(organizadores, peca.getValor())) {
            organizadores.add(peca);
        }
    }

    /** Nova palavra digitada pela usuária, adicionada ao saco de palavras comuns (se ainda não existir lá). */
    public void adicionarPalavraComum(String valorDigitado) {
        if (valorDigitado == null) {
            return;
        }
        String valor = valorDigitado.trim();
        if (valor.length() == 0 || contemValor(comuns, valor)) {
            return;
        }
        comuns.add(PecaPalavraRascunho.apartirDePalavraComumDigitada(valor, proximaSequenciaComum++));
    }

    private static boolean contemValor(List<PecaPalavraRascunho> pecas, String valor) {
        for (PecaPalavraRascunho peca : pecas) {
            if (peca.getValor().equals(valor)) {
                return true;
            }
        }
        return false;
    }
}
