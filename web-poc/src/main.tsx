import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import App from "./App";
import "../styles.css";

createRoot(document.getElementById("root")!).render(<StrictMode><App /></StrictMode>);

// Gatilho best-effort de fim de sessão ao fechar a aba, complementando o
// envio periódico do log de pesquisa (ver ServidorPrototipoWeb.java). sem
// garantia de entrega (limitação real do navegador, não escolha nossa) —
// pagehide é o evento mais confiável disponível para isso.
window.addEventListener("pagehide", () => {
  navigator.sendBeacon("/api/pesquisa/log/sincronizar");
});
