package gerard.campoaditivo.curadoria;

import gerard.campoaditivo.servico.RepositorioSituacoesAditivas;
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
import gerard.dominio.campoaditivo.situacao.StatusCuradoriaSituacao;
import gerard.semantica.numero.NumeroNatural;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Persistência XML do conhecimento narrativo declarado pelo pesquisador.
 * O formato é separado da tabela legada para não transformar campos
 * posicionais em identidades ou correspondências semânticas.
 */
public final class RepositorioCuradoriaNarrativaRica {
    private static final String VERSAO_FORMATO = "2";
    private static final String VERSAO_ANTERIOR = "1";
    private static final String SUBDIRETORIO = "narrativas-ricas";
    private final File diretorio;

    public RepositorioCuradoriaNarrativaRica() {
        this(new File(
                RepositorioSituacoesAditivas.obterDiretorioCuradoriaUsuario(),
                SUBDIRETORIO));
    }

    public RepositorioCuradoriaNarrativaRica(File diretorio) {
        if (diretorio == null) {
            throw new IllegalArgumentException(
                    "diretório da curadoria narrativa é obrigatório");
        }
        this.diretorio = diretorio;
    }

    public File getDiretorio() { return diretorio; }

    public File arquivoDaSituacao(String idSituacao) {
        String id = obrigatorio(idSituacao, "id da situação é obrigatório");
        String prefixo = id.replaceAll("[^A-Za-z0-9._-]", "_");
        if (prefixo.length() > 48) prefixo = prefixo.substring(0, 48);
        if (prefixo.isEmpty()) prefixo = "situacao";
        return new File(diretorio, prefixo + "-" + hashCurto(id) + ".xml");
    }

    public void salvar(RegistroCuradoriaNarrativaRica registro)
            throws IOException {
        if (registro == null) {
            throw new IllegalArgumentException(
                    "registro da curadoria narrativa é obrigatório");
        }
        Catalogo catalogo = catalogar(registro);
        if (!diretorio.exists()
                && !diretorio.mkdirs()
                && !diretorio.exists()) {
            throw new IOException(
                    "não foi possível criar o diretório: "
                            + diretorio.getAbsolutePath());
        }
        File destino = arquivoDaSituacao(registro.getIdSituacao());
        File temporario = File.createTempFile(
                destino.getName() + ".", ".tmp", diretorio);
        boolean substituiu = false;
        try {
            escreverXml(temporario, registro, catalogo);
            if (destino.exists()) {
                File backup = new File(
                        diretorio,
                        destino.getName() + ".backup-"
                                + System.currentTimeMillis() + "-"
                                + System.nanoTime());
                Files.copy(
                        destino.toPath(), backup.toPath(),
                        StandardCopyOption.COPY_ATTRIBUTES);
            }
            try {
                Files.move(
                        temporario.toPath(), destino.toPath(),
                        StandardCopyOption.ATOMIC_MOVE,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException ex) {
                Files.move(
                        temporario.toPath(), destino.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);
            }
            substituiu = true;
        } finally {
            if (!substituiu) Files.deleteIfExists(temporario.toPath());
        }
    }

    public Optional<RegistroCuradoriaNarrativaRica> carregar(
            String idSituacao) throws IOException {
        String id = obrigatorio(idSituacao, "id da situação é obrigatório");
        File arquivo = arquivoDaSituacao(id);
        if (!arquivo.exists()) return Optional.empty();
        if (!arquivo.isFile()) {
            throw new IOException(
                    "registro narrativo não é um arquivo: "
                            + arquivo.getAbsolutePath());
        }
        try (FileInputStream entrada = new FileInputStream(arquivo)) {
            Document documento = novoConstrutorSeguro().parse(entrada);
            Element raiz = documento.getDocumentElement();
            if (raiz == null || !"curadoria-narrativa".equals(raiz.getTagName())) {
                throw new IOException("raiz XML da curadoria narrativa inválida");
            }
            String versao = raiz.getAttribute("versao");
            if (!VERSAO_FORMATO.equals(versao)
                    && !VERSAO_ANTERIOR.equals(versao)) {
                throw new IOException(
                        "versão da curadoria narrativa não suportada: "
                                + raiz.getAttribute("versao"));
            }
            String idPersistido = atributoObrigatorio(raiz, "situacao-id");
            if (!id.equals(idPersistido)) {
                throw new IOException(
                        "id solicitado diverge do id persistido: " + idPersistido);
            }
            return Optional.of(lerRegistro(raiz, idPersistido, versao));
        } catch (ParserConfigurationException | SAXException ex) {
            throw new IOException("XML da curadoria narrativa inválido", ex);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            throw new IOException(
                    "conteúdo da curadoria narrativa inválido: "
                            + ex.getMessage(), ex);
        }
    }

    private static void escreverXml(
            File arquivo,
            RegistroCuradoriaNarrativaRica registro,
            Catalogo catalogo) throws IOException {
        try (FileOutputStream saida = new FileOutputStream(arquivo)) {
            Document documento = novoDocumento();
            Element raiz = documento.createElement("curadoria-narrativa");
            raiz.setAttribute("versao", VERSAO_FORMATO);
            raiz.setAttribute("situacao-id", registro.getIdSituacao());
            raiz.setAttribute("status-curadoria",
                    registro.getStatusCuradoria().name());
            documento.appendChild(raiz);

            texto(documento, raiz, "contexto",
                    registro.getNarrativa().getContexto());
            escreverFamilias(documento, raiz, catalogo.familias);
            escreverObjetos(documento, raiz, catalogo.objetos);
            escreverParticipantes(documento, raiz, catalogo.participantes);
            escreverEstado(documento, raiz, "estado-inicial",
                    registro.getNarrativa().getEstadoInicial());
            escreverEventos(documento, raiz,
                    registro.getNarrativa().getEventos());
            escreverEstado(documento, raiz, "estado-final-declarado",
                    registro.getNarrativa().getEstadoFinalDeclarado());
            escreverCorrespondencias(documento, raiz,
                    registro.getCorrespondencias());

            TransformerFactory fabrica = TransformerFactory.newInstance();
            fabrica.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            restringirAcessoExterno(fabrica);
            Transformer transformer = fabrica.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING,
                    StandardCharsets.UTF_8.name());
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(
                    new DOMSource(documento), new StreamResult(saida));
        } catch (ParserConfigurationException | TransformerException ex) {
            throw new IOException(
                    "não foi possível escrever a curadoria narrativa", ex);
        }
    }

