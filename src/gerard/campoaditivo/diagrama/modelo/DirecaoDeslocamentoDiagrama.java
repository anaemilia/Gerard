package gerard.campoaditivo.diagrama.modelo;

/**
 * Direção em que o gerador de cena deve deslocar visualmente o diagrama
 * (via margem assimétrica no viewport) quando o material concreto está
 * disponível ao lado dele — nunca uma posição fixa. Cada categoria tem sua
 * própria direção porque a colisão real com o "indicador de sucesso" do
 * desktop varia de categoria para categoria (ver
 * GeradorCenaDiagramaAditivo.direcaoDeslocamentoParaMaterialConcreto).
 */
public enum DirecaoDeslocamentoDiagrama {
    SEM_DESLOCAMENTO,
    PARA_ESQUERDA,
    PARA_DIREITA
}
