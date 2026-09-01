package gerard.pesquisador.replay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Preservação (não uso imediato) das transcrições reais do campo
 * multiplicativo (Multiplicação, Divisão por partes, Divisão por cotas),
 * vindas da pasta "BIA-felipe/Felipe" do doutorado — participantes "S01"
 * (arquivos 02-01-11 e 02-02-11, que são a MESMA sessão duplicada — mesmo
 * conteúdo, mesmas 7 situações-problema, não duas sessões diferentes) e
 * "S04" (04-02-11, 7 situações-problema; havia uma 8ª no vídeo original,
 * mas a própria pesquisadora registrou que a gravação não deu suporte a
 * transcrevê-la).
 *
 * ESTE ARQUIVO NÃO É USADO POR TesteReplayProtocolosReais nem por nenhum
 * agente hoje. Diferente de protocolos_reais_replay.tsv (campo aditivo),
 * não há como transformar isto em pares (chavePapelNumeral, chavePapelAlvo)
 * reais: o Gérard atual não implementa o campo multiplicativo — não existe
 * TipoSituacaoAditiva, ElementoVergnaud nem catálogo de papel semântico para
 * multiplicando/multiplicador/cota/quociente em nenhum lugar do código (ver
 * gerard-ajuda-adaptativa e gerard-scaffolding-interacao, que apontam essa
 * mesma lacuna a partir de outro documento histórico). Este arquivo existe
 * só para não perder o dado até o dia em que essa infraestrutura for
 * decidida e construída.
 *
 * O protótipo usado nessas sessões representa cada situação como uma tabela
 * de proporção de 4 quadrantes (superior esquerdo/direito, inferior
 * esquerdo/direito) — a "tabela de quatro termos" de Vergnaud para
 * isomorfismo de medidas, estruturalmente diferente dos diagramas ternários
 * do campo aditivo (parte1/parte2/todo etc.). Os nomes de papel abaixo
 * ("quadrante_superior_esquerdo" etc.) são só posição bruta, não uma
 * proposta de nomenclatura semântica — quem construir a infraestrutura
 * multiplicativa decide os papéis de verdade (ex.: valor-unitário,
 * quantidade-de-unidades, valor-total).
 *
 * Cada ProblemaMultiplicativo preserva o enunciado, a categoria finalmente
 * escolhida (depois de qualquer troca — a escolha de legenda em si, com
 * seus erros de tentativa, foi resumida em texto, não step-a-step, já que
 * não há schema para validar contra) e o posicionamento final aceito dos
 * valores nos quadrantes, em ordem. Ambiguidades da transcrição original
 * (ex.: um valor repetido em dois quadrantes diferentes) foram preservadas
 * como estão, sinalizadas — não resolvidas por mim, por não ter como
 * conferir contra o vídeo original.
 */
public final class CampoMultiplicativoDadosFuturos {

    public static final class ProblemaMultiplicativo {
        public final String participante;
        public final String sessao;
        public final String enunciado;
        public final String categoriaEscolhidaFinal;
        public final List<String> posicionamentoFinal;
        public final String observacao;

        ProblemaMultiplicativo(String participante, String sessao, String enunciado,
                String categoriaEscolhidaFinal, List<String> posicionamentoFinal, String observacao) {
            this.participante = participante;
            this.sessao = sessao;
            this.enunciado = enunciado;
            this.categoriaEscolhidaFinal = categoriaEscolhidaFinal;
            this.posicionamentoFinal = posicionamentoFinal;
            this.observacao = observacao;
        }
    }

    private CampoMultiplicativoDadosFuturos() {
    }

    public static List<ProblemaMultiplicativo> obterTodos() {
        List<ProblemaMultiplicativo> lista = new ArrayList<ProblemaMultiplicativo>();
        lista.addAll(sessaoS01());
        lista.addAll(sessaoS04());
        return lista;
    }

    // ---- S01 (02-01-11 == 02-02-11, arquivos duplicados; usei só uma vez) ----