    private static void escreverFamilias(
            Document documento,
            Element raiz,
            Map<String, FamiliaObjeto> familias) {
        Element secao = filho(documento, raiz, "familias");
        for (FamiliaObjeto familia : familias.values()) {
            Element elemento = filho(documento, secao, "familia");
            elemento.setAttribute("id", familia.getId());
            elemento.setAttribute("nome", familia.getNomeConceitual());
        }
    }

    private static void escreverObjetos(
            Document documento,
            Element raiz,
            Map<String, ObjetoContado> objetos) {
        Element secao = filho(documento, raiz, "objetos");
        for (ObjetoContado objeto : objetos.values()) {
            Element elemento = filho(documento, secao, "objeto");
            elemento.setAttribute("id", objeto.getId());
            elemento.setAttribute("familia-id", objeto.getFamilia().getId());
            elemento.setAttribute(
                    "chave-visual", objeto.getChaveVisualAbstrata());
            for (Map.Entry<String, String> caracteristica
                    : objeto.getCaracteristicas().entrySet()) {
                Element item = filho(documento, elemento, "caracteristica");
                item.setAttribute("chave", caracteristica.getKey());
                item.setAttribute("valor", caracteristica.getValue());
            }
        }
    }

    private static void escreverParticipantes(
            Document documento,
            Element raiz,
            Map<String, ParticipanteNarrativo> participantes) {
        Element secao = filho(documento, raiz, "participantes");
        for (ParticipanteNarrativo participante : participantes.values()) {
            Element elemento = filho(documento, secao, "participante");
            elemento.setAttribute("id", participante.getId());
            elemento.setAttribute("nome", participante.getNomeExibicao());
        }
    }

    private static void escreverEstado(
            Document documento,
            Element raiz,
            String nome,
            EstadoNarrativo estado) {
        Element elemento = filho(documento, raiz, nome);
        elemento.setAttribute(
                "ordem", Integer.toString(estado.getMarcador().getOrdem()));
        elemento.setAttribute("chave", estado.getMarcador().getChave());
        for (Map.Entry<ParticipanteNarrativo, InventarioNarrativo> entrada
                : estado.getInventarios().entrySet()) {
            Element inventario = filho(documento, elemento, "inventario");
            inventario.setAttribute(
                    "participante-id", entrada.getKey().getId());
            for (Map.Entry<ObjetoContado, NumeroNatural> item
                    : entrada.getValue().getQuantidades().entrySet()) {
                Element quantidade = filho(documento, inventario, "item");
                quantidade.setAttribute("objeto-id", item.getKey().getId());
                quantidade.setAttribute(
                        "quantidade",
                        Integer.toString(item.getValue().intValue()));
            }
        }
    }

