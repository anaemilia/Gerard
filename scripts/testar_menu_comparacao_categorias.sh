#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT
cat > "$TMP/TesteMenuComparacaoCategorias.java" <<'JAVA'
import gerard.ui.menu.ConfiguradorOpcaoComparacaoCategorias;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.JButton;

public final class TesteMenuComparacaoCategorias {
    public static void main(String[] args) {
        JButton opcao = new JButton("Comparação entre categorias");
        opcao.setEnabled(false);
        opcao.setToolTipText("Em construção");
        final AtomicInteger aberturas = new AtomicInteger();

        ConfiguradorOpcaoComparacaoCategorias.configurar(opcao,
                new Runnable() {
                    public void run() {
                        aberturas.incrementAndGet();
                    }
                });

        if (!opcao.isEnabled()) {
            throw new AssertionError("A opção permaneceu desabilitada.");
        }
        if (opcao.getToolTipText() != null) {
            throw new AssertionError("O tooltip de em construção permaneceu ativo.");
        }
        opcao.doClick();
        if (aberturas.get() != 1) {
            throw new AssertionError("A ação de abertura não foi executada exatamente uma vez.");
        }
        System.out.println("PASSOU: menu Comparação entre categorias habilitado e acionável.");
    }
}
JAVA
javac -cp "$ROOT/build/classes" -d "$TMP" "$TMP/TesteMenuComparacaoCategorias.java"
java -Djava.awt.headless=true -cp "$ROOT/build/classes:$TMP" TesteMenuComparacaoCategorias
