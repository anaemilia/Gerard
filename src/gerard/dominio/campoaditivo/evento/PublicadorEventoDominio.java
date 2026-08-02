package gerard.dominio.campoaditivo.evento;

/**
 * Fronteira entre domínio e infraestrutura: o objeto de domínio publica,
 * nunca persiste. Quem implementa esta interface decide como armazenar,
 * indexar ou exportar o evento — o domínio não sabe disso, e não precisa
 * saber.
 *
 * Um PapelQuantitativo funciona normalmente sem infraestrutura de eventos
 * anexada, mas nunca recebe {@code null} para isso — usa o Null Object
 * {@link #NENHUM}, para que o objeto de domínio não precise checar
 * nulidade em nenhum ponto interno.
 */
public interface PublicadorEventoDominio {

    void publicar(EventoDominio evento);

    /** Null Object: publica descartando, sem custar uma checagem de nulidade a quem chama. */
    PublicadorEventoDominio NENHUM = evento -> { };
}
