package gerard.idioma;

import java.util.EnumMap;
import java.util.Map;

/**
 * Repositório simples de situações-problema traduzidas.
 * Em uma versão maior, esta classe pode ser substituída por leitura de arquivo,
 * banco de dados ou serviço externo sem alterar a tela principal.
 */
public class RepositorioSituacoesProblema {
    private final Map<IdiomaInterface, SituacaoProblemaTraduzida> situacoes;

    public RepositorioSituacoesProblema() {
        this.situacoes = new EnumMap<IdiomaInterface, SituacaoProblemaTraduzida>(IdiomaInterface.class);
        carregarSituacaoPrincipal();
    }

    private void carregarSituacaoPrincipal() {
        situacoes.put(
                IdiomaInterface.PORTUGUES,
                new SituacaoProblemaTraduzida(
                        IdiomaInterface.PORTUGUES,
                        "Ricardo saiu para jogar bola de gude. Ao sair de casa ele possuía 2 bolas. Ao voltar, ele tinha 6 bolas. O que aconteceu no jogo?"
                )
        );

        situacoes.put(
                IdiomaInterface.INGLES,
                new SituacaoProblemaTraduzida(
                        IdiomaInterface.INGLES,
                        "Ricardo went out to play marbles. When he left home, he had 2 marbles. When he returned, he had 6 marbles. What happened in the game?"
                )
        );

        situacoes.put(
                IdiomaInterface.FRANCES,
                new SituacaoProblemaTraduzida(
                        IdiomaInterface.FRANCES,
                        "Ricardo est sorti jouer aux billes. En quittant la maison, il avait 2 billes. En revenant, il avait 6 billes. Que s'est-il passé dans le jeu?"
                )
        );
    }

    public SituacaoProblemaTraduzida obterSituacaoPrincipal(IdiomaInterface idioma) {
        SituacaoProblemaTraduzida situacao = situacoes.get(idioma);

        if (situacao == null) {
            return situacoes.get(IdiomaInterface.PORTUGUES);
        }

        return situacao;
    }
}
