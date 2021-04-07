package com.rabidgremlin.mutters.slots;

public class SlotModel {
    private String slotName;
    private String nerModel;

    public SlotModel(String slotName, String nerModel) {
        this.slotName = slotName;
        this.nerModel = nerModel;
    }

    public String getSlotName() {
        return slotName;
    }

    public String getNerModel() {
        return nerModel;
    }

    public void setSlotName(String slotName) {
        this.slotName = slotName;
    }

    public void setNerModel(String nerModel) {
        this.nerModel = nerModel;
    }
}
