package gerard.campoaditivo.servico;

import gerard.campoaditivo.modelo.DefinicaoDiagramaAditivo;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.i18n.ServicoLocalizacao;

public class CatalogoDefinicoesAditivas {
    private final ServicoLocalizacao localizacao = ServicoLocalizacao.getInstancia();

    public DefinicaoDiagramaAditivo obter(TipoSituacaoAditiva tipo) {
        if (tipo == null) {
            tipo = TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS;
        }

        switch (tipo) {
            case COMPOSICAO_MEDIDAS:
                return new DefinicaoDiagramaAditivo(
                        localizacao.texto("def.titulo.composicao_medidas"),
                        localizacao.texto("def.r1.composicao_medidas"),
                        localizacao.texto("def.r2.composicao_medidas"),
                        localizacao.texto("def.r3.composicao_medidas")
                );
            case TRANSFORMACAO_MEDIDAS:
                return new DefinicaoDiagramaAditivo(
                        localizacao.texto("def.titulo.transformacao_medidas"),
                        localizacao.texto("def.r1.transformacao_medidas"),
                        localizacao.texto("def.r2.transformacao_medidas"),
                        localizacao.texto("def.r3.transformacao_medidas")
                );
            case COMPARACAO_MEDIDAS:
                return new DefinicaoDiagramaAditivo(
                        localizacao.texto("def.titulo.comparacao_medidas"),
                        localizacao.texto("def.r1.comparacao_medidas"),
                        localizacao.texto("def.r2.comparacao_medidas"),
                        localizacao.texto("def.r3.comparacao_medidas")
                );
            case COMPOSICAO_TRANSFORMACOES:
                return new DefinicaoDiagramaAditivo(
                        localizacao.texto("def.titulo.composicao_transformacoes"),
                        localizacao.texto("def.r1.composicao_transformacoes"),
                        localizacao.texto("def.r2.composicao_transformacoes"),
                        localizacao.texto("def.r3.composicao_transformacoes")
                );
            case TRANSFORMACAO_RELACAO:
                return new DefinicaoDiagramaAditivo(
                        localizacao.texto("def.titulo.transformacao_relacao"),
                        localizacao.texto("def.r1.transformacao_relacao"),
                        localizacao.texto("def.r2.transformacao_relacao"),
                        localizacao.texto("def.r3.transformacao_relacao")
                );
            case COMPOSICAO_RELACOES:
                return new DefinicaoDiagramaAditivo(
                        localizacao.texto("def.titulo.composicao_relacoes"),
                        localizacao.texto("def.r1.composicao_relacoes"),
                        localizacao.texto("def.r2.composicao_relacoes"),
                        localizacao.texto("def.r3.composicao_relacoes")
                );
            default:
                return new DefinicaoDiagramaAditivo(localizacao.texto("def.titulo.transformacao_medidas"), "E1", "E2", "E3");
        }
    }
}
