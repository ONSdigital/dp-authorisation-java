package com.github.onsdigital.dp.authorisation.permissions;

import static com.github.onsdigital.logging.v2.event.SimpleEvent.warn;

import com.github.onsdigital.UserDataPayload;
import com.github.onsdigital.dp.authorisation.permissions.models.Bundle;
import com.github.onsdigital.dp.authorisation.permissions.models.Condition;
import com.github.onsdigital.dp.authorisation.permissions.models.EntityIDToPolicies;
import com.github.onsdigital.dp.authorisation.permissions.models.Policy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.joda.time.Duration;


/**
 * PermissionChecker.
 */
public class PermissionChecker {
  Cache cache;

  /**
   * PermissionChecker.
   *
   * @param permissionsAPIHost - permissionsAPIHost
   * @param cacheUpdateInterval - cacheUpdateInterval
   * @param expiryCheckInterval - expiryCheckInterval
   * @param maxCacheTime - maxCacheTime
   */
  public PermissionChecker(String permissionsAPIHost, Duration cacheUpdateInterval,
                           Duration expiryCheckInterval, Duration maxCacheTime) {
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

  /**
   * hasPermission returns true if one of the given entities has the given permission.
   *
   * @param userData   - ID of the caller (user or service), as well as any associated groups
   * @param permission - the action or permission the user wants to take, e.g. `datasets:edit`
   * @param attributes - other key value attributes for use in access control decision,
   *                   e.g. `collectionID`, `datasetID`, `isPublished`, `roleId`, etc
   *
   * @return boolean - authorisation status
   *
   * @throws Exception -
   */
  public Boolean hasPermission(UserDataPayload userData,
                               String permission, Map<String, String> attributes) throws Exception {
    List<String> entities = mapEntityDataToEntities(userData);
    return hasPermission(entities, permission, attributes);
  }

  private Boolean hasPermission(List<String> entities,
                                String permission,
                                Map<String, String> attributes) throws Exception {

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
      if (conditionIsMet(policy.condition, attributes)) {
        return true;
      }
    }

    return false;
  }

  Boolean conditionIsMet(Condition condition, Map<String, String> attributes) {
    if (condition == null) {
      return true;
    }
    if (!attributes.containsKey(condition.getAttribute())) {
      return false;
    }

    String value = attributes.get(condition.getAttribute());
    for (String conditionValue : condition.getValues()) {
      if (condition.getOperator().equals(Constants.OPERATOR_STRING_EQUALS)
              && value.equals(conditionValue)) {
        return true;
      }
      if (condition.getOperator().equals(Constants.OPERATOR_STARTS_WITH)
              && value.startsWith(conditionValue)) {
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
