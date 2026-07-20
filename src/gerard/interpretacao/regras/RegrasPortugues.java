package gerard.interpretacao.regras;

import gerard.interpretacao.modelo.CategoriaProblema;
import gerard.interpretacao.modelo.IdiomaProblema;

public class RegrasPortugues extends RegrasBase {
    public RegrasPortugues() { super(IdiomaProblema.PORTUGUES); }

    protected void configurar() {
        indicador("quantos");
        indicador("quantas");
        indicador("tem");
        indicador("tinha");
        indicador("ficou com");
        indicador("ao todo");

        regra("ao todo", CategoriaProblema.COMPOSICAO_MEDIDAS, 5);
        regra("no total", CategoriaProblema.COMPOSICAO_MEDIDAS, 5);
        regra("juntos", CategoriaProblema.COMPOSICAO_MEDIDAS, 4);
        regra("juntas", CategoriaProblema.COMPOSICAO_MEDIDAS, 4);
        regra("colecao", CategoriaProblema.COMPOSICAO_MEDIDAS, 2);

        regra("a mais que", CategoriaProblema.COMPARACAO_MEDIDAS, 5);
        regra("a menos que", CategoriaProblema.COMPARACAO_MEDIDAS, 5);
        regra("a mais", CategoriaProblema.COMPARACAO_MEDIDAS, 3);
        regra("a menos", CategoriaProblema.COMPARACAO_MEDIDAS, 3);
        regra("mais velho que", CategoriaProblema.COMPARACAO_MEDIDAS, 5);
        regra("mais velha que", CategoriaProblema.COMPARACAO_MEDIDAS, 5);
        regra("menos que", CategoriaProblema.COMPARACAO_MEDIDAS, 4);
        regra("maior que", CategoriaProblema.COMPARACAO_MEDIDAS, 4);
        regra("menor que", CategoriaProblema.COMPARACAO_MEDIDAS, 4);

        regra("tinha", CategoriaProblema.TRANSFORMACAO_MEDIDAS, 3);
        regra("ganhou", CategoriaProblema.TRANSFORMACAO_MEDIDAS, 5);
        regra("perdeu", CategoriaProblema.TRANSFORMACAO_MEDIDAS, 5);
        regra("comprou", CategoriaProblema.TRANSFORMACAO_MEDIDAS, 4);
        regra("gastou", CategoriaProblema.TRANSFORMACAO_MEDIDAS, 4);
        regra("recebeu", CategoriaProblema.TRANSFORMACAO_MEDIDAS, 4);
        regra("deu", CategoriaProblema.TRANSFORMACAO_MEDIDAS, 3);
        regra("tomou", CategoriaProblema.TRANSFORMACAO_MEDIDAS, 3);
        regra("ficou com", CategoriaProblema.TRANSFORMACAO_MEDIDAS, 5);
        regra("restaram", CategoriaProblema.TRANSFORMACAO_MEDIDAS, 5);
        regra("restou", CategoriaProblema.TRANSFORMACAO_MEDIDAS, 5);

        regra("ontem", CategoriaProblema.TRANSFORMACAO_COMPOSTA_DOIS_PASSOS, 4);
        regra("hoje", CategoriaProblema.TRANSFORMACAO_COMPOSTA_DOIS_PASSOS, 4);
        regra("em seguida", CategoriaProblema.TRANSFORMACAO_COMPOSTA_DOIS_PASSOS, 4);
        regra("depois", CategoriaProblema.TRANSFORMACAO_COMPOSTA_DOIS_PASSOS, 3);
        regra("na primeira etapa", CategoriaProblema.TRANSFORMACAO_COMPOSTA_DOIS_PASSOS, 4);
        regra("na segunda etapa", CategoriaProblema.TRANSFORMACAO_COMPOSTA_DOIS_PASSOS, 4);
        regra("primeiro", CategoriaProblema.TRANSFORMACAO_COMPOSTA_DOIS_PASSOS, 3);
        regra("segundo", CategoriaProblema.TRANSFORMACAO_COMPOSTA_DOIS_PASSOS, 3);

        regra("transformacao total", CategoriaProblema.COMPOSICAO_TRANSFORMACOES, 6);
        regra("na primeira rodada", CategoriaProblema.COMPOSICAO_TRANSFORMACOES, 4);
        regra("na segunda rodada", CategoriaProblema.COMPOSICAO_TRANSFORMACOES, 4);
        regra("total em sua pontuacao", CategoriaProblema.COMPOSICAO_TRANSFORMACOES, 4);

        regra("passou a ter", CategoriaProblema.TRANSFORMACAO_RELACAO, 6);
        regra("agora", CategoriaProblema.TRANSFORMACAO_RELACAO, 2);
        regra("depois", CategoriaProblema.TRANSFORMACAO_RELACAO, 3);
        regra("a mais que", CategoriaProblema.TRANSFORMACAO_RELACAO, 2);

        regra("qual e a relacao entre", CategoriaProblema.COMPOSICAO_RELACOES, 6);
        regra("relacao entre", CategoriaProblema.COMPOSICAO_RELACOES, 5);
        regra("a de", CategoriaProblema.COMPOSICAO_RELACOES, 2);

        regra("cada", CategoriaProblema.MULTIPLICACAO, 4);
        regra("vezes", CategoriaProblema.MULTIPLICACAO, 4);
        regra("por", CategoriaProblema.MULTIPLICACAO, 2);

        regra("em partes iguais", CategoriaProblema.DIVISAO_PARTES, 5);
        regra("igualmente", CategoriaProblema.DIVISAO_PARTES, 4);
        regra("quantos em cada", CategoriaProblema.DIVISAO_PARTES, 5);

        regra("quantos grupos", CategoriaProblema.DIVISAO_COTAS, 5);
        regra("quantas equipes", CategoriaProblema.DIVISAO_COTAS, 5);
        regra("quantos podem ser feitos", CategoriaProblema.DIVISAO_COTAS, 5);
    }
}
