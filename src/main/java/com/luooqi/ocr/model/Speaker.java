package com.luooqi.ocr.model;


import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum  Speaker {
    XIAO_MEI("度小美",0),
    XIAO_YU("度小宇",1),
    XIAO_YAO("度逍遥",3),
    YAYA("度丫丫",4),
    XIAO_JIAO("度小娇",5),
    MI_DUO("度米朵",103),
    BOWEN("度博文",106),
    XIAO_MENG("度小萌",111);



    private String name;
    private Integer val;

    private Speaker(String name, Integer val) {
        this.name = name;
        this.val = val;
    }

    public static int getValue(Number index){
        for(Speaker s : Speaker.values()){
            if(index.intValue() == s.ordinal()){
                return s.val;
            }
        }
        return -1;
    }

    public static List<String> getNames(){
        return Stream.of(values()).map(v->v.name).collect(Collectors.toList());
    }
}
