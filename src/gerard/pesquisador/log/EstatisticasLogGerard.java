package gerard.pesquisador.log;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EstatisticasLogGerard {

    private final List<EventoLogGerard> eventos;

    public EstatisticasLogGerard(List<EventoLogGerard> eventos) {
        this.eventos = eventos == null ? new ArrayList<EventoLogGerard>() : eventos;
    }

    public int totalEventos() {
        return eventos.size();
    }

    public int totalPorAgente(String agente) {
        int total = 0;
        for (EventoLogGerard evento : eventos) {
            if (agente.equals(evento.getAgenteDaAcao())) {
                total++;
            }
        }
        return total;
    }

    public int totalAcertos() {
        return totalPorCE("C");
    }

    public int totalErros() {
        return totalPorCE("E");
    }

    public int totalPorCE(String ce) {
        int total = 0;
        for (EventoLogGerard evento : eventos) {
            if (ce.equals(evento.getCe())) {
                total++;
            }
        }
        return total;
    }

    public int totalProblemas() {
        Map<String, Integer> mapa = contarPorCampo(new Campo() {
            public String get(EventoLogGerard e) { return e.getSessao() + "::" + e.getProblema(); }
        });
        return mapa.size();
    }

    public Map<String, Integer> contarPorCategoria() {
        return contarPorCampo(new Campo() {
            public String get(EventoLogGerard e) { return vazioComoIndefinido(e.getCategoria()); }
        });
    }

    public Map<String, Integer> contarPorAgente() {
        return contarPorCampo(new Campo() {
            public String get(EventoLogGerard e) { return vazioComoIndefinido(e.getAgenteDaAcao()); }
        });
    }

    public Map<String, Integer> contarPorTarefa() {
        return contarPorCampo(new Campo() {
            public String get(EventoLogGerard e) { return vazioComoIndefinido(e.getTarefa()); }
        });
    }

    public Map<String, Integer> contarErrosPorCategoria() {
        Map<String, Integer> mapa = new LinkedHashMap<String, Integer>();
        for (EventoLogGerard evento : eventos) {
            if (!"E".equals(evento.getCe())) {
                continue;
            }
            incrementar(mapa, vazioComoIndefinido(evento.getCategoria()));
        }
        return mapa;
    }

    public Map<String, Integer> contarAcertosPorCategoria() {
        Map<String, Integer> mapa = new LinkedHashMap<String, Integer>();
        for (EventoLogGerard evento : eventos) {
            if (!"C".equals(evento.getCe())) {
                continue;
            }
            incrementar(mapa, vazioComoIndefinido(evento.getCategoria()));
        }
        return mapa;
    }

    public Map<String, Integer> contarPorInstrumentoArtefato() {
        return contarPorCampo(new Campo() {
            public String get(EventoLogGerard e) { return vazioComoIndefinido(e.getInstrumentoArtefato()); }
        });
    }

    public List<EventoLogGerard> getEventos() {
        return eventos;
    }

    private Map<String, Integer> contarPorCampo(Campo campo) {
        Map<String, Integer> mapa = new LinkedHashMap<String, Integer>();
        for (EventoLogGerard evento : eventos) {
            incrementar(mapa, campo.get(evento));
        }
        return mapa;
    }

    private void incrementar(Map<String, Integer> mapa, String chave) {
        String normalizada = vazioComoIndefinido(chave);
        Integer valor = mapa.get(normalizada);
        mapa.put(normalizada, Integer.valueOf(valor == null ? 1 : valor.intValue() + 1));
    }

    private String vazioComoIndefinido(String valor) {
        if (valor == null || valor.trim().length() == 0) {
            return "Não informado";
        }
        return valor.trim();
    }

    private interface Campo {
        String get(EventoLogGerard evento);
    }
}
