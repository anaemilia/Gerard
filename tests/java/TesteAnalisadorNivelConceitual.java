package gerard.agente.modelador;

import gerard.agente.modelousuario.NivelConceitualExplicacao;

/**
 * Cobre a heurística de nível conceitual (AnalisadorNivelConceitual) com
 * frases representativas de cada nível da escada de Vergnaud (1998).
 */
public final class TesteAnalisadorNivelConceitual {
    private static int verificacoes;

    public static void main(String[] args) {
        AnalisadorNivelConceitual analisador = new AnalisadorNivelConceitual();

        confirmar(analisador.classificar(null) == NivelConceitualExplicacao.AUSENTE,
                "texto nulo deve ser AUSENTE");
        confirmar(analisador.classificar("") == NivelConceitualExplicacao.AUSENTE,
                "texto vazio deve ser AUSENTE");
        confirmar(analisador.classificar("6") == NivelConceitualExplicacao.AUSENTE,
                "texto curto demais deve ser AUSENTE");

        confirmar(analisador.classificar("o seis é a parte 1") == NivelConceitualExplicacao.ADJETIVO,
                "um papel ligado a um elemento deve ser ADJETIVO");

        confirmar(analisador.classificar("6 é a parte 1 porque 8 é a parte 2")
                        == NivelConceitualExplicacao.RELACIONAL,
                "dois papéis na mesma frase deve ser RELACIONAL");
        confirmar(analisador.classificar("posicionei o 6 depois do estado inicial")
                        == NivelConceitualExplicacao.RELACIONAL,
                "conector relacional deve ser RELACIONAL");

        confirmar(analisador.classificar("a transformação representa o ganho")
                        == NivelConceitualExplicacao.SUBSTANTIVADO,
                "papel como sujeito de verbo de nível 3 deve ser SUBSTANTIVADO");
        confirmar(analisador.classificar("o valor relativo indica a diferença entre os dois")
                        == NivelConceitualExplicacao.SUBSTANTIVADO,
                "outro caso de papel-sujeito com verbo de propriedade deve ser SUBSTANTIVADO");

        confirmar(analisador.classificar("isso é sempre assim quando o valor aumenta")
                        == NivelConceitualExplicacao.CLASSIFICATORIO,
                "marcador de generalização deve ser CLASSIFICATORIO");
        confirmar(analisador.classificar("essa situação é uma composição de medidas")
                        == NivelConceitualExplicacao.CLASSIFICATORIO,
                "citar a categoria como classe deve ser CLASSIFICATORIO");

        System.out.println("Teste do analisador de nível conceitual aprovado: "
                + verificacoes + " verificações.");
    }

    private static void confirmar(boolean condicao, String mensagem) {
        verificacoes++;
        if (!condicao) throw new AssertionError(mensagem);
    }
}
