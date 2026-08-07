package gerard.campoaditivo.transformacao.composicao;

import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.sincronizacao.EstadoSemanticoCompartilhado;
import gerard.campoaditivo.transformacao.processo.PlanoUnidadesProcessoTransformacao;
import gerard.campoaditivo.transformacao.processo.TipoProcessoTransformacao;
import gerard.semantica.quantidade.EscalaVisualQuantidade;
import gerard.semantica.quantidade.PerfilQuantidadeSituacao;
import gerard.semantica.quantidade.PoliticaEscalaVisualQuantidade;
import gerard.semantica.quantidade.ResolvedorPerfilQuantidadeSituacao;
import gerard.semantica.quantidade.ServicoQuantidadeContextual;

/**
 * Converte os três valores semânticos de Composição de Transformações em
 * magnitudes visuais — análogo a
 * {@link gerard.campoaditivo.transformacao.processo.SincronizadorUnidadesProcessoTransformacao},
 * reaproveitando o mesmo {@link PlanoUnidadesProcessoTransformacao} (classe
 * já genérica por índice, sem suposição de "estado"). Diferença real: os
 * três índices são tratados por igual (todos podem ser retirada/inserção
 * independentemente — origem "transformacao_negativa" é decidida por
 * índice, não por um único índice central fixo como no processo simples).
 */
public final class SincronizadorUnidadesComposicaoTransformacoes {
    private final ResolvedorPerfilQuantidadeSituacao resolvedorPerfil =
            new ResolvedorPerfilQuantidadeSituacao();
    private final PoliticaEscalaVisualQuantidade politicaEscala =
            new PoliticaEscalaVisualQuantidade();
    private final ServicoQuantidadeContextual servicoQuantidade =
            new ServicoQuantidadeContextual();

    public PlanoUnidadesProcessoTransformacao criarPlano(
            EstadoSemanticoCompartilhado.Snapshot snapshot) {
        return criarPlano(snapshot, null);
    }

    public PlanoUnidadesProcessoTransformacao criarPlano(
            EstadoSemanticoCompartilhado.Snapshot snapshot,
            SituacaoProblemaAditiva situacao) {
        EstadoComposicaoTransformacoes estado =
                EstadoComposicaoTransformacoes.aPartir(snapshot);
        int[] valores = new int[] {0, 0, 0};
        int[] quantidades = new int[] {0, 0, 0};
        boolean[] conhecidos = new boolean[] {false, false, false};
        String[] origens = new String[] {
            "situacao_problema", "situacao_problema", "situacao_problema"
        };
        String[] valoresFormatados = new String[] {"", "", ""};
        for (int i = 0; i < 3; i++) {
            conhecidos[i] = estado.isConhecido(i);
            valores[i] = estado.valorOuZero(i);
        }

        PerfilQuantidadeSituacao perfil = resolvedorPerfil.resolver(situacao);
        EscalaVisualQuantidade escala = politicaEscala.calcular(
                perfil, valores, conhecidos);
        for (int i = 0; i < 3; i++) {
            quantidades[i] = conhecidos[i]
                    ? escala.converterExato(valores[i]) : 0;
            if (conhecidos[i]) {
                valoresFormatados[i] = servicoQuantidade
                        .formatarInteiroLegado(valores[i], situacao, true);
            }
            if (estado.getTipoProcesso(i) == TipoProcessoTransformacao.RETIRADA) {
                origens[i] = "transformacao_negativa";
            }
        }
        return new PlanoUnidadesProcessoTransformacao(
                quantidades, conhecidos, origens, perfil.getTipo(),
                escala.getValorPorUnidade(), escala.getLegenda(),
                valoresFormatados);
    }
}
