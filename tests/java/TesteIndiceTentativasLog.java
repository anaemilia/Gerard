import gerard.pesquisador.log.EventoLogGerard;
import gerard.pesquisador.log.LoggerInteracaoGerard;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * O número da tentativa de uma situação (coluna tentativa_numero_situacao) tem que ser o de sempre — 1 mais as
 * tentativas distintas já registradas desta pessoa nesta situação — agora respondido por um índice em memória em vez
 * de reler todos os arquivos de log a cada situação nova. Confere o índice contra a contagem por força bruta no
 * próprio arquivo, inclusive para linhas que já estavam em disco antes do logger subir.
 */
public class TesteIndiceTentativasLog {
    static int falhas;

    static void conferir(boolean ok, String msg) {
        if (!ok) {
            falhas++;
        }
        System.out.println((ok ? "[OK] " : "[FALHA] ") + msg);
    }

    /** Contagem por força bruta: tentativas distintas do usuário nesta situação (grupo), em todos os logs. */
    static int forcaBruta(File dir, String usuario, String grupo) throws Exception {
        Set<String> tentativas = new HashSet<String>();
        for (File f : dir.listFiles()) {
            if (!f.getName().startsWith("gerard_interacao_") || !f.getName().endsWith(".tsv")) {
                continue;
            }
            for (String linha : Files.readAllLines(f.toPath(), StandardCharsets.UTF_8)) {
                EventoLogGerard e = EventoLogGerard.deTsv(linha);
                if (e != null && usuario.equals(e.getUsuario()) && grupo.equals(e.getSituacaoGrupoId())
                        && e.getTentativa() != null && e.getTentativa().trim().length() > 0) {
                    tentativas.add(e.getTentativa().trim());
                }
            }
        }
        return tentativas.size() + 1;
    }

    public static void main(String[] args) throws Exception {
        File home = Files.createTempDirectory("gerard-home-").toFile();
        System.setProperty("user.home", home.getAbsolutePath());
        File logs = new File(new File(home, "Gerard"), "logs");
        logs.mkdirs();

        // Um log antigo já em disco antes de o logger subir: duas tentativas de "pre" em SG_PRE.
        StringBuilder antigo = new StringBuilder(EventoLogGerard.cabecalhoTsv()).append('\n');
        for (String tentativa : new String[] {"T0001_antiga", "T0002_antiga"}) {
            EventoLogGerard e = EventoLogGerard.criar("20200101_000000", "pre", "C", "P1", tentativa, "tarefa", "", "", "",
                    "", "", "", "COMPOSICAO_MEDIDAS", "Enunciado pré", "origem", "detalhe", "V_PRE", "SG_PRE", "pt-BR",
                    "", "", "");
            antigo.append(e.toTsv()).append('\n');
        }
        Files.write(new File(logs, "gerard_interacao_20200101_000000.tsv").toPath(),
                antigo.toString().getBytes(StandardCharsets.UTF_8));

        // O logger sobe depois; o índice carrega no primeiro cálculo e enxerga o arquivo antigo.
        LoggerInteracaoGerard logger = LoggerInteracaoGerard.getInstancia();
        logger.definirUsuario("pre");
        logger.novoProblema("COMPOSICAO_MEDIDAS", "Enunciado pré", "V_PRE", "SG_PRE", "pt-BR");
        // O arquivo do processo passa a ser "antigo" para o índice, que só carrega no primeiro cálculo.
        List<Integer> numeros = new ArrayList<Integer>();
        String[] situacoes = {"SG_A", "SG_A", "SG_B", "SG_A", "SG_B", "SG_PRE"};
        String[] usuarios = {"ana", "ana", "ana", "bia", "ana", "pre"};
        for (int i = 0; i < situacoes.length; i++) {
            logger.definirUsuario(usuarios[i]);
            int esperado = forcaBruta(logs, usuarios[i], situacoes[i]);
            logger.novoProblema("COMPOSICAO_MEDIDAS", "Enunciado " + situacoes[i], "V_" + situacoes[i],
                    situacoes[i], "pt-BR");
            String ultimo = null;
            for (String linha : Files.readAllLines(logger.getArquivoSessao().toPath(), StandardCharsets.UTF_8)) {
                EventoLogGerard e = EventoLogGerard.deTsv(linha);
                if (e != null && situacoes[i].equals(e.getSituacaoGrupoId()) && usuarios[i].equals(e.getUsuario())) {
                    ultimo = e.getTentativaNumeroSituacao();
                }
            }
            numeros.add(Integer.valueOf(ultimo));
            conferir(String.valueOf(esperado).equals(ultimo),
                    usuarios[i] + "/" + situacoes[i] + ": índice=" + ultimo + " força bruta=" + esperado);
        }
        conferir(numeros.get(0).intValue() == 1 && numeros.get(1).intValue() == 2 && numeros.get(3).intValue() == 1,
                "a contagem é por pessoa e por situação (ana A=1,2; bia A=1)");
        System.out.println(falhas == 0 ? "APROVADO: índice de tentativas igual à contagem direta"
                : "REPROVADO: " + falhas + " falha(s)");
        System.exit(falhas == 0 ? 0 : 1);
    }
}
