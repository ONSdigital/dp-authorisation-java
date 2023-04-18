# dp-authorisation-java

Authorize user:

- Permissions check - the action that the user is taking will have a permission associated with it. The permissions
  check does a lookup to see if the requested permission is granted to the user, or the groups that the user belongs to.
  This functionality is within the `permissions` package. See
  the [package readme for more details](permissions/README.md)

### Usage

The permission check can be checked within a handler with more complex logic if needed.

#### Add authorisation within a handler

The service should parse the JWT token and the UserDataPayload that comes from the JWT could be stored in the request
content for later use within the handler.

Once the JWT token is parsed into UserDataPayload, it can be passed to the permissions checker to determine if the user
has access. It's likely at this point that additional data will be needed by the permissions checker to make a decision.
This is where the `attributes` parameter of the permissions checker is used - for example to set a collection ID:

```java
  String permission = "legacy.read";
  HashMap<String, String> attributes = new HashMap<String, String>() {{
        put("collection_id", "collection768");
  }};

  Boolean hasPermission = permissionChecker.hasPermission(userDataPayload, permission, attributes);

```
