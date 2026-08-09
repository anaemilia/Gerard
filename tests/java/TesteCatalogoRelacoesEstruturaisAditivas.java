package gerard.campoaditivo.sincronizacao;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.dominio.campoaditivo.ResultadoCalculo;
import gerard.semantica.numero.DominioNumerico;
import gerard.semantica.numero.NumeroInteiro;
import gerard.semantica.numero.NumeroNatural;
import gerard.semantica.numero.ValorDesconhecido;
import gerard.semantica.numero.ValorNumerico;

/** Protege a associação entre as seis categorias, seus papéis e relações. */
public final class TesteCatalogoRelacoesEstruturaisAditivas {
    private static int verificacoes;

    public static void main(String[] args) {
        CatalogoRelacoesEstruturaisAditivas catalogo =
                new CatalogoRelacoesEstruturaisAditivas();

        for (TipoSituacaoAditiva tipo : TipoSituacaoAditiva.values()) {
            testarValorAusente(catalogo, tipo);
            testarRecalculoConsistencia(catalogo, tipo);
        }

        confirmar(TipoSituacaoAditiva.values().length == 6,
                "o catálogo deve cobrir exatamente as seis categorias canônicas");
        System.out.println("Teste do catálogo de relações estruturais aprovado: "
                + verificacoes + " verificações.");
    }

    private static void testarValorAusente(
            CatalogoRelacoesEstruturaisAditivas catalogo,
            TipoSituacaoAditiva tipo) {
        CatalogoRelacoesEstruturaisAditivas.RelacaoContextualizada contexto =
                catalogo.criar(tipo, valores(tipo, 8, 6, null));
        confirmar(contexto != null, tipo + " deve possuir relação estrutural");

        ResultadoCalculo resultado = contexto.calcularValorAusente();
        confirmar(resultado != null && resultado.temValorCalculavel(),
                tipo + " deve calcular o terceiro papel ausente");
        confirmar(resultado.getValorCalculado().valorOuNull().intValue() == 14,
                tipo + " deve calcular 8 + 6 = 14");
        confirmar(contexto.indiceDoPapel(resultado.getPapelCalculado()) == 2,
                tipo + " deve associar o resultado ao terceiro papel");
    }

    private static void testarRecalculoConsistencia(
            CatalogoRelacoesEstruturaisAditivas catalogo,
            TipoSituacaoAditiva tipo) {
        CatalogoRelacoesEstruturaisAditivas.RelacaoContextualizada contexto =
                catalogo.criar(tipo, valores(tipo, 10, 6, 14));
        ResultadoCalculo resultado = contexto.recalcularParaConsistencia(0);

        confirmar(resultado != null && resultado.temValorCalculavel(),
                tipo + " deve recalcular após alteração do primeiro papel");
        confirmar(resultado.getValorCalculado().valorOuNull().intValue() == 16,
                tipo + " deve recalcular 10 + 6 = 16");
        confirmar(contexto.indiceDoPapel(resultado.getPapelCalculado()) == 2,
                tipo + " deve recalcular o terceiro papel");
    }

    private static ValorNumerico[] valores(TipoSituacaoAditiva tipo,
            int primeiro, int segundo, Integer terceiro) {
        boolean aceitaNaturaisNasExtremidades =
                tipo == TipoSituacaoAditiva.COMPOSICAO_MEDIDAS
                || tipo == TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS
                || tipo == TipoSituacaoAditiva.COMPARACAO_MEDIDAS;
        DominioNumerico dominioExtremidades = aceitaNaturaisNasExtremidades
                ? DominioNumerico.NATURAIS : DominioNumerico.INTEIROS;
        DominioNumerico dominioCentral =
                tipo == TipoSituacaoAditiva.COMPOSICAO_MEDIDAS
                ? DominioNumerico.NATURAIS : DominioNumerico.INTEIROS;

        return new ValorNumerico[] {
            conhecido(dominioExtremidades, primeiro),
            conhecido(dominioCentral, segundo),
            terceiro == null
                    ? new ValorDesconhecido(dominioExtremidades)
                    : conhecido(dominioExtremidades, terceiro.intValue())
        };
    }

    private static ValorNumerico conhecido(DominioNumerico dominio, int valor) {
        return dominio == DominioNumerico.NATURAIS
                ? new NumeroNatural(valor) : new NumeroInteiro(valor);
    }

    private static void confirmar(boolean condicao, String mensagem) {
        verificacoes++;
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
