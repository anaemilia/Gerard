package gerard.campoaditivo.curadoria;

import gerard.dominio.campoaditivo.situacao.CorrespondenciaPapelNarrativa;
import gerard.dominio.campoaditivo.situacao.EstadoNarrativo;
import gerard.dominio.campoaditivo.situacao.EventoNarrativoCurado;
import gerard.dominio.campoaditivo.situacao.FamiliaObjeto;
import gerard.dominio.campoaditivo.situacao.InventarioNarrativo;
import gerard.dominio.campoaditivo.situacao.MarcadorTemporal;
import gerard.dominio.campoaditivo.situacao.NarrativaCurada;
import gerard.dominio.campoaditivo.situacao.ObjetoContado;
import gerard.dominio.campoaditivo.situacao.ParticipanteNarrativo;
import gerard.dominio.campoaditivo.situacao.ReferenciaValorNarrativo;
import gerard.semantica.numero.NumeroNatural;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Constrói e decompõe a curadoria rica sem depender de Swing. */
public final class MontadorCuradoriaNarrativaRica {

    public RegistroCuradoriaNarrativaRica montar(
            RascunhoCuradoriaNarrativaRica rascunho) {
        if (rascunho == null) {
            throw new IllegalArgumentException("rascunho narrativo é obrigatório");
        }
        String idSituacao = obrigatorio(
                rascunho.getIdSituacao(), "id da situação");
        Map<String, ParticipanteNarrativo> participantes =
                montarParticipantes(rascunho.getParticipantes());
        Map<String, FamiliaObjeto> familias =
                montarFamilias(rascunho.getFamilias());
        Map<String, ObjetoContado> objetos =
                montarObjetos(rascunho.getObjetos(), familias);
        EstadoNarrativo inicial = montarEstado(
                rascunho.getOrdemEstadoInicial(),
                rascunho.getChaveEstadoInicial(),
                rascunho.getEstadoInicial(), participantes, objetos,
                "estado inicial");
        List<EventoNarrativoCurado> eventos = montarEventos(
                rascunho.getEventos(), participantes, objetos);
        EstadoNarrativo finalDeclarado = montarEstado(
                rascunho.getOrdemEstadoFinal(),
                rascunho.getChaveEstadoFinal(),
                rascunho.getEstadoFinal(), participantes, objetos,
                "estado final");
        List<CorrespondenciaPapelNarrativa> correspondencias =
                montarCorrespondencias(
                        rascunho.getCorrespondencias(),
                        participantes, familias, objetos, eventos);
        NarrativaCurada narrativa = new NarrativaCurada(
                rascunho.getContexto(), inicial, eventos, finalDeclarado);
        return new RegistroCuradoriaNarrativaRica(
                idSituacao, narrativa, correspondencias);
    }

    public RascunhoCuradoriaNarrativaRica decompor(
            RegistroCuradoriaNarrativaRica registro) {
        if (registro == null) {
            throw new IllegalArgumentException("registro narrativo é obrigatório");
        }
        Catalogo catalogo = catalogar(registro);
        List<RascunhoCuradoriaNarrativaRica.Participante> participantes =
                new ArrayList<>();
        for (ParticipanteNarrativo participante
                : catalogo.participantes.values()) {
            participantes.add(new RascunhoCuradoriaNarrativaRica.Participante(
                    participante.getId(), participante.getNomeExibicao()));
        }
        List<RascunhoCuradoriaNarrativaRica.Familia> familias =
                new ArrayList<>();
        for (FamiliaObjeto familia : catalogo.familias.values()) {
            familias.add(new RascunhoCuradoriaNarrativaRica.Familia(
                    familia.getId(), familia.getNomeConceitual()));
        }
        List<RascunhoCuradoriaNarrativaRica.Objeto> objetos =
                new ArrayList<>();
        for (ObjetoContado objeto : catalogo.objetos.values()) {
            objetos.add(new RascunhoCuradoriaNarrativaRica.Objeto(
                    objeto.getId(), objeto.getFamilia().getId(),
                    objeto.getChaveVisualAbstrata(),
                    formatarCaracteristicas(objeto.getCaracteristicas())));
        }
        NarrativaCurada narrativa = registro.getNarrativa();
        return new RascunhoCuradoriaNarrativaRica(
                registro.getIdSituacao(), narrativa.getContexto(),
                Integer.toString(narrativa.getEstadoInicial()
                        .getMarcador().getOrdem()),
                narrativa.getEstadoInicial().getMarcador().getChave(),
                Integer.toString(narrativa.getEstadoFinalDeclarado()
                        .getMarcador().getOrdem()),
                narrativa.getEstadoFinalDeclarado().getMarcador().getChave(),
                participantes, familias, objetos,
                decomporEstado(narrativa.getEstadoInicial()),
                decomporEventos(narrativa.getEventos()),
                decomporEstado(narrativa.getEstadoFinalDeclarado()),
                decomporCorrespondencias(registro.getCorrespondencias()));
    }

