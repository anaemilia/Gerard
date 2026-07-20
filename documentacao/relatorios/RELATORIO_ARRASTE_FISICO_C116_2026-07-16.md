# C116 — Contorno de origem e seguimento elástico no arraste

## Objetivo

Acrescentar sensação de continuidade física durante o arraste sem modificar a
semântica, as áreas válidas de posicionamento, a sincronização entre
representações ou os registros já existentes.

## Alterações

1. Durante o pickup, o local inicial recebe um contorno fantasma tracejado.
2. O elemento acompanha o cursor por uma mola amortecida, em vez de copiar
   instantaneamente cada coordenada recebida.
3. O atraso visual é limitado a 26 pixels para permanecer discreto.
4. Na soltura, a posição é concluída exatamente na coordenada do cursor antes
   da validação do destino. Assim, o efeito visual não reduz a precisão.
5. O marcador é desenhado acima da cena regular e abaixo do elemento elevado.
6. A solução foi aplicada a elementos textuais, itens semânticos,
   quadradinhos, figuras e conectores de Vergnaud, ponto de comparação e
   ponto/painel do eixo inteiro.

## Arquitetura

A implementação mantém a diretriz de contratos, herança e polimorfismo:

- `ControladorArrasteElastico` e `OuvinteArrasteElastico`: contratos de
  movimento;
- `ControladorArrasteElasticoAbstrato`: ciclo, temporizador, velocidade e
  conclusão exata;
- `ControladorArrasteElasticoMola`: parâmetros concretos da mola;
- `MarcadorOrigemArraste` e `DesenhavelFantasmaOrigem`: contratos do buraco de
  origem;
- `MarcadorOrigemArrasteAbstrato`: apresentação compartilhada;
- `MarcadorOrigemArrasteTracejado`: estilo concreto discreto.

## Preservações

Não foram alterados:

- cálculos e valores semânticos;
- limites curados de unidades;
- incremento unitário da comparação;
- regras de proximidade e snap;
- feedback multissensorial de posicionamento;
- edição por duplo clique;
- cursor de mão no mouseover e cursor de movimentação no arraste;
- escala, elevação, sombra e prioridade visual do pickup;
- logs de interação e sincronização compartilhada.

## Verificações

- compilação Ant/NetBeans: aprovada;
- teste isolado da mola e do marcador: aprovado;
- teste Swing de pickup, atraso limitado e soltura exata: aprovado;
- testes anteriores de pickup e cursores: aprovados;
- adição, remoção, limite e renderização de unidades: aprovados;
- mapeamento visual-semântico da comparação: aprovado;
- feedback multissensorial, ajuda contextual e inicialização: aprovados;
- análise após posicionamento e estado semântico compartilhado: aprovados;
- regressão estrutural geral e vínculos de traduções: aprovados.

A inspeção visual foi realizada em renderização Swing virtual. Não houve
inspeção manual completa em sessão Windows.
