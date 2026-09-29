import gerard.dominio.campoaditivo.ContextoAcao;
import gerard.dominio.campoaditivo.DiagnosticoErroPapel;
import gerard.dominio.campoaditivo.FabricaPapeisComparacaoMedidas;
import gerard.dominio.campoaditivo.OrigemAcao;
import gerard.dominio.campoaditivo.PapelQuantitativo;
import gerard.dominio.campoaditivo.TentativaModelagemAditiva;
import gerard.dominio.campoaditivo.TipoErroPapel;
import gerard.dominio.campoaditivo.TipoRestauracaoModelagem;
import gerard.dominio.campoaditivo.evento.EventoDominio;
import gerard.dominio.campoaditivo.evento.PublicadorEventoDominio;
import gerard.semantica.numero.NumeroNatural;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Decisões da usuária (2026-09-29): depois do diagrama ficar azul, toda
 * modificação é exploratória; o modo exploratório só termina quando uma nova
 * situação é sorteada (nova TentativaModelagemAditiva). A restauração não o
 * encerra. O dono da regra é a tentativa (e cada papel participante), não um
 * controlador.
 */
public final class TesteConclusaoAtingidaExploratoria {
    public static void main(String[] args) {
        List<EventoDominio> eventos = new ArrayList<EventoDominio>();
        PublicadorEventoDominio publicador = eventos::add;
        ContextoAcao contexto = new ContextoAcao("s", "u", "t", "sit", "diag");
        Optional<DiagnosticoErroPapel> incorreto = Optional.of(new DiagnosticoErroPapel(
                TipoErroPapel.VALOR_INCORRETO, "erro.papel.valorIncorreto", null, null));

        TentativaModelagemAditiva tentativa = new TentativaModelagemAditiva("t1");
        PapelQuantitativo papel = FabricaPapeisComparacaoMedidas.referendo(publicador);
        exigir(!tentativa.estaEncerradaPorConclusao() && tentativa.constituir("r").isPresent(),
                "Antes do azul, a ação constitui registro.");
        exigir(papel.registrarTentativaComIdentidade(papel.iniciarAcaoInstrumental(OrigemAcao.ORIGEM_USUARIO),
                incorreto, contexto, new NumeroNatural(5)).isAcaoRegistrada(),
                "Antes do azul, a tentativa do papel é registrada.");

        tentativa.encerrarPorConclusao(papel);
        int eventosAntes = eventos.size();
        exigir(tentativa.estaEncerradaPorConclusao() && papel.estaEncerradoPorConclusao(),
                "O azul encerra a tentativa e os papéis participantes.");
        exigir(!tentativa.constituir("r").isPresent(), "Após o azul, nenhuma ação constitui registro.");
        exigir(!papel.registrarTentativaComIdentidade(papel.iniciarAcaoInstrumental(OrigemAcao.ORIGEM_USUARIO),
                incorreto, contexto, new NumeroNatural(6)).isAcaoRegistrada(),
                "Após o azul, submissão do papel é exploratória.");
        exigir(!papel.registrarRespostaConfirmacaoValorRejeitado(papel.iniciarAcaoInstrumental(OrigemAcao.ORIGEM_USUARIO),
                true, contexto, new NumeroNatural(6)).isAcaoRegistrada(),
                "Após o azul, resposta Sim/Não é exploratória.");
        exigir(eventos.size() == eventosAntes, "Exploração não publica eventos de tentativa.");

        PapelQuantitativo tardio = FabricaPapeisComparacaoMedidas.valorRelativo(publicador);
        tentativa.incorporar(tardio);
        exigir(tardio.estaEncerradoPorConclusao(), "Papel criado depois do azul herda o encerramento.");

        tentativa.restaurar(TipoRestauracaoModelagem.values()[0], OrigemAcao.ORIGEM_USUARIO, papel);
        exigir(tentativa.estaEncerradaPorConclusao() && papel.estaEncerradoPorConclusao(),
                "Restaurar não encerra o modo exploratório.");

        TentativaModelagemAditiva nova = new TentativaModelagemAditiva("t2");
        PapelQuantitativo papelNovo = FabricaPapeisComparacaoMedidas.referendo(publicador);
        nova.incorporar(papelNovo);
        exigir(!nova.estaEncerradaPorConclusao() && !papelNovo.estaEncerradoPorConclusao()
                        && nova.constituir("r").isPresent(),
                "Só a nova situação sorteada (nova tentativa) termina o modo exploratório.");

        System.out.println("Teste aprovado: após o azul tudo é exploratório até nova situação sorteada.");
    }
    private static void exigir(boolean c, String m) { if (!c) throw new AssertionError(m); }
}
