package gerard.semantica.numero;

import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.semantica.quantidade.ServicoQuantidadeContextual;

/**
 * Fachada de compatibilidade para o estado inteiro consolidado.
 *
 * A interpretação por grandeza foi movida para
 * {@code gerard.semantica.quantidade}. O método antigo permanece para não
 * quebrar integrações existentes e usa o comportamento legado sem situação.
 */
public final class ConversorTextoParaInteiroSemantico {
    private final ServicoQuantidadeContextual servico =
            new ServicoQuantidadeContextual();

    public Integer converter(String texto) {
        return servico.converterParaInteiroLegado(texto, null);
    }

    public Integer converter(String texto, SituacaoProblemaAditiva situacao) {
        return servico.converterParaInteiroLegado(texto, situacao);
    }
}
