package gerard.dominio.campoaditivo;

import gerard.dominio.campoaditivo.evento.EventoPapelQuantitativo;
import gerard.dominio.campoaditivo.evento.PublicadorEventoDominio;
import gerard.dominio.campoaditivo.evento.ResultadoAcao;
import gerard.dominio.campoaditivo.evento.TipoEventoPapel;
import gerard.semantica.numero.DominioNumerico;
import gerard.semantica.numero.ValorDesconhecido;
import gerard.semantica.numero.ValorNumerico;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Objeto piloto da nova arquitetura do GERARD.
 *
 * Representa um papel quantitativo do campo aditivo — um lugar num esquema
 * (esta classe já foi reutilizada, sem alteração, para Composição de
 * Medidas — Parte1/Parte2/Todo — e Transformação de Medidas — EstadoInicial/
 * Transformacao/EstadoFinal). Consolida num único objeto o que antes estava
 * espalhado entre matemática/validação, compatibilidade, mensagem de erro,
 * feedback pedagógico e eventos.
 *
 * <b>Hipótese arquitetural ainda não validada</b> (ver relatório técnico,
 * seção "Generalidade de PapelQuantitativo"): a reutilização bem-sucedida em
 * dois esquemas NÃO comprova que esta classe sirva para qualquer papel do
 * campo aditivo. Comparação de Medidas (Referido/Referendo/Valor Relativo)
 * e outros esquemas ainda não foram testados — a generalização só poderá
 * ser afirmada depois de testes específicos com esses casos.
 *
 * Reaproveita deliberadamente DominioNumerico/ValorNumerico de
 * gerard.semantica.numero — as auditorias anteriores já identificaram esses
 * dois como objetos já corretos do sistema por esta mesma régua.
 *
 * Este piloto é isolado por design: PapelQuantitativo (e o restante da
 * arquitetura rica — RelacaoEstrutural*, ResultadoCalculo,
 * DiagnosticoErroPapel) não é referenciado por Main.java nem por nenhum
 * caminho de produção.
 *
 * Exceção pontual, registrada em 2026-08-06: OrigemAcao — só o enum, um
 * tipo de valor sem lógica — passou a ser usado por Main.java para tipar
 * a origem (usuário vs. sistema) do log de interação, no lugar da
 * distinção implícita por nome de método que existia antes. Ver
 * Main.registrarLogPorOrigem. Essa é a única peça deste pacote referenciada
 * fora do piloto; a hipótese de reuso mais ampla continua não validada.
 */
public final class PapelQuantitativo {

    private final String chave;
    private final String nomeConceitual;
    private final DominioNumerico dominio;
    private final DescritorRepresentacaoPapel descritorRepresentacao;
    private final PublicadorEventoDominio publicador;

    private ValorNumerico valorAtual;

    public PapelQuantitativo(String chave, String nomeConceitual, DominioNumerico dominio,
                              DescritorRepresentacaoPapel descritorRepresentacao,
                              PublicadorEventoDominio publicador) {
        this.chave = Objects.requireNonNull(chave, "chave não pode ser nula").trim();
        if (this.chave.isEmpty()) {
            throw new IllegalArgumentException("chave não pode ser vazia");
        }
        this.nomeConceitual = nomeConceitual == null ? "" : nomeConceitual.trim();
        this.dominio = dominio == null ? DominioNumerico.NATURAIS : dominio;
        this.descritorRepresentacao = Objects.requireNonNull(descritorRepresentacao,
                "descritor de representação não pode ser nulo — todo papel sabe o significado de sua representação");
        // Null Object: nunca guardamos null aqui, para que publicar(...) não precise checar nulidade.
        this.publicador = publicador == null ? PublicadorEventoDominio.NENHUM : publicador;
        this.valorAtual = new ValorDesconhecido(this.dominio);
    }

    // ---- fábricas: Parte1/Parte2/Todo são INSTÂNCIAS desta classe, não subclasses ----

    public static PapelQuantitativo parte1(PublicadorEventoDominio publicador) {
        return new PapelQuantitativo("papel.parte1", "Parte", DominioNumerico.NATURAIS,
                new DescritorRepresentacaoPapel(TipoRepresentacaoAbstrata.FIGURA_RETANGULAR, "rotulo.papel.parte1"),
                publicador);
    }

    public static PapelQuantitativo parte2(PublicadorEventoDominio publicador) {
        return new PapelQuantitativo("papel.parte2", "Parte", DominioNumerico.NATURAIS,
                new DescritorRepresentacaoPapel(TipoRepresentacaoAbstrata.FIGURA_RETANGULAR, "rotulo.papel.parte2"),
                publicador);
    }

    public static PapelQuantitativo todo(PublicadorEventoDominio publicador) {
        return new PapelQuantitativo("papel.todo", "Todo", DominioNumerico.NATURAIS,
                new DescritorRepresentacaoPapel(TipoRepresentacaoAbstrata.FIGURA_RETANGULAR_ARREDONDADA, "rotulo.papel.todo"),
                publicador);
    }

