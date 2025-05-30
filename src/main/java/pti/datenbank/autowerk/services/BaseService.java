package pti.datenbank.autowerk.services;

import pti.datenbank.autowerk.enums.Permission;

public abstract class BaseService {
    protected final AuthService authService;

    public BaseService(AuthService authService) {
        this.authService = authService;
    }

    protected void checkPermission(Permission perm) {
        if (!authService.hasPermission(perm)) {
            throw new SecurityException("Недостаточно прав: " + perm);
        }
    }
}