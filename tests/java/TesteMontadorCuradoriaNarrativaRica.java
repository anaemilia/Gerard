import gerard.campoaditivo.curadoria.ConversorSituacaoProblemaRica;
import gerard.campoaditivo.curadoria.MontadorCuradoriaNarrativaRica;
import gerard.campoaditivo.curadoria.RascunhoCuradoriaNarrativaRica;
import gerard.campoaditivo.curadoria.RegistroCuradoriaNarrativaRica;
import gerard.campoaditivo.curadoria.ResultadoConversaoSituacaoProblemaRica;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.campoaditivo.modelo.TipoSituacaoAditiva;
import gerard.dominio.campoaditivo.situacao.EventoNarrativoCurado;
import gerard.dominio.campoaditivo.situacao.ReferenciaValorNarrativo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Prova do montador sem Swing usado pelo editor da narrativa rica. */
public class TesteMontadorCuradoriaNarrativaRica {

    public static void main(String[] args) {
        MontadorCuradoriaNarrativaRica montador =
                new MontadorCuradoriaNarrativaRica();
        RascunhoCuradoriaNarrativaRica rascunho = rascunhoValido();
        RegistroCuradoriaNarrativaRica registro = montador.montar(rascunho);

        checar("montador preserva o id declarado",
                "situacao.nadia".equals(registro.getIdSituacao()), true);
        checar("montador cria o evento nominal de redução",
                registro.getNarrativa().getEventos().size() == 1
                && registro.getNarrativa().getEventos().get(0).getTipo()
                        == EventoNarrativoCurado.Tipo.REDUCAO,
                true);
        checar("montador preserva as três correspondências",
                registro.getCorrespondencias().size() == 3, true);
        checar("narrativa declarada é consistente",
                registro.getNarrativa().validar().ehValida(), true);

        ResultadoConversaoSituacaoProblemaRica conversao =
                new ConversorSituacaoProblemaRica().converter(
                        situacaoTabularComPersonagensIncorretos(),
                        registro.getNarrativa(),
                        registro.getCorrespondencias());
        checar("conversão usa o rascunho nominal, não personagem_*",
                conversao.ehValida()
                && "Nadia".equals(conversao.getSituacaoOuFalhar()
                        .getNarrativa().getEstadoInicial().getInventarios()
                        .keySet().iterator().next().getNomeExibicao()),
                true);

        RascunhoCuradoriaNarrativaRica reaberto = montador.decompor(registro);
        RegistroCuradoriaNarrativaRica remontado = montador.montar(reaberto);
        checar("abrir e remontar preserva a narrativa",
                remontado.getNarrativa().validar().ehValida()
                && remontado.getCorrespondencias().size() == 3,
                true);
        checar("características explícitas sobrevivem ao formulário",
                "cor=vermelho;tamanho=pequeno".equals(
                        reaberto.getObjetos().get(0).getCaracteristicas()),
                true);

        checar("id nominal desconhecido é rejeitado",
                falhaAoMontar(rascunhoComParticipanteDesconhecido()), true);
        checar("id duplicado não é resolvido por posição",
                falhaAoMontar(rascunhoComParticipanteDuplicado()), true);

        System.out.println(
                "APROVADO: montador nominal da curadoria narrativa rica.");
    }

