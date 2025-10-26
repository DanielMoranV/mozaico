#!/bin/bash
# Script de instalación del servicio systemd para Mozaico

# Colores para output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # Sin color

echo -e "${BLUE}=== Instalación del Servicio Mozaico ===${NC}\n"

# Verificar que se ejecuta con sudo
if [ "$EUID" -ne 0 ]; then
    echo -e "${RED}Error: Este script debe ejecutarse con sudo${NC}"
    echo -e "Usa: ${YELLOW}sudo ./install-service.sh${NC}"
    exit 1
fi

# Verificar que el archivo JAR existe
if [ ! -f "/home/maeldev/Code/mozaico/target/mozaico-0.0.1-SNAPSHOT.jar" ]; then
    echo -e "${RED}Error: No se encontró el archivo JAR${NC}"
    echo -e "Compila el proyecto primero con: ${YELLOW}./mvnw clean package${NC}"
    exit 1
fi

# Crear directorio de logs
echo -e "${BLUE}1.${NC} Creando directorio de logs..."
mkdir -p /home/maeldev/Code/mozaico/logs
chown maeldev:maeldev /home/maeldev/Code/mozaico/logs

# Copiar archivo de servicio
echo -e "${BLUE}2.${NC} Copiando archivo de servicio a systemd..."
cp /home/maeldev/Code/mozaico/mozaico.service /etc/systemd/system/

# Recargar systemd
echo -e "${BLUE}3.${NC} Recargando configuración de systemd..."
systemctl daemon-reload

# Habilitar el servicio
echo -e "${BLUE}4.${NC} Habilitando servicio para auto-inicio..."
systemctl enable mozaico.service

echo -e "\n${GREEN}✓ Servicio instalado correctamente!${NC}\n"
echo -e "${YELLOW}Comandos útiles:${NC}"
echo -e "  Iniciar servicio:     ${GREEN}sudo systemctl start mozaico${NC}"
echo -e "  Detener servicio:     ${GREEN}sudo systemctl stop mozaico${NC}"
echo -e "  Reiniciar servicio:   ${GREEN}sudo systemctl restart mozaico${NC}"
echo -e "  Ver estado:           ${GREEN}sudo systemctl status mozaico${NC}"
echo -e "  Ver logs:             ${GREEN}journalctl -u mozaico -f${NC}"
echo -e "  Ver logs del archivo: ${GREEN}tail -f /home/maeldev/Code/mozaico/logs/mozaico.log${NC}"
echo -e "  Deshabilitar auto-inicio: ${GREEN}sudo systemctl disable mozaico${NC}"
echo -e "\n${BLUE}La aplicación se iniciará automáticamente al encender la PC${NC}"
echo -e "${YELLOW}¿Quieres iniciar el servicio ahora? (s/n)${NC}"
read -r respuesta

if [ "$respuesta" = "s" ] || [ "$respuesta" = "S" ]; then
    echo -e "\n${BLUE}Iniciando servicio...${NC}"
    systemctl start mozaico.service
    sleep 3
    systemctl status mozaico.service --no-pager
    echo -e "\n${GREEN}Servicio iniciado. Accede a: http://localhost:8091/swagger-ui.html${NC}"
else
    echo -e "\n${YELLOW}Para iniciar el servicio más tarde, usa:${NC} ${GREEN}sudo systemctl start mozaico${NC}"
fi
