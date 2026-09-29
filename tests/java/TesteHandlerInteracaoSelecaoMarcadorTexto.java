import gerard.campoaditivo.diagrama.elementos.ItemTextoArrastavel;
import gerard.campoaditivo.diagrama.elementos.MarcadorTexto;
import gerard.interacao.arraste.SessaoArrasteTextoParaDiagrama;
import gerard.interacao.texto.HandlerInteracaoSelecaoMarcadorTexto;
import gerard.interacao.texto.PoliticaUnicidadeElementoMatematicoTexto;
import gerard.interacao.texto.ResultadoSelecaoMarcadorTexto;
import java.util.ArrayList;
import java.util.List;

/** Fase 7.11: sequência portátil da seleção de marcador do enunciado. */
public final class TesteHandlerInteracaoSelecaoMarcadorTexto {

    public static void main(String[] args) {
        testarCliqueSemMarcadorNaoConsome();
        testarOcorrenciaJaPosicionadaNaoCriaProxy();
        testarSelecaoCriaCopiaSemRemoverOrigem();
        System.out.println("Teste aprovado: seleção do marcador preserva ausência, unicidade e cópia representacional.");
    }

    private static void testarCliqueSemMarcadorNaoConsome() {
        exigir(novo().processar(null, new ArrayList<ItemTextoArrastavel>()) == null,
                "Ausência de marcador não deve consumir o clique.");
    }

    private static void testarOcorrenciaJaPosicionadaNaoCriaProxy() {
        MarcadorTexto marcador = marcador();
        SessaoArrasteTextoParaDiagrama sessao = new SessaoArrasteTextoParaDiagrama();
        HandlerInteracaoSelecaoMarcadorTexto handler = novo(sessao);
        List<ItemTextoArrastavel> itens = new ArrayList<ItemTextoArrastavel>();
        ItemTextoArrastavel existente = sessao.iniciarPorMarcador(marcador);
        existente.y = 300;
        itens.add(existente);
        sessao.limpar();

        ResultadoSelecaoMarcadorTexto resultado = handler.processar(marcador, itens);
        exigir(resultado.getDesfecho()
                        == ResultadoSelecaoMarcadorTexto.Desfecho.JA_POSICIONADO,
                "Ocorrência já posicionada deve ser recusada antes de criar proxy.");
        exigir(resultado.getItem() == null && sessao.getElementoOrigem() == null,
                "Recusa por unicidade não pode abrir sessão de arraste.");
    }

    private static void testarSelecaoCriaCopiaSemRemoverOrigem() {
        MarcadorTexto marcador = marcador();
        SessaoArrasteTextoParaDiagrama sessao = new SessaoArrasteTextoParaDiagrama();
        ResultadoSelecaoMarcadorTexto resultado = novo(sessao).processar(
                marcador, new ArrayList<ItemTextoArrastavel>());

        exigir(resultado.getDesfecho()
                        == ResultadoSelecaoMarcadorTexto.Desfecho.INICIADO,
                "Marcador disponível deve iniciar a seleção.");
        exigir(resultado.getMarcador() == marcador,
                "O marcador de origem deve continuar referenciado, não removido.");
        exigir(resultado.getItem() != null && resultado.getItem().x == marcador.x
                        && resultado.getItem().y == marcador.y,
                "A seleção deve criar uma cópia na geometria real do marcador.");
        exigir(sessao.ehProxyAtivo(resultado.getItem()),
                "A cópia criada deve ser o proxy ativo da sessão.");
    }

    private static HandlerInteracaoSelecaoMarcadorTexto novo() {
        return novo(new SessaoArrasteTextoParaDiagrama());
    }

    private static HandlerInteracaoSelecaoMarcadorTexto novo(
            SessaoArrasteTextoParaDiagrama sessao) {
        return new HandlerInteracaoSelecaoMarcadorTexto(
                new PoliticaUnicidadeElementoMatematicoTexto(), sessao);
    }

    private static MarcadorTexto marcador() {
        return new MarcadorTexto(100, 80, 28, 24, "6", false,
                "papel.estadoFinal");
    }

    private static void exigir(boolean condicao, String mensagem) {
        if (!condicao) {
            throw new AssertionError(mensagem);
        }
    }
}
