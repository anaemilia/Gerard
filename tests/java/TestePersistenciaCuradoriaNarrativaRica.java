import gerard.campoaditivo.curadoria.RegistroCuradoriaNarrativaRica;
import gerard.campoaditivo.curadoria.RepositorioCuradoriaNarrativaRica;
import gerard.campoaditivo.curadoria.ResultadoConversaoSituacaoProblemaRica;
import gerard.campoaditivo.curadoria.ServicoSituacaoProblemaRicaCurada;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.dominio.campoaditivo.situacao.CorrespondenciaPapelNarrativa;
import gerard.dominio.campoaditivo.situacao.EstadoNarrativo;
import gerard.dominio.campoaditivo.situacao.EventoNarrativoCurado;
import gerard.dominio.campoaditivo.situacao.FamiliaObjeto;
import gerard.dominio.campoaditivo.situacao.InventarioNarrativo;
import gerard.dominio.campoaditivo.situacao.MarcadorTemporal;
import gerard.dominio.campoaditivo.situacao.NarrativaCurada;
import gerard.dominio.campoaditivo.situacao.ObjetoContado;
import gerard.dominio.campoaditivo.situacao.ParticipanteNarrativo;
import gerard.dominio.campoaditivo.situacao.ReferenciaValorNarrativo;
import gerard.dominio.campoaditivo.situacao.StatusCuradoriaSituacao;
import gerard.semantica.numero.NumeroNatural;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Prova do sidecar explícito e da entrada real do agregado rico. */
public class TestePersistenciaCuradoriaNarrativaRica {

    public static void main(String[] args) throws Exception {
        File base = Files.createTempDirectory(
                new File("build").toPath(), "narrativa-rica-").toFile();
        RepositorioCuradoriaNarrativaRica repositorio =
                new RepositorioCuradoriaNarrativaRica(base);
        Cenario cenario = cenarioReducao();
        String id = "../PO:TRANSFORMACAO_MEDIDAS:nadia/001";
        RegistroCuradoriaNarrativaRica registroNarrativo =
                new RegistroCuradoriaNarrativaRica(
                        id, cenario.narrativa, cenario.correspondencias);

        repositorio.salvar(registroNarrativo);
        File arquivo = repositorio.arquivoDaSituacao(id);
        checar("nome lógico não escapa do diretório do sidecar",
                arquivo.getCanonicalFile().getParentFile().equals(
                        base.getCanonicalFile()), true);
        checar("sidecar XML foi criado", arquivo.isFile(), true);

        RegistroCuradoriaNarrativaRica recarregado = repositorio.carregar(id)
                .orElseThrow(() -> new AssertionError("sidecar ausente"));
        checar("id lógico é preservado no conteúdo",
                id.equals(recarregado.getIdSituacao()), true);
        checar("nome declarado do participante é preservado",
                "Nadia".equals(recarregado.getNarrativa()
                        .getEstadoInicial().getInventarios().keySet()
                        .iterator().next().getNomeExibicao()), true);
        checar("evento declarado é preservado",
                recarregado.getNarrativa().getEventos().size() == 1, true);
        checar("tipo e quantidade do evento são preservados",
                recarregado.getNarrativa().getEventos().get(0).getTipo()
                        == EventoNarrativoCurado.Tipo.REDUCAO
                && recarregado.getNarrativa().getEventos().get(0)
                        .getQuantidade().intValue() == 3,
                true);
        checar("correspondências nominais são preservadas",
                recarregado.getCorrespondencias().size() == 3
                && "papel.transformacao".equals(
                        recarregado.getCorrespondencias().get(1)
                                .getChavePapel())
                && "evento.consumo".equals(
                        recarregado.getCorrespondencias().get(1)
                                .getReferencia().getChaveEvento()),
                true);
        checar("construtor compatível persiste candidata por padrão",
                recarregado.getStatusCuradoria()
                        == StatusCuradoriaSituacao.CANDIDATA_NAO_CURADA,
                true);

        repositorio.salvar(new RegistroCuradoriaNarrativaRica(
                id, StatusCuradoriaSituacao.VALIDADA_PELO_PESQUISADOR,
                cenario.narrativa, cenario.correspondencias));
        RegistroCuradoriaNarrativaRica promovido = repositorio.carregar(id)
                .orElseThrow(() -> new AssertionError("sidecar promovido ausente"));
        checar("ato humano explícito é persistido no sidecar",
                promovido.getStatusCuradoria()
                        == StatusCuradoriaSituacao.VALIDADA_PELO_PESQUISADOR,
                true);

        SituacaoProblemaAditiva tabular = registroTabular(
                id,
                "CAMPO_POSICIONAL_NAO_E_PARTICIPANTE",
                "OUTRO_CAMPO_POSICIONAL");
        ResultadoConversaoSituacaoProblemaRica resultado =
                new ServicoSituacaoProblemaRicaCurada(repositorio)
                        .converter(tabular);
        checar("serviço integra sidecar e tabela na situação rica",
                resultado.ehValida(), true);
        checar("serviço aplica a promoção editorial explícita",
                resultado.getSituacaoOuFalhar().getStatusCuradoria()
                        == StatusCuradoriaSituacao.VALIDADA_PELO_PESQUISADOR,
                true);
        checar("campos personagem antigos não substituem identidade curada",
                "Nadia".equals(resultado.getSituacaoOuFalhar().getNarrativa()
                        .getEstadoInicial().getInventarios().keySet()
                        .iterator().next().getNomeExibicao()), true);

        SituacaoProblemaAditiva traducao = registroTabular(
                "situacao.nadia.en", "POSICAO_TRADUZIDA_1",
                "POSICAO_TRADUZIDA_2", "traducao", id);
        ResultadoConversaoSituacaoProblemaRica resultadoTraducao =
                new ServicoSituacaoProblemaRicaCurada(repositorio)
                        .converter(traducao);
        checar("tradução usa a narrativa semântica da versão original",
                resultadoTraducao.ehValida()
                && "Nadia".equals(resultadoTraducao.getSituacaoOuFalhar()
                        .getNarrativa().getEstadoInicial().getInventarios()
                        .keySet().iterator().next().getNomeExibicao())
                && resultadoTraducao.getSituacaoOuFalhar()
                        .getStatusCuradoria()
                        == StatusCuradoriaSituacao.VALIDADA_PELO_PESQUISADOR,
                true);

        verificarLeituraFormatoAnteriorComoCandidata(
                repositorio, arquivo, id);

        ResultadoConversaoSituacaoProblemaRica ausente =
                new ServicoSituacaoProblemaRicaCurada(repositorio)
                        .converter(registroTabular(
                                "situacao.sem.sidecar", "A", "B"));
        checar("ausência do sidecar não aciona inferência posicional",
                ausente.foiConstruida(), false);
        checar("ausência recebe diagnóstico factual explícito",
                ausente.possuiCodigo(
                        "conversao.narrativa_persistida.ausente"), true);

        verificarReferenciaDesconhecidaBloqueada(cenario);
        verificarDoctypeBloqueado(cenario);

        System.out.println(
                "APROVADO: persistência e integração da curadoria narrativa rica.");
    }