    private static Map<String, ParticipanteNarrativo> montarParticipantes(
            List<RascunhoCuradoriaNarrativaRica.Participante> linhas) {
        Map<String, ParticipanteNarrativo> resultado = new LinkedHashMap<>();
        for (RascunhoCuradoriaNarrativaRica.Participante linha : linhas) {
            ParticipanteNarrativo participante = new ParticipanteNarrativo(
                    obrigatorio(linha.getId(), "id do participante"),
                    obrigatorio(linha.getNome(), "nome do participante"));
            adicionarUnico(resultado, participante.getId(), participante,
                    "participante");
        }
        if (resultado.isEmpty()) {
            throw new IllegalArgumentException(
                    "declare pelo menos um participante");
        }
        return resultado;
    }

    private static Map<String, FamiliaObjeto> montarFamilias(
            List<RascunhoCuradoriaNarrativaRica.Familia> linhas) {
        Map<String, FamiliaObjeto> resultado = new LinkedHashMap<>();
        for (RascunhoCuradoriaNarrativaRica.Familia linha : linhas) {
            FamiliaObjeto familia = new FamiliaObjeto(
                    obrigatorio(linha.getId(), "id da família"),
                    obrigatorio(linha.getNome(), "nome da família"));
            adicionarUnico(resultado, familia.getId(), familia, "família");
        }
        if (resultado.isEmpty()) {
            throw new IllegalArgumentException("declare pelo menos uma família");
        }
        return resultado;
    }

    private static Map<String, ObjetoContado> montarObjetos(
            List<RascunhoCuradoriaNarrativaRica.Objeto> linhas,
            Map<String, FamiliaObjeto> familias) {
        Map<String, ObjetoContado> resultado = new LinkedHashMap<>();
        for (RascunhoCuradoriaNarrativaRica.Objeto linha : linhas) {
            FamiliaObjeto familia = exigir(
                    familias, linha.getFamiliaId(), "família do objeto");
            ObjetoContado objeto = new ObjetoContado(
                    obrigatorio(linha.getId(), "id do objeto"),
                    familia,
                    lerCaracteristicas(linha.getCaracteristicas()),
                    obrigatorio(linha.getChaveVisual(), "chave visual do objeto"));
            adicionarUnico(resultado, objeto.getId(), objeto, "objeto");
        }
        if (resultado.isEmpty()) {
            throw new IllegalArgumentException("declare pelo menos um objeto");
        }
        return resultado;
    }

