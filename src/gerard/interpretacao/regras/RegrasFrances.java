package gerard.interpretacao.regras;

import gerard.interpretacao.modelo.CategoriaProblema;
import gerard.interpretacao.modelo.IdiomaProblema;

public class RegrasFrances extends RegrasBase {
    public RegrasFrances() { super(IdiomaProblema.FRANCES); }

    protected void configurar() {
        indicador("combien");
        indicador("avait");
        indicador("a gagne");
        indicador("a perdu");
        indicador("en tout");
        indicador("au total");

        regra("en tout", CategoriaProblema.COMPOSICAO_MEDIDAS, 5);
        regra("au total", CategoriaProblema.COMPOSICAO_MEDIDAS, 5);
        regra("ensemble", CategoriaProblema.COMPOSICAO_MEDIDAS, 4);
        regra("total", CategoriaProblema.COMPOSICAO_MEDIDAS, 4);

        regra("de plus que", CategoriaProblema.COMPARACAO_MEDIDAS, 5);
        regra("de moins que", CategoriaProblema.COMPARACAO_MEDIDAS, 5);
        regra("plus que", CategoriaProblema.COMPARACAO_MEDIDAS, 4);
        regra("moins que", CategoriaProblema.COMPARACAO_MEDIDAS, 4);
        regra("plus age que", CategoriaProblema.COMPARACAO_MEDIDAS, 5);
        regra("plus agee que", CategoriaProblema.COMPARACAO_MEDIDAS, 5);
        regra("plus jeune que", CategoriaProblema.COMPARACAO_MEDIDAS, 5);

        regra("avait", CategoriaProblema.TRANSFORMACAO_MEDIDAS, 3);
        regra("a gagne", CategoriaProblema.TRANSFORMACAO_MEDIDAS, 5);
        regra("a perdu", CategoriaProblema.TRANSFORMACAO_MEDIDAS, 5);
        regra("a achete", CategoriaProblema.TRANSFORMACAO_MEDIDAS, 4);
        regra("a depense", CategoriaProblema.TRANSFORMACAO_MEDIDAS, 4);
        regra("a recu", CategoriaProblema.TRANSFORMACAO_MEDIDAS, 4);
        regra("a donne", CategoriaProblema.TRANSFORMACAO_MEDIDAS, 4);
        regra("il reste", CategoriaProblema.TRANSFORMACAO_MEDIDAS, 5);
        regra("elle reste", CategoriaProblema.TRANSFORMACAO_MEDIDAS, 5);
        regra("est reste", CategoriaProblema.TRANSFORMACAO_MEDIDAS, 4);

        regra("hier", CategoriaProblema.TRANSFORMACAO_COMPOSTA_DOIS_PASSOS, 4);
        regra("aujourd'hui", CategoriaProblema.TRANSFORMACAO_COMPOSTA_DOIS_PASSOS, 4);
        regra("ensuite", CategoriaProblema.TRANSFORMACAO_COMPOSTA_DOIS_PASSOS, 3);
        regra("apres", CategoriaProblema.TRANSFORMACAO_COMPOSTA_DOIS_PASSOS, 3);
        regra("premiere etape", CategoriaProblema.TRANSFORMACAO_COMPOSTA_DOIS_PASSOS, 4);
        regra("deuxieme etape", CategoriaProblema.TRANSFORMACAO_COMPOSTA_DOIS_PASSOS, 4);

        regra("transformation totale", CategoriaProblema.COMPOSICAO_TRANSFORMACOES, 6);
        regra("premier tour", CategoriaProblema.COMPOSICAO_TRANSFORMACOES, 4);
        regra("deuxieme tour", CategoriaProblema.COMPOSICAO_TRANSFORMACOES, 4);

        regra("maintenant", CategoriaProblema.TRANSFORMACAO_RELACAO, 2);
        regra("ensuite", CategoriaProblema.TRANSFORMACAO_RELACAO, 3);
        regra("combien de plus", CategoriaProblema.TRANSFORMACAO_RELACAO, 5);

        regra("quelle est la relation entre", CategoriaProblema.COMPOSICAO_RELACOES, 6);
        regra("relation entre", CategoriaProblema.COMPOSICAO_RELACOES, 5);

        regra("chaque", CategoriaProblema.MULTIPLICACAO, 4);
        regra("par", CategoriaProblema.MULTIPLICACAO, 3);
        regra("fois", CategoriaProblema.MULTIPLICACAO, 4);

        regra("parts egales", CategoriaProblema.DIVISAO_PARTES, 5);
        regra("egalement", CategoriaProblema.DIVISAO_PARTES, 4);
        regra("combien dans chaque", CategoriaProblema.DIVISAO_PARTES, 5);

        regra("combien de groupes", CategoriaProblema.DIVISAO_COTAS, 5);
        regra("combien peut-on faire", CategoriaProblema.DIVISAO_COTAS, 5);
    }
}
