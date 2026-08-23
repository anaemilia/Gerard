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
 * Este piloto é isolado por design para armazenamento/cálculo de valor: a
 * hipótese de reuso mais ampla de PapelQuantitativo como guardião do
 * estado real de um esquema continua não validada — quem guarda e calcula
 * o valor real em produção é EstadoSemanticoCompartilhado (ver Fase B2
 * completa, RELATORIO_MIGRACAO_B2_MAIN_PILOTO_2026-08-07.md), não este
 * piloto diretamente.
 *
 * Duas exceções pontuais, registradas nesta mesma linha de decisão:
 *
 * <ul>
 * <li>2026-08-06: OrigemAcao — só o enum, um tipo de valor sem lógica —
 * passou a ser usado por Main.java para tipar a origem (usuário vs.
 * sistema) do log de interação. Ver Main.registrarLogPorOrigem.</li>
 * <li>2026-08-07: Main.java passou a manter uma instância de
 * PapelQuantitativo (tentativasIncognitaAtual) só para o fluxo de
 * tentativas rejeitadas (REFERENCE.md §4.8 — registrarTentativa/
 * restaurar/estaBloqueadoPorLimiteTentativas). Uso deliberadamente
 * restrito: nunca chama posicionar(...) para armazenar um valor real, só
 * a contagem/bloqueio. Ver Main.garantirTentativasIncognitaAtual e
 * TAREFA_PENDENTE_FLUXO_TENTATIVAS_E_SCAFFOLDING.md.</li>
 * </ul>
 */
public final class PapelQuantitativo {

    private final String chave;
    private final String nomeConceitual;
    private final DominioNumerico dominio;
    private final DescritorRepresentacaoPapel descritorRepresentacao;
    private final PublicadorEventoDominio publicador;

    private ValorNumerico valorAtual;

    // ---- estado do fluxo de tentativas (REFERENCE.md §4.8, cardinalidade
    // ação:evento, Alternativa B) — conhecimento do próprio papel, não de
    // quem chama. Ortogonal a valorAtual: registrarTentativa(...) nunca
    // altera o valor armazenado, só contabiliza o resultado de uma
    // avaliação de correção já feita por quem enxerga a relação completa
    // (RelacaoEstruturalX.diagnosticarValorProposto) — PapelQuantitativo
    // sozinho não tem essa informação (ver posicionar(), que só valida
    // domínio, não correção).
    private String actionIdAtual;
    private int tentativasRejeitadasConsecutivas;
    private boolean bloqueadoPorLimiteTentativas;

