# Regras arquiteturais do Gérard para agentes

## Composição obrigatória das skills

Nenhuma skill do Gérard deve ser interpretada isoladamente quando a tarefa
cruza mais de um tipo de conhecimento.

Antes de decidir ou implementar:

1. identificar os conhecimentos afetados: domínio, representação, interação,
   geometria, consistência, scaffolding, registro factual, modelo do usuário
   e análise do pesquisador;
2. validar e percorrer `.agents/skills/dependencies.json` a partir das skills
   inicialmente afetadas;
3. ler integralmente as fontes descobertas que alcancem o escopo concreto;
4. separar responsabilidades por localidade do conhecimento;
5. reconciliar relações `conflicts` por escopo, estado verificado e decisões
   explícitas mais recentes da usuária;
6. não converter exemplos teóricos em classes, inferências ou comportamentos
   automáticos sem fundamento adicional.

Cada conhecimento novo deve ser registrado na skill proprietária. O grafo
possui as interdependências necessárias para preservar consistência, sem
duplicar as regras internas das skills.

## Grafo executável de consulta

Validar cobertura, caminhos, relações e ciclos:

```powershell
python scripts\verificar_dependencias_skills.py --validar
```

Gerar busca em largura, árvore da tarefa e ordem de leitura:

```powershell
python scripts\verificar_dependencias_skills.py --percorrer gerard-handlers-de-interacao
```

Relações `requires` e `constrains` participam obrigatoriamente da ordem de
leitura. Relações `related` são consultadas quando o escopo concreto alcançar
aquele conhecimento. Relações `conflicts` exigem reconciliação explícita.

## Proibição de deslocar concentração

Antes de criar ou renomear uma classe como `Controller`, `Controlador`,
`Service`, `Servico`, `Coordinator`, `Coordenador`, `Manager`, `Gerenciador`
ou equivalente genérico, o agente deve aplicar
`documentacao/specs/SPEC_LOCALIDADE_SEM_DESLOCAMENTO_DE_CONCENTRACAO.md`.

O nome nunca constitui justificativa arquitetural. Antes de editar, o agente
deve registrar para o recorte concreto:

1. conhecimento ou efeito;
2. proprietário autorizado;
3. entrada e saída;
4. porta usada;
5. adaptador tecnológico, se houver.

Se a responsabilidade da classe precisar combinar, na mesma frase,
interpretação semântica **e** apresentação, persistência ou scaffolding, a
implementação deve parar e redistribuir as responsabilidades. O mesmo vale
quando um serviço de aplicação conhecer simultaneamente UI concreta, logger
concreto ou scaffolding concreto.

Coordenação não é proibida: uma relação estrutural coordena os participantes
do conhecimento relacional que possui; um caso de uso apenas sequencia portas
e resultados já decididos. Nenhum deles adquire autoridade por transportar ou
orquestrar uma decisão de outro proprietário.

Reduzir linhas de `Main` não prova componentização. A alteração só está
concluída quando testes ou verificadores demonstram também as dependências
permitidas, a produção factual no proprietário e a ausência da mesma
concentração sob outro nome.
