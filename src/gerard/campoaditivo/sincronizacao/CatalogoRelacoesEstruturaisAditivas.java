package gerard.campoaditivo.sincronizacao;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.dominio.campoaditivo.ContextoAcao;
import gerard.dominio.campoaditivo.FabricaPapeisComparacaoMedidas;
import gerard.dominio.campoaditivo.FabricaPapeisComposicaoDeRelacoes;
import gerard.dominio.campoaditivo.FabricaPapeisComposicaoDeTransformacoes;
import gerard.dominio.campoaditivo.FabricaPapeisTransformacaoDeRelacao;
import gerard.dominio.campoaditivo.FabricaPapeisTransformacaoMedidas;
import gerard.dominio.campoaditivo.OrigemAcao;
import gerard.dominio.campoaditivo.PapelQuantitativo;
import gerard.dominio.campoaditivo.RelacaoEstruturalAditiva;
import gerard.dominio.campoaditivo.RelacaoEstruturalComparacao;
import gerard.dominio.campoaditivo.RelacaoEstruturalComposicao;
import gerard.dominio.campoaditivo.RelacaoEstruturalComposicaoDeRelacoes;
import gerard.dominio.campoaditivo.RelacaoEstruturalComposicaoDeTransformacoes;
import gerard.dominio.campoaditivo.RelacaoEstruturalTransformacao;
import gerard.dominio.campoaditivo.RelacaoEstruturalTransformacaoDeRelacao;
import gerard.dominio.campoaditivo.ResultadoCalculo;
import gerard.dominio.campoaditivo.evento.PublicadorEventoDominio;
import gerard.semantica.numero.ValorNumerico;

/**
 * Associa cada categoria aditiva canônica aos seus três papéis e à relação
 * estrutural correspondente. Não contém regras matemáticas.
 */
public final class CatalogoRelacoesEstruturaisAditivas {

    public RelacaoContextualizada criar(TipoSituacaoAditiva tipo,
            ValorNumerico[] valores) {
        if (tipo == null) {
            return null;
        }

        PublicadorEventoDominio semEventos = PublicadorEventoDominio.NENHUM;
        PapelQuantitativo[] papeis = new PapelQuantitativo[3];
        RelacaoEstruturalAditiva relacao;

        switch (tipo) {
            case COMPOSICAO_MEDIDAS:
                papeis[0] = PapelQuantitativo.parte1(semEventos);
                papeis[1] = PapelQuantitativo.parte2(semEventos);
                papeis[2] = PapelQuantitativo.todo(semEventos);
                relacao = RelacaoEstruturalComposicao.composicaoDeMedidas();
                break;
            case TRANSFORMACAO_MEDIDAS:
                papeis[0] = FabricaPapeisTransformacaoMedidas.estadoInicial(semEventos);
                papeis[1] = FabricaPapeisTransformacaoMedidas.transformacao(semEventos);
                papeis[2] = FabricaPapeisTransformacaoMedidas.estadoFinal(semEventos);
                relacao = RelacaoEstruturalTransformacao.transformacaoDeMedidas();
                break;
            case COMPARACAO_MEDIDAS:
                papeis[0] = FabricaPapeisComparacaoMedidas.referido(semEventos);
                papeis[1] = FabricaPapeisComparacaoMedidas.valorRelativo(semEventos);
                papeis[2] = FabricaPapeisComparacaoMedidas.referendo(semEventos);
                relacao = RelacaoEstruturalComparacao.comparacaoDeMedidas();
                break;
            case COMPOSICAO_TRANSFORMACOES:
                papeis[0] = FabricaPapeisComposicaoDeTransformacoes.transformacao1(semEventos);
                papeis[1] = FabricaPapeisComposicaoDeTransformacoes.transformacao2(semEventos);
                papeis[2] = FabricaPapeisComposicaoDeTransformacoes.transformacaoFinal(semEventos);
                relacao = RelacaoEstruturalComposicaoDeTransformacoes.composicaoDeTransformacoes();
                break;
            case TRANSFORMACAO_RELACAO:
                papeis[0] = FabricaPapeisTransformacaoDeRelacao.relacaoInicial(semEventos);
                papeis[1] = FabricaPapeisTransformacaoDeRelacao.transformacao(semEventos);
                papeis[2] = FabricaPapeisTransformacaoDeRelacao.relacaoFinal(semEventos);
                relacao = RelacaoEstruturalTransformacaoDeRelacao.transformacaoDeRelacao();
                break;
            case COMPOSICAO_RELACOES:
                papeis[0] = FabricaPapeisComposicaoDeRelacoes.relacao1(semEventos);
                papeis[1] = FabricaPapeisComposicaoDeRelacoes.relacao2(semEventos);
                papeis[2] = FabricaPapeisComposicaoDeRelacoes.relacaoFinal(semEventos);
                relacao = RelacaoEstruturalComposicaoDeRelacoes.composicaoDeRelacoes();
                break;
            default:
                return null;
        }

        for (int i = 0; i < papeis.length; i++) {
            ValorNumerico valor = valores != null && i < valores.length
                    ? valores[i] : null;
            if (valor != null && valor.ehConhecido()) {
                papeis[i].posicionar(valor, OrigemAcao.ORIGEM_SISTEMA,
                        ContextoAcao.NAO_INFORMADO);
            }
        }
        return new RelacaoContextualizada(relacao, papeis);
    }

    public static final class RelacaoContextualizada {
        private final RelacaoEstruturalAditiva relacao;
        private final PapelQuantitativo[] papeis;

        private RelacaoContextualizada(RelacaoEstruturalAditiva relacao,
                PapelQuantitativo[] papeis) {
            this.relacao = relacao;
            this.papeis = papeis;
        }

        public ResultadoCalculo calcularValorAusente() {
            return relacao.calcularValorAusente(papeis[0], papeis[1], papeis[2]);
        }

        public ResultadoCalculo recalcularParaConsistencia(int indiceAlterado) {
            if (indiceAlterado < 0 || indiceAlterado >= papeis.length) {
                return null;
            }
            return relacao.recalcularParaConsistencia(
                    papeis[0], papeis[1], papeis[2], papeis[indiceAlterado]);
        }

        public int indiceDoPapel(PapelQuantitativo papel) {
            for (int i = 0; i < papeis.length; i++) {
                if (papeis[i] == papel) {
                    return i;
                }
            }
            return -1;
        }
    }
}
