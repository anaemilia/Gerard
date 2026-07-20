package gerard.interpretacao.regras;

import gerard.interpretacao.modelo.CategoriaProblema;
import gerard.interpretacao.modelo.IdiomaProblema;
import gerard.interpretacao.modelo.PistaLinguistica;
import java.util.ArrayList;
import java.util.List;

abstract class RegrasBase implements RegraLinguistica {
    private final IdiomaProblema idioma;
    private final List<EntradaRegra> regras = new ArrayList<EntradaRegra>();
    private final List<String> indicadoresIdioma = new ArrayList<String>();

    RegrasBase(IdiomaProblema idioma) {
        this.idioma = idioma;
        configurar();
    }

    public IdiomaProblema getIdioma() {
        return idioma;
    }

    protected abstract void configurar();

    protected void indicador(String expressao) {
        indicadoresIdioma.add(expressao);
    }

    protected void regra(String expressao, CategoriaProblema categoria, int peso) {
        regras.add(new EntradaRegra(expressao, categoria, peso));
    }

    public int pontuarIdioma(String textoNormalizado) {
        int pontos = 0;

        for (String indicador : indicadoresIdioma) {
            if (contemExpressao(textoNormalizado, indicador)) {
                pontos++;
            }
        }

        return pontos;
    }

    public List<PistaLinguistica> avaliar(String textoOriginal, String textoNormalizado) {
        List<PistaLinguistica> pistas = new ArrayList<PistaLinguistica>();

        for (EntradaRegra regra : regras) {
            if (contemExpressao(textoNormalizado, regra.expressao)) {
                pistas.add(new PistaLinguistica(idioma, regra.categoria, regra.expressao, regra.peso));
            }
        }

        return pistas;
    }

    private boolean contemExpressao(String textoNormalizado, String expressao) {
        String alvo = " " + textoNormalizado + " ";
        String exp = " " + expressao.toLowerCase() + " ";
        return alvo.contains(exp);
    }
}