    private static void escreverEventos(
            Document documento,
            Element raiz,
            List<EventoNarrativoCurado> eventos) {
        Element secao = filho(documento, raiz, "eventos");
        for (EventoNarrativoCurado evento : eventos) {
            Element elemento = filho(documento, secao, "evento");
            elemento.setAttribute("tipo", evento.getTipo().name());
            elemento.setAttribute(
                    "ordem", Integer.toString(evento.getMarcador().getOrdem()));
            elemento.setAttribute("chave", evento.getMarcador().getChave());
            if (evento.getOrigem() != null) {
                elemento.setAttribute(
                        "origem-id", evento.getOrigem().getId());
            }
            if (evento.getDestino() != null) {
                elemento.setAttribute(
                        "destino-id", evento.getDestino().getId());
            }
            elemento.setAttribute("objeto-id", evento.getObjeto().getId());
            elemento.setAttribute(
                    "quantidade",
                    Integer.toString(evento.getQuantidade().intValue()));
        }
    }

    private static void escreverCorrespondencias(
            Document documento,
            Element raiz,
            List<CorrespondenciaPapelNarrativa> correspondencias) {
        Element secao = filho(documento, raiz, "correspondencias");
        for (CorrespondenciaPapelNarrativa correspondencia : correspondencias) {
            ReferenciaValorNarrativo referencia =
                    correspondencia.getReferencia();
            Element elemento = filho(documento, secao, "correspondencia");
            elemento.setAttribute("papel", correspondencia.getChavePapel());
            elemento.setAttribute("tipo", referencia.getTipo().name());
            elemento.setAttribute(
                    "participante-id", referencia.getParticipante().getId());
            if (referencia.getParticipanteComparado() != null) {
                elemento.setAttribute(
                        "participante-comparado-id",
                        referencia.getParticipanteComparado().getId());
            }
            elemento.setAttribute(
                    "familia-id", referencia.getFamilia().getId());
            if (!referencia.getChaveEvento().isEmpty()) {
                elemento.setAttribute(
                        "evento-chave", referencia.getChaveEvento());
            }
            if (referencia.getObjeto() != null) {
                elemento.setAttribute(
                        "objeto-id", referencia.getObjeto().getId());
            }
        }
    }

    private static RegistroCuradoriaNarrativaRica lerRegistro(
            Element raiz,
            String idSituacao,
            String versao) throws IOException {
        StatusCuradoriaSituacao statusCuradoria =
                VERSAO_ANTERIOR.equals(versao)
                ? StatusCuradoriaSituacao.CANDIDATA_NAO_CURADA
                : StatusCuradoriaSituacao.valueOf(
                        atributoObrigatorio(raiz, "status-curadoria"));
        String contexto = textoDoFilhoUnico(raiz, "contexto");
        Map<String, FamiliaObjeto> familias = lerFamilias(
                filhoUnico(raiz, "familias"));
        Map<String, ObjetoContado> objetos = lerObjetos(
                filhoUnico(raiz, "objetos"), familias);
        Map<String, ParticipanteNarrativo> participantes = lerParticipantes(
                filhoUnico(raiz, "participantes"));
        EstadoNarrativo inicial = lerEstado(
                filhoUnico(raiz, "estado-inicial"), participantes, objetos);
        Set<String> chavesEventos = new LinkedHashSet<String>();
        List<EventoNarrativoCurado> eventos = lerEventos(
                filhoUnico(raiz, "eventos"), participantes, objetos,
                chavesEventos);
        EstadoNarrativo finalDeclarado = lerEstado(
                filhoUnico(raiz, "estado-final-declarado"),
                participantes, objetos);
        List<CorrespondenciaPapelNarrativa> correspondencias =
                lerCorrespondencias(
                        filhoUnico(raiz, "correspondencias"),
                        participantes, familias, objetos, chavesEventos);
        NarrativaCurada narrativa = new NarrativaCurada(
                contexto, inicial, eventos, finalDeclarado);
        return new RegistroCuradoriaNarrativaRica(
                idSituacao, statusCuradoria, narrativa, correspondencias);
    }

