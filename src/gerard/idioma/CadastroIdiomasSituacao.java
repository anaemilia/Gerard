package gerard.idioma;

import gerard.campoaditivo.servico.RepositorioSituacoesAditivas;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** Catálogo local e fechado dos quatro idiomas suportados pelo Gérard. */
public final class CadastroIdiomasSituacao {
    private static final String NOME_ARQUIVO = "idiomas_situacoes.tsv";
    private final LinkedHashMap<String, IdiomaSituacao> idiomas = new LinkedHashMap<String, IdiomaSituacao>();

    public CadastroIdiomasSituacao() { carregar(); }

    public synchronized List<IdiomaSituacao> listar() {
        return new ArrayList<IdiomaSituacao>(idiomas.values());
    }

    public synchronized IdiomaSituacao obter(String codigo) {
        IdiomaInterface interfaceIdioma = IdiomaSituacao.paraIdiomaInterface(codigo);
        return interfaceIdioma == null ? null : idiomas.get(IdiomaSituacao.codigoPadrao(interfaceIdioma));
    }

    public synchronized void recarregar() { carregar(); }

    /** Mantido para compatibilidade; não amplia o catálogo além dos quatro idiomas. */
    public synchronized IdiomaSituacao adicionar(String codigo, String nome, String direcao) throws IOException {
        return adicionar(codigo, nome, direcao, IdiomaSituacao.SCRIPT_PADRAO);
    }

    /** Mantido para compatibilidade; direção e script externos são ignorados. */
    public synchronized IdiomaSituacao adicionar(String codigo, String nome, String direcao, String script) throws IOException {
        IdiomaInterface interfaceIdioma = IdiomaSituacao.paraIdiomaInterface(codigo);
        if (interfaceIdioma == null) {
            throw new IllegalArgumentException("O Gérard oferece suporte apenas a português, inglês, francês e espanhol.");
        }
        IdiomaSituacao idioma = idiomaPadrao(IdiomaSituacao.codigoPadrao(interfaceIdioma));
        idiomas.put(idioma.getCodigo(), idioma);
        salvar();
        return idioma;
    }

    private void carregar() {
        idiomas.clear();
        adicionarPadrao(idiomaPadrao("pt-BR"));
        adicionarPadrao(idiomaPadrao("en"));
        adicionarPadrao(idiomaPadrao("fr"));
        adicionarPadrao(idiomaPadrao("es"));

        // Arquivos antigos podem conter outros idiomas. Eles são deliberadamente
        // ignorados e o catálogo é regravado no formato canônico de quatro idiomas.
        try { salvar(); } catch (IOException ignored) { }
    }

    public static boolean ehIdiomaPermitido(String codigo) {
        return IdiomaSituacao.paraIdiomaInterface(codigo) != null;
    }

    private static IdiomaSituacao idiomaPadrao(String codigo) {
        IdiomaInterface idioma = IdiomaSituacao.paraIdiomaInterface(codigo);
        if (idioma == IdiomaInterface.PORTUGUES) {
            return new IdiomaSituacao("pt-BR", "Português (Brasil)", IdiomaSituacao.DIRECAO_PADRAO, IdiomaSituacao.SCRIPT_PADRAO);
        }
        if (idioma == IdiomaInterface.FRANCES) {
            return new IdiomaSituacao("fr", "Français", IdiomaSituacao.DIRECAO_PADRAO, IdiomaSituacao.SCRIPT_PADRAO);
        }
        if (idioma == IdiomaInterface.ESPANHOL) {
            return new IdiomaSituacao("es", "Español", IdiomaSituacao.DIRECAO_PADRAO, IdiomaSituacao.SCRIPT_PADRAO);
        }
        return new IdiomaSituacao("en", "English", IdiomaSituacao.DIRECAO_PADRAO, IdiomaSituacao.SCRIPT_PADRAO);
    }

    private void adicionarPadrao(IdiomaSituacao idioma) {
        idiomas.put(idioma.getCodigo(), idioma);
    }

    private void salvar() throws IOException {
        File arquivo = obterArquivo();
        File dir = arquivo.getParentFile();
        if (!dir.exists()) dir.mkdirs();
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(arquivo), StandardCharsets.UTF_8))) {
            bw.write("# codigo\tnome\tdirecao\tscript");
            bw.newLine();
            for (IdiomaSituacao idioma : idiomas.values()) {
                bw.write(idioma.getCodigo() + "\t" + limpar(idioma.getNome()) + "\t"
                        + IdiomaSituacao.DIRECAO_PADRAO + "\t" + IdiomaSituacao.SCRIPT_PADRAO);
                bw.newLine();
            }
        }
    }

    public static File obterArquivo() {
        return new File(RepositorioSituacoesAditivas.obterDiretorioCuradoriaUsuario(), NOME_ARQUIVO);
    }

    private static String limpar(String texto) {
        return texto == null ? "" : texto.replace('\t', ' ').replace('\n', ' ').replace('\r', ' ').trim();
    }
}
