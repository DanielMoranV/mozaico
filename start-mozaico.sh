#!/bin/bash
# Script de inicio para Mozaico
# Este script inicia la aplicación Spring Boot

# Colores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # Sin color

# Directorio base del proyecto
PROJECT_DIR="/home/maeldev/Code/mozaico"
JAR_FILE="$PROJECT_DIR/target/mozaico-0.0.1-SNAPSHOT.jar"
LOG_DIR="$PROJECT_DIR/logs"
LOG_FILE="$LOG_DIR/mozaico.log"

# Crear directorio de logs si no existe
mkdir -p "$LOG_DIR"

echo -e "${GREEN}Iniciando Mozaico...${NC}"

# Verificar que el archivo JAR existe
if [ ! -f "$JAR_FILE" ]; then
    echo -e "${RED}Error: No se encontró el archivo JAR en $JAR_FILE${NC}"
    echo -e "${RED}Por favor, compile el proyecto primero con: ./mvnw clean package${NC}"
    exit 1
fi

# Verificar que PostgreSQL está corriendo
if ! pg_isready -h localhost -p 5432 > /dev/null 2>&1; then
    echo -e "${RED}Advertencia: PostgreSQL no parece estar corriendo en localhost:5432${NC}"
    echo -e "${RED}La aplicación podría no iniciar correctamente.${NC}"
fi

# Iniciar la aplicación
echo "Iniciando aplicación en puerto 8091..."
echo "Logs en: $LOG_FILE"
echo "Swagger UI: http://localhost:8091/swagger-ui.html"
echo ""

# Ejecutar el JAR
cd "$PROJECT_DIR"
java -jar "$JAR_FILE" >> "$LOG_FILE" 2>&1
