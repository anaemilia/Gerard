#!/usr/bin/env python3
"""
Checagem estrutural/textual (não é um teste unitário Java): confirma
vínculos entre situações-problema e suas traduções a partir dos arquivos
de dados (.tsv), sem executar a interface Java.
"""
from pathlib import Path
from collections import defaultdict
import sys
ROOT=Path(__file__).resolve().parents[1]
ARQ=ROOT/'src/gerard/campoaditivo/dados/situacoes_vergnaud.tsv'
with ARQ.open(encoding='utf-8') as f:
    cab=f.readline().lstrip('# ').rstrip('\n').split('\t')
    rows=[dict(zip(cab,l.rstrip('\n').split('\t'))) for l in f if l.strip() and not l.startswith('#')]
required={'id','situacao_grupo_id','tipo_versao','versao_origem_id','idioma'}
assert required.issubset(cab), 'colunas relacionais ausentes'
ids={r['id']:r for r in rows}
groups=defaultdict(list)
for r in rows: groups[r['situacao_grupo_id']].append(r)
concept=['tipo','subtipo','estado_inicial','transformacao','sinal_transformacao','estado_final','quantidade_1','quantidade_2','resultado','referido','referendo','valor_relativo','sinal_valor_relativo','termo_desconhecido','representacao_visual']
errors=[]
for gid,rs in groups.items():
    originals=[r for r in rs if r['tipo_versao']=='original']
    if len(originals)!=1: errors.append(f'{gid}: originais={len(originals)}'); continue
    langs=[r['idioma'] for r in rs]
    if len(langs)!=len(set(langs)): errors.append(f'{gid}: idioma duplicado')
    ref=originals[0]
    for r in rs:
        if r['tipo_versao']=='traducao':
            o=ids.get(r['versao_origem_id'])
            if not o or o['situacao_grupo_id']!=gid or o['tipo_versao']!='original': errors.append(f"{r['id']}: origem inválida")
        elif r['versao_origem_id'].strip(): errors.append(f"{r['id']}: original com origem")
        for c in concept:
            norm=lambda x:x.strip().lower().replace(',','.')
            if norm(r[c])!=norm(ref[c]): errors.append(f"{gid}: {c} diverge em {r['id']}")
if errors:
    print('\n'.join(errors[:30])); sys.exit(1)
print(f'OK: {len(rows)} versões, {len(groups)} grupos conceituais, vínculos e metadados coerentes.')
