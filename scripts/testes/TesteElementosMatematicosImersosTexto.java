import gerard.Scaffolding.questionamento.ResultadoQuestionamento;
import gerard.Scaffolding.questionamento.ScaffoldingQuestionamento;
import gerard.campoaditivo.diagrama.elementos.ItemTextoArrastavel;
import gerard.campoaditivo.diagrama.elementos.MarcadorTexto;
import gerard.interacao.arraste.SessaoArrasteTextoParaDiagrama;
import gerard.interacao.texto.PoliticaElementoMatematicoTexto;
import gerard.interpretacao.incognita.InferidorIncognitaLinguistica;
import gerard.interpretacao.incognita.ResultadoIncognitaLinguistica;
import gerard.interpretacao.modelo.CategoriaProblema;
import gerard.interpretacao.modelo.NumeroEncontrado;
import gerard.interpretacao.modelo.PapelElementoInterpretado;
import gerard.interpretacao.servico.InferidorPapeisNumericos;
import java.util.ArrayList;
import java.util.List;

public class TesteElementosMatematicosImersosTexto {
    public static void main(String[] args) {
        testarPoliticaGeral();
        testarIncognitaTransformacao();
        testarValidacaoTransformacao();
        testarProxyNumeroEInterrogacao();
        testarIncognitaEmTodosOsPapeis();
        System.out.println("TesteElementosMatematicosImersosTexto: OK");
    }

    private static void testarPoliticaGeral() {
        PoliticaElementoMatematicoTexto politica = new PoliticaElementoMatematicoTexto();
        exigir(politica.ehElementoMatematico("10"), "Número natural deve ser matemático.");
        exigir(politica.ehElementoMatematico("+6"), "Número positivo deve ser matemático.");
        exigir(politica.ehElementoMatematico("-8"), "Número negativo deve ser matemático.");
        exigir(politica.ehElementoMatematico("3,5"), "Número decimal deve ser matemático.");
        exigir(politica.ehElementoMatematico("?"), "Interrogação deve ser elemento matemático.");
        exigir(!politica.ehElementoMatematico("balas"), "Texto comum não deve entrar no fluxo matemático.");
    }

    private static void testarIncognitaTransformacao() {
        String texto = "Leandro tinha 10 balas. Lucas, seu irmão, pegou algumas balas escondidas de Leandro. "
                + "Quando Leandro foi comer suas balas só restavam 6 balas. Quantas balas Lucas comeu?";
        List<NumeroEncontrado> numeros = new ArrayList<NumeroEncontrado>();
        numeros.add(new NumeroEncontrado("10", texto.indexOf("10"), texto.indexOf("10") + 2, "10"));
        numeros.add(new NumeroEncontrado("6", texto.indexOf("6 balas"), texto.indexOf("6 balas") + 1, "6"));

        ResultadoIncognitaLinguistica incognita = new InferidorIncognitaLinguistica()
                .inferir(texto, CategoriaProblema.TRANSFORMACAO_MEDIDAS, numeros);
        exigir("papel.transformacao".equals(incognita.getChavePapel()),
                "A pergunta por quantas balas foram comidas deve indicar transformação.");

        List<PapelElementoInterpretado> papeis = new InferidorPapeisNumericos()
                .inferir(texto, CategoriaProblema.TRANSFORMACAO_MEDIDAS, numeros);
        exigir(temPapel(papeis, "10", "papel.estadoInicial", true),
                "10 deve ser estado inicial.");
        exigir(temPapel(papeis, "6", "papel.estadoFinal", true),
                "6 deve ser estado final.");
        exigir(temPapel(papeis, "?", "papel.transformacao", false),
                "A interrogação deve ocupar transformação.");
    }

    private static void testarValidacaoTransformacao() {
        ScaffoldingQuestionamento questionamento = new ScaffoldingQuestionamento();
        ResultadoQuestionamento errado = questionamento.avaliarPosicionamento(
                "papel.estadoFinal", "papel.transformacao", "Transformação", "Transformação de medidas");
        exigir(errado.isAplicavel() && !errado.isCorreto(),
                "Estado final não pode ser aceito como transformação.");

        ResultadoQuestionamento correto = questionamento.avaliarPosicionamento(
                "papel.transformacao", "papel.transformacao", "Transformação", "Transformação de medidas");
        exigir(correto.isAplicavel() && correto.isCorreto(),
                "A incógnita da transformação deve ser aceita no círculo da transformação.");

        ResultadoQuestionamento estadoFinalCorreto =
                questionamento.avaliarPosicionamento(
                        "papel.estadoFinal", "papel.estadoFinal",
                        "Estado final", "Transformação de medidas");
        exigir(estadoFinalCorreto.isAplicavel()
                        && estadoFinalCorreto.isCorreto(),
                "O valor 6 deve ser aceito no estado final da transformação.");
        exigir(questionamento.deveLimparQuestionamentoPersistente(
                        estadoFinalCorreto),
                "Ao corrigir o 6 para o estado final, o tip anterior deve desaparecer.");
        exigir(!questionamento.deveLimparQuestionamentoPersistente(errado),
                "Uma incompatibilidade ainda ativa deve manter o questionamento.");
    }

