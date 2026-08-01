package gerard.dominio.campoaditivo;

import java.util.Objects;

/**
 * Erro específico de um PapelQuantitativo, com significado pedagógico — não
 * um booleano solto. Concentra o que hoje está espalhado entre
 * mensagens_*.properties, ScaffoldingFeedbackMultissensorialErro e
 * MotorRegrasConhecimento (ver auditoria Knowledge-Oriented, seções 3.6-3.8).
 *
 * As chaves aqui são identificadores de mensagem (o mesmo vocabulário de
 * gerard.i18n.ServicoLocalizacao), não o texto final: o objeto de domínio
 * decide QUAL mensagem se aplica; resolver a chave para texto localizado
 * continua sendo responsabilidade da infraestrutura de i18n, que já existe.
 * Este piloto não adiciona entradas a mensagens_*.properties — é uma
 * decisão deliberada de manter o piloto isolado (ver relatório da
 * implementação piloto, seção "decisões arquiteturais").
 */
public final class DiagnosticoErroPapel {

    private final TipoErroPapel tipo;
    private final String chaveMensagem;
    private final String chaveFeedbackPedagogico;
    private final String chaveSugestaoCorrecao;

    public DiagnosticoErroPapel(TipoErroPapel tipo, String chaveMensagem,
                                 String chaveFeedbackPedagogico, String chaveSugestaoCorrecao) {
        this.tipo = Objects.requireNonNull(tipo, "tipo não pode ser nulo");
        this.chaveMensagem = chaveMensagem;
        this.chaveFeedbackPedagogico = chaveFeedbackPedagogico;
        this.chaveSugestaoCorrecao = chaveSugestaoCorrecao;
    }

    public TipoErroPapel getTipo() { return tipo; }
    public String getChaveMensagem() { return chaveMensagem; }
    public String getChaveFeedbackPedagogico() { return chaveFeedbackPedagogico; }
    public String getChaveSugestaoCorrecao() { return chaveSugestaoCorrecao; }

    @Override
    public String toString() {
        return tipo + " [" + chaveMensagem + "]";
    }
}
