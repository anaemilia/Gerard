package gerard.interpretacao.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Tokeniza um enunciado em segmentos vinculados a papéis semânticos —
 * portação fiel de {@code inicializarElementosTexto}/
 * {@code vincularPapeisSemanticosAosElementosTexto}/{@code obterChavePapelDoNumero}
 * em {@code Main.java} (linhas ~6501-6566, ~6770-6785), sem nenhuma
 * dependência de Swing: usa só {@link ResultadoInterpretacao} (já disponível
 * no servidor via {@code ContextoCarregamentoAtividade.getInterpretacao()})
 * e {@link ResolvedorPapelInterpretado} (já extraído, portátil desde
 * 2026-09-01). Extraído em 2026-09-04 para dar rota de API ao "elementos do
 * enunciado marcados e arrastáveis após a categoria ser aceita" — recurso
 * que já existe no desktop e não tinha nenhum equivalente na web.
 */
public final class SegmentadorTextoSemantico {

    private SegmentadorTextoSemantico() {
    }

    public static List<SegmentoTextoSemantico> segmentar(
            String enunciado, ResultadoInterpretacao interpretacao) {
        List<SegmentoTextoSemantico> elementos = new ArrayList<SegmentoTextoSemantico>();
        String texto = enunciado == null ? "" : enunciado;
        Matcher matcher = Pattern.compile("\\S+").matcher(texto);
        while (matcher.find()) {
            String palavra = matcher.group();
            int inicioPalavra = matcher.start();
            if (palavra.endsWith("?") && palavra.length() > 1) {
                String antesDaInterrogacao = palavra.substring(0, palavra.length() - 1);
                if (antesDaInterrogacao.length() > 0) {
                    elementos.add(new SegmentoTextoSemantico(antesDaInterrogacao, inicioPalavra));
                }
                elementos.add(new SegmentoTextoSemantico("?", inicioPalavra + palavra.length() - 1));
            } else {
                elementos.add(new SegmentoTextoSemantico(palavra, inicioPalavra));
            }
        }
        elementos = agruparOrganizadores(elementos);
        vincular(elementos, interpretacao);
        return Collections.unmodifiableList(elementos);
    }

    private static List<SegmentoTextoSemantico> agruparOrganizadores(
            List<SegmentoTextoSemantico> elementos) {
        List<SegmentoTextoSemantico> agrupados = new ArrayList<SegmentoTextoSemantico>();
        for (int inicio = 0; inicio < elementos.size();) {
            boolean agrupou = false;
            for (String expressao : VocabularioOrganizadoresInformacao.expressoes()) {
                String[] partes = expressao.split(" ");
                if (inicio + partes.length > elementos.size()) continue;
                boolean corresponde = true;
                for (int deslocamento = 0; deslocamento < partes.length; deslocamento++) {
                    String atual = VocabularioOrganizadoresInformacao.normalizar(
                            elementos.get(inicio + deslocamento).getValor());
                    if (!VocabularioOrganizadoresInformacao.normalizar(partes[deslocamento]).equals(atual)) {
                        corresponde = false;
                        break;
                    }
                }
                if (corresponde) {
                    StringBuilder valor = new StringBuilder();
                    for (int deslocamento = 0; deslocamento < partes.length; deslocamento++) {
                        if (valor.length() > 0) valor.append(' ');
                        valor.append(elementos.get(inicio + deslocamento).getValor());
                    }
                    SegmentoTextoSemantico composto = new SegmentoTextoSemantico(
                            valor.toString(), elementos.get(inicio).getPosicaoInicial());
                    composto.marcarComoCandidatoOrganizadorInformacao();
                    agrupados.add(composto);
                    inicio += partes.length;
                    agrupou = true;
                    break;
                }
            }
            if (!agrupou) {
                agrupados.add(elementos.get(inicio));
                inicio++;
            }
        }
        return agrupados;
    }

    private static void vincular(
            List<SegmentoTextoSemantico> elementos, ResultadoInterpretacao interpretacao) {
        if (interpretacao == null) {
            return;
        }
        List<NumeroEncontrado> numeros = interpretacao.getNumeros();
        for (SegmentoTextoSemantico elemento : elementos) {
            int inicioElemento = elemento.getPosicaoInicial();
            int fimElemento = inicioElemento + elemento.getValor().length();
            for (int n = 0; n < numeros.size(); n++) {
                NumeroEncontrado numero = numeros.get(n);
                if (numero.getPosicaoInicial() >= inicioElemento
                        && numero.getPosicaoFinal() <= fimElemento) {
                    int inicioLocal = numero.getPosicaoInicial() - inicioElemento;
                    int fimLocal = numero.getPosicaoFinal() - inicioElemento;
                    elemento.vincularSemantica(
                            ResolvedorPapelInterpretado.obterChavePapelDoNumero(
                                    interpretacao, numero, n),
                            inicioLocal, fimLocal, numero.getValorCanonico());
                    break;
                }
            }
            if (!elemento.possuiVinculoSemantico()) {
                int indiceInterrogacao = elemento.getValor().indexOf('?');
                if (indiceInterrogacao >= 0) {
                    elemento.vincularSemantica(
                            ResolvedorPapelInterpretado.obterChavePapelExataPorValor(interpretacao, "?"),
                            indiceInterrogacao, indiceInterrogacao + 1, "?");
                }
            }
        }
    }

}
