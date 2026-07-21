package gerard.campoaditivo.montagem;

import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.idioma.IdiomaInterface;
import gerard.pesquisador.log.LoggerInteracaoGerard;
import java.io.File;
import java.text.Normalizer;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public final class TesteMontagemSituacao {
    private static int verificacoes;

    public static void main(String[] args) {
        File home = new File(System.getProperty("java.io.tmpdir"), "gerard_teste_montagem_" + System.nanoTime());
        home.mkdirs();
        System.setProperty("user.home", home.getAbsolutePath());

        testarComparacao();
        testarTransformacao();
        testarComposicao();
        testarDiagramaIncompleto();
        testarSinalNegativo();
        testarContextoDoLogger();
        testarCatalogoPadrao();
        testarVerificacaoPassoAPasso();

        System.out.println("Teste de montagem aprovado: " + verificacoes + " verificações.");
    }

    private static void testarComparacao() {
        SituacaoProblemaAditiva s = novaSituacao(
                "COP", TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
                "Paulo tem 6 bolas. José tem 8 bolas a mais que Paulo. Quantas bolas tem José?",
                "", "", "", "", "", "", "",
                "6", "14", "8", "positivo", "");
        verificarAtividade(s, new String[] {"6", "+8", "14"});
    }

    private static void testarTransformacao() {
        SituacaoProblemaAditiva s = novaSituacao(
                "TM", TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                "Paulo tinha 6 bolas. Ele ganhou 8 bolas. Com quantas bolas ficou?",
                "6", "8", "positivo", "14", "", "", "",
                "", "", "", "", "");
        verificarAtividade(s, new String[] {"6", "+8", "14"});
    }

    private static void testarComposicao() {
        SituacaoProblemaAditiva s = novaSituacao(
                "CM", TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                "Paulo tem 6 bolas azuis e José tem 8 bolas vermelhas. Quantas bolas eles têm ao todo?",
                "", "", "", "", "6", "8", "14",
                "", "", "", "", "");
        verificarAtividade(s, new String[] {"6", "8", "14"});
    }

    private static void testarDiagramaIncompleto() {
        SituacaoProblemaAditiva s = novaSituacao(
                "INCOMPLETA", TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
                "Paulo tem 6 bolas. José tem 8 bolas a mais que Paulo. Quantas bolas tem José?",
                "", "", "", "", "", "", "",
                "6", "?", "8", "positivo", "referendo");
        GeradorBlocosMontagem g = new GeradorBlocosMontagem(new Random(1));
        confirmar(!g.podeGerar(s), "uma interrogação não pode ser tratada como diagrama preenchido");
    }

    private static void testarSinalNegativo() {
        SituacaoProblemaAditiva s = novaSituacao(
                "COP_NEG", TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
                "Paulo tem 14 bolas. José tem 8 bolas a menos que Paulo. Quantas bolas tem José?",
                "", "", "", "", "", "", "",
                "14", "6", "8", "negativo", "");
        GeradorBlocosMontagem g = new GeradorBlocosMontagem(new Random(2));
        ConjuntoBlocosMontagem c = g.gerar(s);
        boolean encontrouRelacaoNegativa = false;
        for (BlocoTextoMontagem bloco : c.getBlocosDisponiveis()) {
            if (!bloco.isCorreto()) {
                String n = normalizar(bloco.getTexto());
                if (n.contains("a menos que") || n.contains("perdeu")) {
                    encontrouRelacaoNegativa = true;
                }
            }
        }
        confirmar(encontrouRelacaoNegativa, "os blocos próximos devem preservar o sinal negativo");
    }

    private static void verificarAtividade(SituacaoProblemaAditiva s, String[] valoresEsperados) {
        GeradorBlocosMontagem g = new GeradorBlocosMontagem(new Random(7));
        confirmar(g.podeGerar(s), "situação completa deve gerar atividade: " + s.getId());
        ConjuntoBlocosMontagem c = g.gerar(s);
        String[] valores = c.getValoresDiagrama();
        confirmar(valores.length == 3, "diagrama deve ter três valores");
        for (int i = 0; i < 3; i++) {
            confirmar(valoresEsperados[i].equals(valores[i]), "valor do diagrama divergente em " + s.getId());
        }

        int corretos = 0;
        int naoCompativeis = 0;
        Set<String> textos = new HashSet<String>();
        StringBuilder reconstruido = new StringBuilder();
        List<String> idsOrdenados = c.getIdsCorretosOrdenados();
        for (String id : idsOrdenados) {
            for (BlocoTextoMontagem bloco : c.getBlocosDisponiveis()) {
                if (id.equals(bloco.getId())) {
                    if (reconstruido.length() > 0) reconstruido.append(' ');
                    reconstruido.append(bloco.getTexto());
                }
            }
        }
        for (BlocoTextoMontagem bloco : c.getBlocosDisponiveis()) {
            confirmar(textos.add(normalizar(bloco.getTexto())), "não pode haver blocos textuais duplicados");
            if (bloco.isCorreto()) corretos++; else naoCompativeis++;
        }
        confirmar(corretos == idsOrdenados.size(), "quantidade de blocos corretos inconsistente");
        confirmar(naoCompativeis >= 3 && naoCompativeis <= 4,
                "a quantidade de blocos não compatíveis deve evitar trivialidade e excesso");
        confirmar(normalizar(reconstruido.toString()).equals(normalizar(s.getEnunciado())),
                "os blocos corretos devem reconstruir o enunciado curado");
        if (normalizar(s.getEnunciado()).contains("bolas")) {
            for (BlocoTextoMontagem bloco : c.getBlocosDisponiveis()) {
                confirmar(!normalizar(bloco.getTexto()).contains("quantos bolas"),
                        "a proximidade semântica não pode introduzir erro de concordância");
            }
        }
    }

    private static void testarContextoDoLogger() {
        LoggerInteracaoGerard logger = LoggerInteracaoGerard.getInstancia();
        logger.novoProblema("A", "Situação A", "A1", "GA", "pt-BR");
        String tentativaA = logger.getTentativaAtualId();
        String problemaA = logger.getProblemaAtual();
        LoggerInteracaoGerard.ContextoInteracao contextoA = logger.capturarContextoAtual();
        logger.novoProblema("B", "Situação B", "B1", "GB", "pt-BR");
        confirmar(!tentativaA.equals(logger.getTentativaAtualId()), "a nova tarefa deve criar outra tentativa");
        logger.restaurarContexto(contextoA);
        confirmar(tentativaA.equals(logger.getTentativaAtualId()), "a tentativa anterior deve ser restaurada");
        confirmar(problemaA.equals(logger.getProblemaAtual()), "o problema anterior deve ser restaurado");
    }


    private static void testarVerificacaoPassoAPasso() {
        SituacaoProblemaAditiva s = novaSituacao(
                "PASSO", TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                "Paulo tinha 6 bolas. Ele ganhou 8 bolas. Com quantas bolas ficou?",
                "6", "8", "positivo", "14", "", "", "",
                "", "", "", "", "");
        GeradorBlocosMontagem gerador = new GeradorBlocosMontagem(new Random(11));
        ConjuntoBlocosMontagem atividade = gerador.gerar(s);

        BlocoTextoMontagem primeiroCorreto = null;
        BlocoTextoMontagem segundoCorreto = null;
        BlocoTextoMontagem distrator = null;
        for (BlocoTextoMontagem bloco : atividade.getBlocosDisponiveis()) {
            if (bloco.isCorreto()) {
                if (primeiroCorreto == null) primeiroCorreto = bloco;
                else if (segundoCorreto == null) segundoCorreto = bloco;
            } else if (distrator == null) {
                distrator = bloco;
            }
        }
        confirmar(primeiroCorreto != null && segundoCorreto != null && distrator != null,
                "atividade deve oferecer corretos e distrator para a verificação passo a passo");

        AvaliadorMontagemPassoAPasso avaliador = new AvaliadorMontagemPassoAPasso();
        java.util.List<BlocoTextoMontagem> montados = new java.util.ArrayList<BlocoTextoMontagem>();
        montados.add(primeiroCorreto);
        java.util.List<StatusVerificacaoBlocoMontagem> estados =
                avaliador.avaliar(montados, atividade.getIdsCorretosOrdenados());
        confirmar(estados.size() == 1 && estados.get(0) == StatusVerificacaoBlocoMontagem.CORRETO,
                "o primeiro bloco correto deve ser marcado em azul");

        montados.clear();
        montados.add(distrator);
        estados = avaliador.avaliar(montados, atividade.getIdsCorretosOrdenados());
        confirmar(estados.size() == 1 && estados.get(0) == StatusVerificacaoBlocoMontagem.INCORRETO,
                "um distrator inserido deve ser marcado em vermelho suave");

        montados.clear();
        montados.add(segundoCorreto);
        estados = avaliador.avaliar(montados, atividade.getIdsCorretosOrdenados());
        confirmar(estados.size() == 1 && estados.get(0) == StatusVerificacaoBlocoMontagem.INCORRETO,
                "um bloco correto fora de ordem deve ser marcado como incorreto");
    }

    private static void testarCatalogoPadrao() {
        GeradorBlocosMontagem gerador = new GeradorBlocosMontagem(new Random(3));
        for (IdiomaInterface idioma : new IdiomaInterface[] {
                IdiomaInterface.PORTUGUES, IdiomaInterface.INGLES, IdiomaInterface.FRANCES}) {
            List<SituacaoProblemaAditiva> atividades = CatalogoAtividadesMontagemPadrao.listar(idioma);
            confirmar(atividades.size() == 3, "o catálogo deve oferecer três categorias por idioma");
            Set<TipoSituacaoAditiva> tipos = new HashSet<TipoSituacaoAditiva>();
            for (SituacaoProblemaAditiva atividade : atividades) {
                confirmar(atividade.getIdioma() == idioma, "idioma divergente no catálogo padrão");
                confirmar(gerador.podeGerar(atividade), "atividade padrão deve possuir diagrama completamente preenchido");
                tipos.add(atividade.getTipo());
            }
            confirmar(tipos.contains(TipoSituacaoAditiva.COMPOSICAO_MEDIDAS), "catálogo sem composição");
            confirmar(tipos.contains(TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS), "catálogo sem transformação");
            confirmar(tipos.contains(TipoSituacaoAditiva.COMPARACAO_MEDIDAS), "catálogo sem comparação");
        }
    }

    private static SituacaoProblemaAditiva novaSituacao(
            String id, TipoSituacaoAditiva tipo, String enunciado,
            String estadoInicial, String transformacao, String sinalTransformacao, String estadoFinal,
            String quantidade1, String quantidade2, String resultado,
            String referido, String referendo, String valorRelativo, String sinalValorRelativo,
            String termoDesconhecido) {
        return new SituacaoProblemaAditiva(
                id, true, tipo, IdiomaInterface.PORTUGUES, enunciado, "Bolas", "teste", "",
                estadoInicial, transformacao, sinalTransformacao, estadoFinal,
                quantidade1, quantidade2, resultado,
                referido, referendo, valorRelativo, sinalValorRelativo,
                termoDesconhecido, tipo.name(), "");
    }

    private static String normalizar(String texto) {
        String n = Normalizer.normalize(texto == null ? "" : texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "").toLowerCase();
        return n.replaceAll("[^a-z0-9?]+", " ").trim().replaceAll("\\s+", " ");
    }

    private static void confirmar(boolean condicao, String mensagem) {
        verificacoes++;
        if (!condicao) throw new AssertionError(mensagem);
    }
}