    private static Map<String, FamiliaObjeto> lerFamilias(Element secao)
            throws IOException {
        Map<String, FamiliaObjeto> familias = new LinkedHashMap<>();
        for (Element elemento : filhosDiretos(secao, "familia")) {
            FamiliaObjeto familia = new FamiliaObjeto(
                    atributoObrigatorio(elemento, "id"),
                    atributoObrigatorio(elemento, "nome"));
            if (familias.put(familia.getId(), familia) != null) {
                throw new IOException(
                        "id de família duplicado: " + familia.getId());
            }
        }
        return familias;
    }

    private static Map<String, ObjetoContado> lerObjetos(
            Element secao,
            Map<String, FamiliaObjeto> familias) throws IOException {
        Map<String, ObjetoContado> objetos = new LinkedHashMap<>();
        for (Element elemento : filhosDiretos(secao, "objeto")) {
            String familiaId = atributoObrigatorio(elemento, "familia-id");
            FamiliaObjeto familia = exigirReferencia(
                    familias, familiaId, "família do objeto");
            Map<String, String> caracteristicas = new LinkedHashMap<>();
            for (Element caracteristica
                    : filhosDiretos(elemento, "caracteristica")) {
                String chave = atributoObrigatorio(caracteristica, "chave");
                String valor = atributoObrigatorio(caracteristica, "valor");
                if (caracteristicas.put(chave, valor) != null) {
                    throw new IOException(
                            "característica duplicada: " + chave);
                }
            }
            ObjetoContado objeto = new ObjetoContado(
                    atributoObrigatorio(elemento, "id"),
                    familia,
                    caracteristicas,
                    atributoObrigatorio(elemento, "chave-visual"));
            if (objetos.put(objeto.getId(), objeto) != null) {
                throw new IOException(
                        "id de objeto duplicado: " + objeto.getId());
            }
        }
        return objetos;
    }

    private static Map<String, ParticipanteNarrativo> lerParticipantes(
            Element secao) throws IOException {
        Map<String, ParticipanteNarrativo> participantes =
                new LinkedHashMap<>();
        for (Element elemento : filhosDiretos(secao, "participante")) {
            ParticipanteNarrativo participante = new ParticipanteNarrativo(
                    atributoObrigatorio(elemento, "id"),
                    atributoObrigatorio(elemento, "nome"));
            if (participantes.put(participante.getId(), participante) != null) {
                throw new IOException(
                        "id de participante duplicado: "
                                + participante.getId());
            }
        }
        return participantes;
    }

    private static EstadoNarrativo lerEstado(
            Element elemento,
            Map<String, ParticipanteNarrativo> participantes,
            Map<String, ObjetoContado> objetos) throws IOException {
        MarcadorTemporal marcador = new MarcadorTemporal(
                inteiroNaoNegativo(elemento, "ordem"),
                atributoObrigatorio(elemento, "chave"));
        Map<ParticipanteNarrativo, InventarioNarrativo> inventarios =
                new LinkedHashMap<>();
        for (Element inventarioElement
                : filhosDiretos(elemento, "inventario")) {
            String participanteId = atributoObrigatorio(
                    inventarioElement, "participante-id");
            ParticipanteNarrativo participante = exigirReferencia(
                    participantes, participanteId, "participante do inventário");
            Map<ObjetoContado, NumeroNatural> itens = new LinkedHashMap<>();
            for (Element item : filhosDiretos(inventarioElement, "item")) {
                String objetoId = atributoObrigatorio(item, "objeto-id");
                ObjetoContado objeto = exigirReferencia(
                        objetos, objetoId, "objeto do inventário");
                NumeroNatural quantidade = new NumeroNatural(
                        inteiroNaoNegativo(item, "quantidade"));
                if (itens.put(objeto, quantidade) != null) {
                    throw new IOException(
                            "objeto duplicado no inventário: " + objetoId);
                }
            }
            if (inventarios.put(
                    participante, new InventarioNarrativo(itens)) != null) {
                throw new IOException(
                        "inventário duplicado do participante: "
                                + participanteId);
            }
        }
        return new EstadoNarrativo(marcador, inventarios);
    }

