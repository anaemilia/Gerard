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

## Produção das historinhas ilustradas — estado verificado em 2026-10-03

As folhas e os rascunhos autorizados ficam em
`documentacao/producao_historinhas/`; cada especificação preserva título,
legendas, equação e conclusão do rascunho. O montador existente produz GIF e
storyboard. Para quadros panorâmicos, `larguras_recorte` permite preservar
os participantes sem deformar a imagem, preenchendo a área restante com
fundo desfocado; sua ausência mantém o enquadramento anterior.

O vínculo entre situação curada e recurso pertence ao repertório de
`TipoSituacaoAditiva`. Acrescentar mídia não altera o limite de rejeições
nem sua decisão. Swing e web realizam o apoio já projetado pela cena/API.
As 13 montagens desta etapa acrescentam seis vínculos em transformação de
medidas, seis em comparação de medidas e um em composição de transformações.
Inconsistências encontradas na curadoria são relatadas, sem correção automática.
