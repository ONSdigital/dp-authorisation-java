package com.github.onsdigital.dp.authorisation.permissions;

import com.github.onsdigital.dp.authorisation.permissions.models.Bundle;

/**
 * Store.
 */
public interface Store {

    Bundle getPermissionsBundle() throws Exception;
}
