package gerard.dominio.campoaditivo;

/**
 * A conclusão observada por qualquer representação (desktop, web) encerra a tentativa uma única
 * vez; a tentativa, e só ela, decide se admite registro factual e exploração.
 */
public final class TesteEncerrarSeConcluidaPelaTentativa {
    public static void main(String[] args) {
        final boolean[] azul = {false};
        FonteDeConclusao fonte = () -> azul[0];
        TentativaModelagemAditiva t = new TentativaModelagemAditiva("t");
        exigir(!t.encerrarSeConcluida(fonte), "sem conclusão observada nada encerra");
        exigir(t.getFase() == FaseDaTentativa.MODELAGEM && t.admiteRegistroFactual() && !t.admiteExploracao(),
                "antes do azul: fase de modelagem, registra, não explora");
        azul[0] = true;
        exigir(t.encerrarSeConcluida(fonte), "a primeira conclusão encerra");
        exigir(t.getFase() == FaseDaTentativa.EXPLORATORIA && !t.admiteRegistroFactual() && t.admiteExploracao(),
                "depois do azul: fase exploratória, explora, não registra");
        exigir(t.getFase() != new TentativaModelagemAditiva("nova").getFase(),
                "reiniciar/sortear cria nova tentativa, de volta à modelagem");
        exigir(!t.encerrarSeConcluida(fonte), "observar a conclusão de novo não encerra outra vez");
        TentativaCorrente corrente = new TentativaCorrente();
        exigir(corrente.admiteRegistroFactual() && !corrente.admiteExploracao(), "sem tentativa: padrão seguro");
        corrente.definir(t);
        exigir(!corrente.admiteRegistroFactual() && corrente.admiteExploracao(), "a porta reflete a tentativa");
        System.out.println("APROVADO: encerrarSeConcluida é o ponto único e idempotente da conclusão.");
    }

    private static void exigir(boolean ok, String msg) { if (!ok) throw new AssertionError(msg); }
}
