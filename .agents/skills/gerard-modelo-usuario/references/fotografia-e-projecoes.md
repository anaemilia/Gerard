# Fotografia da sessão e projeções

Leia esta referência ao trabalhar com carregamento no login, estabilidade da
sessão, contexto adaptativo ou combinação entre histórico e fatos correntes.

A fotografia carregada no login permanece estável até o logout. Atualizações
publicadas pelo Modelador passam a valer somente numa sessão posterior.

Essa fotografia representa o nível histórico/intersessões da adaptação. Ela
não contém nem substitui o Modelo da Situação/Solução corrente. Durante a
sessão, o proprietário semântico combina a projeção histórica com fatos
tipados e mínimos produzidos pela tentativa, situação ou relação estrutural
responsável. Esses fatos podem mudar a cada ação sem mutar a fotografia.

Não entregue `ModeloUsuario` mutável inteiro aos objetos. Produza
`ContextoAdaptativoUsuario` imutável, reduzido ao proprietário e à decisão
corrente. A decisão registra a versão da fotografia e a regra usada, inclusive
quando nenhuma regra aplicável for encontrada.

O Modelo do Usuário não decide como J48/PART e Apriori inferem regras nem
define o conteúdo dos repertórios; documenta o que é armazenado e exposto para
leitura.
