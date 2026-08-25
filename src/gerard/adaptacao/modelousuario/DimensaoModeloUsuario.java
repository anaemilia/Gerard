package gerard.adaptacao.modelousuario;

/** As cinco dimensões do Modelo do Usuário descritas no Quadro 5.60. */
public enum DimensaoModeloUsuario {
    NIVEL_TAREFAS,
    PARTES_CONHECIMENTO_E_FASES,
    PERFIL_ALUNO,
    PERFIL_APRENDIZAGEM,
    DIAGNOSTICO_TAREFA;

    public boolean ehPerfil() {
        return this == PERFIL_ALUNO || this == PERFIL_APRENDIZAGEM;
    }
}
