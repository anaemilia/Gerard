package gerard.util.Aplicativo1;

import gerard.controller.GerardController;
import gerard.util.Aplicativo1.Nucleo;
import gerard.view.JPanelAplicativo1;
import javax.swing.SwingUtilities;

/**
 *
 * @author Kecia
 */
public class ControllerAplicativo1 {

    String[] numero = new String[3];
    private int tipo_problema;
    private int posicao_pergunta;
    private GerardController controladorPrincipal;
    private Nucleo nucleo;
    private JPanelAplicativo1 viewAplicativo1;
    private String textoParaValidar;

    public ControllerAplicativo1(GerardController controladorPrincipal) {
        this.controladorPrincipal = controladorPrincipal;
        nucleo = new Nucleo();
        tipo_problema = 1;
        numero[0] = "2";
        numero[1] = "5";
        numero[2] = "?";
    }

    public void setViewAplicativo1(JPanelAplicativo1 viewAplicativo1) {
        this.viewAplicativo1 = viewAplicativo1;
    }

    public void setTipoEPosicaoPergunta(int tipo, int pos) {
        this.tipo_problema = tipo;
        this.posicao_pergunta = pos;
        this.gerarProblema();

    }

    public void validar(String texto) {
        textoParaValidar = texto;
        viewAplicativo1.setTelaAguarde(true);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                nucleo.set_problema(numero[0], numero[1], numero[2], tipo_problema);
                nucleo.set_sentenca(textoParaValidar);
                viewAplicativo1.setTelaAguarde(false);
                if (tipo_problema == 1) {
                    nucleo.Validar_Problema_Composicao();
                } else if (tipo_problema == 2) {
                    nucleo.Validar_Problema_Transformacao();
                } else if (tipo_problema == 3) {
                    nucleo.Validar_Problema_Comparacao();
                }
            }
        });

    }

    public void gerarProblema() {
        //Números maiores que zero e diferentes
        int n1 = 2 + (int) (10 * Math.random());
        int n2 = (int) (10 * Math.random()) + n1 + 1;

        String num_01 = Integer.toString(n1);
        String num_02 = Integer.toString(n2);

        switch (posicao_pergunta) {
            case 1: {
                if (tipo_problema == 1) {
                    numero[0] = "?";
                    numero[1] = num_01;
                    numero[2] = num_02;
                } else if (tipo_problema == 2) {
                    numero[0] = "?";
                    if (n1 % 2 == 0) {
                        numero[1] = num_02;
                        numero[2] = num_01;
                    } else {
                        numero[1] = num_02;
                        numero[2] = Integer.toString(-Integer.parseInt(num_01));
                    }
                } else if (tipo_problema == 3) {
                    numero[0] = "?";
                    if (n1 % 2 == 0) {
                        numero[1] = num_02;
                        numero[2] = num_01;
                    } else {
                        if (n2 % 2 == 0) {
                            numero[2] = Integer.toString(-Integer.parseInt(num_01));
                            numero[1] = num_02;
                        } else {
                            numero[2] = Integer.toString(-Integer.parseInt(num_02));
                            numero[1] = num_01;
                        }
                    }
                }
            }
            break;

            case 2: {
                if (tipo_problema == 1) {
                    numero[0] = num_01;
                    numero[1] = "?";
                    numero[2] = num_02;
                } else if (tipo_problema == 2) {
                    numero[1] = "?";
                    if (n1 % 2 == 0) {
                        numero[0] = num_01;
                        numero[2] = num_02;
                    } else {
                        numero[0] = num_02;

                        if (n2 % 2 == 0) {
                            numero[2] = num_01;
                        } else {
                            numero[2] = Integer.toString(-Integer.parseInt(num_01));
                        }
                    }
                } else if (tipo_problema == 3) {
                    if (n1 % 2 == 0) {
                        numero[0] = num_01;
                        numero[1] = num_02;
                    } else {
                        numero[0] = num_02;
                        numero[1] = num_01;
                    }
                    numero[2] = "?";
                }
            }
            break;

            case 3: {
                if (tipo_problema == 1) {
                    numero[0] = num_01;
                    numero[1] = num_02;
                    numero[2] = "?";
                } else if (tipo_problema == 2) {
                    if (n1 % 2 == 0) {
                        numero[0] = num_01;
                        numero[1] = num_02;
                    } else {
                        numero[0] = num_02;
                        numero[1] = num_01;
                    }
                    numero[2] = "?";
                } else if (tipo_problema == 3) {
                    if (n1 % 2 == 0) {
                        numero[0] = num_01;
                        numero[2] = num_02;
                    } else {
                        numero[0] = num_02;

                        if (n2 % 2 == 0) {
                            numero[2] = num_01;
                        } else {
                            numero[2] = Integer.toString(-Integer.parseInt(num_01));
                        }

                    }

                    numero[1] = "?";
                }
            }
            break;
        }

    }

    public int getTipoProblema() {
        return this.tipo_problema;
    }

    public String[] getValores() {
        return this.numero;
    }

    public void voltar() {
        tipo_problema = 1;
        numero[0] = "2";
        numero[1] = "5";
        numero[2] = "?";
        this.controladorPrincipal.setTelaLogin();
    }

    public Nucleo getNucleo() {
        return this.nucleo;
    }
}
