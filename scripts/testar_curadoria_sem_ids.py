from pathlib import Path

arquivo = Path(__file__).resolve().parents[1] / "src/gerard/campoaditivo/curadoria/TelaCuradoriaSituacoes.java"
texto = arquivo.read_text(encoding="utf-8")
rotulos_proibidos = [
    'adicionarCampo(formulario, gbc, y, "id da versão"',
    'adicionarCampo(formulario, gbc, y, "situação_grupo_id"',
    'adicionarCampo(formulario, gbc, y, "versão_origem_id"',
]
for rotulo in rotulos_proibidos:
    if rotulo in texto:
        raise SystemExit("Falha: identificador técnico ainda exposto na curadoria: " + rotulo)

# Os campos internos devem continuar presentes para preservar vínculos e persistência.
campos_internos = ["campoId", "campoSituacaoGrupoId", "campoVersaoOrigemId"]
for campo in campos_internos:
    if campo not in texto:
        raise SystemExit("Falha: campo interno necessário foi removido do modelo: " + campo)

print("OK: identificadores técnicos ocultos da tela e preservados internamente.")
