package nl.interjel.management.model.anonymous;

import com.google.gson.GsonBuilder;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * @author Finn Bon
 */
@SuppressWarnings("unchecked")
public class AnonymousObjectTest {

    private AnonymousObject obj;

    @Before
    public void setUp() {
        obj = AnonymousObject.createRoot();
    }

    @Test
    public void testMinimalObject() {
        obj.set("key", "value");
        Map<String, Object> map = obj.build();
        assertEquals(1, map.size());
        assertEquals("key", map.keySet().iterator().next());
        assertEquals("value", map.values().iterator().next());
    }

    @Test
    public void testBasicObject() {
        obj.set("keyString", "value");
        obj.set("keyNumber", 123);
        obj.set("keyNull", null);
        Map<String, Object> map = obj.build();

        Iterator<String> keyIterator = map.keySet().iterator();
        Iterator<Object> valueIterator = map.values().iterator();

        assertEquals(3, map.size());
        assertEquals("keyNull", keyIterator.next());
        assertEquals(null, valueIterator.next());
        assertEquals("keyNumber", keyIterator.next());
        assertEquals(123, valueIterator.next());
        assertEquals("keyString", keyIterator.next());
        assertEquals("value", valueIterator.next());
    }

    @Test
    public void testObjectDepth1() {
        obj.set("normalKey", "normalValue");
        AnonymousObject child = obj.createChild("objectKey");
        child.set("deeperNormalKey", "deeperNormalValue");

        Map<String, Object> map = obj.build();
        Iterator<String> keyIterator = map.keySet().iterator();
        Iterator<Object> valueIterator = map.values().iterator();

        assertEquals(2, map.size());
        assertEquals("normalKey", keyIterator.next());
        assertEquals("normalValue", valueIterator.next());
        assertEquals("objectKey", keyIterator.next());
        map = (Map<String, Object>) valueIterator.next();
        assertEquals(TreeMap.class, map.getClass());
        assertEquals("deeperNormalKey", map.keySet().iterator().next());
        assertEquals("deeperNormalValue", map.values().iterator().next());
    }

    @Test
    public void testObjectDepth1Double() {
        obj.set("normalKey", "normalValue");
        AnonymousObject child;
        child = obj.createChild("objectKey");
        child.set("deeperNormalKey", "deeperNormalValue");
        child = obj.createChild("objectKey2");
        child.set("deeperNormalKey", "deeperNormalValue");

        Map<String, Object> map = obj.build();
        Iterator<String> keyIterator = map.keySet().iterator();
        Iterator<Object> valueIterator = map.values().iterator();

        assertEquals(3, map.size());

        assertEquals("normalKey", keyIterator.next());
        assertEquals("normalValue", valueIterator.next());

        assertEquals("objectKey", keyIterator.next());
        map = (Map<String, Object>) valueIterator.next();
        assertEquals(TreeMap.class, map.getClass());
        assertEquals("deeperNormalKey", map.keySet().iterator().next());
        assertEquals("deeperNormalValue", map.values().iterator().next());

        assertEquals("objectKey2", keyIterator.next());
        map = (Map<String, Object>) valueIterator.next();
        assertEquals(TreeMap.class, map.getClass());
        assertEquals("deeperNormalKey", map.keySet().iterator().next());
        assertEquals("deeperNormalValue", map.values().iterator().next());
    }

    @Test
    public void testCustomSerializer() {
        obj.set("date", LocalDate.now());
        Map<String, Object> map = obj.build();
        assertEquals(1, map.size());
        assertEquals("date", map.keySet().iterator().next());
        Object temp = map.values().iterator().next();
        assertEquals(TreeMap.class, temp.getClass());
        map = (Map<String, Object>) temp;
        Iterator<String> keys = map.keySet().iterator();
        assertEquals("day", keys.next());
        assertEquals("month", keys.next());
        assertEquals("year", keys.next());
        Iterator<Object> values = map.values().iterator();
        assertEquals(LocalDate.now().getDayOfMonth(), values.next());
        assertEquals(LocalDate.now().getMonthValue(), values.next());
        assertEquals(LocalDate.now().getYear(), values.next());
    }

    @Test
    public void testCollection() {
        obj.set("collection", Arrays.asList("Test1",
                "Test2",
                "Test3",
                "Test4"));
        Map<String, Object> map = obj.build();
        assertEquals(1, map.size());
        assertEquals("collection", map.keySet().iterator().next());
        Object collObj = map.values().iterator().next();
        assertEquals(LinkedList.class, collObj.getClass());
        Iterator it = ((Collection) collObj).iterator();
        assertEquals("Test1", it.next());
        assertEquals("Test2", it.next());
        assertEquals("Test3", it.next());
        assertEquals("Test4", it.next());
    }

    @Test
    public void testArray() {
        obj.set("collection", new String[]{
                "Test1",
                "Test2",
                "Test3",
                "Test4"});
        Map<String, Object> map = obj.build();
        assertEquals(1, map.size());
        assertEquals("collection", map.keySet().iterator().next());
        Object collObj = map.values().iterator().next();
        assertEquals(LinkedList.class, collObj.getClass());
        Iterator it = ((Collection) collObj).iterator();
        assertEquals("Test1", it.next());
        assertEquals("Test2", it.next());
        assertEquals("Test3", it.next());
        assertEquals("Test4", it.next());
    }

    @Test
    public void printComplicatedObject() {
        obj.set("ArrayKey", new String[]{
                "Value1",
                "Value2",
                "Value3"
        });

        AnonymousObject child = obj.createChild("ChildKey");
        child.set("Array", new Integer[]{
                1, 8, 4, 6
        });
        child.set("InnerDate", LocalDate.now());
        child.set("Key", "Value");

        obj.set("DateKey", LocalDate.now());

        obj.set("Dates", new LocalDate[]{
                LocalDate.now(),
                LocalDate.now(),
                LocalDate.now()
        });

        obj.set("Key", "Value");

        Map<String, Object> map = obj.build();
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(map));
    }

}