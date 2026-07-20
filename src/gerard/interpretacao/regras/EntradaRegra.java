package gerard.interpretacao.regras;

import gerard.interpretacao.modelo.CategoriaProblema;

class EntradaRegra {
    final String expressao;
    final CategoriaProblema categoria;
    final int peso;

    EntradaRegra(String expressao, CategoriaProblema categoria, int peso) {
        this.expressao = expressao;
        this.categoria = categoria;
        this.peso = peso;
    }
}
