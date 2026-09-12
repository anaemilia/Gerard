import { useEffect, useState } from "react";
import { IconeChat } from "./ChatbotGerard";
import { api } from "./api";
import type { PerfilUsuarioWeb } from "./contratos";

const ROTULOS_MIDIA: Record<string, string> = { SOM: "Som", GRAFICO: "Gráfico",
  LINGUAGEM_NATURAL: "Linguagem natural", VIDEO: "Vídeo", HISTORIA_EM_QUADRINHOS: "História em quadrinhos" };
const VALORES_MIDIA: Record<string, string> = { "Som": "SOM", "Gráfico": "GRAFICO",
  "Linguagem natural": "LINGUAGEM_NATURAL", "Vídeo": "VIDEO", "História em quadrinhos": "HISTORIA_EM_QUADRINHOS" };
const ROTULOS_SEXO: Record<string, string> = { MASCULINO: "Masculino", FEMININO: "Feminino", OUTRO: "Outro" };

type IconeProps = { tipo: "composicao" | "transformacao" | "comparacao" | "relacoes" | "transformacaoRelacao" | "composicaoTransformacoes" };

function IconeCategoria({ tipo }: IconeProps) {
  if (tipo === "composicao") return <svg viewBox="0 0 72 64" aria-hidden="true"><rect x="7" y="8" width="18" height="18"/><rect x="7" y="38" width="18" height="18"/><path d="M30 12c14 7 14 33 0 40"/><rect x="47" y="23" width="18" height="18"/></svg>;
  if (tipo === "transformacao") return <svg viewBox="0 0 72 64" aria-hidden="true"><rect x="5" y="24" width="17" height="17"/><path d="M25 32h30m-9-8 9 8-9 8"/><rect x="55" y="24" width="12" height="17"/><circle cx="36" cy="9" r="8"/></svg>;
  if (tipo === "comparacao") return <svg viewBox="0 0 72 64" aria-hidden="true"><rect x="28" y="4" width="16" height="16"/><path d="M36 20v35m0-30-10 10m10-10 10 10"/><rect x="28" y="44" width="16" height="16"/><circle cx="61" cy="34" r="8"/></svg>;
  // Transformação de relação: mesma estrutura do ícone "transformacao" (estado-seta-estado, círculo acima),
  // com as duas formas de estado também em círculo — ver criarIconeCategoriaTransformacaoRelacao em Main.java.
  if (tipo === "relacoes") return <svg viewBox="0 0 72 64" aria-hidden="true"><circle cx="13" cy="32" r="8"/><path d="M21 32h32m-9-8 9 8-9 8"/><circle cx="61" cy="32" r="8"/><circle cx="36" cy="9" r="8"/></svg>;
  if (tipo === "transformacaoRelacao") return <svg viewBox="0 0 72 64" aria-hidden="true"><circle cx="15" cy="15" r="8"/><circle cx="15" cy="49" r="8"/><path d="M28 10c15 8 15 36 0 44"/><circle cx="51" cy="32" r="8"/></svg>;
  // Composição de transformações: estado→transf.1→estado→transf.2→estado, com um arco por baixo ligando
  // o primeiro e o último estado (a transformação composta) — ver criarIconeCategoriaComposicaoTransformacoes.
  return <svg viewBox="0 0 72 64" aria-hidden="true">
    <rect x="4" y="24" width="12" height="12"/><rect x="30" y="24" width="12" height="12"/><rect x="56" y="24" width="12" height="12"/>
    <circle cx="23" cy="9" r="6"/><circle cx="49" cy="9" r="6"/><circle cx="36" cy="55" r="6"/>
    <path d="M16 30h14m-6-4 6 4-6 4"/>
    <path d="M42 30h14m-6-4 6 4-6 4"/>
    <path d="M10 36c4 18 44 18 48 0m0 0-7-3m7 3-3 6"/>
  </svg>;
}

function BotaoCategoria({ rotulo, tipo, habilitado, selecionado, aoEscolher }: {
  rotulo: string; tipo: IconeProps["tipo"]; habilitado: boolean; selecionado: boolean; aoEscolher: () => void;
}) {
  return <button className="category-button" type="button"
    aria-label={rotulo} title={rotulo} aria-pressed={selecionado} disabled={!habilitado} onClick={aoEscolher}>
    <IconeCategoria tipo={tipo} />
  </button>;
}

