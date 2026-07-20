#!/usr/bin/env bash
set -euo pipefail
DIR="$(cd "$(dirname "$0")/.." && pwd)"
TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT
cat > "$TMP/TesteJanelasResponsivas.java" <<'JAVA'
import gerard.ui.janela.DimensionadorJanelaComparacaoCategorias;
import java.awt.Dimension;
import java.awt.Rectangle;

public class TesteJanelasResponsivas {
    private static int testes;
    private static void verificar(boolean condicao, String mensagem) {
        testes++;
        if (!condicao) throw new AssertionError(mensagem);
    }
    public static void main(String[] args) {
        Rectangle tela = new Rectangle(0, 0, 1920, 1040);
        Rectangle principal = new Rectangle(0, 0, 1920, 1040);
        Dimension d = DimensionadorJanelaComparacaoCategorias.calcularDimensao(
                principal, tela, null);
        verificar(d.width == 1728, "largura deve respeitar 90% do monitor");
        verificar(d.height == 884, "altura deve usar 85% da principal");

        Rectangle notebook = new Rectangle(0, 0, 1366, 728);
        Dimension dn = DimensionadorJanelaComparacaoCategorias.calcularDimensao(
                notebook, notebook, null);
        verificar(dn.width <= 1229, "não ultrapassa 90% da largura útil");
        verificar(dn.height <= 655, "não ultrapassa 90% da altura útil");
        verificar(dn.width >= 960, "mantém mínimo quando possível");
        verificar(dn.height >= 620, "mantém mínimo quando possível");

        Dimension lembrada = DimensionadorJanelaComparacaoCategorias.calcularDimensao(
                principal, tela, new Dimension(1100, 700));
        verificar(lembrada.width == 1100 && lembrada.height == 700,
                "reutiliza tamanho da sessão dentro dos limites");

        Rectangle pos = DimensionadorJanelaComparacaoCategorias.calcularPosicaoCentralizada(
                new Rectangle(100, 50, 1200, 800),
                new Rectangle(0, 0, 1600, 900),
                new Dimension(1000, 700));
        verificar(pos.x == 200 && pos.y == 100,
                "centraliza sobre a proprietária");

        Rectangle posLimitada = DimensionadorJanelaComparacaoCategorias.calcularPosicaoCentralizada(
                new Rectangle(1500, 800, 300, 200),
                new Rectangle(0, 0, 1600, 900),
                new Dimension(1000, 700));
        verificar(posLimitada.x == 600 && posLimitada.y == 200,
                "mantém diálogo dentro da área útil");

        System.out.println("PASSOU: " + testes + " verificações de dimensionamento.");
    }
}
JAVA
mkdir -p "$TMP/classes"
javac -encoding UTF-8 -source 8 -target 8 \
  -d "$TMP/classes" \
  "$DIR/src/gerard/ui/janela/DimensionadorJanelaComparacaoCategorias.java" \
  "$TMP/TesteJanelasResponsivas.java"
java -Djava.awt.headless=true -cp "$TMP/classes" TesteJanelasResponsivas

grep -q 'Configurator\|ConfiguradorJanelaPrincipal.aplicar(this)' "$DIR/src/Main.java" || {
  echo 'FALHOU: Main não delega configuração da janela principal.' >&2
  exit 1
}
grep -q 'DimensionadorJanelaComparacaoCategorias.aplicar' "$DIR/src/Main.java" || {
  echo 'FALHOU: diálogo comparativo não usa dimensionador responsivo.' >&2
  exit 1
}
grep -q 'MAXIMIZED_BOTH' "$DIR/src/gerard/ui/janela/ConfiguradorJanelaPrincipal.java" || {
  echo 'FALHOU: janela principal não inicia maximizada.' >&2
  exit 1
}
echo 'PASSOU: integração estrutural das janelas.'
