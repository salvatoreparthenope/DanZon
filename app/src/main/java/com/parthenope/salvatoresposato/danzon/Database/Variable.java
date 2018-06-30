package com.parthenope.salvatoresposato.danzon.Database;

import com.orm.SugarRecord;

import java.util.List;

public class Variable extends SugarRecord{

    public String key;
    public String value;

    public Variable(){

    }

    /**
     *
     * @param key
     * @return
     */
    public static String getValue(String key) {
        List<Variable> var = Variable.find(Variable.class,"key = ?",key);
        return var.size() == 1 ? var.get(0).value : null;
    }

    /**
     *
     * @param value
     * @param key
     * @return
     */
    public static Variable updateOrAddVariabile(String key, String value){
        Variable.deleteAll(Variable.class,"key = ?", key);
        Variable var = new Variable();
        var.key = key;
        var.value = value;
        var.save();
        return var;
    }

}