    private static List<EventoNarrativoCurado> lerEventos(
            Element secao,
            Map<String, ParticipanteNarrativo> participantes,
            Map<String, ObjetoContado> objetos,
            Set<String> chavesEventos) throws IOException {
        List<EventoNarrativoCurado> eventos = new ArrayList<>();
        for (Element elemento : filhosDiretos(secao, "evento")) {
            EventoNarrativoCurado.Tipo tipo;
            try {
                tipo = EventoNarrativoCurado.Tipo.valueOf(
                        atributoObrigatorio(elemento, "tipo"));
            } catch (IllegalArgumentException ex) {
                throw new IOException("tipo de evento narrativo inválido", ex);
            }
            MarcadorTemporal marcador = new MarcadorTemporal(
                    inteiroNaoNegativo(elemento, "ordem"),
                    atributoObrigatorio(elemento, "chave"));
            if (!chavesEventos.add(marcador.getChave())) {
                throw new IOException(
                        "chave de evento duplicada: " + marcador.getChave());
            }
            ParticipanteNarrativo origem = referenciaOpcional(
                    participantes, elemento.getAttribute("origem-id"),
                    "origem do evento");
            ParticipanteNarrativo destino = referenciaOpcional(
                    participantes, elemento.getAttribute("destino-id"),
                    "destino do evento");
            ObjetoContado objeto = exigirReferencia(
                    objetos,
                    atributoObrigatorio(elemento, "objeto-id"),
                    "objeto do evento");
            NumeroNatural quantidade = new NumeroNatural(
                    inteiroNaoNegativo(elemento, "quantidade"));
            switch (tipo) {
                case ACRESCIMO:
                    eventos.add(EventoNarrativoCurado.acrescimo(
                            marcador, destino, objeto, quantidade));
                    break;
                case REDUCAO:
                    eventos.add(EventoNarrativoCurado.reducao(
                            marcador, origem, objeto, quantidade));
                    break;
                case TRANSFERENCIA:
                    eventos.add(EventoNarrativoCurado.transferencia(
                            marcador, origem, destino, objeto, quantidade));
                    break;
                default:
                    throw new IOException("tipo de evento não suportado");
            }
        }
        return eventos;
    }

    private static List<CorrespondenciaPapelNarrativa> lerCorrespondencias(
            Element secao,
            Map<String, ParticipanteNarrativo> participantes,
            Map<String, FamiliaObjeto> familias,
            Map<String, ObjetoContado> objetos,
            Set<String> chavesEventos) throws IOException {
        List<CorrespondenciaPapelNarrativa> correspondencias =
                new ArrayList<>();
        for (Element elemento : filhosDiretos(secao, "correspondencia")) {
            String papel = atributoObrigatorio(elemento, "papel");
            ReferenciaValorNarrativo.Tipo tipo;
            try {
                tipo = ReferenciaValorNarrativo.Tipo.valueOf(
                        atributoObrigatorio(elemento, "tipo"));
            } catch (IllegalArgumentException ex) {
                throw new IOException("tipo de referência narrativa inválido", ex);
            }
            ParticipanteNarrativo participante = exigirReferencia(
                    participantes,
                    atributoObrigatorio(elemento, "participante-id"),
                    "participante da correspondência");
            FamiliaObjeto familia = exigirReferencia(
                    familias,
                    atributoObrigatorio(elemento, "familia-id"),
                    "família da correspondência");
            ParticipanteNarrativo comparado = referenciaOpcional(
                    participantes,
                    elemento.getAttribute("participante-comparado-id"),
                    "participante comparado");
            ObjetoContado objeto = referenciaOpcional(
                    objetos,
                    elemento.getAttribute("objeto-id"),
                    "objeto da correspondência");
            String evento = elemento.getAttribute("evento-chave").trim();
            if ((tipo == ReferenciaValorNarrativo.Tipo.VARIACAO_DE_EVENTO
                    || tipo == ReferenciaValorNarrativo.Tipo.QUANTIDADE_APOS_EVENTO)
                    && !chavesEventos.contains(evento)) {
                throw new IOException(
                        "correspondência referencia evento ausente: " + evento);
            }
            ReferenciaValorNarrativo referencia = criarReferencia(
                    tipo, participante, comparado, familia, evento, objeto);
            correspondencias.add(new CorrespondenciaPapelNarrativa(
                    papel, referencia));
        }
        return correspondencias;
    }

