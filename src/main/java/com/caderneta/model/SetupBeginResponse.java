package com.caderneta.model;

import java.util.List;

public record SetupBeginResponse(
        Boolean enableIncludCategoria,
        List<CategoryIconDTO> sugestaoCategoria,
        Boolean enableIncludInvoice
) {}
