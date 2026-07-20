# Relatório de regressão - aba Construir situação-problema

Data: 14 de julho de 2026

## Escopo verificado

- compilação integral do projeto com Ant;
- presença e posição da nova aba;
- carregamento de situações validadas e completamente preenchidas;
- geração de blocos corretos e não compatíveis;
- preservação dos valores do diagrama;
- distância semântica controlada;
- tratamento de sinais negativos;
- quantidade controlada de alternativas;
- seleção, retirada, reordenação, reinício e validação;
- isolamento e restauração do contexto do logger;
- igualdade visual entre todos os blocos;
- internacionalização em português, inglês e francês;
- regressão das funcionalidades anteriormente verificadas no Gérard;
- regressão documental do artigo de co-design.

## Testes automatizados

O script `scripts/testar_montagem_situacao.sh` executa:

1. `ant clean jar`;
2. compilação dos testes específicos;
3. teste headless do gerador e do logger;
4. teste de integração Swing sob Xvfb.

O teste de domínio contém 69 verificações sobre as três categorias iniciais, a reconstrução exata, os valores, unicidade dos blocos, quantidade de alternativas, rejeição de diagramas incompletos, sinais negativos e restauração de contexto.

O teste de interface verifica a criação das três abas, a posição da montagem e a alternância de contexto.

O script geral `scripts/verificar_regressao_gerard.py` continua responsável pelas invariantes históricas de compilação, internacionalização, vínculos, curadoria, logs, interface e papéis semânticos.

## Resultado

Os quatro controles foram aprovados:

- teste específico da montagem: **69 verificações aprovadas**;
- teste de interface: **aba integrada e alternância de contexto aprovadas**;
- regressão geral do Gérard: **compilação, vínculos, curadoria, log, internacionalização, usabilidade, padrões arquiteturais e papéis semânticos aprovados**;
- artigo de co-design: **33 ciclos gerados, sendo 17 estruturais, 7 teórico-metodológicos e 9 visuais/operacionais**;
- regressão documental do artigo: **3 decisões metodológicas refletidas no resumo, nos resultados e na discussão**;
- PDF do artigo: **24 páginas renderizadas e inspecionadas, sem cortes ou sobreposições observados nas páginas verificadas**.

A compilação integral produziu `dist/GerardNetBeans_D3_Leitura_Redes_Transicoes.jar` com 112 arquivos-fonte Java.

## Limites desta versão

- o escopo inicial compreende composição, transformação e comparação de medidas;
- somente situações validadas e com todos os valores preenchidos entram no sorteio;
- a qualidade pedagógica das formulações geradas deve continuar sendo observada em uso real, pois testes estruturais não substituem avaliação com participantes;
- novas categorias exigirão famílias próprias de blocos e novos testes de ambiguidade.
