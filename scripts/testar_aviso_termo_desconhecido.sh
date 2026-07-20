#!/usr/bin/env bash
set -euo pipefail
RAIZ="$(cd "$(dirname "$0")/.." && pwd)"
TMP="$(mktemp -d)"
trap 'rm -rf "$TMP"' EXIT
cat > "$TMP/TesteAvisoTermoDesconhecido.java" <<'JAVA'
import gerard.campoaditivo.curadoria.AvisoTermoDesconhecidoVazio;
import gerard.i18n.ServicoLocalizacao;
import javax.swing.JComboBox;

public class TesteAvisoTermoDesconhecido {
    private static int verificacoes;
    private static void conferir(boolean condicao, String mensagem) {
        verificacoes++;
        if (!condicao) throw new AssertionError(mensagem);
    }
    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "true");
        ServicoLocalizacao loc = ServicoLocalizacao.getInstancia();

        JComboBox<String> vazio = new JComboBox<String>(new String[]{"", "estado_inicial", "transformação", "estado_final"});
        AvisoTermoDesconhecidoVazio avisoVazio = new AvisoTermoDesconhecidoVazio(vazio, true, "", loc);
        conferir(avisoVazio.termoEstaVazio(), "o campo vazio deve ser detectado");
        conferir(avisoVazio.isAvisoVisivel(), "o aviso deve aparecer para campo vazio");
        conferir(avisoVazio.getTextoAviso().contains("está vazio"), "o texto deve explicar que o campo está vazio");

        vazio.setSelectedItem("estado_final");
        conferir(!avisoVazio.termoEstaVazio(), "a seleção deve preencher o termo");
        conferir(!avisoVazio.isAvisoVisivel(), "o aviso deve desaparecer após seleção explícita");

        JComboBox<String> harmonizado = new JComboBox<String>(new String[]{"", "transformação"});
        harmonizado.setSelectedItem("transformação");
        AvisoTermoDesconhecidoVazio avisoHarmonizado = new AvisoTermoDesconhecidoVazio(harmonizado, true, "transformação", loc);
        conferir(avisoHarmonizado.isAvisoVisivel(), "o aviso deve informar o preenchimento automático");
        conferir(avisoHarmonizado.getTextoAviso().contains("preenchido automaticamente"), "o texto deve mencionar a harmonização automática");

        avisoHarmonizado.definirSemanticaHerdada(true);
        conferir(!avisoHarmonizado.isAvisoVisivel(), "traduções com semântica herdada não devem exibir o aviso");

        System.out.println("PASSOU: " + verificacoes + " verificações do aviso de termo desconhecido.");
    }
}
JAVA
javac -encoding UTF-8 -cp "$RAIZ/build/classes" -d "$TMP" "$TMP/TesteAvisoTermoDesconhecido.java"
java -Djava.awt.headless=true -cp "$RAIZ/build/classes:$TMP" TesteAvisoTermoDesconhecido
