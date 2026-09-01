package gerard.campoaditivo.curadoria;

import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.dominio.campoaditivo.situacao.DiagnosticoSituacao;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

/**
 * Porta de entrada da situação rica no fluxo real de curadoria.
 * A tabela fornece os valores formais; o sidecar fornece somente o
 * conhecimento narrativo que o pesquisador declarou nominalmente.
 */
public final class ServicoSituacaoProblemaRicaCurada {
    private final RepositorioCuradoriaNarrativaRica repositorioNarrativo;
    private final ConversorSituacaoProblemaRica conversor;

    public ServicoSituacaoProblemaRicaCurada(
            RepositorioCuradoriaNarrativaRica repositorioNarrativo) {
        if (repositorioNarrativo == null) {
            throw new IllegalArgumentException(
                    "repositório da narrativa rica é obrigatório");
        }
        this.repositorioNarrativo = repositorioNarrativo;
        this.conversor = new ConversorSituacaoProblemaRica();
    }

    public ResultadoConversaoSituacaoProblemaRica converter(
            SituacaoProblemaAditiva registro) throws IOException {
        if (registro == null) {
            return conversor.converter(null, null, null);
        }
        String idNarrativa = idProprietarioNarrativa(registro);
        Optional<RegistroCuradoriaNarrativaRica> complemento =
                repositorioNarrativo.carregar(idNarrativa);
        if (!complemento.isPresent()) {
            return new ResultadoConversaoSituacaoProblemaRica(
                    null,
                    Collections.singletonList(new DiagnosticoSituacao(
                            "conversao.narrativa_persistida.ausente",
                            idNarrativa)));
        }
        RegistroCuradoriaNarrativaRica curadoria = complemento.get();
        return conversor.converter(
                registro,
                curadoria.getNarrativa(),
                curadoria.getCorrespondencias(),
                curadoria.getStatusCuradoria());
    }

    /**
     * A semântica narrativa pertence à versão original. Traduções mudam a
     * realização textual e referenciam explicitamente o mesmo complemento;
     * não criam outro conjunto de participantes, objetos ou eventos.
     */
    private static String idProprietarioNarrativa(
            SituacaoProblemaAditiva registro) {
        String origem = limpar(registro.getVersaoOrigemId());
        if ("traducao".equalsIgnoreCase(limpar(registro.getTipoVersao()))
                && !origem.isEmpty()) {
            return origem;
        }
        return registro.getId();
    }

    private static String limpar(String valor) {
        return valor == null ? "" : valor.trim();
    }
}
