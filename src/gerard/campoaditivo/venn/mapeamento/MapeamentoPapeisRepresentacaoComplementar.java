package gerard.campoaditivo.venn.mapeamento;

/**
 * Contrato entre a ordem visual dos agrupamentos do diagrama complementar e
 * a ordem semântica mantida pelo estado compartilhado.
 *
 * A ordem semântica canônica é A, relação e C. Algumas representações, como
 * a comparação de medidas, usam uma ordem visual diferente para favorecer a
 * leitura: referido, referendo e valor relativo.
 */
public interface MapeamentoPapeisRepresentacaoComplementar {
    int paraIndiceSemantico(int indiceVisual);
    int paraIndiceVisual(int indiceSemantico);
}