    private static void verificarReferenciaDesconhecidaBloqueada(
            Cenario cenario) throws Exception {
        File base = Files.createTempDirectory(
                new File("build").toPath(), "narrativa-ref-invalida-")
                .toFile();
        RepositorioCuradoriaNarrativaRica repositorio =
                new RepositorioCuradoriaNarrativaRica(base);
        String id = "situacao.referencia.invalida";
        repositorio.salvar(new RegistroCuradoriaNarrativaRica(
                id, cenario.narrativa, cenario.correspondencias));
        File arquivo = repositorio.arquivoDaSituacao(id);
        String xml = new String(
                Files.readAllBytes(arquivo.toPath()), StandardCharsets.UTF_8);
        xml = substituirPrimeira(
                xml,
                "participante-id=\"participante.nadia\"",
                "participante-id=\"participante.inexistente\"");
        Files.write(arquivo.toPath(), xml.getBytes(StandardCharsets.UTF_8));
        checar("referência nominal desconhecida bloqueia leitura",
                falhaAoCarregar(repositorio, id), true);
    }

    private static void verificarLeituraFormatoAnteriorComoCandidata(
            RepositorioCuradoriaNarrativaRica repositorio,
            File arquivo,
            String id) throws Exception {
        String xml = new String(
                Files.readAllBytes(arquivo.toPath()), StandardCharsets.UTF_8);
        xml = xml.replaceFirst("versao=\"2\"", "versao=\"1\"")
                .replaceFirst("\\sstatus-curadoria=\"[^\"]+\"", "");
        Files.write(arquivo.toPath(), xml.getBytes(StandardCharsets.UTF_8));
        RegistroCuradoriaNarrativaRica legado = repositorio.carregar(id)
                .orElseThrow(() -> new AssertionError("sidecar v1 ausente"));
        checar("sidecar v1 sem status permanece candidato",
                legado.getStatusCuradoria()
                        == StatusCuradoriaSituacao.CANDIDATA_NAO_CURADA,
                true);
    }

    private static void verificarDoctypeBloqueado(Cenario cenario)
            throws Exception {
        File base = Files.createTempDirectory(
                new File("build").toPath(), "narrativa-xml-seguro-")
                .toFile();
        RepositorioCuradoriaNarrativaRica repositorio =
                new RepositorioCuradoriaNarrativaRica(base);
        String id = "situacao.doctype";
        repositorio.salvar(new RegistroCuradoriaNarrativaRica(
                id, cenario.narrativa, cenario.correspondencias));
        File arquivo = repositorio.arquivoDaSituacao(id);
        String xml = new String(
                Files.readAllBytes(arquivo.toPath()), StandardCharsets.UTF_8);
        int declaracao = xml.indexOf("?>");
        String malicioso = xml.substring(0, declaracao + 2)
                + "<!DOCTYPE curadoria-narrativa [<!ENTITY xxe SYSTEM "
                + "\"file:///arquivo-que-nao-deve-ser-lido\">]>"
                + xml.substring(declaracao + 2);
        Files.write(
                arquivo.toPath(), malicioso.getBytes(StandardCharsets.UTF_8));
        checar("DOCTYPE e entidades externas são bloqueados",
                falhaAoCarregar(repositorio, id), true);
    }

