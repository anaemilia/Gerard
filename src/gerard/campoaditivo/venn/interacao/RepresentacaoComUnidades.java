package gerard.campoaditivo.venn.interacao;

import gerard.campoaditivo.diagrama.elementos.CirculoVenn;

/**
 * Contrato-base para representações que materializam uma quantidade por
 * unidades visuais. Não define mutação: apenas expõe estado e identidade.
 */
public interface RepresentacaoComUnidades {
    CirculoVenn obterAgrupamento();
    int obterQuantidadeAtual();
    String obterPapelSemantico();
}
