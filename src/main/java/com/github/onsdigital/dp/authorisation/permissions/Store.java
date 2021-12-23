package com.github.onsdigital.dp.authorisation.permissions;

import com.github.onsdigital.dp.authorisation.exceptions.BundleNotCached;
import com.github.onsdigital.dp.authorisation.permissions.models.Bundle;

import java.io.IOException;

public interface Store {

    Bundle getPermissionsBundle() throws Exception;
}
