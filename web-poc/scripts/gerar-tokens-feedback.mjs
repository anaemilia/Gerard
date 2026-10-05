// Gera web-poc/tokens-feedback.css a partir da FONTE ÚNICA das cores de feedback
// (src/gerard/recursos/tema/feedback.properties, a mesma que o Swing lê). Não edite o CSS gerado.
import { existsSync, readFileSync, writeFileSync } from "node:fs";
import { dirname, resolve } from "node:path";
import { fileURLToPath } from "node:url";

const aqui = dirname(fileURLToPath(import.meta.url));
const candidatos = [
  resolve(aqui, "../../src/gerard/recursos/tema/feedback.properties"), // repositório
  resolve(aqui, "../tema-fonte/feedback.properties"),                 // build do Docker
];
const fonte = candidatos.find(existsSync);
if (!fonte) throw new Error("feedback.properties não encontrado");
const cores = Object.fromEntries(readFileSync(fonte, "utf8").split(/\r?\n/)
  .filter((l) => l.trim() && !l.startsWith("#")).map((l) => l.split("=").map((s) => s.trim())));
for (const k of ["erro", "erro_fundo", "erro_texto", "erro_claro"]) {
  if (!/^[0-9a-fA-F]{6}$/.test(cores[k] ?? "")) throw new Error(`cor inválida/ausente: ${k}`);
}
writeFileSync(resolve(aqui, "../tokens-feedback.css"),
  `/* GERADO por scripts/gerar-tokens-feedback.mjs a partir de feedback.properties. Não edite. */\n` +
  `:root { --erro:#${cores.erro}; --erro-fundo:#${cores.erro_fundo}; --erro-texto:#${cores.erro_texto}; --erro-claro:#${cores.erro_claro}; }\n`);
