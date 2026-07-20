package gerard.campoaditivo.servico;

import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Mantém as invariantes dos vínculos entre a situação conceitual e suas versões
 * linguísticas. Não usa banco de dados nem altera o formato TSV.
 */
public final class ValidadorVinculosTraducoes {
    private static final String[] CAMPOS_CONCEITUAIS = new String[] {
        "tipo", "subtipo", "personagem_1", "personagem_2", "personagem_3",
        "estado_inicial", "transformacao", "sinal_transformacao", "estado_final",
        "quantidade_1", "quantidade_2", "resultado", "referido", "referendo",
        "valor_relativo", "sinal_valor_relativo", "termo_desconhecido",
        "representacao_visual", "observacoes"
    };

    private ValidadorVinculosTraducoes() { }

    public static void validarOuFalhar(List<SituacaoProblemaAditiva> situacoes) {
        List<String> erros = validar(situacoes);
        if (!erros.isEmpty()) {
            StringBuilder mensagem = new StringBuilder("Vínculos de tradução inválidos:");
            int limite = Math.min(8, erros.size());
            for (int i = 0; i < limite; i++) mensagem.append("\n- ").append(erros.get(i));
            if (erros.size() > limite) mensagem.append("\n- ... e mais ").append(erros.size() - limite).append(" inconsistência(s).");
            throw new IllegalArgumentException(mensagem.toString());
        }
    }

    public static List<String> validar(List<SituacaoProblemaAditiva> situacoes) {
        List<String> erros = new ArrayList<String>();
        erros.addAll(validarVinculosEstruturais(situacoes));
        erros.addAll(validarDivergenciasConceituais(situacoes));
        return erros;
    }

    public static List<String> validarVinculosEstruturais(List<SituacaoProblemaAditiva> situacoes) {
        List<String> erros = new ArrayList<String>();
        Map<String, SituacaoProblemaAditiva> porId = new LinkedHashMap<String, SituacaoProblemaAditiva>();
        Map<String, List<SituacaoProblemaAditiva>> porGrupo = new LinkedHashMap<String, List<SituacaoProblemaAditiva>>();
        for (SituacaoProblemaAditiva s : situacoes) {
            String id = limpar(s.getId());
            String grupo = limpar(s.getSituacaoGrupoId());
            if (id.length() == 0) { erros.add("há uma versão sem id"); continue; }
            if (porId.put(id, s) != null) erros.add("id duplicado: " + id);
            if (grupo.length() == 0) erros.add(id + " não possui situacao_grupo_id");
            List<SituacaoProblemaAditiva> lista = porGrupo.get(grupo);
            if (lista == null) { lista = new ArrayList<SituacaoProblemaAditiva>(); porGrupo.put(grupo, lista); }
            lista.add(s);
        }

        for (Map.Entry<String, List<SituacaoProblemaAditiva>> entrada : porGrupo.entrySet()) {
            String grupo = entrada.getKey();
            List<SituacaoProblemaAditiva> versoes = entrada.getValue();
            Set<String> idiomas = new HashSet<String>();
            List<SituacaoProblemaAditiva> originais = new ArrayList<SituacaoProblemaAditiva>();
            for (SituacaoProblemaAditiva s : versoes) {
                if (!idiomas.add(limpar(s.getCodigoIdioma()).toLowerCase(java.util.Locale.ROOT))) erros.add("grupo " + grupo + " possui mais de uma versão no idioma " + s.getCodigoIdioma());
                String tipo = limpar(s.getTipoVersao());
                if ("original".equals(tipo)) originais.add(s);
                else if (!"traducao".equals(tipo)) erros.add(s.getId() + " possui tipo_versao inválido: " + tipo);
            }
            if (originais.size() != 1) erros.add("grupo " + grupo + " deve possuir exatamente uma versão original (encontradas: " + originais.size() + ")");
            SituacaoProblemaAditiva referencia = originais.isEmpty() ? versoes.get(0) : originais.get(0);
            for (SituacaoProblemaAditiva s : versoes) {
                if ("original".equals(limpar(s.getTipoVersao()))) {
                    if (limpar(s.getVersaoOrigemId()).length() > 0) erros.add(s.getId() + " é uma versão original e não pode manter referência à origem");
                } else {
                    String origemId = limpar(s.getVersaoOrigemId());
                    SituacaoProblemaAditiva origem = porId.get(origemId);
                    if (origem == null) erros.add(s.getId() + " aponta para origem inexistente: " + origemId);
                    else if (!grupo.equals(limpar(origem.getSituacaoGrupoId()))) erros.add(s.getId() + " aponta para origem de outro grupo: " + origemId);
                    else if (!"original".equals(limpar(origem.getTipoVersao()))) erros.add(s.getId() + " deve apontar para uma versão original: " + origemId);
                }
            }
        }
        return erros;
    }

    public static List<String> validarDivergenciasConceituais(List<SituacaoProblemaAditiva> situacoes) {
        List<String> erros = new ArrayList<String>();
        Map<String, List<SituacaoProblemaAditiva>> porGrupo = new LinkedHashMap<String, List<SituacaoProblemaAditiva>>();
        for (SituacaoProblemaAditiva s : situacoes) {
            String grupo = limpar(s.getSituacaoGrupoId());
            if (grupo.length() == 0) continue;
            List<SituacaoProblemaAditiva> lista = porGrupo.get(grupo);
            if (lista == null) { lista = new ArrayList<SituacaoProblemaAditiva>(); porGrupo.put(grupo, lista); }
            lista.add(s);
        }
        for (Map.Entry<String, List<SituacaoProblemaAditiva>> entrada : porGrupo.entrySet()) {
            List<SituacaoProblemaAditiva> versoes = entrada.getValue();
            if (versoes.size() < 2) continue;
            SituacaoProblemaAditiva referencia = versoes.get(0);
            for (SituacaoProblemaAditiva s : versoes) {
                if ("original".equals(limpar(s.getTipoVersao()))) { referencia = s; break; }
            }
            for (SituacaoProblemaAditiva s : versoes) compararMetadados(entrada.getKey(), referencia, s, erros);
        }
        return erros;
    }

    private static void compararMetadados(String grupo, SituacaoProblemaAditiva a, SituacaoProblemaAditiva b, List<String> erros) {
        String[] va = valoresConceituais(a);
        String[] vb = valoresConceituais(b);
        for (int i = 0; i < CAMPOS_CONCEITUAIS.length; i++) {
            if (!normalizar(va[i]).equals(normalizar(vb[i]))) {
                erros.add("grupo " + grupo + ": campo conceitual " + CAMPOS_CONCEITUAIS[i]
                        + " diverge entre " + a.getId() + " e " + b.getId());
            }
        }
    }

    private static String[] valoresConceituais(SituacaoProblemaAditiva s) {
        return new String[] { s.getTipo().name(), s.getSubtipo(), s.getPersonagem1(),
            s.getPersonagem2(), s.getPersonagem3(), s.getEstadoInicial(), s.getTransformacao(),
            s.getSinalTransformacao(), s.getEstadoFinal(), s.getQuantidade1(), s.getQuantidade2(),
            s.getResultado(), s.getReferido(), s.getReferendo(), s.getValorRelativo(),
            s.getSinalValorRelativo(), s.getTermoDesconhecido(), s.getRepresentacaoVisual(),
            s.getObservacoes() };
    }

    private static String normalizar(String v) {
        return limpar(v).replace(',', '.').replaceAll("\\s+", " ").toLowerCase(java.util.Locale.ROOT);
    }
    private static String limpar(String v) { return v == null ? "" : v.trim(); }
}
