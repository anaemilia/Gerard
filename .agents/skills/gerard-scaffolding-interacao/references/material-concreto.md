# Material concreto e AG_AC

Leia esta referência ao alterar quadradinhos, contagem, adição/remoção, versões
simplificadas ou a distribuição técnica desse apoio.

No Gérard, a ajuda por metáfora de material concreto corresponde aos
quadradinhos arrastáveis e manipuláveis já implementados. Não presuma outras
metáforas sem decisão explícita da usuária. A representação pode ser retirada
de versões simplificadas, como uma versão mobile que conserve apenas a
modelagem formal.

Por decisão de 2026-08-08, `AG_AC` só existe incorporado ao material concreto.
Adicionar ou remover um quadradinho por clique já constitui essa automação:
cada comando corresponde a exatamente uma unidade manipulável e progressiva.
A classificação como ajuda guiada por movimento/manipulativa não autoriza
construir outro mecanismo nem alterar a aparência existente.

Por decisão explícita da usuária em 2026-10-01, as representações
complementares com funil não participam mais da atividade. Em
`TRANSFORMACAO_MEDIDAS`, o painel complementar reservado permanece vazio. Em
`COMPOSICAO_TRANSFORMACOES`, as representações com funis foram substituídas
pelas historinhas existentes. As historinhas são somente representações
visuais passivas: não possuem interação, semântica nem conhecimento próprio.
Elas aparecem em situações com ocorrência de número relativo depois da
terceira tentativa. O diagrama de Vergnaud não muda de
posição, tamanho, estado semântico ou protocolo de interação. Essa retirada
não elimina `AG_EMCME` nem `AG_AC` do repertório operacional das outras
categorias que ainda possuem material concreto.

Decisão posterior da usuária em 2026-10-02: a regra das historinhas é geral
para situações com números relativos, inclusive positivos e incógnitos:
exibir somente o repertório da própria categoria após três rejeições
consecutivas do mesmo item. Correção explícita posterior da usuária na mesma
data: a decisão de acionar historinha depende somente da ocorrência de número
relativo e das três rejeições, nunca da disponibilidade do repertório. Acervo
vazio é uma pendência de conteúdo posterior à decisão; não revoga o acionamento.
O contrato web publica `ajuda_visual_acionada` separadamente da lista de conteúdo.
A representação materializa o conteúdo disponível da própria categoria.
O gatilho existente da
incógnita é conservado. Para escolha de operação, a tentativa acompanha os
resultados factuais do mesmo seletor, sem duplicar action_id, sem alterar C/E
e sem bloquear o seletor. Acerto antes do limite encerra a sequência; apoio
já liberado permanece até restauração ou novo sorteio. Exploração após
conclusão não produz rejeições. Esta decisão substitui comentários anteriores
que dispensavam o gatilho das historinhas ou fixavam o repertório de
Composição de Transformações no desktop.

Na divisão de responsabilidades verificada, `Main` traduz o clique em comando,
delega a sincronização entre representações e encaminha o registro factual.
`CirculoVenn` e `QuadradinhoVenn` seguem a convenção representacional já usada
por `ElementoVergnaud` e `ConectorVergnaud`; isso não transfere cálculo
semântico nem persistência para os objetos visuais.

Verificação de 2026-10-02: a conclusão limpa de Composição de Medidas não
libera material concreto. A situação Lucas/Pokémon/Digimon foi concluída por
mouse real no desktop e web com zero rejeições e painel complementar vazio.
Isso verifica o gatilho existente, sem criar outra regra. O histórico do
print antigo 04 não pôde ser reconstruído. Evidências e limites em
`documentacao/relatorios/RELATORIO_COMPOSICAO_MATERIAL_CONCRETO_2026-10-02.md`.
