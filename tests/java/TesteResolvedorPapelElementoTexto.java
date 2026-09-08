import gerard.campoaditivo.semantica.CatalogoPapeisSemanticosAditivos;
import gerard.campoaditivo.sincronizacao.texto.ElementoSemanticoTexto;
import gerard.campoaditivo.sincronizacao.texto.ResolvedorPapelElementoTexto;
import gerard.interpretacao.modelo.CategoriaProblema;
import gerard.interpretacao.modelo.IdiomaProblema;
import gerard.interpretacao.modelo.PapelElementoInterpretado;
import gerard.interpretacao.modelo.ResultadoInterpretacao;
import gerard.interpretacao.modelo.SubtipoVergnaud;

import java.util.Arrays;
import java.util.Collections;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TesteResolvedorPapelElementoTexto {

    public static void main(String[] args) throws Exception {
        ResolvedorPapelElementoTexto resolvedor =
                new ResolvedorPapelElementoTexto(
                        new CatalogoPapeisSemanticosAditivos());
        ResultadoInterpretacao interpretacao = interpretacao();

        exigir("papel.parte1".equals(
                resolvedor.obterChavePapelExataDoItem(
                        interpretacao,
                        new ElementoTeste("papel.parte1", "6", true, false),
                        "6")),
                "vinculo nominal específico deve prevalecer");

        exigir("papel.parte2".equals(
                resolvedor.obterChavePapelExataDoItem(
                        interpretacao,
                        new ElementoTeste("papel.valor", "8", false, false),
                        "8")),
                "compatibilidade deve preservar fallback por valor original");

        exigir("papel.todo".equals(
                resolvedor.obterChavePapelExataDoItem(
                        interpretacao,
                        new ElementoTeste("papel.valor", "?", false, true),
                        "14")),
                "incógnita original preenchida deve preservar o papel curado");

        exigir("papel.valor".equals(
                resolvedor.obterChavePapelExataDoItem(
                        interpretacao, null, null)),
                "item ausente deve permanecer genérico");

        ElementoTeste elementoExplicito =
                new ElementoTeste("papel.parte2", "8", true, false);
        exigir("papel.parte2".equals(
                resolvedor.obterChavePapelExataDoElemento(
                        interpretacao, elementoExplicito, false, 0)),
                "elemento vinculado não pode ser redefinido pelo índice");
        exigir("papel.parte".equals(
                resolvedor.obterChavePapelCanonicoDoElemento(
                        interpretacao, elementoExplicito, false, 0)),
                "projeção canônica deve conservar a família do papel");

        ElementoTeste elementoLegado =
                new ElementoTeste(null, "", false, false);
        exigir("papel.parte2".equals(
                resolvedor.obterChavePapelExataDoElemento(
                        interpretacao, elementoLegado, false, 1)),
                "fallback posicional legado deve permanecer disponível");

        exigir("papel.todo".equals(
                resolvedor.obterChavePapelExataDoElemento(
                        interpretacao, elementoLegado, true, 0)),
                "interrogação deve usar a incógnita declarada na curadoria");

        exigir("papel.todo".equals(
                resolvedor.obterChavePapelCanonicoDoElemento(
                        interpretacao, elementoLegado, false, 99)),
                "fallback genérico do tooltip deve recuperar a incógnita curada");

        testarFronteiraArquitetural();

        System.out.println(
                "OK: resolução portátil dos papéis dos elementos textuais");
    }

    private static void testarFronteiraArquitetural() throws Exception {
        String servico = new String(Files.readAllBytes(Paths.get(
                "src/gerard/campoaditivo/sincronizacao/texto/"
                        + "ResolvedorPapelElementoTexto.java")),
                StandardCharsets.UTF_8);
        exigir(!servico.contains("import javax.swing")
                        && !servico.contains("import java.awt")
                        && !servico.contains("MouseEvent")
                        && !servico.contains("Graphics2D"),
                "resolvedor portátil não pode depender da tecnologia visual");

        String main = new String(Files.readAllBytes(
                Paths.get("src/Main.java")), StandardCharsets.UTF_8);
        String inicio = "private String obterChavePapelExataDoItem";
        String fim = "private int obterIndiceElementoVergnaudPorPapel";
        int posicaoInicio = main.indexOf(inicio);
        int posicaoFim = main.indexOf(fim, posicaoInicio);
        exigir(posicaoInicio >= 0 && posicaoFim > posicaoInicio,
                "wrapper do item textual deve permanecer localizável");
        String wrapper = main.substring(posicaoInicio, posicaoFim);
        exigir(wrapper.contains(
                        "resolvedorPapelElementoTexto.obterChavePapelExataDoItem")
                        && !wrapper.contains("SimboloDesconhecido")
                        && !wrapper.contains("chavePapelEspecifica"),
                "Main deve apenas encaminhar a resolução semântica do item");
    }

    private static ResultadoInterpretacao interpretacao() {
        return new ResultadoInterpretacao(
                IdiomaProblema.PORTUGUES,
                CategoriaProblema.COMPOSICAO_MEDIDAS,
                1.0,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList(),
                Arrays.asList(
                        new PapelElementoInterpretado(
                                "6", "papel.parte1", true),
                        new PapelElementoInterpretado(
                                "8", "papel.parte2", true),
                        new PapelElementoInterpretado(
                                "?", "papel.todo", false)),
                "soma",
                (SubtipoVergnaud) null);
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }

    private static final class ElementoTeste
            implements ElementoSemanticoTexto {
        private final String chave;
        private final String valorOriginal;
        private final boolean vinculado;
        private final boolean incognita;

        private ElementoTeste(String chave, String valorOriginal,
                boolean vinculado, boolean incognita) {
            this.chave = chave;
            this.valorOriginal = valorOriginal;
            this.vinculado = vinculado;
            this.incognita = incognita;
        }

        public String getChavePapelSemantico() {
            return chave;
        }

        public String getValorSemanticoOriginal() {
            return valorOriginal;
        }

        public boolean possuiVinculoSemantico() {
            return vinculado;
        }

        public boolean representaIncognitaOriginal() {
            return incognita;
        }

        public void atualizarValorSemantico(String novoValor) {
            // O teste cobre somente resolução, sem mutação da representação.
        }
    }
}
