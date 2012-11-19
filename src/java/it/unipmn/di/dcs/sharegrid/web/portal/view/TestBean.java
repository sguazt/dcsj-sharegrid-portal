package it.unipmn.di.dcs.sharegrid.web.portal.view;

import java.util.List;
import java.util.ArrayList;

public class TestBean
{

    public TestBean()
    {
    }

    public List getProvinces()
    {
        List<String> list = new ArrayList<String>();
        list.add("Alberta");
        list.add("Manitoba");
        list.add("New Brunswick");
        list.add("Newfoundland and Labrador");
        list.add("Northwest Territories");
        list.add("Nova Scotia");
        list.add("Ontario");
        list.add("Prince Edward Island");
        list.add("Quebec");
        list.add("Saskatchewan");
        list.add("Britiech Columbia");
        list.add("Yukon Territory");
        return list;
    }
}