    private static boolean falhaAoCarregar(
            RepositorioCuradoriaNarrativaRica repositorio,
            String id) {
        try {
            repositorio.carregar(id);
            return false;
        } catch (IOException esperada) {
            return true;
        }
    }

    private static String substituirPrimeira(
            String texto,
            String trecho,
            String substituto) {
        int indice = texto.indexOf(trecho);
        if (indice < 0) throw new AssertionError("trecho XML não encontrado");
        return texto.substring(0, indice)
                + substituto
                + texto.substring(indice + trecho.length());
    }

    private static Cenario cenarioReducao() {
        FamiliaObjeto morangos = new FamiliaObjeto(
                "familia.morangos", "Morangos");
        Map<String, String> caracteristicas = new LinkedHashMap<>();
        caracteristicas.put("cor", "vermelho");
        ObjetoContado morango = new ObjetoContado(
                "objeto.morango", morangos,
                caracteristicas, "visual.morango");
        ParticipanteNarrativo nadia = new ParticipanteNarrativo(
                "participante.nadia", "Nadia");
        EstadoNarrativo inicial = estado(
                0, "tempo.inicial", nadia, morango, 10);
        EventoNarrativoCurado reducao = EventoNarrativoCurado.reducao(
                new MarcadorTemporal(1, "evento.consumo"),
                nadia, morango, new NumeroNatural(3));
        EstadoNarrativo finalDeclarado = estado(
                2, "tempo.final", nadia, morango, 7);
        NarrativaCurada narrativa = new NarrativaCurada(
                "Nadia consumiu três morangos.",
                inicial,
                Collections.singletonList(reducao),
                finalDeclarado);
        List<CorrespondenciaPapelNarrativa> correspondencias =
                new ArrayList<>();
        correspondencias.add(new CorrespondenciaPapelNarrativa(
                "papel.estadoInicial",
                ReferenciaValorNarrativo.quantidadeInicial(nadia, morangos)));
        correspondencias.add(new CorrespondenciaPapelNarrativa(
                "papel.transformacao",
                ReferenciaValorNarrativo.variacaoDeEvento(
                        "evento.consumo", nadia, morangos)));
        correspondencias.add(new CorrespondenciaPapelNarrativa(
                "papel.estadoFinal",
                ReferenciaValorNarrativo.quantidadeFinalCalculada(
                        nadia, morangos)));
        return new Cenario(narrativa, correspondencias);
    }

    private static EstadoNarrativo estado(
            int ordem,
            String chave,
            ParticipanteNarrativo participante,
            ObjetoContado objeto,
            int quantidade) {
        Map<ObjetoContado, NumeroNatural> itens = new LinkedHashMap<>();
        itens.put(objeto, new NumeroNatural(quantidade));
        Map<ParticipanteNarrativo, InventarioNarrativo> inventarios =
                new LinkedHashMap<>();
        inventarios.put(participante, new InventarioNarrativo(itens));
        return new EstadoNarrativo(
                new MarcadorTemporal(ordem, chave), inventarios);
    }

    private static SituacaoProblemaAditiva registroTabular(
            String id,
            String personagem1,
            String personagem2) {
        return registroTabular(
                id, personagem1, personagem2, "original", "");
    }

    private static SituacaoProblemaAditiva registroTabular(
            String id,
            String personagem1,
            String personagem2,
            String tipoVersao,
            String versaoOrigemId) {
        return new SituacaoProblemaAditiva(
                id, "grupo.nadia", tipoVersao, versaoOrigemId, true,
                TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS, "pt-BR",
                "Nadia tinha dez morangos e consumiu três.",
                "Morangos", "curadoria", "",
                "10", "3", "negativo", "7",
                "", "", "",
                "", "", "", "",
                "estado_final", "TRANSFORMACAO_MEDIDAS", "",
                personagem1, personagem2, "CAMPO_POSICIONAL_3",
                "", "", "", "", "", "",
                "", "", "");
    }

    private static void checar(String rotulo, boolean obtido, boolean esperado) {
        if (obtido != esperado) {
            throw new AssertionError(
                    rotulo + ": esperado [" + esperado
                            + "], obtido [" + obtido + "]");
        }
        System.out.println("OK - " + rotulo);
    }

    private static final class Cenario {
        private final NarrativaCurada narrativa;
        private final List<CorrespondenciaPapelNarrativa> correspondencias;

        private Cenario(
                NarrativaCurada narrativa,
                List<CorrespondenciaPapelNarrativa> correspondencias) {
            this.narrativa = narrativa;
            this.correspondencias = correspondencias;
        }
    }
}