    private static ReferenciaValorNarrativo criarReferencia(
            ReferenciaValorNarrativo.Tipo tipo,
            ParticipanteNarrativo participante,
            ParticipanteNarrativo comparado,
            FamiliaObjeto familia,
            String evento,
            ObjetoContado objeto) throws IOException {
        switch (tipo) {
            case QUANTIDADE_INICIAL:
                return ReferenciaValorNarrativo.quantidadeInicial(
                        participante, familia);
            case QUANTIDADE_INICIAL_DO_OBJETO:
                if (objeto == null) {
                    throw new IOException(
                            "quantidade inicial do objeto exige objeto-id");
                }
                if (!objeto.getFamilia().equals(familia)) {
                    throw new IOException(
                            "família da correspondência diverge da família do objeto");
                }
                return ReferenciaValorNarrativo.quantidadeInicialDoObjeto(
                        participante, objeto);
            case DIFERENCA_ENTRE_QUANTIDADES_INICIAIS:
                return ReferenciaValorNarrativo.diferencaEntreQuantidadesIniciais(
                        participante, exigirComparado(comparado), familia);
            case DIFERENCA_ENTRE_QUANTIDADES_FINAIS:
                return ReferenciaValorNarrativo.diferencaEntreQuantidadesFinais(
                        participante, exigirComparado(comparado), familia);
            case VARIACAO_DE_EVENTO:
                return ReferenciaValorNarrativo.variacaoDeEvento(
                        evento, participante, familia);
            case QUANTIDADE_APOS_EVENTO:
                return ReferenciaValorNarrativo.quantidadeAposEvento(
                        evento, participante, familia);
            case VARIACAO_TOTAL:
                return ReferenciaValorNarrativo.variacaoTotal(
                        participante, familia);
            case QUANTIDADE_FINAL_CALCULADA:
                return ReferenciaValorNarrativo.quantidadeFinalCalculada(
                        participante, familia);
            default:
                throw new IOException("tipo de referência não suportado");
        }
    }

    private static ParticipanteNarrativo exigirComparado(
            ParticipanteNarrativo comparado) throws IOException {
        if (comparado == null) {
            throw new IOException(
                    "referência de diferença exige participante comparado");
        }
        return comparado;
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
            EstadoNarrativo estado,
            Catalogo catalogo) {
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
            ParticipanteNarrativo participante,
            Catalogo catalogo) {
        if (participante == null) return;
        ParticipanteNarrativo anterior = catalogo.participantes.put(
                participante.getId(), participante);
        if (anterior != null
                && !anterior.getNomeExibicao().equals(
                        participante.getNomeExibicao())) {
            throw new IllegalArgumentException(
                    "participante com definições divergentes: "
                            + participante.getId());
        }
    }

    private static void catalogarFamilia(
            FamiliaObjeto familia,
            Catalogo catalogo) {
        if (familia == null) return;
        FamiliaObjeto anterior = catalogo.familias.put(
                familia.getId(), familia);
        if (anterior != null
                && !anterior.getNomeConceitual().equals(
                        familia.getNomeConceitual())) {
            throw new IllegalArgumentException(
                    "família com definições divergentes: " + familia.getId());
        }
    }

    private static void catalogarObjeto(
            ObjetoContado objeto,
            Catalogo catalogo) {
        if (objeto == null) return;
        catalogarFamilia(objeto.getFamilia(), catalogo);
        ObjetoContado anterior = catalogo.objetos.put(objeto.getId(), objeto);
        if (anterior != null
                && (!anterior.getFamilia().equals(objeto.getFamilia())
                || !anterior.getCaracteristicas().equals(
                        objeto.getCaracteristicas())
                || !anterior.getChaveVisualAbstrata().equals(
                        objeto.getChaveVisualAbstrata()))) {
            throw new IllegalArgumentException(
                    "objeto com definições divergentes: " + objeto.getId());
        }
    }

    private static Document novoDocumento()
            throws ParserConfigurationException {
        return novaFabricaSegura().newDocumentBuilder().newDocument();
    }

    private static DocumentBuilder novoConstrutorSeguro()
            throws ParserConfigurationException {
        DocumentBuilder construtor =
                novaFabricaSegura().newDocumentBuilder();
        construtor.setErrorHandler(new DefaultHandler());
        return construtor;
    }

