package gerard.campoaditivo.montagem;

import gerard.campoaditivo.curadoria.ResolvedorIncognitaCurada;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.idioma.IdiomaInterface;
import gerard.interpretacao.simbolo.SimboloDesconhecido;
import java.text.BreakIterator;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Prepara blocos corretos e blocos semanticamente próximos para a atividade.
 *
 * Princípio pedagógico: os blocos não compatíveis preservam personagens,
 * objeto, valores e forma linguística aproximada. A diferença relevante deve
 * residir na relação entre as quantidades, não em pistas superficiais.
 */
public final class GeradorBlocosMontagem {
    private static final Set<TipoSituacaoAditiva> TIPOS_SUPORTADOS = new HashSet<TipoSituacaoAditiva>();
    private static final Pattern NOME_PROPRIO = Pattern.compile("(?U)\\b\\p{Lu}[\\p{L}'’\\-]+\\b");
    private static final Pattern QUANTIDADE_TEXTUAL = Pattern.compile(
            "(?iu)\\b(?:um|uma|dois|duas|três|tres|quatro|cinco|seis|sete|oito|nove|dez|onze|doze|treze|quatorze|catorze|quinze|dezesseis|dezessete|dezoito|dezenove|vinte|one|two|three|four|five|six|seven|eight|nine|ten|eleven|twelve|thirteen|fourteen|fifteen|sixteen|seventeen|eighteen|nineteen|twenty|un|une|deux|trois|quatre|cinq|six|sept|huit|neuf|dix|onze|douze|treize|quatorze|quinze|seize)\\b");

    private static final java.util.Map<TipoSituacaoAditiva, String[]> PAPEIS_DIAGRAMA_POR_TIPO =
            new java.util.EnumMap<TipoSituacaoAditiva, String[]>(TipoSituacaoAditiva.class);

    static {
        TIPOS_SUPORTADOS.add(TipoSituacaoAditiva.COMPOSICAO_MEDIDAS);
        TIPOS_SUPORTADOS.add(TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS);
        TIPOS_SUPORTADOS.add(TipoSituacaoAditiva.COMPARACAO_MEDIDAS);

        PAPEIS_DIAGRAMA_POR_TIPO.put(TipoSituacaoAditiva.COMPOSICAO_MEDIDAS,
                new String[] { "papel.parte1", "papel.parte2", "papel.todo" });
        PAPEIS_DIAGRAMA_POR_TIPO.put(TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS,
                new String[] { "papel.estadoInicial", "papel.transformacao", "papel.estadoFinal" });
        PAPEIS_DIAGRAMA_POR_TIPO.put(TipoSituacaoAditiva.COMPARACAO_MEDIDAS,
                new String[] { "papel.referido", "papel.diferenca", "papel.referendo" });
    }

    private final Random random;

    public GeradorBlocosMontagem() {
        this(new Random());
    }

    GeradorBlocosMontagem(Random random) {
        this.random = random == null ? new Random() : random;
    }

