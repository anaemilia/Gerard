package gerard.aplicacao.portabilidade;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Porta do menu "E agora?" (ajuda contextual) usada pela web — área e
 * intenção são identificadas por nome (mesma representação já usada na
 * fronteira HTTP), para que a camada de aplicação não precise importar os
 * enums do scaffolding concreto. A implementação real vive fora da camada de
 * aplicação (ver {@code gerard.infraestrutura.web.scaffolding.AdaptadorAjudaContextualWeb}), entregue pela raiz de composição.
 */
public interface PortaAjudaContextual {

    /**
     * Valor neutro da aplicação: sem adaptador fornecido pela raiz de
     * composição, o menu "E agora?" não projeta conteúdo algum (nenhum texto
     * é inventado aqui).
     */
    PortaAjudaContextual NENHUMA = new PortaAjudaContextual() {
        public Map<String, Object> projetarArea(String area) {
            return new LinkedHashMap<String, Object>();
        }

        public String nomeArea(String area) {
            return area;
        }

        public String rotuloOpcao(String intencao) {
            return intencao;
        }

        public String mensagem(String area, String intencao) {
            return "";
        }
    };

    Map<String, Object> projetarArea(String area);

    String nomeArea(String area);

    String rotuloOpcao(String intencao);

    String mensagem(String area, String intencao);
}
