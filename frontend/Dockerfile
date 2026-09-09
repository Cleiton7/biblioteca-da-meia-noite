# ============================
# Stage 1: Build da aplicação Angular
# ============================
FROM node:22-alpine AS build

WORKDIR /app

# Copia arquivos de manifesto e instala dependências (aproveita cache)
COPY package.json package-lock.json ./
RUN npm ci --legacy-peer-deps

# Copia o restante do código-fonte
COPY . .

# Gera o build de produção
RUN npm run build -- --configuration production

# ============================
# Stage 2: Imagem de execução (Nginx)
# ============================
FROM nginx:alpine

# Remove a configuração default do Nginx
RUN rm -rf /usr/share/nginx/html/* && rm -f /etc/nginx/conf.d/default.conf

# Copia a configuração customizada do Nginx (proxy reverso + SPA fallback)
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copia os arquivos estáticos gerados pelo build do Angular
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
