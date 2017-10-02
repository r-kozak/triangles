package com.kozak.triangles.model;

/**
 * Класс для группировки по статье выигрыша информации о выигрыше в лотерею
 * 
 * @author Roman: 14 вер. 2015 21:49:39
 */
public class LotteryWinGroupModel {

    /**
     * общее количество билетов - например, 8 билетов.
     */
    private int ticketsCount;

    /**
     * общее количество выигранных сущностей по конкретной статье затрат. Например, 10 киосков (получено за 8
     * билетов).
     */
    private int entitiesCount;

    public int getTicketsCount() {
        return ticketsCount;
    }

    public void setTicketsCount(int ticketsCount) {
        this.ticketsCount = ticketsCount;
    }

    public int getEntitiesCount() {
        return entitiesCount;
    }

    public void setEntitiesCount(int entitiesCount) {
        this.entitiesCount = entitiesCount;
    }

    @Override
    public String toString() {
        return String.format("[%d x %d]", entitiesCount, ticketsCount);
    }

}