    private static DocumentBuilderFactory novaFabricaSegura()
            throws ParserConfigurationException {
        DocumentBuilderFactory fabrica = DocumentBuilderFactory.newInstance();
        fabrica.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        fabrica.setFeature(
                "http://apache.org/xml/features/disallow-doctype-decl", true);
        fabrica.setFeature(
                "http://xml.org/sax/features/external-general-entities", false);
        fabrica.setFeature(
                "http://xml.org/sax/features/external-parameter-entities", false);
        fabrica.setFeature(
                "http://apache.org/xml/features/nonvalidating/load-external-dtd",
                false);
        fabrica.setXIncludeAware(false);
        fabrica.setExpandEntityReferences(false);
        fabrica.setIgnoringComments(true);
        fabrica.setCoalescing(true);
        return fabrica;
    }

    private static void restringirAcessoExterno(TransformerFactory fabrica) {
        try {
            fabrica.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            fabrica.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        } catch (IllegalArgumentException ex) {
            // FEATURE_SECURE_PROCESSING já permanece obrigatório.
        }
    }

    private static Element filho(
            Document documento,
            Element pai,
            String nome) {
        Element elemento = documento.createElement(nome);
        pai.appendChild(elemento);
        return elemento;
    }

    private static void texto(
            Document documento,
            Element pai,
            String nome,
            String valor) {
        Element elemento = filho(documento, pai, nome);
        elemento.appendChild(documento.createTextNode(
                valor == null ? "" : valor));
    }

    private static Element filhoUnico(Element pai, String nome)
            throws IOException {
        List<Element> encontrados = filhosDiretos(pai, nome);
        if (encontrados.size() != 1) {
            throw new IOException(
                    "elemento XML deve ocorrer uma vez: " + nome);
        }
        return encontrados.get(0);
    }

    private static String textoDoFilhoUnico(Element pai, String nome)
            throws IOException {
        return filhoUnico(pai, nome).getTextContent();
    }

    private static List<Element> filhosDiretos(Element pai, String nome) {
        List<Element> elementos = new ArrayList<>();
        NodeList nos = pai.getChildNodes();
        for (int i = 0; i < nos.getLength(); i++) {
            Node no = nos.item(i);
            if (no instanceof Element
                    && nome.equals(((Element) no).getTagName())) {
                elementos.add((Element) no);
            }
        }
        return elementos;
    }

    private static String atributoObrigatorio(Element elemento, String nome)
            throws IOException {
        String valor = elemento.getAttribute(nome).trim();
        if (valor.isEmpty()) {
            throw new IOException(
                    "atributo XML obrigatório ausente: " + nome);
        }
        return valor;
    }

    private static int inteiroNaoNegativo(Element elemento, String atributo)
            throws IOException {
        String valor = atributoObrigatorio(elemento, atributo);
        try {
            int numero = Integer.parseInt(valor);
            if (numero < 0) throw new NumberFormatException(valor);
            return numero;
        } catch (NumberFormatException ex) {
            throw new IOException(
                    "inteiro não negativo inválido em " + atributo
                            + ": " + valor, ex);
        }
    }

    private static <T> T exigirReferencia(
            Map<String, T> catalogo,
            String id,
            String papel) throws IOException {
        T valor = catalogo.get(id);
        if (valor == null) {
            throw new IOException(papel + " referencia id ausente: " + id);
        }
        return valor;
    }

    private static <T> T referenciaOpcional(
            Map<String, T> catalogo,
            String id,
            String papel) throws IOException {
        String limpo = id == null ? "" : id.trim();
        return limpo.isEmpty() ? null : exigirReferencia(catalogo, limpo, papel);
    }

    private static String obrigatorio(String valor, String mensagem) {
        String limpo = valor == null ? "" : valor.trim();
        if (limpo.isEmpty()) throw new IllegalArgumentException(mensagem);
        return limpo;
    }

    private static String hashCurto(String valor) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(
                    valor.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexadecimal = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                hexadecimal.append(String.format("%02x", bytes[i] & 0xff));
            }
            return hexadecimal.toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 indisponível", ex);
        }
    }

    private static final class Catalogo {
        private final Map<String, FamiliaObjeto> familias =
                new LinkedHashMap<>();
        private final Map<String, ObjetoContado> objetos =
                new LinkedHashMap<>();
        private final Map<String, ParticipanteNarrativo> participantes =
                new LinkedHashMap<>();
    }
}
