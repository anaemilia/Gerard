package gerard.agente.modelador;

/**
 * Retrato do perfil do usuário num instante, para o log de auditoria.
 *
 * Nem todo campo pedido no schema de referência existe no sistema real
 * hoje: {@code ModeloUsuario}/{@code PerfilAluno}/{@code PerfilAprendizagem}
 * não guardam nível global, diagnóstico corrente, estratégia predominante
 * nem confiança de modelo — nenhum agente calcula isso ainda. Os campos que
 * SÃO reais (contagem de erro/acerto) vêm dos registros factuais por tarefa
 * e dos agregados que o serviço de auditoria calcula
 * observando os eventos desta sessão (não um estado que o AgenteModelador
 * guarde internamente).
 */
public final class ProfileSnapshot {
    private final Integer totalErros;
    private final Integer errosConsecutivos;
    private final Integer errosCategoria;
    private final Integer errosPosicionamento;
    private final Integer errosSinal;
    private final Integer errosCalculo;
    private final Integer totalAcertos;
    private final Integer acertosConsecutivos;
    private final Integer ajudasUsadas;
    private final String ultimaAvaliacao;
    private final String ultimaFamiliaAcao;
    private final String motivoCamposIndisponiveis;

    public ProfileSnapshot(Integer totalErros, Integer errosConsecutivos, Integer errosCategoria,
            Integer errosPosicionamento, Integer errosSinal, Integer errosCalculo, Integer totalAcertos,
            Integer acertosConsecutivos, Integer ajudasUsadas, String ultimaAvaliacao, String ultimaFamiliaAcao,
            String motivoCamposIndisponiveis) {
        this.totalErros = totalErros;
        this.errosConsecutivos = errosConsecutivos;
        this.errosCategoria = errosCategoria;
        this.errosPosicionamento = errosPosicionamento;
        this.errosSinal = errosSinal;
        this.errosCalculo = errosCalculo;
        this.totalAcertos = totalAcertos;
        this.acertosConsecutivos = acertosConsecutivos;
        this.ajudasUsadas = ajudasUsadas;
        this.ultimaAvaliacao = ultimaAvaliacao;
        this.ultimaFamiliaAcao = ultimaFamiliaAcao;
        this.motivoCamposIndisponiveis = motivoCamposIndisponiveis;
    }

    public Integer getTotalErros() { return totalErros; }
    public Integer getErrosConsecutivos() { return errosConsecutivos; }
    public Integer getErrosCategoria() { return errosCategoria; }
    public Integer getErrosPosicionamento() { return errosPosicionamento; }
    public Integer getErrosSinal() { return errosSinal; }
    public Integer getErrosCalculo() { return errosCalculo; }
    public Integer getTotalAcertos() { return totalAcertos; }
    public Integer getAcertosConsecutivos() { return acertosConsecutivos; }
    public Integer getAjudasUsadas() { return ajudasUsadas; }
    public String getUltimaAvaliacao() { return ultimaAvaliacao; }
    public String getUltimaFamiliaAcao() { return ultimaFamiliaAcao; }

    /**
     * Explica por que nível_global/diagnóstico_atual/estratégia_predominante/
     * confiança_do_modelo (pedidos no schema de referência) não aparecem
     * aqui: nenhum desses campos existe em ModeloUsuario/PerfilAluno/
     * PerfilAprendizagem hoje.
     */
    public String getMotivoCamposIndisponiveis() {
        return motivoCamposIndisponiveis;
    }
}
