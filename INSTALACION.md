# Instalación y Configuración de Mozaico

## 📋 Requisitos Previos

Antes de compilar y ejecutar el proyecto, asegúrate de tener instalado:

- **Java 21** o superior
- **PostgreSQL** (corriendo en localhost:5432)
- **Base de datos**: `mozaico_db`

## 🔨 Compilar el Proyecto

Para compilar el proyecto y generar el archivo JAR ejecutable:

```bash
./mvnw clean package
```

Si quieres omitir las pruebas (más rápido):

```bash
./mvnw clean package -DskipTests
```

El archivo JAR se generará en: `target/mozaico-0.0.1-SNAPSHOT.jar`

## 🚀 Formas de Ejecutar la Aplicación

### Opción 1: Ejecución Manual

Ejecutar directamente con Maven:
```bash
./mvnw spring-boot:run
```

O ejecutar el JAR directamente:
```bash
java -jar target/mozaico-0.0.1-SNAPSHOT.jar
```

### Opción 2: Usando el Script de Inicio

Usar el script que creamos:
```bash
./start-mozaico.sh
```

Este script:
- Verifica que el JAR existe
- Verifica que PostgreSQL está corriendo
- Crea logs en `logs/mozaico.log`
- Inicia la aplicación

### Opción 3: Como Servicio del Sistema (Auto-inicio al encender la PC)

Esta es la opción recomendada para que la aplicación se inicie automáticamente.

#### Instalación del Servicio

1. **Instalar el servicio:**
   ```bash
   sudo ./install-service.sh
   ```

2. El script te preguntará si quieres iniciar el servicio inmediatamente.

#### Comandos del Servicio

Una vez instalado, puedes usar estos comandos:

```bash
# Iniciar el servicio
sudo systemctl start mozaico

# Detener el servicio
sudo systemctl stop mozaico

# Reiniciar el servicio
sudo systemctl restart mozaico

# Ver el estado del servicio
sudo systemctl status mozaico

# Ver los logs en tiempo real
journalctl -u mozaico -f

# O ver los logs del archivo
tail -f logs/mozaico.log

# Deshabilitar auto-inicio (si ya no lo quieres)
sudo systemctl disable mozaico

# Habilitar auto-inicio nuevamente
sudo systemctl enable mozaico
```

## 🌐 Acceder a la Aplicación

Una vez que la aplicación esté corriendo:

- **API REST**: http://localhost:8091
- **Swagger UI** (Documentación interactiva): http://localhost:8091/swagger-ui.html
- **API Docs**: http://localhost:8091/api-docs

## 📝 Logs

Los logs se guardan en:
- `logs/mozaico.log` - Logs de la aplicación
- `logs/mozaico-error.log` - Logs de errores (cuando se ejecuta como servicio)

## 🔄 Recompilar y Actualizar

Si haces cambios en el código:

1. **Detener el servicio** (si está corriendo):
   ```bash
   sudo systemctl stop mozaico
   ```

2. **Recompilar**:
   ```bash
   ./mvnw clean package -DskipTests
   ```

3. **Reiniciar el servicio**:
   ```bash
   sudo systemctl start mozaico
   ```

O todo en un solo comando:
```bash
sudo systemctl stop mozaico && ./mvnw clean package -DskipTests && sudo systemctl start mozaico
```

## 🗄️ Configuración de Base de Datos

La configuración de la base de datos está en `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/mozaico_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```

Asegúrate de que PostgreSQL esté corriendo y la base de datos exista:

```bash
# Verificar que PostgreSQL está corriendo
pg_isready

# Crear la base de datos (si no existe)
sudo -u postgres psql -c "CREATE DATABASE mozaico_db;"
```

## ⚙️ Configuración Avanzada del Servicio

Si necesitas modificar la configuración del servicio, edita el archivo:
```bash
sudo nano /etc/systemd/system/mozaico.service
```

Después de cualquier cambio, recarga systemd:
```bash
sudo systemctl daemon-reload
sudo systemctl restart mozaico
```

## 🛠️ Solución de Problemas

### El servicio no inicia

1. Verifica los logs:
   ```bash
   journalctl -u mozaico -n 50
   ```

2. Verifica que PostgreSQL esté corriendo:
   ```bash
   sudo systemctl status postgresql
   ```

3. Verifica que el JAR existe:
   ```bash
   ls -lh target/mozaico-0.0.1-SNAPSHOT.jar
   ```

### Puerto 8091 ya está en uso

1. Ver qué proceso está usando el puerto:
   ```bash
   sudo lsof -i :8091
   ```

2. Detener Mozaico si está corriendo:
   ```bash
   sudo systemctl stop mozaico
   ```

### Recompilar después de cambios

```bash
# Detener, recompilar e iniciar
sudo systemctl stop mozaico
./mvnw clean package -DskipTests
sudo systemctl start mozaico
sudo systemctl status mozaico
```

## 📚 Archivos Creados

- `start-mozaico.sh` - Script de inicio manual
- `mozaico.service` - Archivo de configuración del servicio systemd
- `install-service.sh` - Script de instalación del servicio
- `INSTALACION.md` - Este archivo de documentación

## ✅ Verificación de la Instalación

Para verificar que todo está funcionando:

```bash
# 1. Verificar que el servicio está activo
sudo systemctl status mozaico

# 2. Verificar que la aplicación responde
curl http://localhost:8091/api-docs

# 3. Ver los logs
tail -20 logs/mozaico.log
```

Si todo está bien, deberías ver que el servicio está "active (running)" y la aplicación responde en el puerto 8091.