    /**
     * Limite fixo de tentativas rejeitadas consecutivas antes da ação se
     * encerrar automaticamente e o papel ficar bloqueado
     * (estaBloqueadoPorLimiteTentativas()) até restaurar() ser chamado —
     * REFERENCE.md §4.8. Fixo em 3, para não repetir a mesma ajuda mais de
     * três vezes ao participante.
     */
    public static final int LIMITE_TENTATIVAS_REJEITADAS_CONSECUTIVAS = 3;

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
                new DescritorRepresentacaoPapel(TipoRepresentacaoAbstrata.FIGURA_RETANGULAR, "", "rotulo.papel.parte1",
                        "explicacao.papel.parte"),
                publicador);
    }

    public static PapelQuantitativo parte2(PublicadorEventoDominio publicador) {
        return new PapelQuantitativo("papel.parte2", "Parte", DominioNumerico.NATURAIS,
                new DescritorRepresentacaoPapel(TipoRepresentacaoAbstrata.FIGURA_RETANGULAR, "", "rotulo.papel.parte2",
                        "explicacao.papel.parte"),
                publicador);
    }

    public static PapelQuantitativo todo(PublicadorEventoDominio publicador) {
        return new PapelQuantitativo("papel.todo", "Todo", DominioNumerico.NATURAIS,
                new DescritorRepresentacaoPapel(TipoRepresentacaoAbstrata.FIGURA_RETANGULAR_ARREDONDADA, "", "rotulo.papel.todo",
                        "explicacao.papel.todo"),
                publicador);
    }

    // ---- identidade e significado semântico ----

    public String getChave() { return chave; }
    public String getNomeConceitual() { return nomeConceitual; }
    public DominioNumerico getDominio() { return dominio; }

    /**
     * Objeto rico (2026-08-18, decisão da usuária): "todo número relativo ou
     * transformação carrega uma lupa. Essa é a regra" — mas a pergunta "eu
     * preciso de lupa?" é comportamento do PRÓPRIO papel, não inferência de
     * quem olha de fora a partir de forma/desenho. A fonte de verdade é
     * matemática, não visual: {@code dominio == DominioNumerico.INTEIROS}
     * (ℤ, aceita negativos) é estritamente mais fundamental que
     * {@code descritorRepresentacao().getForma() == FIGURA_ELIPTICA} — a
     * elipse é só a escolha visual atual que hoje representa esse domínio;
     * o fato semântico real é o domínio numérico do papel.
     *
     * Nome deliberadamente sobre o CONCEITO pedagógico (representação de
     * sinal — eixo dos inteiros, positivo/negativo), não sobre a lupa em si
     * (um detalhe de interação/UI) nem sobre "elipse" (um detalhe de
     * desenho) — o domínio não deve conhecer nenhum dos dois. Cabe à
     * camada de interface decidir COMO atender essa necessidade (lupa,
     * sempre visível, ou qualquer outro mecanismo futuro).
     *
     * Ainda não conectado à Main.java ao vivo — este piloto continua
     * isolado (ver Javadoc da classe); Main.java hoje decide o mesmo fato
     * observando a forma do ElementoVergnaud (ehElementoNumeroRelativo/
     * TipoFiguraDiagrama.ELIPSE), não este método.
     */
    public boolean necessitaRepresentacaoDeSinal() {
        return dominio == DominioNumerico.INTEIROS;
    }

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

    // ---- fluxo de tentativas (REFERENCE.md §4.8) ----

    public boolean estaBloqueadoPorLimiteTentativas() { return bloqueadoPorLimiteTentativas; }
    public int getTentativasRejeitadasConsecutivas() { return tentativasRejeitadasConsecutivas; }
    public String getActionIdAtual() { return actionIdAtual; }

    /**
     * Aciona "restaurar" (REFERENCE.md §4.8): encerra o bloqueio, se houver,
     * e zera a contagem — a próxima tentativa rejeitada abre uma ação nova
     * (novo action_id). Não altera valorAtual nem publica evento: acionar
     * "restaurar" não é, em si, uma tentativa de posicionamento.
     */
    public void restaurar() {
        actionIdAtual = null;
        tentativasRejeitadasConsecutivas = 0;
        bloqueadoPorLimiteTentativas = false;
    }

    /**
     * Registra o resultado de uma tentativa de resposta para este papel,
     * quando quem chama já determinou separadamente se o valor proposto
     * está correto — tipicamente o resultado de
     * RelacaoEstruturalX.diagnosticarValorProposto(...), que é quem de fato
     * sabe avaliar correção (precisa dos três papéis da relação;
     * PapelQuantitativo sozinho só sabe validade de domínio, ver
     * posicionar()). Distinto de posicionar(): aquele valida e armazena um
     * valor; este só contabiliza o resultado de uma avaliação de correção
     * já feita — nunca altera valorAtual. Se a tentativa foi correta, quem
     * chama ainda precisa chamar posicionar(...) separadamente para de fato
     * armazenar o valor; este método só encerra a ação em contagem.
     *
     * Implementa a cardinalidade ação:evento da REFERENCE.md §4.8
     * (Alternativa B): a primeira tentativa rejeitada de uma sequência abre
     * uma ação nova (novo action_id); tentativas seguintes, ainda
     * rejeitadas, correlacionam ao mesmo action_id; ao atingir
     * LIMITE_TENTATIVAS_REJEITADAS_CONSECUTIVAS, a ação se encerra e o
     * papel fica bloqueado (estaBloqueadoPorLimiteTentativas()) até
     * restaurar() ser chamado — quem chama deve, então, mostrar alguma
     * ajuda ao participante (conteúdo concreto ainda não decidido, ver
     * TAREFA_PENDENTE_FLUXO_TENTATIVAS_E_SCAFFOLDING.md — pendência
     * separada, não resolvida por este método). Uma tentativa correta zera
     * a contagem e encerra a ação, sem bloqueio.
     *
     * Só conta tentativas do participante (origem == ORIGEM_USUARIO) — um
     * recálculo do sistema não é uma "tentativa" do participante e não
     * deve consumir nem alterar esta contagem; chamadas com outra origem
     * não fazem nada e devolvem false.
     *
     * Se o papel já está bloqueado, a tentativa não é avaliada nem contada
     * de novo — só publica um evento REJEITADO com diagnóstico
     * BLOQUEADO_AGUARDANDO_RESTAURACAO, para preservar o registro de que o
     * participante tentou de novo enquanto bloqueado, sem inflar a
     * contagem além do limite.
     *
     * @param diagnostico o diagnóstico de correção já calculado por quem
     *        chama; {@link Optional#empty()} significa correto — mesma
     *        convenção de diagnosticarValorProposto
     * @return true se esta chamada fez o limite ser atingido agora
     */
    public boolean registrarTentativa(Optional<DiagnosticoErroPapel> diagnostico, OrigemAcao origem,
            ContextoAcao contexto, ValorNumerico valorProposto) {
        if (origem != OrigemAcao.ORIGEM_USUARIO) {
            return false;
        }
        String estado = descreverEstadoAtual();
        if (bloqueadoPorLimiteTentativas) {
            DiagnosticoErroPapel bloqueio = new DiagnosticoErroPapel(
                    TipoErroPapel.BLOQUEADO_AGUARDANDO_RESTAURACAO,
                    "erro.papel.bloqueadoAguardandoRestauracao", null,
                    "correcao.papel.acionarRestaurar");
            publicar(new EventoPapelQuantitativo(TipoEventoPapel.VALOR_REJEITADO, origem, contexto, chave,
                    estado, estado, formatarValorProposto(valorProposto),
                    ResultadoAcao.REJEITADO, bloqueio, actionIdAtual));
            return false;
        }
        boolean correto = diagnostico == null || !diagnostico.isPresent();
        if (correto) {
            actionIdAtual = null;
            tentativasRejeitadasConsecutivas = 0;
            return false;
        }
        if (actionIdAtual == null) {
            actionIdAtual = java.util.UUID.randomUUID().toString();
            tentativasRejeitadasConsecutivas = 0;
        }
        tentativasRejeitadasConsecutivas++;
        boolean atingiuLimiteAgora = tentativasRejeitadasConsecutivas >= LIMITE_TENTATIVAS_REJEITADAS_CONSECUTIVAS;
        if (atingiuLimiteAgora) {
            bloqueadoPorLimiteTentativas = true;
        }
        publicar(new EventoPapelQuantitativo(TipoEventoPapel.VALOR_REJEITADO, origem, contexto, chave,
                estado, estado, formatarValorProposto(valorProposto),
                ResultadoAcao.REJEITADO, diagnostico.get(), actionIdAtual));
        return atingiuLimiteAgora;
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
