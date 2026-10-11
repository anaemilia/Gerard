// Selo de erro da modelagem (SeloErroModelagem, desktop): círculo vermelho vazio com um X, sem texto, puramente visual.
// Quem decide quando aparece é o domínio (cena.estado_feedback === "ERRO"); aqui só se realiza a geometria do desktop
// (círculo de diâmetro 44 e cruz com alcance de 11).
export function SeloErro() {
  return <div className="selo-erro">
    <svg viewBox="0 0 44 44" aria-hidden="true" focusable="false">
      <circle cx="22" cy="22" r="20" />
      <path d="M11 11L33 33M33 11L11 33" strokeWidth="3" />
    </svg>
  </div>;
}
