#!/usr/bin/env python3
"""
Checagem estrutural/textual (não é um teste unitário Java): confirma que
os arquivos de configuração de idioma contêm as chaves esperadas, lendo
o texto-fonte diretamente. Não executa a interface nem valida
comportamento em tempo de execução.
"""
from pathlib import Path
root = Path(__file__).resolve().parents[1]
idioma_interface = (root/'src/gerard/idioma/IdiomaInterface.java').read_text(encoding='utf-8')
idioma_situacao = (root/'src/gerard/idioma/IdiomaSituacao.java').read_text(encoding='utf-8')
cadastro = (root/'src/gerard/idioma/CadastroIdiomasSituacao.java').read_text(encoding='utf-8')
simbolo = (root/'src/gerard/interpretacao/simbolo/SimboloDesconhecido.java').read_text(encoding='utf-8')
curadoria = (root/'src/gerard/campoaditivo/curadoria/TelaCuradoriaSituacoes.java').read_text(encoding='utf-8')

checks = {
    'quatro idiomas na interface': all(x in idioma_interface for x in ['PORTUGUES', 'INGLES', 'FRANCES', 'ESPANHOL']),
    'catálogo fechado': 'Catálogo local e fechado' in cadastro,
    'somente quatro idiomas permitidos': 'português, inglês, francês e espanhol' in cadastro,
    'orientação LTR única': 'DIRECAO_PADRAO = "LTR"' in idioma_situacao and 'RIGHT_TO_LEFT' not in (root/'src/gerard/idioma/UnicodeTexto.java').read_text(encoding='utf-8'),
    'script latino único': 'SCRIPT_PADRAO = "Latn"' in idioma_situacao,
    'símbolo desconhecido canônico': 'CANONICO = "?"' in simbolo and '？' not in simbolo and '؟' not in simbolo,
    'cadastro de novos idiomas removido da curadoria': 'cadastrarNovoIdioma' not in curadoria,
    'persistência continua usando código de idioma': 'getCodigoIdioma' in (root/'src/gerard/campoaditivo/modelo/SituacaoProblemaAditiva.java').read_text(encoding='utf-8'),
}
for k, v in checks.items():
    print(('[OK] ' if v else '[FALHA] ') + k)
if not all(checks.values()):
    raise SystemExit(1)
print('OK: escopo linguístico controlado em português, inglês, francês e espanhol.')
