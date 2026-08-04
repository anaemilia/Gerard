package gerard.pesquisador.auditoria;

import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Diagnóstico de baixo nível do despacho de {@code mouseReleased} (rodada
 * 4, 2026-07-31) — instrumenta a cadeia real de eventos AWT pra achar a
 * causa do disparo triplo que a rodada 3 só mitigou (debounce +
 * idempotência), sem identificar. Fica em pacote próprio (não em
 * {@code Main.java} — "não coloque código solto na Main") e é chamado por
 * UMA linha em cada handler relevante.
 *
 * Não decide nada, não muta estado pedagógico — só observa e grava em
 * {@code despacho_mouse_released.log}. Se este arquivo falhar ao escrever,
 * a interação principal não pode parar (mesmo princípio de
 * {@link FalhaAuditoriaLogger}).
 */
public final class DespachoMouseReleasedDiagnostico {
    private static PrintWriter destino;
    private static File arquivoDestino;

    // Identidade do MouseEvent (por referência, não por conteúdo) -> physical_event_id
    // ja atribuido, pra detectar se o MESMO objeto de evento é despachado
    // mais de uma vez (redispatch manual) versus objetos DIFERENTES com
    // when/coordenadas parecidos (evento fisico realmente duplicado antes
    // de chegar ao listener).
    private static final Map<MouseEvent, String> physicalEventIdPorObjeto = new IdentityHashMap<MouseEvent, String>();
    private static int contadorEventosFisicos = 0;

    // Contagem de dispatch por physical_event_id (quantas vezes o MESMO
    // physical_event_id chegou a um listener).
    private static final Map<String, Integer> dispatchesPorPhysicalEventId = new HashMap<String, Integer>();

    // Registro de listener: identidade (System.identityHashCode) -> quantas
    // vezes addMouseListener/addMouseMotionListener foi chamado para essa
    // MESMA instância, e em que ponto do código.
    private static final Map<Integer, Integer> contadorRegistroPorListener = new HashMap<Integer, Integer>();
    private static final Map<Integer, String> pontoRegistroPorListener = new HashMap<Integer, String>();

    private DespachoMouseReleasedDiagnostico() {
    }

    public static synchronized void definirArquivo(File arquivo) {
        try {
            if (destino != null) {
                destino.close();
            }
            arquivoDestino = arquivo;
            destino = new PrintWriter(new OutputStreamWriter(new FileOutputStream(arquivo, true), StandardCharsets.UTF_8));
            destino.println("# despacho_mouse_released.log — rodada 4, 2026-07-31 — uma linha JSON-lite por evento/registro/despacho");
            destino.flush();
        } catch (IOException e) {
            System.err.println("[DespachoMouseReleasedDiagnostico] falha ao abrir " + arquivo + ": " + e);
        }
    }

    /**
     * Chamado de UM lugar: {@code TelaGerard.addMouseListener}/{@code
     * addMouseMotionListener} logo antes de repassar pro método real do
     * {@code JComponent} — testa a hipótese "listener registrado mais de
     * uma vez" com contagem real, não suposição de leitura de código.
     */
    public static synchronized void registrarRegistroListener(Object listener, String tipoListener, String pontoCodigo) {
        int identidade = System.identityHashCode(listener);
        Integer anterior = contadorRegistroPorListener.get(identidade);
        int novo = (anterior == null ? 0 : anterior.intValue()) + 1;
        contadorRegistroPorListener.put(identidade, novo);
        String pontoAnterior = pontoRegistroPorListener.get(identidade);
        pontoRegistroPorListener.put(identidade, pontoCodigo);
        escrever("REGISTRO_LISTENER",
                "listener_class=" + listener.getClass().getName()
                        + " listener_identity=" + identidade
                        + " tipo=" + tipoListener
                        + " registration_count=" + novo
                        + " ponto_codigo=" + pontoCodigo
                        + (novo > 1 ? (" DUPLICATE_DETECTED ponto_anterior=" + pontoAnterior) : ""));
    }

    public static synchronized int getContadorRegistro(Object listener) {
        Integer c = contadorRegistroPorListener.get(Integer.valueOf(System.identityHashCode(listener)));
        return c == null ? 0 : c.intValue();
    }

