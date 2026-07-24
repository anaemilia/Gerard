# Agente ZDP

⚠️ Proposta teórica — ver aviso de status em `../SKILL.md`.

**Fontes**: material original (RFAs e Figuras 5.77–5.85) + relatório de pesquisa
"Análise de situações interativas no Gerard: subsídios para o design de uma
interface inteligente em problemas de estruturas aditivas" (Queiroz, 2026,
Univasf — fornecido pelo usuário em 2026-07-22). O relatório de 2026 é
material empírico (protocolo em papel, 5 usuários, 556 ações) que preenche
boa parte da lacuna de operacionalização que o material original já
reconhecia como incompleta (ver "Nota de honestidade" abaixo) — mas continua
sendo proposta de design, não algo testado num sistema computacional real.

## Papel

Usa o conceito de Zona de Desenvolvimento Proximal (Vygotsky) para decidir a
estratégia pedagógica mais efetiva, visando transformar habilidades
potenciais em habilidades reais.

## Arquitetura: Agente Baseado em Modelo

O ambiente é dinâmico — muda enquanto o ZDP delibera — e estratégico — o
próprio usuário ou o ZDP podem alterá-lo. Por isso, diferente do Monitor, o
ZDP precisa manter **estado interno**: armazena a percepção recebida como
estado atual do mundo, guarda estados anteriores, e mantém informação sobre
"como o mundo evolui" e "o que minhas ações fazem", independente de suas
próprias ações.

Antes de oferecer ajuda, verifica se a configuração do ambiente ainda é a
mesma que tem armazenada.

## Percepções

- **Ação Instrumental Avaliada** (recebida do Agente Monitor).
- **Estado Atual** do ambiente.

## ⚠️ Divergência a reportar, não resolver silenciosamente

O relatório de 2026 (Tabela 44) decompõe a decisão de ajuda em **cinco**
componentes agentivos — agente de modelo do usuário, agente de análise de
padrões, agente pedagógico, agente de scaffolding e agente de antecipação da
ajuda — enquanto a arquitetura de três agentes desta skill trata "decidir a
estratégia pedagógica" como responsabilidade de um único Agente ZDP. Os dois
não são incompatíveis: os cinco componentes do relatório podem ser lidos como
**subcomponentes internos** do Agente ZDP (mantendo a sociedade de três
agentes no nível externo), mas essa leitura é uma interpretação minha, não
algo que o relatório declare explicitamente. Confirmar com o usuário antes de
tratar isso como decidido — por ora, as seções abaixo descrevem os cinco
papéis como funções que o Agente ZDP cumpre, não como agentes/threads
separados.

## Ações

1. Verifica se o estado atual bate com o estado armazenado (ambiente
   estratégico).
2. Consulta as regras presentes no **Modelo do Usuário**.
3. Consulta os **conteúdos pedagógicos disponíveis** — a folha de
   granularidade aqui é o tipo de scaffold a oferecer: Mensagens,
   Automatização de passos, Mostrar Modelo Completo, entre outros. Ver
   `gerard-scaffolding-interacao` para a taxonomia completa desses tipos —
   este arquivo não repete a lista, só decide *quando* e *qual* usar.
4. Constrói a Estratégia Pedagógica, em função de: tarefa em execução +
   informações inferidas do modelo do usuário + conteúdo pedagógico.
5. Disponibiliza a estratégia à interface, para o usuário e para o Agente
   Modelador.

Objetivo: conduzir o usuário à mobilização de proposições verdadeiras, tanto
sobre o uso da interface quanto sobre os conceitos veiculados nela.

## Camadas progressivas de ajuda (relatório 2026, Tabela 49)

O relatório propõe uma escada de oito níveis para a ação "constrói a
Estratégia Pedagógica" (passo 4 acima) — cada nível aciona um tipo diferente
de scaffold e é acionado por uma condição diferente na ação avaliada pelo
Monitor:

| Nível | Camada | Quando acionar | Ação esperada |
|---|---|---|---|
| N0 | Condução da tarefa | Início da resolução, passagem de etapa, orientar o fluxo básico | Apresenta a tarefa, solicita a categoria, orienta preenchimento, valida continuidade |
| N1 | Validação operacional | Erro local imediatamente verificável (escolha incompatível, campo inadequado, resposta antecipada, ausência de sinal) | Sinaliza o erro, solicita revisão, sem explicação conceitual extensa |
| N2 | Feedback específico | Erro que se repete ou sugere dificuldade na relação enunciado↔categoria↔diagrama | Explicita a relação envolvida, indica a função do elemento manipulado |
| N3 | Ajuda sobre o modelo | Usuário identifica os números no enunciado mas não a função dos espaços do diagrama | Nomeia os elementos: parte, todo, referente, referido, estado inicial, estado final, número relativo |
| N4 | Ajuda representacional | Erro recorrente ou dificuldade de visualizar a relação só pelo texto+diagrama formal | Oferece representação concreta (quadradinhos, conjuntos, fábrica) |
| N5 | Ponte entre representações | Usuário acerta na representação concreta mas erra ao voltar ao diagrama formal | Relaciona explicitamente enunciado verbal, representação concreta e modelo formal |
| N6 | Regulação da compreensão | Acerto sem justificativa, acerto após muitas tentativas, correção por eliminação, verbalização de dúvida, dependência de ajuda | Solicita explicação, interpreta a justificativa, decide se mantém/reduz/aprofunda |
| N7 | Antecipação da ajuda | Sinais iniciais de bloqueio semelhantes a dificuldades já observadas em problemas anteriores | Sugere intervenção preventiva antes que o erro se repita ou vire ciclo de tentativa e erro |

