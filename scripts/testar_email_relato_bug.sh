#!/usr/bin/env bash
set -euo pipefail
RAIZ="$(cd "$(dirname "$0")/.." && pwd)"
TMP="${TMPDIR:-/tmp}/gerard_email_relato_bug_c89"
rm -rf "$TMP"
mkdir -p "$TMP/classes"
cd "$RAIZ"
ant -noinput -q compile
cat > "$TMP/TesteEmailRelatoBug.java" <<'JAVA'
import gerard.suporte.PreparadorEmailRelatoBug;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class TesteEmailRelatoBug {
    private static void conferir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }

    private static String decodificar(String texto) throws Exception {
        return URLDecoder.decode(texto, StandardCharsets.UTF_8.name());
    }

    public static void main(String[] args) throws Exception {
        PreparadorEmailRelatoBug.MensagemPreparada email =
                PreparadorEmailRelatoBug.preparar(
                        "Arrastei o número e o diagrama não atualizou.",
                        "SP_TESTE", "COMPOSICAO_MEDIDAS",
                        "Diagrama de Vergnaud; Diagrama de composição de coleções; Eixo dos números inteiros",
                        "PORTUGUES", "pt", "Ana tinha 6 e recebeu 8.");

        String gmail = email.getUriGmail().toASCIIString();
        conferir(gmail.startsWith("https://mail.google.com/mail/?"),
                "A abertura principal deve usar o compositor web do Gmail.");
        conferir(gmail.contains("view=cm") && gmail.contains("fs=1")
                && gmail.contains("to=anaemilia%40gmail.com")
                && gmail.contains("su=") && gmail.contains("body="),
                "A URL do Gmail deve preencher destinatário, assunto e corpo.");

        String gmailDecodificado = decodificar(gmail);
        conferir(gmailDecodificado.contains("SP_TESTE")
                && gmailDecodificado.contains("COMPOSICAO_MEDIDAS")
                && gmailDecodificado.contains("Diagrama de composição de coleções")
                && gmailDecodificado.contains("Eixo dos números inteiros")
                && gmailDecodificado.contains("Arrastei o número"),
                "O corpo do Gmail deve conter relato e contexto.");
        conferir(gmailDecodificado.contains("Versão do Gérard: C89"),
                "A versão do sistema deve acompanhar o relato.");

        String mailto = email.getUriMailto().toASCIIString();
        conferir(mailto.startsWith("mailto:anaemilia@gmail.com?"),
                "A contingência deve manter o destinatário no mailto.");
        conferir(mailto.contains("subject=") && mailto.contains("body="),
                "A contingência mailto deve manter assunto e corpo.");

        URI compatibilidade = PreparadorEmailRelatoBug.criarUri(
                "Teste", "SP", "CAT", "Vergnaud", "pt", "pt", "Enunciado");
        conferir(compatibilidade.toASCIIString().startsWith("mailto:anaemilia@gmail.com?"),
                "A API antiga deve continuar disponível para compatibilidade.");

        System.out.println("TESTE_EMAIL_RELATO_BUG_GMAIL_C89_OK");
    }
}
JAVA
javac -cp build/classes -d "$TMP/classes" "$TMP/TesteEmailRelatoBug.java"
java -cp "build/classes:$TMP/classes" TesteEmailRelatoBug
