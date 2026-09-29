# Estado verificado da implementação de scaffolding

Leia esta referência ao auditar ou alterar o piloto adaptativo, a atração
magnética, seus consumidores reais ou divergências entre códigos e
comportamentos legados. As definições e os limites pedagógicos permanecem no
`SKILL.md` proprietário.

## Verificação geral em 2026-07-20

Oito das nove regras então documentadas foram conferidas linha a linha contra
o código e se confirmaram. A exceção era a antiga formulação sobre “duas
categorias de mensagem”: existe uma classe dedicada para questionamento, mas
não uma estrutura equivalente para “mensagem informativa”. A seção de
mensagens do arquivo principal registra a correção; versões anteriores não são
fonte válida.

## Piloto P2.3C em 2026-08-13

O repertório local de `IncognitaQuantitativa` referencia os três apoios já
aprovados para a sequência de rejeições: `AG_EMLQ`
(metacognitivo/visual), `AG_EME` (conceitual/visual) e `AG_EMCME`
(procedimental/manipulativa e visual). Esse repertório é o subconjunto
pertencente à incógnita, não um sétimo catálogo.

A interface materializa decisões publicadas desse proprietário e registra
separadamente decisão e apresentação. Na ausência de regra publicada, o
fallback legado permanece explícito.

## Atração magnética adaptativa P2.4A em 2026-08-13

A mecânica de `AG_AE` permanece em `ScaffoldingProximidade`,
`aplicarAtracaoMagnetica` e `centralizarItemNoElemento`, usando a geometria
real do item e do alvo. Sua disponibilidade deixou de ser permanente.
`PapelQuantitativoPosicionavel` decide localmente a partir do diagnóstico
factual, da fotografia do Modelo do Usuário e de regra publicada pelo
Modelador. `MaterializadorAtracaoMagneticaAdaptativa` conserva a ativação
temporária por chave de papel, e `Main` somente consulta essa ativação antes de
executar atração ou centralização.

Sem regra aplicável, o movimento magnético fica desligado. Quando `AG_AE` é
selecionado, somente o objeto semanticamente vinculado ao papel decidido
recebe a affordance. O ciclo termina no posicionamento correto desse papel ou
na restauração/troca da atividade. A P2.4A alterou exclusivamente a
disponibilidade da mecânica; não alterou sua geometria nem os demais efeitos
visuais do estilo de proximidade.

O botão “Ver dica” e seus registros ainda aparecem no legado com o código
`AG_AE`, mas oferecem uma pista visual sob demanda, não a automatização por
atração magnética definida pela usuária. Esse uso do código é uma divergência
de classificação a corrigir sem duplicar nem reimplementar a atração já
existente. A P2.3C não altera esse protocolo de arraste.
