package gerard.interpretacao.classificacao;

import gerard.campoaditivo.modelo.TipoSituacaoAditiva;

public interface ClassificadorCategoriaVergnaud {
    ResultadoClassificacaoVergnaud classificar(String enunciado, TipoSituacaoAditiva categoriaManual);
}
