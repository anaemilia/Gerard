package gerard.adaptacao;

import gerard.adaptacao.modelousuario.ProjecaoModeloUsuario;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Projeção imutável do Modelo do Usuário carregada para um único proprietário.
 * Não expõe o modelo mutável completo nem permite atualização durante a sessão.
 */
public final class ContextoAdaptativoUsuario {

    private final String versaoModelo;
    private final String proprietarioSemantico;
    private final EscopoProprietarioSemantico escopo;
    private final ProjecaoModeloUsuario projecaoModeloUsuario;
    private final List<RegraAdaptativaPublicada> regras;

    public ContextoAdaptativoUsuario(
            String proprietarioSemantico,
            EscopoProprietarioSemantico escopo,
            ProjecaoModeloUsuario projecaoModeloUsuario,
            List<RegraAdaptativaPublicada> regras) {
        if (projecaoModeloUsuario == null) {
            throw new IllegalArgumentException("projeção do Modelo do Usuário é obrigatória");
        }
        this.versaoModelo = textoObrigatorio(
                projecaoModeloUsuario.getVersaoModelo(), "versão do modelo");
        this.proprietarioSemantico = textoObrigatorio(proprietarioSemantico, "proprietário semântico");
        if (escopo == null) {
            throw new IllegalArgumentException("escopo não pode ser nulo");
        }
        this.escopo = escopo;
        if (!projecaoModeloUsuario.solicitaConhecimentoAlemDePerfil()) {
            throw new IllegalArgumentException(
                    "contexto adaptativo exige projeção multidimensional além do perfil");
        }
        this.projecaoModeloUsuario = projecaoModeloUsuario;
        this.regras = copiarEValidar(regras);
    }

    public String getVersaoModelo() { return versaoModelo; }
    public String getProprietarioSemantico() { return proprietarioSemantico; }
    public EscopoProprietarioSemantico getEscopo() { return escopo; }
    public ProjecaoModeloUsuario getProjecaoModeloUsuario() { return projecaoModeloUsuario; }
    public List<RegraAdaptativaPublicada> getRegras() { return regras; }

    public boolean contem(RegraAdaptativaPublicada regra) {
        if (regra == null) return false;
        for (RegraAdaptativaPublicada candidata : regras) {
            if (candidata.getId().equals(regra.getId())
                    && candidata.getVersao().equals(regra.getVersao())) {
                return true;
            }
        }
        return false;
    }

    private List<RegraAdaptativaPublicada> copiarEValidar(List<RegraAdaptativaPublicada> origem) {
        if (origem == null) {
            throw new IllegalArgumentException("regras não podem ser nulas");
        }
        List<RegraAdaptativaPublicada> copia = new ArrayList<RegraAdaptativaPublicada>();
        Map<String, Boolean> identidades = new LinkedHashMap<String, Boolean>();
        for (RegraAdaptativaPublicada regra : origem) {
            if (regra == null || !regra.estaPublicada()) {
                throw new IllegalArgumentException("a fotografia só pode conter regras publicadas");
            }
            if (!proprietarioSemantico.equals(regra.getProprietarioSemantico())
                    || !escopo.equals(regra.getEscopo())) {
                throw new IllegalArgumentException("regra fora da projeção do proprietário semântico");
            }
            String identidade = regra.getId() + "@" + regra.getVersao();
            if (identidades.put(identidade, Boolean.TRUE) != null) {
                throw new IllegalArgumentException("regra duplicada na fotografia: " + identidade);
            }
            copia.add(regra);
        }
        return Collections.unmodifiableList(copia);
    }

    private static String textoObrigatorio(String valor, String nome) {
        String normalizado = valor == null ? "" : valor.trim();
        if (normalizado.isEmpty()) {
            throw new IllegalArgumentException(nome + " não pode ser vazio");
        }
        return normalizado;
    }
}
