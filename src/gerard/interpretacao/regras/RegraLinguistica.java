package gerard.interpretacao.regras;

import gerard.interpretacao.modelo.IdiomaProblema;
import gerard.interpretacao.modelo.PistaLinguistica;
import java.util.List;

public interface RegraLinguistica {
    IdiomaProblema getIdioma();
    int pontuarIdioma(String textoNormalizado);
    List<PistaLinguistica> avaliar(String textoOriginal, String textoNormalizado);
}
