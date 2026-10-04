#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""Gera situações-problema GENÉRICAS (só para teste) para validar as regras por classe estrutural, não por
situação curada: toda categoria x todo papel desconhecido x pt-BR/en/fr, com números aleatórios coerentes.

As regras da historinha (número relativo/transformação + 3 rejeições consecutivas => historinha, sem aviso;
dados do enunciado imutáveis até o azul; a tradução herda a validação e a decisão do original) valem para
qualquer situação. As situações curadas ficam como amostras de ajuste fino depois que as regras passam aqui.

Os textos são fixtures de teste, nunca conteúdo de interface: o arquivo gerado só é lido por servidores/telas de
teste apontados para uma pasta de usuário isolada (user.home).

Uso: python gerar_situacoes_genericas.py <saida.tsv> [variantes_por_modelo=2] [semente=7]
"""
import random
import sys
from pathlib import Path

CABECALHO = ("# id\tsituacao_grupo_id\ttipo_versao\tversao_origem_id\tvalidada\tidioma\ttipo\tcontexto\tenunciado\t"
             "fonte\tsubtipo\testado_inicial\ttransformacao\tsinal_transformacao\testado_final\tquantidade_1\t"
             "quantidade_2\tresultado\treferido\treferendo\tvalor_relativo\tsinal_valor_relativo\t"
             "termo_desconhecido\trepresentacao_visual\tobservacoes\tpersonagem_1\tpersonagem_2\tpersonagem_3\t"
             "fragmento_texto_1\tfragmento_texto_2\tfragmento_texto_3\tfragmento_texto_4\tfragmento_texto_5\t"
             "fragmento_texto_6\toperacao_relacao\testado_intermediario\toperacao_estado_transformacao\t"
             "estado_inicial_parte1\testado_inicial_parte1_personagem\testado_inicial_parte2\t"
             "estado_inicial_parte2_personagem")
COLUNAS = [c.lstrip("# ").strip() for c in CABECALHO.split("\t")]

OBJ = {"pt-BR": "figurinhas", "en": "stickers", "fr": "images"}
PREF = {"pt-BR": "PO", "en": "IN", "fr": "FR"}
NOMES = {"pt-BR": ("Ana", "Bruno", "Carla"), "en": ("Ana", "Bruno", "Carla"), "fr": ("Ana", "Bruno", "Carla")}

# modelo -> (tipo, termo_desconhecido, textos por idioma, gerador de campos)
MODELOS = []


def modelo(tipo, termo, textos, campos):
    MODELOS.append((tipo, termo, textos, campos))


def distintos(r, quantidade, minimo=2, maximo=30):
    return r.sample(range(minimo, maximo), quantidade)


modelo("COMPOSICAO_MEDIDAS", "todo", {
    "pt-BR": "{A} tem {p1} {o} azuis e {p2} {o} vermelhas. Quantas {o} {A} tem ao todo?",
    "en": "{A} has {p1} blue {o} and {p2} red {o}. How many {o} does {A} have in all?",
    "fr": "{A} a {p1} {o} bleues et {p2} {o} rouges. Combien de {o} {A} a-t-elle en tout ?"},
    lambda r: (lambda p1, p2: dict(p1=p1, p2=p2, f=dict(quantidade_1=p1, quantidade_2=p2, resultado=p1 + p2)))(*distintos(r, 2)))
modelo("COMPOSICAO_MEDIDAS", "quantidade_2", {
    "pt-BR": "{A} tem {todo} {o} no total. {p1} {o} são azuis. Quantas {o} são vermelhas?",
    "en": "{A} has {todo} {o} in total. {p1} {o} are blue. How many {o} are red?",
    "fr": "{A} a {todo} {o} au total. {p1} {o} sont bleues. Combien de {o} sont rouges ?"},
    lambda r: (lambda a, b: dict(p1=a, todo=a + b, f=dict(quantidade_1=a, quantidade_2=b, resultado=a + b)))(*distintos(r, 2)))
modelo("TRANSFORMACAO_MEDIDAS", "estado_final", {
    "pt-BR": "{A} tinha {ei} {o}. Ganhou mais {t} {o}. Quantas {o} {A} tem agora?",
    "en": "{A} had {ei} {o}. {A} got {t} more {o}. How many {o} does {A} have now?",
    "fr": "{A} avait {ei} {o}. {A} a reçu {t} {o} de plus. Combien de {o} {A} a-t-elle maintenant ?"},
    lambda r: (lambda ei, t: dict(ei=ei, t=t, f=dict(estado_inicial=ei, transformacao=t, sinal_transformacao="positivo",
                                                      estado_final=ei + t)))(*distintos(r, 2)))
modelo("TRANSFORMACAO_MEDIDAS", "transformação", {
    "pt-BR": "{A} tinha {ei} {o}. Perdeu algumas {o}. Agora tem {ef} {o}. Quantas {o} {A} perdeu?",
    "en": "{A} had {ei} {o}. {A} lost some {o}. Now {A} has {ef} {o}. How many {o} did {A} lose?",
    "fr": "{A} avait {ei} {o}. {A} en a perdu quelques-unes. Maintenant {A} a {ef} {o}. Combien de {o} {A} a-t-elle perdues ?"},
    lambda r: (lambda ef, d: dict(ei=ef + d, ef=ef, f=dict(estado_inicial=ef + d, transformacao="-%d" % d,
                                                            sinal_transformacao="negativo", estado_final=ef)))(*distintos(r, 2)))
modelo("TRANSFORMACAO_MEDIDAS", "estado_inicial", {
    "pt-BR": "{A} ganhou {t} {o}. Agora tem {ef} {o}. Quantas {o} {A} tinha antes?",
    "en": "{A} got {t} {o}. Now {A} has {ef} {o}. How many {o} did {A} have before?",
    "fr": "{A} a reçu {t} {o}. Maintenant {A} a {ef} {o}. Combien de {o} {A} avait-elle avant ?"},
    lambda r: (lambda t, ei: dict(t=t, ef=ei + t, f=dict(estado_inicial=ei, transformacao=t, sinal_transformacao="positivo",
                                                          estado_final=ei + t)))(*distintos(r, 2)))
modelo("COMPARACAO_MEDIDAS", "referendo", {
    "pt-BR": "{A} tem {rf} {o}. {B} tem {v} {o} a mais que {A}. Quantas {o} {B} tem?",
    "en": "{A} has {rf} {o}. {B} has {v} more {o} than {A}. How many {o} does {B} have?",
    "fr": "{A} a {rf} {o}. {B} a {v} {o} de plus que {A}. Combien de {o} {B} a-t-il ?"},
    lambda r: (lambda rf, v: dict(rf=rf, v=v, f=dict(referido=rf, referendo=rf + v, valor_relativo=v,
                                                     sinal_valor_relativo="positivo")))(*distintos(r, 2)))
modelo("COMPARACAO_MEDIDAS", "valor_relativo", {
    "pt-BR": "{B} tem {rd} {o} e {A} tem {rf} {o}. Quantas {o} a mais {B} tem que {A}?",
    "en": "{B} has {rd} {o} and {A} has {rf} {o}. How many more {o} does {B} have than {A}?",
    "fr": "{B} a {rd} {o} et {A} a {rf} {o}. Combien de {o} de plus {B} a-t-il que {A} ?"},
    lambda r: (lambda rf, v: dict(rf=rf, rd=rf + v, f=dict(referido=rf, referendo=rf + v, valor_relativo=v,
                                                           sinal_valor_relativo="positivo")))(*distintos(r, 2)))
modelo("COMPARACAO_MEDIDAS", "referido", {
    "pt-BR": "{A} tem {rd} {o}. {A} tem {v} {o} a menos que {B}. Quantas {o} {B} tem?",
    "en": "{A} has {rd} {o}. {A} has {v} fewer {o} than {B}. How many {o} does {B} have?",
    "fr": "{A} a {rd} {o}. {A} a {v} {o} de moins que {B}. Combien de {o} {B} a-t-il ?"},
    lambda r: (lambda rd, v: dict(rd=rd, v=v, f=dict(referido=rd + v, referendo=rd, valor_relativo=v,
                                                     sinal_valor_relativo="negativo")))(*distintos(r, 2)))
modelo("COMPOSICAO_TRANSFORMACOES", "transformacao_resultante", {
    "pt-BR": "{A} ganhou {q1} {o} de {B}. Mais tarde {A} ganhou mais {q2} {o} de {C}. Quantas {o} {A} ganhou ao todo?",
    "en": "{A} got {q1} {o} from {B}. Later {A} got {q2} more {o} from {C}. How many {o} did {A} get in all?",
    "fr": "{A} a reçu {q1} {o} de {B}. Plus tard {A} a reçu {q2} {o} de plus de {C}. Combien de {o} {A} a-t-elle reçues en tout ?"},
    lambda r: (lambda a, b: dict(q1=a, q2=b, f=dict(quantidade_1="+%d" % a, quantidade_2="+%d" % b, resultado=str(a + b))))(*distintos(r, 2)))
modelo("COMPOSICAO_TRANSFORMACOES", "transformacao_1", {
    "pt-BR": "{A} jogou duas partidas. Na segunda ganhou {q2} {o}. Ao final das duas ganhou {res} {o}. Quantas {o} ganhou na primeira?",
    "en": "{A} played two games. In the second {A} won {q2} {o}. After both games {A} had won {res} {o}. How many {o} did {A} win in the first?",
    "fr": "{A} a joué deux parties. Dans la deuxième {A} a gagné {q2} {o}. À la fin des deux {A} avait gagné {res} {o}. Combien de {o} {A} a-t-elle gagnées dans la première ?"},
    lambda r: (lambda a, b: dict(q2=b, res=a + b, f=dict(quantidade_1="+%d" % a, quantidade_2="+%d" % b,
                                                         resultado="+%d" % (a + b), operacao_relacao="soma",
                                                         operacao_estado_transformacao="soma")))(*distintos(r, 2)))
modelo("COMPOSICAO_TRANSFORMACOES", "estado_final", {
    "pt-BR": "{A} tem {p1} {o} brancas e {p2} {o} amarelas. Deu {q1} {o} brancas e {q2} {o} amarelas. Com quantas {o} {A} ficou?",
    "en": "{A} has {p1} white {o} and {p2} yellow {o}. {A} gave away {q1} white {o} and {q2} yellow {o}. How many {o} does {A} have left?",
    "fr": "{A} a {p1} {o} blanches et {p2} {o} jaunes. {A} a donné {q1} {o} blanches et {q2} {o} jaunes. Combien de {o} lui reste-t-il ?"},
    lambda r: (lambda p1, p2, q1, q2: dict(p1=p1 + q1, p2=p2 + q2, q1=q1, q2=q2, f=dict(
        estado_inicial=p1 + q1 + p2 + q2, transformacao=str(q1 + q2), sinal_transformacao="negativo",
        estado_final=p1 + p2, quantidade_1=q1, quantidade_2=q2, resultado=q1 + q2,
        estado_inicial_parte1=p2 + q2, estado_inicial_parte1_personagem="amarelas",
        estado_inicial_parte2=p1 + q1, estado_inicial_parte2_personagem="brancas")))(*distintos(r, 4, 2, 12)))
modelo("TRANSFORMACAO_RELACAO", "relacao_final", {
    "pt-BR": "{A} tem {x} {o} a mais que {B}. {B} ganhou mais {t} {o}. Quantas {o} a mais {B} ficou que {A}?",
    "en": "{A} has {x} more {o} than {B}. {B} got {t} more {o}. How many more {o} does {B} have than {A} now?",
    "fr": "{A} a {x} {o} de plus que {B}. {B} a reçu {t} {o} de plus. Combien de {o} de plus {B} a-t-elle que {A} maintenant ?"},
    lambda r: (lambda x, d: dict(x=x, t=x + d, f=dict(estado_inicial="+%d" % x, transformacao=str(x + d),
                                                       sinal_transformacao="positivo", estado_final="+%d" % d,
                                                       operacao_relacao="subtracao")))(*distintos(r, 2)))
modelo("COMPOSICAO_RELACOES", "relacao_resultante", {
    "pt-BR": "{A} tem {a} {o} a mais que {B}. {B} tem {b} {o} a mais que {C}. Quantas {o} a mais {A} tem que {C}?",
    "en": "{A} has {a} more {o} than {B}. {B} has {b} more {o} than {C}. How many more {o} does {A} have than {C}?",
    "fr": "{A} a {a} {o} de plus que {B}. {B} a {b} {o} de plus que {C}. Combien de {o} de plus {A} a-t-elle que {C} ?"},
    lambda r: (lambda a, b: dict(a=a, b=b, f=dict(quantidade_1="+%d" % a, quantidade_2="+%d" % b,
                                                  resultado=str(a + b), operacao_relacao="soma")))(*distintos(r, 2)))


def linha(valores):
    return "\t".join(valores.get(c, "") for c in COLUNAS)


def gerar(saida, variantes, semente):
    r = random.Random(semente)
    linhas = [CABECALHO]
    n = 0
    for tipo, termo, textos, gerador in MODELOS:
        for v in range(variantes):
            n += 1
            numeros = gerador(r)
            campos = numeros.pop("f")
            grupo = "SP_GEN_%03d" % n
            raiz = "PO_GEN_%03d" % n
            for idioma in ("pt-BR", "en", "fr"):
                a, b, c = NOMES[idioma]
                texto = textos[idioma].format(A=a, B=b, C=c, o=OBJ[idioma], **numeros)
                original = idioma == "pt-BR"
                valores = {
                    "id": raiz if original else "%s_GEN_%03d" % (PREF[idioma], n),
                    "situacao_grupo_id": grupo,
                    "tipo_versao": "original" if original else "traducao",
                    "versao_origem_id": "" if original else raiz,
                    "validada": "true" if original else "false",   # a tradução herda a validação do original
                    "idioma": idioma, "tipo": tipo, "contexto": "Generica", "enunciado": texto, "fonte": "teste",
                    "termo_desconhecido": termo, "representacao_visual": tipo,
                    "personagem_1": "figurinhas", "personagem_2": "figurinhas", "personagem_3": "figurinhas",
                }
                valores.update({k: str(x) for k, x in campos.items()})
                linhas.append(linha(valores))
    Path(saida).write_text("\r\n".join(linhas) + "\r\n", encoding="utf-8")
    print("%d situações-modelo -> %d linhas em %s" % (n, len(linhas) - 1, saida))


if __name__ == "__main__":
    gerar(sys.argv[1], int(sys.argv[2]) if len(sys.argv) > 2 else 2, int(sys.argv[3]) if len(sys.argv) > 3 else 7)
