# Relatório de alteração - limite semântico das coleções manipuláveis

**Data:** 2026-07-16  
**Ciclo:** C105  
**Tipo de intervenção:** estrutural e de generalização.

## Síntese
A criação incremental de quadradinhos passa a respeitar o valor semântico curado de cada papel. A cardinalidade visual não pode ultrapassar a quantidade definida na situação-problema. Quando o papel desconhecido está representado por `?`, o limite é derivado dos outros dois papéis segundo a relação semântica da categoria.

## Comportamento
- antes de adicionar, o sistema compara a quantidade atual com o limite curado;
- ao acrescentar a unidade que completa o valor, ou ao tentar ultrapassá-lo, a coleção recebe tremor leve, som sutil e o tooltip `Foi atingida a quantidade que consta no texto`;
- a adição excedente é bloqueada;
- a mensagem desaparece quando o usuário remove uma unidade ou muda o contexto;
- a regra foi generalizada para toda categoria cuja representação complementar exiba quadradinhos;
- sem valor numérico curado ou derivável, o sistema não inventa um limite.

## Verificação
- teste puro da resolução de limites: aprovado;
- compilação Ant/NetBeans: aprovada.
