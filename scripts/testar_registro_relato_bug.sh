#!/usr/bin/env bash
set -euo pipefail
RAIZ="$(cd "$(dirname "$0")/.." && pwd)"
TMP="${TMPDIR:-/tmp}/gerard_relato_bug_c75"
rm -rf "$TMP"
mkdir -p "$TMP/home" "$TMP/classes"
cd "$RAIZ"
ant -noinput -q compile
cat > "$TMP/TesteRegistroRelatoBug.java" <<'JAVA'
import gerard.suporte.RegistroRelatoBug;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class TesteRegistroRelatoBug {
    private static void conferir(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }

    public static void main(String[] args) throws Exception {
        File arquivo = RegistroRelatoBug.registrar(
                "Arrastei o valor e o diagrama não atualizou.",
                "SP_TESTE", "COMPOSICAO_MEDIDAS",
                "Diagrama de Vergnaud; Diagrama de composição de coleções",
                "PORTUGUES", "pt", "Enunciado de teste");
        conferir(arquivo.isFile(), "O arquivo de relatos deve ser criado.");
        List<String> linhas = Files.readAllLines(arquivo.toPath(), StandardCharsets.UTF_8);
        conferir(linhas.size() == 2, "O arquivo deve conter cabeçalho e um relato.");
        conferir(linhas.get(0).contains("descricao") && linhas.get(0).contains("situacao_id")
                && linhas.get(0).contains("representacoes"),
                "O cabeçalho deve documentar os campos.");
        conferir(linhas.get(1).contains("SP_TESTE")
                && linhas.get(1).contains("COMPOSICAO_MEDIDAS")
                && linhas.get(1).contains("Diagrama de composição de coleções"),
                "O contexto e as representações devem acompanhar o relato.");
        System.out.println("TESTE_REGISTRO_RELATO_BUG_OK");
    }
}
JAVA
javac -cp build/classes -d "$TMP/classes" "$TMP/TesteRegistroRelatoBug.java"
java -Duser.home="$TMP/home" -cp "build/classes:$TMP/classes" TesteRegistroRelatoBug
