package gerard.dominio.campoaditivo;

import gerard.campoaditivo.diagrama.modelo.TipoFiguraDiagrama;
import gerard.dominio.campoaditivo.evento.EventoPapelQuantitativo;
import gerard.dominio.campoaditivo.evento.PublicadorEventoDominio;
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
 * Representa um papel quantitativo do campo aditivo (ex.: Parte, Todo,
 * Transformação) consolidando em um único objeto o que hoje está espalhado
 * entre várias camadas:
 * - matemática/validação: gerard.semantica.papel.PapelQuantitativo (versão anêmica);
 * - compatibilidade entre papéis: ScaffoldingQuestionamento (comparação de string);
 * - mensagem de erro: mensagens_*.properties + ServicoLocalizacao (switch externo);
 * - feedback pedagógico: ScaffoldingFeedbackMultissensorialErro (recebe elemento
 *   gráfico, não papel semântico);
 * - eventos: nenhum lugar hoje.
 *
 * Reaproveita deliberadamente DominioNumerico/ValorNumerico de
 * gerard.semantica.numero: as quatro auditorias anteriores já identificaram
 * esses dois como os únicos objetos do sistema já corretos por esta mesma
 * régua — duplicá-los aqui contradiria o princípio que este piloto
 * demonstra. Também reaproveita TipoFiguraDiagrama (já livre de AWT/Swing).
 *
 * Este piloto é isolado por design: não é referenciado por Main.java nem
 * por nenhum caminho de produção. Ver TestePilotoPapelQuantitativo.java
 * para a demonstração executável.
 */
public final class PapelQuantitativo {

    private final String chave;
    private final String nomeConceitual;
    private final DominioNumerico dominio;
    private final RepresentacaoGraficaPapel representacaoGrafica;
    private final PublicadorEventoDominio publicador;

    private ValorNumerico valorAtual;

    public PapelQuantitativo(String chave, String nomeConceitual, DominioNumerico dominio,
                              RepresentacaoGraficaPapel representacaoGrafica,
                              PublicadorEventoDominio publicador) {
        this.chave = Objects.requireNonNull(chave, "chave não pode ser nula").trim();
        if (this.chave.isEmpty()) {
            throw new IllegalArgumentException("chave não pode ser vazia");
        }
        this.nomeConceitual = nomeConceitual == null ? "" : nomeConceitual.trim();
        this.dominio = dominio == null ? DominioNumerico.NATURAIS : dominio;
        this.representacaoGrafica = Objects.requireNonNull(representacaoGrafica,
                "representação gráfica não pode ser nula — todo papel sabe como se apresenta");
        this.publicador = publicador; // pode ser null (uso isolado, sem infraestrutura anexada)
        this.valorAtual = new ValorDesconhecido(this.dominio);
    }

    // ---- fábricas: Parte1/Parte2/Todo são INSTÂNCIAS desta classe, não subclasses ----
    // (a diferença entre "Parte" e "Todo" está nos dados — chave, domínio, rótulo —
    // não no comportamento; é o mesmo conceito de papel quantitativo em dois lugares
    // diferentes do esquema de composição de medidas)

    public static PapelQuantitativo parte1(PublicadorEventoDominio publicador) {
        return new PapelQuantitativo("papel.parte1", "Parte", DominioNumerico.NATURAIS,
                new RepresentacaoGraficaPapel(TipoFiguraDiagrama.RETANGULO, "rotulo.papel.parte1"),
                publicador);
    }

    public static PapelQuantitativo parte2(PublicadorEventoDominio publicador) {
        return new PapelQuantitativo("papel.parte2", "Parte", DominioNumerico.NATURAIS,
                new RepresentacaoGraficaPapel(TipoFiguraDiagrama.RETANGULO, "rotulo.papel.parte2"),
                publicador);
    }

    public static PapelQuantitativo todo(PublicadorEventoDominio publicador) {
        return new PapelQuantitativo("papel.todo", "Todo", DominioNumerico.NATURAIS,
                new RepresentacaoGraficaPapel(TipoFiguraDiagrama.RETANGULO_ARREDONDADO, "rotulo.papel.todo"),
                publicador);
    }

    // ---- identidade e significado semântico ----

