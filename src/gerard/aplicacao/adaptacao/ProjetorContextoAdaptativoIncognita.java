package gerard.aplicacao.adaptacao;

import gerard.adaptacao.ContextoAdaptativoUsuario;
import gerard.adaptacao.sessao.SessaoAdaptativaUsuario;
import gerard.campoaditivo.curadoria.ResolvedorIncognitaCurada;
import gerard.campoaditivo.modelo.SituacaoProblemaAditiva;
import gerard.dominio.campoaditivo.DescritorRepresentacaoPapel;
import gerard.dominio.campoaditivo.IncognitaQuantitativa;
import gerard.dominio.campoaditivo.PapelQuantitativo;
import gerard.dominio.campoaditivo.TipoRepresentacaoAbstrata;
import gerard.dominio.campoaditivo.evento.PublicadorEventoDominio;
import gerard.semantica.numero.DominioNumerico;
import java.util.Optional;

/**
 * Fronteira de aplicação que liga a designação curada da situação à fotografia
 * ativa. Delega a resolução da identidade à curadoria, a definição do papel ao
 * modelo semântico e o recorte do usuário à sessão; não seleciona nem apresenta
 * ajuda.
 */
public final class ProjetorContextoAdaptativoIncognita {

    private final SessaoAdaptativaUsuario sessao;
    private final ResolvedorIncognitaCurada resolvedorIncognita;

    public ProjetorContextoAdaptativoIncognita(
            SessaoAdaptativaUsuario sessao) {
        if (sessao == null) {
            throw new IllegalArgumentException("sessão adaptativa é obrigatória");
        }
        this.sessao = sessao;
        this.resolvedorIncognita = new ResolvedorIncognitaCurada();
    }

    public ResultadoContextualizacaoIncognita projetarPara(
            SituacaoProblemaAditiva situacao) {
        return projetarPara(situacao, null);
    }

    /**
     * Reutiliza o fluxo de tentativas já ativo na situação. A projeção não
     * cria outra contagem nem outra identidade para a mesma incógnita.
     */
    public ResultadoContextualizacaoIncognita projetarPara(
            SituacaoProblemaAditiva situacao,
            PapelQuantitativo fluxoTentativas) {
        if (situacao == null) {
            return ResultadoContextualizacaoIncognita.indisponivel(
                    ResultadoContextualizacaoIncognita.Estado.SEM_SITUACAO,
                    "nenhuma situação-problema está ativa");
        }

        ResolvedorIncognitaCurada.Resultado designacao =
                resolvedorIncognita.resolver(situacao);
        if (designacao.possuiConflito()
                || designacao.possuiMultiplasInterrogacoes()) {
            return ResultadoContextualizacaoIncognita.indisponivel(
                    ResultadoContextualizacaoIncognita.Estado.DESIGNACAO_INCONSISTENTE,
                    designacao.mensagemInconsistencia());
        }
        if (!designacao.possuiIncognita()) {
            return ResultadoContextualizacaoIncognita.indisponivel(
                    ResultadoContextualizacaoIncognita.Estado.SEM_DESIGNACAO_INCOGNITA,
                    "a situação não designa uma incógnita original");
        }

        PapelQuantitativo fluxo = fluxoTentativas == null
                ? criarFluxoIsolado(designacao.getChaveEfetiva(), situacao)
                : fluxoTentativas;
        IncognitaQuantitativa incognita = new IncognitaQuantitativa(
                designacao.getChaveEfetiva(), situacao.getTipo(), fluxo);
        Optional<ContextoAdaptativoUsuario> contexto =
                sessao.projetarContextoPara(incognita);
        if (!contexto.isPresent()) {
            return ResultadoContextualizacaoIncognita.indisponivel(
                    ResultadoContextualizacaoIncognita.Estado.SEM_FOTOGRAFIA_ATIVA,
                    incognita,
                    "o usuário ainda não possui fotografia carregada no login");
        }
        return ResultadoContextualizacaoIncognita.disponivel(
                incognita, contexto.get());
    }

    private PapelQuantitativo criarFluxoIsolado(
            String chavePapel,
            SituacaoProblemaAditiva situacao) {
        String chave = chavePapel + "@" + (situacao.getId() == null
                ? "" : situacao.getId());
        return new PapelQuantitativo(
                chave,
                "Incógnita atual",
                DominioNumerico.INTEIROS,
                new DescritorRepresentacaoPapel(
                        TipoRepresentacaoAbstrata.FIGURA_RETANGULAR,
                        "rotulo.papel.incognitaAtual"),
                PublicadorEventoDominio.NENHUM);
    }
}