    private static EstadoNarrativo montarEstado(
            String ordem,
            String chave,
            List<RascunhoCuradoriaNarrativaRica.ItemEstado> linhas,
            Map<String, ParticipanteNarrativo> participantes,
            Map<String, ObjetoContado> objetos,
            String nomeEstado) {
        Map<ParticipanteNarrativo, Map<ObjetoContado, NumeroNatural>> itens =
                new LinkedHashMap<>();
        for (RascunhoCuradoriaNarrativaRica.ItemEstado linha : linhas) {
            ParticipanteNarrativo participante = exigir(
                    participantes, linha.getParticipanteId(),
                    "participante do " + nomeEstado);
            Map<ObjetoContado, NumeroNatural> inventario = itens.get(participante);
            if (inventario == null) {
                inventario = new LinkedHashMap<>();
                itens.put(participante, inventario);
            }
            if (linha.getObjetoId().isEmpty()
                    && linha.getQuantidade().isEmpty()) {
                continue;
            }
            ObjetoContado objeto = exigir(
                    objetos, linha.getObjetoId(), "objeto do " + nomeEstado);
            NumeroNatural quantidade = new NumeroNatural(
                    inteiroNaoNegativo(linha.getQuantidade(),
                            "quantidade do " + nomeEstado));
            if (inventario.put(objeto, quantidade) != null) {
                throw new IllegalArgumentException(
                        "objeto repetido no inventário de "
                                + participante.getId() + ": " + objeto.getId());
            }
        }
        if (itens.isEmpty()) {
            throw new IllegalArgumentException(
                    nomeEstado + " precisa declarar ao menos um inventário");
        }
        Map<ParticipanteNarrativo, InventarioNarrativo> inventarios =
                new LinkedHashMap<>();
        for (Map.Entry<ParticipanteNarrativo,
                Map<ObjetoContado, NumeroNatural>> entrada : itens.entrySet()) {
            inventarios.put(
                    entrada.getKey(),
                    new InventarioNarrativo(entrada.getValue()));
        }
        return new EstadoNarrativo(
                new MarcadorTemporal(
                        inteiroNaoNegativo(ordem, "ordem do " + nomeEstado),
                        obrigatorio(chave, "chave do " + nomeEstado)),
                inventarios);
    }

    private static List<EventoNarrativoCurado> montarEventos(
            List<RascunhoCuradoriaNarrativaRica.Evento> linhas,
            Map<String, ParticipanteNarrativo> participantes,
            Map<String, ObjetoContado> objetos) {
        List<EventoNarrativoCurado> resultado = new ArrayList<>();
        Set<String> chaves = new LinkedHashSet<>();
        for (RascunhoCuradoriaNarrativaRica.Evento linha : linhas) {
            EventoNarrativoCurado.Tipo tipo;
            try {
                tipo = EventoNarrativoCurado.Tipo.valueOf(
                        obrigatorio(linha.getTipo(), "tipo do evento"));
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException(
                        "tipo do evento inválido: " + linha.getTipo(), ex);
            }
            MarcadorTemporal marcador = new MarcadorTemporal(
                    inteiroNaoNegativo(linha.getOrdem(), "ordem do evento"),
                    obrigatorio(linha.getChave(), "chave do evento"));
            if (!chaves.add(marcador.getChave())) {
                throw new IllegalArgumentException(
                        "chave de evento repetida: " + marcador.getChave());
            }
            ParticipanteNarrativo origem = opcional(
                    participantes, linha.getOrigemId(), "origem do evento");
            ParticipanteNarrativo destino = opcional(
                    participantes, linha.getDestinoId(), "destino do evento");
            ObjetoContado objeto = exigir(
                    objetos, linha.getObjetoId(), "objeto do evento");
            NumeroNatural quantidade = new NumeroNatural(
                    inteiroNaoNegativo(linha.getQuantidade(),
                            "quantidade do evento"));
            switch (tipo) {
                case ACRESCIMO:
                    resultado.add(EventoNarrativoCurado.acrescimo(
                            marcador, destino, objeto, quantidade));
                    break;
                case REDUCAO:
                    resultado.add(EventoNarrativoCurado.reducao(
                            marcador, origem, objeto, quantidade));
                    break;
                case TRANSFERENCIA:
                    resultado.add(EventoNarrativoCurado.transferencia(
                            marcador, origem, destino, objeto, quantidade));
                    break;
                default:
                    throw new IllegalArgumentException(
                            "tipo de evento não suportado");
            }
        }
        return resultado;
    }

