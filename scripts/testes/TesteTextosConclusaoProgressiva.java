package gerard.ui.conclusao;

import gerard.i18n.ServicoLocalizacao;
import gerard.idioma.IdiomaInterface;

public final class TesteTextosConclusaoProgressiva {
    private static int verificacoes;

    public static void main(String[] args) {
        ServicoLocalizacao loc = ServicoLocalizacao.getInstancia();
        verificar(loc, IdiomaInterface.PORTUGUES,
                "Modelagem concluída", "Podemos passar para a próxima tarefa?");
        verificar(loc, IdiomaInterface.INGLES,
                "Modeling completed", "Can we move on to the next task?");
        verificar(loc, IdiomaInterface.FRANCES,
                "Modélisation terminée", "Pouvons-nous passer à la tâche suivante ?");
        System.out.println("Textos da conclusão progressiva aprovados: "
                + verificacoes + " verificações.");
    }

    private static void verificar(ServicoLocalizacao loc,
            IdiomaInterface idioma, String selo, String pergunta) {
        loc.definirIdioma(idioma);
        confirmar(selo.equals(loc.texto("ui.completion.completed")),
                "selo incorreto em " + idioma);
        confirmar(pergunta.equals(loc.texto("ui.completion.congratulations")),
                "pergunta incorreta em " + idioma);
    }

    private static void confirmar(boolean condicao, String mensagem) {
        verificacoes++;
        if (!condicao) throw new AssertionError(mensagem);
    }
}
