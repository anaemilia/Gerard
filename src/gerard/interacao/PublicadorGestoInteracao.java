package gerard.interacao;

/** Porta de saída; persiste um fato já produzido, sem interpretá-lo. */
public interface PublicadorGestoInteracao {
    PublicadorGestoInteracao NENHUM = new PublicadorGestoInteracao() {
        @Override
        public void publicar(RegistroGestoInteracao registro) {
            // Ausência explícita de infraestrutura de persistência.
        }
    };

    void publicar(RegistroGestoInteracao registro);
}
