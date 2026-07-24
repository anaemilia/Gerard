package gerard.agente.modelousuario;

/**
 * Dimensão 5 do Modelo do Usuário (Quadro 5.60): diagnóstico de uma tentativa
 * de tarefa. É a entrada principal usada pelo Agente Modelador para inferir
 * regras (J48.PART + APRIORI). Ver gerard-modelo-usuario/SKILL.md e
 * gerard-ajuda-adaptativa/references/agente-modelador.md.
 *
 * Esta classe não calcula probabilidadeSaberConteudo (teorema de Bayes) nem
 * decide internalizado a partir dela — isso é responsabilidade do Agente
 * Modelador, que ainda não existe.
 *
 * "Tarefa" aqui é (Quadro 5.60) uma referência à Ação Instrumental completa
 * (ver gerard-log-acao-instrumental) — regraDeAcao é o primeiro pedaço dessa
 * referência trazido pra cá (a Tarefa de Interação de Shneiderman, ex.:
 * POSICIONAR, SELECIONAR — ver agente-monitor.md).
 *
 * "invariante" (o teorema-em-ato mobilizado) — resolvido em 2026-07-23:
 * nenhum agente calcula automaticamente qual invariante uma ação mobiliza
 * (isso segue de fora de propósito), mas o pesquisador já atribui um, por
 * ação, via catálogo fechado ou forma simbólica nova (ver
 * TelaArtefatoExplicativo) — decisão humana direta, não um palpite que
 * precise de curadoria posterior como nivelConceitualEstimado/Curado abaixo.
 */
public class DiagnosticoTarefa {
    private final String tarefa;
    private String regraDeAcao;
    private NivelSuporte suporte;
    private boolean internalizado;
    private double probabilidadeSaberConteudo;
    private String dificuldadeAutorrelatada;
    private String explicacaoElemento;
    private String explicacaoGeral;
    private NivelConceitualExplicacao nivelConceitualEstimado;
    private NivelConceitualExplicacao nivelConceitualCurado;
    private String invarianteOrigem;
    private String invarianteCodigo;
    private String invarianteSimbolico;
    private String invarianteObservacao;

    public DiagnosticoTarefa(String tarefa) {
        this.tarefa = tarefa;
    }

    public String getTarefa() {
        return tarefa;
    }

    public String getRegraDeAcao() {
        return regraDeAcao;
    }

    public void setRegraDeAcao(String regraDeAcao) {
        this.regraDeAcao = regraDeAcao;
    }

    public NivelSuporte getSuporte() {
        return suporte;
    }

    public void setSuporte(NivelSuporte suporte) {
        this.suporte = suporte;
    }

    public boolean isInternalizado() {
        return internalizado;
    }

    public void setInternalizado(boolean internalizado) {
        this.internalizado = internalizado;
    }

    public double getProbabilidadeSaberConteudo() {
        return probabilidadeSaberConteudo;
    }

    public void setProbabilidadeSaberConteudo(double probabilidadeSaberConteudo) {
        this.probabilidadeSaberConteudo = probabilidadeSaberConteudo;
    }

    /**
     * Autorrelato do usuário sobre este caso (FACIL/INTERMEDIARIA/DIFICIL),
     * capturado no Artefato Explicativo (TelaArtefatoExplicativo) — não faz
     * parte do Quadro 5.60 original, é uma extensão decidida com o usuário
     * em 2026-07-23 a partir de achado da tese de Queiroz (2012): a
     * dificuldade/justificativa autorrelatada por elemento é o análogo
     * escrito da entrevista pós-tarefa usada na pesquisa original. Nem todo
     * caso tem autorrelato (o artefato é opcional e posterior à ação), por
     * isso pode ficar vazio.
     */
    public String getDificuldadeAutorrelatada() {
        return dificuldadeAutorrelatada;
    }

    public void setDificuldadeAutorrelatada(String dificuldadeAutorrelatada) {
        this.dificuldadeAutorrelatada = dificuldadeAutorrelatada;
    }

    /** Justificativa escrita pelo usuário para este elemento específico, ver getDificuldadeAutorrelatada. */
    public String getExplicacaoElemento() {
        return explicacaoElemento;
    }

    public void setExplicacaoElemento(String explicacaoElemento) {
        this.explicacaoElemento = explicacaoElemento;
    }

    /**
     * Explicação geral da estratégia de modelagem na tentativa em que este
     * caso ocorreu — a mesma string se repete em todos os casos dessa
     * tentativa (replicação aceita por decisão do usuário em 2026-07-23,
     * não normalizada por ora).
     */
    public String getExplicacaoGeral() {
        return explicacaoGeral;
    }

    public void setExplicacaoGeral(String explicacaoGeral) {
        this.explicacaoGeral = explicacaoGeral;
    }

    /**
     * Palpite automático (AnalisadorNivelConceitual) sobre o nível de
     * conceitualização linguística de explicacaoElemento — sinal fraco, ver
     * NivelConceitualExplicacao. Nunca usar para inferência de regras sem
     * antes checar nivelConceitualCurado.
     */
    public NivelConceitualExplicacao getNivelConceitualEstimado() {
        return nivelConceitualEstimado;
    }

    public void setNivelConceitualEstimado(NivelConceitualExplicacao nivelConceitualEstimado) {
        this.nivelConceitualEstimado = nivelConceitualEstimado;
    }

    /**
     * Nível confirmado ou corrigido por um pesquisador — nulo até a
     * curadoria acontecer (ver aba de curadoria na Visão de Pesquisador).
     * Só este campo, quando preenchido, deve alimentar
     * InferenciaRegrasModelador — o estimado é só sugestão.
     */
    public NivelConceitualExplicacao getNivelConceitualCurado() {
        return nivelConceitualCurado;
    }

    public void setNivelConceitualCurado(NivelConceitualExplicacao nivelConceitualCurado) {
        this.nivelConceitualCurado = nivelConceitualCurado;
    }

    /** "CATALOGO" (escolhido de uma lista fechada) ou "PESQUISADOR" (forma simbólica nova), ver TelaArtefatoExplicativo. */
    public String getInvarianteOrigem() {
        return invarianteOrigem;
    }

    public void setInvarianteOrigem(String invarianteOrigem) {
        this.invarianteOrigem = invarianteOrigem;
    }

    /** Identificador estável do invariante (ex.: "COMP_CARDINAL_TODO", ou "PERSONALIZADO" quando invarianteOrigem é PESQUISADOR). Insumo de InferenciaRegrasModelador. */
    public String getInvarianteCodigo() {
        return invarianteCodigo;
    }

    public void setInvarianteCodigo(String invarianteCodigo) {
        this.invarianteCodigo = invarianteCodigo;
    }

    /** Expressão simbólica do invariante (ex.: "Card(Todo) = Card(Parte₁) + Card(Parte₂)"). */
    public String getInvarianteSimbolico() {
        return invarianteSimbolico;
    }

    public void setInvarianteSimbolico(String invarianteSimbolico) {
        this.invarianteSimbolico = invarianteSimbolico;
    }

    /** Observação livre do pesquisador sobre o invariante atribuído a este caso. */
    public String getInvarianteObservacao() {
        return invarianteObservacao;
    }

    public void setInvarianteObservacao(String invarianteObservacao) {
        this.invarianteObservacao = invarianteObservacao;
    }
}
