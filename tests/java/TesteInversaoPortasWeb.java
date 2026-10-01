import gerard.aplicacao.portabilidade.PortaAjudaContextual;
import gerard.aplicacao.portabilidade.PortaQuestionamentoPosicionamento;
import gerard.aplicacao.portabilidade.PortaRegistroAtividadeWeb;
import gerard.aplicacao.portabilidade.ResultadoQuestionamentoPosicionamento;
import gerard.aplicacao.portabilidade.ServicoSorteioAtividadeWeb;
import gerard.infraestrutura.web.scaffolding.AdaptadorAjudaContextualWeb;
import gerard.infraestrutura.web.scaffolding.AdaptadorQuestionamentoPosicionamentoWeb;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Protege a inversão de dependência das portas web: a camada de aplicação não
 * conhece nem instancia a infraestrutura; quem entrega as implementações é a
 * raiz de composição (ServidorPrototipoWeb). Sem adaptador, a aplicação não
 * inventa conteúdo; com os adaptadores reais, a ajuda contextual responde com
 * o texto de mensagens_pt.properties.
 */
public class TesteInversaoPortasWeb {
    public static void main(String[] args) throws Exception {
        aplicacaoNaoImportaInfraestrutura();
        semPortasAAplicacaoNaoInventaConteudo();
        comPortasDaRaizDeComposicaoAAjudaResponde();
        System.out.println("APROVADO: aplicação web só conhece portas; a raiz de composição entrega os adaptadores.");
    }

    private static void aplicacaoNaoImportaInfraestrutura() throws Exception {
        List<File> fontes = new ArrayList<File>();
        coletar(new File("src/gerard/aplicacao"), fontes);
        exigir(!fontes.isEmpty(), "nenhuma fonte de aplicação encontrada (executar na raiz do projeto)");
        for (File fonte : fontes) {
            String codigo = new String(Files.readAllBytes(fonte.toPath()), StandardCharsets.UTF_8);
            exigir(!codigo.matches("(?s).*\\bimport\\s+gerard\\.infraestrutura\\..*"),
                    fonte + " importa gerard.infraestrutura (inverter por porta)");
            exigir(!codigo.contains("new gerard.infraestrutura."),
                    fonte + " instancia infraestrutura concreta");
        }
    }

    private static void semPortasAAplicacaoNaoInventaConteudo() {
        ServicoSorteioAtividadeWeb servico = new ServicoSorteioAtividadeWeb();
        Map<String, Object> ajuda = servico.ajudaContextual("TEXTO", "DUVIDA");
        exigir("".equals(ajuda.get("mensagem")),
                "sem porta de ajuda, a aplicação não pode fabricar mensagem: " + ajuda.get("mensagem"));
        ResultadoQuestionamentoPosicionamento neutro = PortaQuestionamentoPosicionamento.NAO_APLICAVEL
                .avaliar("papel.parte1", "papel.parte2", "Parte 2", "Composição de medidas");
        exigir(!neutro.isAplicavel(), "porta neutra não questiona nenhuma soltura");
        exigir(PortaAjudaContextual.NENHUMA.projetarArea("TEXTO").isEmpty(),
                "porta neutra de ajuda não projeta área alguma");
    }

    private static void comPortasDaRaizDeComposicaoAAjudaResponde() {
        ServicoSorteioAtividadeWeb servico = new ServicoSorteioAtividadeWeb(
                PortaRegistroAtividadeWeb.NENHUMA,
                new AdaptadorAjudaContextualWeb(),
                new AdaptadorQuestionamentoPosicionamentoWeb());
        Object mensagem = servico.ajudaContextual("TEXTO", "DUVIDA").get("mensagem");
        exigir(mensagem instanceof String && ((String) mensagem).trim().length() > 0,
                "com o adaptador real, a ajuda contextual deve responder com texto do i18n");
        ResultadoQuestionamentoPosicionamento real = new AdaptadorQuestionamentoPosicionamentoWeb()
                .avaliar("papel.parte2", "papel.parte1", "Parte 1", "Composição de medidas");
        exigir(real.isAplicavel() && !real.isCorreto(),
                "o adaptador real deve questionar origem incompatível com o destino");
    }

    private static void coletar(File diretorio, List<File> resultado) {
        File[] itens = diretorio.listFiles();
        if (itens == null) return;
        for (File item : itens) {
            if (item.isDirectory()) coletar(item, resultado);
            else if (item.getName().endsWith(".java")) resultado.add(item);
        }
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }
}
