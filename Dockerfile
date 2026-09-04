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

# lib/*.jar não é versionado (ver scripts/baixar_dependencias.sh) — baixamos
# aqui as mesmas duas dependências externas, com verificação de checksum,
# em vez de depender de um lib/ já presente no checkout (que não existe num
# clone limpo, como o que o Render usa para o build).
RUN command -v curl >/dev/null 2>&1 || (apt-get update && apt-get install -y --no-install-recommends curl ca-certificates && rm -rf /var/lib/apt/lists/*)
RUN mkdir -p lib \
    && curl -sL --fail --max-time 120 -o lib/weka-stable-3.8.6.jar \
         https://repo1.maven.org/maven2/nz/ac/waikato/cms/weka/weka-stable/3.8.6/weka-stable-3.8.6.jar \
    && echo "932ea2f342b58fe45736389e9c426d5b5955e610d1f5f6485117f532068915e9  lib/weka-stable-3.8.6.jar" | sha256sum -c - \
    && curl -sL --fail --max-time 120 -o lib/bounce-0.18.jar \
         https://repo1.maven.org/maven2/nz/ac/waikato/cms/weka/thirdparty/bounce/0.18/bounce-0.18.jar \
    && echo "bffff1505335c02256b7ab2ccffbe4aa4d3ac9fe14c17557809b7c9d99d666ca  lib/bounce-0.18.jar" | sha256sum -c -
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