    private static RascunhoCuradoriaNarrativaRica rascunhoValido() {
        List<RascunhoCuradoriaNarrativaRica.Participante> participantes =
                Collections.singletonList(
                        new RascunhoCuradoriaNarrativaRica.Participante(
                                "participante.nadia", "Nadia"));
        List<RascunhoCuradoriaNarrativaRica.Familia> familias =
                Collections.singletonList(
                        new RascunhoCuradoriaNarrativaRica.Familia(
                                "familia.morangos", "Morangos"));
        List<RascunhoCuradoriaNarrativaRica.Objeto> objetos =
                Collections.singletonList(
                        new RascunhoCuradoriaNarrativaRica.Objeto(
                                "objeto.morango", "familia.morangos",
                                "visual.morango",
                                "cor=vermelho;tamanho=pequeno"));
        List<RascunhoCuradoriaNarrativaRica.ItemEstado> inicial =
                Collections.singletonList(
                        new RascunhoCuradoriaNarrativaRica.ItemEstado(
                                "participante.nadia", "objeto.morango", "10"));
        List<RascunhoCuradoriaNarrativaRica.Evento> eventos =
                Collections.singletonList(
                        new RascunhoCuradoriaNarrativaRica.Evento(
                                EventoNarrativoCurado.Tipo.REDUCAO.name(),
                                "1", "evento.consumo",
                                "participante.nadia", "",
                                "objeto.morango", "3"));
        List<RascunhoCuradoriaNarrativaRica.ItemEstado> finalDeclarado =
                Collections.singletonList(
                        new RascunhoCuradoriaNarrativaRica.ItemEstado(
                                "participante.nadia", "objeto.morango", "7"));
        List<RascunhoCuradoriaNarrativaRica.Correspondencia> correspondencias =
                Arrays.asList(
                        new RascunhoCuradoriaNarrativaRica.Correspondencia(
                                "papel.estadoInicial",
                                ReferenciaValorNarrativo.Tipo
                                        .QUANTIDADE_INICIAL.name(),
                                "participante.nadia", "",
                                "familia.morangos", "", ""),
                        new RascunhoCuradoriaNarrativaRica.Correspondencia(
                                "papel.transformacao",
                                ReferenciaValorNarrativo.Tipo
                                        .VARIACAO_DE_EVENTO.name(),
                                "participante.nadia", "",
                                "familia.morangos", "evento.consumo", ""),
                        new RascunhoCuradoriaNarrativaRica.Correspondencia(
                                "papel.estadoFinal",
                                ReferenciaValorNarrativo.Tipo
                                        .QUANTIDADE_FINAL_CALCULADA.name(),
                                "participante.nadia", "",
                                "familia.morangos", "", ""));
        return new RascunhoCuradoriaNarrativaRica(
                "situacao.nadia", "Nadia consumiu três morangos.",
                "0", "tempo.inicial", "2", "tempo.final",
                participantes, familias, objetos,
                inicial, eventos, finalDeclarado, correspondencias);
    }

    private static RascunhoCuradoriaNarrativaRica
            rascunhoComParticipanteDesconhecido() {
        RascunhoCuradoriaNarrativaRica base = rascunhoValido();
        List<RascunhoCuradoriaNarrativaRica.ItemEstado> inicial =
                Collections.singletonList(
                        new RascunhoCuradoriaNarrativaRica.ItemEstado(
                                "participante.inexistente",
                                "objeto.morango", "10"));
        return copiarCom(base, base.getParticipantes(), inicial);
    }

    private static RascunhoCuradoriaNarrativaRica
            rascunhoComParticipanteDuplicado() {
        RascunhoCuradoriaNarrativaRica base = rascunhoValido();
        List<RascunhoCuradoriaNarrativaRica.Participante> participantes =
                new ArrayList<RascunhoCuradoriaNarrativaRica.Participante>(
                        base.getParticipantes());
        participantes.add(new RascunhoCuradoriaNarrativaRica.Participante(
                "participante.nadia", "Outra Nadia"));
        return copiarCom(base, participantes, base.getEstadoInicial());
    }

    private static RascunhoCuradoriaNarrativaRica copiarCom(
            RascunhoCuradoriaNarrativaRica base,
            List<RascunhoCuradoriaNarrativaRica.Participante> participantes,
            List<RascunhoCuradoriaNarrativaRica.ItemEstado> inicial) {
        return new RascunhoCuradoriaNarrativaRica(
                base.getIdSituacao(), base.getContexto(),
                base.getOrdemEstadoInicial(), base.getChaveEstadoInicial(),
                base.getOrdemEstadoFinal(), base.getChaveEstadoFinal(),
                participantes, base.getFamilias(), base.getObjetos(),
                inicial, base.getEventos(), base.getEstadoFinal(),
                base.getCorrespondencias());
    }

    private static boolean falhaAoMontar(
            RascunhoCuradoriaNarrativaRica rascunho) {
        try {
            new MontadorCuradoriaNarrativaRica().montar(rascunho);
            return false;
        } catch (IllegalArgumentException esperada) {
            return true;
        }
    }

    private static SituacaoProblemaAditiva
            situacaoTabularComPersonagensIncorretos() {
        return new SituacaoProblemaAditiva(
                "situacao.nadia", "grupo.nadia", "original", "", true,
                TipoSituacaoAditiva.TRANSFORMACAO_MEDIDAS, "pt-BR",
                "Nadia tinha dez morangos e consumiu três.",
                "Morangos", "curadoria", "",
                "10", "3", "negativo", "7",
                "", "", "", "", "", "", "",
                "estado_final", "TRANSFORMACAO_MEDIDAS", "",
                "POSICAO_1_INCORRETA", "POSICAO_2_INCORRETA", "",
                "", "", "", "", "", "", "", "", "");
    }

    private static void checar(String rotulo, boolean obtido, boolean esperado) {
        if (obtido != esperado) {
            throw new AssertionError(
                    rotulo + ": esperado [" + esperado
                            + "], obtido [" + obtido + "]");
        }
        System.out.println("OK - " + rotulo);
    }
}