    private static void testarProxyNumeroEInterrogacao() {
        SessaoArrasteTextoParaDiagrama sessao = new SessaoArrasteTextoParaDiagrama();

        MarcadorTexto numero = new MarcadorTexto(100, 70, 24, 22, "6", false, "papel.estadoFinal");
        ItemTextoArrastavel proxyNumero = sessao.iniciarPorMarcador(numero);
        exigir(proxyNumero != null && "papel.estadoFinal".equals(proxyNumero.chavePapel),
                "Número deve criar proxy com papel semântico.");
        exigir(sessao.deveManterNoDiagramaAposErro(proxyNumero, true, false),
                "Proxy semanticamente incorreto deve permanecer manipulável sobre o diagrama.");
        exigir(!sessao.deveDescartarAoSoltar(proxyNumero, true, false),
                "Erro semântico não pode remover o número do diagrama.");
        sessao.confirmarPersistencia(proxyNumero);

        MarcadorTexto incognita = new MarcadorTexto(200, 70, 18, 22, "?", true, "papel.transformacao");
        ItemTextoArrastavel proxyIncognita = sessao.iniciarPorMarcador(incognita);
        exigir(proxyIncognita != null && proxyIncognita.editavel,
                "Interrogação deve usar o mesmo proxy dos números.");
        exigir("?".equals(proxyIncognita.origemValor),
                "A origem semântica da incógnita deve ser preservada.");
        exigir(sessao.devePersistirAoSoltar(proxyIncognita, true, true),
                "Interrogação correta deve poder ser promovida ao diagrama.");
        exigir(incognita.x == 200 && incognita.y == 70,
                "O marcador original deve permanecer no texto.");
        exigir(incognita.contem(197, 69),
                "A área de pickup ampliada deve facilitar a seleção da interrogação.");
    }

    private static void testarIncognitaEmTodosOsPapeis() {
        String[] papeis = new String[] {
            "papel.parte1", "papel.parte2", "papel.todo",
            "papel.estadoInicial", "papel.transformacao", "papel.estadoFinal",
            "papel.referido", "papel.referendo", "papel.diferenca",
            "papel.transformacao1", "papel.transformacao2", "papel.transformacaoFinal",
            "papel.relacaoInicial", "papel.relacao1", "papel.relacao2", "papel.relacaoFinal"
        };
        PoliticaElementoMatematicoTexto politica = new PoliticaElementoMatematicoTexto();
        ScaffoldingQuestionamento questionamento = new ScaffoldingQuestionamento();
        for (int i = 0; i < papeis.length; i++) {
            String papel = papeis[i];
            exigir(politica.deveValidar("?", papel),
                    "A incógnita deve ser validável no papel " + papel);
            ResultadoQuestionamento resultado = questionamento.avaliarPosicionamento(
                    papel, papel, papel, "categoria");
            exigir(resultado.isAplicavel() && resultado.isCorreto(),
                    "A incógnita deve usar o mesmo fluxo de validação no papel " + papel);

            SessaoArrasteTextoParaDiagrama sessao = new SessaoArrasteTextoParaDiagrama();
            MarcadorTexto marcador = new MarcadorTexto(
                    20 + i, 30, 18, 22, "?", true, papel);
            ItemTextoArrastavel proxy = sessao.iniciarPorMarcador(marcador);
            exigir(proxy != null && papel.equals(proxy.chavePapel),
                    "O proxy da incógnita deve preservar o papel " + papel);
        }
    }

    private static boolean temPapel(List<PapelElementoInterpretado> papeis,
            String elemento, String chave, boolean conhecido) {
        for (PapelElementoInterpretado papel : papeis) {
            if (elemento.equals(papel.getElemento())
                    && chave.equals(papel.getChavePapel())
                    && conhecido == papel.isConhecido()) {
                return true;
            }
        }
        return false;
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
