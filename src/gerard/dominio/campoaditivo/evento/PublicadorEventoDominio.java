package gerard.dominio.campoaditivo.evento;

/**
 * Fronteira entre domínio e infraestrutura: o objeto de domínio publica,
 * nunca persiste. Quem implementa esta interface decide como armazenar,
 * indexar ou exportar o evento — o domínio não sabe disso, e não precisa
 * saber. Um PapelQuantitativo funciona normalmente com publicador nulo (uso
 * isolado, sem infraestrutura anexada) — ver TestePilotoPapelQuantitativo.
 */
public interface PublicadorEventoDominio {
    void publicar(EventoDominio evento);
}
