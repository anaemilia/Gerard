/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gerard.banco;

import gerard.model.Configuracao;
import gerard.util.ComponenteDaSP;
import gerard.util.ManipularProperties;
import gerard.util.SituacaoProblema;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kecia
 */
public class BancoSingleton {

    private Connection con;
    private Statement stmt;
    //private ResultSet resultSet;
    private String JDBC_DRIVER;
    private String DATABASE_URL;
    private String USERNAME;
    private String PASSWORD;
    private ManipularProperties properties;
    private ArrayList<String[]> codProblema;
    private ArrayList<String[]> codTentativa;
    private int contadorProblemas = 0;
    private int contadorTentativas = 0;
    // private boolean conexaoEstabelecida = false;
    private static BancoSingleton instanciaUnica;
    private int codProblemaTemporario;//para abrir estado salvo
    private int codTentativaTemporario;//para abrir estado salvo
    private String cabecalhoHistorico;

    private BancoSingleton() {
        properties = new ManipularProperties("gerard.propriedades.sql");
        JDBC_DRIVER = properties.getMensagem("JDBC_DRIVER");
        DATABASE_URL = properties.getMensagem("DATABASE_URL");
        USERNAME = properties.getMensagem("USERNAME");
        PASSWORD = properties.getMensagem("PASSWORD");
        try {
            Class.forName(JDBC_DRIVER);

            con = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
            stmt = con.createStatement();
            criarTabelas();
            carregarBase();
            //        conexaoEstabelecida = true;
        } catch (ClassNotFoundException ex) {
            //Logger.getLogger(BancoSingleton.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            //Logger.getLogger(BancoSingleton.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static BancoSingleton getInstancia() {
        if (instanciaUnica == null) {
            instanciaUnica = new BancoSingleton();
        }
        return instanciaUnica;
    }

    private void criarTabelas() {
        String criarTabela = this.properties.getMensagem("CRIAR_TABELAS");
        String tabelas[] = criarTabela.split(";");
        for (int i = 0; i < tabelas.length; i++) {
            inserirDados(tabelas[i] + ";");
        }

    }

    public Usuario getUsuario(String login) {
        String sql = this.properties.getMensagem("SELECT_USUARIO");
        String query = properties.replaceString(login, sql);

        ResultSet rs = this.consultar(query);
        if (this.resultSet(rs)) {
            Usuario usuario = new Usuario(this.getColuna(rs, 1), this.getColuna(rs, 2),
                    this.getColuna(rs, 3), this.getColuna(rs, 4),
                    this.getColuna(rs, 5), this.getColuna(rs, 6),
                    this.getColuna(rs, 7), this.getColuna(rs, 8), this.getColuna(rs, 9),
                    this.getColuna(rs, 10));
            return usuario;
        }
        return null;

    }

    private ResultSet consultar(String query) {
        try {
            return stmt.executeQuery(query);
        } catch (SQLException ex) {
            Logger.getLogger(BancoSingleton.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("erro consulta");
        }
        return null;
    }

    private boolean inserirDados(String query) {
        try {
            stmt.executeUpdate(query);
            return true;
        } catch (SQLException ex) {
            //System.out.println(query);
            // Logger.getLogger(BancoSingleton.class.getName()).log(Level.SEVERE, null, ex);
          // System.out.println("erro inserir dados");
        }
        return false;
    }

    private boolean resultSet(ResultSet rs) {
        try {
            return rs.next();
        } catch (SQLException ex) {
            Logger.getLogger(BancoSingleton.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private String getColuna(ResultSet rs, int coluna) {
        try {
            return rs.getString(coluna);
        } catch (SQLException ex) {
            Logger.getLogger(BancoSingleton.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private int getColunaInt(ResultSet rs, int coluna) {
        try {
            return rs.getInt(coluna);
        } catch (SQLException ex) {
            Logger.getLogger(BancoSingleton.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    public boolean cadastrarUsuario(Usuario usuario) {
        String sql = this.properties.getMensagem("INSERT_USUARIO");
//INSERT INTO usuarios VALUES (CPF,Senha,Nome,Idade,Sexo,GrauFormacao,Email,NomeEscola,CidadeEscola,EstadoEscola)
        String[] novaString = {usuario.getCpfLogin(),
            usuario.getSenha(),
            usuario.getNome(),
            usuario.getIdade(),
            usuario.getSexo(),
            usuario.getGrauFormacao(),
            usuario.getEmail(),
            usuario.getNomeEscola(),
            usuario.getCidadeEscola(),
            usuario.getEstadoEscola()
        };
        String query = properties.replaceString(novaString, sql);

        return this.inserirDados(query);

    }

    private void carregarBase() {

        String query;
        ResultSet rs;
        //Tabela Objetos
        query = this.properties.getMensagem("EXISTE_OBJETOS");
        rs = this.consultar(query);
        if (rs != null && this.resultSet(rs)) {
            query = this.properties.getMensagem("INSERT_OBJETOS");
            String tabelas[] = query.split(";");
            if (this.getColunaInt(rs, 1) < tabelas.length - 1) {
                //se o numero de dados for menor que o numero de dados do arquivo properties entao inserir os dados do properties
                for (int i = 0; i < tabelas.length - 1; i++) {
                    inserirDados(tabelas[i] + ";");

                }
            }
        }


        //Tabela artefatos
        query = this.properties.getMensagem("EXISTE_ARTEFATOS");
        rs = this.consultar(query);
        if (rs != null && this.resultSet(rs)) {
            query = this.properties.getMensagem("INSERT_ARTEFATOS");
            String tabelas[] = query.split(";");
            if (this.getColunaInt(rs, 1) < tabelas.length - 1) {
                //se o numero de dados for menor que o numero de dados do arquivo properties entao inserir os dados do properties
                for (int i = 0; i < tabelas.length - 1; i++) {
                    inserirDados(tabelas[i] + ";");

                }
            }
        }


        //Tabela erros
        query = this.properties.getMensagem("EXISTE_ERROS");
        rs = this.consultar(query);
        if (rs != null && this.resultSet(rs)) {
            query = this.properties.getMensagem("INSERT_ERROS");
            String tabelas[] = query.split(";");

            if (this.getColunaInt(rs, 1) < tabelas.length - 1) {
                //se o numero de dados for menor que o numero de dados do arquivo properties entao inserir os dados do properties
                for (int i = 0; i < tabelas.length - 1; i++) {
                    inserirDados(tabelas[i] + ";");

                }
            }
        }

        //Tabela problemas
        //  numero = 100;//valor default na dúvida fazer com que o aplicativo excute os inserts
        query = this.properties.getMensagem("EXISTE_PROBLEMAS");
        rs = this.consultar(query);
        if (rs != null && this.resultSet(rs)) {
            query = this.properties.getMensagem("INSERT_PROBLEMAS");
            String tabelas[] = query.split(";");
            if (this.getColunaInt(rs, 1) < tabelas.length - 1) {
                //se o numero de dados for menor que o numero de dados do arquivo properties entao inserir os dados do properties
                for (int i = 0; i < tabelas.length - 1; i++) {
                    inserirDados(tabelas[i] + ";");

                }
            }
        }

        //Tabela COMPONENTES
        //  numero = 100;//valor default na dúvida fazer com que o aplicativo excute os inserts
        query = this.properties.getMensagem("EXISTE_COMPONENTES");
        rs = this.consultar(query);
        if (rs != null && this.resultSet(rs)) {
            query = this.properties.getMensagem("INSERT_COMPONENTES");
            String tabelas[] = query.split(";");
            if (this.getColunaInt(rs, 1) < tabelas.length - 1) {
                //se o numero de dados for menor que o numero de dados do arquivo properties entao inserir os dados do properties
                for (int i = 0; i < tabelas.length - 1; i++) {
                    inserirDados(tabelas[i] + ";");

                }
            }
        }


    }

    private boolean ehNumero(String query) {
        int tamanho = query.length();
        char c;
        for (int i = 0; i < tamanho; i++) {
            c = query.charAt(i);
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    public int getNumeroProblemas() {
        String query = this.properties.getMensagem("EXISTE_PROBLEMAS");
        ResultSet rs = this.consultar(query);
        if (rs != null && this.resultSet(rs)) {
            return getColunaInt(rs, 1);
        }
        return 0;
    }

    public int[] getCodProblemas(String estrutura) {
        if (estrutura.equalsIgnoreCase(SituacaoProblema.aditiva)) {
            return this.getCodProblemasAditivas();
        } else {
            if (estrutura.endsWith(SituacaoProblema.multiplicativa)) {
                return getCodProblemasMultiplicativas();
            }
        }
        return null;
    }

    private int[] getCodProblemasAditivas() {
        //Primeiro: pegar o numero de linhas
        int contLinhas = -1;//=this.getNumeroLinhas(this.properties.getMensagem("SELECT_COUNT_COD_PROBLEMAS"));
        String categorias[] = {SituacaoProblema.composicao, SituacaoProblema.transformacao, SituacaoProblema.comparacao};
        String query = this.properties.getMensagem("SELECT_COUNT_COD_PROBLEMAS");
        query = ManipularProperties.replaceString(categorias, query);
        ResultSet rs = this.consultar(query);
        if (rs != null && this.resultSet(rs)) {
            contLinhas = this.getColunaInt(rs, 1);
        }
        if (contLinhas == -1 || contLinhas == 0)//se deu erro ou nao existe valores na tabela problemas
        {
            return null;
        }

        //Segundo: pegar valores dos codigos das situações problemas que são aditivas
        int cod[] = new int[contLinhas];
        query = this.properties.getMensagem("SELECT_COD_PROBLEMAS");
        query = ManipularProperties.replaceString(categorias, query);
        rs = this.consultar(query);
        if (rs != null) {
            for (int i = 0; this.resultSet(rs); i++) {
                cod[i] = this.getColunaInt(rs, 1);
            }
        }
        return cod;
    }

    private int[] getCodProblemasMultiplicativas() {
        //Primeiro: pegar o numero de linhas
        int contLinhas = -1;//=this.getNumeroLinhas(this.properties.getMensagem("SELECT_COUNT_COD_PROBLEMAS"));
        String categorias[] = {SituacaoProblema.multiplicacao, SituacaoProblema.divisao_partes, SituacaoProblema.divisao_cotas};
        String query = this.properties.getMensagem("SELECT_COUNT_COD_PROBLEMAS");
        query = ManipularProperties.replaceString(categorias, query);
        ResultSet rs = this.consultar(query);
        if (rs != null && this.resultSet(rs)) {
            contLinhas = this.getColunaInt(rs, 1);
        }
        if (contLinhas == -1 || contLinhas == 0)//se deu erro ou nao existe valores na tabela problemas
        {
            return null;
        }

        //Segundo: pegar valores dos codigos das situações problemas que são multiplicativas
        int cod[] = new int[contLinhas];
        query = this.properties.getMensagem("SELECT_COD_PROBLEMAS");
        query = ManipularProperties.replaceString(categorias, query);
        rs = this.consultar(query);
        if (rs != null) {
            for (int i = 0; this.resultSet(rs); i++) {
                cod[i] = this.getColunaInt(rs, 1);
            }
        }
        return cod;
    }

    public SituacaoProblema getSituacaoProblema(int codProblema, int numeroComponentes) {

        String query = this.properties.getMensagem("SELECT_PROBLEMAS");
        query = ManipularProperties.replaceString(String.valueOf(codProblema), query);
        ResultSet rs = this.consultar(query);
        //pegando situação problema do banco
        SituacaoProblema sp;// = new SituacaoProblema();
        String colunas[] = new String[5];
        if (rs != null && this.resultSet(rs)) {
            for (int coluna = 0; coluna < 5; coluna++) {
                colunas[coluna] = this.getColuna(rs, coluna + 1);

            }
            sp = new SituacaoProblema(colunas[0], colunas[1], colunas[2], colunas[3], colunas[4], numeroComponentes);

            query = this.properties.getMensagem("SELECT_COMPONENTES");
            query = ManipularProperties.replaceString(String.valueOf(codProblema), query);
            rs = this.consultar(query);
            ArrayList<ComponenteDaSP> componentesSP = new ArrayList<ComponenteDaSP>();
            if (rs != null) {
                while (this.resultSet(rs)) {
                    for (int coluna = 0; coluna < 4; coluna++) {
                        colunas[coluna] = this.getColuna(rs, coluna + 1);
                    }//
                    componentesSP.add(new ComponenteDaSP(colunas[0], Integer.parseInt(colunas[1]), colunas[2], colunas[3]));

                }

                sp.setComponentes(componentesSP);
            } else {
                return null;//nao existe componentes
            }
            return sp;
        }
        return null;
    }

    public int insertTentativa(String cpfUsuuario, int codProblema) {
        String query = this.properties.getMensagem("INSERT_USUARIOS_PROBLEMAS");
        Calendar cal = Calendar.getInstance();
        String data = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(cal.getTime());
        String valores[] = {cpfUsuuario, String.valueOf(codProblema), data};
        query = ManipularProperties.replaceString(valores, query);
        this.inserirDados(query);

        query = this.properties.getMensagem("INSERT_TENTATIVAS");
        query = ManipularProperties.replaceString(valores, query);

        this.inserirDados(query);

        query = this.properties.getMensagem("SELECT_COUNT_TENTATIVAS");
        query = ManipularProperties.replaceString(valores, query);
        ResultSet rs = this.consultar(query);
        if (rs != null & this.resultSet(rs)) {
            return this.getColunaInt(rs, 1);
        }

        return 0;
    }

    public void inserirEtapa(Etapas etapa, String artefato) {
        String query = this.properties.getMensagem("INSERT_ETAPAS");
        String valores[] = {etapa.getdescricao(),
            etapa.getvalor(),
            etapa.getcodTentativa(),
            etapa.getator(),
            etapa.getcodErro(),
            etapa.getcodObjeto(),
            etapa.getcpf(),
            etapa.getcodProblema()
        };
        query = query = ManipularProperties.replaceString(valores, query);
        this.inserirDados(query);

        ///pegar o codigo do artefato
        String codArtefato = "0";
        query = this.properties.getMensagem("SELECT_ARTEFATOS");
        query = query = ManipularProperties.replaceString(artefato, query);
        ResultSet rs = this.consultar(query);
        if (rs != null & this.resultSet(rs)) {
            codArtefato = this.getColuna(rs, 1);
            //
            query = this.properties.getMensagem("SELECT_COUNT_ETAPAS");
            //String novaString[]={ etapa.getcpf(),etapa.getcodProblema(),etapa.getcodTentativa()};
            //query = query = ManipularProperties.replaceString(novaString, query);
            rs = this.consultar(query);
            String codEtapa;
            if (rs != null & this.resultSet(rs)) {
                codEtapa = this.getColuna(rs, 1);
                query = this.properties.getMensagem("INSERT_ETAPAS_ARTEFATOS");
                String novaS[] = {codEtapa, codArtefato};
                query = query = ManipularProperties.replaceString(novaS, query);

                this.inserirDados(query);
            }
        }



        /*   query = this.properties.getMensagem("SELECT_ERROS");
        query = query = ManipularProperties.replaceString(etapa.getcodErro(), query);
        rs = this.consultar(query);
        if (rs != null & this.resultSet(rs)) {
        etapa.setcodErro(this.getColuna(rs, 1));
        }

        query = this.properties.getMensagem("SELECT_OBJETOS");
        query = query = ManipularProperties.replaceString(etapa.getcodObjeto(), query);
        rs = this.consultar(query);
        if (rs != null & this.resultSet(rs)) {
        etapa.setcodObjeto(this.getColuna(rs, 1));
        }*/

    }

    public String getHistorico(String cpf) {
        codProblema = new ArrayList<String[]>();
        codTentativa = new ArrayList<String[]>();
        codProblema = this.getArrayProblemasHistorico(cpf);
        if (codProblema != null) {
            this.contadorProblemas = 0;
            codTentativa = this.getArrayTentativasHistorico(cpf, this.codProblema.get(this.contadorProblemas));
            if (codTentativa != null) {
                //this.contadorProblemas++;
                this.contadorTentativas = 0;
                this.cabecalhoHistorico = getCabecalhoHistorico(this.codProblema.get(this.contadorProblemas), this.codTentativa.get(this.contadorTentativas));
                String etapa = this.getEtapa(cpf, this.codProblema.get(this.contadorProblemas), this.codTentativa.get(this.contadorTentativas));
                if (etapa != null) {
                    this.contadorTentativas++;
                    return this.cabecalhoHistorico + etapa;
                }
            }//if
        }
        return null;
    }//fim do metodo

    public String getMaisHistorico(String cpf) {
        if (this.contadorTentativas < this.codTentativa.size()) {//ainda tem tentativas referente a situcao problema this.contadorProblemas
            this.cabecalhoHistorico = getCabecalhoHistorico(this.codProblema.get(this.contadorProblemas), this.codTentativa.get(this.contadorTentativas));
            String etapa = this.getEtapa(cpf, this.codProblema.get(this.contadorProblemas), this.codTentativa.get(this.contadorTentativas));
            if (etapa != null) {
                this.contadorTentativas++;
                return this.cabecalhoHistorico + etapa;
            }
        } else {//passar pra proxima situacaoProblema
            this.contadorProblemas++;
            if (this.contadorProblemas < this.codProblema.size()) {
                codTentativa = this.getArrayTentativasHistorico(cpf, this.codProblema.get(this.contadorProblemas));
                if (codTentativa != null) {
                    //this.contadorProblemas++;
                    this.contadorTentativas = 0;
                    this.cabecalhoHistorico = getCabecalhoHistorico(this.codProblema.get(this.contadorProblemas), this.codTentativa.get(this.contadorTentativas));
                    String etapa = this.getEtapa(cpf, this.codProblema.get(this.contadorProblemas), this.codTentativa.get(this.contadorTentativas));
                    if (etapa != null) {
                        this.contadorTentativas++;
                        return this.cabecalhoHistorico + etapa;
                    }
                }//if
            }//else //sem mais historico
        }


        return null;
    }

    public boolean salvarEstado(String cpfUsuario, int codProblema, int codTentativa, Configuracao conf) {
        int x = existeEstadoSalvo(cpfUsuario);
        if (x == 0) {
            return inserirEstadoSalvo(cpfUsuario, codProblema, codTentativa, conf);
        } else {
            return updateEstadoSalvo(cpfUsuario, codProblema, codTentativa, conf);
        }

    }

    public int existeEstadoSalvo(String cpf) {
        String query = this.properties.getMensagem("EXISTE_ESTADO_SALVO");
        query = ManipularProperties.replaceString(cpf, query);
        ResultSet rs = this.consultar(query);
        if (rs != null && this.resultSet(rs)) {
            return getColunaInt(rs, 1);
        }
        return 0;
    }

    private boolean inserirEstadoSalvo(String cpfUsuario, int codProblema, int codTentativa, Configuracao conf) {
        String query = this.properties.getMensagem("INSERT_ESTADO_SALVO");
        String novoTexto[] = {cpfUsuario,
            String.valueOf(codProblema),
            String.valueOf(codTentativa),
            String.valueOf(conf.getPasso()),
            String.valueOf(conf.getContadorDragDrop()),
            String.valueOf(conf.getContadorPosicoesSelecao())};
        query = ManipularProperties.replaceString(novoTexto, query);
        return this.inserirDados(query);


    }

    private boolean updateEstadoSalvo(String cpfUsuario, int codProblema, int codTentativa, Configuracao conf) {
        String query = this.properties.getMensagem("UPDATE_ESTADO_SALVO");
        String novoTexto[] = {
            String.valueOf(codProblema),
            String.valueOf(conf.getPasso()),
            String.valueOf(conf.getContadorDragDrop()),
            String.valueOf(conf.getContadorPosicoesSelecao()),
            String.valueOf(codTentativa),
            cpfUsuario};
        query = ManipularProperties.replaceString(novoTexto, query);
        return this.inserirDados(query);

    }

    public Configuracao abrirEstado(String cpfUsuario, String estrutura) {
        if (existeEstadoSalvo(cpfUsuario) > 0) {
            String query = this.properties.getMensagem("SELECT_ESTADO_SALVO");
            query = ManipularProperties.replaceString(cpfUsuario, query);
            ResultSet rs = this.consultar(query);
            if (rs != null && this.resultSet(rs)) {
                String categoria = this.getColuna(rs, 2);
                String estr = SituacaoProblema.getEstrutura(categoria);
                if (!estrutura.equalsIgnoreCase(estr)) {
                    return null;
                }
//estadosalvo.codProblema,Categoria, passo, ContadorDragAndDrop, ContadorPosSelecao, estadosalvo.codTentativa
                codProblemaTemporario = this.getColunaInt(rs, 1);
                this.codTentativaTemporario = this.getColunaInt(rs, 6);
                return new Configuracao(null,
                        this.getColunaInt(rs, 5),
                        this.getColunaInt(rs, 3),
                        this.getColunaInt(rs, 4));
            }

        }
        return null;
    }

    public int getCodProblemaTemporario() {
        return this.codProblemaTemporario;
    }

    public int getCodTentativaTemporario() {
        return this.codTentativaTemporario;
    }

    private ArrayList<String[]> getArrayProblemasHistorico(String cpf) {

        ArrayList<String[]> array = new ArrayList<String[]>();

        String query = this.properties.getMensagem("SELECT_HISTORICO_PROBLEMAS");
        query = ManipularProperties.replaceString(cpf, query);
        ResultSet rs = this.consultar(query);
        if (rs != null) {
            String tupla[];
            while (this.resultSet(rs)) {
                tupla = new String[3];
                for (int i = 0; i < 3; i++) {
                    tupla[i] = this.getColuna(rs, i + 1);
                    // System.out.print(tupla[i]+"\t");
                }
                //System.out.print("\n");
                array.add(tupla);
            }

            return array;
        }
        return null;
    }

    private ArrayList<String[]> getArrayTentativasHistorico(String cpf, String[] tupla) {
        ArrayList<String[]> array = new ArrayList<String[]>();
        String query = this.properties.getMensagem("SELECT_TENTATIVAS");
        String novaString[] = {cpf, tupla[0]};
        query = ManipularProperties.replaceString(novaString, query);
        ResultSet rs = this.consultar(query);
        if (rs != null) {
            String tuplaTentativa[];
            while (this.resultSet(rs)) {
                tuplaTentativa = new String[2];
                for (int i = 0; i < 2; i++) {
                    tuplaTentativa[i] = this.getColuna(rs, i + 1);
                    // System.out.print(tuplaTentativa[i]+"\t");
                }
                // System.out.print("\n");
                array.add(tuplaTentativa);
            }
            return array;
        }
        return null;
    }

    private String getCabecalhoHistorico(String[] tuplaProblemas, String[] tuplaTentativa) {
        String texto = ManipularProperties.getMensagemEm("historicoCabecalho", "gerard.propriedades.historico");
        String fim[] = {tuplaProblemas[1], tuplaProblemas[2], tuplaTentativa[0], tuplaTentativa[1]};
        texto = ManipularProperties.replaceString(fim, texto);
        return texto;
    }

    private String getEtapa(String cpf, String[] tupla, String[] tuplaTentativa) {
        //ArrayList<String[]> etapa = new ArrayList<String[]>();
        String texto = ManipularProperties.getMensagemEm("historico", "gerard.propriedades.historico");
        String retorno = "";
        String query = this.properties.getMensagem("SELECT_ETAPA_HISTORICO");
        String novaS[] = {cpf, tupla[0], tuplaTentativa[0]};
        query = ManipularProperties.replaceString(novaS, query);
        ResultSet rs = this.consultar(query);
        String tuplaEtapa[] = new String[4];
        if (rs != null) {
            while (this.resultSet(rs)) {
                for (int i = 0; i < 4; i++) {
                    tuplaEtapa[i] = this.getColuna(rs, i + 1);

                }
                retorno += ManipularProperties.replaceString(tuplaEtapa, texto);
                //etapa.add(tuplaEtapa);
            }
            return retorno;
        }
        return null;

    }

    public boolean editarCadastro(Usuario usuario) {
//,Email=" 5}",NomeEscola="{6}",CidadeEscola="{7}", EstadoEscola= "{8}" WHERE CPF = "{9}";
        String query = this.properties.getMensagem("UPDATE_USUARIO");
        String novaS[] = {usuario.getSenha(),
            usuario.getNome(),
            usuario.getIdade(),
            usuario.getSexo(),
            usuario.getGrauFormacao(),
            usuario.getEmail(),
            usuario.getEstadoEscola(),
            usuario.getCidadeEscola(),
            usuario.getEstadoEscola(),
            usuario.getCpfLogin()};
        query = ManipularProperties.replaceString(novaS, query);

        return this.inserirDados(query);

    }

    public Vector showRecords(String sql, String field) {
        Vector records = new Vector();
        ResultSet rs;
        try {
            rs = this.consultar(formatarAcentuacao(sql));

            while (rs.next()) {
                records.add(rs.getString(field));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return records;
    }

    public String formatarAcentuacao(String texto) {
        try {
            return new String(texto.getBytes("UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }


    }
}
