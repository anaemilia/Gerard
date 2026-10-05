package gerard.ui.comparacao;

/** Porta pela qual a tela "Comparar categorias" entrega a ação do sujeito a quem sabe registrá-la. */
public interface RegistroAcaoComparacao {
    void registrar(String evento, String detalhes);
}
