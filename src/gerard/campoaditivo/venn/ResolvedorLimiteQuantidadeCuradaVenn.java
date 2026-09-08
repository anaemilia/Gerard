package gerard.campoaditivo.venn;

import gerard.Scaffolding.venn.ScaffoldingLimiteQuantidadeVenn;
import gerard.campoaditivo.curadoria.SemanticaCuradaSituacao;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.i18n.ServicoLocalizacao;

/**
 * Consulta portátil do limite semântico de um agrupamento do material
 * concreto. A representação informa apenas as chaves dos papéis que exibe;
 * este serviço associa os valores curados e delega a relação quantitativa à
 * política já existente de limite.
 */
public final class ResolvedorLimiteQuantidadeCuradaVenn {

    private final ScaffoldingLimiteQuantidadeVenn politicaLimite;

    public ResolvedorLimiteQuantidadeCuradaVenn(
            ScaffoldingLimiteQuantidadeVenn politicaLimite) {
        this.politicaLimite = politicaLimite == null
                ? new ScaffoldingLimiteQuantidadeVenn() : politicaLimite;
    }

    public Integer resolver(SituacaoProblemaAditiva situacao,
            ServicoLocalizacao localizacao, String[] chavesPapeis,
            int indiceSemanticoAlvo) {
        if (situacao == null || chavesPapeis == null
                || chavesPapeis.length < 3) {
            return null;
        }
        Integer[] valoresCurados = new Integer[chavesPapeis.length];
        for (int i = 0; i < chavesPapeis.length; i++) {
            valoresCurados[i] = SemanticaCuradaSituacao
                    .buscarValorInteiroVisivel(
                            situacao, localizacao, chavesPapeis[i]);
        }
        return politicaLimite.resolverLimite(
                chavesPapeis, valoresCurados, indiceSemanticoAlvo);
    }
}
