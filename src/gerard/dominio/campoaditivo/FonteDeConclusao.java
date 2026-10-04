package gerard.dominio.campoaditivo;

/**
 * Quem observa, na sua própria representação, que a modelagem foi concluída (diagrama azul).
 * Cada plataforma implementa do seu jeito (o controlador de conclusão do desktop, o estado dos
 * serviços da web); a decisão de encerrar a tentativa é única e pertence a ela.
 */
public interface FonteDeConclusao {
    boolean isConcluida();
}
