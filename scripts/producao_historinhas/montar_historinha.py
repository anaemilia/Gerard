#!/usr/bin/env python3
"""Monta uma historinha animada (GIF + storyboard) a partir de uma folha ilustrada e de uma especificação.

Mesmo processo das historinhas existentes: a folha traz os quadros ilustrados SEM texto; título,
legendas, equação e conclusão são acrescentados aqui, de forma determinística, a partir da
especificação (que deriva da situação curada). Uso:
    python montar_historinha.py especificacao.json
"""
from __future__ import annotations

import json
import sys
from pathlib import Path

from PIL import Image, ImageDraw, ImageEnhance, ImageFilter, ImageFont

LARGURA, ALTURA, FPS = 960, 540, 8
LARGURA_SAIDA, ALTURA_SAIDA = 640, 360  # tamanho das historinhas já integradas ao Gérard
DURACAO_CENA, TRANSICAO = 2.4, 0.38
DURACAO_FINAL = 2.5
COR_TEXTO = (51, 46, 40)
COR_TEXTO_CLARO = (252, 251, 248)
COR_SUCESSO_TEXTO = (23, 63, 150)
COR_FUNDO = (247, 246, 241)


def fonte(tamanho: int, negrito: bool = False) -> ImageFont.FreeTypeFont:
    candidatos = (
        r"C:\Windows\Fonts\arialbd.ttf" if negrito else r"C:\Windows\Fonts\arial.ttf",
        "/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf" if negrito
        else "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf",
    )
    for caminho in candidatos:
        if Path(caminho).exists():
            return ImageFont.truetype(caminho, tamanho)
    return ImageFont.load_default()


FONTES = {"titulo": fonte(34, True), "legenda": fonte(28, True), "final": fonte(29, True),
          "equacao": fonte(48, True), "conclusao": fonte(27, True)}


def limitar(v, a=0.0, b=1.0):
    return max(a, min(b, v))


def suavizar(v):
    v = limitar(v)
    return v * v * (3.0 - 2.0 * v)


def alpha_fade(t, d):
    return min(suavizar(t / 0.35), suavizar((d - t) / 0.35))


def linhas_texto(d, texto, fnt, largura):
    linhas, atual = [], ""
    for p in texto.split():
        c = p if not atual else atual + " " + p
        b = d.textbbox((0, 0), c, font=fnt)
        if atual and b[2] - b[0] > largura:
            linhas.append(atual)
            atual = p
        else:
            atual = c
    return linhas + ([atual] if atual else [])


