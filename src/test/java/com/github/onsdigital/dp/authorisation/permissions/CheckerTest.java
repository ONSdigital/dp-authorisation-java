package com.github.onsdigital.dp.authorisation.permissions;


import com.github.onsdigital.UserDataPayload;
import com.github.onsdigital.dp.authorisation.permissions.models.Bundle;
import com.github.onsdigital.dp.authorisation.permissions.models.Condition;
import com.github.onsdigital.dp.authorisation.permissions.models.EntityIDToPolicies;
import com.github.onsdigital.dp.authorisation.permissions.models.Policy;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

public class CheckerTest {

    Bundle permissionsBundle;


    @Mock
    private CachingStore cachingStore;

    private PermissionChecker checker;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        permissionsBundle = new Bundle("users.add", new EntityIDToPolicies("groups/admin", new Policy("policy1")));

        EntityIDToPolicies legacyRead = new EntityIDToPolicies("groups/admin", new Policy("policy3", new Condition[]{}));
        legacyRead.put("groups/viewer", new ArrayList<Policy>(Arrays.asList(
                new Policy("policy2", new Condition[]{
                        new Condition("collection_id", Constants.OperatorStringEquals, "collection765"),
                        new Condition("collection_id", Constants.OperatorStringEquals, "collection767"),
                        new Condition("collection_id", "stringequals", "collection768"),

                })
        )));
        permissionsBundle.put("legacy.read", legacyRead);

        permissionsBundle.put("some_service.write", new EntityIDToPolicies("groups/publisher", new Policy("policy7",
                new Condition[]{
                        new Condition("path", Constants.OperatorStartsWith, "/files/dir/a/"),
                        new Condition("path", Constants.OperatorStartsWith, "/files/dir/b")}
        )));

        when(cachingStore.getPermissionsBundle()).

                thenReturn(permissionsBundle);
        this.checker = new

                PermissionChecker(cachingStore);

    }

    @Test
    public void testHasPermission() throws Exception {
        Boolean hasPermission = checker.hasPermission(
                new UserDataPayload("userId", "userEmail", Arrays.asList("admin")), "users.add", null
        );

        assertThat(hasPermission, equalTo(true));
    }

    @Test
    public void testHasPermission_false() throws Exception {

        Boolean hasPermission = checker.hasPermission(
                new UserDataPayload("userId", "userEmail", Arrays.asList("publisher")), "users.add", null
        );

        assertThat(hasPermission, equalTo(false));
    }


    @Test
    public void testHasPermission_noGroupMatch() throws Exception {

        Boolean hasPermission = checker.hasPermission(
                new UserDataPayload("userId","userEmail", Arrays.asList("default")), "legacy.read", null
        );

        assertThat(hasPermission, equalTo(false));

    }

    @Test
    public void testHasPermission_withStringEqualsConditionTrue() throws Exception {
        HashMap<String, String> attributes = new HashMap<String, String>() {{
            put("collection_id", "collection765");
        }};

        Boolean hasPermission = checker.hasPermission(
                new UserDataPayload("userId","userEmail", Arrays.asList("viewer")), "legacy.read", attributes
        );

        assertThat(hasPermission, equalTo(true));
    }


    @Test
    public void testHasPermission_withStringEqualsConditionFalse() throws Exception {
        HashMap<String, String> attributes = new HashMap<String, String>() {{
            put("collection_id", "collection999");
        }};

        Boolean hasPermission = checker.hasPermission(
                new UserDataPayload("userId","userEmail", Arrays.asList("viewer")), "legacy.read", attributes
        );

        assertThat(hasPermission, equalTo(false));
    }


    @Test
    public void testHasPermission_withCaseInsensitivePolicyConditionOperatorFalse() throws Exception {

        HashMap<String, String> attributes = new HashMap<String, String>() {{
            put("collection_id", "collection768");
        }};

        Boolean hasPermission = checker.hasPermission(
                new UserDataPayload("userId","userEmail", Arrays.asList("viewer")), "legacy.read", attributes
        );

        assertThat(hasPermission, equalTo(false));
    }


    @Test
    public void testHasPermission_withStartsWithConditionTrue() throws Exception {
        HashMap<String, String> attributes = new HashMap<String, String>() {{
            put("path", "/files/dir/a/some/dir/");
        }};

        Boolean hasPermission = checker.hasPermission(
                new UserDataPayload("userId","userEmail", Arrays.asList("publisher")), "some_service.write", attributes
        );

        assertThat(hasPermission, equalTo(true));
    }


    @Test
    public void testHasPermission_withStartsWithConditionFalse() throws Exception {

        HashMap<String, String> attributes = new HashMap<String, String>() {{
            put("path", "/files/dir/c/some/dir");
        }};

        Boolean hasPermission = checker.hasPermission(
                new UserDataPayload("userId","userEmail", Arrays.asList("publisher")), "some_service.write", attributes
        );

        assertThat(hasPermission, equalTo(false));
    }

    @Test
    public void testHasPermission_multipleConditionsChecked() throws Exception {

        HashMap<String, String> attributes = new HashMap<String, String>() {{
            put("collection_id", "collection767");
        }};

        Boolean hasPermission = checker.hasPermission(
                new UserDataPayload("userId", "userEmail", Arrays.asList("viewer")), "legacy.read", attributes
        );

        assertThat(hasPermission, equalTo(true));

    }
}
