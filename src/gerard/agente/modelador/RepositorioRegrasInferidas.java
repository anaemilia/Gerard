package gerard.agente.modelador;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Persistência das regras inferidas por InferenciaRegrasModelador (PART +
 * Apriori). Toda regra gravada aqui nasce com status "experimental" — a
 * mesma cautela já aplicada pela ferramenta manual (aba "Modelo do
 * Usuário" da Visão Pesquisador), nunca menor: quem gera automaticamente,
 * sem um pesquisador olhando no momento da geração, precisa da mesma
 * desconfiança de quem gera com supervisão direta.
 *
 * origem é sempre INFERENCIA_COMPUTACIONAL (REFERENCE.md §4.8): PART e
 * Apriori derivam um padrão a partir de evidência estatística, não aplicam
 * uma regra explícita e fixa — nunca SISTEMA, independentemente do
 * algoritmo ser determinístico.
 *
 * MotorRegrasConhecimento nunca lê regras daqui — só considera
 * status="ativa". A promoção de experimental para ativa é sempre um ato
 * humano deliberado, nunca automático, independente de quantas vezes um
 * padrão apareça nos dados ou da confiança estatística do algoritmo.
 */
public final class RepositorioRegrasInferidas {

    /**
     * Mesmo aviso que a ferramenta manual mostra em tela no instante da
     * geração. Persistido em cada linha para que uma futura tela ou
     * relatório de revisão o exiba mesmo quando não há pesquisador olhando
     * no momento em que a regra foi gerada (caminho automático).
     */
    public static final String AVISO_REGRA_EXPERIMENTAL =
            "Trate como validação da dependência, não como regra pronta para uso.";

    private final File arquivo;

    public RepositorioRegrasInferidas() {
        File diretorio = new File(new File(System.getProperty("user.home"), "Gerard"), "analises");
        if (!diretorio.exists()) diretorio.mkdirs();
        arquivo = new File(diretorio, "regras_inferidas.tsv");
    }

    public RepositorioRegrasInferidas(File arquivo) {
        this.arquivo = arquivo;
    }

    private static final String CABECALHO =
            "timestamp\tid_usuario\torigem\tstatus\taviso\tquantidade_instancias\tduracao_total_ms\tregras_part\tregras_apriori";

    /**
     * Escrita atômica (arquivo temporário + renomear), mesmo padrão já usado
     * em LoggerInteracaoGerard.associarInvarianteATentativaAtual: reescreve
     * o conteúdo existente inteiro mais a linha nova num arquivo temporário,
     * e só então substitui o arquivo real. Evita uma linha corrompida se o
     * processo for encerrado no meio da escrita (relevante especialmente
     * para o caminho automático — ver Main, gatilho de fechamento com
     * timeout).
     */
    public synchronized void salvar(String idUsuario, InferenciaRegrasModelador.Resultado resultado) throws Exception {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
        String[] campos = new String[]{
                timestamp,
                idUsuario,
                "INFERENCIA_COMPUTACIONAL",
                "experimental",
                AVISO_REGRA_EXPERIMENTAL,
                String.valueOf(resultado.quantidadeInstancias),
                String.valueOf(resultado.duracaoTotalMs()),
                resultado.regrasPart,
                resultado.regrasApriori
        };
        StringBuilder novaLinha = new StringBuilder();
        for (int i = 0; i < campos.length; i++) {
            if (i > 0) novaLinha.append('\t');
            novaLinha.append(escapar(campos[i]));
        }

        File temporario = new File(arquivo.getParentFile(), arquivo.getName() + ".tmp");
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(temporario), "UTF-8"));
        try {
            boolean existiaAntes = arquivo.exists() && arquivo.length() > 0;
            if (existiaAntes) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(arquivo), "UTF-8"));
                try {
                    String linha;
                    while ((linha = reader.readLine()) != null) {
                        writer.write(linha);
                        writer.newLine();
                    }
                } finally {
                    reader.close();
                }
            } else {
                writer.write(CABECALHO);
                writer.newLine();
            }
            writer.write(novaLinha.toString());
            writer.newLine();
        } finally {
            writer.close();
        }
        if (arquivo.exists() && !arquivo.delete()) {
            throw new Exception("Não foi possível substituir " + arquivo.getName() + " (arquivo em uso?)");
        }
        if (!temporario.renameTo(arquivo)) {
            throw new Exception("Não foi possível renomear o arquivo temporário para " + arquivo.getName());
        }
    }

    private String escapar(String valor) {
        if (valor == null) return "";
        return valor.replace("\\", "\\\\").replace("\t", "\\t").replace("\r", "\\r").replace("\n", "\\n");
    }
}
