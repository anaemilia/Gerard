# Montagem das historinhas das folhas — 2026-10-03

13 folhas aprovadas; nenhuma reprovada. Conferência visual das folhas e dos 13 storyboards: quatro quadros, personagens consistentes, sem texto acrescentado à fonte, legendas e equações preservadas do rascunho. Andréa não foi remontada. Nenhuma geração de imagem ou serviço pago foi usado nesta etapa.

Foram registrados 13 vínculos entre situação curada e animação no repertório do backend: seis em transformação de medidas, seis em comparação de medidas e um em composição de transformações. A decisão de ajuda e o limite de rejeições não foram alterados. O total verificado passou de 9 para 22 situações ilustradas, com 15 restantes de texto (37 com apoio).

Cada GIF tem 640 × 360. O montador calcula 96 quadros a 8 quadros/s; a codificação GIF agrega imagens repetidas, resultando em 71–84 quadros armazenados e duração de aproximadamente 11,6 segundos. O enquadramento panorâmico preserva ambos os participantes com preenchimento desfocado, sem esticar a imagem. Títulos, legendas, equações e conclusões coincidem integralmente com o JSON autorizado.

| Historinha | GIF | Tamanho |
| --- | --- | --- |
| Maria e as figurinhas | `src/gerard/recursos/ajuda/transformacao_medidas/02_maria_e_as_figurinhas_historinha.gif` | 6.36 MiB |
| Maria na feira | `src/gerard/recursos/ajuda/transformacao_medidas/03_maria_na_feira_historinha.gif` | 5.73 MiB |
| Nádia e os morangos | `src/gerard/recursos/ajuda/transformacao_medidas/04_nadia_e_os_morangos_historinha.gif` | 6.24 MiB |
| Leandro e as balas | `src/gerard/recursos/ajuda/transformacao_medidas/05_leandro_e_as_balas_historinha.gif` | 6.19 MiB |
| Paulo e as bolas | `src/gerard/recursos/ajuda/transformacao_medidas/06_paulo_e_as_bolas_historinha.gif` | 6.03 MiB |
| Lucas e as figurinhas do tio | `src/gerard/recursos/ajuda/transformacao_medidas/07_lucas_e_as_figurinhas_do_tio_historinha.gif` | 6.25 MiB |
| Paulo e José | `src/gerard/recursos/ajuda/comparacao_medidas/01_paulo_e_jose_historinha.gif` | 6.21 MiB |
| Marcus e Jardel | `src/gerard/recursos/ajuda/comparacao_medidas/02_marcus_e_jardel_historinha.gif` | 6.59 MiB |
| Ingrid e Ligianne | `src/gerard/recursos/ajuda/comparacao_medidas/03_ingrid_e_ligianne_historinha.gif` | 6.24 MiB |
| Jammes e Gisele | `src/gerard/recursos/ajuda/comparacao_medidas/04_jammes_e_gisele_historinha.gif` | 6.21 MiB |
| Claudia e Joana | `src/gerard/recursos/ajuda/comparacao_medidas/05_claudia_e_joana_historinha.gif` | 6.33 MiB |
| Claudenice e Nádia | `src/gerard/recursos/ajuda/comparacao_medidas/06_claudenice_e_nadia_historinha.gif` | 5.92 MiB |
| Geisa e a caixa de chocolates | `src/gerard/recursos/ajuda/composicao_transformacoes/05_geisa_e_a_caixa_de_chocolates_historinha.gif` | 5.97 MiB |

Cada GIF tem um `_storyboard.png` ao lado; as especificações e folhas correspondentes ficam em `documentacao/producao_historinhas/`. Caminhos, tamanho exato em bytes e duração estão em [manifest.json](evidencias/historinhas_folhas_20261002/manifest.json).

## Validação

Compilação Java completa com compatibilidade Java 8 e build web aprovados. Foi corrigido o escape da expressão regular em `PainelHistorinhaTextual`, que impedia a compilação com Java 8. A captura gráfica também revelou um painel ilustrado sem layout interno; o adaptador Swing agora dispõe os filhos antes da pintura, sem escolher a ajuda.

- `TesteAjudaVisualPorCategoriaWeb`: aprovado, 22 ilustradas e 15 de texto.
- `TesteLimiteHistorinhasOperacao`: aprovado, limite, deduplicação, seletores, acerto, restauração e exploração.
- `TesteCenaSubtitulos`: aprovado.
- `TesteAreaDiagramaPortatil`: aprovado nas seis cenas.
- `TesteRobotHistorinhasNovas`: aprovado, `RESUMO falhas=0`, quatro situações novas por mouse/teclado reais: Maria/figurinhas, Nádia/morangos, Paulo/José e Geisa/chocolates. Ausência antes e após as duas primeiras rejeições; ilustração própria e imagem com área visível após a terceira. Capturas em [desktop-3](evidencias/historinhas_folhas_20261002/desktop-3/). Há somente três categorias entre as 13 folhas, por isso transformação de medidas teve dois casos.
- `e2e_historinhas_folhas.mjs`: aprovado para Maria/figurinhas, Nádia/morangos e Paulo/José. Interações reais, contagens 1/2/3 observadas no HTTP, apoio apenas na terceira rejeição, `HISTORINHA_ILUSTRADA`, referência própria e GIF completamente carregado. A quarta situação (Geisa) documentou a limitação descrita abaixo. Capturas, sequência HTTP e [resultado](evidencias/historinhas_folhas_20261002/web/resultado.txt) na pasta `web`.
- `e2e_historinhas_categorias_relativas.mjs`: executado; terceira rejeição e referência própria aprovadas nas três categorias anteriores. Conclusão e restauração aprovadas em transformação de relação e composição de relações; falhou na conclusão da situação de João/bilas em composição de transformações. A API publicou os três valores como conhecidos e `concluida=false` após escolher Soma, sem proposta de incógnita disponível; essa limitação anterior não foi alterada pela montagem. O teste agora aguarda a atualização do campo e a resposta da proposta, eliminando uma corrida no roteiro. [Resultado completo](evidencias/historinhas_folhas_20261002/web/regressao_categorias_relativas.txt).

As primeiras execuções gráficas não constituem prova final: uma repetia Enter no editor; outra verificava a presença do painel sem conferir a área da imagem. A evidência válida é a pasta `desktop-3`. No teste web a sequência aguarda as respostas completas antes de iniciar a próxima ação; nenhuma resposta é simulada.

## Curadoria e pendências

A inconsistência conhecida da Geisa permanece: `estado_final=16` e `resultado=3`, enquanto o enunciado e o rascunho descrevem 25 − 2 − 5 = 18. O GIF conserva exatamente o rascunho autorizado. Nenhum dado curado foi alterado.

No web, essa situação também publica `estado_final` como conhecido (16), sem ação de propor a incógnita após posicionar os dados. Por isso a ativação da animação por três propostas não foi concluída nesse cliente para Geisa. O vínculo da animação foi validado no teste do backend; a ativação e exibição foram concluídas por mouse no desktop. Corrigir o modelo/curadoria web dessa situação permanece pendente, fora da montagem autorizada. Evidência: [captura da limitação](evidencias/historinhas_folhas_20261002/web/05_geisa_e_a_caixa_de_chocolates_limitacao_curadoria.png).

Sem mudanças em `C:\gd`, sem push e sem implantação no Render. Commits locais com coautoria: transformação de medidas `69fe49b`; comparação de medidas `f9f7192`; composição de transformações é o commit que acrescenta este relatório (`feat(ajuda): montar e registrar historinhas de composição de transformações`). O hash final é informado na entrega do chat.