    private static List<ProblemaMultiplicativo> sessaoS01() {
        String p = "S01";
        String sessao = "02-01-11 (duplicado em 02-02-11)";
        List<ProblemaMultiplicativo> lista = new ArrayList<ProblemaMultiplicativo>();

        lista.add(new ProblemaMultiplicativo(p, sessao,
                "Para fazer sete bolos são necessários trinta e cinco ovos, se todos tem a mesma receita "
                        + "quantos ovos são necessários para fazer somente um bolo?",
                "Divisão por partes (depois de alternar 3x entre partes/cotas)",
                Arrays.asList(
                        "1 -> quadrante_superior_esquerdo",
                        "5 -> quadrante_superior_direito (computado, 35/7)",
                        "7 -> quadrante_inferior_esquerdo",
                        "35 -> quadrante_inferior_direito"),
                "Tentativa inicial (7->sup.esq., 35->sup.dir., 1->inf.esq.) foi rejeitada pela interface "
                        + "antes deste posicionamento final."));

        lista.add(new ProblemaMultiplicativo(p, sessao,
                "Tenho doze reais e quero comprar uma quantidade de pacotes de caramelo, que custa quatro "
                        + "reais cada pacote. Quantos pacotes posso comprar com essa quantia?",
                "Divisão por cotas",
                Arrays.asList(
                        "12 -> quadrante_superior_direito (rejeitado pela interface)",
                        "12 -> quadrante_inferior_direito (reposicionado)",
                        "4 -> quadrante_superior_direito",
                        "1 -> quadrante_superior_esquerdo",
                        "4 -> quadrante_inferior_esquerdo"),
                "A transcrição original usa \"quatro\" tanto no quadrante superior direito quanto no "
                        + "inferior esquerdo (o resultado computado seria 3 = 12/4) — preservado como está, "
                        + "possível erro de transcrição a conferir contra o vídeo."));

        lista.add(new ProblemaMultiplicativo(p, sessao,
                "Uma garrafa de vinho custa cinco reais quanto custam quatro garrafas?",
                "Multiplicação",
                Arrays.asList(
                        "1 -> quadrante_superior_esquerdo",
                        "5 -> quadrante_superior_direito",
                        "4 -> quadrante_inferior_esquerdo",
                        "? -> quadrante_inferior_direito, substituído por 20 (computado)"),
                null));

        lista.add(new ProblemaMultiplicativo(p, sessao,
                "Quantos bolos podem ser feitos com trinta e cinco ovos se cada bolo leva cinco ovos?",
                "Divisão por partes (depois de errar a categoria)",
                Arrays.asList(
                        "1 -> quadrante_superior_esquerdo",
                        "35 -> quadrante_superior_direito (posicionamento errado, rejeitado)",
                        "5 -> quadrante_superior_esquerdo (reposicionado)",
                        "35 -> quadrante_inferior_esquerdo",
                        "7 -> quadrante_inferior_esquerdo (computado, 35/5)"),
                "Dois valores diferentes (35 e 7) aparecem como indo para o mesmo quadrante inferior "
                        + "esquerdo na transcrição original — preservado como está."));

        lista.add(new ProblemaMultiplicativo(p, sessao,
                "Paguei doze reais por quatro garrafas de vinho. Qual o preço de cada garrafa?",
                "Divisão por partes",
                Arrays.asList(
                        "4 -> quadrante_superior_esquerdo",
                        "12 -> quadrante_superior_direito (posicionamento errado, rejeitado)",
                        "4 -> quadrante_inferior_esquerdo (reposicionado)",
                        "12 -> quadrante_inferior_direito",
                        "1 -> quadrante_superior_esquerdo",
                        "3 -> quadrante_superior_direito (computado, 12/4)"),
                null));

        lista.add(new ProblemaMultiplicativo(p, sessao,
                "Um bolo leva cinco ovos, quantos ovos são necessários para fazer sete bolos?",
                "Multiplicação",
                Arrays.asList(
                        "1 -> quadrante_superior_esquerdo",
                        "5 -> quadrante_superior_direito",
                        "7 -> quadrante_inferior_esquerdo",
                        "35 -> quadrante_inferior_direito (computado, 5*7)"),
                null));

        lista.add(new ProblemaMultiplicativo(p, sessao,
                "Quantos bolos podem ser feitos com trinta e cinco ovos se cada bolo leva cinco ovos?"
                        + " (repetição do problema 4, formulação ligeiramente diferente)",
                "Divisão por cotas",
                Arrays.asList(
                        "1 -> quadrante_superior_esquerdo",
                        "5 -> quadrante_superior_direito",
                        "35 -> quadrante_inferior_direito",
                        "7 -> quadrante_inferior_esquerdo (computado, 35/5)"),
                "Transcrição incompleta — não registra o clique final de confirmação/avanço para a "
                        + "próxima tarefa."));

        return lista;
    }

