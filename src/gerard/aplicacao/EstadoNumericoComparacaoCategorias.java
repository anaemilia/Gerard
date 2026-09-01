package gerard.aplicacao;

import gerard.dominio.campoaditivo.RelacaoEstruturalComposicao;

import java.util.ArrayList;
import java.util.List;

/**
 * Estado numérico comum usado para comparar representações de categorias
 * aditivas equivalentes. Não conhece Swing, geometria nem índices visuais.
 */
public final class EstadoNumericoComparacaoCategorias {
    public enum Papel {
        PRIMEIRA_PARCELA,
        SEGUNDA_PARCELA,
        TOTAL
    }

    private final RelacaoEstruturalComposicao relacao =
            RelacaoEstruturalComposicao.composicaoDeMedidas();
    private final List<Runnable> ouvintes = new ArrayList<Runnable>();
    private int primeiraParcela;
    private int segundaParcela;

    public EstadoNumericoComparacaoCategorias(int primeiraParcela,
            int segundaParcela) {
        this.primeiraParcela = normalizar(primeiraParcela);
        this.segundaParcela = normalizar(segundaParcela);
    }

    public int getPrimeiraParcela() {
        return primeiraParcela;
    }

    public int getSegundaParcela() {
        return segundaParcela;
    }

    public int getTotal() {
        return relacao.calcularTodo(primeiraParcela, segundaParcela);
    }

    public void definirParcelas(int primeira, int segunda) {
        primeiraParcela = normalizar(primeira);
        segundaParcela = normalizar(segunda);
        notificar();
    }

    public void definir(Papel papel, int valor) {
        if (papel == null) {
            throw new IllegalArgumentException("papel obrigatorio");
        }
        int valorNormalizado = normalizar(valor);
        switch (papel) {
            case PRIMEIRA_PARCELA:
                primeiraParcela = valorNormalizado;
                break;
            case SEGUNDA_PARCELA:
                segundaParcela = valorNormalizado;
                break;
            case TOTAL:
                segundaParcela = Math.max(0, valorNormalizado - primeiraParcela);
                break;
            default:
                throw new IllegalArgumentException("papel nao suportado: " + papel);
        }
        notificar();
    }

    public void adicionarOuvinte(Runnable ouvinte) {
        if (ouvinte != null) {
            ouvintes.add(ouvinte);
        }
    }

    private void notificar() {
        for (Runnable ouvinte : new ArrayList<Runnable>(ouvintes)) {
            ouvinte.run();
        }
    }

    private static int normalizar(int valor) {
        return Math.max(0, valor);
    }
}
