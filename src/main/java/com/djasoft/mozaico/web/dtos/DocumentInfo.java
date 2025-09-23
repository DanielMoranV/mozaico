package com.djasoft.mozaico.web.dtos;

import com.djasoft.mozaico.domain.enums.usuario.TipoDocumentoIdentidad;

public interface DocumentInfo {
    TipoDocumentoIdentidad getTipoDocumentoIdentidad();
    String getNumeroDocumento();
}
