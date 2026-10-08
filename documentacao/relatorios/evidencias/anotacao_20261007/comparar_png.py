import os, collections
from PIL import Image, ImageChops, ImageDraw

E = r'C:/Users/cecomp/Documents/aemq/git/Gerard/documentacao/relatorios/evidencias/anotacao_20261007/'
FAIXA = (0, 205, 2000, 245)   # linha do texto da situação-problema: varia 1 px entre execuções (também no código antigo)


def mascarar(im):
    im = im.convert('RGB')
    ImageDraw.Draw(im).rectangle(FAIXA, fill=(255, 255, 255))
    return im


def comparar(pa, pb):
    res = []
    for f in sorted(os.listdir(E + pa)):
        if not os.path.exists(E + pb + '/' + f):
            continue
        a = mascarar(Image.open(E + pa + '/' + f)); b = mascarar(Image.open(E + pb + '/' + f))
        res.append((f, None if a.size != b.size else ImageChops.difference(a, b).getbbox()))
    return res


r = comparar('png_antes', 'png_depois')
iguais = [x for x in r if x[1] is None]
print('antes x depois (faixa do texto mascarada): PNGs', len(r), 'idênticos', len(iguais), 'diferentes', len(r) - len(iguais))
por = collections.Counter(x[0].split('_')[-3] if False else x[0].rsplit('_', 1)[0] for x in r if x[1] is not None)
for f, bb in r:
    if bb is not None:
        print(f, bb)
