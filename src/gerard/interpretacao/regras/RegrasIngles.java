package gerard.interpretacao.regras;

import gerard.interpretacao.modelo.CategoriaProblema;
import gerard.interpretacao.modelo.IdiomaProblema;

public class RegrasIngles extends RegrasBase {
    public RegrasIngles() { super(IdiomaProblema.INGLES); }

    protected void configurar() {
        indicador("how many");
        indicador("how much");
        indicador("has");
        indicador("had");
        indicador("left with");
        indicador("in all");

        regra("in all", CategoriaProblema.COMPOSICAO_MEDIDAS, 5);
        regra("altogether", CategoriaProblema.COMPOSICAO_MEDIDAS, 5);
        regra("total", CategoriaProblema.COMPOSICAO_MEDIDAS, 4);
        regra("together", CategoriaProblema.COMPOSICAO_MEDIDAS, 4);

        regra("more than", CategoriaProblema.COMPARACAO_MEDIDAS, 5);
        regra("less than", CategoriaProblema.COMPARACAO_MEDIDAS, 5);
        regra("fewer than", CategoriaProblema.COMPARACAO_MEDIDAS, 5);
        regra("older than", CategoriaProblema.COMPARACAO_MEDIDAS, 5);
        regra("younger than", CategoriaProblema.COMPARACAO_MEDIDAS, 5);
        regra("greater than", CategoriaProblema.COMPARACAO_MEDIDAS, 4);
        regra("smaller than", CategoriaProblema.COMPARACAO_MEDIDAS, 4);

        regra("had", CategoriaProblema.TRANSFORMACAO_MEDIDAS, 3);
        regra("won", CategoriaProblema.TRANSFORMACAO_MEDIDAS, 5);
        regra("lost", CategoriaProblema.TRANSFORMACAO_MEDIDAS, 5);
        regra("bought", CategoriaProblema.TRANSFORMACAO_MEDIDAS, 4);
        regra("spent", CategoriaProblema.TRANSFORMACAO_MEDIDAS, 4);
        regra("received", CategoriaProblema.TRANSFORMACAO_MEDIDAS, 4);
        regra("gave", CategoriaProblema.TRANSFORMACAO_MEDIDAS, 4);
        regra("left with", CategoriaProblema.TRANSFORMACAO_MEDIDAS, 5);
        regra("remained", CategoriaProblema.TRANSFORMACAO_MEDIDAS, 4);

        regra("yesterday", CategoriaProblema.TRANSFORMACAO_COMPOSTA_DOIS_PASSOS, 4);
        regra("today", CategoriaProblema.TRANSFORMACAO_COMPOSTA_DOIS_PASSOS, 4);
        regra("then", CategoriaProblema.TRANSFORMACAO_COMPOSTA_DOIS_PASSOS, 3);
        regra("after that", CategoriaProblema.TRANSFORMACAO_COMPOSTA_DOIS_PASSOS, 4);
        regra("first step", CategoriaProblema.TRANSFORMACAO_COMPOSTA_DOIS_PASSOS, 4);
        regra("second step", CategoriaProblema.TRANSFORMACAO_COMPOSTA_DOIS_PASSOS, 4);

        regra("total change", CategoriaProblema.COMPOSICAO_TRANSFORMACOES, 6);
        regra("first round", CategoriaProblema.COMPOSICAO_TRANSFORMACOES, 4);
        regra("second round", CategoriaProblema.COMPOSICAO_TRANSFORMACOES, 4);
        regra("score", CategoriaProblema.COMPOSICAO_TRANSFORMACOES, 2);

        regra("now", CategoriaProblema.TRANSFORMACAO_RELACAO, 2);
        regra("then", CategoriaProblema.TRANSFORMACAO_RELACAO, 3);
        regra("how many more", CategoriaProblema.TRANSFORMACAO_RELACAO, 5);

        regra("what is the relation between", CategoriaProblema.COMPOSICAO_RELACOES, 6);
        regra("relation between", CategoriaProblema.COMPOSICAO_RELACOES, 5);

        regra("each", CategoriaProblema.MULTIPLICACAO, 4);
        regra("per", CategoriaProblema.MULTIPLICACAO, 4);
        regra("times", CategoriaProblema.MULTIPLICACAO, 4);

        regra("equally", CategoriaProblema.DIVISAO_PARTES, 4);
        regra("equal parts", CategoriaProblema.DIVISAO_PARTES, 5);
        regra("how many in each", CategoriaProblema.DIVISAO_PARTES, 5);

        regra("how many groups", CategoriaProblema.DIVISAO_COTAS, 5);
        regra("how many can be made", CategoriaProblema.DIVISAO_COTAS, 5);
    }
}
