package gerard.agente.modelador;

import gerard.agente.modelousuario.DiagnosticoTarefa;
import gerard.agente.modelousuario.ModeloUsuario;
import gerard.agente.modelousuario.RepositorioModeloUsuario;

/**
 * Agente Reativo Simples descrito em gerard-ajuda-adaptativa/references/
 * agente-modelador.md: mantém o Modelo do Usuário atualizado.
 *
 * Implementa só a ação 1 da especificação (armazenar o novo caso na base do
 * Modelo do Usuário). Duas partes da especificação ficam de fora de
 * propósito, e não foram decididas silenciosamente:
 *
 *  - Ação 2 (inferir regras via J48.PART + APRIORI): exigiria adicionar uma
 *    biblioteca de aprendizado de máquina (ex.: Weka) como dependência
 *    externa do projeto — decisão que precisa de confirmação explícita do
 *    usuário, não tomada aqui.
 *  - A percepção formal "Estratégia Pedagógica": vem do Agente ZDP, que
 *    ainda não existe. Por isso armazenarCaso recebe o DiagnosticoTarefa já
 *    pronto, em vez de deliberar a partir de uma estratégia pedagógica.
 */
public class AgenteModelador {
    private final RepositorioModeloUsuario repositorio;

    public AgenteModelador(RepositorioModeloUsuario repositorio) {
        this.repositorio = repositorio;
    }

    public ModeloUsuario armazenarCaso(String idUsuario, DiagnosticoTarefa diagnostico) {
        ModeloUsuario modelo = repositorio.obterOuCriar(idUsuario);
        modelo.adicionarDiagnostico(diagnostico);
        return modelo;
    }
}
