import gerard.agente.conhecimento.AnalisadorJsonSimples;
import gerard.aplicacao.portabilidade.ProjetorSituacaoProblemaPortatil;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.dominio.campoaditivo.PapelQuantitativo;
import gerard.dominio.campoaditivo.RelacaoEstruturalComposicao;
import gerard.dominio.campoaditivo.evento.PublicadorEventoDominio;
import gerard.dominio.campoaditivo.situacao.CorrespondenciaPapelNarrativa;
import gerard.dominio.campoaditivo.situacao.EstadoNarrativo;
import gerard.dominio.campoaditivo.situacao.EstruturaAditiva;
import gerard.dominio.campoaditivo.situacao.FamiliaObjeto;
import gerard.dominio.campoaditivo.situacao.InventarioNarrativo;
import gerard.dominio.campoaditivo.situacao.MarcadorTemporal;
import gerard.dominio.campoaditivo.situacao.NarrativaCurada;
import gerard.dominio.campoaditivo.situacao.ObjetoContado;
import gerard.dominio.campoaditivo.situacao.ParticipanteNarrativo;
import gerard.dominio.campoaditivo.situacao.ReferenciaValorNarrativo;
import gerard.dominio.campoaditivo.situacao.RelacaoEstruturalVinculada;
import gerard.dominio.campoaditivo.situacao.SituacaoProblema;
import gerard.dominio.campoaditivo.situacao.StatusCuradoriaSituacao;
import gerard.pesquisador.auditoria.EscritorJsonSimples;
import gerard.semantica.numero.NumeroNatural;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Contrato executável da primeira projeção portátil consumida pela web. */
public class TesteContratoWebSituacaoProblema {
    private static final Path AMOSTRA = Paths.get(
            "web-poc", "dados", "situacao-exemplo.json");

    public static void main(String[] args) throws Exception {
        Map<String, Object> projecao =
                new ProjetorSituacaoProblemaPortatil().projetar(criarSituacao());
        String json = EscritorJsonSimples.escrever(projecao);

        if (args.length == 1 && "--imprimir".equals(args[0])) {
            System.out.println(json);
            return;
        }

        String amostra = new String(Files.readAllBytes(AMOSTRA),
                StandardCharsets.UTF_8).trim();
        checar("a amostra web é JSON válido",
                AnalisadorJsonSimples.analisar(amostra) instanceof Map);
        checar("a amostra foi produzida pelo projetor portátil",
                json.equals(amostra));
        checar("o contrato identifica a categoria canônica",
                "COMPOSICAO_MEDIDAS".equals(projecao.get("categoria")));
        checar("o contrato identifica nominalmente a incógnita",
                "papel.todo".equals(
                        projecao.get("papel_desconhecido_original")));
        String fonteProjetor = new String(Files.readAllBytes(Paths.get(
                "src", "gerard", "aplicacao", "portabilidade",
                "ProjetorSituacaoProblemaPortatil.java")),
                StandardCharsets.UTF_8);
        checar("o projetor não usa tipos Swing/AWT",
                !fonteProjetor.contains("java.awt")
                        && !fonteProjetor.contains("javax.swing"));
        System.out.println(
                "APROVADO: contrato portátil da situação-problema para a web.");
    }

    private static SituacaoProblema criarSituacao() {
        FamiliaObjeto blocos = new FamiliaObjeto(
                "familia.blocos", "Blocos");
        ObjetoContado vermelhos = new ObjetoContado(
                "objeto.blocos.vermelhos", blocos,
                caracteristica("cor", "vermelha"),
                "visual.bloco.vermelho");
        ObjetoContado amarelos = new ObjetoContado(
                "objeto.blocos.amarelos", blocos,
                caracteristica("cor", "amarela"),
                "visual.bloco.amarelo");
        ParticipanteNarrativo colecao = new ParticipanteNarrativo(
                "participante.colecao", "Coleção");

        Map<ObjetoContado, NumeroNatural> quantidades = new LinkedHashMap<>();
        quantidades.put(vermelhos, new NumeroNatural(3));
        quantidades.put(amarelos, new NumeroNatural(5));
        InventarioNarrativo inventario = new InventarioNarrativo(quantidades);
        Map<ParticipanteNarrativo, InventarioNarrativo> inventarios =
                new LinkedHashMap<>();
        inventarios.put(colecao, inventario);
        EstadoNarrativo inicial = new EstadoNarrativo(
                new MarcadorTemporal(0, "tempo.inicial"), inventarios);
        EstadoNarrativo finalDeclarado = new EstadoNarrativo(
                new MarcadorTemporal(1, "tempo.final"), inventarios);
        NarrativaCurada narrativa = new NarrativaCurada(
                "Exemplo técnico de uma coleção com dois grupos de blocos.",
                inicial, Collections.emptyList(), finalDeclarado);

        PapelQuantitativo parte1 = PapelQuantitativo.parte1(
                PublicadorEventoDominio.NENHUM);
        PapelQuantitativo parte2 = PapelQuantitativo.parte2(
                PublicadorEventoDominio.NENHUM);
        PapelQuantitativo todo = PapelQuantitativo.todo(
                PublicadorEventoDominio.NENHUM);
        parte1.posicionar(new NumeroNatural(3));
        parte2.posicionar(new NumeroNatural(5));
        todo.posicionar(new NumeroNatural(8));

        EstruturaAditiva estrutura = new EstruturaAditiva(
                TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                Arrays.asList(parte1, parte2, todo),
                Collections.singletonList(new RelacaoEstruturalVinculada(
                        "relacao.composicao_medidas",
                        RelacaoEstruturalComposicao.composicaoDeMedidas(),
                        parte1, parte2, todo)),
                todo.getChave());

        return new SituacaoProblema(
                "situacao.web-poc.composicao-medidas",
                StatusCuradoriaSituacao.CANDIDATA_NAO_CURADA,
                Collections.singletonList("exemplo-tecnico-web-poc"),
                estrutura,
                narrativa,
                Arrays.asList(
                        new CorrespondenciaPapelNarrativa(
                                parte1.getChave(),
                                ReferenciaValorNarrativo
                                        .quantidadeInicialDoObjeto(
                                                colecao, vermelhos)),
                        new CorrespondenciaPapelNarrativa(
                                parte2.getChave(),
                                ReferenciaValorNarrativo
                                        .quantidadeInicialDoObjeto(
                                                colecao, amarelos)),
                        new CorrespondenciaPapelNarrativa(
                                todo.getChave(),
                                ReferenciaValorNarrativo.quantidadeInicial(
                                        colecao, blocos))));
    }

    private static Map<String, String> caracteristica(
            String chave, String valor) {
        Map<String, String> mapa = new LinkedHashMap<>();
        mapa.put(chave, valor);
        return mapa;
    }

    private static void checar(String rotulo, boolean condicao) {
        if (!condicao) throw new AssertionError(rotulo);
        System.out.println("OK - " + rotulo);
    }
}
