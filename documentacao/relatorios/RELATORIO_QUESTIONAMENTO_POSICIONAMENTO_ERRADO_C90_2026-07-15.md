# Relatório de alteração — feedback multissensorial de posicionamento semântico incorreto

**Data:** 2026-07-15  
**Ciclo:** C90  
**Tipo de intervenção:** scaffolding pedagógico e feedback de erro.

## Síntese
Quando o usuário tenta posicionar um número ou uma interrogação em um papel semântico incompatível, o Gérard mantém o elemento na cena e combina três sinais discretos: tremor curto, som padrão sutil do sistema e tooltip contextual ao lado do elemento manipulado.

## Mensagem
A mensagem segue o padrão:

> Tem certeza que esse número corresponde ao {nome do elemento semântico} da {categoria escolhida}?

O nome do papel semântico e da categoria é obtido dinamicamente da situação selecionada.

## Comportamento
- o item permanece visível na posição tentada;
- o item executa um tremor horizontal leve e curto;
- o sistema emite um único aviso sonoro sutil por tentativa incorreta;
- a pergunta aparece ao lado do item que está sendo posicionado;
- ao mover o item para um papel semanticamente correto, ou retirar a tentativa da posição incompatível, a mensagem desaparece;
- posicionamentos incorretos não atualizam as demais representações reativas;
- a avaliação utiliza a mesma infraestrutura genérica para todas as categorias aditivas suportadas.

## Arquitetura
O efeito multissensorial foi isolado no módulo:

`gerard.Scaffolding.feedbackerro.ScaffoldingFeedbackMultissensorialErro`

A avaliação dos papéis continua concentrada em:

`gerard.Scaffolding.questionamento.ScaffoldingQuestionamento`

## Verificações
- compilação Ant/NetBeans;
- regressão estrutural, internacionalização, vínculos e logs;
- teste específico dos papéis semânticos nas oito categorias;
- inspeção automatizada da presença do tremor, som, tooltip ancorado no item e remoção da mensagem após correção.
