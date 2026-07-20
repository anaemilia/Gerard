package gerard.semantica;

import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado;
import gerard.idioma.IdiomaInterface;
import gerard.semantica.categoria.CatalogoEsquemasCategoriasAditivas;
import gerard.semantica.categoria.CategoriaComposta;
import gerard.semantica.categoria.EsquemaCategoriaAditiva;
import gerard.semantica.contexto.ContextoSituacao;
import gerard.semantica.contexto.ReferenteContextual;
import gerard.semantica.contexto.TipoReferenteContextual;
import gerard.semantica.contexto.UnidadeMedida;
import gerard.semantica.elemento.ElementoNumerico;
import gerard.semantica.entidade.Personagem;
import gerard.semantica.numero.DominioNumerico;
import gerard.semantica.numero.FabricaValoresNumericos;
import gerard.semantica.numero.NumeroInteiro;
import gerard.semantica.numero.NumeroNatural;
import gerard.semantica.numero.ValorDesconhecido;
import gerard.semantica.papel.CatalogoPapeisSemanticos;
import gerard.semantica.papel.PapelQuantitativo;
import gerard.semantica.pista.LexicoPistasAditivas;
import gerard.semantica.pista.OcorrenciaPista;
import gerard.semantica.pista.TipoPistaLinguistica;
import gerard.semantica.situacao.FabricaInstanciaSemanticaAditiva;
import gerard.semantica.situacao.InstanciaSituacaoAditiva;
import java.util.Arrays;
import java.util.List;

public final class TesteModeloSemanticoExplicito {
    private static int verificacoes;

    public static void main(String[] args) {
        testarUniversosNumericos();
        testarPapeisEDominios();
        testarContextoPersonagensEPistas();
        testarCategoriasComoComposite();
        testarEstadoCompartilhadoComObjetosNumericos();
        testarAdaptadorDaSituacaoAtual();
        System.out.println("Teste do modelo semântico explícito aprovado: "
                + verificacoes + " verificações.");
    }

    private static void testarUniversosNumericos() {
        NumeroNatural zero = new NumeroNatural(0);
        NumeroNatural cinco = new NumeroNatural(5);
        NumeroInteiro negativo = new NumeroInteiro(-4);
        confirmar(zero.getDominio() == DominioNumerico.NATURAIS,
                "zero deve pertencer aos naturais N0");
        confirmar(cinco.valorOuNull().intValue() == 5,
                "natural deve preservar magnitude");
        confirmar(negativo.getDominio() == DominioNumerico.INTEIROS
                        && "-4".equals(negativo.formatar(true)),
                "inteiro deve preservar sinal negativo");
        confirmar("+4".equals(new NumeroInteiro(4).formatar(true)),
                "sinal positivo explícito é forma de apresentação do inteiro");
        boolean rejeitou = false;
        try {
            new NumeroNatural(-1);
        } catch (IllegalArgumentException esperado) {
            rejeitou = true;
        }
        confirmar(rejeitou, "natural negativo deve ser rejeitado no próprio número");
        ValorDesconhecido desconhecido = new ValorDesconhecido(DominioNumerico.INTEIROS);
        confirmar(!desconhecido.ehConhecido()
                        && desconhecido.getDominio() == DominioNumerico.INTEIROS,
                "incógnita deve preservar o universo esperado");
    }

    private static void testarPapeisEDominios() {
        CatalogoPapeisSemanticos catalogo = new CatalogoPapeisSemanticos();
        confirmar(catalogo.dominioDoPapel("papel.transformacao")
                        == DominioNumerico.INTEIROS,
                "transformação pertence aos inteiros");
        confirmar(catalogo.dominioDoPapel("papel.diferenca")
                        == DominioNumerico.INTEIROS,
                "valor relativo pertence aos inteiros");
        confirmar(catalogo.dominioDoPapel("papel.estadoInicial")
                        == DominioNumerico.NATURAIS,
                "estado inicial pertence aos naturais");
        confirmar(catalogo.dominioDoPapel("papel.referendo")
                        == DominioNumerico.NATURAIS,
                "referendo pertence aos naturais");

        PapelQuantitativo estado = catalogo.obter("papel.estadoFinal");
        PapelQuantitativo transformacao = catalogo.obter("papel.transformacao");
        new ElementoNumerico("n1", estado, new NumeroNatural(3), "3");
        new ElementoNumerico("n2", transformacao, new NumeroInteiro(-2), "-2");
        boolean rejeitou = false;
        try {
            new ElementoNumerico("erro", estado, new NumeroInteiro(-2), "-2");
        } catch (IllegalArgumentException esperado) {
            rejeitou = true;
        }
        confirmar(rejeitou, "elemento numérico deve bloquear universo incompatível");
    }

