# Relatório de alteração — foto no submenu “Em Recife”

**Data:** 2026-07-15  
**Ciclo:** C76  
**Tipo de intervenção:** atualização visual localizada.

## Síntese
A foto exibida no submenu **Em Recife** foi substituída pela nova foto solicitada pelo usuário.

## Alterações implementadas
- substituição do arquivo de imagem `src/gerard/imagens/em_recife_rostos.png`;
- preservação da lógica existente de redimensionamento automático por largura e altura máximas;
- manutenção do restante do submenu, incluindo título e legenda textual.

## Resultado esperado
Ao abrir **Sobre → Gérard Vergnaud → Em Recife**, a nova foto passa a aparecer na área da imagem, ajustada automaticamente quando necessário para caber no espaço do menu sem distorção.

## Verificação realizada
- compilação Ant/NetBeans: **aprovada**.
