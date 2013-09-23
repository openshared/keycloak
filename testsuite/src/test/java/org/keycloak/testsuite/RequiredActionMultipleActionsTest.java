/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.keycloak.testsuite;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.testsuite.pages.LoginPasswordUpdatePage;
import org.keycloak.testsuite.pages.LoginUpdateProfilePage;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
@RunWith(Arquillian.class)
public class RequiredActionMultipleActionsTest extends AbstractDroneTest {

    @Deployment(name = "properties", testable = false, order = 1)
    public static WebArchive propertiesDeployment() {
        return ShrinkWrap.create(WebArchive.class, "properties.war").addClass(SystemPropertiesSetter.class)
                .addAsWebInfResource("web-properties-email-verfication.xml", "web.xml");
    }

    @Rule
    public GreenMailRule greenMail = new GreenMailRule();

    @Page
    protected LoginPasswordUpdatePage changePasswordPage;

    @Page
    protected LoginUpdateProfilePage updateProfilePage;

    @After
    public void after() {
        appPage.open();
        if (appPage.isCurrent()) {
            appPage.logout();
        }
    }

    @Test
    public void updateProfileAndPassword() {
        appPage.open();

        Assert.assertTrue(loginPage.isCurrent());

        loginPage.login("multiple@actions.com", "temp-password");

        if (changePasswordPage.isCurrent()) {
            updatePassword();

            Assert.assertTrue(updateProfilePage.isCurrent());
            updateProfile();
        } else if (updateProfilePage.isCurrent()) {
            updateProfile();

            Assert.assertTrue(changePasswordPage.isCurrent());
            updatePassword();
        } else {
            Assert.fail("Expected to update password and profile before login");
        }

        Assert.assertTrue(appPage.isCurrent());
        Assert.assertEquals("multiple@actions.com", appPage.getUser());

    }

    public void updatePassword() {
        changePasswordPage.changePassword("new-password", "new-password");
    }

    public void updateProfile() {
        updateProfilePage.update("New first", "New last", "new@email.com");
    }

}
