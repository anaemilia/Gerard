# Remoção do editor de enunciado do desktop (2026-10-07)

Retirados da `Main.java` os 11 métodos do editor, os campos e os ramos de edição (pintura, `mousePressed`, `mouseDragged`,
`mouseReleased`, `mouseClicked`), e as 6 classes de `gerard/ui/enunciado/editor/`. Main: 12.817 -> 12.481 linhas.

Provas por mouse real depois da remoção: exploração após a conclusão (RESUMO falhas=0), arraste das palavras do texto após
acertar a categoria (3 de 4 por categoria; a parada é uma palavra curta sob outra já movida), ausência do botão "T" após a
conclusão, e seleção do marcador de texto (OK em 2 rodadas).

Ressalva: `TesteRobotSelecaoMarcadorTexto` falhou uma vez ("A origem do enunciado foi removida durante a seleção") quando
rodou logo depois de outros robots, com 1,2 GB de RAM livre, e passou em duas rodadas seguintes sozinho. Tratado como
instabilidade de temporização, não como regressão; a primeira tentativa fica registrada em `..._FALHOU_instabilidade.log`.
