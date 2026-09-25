package gerard.pesquisador.log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Backend central de log de pesquisa (POST/GET /api/pesquisa/log,
 * ServidorPrototipoWeb): qualquer instância do Gérard (desktop de um
 * pesquisador, ou outro servidor web) envia seu log local (mesmo formato
 * TSV/JSON de EventoLogGerard) via API; aqui, e só aqui, esses dados são
 * traduzidos para linhas de banco. O lado que envia nunca fala com o banco
 * diretamente — só TSV ou JSON, como pedido explicitamente pela
 * pesquisadora.
 *
 * SQLite por enquanto (zero infraestrutura nova para validar o desenho);
 * a persistência real em produção (disco persistente vs. Postgres
 * gerenciado) é decisão de infraestrutura separada, ainda em aberto. A
 * camada é só JDBC puro — trocar de banco é trocar a URL de conexão, sem
 * mudar o schema nem os métodos abaixo.
 */
public final class RepositorioEventosLogPesquisa {
    private final Connection conexao;

    /**
     * Uma única conexão persistente, reutilizada por todos os métodos
     * (sincronizados). Abrir uma conexão nova por linha — como era antes —
     * é o gargalo que derruba conexões HTTP sob carga real de múltiplos
     * clientes: cada handshake de conexão custa caro, e um lote de N
     * eventos chegava a abrir N conexões sequenciais segurando o mesmo
     * lock. Uma conexão só, com o lote gravado numa única transação,
     * resolve os dois problemas de uma vez.
     */
    public RepositorioEventosLogPesquisa(String caminhoArquivoBanco) {
        String urlJdbc = "jdbc:sqlite:" + caminhoArquivoBanco;
        try {
            this.conexao = DriverManager.getConnection(urlJdbc);
        } catch (SQLException ex) {
            throw new IllegalStateException("não foi possível abrir o banco de log de pesquisa", ex);
        }
        criarTabelaSeNecessario();
    }

    private void criarTabelaSeNecessario() {
        String sql = "CREATE TABLE IF NOT EXISTS eventos_log_pesquisa ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "origem_instancia TEXT, "
                + "recebido_em TEXT, "
                + "timestamp TEXT, "
                + "sessao TEXT, "
                + "usuario TEXT, "
                + "agente_da_acao TEXT, "
                + "problema TEXT, "
                + "situacao_versao_id TEXT, "
                + "situacao_grupo_id TEXT, "
                + "idioma_situacao TEXT, "
                + "tentativa TEXT, "
                + "tarefa TEXT, "
                + "ce TEXT, "
                + "instrumento_organizacao TEXT, "
                + "instrumento_artefato TEXT, "
                + "funcao_do_artefato TEXT, "
                + "objeto TEXT, "
                + "regras TEXT, "
                + "categoria TEXT, "
                + "enunciado TEXT, "
                + "origem_evento TEXT, "
                + "detalhes TEXT, "
                + "tipo_acao_interacao TEXT, "
                + "propriedade_acao TEXT, "
                + "mudanca_observavel TEXT, "
                + "tentativa_numero_situacao TEXT, "
                + "invariante_origem TEXT, "
                + "invariante_codigo TEXT, "
                + "invariante_simbolico TEXT, "
                + "invariante_observacao TEXT, "
                + "natureza_acao TEXT, "
                + "efeito_acao TEXT, "
                + "invariante_sugestao_adotada TEXT, "
                + "action_id TEXT, "
                + "rejection_sequence_id TEXT)";
        try (Statement st = conexao.createStatement()) {
            st.execute(sql);
        } catch (SQLException ex) {
            throw new IllegalStateException("não foi possível preparar o banco de log de pesquisa", ex);
        }
    }

