package gerard.agente.modelador;

import gerard.agente.modelousuario.NivelConceitualExplicacao;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Estima o nível de conceitualização linguística de uma explicação livre
 * (ver DiagnosticoTarefa.explicacaoElemento), seguindo a escada de Vergnaud
 * (1998, "The role of language and symbols in representation", seção 3) —
 * ver NivelConceitualExplicacao para a definição de cada nível.
 *
 * Casamento de padrão sobre listas fechadas de vocabulário, em português,
 * independente do idioma atual da interface (quem escreve a explicação é o
 * usuário, em português, mesmo que a interface esteja em outro idioma no
 * momento — por isso os termos aqui não vêm de ServicoLocalizacao). Não é
 * análise sintática real: não identifica sujeito/predicado de verdade, só
 * proximidade de palavras. Sinal fraco — ver aviso em
 * NivelConceitualExplicacao e a exigência de curadoria humana antes de usar
 * este valor em inferência de regras.
 */
public final class AnalisadorNivelConceitual {

    // Termos dos papéis semânticos, em português, mais longos primeiro para
    // que "transformação 1" não seja mascarado por um casamento parcial de
    // "transformação". Cobre as 8 categorias do campo aditivo (ver
    // TipoSituacaoAditiva) — fonte fechada, não deriva de i18n de propósito.
    private static final String[] PAPEIS_PT = {
            "transformacao final", "transformacao 1", "transformacao 2", "transformacao",
            "relacao inicial", "relacao final", "relacao 1", "relacao 2",
            "estado inicial", "estado final",
            "parte 1", "parte 2", "todo",
            "valor relativo", "referido", "referendo",
    };

    private static final String[] VERBOS_NIVEL3 = {
            "representa", "indica", "mostra", "junta", "soma", "subtrai",
            "aumenta", "diminui", "expressa", "significa", "corresponde",
            "transforma", "resulta",
    };

    private static final String[] MARCADORES_NIVEL4 = {
            "sempre", "toda vez", "qualquer", "em geral", "por definicao",
            "e um tipo de", "e uma forma de", "nesse tipo de situacao",
    };

    // Nome das 8 categorias por extenso, em português — mesmo critério de
    // lista fechada dos papéis acima; citar a categoria como classe da
    // situação (não como papel de um elemento específico) é a mesma
    // jogada de classificação/inclusão do nível 4.
    private static final String[] CATEGORIAS_PT = {
            "composicao de medidas", "transformacao de medidas",
            "composicao seguida de transformacao", "comparacao de medidas",
            "composicao de transformacoes", "transformacao composta",
            "transformacao de uma relacao", "composicao de relacoes",
    };

    private static final String[] CONECTORES_NIVEL2 = {
            "porque", "ja que", "pois", "depois", "antes", "junto com",
            "em relacao a", "comparado a", "alem de",
    };

    private static final int PALAVRAS_MINIMAS = 3;
    private static final int JANELA_SUJEITO_VERBO = 4;

    public NivelConceitualExplicacao classificar(String textoOriginal) {
        String texto = normalizar(textoOriginal);
        if (texto.length() == 0 || contarPalavras(texto) < PALAVRAS_MINIMAS) {
            return NivelConceitualExplicacao.AUSENTE;
        }

        if (contemAlgum(texto, MARCADORES_NIVEL4) || contemAlgum(texto, CATEGORIAS_PT)) {
            return NivelConceitualExplicacao.CLASSIFICATORIO;
        }

        if (contemPapelPertoDeVerboNivel3(texto)) {
            return NivelConceitualExplicacao.SUBSTANTIVADO;
        }

        List<String> papeis = papeisEncontrados(texto);
        if (papeis.size() >= 2 || contemAlgum(texto, CONECTORES_NIVEL2)) {
            return NivelConceitualExplicacao.RELACIONAL;
        }

        if (papeis.size() == 1) {
            return NivelConceitualExplicacao.ADJETIVO;
        }

        return NivelConceitualExplicacao.AUSENTE;
    }

    private boolean contemPapelPertoDeVerboNivel3(String texto) {
        String[] palavras = texto.split("\\s+");
        for (int i = 0; i < palavras.length; i++) {
            if (!contemAlgum(palavras[i], VERBOS_NIVEL3)) {
                continue;
            }
            int inicio = Math.max(0, i - JANELA_SUJEITO_VERBO);
            String janela = juntar(palavras, inicio, i);
            if (!papeisEncontrados(janela).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private List<String> papeisEncontrados(String texto) {
        Set<String> encontrados = new LinkedHashSet<String>();
        for (String papel : PAPEIS_PT) {
            if (texto.contains(papel)) {
                encontrados.add(papel);
            }
        }
        return new ArrayList<String>(encontrados);
    }

    private boolean contemAlgum(String texto, String[] termos) {
        for (String termo : termos) {
            if (texto.contains(termo)) {
                return true;
            }
        }
        return false;
    }

    private String juntar(String[] palavras, int inicio, int fimExclusivo) {
        StringBuilder sb = new StringBuilder();
        for (int i = inicio; i < fimExclusivo; i++) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(palavras[i]);
        }
        return sb.toString();
    }

    private int contarPalavras(String texto) {
        if (texto.length() == 0) {
            return 0;
        }
        return texto.split("\\s+").length;
    }

    private String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        String semAcento = Normalizer.normalize(texto.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
        return semAcento.toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }
}