    private static List<CorrespondenciaPapelNarrativa>
            montarCorrespondencias(
                    List<RascunhoCuradoriaNarrativaRica.Correspondencia> linhas,
                    Map<String, ParticipanteNarrativo> participantes,
                    Map<String, FamiliaObjeto> familias,
                    Map<String, ObjetoContado> objetos,
                    List<EventoNarrativoCurado> eventos) {
        Set<String> chavesEventos = new LinkedHashSet<>();
        for (EventoNarrativoCurado evento : eventos) {
            chavesEventos.add(evento.getMarcador().getChave());
        }
        List<CorrespondenciaPapelNarrativa> resultado = new ArrayList<>();
        for (RascunhoCuradoriaNarrativaRica.Correspondencia linha : linhas) {
            ReferenciaValorNarrativo.Tipo tipo;
            try {
                tipo = ReferenciaValorNarrativo.Tipo.valueOf(
                        obrigatorio(linha.getTipo(),
                                "tipo da correspondência"));
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException(
                        "tipo de correspondência inválido: "
                                + linha.getTipo(), ex);
            }
            ParticipanteNarrativo participante = exigir(
                    participantes, linha.getParticipanteId(),
                    "participante da correspondência");
            ParticipanteNarrativo comparado = opcional(
                    participantes, linha.getParticipanteComparadoId(),
                    "participante comparado");
            FamiliaObjeto familia = exigir(
                    familias, linha.getFamiliaId(),
                    "família da correspondência");
            ObjetoContado objeto = opcional(
                    objetos, linha.getObjetoId(),
                    "objeto da correspondência");
            String chaveEvento = linha.getEventoChave();
            if ((tipo == ReferenciaValorNarrativo.Tipo.VARIACAO_DE_EVENTO
                    || tipo == ReferenciaValorNarrativo.Tipo.QUANTIDADE_APOS_EVENTO)
                    && !chavesEventos.contains(chaveEvento)) {
                throw new IllegalArgumentException(
                        "correspondência referencia evento ausente: "
                                + chaveEvento);
            }
            ReferenciaValorNarrativo referencia = criarReferencia(
                    tipo, participante, comparado, familia,
                    chaveEvento, objeto);
            resultado.add(new CorrespondenciaPapelNarrativa(
                    obrigatorio(linha.getPapel(), "papel da correspondência"),
                    referencia));
        }
        if (resultado.isEmpty()) {
            throw new IllegalArgumentException(
                    "declare as correspondências entre papéis e narrativa");
        }
        return resultado;
    }

    private static ReferenciaValorNarrativo criarReferencia(
            ReferenciaValorNarrativo.Tipo tipo,
            ParticipanteNarrativo participante,
            ParticipanteNarrativo comparado,
            FamiliaObjeto familia,
            String chaveEvento,
            ObjetoContado objeto) {
        switch (tipo) {
            case QUANTIDADE_INICIAL:
                return ReferenciaValorNarrativo.quantidadeInicial(
                        participante, familia);
            case QUANTIDADE_INICIAL_DO_OBJETO:
                if (objeto == null) {
                    throw new IllegalArgumentException(
                            "quantidade inicial do objeto exige objeto");
                }
                if (!objeto.getFamilia().equals(familia)) {
                    throw new IllegalArgumentException(
                            "família da correspondência diverge do objeto");
                }
                return ReferenciaValorNarrativo.quantidadeInicialDoObjeto(
                        participante, objeto);
            case DIFERENCA_ENTRE_QUANTIDADES_INICIAIS:
                return ReferenciaValorNarrativo.diferencaEntreQuantidadesIniciais(
                        participante, comparadoObrigatorio(comparado), familia);
            case DIFERENCA_ENTRE_QUANTIDADES_FINAIS:
                return ReferenciaValorNarrativo.diferencaEntreQuantidadesFinais(
                        participante, comparadoObrigatorio(comparado), familia);
            case VARIACAO_DE_EVENTO:
                return ReferenciaValorNarrativo.variacaoDeEvento(
                        chaveEvento, participante, familia);
            case QUANTIDADE_APOS_EVENTO:
                return ReferenciaValorNarrativo.quantidadeAposEvento(
                        chaveEvento, participante, familia);
            case VARIACAO_TOTAL:
                return ReferenciaValorNarrativo.variacaoTotal(
                        participante, familia);
            case QUANTIDADE_FINAL_CALCULADA:
                return ReferenciaValorNarrativo.quantidadeFinalCalculada(
                        participante, familia);
            default:
                throw new IllegalArgumentException(
                        "tipo de correspondência não suportado");
        }
    }