    public String getChave() { return chave; }
    public String getNomeConceitual() { return nomeConceitual; }
    public DominioNumerico getDominio() { return dominio; }

    // ---- representação gráfica (descrição, não desenho — ver RepresentacaoGraficaPapel) ----

    public RepresentacaoGraficaPapel representacaoGrafica() { return representacaoGrafica; }

    // ---- conhecimento matemático / regras de validação ----

    /**
     * Regra matemática: um valor só é aceito se satisfizer o domínio numérico
     * deste papel. Decide pelo VALOR em si (magnitude/sinal), não pelo tipo
     * Java que o carrega — um NumeroInteiro negativo é rejeitado por um papel
     * de domínio NATURAIS mesmo que ambos "existam" no universo dos inteiros;
     * o que importa é se o número cabe na restrição deste papel específico.
     */
    public boolean aceita(ValorNumerico valor) {
        return valor != null && (!valor.ehConhecido() || dominio.aceita(valor.valorOuNull()));
    }

    /**
     * Regra semântica: dois papéis são compatíveis quando representam o
     * mesmo conceito (mesma chave). Migrada de
     * ScaffoldingQuestionamento.papeisCompativeis, que hoje decide isto por
     * String.startsWith/equals em vez de perguntar ao próprio papel (ver
     * auditoria Knowledge-Oriented, seção 3.7).
     */
    public boolean compativelCom(PapelQuantitativo outro) {
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

    /**
     * Tenta posicionar um valor neste papel. O próprio objeto decide o que
     * aconteceu — quem chama este método só recebe o resultado, nunca o
     * reinterpreta:
     * - se o valor pertence ao domínio, o papel aceita, atualiza seu estado
     *   e publica VALOR_POSICIONADO;
     * - caso contrário, permanece inalterado, produz um DiagnosticoErroPapel
     *   (mensagem + feedback pedagógico + sugestão de correção) e publica
     *   VALOR_REJEITADO.
     */
    public Optional<DiagnosticoErroPapel> posicionar(ValorNumerico valorProposto) {
        boolean aceito = aceita(valorProposto);
        if (aceito) {
            this.valorAtual = valorProposto;
            publicar(new EventoPapelQuantitativo(TipoEventoPapel.VALOR_POSICIONADO, chave,
                    formatarValorProposto(valorProposto), true, null));
            return Optional.empty();
        }
        DiagnosticoErroPapel diagnostico = new DiagnosticoErroPapel(
                TipoErroPapel.VALOR_FORA_DO_DOMINIO,
                "erro.papel.valorForaDoDominio",
                "feedback.papel.valorForaDoDominio",
                "correcao.papel.valorForaDoDominio");
        publicar(new EventoPapelQuantitativo(TipoEventoPapel.VALOR_REJEITADO, chave,
                formatarValorProposto(valorProposto), false, diagnostico));
        return Optional.of(diagnostico);
    }

    private static String formatarValorProposto(ValorNumerico valor) {
        return valor == null ? "null" : valor.formatar(true);
    }

    private void publicar(EventoPapelQuantitativo evento) {
        if (publicador != null) {
            publicador.publicar(evento);
        }
    }

    // ---- serialização (dados, não infraestrutura de gravação) ----

    /**
     * Representação serializável deste papel — o objeto sabe descrever a si
     * mesmo; quem grava em disco/banco é responsabilidade da infraestrutura,
     * nunca deste método (skill Semantic Event Logging: o domínio não grava
     * logs diretamente).
     */
    public Map<String, Object> paraMapa() {
        Map<String, Object> mapa = new LinkedHashMap<>();
        mapa.put("chave", chave);
        mapa.put("nome_conceitual", nomeConceitual);
        mapa.put("dominio", dominio.name());
        mapa.put("forma_grafica", representacaoGrafica.getForma().name());
        mapa.put("chave_rotulo", representacaoGrafica.getChaveRotulo());
        mapa.put("valor_atual", valorAtual.ehConhecido() ? valorAtual.valorOuNull() : null);
        mapa.put("preenchido", estaPreenchido());
        return mapa;
    }

    @Override
    public String toString() {
        return chave + "=" + valorAtual.formatar(true);
    }
}
