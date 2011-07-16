/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gerard.view;

import gerard.banco.Usuario;
import gerard.controller.ControllerInterface;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.JPanel;

/**
 *
 * @author Kecia
 */
public class JPanelCadastro extends JPanel {

    private Component component_pai;
    private ControllerInterface controller;
    private Usuario usuario;
    private String sexo = "F";
    private int modo=0; //0 - modo cadastro 1 - modo editar cadastro

    public JPanelCadastro(Component component_pai, ControllerInterface cont) {
        this.component_pai = component_pai;
        this.controller = cont;
        initComponents();

          this.jRadioButton_feminino.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                sexo = "F";
            }
        });
        this.jRadioButton_masculino.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                sexo = "M";
            }
        });

        this.jButton_cadastrar.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                usuario = new Usuario(jTextField_cpf.getText(),
                        String.valueOf(jPasswordField_senha.getPassword()),
                        jTextField_nomeUsuario.getText(),
                        jTextField_idade.getText(),
                        sexo,
                        String.valueOf(jComboBox_grauFormacao.getSelectedItem()),
                        jTextField_email.getText(),
                        jTextField_nomeEscola.getText(),
                        jTextField_cidadeEscola.getText(),
                        String.valueOf(jComboBox_estados.getSelectedItem()));
                String senhaConfirmacao = String.valueOf(jPasswordField_confirmaSenha.getPassword());
                if(modo==0)
                    controller.cadastrarUsuario(usuario, senhaConfirmacao);
                else
                    controller.editarCadastro(usuario,senhaConfirmacao);




            }
        });
        this.jButton_cancelar.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if(modo==0)
                    controller.cancelarCadastro();
                else
                    controller.cancelarEditarCadastro();
                jTextField_cidadeEscola.setText("");
                jTextField_cpf.setText("");
                jTextField_email.setText("");
                jTextField_idade.setText("");
                jTextField_nomeEscola.setText("");
                jTextField_nomeUsuario.setText("");
                jPasswordField_confirmaSenha.setText("");
                jPasswordField_senha.setText("");

            }
        });


    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        int width = component_pai.getWidth();
        int height = component_pai.getHeight();

        java.net.URL caminho = getClass().getResource("/gerard/imagens/telaLogin.png");

        Image img = null;
        try {
            img = javax.imageio.ImageIO.read(caminho);
        } catch (IOException ex) {
            //Logger.getLogger(JPanelLogin.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (img != null) {
            g.drawImage(img, 0, 0, width, height, this);
        }
    }
    public void modoCadastrar(){
        this.modo=0;
         jLabel_titulo.setText("Cadastrar Usuário");
         this.jTextField_cpf.setEditable(true);
         jButton_cadastrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoCadastrar.png"))); // NOI18N
        jButton_cadastrar.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoCadastrar_selecionado.png"))); // NOI18N
    }
    public void modoEditar(Usuario usuario){
         jTextField_cidadeEscola.setText(usuario.getCidadeEscola());
                jTextField_cpf.setText(usuario.getCpfLogin());
                jTextField_email.setText(usuario.getEmail());
                jTextField_idade.setText(usuario.getIdade());
                jTextField_nomeEscola.setText(usuario.getNomeEscola());
                jTextField_nomeUsuario.setText(usuario.getNome());
                jPasswordField_confirmaSenha.setText(usuario.getSenha());
                jPasswordField_senha.setText(usuario.getSenha());
                if(usuario.getSexo().equalsIgnoreCase("F"))
                  this.jRadioButton_feminino.setSelected(true);
                else
                  this.jRadioButton_masculino.setSelected(true);

         jLabel_titulo.setText("Editar Cadastro");
        this.modo=1;
        this.jTextField_cpf.setEditable(false);
         jButton_cadastrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoSalvar.png"))); // NOI18N
        jButton_cadastrar.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoSalvar_selecionado.png"))); // NOI18N
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        buttonGroup_sexo = new javax.swing.ButtonGroup();
        jLabel_nomeUsuario = new javax.swing.JLabel();
        jTextField_nomeUsuario = new javax.swing.JTextField();
        jLabel_sexo = new javax.swing.JLabel();
        jRadioButton_feminino = new javax.swing.JRadioButton();
        jRadioButton_masculino = new javax.swing.JRadioButton();
        jLabel_grauFormacao = new javax.swing.JLabel();
        jComboBox_grauFormacao = new javax.swing.JComboBox();
        jLabel_email = new javax.swing.JLabel();
        jTextField_email = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel_nomeEscola = new javax.swing.JLabel();
        jTextField_nomeEscola = new javax.swing.JTextField();
        jLabel_cidadeEscola = new javax.swing.JLabel();
        jTextField_cidadeEscola = new javax.swing.JTextField();
        jLabel_estadoEscola = new javax.swing.JLabel();
        jComboBox_estados = new javax.swing.JComboBox();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel_senha = new javax.swing.JLabel();
        jLabel_confirmaSenha = new javax.swing.JLabel();
        jPasswordField_senha = new javax.swing.JPasswordField();
        jPasswordField_confirmaSenha = new javax.swing.JPasswordField();
        jLabel_cpf = new javax.swing.JLabel();
        jTextField_cpf = new javax.swing.JTextField();
        jLabel_exCPF = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jButton_cadastrar = new javax.swing.JButton();
        jButton_cancelar = new javax.swing.JButton();
        jLabel_idade = new javax.swing.JLabel();
        jTextField_idade = new javax.swing.JTextField();
        jLabel_titulo = new javax.swing.JLabel();

        jLabel_nomeUsuario.setFont(new java.awt.Font("Lucida Calligraphy", 0, 14)); // NOI18N
        jLabel_nomeUsuario.setText("Nome do usuário:");
        jLabel_nomeUsuario.setVerifyInputWhenFocusTarget(false);
        jLabel_nomeUsuario.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        jTextField_nomeUsuario.setFont(new java.awt.Font("Lucida Calligraphy", 0, 14)); // NOI18N

        jLabel_sexo.setFont(new java.awt.Font("Lucida Calligraphy", 0, 14)); // NOI18N
        jLabel_sexo.setText("Sexo:");
        jLabel_sexo.setVerifyInputWhenFocusTarget(false);
        jLabel_sexo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        jRadioButton_feminino.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup_sexo.add(jRadioButton_feminino);
        jRadioButton_feminino.setSelected(true);
        jRadioButton_feminino.setText("F");

        jRadioButton_masculino.setBackground(new java.awt.Color(255, 255, 255));
        buttonGroup_sexo.add(jRadioButton_masculino);
        jRadioButton_masculino.setText("M");

        jLabel_grauFormacao.setFont(new java.awt.Font("Lucida Calligraphy", 0, 14)); // NOI18N
        jLabel_grauFormacao.setText("Grau de formação:");
        jLabel_grauFormacao.setVerifyInputWhenFocusTarget(false);
        jLabel_grauFormacao.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        jComboBox_grauFormacao.setFont(new java.awt.Font("Lucida Calligraphy", 0, 12)); // NOI18N
        jComboBox_grauFormacao.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Ensino Fundamental", "Ensino Médio", "Superior", "Pos-Graduado", "Mestrado", "Doutorado", "Pos-Doutorado"}));

        jLabel_email.setFont(new java.awt.Font("Lucida Calligraphy", 0, 14)); // NOI18N
        jLabel_email.setText("Email:");
        jLabel_email.setVerifyInputWhenFocusTarget(false);
        jLabel_email.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        jTextField_email.setFont(new java.awt.Font("Lucida Calligraphy", 0, 14)); // NOI18N

        jSeparator1.setForeground(new java.awt.Color(0, 102, 0));

        jLabel_nomeEscola.setFont(new java.awt.Font("Lucida Calligraphy", 0, 14)); // NOI18N
        jLabel_nomeEscola.setText("Nome da Escola:");
        jLabel_nomeEscola.setVerifyInputWhenFocusTarget(false);
        jLabel_nomeEscola.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        jTextField_nomeEscola.setFont(new java.awt.Font("Lucida Calligraphy", 0, 14)); // NOI18N

        jLabel_cidadeEscola.setFont(new java.awt.Font("Lucida Calligraphy", 0, 14)); // NOI18N
        jLabel_cidadeEscola.setText("Cidade da Escola:");
        jLabel_cidadeEscola.setVerifyInputWhenFocusTarget(false);
        jLabel_cidadeEscola.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        jTextField_cidadeEscola.setFont(new java.awt.Font("Lucida Calligraphy", 0, 14)); // NOI18N

        jLabel_estadoEscola.setFont(new java.awt.Font("Lucida Calligraphy", 0, 14)); // NOI18N
        jLabel_estadoEscola.setText("Estado da Escola:");
        jLabel_estadoEscola.setVerifyInputWhenFocusTarget(false);
        jLabel_estadoEscola.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        jComboBox_estados.setFont(new java.awt.Font("Lucida Calligraphy", 0, 12)); // NOI18N
        jComboBox_estados.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Acre", "Alagoas", "Amapá", "Amazonas", "Bahia", "Ceará", "Distrito Federal", "Espírito Santo", "Goiás", "Maranhão", "Mato Grosso", "Mato Grosso do Sul", "Minas Gerais", "Pará", "Paraíba", "Paraná", "Pernambuco", "Piauí", "Rio de Janeiro", "Rio Grande do Norte", "Rio Grande do Sul", "Rondônia", "Roraima", "Santa Catarina", "São Paulo", "Sergipe", "Tocantins"}));

        jSeparator2.setForeground(new java.awt.Color(0, 102, 0));

        jLabel_senha.setFont(new java.awt.Font("Lucida Calligraphy", 0, 14)); // NOI18N
        jLabel_senha.setText("Senha de acesso:");
        jLabel_senha.setVerifyInputWhenFocusTarget(false);
        jLabel_senha.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        jLabel_confirmaSenha.setFont(new java.awt.Font("Lucida Calligraphy", 0, 14)); // NOI18N
        jLabel_confirmaSenha.setText("Confirme a senha:");
        jLabel_confirmaSenha.setVerifyInputWhenFocusTarget(false);
        jLabel_confirmaSenha.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        jPasswordField_senha.setFont(new java.awt.Font("Lucida Calligraphy", 0, 14)); // NOI18N

        jPasswordField_confirmaSenha.setFont(new java.awt.Font("Lucida Calligraphy", 0, 14)); // NOI18N

        jLabel_cpf.setFont(new java.awt.Font("Lucida Calligraphy", 0, 14)); // NOI18N
        jLabel_cpf.setText("CPF:");
        jLabel_cpf.setVerifyInputWhenFocusTarget(false);
        jLabel_cpf.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        jTextField_cpf.setFont(new java.awt.Font("Lucida Calligraphy", 0, 14)); // NOI18N

        jLabel_exCPF.setFont(new java.awt.Font("Arial", 0, 8)); // NOI18N
        jLabel_exCPF.setText("(ex: 99999999999)");
        jLabel_exCPF.setVerifyInputWhenFocusTarget(false);
        jLabel_exCPF.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        jSeparator3.setForeground(new java.awt.Color(0, 102, 0));

        jButton_cadastrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoCadastrar.png"))); // NOI18N
        jButton_cadastrar.setToolTipText("Criar Login");
        jButton_cadastrar.setBorderPainted(false);
        jButton_cadastrar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton_cadastrar.setPreferredSize(new java.awt.Dimension(104, 50));
        jButton_cadastrar.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoCadastrar_selecionado.png"))); // NOI18N

        jButton_cancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoCancelar.png"))); // NOI18N
        jButton_cancelar.setToolTipText("Criar Login");
        jButton_cancelar.setBorderPainted(false);
        jButton_cancelar.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton_cancelar.setPreferredSize(new java.awt.Dimension(104, 50));
        jButton_cancelar.setRolloverIcon(new javax.swing.ImageIcon(getClass().getResource("/gerard/imagens/botaoCancelarSelecionado.png"))); // NOI18N

        jLabel_idade.setFont(new java.awt.Font("Lucida Calligraphy", 0, 14)); // NOI18N
        jLabel_idade.setText("Idade:");
        jLabel_idade.setVerifyInputWhenFocusTarget(false);
        jLabel_idade.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        jTextField_idade.setFont(new java.awt.Font("Lucida Calligraphy", 0, 14)); // NOI18N

        jLabel_titulo.setFont(new java.awt.Font("Lucida Calligraphy", 0, 18)); // NOI18N
        jLabel_titulo.setForeground(new java.awt.Color(0, 102, 0));
        jLabel_titulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel_titulo.setText("Cadastrar Usuário");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(jRadioButton_feminino).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(jRadioButton_masculino)).addComponent(jLabel_sexo)).addGap(44, 44, 44).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false).addComponent(jComboBox_grauFormacao, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabel_grauFormacao)).addGap(22, 22, 22).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jLabel_idade).addComponent(jTextField_idade, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(40, 40, 40).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(jLabel_cpf).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jLabel_exCPF).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 118, Short.MAX_VALUE)).addComponent(jTextField_cpf, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE)).addGap(14, 14, 14)).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(jLabel_email, javax.swing.GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE).addGap(543, 543, 543)).addComponent(jTextField_email, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 604, Short.MAX_VALUE)).addGap(14, 14, 14)).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING).addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 628, Short.MAX_VALUE).addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup().addGap(18, 18, 18).addComponent(jLabel_titulo)).addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING).addComponent(jTextField_nomeUsuario, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 604, Short.MAX_VALUE).addComponent(jLabel_nomeUsuario, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 604, Short.MAX_VALUE)).addGap(14, 14, 14))).addGap(0, 0, 0)).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(jLabel_nomeEscola).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 496, javax.swing.GroupLayout.PREFERRED_SIZE)).addComponent(jSeparator3, javax.swing.GroupLayout.DEFAULT_SIZE, 618, Short.MAX_VALUE).addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 618, Short.MAX_VALUE).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addComponent(jButton_cadastrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jButton_cancelar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(14, 14, 14)).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jLabel_senha).addComponent(jPasswordField_senha, javax.swing.GroupLayout.DEFAULT_SIZE, 286, Short.MAX_VALUE)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jLabel_confirmaSenha).addComponent(jPasswordField_confirmaSenha, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)).addGap(23, 23, 23)).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jLabel_cidadeEscola).addComponent(jTextField_cidadeEscola, javax.swing.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)).addGap(28, 28, 28).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jLabel_estadoEscola).addComponent(jComboBox_estados, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)).addGroup(layout.createSequentialGroup().addComponent(jTextField_nomeEscola, javax.swing.GroupLayout.DEFAULT_SIZE, 599, Short.MAX_VALUE).addGap(19, 19, 19))).addGap(0, 0, 0)));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(jLabel_titulo).addGap(18, 18, 18).addComponent(jLabel_nomeUsuario).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jTextField_nomeUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(18, 18, 18).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING).addComponent(jLabel_exCPF).addComponent(jLabel_cpf)).addComponent(jLabel_sexo).addComponent(jLabel_grauFormacao).addComponent(jLabel_idade)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jTextField_idade, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jTextField_cpf, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))).addGroup(layout.createSequentialGroup().addGap(22, 22, 22).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jRadioButton_feminino).addComponent(jRadioButton_masculino).addComponent(jComboBox_grauFormacao, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))).addGap(18, 18, 18).addComponent(jLabel_email).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jTextField_email, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(18, 18, 18).addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(jLabel_nomeEscola).addGap(4, 4, 4).addComponent(jTextField_nomeEscola, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(18, 18, 18).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false).addComponent(jLabel_estadoEscola, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(jLabel_cidadeEscola)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jTextField_cidadeEscola, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jComboBox_estados, javax.swing.GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)).addGap(18, 18, 18).addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 8, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(jLabel_senha).addGap(1, 1, 1).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jPasswordField_senha, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jPasswordField_confirmaSenha, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))).addComponent(jLabel_confirmaSenha)).addGap(23, 23, 23).addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(12, 12, 12).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING).addComponent(jButton_cadastrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jButton_cancelar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addContainerGap()));
    }// </editor-fold>
    // Variables declaration - do not modify
    private javax.swing.ButtonGroup buttonGroup_sexo;
    private javax.swing.JButton jButton_cadastrar;
    private javax.swing.JButton jButton_cancelar;
    private javax.swing.JComboBox jComboBox_estados;
    private javax.swing.JComboBox jComboBox_grauFormacao;
    private javax.swing.JLabel jLabel_cidadeEscola;
    private javax.swing.JLabel jLabel_confirmaSenha;
    private javax.swing.JLabel jLabel_cpf;
    private javax.swing.JLabel jLabel_email;
    private javax.swing.JLabel jLabel_estadoEscola;
    private javax.swing.JLabel jLabel_exCPF;
    private javax.swing.JLabel jLabel_grauFormacao;
    private javax.swing.JLabel jLabel_idade;
    private javax.swing.JLabel jLabel_nomeEscola;
    private javax.swing.JLabel jLabel_nomeUsuario;
    private javax.swing.JLabel jLabel_senha;
    private javax.swing.JLabel jLabel_sexo;
    private javax.swing.JLabel jLabel_titulo;
    private javax.swing.JPasswordField jPasswordField_confirmaSenha;
    private javax.swing.JPasswordField jPasswordField_senha;
    private javax.swing.JRadioButton jRadioButton_feminino;
    private javax.swing.JRadioButton jRadioButton_masculino;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTextField jTextField_cidadeEscola;
    private javax.swing.JTextField jTextField_cpf;
    private javax.swing.JTextField jTextField_email;
    private javax.swing.JTextField jTextField_idade;
    private javax.swing.JTextField jTextField_nomeEscola;
    private javax.swing.JTextField jTextField_nomeUsuario;
}
