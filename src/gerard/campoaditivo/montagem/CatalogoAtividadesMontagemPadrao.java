package gerard.campoaditivo.montagem;

import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.idioma.IdiomaInterface;
import java.util.ArrayList;
import java.util.List;

/**
 * Pequeno catálogo de atividades de referência para a aba de construção.
 *
 * O catálogo garante variedade mínima mesmo quando o pesquisador ainda
 * validou apenas uma situação no corpus local. Os três exemplos usam o mesmo
 * contexto, personagens e valores (4, 7 e 11), variando a relação semântica
 * entre composição, transformação e comparação de medidas.
 */
public final class CatalogoAtividadesMontagemPadrao {
    private CatalogoAtividadesMontagemPadrao() {
    }

    public static List<SituacaoProblemaAditiva> listar(IdiomaInterface idioma) {
        IdiomaInterface efetivo = idioma == null ? IdiomaInterface.PORTUGUES : idioma;
        if (efetivo == IdiomaInterface.INGLES) {
            return ingles();
        }
        if (efetivo == IdiomaInterface.FRANCES) {
            return frances();
        }
        return portugues();
    }

    private static List<SituacaoProblemaAditiva> portugues() {
        List<SituacaoProblemaAditiva> lista = new ArrayList<SituacaoProblemaAditiva>();
        lista.add(composicao(
                "MONT_PADRAO_PT_CM_01", IdiomaInterface.PORTUGUES,
                "Paulo tem 4 bolas azuis e José tem 7 bolas vermelhas. Quantas bolas os dois têm ao todo?",
                "Bolas"));
        lista.add(transformacao(
                "MONT_PADRAO_PT_TM_01", IdiomaInterface.PORTUGUES,
                "Paulo tinha 4 bolas. José lhe deu mais 7 bolas. Com quantas bolas Paulo ficou?",
                "Bolas"));
        lista.add(comparacao(
                "MONT_PADRAO_PT_COP_01", IdiomaInterface.PORTUGUES,
                "Paulo tem 4 bolas. José tem 7 bolas a mais que Paulo. Quantas bolas tem José?",
                "Bolas"));
        return lista;
    }

    private static List<SituacaoProblemaAditiva> ingles() {
        List<SituacaoProblemaAditiva> lista = new ArrayList<SituacaoProblemaAditiva>();
        lista.add(composicao(
                "MONT_PADRAO_EN_CM_01", IdiomaInterface.INGLES,
                "Paul has 4 blue balls and Joseph has 7 red balls. How many balls do they have altogether?",
                "Balls"));
        lista.add(transformacao(
                "MONT_PADRAO_EN_TM_01", IdiomaInterface.INGLES,
                "Paul had 4 balls. Joseph gave him 7 more balls. How many balls does Paul have now?",
                "Balls"));
        lista.add(comparacao(
                "MONT_PADRAO_EN_COP_01", IdiomaInterface.INGLES,
                "Paul has 4 balls. Joseph has 7 more balls than Paul. How many balls does Joseph have?",
                "Balls"));
        return lista;
    }

    private static List<SituacaoProblemaAditiva> frances() {
        List<SituacaoProblemaAditiva> lista = new ArrayList<SituacaoProblemaAditiva>();
        lista.add(composicao(
                "MONT_PADRAO_FR_CM_01", IdiomaInterface.FRANCES,
                "Paul a 4 balles bleues et Joseph a 7 balles rouges. Combien de balles ont-ils en tout ?",
                "Balles"));
        lista.add(transformacao(
                "MONT_PADRAO_FR_TM_01", IdiomaInterface.FRANCES,
                "Paul avait 4 balles. Joseph lui a donné 7 balles de plus. Combien de balles Paul a-t-il maintenant ?",
                "Balles"));
        lista.add(comparacao(
                "MONT_PADRAO_FR_COP_01", IdiomaInterface.FRANCES,
                "Paul a 4 balles. Joseph a 7 balles de plus que Paul. Combien de balles Joseph a-t-il ?",
                "Balles"));
        return lista;
    }

    private static SituacaoProblemaAditiva composicao(String id, IdiomaInterface idioma,
                                                        String enunciado, String contexto) {
        return new SituacaoProblemaAditiva(
                id, true, TipoSituacaoAditiva.COMPOSICAO_MEDIDAS, idioma,
                enunciado, contexto, "catalogo_montagem_pesquisador", "referencia_controlada",
                "", "", "", "",
                "4", "7", "11",
                "", "", "", "",
                "", TipoSituacaoAditiva.COMPOSICAO_MEDIDAS.name(),
                "Atividade de referência introduzida pelo pesquisador para garantir variedade mínima e distância semântica controlada.");
    }

    private static SituacaoProblemaAditiva transformacao(String id, IdiomaInterface idioma,
                                                          String enunciado, String contexto) {
        return new SituacaoProblemaAditiva(
                id, true, TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS, idioma,
                enunciado, contexto, "catalogo_montagem_pesquisador", "referencia_controlada",
                "4", "7", "positivo", "11",
                "", "", "",
                "", "", "", "",
                "", TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS.name(),
                "Atividade de referência introduzida pelo pesquisador para garantir variedade mínima e distância semântica controlada.");
    }

    private static SituacaoProblemaAditiva comparacao(String id, IdiomaInterface idioma,
                                                        String enunciado, String contexto) {
        return new SituacaoProblemaAditiva(
                id, true, TipoSituacaoAditiva.COMPARACAO_MEDIDAS, idioma,
                enunciado, contexto, "catalogo_montagem_pesquisador", "referencia_controlada",
                "", "", "", "",
                "", "", "",
                "4", "11", "7", "positivo",
                "", TipoSituacaoAditiva.COMPARACAO_MEDIDAS.name(),
                "Atividade de referência introduzida pelo pesquisador para garantir variedade mínima e distância semântica controlada.");
    }
}
