package dsknn;
import java.util.*;

/**
 * @desc TreeMap���Գ��� 
 *
 * @author skywang
 */
public class TreeMapTest  {

    public static void main(String[] args) {
        // ���Գ��õ�API
        testTreeMapOridinaryAPIs();

        // ����TreeMap�ĵ�������
        //testNavigableMapAPIs();

        // ����TreeMap����Map����
        //testSubMapAPIs();
    }

    /**
     * ���Գ��õ�API
     */
    private static void testTreeMapOridinaryAPIs() {
        // ��ʼ���������
        Random r = new Random();
        // �½�TreeMap
        TreeMap tmap = new TreeMap();
        // ��Ӳ���
//        tmap.put("one", r.nextInt(10));
//        tmap.put("two", r.nextInt(10));
//        tmap.put("three", r.nextInt(10));
//        tmap.put("three", r.nextInt(10));

        tmap.put(1, r.nextInt(10));
        tmap.put(2, r.nextInt(10));
        tmap.put(6, r.nextInt(10));
        tmap.put(4, r.nextInt(10));
        tmap.put(4, r.nextInt(10));
        
        System.out.printf("\n ---- testTreeMapOridinaryAPIs ----\n");
        // ��ӡ��TreeMap
        System.out.printf("%s\n",tmap );

//        // ͨ��Iterator����key-value
//        Iterator iter = tmap.entrySet().iterator();
//        while(iter.hasNext()) {
//            Map.Entry entry = (Map.Entry)iter.next();
//            System.out.printf("next : %s - %s\n", entry.getKey(), entry.getValue());
//        }
//
//        // TreeMap�ļ�ֵ�Ը���        
//        System.out.printf("size: %s\n", tmap.size());
//
//        // containsKey(Object key) :�Ƿ������key
//        System.out.printf("contains key two : %s\n",tmap.containsKey("two"));
//        System.out.printf("contains key five : %s\n",tmap.containsKey("five"));
//
//        // containsValue(Object value) :�Ƿ����ֵvalue
//        System.out.printf("contains value 0 : %s\n",tmap.containsValue(new Integer(0)));
//
//        // remove(Object key) �� ɾ����key��Ӧ�ļ�ֵ��
//        tmap.remove("three");
//
//        System.out.printf("tmap:%s\n",tmap );
//
//        // clear() �� ���TreeMap
//        tmap.clear();
//
//        // isEmpty() : TreeMap�Ƿ�Ϊ��
//        System.out.printf("%s\n", (tmap.isEmpty()?"tmap is empty":"tmap is not empty") );
    }


    /**
     * ����TreeMap����Map����
     */
    public static void testSubMapAPIs() {
        // �½�TreeMap
        TreeMap tmap = new TreeMap();
        // ��ӡ���ֵ�ԡ�
        tmap.put("a", 101);
        tmap.put("b", 102);
        tmap.put("c", 103);
        tmap.put("d", 104);
        tmap.put("e", 105);

        System.out.printf("\n ---- testSubMapAPIs ----\n");
        // ��ӡ��TreeMap
        System.out.printf("tmap:\n\t%s\n", tmap);

        // ���� headMap(K toKey)
        System.out.printf("tmap.headMap(\"c\"):\n\t%s\n", tmap.headMap("c"));
        // ���� headMap(K toKey, boolean inclusive) 
        System.out.printf("tmap.headMap(\"c\", true):\n\t%s\n", tmap.headMap("c", true));
        System.out.printf("tmap.headMap(\"c\", false):\n\t%s\n", tmap.headMap("c", false));

        // ���� tailMap(K fromKey)
        System.out.printf("tmap.tailMap(\"c\"):\n\t%s\n", tmap.tailMap("c"));
        // ���� tailMap(K fromKey, boolean inclusive)
        System.out.printf("tmap.tailMap(\"c\", true):\n\t%s\n", tmap.tailMap("c", true));
        System.out.printf("tmap.tailMap(\"c\", false):\n\t%s\n", tmap.tailMap("c", false));
   
        // ���� subMap(K fromKey, K toKey)
        System.out.printf("tmap.subMap(\"a\", \"c\"):\n\t%s\n", tmap.subMap("a", "c"));
        // ���� 
        System.out.printf("tmap.subMap(\"a\", true, \"c\", true):\n\t%s\n", 
                tmap.subMap("a", true, "c", true));
        System.out.printf("tmap.subMap(\"a\", true, \"c\", false):\n\t%s\n", 
                tmap.subMap("a", true, "c", false));
        System.out.printf("tmap.subMap(\"a\", false, \"c\", true):\n\t%s\n", 
                tmap.subMap("a", false, "c", true));
        System.out.printf("tmap.subMap(\"a\", false, \"c\", false):\n\t%s\n", 
                tmap.subMap("a", false, "c", false));

        // ���� navigableKeySet()
        System.out.printf("tmap.navigableKeySet():\n\t%s\n", tmap.navigableKeySet());
        // ���� descendingKeySet()
        System.out.printf("tmap.descendingKeySet():\n\t%s\n", tmap.descendingKeySet());
    }

    /**
     * ����TreeMap�ĵ�������
     */
    public static void testNavigableMapAPIs() {
        // �½�TreeMap
        NavigableMap nav = new TreeMap();
        // ��ӡ���ֵ�ԡ�
        nav.put("aaa", 111);
        nav.put("bbb", 222);
        nav.put("eee", 333);
        nav.put("ccc", 555);
        nav.put("ddd", 444);

        System.out.printf("\n ---- testNavigableMapAPIs ----\n");
        // ��ӡ��TreeMap
        System.out.printf("Whole list:%s%n", nav);

        // ��ȡ��һ��key����һ��Entry
        System.out.printf("First key: %s\tFirst entry: %s%n",nav.firstKey(), nav.firstEntry());

        // ��ȡ���һ��key�����һ��Entry
        System.out.printf("Last key: %s\tLast entry: %s%n",nav.lastKey(), nav.lastEntry());

        // ��ȡ��С��/����bbb��������ֵ��
        System.out.printf("Key floor before bbb: %s%n",nav.floorKey("bbb"));

        // ��ȡ��С��bbb��������ֵ��
        System.out.printf("Key lower before bbb: %s%n", nav.lowerKey("bbb"));

        // ��ȡ������/����bbb������С��ֵ��
        System.out.printf("Key ceiling after ccc: %s%n",nav.ceilingKey("ccc"));

        // ��ȡ������bbb������С��ֵ��
        System.out.printf("Key higher after ccc: %s%n\n",nav.higherKey("ccc"));
    }

}