"use strict";

const SCHEMA = "gerard.atividade-web.estado.v1";
let estadoAtual;

document.addEventListener("DOMContentLoaded", async () => {
  document.querySelector("#form-resposta").addEventListener("submit", submeter);
  document.querySelector("#reiniciar").addEventListener("click", reiniciar);
  await carregar();
});

async function carregar() {
  try {
    apresentar(await requisitar("/api/situacao"));
  } catch (erro) {
    mensagem(erro.message, "erro");
  }
}

async function submeter(evento) {
  evento.preventDefault();
  const campo = document.querySelector("#valor-todo");
  const valor = Number(campo.value);
  if (!Number.isInteger(valor) || valor < 0) {
    mensagem("Informe um número natural.", "erro");
    return;
  }
  bloquear(true);
  try {
    const resultado = await requisitar("/api/acoes/posicionar", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ papel_id: "papel.todo", valor })
    });
    apresentar(resultado.estado);
    mensagem(resultado.aceita
      ? `Resposta aceita pelo domínio. Ação ${resultado.action_id}.`
      : `Resposta rejeitada pelo domínio: ${rotuloDiagnostico(resultado.diagnostico)}.`,
    resultado.aceita ? "sucesso" : "erro");
    if (!resultado.aceita) campo.select();
  } catch (erro) {
    mensagem(erro.message, "erro");
  } finally {
    bloquear(Boolean(estadoAtual && estadoAtual.concluida));
  }
}

async function reiniciar() {
  try {
    apresentar(await requisitar("/api/reiniciar", { method: "POST" }));
    document.querySelector("#valor-todo").value = "";
    mensagem("Tentativa reiniciada.", "neutra");
  } catch (erro) {
    mensagem(erro.message, "erro");
  }
}

async function requisitar(url, opcoes) {
  const resposta = await fetch(url, opcoes);
  const corpo = await resposta.json();
  if (!resposta.ok) throw new Error(corpo.erro || `Falha HTTP ${resposta.status}`);
  return corpo;
}

function apresentar(estado) {
  if (!estado || estado.schema !== SCHEMA) throw new Error("Contrato de estado não suportado.");
  estadoAtual = estado;
  document.querySelector("#enunciado").textContent = estado.enunciado;
  document.querySelector("#categoria").textContent = estado.categoria;
  document.querySelector("#relacao").textContent = estado.relacao;
  document.querySelector("#tentativa").textContent = estado.tentativa_id;
  const status = document.querySelector("#estado-atividade");
  status.textContent = estado.concluida ? "Concluída" : "Em andamento";
  status.className = `status ${estado.concluida ? "status-success" : ""}`;
  renderizar(estado);
  bloquear(Boolean(estado.concluida));
}

function renderizar(estado) {
  const svg = svgEl("svg", { viewBox: "0 0 840 390", role: "img", "aria-labelledby": "titulo-diagrama" });
  svg.appendChild(svgEl("path", {
    d: "M270 145 C355 145 350 225 445 225 M270 305 C355 305 350 225 445 225 M445 225 L565 225",
    class: "connector", "aria-hidden": "true"
  }));
  [[estado.parte1,160,105,false], [estado.parte2,160,265,false], [estado.todo,650,185,true]]
    .forEach(([papel,x,y,incognita]) => svg.appendChild(criarPapel(papel,x,y,incognita)));
  document.querySelector("#diagrama").replaceChildren(svg);
}

function criarPapel(papel, x, y, incognita) {
  const grupo = svgEl("g", { transform: `translate(${x} ${y})`, role: "group",
    "aria-label": `${papel.nome}: ${papel.conhecido ? papel.valor : "desconhecido"}` });
  grupo.appendChild(svgEl("rect", { x:-110, y:-54, width:220, height:108, rx:18,
    class:`shape ${incognita ? "unknown-shape" : ""}` }));
  grupo.appendChild(textoSvg(0,-12,papel.nome,"role-name"));
  grupo.appendChild(textoSvg(0,27,papel.conhecido ? String(papel.valor) : "?","role-value"));
  return grupo;
}

function svgEl(nome, atributos) {
  const elemento = document.createElementNS("http://www.w3.org/2000/svg", nome);
  Object.entries(atributos).forEach(([chave,valor]) => elemento.setAttribute(chave,valor));
  return elemento;
}

function textoSvg(x,y,conteudo,classe) {
  const texto = svgEl("text", { x,y,class:classe,"text-anchor":"middle" });
  texto.textContent = conteudo;
  return texto;
}

function bloquear(valor) {
  document.querySelector("#valor-todo").disabled = valor;
  document.querySelector("#confirmar").disabled = valor;
}

function mensagem(texto, tipo) {
  const alvo = document.querySelector("#mensagem");
  alvo.textContent = texto;
  alvo.className = `message message-${tipo}`;
}

function rotuloDiagnostico(tipo) {
  return ({
    VALOR_INCORRETO: "o valor não satisfaz Todo = Parte1 + Parte2",
    OPERACAO_INVERTIDA: "a operação parece ter sido invertida",
    VALOR_FORA_DO_DOMINIO: "o valor não pertence ao domínio do papel"
  })[tipo] || tipo || "proposta inválida";
}
