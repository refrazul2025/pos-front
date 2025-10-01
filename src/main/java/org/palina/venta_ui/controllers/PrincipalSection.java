package org.palina.venta_ui.controllers;

import org.palina.venta_ui.dto.OutletDto;
import org.palina.venta_ui.dto.UserDto;

public interface PrincipalSection {

    void setUsuario(UserDto usuario);
    void setTienda(OutletDto tienda);
    void initData();
}
