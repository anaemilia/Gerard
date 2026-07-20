# Relatório de aplicação gradual de padrões de projeto no Gérard

## Escopo

A refatoração foi aplicada de forma incremental, preservando a arquitetura local em Java/Swing e evitando abstrações extensas.

## Padrões aplicados

### Strategy — estilos de interação

O comportamento de proximidade, realce de alvo, feedback durante o arraste, affordance e atração para o alvo foi retirado da sequência de condicionais do scaffolding e centralizado em estratégias. O enum `EstiloInteracao` continua sendo a identificação pública do estilo, enquanto `EstrategiasEstiloInteracao` fornece seu comportamento.

### State simplificado — ciclo de vida da atividade

`AcaoAtividade` explicita quais ações reiniciam a modelagem e quais a preservam. `ControladorEstadoAtividade` centraliza essa política sem criar uma hierarquia complexa de classes de estado.

Reiniciam: inicialização, sorteio, seleção de categoria e restauração explícita.

Preservam: troca de idioma, estilo de interação, abertura/fechamento da curadoria, atualização textual e troca de representação.

### Facade — carregamento da atividade

`FachadaCarregamentoAtividade` centraliza a consulta ao repositório, a obtenção da definição da representação e a construção do resultado curado. A tela principal recebe um `ContextoCarregamentoAtividade` em vez de coordenar essas responsabilidades separadamente.

### Factory — representações

A fábrica existente de renderizadores aditivos foi preservada e incluída na regressão arquitetural. Não foi criada outra fábrica redundante.

## Decisões de simplicidade

Não foram introduzidos Observer global, Event Bus, Command generalizado, heranças extensas, microserviços ou novas dependências. A implementação responde apenas às responsabilidades que já estavam consolidadas no código.

## Invariantes preservados

- todos os idiomas usam as mesmas regras de exibição;
- representações preservam os mesmos papéis semânticos;
- troca de idioma ou estilo não apaga a modelagem;
- selecionar categoria, sortear ou restaurar inicia modelagem vazia;
- nenhum valor é inserido automaticamente em diagramas;
- o item desconhecido continua sendo `?` no enunciado e só entra no diagrama por ação do usuário.
