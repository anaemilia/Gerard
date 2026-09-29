# Granularidade de eventos durante gesto contínuo

Leia esta referência ao alterar emissão de eventos durante arraste, retenção
de snapshots ou encerramento normal e defensivo de um gesto contínuo.

Atualizar uma representação a cada amostra do ponteiro não exige emitir novo
evento semântico por pixel. No controle de barras de
`COMPARACAO_MEDIDAS`, o estado e as representações são propagados a cada
movimento, mas `CONSISTENCIA_AUTOMATICA` retém somente o último `Snapshot` e é
gravado no término do gesto. Um novo pressionamento descarrega defensivamente
o evento pendente caso o término anterior tenha sido interrompido.

Essa é uma política de granularidade do registro, não uma regra matemática
nem um bloqueio da sincronização visual. A identidade do papel recalculado
permanece no estado semântico ou na relação proprietária; a apresentação
somente retém o fato já produzido. Amostras intermediárias podem integrar o
registro factual do gesto segundo `gerard-log-gestos-interacao`, mas não se
transformam automaticamente em ações instrumentais.

Status verificado em 2026-08-30: os métodos
`registrarLogConsistenciaAutomaticaSeHouve` e
`flushLogConsistenciaAutomaticaPendenteDoArrasteComparacao` implementam a
retenção, e o verificador determinístico protege os términos normal e
defensivo.
