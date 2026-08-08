package gerard.campoaditivo.conclusao;

import gerard.Scaffolding.questionamento.ScaffoldingQuestionamento;
import gerard.interpretacao.simbolo.SimboloDesconhecido;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Avalia o ciclo completo da incógnita. Todos os papéis precisam estar
 * semanticamente corretos. Além disso, deve existir exatamente um item
 * originalmente desconhecido: primeiro ele ocupa o papel correto como "?";
 * depois é substituído por número pelo protocolo de mouse/texto.
 */
public final class AvaliadorConclusaoModelagem {
    private final ScaffoldingQuestionamento questionamento =
            new ScaffoldingQuestionamento();
    private final PoliticaValorNumericoConclusao politicaValorNumerico =
            new PoliticaValorNumericoConclusao();

    public FaseConclusaoModelagem avaliar(Collection<String> papeisEsperados,
            List<EstadoPosicionamentoModelagem> posicionamentos) {
        List<String> esperados = normalizarPapeis(papeisEsperados);
        if (esperados.isEmpty() || posicionamentos == null
                || posicionamentos.size() != esperados.size()) {
            return FaseConclusaoModelagem.INCOMPLETA;
        }

        boolean[] atendidos = new boolean[esperados.size()];
        int quantidadeIncognitasOriginais = 0;
        boolean incognitaAguardandoTexto = false;
        boolean incognitaPreenchidaCorretamente = false;

        for (EstadoPosicionamentoModelagem estado : posicionamentos) {
            if (estado == null || !estado.isNoDiagrama()) {
                return FaseConclusaoModelagem.INCOMPLETA;
            }
            String papelItem = estado.getPapelItem();
            String papelAlvo = estado.getPapelAlvo();
            if (!papelValido(papelItem) || !papelValido(papelAlvo)
                    || !compativeis(papelItem, papelAlvo)) {
                return FaseConclusaoModelagem.INCOMPLETA;
            }
            int indiceEsperado = encontrarEsperadoNaoAtendido(
                    esperados, atendidos, papelAlvo);
            if (indiceEsperado < 0) {
                return FaseConclusaoModelagem.INCOMPLETA;
            }
            atendidos[indiceEsperado] = true;

            if (estado.isIncognitaOriginal()) {
                quantidadeIncognitasOriginais++;
                if (SimboloDesconhecido.eh(estado.getValorMatematico())
                        && !estado.isPreenchidoPeloProtocoloMouseTexto()) {
                    incognitaAguardandoTexto = true;
                } else if (politicaValorNumerico.ehNumero(
                        estado.getValorMatematico())
                        && estado.isPreenchidoPeloProtocoloMouseTexto()
                        && !Boolean.FALSE.equals(estado.getValorCorrespondeAoCurado())) {
                    incognitaPreenchidaCorretamente = true;
                } else {
                    // Um número colocado automaticamente sobre a incógnita, ou
                    // informado pelo protocolo mas divergente do valor curado
                    // da situação, não satisfaz o protocolo pedagógico de
                    // conclusão. getValorCorrespondeAoCurado() null (sem valor
                    // curado disponível para conferir) não cai aqui.
                    return FaseConclusaoModelagem.INCOMPLETA;
                }
            } else if (!politicaValorNumerico.ehNumero(
                    estado.getValorMatematico())) {
                return FaseConclusaoModelagem.INCOMPLETA;
            }
        }

        for (boolean atendido : atendidos) {
            if (!atendido) {
                return FaseConclusaoModelagem.INCOMPLETA;
            }
        }
        if (quantidadeIncognitasOriginais != 1) {
            return FaseConclusaoModelagem.INCOMPLETA;
        }
        if (incognitaPreenchidaCorretamente) {
            return FaseConclusaoModelagem.CONCLUIDA;
        }
        if (incognitaAguardandoTexto) {
            return FaseConclusaoModelagem.AGUARDANDO_PREENCHIMENTO_INCOGNITA;
        }
        return FaseConclusaoModelagem.INCOMPLETA;
    }

    public boolean estaConcluida(Collection<String> papeisEsperados,
            List<EstadoPosicionamentoModelagem> posicionamentos) {
        return avaliar(papeisEsperados, posicionamentos)
                == FaseConclusaoModelagem.CONCLUIDA;
    }

    /**
     * AG_AE (dica de posicionamento) — verdadeiro quando o papel indicado
     * já está corretamente posicionado no diagrama: mesmo critério de
     * "atendido" usado por {@link #avaliar}, aplicado a um único papel em
     * vez do conjunto inteiro. Não duplica a comparação de papéis — reusa
     * {@link #compativeis}, a mesma que decide a conclusão da modelagem
     * inteira.
     */
    public boolean papelResolvido(String papel,
            List<EstadoPosicionamentoModelagem> posicionamentos) {
        if (!papelValido(papel) || posicionamentos == null) {
            return true;
        }
        for (EstadoPosicionamentoModelagem estado : posicionamentos) {
            if (estado != null && compativeis(papel, estado.getPapelAlvo())) {
                return estado.isNoDiagrama()
                        && compativeis(estado.getPapelItem(), estado.getPapelAlvo());
            }
        }
        return true;
    }

    /**
     * AG_AE (dica de posicionamento) — primeiro papel esperado, em ordem
     * canônica, que ainda não está resolvido (ver {@link #papelResolvido}) e
     * que não seja {@code papelExcluido} (a incógnita atual — a dica nunca
     * aponta para ela; ver gerard-consistencia-estado). null quando todos
     * os papéis restantes (fora o excluído) já estão resolvidos.
     */
    public String obterProximoPapelNaoResolvido(Collection<String> papeisEsperados,
            List<EstadoPosicionamentoModelagem> posicionamentos, String papelExcluido) {
        for (String papel : normalizarPapeis(papeisEsperados)) {
            if (papel.equals(papelExcluido)) {
                continue;
            }
            if (!papelResolvido(papel, posicionamentos)) {
                return papel;
            }
        }
        return null;
    }

    private int encontrarEsperadoNaoAtendido(List<String> esperados,
            boolean[] atendidos, String papelAlvo) {
        for (int i = 0; i < esperados.size(); i++) {
            if (!atendidos[i] && compativeis(esperados.get(i), papelAlvo)) {
                return i;
            }
        }
        return -1;
    }

    private List<String> normalizarPapeis(Collection<String> papeis) {
        List<String> resultado = new ArrayList<String>();
        if (papeis == null) return resultado;
        for (String papel : papeis) {
            String normalizado = papel == null ? "" : papel.trim();
            if (papelValido(normalizado)) resultado.add(normalizado);
        }
        return resultado;
    }

    private boolean compativeis(String a, String b) {
        return questionamento.papeisCompativeis(a, b)
                || questionamento.papeisCompativeis(b, a);
    }

    private boolean papelValido(String papel) {
        return papel != null && papel.trim().length() > 0
                && !"papel.valor".equals(papel.trim());
    }
}
