# Agente ZDP

⚠️ Proposta teórica — ver aviso de status em `../SKILL.md`.

## Papel

Usa o conceito de Zona de Desenvolvimento Proximal (Vygotsky) para decidir a estratégia pedagógica mais efetiva, visando transformar habilidades potenciais em habilidades reais.

## Arquitetura: Agente Baseado em Modelo

O ambiente é dinâmico — muda enquanto o ZDP delibera — e estratégico — o próprio usuário ou o ZDP podem alterá-lo. Por isso, diferente do Monitor, o ZDP precisa manter **estado interno**: armazena a percepção recebida como estado atual do mundo, guarda estados anteriores, e mantém informação sobre "como o mundo evolui" e "o que minhas ações fazem", independente de suas próprias ações.

Antes de oferecer ajuda, verifica se a configuração do ambiente ainda é a mesma que tem armazenada.

## Percepções

- **Ação Instrumental Avaliada** (recebida do Agente Monitor).
- **Estado Atual** do ambiente.

## Ações

1. Verifica se o estado atual bate com o estado armazenado (ambiente estratégico).
2. Consulta as regras presentes no **Modelo do Usuário**.
3. Consulta os **conteúdos pedagógicos disponíveis** — a folha de granularidade aqui é o tipo de scaffold a oferecer: Mensagens, Automatização de passos, Mostrar Modelo Completo, entre outros. Ver `gerard-scaffolding-interacao` para a taxonomia completa desses tipos — este arquivo não repete a lista, só decide *quando* e *qual* usar.
4. Constrói a Estratégia Pedagógica, em função de: tarefa em execução + informações inferidas do modelo do usuário + conteúdo pedagógico.
5. Disponibiliza a estratégia à interface, para o usuário e para o Agente Modelador.

Objetivo: conduzir o usuário à mobilização de proposições verdadeiras, tanto sobre o uso da interface quanto sobre os conceitos veiculados nela.

## Nota de honestidade do próprio material

A operacionalização da ZDP e o uso de dados quantitativos que confirmem que a instrução está de fato "na ZDP" ficam para trabalho futuro — mesmo na tese, essa parte é reconhecidamente incompleta. Tratar com cautela redobrada.