    private static void testarContextoPersonagensEPistas() {
        UnidadeMedida unidade = new UnidadeMedida("carrinho", "carrinhos", "");
        ReferenteContextual carrinhos = new ReferenteContextual(
                "objeto_1", "carrinhos", TipoReferenteContextual.OBJETO_CONTAVEL,
                unidade);
        ContextoSituacao contexto = new ContextoSituacao(
                "Brincadeira", Arrays.asList(carrinhos));
        Personagem ana = new Personagem("personagem_1", "Ana");
        confirmar("carrinhos".equals(unidade.formatar(2))
                        && contexto.getReferentes().size() == 1,
                "contexto deve representar referente e unidade sem criar classe por objeto");
        confirmar("Ana".equals(ana.getNome()),
                "nome deve ser dado de Personagem, não uma subclasse");

        LexicoPistasAditivas lexico = new LexicoPistasAditivas();
        List<OcorrenciaPista> pistas = lexico.localizar(
                "Ontem Ana tinha 8 bolas. Hoje perdeu 3.", "pt");
        boolean encontrouTemporal = false;
        boolean encontrouPerda = false;
        for (OcorrenciaPista pista : pistas) {
            if (pista.getPista().getTipo() == TipoPistaLinguistica.TEMPORAL_INICIAL) {
                encontrouTemporal = true;
            }
            if (pista.getPista().getTipo() == TipoPistaLinguistica.TRANSFORMACAO_NEGATIVA) {
                encontrouPerda = true;
            }
        }
        confirmar(encontrouTemporal && encontrouPerda,
                "palavras-pista devem ser evidências localizadas no texto");
    }

    private static void testarCategoriasComoComposite() {
        CatalogoEsquemasCategoriasAditivas catalogo =
                new CatalogoEsquemasCategoriasAditivas();
        EsquemaCategoriaAditiva transformacao = catalogo.obter(
                TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS);
        confirmar(transformacao.obterPapeis().size() == 3,
                "transformação simples deve possuir três papéis");
        confirmar(transformacao.obterDominioCompartilhado(0) == DominioNumerico.NATURAIS
                        && transformacao.obterDominioCompartilhado(1) == DominioNumerico.INTEIROS
                        && transformacao.obterDominioCompartilhado(2) == DominioNumerico.NATURAIS,
                "esquema de transformação deve ser N0 + Z = N0");

        EsquemaCategoriaAditiva composta = catalogo.obter(
                TipoSituacaoAditiva.TRANSFORMACAO_COMPOSTA_DOIS_PASSOS);
        confirmar(composta.getRaiz() instanceof CategoriaComposta,
                "transformação de dois passos deve usar Composite");
        confirmar(composta.obterPapeis().size() == 6,
                "composite deve manter os papéis dos dois passos");
    }

    private static void testarEstadoCompartilhadoComObjetosNumericos() {
        EstadoSemanticoCompartilhado estado = new EstadoSemanticoCompartilhado();
        EstadoSemanticoCompartilhado.Snapshot transformacao = estado.atualizar(
                TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                new Integer[] { Integer.valueOf(9), Integer.valueOf(-4), null },
                new boolean[] { true, true, false }, 1,
                EstadoSemanticoCompartilhado.Origem.PROTOCOLO);
        confirmar(transformacao.getDominio(0) == DominioNumerico.NATURAIS
                        && transformacao.getDominio(1) == DominioNumerico.INTEIROS,
                "snapshot deve transportar os universos dos valores");
        confirmar(transformacao.valorOuZero(2) == 5,
                "estado final deve ser derivado mantendo a relação aditiva");

        EstadoSemanticoCompartilhado.Snapshot invalido = estado.atualizar(
                TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                new Integer[] { Integer.valueOf(-2), Integer.valueOf(3), null },
                new boolean[] { true, true, false }, 0,
                EstadoSemanticoCompartilhado.Origem.PROTOCOLO);
        confirmar(!invalido.isConhecido(0),
                "medida natural negativa deve ser recusada antes da representação");
    }

    private static void testarAdaptadorDaSituacaoAtual() {
        SituacaoProblemaAditiva situacao = new SituacaoProblemaAditiva(
                TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                IdiomaInterface.PORTUGUES,
                "Ontem Ana tinha bolas. Hoje perdeu algumas bolas.",
                "Bolas", "teste");
        InstanciaSituacaoAditiva instancia =
                new FabricaInstanciaSemanticaAditiva().criar(situacao);
        confirmar(instancia != null
                        && instancia.getEsquema().getTipo()
                                == TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                "dados atuais devem ser adaptáveis ao novo modelo semântico");
        confirmar(!instancia.getPistas().isEmpty(),
                "adaptador deve registrar pistas como evidências, sem classificar só pela palavra");
    }

    private static void confirmar(boolean condicao, String mensagem) {
        verificacoes++;
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
