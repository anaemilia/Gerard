package gerard.ui.interacao;

import java.util.Collection;

import gerard.campoaditivo.diagrama.elementos.ItemTextoArrastavel;

/**
 * A pergunta de posicionamento incorreto que fica aberta, ancorada no item questionado, até o posicionamento ser
 * corrigido. Guarda só o que a tela precisa lembrar (qual item e qual texto); quem julga o posicionamento é o
 * ScaffoldingQuestionamento, e a tela só consulta aqui. Sem Swing.
 */
public final class QuestionamentoPersistente {
    private ItemTextoArrastavel item;
    private String texto = "";

    /** Abre a pergunta sobre o item; mensagem vazia não abre nada. */
    public boolean abrir(ItemTextoArrastavel item, String mensagem) {
        if (item == null || mensagem == null || mensagem.trim().length() == 0) {
            return false;
        }
        this.item = item;
        this.texto = mensagem;
        return true;
    }

    /** Mantém aberta a pergunta do mesmo item com o texto reavaliado. */
    public void atualizarTexto(String mensagem) {
        this.texto = mensagem;
    }

    public void fechar() {
        this.item = null;
        this.texto = "";
    }

    /** true quando a pergunta aberta é sobre este item. */
    public boolean ehSobre(ItemTextoArrastavel candidato) {
        return candidato != null && candidato == item;
    }

    /** Aberta, com texto, e sobre um item que ainda está na tela. */
    public boolean estaValida(Collection<ItemTextoArrastavel> itensNaTela) {
        return item != null && itensNaTela.contains(item) && texto != null && texto.trim().length() > 0;
    }

    public ItemTextoArrastavel getItem() {
        return item;
    }

    public String getTexto() {
        return texto;
    }

    public boolean estaAberta() {
        return item != null;
    }
}