    public boolean podeGerar(SituacaoProblemaAditiva situacao) {
        if (situacao == null || situacao.getTipo() == null || !TIPOS_SUPORTADOS.contains(situacao.getTipo())) {
            return false;
        }
        if (segmentarEnunciado(situacao).size() < 2) {
            return false;
        }
        String[] valores = obterValoresDiagrama(situacao);
        if (valores.length != 3) {
            return false;
        }
        for (String valor : valores) {
            if (vazio(valor) || SimboloDesconhecido.eh(valor)) {
                return false;
            }
        }
        // A construção exige que os três papéis do diagrama já sejam
        // conhecidos: uma situação cuja incógnita caia em um deles não pode
        // ser usada aqui, senão o valor curado do papel escondido vazaria no
        // enunciado/diagrama gerado antes de o aluno resolver.
        String incognita = new ResolvedorIncognitaCurada().resolver(situacao).getChaveEfetiva();
        if (incognita.length() > 0) {
            String[] papeisDoTipo = PAPEIS_DIAGRAMA_POR_TIPO.get(situacao.getTipo());
            if (papeisDoTipo != null) {
                for (String papel : papeisDoTipo) {
                    if (papel.equals(incognita)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public ConjuntoBlocosMontagem gerar(SituacaoProblemaAditiva situacao) {
        if (!podeGerar(situacao)) {
            throw new IllegalArgumentException("A situação não possui estrutura suficiente para a construção.");
        }

        List<String> sentencas = segmentarEnunciado(situacao);
        List<BlocoTextoMontagem> blocos = new ArrayList<BlocoTextoMontagem>();
        List<String> corretosOrdenados = new ArrayList<String>();

        for (int i = 0; i < sentencas.size(); i++) {
            String id = "correto_" + i;
            blocos.add(new BlocoTextoMontagem(id, sentencas.get(i), true, i,
                    situacao.getTipo(), papelCorretoAproximado(situacao.getTipo(), i, sentencas.size())));
            corretosOrdenados.add(id);
        }

        String[] valores = obterValoresDiagrama(situacao);
        Entidades entidades = extrairEntidades(situacao);
        List<BlocoTextoMontagem> naoCompativeis = criarBlocosNaoCompativeis(situacao, entidades, valores);
        adicionarSemDuplicar(blocos, naoCompativeis, sentencas);

        Collections.shuffle(blocos, random);
        return new ConjuntoBlocosMontagem(situacao, blocos, corretosOrdenados, valores);
    }

    public String[] obterValoresDiagrama(SituacaoProblemaAditiva situacao) {
        if (situacao == null || situacao.getTipo() == null) {
            return new String[0];
        }
        switch (situacao.getTipo()) {
            case COMPOSICAO_MEDIDAS:
                return new String[] {
                    limparValor(situacao.getQuantidade1()),
                    limparValor(situacao.getQuantidade2()),
                    limparValor(situacao.getResultado())
                };
            case TRANSFORMACAO_MEDIDAS:
                return new String[] {
                    limparValor(situacao.getEstadoInicial()),
                    aplicarSinal(situacao.getTransformacao(), situacao.getSinalTransformacao()),
                    limparValor(situacao.getEstadoFinal())
                };
            case COMPARACAO_MEDIDAS:
                return new String[] {
                    limparValor(situacao.getReferido()),
                    aplicarSinal(situacao.getValorRelativo(), situacao.getSinalValorRelativo()),
                    limparValor(situacao.getReferendo())
                };
            default:
                return new String[0];
        }
    }

    private List<String> segmentarEnunciado(SituacaoProblemaAditiva situacao) {
        String texto = situacao == null ? "" : situacao.getEnunciado();
        Locale locale = locale(situacao == null ? null : situacao.getIdioma());
        List<String> resultado = new ArrayList<String>();
        if (texto == null || texto.trim().length() == 0) {
            return resultado;
        }

        BreakIterator iterador = BreakIterator.getSentenceInstance(locale);
        iterador.setText(texto.trim());
        int inicio = iterador.first();
        int fim = iterador.next();
        while (fim != BreakIterator.DONE) {
            String trecho = texto.trim().substring(inicio, fim).trim();
            if (trecho.length() > 0) {
                resultado.add(trecho);
            }
            inicio = fim;
            fim = iterador.next();
        }

        if (resultado.size() == 1) {
            resultado = segmentarPorClausulas(texto.trim());
        }
        return agruparContextoComBlocoQuantitativo(resultado);
    }

    /**
     * Evita que uma frase apenas contextual se torne uma pista visual óbvia.
     * Ela é anexada ao bloco quantitativo seguinte; perguntas permanecem em
     * bloco próprio para preservar a estrutura da situação-problema.
     */
    private List<String> agruparContextoComBlocoQuantitativo(List<String> sentencas) {
        List<String> agrupadas = new ArrayList<String>();
        String pendente = "";
        for (String sentenca : sentencas) {
            String atual = sentenca == null ? "" : sentenca.trim();
            if (atual.length() == 0) {
                continue;
            }
            boolean pergunta = atual.indexOf('?') >= 0;
            boolean quantitativa = contemQuantidade(atual);
            if (!pergunta && !quantitativa) {
                pendente = pendente.length() == 0 ? atual : pendente + " " + atual;
                continue;
            }
            if (pendente.length() > 0) {
                atual = pendente + " " + atual;
                pendente = "";
            }
            agrupadas.add(atual);
        }
        if (pendente.length() > 0) {
            if (agrupadas.isEmpty()) {
                agrupadas.add(pendente);
            } else {
                int ultimo = agrupadas.size() - 1;
                agrupadas.set(ultimo, agrupadas.get(ultimo) + " " + pendente);
            }
        }
        return agrupadas;
    }

    private boolean contemQuantidade(String texto) {
        return texto.matches(".*[0-9].*") || QUANTIDADE_TEXTUAL.matcher(texto).find();
    }

    private List<String> segmentarPorClausulas(String texto) {
        List<String> resultado = new ArrayList<String>();
        String[] partes = texto.split("(?<=,)|(?<=;)|(?<=:)\\s+");
        for (String parte : partes) {
            String p = parte.trim();
            if (p.length() > 0) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    private List<BlocoTextoMontagem> criarBlocosNaoCompativeis(SituacaoProblemaAditiva situacao,
                                                                 Entidades e,
                                                                 String[] v) {
        IdiomaInterface idioma = situacao.getIdioma();
        if (idioma == IdiomaInterface.INGLES) {
            return criarEmIngles(situacao.getTipo(), e, v);
        }
        if (idioma == IdiomaInterface.FRANCES) {
            return criarEmFrances(situacao.getTipo(), e, v);
        }
        return criarEmPortugues(situacao.getTipo(), e, v);
    }

    private List<BlocoTextoMontagem> criarEmPortugues(TipoSituacaoAditiva tipo, Entidades e, String[] v) {
        List<BlocoTextoMontagem> b = new ArrayList<BlocoTextoMontagem>();
        String a = e.nome1;
        String c = e.nome2;
        String obj = e.objeto;
        String relativo = semSinal(v[1]);
        boolean negativo = ehNegativo(v[1]);
        String verboMudanca = negativo ? " perdeu " : " recebeu ";
        String comparativo = negativo ? " a menos que " : " a mais que ";
        String quantos = quantificadorPt(obj);
        String comQuantos = "Com " + quantos.toLowerCase(locale(IdiomaInterface.PORTUGUES));
        if (tipo == TipoSituacaoAditiva.COMPARACAO_MEDIDAS) {
            b.add(d("d_tm_rel", c + verboMudanca + relativo + " " + obj + " de " + a + ".",
                    TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS, "transformacao"));
            b.add(d("d_cm_rel", a + " tem " + v[0] + " " + obj + " e " + c + " tem " + relativo + " " + obj + ".",
                    TipoSituacaoAditiva.COMPOSICAO_MEDIDAS, "partes"));
            b.add(d("d_tm_q", comQuantos + " " + obj + " " + c + " ficou?",
                    TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS, "estado_final"));
            b.add(d("d_cm_q", quantos + " " + obj + " os dois têm ao todo?",
                    TipoSituacaoAditiva.COMPOSICAO_MEDIDAS, "todo"));
        } else if (tipo == TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS) {
            b.add(d("d_cop_rel", c + " tem " + relativo + " " + obj + comparativo + a + ".",
                    TipoSituacaoAditiva.COMPARACAO_MEDIDAS, "valor_relativo"));
            b.add(d("d_cm_rel", a + " tem " + v[0] + " " + obj + " e " + c + " tem " + relativo + " " + obj + ".",
                    TipoSituacaoAditiva.COMPOSICAO_MEDIDAS, "partes"));
            b.add(d("d_cop_q", quantos + " " + obj + " " + c + " tem" + comparativo + a + "?",
                    TipoSituacaoAditiva.COMPARACAO_MEDIDAS, "valor_relativo"));
            b.add(d("d_cm_q", quantos + " " + obj + " os dois têm ao todo?",
                    TipoSituacaoAditiva.COMPOSICAO_MEDIDAS, "todo"));
        } else {
            b.add(d("d_tm_rel", a + verboMudanca + relativo + " " + obj + ".",
                    TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS, "transformacao"));
            b.add(d("d_cop_rel", c + " tem " + relativo + " " + obj + comparativo + a + ".",
                    TipoSituacaoAditiva.COMPARACAO_MEDIDAS, "valor_relativo"));
            b.add(d("d_tm_q", comQuantos + " " + obj + " " + a + " ficou?",
                    TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS, "estado_final"));
            b.add(d("d_cop_q", quantos + " " + obj + " " + c + " tem" + comparativo + a + "?",
                    TipoSituacaoAditiva.COMPARACAO_MEDIDAS, "valor_relativo"));
        }
        return b;
    }

    private List<BlocoTextoMontagem> criarEmIngles(TipoSituacaoAditiva tipo, Entidades e, String[] v) {
        List<BlocoTextoMontagem> b = new ArrayList<BlocoTextoMontagem>();
        String a = e.nome1;
        String c = e.nome2;
        String obj = e.objeto;
        String relativo = semSinal(v[1]);
        boolean negativo = ehNegativo(v[1]);
        String verboMudanca = negativo ? " lost " : " received ";
        String comparativo = negativo ? " fewer " : " more ";
        if (tipo == TipoSituacaoAditiva.COMPARACAO_MEDIDAS) {
            b.add(d("d_tm_rel", c + verboMudanca + relativo + " " + obj + " from " + a + ".",
                    TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS, "transformation"));
            b.add(d("d_cm_rel", a + " has " + v[0] + " " + obj + " and " + c + " has " + relativo + " " + obj + ".",
                    TipoSituacaoAditiva.COMPOSICAO_MEDIDAS, "parts"));
            b.add(d("d_tm_q", "How many " + obj + " did " + c + " have after that?",
                    TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS, "final_state"));
            b.add(d("d_cm_q", "How many " + obj + " do they have altogether?",
                    TipoSituacaoAditiva.COMPOSICAO_MEDIDAS, "whole"));
        } else if (tipo == TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS) {
            b.add(d("d_cop_rel", c + " has " + relativo + comparativo + obj + " than " + a + ".",
                    TipoSituacaoAditiva.COMPARACAO_MEDIDAS, "relative_value"));
            b.add(d("d_cm_rel", a + " has " + v[0] + " " + obj + " and " + c + " has " + relativo + " " + obj + ".",
                    TipoSituacaoAditiva.COMPOSICAO_MEDIDAS, "parts"));
            b.add(d("d_cop_q", "How many" + comparativo + obj + " does " + c + " have than " + a + "?",
                    TipoSituacaoAditiva.COMPARACAO_MEDIDAS, "relative_value"));
            b.add(d("d_cm_q", "How many " + obj + " do they have altogether?",
                    TipoSituacaoAditiva.COMPOSICAO_MEDIDAS, "whole"));
        } else {
            b.add(d("d_tm_rel", a + verboMudanca + relativo + " " + obj + ".",
                    TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS, "transformation"));
            b.add(d("d_cop_rel", c + " has " + relativo + comparativo + obj + " than " + a + ".",
                    TipoSituacaoAditiva.COMPARACAO_MEDIDAS, "relative_value"));
            b.add(d("d_tm_q", "How many " + obj + " did " + a + " have after that?",
                    TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS, "final_state"));
            b.add(d("d_cop_q", "How many" + comparativo + obj + " does " + c + " have than " + a + "?",
                    TipoSituacaoAditiva.COMPARACAO_MEDIDAS, "relative_value"));
        }
        return b;
    }

    private List<BlocoTextoMontagem> criarEmFrances(TipoSituacaoAditiva tipo, Entidades e, String[] v) {
        List<BlocoTextoMontagem> b = new ArrayList<BlocoTextoMontagem>();
        String a = e.nome1;
        String c = e.nome2;
        String obj = e.objeto;
        String relativo = semSinal(v[1]);
        boolean negativo = ehNegativo(v[1]);
        String verboMudanca = negativo ? " a perdu " : " a reçu ";
        String comparativo = negativo ? " de moins que " : " de plus que ";
        if (tipo == TipoSituacaoAditiva.COMPARACAO_MEDIDAS) {
            b.add(d("d_tm_rel", c + verboMudanca + relativo + " " + obj + " de " + a + ".",
                    TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS, "transformation"));
            b.add(d("d_cm_rel", a + " a " + v[0] + " " + obj + " et " + c + " a " + relativo + " " + obj + ".",
                    TipoSituacaoAditiva.COMPOSICAO_MEDIDAS, "parties"));
            b.add(d("d_tm_q", "Combien de " + obj + " " + c + " a-t-il après cela ?",
                    TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS, "etat_final"));
            b.add(d("d_cm_q", "Combien de " + obj + " ont-ils en tout ?",
                    TipoSituacaoAditiva.COMPOSICAO_MEDIDAS, "tout"));
        } else if (tipo == TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS) {
            b.add(d("d_cop_rel", c + " a " + relativo + " " + obj + comparativo + a + ".",
                    TipoSituacaoAditiva.COMPARACAO_MEDIDAS, "valeur_relative"));
            b.add(d("d_cm_rel", a + " a " + v[0] + " " + obj + " et " + c + " a " + relativo + " " + obj + ".",
                    TipoSituacaoAditiva.COMPOSICAO_MEDIDAS, "parties"));
            b.add(d("d_cop_q", "Combien de " + obj + comparativo + c + " a-t-il par rapport à " + a + " ?",
                    TipoSituacaoAditiva.COMPARACAO_MEDIDAS, "valeur_relative"));
            b.add(d("d_cm_q", "Combien de " + obj + " ont-ils en tout ?",
                    TipoSituacaoAditiva.COMPOSICAO_MEDIDAS, "tout"));
        } else {
            b.add(d("d_tm_rel", a + verboMudanca + relativo + " " + obj + ".",
                    TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS, "transformation"));
            b.add(d("d_cop_rel", c + " a " + relativo + " " + obj + comparativo + a + ".",
                    TipoSituacaoAditiva.COMPARACAO_MEDIDAS, "valeur_relative"));
            b.add(d("d_tm_q", "Combien de " + obj + " " + a + " a-t-il après cela ?",
                    TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS, "etat_final"));
            b.add(d("d_cop_q", "Combien de " + obj + comparativo + c + " a-t-il par rapport à " + a + " ?",
                    TipoSituacaoAditiva.COMPARACAO_MEDIDAS, "valeur_relative"));
        }
        return b;
    }

    private BlocoTextoMontagem d(String id, String texto, TipoSituacaoAditiva categoria, String papel) {
        return new BlocoTextoMontagem(id, texto, false, -1, categoria, papel);
    }

    private void adicionarSemDuplicar(List<BlocoTextoMontagem> destino,
                                      List<BlocoTextoMontagem> candidatos,
                                      List<String> corretos) {
        Set<String> normalizados = new LinkedHashSet<String>();
        for (String correto : corretos) {
            normalizados.add(normalizar(correto));
        }
        for (BlocoTextoMontagem existente : destino) {
            normalizados.add(normalizar(existente.getTexto()));
        }
        int maximo = 4;
        int adicionados = 0;
        for (BlocoTextoMontagem candidato : candidatos) {
            String chave = normalizar(candidato.getTexto());
            if (chave.length() == 0 || normalizados.contains(chave)) {
                continue;
            }
            destino.add(candidato);
            normalizados.add(chave);
            adicionados++;
            if (adicionados >= maximo) {
                break;
            }
        }
    }

    private Entidades extrairEntidades(SituacaoProblemaAditiva situacao) {
        List<String> nomes = new ArrayList<String>();
        Matcher matcher = NOME_PROPRIO.matcher(situacao.getEnunciado());
        while (matcher.find()) {
            String nome = matcher.group();
            if (!ehPalavraInicialGenerica(nome) && !nomes.contains(nome)) {
                nomes.add(nome);
            }
        }

        IdiomaInterface idioma = situacao.getIdioma();
        String nome1 = nomes.size() > 0 ? nomes.get(0) : nomePadrao1(idioma);
        String nome2 = nomes.size() > 1 ? nomes.get(1) : nomePadrao2(idioma);
        if (nome1.equals(nome2)) {
            nome2 = nomePadrao2(idioma);
        }

        String objeto = extrairObjetoDoEnunciado(situacao.getEnunciado(), idioma);
        if (objeto.length() == 0) {
            objeto = situacao.getContexto() == null ? "" : situacao.getContexto().trim().toLowerCase(locale(idioma));
        }
        if (objeto.length() == 0) {
            objeto = objetoPadrao(idioma);
        }
        objeto = removerPrefixosContexto(objeto, idioma);
        objeto = objeto.replaceFirst("(?i)\\s+(e|and|et)\\s+.*$", "").trim();
        return new Entidades(nome1, nome2, objeto);
    }

    private String extrairObjetoDoEnunciado(String enunciado, IdiomaInterface idioma) {
        if (enunciado == null) return "";
        String n = normalizar(enunciado);
        if (n.contains("r$ ") || n.matches(".*\\breais?\\b.*")) return "reais";
        if (idioma == IdiomaInterface.INGLES && n.matches(".*\\byears?\\b.*")) return "years";
        if (idioma == IdiomaInterface.FRANCES && n.matches(".*\\bans?\\b.*")) return "ans";
        if (idioma == IdiomaInterface.PORTUGUES && n.matches(".*\\banos?\\b.*")) return "anos";

        Matcher m = Pattern.compile("(?U)(?:R\\$\\s*)?[+-]?\\d+(?:[.,]\\d+)?\\s+(?:de\\s+|d['’]\\s*)?([\\p{L}][\\p{L}'’\\-]+)")
                .matcher(enunciado);
        while (m.find()) {
            String candidato = m.group(1).toLowerCase(locale(idioma));
            String chave = normalizar(candidato);
            if (!chave.equals("a") && !chave.equals("mais") && !chave.equals("menos")
                    && !chave.equals("more") && !chave.equals("less") && !chave.equals("plus")
                    && !chave.equals("moins") && !chave.equals("de")) {
                return candidato;
            }
        }
        return "";
    }

    private String removerPrefixosContexto(String objeto, IdiomaInterface idioma) {
        String o = objeto.trim();
        if (idioma == IdiomaInterface.INGLES) {
            return o.replaceFirst("^(the|a|an)\\s+", "");
        }
        if (idioma == IdiomaInterface.FRANCES) {
            return o.replaceFirst("^(les|des|le|la|un|une)\\s+", "");
        }
        return o.replaceFirst("^(as|os|uma|um)\\s+", "");
    }

    private boolean ehPalavraInicialGenerica(String palavra) {
        String n = normalizar(palavra);
        return n.equals("quantas") || n.equals("quantos") || n.equals("quanto") || n.equals("com")
                || n.equals("como") || n.equals("qual") || n.equals("quando") || n.equals("uma")
                || n.equals("um") || n.equals("na") || n.equals("no") || n.equals("em")
                || n.equals("depois") || n.equals("ao") || n.equals("dans") || n.equals("combien")
                || n.equals("how") || n.equals("what") || n.equals("when") || n.equals("in")
                || n.equals("on") || n.equals("at") || n.equals("the");
    }

    private String papelCorretoAproximado(TipoSituacaoAditiva tipo, int indice, int total) {
        if (tipo == TipoSituacaoAditiva.COMPOSICAO_MEDIDAS) {
            return indice == total - 1 ? "todo" : "parte";
        }
        if (tipo == TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS) {
            if (indice == 0) return "estado_inicial";
            if (indice == total - 1) return "estado_final";
            return "transformacao";
        }
        if (tipo == TipoSituacaoAditiva.COMPARACAO_MEDIDAS) {
            if (indice == total - 1) return "referendo";
            return indice == 0 ? "referido" : "valor_relativo";
        }
        return "";
    }

    private String aplicarSinal(String valor, String sinal) {
        String v = limparValor(valor);
        if (v.length() == 0 || SimboloDesconhecido.eh(v) || v.startsWith("+") || v.startsWith("-")) {
            return v;
        }
        String s = normalizar(sinal);
        if (s.equals("negativo") || s.equals("negative") || s.equals("negatif") || s.equals("-")) {
            return "-" + v;
        }
        if (s.equals("positivo") || s.equals("positive") || s.equals("positif") || s.equals("+")) {
            return "+" + v;
        }
        return v;
    }

    private String limparValor(String valor) {
        return valor == null ? "" : valor.trim();
    }

    private String semSinal(String valor) {
        if (valor == null) return "";
        return valor.replaceFirst("^[+-]", "");
    }

    private boolean ehNegativo(String valor) {
        return valor != null && valor.trim().startsWith("-");
    }

    private boolean vazio(String valor) {
        return valor == null || valor.trim().length() == 0;
    }

    private String normalizar(String texto) {
        if (texto == null) return "";
        String n = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
        return n.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9?]+", " ").trim().replaceAll("\\s+", " ");
    }

    private Locale locale(IdiomaInterface idioma) {
        if (idioma == IdiomaInterface.INGLES) return Locale.ENGLISH;
        if (idioma == IdiomaInterface.FRANCES) return Locale.FRENCH;
        return new Locale("pt", "BR");
    }

    private String quantificadorPt(String objeto) {
        String n = normalizar(objeto);
        int espaco = n.lastIndexOf(' ');
        String palavra = espaco >= 0 ? n.substring(espaco + 1) : n;
        boolean feminino = palavra.endsWith("a") || palavra.endsWith("as")
                || palavra.equals("flores") || palavra.equals("aves")
                || palavra.equals("mulheres") || palavra.equals("pessoas")
                || palavra.equals("unidades") || palavra.equals("quantidades");
        return feminino ? "Quantas" : "Quantos";
    }

    private String nomePadrao1(IdiomaInterface idioma) {
        return idioma == IdiomaInterface.PORTUGUES ? "Paulo" : "Paul";
    }

    private String nomePadrao2(IdiomaInterface idioma) {
        return idioma == IdiomaInterface.FRANCES ? "Marie" : (idioma == IdiomaInterface.INGLES ? "Mary" : "José");
    }

    private String objetoPadrao(IdiomaInterface idioma) {
        if (idioma == IdiomaInterface.INGLES) return "objects";
        if (idioma == IdiomaInterface.FRANCES) return "objets";
        return "objetos";
    }

    private static final class Entidades {
        final String nome1;
        final String nome2;
        final String objeto;

        Entidades(String nome1, String nome2, String objeto) {
            this.nome1 = nome1;
            this.nome2 = nome2;
            this.objeto = objeto;
        }
    }
}
