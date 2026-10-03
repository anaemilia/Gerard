#!/usr/bin/env python3
"""Converte as animações HTML autocontidas (NN_nome_linha.html) nos GIFs das historinhas do Gérard.

Para cada HTML, acha o GIF antigo correspondente (NN_nome_historinha.gif) em src/gerard/recursos/ajuda,
faz backup do GIF e do storyboard antigos em documentacao/producao_historinhas/_antigos/ (mesma estrutura
de pastas), captura a animação quadro a quadro com Playwright (Chromium), monta o GIF com ffmpeg
(640x360, paleta otimizada, loop infinito como os antigos) e gera o storyboard novo no formato antigo
(5 momentos: 1,5 s, 4,5 s, 7,8 s, 11 s e duração - 1 s).

Dependências (fora do repositório): pip install playwright pillow imageio-ffmpeg
                                     python -m playwright install chromium
Uso:
    python html_para_gif.py --html-dir "<pasta>/historinhas_offline"
    python html_para_gif.py --html-dir "<pasta>/historinhas_offline" --somente 01_paulo_e_jose
    python html_para_gif.py --html-dir "<pasta>/historinhas_offline" --listar     (só mostra a correspondência)
"""
from __future__ import annotations

import argparse
import json
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path
from typing import List, Optional, Sequence, Tuple

from PIL import Image, ImageDraw, ImageFont

RAIZ = Path(__file__).resolve().parents[3]
AJUDA = RAIZ / "src" / "gerard" / "recursos" / "ajuda"
ANTIGOS = RAIZ / "documentacao" / "producao_historinhas" / "_antigos"

SELETOR = "[data-om-exportable-video-with-duration-secs]"
ATRIBUTO = "data-om-exportable-video-with-duration-secs"
EVENTO_SEEK = "data-om-seek-to-time-frame"
# A imagem não grava texto algum: título, legendas e cartão final são entregues em tempo de execução
# pelo renderizador (no idioma da situação). Oculta todo elemento que contém texto próprio.
JS_OCULTAR_TEXTOS = """(sel) => {
  const raiz = document.querySelector(sel);
  const andador = document.createTreeWalker(raiz, NodeFilter.SHOW_TEXT);
  let no;
  while ((no = andador.nextNode())) {
    if (!no.textContent.trim()) continue;
    const el = no.parentElement;
    if (el && !['STYLE', 'SCRIPT'].includes(el.tagName)) el.style.visibility = 'hidden';
  }
}"""
LARGURA_GIF, ALTURA_GIF = 640, 360
INSTANTES_FIXOS = (1.5, 4.5, 7.8, 11.0)

# Layout do storyboard antigo (ver tmp/gerar_storyboards_dos_gifs.py).
LARGURA_QUADRO, ALTURA_QUADRO, ALTURA_ROTULO, ALTURA_CABECALHO = 384, 216, 27, 58
FUNDO, TEXTO, SECUNDARIO, BORDA, AZUL = (247, 246, 241), (51, 46, 40), (116, 110, 98), (216, 212, 200), (23, 90, 173)


def fonte(tamanho: int, negrito: bool = False) -> ImageFont.FreeTypeFont:
    for caminho in (r"C:\Windows\Fonts\arialbd.ttf" if negrito else r"C:\Windows\Fonts\arial.ttf",
                    "/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf" if negrito
                    else "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf"):
        if Path(caminho).exists():
            return ImageFont.truetype(caminho, tamanho)
    return ImageFont.load_default()


def achar_ffmpeg(explicito: Optional[str]) -> str:
    if explicito:
        return explicito
    achado = shutil.which("ffmpeg")
    if achado:
        return achado
    try:
        import imageio_ffmpeg  # type: ignore
        return imageio_ffmpeg.get_ffmpeg_exe()
    except Exception as erro:  # noqa: BLE001
        raise SystemExit("ffmpeg não encontrado (instale ffmpeg ou 'pip install imageio-ffmpeg'): %s" % erro)


def gif_antigo_de(html: Path, ajuda: Path) -> Optional[Path]:
    nome = html.name[: -len("_linha.html")]
    candidatos = sorted(ajuda.glob("*/%s_historinha.gif" % nome))
    return candidatos[0] if len(candidatos) == 1 else None


