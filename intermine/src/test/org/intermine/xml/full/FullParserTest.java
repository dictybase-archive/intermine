package org.flymine.xml.full;

/*
 * Copyright (C) 2002-2003 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import junit.framework.TestCase;

import java.io.InputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import org.flymine.model.testmodel.*;
import org.flymine.model.fulldata.Attribute;
import org.flymine.model.fulldata.Identifier;
import org.flymine.model.fulldata.Item;
import org.flymine.model.fulldata.Reference;
import org.flymine.model.fulldata.ReferenceList;
import org.flymine.metadata.Model;

public class FullParserTest extends TestCase
{
    private List exampleItems;

    public FullParserTest(String arg) {
        super(arg);
    }

    public void setUp() throws Exception {
        Item item1 = new Item();
        item1.setImplementations("http://www.flymine.org/model/testmodel#Company");
        item1.setIdentifier(newIdentifier("1"));
        Attribute attr1 = new Attribute();
        attr1.setName("name");
        attr1.setValue("Company1");
        item1.addAttributes(attr1);
        Attribute attr2 = new Attribute();
        attr2.setName("vatNumber");
        attr2.setValue("10");
        item1.addAttributes(attr2);
        Reference ref1 = new Reference();
        ref1.setName("address");
        ref1.setIdentifier(newIdentifier("2"));
        item1.addReferences(ref1);
        ReferenceList col1 = new ReferenceList();
        col1.setName("departments");
        col1.addIdentifiers(newIdentifier("3"));
        col1.addIdentifiers(newIdentifier("4"));
        item1.addCollections(col1);

        Item item2 = new Item();
        item2.setClassName("http://www.flymine.org/model/testmodel#Address");
        item2.setImplementations("http://www.flymine.org/model/testmodel#Thing");
        item2.setIdentifier(newIdentifier("2"));
        Attribute field2 = new Attribute();
        field2.setName("address");
        field2.setValue("Address1");
        item2.addAttributes(field2);

        Item item3 = new Item();
        item3.setClassName("http://www.flymine.org/model/testmodel#Department");
        item3.setImplementations("http://www.flymine.org/model/testmodel#RandomInterface");
        item3.setIdentifier(newIdentifier("3"));
        Attribute field3 = new Attribute();
        field3.setName("name");
        field3.setValue("Department1");
        item3.addAttributes(field3);

        Item item4 = new Item();
        item4.setClassName("http://www.flymine.org/model/testmodel#Department");
        item4.setImplementations("http://www.flymine.org/model/testmodel#RandomInterface");
        item4.setIdentifier(newIdentifier("4"));
        Attribute field4 = new Attribute();
        field4.setName("name");
        field4.setValue("Department2");
        item4.addAttributes(field4);

        exampleItems = Arrays.asList(new Object[] {item1, item2, item3, item4});
    }

    private Identifier newIdentifier(String value) {
        Identifier id = new Identifier();
        id.setValue(value);
        return id;
    }

    public void testParse() throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream("test/FullParserTest.xml");
        List items = FullParser.parse(is);
        assertEquals(FullRenderer.render(exampleItems), FullRenderer.render(items));
    }

    public void testParseNull() throws Exception {
        try {
            FullParser.parse(null);
            fail("Expected: NullPointerException");
        } catch (NullPointerException e) {
        }
    }

    public void testRealiseObjects() throws Exception {
        Collection objects = FullParser.realiseObjects(exampleItems, Model.getInstanceByName("testmodel"));
        Company c1 = (Company) objects.iterator().next();
        assertEquals("Company1", c1.getName());
        assertNull(c1.getId());
        Address a1 = c1.getAddress();
        assertEquals("Address1", a1.getAddress());
        assertNull(a1.getId());
        List departments = new ArrayList(c1.getDepartments());
        Collections.sort(departments, new DepartmentComparator());
        Department d1 = (Department) departments.get(0);
        assertEquals("Department1", d1.getName());
        assertNull(d1.getId());
        Department d2 = (Department) departments.get(1);
        assertEquals("Department2", d2.getName());
        assertNull(d1.getId());
    }

    class DepartmentComparator implements Comparator
    {
        public int compare(Object a, Object b) {
            return ((Department) a).getName().compareTo(((Department) b).getName());
        }
    }
}
