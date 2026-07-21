package gerard.agente.modelousuario;

import java.util.HashMap;
import java.util.Map;

/**
 * Repositório compartilhado de Modelo do Usuário mencionado em
 * gerard-ajuda-adaptativa/SKILL.md ("Repositórios de dados compartilhados"):
 * escrito pelo Agente Modelador, consultado pelo Agente ZDP. Nenhum dos dois
 * agentes existe ainda.
 *
 * Guarda em memória, por id de usuário (mesmo identificador do campo
 * "usuario" do log — ver gerard-log-acao-instrumental/SKILL.md). Sem
 * persistência em disco: ainda não há consumidor que exija isso.
 */
public class RepositorioModeloUsuario {
    private final Map<String, ModeloUsuario> modelosPorId = new HashMap<String, ModeloUsuario>();

    public ModeloUsuario obterOuCriar(String idUsuario) {
        ModeloUsuario modelo = modelosPorId.get(idUsuario);
        if (modelo == null) {
            modelo = new ModeloUsuario(idUsuario);
            modelosPorId.put(idUsuario, modelo);
        }
        return modelo;
    }

    public ModeloUsuario obter(String idUsuario) {
        return modelosPorId.get(idUsuario);
    }
}