def listar(html_dir: Path, ajuda: Path) -> List[Tuple[Path, Optional[Path]]]:
    pares = [(h, gif_antigo_de(h, ajuda)) for h in sorted(html_dir.glob("*_linha.html"))]
    usados = {g for _, g in pares if g}
    sem_html = [g for g in sorted(ajuda.glob("*/*_historinha.gif")) if g not in usados]
    for h, g in pares:
        print("%-52s -> %s" % (h.name, g.relative_to(ajuda).as_posix() if g else "SEM PAR"))
    for g in sem_html:
        print("GIF sem HTML novo:", g.relative_to(ajuda).as_posix())
    return pares


def fazer_backup(gif: Path, ajuda: Path) -> List[Path]:
    copiados = []
    for origem in (gif, gif.with_name(gif.stem + "_storyboard.png")):
        if not origem.exists():
            continue
        destino = ANTIGOS / origem.relative_to(RAIZ)
        if destino.exists():  # nunca sobrescreve um backup anterior
            continue
        destino.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(origem, destino)
        copiados.append(destino)
    return copiados


def capturar_quadros(html: Path, pasta: Path, fps: int, com_texto: bool = False) -> Tuple[float, int]:
    from playwright.sync_api import sync_playwright  # importação tardia: só quem converte precisa dela

    with sync_playwright() as p:
        navegador = p.chromium.launch()
        pagina = navegador.new_page(viewport={"width": 1920, "height": 1080})
        pagina.goto(html.resolve().as_uri())
        pagina.wait_for_timeout(3000)
        pagina.wait_for_selector(SELETOR, timeout=60000, state="attached")
        elemento = pagina.query_selector(SELETOR)
        duracao = float(elemento.get_attribute(ATRIBUTO))
        total = int(duracao * fps)
        for i in range(total):
            t = i / fps
            pagina.evaluate(
                "([sel, t]) => document.querySelector(sel).dispatchEvent("
                "new CustomEvent('%s', {detail: {time: t, sync: true}}))" % EVENTO_SEEK,
                [SELETOR, t],
            )
            pagina.wait_for_timeout(50)
            if not com_texto:
                pagina.evaluate(JS_OCULTAR_TEXTOS, SELETOR)
            elemento.screenshot(path=str(pasta / ("f_%05d.png" % i)))
        navegador.close()
    return duracao, total


def montar_gif(ffmpeg: str, pasta: Path, fps: int, destino: Path) -> None:
    filtro = ("scale=%d:%d:force_original_aspect_ratio=decrease:flags=lanczos,"
              "pad=%d:%d:(ow-iw)/2:(oh-ih)/2:color=white,split[a][b];"
              "[a]palettegen=max_colors=128[p];[b][p]paletteuse=dither=bayer:bayer_scale=3"
              % (LARGURA_GIF, ALTURA_GIF, LARGURA_GIF, ALTURA_GIF))
    comando = [ffmpeg, "-y", "-loglevel", "error", "-framerate", str(fps), "-i", str(pasta / "f_%05d.png"),
               "-vf", filtro, "-loop", "0", str(destino)]
    subprocess.run(comando, check=True)


def ler_gif(caminho: Path) -> Tuple[List[Image.Image], List[int]]:
    quadros, duracoes = [], []
    with Image.open(caminho) as gif:
        for i in range(gif.n_frames):
            gif.seek(i)
            quadros.append(gif.convert("RGB").copy())
            duracoes.append(max(1, int(gif.info.get("duration", 100))))
    return quadros, duracoes


def quadro_no_tempo(quadros: Sequence[Image.Image], duracoes: Sequence[int], tempo_ms: int) -> Image.Image:
    acumulado = 0
    for q, d in zip(quadros, duracoes):
        acumulado += d
        if tempo_ms < acumulado:
            return q
    return quadros[-1]


def nome_legivel(nome: str) -> str:
    partes = nome.replace("_historinha", "").split("_")
    if partes and partes[0].isdigit():
        partes = partes[1:]
    return " ".join(partes).capitalize()