    private static ParticipanteNarrativo comparadoObrigatorio(
            ParticipanteNarrativo comparado) {
        if (comparado == null) {
            throw new IllegalArgumentException(
                    "diferença exige participante comparado");
        }
        return comparado;
    }

    private static Map<String, String> lerCaracteristicas(String texto) {
        Map<String, String> resultado = new LinkedHashMap<>();
        String limpo = texto == null ? "" : texto.trim();
        if (limpo.isEmpty()) return resultado;
        for (String parte : limpo.split(";")) {
            String item = parte.trim();
            if (item.isEmpty()) continue;
            int separador = item.indexOf('=');
            if (separador <= 0 || separador == item.length() - 1) {
                throw new IllegalArgumentException(
                        "característica deve usar chave=valor: " + item);
            }
            String chave = item.substring(0, separador).trim();
            String valor = item.substring(separador + 1).trim();
            if (resultado.put(
                    obrigatorio(chave, "chave da característica"),
                    obrigatorio(valor, "valor da característica")) != null) {
                throw new IllegalArgumentException(
                        "característica repetida: " + chave);
            }
        }
        return resultado;
    }

    private static String formatarCaracteristicas(
            Map<String, String> caracteristicas) {
        StringBuilder texto = new StringBuilder();
        for (Map.Entry<String, String> entrada : caracteristicas.entrySet()) {
            if (texto.length() > 0) texto.append(';');
            texto.append(entrada.getKey()).append('=').append(entrada.getValue());
        }
        return texto.toString();
    }

    private static List<RascunhoCuradoriaNarrativaRica.ItemEstado>
            decomporEstado(EstadoNarrativo estado) {
        List<RascunhoCuradoriaNarrativaRica.ItemEstado> resultado =
                new ArrayList<>();
        for (Map.Entry<ParticipanteNarrativo, InventarioNarrativo> entrada
                : estado.getInventarios().entrySet()) {
            if (entrada.getValue().getQuantidades().isEmpty()) {
                resultado.add(new RascunhoCuradoriaNarrativaRica.ItemEstado(
                        entrada.getKey().getId(), "", ""));
                continue;
            }
            for (Map.Entry<ObjetoContado, NumeroNatural> item
                    : entrada.getValue().getQuantidades().entrySet()) {
                resultado.add(new RascunhoCuradoriaNarrativaRica.ItemEstado(
                        entrada.getKey().getId(), item.getKey().getId(),
                        Integer.toString(item.getValue().intValue())));
            }
        }
        return resultado;
    }

    private static List<RascunhoCuradoriaNarrativaRica.Evento>
            decomporEventos(List<EventoNarrativoCurado> eventos) {
        List<RascunhoCuradoriaNarrativaRica.Evento> resultado =
                new ArrayList<>();
        for (EventoNarrativoCurado evento : eventos) {
            resultado.add(new RascunhoCuradoriaNarrativaRica.Evento(
                    evento.getTipo().name(),
                    Integer.toString(evento.getMarcador().getOrdem()),
                    evento.getMarcador().getChave(),
                    evento.getOrigem() == null
                            ? "" : evento.getOrigem().getId(),
                    evento.getDestino() == null
                            ? "" : evento.getDestino().getId(),
                    evento.getObjeto().getId(),
                    Integer.toString(evento.getQuantidade().intValue())));
        }
        return resultado;
    }

    private static List<RascunhoCuradoriaNarrativaRica.Correspondencia>
            decomporCorrespondencias(
                    List<CorrespondenciaPapelNarrativa> correspondencias) {
        List<RascunhoCuradoriaNarrativaRica.Correspondencia> resultado =
                new ArrayList<>();
        for (CorrespondenciaPapelNarrativa correspondencia : correspondencias) {
            ReferenciaValorNarrativo referencia =
                    correspondencia.getReferencia();
            resultado.add(new RascunhoCuradoriaNarrativaRica.Correspondencia(
                    correspondencia.getChavePapel(),
                    referencia.getTipo().name(),
                    referencia.getParticipante().getId(),
                    referencia.getParticipanteComparado() == null
                            ? "" : referencia.getParticipanteComparado().getId(),
                    referencia.getFamilia().getId(),
                    referencia.getChaveEvento(),
                    referencia.getObjeto() == null
                            ? "" : referencia.getObjeto().getId()));
        }
        return resultado;
    }

