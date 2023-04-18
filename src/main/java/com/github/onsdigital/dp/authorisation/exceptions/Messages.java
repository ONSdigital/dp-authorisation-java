package com.github.onsdigital.dp.authorisation.exceptions;

/**
 * exception messages.
 */
public class Messages {
    public static final String PERMISSIONS_API_FAILED_EXCEPTION =
            "unexpected status returned from the permissions api permissions-bundle endpoint";

    protected Messages() {
        // prevents calls from subclass
        throw new UnsupportedOperationException();
    }


}
