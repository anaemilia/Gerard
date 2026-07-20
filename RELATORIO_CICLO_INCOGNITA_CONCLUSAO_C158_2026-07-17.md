# Relatório técnico — C158

## Objetivo

Generalizar a regra de conclusão para que o item desconhecido não seja revelado ou preenchido automaticamente antes da ação pedagógica esperada.

## Novo ciclo

- `INCOMPLETA`: faltam papéis, existem posicionamentos incorretos ou a incógnita não foi posicionada.
- `AGUARDANDO_PREENCHIMENTO_INCOGNITA`: todos os papéis estão corretos e o `?` ocupa o papel desconhecido, mas ainda não foi preenchido.
- `CONCLUIDA`: o próprio item originalmente desconhecido foi substituído por um número pelo protocolo de mouse/texto.

## Proteção contra preenchimento antecipado

O estado semântico compartilhado agora recebe o índice local da incógnita. Enquanto o protocolo não for concluído:

- o índice permanece desconhecido;
- a relação aditiva não calcula esse papel para exibição;
- o Vergnaud preserva `?`;
- a representação complementar não recebe unidades correspondentes à resposta;
- a edição direta do elemento vazio não substitui o ciclo do item `?`.

Depois do preenchimento explícito, o valor é propagado normalmente para texto, Vergnaud, eixo e representação complementar.

## Arquitetura

Novas classes:

- `gerard.campoaditivo.conclusao.FaseConclusaoModelagem`
- `gerard.campoaditivo.conclusao.PoliticaPreenchimentoIncognita`

Classes modificadas:

- `AvaliadorConclusaoModelagem`
- `ControladorConclusaoModelagem`
- `EstadoPosicionamentoModelagem`
- `EstadoSemanticoCompartilhado`
- `ItemTextoArrastavel`

A `Main.java` apenas conecta a política ao estado da interface, sem receber a regra matemática central.

## Validação automática

Foram validados:

- ciclo `?` posicionado → espera → preenchimento mouse/texto → conclusão;
- rejeição de valor automático sobre a incógnita;
- proteção de estado final e transformação desconhecidos;
- liberação após o protocolo;
- aplicação nas oito categorias;
- conclusão progressiva;
- estado semântico compartilhado;
- processo de transformação;
- valores monetários integrais;
- grandezas contextuais;
- regra do primeiro posicionamento;
- arraste físico;
- regressão estrutural consolidada.

## Limite da validação

Os testes foram executados em modo automatizado/headless. A interação real de duplo clique, abertura do campo e percepção visual deve ser confirmada na execução gráfica no Windows.