    /**
     * Chamado no TOPO de {@code mouseReleased} real (antes de qualquer
     * lógica de negócio) — captura tudo que o pacote da rodada 4 pediu:
     * identidade do objeto MouseEvent, AWTEvent.getID(), when, botão,
     * cliques, coordenadas, thread, listener, stack fingerprint,
     * dispatch_index, dispatches_for_same_physical_release.
     *
     * @return dispatchIndex — 1 na primeira vez que ESTE physical_event_id
     *         (por identidade de objeto OU por when+botão+coordenadas, se
     *         forem objetos diferentes) é despachado; 2, 3... nas
     *         repetições. Quem chama pode usar isto pra decidir se é o
     *         despacho canônico (index 1) ou um redespacho a suprimir.
     */
    public static synchronized int registrarDespacho(MouseEvent evento, Object listener, String metodo) {
        String physicalEventId = physicalEventIdPorObjeto.get(evento);
        boolean objetoNovo = physicalEventId == null;
        if (objetoNovo) {
            // Objeto de evento nunca visto por identidade — mas pode ainda
            // assim ser o "mesmo" evento fisico se outro objeto MouseEvent
            // com when/botao/coordenadas identicos ja foi visto (isso
            // testaria a hipotese "criacao de novo MouseEvent durante
            // sincronizacao" / "redispatch manual com evento clonado").
            String chaveConteudo = evento.getWhen() + "|" + evento.getButton() + "|" + evento.getX() + "|" + evento.getY()
                    + "|" + evento.getID();
            String existente = physicalEventIdPorConteudo.get(chaveConteudo);
            if (existente != null) {
                physicalEventId = existente;
            } else {
                contadorEventosFisicos++;
                physicalEventId = "PHYS-RELEASE-" + String.format("%04d", contadorEventosFisicos);
                physicalEventIdPorConteudo.put(chaveConteudo, physicalEventId);
            }
            physicalEventIdPorObjeto.put(evento, physicalEventId);
        }

        Integer contagemAnterior = dispatchesPorPhysicalEventId.get(physicalEventId);
        int dispatchIndex = (contagemAnterior == null ? 0 : contagemAnterior.intValue()) + 1;
        dispatchesPorPhysicalEventId.put(physicalEventId, dispatchIndex);

        Thread threadAtual = Thread.currentThread();
        String fingerprint = fingerprintDaPilha();

        escrever("DESPACHO",
                "physical_event_id=" + physicalEventId
                        + " event_object_identity=" + System.identityHashCode(evento)
                        + " objeto_evento_novo=" + objetoNovo
                        + " metodo=" + metodo
                        + " awt_event_id=" + evento.getID()
                        + " when=" + evento.getWhen()
                        + " button=" + evento.getButton()
                        + " click_count=" + evento.getClickCount()
                        + " x=" + evento.getX() + " y=" + evento.getY()
                        + " thread_name=" + threadAtual.getName()
                        + " thread_id=" + threadAtual.getId()
                        + " listener_class=" + (listener == null ? "null" : listener.getClass().getName())
                        + " listener_identity=" + (listener == null ? -1 : System.identityHashCode(listener))
                        + " listener_registration_count=" + getContadorRegistro(listener)
                        + " stack_fingerprint=" + fingerprint
                        + " dispatch_index=" + dispatchIndex
                        + (dispatchIndex > 1 ? " REDISPATCH_DETECTED" : ""));
        return dispatchIndex;
    }

    private static final Map<String, String> physicalEventIdPorConteudo = new HashMap<String, String>();

    /** Correlaciona o resultado da avaliação (canônica ou suprimida) com o despacho já registrado. */
    public static synchronized void registrarCorrelacaoAvaliacao(int dispatchIndex, String gestureId,
            String actionId, boolean acaoCanonicaAberta, boolean chamouMonitor, boolean chamouZdp,
            boolean chamouModelador, boolean mutouEstado) {
        escrever("CORRELACAO",
                "dispatch_index=" + dispatchIndex
                        + " gesture_id=" + gestureId
                        + " action_id=" + actionId
                        + " acao_canonica_aberta=" + acaoCanonicaAberta
                        + " chamou_monitor=" + chamouMonitor
                        + " chamou_zdp=" + chamouZdp
                        + " chamou_modelador=" + chamouModelador
                        + " mutou_estado=" + mutouEstado);
    }

    private static String fingerprintDaPilha() {
        StackTraceElement[] pilha = Thread.currentThread().getStackTrace();
        StringBuilder sb = new StringBuilder();
        // Pula getStackTrace/fingerprintDaPilha/registrarDespacho (3
        // primeiros) — começa no chamador real de registrarDespacho.
        int inicio = Math.min(4, pilha.length);
        int fim = Math.min(inicio + 12, pilha.length);
        for (int i = inicio; i < fim; i++) {
            sb.append(pilha[i].getClassName()).append('.').append(pilha[i].getMethodName())
                    .append(':').append(pilha[i].getLineNumber()).append(';');
        }
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] hash = sha256.digest(sb.toString().getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder("sha256:");
            for (int i = 0; i < 8; i++) {
                hex.append(String.format("%02x", Byte.valueOf(hash[i])));
            }
            return hex.toString();
        } catch (Exception e) {
            return "sha256:indisponivel";
        }
    }

    private static synchronized void escrever(String tipo, String detalhe) {
        if (destino == null) {
            return;
        }
        try {
            destino.println("[" + tipo + "] " + detalhe);
            destino.flush();
        } catch (RuntimeException e) {
            System.err.println("[DespachoMouseReleasedDiagnostico] falha ao gravar: " + e);
        }
    }

    public static synchronized void fechar() {
        if (destino != null) {
            destino.close();
            destino = null;
        }
    }

    public static File getArquivoDestino() {
        return arquivoDestino;
    }
}
