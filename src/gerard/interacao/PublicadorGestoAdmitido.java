package gerard.interacao;

import java.util.function.BooleanSupplier;

/**
 * Fronteira de infraestrutura: entrega o gesto ao publicador real somente
 * quando quem conhece a regra de persistência (a tentativa em curso) o
 * admite. Não interpreta o gesto.
 */
public final class PublicadorGestoAdmitido implements PublicadorGestoInteracao {
    private final PublicadorGestoInteracao destino;
    private final BooleanSupplier admissao;

    public PublicadorGestoAdmitido(PublicadorGestoInteracao destino, BooleanSupplier admissao) {
        this.destino = destino == null ? PublicadorGestoInteracao.NENHUM : destino;
        this.admissao = admissao == null ? () -> true : admissao;
    }

    @Override
    public void publicar(RegistroGestoInteracao registro) {
        if (admissao.getAsBoolean()) {
            destino.publicar(registro);
        }
    }
}
