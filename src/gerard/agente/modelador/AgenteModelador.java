package gerard.agente.modelador;

import gerard.agente.modelousuario.DiagnosticoTarefa;
import gerard.agente.modelousuario.ModeloUsuario;
import gerard.agente.modelousuario.NivelConceitualExplicacao;
import gerard.agente.modelousuario.RepositorioModeloUsuario;
import java.util.List;

/**
 * Agente Reativo Simples descrito em gerard-ajuda-adaptativa/references/
 * agente-modelador.md: mantém o Modelo do Usuário atualizado.
 *
 * Implementa as ações 1 (armazenar o novo caso) e 2 (inferir regras via
 * PART + Apriori — dependência Weka adicionada em 2026-07-22, ver lib/ e
 * InferenciaRegrasModelador). Uma parte da especificação segue de fora de
 * propósito:
 *
 *  - A percepção formal "Estratégia Pedagógica": vem do Agente ZDP. O ZDP
 *    hoje só decide a camada (ver gerard.agente.zdp.AgenteZDP), não uma
 *    estratégia pedagógica completa; por isso armazenarCaso recebe o
 *    DiagnosticoTarefa já pronto (com suporte preenchido a partir da
 *    camada), em vez de deliberar a partir de uma estratégia mais rica.
 */
public class AgenteModelador {
    private final RepositorioModeloUsuario repositorio;
    private final InferenciaRegrasModelador inferenciaRegras = new InferenciaRegrasModelador();
    private final AnalisadorNivelConceitual analisadorNivelConceitual = new AnalisadorNivelConceitual();

    public AgenteModelador(RepositorioModeloUsuario repositorio) {
        this.repositorio = repositorio;
    }

    public ModeloUsuario armazenarCaso(String idUsuario, DiagnosticoTarefa diagnostico) {
        ModeloUsuario modelo = repositorio.obterOuCriar(idUsuario);
        modelo.adicionarDiagnostico(diagnostico);
        repositorio.salvarDiagnosticos();
        return modelo;
    }

    /**
     * Complementa, com o autorrelato e o invariante operatório atribuído
     * pelo pesquisador no Artefato Explicativo (ver
     * DiagnosticoTarefa.getDificuldadeAutorrelatada/getInvarianteCodigo), o
     * caso mais recente já armazenado para a tarefa indicada — não cria um
     * caso novo, a ação instrumental em si já foi armazenada por
     * armazenarCaso no momento em que aconteceu. Chamado para cada ação
     * instrumental explicada no artefato (decisão do usuário em
     * 2026-07-23). O invariante entra direto como insumo de
     * InferenciaRegrasModelador: é escolha humana no momento da ação
     * (catálogo fechado ou forma simbólica nova), não um palpite que
     * precise de curadoria posterior como nivelConceitualEstimado.
     *
     * @return true se encontrou um caso da tarefa para complementar; false
     *         se não havia nenhum (ex.: ação nunca avaliada certo/errado).
     */
    public boolean registrarExplicacaoNoUltimoDiagnostico(String idUsuario, String tarefa,
            String dificuldadeAutorrelatada, String explicacaoElemento, String explicacaoGeral,
            String invarianteOrigem, String invarianteCodigo, String invarianteSimbolico, String invarianteObservacao) {
        if (idUsuario == null || tarefa == null) {
            return false;
        }
        ModeloUsuario modelo = repositorio.obterOuCriar(idUsuario);
        List<DiagnosticoTarefa> diagnosticos = modelo.getDiagnosticos();
        for (int i = diagnosticos.size() - 1; i >= 0; i--) {
            DiagnosticoTarefa diagnostico = diagnosticos.get(i);
            if (tarefa.equals(diagnostico.getTarefa())) {
                diagnostico.setDificuldadeAutorrelatada(dificuldadeAutorrelatada);
                diagnostico.setExplicacaoElemento(explicacaoElemento);
                diagnostico.setExplicacaoGeral(explicacaoGeral);
                NivelConceitualExplicacao nivelEstimado = analisadorNivelConceitual.classificar(explicacaoElemento);
                diagnostico.setNivelConceitualEstimado(nivelEstimado);
                diagnostico.setInvarianteOrigem(invarianteOrigem);
                diagnostico.setInvarianteCodigo(invarianteCodigo);
                diagnostico.setInvarianteSimbolico(invarianteSimbolico);
                diagnostico.setInvarianteObservacao(invarianteObservacao);
                repositorio.salvarDiagnosticos();
                return true;
            }
        }
        return false;
    }

    /**
     * Roda a ação 2 sobre os casos acumulados de um usuário: PART (indução
     * de regras) + Apriori (associação). Ver InferenciaRegrasModelador para
     * o porquê de não combinar as duas saídas via AND automaticamente, e
     * para o porquê dos dois limiares mínimos (projeto ainda sem corpus
     * real acumulado).
     */
    public InferenciaRegrasModelador.Resultado inferirRegras(String idUsuario, int numeroMinimoInstanciasPart,
                                                               int numeroMinimoInstanciasApriori) throws Exception {
        ModeloUsuario modelo = repositorio.obterOuCriar(idUsuario);
        return inferenciaRegras.inferir(modelo.getDiagnosticos(), numeroMinimoInstanciasPart,
                numeroMinimoInstanciasApriori);
    }
}
