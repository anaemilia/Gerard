package gerard.campoaditivo.sincronizacao.texto;

import gerard.campoaditivo.semantica.CatalogoPapeisSemanticosAditivos;
import gerard.interpretacao.modelo.ResolvedorPapelInterpretado;
import gerard.interpretacao.modelo.ResultadoInterpretacao;
import gerard.interpretacao.simbolo.SimboloDesconhecido;

/**
 * Resolve o papel transportado por uma projeção textual sem conhecer Swing,
 * geometria ou o componente concreto que materializa o texto.
 *
 * <p>A ordem dos fallbacks preserva o protocolo legado: vínculo nominal,
 * incógnita curada, correspondência por valor e, somente quando informado
 * pelo adaptador, índice posicional de compatibilidade.</p>
 */
public final class ResolvedorPapelElementoTexto {

    private static final String PAPEL_GENERICO = "papel.valor";

    private final CatalogoPapeisSemanticosAditivos catalogoPapeis;

    public ResolvedorPapelElementoTexto(
            CatalogoPapeisSemanticosAditivos catalogoPapeis) {
        if (catalogoPapeis == null) {
            throw new IllegalArgumentException("catalogoPapeis obrigatorio");
        }
        this.catalogoPapeis = catalogoPapeis;
    }

    public String obterChavePapelExataDoItem(
            ResultadoInterpretacao interpretacao,
            ElementoSemanticoTexto item,
            String valorAtual) {
        if (item == null) {
            return PAPEL_GENERICO;
        }

        String chaveDeclarada = item.getChavePapelSemantico();
        boolean incognita = SimboloDesconhecido.eh(
                item.getValorSemanticoOriginal())
                || SimboloDesconhecido.eh(valorAtual);
        if (incognita) {
            String chaveIncognita =
                    ResolvedorPapelInterpretado
                            .aplicarFallbackCuradoItemDesconhecido(
                                    interpretacao,
                                    chaveDeclarada == null
                                            ? PAPEL_GENERICO
                                            : chaveDeclarada);
            if (catalogoPapeis.chavePapelEspecifica(chaveIncognita)) {
                return chaveIncognita;
            }
            String chavePorInterrogacao =
                    ResolvedorPapelInterpretado
                            .obterChavePapelExataPorValor(
                                    interpretacao, "?");
            if (catalogoPapeis.chavePapelEspecifica(
                    chavePorInterrogacao)) {
                return chavePorInterrogacao;
            }
        }

        if (catalogoPapeis.chavePapelEspecifica(chaveDeclarada)) {
            return chaveDeclarada;
        }

        String chavePorValor =
                ResolvedorPapelInterpretado.obterChavePapelExataPorValor(
                        interpretacao, item.getValorSemanticoOriginal());
        if (catalogoPapeis.chavePapelEspecifica(chavePorValor)) {
            return chavePorValor;
        }

        return chaveDeclarada != null ? chaveDeclarada : PAPEL_GENERICO;
    }

    public String obterChavePapelCanonicoDoItem(
            ResultadoInterpretacao interpretacao,
            ElementoSemanticoTexto item) {
        if (item == null) {
            return PAPEL_GENERICO;
        }
        String chavePapel = item.getChavePapelSemantico();
        if (chavePapel == null || chavePapel.length() == 0) {
            chavePapel =
                    ResolvedorPapelInterpretado
                            .obterChavePapelCanonicoPorValor(
                                    interpretacao,
                                    item.getValorSemanticoOriginal());
        } else {
            chavePapel =
                    ResolvedorPapelInterpretado.converterParaPapelCanonico(
                            chavePapel);
        }
        return ResolvedorPapelInterpretado
                .aplicarFallbackCuradoItemDesconhecido(
                        interpretacao, chavePapel);
    }

    public String obterChavePapelExataDoElemento(
            ResultadoInterpretacao interpretacao,
            ElementoSemanticoTexto elemento,
            boolean interrogacaoDoTexto,
            int indiceFallback) {
        if (elemento != null && elemento.possuiVinculoSemantico()
                && elemento.getChavePapelSemantico() != null) {
            return elemento.getChavePapelSemantico();
        }
        if (interrogacaoDoTexto) {
            return ResolvedorPapelInterpretado.obterChavePapelExataPorValor(
                    interpretacao, "?");
        }
        return ResolvedorPapelInterpretado.obterChavePapelExataPorIndice(
                interpretacao, indiceFallback);
    }

    public String obterChavePapelCanonicoDoElemento(
            ResultadoInterpretacao interpretacao,
            ElementoSemanticoTexto elemento,
            boolean interrogacaoDoTexto,
            int indiceFallback) {
        String chavePapel;
        if (elemento != null && elemento.possuiVinculoSemantico()
                && elemento.getChavePapelSemantico() != null) {
            chavePapel = ResolvedorPapelInterpretado.converterParaPapelCanonico(
                    elemento.getChavePapelSemantico());
        } else if (interrogacaoDoTexto) {
            chavePapel = ResolvedorPapelInterpretado.converterParaPapelCanonico(
                    ResolvedorPapelInterpretado
                            .obterChavePapelExataPorValor(
                                    interpretacao, "?"));
        } else {
            chavePapel = ResolvedorPapelInterpretado
                    .obterChavePapelCanonicoPorIndice(
                            interpretacao, indiceFallback);
        }
        return ResolvedorPapelInterpretado
                .aplicarFallbackCuradoItemDesconhecido(
                        interpretacao, chavePapel);
    }
}
