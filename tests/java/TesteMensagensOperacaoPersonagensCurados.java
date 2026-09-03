package gerard.ui.vergnaud;

import gerard.campoaditivo.curadoria.sinal.AvaliacaoEscolhaOperacaoRelacao;
import gerard.campoaditivo.curadoria.sinal.OpcaoOperacaoCuradoria;
import gerard.campoaditivo.diagrama.elementos.ElementoVergnaud;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.idioma.IdiomaInterface;
import gerard.i18n.ServicoLocalizacao;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Protege o contrato entre as mensagens explicativas de operação e os campos
 * Personagem_1, Personagem_2 e Personagem_3 informados na curadoria humana.
 * A geometria e os rótulos dos elementos visuais não participam da resolução
 * dos personagens.
 */
public final class TesteMensagensOperacaoPersonagensCurados {

    private static final String PERSONAGEM_1 = "CAMPO_PERSONAGEM_1_CURADO";
    private static final String PERSONAGEM_2 = "CAMPO_PERSONAGEM_2_CURADO";
    private static final String PERSONAGEM_3 = "CAMPO_PERSONAGEM_3_CURADO";

    private static final Pattern MARCADOR = Pattern.compile("\\{([^{}]+)\\}");
    private static final Pattern MARCADOR_POSICIONAL = Pattern.compile("\\{\\d+\\}");

    private static final Caso[] CASOS = new Caso[] {
        new Caso(TipoSituacaoAditiva.TRANSFORMACAO_RELACAO,
                OpcaoOperacaoCuradoria.SOMA,
                "operacao.explicacao.transformacaoRelacao.soma"),
        new Caso(TipoSituacaoAditiva.TRANSFORMACAO_RELACAO,
                OpcaoOperacaoCuradoria.SUBTRACAO,
                "operacao.explicacao.transformacaoRelacao.subtracao"),
        new Caso(TipoSituacaoAditiva.COMPOSICAO_RELACOES,
                OpcaoOperacaoCuradoria.SOMA,
                "operacao.explicacao.composicaoRelacoes.soma"),
        new Caso(TipoSituacaoAditiva.COMPOSICAO_RELACOES,
                OpcaoOperacaoCuradoria.SUBTRACAO,
                "operacao.explicacao.composicaoRelacoes.subtracao"),
        new Caso(TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES,
                OpcaoOperacaoCuradoria.SOMA,
                "operacao.explicacao.composicaoTransformacoes.soma"),
        new Caso(TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES,
                OpcaoOperacaoCuradoria.SUBTRACAO,
                "operacao.explicacao.composicaoTransformacoes.subtracao")
    };

    private static final IdiomaInterface[] IDIOMAS = new IdiomaInterface[] {
        IdiomaInterface.PORTUGUES,
        IdiomaInterface.INGLES,
        IdiomaInterface.ESPANHOL,
        IdiomaInterface.FRANCES
    };

    private static int verificacoes;

    public static void main(String[] args) throws Exception {
        ServicoLocalizacao localizacao = ServicoLocalizacao.getInstancia();
        IdiomaInterface idiomaAnterior = localizacao.getIdiomaAtual();
        try {
            for (IdiomaInterface idioma : IDIOMAS) {
                localizacao.definirIdioma(idioma);
                for (Caso caso : CASOS) {
                    verificarModeloLocalizado(localizacao, idioma, caso);
                    verificarSubstituicaoPelosCamposCurados(localizacao, idioma, caso);
                }
            }
        } finally {
            localizacao.definirIdioma(idiomaAnterior);
        }
        System.out.println("OK: " + verificacoes
                + " verificacoes das mensagens de operacao com personagens curados.");
    }

    private static void verificarModeloLocalizado(ServicoLocalizacao localizacao,
            IdiomaInterface idioma, Caso caso) {
        String modelo = localizacao.texto(caso.chaveMensagem);
        exigir(!caso.chaveMensagem.equals(modelo),
                contexto(idioma, caso) + ": mensagem localizada ausente");
        exigir(!MARCADOR_POSICIONAL.matcher(modelo).find(),
                contexto(idioma, caso) + ": marcador posicional proibido em " + modelo);

        Matcher marcadores = MARCADOR.matcher(modelo);
        while (marcadores.find()) {
            String nome = marcadores.group(1);
            exigir("Personagem_1".equals(nome)
                            || "Personagem_2".equals(nome)
                            || "Personagem_3".equals(nome),
                    contexto(idioma, caso) + ": marcador sem fonte curada: {" + nome + "}");
        }
    }

