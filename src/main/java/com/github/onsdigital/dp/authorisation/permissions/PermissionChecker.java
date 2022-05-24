package com.github.onsdigital.dp.authorisation.permissions;

import com.github.onsdigital.dp.authorisation.permissions.models.*;
import com.github.onsdigital.impl.UserDataPayload;
import org.joda.time.Duration;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.github.onsdigital.logging.v2.event.SimpleEvent.warn;

public class PermissionChecker {
    Cache cache;

    public PermissionChecker(String permissionsAPIHost, Duration cacheUpdateInterval, Duration expiryCheckInterval, Duration maxCacheTime) {
        CachingStore cachingStore = new CachingStore(new APIClient(permissionsAPIHost));
        cachingStore.startCacheUpdater(cacheUpdateInterval);
        cachingStore.startExpiryChecker(expiryCheckInterval, maxCacheTime);
        cache = cachingStore;
    }

    public PermissionChecker(Cache cachingStore) {
        cache = cachingStore;
    }

    public void close() {
        cache.close();
    }

    // hasPermission returns true if one of the given entities has the given permission.
    // userData - ID of the caller (user or service), as well as any associated groups
    // permission - the action or permission the user wants to take, e.g. `datasets:edit`
    // attributes - other key value attributes for use in access control decision, e.g. `collectionID`, `datasetID`, `isPublished`, `roleId`, etc
    public Boolean hasPermission(UserDataPayload userData, String permission, Map<String, String> attributes) throws Exception {
        List<String> entities = mapEntityDataToEntities(userData);
        return hasPermission(entities, permission, attributes);
    }

    private Boolean hasPermission(List<String> entities, String permission, Map<String, String> attributes) throws Exception {

        Bundle permissionsBundle = cache.getPermissionsBundle();

        if (!permissionsBundle.containsKey(permission)) {
            warn().data("permission", permission).log("permission not found in permissions bundle");
            return false;
        }
        EntityIDToPolicies entityLookup = permissionsBundle.get(permission);

        for (String entity : entities) {
            if (!entityLookup.containsKey(entity)) {
                continue;
            }
            if (aPolicyApplies(entityLookup.get(entity), attributes)) {
                return true;
            }
        }

        return false;
    }

    Boolean aPolicyApplies(List<Policy> policies, Map<String, String> attributes) {
        if (policies == null || policies.size() == 0) {
            return false;
        }
        for (Policy policy : policies) {
            if (aConditionIsMet(policy.conditions, attributes)) {
                return true;
            }
        }

        return false;
    }

    Boolean aConditionIsMet(List<Condition> conditions, Map<String, String> attributes) {
        if (conditions == null || conditions.size() == 0) {
            return true;
        }

        for (Condition condition : conditions) {
            if (conditionIsMet(condition, attributes)) {
                return true;
            }
        }

        return false;
    }

    Boolean conditionIsMet(Condition condition, Map<String, String> attributes) {
        if (!attributes.containsKey(condition.attribute)) {
            return false;
        }

        String value = attributes.get(condition.attribute);
        for (String conditionValue : condition.values) {
            if (condition.operator.equals(Constants.OperatorStringEquals) && value.equals(conditionValue)) {
                return true;
            }
            if (condition.operator.equals(Constants.OperatorStartsWith) && value.startsWith(conditionValue)) {
                return true;
            }
        }

        return false;
    }
    List<String> mapEntityDataToEntities(UserDataPayload userData) {
        List<String> entities = new ArrayList<String>();
        if (userData.getEmail().length() > 0) {
            entities.add("users/" + userData.getEmail());
        }
        for (String group : userData.getGroups()) {
            if (group.length() > 0) {
                entities.add("groups/" + group);
            }
        }
        return entities;
    }
}
