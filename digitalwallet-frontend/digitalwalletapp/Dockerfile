# 1. AŞAMA: Build 

FROM node:24-alpine AS build
WORKDIR /app


COPY package*.json ./

RUN npm install

# Tüm kaynak kodlarını kopyala
COPY . .

RUN npm run build -- --configuration=production

#2. Aşama: Runtime
FROM nginx:alpine

# Nginx SPA (Single Page Application) ayarını kopyalıyoruz
COPY nginx.conf /etc/nginx/conf.d/default.conf

COPY --from=build /app/dist/digitalwalletapp/browser /usr/share/nginx/html

EXPOSE 80

#nginx sürekli çalışsın diyoruz 
#Bu satır olmasaydı veya sadece nginx yazsaydık caontainer başlar başlamaz exited 0 hatasıyla kapanırdı.
CMD ["nginx", "-g", "daemon off;"]