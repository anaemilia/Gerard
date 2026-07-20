package gerard.campoaditivo.modelo;

import gerard.idioma.IdiomaInterface;
import gerard.idioma.IdiomaSituacao;

public class SituacaoProblemaAditiva {
    private final String id;
    private final String situacaoGrupoId;
    private final String tipoVersao;
    private final String versaoOrigemId;
    private final boolean validada;
    private final TipoSituacaoAditiva tipo;
    private final IdiomaInterface idioma;
    private final String codigoIdioma;
    private final String enunciado;
    private final String contexto;
    private final String fonte;
    private final String subtipo;
    private final String estadoInicial;
    private final String transformacao;
    private final String sinalTransformacao;
    private final String estadoFinal;
    private final String quantidade1;
    private final String quantidade2;
    private final String resultado;
    private final String referido;
    private final String referendo;
    private final String valorRelativo;
    private final String sinalValorRelativo;
    private final String termoDesconhecido;
    private final String representacaoVisual;
    private final String observacoes;
    private final String personagem1;
    private final String personagem2;
    private final String personagem3;
    private final String fragmentoTexto1;
    private final String fragmentoTexto2;
    private final String fragmentoTexto3;
    private final String fragmentoTexto4;
    private final String fragmentoTexto5;
    private final String fragmentoTexto6;

