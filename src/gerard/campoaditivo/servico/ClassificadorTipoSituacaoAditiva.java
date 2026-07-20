package gerard.campoaditivo.servico;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.interpretacao.classificacao.ClassificadorHibridoVergnaud;
import gerard.interpretacao.classificacao.ResultadoClassificacaoVergnaud;

/**
 * Adaptador entre o campo aditivo e o classificador híbrido de Vergnaud.
 *
 * Mantém a assinatura antiga usada pelo repositório, mas delega a decisão para
 * o pacote gerard.interpretacao.classificacao, que combina modelo treinado,
 * regras linguísticas e fallback manual.
 */
public class ClassificadorTipoSituacaoAditiva {
    private final ClassificadorHibridoVergnaud classificadorHibrido;
    private ResultadoClassificacaoVergnaud ultimoResultado;

    public ClassificadorTipoSituacaoAditiva() {
        this.classificadorHibrido = new ClassificadorHibridoVergnaud();
    }

    public TipoSituacaoAditiva corrigirTipo(TipoSituacaoAditiva tipoInformado, String enunciado) {
        ultimoResultado = classificadorHibrido.classificar(enunciado, tipoInformado);
        if (ultimoResultado != null && ultimoResultado.getCategoriaPrevista() != null) {
            String origem = ultimoResultado.getOrigem();
            // Para exemplos já rotulados no repositório, o modelo atua como sugestão.
            // A categoria só é corrigida automaticamente quando há regra linguística
            // explícita, evitando que um modelo treinado com poucos exemplos desloque
            // rótulos manuais de forma opaca.
            if (origem != null && origem.startsWith("regra")) {
                return ultimoResultado.getCategoriaPrevista();
            }
            // Quando modelo e regra linguística apontam para a mesma categoria,
            // considera-se que há evidência explicável suficiente para corrigir
            // um rótulo manual/dados possivelmente inconsistente. Isso evita que
            // casos como "tinha ... comprou ... ficou?" permaneçam em composição
            // apenas porque a linha original do TSV veio rotulada incorretamente.
            if (ultimoResultado.getCategoriaRegra() != null
                    && ultimoResultado.getCategoriaRegra() == ultimoResultado.getCategoriaPrevista()
                    && ultimoResultado.getConfiancaRegra() >= 0.66
                    && ultimoResultado.getCategoriaPrevista() != tipoInformado) {
                return ultimoResultado.getCategoriaPrevista();
            }
            if (tipoInformado == null) {
                return ultimoResultado.getCategoriaPrevista();
            }
        }
        return tipoInformado == null ? TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS : tipoInformado;
    }

    public ResultadoClassificacaoVergnaud classificar(String enunciado, TipoSituacaoAditiva tipoInformado) {
        ultimoResultado = classificadorHibrido.classificar(enunciado, tipoInformado);
        return ultimoResultado;
    }

    public ResultadoClassificacaoVergnaud getUltimoResultado() {
        return ultimoResultado;
    }

    public boolean ehTransformacaoCompostaEmPassos(String enunciado) {
        return classificadorHibrido.ehTransformacaoCompostaEmPassos(enunciado);
    }
}
