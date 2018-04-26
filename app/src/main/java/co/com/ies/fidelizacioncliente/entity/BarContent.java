package co.com.ies.fidelizacioncliente.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BarContent {
    private static List<BarItem> barList = new ArrayList<>();

    public static List getBarList() {
        return barList;
    }

    public static final Map<String, BarItem> ITEM_MAP = new HashMap<>();

    public static void addItem(BarItem item) {
        barList.add(item);
        ITEM_MAP.put(item.getId(), item);
    }


}