    public SituacaoProblemaAditiva(TipoSituacaoAditiva tipo, IdiomaInterface idioma, String enunciado) {
        this("", false, tipo, idioma, enunciado, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
    }

    public SituacaoProblemaAditiva(TipoSituacaoAditiva tipo, IdiomaInterface idioma, String enunciado, String contexto, String fonte) {
        this("", false, tipo, idioma, enunciado, contexto, fonte, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
    }

    /**
     * Construtor compatível com o formato anterior da curadoria.
     */
    public SituacaoProblemaAditiva(
            String id,
            boolean validada,
            TipoSituacaoAditiva tipo,
            IdiomaInterface idioma,
            String enunciado,
            String contexto,
            String fonte,
            String subtipo,
            String estadoInicial,
            String transformacao,
            String estadoFinal,
            String quantidade1,
            String quantidade2,
            String resultado,
            String termoDesconhecido,
            String representacaoVisual,
            String observacoes) {
        this(id, validada, tipo, idioma, enunciado, contexto, fonte, subtipo, estadoInicial, transformacao, "",
                estadoFinal, quantidade1, quantidade2, resultado, "", "", "", "", termoDesconhecido,
                representacaoVisual, observacoes);
    }

    /**
     * Construtor completo usado pela curadoria humana. Para comparação de medidas,
     * referido, referendo e valorRelativo eliminam dependência de inferência textual.
     */
    public SituacaoProblemaAditiva(
            String id,
            boolean validada,
            TipoSituacaoAditiva tipo,
            IdiomaInterface idioma,
            String enunciado,
            String contexto,
            String fonte,
            String subtipo,
            String estadoInicial,
            String transformacao,
            String estadoFinal,
            String quantidade1,
            String quantidade2,
            String resultado,
            String referido,
            String referendo,
            String valorRelativo,
            String termoDesconhecido,
            String representacaoVisual,
            String observacoes) {
        this(id, validada, tipo, idioma, enunciado, contexto, fonte, subtipo, estadoInicial, transformacao, "",
                estadoFinal, quantidade1, quantidade2, resultado, referido, referendo, valorRelativo, "",
                termoDesconhecido, representacaoVisual, observacoes);
    }

    /**
     * Construtor completo usado pela curadoria humana. Os sinais permitem
     * representar explicitamente transformação e valor relativo como positivos,
     * negativos, neutros ou não aplicáveis, sem depender de inferência textual.
     */
    public SituacaoProblemaAditiva(
            String id,
            boolean validada,
            TipoSituacaoAditiva tipo,
            IdiomaInterface idioma,
            String enunciado,
            String contexto,
            String fonte,
            String subtipo,
            String estadoInicial,
            String transformacao,
            String sinalTransformacao,
            String estadoFinal,
            String quantidade1,
            String quantidade2,
            String resultado,
            String referido,
            String referendo,
            String valorRelativo,
            String sinalValorRelativo,
            String termoDesconhecido,
            String representacaoVisual,
            String observacoes) {
        this(id, id, idioma == IdiomaInterface.PORTUGUES ? "original" : "traducao", "", validada, tipo, idioma, enunciado, contexto, fonte, subtipo,
                estadoInicial, transformacao, sinalTransformacao, estadoFinal, quantidade1, quantidade2, resultado, referido, referendo,
                valorRelativo, sinalValorRelativo, termoDesconhecido, representacaoVisual, observacoes);
    }

    /**
     * Construtor com vínculo explícito entre a situação conceitual e sua versão linguística.
     */
    public SituacaoProblemaAditiva(
            String id,
            String situacaoGrupoId,
            String tipoVersao,
            String versaoOrigemId,
            boolean validada,
            TipoSituacaoAditiva tipo,
            IdiomaInterface idioma,
            String enunciado,
            String contexto,
            String fonte,
            String subtipo,
            String estadoInicial,
            String transformacao,
            String sinalTransformacao,
            String estadoFinal,
            String quantidade1,
            String quantidade2,
            String resultado,
            String referido,
            String referendo,
            String valorRelativo,
            String sinalValorRelativo,
            String termoDesconhecido,
            String representacaoVisual,
            String observacoes) {
        this(id, situacaoGrupoId, tipoVersao, versaoOrigemId, validada, tipo,
                IdiomaSituacao.codigoPadrao(idioma), enunciado, contexto, fonte, subtipo,
                estadoInicial, transformacao, sinalTransformacao, estadoFinal, quantidade1,
                quantidade2, resultado, referido, referendo, valorRelativo, sinalValorRelativo,
                termoDesconhecido, representacaoVisual, observacoes);
    }

    /** Construtor para idiomas configuráveis das situações-problema. */
    public SituacaoProblemaAditiva(
            String id, String situacaoGrupoId, String tipoVersao, String versaoOrigemId,
            boolean validada, TipoSituacaoAditiva tipo, String codigoIdioma, String enunciado,
            String contexto, String fonte, String subtipo, String estadoInicial,
            String transformacao, String sinalTransformacao, String estadoFinal,
            String quantidade1, String quantidade2, String resultado, String referido,
            String referendo, String valorRelativo, String sinalValorRelativo,
            String termoDesconhecido, String representacaoVisual, String observacoes) {
        this(id, situacaoGrupoId, tipoVersao, versaoOrigemId, validada, tipo, codigoIdioma,
                enunciado, contexto, fonte, subtipo, estadoInicial, transformacao,
                sinalTransformacao, estadoFinal, quantidade1, quantidade2, resultado,
                referido, referendo, valorRelativo, sinalValorRelativo, termoDesconhecido,
                representacaoVisual, observacoes, "", "", "", "", "", "", "", "", "");
    }

    /**
     * Construtor completo com os participantes/objetos explicitamente curados.
     * Os três campos são linguísticos e não impõem que o conteúdo seja pessoa:
     * podem registrar nomes, grupos, animais ou objetos envolvidos no enunciado.
     */
    public SituacaoProblemaAditiva(
            String id, String situacaoGrupoId, String tipoVersao, String versaoOrigemId,
            boolean validada, TipoSituacaoAditiva tipo, String codigoIdioma, String enunciado,
            String contexto, String fonte, String subtipo, String estadoInicial,
            String transformacao, String sinalTransformacao, String estadoFinal,
            String quantidade1, String quantidade2, String resultado, String referido,
            String referendo, String valorRelativo, String sinalValorRelativo,
            String termoDesconhecido, String representacaoVisual, String observacoes,
            String personagem1, String personagem2, String personagem3,
            String fragmentoTexto1, String fragmentoTexto2, String fragmentoTexto3,
            String fragmentoTexto4, String fragmentoTexto5, String fragmentoTexto6) {
        this.id = limpar(id);
        this.situacaoGrupoId = limpar(situacaoGrupoId).isEmpty() ? limpar(id) : limpar(situacaoGrupoId);
        this.codigoIdioma = IdiomaSituacao.normalizarCodigo(codigoIdioma);
        this.idioma = IdiomaSituacao.paraIdiomaInterface(this.codigoIdioma);
        this.tipoVersao = limpar(tipoVersao).isEmpty() ? "original" : limpar(tipoVersao);
        // Uma versão original é a referência do grupo. Qualquer vínculo antigo ou
        // externo para outra origem é removido automaticamente, sem expor ao
        // pesquisador uma inconsistência técnica que o sistema pode resolver.
        this.versaoOrigemId = "original".equals(this.tipoVersao) ? "" : limpar(versaoOrigemId);
        this.validada = validada;
        this.tipo = tipo;
        this.enunciado = limpar(enunciado);
        this.contexto = limpar(contexto);
        this.fonte = limpar(fonte);
        this.subtipo = limpar(subtipo);
        this.estadoInicial = limpar(estadoInicial);
        this.transformacao = limpar(transformacao);
        this.sinalTransformacao = limpar(sinalTransformacao);
        this.estadoFinal = limpar(estadoFinal);
        this.quantidade1 = limpar(quantidade1);
        this.quantidade2 = limpar(quantidade2);
        this.resultado = limpar(resultado);
        this.referido = limpar(referido);
        this.referendo = limpar(referendo);
        this.valorRelativo = limpar(valorRelativo);
        this.sinalValorRelativo = limpar(sinalValorRelativo);
        this.termoDesconhecido = limpar(termoDesconhecido);
        this.representacaoVisual = limpar(representacaoVisual);
        this.observacoes = limpar(observacoes);
        this.personagem1 = limpar(personagem1);
        this.personagem2 = limpar(personagem2);
        this.personagem3 = limpar(personagem3);
        this.fragmentoTexto1 = limpar(fragmentoTexto1);
        this.fragmentoTexto2 = limpar(fragmentoTexto2);
        this.fragmentoTexto3 = limpar(fragmentoTexto3);
        this.fragmentoTexto4 = limpar(fragmentoTexto4);
        this.fragmentoTexto5 = limpar(fragmentoTexto5);
        this.fragmentoTexto6 = limpar(fragmentoTexto6);
    }

    private static String limpar(String texto) {
        return texto == null ? "" : texto;
    }

    public String getId() { return id; }
    public String getSituacaoGrupoId() { return situacaoGrupoId; }
    public String getTipoVersao() { return tipoVersao; }
    public String getVersaoOrigemId() { return versaoOrigemId; }
    public boolean isValidada() { return validada; }
    public TipoSituacaoAditiva getTipo() { return tipo; }
    public IdiomaInterface getIdioma() { return idioma; }
    public String getCodigoIdioma() { return codigoIdioma; }
    public String getEnunciado() { return enunciado; }
    public String getContexto() { return contexto; }
    public String getFonte() { return fonte; }
    public String getSubtipo() { return subtipo; }
    public String getEstadoInicial() { return estadoInicial; }
    public String getTransformacao() { return transformacao; }
    public String getSinalTransformacao() { return sinalTransformacao; }
    public String getEstadoFinal() { return estadoFinal; }
    public String getQuantidade1() { return quantidade1; }
    public String getQuantidade2() { return quantidade2; }
    public String getResultado() { return resultado; }
    public String getReferido() { return referido; }
    public String getReferendo() { return referendo; }
    public String getValorRelativo() { return valorRelativo; }
    public String getSinalValorRelativo() { return sinalValorRelativo; }
    public String getTermoDesconhecido() { return termoDesconhecido; }
    public String getRepresentacaoVisual() { return representacaoVisual; }
    public String getObservacoes() { return observacoes; }
    public String getPersonagem1() { return personagem1; }
    public String getPersonagem2() { return personagem2; }
    public String getPersonagem3() { return personagem3; }
    public String getFragmentoTexto1() { return fragmentoTexto1; }
    public String getFragmentoTexto2() { return fragmentoTexto2; }
    public String getFragmentoTexto3() { return fragmentoTexto3; }
    public String getFragmentoTexto4() { return fragmentoTexto4; }
    public String getFragmentoTexto5() { return fragmentoTexto5; }
    public String getFragmentoTexto6() { return fragmentoTexto6; }
}