def texto_centralizado(img, texto, fnt, y_centro, largura, cor, contorno=0, cor_contorno=(0, 0, 0, 180)):
    d = ImageDraw.Draw(img)
    linhas = linhas_texto(d, texto, fnt, largura)
    caixas = [d.textbbox((0, 0), l, font=fnt, stroke_width=contorno) for l in linhas]
    alturas = [b[3] - b[1] for b in caixas]
    y = y_centro - (sum(alturas) + 5 * max(0, len(linhas) - 1)) // 2
    for l, b, h in zip(linhas, caixas, alturas):
        d.text(((LARGURA - (b[2] - b[0])) // 2, y), l, font=fnt, fill=cor,
               stroke_width=contorno, stroke_fill=cor_contorno)
        y += h + 5


def recortar_paineis(folha: Image.Image, colunas: int, linhas: int):
    """Caixas da grade colunas x linhas, descontando a margem das linhas de separação."""
    w, h = folha.size
    caixas = []
    for r in range(linhas):
        for c in range(colunas):
            x0, x1 = round(c * w / colunas), round((c + 1) * w / colunas)
            y0, y1 = round(r * h / linhas), round((r + 1) * h / linhas)
            caixas.append((x0 + 8, y0 + 8, x1 - 8, y1 - 8))
    return caixas


def enquadrar(painel, foco, progresso, direcao, largura_minima=0.0):
    altura_crop = painel.height
    largura_crop = min(max(int(round(altura_crop * LARGURA / ALTURA)),
                           int(round(limitar(largura_minima) * painel.width))), painel.width)
    centro = foco * painel.width + direcao * (progresso - 0.5) * painel.width * 0.025
    x0 = max(0, min(painel.width - largura_crop, int(round(centro - largura_crop / 2))))
    corte = painel.crop((x0, 0, x0 + largura_crop, altura_crop))
    if largura_minima <= 0:
        return corte.resize((LARGURA, ALTURA), Image.Resampling.LANCZOS)
    # Faixas panorâmicas: preservar o recorte editorial inteiro sem esticar
    # nem excluir um participante. O fundo só preenche a área 16:9 restante.
    fundo = corte.resize((LARGURA, ALTURA), Image.Resampling.LANCZOS).filter(
        ImageFilter.GaussianBlur(radius=18))
    fator = min(LARGURA / corte.width, ALTURA / corte.height)
    frente = corte.resize((round(corte.width * fator), round(corte.height * fator)),
                          Image.Resampling.LANCZOS)
    fundo.paste(frente, ((LARGURA - frente.width) // 2, (ALTURA - frente.height) // 2))
    return fundo


def gradiente(img, opacidade):
    camada = Image.new("RGBA", img.size, (0, 0, 0, 0))
    px = camada.load()
    for y in range(350, ALTURA):
        a = int(190 * ((y - 350) / (ALTURA - 350)) * opacidade)
        for x in range(LARGURA):
            px[x, y] = (38, 33, 28, a)
    img.alpha_composite(camada)


def cena(painel, titulo, legenda, prog, foco, direcao, op, largura_minima=0.0):
    q = enquadrar(painel, foco, prog, direcao, largura_minima).convert("RGBA")
    gradiente(q, op)
    camada = Image.new("RGBA", q.size, (0, 0, 0, 0))
    ImageDraw.Draw(camada).text((34, 27), titulo, font=FONTES["titulo"],
                                fill=COR_TEXTO_CLARO + (int(235 * op),),
                                stroke_width=2, stroke_fill=(42, 36, 30, int(170 * op)))
    q.alpha_composite(camada)
    texto_centralizado(q, legenda, FONTES["legenda"], 475, 860, COR_TEXTO_CLARO + (int(235 * op),),
                       contorno=2, cor_contorno=(35, 30, 25, int(180 * op)))
    return q.convert("RGB")


def cena_final(painel, esp, prog):
    fundo = enquadrar(painel, esp["focos"][3], 1.0, 1,
                     esp.get("larguras_recorte", [0.0] * 4)[3]).filter(ImageFilter.GaussianBlur(radius=2.2))
    fundo = ImageEnhance.Brightness(fundo).enhance(0.62).convert("RGBA")
    e = suavizar(prog / 0.7)
    fundo.alpha_composite(Image.new("RGBA", fundo.size, (247, 246, 241, int(198 * e))))
    a = int(255 * e)
    texto_centralizado(fundo, esp["titulo_final"], FONTES["final"], 112, 860, COR_TEXTO + (a,))
    texto_centralizado(fundo, esp["equacao"], FONTES["equacao"], 250, 880, COR_SUCESSO_TEXTO + (a,))
    texto_centralizado(fundo, esp["conclusao"], FONTES["conclusao"], 352, 850, COR_TEXTO + (a,))
    return fundo.convert("RGB")


def quadro(esp, paineis, t):
    fim = DURACAO_CENA * 4
    if t >= fim:
        return cena_final(paineis[3], esp, t - fim)
    i = min(3, int(t / DURACAO_CENA))
    local = t - i * DURACAO_CENA
    atual = cena(paineis[i], esp["titulo"], esp["legendas"][i], local / DURACAO_CENA,
                 esp["focos"][i], 1 if i % 2 == 0 else -1, alpha_fade(local, DURACAO_CENA),
                 esp.get("larguras_recorte", [0.0] * 4)[i])
    if local > DURACAO_CENA - TRANSICAO and i < 3:
        p = suavizar((local - (DURACAO_CENA - TRANSICAO)) / TRANSICAO)
        prox = cena(paineis[i + 1], esp["titulo"], esp["legendas"][i + 1], 0.0, esp["focos"][i + 1],
                    1 if (i + 1) % 2 == 0 else -1, p,
                    esp.get("larguras_recorte", [0.0] * 4)[i + 1])
        return Image.blend(atual, prox, p)
    return atual


def montar(caminho_esp: Path) -> None:
    esp = json.loads(caminho_esp.read_text(encoding="utf-8"))
    base = caminho_esp.parent
    folha = Image.open((base / esp["folha"]).resolve()).convert("RGB")
    caixas = recortar_paineis(folha, esp["grade"][0], esp["grade"][1])
    paineis = [folha.crop(caixas[i]) for i in esp["paineis"]]
    total = int(FPS * (DURACAO_CENA * 4 + DURACAO_FINAL))
    frames = [quadro(esp, paineis, n / FPS) for n in range(total)]
    saida = (base / esp["saida"]).resolve()
    saida.parent.mkdir(parents=True, exist_ok=True)
    reduzidos = [f.resize((LARGURA_SAIDA, ALTURA_SAIDA), Image.Resampling.LANCZOS) for f in frames]
    pal = [f.quantize(colors=96, method=Image.Quantize.MEDIANCUT, dither=Image.Dither.NONE) for f in reduzidos]
    pal[0].save(str(saida) + ".gif", save_all=True, append_images=pal[1:], duration=int(1000 / FPS),
                loop=0, optimize=True, disposal=2)
    instantes = [0.9, 3.2, 5.7, 8.2, 10.7]
    folha_prev = Image.new("RGB", (384 * 3, 216 * 2), COR_FUNDO)
    for k, s in enumerate(instantes):
        f = frames[min(int(s * FPS), len(frames) - 1)].resize((384, 216), Image.Resampling.LANCZOS)
        folha_prev.paste(f, ((k % 3) * 384, (k // 3) * 216))
    folha_prev.save(str(saida) + "_storyboard.png")
    print("GIF:", str(saida) + ".gif", "quadros:", total)


if __name__ == "__main__":
    montar(Path(sys.argv[1]))
