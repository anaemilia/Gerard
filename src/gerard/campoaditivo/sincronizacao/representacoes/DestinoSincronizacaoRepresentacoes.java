package gerard.campoaditivo.sincronizacao.representacoes;

import gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado;

/**
 * Portas de aplicação de um estado semântico nas representações da tela.
 * O contrato não conhece componentes visuais nem decide valores.
 */
public interface DestinoSincronizacaoRepresentacoes {

    void aplicarNoVergnaud(EstadoSemanticoCompartilhado.Snapshot snapshot);

    void aplicarNoTexto(EstadoSemanticoCompartilhado.Snapshot snapshot);

    void reconstruirRepresentacaoComplementar();

    void aplicarNosEixos(EstadoSemanticoCompartilhado.Snapshot snapshot);
}
