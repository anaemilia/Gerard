import gerard.interpretacao.modelo.SegmentadorTextoSemantico;
import gerard.interpretacao.modelo.SegmentoTextoSemantico;
import gerard.interpretacao.modelo.TipoSegmentoNarrativo;
import java.util.List;

public final class TesteEditorNarrativoPalavras {
    public static void main(String[] args) {
        List<SegmentoTextoSemantico> partes = SegmentadorTextoSemantico.segmentar(
                "Antes Paulo tinha bolas e depois ganhou outras.", null);
        exigir(tipo(partes, "Antes") == TipoSegmentoNarrativo.CANDIDATO_ORGANIZADOR_INFORMACAO,
                "antes deve ser apenas candidato a organizador");
        exigir(tipo(partes, "depois") == TipoSegmentoNarrativo.CANDIDATO_ORGANIZADOR_INFORMACAO,
                "depois deve ser apenas candidato a organizador");
        exigir(!obter(partes, "e").isManipulavelNaNarrativa(),
                "stopword isolada deve ser desconsiderada");
        exigir(obter(partes, "Paulo").isManipulavelNaNarrativa(),
                "palavra comum de conteúdo deve permanecer manipulável");
        exigir(tipo(partes, "tinha") == TipoSegmentoNarrativo.COMUM,
                "verbo de estado não pode ser inventado como organizador");
        System.out.println("APROVADO: palavras manipuláveis, stopwords e candidatos respeitam a fonte teórica.");
    }

    private static TipoSegmentoNarrativo tipo(List<SegmentoTextoSemantico> partes, String valor) {
        return obter(partes, valor).getTipoNarrativo();
    }

    private static SegmentoTextoSemantico obter(List<SegmentoTextoSemantico> partes, String valor) {
        for (SegmentoTextoSemantico parte : partes) if (valor.equals(parte.getValor())) return parte;
        throw new AssertionError("segmento ausente: " + valor);
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }
}
