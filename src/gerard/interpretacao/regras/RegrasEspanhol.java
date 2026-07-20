package gerard.interpretacao.regras;

import gerard.interpretacao.modelo.CategoriaProblema;
import gerard.interpretacao.modelo.IdiomaProblema;

/** Regras linguísticas básicas para situações em espanhol. */
public class RegrasEspanhol extends RegrasBase {
    public RegrasEspanhol() { super(IdiomaProblema.ESPANHOL); }

    protected void configurar() {
        indicador("cuantos");
        indicador("cuantas");
        indicador("tenia");
        indicador("gano");
        indicador("perdio");
        indicador("en total");

        regra("en total", CategoriaProblema.COMPOSICAO_MEDIDAS, 5);
        regra("entre todos", CategoriaProblema.COMPOSICAO_MEDIDAS, 5);
        regra("total", CategoriaProblema.COMPOSICAO_MEDIDAS, 4);

        regra("mas que", CategoriaProblema.COMPARACAO_MEDIDAS, 4);
        regra("menos que", CategoriaProblema.COMPARACAO_MEDIDAS, 4);
        regra("cuanto mas", CategoriaProblema.COMPARACAO_MEDIDAS, 5);
        regra("cuanto menos", CategoriaProblema.COMPARACAO_MEDIDAS, 5);

        regra("tenia", CategoriaProblema.TRANSFORMACAO_MEDIDAS, 3);
        regra("gano", CategoriaProblema.TRANSFORMACAO_MEDIDAS, 5);
        regra("perdio", CategoriaProblema.TRANSFORMACAO_MEDIDAS, 5);
        regra("compro", CategoriaProblema.TRANSFORMACAO_MEDIDAS, 4);
        regra("recibio", CategoriaProblema.TRANSFORMACAO_MEDIDAS, 4);
        regra("dio", CategoriaProblema.TRANSFORMACAO_MEDIDAS, 4);
        regra("quedan", CategoriaProblema.TRANSFORMACAO_MEDIDAS, 5);

        regra("despues", CategoriaProblema.TRANSFORMACAO_COMPOSTA_DOIS_PASSOS, 3);
        regra("primero", CategoriaProblema.TRANSFORMACAO_COMPOSTA_DOIS_PASSOS, 3);
        regra("luego", CategoriaProblema.TRANSFORMACAO_COMPOSTA_DOIS_PASSOS, 3);

        regra("transformacion total", CategoriaProblema.COMPOSICAO_TRANSFORMACOES, 6);
        regra("primera ronda", CategoriaProblema.COMPOSICAO_TRANSFORMACOES, 4);
        regra("segunda ronda", CategoriaProblema.COMPOSICAO_TRANSFORMACOES, 4);

        regra("relacion entre", CategoriaProblema.COMPOSICAO_RELACOES, 5);

        regra("cada", CategoriaProblema.MULTIPLICACAO, 4);
        regra("veces", CategoriaProblema.MULTIPLICACAO, 4);

        regra("partes iguales", CategoriaProblema.DIVISAO_PARTES, 5);
        regra("por igual", CategoriaProblema.DIVISAO_PARTES, 4);
        regra("cuanto en cada", CategoriaProblema.DIVISAO_PARTES, 5);

        regra("cuantos grupos", CategoriaProblema.DIVISAO_COTAS, 5);
    }
}