def criar_storyboard(gif: Path) -> Path:
    quadros, duracoes = ler_gif(gif)
    total_ms = sum(duracoes)
    segundos = list(INSTANTES_FIXOS) + [total_ms / 1000.0 - 1.0]
    instantes = [max(0, min(total_ms - 1, int(s * 1000))) for s in segundos]
    l_fonte, p_fonte, t_fonte = fonte(17), fonte(14), fonte(22, True)
    altura_linha = ALTURA_QUADRO + ALTURA_ROTULO
    folha = Image.new("RGB", (LARGURA_QUADRO * 3, ALTURA_CABECALHO + altura_linha * 2), FUNDO)
    d = ImageDraw.Draw(folha)
    d.text((18, 8), nome_legivel(gif.stem), font=t_fonte, fill=TEXTO)
    d.text((18, 35), gif.parent.name.replace("_", " ").upper(), font=p_fonte, fill=SECUNDARIO)
    for i, ms in enumerate(instantes):
        x, y = (i % 3) * LARGURA_QUADRO, ALTURA_CABECALHO + (i // 3) * altura_linha
        folha.paste(quadro_no_tempo(quadros, duracoes, ms).resize((LARGURA_QUADRO, ALTURA_QUADRO),
                                                                   Image.Resampling.LANCZOS), (x, y))
        d.rectangle((x, y, x + LARGURA_QUADRO - 1, y + ALTURA_QUADRO - 1), outline=BORDA, width=1)
        d.text((x + 10, y + ALTURA_QUADRO + 5), "Quadro %d · %.1f s" % (i + 1, ms / 1000.0),
               font=p_fonte, fill=SECUNDARIO)
    x, y = LARGURA_QUADRO * 2, ALTURA_CABECALHO + altura_linha
    d.rectangle((x, y, folha.width - 1, folha.height - 1), fill=(252, 251, 248), outline=BORDA)
    d.text((x + 24, y + 42), "Storyboard extraído do GIF", font=l_fonte, fill=TEXTO)
    d.text((x + 24, y + 78), "Duração: %.2f s" % (total_ms / 1000.0), font=l_fonte, fill=AZUL)
    d.text((x + 24, y + 110), "5 momentos representativos", font=p_fonte, fill=SECUNDARIO)
    destino = gif.with_name(gif.stem + "_storyboard.png")
    folha.save(destino)
    return destino


def converter(html: Path, gif: Path, ffmpeg: str, fps: int, com_texto: bool = False) -> dict:
    tamanho_antigo = gif.stat().st_size
    fazer_backup(gif, AJUDA)
    with tempfile.TemporaryDirectory(prefix="html_para_gif_") as tmp:
        pasta = Path(tmp)
        duracao, total = capturar_quadros(html, pasta, fps, com_texto)
        novo = pasta / "saida.gif"
        montar_gif(ffmpeg, pasta, fps, novo)
        shutil.copyfile(novo, gif)
    storyboard = criar_storyboard(gif)
    return {"html": html.name, "gif": gif.relative_to(RAIZ).as_posix(), "duracao_s": duracao, "quadros": total,
            "tamanho_antigo_kb": tamanho_antigo // 1024, "tamanho_novo_kb": gif.stat().st_size // 1024,
            "storyboard": storyboard.name}


def main() -> int:
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--html-dir", required=True, type=Path)
    ap.add_argument("--ajuda-dir", type=Path, default=AJUDA)
    ap.add_argument("--fps", type=int, default=12)
    ap.add_argument("--ffmpeg")
    ap.add_argument("--com-texto", action="store_true",
                    help="mantém o texto gravado na imagem (padrão: sem texto; o texto é entregue em tempo de execução)")
    ap.add_argument("--somente", help="converte só NN_nome (sem _linha.html)")
    ap.add_argument("--listar", action="store_true", help="só lista a correspondência HTML -> GIF")
    ap.add_argument("--relatorio", type=Path, help="grava o relatório em JSON")
    a = ap.parse_args()

    pares = listar(a.html_dir, a.ajuda_dir)
    if a.listar:
        return 0
    ffmpeg = achar_ffmpeg(a.ffmpeg)
    resultados, erros = [], []
    for html, gif in pares:
        nome = html.name[: -len("_linha.html")]
        if a.somente and a.somente != nome:
            continue
        if gif is None:
            erros.append({"html": html.name, "erro": "sem GIF antigo correspondente"})
            continue
        try:
            r = converter(html, gif, ffmpeg, a.fps, a.com_texto)
            resultados.append(r)
            print("OK  %-44s %6d KB -> %6d KB  (%.1f s, %d quadros)" % (
                nome, r["tamanho_antigo_kb"], r["tamanho_novo_kb"], r["duracao_s"], r["quadros"]))
        except Exception as erro:  # noqa: BLE001
            erros.append({"html": html.name, "erro": "%s: %s" % (type(erro).__name__, erro)})
            print("ERRO", nome, erro)
    relatorio = {"substituidos": resultados, "erros": erros}
    if a.relatorio:
        a.relatorio.write_text(json.dumps(relatorio, ensure_ascii=False, indent=2), encoding="utf-8")
    return 1 if erros else 0


if __name__ == "__main__":
    sys.exit(main())
