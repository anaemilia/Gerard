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

Na divisão de responsabilidades verificada, `Main` traduz o clique em comando,
delega a sincronização entre representações e encaminha o registro factual.
`CirculoVenn` e `QuadradinhoVenn` seguem a convenção representacional já usada
por `ElementoVergnaud` e `ConectorVergnaud`; isso não transfere cálculo
semântico nem persistência para os objetos visuais.
