# syntax=docker/dockerfile:1

# ---------- Etapa 1: build do frontend (web-poc) ----------
FROM node:22-slim AS frontend
WORKDIR /app/web-poc
COPY web-poc/package.json web-poc/package-lock.json ./
RUN npm ci
COPY web-poc/ ./
RUN npm run build

# ---------- Etapa 2: build do backend Java (domínio + servidor da prova web) ----------
FROM eclipse-temurin:11-jdk AS backend
WORKDIR /app
COPY src ./src
COPY lib ./lib
RUN mkdir -p build/classes \
    && find src -name '*.java' > /tmp/fontes.txt \
    && javac -encoding UTF-8 -source 8 -target 8 -nowarn \
         -d build/classes -cp "lib/*" @/tmp/fontes.txt \
    && find src -type f ! -name '*.java' | while read -r f; do \
         destino="build/classes/${f#src/}"; \
         mkdir -p "$(dirname "$destino")"; \
         cp "$f" "$destino"; \
       done

# ---------- Etapa 3: imagem final de execução ----------
FROM eclipse-temurin:11-jre
WORKDIR /app
COPY --from=backend /app/build/classes ./build/classes
COPY --from=backend /app/lib ./lib
COPY --from=frontend /app/web-poc/dist ./web-poc/dist

# O Render injeta a variável PORT; ServidorPrototipoWeb lê PORT (com 8080 como
# padrão local) e serve tanto os endpoints /api quanto os arquivos estáticos
# do frontend a partir de web-poc/dist.
ENV PORT=8080
EXPOSE 8080

CMD ["sh", "-c", "java -cp build/classes:lib/* gerard.infraestrutura.web.ServidorPrototipoWeb"]
