package gerard.semantica.situacao;

import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.semantica.categoria.CatalogoEsquemasCategoriasAditivas;
import gerard.semantica.categoria.EsquemaCategoriaAditiva;
import gerard.semantica.contexto.ContextoSituacao;
import gerard.semantica.elemento.ElementoEntidade;
import gerard.semantica.elemento.ElementoLinguistico;
import gerard.semantica.elemento.ElementoNumerico;
import gerard.semantica.elemento.ElementoSemantico;
import gerard.semantica.entidade.EntidadeSemantica;
import gerard.semantica.entidade.Personagem;
import gerard.semantica.numero.FabricaValoresNumericos;
import gerard.semantica.numero.ValorNumerico;
import gerard.semantica.papel.CatalogoPapeisSemanticos;
import gerard.semantica.papel.PapelQuantitativo;
import gerard.semantica.pista.LexicoPistasAditivas;
import gerard.semantica.pista.OcorrenciaPista;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Adapta os dados curados atuais ao novo modelo semântico explícito. */
public final class FabricaInstanciaSemanticaAditiva {
    private final CatalogoEsquemasCategoriasAditivas categorias =
            new CatalogoEsquemasCategoriasAditivas();
    private final CatalogoPapeisSemanticos papeis =
            new CatalogoPapeisSemanticos();
    private final FabricaValoresNumericos valores =
            new FabricaValoresNumericos();
    private final LexicoPistasAditivas lexico = new LexicoPistasAditivas();

    public InstanciaSituacaoAditiva criar(SituacaoProblemaAditiva situacao) {
        if (situacao == null) {
            return null;
        }
        EsquemaCategoriaAditiva esquema = categorias.obter(situacao.getTipo());
        List<EntidadeSemantica> entidades = criarPersonagens(situacao);
        List<ElementoSemantico> elementos = new ArrayList<ElementoSemantico>();
        int contador = 1;
        for (PapelQuantitativo papel : esquema.obterPapeis()) {
            String bruto = valorBrutoDoPapel(situacao, papel.getChave());
            boolean desconhecido = papelEhDesconhecido(situacao, papel.getChave())
                    || "?".equals(bruto);
            Integer numero = parseInteiro(bruto, papel.getChave(), situacao);
            ValorNumerico valor = desconhecido || numero == null
                    ? valores.desconhecido(papel.getDominio())
                    : valores.conhecido(papel.getDominio(), numero.intValue());
            elementos.add(new ElementoNumerico(
                    "numero_" + contador++, papel, valor, bruto));
        }
        for (EntidadeSemantica entidade : entidades) {
            elementos.add(new ElementoEntidade("entidade_" + contador++, entidade));
        }
        String idioma = situacao.getCodigoIdioma();
        List<OcorrenciaPista> ocorrencias = lexico.localizar(
                situacao.getEnunciado(), normalizarIdioma(idioma));
        for (OcorrenciaPista ocorrencia : ocorrencias) {
            elementos.add(new ElementoLinguistico(
                    "pista_" + contador++, ocorrencia));
        }
        return new InstanciaSituacaoAditiva(
                situacao.getId(), situacao.getEnunciado(), esquema,
                new ContextoSituacao(situacao.getContexto(), Collections.emptyList()),
                entidades, elementos, ocorrencias);
    }

    private List<EntidadeSemantica> criarPersonagens(SituacaoProblemaAditiva s) {
        List<EntidadeSemantica> resultado = new ArrayList<EntidadeSemantica>();
        adicionarPersonagem(resultado, "personagem_1", s.getPersonagem1());
        adicionarPersonagem(resultado, "personagem_2", s.getPersonagem2());
        adicionarPersonagem(resultado, "personagem_3", s.getPersonagem3());
        return resultado;
    }

    private void adicionarPersonagem(List<EntidadeSemantica> destino,
                                     String id, String nome) {
        if (nome != null && nome.trim().length() > 0) {
            destino.add(new Personagem(id, nome));
        }
    }

    private String valorBrutoDoPapel(SituacaoProblemaAditiva s, String papel) {
        if ("papel.estadoInicial".equals(papel)) return s.getEstadoInicial();
        if (papel.startsWith("papel.transformacao")) return s.getTransformacao();
        if ("papel.estadoFinal".equals(papel)) return primeiro(s.getEstadoFinal(), s.getResultado());
        if ("papel.parte1".equals(papel)) return s.getQuantidade1();
        if ("papel.parte2".equals(papel)) return s.getQuantidade2();
        if ("papel.todo".equals(papel)) return s.getResultado();
        if ("papel.referido".equals(papel)) return s.getReferido();
        if ("papel.referendo".equals(papel)) return s.getReferendo();
        if ("papel.diferenca".equals(papel)) return s.getValorRelativo();
        if (papel.startsWith("papel.relacao")) return s.getResultado();
        return "";
    }

    private Integer parseInteiro(String bruto, String papel,
                                 SituacaoProblemaAditiva s) {
        String texto = bruto == null ? "" : bruto.trim().replace(',', '.');
        if (texto.length() == 0 || "?".equals(texto)) return null;
        try {
            int valor = (int) Math.round(Double.parseDouble(texto.replace("+", "")));
            if (papel.startsWith("papel.transformacao")
                    && s.getSinalTransformacao() != null
                    && s.getSinalTransformacao().toLowerCase().contains("neg")) {
                return Integer.valueOf(-Math.abs(valor));
            }
            if ("papel.diferenca".equals(papel)
                    && s.getSinalValorRelativo() != null
                    && s.getSinalValorRelativo().toLowerCase().contains("neg")) {
                return Integer.valueOf(-Math.abs(valor));
            }
            return Integer.valueOf(valor);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private boolean papelEhDesconhecido(SituacaoProblemaAditiva s, String papel) {
        String desconhecido = s.getTermoDesconhecido();
        return desconhecido != null && normalizar(desconhecido).equals(normalizar(papel));
    }

    private String primeiro(String a, String b) {
        return a != null && a.trim().length() > 0 ? a : b;
    }

    private String normalizarIdioma(String codigo) {
        String c = codigo == null ? "" : codigo.toLowerCase();
        if (c.startsWith("en")) return "en";
        if (c.startsWith("fr")) return "fr";
        return "pt";
    }

    private String normalizar(String valor) {
        return valor == null ? "" : valor.toLowerCase()
                .replace("papel.", "").replace("_", "").replace(" ", "");
    }
}