    private static Catalogo catalogar(RegistroCuradoriaNarrativaRica registro) {
        Catalogo catalogo = new Catalogo();
        catalogarEstado(registro.getNarrativa().getEstadoInicial(), catalogo);
        catalogarEstado(
                registro.getNarrativa().getEstadoFinalDeclarado(), catalogo);
        for (EventoNarrativoCurado evento
                : registro.getNarrativa().getEventos()) {
            catalogarParticipante(evento.getOrigem(), catalogo);
            catalogarParticipante(evento.getDestino(), catalogo);
            catalogarObjeto(evento.getObjeto(), catalogo);
        }
        for (CorrespondenciaPapelNarrativa correspondencia
                : registro.getCorrespondencias()) {
            ReferenciaValorNarrativo referencia =
                    correspondencia.getReferencia();
            catalogarParticipante(referencia.getParticipante(), catalogo);
            catalogarParticipante(
                    referencia.getParticipanteComparado(), catalogo);
            catalogarFamilia(referencia.getFamilia(), catalogo);
            catalogarObjeto(referencia.getObjeto(), catalogo);
        }
        return catalogo;
    }

    private static void catalogarEstado(
            EstadoNarrativo estado, Catalogo catalogo) {
        for (Map.Entry<ParticipanteNarrativo, InventarioNarrativo> entrada
                : estado.getInventarios().entrySet()) {
            catalogarParticipante(entrada.getKey(), catalogo);
            for (ObjetoContado objeto
                    : entrada.getValue().getQuantidades().keySet()) {
                catalogarObjeto(objeto, catalogo);
            }
        }
    }

    private static void catalogarParticipante(
            ParticipanteNarrativo participante, Catalogo catalogo) {
        if (participante != null) {
            catalogo.participantes.put(participante.getId(), participante);
        }
    }

    private static void catalogarFamilia(
            FamiliaObjeto familia, Catalogo catalogo) {
        if (familia != null) {
            catalogo.familias.put(familia.getId(), familia);
        }
    }

    private static void catalogarObjeto(
            ObjetoContado objeto, Catalogo catalogo) {
        if (objeto != null) {
            catalogarFamilia(objeto.getFamilia(), catalogo);
            catalogo.objetos.put(objeto.getId(), objeto);
        }
    }

    private static int inteiroNaoNegativo(String valor, String nome) {
        try {
            int numero = Integer.parseInt(obrigatorio(valor, nome));
            if (numero < 0) throw new NumberFormatException(valor);
            return numero;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(
                    nome + " deve ser inteiro não negativo", ex);
        }
    }

    private static String obrigatorio(String valor, String nome) {
        String limpo = valor == null ? "" : valor.trim();
        if (limpo.isEmpty()) {
            throw new IllegalArgumentException(nome + " é obrigatório");
        }
        return limpo;
    }

    private static <T> T exigir(
            Map<String, T> catalogo, String id, String nome) {
        String chave = obrigatorio(id, nome);
        T valor = catalogo.get(chave);
        if (valor == null) {
            throw new IllegalArgumentException(
                    nome + " referencia id inexistente: " + chave);
        }
        return valor;
    }

    private static <T> T opcional(
            Map<String, T> catalogo, String id, String nome) {
        String limpo = id == null ? "" : id.trim();
        return limpo.isEmpty() ? null : exigir(catalogo, limpo, nome);
    }

    private static <T> void adicionarUnico(
            Map<String, T> catalogo,
            String id,
            T valor,
            String nome) {
        if (catalogo.put(id, valor) != null) {
            throw new IllegalArgumentException(
                    "id de " + nome + " repetido: " + id);
        }
    }

    private static final class Catalogo {
        private final Map<String, ParticipanteNarrativo> participantes =
                new LinkedHashMap<>();
        private final Map<String, FamiliaObjeto> familias =
                new LinkedHashMap<>();
        private final Map<String, ObjetoContado> objetos =
                new LinkedHashMap<>();
    }
}
