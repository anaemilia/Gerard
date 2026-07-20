import gerard.campoaditivo.curadoria.sinal.ControladorSinaisCuradoria;
import gerard.campoaditivo.curadoria.sinal.ModoPersistenciaSinalCuradoria;
import gerard.campoaditivo.curadoria.sinal.OpcaoSinalCuradoria;
import gerard.campoaditivo.curadoria.sinal.PainelValorComSinalCuradoria;
import gerard.campoaditivo.curadoria.sinal.PapelSinalCuradoria;
import gerard.campoaditivo.curadoria.sinal.PoliticaSinalCuradoria;
import gerard.campoaditivo.curadoria.sinal.ResultadoValidacaoSinalCuradoria;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.i18n.ServicoLocalizacao;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class TesteSinalObrigatorioCuradoria {
    private static int verificacoes;

    private static void ok(boolean condicao, String mensagem) {
        verificacoes++;
        if (!condicao) throw new AssertionError(mensagem);
    }

    public static void main(String[] args) throws Exception {
        final Throwable[] falha = new Throwable[1];
        SwingUtilities.invokeAndWait(() -> {
            try {
                executar();
            } catch (Throwable t) {
                falha[0] = t;
            }
        });
        if (falha[0] != null) {
            if (falha[0] instanceof RuntimeException) throw (RuntimeException) falha[0];
            if (falha[0] instanceof Error) throw (Error) falha[0];
            throw new RuntimeException(falha[0]);
        }
        System.out.println("OK: " + verificacoes
                + " verificações da escolha obrigatória de sinal na curadoria.");
    }

    private static void executar() {
        PoliticaSinalCuradoria politica = new PoliticaSinalCuradoria();
        ok(politica.exigeEscolha(TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                PapelSinalCuradoria.TRANSFORMACAO),
                "Transformação de medidas deve exigir sinal");
        ok(politica.exigeEscolha(TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
                PapelSinalCuradoria.VALOR_RELATIVO),
                "Comparação deve exigir sinal do valor relativo");
        ok(!politica.exigeEscolha(TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES,
                PapelSinalCuradoria.TRANSFORMACAO_RESULTANTE),
                "A exigência nova deve se limitar aos metadados de sinal dedicados");
        ok(!politica.exigeEscolha(TipoSituacaoAditiva.TRANSFORMACAO_COMPOSTA_DOIS_PASSOS,
                PapelSinalCuradoria.TRANSFORMACAO_1),
                "Transformações embutidas no valor permanecem no formato legado");
        ok(!politica.exigeEscolha(TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                PapelSinalCuradoria.TRANSFORMACAO),
                "Composição de medidas não possui sinal de transformação");

        ok(OpcaoSinalCuradoria.aPartirDoEstado("", "12")
                == OpcaoSinalCuradoria.NAO_SELECIONADO,
                "Magnitude positiva sem metadado não pode escolher o sinal automaticamente");
        ok(OpcaoSinalCuradoria.aPartirDoEstado("", "-12")
                == OpcaoSinalCuradoria.NEGATIVO,
                "Valor legado negativo deve inicializar o seletor como negativo");
        ok(OpcaoSinalCuradoria.aPartirDoEstado("positivo", "12")
                == OpcaoSinalCuradoria.POSITIVO,
                "Metadado positivo deve prevalecer");
        ok(OpcaoSinalCuradoria.aPartirDoEstado("", "0")
                == OpcaoSinalCuradoria.NEUTRO,
                "Zero pode ser reconhecido como neutro");

        ResultadoValidacaoSinalCuradoria ausente = politica.validar(
                TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
                PapelSinalCuradoria.VALOR_RELATIVO,
                OpcaoSinalCuradoria.NAO_SELECIONADO, "?");
        ok(!ausente.isValido(), "Mesmo com ?, o curador deve escolher o sinal");
        ok(politica.validar(TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
                PapelSinalCuradoria.VALOR_RELATIVO,
                OpcaoSinalCuradoria.NEGATIVO, "?").isValido(),
                "Valor relativo desconhecido pode ter sinal negativo curado");
        ok(!politica.validar(TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                PapelSinalCuradoria.TRANSFORMACAO,
                OpcaoSinalCuradoria.POSITIVO, "0").isValido(),
                "Zero não pode ser marcado como positivo");
        ok(politica.validar(TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                PapelSinalCuradoria.TRANSFORMACAO,
                OpcaoSinalCuradoria.NEUTRO, "0").isValido(),
                "Zero deve aceitar sinal neutro");
        ok(!politica.validar(TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                PapelSinalCuradoria.TRANSFORMACAO,
                OpcaoSinalCuradoria.NEUTRO, "5").isValido(),
                "Magnitude não nula não pode usar sinal neutro");

        ServicoLocalizacao localizacao = ServicoLocalizacao.getInstancia();
        JTextField campoRelativo = new JTextField("?");
        PainelValorComSinalCuradoria painelRelativo =
                new PainelValorComSinalCuradoria(
                        TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
                        PapelSinalCuradoria.VALOR_RELATIVO,
                        ModoPersistenciaSinalCuradoria.METADADO_SEPARADO,
                        campoRelativo, "", localizacao);
        ok(painelRelativo.obterOpcaoSelecionada()
                == OpcaoSinalCuradoria.NAO_SELECIONADO,
                "O seletor deve abrir pedindo uma escolha explícita");
        ok(!painelRelativo.validarEscolha().isValido(),
                "Painel sem sinal deve ser inválido");
        painelRelativo.getSeletorSinal().setSelectedItem(
                OpcaoSinalCuradoria.NEGATIVO);
        ok(painelRelativo.validarEscolha().isValido(),
                "Painel deve validar depois da escolha negativa");
        ok("negativo".equals(painelRelativo.obterSinalCanonico()),
                "Persistência deve usar o valor canônico negativo");
        ok("?".equals(painelRelativo.obterValorParaPersistencia()),
                "Metadado separado preserva a interrogação sem prefixo");
        ok(painelRelativo.getSeletorSinal().getAccessibleContext()
                .getAccessibleName() != null,
                "Seletor deve possuir nome acessível");

        JTextField campoLegado = new JTextField("-7");
        PainelValorComSinalCuradoria painelLegado =
                new PainelValorComSinalCuradoria(
                        TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES,
                        PapelSinalCuradoria.TRANSFORMACAO_1,
                        ModoPersistenciaSinalCuradoria.EMBUTIDO_NO_VALOR,
                        campoLegado, "", localizacao);
        ok("7".equals(painelLegado.obterMagnitude()),
                "O campo visual deve exibir somente a magnitude");
        ok(painelLegado.obterOpcaoSelecionada()
                == OpcaoSinalCuradoria.NEGATIVO,
                "Sinal legado deve ser recuperado no seletor");
        ok("-7".equals(painelLegado.obterValorParaPersistencia()),
                "Formato legado deve receber novamente o sinal na persistência");

        campoLegado.setText("+8");
        ok("7".equals(campoLegado.getText()),
                "O filtro deve impedir que o sinal seja digitado no campo de magnitude");

        ControladorSinaisCuradoria controlador = new ControladorSinaisCuradoria(
                TipoSituacaoAditiva.COMPOSICAO_TRANSFORMACOES,
                localizacao);
        PainelValorComSinalCuradoria primeiro = controlador.registrar(
                PapelSinalCuradoria.TRANSFORMACAO_1,
                ModoPersistenciaSinalCuradoria.EMBUTIDO_NO_VALOR,
                new JTextField("4"), "");
        ok(primeiro != null, "Controlador deve registrar papel aplicável");
        primeiro.getSeletorSinal().setSelectedItem(OpcaoSinalCuradoria.POSITIVO);
        ok("+4".equals(controlador.obterValorParaPersistencia(
                PapelSinalCuradoria.TRANSFORMACAO_1, "")),
                "Controlador deve persistir transformação positiva embutida");
        controlador.definirSemanticaHerdada(true, "herdado");
        ok(!primeiro.getSeletorSinal().isEnabled(),
                "Tradução deve herdar e bloquear a escolha do sinal");
        ok(!primeiro.getCampoMagnitude().isEditable(),
                "Tradução deve bloquear a magnitude herdada");
    }
}