function IconeDado() {
  return <svg viewBox="0 0 48 48" aria-hidden="true">
    <rect x="7" y="7" width="34" height="34" rx="4" />
    <circle cx="16" cy="16" r="2.25" />
    <circle cx="32" cy="16" r="2.25" />
    <circle cx="24" cy="24" r="2.25" />
    <circle cx="16" cy="32" r="2.25" />
    <circle cx="32" cy="32" r="2.25" />
  </svg>;
}

function BotaoSortear({ grupo, habilitado, ocupado, aoSortear }: {
  grupo: "Medidas" | "Relações"; habilitado: boolean; ocupado: boolean; aoSortear: () => void;
}) {
  const rotulo = `Sortear ${grupo}`;
  return <span className="draw-button-tooltip" title={rotulo}>
    <button className="draw-button" type="button" disabled={!habilitado || ocupado} onClick={aoSortear}
      aria-label={rotulo}
      aria-description={`Solicita ao servidor uma situação do grupo ${grupo}`}>
      <IconeDado />
    </button>
  </span>;
}

export function BarraCategorias({ podeSortearMedidas, podeSortearRelacoes, ocupado, aoSortearMedidas, aoSortearRelacoes, categoriasHabilitadas, categoriaSelecionada, aoEscolherCategoria, aoAbrirChat, contextoRelatoBug }: {
  podeSortearMedidas: boolean; podeSortearRelacoes: boolean; ocupado: boolean;
  aoSortearMedidas: () => void; aoSortearRelacoes: () => void;
  categoriasHabilitadas: readonly string[]; categoriaSelecionada: string | null;
  aoEscolherCategoria: (categoria: string) => void;
  aoAbrirChat: () => void;
  contextoRelatoBug?: { situacaoId: string; categoria: string; enunciado: string } | null;
}) {
  const [dialogo, setDialogo] = useState<"bug" | "gerard" | "usuario" | null>(null);
  const [relatoBug, setRelatoBug] = useState("");
  const [erroRelatoBug, setErroRelatoBug] = useState("");
  const [enviandoRelatoBug, setEnviandoRelatoBug] = useState(false);
  const [usuarios, setUsuarios] = useState<readonly PerfilUsuarioWeb[]>([]);
  const [usuarioSelecionado, setUsuarioSelecionado] = useState("");
  const [usuarioAtual, setUsuarioAtual] = useState<PerfilUsuarioWeb | null>(null);
  const [nome, setNome] = useState("");
  const [idade, setIdade] = useState(30);
  const [sexo, setSexo] = useState("Masculino");
  const [midia, setMidia] = useState("Som");
  const [escolaridade, setEscolaridade] = useState("GRADUACAO");
  const [foto, setFoto] = useState<string | null>(null);
  const [erroUsuario, setErroUsuario] = useState("");
  const [salvandoUsuario, setSalvandoUsuario] = useState(false);
  useEffect(() => {
    api.consultarSessaoUsuario().then(r => setUsuarioAtual(r.perfil)).catch(() => {});
  }, []);
  useEffect(() => {
    if (dialogo !== "usuario") return;
    if (usuarioAtual) {
      setNome(usuarioAtual.nome); setIdade(usuarioAtual.idade);
      setSexo(ROTULOS_SEXO[usuarioAtual.sexo] ?? "Masculino");
      setMidia(ROTULOS_MIDIA[usuarioAtual.midia_preferida] ?? "Som");
      setEscolaridade(usuarioAtual.nivel_escolaridade); setFoto(null);
    } else {
      api.listarUsuarios().then(r => setUsuarios(r.usuarios)).catch(e => setErroUsuario(e.message));
    }
  }, [dialogo, usuarioAtual]);
  // Mesmo conteúdo/ordem de PreparadorEmailRelatoBug.montarCorpo (Java) —
  // o servidor monta o assunto/corpo reais e devolve a URL do Gmail Web
  // (preferida) e do mailto (contingência), como no desktop.
  const prepararEmailBug = async () => {
    const descricao = relatoBug.trim();
    if (!descricao) {
      setErroRelatoBug("Descreva o problema antes de registrar o relato.");
      return;
    }
    setEnviandoRelatoBug(true); setErroRelatoBug("");
    try {
      const resultado = await api.relatarBug({
        descricao,
        situacao_id: contextoRelatoBug?.situacaoId ?? "",
        categoria: contextoRelatoBug?.categoria ?? "",
        representacoes: "Diagrama de Vergnaud",
        idioma_interface: "PORTUGUES",
        idioma_situacao: "pt-BR",
        enunciado: contextoRelatoBug?.enunciado ?? ""
      });
      window.open(resultado.uri_gmail, "_blank", "noopener,noreferrer");
      setRelatoBug(""); setDialogo(null);
    } catch (erro) {
      setErroRelatoBug(erro instanceof Error ? erro.message : String(erro));
    } finally {
      setEnviandoRelatoBug(false);
    }
  };
  const entrar = async (id: string) => {
    setSalvandoUsuario(true); setErroUsuario("");
    try {
      await api.entrarUsuario(id);
      setUsuarioSelecionado(id);
      setUsuarioAtual(usuarios.find(u => u.id === id) ?? null);
      setDialogo(null);
    }
    catch (erro) { setErroUsuario(erro instanceof Error ? erro.message : String(erro)); }
    finally { setSalvandoUsuario(false); }
  };
  const cadastrarEEntrar = async () => {
    const nomeLimpo = nome.trim();
    if (!nomeLimpo) return;
    setSalvandoUsuario(true); setErroUsuario("");
    try {
      const perfil = await api.cadastrarUsuario({ nome:nomeLimpo, idade, sexo:sexo.toUpperCase(),
        midia_preferida:VALORES_MIDIA[midia], nivel_escolaridade:escolaridade, foto_data_url:foto });
      setUsuarios(atuais => [...atuais, perfil]);
      await api.entrarUsuario(perfil.id);
      setUsuarioAtual(perfil);
      setDialogo(null);
    } catch (erro) { setErroUsuario(erro instanceof Error ? erro.message : String(erro)); }
    finally { setSalvandoUsuario(false); }
  };
  const salvarEdicao = async () => {
    const nomeLimpo = nome.trim();
    if (!nomeLimpo || !usuarioAtual) return;
    setSalvandoUsuario(true); setErroUsuario("");
    try {
      const perfil = await api.atualizarUsuario({ usuario_id: usuarioAtual.id, nome:nomeLimpo, idade,
        sexo:sexo.toUpperCase(), midia_preferida:VALORES_MIDIA[midia],
        nivel_escolaridade:escolaridade, foto_data_url:foto });
      setUsuarioAtual(perfil);
      setDialogo(null);
    } catch (erro) { setErroUsuario(erro instanceof Error ? erro.message : String(erro)); }
    finally { setSalvandoUsuario(false); }
  };

  return <header className="top-area">
    <div className="utility-row">
      <div className="utility-left">
        <a href="https://github.com/anaemilia/Gerard/discussions" target="_blank" rel="noreferrer" aria-label="Abrir a comunidade do Gérard (GitHub Discussions)" title="Abrir a comunidade do Gérard (GitHub Discussions)"><svg viewBox="0 0 24 24"><circle cx="8" cy="7" r="3"/><circle cx="16" cy="9" r="3"/><path d="M2 19c1-5 11-5 12 0M11 19c1-4 9-3 10 0"/></svg></a>
        <a href="https://anaemilia.github.io/Gerard/" target="_blank" rel="noreferrer" aria-label="Abrir a página do Gérard" title="Abrir a página do Gérard"><svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="9"/><path d="M3 12h18M12 3c4 4 4 14 0 18M12 3c-4 4-4 14 0 18"/></svg></a>
        <button type="button" onClick={() => setDialogo("bug")} aria-label="Informar um problema encontrado no Gérard" title="Informar um problema encontrado no Gérard"><svg viewBox="0 0 24 24"><circle cx="12" cy="7" r="3"/><rect x="7" y="10" width="10" height="9" rx="4"/><path d="M4 11h3M17 11h3M4 16h3M17 16h3M8 5 6 3M16 5l2-2"/></svg></button>
        <button type="button" onClick={() => setDialogo("gerard")} className="gerard-info" aria-label="Gérard Vergnaud" title="Gérard Vergnaud"><img src="/gerard_vergnaud.png" alt="" /></button>
        <button type="button" className={usuarioAtual ? "usuario-trigger-logado" : undefined}
            onClick={() => setDialogo("usuario")} aria-label="Selecionar ou cadastrar usuário"
            title="Selecionar ou cadastrar usuário">
          {usuarioAtual
            ? <span>{usuarioAtual.nome}  ▼</span>
            : <svg viewBox="0 0 32 32"><circle cx="16" cy="16" r="12"/><path d="M9 16h13m-5-5 5 5-5 5"/></svg>}
        </button>
      </div>
      <div className="utility-right"><button className="chat-trigger" type="button" onClick={aoAbrirChat} aria-label="Abrir o chat de ajuda" title="Abrir o chat de ajuda"><IconeChat /></button><span className="language">Português</span></div>
    </div>
    <nav className="category-navigation" aria-label="Categorias de situações aditivas">
      <div className="category-group"><strong>Medidas</strong><div className="category-buttons">
        <BotaoCategoria rotulo="Composição de medidas" tipo="composicao" habilitado={categoriasHabilitadas.includes("COMPOSICAO_MEDIDAS") && !ocupado} selecionado={categoriaSelecionada === "COMPOSICAO_MEDIDAS"} aoEscolher={() => aoEscolherCategoria("COMPOSICAO_MEDIDAS")} />
        <BotaoCategoria rotulo="Transformação de medidas" tipo="transformacao" habilitado={categoriasHabilitadas.includes("TRANSFORMACAO_MEDIDAS") && !ocupado} selecionado={categoriaSelecionada === "TRANSFORMACAO_MEDIDAS"} aoEscolher={() => aoEscolherCategoria("TRANSFORMACAO_MEDIDAS")} />
        <BotaoCategoria rotulo="Comparação de medidas" tipo="comparacao" habilitado={categoriasHabilitadas.includes("COMPARACAO_MEDIDAS") && !ocupado} selecionado={categoriaSelecionada === "COMPARACAO_MEDIDAS"} aoEscolher={() => aoEscolherCategoria("COMPARACAO_MEDIDAS")} />
      </div></div>
      <BotaoSortear grupo="Medidas" habilitado={podeSortearMedidas} ocupado={ocupado} aoSortear={aoSortearMedidas} />
      <span className="category-divider" aria-hidden="true" />
      <BotaoSortear grupo="Relações" habilitado={podeSortearRelacoes} ocupado={ocupado} aoSortear={aoSortearRelacoes} />
      <div className="category-group"><strong>Relações</strong><div className="category-buttons">
        <BotaoCategoria rotulo="Composição de transformações" tipo="composicaoTransformacoes" habilitado={categoriasHabilitadas.includes("COMPOSICAO_TRANSFORMACOES") && !ocupado} selecionado={categoriaSelecionada === "COMPOSICAO_TRANSFORMACOES"} aoEscolher={() => aoEscolherCategoria("COMPOSICAO_TRANSFORMACOES")} />
        <BotaoCategoria rotulo="Transformação de relação" tipo="relacoes" habilitado={categoriasHabilitadas.includes("TRANSFORMACAO_RELACAO") && !ocupado} selecionado={categoriaSelecionada === "TRANSFORMACAO_RELACAO"} aoEscolher={() => aoEscolherCategoria("TRANSFORMACAO_RELACAO")} />
        <BotaoCategoria rotulo="Composição de relações" tipo="transformacaoRelacao" habilitado={categoriasHabilitadas.includes("COMPOSICAO_RELACOES") && !ocupado} selecionado={categoriaSelecionada === "COMPOSICAO_RELACOES"} aoEscolher={() => aoEscolherCategoria("COMPOSICAO_RELACOES")} />
      </div></div>
    </nav>
    {dialogo === "bug" && <div className="utility-modal-backdrop" role="presentation" onMouseDown={() => setDialogo(null)}>
      <section className="utility-dialog bug-dialog" role="dialog" aria-modal="true" aria-labelledby="bug-title" onMouseDown={e => e.stopPropagation()}>
        <header><h2 id="bug-title">Reportar problema</h2><button type="button" onClick={() => setDialogo(null)} aria-label="Fechar">×</button></header>
        <p>Descreva o que você estava fazendo quando o problema ocorreu. Após confirmar, o Gmail será aberto no navegador com destinatário, assunto e contexto preenchidos.</p>
        <label htmlFor="relato-bug">Descrição do problema</label>
        <textarea id="relato-bug" value={relatoBug} onChange={e => setRelatoBug(e.target.value)} autoFocus />
        {erroRelatoBug && <p className="message message-erro" role="alert">{erroRelatoBug}</p>}
        <div className="utility-dialog-actions"><button type="button" onClick={() => setDialogo(null)}>Cancelar</button><button type="button" disabled={enviandoRelatoBug} onClick={() => void prepararEmailBug()}>Preparar e-mail</button></div>
      </section>
    </div>}
    {dialogo === "gerard" && <div className="utility-modal-backdrop" role="presentation" onMouseDown={() => setDialogo(null)}>
      <section className="utility-dialog gerard-dialog" role="dialog" aria-modal="true" aria-labelledby="gerard-title" onMouseDown={e => e.stopPropagation()}>
        <header><h2 id="gerard-title">Gérard Vergnaud</h2><button type="button" onClick={() => setDialogo(null)} aria-label="Fechar">×</button></header>
        <img className="gerard-main-photo" src="/gerard_vergnaud.png" alt="Gérard Vergnaud" />
        <p>Gérard Vergnaud (1933–2021), referência teórica das estruturas aditivas.</p>
        <div className="gerard-links"><a href="https://pt.wikipedia.org/wiki/G%C3%A9rard_Vergnaud" target="_blank" rel="noreferrer">Abrir página na Wikipédia</a><a href="https://www.youtube.com/watch?v=pU7um4GX5XQ" target="_blank" rel="noreferrer">Abrir vídeo no YouTube</a><a href="https://vergnaudbrasil.com/" target="_blank" rel="noreferrer">Abrir site Vergnaud Brasil</a></div>
        <h3>Na UFPE em 2009</h3>
        <p>Registro em Recife.</p>
        <div className="recife-photos"><img src="/em_recife_rostos.png" alt="Gérard Vergnaud na UFPE"/><img src="/em_recife_adicional.png" alt="Gérard Vergnaud com participantes na UFPE"/></div>
      </section>
    </div>}
    {dialogo === "usuario" && usuarioAtual && <div className="utility-modal-backdrop" role="presentation" onMouseDown={() => setDialogo(null)}>
      <section className="utility-dialog usuario-dialog" role="dialog" aria-modal="true" aria-labelledby="usuario-title" onMouseDown={e => e.stopPropagation()}>
        <header><h2 id="usuario-title">Meus dados</h2><button type="button" onClick={() => setDialogo(null)} aria-label="Fechar">×</button></header>
        <p>Edite seus dados cadastrados.</p>
        <section className="usuario-cadastro">
          <div className="usuario-nome-foto"><label>Nome<input value={nome} onChange={e => setNome(e.target.value)} /></label><label className="usuario-foto"><span className="foto-preview">{foto ? <img src={foto} alt="Foto escolhida" /> : "Foto"}</span><span className="foto-escolher">Escolher foto…</span><input type="file" accept="image/*" onChange={e => { const arquivo=e.target.files?.[0]; if (arquivo) { const leitor=new FileReader(); leitor.onload=() => setFoto(String(leitor.result)); leitor.readAsDataURL(arquivo); } }} /></label></div>
          <label>Idade<input type="number" min="1" max="120" value={idade} onChange={e => setIdade(Number(e.target.value))} /></label>
          <label>Sexo<select value={sexo} onChange={e => setSexo(e.target.value)}><option>Masculino</option><option>Feminino</option><option>Outro</option></select></label>
          <fieldset><legend>Mídia preferida</legend>{["Som","Gráfico","Linguagem natural","Vídeo","História em quadrinhos"].map(opcao => <label key={opcao}><input type="radio" name="midia" value={opcao} checked={midia === opcao} onChange={e => setMidia(e.target.value)} />{opcao}</label>)}</fieldset>
          <label>Nível de escolaridade<select value={escolaridade} onChange={e => setEscolaridade(e.target.value)}><option value="PRIMEIRO_GRAU">1º grau</option><option value="SEGUNDO_GRAU">2º grau</option><option value="GRADUACAO">Graduação</option><option value="POS_GRADUACAO">Pós-graduação</option></select></label>
          <button className="usuario-acao" type="button" disabled={!nome.trim() || salvandoUsuario} onClick={salvarEdicao}>Salvar</button>
        </section>
        {erroUsuario && <p className="message message-erro" role="alert">{erroUsuario}</p>}
        <div className="utility-dialog-actions"><button type="button" onClick={() => setDialogo(null)}>Cancelar</button></div>
      </section>
    </div>}
    {dialogo === "usuario" && !usuarioAtual && <div className="utility-modal-backdrop" role="presentation" onMouseDown={() => setDialogo(null)}>
      <section className="utility-dialog usuario-dialog" role="dialog" aria-modal="true" aria-labelledby="usuario-title" onMouseDown={e => e.stopPropagation()}>
        <header><h2 id="usuario-title">Usuário</h2><button type="button" onClick={() => setDialogo(null)} aria-label="Fechar">×</button></header>
        <p>Quem está usando o Gérard agora?</p>
        <div className="usuario-layout">
          <section className="usuario-cadastrados"><h3>Usuários cadastrados</h3>
            <div className="usuario-lista" role="listbox" aria-label="Usuários cadastrados">{usuarios.map(usuario => <button key={usuario.id} type="button" role="option" aria-selected={usuarioSelecionado === usuario.id} onClick={() => setUsuarioSelecionado(usuario.id)}>{usuario.nome}</button>)}</div>
            <button className="usuario-acao" type="button" disabled={!usuarioSelecionado || salvandoUsuario} onClick={() => entrar(usuarioSelecionado)}>Entrar</button>
          </section>
          <section className="usuario-cadastro"><h3>Cadastrar novo usuário</h3>
            <div className="usuario-nome-foto"><label>Nome<input value={nome} onChange={e => setNome(e.target.value)} /></label><label className="usuario-foto"><span className="foto-preview">{foto ? <img src={foto} alt="Foto escolhida" /> : "Foto"}</span><span className="foto-escolher">Escolher foto…</span><input type="file" accept="image/*" onChange={e => { const arquivo=e.target.files?.[0]; if (arquivo) { const leitor=new FileReader(); leitor.onload=() => setFoto(String(leitor.result)); leitor.readAsDataURL(arquivo); } }} /></label></div>
            <label>Idade<input type="number" min="1" max="120" value={idade} onChange={e => setIdade(Number(e.target.value))} /></label>
            <label>Sexo<select value={sexo} onChange={e => setSexo(e.target.value)}><option>Masculino</option><option>Feminino</option><option>Outro</option></select></label>
            <fieldset><legend>Mídia preferida</legend>{["Som","Gráfico","Linguagem natural","Vídeo","História em quadrinhos"].map(opcao => <label key={opcao}><input type="radio" name="midia" value={opcao} checked={midia === opcao} onChange={e => setMidia(e.target.value)} />{opcao}</label>)}</fieldset>
            <label>Nível de escolaridade<select value={escolaridade} onChange={e => setEscolaridade(e.target.value)}><option value="PRIMEIRO_GRAU">1º grau</option><option value="SEGUNDO_GRAU">2º grau</option><option value="GRADUACAO">Graduação</option><option value="POS_GRADUACAO">Pós-graduação</option></select></label>
            <button className="usuario-acao" type="button" disabled={!nome.trim() || salvandoUsuario} onClick={cadastrarEEntrar}>Cadastrar e entrar</button>
          </section>
        </div>
        {erroUsuario && <p className="message message-erro" role="alert">{erroUsuario}</p>}
        <div className="utility-dialog-actions"><button type="button" onClick={() => setDialogo(null)}>Cancelar</button></div>
      </section>
    </div>}
  </header>;
}