    private static void verificarSubstituicaoPelosCamposCurados(
            ServicoLocalizacao localizacao, IdiomaInterface idioma, Caso caso)
            throws Exception {
        SituacaoProblemaAditiva situacao = criarSituacao(caso);
        List<ElementoVergnaud> elementos = elementosComRotulosNaoSemanticos();
        SeletorOperacaoRelacaoAluno seletor = new SeletorOperacaoRelacaoAluno();
        seletor.ativar(caso.tipo, situacao, elementos,
                Collections.emptyList(),
                AvaliacaoEscolhaOperacaoRelacao.TipoOperacaoSeletor.ENTRE_TRANSFORMACOES,
                localizacao);

        exigir(seletor.estaAtivo(),
                contexto(idioma, caso) + ": seletor deveria estar ativo");

        String modelo = localizacao.texto(caso.chaveMensagem);
        String esperado = modelo
                .replace("{Personagem_1}", PERSONAGEM_1)
                .replace("{Personagem_2}", PERSONAGEM_2)
                .replace("{Personagem_3}", PERSONAGEM_3);
        String obtido = obterTextoExplicacao(seletor);

        exigir(esperado.equals(obtido),
                contexto(idioma, caso)
                        + ": a explicacao deve substituir somente os campos curados homonimos"
                        + "\nesperado: " + esperado + "\nobtido: " + obtido);
        exigir(!MARCADOR.matcher(obtido).find(),
                contexto(idioma, caso) + ": marcador nao resolvido em " + obtido);
        exigir(!obtido.contains("ROTULO_VISUAL_"),
                contexto(idioma, caso)
                        + ": personagem foi associado a um rotulo visual");
    }

    private static SituacaoProblemaAditiva criarSituacao(Caso caso) {
        return new SituacaoProblemaAditiva(
                "TESTE_MENSAGEM_OPERACAO", "GRUPO_TESTE_MENSAGEM", "original", "",
                true, caso.tipo, "pt-BR", "", "", "teste", "",
                "", "", "", "", "", "", "", "", "", "", "", "", "", "",
                PERSONAGEM_1, PERSONAGEM_2, PERSONAGEM_3,
                "", "", "", "", "", "", caso.operacao.getValorCanonico());
    }

    private static List<ElementoVergnaud> elementosComRotulosNaoSemanticos() {
        return Arrays.asList(
                new ElementoVergnaud(240, 80, 80, 80, null,
                        "ROTULO_VISUAL_3", null, false),
                new ElementoVergnaud(20, 200, 80, 80, null,
                        "ROTULO_VISUAL_1", null, false),
                new ElementoVergnaud(460, 200, 80, 80, null,
                        "ROTULO_VISUAL_2", null, false));
    }

    private static String obterTextoExplicacao(SeletorOperacaoRelacaoAluno seletor)
            throws Exception {
        Field campo = SeletorOperacaoRelacaoAluno.class
                .getDeclaredField("textoExplicacaoCorreta");
        campo.setAccessible(true);
        return (String) campo.get(seletor);
    }

    private static String contexto(IdiomaInterface idioma, Caso caso) {
        return idioma.name() + "/" + caso.tipo.name() + "/" + caso.operacao.name();
    }

    private static void exigir(boolean condicao, String mensagem) {
        verificacoes++;
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }

    private static final class Caso {
        private final TipoSituacaoAditiva tipo;
        private final OpcaoOperacaoCuradoria operacao;
        private final String chaveMensagem;

        private Caso(TipoSituacaoAditiva tipo,
                OpcaoOperacaoCuradoria operacao, String chaveMensagem) {
            this.tipo = tipo;
            this.operacao = operacao;
            this.chaveMensagem = chaveMensagem;
        }
    }
}