    // ---- identidade e significado semântico ----

    public String getChave() { return chave; }
    public String getNomeConceitual() { return nomeConceitual; }
    public DominioNumerico getDominio() { return dominio; }

    // ---- representação (descritor abstrato, não desenho) ----

    public DescritorRepresentacaoPapel descritorRepresentacao() { return descritorRepresentacao; }

    // ---- conhecimento matemático / regras de validação ----

    public boolean aceita(ValorNumerico valor) {
        return valor != null && (!valor.ehConhecido() || dominio.aceita(valor.valorOuNull()));
    }

    /**
     * Compara IDENTIDADE semântica (mesma chave) — não uma noção mais ampla
     * de "compatibilidade". Dois papéis diferentes podem legitimamente
     * participar da mesma relação estrutural sem serem a mesma identidade
     * semântica; uma futura noção de compatibilidade real dependeria de
     * esquema, situação-problema, tentativa, grandeza, domínio numérico e
     * relação estrutural permitida — nenhuma dessas dimensões é avaliada
     * aqui. Nome escolhido deliberadamente para não prometer mais do que o
     * método faz (ver relatório técnico, seção "Revisão de compativelCom").
     */
    public boolean mesmaIdentidadeSemantica(PapelQuantitativo outro) {
        return outro != null && this.chave.equals(outro.chave);
    }

    // ---- comportamento / regras de negócio ----

    public boolean estaPreenchido() {
        return valorAtual.ehConhecido();
    }

    public boolean ehIncognita() {
        return !valorAtual.ehConhecido();
    }

    public ValorNumerico valorAtual() { return valorAtual; }

    /** Posicionamento por ação do estudante, sem contexto de rastreabilidade explícito. */
    public Optional<DiagnosticoErroPapel> posicionar(ValorNumerico valorProposto) {
        return posicionar(valorProposto, OrigemAcao.ORIGEM_USUARIO, ContextoAcao.NAO_INFORMADO);
    }

    /**
     * Tenta posicionar um valor neste papel. O próprio objeto decide o que
     * aconteceu — quem chama este método só recebe o resultado, nunca o
     * reinterpreta. {@code origem} nunca deve ser {@code ORIGEM_USUARIO}
     * quando o valor vem de um cálculo automático (ver
     * RelacaoEstruturalTransformacao.aplicar) — é assim que a integridade
     * dos dados de pesquisa é preservada.
     */
    public Optional<DiagnosticoErroPapel> posicionar(ValorNumerico valorProposto, OrigemAcao origem,
                                                       ContextoAcao contexto) {
        String estadoAnterior = descreverEstadoAtual();
        boolean aceito = aceita(valorProposto);
        if (aceito) {
            this.valorAtual = valorProposto;
            publicar(new EventoPapelQuantitativo(TipoEventoPapel.VALOR_POSICIONADO, origem, contexto, chave,
                    estadoAnterior, descreverEstadoAtual(), formatarValorProposto(valorProposto),
                    ResultadoAcao.ACEITO, null));
            return Optional.empty();
        }
        DiagnosticoErroPapel diagnostico = new DiagnosticoErroPapel(
                TipoErroPapel.VALOR_FORA_DO_DOMINIO,
                "erro.papel.valorForaDoDominio",
                "feedback.papel.valorForaDoDominio",
                "correcao.papel.valorForaDoDominio");
        publicar(new EventoPapelQuantitativo(TipoEventoPapel.VALOR_REJEITADO, origem, contexto, chave,
                estadoAnterior, descreverEstadoAtual(), formatarValorProposto(valorProposto),
                ResultadoAcao.REJEITADO, diagnostico));
        return Optional.of(diagnostico);
    }

    private String descreverEstadoAtual() {
        return valorAtual.ehConhecido() ? valorAtual.formatar(true) : "?";
    }

    private static String formatarValorProposto(ValorNumerico valor) {
        return valor == null ? "null" : valor.formatar(true);
    }

    private void publicar(EventoPapelQuantitativo evento) {
        publicador.publicar(evento);
    }

    // ---- serialização (dados, não infraestrutura de gravação) ----

    public Map<String, Object> paraMapa() {
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("chave", chave);
        mapa.put("nome_conceitual", nomeConceitual);
        mapa.put("dominio", dominio.name());
        mapa.put("forma_representacao_abstrata", descritorRepresentacao.getForma().name());
        mapa.put("simbolo_representacao", descritorRepresentacao.getSimbolo());
        mapa.put("chave_rotulo", descritorRepresentacao.getChaveRotulo());
        mapa.put("valor_atual", valorAtual.ehConhecido() ? valorAtual.valorOuNull() : null);
        mapa.put("preenchido", estaPreenchido());
        return mapa;
    }

    @Override
    public String toString() {
        return chave + "=" + valorAtual.formatar(true);
    }
}