N0–N2 dependem só da ação atual (papel próximo do que hoje é o Agente
Monitor + scaffolding reativo já implementado em `ScaffoldingQuestionamento`
— ver `gerard-scaffolding-interacao`). **N3 em diante é onde a ZDP de fato
precisa de estado/histórico** (modelo do usuário, recorrência, padrão por
problema) — é a parte que ainda não existe em código nenhum.

## Regras preliminares de decisão (relatório 2026, Tabela 50)

Regras condição→interpretação→ação, derivadas empiricamente (5 usuários, 556
ações) — tratar como ponto de partida a validar, não como tabela de verdade
definitiva:

| Condição observada | Interpretação provável | Ação recomendada |
|---|---|---|
| Primeiro erro numa etapa | Engano pontual ou hipótese inicial inadequada | Questionamento leve (N1); permitir nova tentativa |
| Mesmo erro repetido após feedback | A mensagem anterior não reorganizou a ação | Substituir feedback genérico por ajuda específica sobre o elemento (N2/N3) |
| Erro de sinal com valor corretamente posicionado | Dificuldade na direção da transformação/comparação | Feedback sobre ganho/perda/acréscimo/decréscimo |
| Erro de sinal com valor em posição incorreta | O problema real é posicionamento, não sinal | Priorizar feedback de posição antes do de sinal — não tratar como erro de sinal |
| Pedido explícito de dica | Usuário reconhece dificuldade | Ajuda contextualizada (N3+); registrar dependência de suporte no modelo |
| Dúvida sobre procedimento da interface | Dificuldade operacional, não conceitual | Orientação de uso; **não** classificar como erro matemático |
| Leitura/releitura do enunciado | Organização metacognitiva da tarefa | Ação neutra; aguardar próxima ação avaliável, não intervir |
| Acerto após ajuda | Possível reorganização local | Reduzir gradualmente o apoio (retirada progressiva); observar se o acerto se mantém |
| Acertos sucessivos sem ajuda | Indício de autonomia | Retirar retornos visuais adicionais; manter só condução mínima (N0) |

A distinção "erro de sinal vs. erro de posição" é a mais citada no relatório
como fonte de ajuda mal focalizada no protótipo em papel: oferecer feedback
de sinal quando o problema real é posição faz o usuário "corrigir" o sinal e
persistir no erro (ver protocolo do usuário 01, Tabela 21/22 do relatório) —
por isso a regra acima prioriza checar posição antes de sinal.

## Indícios de reorganização após ajuda (relatório 2026, Tabelas 51/52)

Usados para decidir se o nível de ajuda deve ser mantido, intensificado ou
retirado — é o sinal de retroalimentação que o ZDP usa para regular sua
própria escolha de camada (N0–N7) ao longo da interação, e que também
alimenta o Modelo do Usuário (Agente Modelador):

| Resposta após ajuda | Interpretação | Implicação para o próximo nível |
|---|---|---|
| Manutenção do erro | Ajuda não alterou a hipótese/regra de ação | Reformular a ajuda, não repetir a mesma mensagem |
| Repetição do erro (em nova tentativa ou problema semelhante) | Possível teorema-em-ato falso persistente | Registrar recorrência; elevar nível de scaffolding |
| Correção local | Reorganização pontual, ainda dependente do suporte | Manter apoio temporário; observar estabilidade |
| Revisão com justificativa | Reorganização mais forte (ação + explicitação conceitual) | Reduzir gradualmente a ajuda |
| Estabilização após retirada de apoio | Maior autonomia, possível consolidação | Retirar scaffolding, manter só monitoramento |
| Oscilação entre acerto e erro | Reorganização instável, sem domínio consistente | Manter apoio intermediário; propor novas situações para verificar estabilidade |

**Cautela explícita do relatório**: um acerto isolado após ajuda é evidência
fraca de reorganização conceitual — pode ser tentativa e erro, memória da
mensagem anterior, ou ajuste local à interface. Só junto com justificativa
verbal coerente (não disponível automaticamente numa interface real, só no
protocolo com pesquisador presente) a evidência fica mais forte. **Isto é uma
limitação real para a implementação**: sem entrevista/think-aloud, o ZDP em
produção só tem a ação observável (certo/errado + recorrência), não a
justificativa verbal — então deve tratar acertos isolados com a mesma cautela
que o relatório recomenda, sem superestimar reorganização a partir de um
único acerto.

## Nota de honestidade do próprio material

A operacionalização da ZDP e o uso de dados quantitativos que confirmem que
a instrução está de fato "na ZDP" ficam para trabalho futuro — mesmo na tese
original, essa parte é reconhecidamente incompleta. O relatório de 2026
avança bastante aqui (camadas N0–N7, regras condição→ação, escala de
indícios pós-ajuda), mas ele próprio insiste, na seção de limitações, que:
os dados vêm de protótipo em papel com intervenção humana simulando o
sistema; a validação foi formativa, não operacional; e as regras "devem ser
entendidas como uma contribuição exploratória... exigindo estudos
posteriores com... implementação computacional dos agentes e avaliação
empírica". Tratar como ponto de partida sólido para desenhar o ZDP, não como
especificação já validada.
