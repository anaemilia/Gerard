package gerard.suporte;

import java.awt.Desktop;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Prepara o relato de bug e abre, preferencialmente, a tela de composição do
 * Gmail no navegador. O envio final permanece sob controle do usuário.
 */
public final class PreparadorEmailRelatoBug {
    public static final String DESTINATARIO = "anaemilia@gmail.com";
    public static final String VERSAO_GERARD = "C89";

    public enum ResultadoAbertura {
        GMAIL_WEB,
        CLIENTE_PADRAO,
        INDISPONIVEL
    }

    /**
     * Mantém assunto, corpo e as duas alternativas de abertura derivados do
     * mesmo conteúdo, evitando divergência entre Gmail Web e mailto.
     */
    public static final class MensagemPreparada {
        private final String assunto;
        private final String corpo;
        private final URI uriGmail;
        private final URI uriMailto;

        private MensagemPreparada(String assunto, String corpo,
                URI uriGmail, URI uriMailto) {
            this.assunto = assunto;
            this.corpo = corpo;
            this.uriGmail = uriGmail;
            this.uriMailto = uriMailto;
        }

        public String getAssunto() {
            return assunto;
        }

        public String getCorpo() {
            return corpo;
        }

        public URI getUriGmail() {
            return uriGmail;
        }

        public URI getUriMailto() {
            return uriMailto;
        }
    }

    private PreparadorEmailRelatoBug() { }

    public static MensagemPreparada preparar(String descricao, String situacaoId,
            String categoria, String representacoes, String idiomaInterface,
            String idiomaSituacao, String enunciado) {
        String assunto = montarAssunto(categoria);
        String corpo = montarCorpo(descricao, situacaoId, categoria,
                representacoes, idiomaInterface, idiomaSituacao, enunciado);
        return new MensagemPreparada(
                assunto,
                corpo,
                criarUriGmail(assunto, corpo),
                criarUriMailto(assunto, corpo));
    }

    /**
     * URL oficial de composição do Gmail Web. Diferentemente do Desktop.mail,
     * esta forma entrega diretamente ao Gmail os campos to, su e body.
     */
    public static URI criarUriGmail(String assunto, String corpo) {
        String uri = "https://mail.google.com/mail/?view=cm&fs=1&tf=1"
                + "&to=" + codificar(DESTINATARIO)
                + "&su=" + codificar(assunto)
                + "&body=" + codificar(corpo);
        return URI.create(uri);
    }

    public static URI criarUriMailto(String assunto, String corpo) {
        String uri = "mailto:" + DESTINATARIO
                + "?subject=" + codificar(assunto)
                + "&body=" + codificar(corpo);
        return URI.create(uri);
    }

    /**
     * Compatibilidade com versões anteriores e testes externos.
     */
    public static URI criarUri(String descricao, String situacaoId,
            String categoria, String representacoes, String idiomaInterface,
            String idiomaSituacao, String enunciado) {
        return preparar(descricao, situacaoId, categoria, representacoes,
                idiomaInterface, idiomaSituacao, enunciado).getUriMailto();
    }

    public static String montarCorpo(String descricao, String situacaoId,
            String categoria, String representacoes, String idiomaInterface,
            String idiomaSituacao, String enunciado) {
        StringBuilder corpo = new StringBuilder();
        corpo.append("RELATO DE BUG — GÉRARD\n\n");
        corpo.append("O que eu estava fazendo quando o problema ocorreu:\n");
        corpo.append(limpar(descricao)).append("\n\n");
        corpo.append("Contexto registrado automaticamente:\n");
        corpo.append("- Data e hora: ")
                .append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))
                .append('\n');
        corpo.append("- Versão do Gérard: ").append(VERSAO_GERARD).append('\n');
        corpo.append("- Situação-problema: ").append(limpar(situacaoId)).append('\n');
        corpo.append("- Categoria: ").append(limpar(categoria)).append('\n');
        corpo.append("- Representações exibidas: ")
                .append(limpar(representacoes)).append('\n');
        corpo.append("- Idioma da interface: ").append(limpar(idiomaInterface)).append('\n');
        corpo.append("- Idioma da situação: ").append(limpar(idiomaSituacao)).append('\n');
        corpo.append("- Enunciado exibido: ").append(limpar(enunciado)).append('\n');
        return corpo.toString();
    }

    /**
     * Abre primeiro o Gmail Web. O mailto é usado apenas como contingência em
     * ambientes sem suporte à abertura de páginas pelo Desktop.
     */
    public static ResultadoAbertura abrirMensagem(MensagemPreparada mensagem) {
        if (mensagem == null || !Desktop.isDesktopSupported()) {
            return ResultadoAbertura.INDISPONIVEL;
        }
        Desktop desktop = Desktop.getDesktop();

        if (desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(mensagem.getUriGmail());
                return ResultadoAbertura.GMAIL_WEB;
            } catch (Exception ex) {
                // Tenta a alternativa mailto abaixo.
            }
        }

        if (desktop.isSupported(Desktop.Action.MAIL)) {
            try {
                desktop.mail(mensagem.getUriMailto());
                return ResultadoAbertura.CLIENTE_PADRAO;
            } catch (Exception ex) {
                return ResultadoAbertura.INDISPONIVEL;
            }
        }
        return ResultadoAbertura.INDISPONIVEL;
    }

    /**
     * Compatibilidade com versões anteriores.
     */
    public static boolean abrirClienteEmail(URI uri) {
        if (uri == null || !Desktop.isDesktopSupported()) {
            return false;
        }
        Desktop desktop = Desktop.getDesktop();
        if (!desktop.isSupported(Desktop.Action.MAIL)) {
            return false;
        }
        try {
            desktop.mail(uri);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private static String montarAssunto(String categoria) {
        return "[Gérard] Relato de bug"
                + (categoria == null || categoria.trim().length() == 0
                        ? "" : " - " + categoria.trim());
    }

    private static String codificar(String texto) {
        try {
            return URLEncoder.encode(texto == null ? "" : texto,
                    StandardCharsets.UTF_8.name()).replace("+", "%20");
        } catch (Exception ex) {
            throw new IllegalStateException("Não foi possível preparar o e-mail.", ex);
        }
    }

    private static String limpar(String valor) {
        return valor == null ? "" : valor.replace('\t', ' ')
                .replace('\r', ' ').trim();
    }
}
