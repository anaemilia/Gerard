# Mensagens de scaffolding

Leia esta referência ao criar ou revisar mensagens, pistas, explicações ou sua
materialização em mídia. Os exemplos são abertos: não constituem subtipos
exaustivos nem autorizam novos códigos operacionais.

Exemplos de scaffolding realizado por mensagens:

- **Questionamento**: perguntar se o usuário tem certeza de que o passo
  executado está especificado no texto. A classe
  `ScaffoldingQuestionamento` não reduz todo questionamento a um único formato.
- **Fornecer dica ou pista**: oferecer informação parcial que apoie a
  continuidade da modelagem.
- **Explicar passos da modelagem e a legenda utilizada**: tornar explícita a
  sintaxe da representação e o encadeamento dos passos.
- **Dar explicação textual**: apresentar uma explicação em linguagem natural
  como apoio pedagógico.

Confronte a finalidade da mensagem com `gerard-ajuda-adaptativa`,
`gerard-consistencia-estado`, `gerard-log-acao-instrumental` e
`gerard-semantic-event-logging`. Não infira a estratégia pedagógica apenas
pelo formato textual. Mensagens informativas que hoje existem somente como
strings localizadas são estado de implementação, não definição teórica da
quantidade de tipos.

Quando uma mensagem explicativa usar personagens ou entidades da curadoria, o
modelo textual deve nomear os campos de origem, como `{Personagem_1}`,
`{Personagem_2}` e `{Personagem_3}`. A representação substitui cada marcador
pelo campo curado homônimo; não associa personagens pela posição no diagrama
nem infere seus papéis. A adequação dos valores e da frase permanece sob
responsabilidade do pesquisador humano.

Uma explicação `AG_EME` pode ser materializada em linguagem natural, animação
ou história em quadrinhos quando essas formas estiverem disponíveis no
repertório local. História em quadrinhos é sintaxe visual de apresentação, não
um sétimo código. A necessidade e a função da ajuda são decididas antes; a
mídia preferida do perfil escolhe apenas a materialização.
