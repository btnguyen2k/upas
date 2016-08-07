package qnd;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.github.ddth.commons.utils.DPathUtils;
import com.github.ddth.commons.utils.SerializationUtils;

public class QndJson2 {
    public static void main(String[] args) {
        String json = "{\"a\":[1,2,3,4]}";
        System.out.println(json);

        Object obj = SerializationUtils.fromJsonString(json);
        System.out.println(obj);
        System.out.println(obj.getClass());

        Object o1 = DPathUtils.getValue(obj, "a");
        System.out.println(o1);
        System.out.println(o1.getClass());

        Object o2 = DPathUtils.getValue(obj, "a", Collection.class);
        System.out.println(o2);
        System.out.println(o2.getClass());

        Object o3 = DPathUtils.getValue(obj, "a", List.class);
        System.out.println(o3);
        System.out.println(o3.getClass());

        Object o4 = DPathUtils.getValue(obj, "a", Set.class);
        System.out.println(o4);
        System.out.println(o4.getClass());
    }
}