    private static final String SQL_INSERIR = "INSERT INTO eventos_log_pesquisa ("
            + "origem_instancia, recebido_em, timestamp, sessao, usuario, agente_da_acao, problema, "
            + "situacao_versao_id, situacao_grupo_id, idioma_situacao, tentativa, tarefa, ce, "
            + "instrumento_organizacao, instrumento_artefato, funcao_do_artefato, objeto, regras, "
            + "categoria, enunciado, origem_evento, detalhes, tipo_acao_interacao, propriedade_acao, "
            + "mudanca_observavel, tentativa_numero_situacao, invariante_origem, invariante_codigo, "
            + "invariante_simbolico, invariante_observacao, natureza_acao, efeito_acao, "
            + "invariante_sugestao_adotada, action_id, rejection_sequence_id) VALUES ("
            + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static void preencherParametros(PreparedStatement ps, String origemInstancia, EventoLogGerard evento)
            throws SQLException {
        int i = 1;
        ps.setString(i++, origemInstancia == null ? "" : origemInstancia);
        ps.setString(i++, new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new java.util.Date()));
        ps.setString(i++, evento.getTimestamp());
        ps.setString(i++, evento.getSessao());
        ps.setString(i++, evento.getUsuario());
        ps.setString(i++, evento.getAgenteDaAcao());
        ps.setString(i++, evento.getProblema());
        ps.setString(i++, evento.getSituacaoVersaoId());
        ps.setString(i++, evento.getSituacaoGrupoId());
        ps.setString(i++, evento.getIdiomaSituacao());
        ps.setString(i++, evento.getTentativa());
        ps.setString(i++, evento.getTarefa());
        ps.setString(i++, evento.getCe());
        ps.setString(i++, evento.getInstrumentoOrganizacao());
        ps.setString(i++, evento.getInstrumentoArtefato());
        ps.setString(i++, evento.getFuncaoDoArtefato());
        ps.setString(i++, evento.getObjeto());
        ps.setString(i++, evento.getRegras());
        ps.setString(i++, evento.getCategoria());
        ps.setString(i++, evento.getEnunciado());
        ps.setString(i++, evento.getOrigemEvento());
        ps.setString(i++, evento.getDetalhes());
        ps.setString(i++, evento.getTipoAcaoInteracao());
        ps.setString(i++, evento.getPropriedadeAcao());
        ps.setString(i++, evento.getMudancaObservavel());
        ps.setString(i++, evento.getTentativaNumeroSituacao());
        ps.setString(i++, evento.getInvarianteOrigem());
        ps.setString(i++, evento.getInvarianteCodigo());
        ps.setString(i++, evento.getInvarianteSimbolico());
        ps.setString(i++, evento.getInvarianteObservacao());
        ps.setString(i++, evento.getNaturezaAcao());
        ps.setString(i++, evento.getEfeitoAcao());
        ps.setString(i++, evento.getInvarianteSugestaoAdotada());
        ps.setString(i++, evento.getActionId());
        ps.setString(i++, evento.getRejectionSequenceId());
    }

    /** Traduz e grava um evento vindo de TSV/JSON — único ponto de escrita no banco. */
    public synchronized void inserir(String origemInstancia, EventoLogGerard evento) {
        try (PreparedStatement ps = conexao.prepareStatement(SQL_INSERIR)) {
            preencherParametros(ps, origemInstancia, evento);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalStateException("não foi possível gravar evento de log de pesquisa", ex);
        }
    }

    /** Grava o lote inteiro numa única transação/conexão — não uma por evento. */
    public synchronized int inserirTodos(String origemInstancia, List<EventoLogGerard> eventos) {
        int gravados = 0;
        try {
            conexao.setAutoCommit(false);
            try (PreparedStatement ps = conexao.prepareStatement(SQL_INSERIR)) {
                for (EventoLogGerard evento : eventos) {
                    if (evento == null) continue;
                    preencherParametros(ps, origemInstancia, evento);
                    ps.addBatch();
                    gravados++;
                }
                if (gravados > 0) {
                    ps.executeBatch();
                }
                conexao.commit();
            } catch (SQLException ex) {
                conexao.rollback();
                throw ex;
            } finally {
                conexao.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("não foi possível gravar lote de eventos de log de pesquisa", ex);
        }
        return gravados;
    }

    /**
     * Traduz linhas do banco de volta para JSON (mapas ordenados) — a outra
     * metade da troca de dados pedida (banco -> JSON), para consulta
     * posterior. Filtros nulos/vazios são ignorados.
     */
    public synchronized List<Map<String, Object>> consultar(
            String origemInstancia, String sessao, String usuario, int limite) {
        StringBuilder sql = new StringBuilder("SELECT * FROM eventos_log_pesquisa WHERE 1=1");
        List<String> parametros = new ArrayList<String>();
        if (origemInstancia != null && origemInstancia.trim().length() > 0) {
            sql.append(" AND origem_instancia = ?");
            parametros.add(origemInstancia);
        }
        if (sessao != null && sessao.trim().length() > 0) {
            sql.append(" AND sessao = ?");
            parametros.add(sessao);
        }
        if (usuario != null && usuario.trim().length() > 0) {
            sql.append(" AND usuario = ?");
            parametros.add(usuario);
        }
        sql.append(" ORDER BY id DESC LIMIT ?");

        List<Map<String, Object>> resultado = new ArrayList<Map<String, Object>>();
        try (PreparedStatement ps = conexao.prepareStatement(sql.toString())) {
            int i = 1;
            for (String parametro : parametros) {
                ps.setString(i++, parametro);
            }
            ps.setInt(i, Math.max(1, Math.min(limite, 5000)));
            try (ResultSet rs = ps.executeQuery()) {
                java.sql.ResultSetMetaData meta = rs.getMetaData();
                int colunas = meta.getColumnCount();
                while (rs.next()) {
                    Map<String, Object> linha = new LinkedHashMap<String, Object>();
                    for (int c = 1; c <= colunas; c++) {
                        linha.put(meta.getColumnLabel(c), rs.getObject(c));
                    }
                    resultado.add(linha);
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("não foi possível consultar o log de pesquisa", ex);
        }
        return resultado;
    }

    public synchronized int contar() {
        try (Statement st = conexao.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM eventos_log_pesquisa")) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException ex) {
            throw new IllegalStateException("não foi possível contar o log de pesquisa", ex);
        }
    }
}
