package com.parthenope.salvatoresposato.danzon.Database;

import com.orm.SugarRecord;
import com.parthenope.salvatoresposato.danzon.WebService.object.AreaJson;

import java.util.List;

public class Area extends SugarRecord {

    public String description;

    /**
     *
     * @return
     */
    public List<Interval> getIntervals(){
        return Interval.find(Interval.class,"id_area=?",String.valueOf(this.getId()));
    }

    /**
     *
     * @param json
     * @return
     */
    public static long InsertFromJson(AreaJson json){

        Area area = new Area();
        area.description = json.description;
        area.save();

        return area.getId();

    }
}
