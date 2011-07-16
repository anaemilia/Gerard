# Marco histórico — Gerard versão 2011

Esta branch (`historico-2011`) não faz parte do histórico de desenvolvimento do Gerard atual. Ela existe só como referência: um instantâneo estanque do código-fonte **original** de 2011, autoria de Alex S. Gomes, Ana E. M. Queiroz, Maurício M. Braga e Kecia G. de Moura (TCC de estágio, UNIVASF), preservado antes de qualquer modificação.

## Procedência

O conteúdo aqui é uma cópia direta de `Estagio/Codigo-Fonte/CodigoFonte/gerard` e `Estagio/Executavel/Gerard.s3db`, encontrados no arquivo pessoal do projeto. É o código-fonte real gerado pelo NetBeans (não uma decompilação) — os cabeçalhos dos arquivos `.java` trazem o comentário padrão `/* To change this template, choose Tools | Templates ... */`, confirmando a autoria original, diferente da árvore decompilada (CFR) usada em outra cópia do projeto.

## Por que essa branch existe

O repositório atual (`main`/`ajuda-adaptativa` e demais branches de desenvolvimento) começou seu histórico de commits já numa versão evoluída (ciclo C166), sem nenhum commit que representasse de fato a versão 2011. Esta branch órfã preenche essa lacuna como marco histórico, sem se misturar ao histórico de desenvolvimento da versão atual — não há ancestralidade comum entre `historico-2011` e as demais branches, de propósito.

## O que NÃO fazer com esta branch

- Não dar merge dela em `main`/`ajuda-adaptativa` ou qualquer branch de desenvolvimento ativo — são bases de código completamente diferentes.
- Não modificar o conteúdo aqui — se for necessário corrigir algo, documente a correção separadamente; o valor desta branch está em preservar o estado original.

## Onde está a versão 2011 estabilizada (buildável)

Uma versão modernizada apenas na parte de build/empacotamento (Maven, driver SQLite atualizado, alguns bugs de exceção silenciosa corrigidos, mas com a mesma arquitetura e funcionalidades de 2011) foi preparada separadamente para depósito no repositório institucional da UNIVASF, em `Gerard_estagio_fonte` (fora deste repositório).
