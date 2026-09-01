package gerard.aplicacao;

import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado;
import gerard.dominio.atividade.ContextoAcaoInstrumental;
import gerard.dominio.atividade.RegistroAcaoInstrumental;
import gerard.dominio.atividade.TarefaInteracao;
import gerard.dominio.campoaditivo.IdentidadeAcaoInstrumentalPapel;
import gerard.dominio.campoaditivo.IncognitaQuantitativa;
import gerard.i18n.ServicoLocalizacao;
import gerard.semantica.numero.ConversorTextoParaInteiroSemantico;
import gerard.semantica.numero.NumeroInteiro;

/** Prepara valores e delega a avaliação factual ao proprietário incógnita. */
public final class ServicoAvaliacaoAcaoIncognita {
    private final ResolvedorValorEsperadoIncognita resolvedorValorEsperado =
            new ResolvedorValorEsperadoIncognita();
    private final ConversorTextoParaInteiroSemantico conversor =
            new ConversorTextoParaInteiroSemantico();

    public Boolean correspondeAoEsperado(IncognitaQuantitativa incognita,
            SituacaoProblemaAditiva situacao, ServicoLocalizacao localizacao,
            String papelSolicitado, EstadoSemanticoCompartilhado.Snapshot estado,
            int indiceIncognita, String valorProposto) {
        Valores valores = preparar(incognita, situacao, localizacao,
                papelSolicitado, estado, indiceIncognita, valorProposto);
        return incognita == null ? null
                : incognita.correspondeAoEsperado(valores.proposto, valores.esperado);
    }

    public RegistroAcaoInstrumental avaliarAcao(
            IncognitaQuantitativa incognita,
            IdentidadeAcaoInstrumentalPapel identidade,
            TarefaInteracao tarefa,
            SituacaoProblemaAditiva situacao,
            ServicoLocalizacao localizacao,
            String papelSolicitado,
            EstadoSemanticoCompartilhado.Snapshot estado,
            int indiceIncognita,
            String valorProposto,
            ContextoAcaoInstrumental contexto) {
        if (incognita == null) {
            throw new IllegalArgumentException("incógnita é obrigatória");
        }
        Valores valores = preparar(incognita, situacao, localizacao,
                papelSolicitado, estado, indiceIncognita, valorProposto);
        return incognita.avaliarAcao(
                identidade, tarefa, valores.proposto, valores.esperado, contexto);
    }

    private Valores preparar(IncognitaQuantitativa incognita,
            SituacaoProblemaAditiva situacao, ServicoLocalizacao localizacao,
            String papelSolicitado, EstadoSemanticoCompartilhado.Snapshot estado,
            int indiceIncognita, String textoProposto) {
        Integer proposto = conversor.converter(textoProposto);
        Integer esperado = resolvedorValorEsperado.resolver(
                situacao, localizacao, papelSolicitado,
                incognita == null ? null : incognita.getChavePapelDesignado(),
                estado, indiceIncognita);
        return new Valores(numero(proposto), numero(esperado));
    }

    private NumeroInteiro numero(Integer valor) {
        return valor == null ? null : new NumeroInteiro(valor.intValue());
    }

    private static final class Valores {
        private final NumeroInteiro proposto;
        private final NumeroInteiro esperado;

        private Valores(NumeroInteiro proposto, NumeroInteiro esperado) {
            this.proposto = proposto;
            this.esperado = esperado;
        }
    }
}