    // ---- S04 (04-02-11) ----

    private static List<ProblemaMultiplicativo> sessaoS04() {
        String p = "S04";
        String sessao = "04-02-11";
        List<ProblemaMultiplicativo> lista = new ArrayList<ProblemaMultiplicativo>();

        lista.add(new ProblemaMultiplicativo(p, sessao,
                "Um bolo leva cinco ovos. Quantos ovos são necessários para fazer sete bolos?",
                "Multiplicação (depois de errar partes e cotas)",
                Arrays.asList(
                        "1 -> quadrante_superior_esquerdo",
                        "5 -> quadrante_superior_direito",
                        "7 -> quadrante_inferior_esquerdo (rotulado 'inferior direita' no texto, mas o "
                                + "resultado final foi colocado no quadrante inferior direito, então o 7 "
                                + "provavelmente ficou no inferior esquerdo)",
                        "35 -> quadrante_inferior_direito (computado, 5*7)"),
                null));

        lista.add(new ProblemaMultiplicativo(p, sessao,
                "Para fazer sete bolos são necessários trinta e cinco ovos, se todos os bolos tem a mesma "
                        + "receita quantos ovos são necessários para fazer um bolo?",
                "Divisão por partes",
                Arrays.asList(
                        "7 -> quadrante_superior_esquerdo",
                        "35 -> quadrante_superior_direito",
                        "1 -> quadrante_inferior_esquerdo",
                        "5 -> quadrante_inferior_direito (computado, 35/7)"),
                null));

        lista.add(new ProblemaMultiplicativo(p, sessao,
                "Uma garrafa de vinho custa cinco reais quanto custam quatro garrafas?",
                "Multiplicação",
                Arrays.asList(
                        "1 -> quadrante_superior_esquerdo",
                        "5 -> quadrante_superior_direito",
                        "4 -> quadrante_inferior_esquerdo",
                        "20 -> quadrante_inferior_direito (computado, 5*4)"),
                null));

        lista.add(new ProblemaMultiplicativo(p, sessao,
                "Paguei doze reais por quatro garrafas de vinho. Qual o preço de uma garrafa?",
                "Divisão por partes (depois de errar cotas)",
                Arrays.asList(
                        "12 -> quadrante_superior_direito (posicionamento errado, rejeitado)",
                        "12 -> quadrante_inferior_direito (reposicionado)",
                        "4 -> quadrante_inferior_esquerdo",
                        "1 -> quadrante_superior_esquerdo",
                        "3 -> quadrante_superior_direito (computado, 12/4)"),
                null));

        lista.add(new ProblemaMultiplicativo(p, sessao,
                "Tenho doze reais e quero comprar alguns pacotes de caramelo que custam quatro reais cada. "
                        + "Quantos pacotes posso comprar com essa quantia?",
                "Divisão por cotas (depois de errar multiplicação)",
                Arrays.asList(
                        "12 -> quadrante_inferior_direito",
                        "4 -> quadrante_superior_direito",
                        "1 -> quadrante_superior_esquerdo",
                        "3 -> quadrante_inferior_esquerdo (computado, 12/4)"),
                null));

        lista.add(new ProblemaMultiplicativo(p, sessao,
                "Quantos bolos podem ser feitos com trinta e cinco ovos se cada bolo leva cinco ovos?",
                "Divisão por cotas (depois de errar partes)",
                Arrays.asList(
                        "35 -> quadrante_inferior_direito",
                        "1 -> quadrante_superior_esquerdo",
                        "5 -> quadrante_superior_direito",
                        "7 -> quadrante_inferior_esquerdo (computado, 35/5)"),
                null));

        lista.add(new ProblemaMultiplicativo(p, sessao,
                "Para fazer sete bolos são necessários trinta e cinco ovos, se todos os bolos tem a mesma "
                        + "receita quantos ovos são necessários para fazer um bolo? (repetição do problema 2)",
                "Divisão por partes, depois cotas (categoria trocada durante a explicação)",
                Arrays.asList(
                        "7 -> quadrante_superior_esquerdo",
                        "35 -> quadrante_superior_direito",
                        "1 -> quadrante_inferior_esquerdo",
                        "5 -> quadrante_inferior_direito (computado, 35/7)"),
                "Havia um 8º problema no vídeo original que a própria pesquisadora não transcreveu "
                        + "(gravação não deu suporte) — não está preservado em lugar nenhum."));

        return lista;
    }
}
