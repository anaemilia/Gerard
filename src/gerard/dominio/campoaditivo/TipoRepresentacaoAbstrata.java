package gerard.dominio.campoaditivo;

/**
 * Forma conceitual abstrata de representação de um papel quantitativo —
 * pertence ao domínio, não à camada de diagrama/renderização. Não deve ser
 * confundida com gerard.campoaditivo.diagrama.modelo.TipoFiguraDiagrama:
 * mesmo esta última já sendo livre de AWT/Swing, ela pertence
 * conceitualmente à camada do diagrama, não ao domínio (ver relatório
 * técnico, seção "Revisão da dependência de TipoFiguraDiagrama").
 *
 * A camada de interface/renderização é responsável por mapear estes
 * valores para TipoFiguraDiagrama ou qualquer outra tecnologia visual —
 * esse mapeamento não existe ainda neste piloto isolado (não há
 * integração com a aplicação principal nesta etapa).
 */
public enum TipoRepresentacaoAbstrata {
    FIGURA_RETANGULAR,
    FIGURA_ELIPTICA,
    FIGURA_RETANGULAR_ARREDONDADA
}
