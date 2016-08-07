package qnd;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.github.ddth.commons.utils.SerializationUtils;

public class QndJson {
    public static void main(String[] args) {
        String json = "[1,2,3,4]";
        System.out.println(json);

        Object obj = SerializationUtils.fromJsonString(json);
        System.out.println(obj);

        Set<?> objSet = SerializationUtils.fromJsonString(json, Set.class);
        System.out.println(objSet);

        List<?> objList = SerializationUtils.fromJsonString(json, List.class);
        System.out.println(objList);

        Collection<?> objCollection = SerializationUtils.fromJsonString(json, Collection.class);
        System.out.println(objCollection);

        Object[] objArr1 = SerializationUtils.fromJsonString(json, Object[].class);
        System.out.println(objArr1);

        Integer[] objArr2 = SerializationUtils.fromJsonString(json, Integer[].class);
        System.out.println(objArr2);

        int[] objArr3 = SerializationUtils.fromJsonString(json, int[].class);
        System.out.println(objArr3);

        Long[] objArr4 = SerializationUtils.fromJsonString(json, Long[].class);
        System.out.println(objArr4);

        long[] objArr5 = SerializationUtils.fromJsonString(json, long[].class);
        System.out.println(objArr5);
    }
}
